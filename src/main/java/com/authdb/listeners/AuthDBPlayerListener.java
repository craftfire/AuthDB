/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.listeners;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.authdb.AuthDB;
import com.authdb.plugins.ZBukkitContrib;
import com.authdb.plugins.ZPermissions;
import com.authdb.plugins.ZPermissions.Permission;
import com.authdb.plugins.ZSpout;
import com.authdb.util.Config;
import com.authdb.util.encryption.Encryption;
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.authdb.util.Messages.Message;
import com.authdb.util.Processes;
import com.authdb.util.databases.EBean;
import com.authdb.util.databases.MySQL;

import com.afforess.backpack.BackpackManager;
import com.afforess.backpack.BackpackPlayer;

public class AuthDBPlayerListener extends PlayerListener {
    private final AuthDB plugin;
    boolean sessionallow;
    int Schedule;

    public AuthDBPlayerListener(AuthDB instance) {
        this.plugin = instance;
    }

    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!MySQL.isConnected()) {
            event.disallow(Result.KICK_OTHER, "You can't join the server when the server has no connection to MySQL.");
            return;
        }
        
        EBean.sync(player);

        if (Config.session_protect && Util.checkIfLoggedIn(player)) {
            Messages.sendMessage(Message.session_protected, player, event);
        }
        if (Config.filter_action.equalsIgnoreCase("kick") || Config.filter_action.equalsIgnoreCase("rename")) {
            String name = player.getName();
            if (Util.checkFilter("username",name) == false && Util.checkWhitelist("username",player) == false) {
            Util.logging.Debug("The player is not in the whitelist and has bad characters in his/her name");
            if (Config.filter_action.equalsIgnoreCase("kick")) Messages.sendMessage(Message.filter_username, player, event);
            }
        }
        if (player.getName().length() < Integer.parseInt(Config.username_minimum)) {
            Messages.sendMessage(Message.username_minimum, player, event);
        } else if (player.getName().length() > Integer.parseInt(Config.username_maximum)) {
            Messages.sendMessage(Message.username_maximum, player, event);
        }
        if (Config.link_rename && !Util.checkOtherName(player.getName()).equals(player.getName())) {
            Util.craftFirePlayer.renamePlayer(player,Util.checkOtherName(player.getName()));
        }
    }
    
    public void checkTimeout(Player player) {
        Util.logging.Debug("Launching function: checkTimeout(Player player))");
        EBean eBeanClass = EBean.checkPlayer(player, true);
        int timeoutid = eBeanClass.getTimeoutid();
        if (plugin.isAuthorized(player) == false && (AuthDB.AuthDB_Timeouts.containsKey(player.getName()) || timeoutid != 0)) {
            eBeanClass.setTimeoutid(0);
            EBean.save(eBeanClass);
            if(plugin.isRegistered("checkguest", player.getName())) {
                Messages.sendMessage(Message.login_timeout, player, null);
            }
            else {
                Messages.sendMessage(Message.register_timeout, player, null);
            }
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        EBean.checkIP(player.getName(), Util.craftFirePlayer.getIP(player));
        player.teleport(Util.landLocation(player.getLocation()));
        if (Config.link_rename && !Util.checkOtherName(player.getName()).equals(player.getName())) {
            String message = event.getJoinMessage();
            message = message.replaceAll(player.getName(), player.getDisplayName());
            event.setJoinMessage(message);
        }
        this.plugin.AuthDB_PasswordTries.put(player.getName(),"0");
        if (Config.session_enabled && Config.session_length != 0) {
            long timestamp = System.currentTimeMillis()/1000;
            if (Util.authDBplayer.sessionTime(player) != 0) {
                long storedtime = Util.authDBplayer.sessionTime(player);
                Util.logging.Debug("Found session for " + player.getName() + ", timestamp: " + storedtime);
                long timedifference = timestamp - storedtime;
                Util.logging.Debug("Difference: " + timedifference);
                Util.logging.Debug("Session in config: " + Config.session_length);
                if (timedifference > Config.session_length) {
                    sessionallow = false;
                } else {
                    sessionallow = true;
                }
            } else {
                sessionallow = false;
            }
        }

        try {
            if (sessionallow == false) {
                int time = 0;
                if (Config.login_timeout > 0 && plugin.isRegistered("checkguest", player.getName())) {
                    Util.logging.Debug("Login timeout time is: " + Config.login_timeout + " ticks.");
                    time = Config.login_timeout;
                }
                else if (Config.register_timeout > 0 && !plugin.isRegistered("checkguest", player.getName())) {
                    Util.logging.Debug("Register timeout time is: " + Config.register_timeout + " ticks.");
                    time = Config.register_timeout;
                }
                if (time > 0) {
                    Schedule = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                                checkTimeout(player);
                        }
                    }, time);
                    EBean eBeanClass = EBean.checkPlayer(player, true);
                    Util.logging.Debug("Adding schedule ID to hashmap and persitence: " + Schedule);
                    eBeanClass.setTimeoutid(Schedule);
                    AuthDB.database.save(eBeanClass);
                    if (AuthDB.AuthDB_Timeouts.put(player.getName(), Schedule) != null) {
                        Util.logging.Debug(player.getName() + " added to the CheckTimeoutTaskList");
                    }
                }
            }
            if (Config.custom_enabled && (Config.custom_encryption.equals("") || Config.custom_encryption == null)) {
                player.sendMessage("§4YOUR PASSWORD WILL NOT BE ENCRYPTED, PLEASE BE ADWARE THAT THIS SERVER STORES THE PASSWORDS IN PLAINTEXT.");
            }
            if (event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1) {
                player.setHealth(20);
                player.teleport(player.getWorld().getSpawnLocation());
            }
            EBean eBeanClass = EBean.checkPlayer(player, true);
            if (Config.session_enabled && ((eBeanClass.getReloadtime() + 30) >= Util.timeStamp())) {
                sessionallow = true;
            }
            
            if (Config.onlineMode && this.plugin.isRegistered("join",player.getName())) {
                sessionallow = true;
            }
            
            /*else if (!Config.onlineMode) {
                Util.logging.Debug("Session id: " + Util.server.getSessionId());
                boolean allow = false;
                URL verify = new URL("http://www.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(player.getName(), "UTF-8") + "&serverId=" + URLEncoder.encode(Util.server.getSessionId(), "UTF-8"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(verify.openStream()));
                String result = reader.readLine();
                reader.close();
                allow = result.equalsIgnoreCase("YES");
                if(allow) { 
                    Util.logging.Debug("Online mode is off but player '" + player.getName() + "' is authed with minecraft.net and does not have to login.");
                    sessionallow = true;
                }
            }
            */
            if (sessionallow) {
                long thetimestamp = System.currentTimeMillis()/1000;
                this.plugin.AuthDB_AuthTime.put(player.getName(), thetimestamp);
                Processes.Login(event.getPlayer());
                Messages.sendMessage(Message.session_valid, player, null);
            } else if (this.plugin.isRegistered("join",player.getName())) {
                if (Config.hasBackpack) {
                    BackpackPlayer BackpackPlayer = BackpackManager.getBackpackPlayer((Player)player);
                    BackpackPlayer.createBackpack();
                    this.plugin.storeInventory(player, BackpackPlayer.getInventory().getContents(), player.getInventory().getArmorContents());
                } else {
                    this.plugin.storeInventory(player, player.getInventory().getContents(), player.getInventory().getArmorContents());
                }
                player.getInventory().clear();
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override public void run() {
                        if (!plugin.isAuthorized(player)) {
                            if (player.getInventory() != null) {
                                player.getInventory().clear();
                            }
                            Util.craftFirePlayer.clearArmorinventory(player);
                        }
                    }
                } , 20);
                if (Util.toLoginMethod(Config.login_method).equalsIgnoreCase("prompt")) {
                    if(!Config.hasSpout) {
                        Messages.sendMessage(Message.login_prompt, player, null);
                    }
                } else {
                    Messages.sendMessage(Message.login_normal, player, null);
                }
            } else if (Config.register_force) {
                if (Config.hasBackpack) {
                    BackpackPlayer BackpackPlayer = BackpackManager.getBackpackPlayer((Player)player);
                    BackpackPlayer.createBackpack();
                    this.plugin.storeInventory(player, BackpackPlayer.getInventory().getContents(), player.getInventory().getArmorContents());
                } else {
                    this.plugin.storeInventory(player, player.getInventory().getContents(), player.getInventory().getArmorContents());
                }
                player.getInventory().clear();
                Util.craftFirePlayer.clearArmorinventory(player);
                Messages.sendMessage(Message.welcome_guest, player, null);
            } else if (!Config.register_force) {
                if(Config.register_enabled) {
                    Messages.sendMessage(Message.welcome_guest, player, null);
                }
            } else {
                long thetimestamp = System.currentTimeMillis()/1000;
                this.plugin.AuthDB_AuthTime.put(player.getName(), thetimestamp);
                Processes.Login(player);
            }
        } catch (IOException e) {
            Util.logging.Severe("[" + AuthDB.pluginName + "] Inventory file error:");
            player.kickPlayer("Inventory protection kicked.");
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            player.sendMessage(Color.red + "Error happend, report to admins!");
        }
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Messages.sendMessage(Message.left_server, player, null);
        if (event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1) {
            player.setHealth(20);
            player.teleport(player.getWorld().getSpawnLocation());
        }
        long thetimestamp = System.currentTimeMillis()/1000;
        if (Config.session_enabled && Config.session_start.equalsIgnoreCase("logoff")) {
            this.plugin.AuthDB_Sessions.put(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)), thetimestamp);
            EBean EBeanClass = EBean.checkPlayer(player, true);
            EBeanClass.setSessiontime(thetimestamp);
            this.plugin.AuthDB_AuthTime.put(player.getName(), thetimestamp);
        }

        if (checkGuest(player,Config.guests_inventory) == false && this.plugin.isRegistered("quit",player.getName()) == false && this.plugin.isRegistered("quit",Util.checkOtherName(player.getName())) == false) {
            ItemStack[] theinv;
			if (Config.hasBackpack) { theinv = new ItemStack[252]; }
			else { theinv = new ItemStack[36]; }
            player.getInventory().setContents(theinv);
        }
        
        Processes.Logout(player);
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Util.logging.Info("Command: " + event.getMessage());
        long start = Util.timeMS();
        String Contrib = event.getMessage();
        Contrib = Contrib.replaceAll("/", "");
        if (!ZBukkitContrib.checkCommand(Contrib)) {
            String[] split = event.getMessage().split(" ");
            Player player = event.getPlayer();
            if (split[0].equalsIgnoreCase(Config.commands_login) || split[0].equalsIgnoreCase(Config.aliases_login)) {
                if (ZPermissions.isAllowed(player, Permission.command_login)) {
                    if (this.plugin.isRegistered("command",player.getName()) == false || this.plugin.isRegistered("command",Util.checkOtherName(player.getName())) == false) {
                        Messages.sendMessage(Message.login_notregistered, player, null);
                    } else if (plugin.isAuthorized(player)) {
                        Messages.sendMessage(Message.login_authorized, player, null);
                    } else if (split.length < 2) {
                        Messages.sendMessage(Message.login_usage, player, null);
                    } else if (this.plugin.checkPassword(player.getName(), split[1])) {
                        Processes.Login(player);
                        Messages.sendMessage(Message.login_success, player, null);
                    } else {
                        Messages.sendMessage(Message.login_failure, player, null);
                    }
                    Util.logging.Debug(player.getName() + " login ********");
                    event.setMessage(Config.commands_login + " ******");
                    event.setCancelled(true);
                } else { Messages.sendMessage(Message.protection_denied, player, null); }
            } else if (split[0].equalsIgnoreCase(Config.commands_link) || split[0].equalsIgnoreCase(Config.aliases_link)) {
                if (Config.link_enabled) {
                    if (ZPermissions.isAllowed(player, Permission.command_link)) {
                        if (split.length == 3) {
                            if (!player.getName().equals(split[1])) {
                                   if (Util.checkOtherName(player.getName()).equals(player.getName())) {
                                       EBean eBeanClass = EBean.checkPlayer(split[1], true);
                                       String linkedname = eBeanClass.getLinkedname();
                                       if (linkedname != null) {
                                           Messages.sendMessage(Message.link_duplicate, player, null);
                                       } else if (this.plugin.checkPassword(split[1], split[2])) {
                                        Processes.Link(player,split[1]);
                                        Messages.sendMessage(Message.link_success, player, null);
                                    } else {
                                        Messages.sendMessage(Message.link_failure, player, null);
                                    }
                                } else {
                                    Messages.sendMessage(Message.link_exists, player, null);
                                }
                            } else {
                                Messages.sendMessage(Message.link_invaliduser, player, null);
                            }
                        } else {
                            Messages.sendMessage(Message.link_usage, player, null);
                        }
                        Util.logging.Debug(player.getName() + " link ******** ********");
                        event.setMessage(Config.commands_link + " ****** ********");
                        event.setCancelled(true);
                    } else { Messages.sendMessage(Message.protection_denied, player, null); }
                }
            } else if (split[0].equalsIgnoreCase(Config.commands_unlink) || split[0].equalsIgnoreCase(Config.aliases_unlink)) {
                if (Config.unlink_enabled) {
                    if (ZPermissions.isAllowed(player, Permission.command_unlink)) {
                        if (split.length == 3) {
                            if (Util.checkOtherName(player.getName()).equals(player.getDisplayName())) {
                                EBean eBeanClass = EBean.checkPlayer(player, true);
                                String linkedname = eBeanClass.getLinkedname();
                                if (linkedname.equals(split[1])) {
                                    if (this.plugin.checkPassword(split[1], split[2])) {
                                        Processes.Unlink(player,split[1]);
                                        Messages.sendMessage(Message.unlink_success, player, null);
                                    } else {
                                        Messages.sendMessage(Message.unlink_invalidpass, player, null);
                                    }
                                } else {
                                    Messages.sendMessage(Message.unlink_invaliduser, player, null);
                                }
                            } else {
                                Messages.sendMessage(Message.unlink_nonexist, player, null);
                            }
                        } else {
                            Messages.sendMessage(Message.unlink_usage, player, null);
                        }
                        Util.logging.Debug(player.getName() + " unlink ******** ********");
                        event.setMessage(Config.commands_unlink + " ****** ********");
                        event.setCancelled(true);
                    } else { Messages.sendMessage(Message.protection_denied, player, null); }
                }
            } else if (split[0].equalsIgnoreCase(Config.commands_register) || split[0].equalsIgnoreCase(Config.aliases_register)) {
                if (ZPermissions.isAllowed(player, Permission.command_register)) {
                    Boolean email = true;
                    if (Config.custom_emailfield == null || Config.custom_emailfield == "") { email = false; }
                    if (!Config.register_enabled) {
                        Messages.sendMessage(Message.register_disabled, player, null);
                    } else if (this.plugin.isRegistered("register-command",player.getName()) || this.plugin.isRegistered("register-command",Util.checkOtherName(player.getName()))) {
                        Messages.sendMessage(Message.register_exists, player, null);
                    } else if (split.length < 2) {
                        Messages.sendMessage(Message.register_usage, player, null);
                    } else if (split.length < 3 && email) {
                        Messages.sendMessage(Message.email_required, player, null);
                    } else if ((split.length >= 3 && email) && (!this.plugin.checkEmail(split[2]))) {
                        Util.logging.Debug("LOG: "+split[2]);
                        Messages.sendMessage(Message.email_invalid, player, null);
                    } else {
                        try {
                            if (split.length >= 3 || (!email && split.length >= 2)) {
                                String themail = null;
                                if (!email) {
                                    themail = null;
                                } else {
                                    themail = split[2];
                                }
                                if (this.plugin.register(player, split[1], themail,Util.craftFirePlayer.getIP(player))) {
                                    ItemStack[] inv = this.plugin.getInventory(player);
                                    if (inv != null) {
                                        player.getInventory().setContents(inv);
                                    }
                                    inv = AuthDB.getArmorInventory(player);
                                    if (inv != null) {
                                        player.getInventory().setArmorContents(inv);
                                    }
                                    long timestamp = System.currentTimeMillis()/1000;
                                    this.plugin.AuthDB_Authed.put(Encryption.md5(player.getName()), "yes");
                                    if(Config.session_enabled) {
                                        this.plugin.AuthDB_Sessions.put(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)), timestamp);
                                        EBean eBeanClass = EBean.checkPlayer(player, true);
                                        eBeanClass.setSessiontime(timestamp);
                                        AuthDB.database.save(eBeanClass);
                                        Util.logging.Debug("Session started for " + player.getName());
                                    }
                                    Processes.Login(player);
                                    long thetimestamp = System.currentTimeMillis()/1000;
                                    this.plugin.AuthDB_AuthTime.put(player.getName(), thetimestamp);
                                    Location temploc = event.getPlayer().getLocation();
                                    while (temploc.getBlock().getTypeId() == 0) {
                                        temploc.setY(temploc.getY() - 1);
                                    }
                                    temploc.setY(temploc.getY() + 1);
                                    event.getPlayer().teleport(temploc);

                                    Messages.sendMessage(Message.register_success, player, null);
                                }
                            }
                        } catch (IOException e) {
                            Messages.sendMessage(Message.register_failure, player, null);
                            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                        } catch (SQLException e) {
                            Messages.sendMessage(Message.register_failure, player, null);
                            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                        }
                    }
                    Util.logging.Debug(player.getName() + " register ********");
                    event.setMessage(Config.commands_register + " *****");
                    event.setCancelled(true);
                } else { Messages.sendMessage(Message.protection_denied, player, null); }
            } else if (!plugin.isAuthorized(player)) {
                if (!checkGuest(player,Config.guests_commands)) {
                    event.setMessage("/iamnotloggedin");
                    event.setCancelled(true);
                }
            }
            } else {
                Util.logging.Debug("BukkitContrib or Spout is trying to check for SP client with command: " + event.getMessage());
            }
            long stop = Util.timeMS();
            Util.logging.timeUsage(stop - start, "process a command");
        }

    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) { return; }
        if (!plugin.isAuthorized(event.getPlayer())) {
            if (!checkGuest(event.getPlayer(),Config.guests_movement)) {
                event.getPlayer().teleport(event.getFrom());
            }
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) { return; }
        if (!AuthDB.isAuthorized(event.getPlayer())) {
            Player player = event.getPlayer();
                if (Util.toLoginMethod(Config.login_method).equalsIgnoreCase("prompt")) {
                    if (this.plugin.isRegistered("chat",event.getPlayer().getName()) || this.plugin.isRegistered("chat",Util.checkOtherName(event.getPlayer().getName()))) {
                        String[] split = event.getMessage().split(" ");
                        if (ZPermissions.isAllowed(player, Permission.command_login)) {
                            if (this.plugin.isRegistered("chatprompt",player.getName()) || this.plugin.isRegistered("chatprompt",Util.checkOtherName(player.getName()))) {
                                if (AuthDB.isAuthorized(player)) {
                                    Messages.sendMessage(Message.login_authorized, player, null);
                                } else if (split.length > 1) {
                                    Messages.sendMessage(Message.login_prompt, player, null);
                                } else if (this.plugin.checkPassword(player.getName(), split[0]) || this.plugin.checkPassword(Util.checkOtherName(player.getName()), split[0])) {
                                    Processes.Login(player);
                                    Messages.sendMessage(Message.login_success, player, null);
                                } else {
                                    Messages.sendMessage(Message.login_failure, player, null);
                                }
                                Util.logging.Debug(player.getName() + " login ********");
                                event.setMessage(" has logged in!");
                                event.setCancelled(true);
                            }
                            event.setMessage("");
                            event.setCancelled(true);
                        }
                    } else if (!checkGuest(event.getPlayer(), Config.guests_chat)) {
                        event.setCancelled(true);
                    }
                } else if (!checkGuest(event.getPlayer(), Config.guests_chat)) {
                    event.setCancelled(true);
                }
            }
        }

    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!plugin.isAuthorized(event.getPlayer())) {
            if (!checkGuest(event.getPlayer(),Config.guests_pickup)) {
                event.setCancelled(true);
            }
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.isAuthorized(event.getPlayer())) {
            if (!checkGuest(event.getPlayer(),Config.guests_interact)) {
                event.setCancelled(true);
            }
        }
    }

    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!plugin.isAuthorized(event.getPlayer())) {
            if (this.plugin.isRegistered("dropitem",event.getPlayer().getName()) || this.plugin.isRegistered("dropitem",Util.checkOtherName(event.getPlayer().getName()))) {
                event.setCancelled(true);
            }
            else if (!checkGuest(event.getPlayer(),Config.guests_drop)) {
                event.setCancelled(true);
            }
        }
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1) {
            event.getPlayer().setHealth(20);
            event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
        }
    }

    boolean checkGuest(Player player,boolean what) {
        if (what) {
            if (this.plugin.isRegistered("checkguest",player.getName()) == false || this.plugin.isRegistered("checkguest",Util.checkOtherName(player.getName())) == false) {
                return true;
            }
        } else if (Config.protection_notify && !AuthDB.isAuthorized(player)) {
            if (!this.plugin.AuthDB_RemindLogin.containsKey(player.getName())) {
                this.plugin.AuthDB_RemindLogin.put(player.getName(), Util.timeStamp() + Config.protection_delay);
                Messages.sendMessage(Message.protection_notauthorized, player, null);
            } else {
                if (this.plugin.AuthDB_RemindLogin.get(player.getName()) < Util.timeStamp()) {
                    Messages.sendMessage(Message.protection_notauthorized, player, null);
                    this.plugin.AuthDB_RemindLogin.put(player.getName(), Util.timeStamp() + Config.protection_delay);
                }
            }
        } else {
            if (Config.protection_notify && this.plugin.AuthDB_RemindLogin.containsKey(player.getName())) {
                this.plugin.AuthDB_RemindLogin.remove(player.getName());
            }
        }
        return false;
    }
}
