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
package com.craftfire.authdb;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.persistence.PersistenceException;

import com.craftfire.authdb.plugins.ZPermissions;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;

import com.craftfire.authdb.listeners.AuthDBBlockListener;
import com.craftfire.authdb.listeners.AuthDBEntityListener;
import com.craftfire.authdb.listeners.AuthDBPlayerListener;
import com.craftfire.authdb.listeners.AuthDBServerListener;
import com.craftfire.authdb.util.Config;
import com.craftfire.authdb.util.Messages;
import com.craftfire.authdb.util.Messages.Message;
import com.craftfire.authdb.util.Processes;
import com.craftfire.authdb.util.Util;
import com.craftfire.authdb.util.databases.EBean;
import com.craftfire.authdb.util.databases.MySQL;
import com.craftfire.authdb.util.encryption.Encryption;

public class AuthDB extends JavaPlugin {
    public String configFolder;
    public String logFolder;

    public static org.bukkit.Server server;
    public static AuthDB plugin;
    public static EbeanServer database;
    public PluginDescriptionFile pluginFile = getDescription();
    public static String pluginName, pluginVersion, pluginWebsite, pluginDescrption;
    public static List<String> authorizedNames = new ArrayList<String>();
    public static HashMap<String, Integer> AuthDB_Timeouts = new HashMap<String, Integer>();
    public static HashMap<String, Long> AuthDB_Sessions = new HashMap<String, Long>();
    public static HashMap<String, String> AuthDB_Authed = new HashMap<String, String>();
    public static HashMap<String, Boolean> AuthDB_loggedOut = new HashMap<String, Boolean>();
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
    public static boolean stop = false;

    private FileConfiguration basicConfig = null, advancedConfig = null, messagesConfig = null, commandsConfig = null;
    private File basicConfigurationFile = null, advancedConfigurationFile = null, messagesConfigurationFile = null, commandsConfigurationFile = null;

    private static Permission permission = null;

    public void onDisable() {
        for (Player p : getServer().getOnlinePlayers()) {
            EBean eBeanClass = EBean.checkPlayer(p, true);
            if (eBeanClass.getAuthorized() != null && eBeanClass.getAuthorized().equalsIgnoreCase("true")) {
                eBeanClass.setReloadtime(Util.timeStamp());
                AuthDB.database.save(eBeanClass);
                Processes.Logout(p, false);
            } else if (isRegistered("disable", p.getName()) && AuthDB_loggedOut.containsKey(p.getName()) && stop) {
                Util.craftFirePlayer.setInventoryFromStorage(p);
            } else {
                Processes.Logout(p, false);
            }
        }
        Util.logging.info(pluginVersion + " has been disabled");
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
        AuthDB_loggedOut.clear();
        Util.databaseManager.close();
     }

    public void onEnable() {
        plugin = this;
        setupPluginInformation();
        server = getServer();
        database = getDatabase();
        Plugin[] plugins = server.getPluginManager().getPlugins();
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
            Util.logging.info("basic.yml could not be found in plugins/AuthDB/config/! Creating basic.yml!");
            DefaultFile("basic.yml", "config");
        }
        new Config(this, "basic", "plugins/" + pluginName + "/config/", "basic.yml");

        f = new File("plugins/" + pluginName + "/config/advanced.yml");
        if (!f.exists()) {
            Util.logging.info("advanced.yml could not be found in plugins/AuthDB/config/! Creating advanced.yml!");
            DefaultFile("advanced.yml", "config");
        }
        new Config(this, "advanced", "plugins/" + pluginName + "/config/", "advanced.yml");

        f = new File(getDataFolder() + "/config/customdb.sql");
        if (!f.exists()) {
            Util.logging.info("customdb.sql could not be found in plugins/AuthDB/config/! Creating customdb.sql!");
            DefaultFile("customdb.sql", "config");
        }

        LoadYml("messages", getClass().getProtectionDomain().getCodeSource());
        LoadYml("commands", getClass().getProtectionDomain().getCodeSource());
        setupDatabase();
        checkOldFiles();
        PluginManager pm = getServer().getPluginManager();
        Plugin check = getServer().getPluginManager().getPlugin("Backpack");
        if (check != null) {
          Config.hasBackpack = true;
          Util.logging.info("Found supported plugin " + check.getDescription().getName() + " " + check.getDescription().getVersion());
        } else {
          Util.logging.debug("Server is running without Backpack.");
        }
        check = getServer().getPluginManager().getPlugin("Buildr");
        if (check != null) {
          Config.hasBuildr = true;
          Util.logging.info("Found supported plugin " + check.getDescription().getName() + " " + check.getDescription().getVersion());
        } else {
          Util.logging.debug("Server is running without Buildr.");
        }
        if (!setupPermissions()) {
            Util.logging.error("Vault could not be found, AuthDB is therefore being disabled. Please install Vault to use AuthDB.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final AuthDBPlayerListener playerListener = new AuthDBPlayerListener(this);
        pm.registerEvents(playerListener, this);

        final AuthDBEntityListener entityListener = new AuthDBEntityListener(this);
        pm.registerEvents(entityListener, this);

        final AuthDBBlockListener blockListener = new AuthDBBlockListener(this);
        pm.registerEvents(blockListener, this);

        final AuthDBServerListener serverListener = new AuthDBServerListener();
        pm.registerEvents(serverListener, this);

        Config.onlineMode = getServer().getOnlineMode();

        Util.logging.debug("Online mode: " + Config.onlineMode);
        updateLinkedNames();

        Util.databaseManager.connect();
        try { Util.checkScript("numusers", Config.script_name, null, null, null, null); }
        catch (SQLException e) {
            if (Config.custom_enabled && Config.custom_autocreate) {
                String enter = "\n";
                Util.logging.info("Creating default table schema for " + Config.custom_table);

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

                Util.logging.debug(enter + query);
                try {
                    MySQL.query("" + query);
                    Util.logging.info("Sucessfully created table " + Config.custom_table);
                    PreparedStatement ps = MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `" + Config.custom_table + "`");
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        Util.logging.info("Found " + rs.getInt("countit") + " user registrations in the database.");
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
                //getServer().getPluginManager().disablePlugin(((org.bukkit.plugin.Plugin) (this)));
            }

        }

        Util.logging.info(pluginVersion + " is enabled");
        Util.logging.debug("Debug is ENABLED, get ready for some heavy spam");
        if (Config.custom_enabled) {
            if (Config.custom_encryption == null) {
                Util.logging.info("**WARNING** SERVER IS RUNNING WITH NO ENCRYPTION: PASSWORDS ARE STORED IN PLAINTEXT");
            }
        }
        Util.logging.info("developed by CraftFire <dev@craftfire.com>");

        String thescript = "", theversion = "";
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
                Util.logging.debug("Could not send usage stats to main server.");
            }
        }
        for (Player p : getServer().getOnlinePlayers()) {
            EBean eBeanClass = EBean.checkPlayer(p, true);
            if (eBeanClass.getReloadtime() + 30 > Util.timeStamp()) {
                Processes.Login(p);
            }
        }
    }

    public void reloadCustomConfig(String config) {
        if (config.equalsIgnoreCase("basic")) {
            if (basicConfigurationFile == null) {
                basicConfigurationFile = new File("plugins/" + pluginName + "/config/", "basic.yml");
            }
            basicConfig = YamlConfiguration.loadConfiguration(basicConfigurationFile);
            InputStream defConfigStream = getResource("/files/config/basic.yml");
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                basicConfig.setDefaults(defConfig);
            }
        } else if (config.equalsIgnoreCase("advanced")) {
            if (advancedConfigurationFile == null) {
                advancedConfigurationFile = new File("plugins/" + pluginName + "/config/", "advanced.yml");
            }
            advancedConfig = YamlConfiguration.loadConfiguration(advancedConfigurationFile);
            InputStream defConfigStream = getResource("/files/config/advanced.yml");
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                advancedConfig.setDefaults(defConfig);
            }
        } else if (config.equalsIgnoreCase("messages")) {
            if (messagesConfigurationFile == null) {
                messagesConfigurationFile = new File("plugins/" + pluginName + "/translations/" + Config.language_messages + "/", "messages.yml");
            }
            messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigurationFile);
            InputStream defConfigStream = getResource("/files/translations/" + Config.language_messages + "/messages.yml");
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                messagesConfig.setDefaults(defConfig);
            }
        } else if (config.equalsIgnoreCase("commands")) {
            if (commandsConfigurationFile == null) {
                commandsConfigurationFile = new File("plugins/" + pluginName + "/translations/" + Config.language_messages + "/", "commands.yml");
            }
            commandsConfig = YamlConfiguration.loadConfiguration(commandsConfigurationFile);
            InputStream defConfigStream = getResource("/files/translations/" + Config.language_messages + "/commands.yml");
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                commandsConfig.setDefaults(defConfig);
            }
        }
    }

    public FileConfiguration getBasicConfig() {
        if (basicConfig == null) {
            reloadCustomConfig("basic");
        }
        return basicConfig;
    }

    public FileConfiguration getAdvancedConfig() {
        if (advancedConfig == null) {
            reloadCustomConfig("advanced");
        }
        return advancedConfig;
    }

    public FileConfiguration getMessagesConfig() {
        if (messagesConfig == null) {
            reloadCustomConfig("messages");
        }
        return messagesConfig;
    }

    public FileConfiguration getCommandsConfig() {
        if (commandsConfig == null) {
            reloadCustomConfig("commands");
        }
        return commandsConfig;
    }

    public void saveBasicConfig() {
        if (basicConfig == null || basicConfigurationFile == null) {
            return;
        }
        try {
            basicConfig.save(basicConfigurationFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + basicConfigurationFile, ex);
        }
    }

    public void saveAdvancedConfig() {
        if (advancedConfig == null || advancedConfigurationFile == null) {
            return;
        }
        try {
            advancedConfig.save(advancedConfigurationFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + advancedConfigurationFile, ex);
        }
    }

    public void saveMessagesConfig() {
        if (messagesConfig == null || messagesConfigurationFile == null) {
            return;
        }
        try {
            messagesConfig.save(messagesConfigurationFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + messagesConfigurationFile, ex);
        }
    }

    public void saveCommandsConfig() {
        if (commandsConfig == null || commandsConfigurationFile == null) {
            return;
        }
        try {
            commandsConfig.save(commandsConfigurationFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + commandsConfigurationFile, ex);
        }
    }

    public String commandString(String command, boolean check) {
       /*if (command.contains(" ")) {
            String[] temp = command.split(" ");
            if (temp.length > 0) {
                command =  temp[0].replaceAll("/", "");
                if (check) {
                    for (int i=1; i<temp.length; i++) {
                        command += " " + temp[i];
                    }
                }
            }
        } else {*/
            command = command.replaceAll("/", "");
        //}
        return command;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)  {
        if (sender instanceof Player) {
            String command = cmd.getName();
            if (args.length > 0) {
                for (int i=0; i<args.length; i++) {
                    command += " " + args[i];
                }
            }
            Player player = (Player)sender;
            if ((cmd.getName().equalsIgnoreCase("authdb") && args.length == 0) || (command.equalsIgnoreCase("authdb version"))) {
                String tempName = "§f" + pluginName.substring(0, 4);
                tempName += "§b" + pluginName.substring(4, pluginName.length()) + "§f";
                player.sendMessage("§f" + tempName + " §f" + pluginVersion);
                player.sendMessage("§fDeveloped by §fCraft§cFire §f<dev@craftfire.com>");
                player.sendMessage("§f" + pluginWebsite);
                return true;
            } else if (command.equalsIgnoreCase(commandString(Config.commands_admin_reload, true)) || command.equalsIgnoreCase(commandString(Config.aliases_admin_reload, true))) {
                if (ZPermissions.isAllowed(player, ZPermissions.ZPermission.command_admin_reload)) {
                    new Config(this, "config", "plugins/" + pluginName + "/config/", "basic.yml");
                    LoadYml("commands", getClass().getProtectionDomain().getCodeSource());
                    LoadYml("messages", getClass().getProtectionDomain().getCodeSource());
                    Messages.sendMessage(Message.reload_success, player, null);
                    return true;
                } else {
                    Messages.sendMessage(Message.protection_denied, player, null);
                    return true;
                }
            } else if (cmd.getName().equalsIgnoreCase(commandString(Config.commands_user_logout, true)) || cmd.getName().equalsIgnoreCase(commandString(Config.aliases_user_logout, true))) {
                if (ZPermissions.isAllowed(player, ZPermissions.ZPermission.command_logout)) {
                    Messages.sendMessage(Message.logout_processing, player, null);
                    if (Processes.Logout(player, true)) {
                        EBean eBeanClass = EBean.checkPlayer(player, true);
                        eBeanClass.setSessiontime(0);
                        getDatabase().save(eBeanClass);
                        String check = Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player));
                        if (AuthDB.AuthDB_Sessions.containsKey(check)) {
                            AuthDB_Sessions.remove(check);
                        }
                        Messages.sendMessage(Message.logout_success, player, null);
                        if (Util.toLoginMethod(Config.login_method).equalsIgnoreCase("prompt")) {
                                 Messages.sendMessage(Message.login_prompt, player, null);
                         } else {
                             Messages.sendMessage(Message.login_normal, player, null);
                         }
                        return true;
                    } else {
                        Messages.sendMessage(Message.logout_failure, player, null);
                        return true;
                    }
                } else {
                    Messages.sendMessage(Message.protection_denied, player, null);
                    return true;
                }
            } else if (command.startsWith(commandString(Config.commands_admin_logout, true)) || command.startsWith(commandString(Config.aliases_admin_logout, true))) {
                if (ZPermissions.isAllowed(player, ZPermissions.ZPermission.command_admin_logout)) {
                    Messages.sendMessage(Message.logout_processing, player, null);
                    String[] temp = commandString(Config.commands_admin_logout, true).split(" ");
                    if (args.length == temp.length) {
                        String PlayerName = args[temp.length - 1];
                        List<Player> players = sender.getServer().matchPlayer(PlayerName);
                        if (!players.isEmpty()) {
                            Messages.sendMessage(Message.logout_processing, players.get(0), null);
                            if (Processes.Logout(players.get(0), true)) {
                                Messages.sendMessage(Message.logout_admin_success, player, null, players.get(0).getName());
                                Messages.sendMessage(Message.logout_admin, players.get(0), null);
                                Messages.sendMessage(Message.login_normal, players.get(0), null);
                                return true;
                            } else {
                                Messages.sendMessage(Message.logout_admin_failure, player, null, players.get(0).getName());
                                return true;
                            }
                        }
                        Messages.sendMessage(Message.logout_admin_notfound, player, null, PlayerName);
                        return true;
                    } else {
                        player.sendMessage("Usage: " + cmd.getUsage());
                        return true;
                    }
                } else {
                    Messages.sendMessage(Message.protection_denied, player, null);
                    return true;
                }
            } else if (command.startsWith(commandString(Config.commands_admin_login, true)) || command.startsWith(commandString(Config.aliases_admin_login, true))) {
                if (ZPermissions.isAllowed(player, ZPermissions.ZPermission.command_admin_login)) {
                    String[] temp = commandString(Config.commands_admin_login, true).split(" ");
                    if (args.length == temp.length) {
                        String PlayerName = args[temp.length - 1];
                        List<Player> players = sender.getServer().matchPlayer(PlayerName);
                        if (!players.isEmpty()) {
                            Processes.Login(players.get(0));
                            Messages.sendMessage(Message.login_admin_success, player, null, players.get(0).getName());
                            Messages.sendMessage(Message.login_admin, players.get(0), null);
                            return true;
                        /*} else {
                            Messages.sendMessage(Message.login_admin_failure, player, null, players.get(0).getName());
                            return true;*/
                        }
                        Messages.sendMessage(Message.login_admin_notfound, player, null, PlayerName);
                        return true;
                    } else {
                        player.sendMessage("Usage: " + cmd.getUsage());
                        return true;
                    }
                } else {
                    Messages.sendMessage(Message.protection_denied, player, null);
                    return true;
                }
            }
        }
        return true;
    }

    private void setupDatabase() {
        try {
            getDatabase().find(EBean.class).findRowCount();
        } catch (PersistenceException ex) {
            Util.logging.info("Installing persistence database for " + pluginName + " due to first time usage");
            installDDL();
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(EBean.class);
        return list;
    }

    public static Permission getPermissions() {
        return permission;
    }

    private Boolean setupPermissions() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }
        }
        return (permission != null);
    }

    void checkOldFiles() {
        File data = new File(getDataFolder() + "/data/", "");
        if (!data.exists()) {
            if (data.mkdir()) {
                Util.logging.debug("Created missing directory: " + getDataFolder() + "\\data\\");
            }
        }
        data = new File(getDataFolder() + "/translations/", "");
        if (!data.exists()) {
            if (data.mkdir()) {
                Util.logging.debug("Created missing directory: " + getDataFolder() + "\\translations\\");
            }
        }
        data = new File(getDataFolder() + "/config/", "");
        if (!data.exists()) {
            if (data.mkdir()) {
                Util.logging.debug("Created missing folder: " + getDataFolder() + "\\config\\");
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
                    if (split.length == 2) {
                        count++;
                        Util.logging.debug("Found linked name: " + split[0] + " linked with name: " + split[1]);
                        eBeanClass = EBean.checkPlayer(split[0], false);
                        eBeanClass.setLinkedname(split[1]);
                        database.save(eBeanClass);
                    }
                }
                in.close();
                if (count > 0) {
                    Util.logging.debug("Successfully imported " + count + " linked names.");
                }
            } catch (Exception e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
            if (data.delete()) {
                Util.logging.debug("Deleted file othernames.db from " + getDataFolder());
            }
        }
        data = new File(getDataFolder() + "/", "idle.db");
        if (data.exists()) {
            if (data.delete()) {
                Util.logging.debug("Deleted file idle.db from " + getDataFolder());
            }
        }
        data = new File(getDataFolder() + "/data/", "timeout.db");
        if (data.exists()) {
            if (data.delete()) {
                Util.logging.debug("Deleted file timeout.db from " + getDataFolder());
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
            if (Util.checkScript("adduser", Config.script_name, player, password, email, ipAddress)) {
                Util.logging.debug("Registered player: " + theplayer.getName());
            } else {
                Util.logging.debug("Failed registring player: " + theplayer.getName());
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
        String language = "English";
        File LanguagesAll = new File(getDataFolder() + "/translations");
        if (!LanguagesAll.exists()) {
            if (LanguagesAll.mkdir()) {
                Util.logging.debug("Sucesfully created directory: " + LanguagesAll);
            }
        }
        boolean set = false;
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
                        if (directory.equals("") == false) {
                            Util.logging.debug("Directory: "+directory);
                            File f = new File(getDataFolder() + "/translations/" + directory + "/" + type + ".yml");
                            if (!f.exists()) {
                                Util.logging.info(type + ".yml" + " could not be found in plugins/" + pluginName + "/translations/" + directory + "/! Creating " + type + ".yml");
                                DefaultFile(type + ".yml","translations/" + directory + "");
                            }
                            if (type.equals("commands") && (Config.language_commands).equalsIgnoreCase(directory))  {
                                set = true;
                                language = directory;
                            } else if (type.equals("messages") && (Config.language_messages).equalsIgnoreCase(directory))  {
                                set = true;
                                language = directory;
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
        if (directories.length > 0) {
            Util.logging.debug("Found " + directories.length + " directories for " + type);
        } else {
            Util.logging.error("Error! Could not find any directories for " + type);
        }
        if (!set) {
            for (int z=0; z<directories.length; z++) {
                if (type.equalsIgnoreCase("commands") && Config.language_commands.equalsIgnoreCase(directories[z].getName()))  {
                    set = true;
                    language = directories[z].getName();
                } else if (type.equalsIgnoreCase("messages") && Config.language_messages.equalsIgnoreCase(directories[z].getName()))  {
                    set = true;
                    language = directories[z].getName();
                }
            }
        }
        if (!set && type.equalsIgnoreCase("commands")) {
            Util.logging.info("Could not find translation files for " + Config.language_commands + ", defaulting to " + language);
        } else if (!set && type.equalsIgnoreCase("messages")) {
            Util.logging.info("Could not find translation files for " + Config.language_messages + ", defaulting to " + language);
        } else if (type.equalsIgnoreCase("commands")) {
            Util.logging.info(type + " language set to " + Config.language_commands);
        } else if (type.equalsIgnoreCase("messages")) {
            Util.logging.info(type + " language set to " + Config.language_messages);
        }
        new Config(this, type, getDataFolder() + "/translations/" + language + "/", type + ".yml");
    }

    public boolean isRegistered(String when, String player) {
        boolean dupe = false;
        boolean checkneeded = true;
        //Util.logging.debug("Checking if player " + player + " is registered.");
        player = Util.checkOtherName(player);
        EBean eBeanClass = EBean.checkPlayer(player, true);
        if (eBeanClass.getRegistered().equalsIgnoreCase("true")) {
            if (when.equalsIgnoreCase("join")) {
                if (!Config.database_keepalive) { Util.databaseManager.connect(); }
                Config.hasForumBoard = false;
                try {
                    if (Util.checkScript("checkuser", Config.script_name, player, null, null, null)) {
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
                    Util.logging.debug("Check to see if user is registered is needed, performing check");
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
        Util.logging.debug("Validating email: " + email);
          Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
          Matcher m = p.matcher(email);
          boolean Matches = m.matches();
          if (Matches) {
            Util.logging.debug("Email validation: passed!");
            return true;
          } else {
            Util.logging.debug("Email validation: failed!");
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
                if (direc.mkdir()) {
                    Util.logging.debug("Sucesfully created directory: "+direc);
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
