/*
 * This file is part of AuthDB.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
 * AuthDB is licensed under the GNU Lesser General Public License.
 *
 * AuthDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.authdb.listeners;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.ItemStack;

import com.craftfire.authdb.AuthDB;
import com.craftfire.authdb.plugins.ZPermissions;
import com.craftfire.authdb.util.Config;
import com.craftfire.authdb.util.Messages;
import com.craftfire.authdb.util.Messages.Message;
import com.craftfire.authdb.util.Processes;
import com.craftfire.authdb.util.Util;
import com.craftfire.authdb.util.databases.EBean;
import com.craftfire.authdb.util.databases.MySQL;
import com.craftfire.authdb.util.encryption.Encryption;

public class AuthDBPlayerListener implements Listener {
    private final AuthDB plugin;
    boolean sessionallow;
    int Schedule;

    public AuthDBPlayerListener(AuthDB instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (!MySQL.isConnected()) {
            event.disallow(Result.KICK_OTHER, "You cannot join when the server has no database connection.");
            return;
        }

        if (Config.session_protect && Util.checkIfLoggedIn(player)) {
            Messages.sendMessage(Message.session_protected, player, event);
            return;
        } else {
            EBean.sync(player);
        }

        if (Config.join_restrict && !plugin.isRegistered("checkguest", player.getName())) {
             Messages.sendMessage(Message.join_restrict, player, event);
             return;
        }

        if (Config.filter_action.equalsIgnoreCase("kick") || Config.filter_action.equalsIgnoreCase("rename")) {
            String name = player.getName();
            if (Util.checkFilter("username", name) == false && Util.checkWhitelist("username", player) == false) {
                Util.logging.debug(name + " is not in the whitelist and has bad characters in his/her name.");
                if (Config.filter_action.equalsIgnoreCase("kick")) {
                    Messages.sendMessage(Message.filter_username, player, event);
                    return;
                }
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
        Util.logging.debug("Launching function: checkTimeout(Player player))");
        EBean eBeanClass = EBean.checkPlayer(player, true);
        int timeoutid = eBeanClass.getTimeoutid();
        if (plugin.isAuthorized(player) == false && (AuthDB.AuthDB_Timeouts.containsKey(player.getName()) || timeoutid != 0)) {
            eBeanClass.setTimeoutid(0);
            EBean.save(eBeanClass);
            if (plugin.isRegistered("checkguest", player.getName())) {
                Messages.sendMessage(Message.login_timeout, player, null);
            } else {
                Messages.sendMessage(Message.register_timeout, player, null);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        EBean.checkIP(player.getName(), Util.craftFirePlayer.getIP(player));
        player.teleport(Util.landLocation(player.getLocation()));
        this.plugin.AuthDB_JoinTime.put(player.getName(), Util.timeStamp());
        if (Config.link_rename && !Util.checkOtherName(player.getName()).equals(player.getName())) {
            String message = event.getJoinMessage();
            message = message.replaceAll(player.getName(), player.getDisplayName());
            event.setJoinMessage(message);
        }
        this.plugin.AuthDB_PasswordTries.put(player.getName(),"0");
        if (Config.session_enabled && Config.session_length != 0) {
            long timestamp = System.currentTimeMillis() / 1000;
            if (Util.authDBplayer.sessionTime(player) != 0) {
                long storedtime = Util.authDBplayer.sessionTime(player);
                Util.logging.debug("Found session for " + player.getName() + ", timestamp: " + storedtime);
                long timedifference = timestamp - storedtime;
                Util.logging.debug("Difference: " + timedifference);
                Util.logging.debug("Session in config: " + Config.session_length);
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
                    Util.logging.debug("Login timeout time is: " + Config.login_timeout + " ticks.");
                    time = Config.login_timeout;
                } else if (Config.register_timeout > 0 && !plugin.isRegistered("checkguest", player.getName())) {
                    Util.logging.debug("Register timeout time is: " + Config.register_timeout + " ticks.");
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
                    Util.logging.debug("Adding schedule ID to hashmap and persitence: " + Schedule);
                    eBeanClass.setTimeoutid(Schedule);
                    AuthDB.database.save(eBeanClass);
                    if (AuthDB.AuthDB_Timeouts.put(player.getName(), Schedule) != null) {
                        Util.logging.debug(player.getName() + " added to the CheckTimeoutTaskList");
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

            if (Config.onlineMode && this.plugin.isRegistered("join", player.getName())) {
                sessionallow = true;
            } /*else if (!Config.onlineMode) {
                Util.logging.debug("Session id: " + Util.server.getSessionId());
                boolean allow = false;
                URL verify = new URL("http://www.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(player.getName(), "UTF-8") + "&serverId=" + URLEncoder.encode(Util.server.getSessionId(), "UTF-8"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(verify.openStream()));
                String result = reader.readLine();
                reader.close();
                allow = result.equalsIgnoreCase("YES");
                if (allow) {
                    Util.logging.debug("Online mode is off but player '" + player.getName() + "' is authed with minecraft.net and does not have to login.");
                    sessionallow = true;
                }
            }*/

            if (sessionallow) {
                long thetimestamp = System.currentTimeMillis() / 1000;
                this.plugin.AuthDB_AuthTime.put(player.getName(), thetimestamp);
                Processes.Login(event.getPlayer());
                Messages.sendMessage(Message.session_valid, player, null);
            } else if (this.plugin.isRegistered("join", player.getName())) {
                Util.craftFirePlayer.storeInventory(player, player.getInventory().getContents(), player.getInventory().getArmorContents());
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
                    Messages.sendMessage(Message.login_prompt, player, null);
                } else {
                    Messages.sendMessage(Message.login_normal, player, null);
                }
            } else if (Config.register_force) {
                Util.craftFirePlayer.storeInventory(player, player.getInventory().getContents(), player.getInventory().getArmorContents());
                player.getInventory().clear();
                Util.craftFirePlayer.clearArmorinventory(player);
                Messages.sendMessage(Message.register_welcome, player, null);
            } else if (!Config.register_force) {
                if (Config.register_enabled) {
                    Messages.sendMessage(Message.register_welcome, player, null);
                }
            } else {
                long thetimestamp = System.currentTimeMillis() / 1000;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (Config.link_rename && !Util.checkOtherName(player.getName()).equals(player.getName())) {
            String message = event.getQuitMessage();
            message = message.replaceAll(player.getName(), player.getDisplayName());
            event.setQuitMessage(message);
        }
        Messages.sendMessage(Message.left_server, player, null);
        if (this.plugin.AuthDB_GUI_PasswordFieldIDs.containsKey(player.getName())) {
            this.plugin.AuthDB_GUI_PasswordFieldIDs.remove(player.getName());
        }
        if (event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1) {
            player.setHealth(20);
            player.teleport(player.getWorld().getSpawnLocation());
        }
        long thetimestamp = System.currentTimeMillis() / 1000;
        if (Config.session_enabled && Config.session_start.equalsIgnoreCase("logoff") && AuthDB.isAuthorized(player)) {
            this.plugin.AuthDB_Sessions.put(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)), thetimestamp);
            EBean EBeanClass = EBean.checkPlayer(player, true);
            EBeanClass.setSessiontime(thetimestamp);
            this.plugin.AuthDB_AuthTime.put(player.getName(), thetimestamp);
        }

        if (checkGuest(player,Config.guests_inventory) == false && this.plugin.isRegistered("quit",player.getName()) == false && this.plugin.isRegistered("quit",Util.checkOtherName(player.getName())) == false) {
            ItemStack[] theinv = new ItemStack[36];
            player.getInventory().setContents(theinv);
        }

        Processes.Quit(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("stop") && event.getPlayer().hasPermission("bukkit.command.stop")) {
            AuthDB.stop = true;
        }
        long start = Util.timeMS();
        String[] split = event.getMessage().split(" ");
        Player player = event.getPlayer();
        if (split[0].equalsIgnoreCase(Config.commands_user_login) || split[0].equalsIgnoreCase(Config.aliases_user_login)) {
            if (ZPermissions.isAllowed(player, ZPermissions.ZPermission.command_login)) {
                Messages.sendMessage(Message.login_processing, player, null);
                if (!this.plugin.isRegistered("command",Util.checkOtherName(player.getName()))) {
                    Messages.sendMessage(Message.login_notregistered, player, null);
                } else if (plugin.isAuthorized(player)) {
                    Messages.sendMessage(Message.login_authorized, player, null);
                } else if (split.length < 2) {
                    Messages.sendMessage(Message.login_usage, player, null);
                } else if (this.plugin.checkPassword(player.getName(), split[1])) {
                    Processes.Login(player);
                    Messages.sendMessage(Message.login_success, player, null);
                } else {
                    if (Config.authdb_enabled) {
                        Messages.sendMessage(Message.login_failure, player, null);
                    } else {
                        Messages.sendMessage(Message.login_offline, player, null);
                    }
                }
                Util.logging.debug(player.getName() + " login ********");
                event.setMessage(Config.commands_user_login + " ******");
                event.setCancelled(true);
            } else { Messages.sendMessage(Message.protection_denied, player, null); }
        } else if (!Config.join_restrict && (split[0].equalsIgnoreCase(Config.commands_user_link) || split[0].equalsIgnoreCase(Config.aliases_user_link))) {
            if (Config.link_enabled) {
                if (ZPermissions.isAllowed(player, ZPermissions.ZPermission.command_link)) {
                    if (split.length == 3) {
                        if (!player.getName().equals(split[1])) {
                            Messages.sendMessage(Message.link_processing, player, null);
                            if (!this.plugin.isRegistered("link",player.getName())) {
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
                                Messages.sendMessage(Message.link_registered, player, null);
                            }
                        } else {
                            Messages.sendMessage(Message.link_invaliduser, player, null);
                        }
                    } else {
                        Messages.sendMessage(Message.link_usage, player, null);
                    }
                    Util.logging.debug(player.getName() + " link ******** ********");
                    event.setMessage(Config.commands_user_link + " ****** ********");
                    event.setCancelled(true);
                } else { Messages.sendMessage(Message.protection_denied, player, null); }
            }
        } else if (!Config.join_restrict && (split[0].equalsIgnoreCase(Config.commands_user_unlink) || split[0].equalsIgnoreCase(Config.aliases_user_unlink))) {
            if (Config.unlink_enabled) {
                if (ZPermissions.isAllowed(player, ZPermissions.ZPermission.command_unlink)) {
                    Messages.sendMessage(Message.unlink_processing, player, null);
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
                    Util.logging.debug(player.getName() + " unlink ******** ********");
                    event.setMessage(Config.commands_user_unlink + " ****** ********");
                    event.setCancelled(true);
                } else { Messages.sendMessage(Message.protection_denied, player, null); }
            }
        } else if (!Config.join_restrict && (split[0].equalsIgnoreCase(Config.commands_user_register) || split[0].equalsIgnoreCase(Config.aliases_user_register))) {
            if (ZPermissions.isAllowed(player, ZPermissions.ZPermission.command_register)) {
                Messages.sendMessage(Message.register_processing, player, null);
                Boolean email = true;
                if (Config.custom_enabled) {
                    email = Config.custom_emailrequired;
                    if (Config.custom_emailfield == null || Config.custom_emailfield.equals("")) {
                        email = false;
                    }
                }
                if (!Config.register_enabled) {
                    Messages.sendMessage(Message.register_disabled, player, null);
                } else if (this.plugin.isRegistered("register-command",player.getName()) || this.plugin.isRegistered("register-command",Util.checkOtherName(player.getName()))) {
                    Messages.sendMessage(Message.register_exists, player, null);
                } else if (Config.register_limit > 0 &&
                        EBean.getAmount("ip", player.getAddress().getAddress().toString().substring(1)) >
                        Config.register_limit) {
                    Messages.sendMessage(Message.register_limit, player, null);
                } else if (split.length < 2) {
                    Messages.sendMessage(Message.register_usage, player, null);
                } else if (split.length < 3 && email) {
                    Messages.sendMessage(Message.email_required, player, null);
                } else if ((split.length >= 3 && email) && (!this.plugin.checkEmail(split[2]))) {
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
                            if (this.plugin.register(player, split[1], themail, Util.craftFirePlayer.getIP(player))) {
                                Processes.Login(player);
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
                Util.logging.debug(player.getName() + " register ********");
                event.setMessage(Config.commands_user_register + " *****");
                event.setCancelled(true);
            } else { Messages.sendMessage(Message.protection_denied, player, null); }
        } else if (!plugin.isAuthorized(player)) {
            if (!checkGuest(player,Config.guests_commands)) {
                event.setMessage("/iamnotloggedin");
                event.setCancelled(true);
            }
        }
        long stop = Util.timeMS();
        Util.logging.timeUsage(stop - start, "process a command");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!plugin.isAuthorized(event.getPlayer())) {
            Location test = new Location(event.getPlayer().getWorld(), event.getFrom().getX(), event.getFrom().getY() - 1, event.getFrom().getZ());
            if (test.getBlock().getTypeId() != 0 && !checkGuest(event.getPlayer(),Config.guests_movement)) {
                if (this.plugin.AuthDB_JoinTime.containsKey(event.getPlayer().getName())) {
                    if (Config.protection_freeze) {
                        long jointime = this.plugin.AuthDB_JoinTime.get(event.getPlayer().getName());
                        if (jointime + Config.protection_freeze_delay < Util.timeStamp()) {
                            this.plugin.AuthDB_JoinTime.remove(event.getPlayer().getName());
                        }
                    }
                } else {
                    event.getPlayer().teleport(event.getFrom());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!AuthDB.isAuthorized(event.getPlayer())) {
            Player player = event.getPlayer();
                if (Util.toLoginMethod(Config.login_method).equalsIgnoreCase("prompt")) {
                    if (this.plugin.isRegistered("chat", event.getPlayer().getName()) || this.plugin.isRegistered("chat",Util.checkOtherName(event.getPlayer().getName()))) {
                        String[] split = event.getMessage().split(" ");
                        if (ZPermissions.isAllowed(player, ZPermissions.ZPermission.command_login)) {
                            Messages.sendMessage(Message.login_processing, player, null);
                            if (this.plugin.isRegistered("chatprompt", player.getName()) || this.plugin.isRegistered("chatprompt",Util.checkOtherName(player.getName()))) {
                                if (AuthDB.isAuthorized(player)) {
                                    Messages.sendMessage(Message.login_authorized, player, null);
                                } else if (split.length > 1) {
                                    Messages.sendMessage(Message.login_prompt, player, null);
                                } else if (this.plugin.checkPassword(Util.checkOtherName(player.getName()), split[0])) {
                                    Processes.Login(player);
                                    Messages.sendMessage(Message.login_success, player, null);
                                } else {
                                    Messages.sendMessage(Message.login_failure, player, null);
                                }
                                Util.logging.debug(player.getName() + " login ********");
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!plugin.isAuthorized(event.getPlayer())) {
            if (!checkGuest(event.getPlayer(),Config.guests_pickup)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.isAuthorized(event.getPlayer())) {
            if (!checkGuest(event.getPlayer(),Config.guests_interact)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!plugin.isAuthorized(event.getPlayer())) {
            if (this.plugin.isRegistered("dropitem", event.getPlayer().getName()) || this.plugin.isRegistered("dropitem",Util.checkOtherName(event.getPlayer().getName()))) {
                event.setCancelled(true);
            } else if (!checkGuest(event.getPlayer(), Config.guests_drop)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1) {
            event.getPlayer().setHealth(20);
            event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
        }
    }

    boolean checkGuest(Player player,boolean what) {
        if (what) {
            if (this.plugin.isRegistered("checkguest", player.getName()) == false || this.plugin.isRegistered("checkguest", Util.checkOtherName(player.getName())) == false) {
                return true;
            }
        } else if (Config.protection_notify && !AuthDB.isAuthorized(player)) {
            if (!this.plugin.AuthDB_RemindLogin.containsKey(player.getName())) {
                this.plugin.AuthDB_RemindLogin.put(player.getName(), Util.timeStamp() + Config.protection_notify_delay);
                Messages.sendMessage(Message.protection_notauthorized, player, null);
            } else {
                if (this.plugin.AuthDB_RemindLogin.get(player.getName()) < Util.timeStamp()) {
                    Messages.sendMessage(Message.protection_notauthorized, player, null);
                    this.plugin.AuthDB_RemindLogin.put(player.getName(), Util.timeStamp() + Config.protection_notify_delay);
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
