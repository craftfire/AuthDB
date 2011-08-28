/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.persistence.PersistenceException;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.authdb.listeners.AuthDBBlockListener;
import com.authdb.listeners.AuthDBEntityListener;
import com.authdb.listeners.AuthDBInputListener;
import com.authdb.listeners.AuthDBPlayerListener;
import com.authdb.listeners.AuthDBScreenListener;
import com.authdb.listeners.AuthDBSpoutListener;
import com.authdb.plugins.ZCraftIRC;
import com.authdb.plugins.ZPermissions;
import com.authdb.plugins.ZPermissions.Permission;
import com.authdb.util.Config;
import com.authdb.util.encryption.Encryption;
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.authdb.util.Messages.Message;
import com.authdb.util.Processes;
import com.authdb.util.databases.EBean;
import com.authdb.util.databases.MySQL;
import com.avaje.ebean.EbeanServer;
import com.nijikokun.bukkit.Permissions.Permissions;

import com.ensifera.animosity.craftirc.CraftIRC;

public class AuthDB extends JavaPlugin {
    //
    public String configFolder;
    public String logFolder;
    //
    public static org.bukkit.Server server;
    public static AuthDB plugin;
    public static EbeanServer database;
    public PluginDescriptionFile pluginFile = getDescription();
    public static String pluginName, pluginVersion, pluginWebsite, pluginDescrption;
    public static CraftIRC craftircHandle;
    //
    private final AuthDBPlayerListener playerListener = new AuthDBPlayerListener(this);
    private final AuthDBBlockListener blockListener = new AuthDBBlockListener(this);
    private final AuthDBEntityListener entityListener = new AuthDBEntityListener(this);
    private AuthDBSpoutListener spoutListener;
    private AuthDBScreenListener screenListener;
    private AuthDBInputListener inputListener;
    public static List<String> authorizedNames = new ArrayList<String>();
    public static HashMap<String, Integer> AuthDB_Timeouts = new HashMap<String, Integer>();
    public static HashMap<String, Long> AuthDB_Sessions = new HashMap<String, Long>();
    public static HashMap<String, String> AuthDB_Authed = new HashMap<String, String>();
    public static HashMap<String, Long> AuthDB_AuthTime = new HashMap<String, Long>();
    public static HashMap<String, Long> AuthDB_RemindLogin = new HashMap<String, Long>();
    public static HashMap<String, Integer> AuthDB_SpamMessage = new HashMap<String, Integer>();
    public static HashMap<String, Long> AuthDB_SpamMessageTime = new HashMap<String, Long>();
    public static HashMap<String, Long> AuthDB_JoinTime = new HashMap<String, Long>();
    public static HashMap<String, String> AuthDB_PasswordTries = new HashMap<String, String>();
    public static HashMap<String, String> AuthDB_LinkedNames = new HashMap<String, String>();
    public static HashMap<String, String> AuthDB_LinkedNameCheck = new HashMap<String, String>();
    public static HashMap<String, UUID> AuthDB_GUI_PasswordFieldIDs = new HashMap<String, UUID>();
    public static HashMap<String, UUID> AuthDB_GUI_ScreenIDs = new HashMap<String, UUID>();
    public static HashMap<String, UUID> AuthDB_GUI_ErrorFieldIDs = new HashMap<String, UUID>();
    public static HashMap<String, String> AuthDB_GUI_TempPasswords = new HashMap<String, String>();
    public static Logger log = Logger.getLogger("Minecraft");

    public void onDisable() {
        for (Player p : getServer().getOnlinePlayers()) {
            EBean eBeanClass = EBean.checkPlayer(p, true);
            if (eBeanClass.getAuthorized() != null && eBeanClass.getAuthorized().equalsIgnoreCase("true")) {
                eBeanClass.setReloadtime(Util.timeStamp());
                AuthDB.database.save(eBeanClass);
            }
            Processes.Logout(p);
        }
        Util.logging.Info(pluginName + " plugin " + pluginVersion + " has been disabled");
        Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
        if ((checkCraftIRC != null) && (checkCraftIRC.isEnabled()) && (Config.CraftIRC_enabled == true)) {
            ZCraftIRC.sendMessage(Message.OnDisable, null);
        }
        authorizedNames.clear();
        AuthDB_AuthTime.clear();
        AuthDB_GUI_ScreenIDs.clear();
        AuthDB_RemindLogin.clear();
        AuthDB_SpamMessage.clear();
        AuthDB_SpamMessageTime.clear();
        AuthDB_GUI_PasswordFieldIDs.clear();
        AuthDB_GUI_ErrorFieldIDs.clear();
        AuthDB_JoinTime.clear();
        AuthDB_LinkedNames.clear();
        AuthDB_LinkedNameCheck.clear();
        AuthDB_PasswordTries.clear();
        AuthDB_GUI_TempPasswords.clear();
        AuthDB_Timeouts.clear();
        AuthDB_Sessions.clear();
        AuthDB_Authed.clear();
        Util.databaseManager.close();
     }

    public void onEnable() {        
        plugin = this;
        setupPluginInformation();
        server = getServer();
        database = getDatabase();
        Plugin[] plugins = server.getPluginManager().getPlugins();
        //logging.Debug(System.getProperty("java.version"));
        /*logging.Debug(System.getProperty("java.io.tmpdir"));
        Util.logging.Debug(System.getProperty("java.library.path"));
        Util.logging.Debug(System.getProperty("java.class.path"));
        Util.logging.Debug(System.getProperty("user.home"));
        Util.logging.Debug(System.getProperty("user.dir"));
        Util.logging.Debug(System.getProperty("user.name"));
        Util.ErrorFile("HELLO"); */
        int counter = 0;
        StringBuffer Plugins = new StringBuffer();
        while (plugins.length > counter) {
            Plugins.append(plugins[counter].getDescription().getName() + "&_&" + plugins[counter].getDescription().getVersion());
            if (plugins.length != (counter + 1)) {
                Plugins.append("*_*");
            }
            counter++;
        }
        File f = new File("plugins/" + pluginName + "/config/basic.yml");
        if (!f.exists()) {
            Util.logging.Info("basic.yml could not be found in plugins/AuthDB/config/! Creating basic.yml!");
            DefaultFile("basic.yml", "config");
        }
        new Config("basic", "plugins/" + pluginName + "/config/", "basic.yml");
        
        f = new File("plugins/" + pluginName + "/config/advanced.yml");
        if (!f.exists()) {
            Util.logging.Info("advanced.yml could not be found in plugins/AuthDB/config/! Creating advanced.yml!");
            DefaultFile("advanced.yml", "config");
        }
        new Config("advanced", "plugins/" + pluginName + "/config/", "advanced.yml");
        
        f = new File("plugins/" + pluginName + "/config/plugins.yml");
        if (!f.exists()) {
            Util.logging.Info("plugins.yml could not be found in plugins/AuthDB/config/! Creating plugins.yml!");
            DefaultFile("plugins.yml", "config");
        }
        new Config("plugins", "plugins/" + pluginName + "/config/", "plugins.yml");
        
        f = new File(getDataFolder() + "/config/customdb.sql");
        if (!f.exists()) {
            Util.logging.Info("customdb.sql could not be found in plugins/AuthDB/config/! Creating customdb.sql!");
            DefaultFile("customdb.sql", "config");
        }
        
        LoadYml("messages", getClass().getProtectionDomain().getCodeSource());
        LoadYml("commands", getClass().getProtectionDomain().getCodeSource());
        setupDatabase();
        checkOldFiles();
          final Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
          CheckPermissions();
          if ((checkCraftIRC != null) && (Config.CraftIRC_enabled == true)) {
              craftircHandle = ((CraftIRC)checkCraftIRC);
              Util.logging.Info("Found supported plugin: " + checkCraftIRC.getDescription().getName());
              this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
              @Override
              public void run() { 
                  if (checkCraftIRC.isEnabled()) { 
                      ZCraftIRC.sendMessage(Message.OnEnable, null); } 
                  } 
              }, 100);
          } else {
              Util.logging.Debug("Server is running without CraftIRC.");
          }
          PluginManager pm = getServer().getPluginManager();
          Plugin check = getServer().getPluginManager().getPlugin("Backpack");
          if (check != null) { 
              Config.hasBackpack = true;
              Util.logging.Info("Found supported plugin " + check.getDescription().getName() + " " + check.getDescription().getVersion());
          } else {
              Util.logging.Debug("Server is running without Backpack.");
          }
          check = getServer().getPluginManager().getPlugin("BukkitContrib");
          if (check != null) { 
              Config.hasBukkitContrib = true; 
              Util.logging.Info("Found supported plugin " + check.getDescription().getName() + " " + check.getDescription().getVersion());
          } else {
              Util.logging.Debug("Server is running without BukkitContrib.");
          }
          check = getServer().getPluginManager().getPlugin("Buildr");
          if (check != null) { 
              Config.hasBuildr = true; 
              Util.logging.Info("Found supported plugin " + check.getDescription().getName() + " " + check.getDescription().getVersion());
          } else {
              Util.logging.Debug("Server is running without Buildr.");
          }
          check = getServer().getPluginManager().getPlugin("Spout");
          if (check != null) { 
              spoutListener = new AuthDBSpoutListener(this);
              screenListener = new AuthDBScreenListener(this);
              inputListener = new AuthDBInputListener(this);
              pm.registerEvent(Event.Type.CUSTOM_EVENT, this.spoutListener, Priority.Low, this);
              pm.registerEvent(Event.Type.CUSTOM_EVENT, this.screenListener, Priority.Low, this);
              pm.registerEvent(Event.Type.CUSTOM_EVENT, this.inputListener, Priority.Low, this);
              Config.hasSpout = true; 
              Util.logging.Info("Found supported plugin " + check.getDescription().getName() + " " + check.getDescription().getVersion());
          } else {
              Util.logging.Debug("Server is running without Spout.");
          }
        pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, this.playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_KICK, this.playerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, this.blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_TARGET, this.entityListener, Event.Priority.Normal, this);
        
        Config.onlineMode = getServer().getOnlineMode();

        Util.logging.Debug("Online mode: " + Config.onlineMode);
        updateLinkedNames();
        
        Util.databaseManager.connect();
        try { Util.checkScript("numusers", Config.script_name, null, null, null, null); }
        catch (SQLException e) {
            if (Config.custom_enabled && Config.custom_autocreate) {
                String enter = "\n";
                Util.logging.Info("Creating default table schema for " + Config.custom_table);
                
                StringBuilder query = new StringBuilder();
                String NL = System.getProperty("line.separator");
                try {
                    Scanner scanner = new Scanner(new FileInputStream(getDataFolder() + "/config/customdb.sql"));
                  while (scanner != null && scanner.hasNextLine()) {
                      String line = scanner.nextLine();
                      if (line.contains("CREATE TABLE") || line.contains("create table")) {
                          query.append("CREATE TABLE IF NOT EXISTS `" + Config.custom_table + "` (" + NL);
                      } else {
                          query.append(line + NL);
                      }
                  }
                  scanner.close();
                } catch (FileNotFoundException e2) {
                    Util.logging.StackTrace(e2.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
                } 
                
                Util.logging.Debug(enter + query);
                try {
                    MySQL.query("" + query);
                    Util.logging.Info("Sucessfully created table " + Config.custom_table);
                    PreparedStatement ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `" + Config.custom_table + "`");
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) { 
                        Util.logging.Info(rs.getInt("countit") + " user registrations in database"); 
                    }
                    ps.close();
                } catch (SQLException e1) {
                    Util.logging.error("Failed creating user table " + Config.custom_table);
                    Config.authdb_enabled = false;
                    Util.logging.StackTrace(e1.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
                }
            } else {
                Util.logging.error("Could not find script's tables in database: " + Config.database_database + "!");
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                Config.authdb_enabled = false;
                // getServer().getPluginManager().disablePlugin(((org.bukkit.plugin.Plugin) (this)));
            }
            
        }
        
        Util.logging.Info(pluginName + " plugin " + pluginVersion + " is enabled");
        Util.logging.Debug("Debug is ENABLED, get ready for some heavy spam");
        if (Config.custom_enabled) {
            if (Config.custom_encryption == null) { 
                Util.logging.Info("**WARNING** SERVER IS RUNNING WITH NO ENCRYPTION: PASSWORDS ARE STORED IN PLAINTEXT");
            }
        }
        Util.logging.Info(pluginName + " is developed by CraftFire <dev@craftfire.com>");

        String thescript = "",theversion = "";
        if (Config.custom_enabled) { 
            thescript = "custom"; 
        } else {
            thescript = Config.script_name;
            theversion = Config.script_version;
        }
        String online = "" + getServer().getOnlinePlayers().length;
        String max = "" + getServer().getMaxPlayers();
        if (Config.usagestats_enabled) {
            try { Util.craftFire.postInfo(getServer().getServerName(),getServer().getVersion(),pluginVersion,System.getProperty("os.name"),System.getProperty("os.version"),System.getProperty("os.arch"),System.getProperty("java.version"),thescript,theversion,Plugins.toString(),online,max,server.getPort()); }
            catch (IOException e1) { 
                Util.logging.Debug("Could not send usage stats to main server.");
                }
        }
        for (Player p : getServer().getOnlinePlayers()) {
            EBean eBeanClass = EBean.checkPlayer(p, true);
            if (eBeanClass.getReloadtime() + 30 > Util.timeStamp()) {
                Processes.Login(p);
            }
        }
    }
    
    public String commandString(String command) {
        if (command.contains(" ")) {
            String[] temp = command.split(" ");
            if (temp.length > 0) {
                command =  temp[0].replaceAll("/", "");
            }
        } else { 
            command = command.replaceAll("/", ""); 
        }
        return command;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)  { 
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("authdb")) {
                if (args.length == 0) {
                    player.sendMessage("§b Name: §f " + pluginName + " §4 " + pluginVersion);
                    player.sendMessage("§b " + pluginName + " is developed by §4 CraftFire �e<dev@craftfire.com>");
                    player.sendMessage("§d " + pluginWebsite);
                    return true;
                }
            } else if (cmd.getName().equalsIgnoreCase(commandString(Config.commands_reload)) || cmd.getName().equalsIgnoreCase(commandString(Config.aliases_reload))) {
                if (args.length == 1) {
                    if (ZPermissions.isAllowed(player, Permission.command_admin_reload)) {
                        new Config("config", "plugins/" + pluginName + "/config/", "config.yml");
                        LoadYml("commands", getClass().getProtectionDomain().getCodeSource());
                        LoadYml("messages", getClass().getProtectionDomain().getCodeSource());
                        player.sendMessage("§a AuthDB has been successfully reloaded!");
                        Messages.sendMessage(Message.reload_success, player, null);
                        return true;
                    }
                    else { Messages.sendMessage(Message.protection_denied, player, null); }
                }
            } else if (cmd.getName().equalsIgnoreCase(commandString(Config.commands_logout)) || cmd.getName().equalsIgnoreCase(commandString(Config.aliases_logout))) {
                if (args.length == 0) {
                    if (ZPermissions.isAllowed(player, Permission.command_logout)) {
                        if (Processes.Logout(player)) {
                            EBean eBeanClass = EBean.checkPlayer(player, true);
                            eBeanClass.setSessiontime(0);
                            getDatabase().save(eBeanClass);
                            String check = Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player));
                            if (AuthDB.AuthDB_Sessions.containsKey(check)) {
                                AuthDB_Sessions.remove(check);
                            }
                            Messages.sendMessage(Message.logout_success, player, null);
                            return true;
                        } else {
                            Messages.sendMessage(Message.logout_failure, player, null);
                            return true;
                        }
                    }
                    else { Messages.sendMessage(Message.protection_denied, player, null); }
                } else if (args.length == 1) {
                    if(ZPermissions.isAllowed(player, Permission.command_admin_logout)) {
                        String PlayerName = args[0];
                        List<Player> players = sender.getServer().matchPlayer(PlayerName);
                        if (!players.isEmpty()) {
                            if (Processes.Logout(players.get(0))) {
                                Messages.sendMessage(Message.logout_admin_success, player, null, players.get(0).getName());
                                Messages.sendMessage(Message.logout_admin, players.get(0), null);
                                return true;
                            } else {
                                Messages.sendMessage(Message.logout_admin_failure, player, null, players.get(0).getName());
                                return true;
                            }
                        }
                        Messages.sendMessage(Message.logout_admin_notfound, player, null, PlayerName);
                        return true;
                    }
                    else { Messages.sendMessage(Message.protection_denied, player, null); }
                }
            } else if (isAuthorized(player) && (cmd.getName().equalsIgnoreCase(commandString(Config.commands_login)) || cmd.getName().equalsIgnoreCase(commandString(Config.aliases_login)))) {
                if (ZPermissions.isAllowed(player, Permission.command_admin_login)) {
                    if (args.length == 1) {
                    String PlayerName = args[0];
                    List<Player> players = sender.getServer().matchPlayer(PlayerName);
                    if (!players.isEmpty()) {
                        if (Processes.Logout(players.get(0))) {
                            Messages.sendMessage(Message.login_admin_success, player, null, players.get(0).getName());
                            Messages.sendMessage(Message.login_admin, players.get(0), null);
                            return true;
                        } else {
                            Messages.sendMessage(Message.login_admin_failure, player, null, players.get(0).getName());
                            return true;
                        }
                    }
                    Messages.sendMessage(Message.login_admin_notfound, player, null, PlayerName);
                    return true;
                    }
                } else { Messages.sendMessage(Message.protection_denied, player, null); }
            }
        }
        return false;
    }
    
    private void setupDatabase() {
        try {
            getDatabase().find(EBean.class).findRowCount();
        } catch (PersistenceException ex) {
            Util.logging.Info("Installing persistence database for " + pluginName + " due to first time usage");
            installDDL();
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(EBean.class);
        return list;
    }
        
    
    void CheckPermissions() {
        Plugin Check1 = getServer().getPluginManager().getPlugin("Permissions");
        if (Check1 != null) {
            ZPermissions.hasPlugin = true;
        }
        
        Plugin Check2 = getServer().getPluginManager().getPlugin("PermissionsBukkit");
        if (Check2 != null) {
            if (ZPermissions.hasPlugin) {
                Util.logging.Info("Found 2 supported permissions plugins: " + Check1.getDescription().getName() + " " + Check1.getDescription().getVersion() + " and " + Check2.getDescription().getName() + " " + Check2.getDescription().getVersion());
                Util.logging.Info("Defaulting permissions to: " + Check2.getDescription().getName() + " " + Check2.getDescription().getVersion());
            } else {
                Util.logging.Info("Found supported plugin: " + Check2.getDescription().getName() + " " + Check2.getDescription().getVersion());
            }
            ZPermissions.hasPermissionsBukkit = true;
        } else {
            if (ZPermissions.hasPlugin) {
                ZPermissions.permissionsHandler = ((Permissions)Check1).getHandler();
                Util.logging.Info("Found supported plugin: " + Check1.getDescription().getName() + " " + Check1.getDescription().getVersion());
            } else {
                Util.logging.Info("Could not load a permissions plugin, going over to OP!");
            }
        }
    }

    void checkOldFiles() {
        File data = new File(getDataFolder() + "/data/", "");
        if (!data.exists()) { 
            if (data.mkdir()) {
                Util.logging.Debug("Created missing directory: " + getDataFolder() + "\\data\\");
            }
        }
        data = new File(getDataFolder() + "/translations/", "");
        if (!data.exists()) { 
            if (data.mkdir()) {
                Util.logging.Debug("Created missing directory: " + getDataFolder() + "\\translations\\");
            }
        }
        data = new File(getDataFolder() + "/config/", "");
        if (!data.exists()) { 
            if (data.mkdir()) {
                Util.logging.Debug("Created missing folder: " + getDataFolder() + "\\config\\");
            }
        }
        data = new File(getDataFolder() + "/", "othernames.db");
        if (data.exists()) { 
            try {
                FileInputStream fstream = new FileInputStream(getDataFolder() + "/othernames.db");
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                String[] split;
                int count = 0;
                EBean eBeanClass = null;
                while ((strLine = br.readLine()) != null) {
                    split = strLine.split(":");
                    if(split.length == 2) {
                        count++;
                        Util.logging.Debug("Found linked name: " + split[0] + " linked with name: " + split[1]);
                        eBeanClass = EBean.checkPlayer(split[0], false);
                        eBeanClass.setLinkedname(split[1]);
                        database.save(eBeanClass);
                    }
                }
                in.close();
                if(count > 0) {
                    Util.logging.Debug("Successfully imported " + count + " linked names.");
                }
            } catch (Exception e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
            if (data.delete()) {
                Util.logging.Debug("Deleted file othernames.db from " + getDataFolder());
            }
        }
        data = new File(getDataFolder() + "/", "idle.db");
        if (data.exists()) { 
            if (data.delete()) {
                Util.logging.Debug("Deleted file idle.db from " + getDataFolder());
            }
        }
        data = new File(getDataFolder() + "/data/", "timeout.db");
        if (data.exists()) { 
            if (data.delete()) {
                Util.logging.Debug("Deleted file timeout.db from " + getDataFolder());
            }
        }
    }

    public static boolean isAuthorized(Player player)  { 
        if (authorizedNames.contains(player.getName())) { 
            return true; 
        }
        EBean eBeanClass = EBean.find(player,EBean.Column.authorized,"true");
        if (eBeanClass != null) {
            authorizedNames.add(player.getName()); 
            return true;
        }
        return false;
    }

    public boolean checkPassword(String player, String password) {
        long start = Util.timeMS();
        try {
            if (!Config.database_keepalive) { 
                Util.databaseManager.connect();
            }
            password = Matcher.quoteReplacement(password);
            if (!Util.checkOtherName(player).equals(player)) {
                player = Util.checkOtherName(player);
            }
            if (Util.checkScript("checkpassword",Config.script_name, player, password, null, null)) {
                long stop = Util.timeMS();
                Util.logging.timeUsage(stop - start, "check the password");
                return true;
            }
            if (!Config.database_keepalive) { 
                Util.databaseManager.close(); 
            }
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            Stop("ERROR in checking password. Plugin will NOT work. Disabling it.");
        }
        long stop = Util.timeMS();
        Util.logging.timeUsage(stop - start, "check the password");
        return false;
    }

    public boolean isWithinRange(int number, int around, int range) {
        int difference = Math.abs(around - number);
        return difference <= range;
    }

    void Stop(String error) {
        Util.logging.advancedWarning(error);
        getServer().getPluginManager().disablePlugin(((org.bukkit.plugin.Plugin) (this)));
    }

    public boolean register(Player theplayer, String password, String email, String ipAddress) throws IOException, SQLException {
        if (password.length() < Integer.parseInt(Config.password_minimum)) {
            Messages.sendMessage(Message.password_minimum, theplayer, null);
            return false;
        } else if (password.length() > Integer.parseInt(Config.password_maximum)) {
            Messages.sendMessage(Message.password_maximum, theplayer, null);
            return false;
        }
        if (!Config.database_keepalive) { 
            Util.databaseManager.connect();
        }
        String player = theplayer.getName();
        if (!Util.checkFilter("password",password)) {
            Messages.sendMessage(Message.filter_password, theplayer, null);
        } else {
            if(Util.checkScript("adduser", Config.script_name, player, password, email, ipAddress)) {
                Util.logging.Debug("Registred player: " + theplayer.getName());
            } else {
                Util.logging.Debug("Failed registring player: " + theplayer.getName());
                if (!Config.database_keepalive) { 
                    Util.databaseManager.close(); 
                }
                return false;
            }
        }
        if (!Config.database_keepalive) { 
            Util.databaseManager.close(); 
        }
        return true;
    }
    
    List<String> getResourceListing(String path) {
        InputStream in = getClass().getResourceAsStream(path);
        ArrayList<String> temp = new ArrayList<String>();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = rdr.readLine()) != null) {
                temp.add(line);
                System.out.println("file: " + line);
            }
            rdr.close();
        } catch (IOException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return temp;
    } 
    
    void LoadYml(String type, CodeSource src) {
        String Language = "English";
        File LanguagesAll = new File(getDataFolder() + "/translations");
        if(!LanguagesAll.exists()) {
            if(LanguagesAll.mkdir()) {
                Util.logging.Debug("Sucesfully created directory: "+LanguagesAll);
            }
        }
        boolean Set = false;
        File[] directories;
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        
        if (src != null) {
            try {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry ze = null;
                
                while ((ze = zip.getNextEntry()) != null) {
                    String directory = ze.getName();
                    if (directory.startsWith("files/translations/") && directory.endsWith(".yml") == false)  {
                    	directory = directory.replace("files/translations/", "");
                    	directory = directory.replace("/", "");
                    	if(directory.equals("") == false) {
                    		Util.logging.Debug("Directory: "+directory);
	                        File f = new File(getDataFolder() + "/translations/" + directory + "/" + type + ".yml");
	                        if (!f.exists()) {
	                            Util.logging.Info(type + ".yml" + " could not be found in plugins/AuthDB/translations/" + directory + "/! Creating " + type + ".yml");
	                            DefaultFile(type + ".yml","translations/" + directory + "");
	                        }
	                        if ((Config.language).equalsIgnoreCase(directory))  { 
	                            Set = true;
	                            Language = directory; 
	                        } 
                    	}
                    }
                }
                zip.close();
            } catch (IOException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
        }
        
        directories = LanguagesAll.listFiles(fileFilter);
        if(directories.length > 0) { Util.logging.Debug("Found " + directories.length + " directories for " + type); }
        else { Util.logging.error("Error! Could not find any directories for " + type); }
        if (!Set) {
            for (int z=0; z<directories.length; z++) {
                if (Config.language.equalsIgnoreCase(directories[z].getName()))  { 
                    Set = true;
                    Language = directories[z].getName(); 
                }
            }
        }
        if (!Set) { Util.logging.Info("Could not find translation files for " + Config.language + ", defaulting to " + Language); } 
        else { Util.logging.Debug(type + " language set to " + Language); }
        new Config(type, getDataFolder() + "/translations/" + Language + "/", type + ".yml");
    }    
    
    public boolean isRegistered(String when, String player) {
        boolean dupe = false;
        boolean checkneeded = true;
        Util.logging.Debug("Checking if player " + player + " is registred.");
        EBean eBeanClass = EBean.checkPlayer(player, true);
        if(eBeanClass.getRegistred().equalsIgnoreCase("true")) {
            if (when.equalsIgnoreCase("join")) {
                if (!Config.database_keepalive) { Util.databaseManager.connect(); }
                Config.hasForumBoard = false;
                try {
                    if (Util.checkScript("checkuser",Config.script_name, player, null, null, null)) {
                        AuthDB_Authed.put(Encryption.md5(player), "yes");
                        dupe = true;
                    }
                } catch (SQLException e) {
                    Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                }
                if (!Config.database_keepalive) { 
                    Util.databaseManager.close(); 
                }
                if (!dupe) {
                    AuthDB_Authed.put(Encryption.md5(player), "no");
                }
                return dupe;
            } else if (when.equalsIgnoreCase("command")) {
                if (!Config.database_keepalive) { Util.databaseManager.connect(); }
                Config.hasForumBoard = false;
                try {
                    if (Util.checkScript("checkuser",Config.script_name, player, null, null, null)) {
                        AuthDB_Authed.put(Encryption.md5(player), "yes");
                        dupe = true;
                    } else if (Util.checkOtherName(player) != player) {
                        AuthDB_Authed.put(Encryption.md5(player), "yes");
                        dupe = true;
                    }
                } catch (SQLException e) {
                    Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                }
                if (!Config.database_keepalive) {
                    Util.databaseManager.close(); 
                }
                if (!dupe) {
                    AuthDB_Authed.put(Encryption.md5(player), "no");
                }
                return dupe;
            } else {
                if (this.AuthDB_Authed.containsKey(Encryption.md5(player))) {
                    String check = AuthDB_Authed.get(Encryption.md5(player));
                    if (check.equalsIgnoreCase("yes")) {
                        checkneeded = false;
                        return true;
                    } else if (check.equalsIgnoreCase("no")) {
                        return false;
                    }
                } else if (checkneeded) {
                    Util.logging.Debug("Check to see if user is registred is needed, performing check");
                    try {
                        if (!Config.database_keepalive) { Util.databaseManager.connect(); }
                        Config.hasForumBoard = false;
                        if (Util.checkScript("checkuser", Config.script_name, player, null, null, null)) {
                            AuthDB_Authed.put(Encryption.md5(player), "yes");
                            dupe = true;
                        }
                        if (!Config.database_keepalive) { 
                            Util.databaseManager.close(); 
                        }
                        if (!dupe) {
                            AuthDB_Authed.put(Encryption.md5(player), "no");
                        }
                        return dupe;
                    } catch (SQLException e) {
                        Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                        Stop("ERRORS in checking user. Plugin will NOT work. Disabling it.");
                    }
                }
            }
        }
        return false;
    }


    public static boolean checkEmail(String email) {
        Util.logging.Debug("Validating email: " + email);
          Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
          Matcher m = p.matcher(email);
          boolean Matches = m.matches();
          if (Matches) {
            Util.logging.Debug("Email validation: passed!");
            return true;
          } else {
            Util.logging.Debug("Email validation: failed!");
            return false;
          }
    }

    public void updateLinkedNames() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            String name = Util.checkOtherName(player.getName());
            if (!name.equals(player.getName())) {
                Util.craftFirePlayer.renamePlayer(player, name);
            }
        }
    }

    void setupPluginInformation() {
        pluginName = getDescription().getName();
        pluginVersion = getDescription().getVersion();
        pluginWebsite = getDescription().getWebsite();
        pluginDescrption = getDescription().getDescription();
        configFolder = getDataFolder() + "\\config\\";
    }

     private void DefaultFile(String name, String folder) {
            File actual = new File(getDataFolder() + "/" + folder + "/", name);
            File direc = new File(getDataFolder() + "/" + folder + "/", "");
            if (!direc.exists()) { 
                if(direc.mkdir()) {
                    Util.logging.Debug("Sucesfully created directory: "+direc);
                }
            }
            if (!actual.exists()) {
              java.io.InputStream input = getClass().getResourceAsStream("/files/" + folder + "/" + name);
              if (input != null) {
                  java.io.FileOutputStream output = null;
                try {
                  output = new java.io.FileOutputStream(actual);
                  byte[] buf = new byte[8192];
                  int length = 0;

                  while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                  }

                  System.out.println("[" + pluginName + "] Written default setup for " + name);
                } catch (Exception e) {
                  Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                } finally {
                  try {
                    if (input != null) {
                        input.close();
                    }
                  } catch (Exception e) {
                      Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                  }
                  try {
                    if (output != null) {
                      output.close();
                    }
                  } catch (Exception e) {
                      Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                  }
                }
              }
            }
          }

}
