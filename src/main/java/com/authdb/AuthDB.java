/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.CodeSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.persistence.PersistenceException;

import net.minecraft.server.PropertyManager;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.authdb.listeners.AuthDBBlockListener;
import com.authdb.listeners.AuthDBEntityListener;
import com.authdb.listeners.AuthDBPlayerListener;
import com.authdb.plugins.zCraftIRC;
import com.authdb.plugins.zPermissions;
import com.authdb.plugins.zPermissions.Permission;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Messages;
import com.authdb.util.Messages.Message;
import com.authdb.util.Processes;
import com.authdb.util.Util;
import com.authdb.util.databases.eBean;
import com.authdb.util.databases.MySQL;
import com.avaje.ebean.EbeanServer;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.nijikokun.bukkit.Permissions.Permissions;

public class AuthDB extends JavaPlugin {
    //
    public static org.bukkit.Server Server;
    public static AuthDB plugin;
    public static EbeanServer Database;
    public PluginDescriptionFile pluginFile = getDescription();
    public static String PluginName,PluginVersion,PluginWebsite, PluginDescrption;
    public static CraftIRC craftircHandle;
    //
    private final AuthDBPlayerListener playerListener = new AuthDBPlayerListener(this);
    private final AuthDBBlockListener blockListener = new AuthDBBlockListener(this);
    private final AuthDBEntityListener entityListener = new AuthDBEntityListener(this);
    public static List<String> authorizedNames = new ArrayList<String>();
    public static HashMap<String, Integer> AuthDB_Timeouts = new HashMap<String, Integer>();
    public static HashMap<String, Long> AuthDB_Sessions = new HashMap<String, Long>();
    public static HashMap<String, String> AuthDB_Authed = new HashMap<String, String>();
    public static HashMap<String, Long> AuthDB_AuthTime = new HashMap<String, Long>();
    public static HashMap<String, Long> AuthDB_RemindLogin = new HashMap<String, Long>();
    public static HashMap<String, Integer> AuthDB_SpamMessage = new HashMap<String, Integer>();
    public static HashMap<String, Long> AuthDB_SpamMessageTime = new HashMap<String, Long>();
    public static HashMap<String, String> AuthDB_PasswordTries = new HashMap<String, String>();
    public static HashMap<String, String> AuthDB_LinkedNames = new HashMap<String, String>();
    public static HashMap<String, String> AuthDB_LinkedNameCheck = new HashMap<String, String>();
    public static String otherNamesFileName = "data/othernames.db";
    public static Logger log = Logger.getLogger("Minecraft");
    public HashMap<String, ItemStack[]> inventories = new HashMap<String, ItemStack[]>();

    public void onDisable() {
        for (Player p : getServer().getOnlinePlayers()) {
            eBean eBeanClass = eBean.CheckPlayer(p);
            if(eBeanClass.getAuthorized().equalsIgnoreCase("true")) {
                eBeanClass.setReloadtime(Util.TimeStamp());
                eBean.save(eBeanClass);
            }
            Processes.Logout(p);
        }
        Util.Logging.Info(  PluginName + " plugin " + PluginVersion + " has been disabled");
        Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
        if ((checkCraftIRC != null) && (checkCraftIRC.isEnabled()) && (Config.CraftIRC_enabled == true))
            zCraftIRC.SendMessage(Message.OnDisable,null);
        disableInventory();
        authorizedNames.clear();
        AuthDB_AuthTime.clear();
        AuthDB_RemindLogin.clear();
        AuthDB_SpamMessage.clear();
        AuthDB_SpamMessageTime.clear();
        AuthDB_LinkedNames.clear();
        AuthDB_LinkedNameCheck.clear();
        AuthDB_PasswordTries.clear();
        AuthDB_Timeouts.clear();
        AuthDB_Sessions.clear();
        AuthDB_Authed.clear();
        MySQL.close();
     }

    public void onEnable() {
        plugin = this;
        SetupPluginInformation();
        CheckOldFiles();
        Server = getServer();
        Database = getDatabase();
        Plugin[] plugins = Server.getPluginManager().getPlugins();
        //Logging.Debug(System.getProperty("java.version"));
        /*Logging.Debug(System.getProperty("java.io.tmpdir"));
        Util.Logging.Debug(System.getProperty("java.library.path"));
        Util.Logging.Debug(System.getProperty("java.class.path"));
        Util.Logging.Debug(System.getProperty("user.home"));
        Util.Logging.Debug(System.getProperty("user.dir"));
        Util.Logging.Debug(System.getProperty("user.name"));
        Util.ErrorFile("HELLO"); */
        int counter = 0;
        String Plugins = "";
        while(plugins.length > counter) {
            Plugins += plugins[counter].getDescription().getName()+"&_&"+plugins[counter].getDescription().getVersion();
            if(plugins.length != (counter + 1))
                Plugins += "*_*";
            counter++;
        }
        File f = new File("plugins/"+PluginName+"/config/config.yml");
        if ( !f.exists() ) {
            Util.Logging.Info( "config.yml could not be found in plugins/AuthDB/config/! Creating config.yml!");
            DefaultFile("config.yml","config");
        }
        new Config("config","plugins/"+PluginName+"/config/", "config.yml");
        
        f = new File(getDataFolder()+"/config/customdb.sql");
        if ( !f.exists() ) {
            Util.Logging.Info( "customdb.sql could not be found in plugins/AuthDB/config/! Creating customdb.sql!");
            DefaultFile("customdb.sql","config");
        }
        
        LoadYml("messages");
        LoadYml("commands");

          final Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
          CheckPermissions();
          if ((checkCraftIRC != null) && (Config.CraftIRC_enabled == true)) {
              craftircHandle = ((CraftIRC)checkCraftIRC);
              Util.Logging.Info("Found supported plugin: " + checkCraftIRC.getDescription().getName());
              this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() { if(checkCraftIRC.isEnabled()) { zCraftIRC.SendMessage(Message.OnEnable, null); } } }, 100);

          }
          final Plugin Backpack = getServer().getPluginManager().getPlugin("Backpack");
          if (Backpack != null) { Config.HasBackpack = true; }
        PluginManager pm = getServer().getPluginManager();
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
        PropertyManager TheSettings = new PropertyManager(new File("server.properties"));
        if (TheSettings.getBoolean("online-mode", true)) { Config.OnlineMode = true; }
        UpdateLinkedNames();

        setupDatabase();
        
        MySQL.connect();
        try {
            Util.CheckScript("numusers",Config.script_name,null,null,null,null);
        }
        catch (SQLException e) {
            if(Config.custom_enabled && Config.custom_autocreate) {
                String enter = "\n";
                Util.Logging.Info( "Creating default table schema for "+Config.custom_table);
                
                StringBuilder query = new StringBuilder();
                String NL = System.getProperty("line.separator");
                Scanner scanner = null;
                try {
                    scanner = new Scanner(new FileInputStream(getDataFolder()+"/config/customdb.sql"));
                } catch (FileNotFoundException e2) {
                    Util.Logging.StackTrace(e2.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
                }
                try {
                  while (scanner.hasNextLine()) {
                      String line = scanner.nextLine();
                      if(line.contains("CREATE TABLE") || line.contains("create table")) {
                          query.append("CREATE TABLE IF NOT EXISTS `"+Config.custom_table+"` (" + NL);
                      }
                      else {
                          query.append(line + NL);
                      }
                  }
                }
                finally{
                  scanner.close();
                }
                
                Util.Logging.Debug(enter+query);
                try {
                    MySQL.query(""+query);
                    Util.Logging.Info( "Sucessfully created table "+Config.custom_table);
                    PreparedStatement ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.custom_table+"`");
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) { Util.Logging.Info( rs.getInt("countit") + " user registrations in database"); }
                }
                catch (SQLException e1) {
                    Util.Logging.Info( "Failed creating user table "+Config.custom_table);
                    Util.Logging.StackTrace(e1.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
                }
            }
            else {
                Util.Logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
            }
            
        }
        
        Util.Logging.Info( PluginName + " plugin " + PluginVersion + " is enabled");
        if(Config.debug_enable) Util.Logging.Info( "Debug is ENABLED, get ready for some heavy spam");
        if(Config.custom_enabled) if(Config.custom_encryption == null) Util.Logging.Info( "**WARNING** SERVER IS RUNNING WITH NO ENCRYPTION: PASSWORDS ARE STORED IN PLAINTEXT");
        Util.Logging.Info( PluginName + " is developed by CraftFire <dev@craftfire.com>");

        String thescript = "",theversion = "";
        if(Config.custom_enabled) { thescript = "custom"; }
        else {
            thescript = Config.script_name;
            theversion = Config.script_version;
        }
        String online = ""+getServer().getOnlinePlayers().length;
        String max = ""+getServer().getMaxPlayers();
        if(Config.usagestats_enabled) {
            try { Util.PostInfo(getServer().getServerName(),getServer().getVersion(),PluginVersion,System.getProperty("os.name"),System.getProperty("os.version"),System.getProperty("os.arch"),System.getProperty("java.version"),thescript,theversion,Plugins,online,max,Server.getPort()); }
            catch (IOException e1) { if(Config.debug_enable) Util.Logging.Debug("Could not send usage stats to main server."); }
        }
        for (Player p : getServer().getOnlinePlayers()) {
            eBean eBeanClass = eBean.CheckPlayer(p);
            if(eBeanClass.getReloadtime() + 30 > Util.TimeStamp()) {
                Processes.Login(p);
            }
        }
    }
    
    public String CommandString(String command) {
        if(command.contains(" ")) {
            String[] temp = command.split(" ");
            if(temp.length > 0) {
                command =  temp[0].replaceAll("/", "");
            }
        }
        else { command = command.replaceAll("/", ""); }
        return command;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)  { 
        if(sender instanceof Player) {
            Player player = (Player)sender;
            String NoPermission = "You do not have permission to use this command.";
            if (cmd.getName().equalsIgnoreCase("authdb")) {
                if(args.length == 0) {
                    player.sendMessage("§b Name: §f "+PluginName+" §4 "+PluginVersion);
                    player.sendMessage("§b "+PluginName + " is developed by §4 CraftFire �e<dev@craftfire.com>");
                    player.sendMessage("§d "+PluginWebsite);
                }
            }
            else if (cmd.getName().equalsIgnoreCase(CommandString(Config.commands_reload)) || cmd.getName().equalsIgnoreCase(CommandString(Config.aliases_reload))) {
                if(args.length == 1) {
                    if(zPermissions.IsAllowed(player, Permission.command_reload)) {
                        new Config("config","plugins/"+PluginName+"/config/", "config.yml");
                        LoadYml("commands");
                        LoadYml("messages");
                        player.sendMessage("§a AuthDB has been successfully reloaded!");
                        return true;
                    }
                }
            }
            else if (cmd.getName().equalsIgnoreCase(CommandString(Config.commands_logout)) || cmd.getName().equalsIgnoreCase(CommandString(Config.aliases_logout))) {
                if(args.length == 0) {
                    if(Processes.Logout(player)) {
                        player.sendMessage("§aSucessfully logged out!");
                        return true;
                    }
                    else {
                        player.sendMessage("§aYou are not logged in!");
                        return true;
                    }
                }
                else if(args.length == 1 && zPermissions.IsAllowed(player, Permission.command_admin_logout)) {
                    String PlayerName = args[0];
                    List<Player> players = sender.getServer().matchPlayer(PlayerName);
                    if(!players.isEmpty()) {
                        if(Processes.Logout(players.get(0))) {
                            player.sendMessage("Successfully logged out player '"+players.get(0).getName()+"'.");
                            players.get(0).sendMessage("You have been logged out by an admin.");
                            return true;
                        }
                        else {
                            player.sendMessage("You cannot logout player '"+players.get(0).getName()+"' because the player is not logged in.");
                            return true;
                        }
                    }
                    player.sendMessage("§aCould not find player '"+PlayerName+"', please try again.");
                    return true;
                }
            }
            else if (isAuthorized(player) && (cmd.getName().equalsIgnoreCase(CommandString(Config.commands_login)) || cmd.getName().equalsIgnoreCase(CommandString(Config.aliases_login)))) {
                if(args.length == 1 && zPermissions.IsAllowed(player, Permission.command_admin_login)) {
                    String PlayerName = args[0];
                    List<Player> players = sender.getServer().matchPlayer(PlayerName);
                    if(!players.isEmpty()) {
                        if(Processes.Logout(players.get(0))) {
                            player.sendMessage("Successfully logged in player '"+players.get(0).getName()+"'.");
                            players.get(0).sendMessage("You have been logged in by an admin.");
                            return true;
                        }
                        else {
                            player.sendMessage("You cannot login player '"+players.get(0).getName()+"' because the player is already logged in.");
                            return true;
                        }
                    }
                    player.sendMessage("§aCould not find player '"+PlayerName+"', please try again.");
                    return true;
                }
            }
            player.sendMessage(NoPermission);
            return true;
        }
        return false;
    }
    
    private void setupDatabase() {
        try {
            getDatabase().find(eBean.class).findRowCount();
        } catch (PersistenceException ex) {
            Util.Logging.Info("Installing persistence database for " + PluginName + " due to first time usage");
            installDDL();
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(eBean.class);
        return list;
    }
        
    
    void CheckPermissions() {
        Plugin Check1 = getServer().getPluginManager().getPlugin("Permissions");
        if (Check1 != null) {
            zPermissions.HasPlugin = true;
        }
        
        Plugin Check2 = getServer().getPluginManager().getPlugin("PermissionsBukkit");
        if (Check2 != null) {
            if(zPermissions.HasPlugin) {
                Util.Logging.Info("Found 2 supported permissions plugins: " + Check1.getDescription().getName() + " "+Check1.getDescription().getVersion()+" and "+ Check2.getDescription().getName() + " "+Check2.getDescription().getVersion());
                Util.Logging.Info("Defaulting permissions to: "+ Check2.getDescription().getName() + " "+Check2.getDescription().getVersion());
            }
            else {
                Util.Logging.Info("Found supported plugin: " + Check2.getDescription().getName() + " "+Check2.getDescription().getVersion());
            }
            zPermissions.HasPermissionsBukkit = true;
        }
        else {
            if(zPermissions.HasPlugin) {
                Util.Logging.Info("Found supported plugin: " + Check1.getDescription().getName() + " "+Check1.getDescription().getVersion());
            }
            else {
                Util.Logging.Info("Could not load a permissions plugin, going over to OP!");
            }
        }
    }

    void CheckOldFiles() {
        File data = new File(getDataFolder()+"/data/","");
        if (!data.exists()) { data.mkdir(); }
        data = new File(getDataFolder()+"/translations/","");
        if (!data.exists()) { data.mkdir(); }
        data = new File(getDataFolder()+"/translations/commands/","");
        if (!data.exists()) { data.mkdir(); }
        data = new File(getDataFolder()+"/translations/messages/","");
        if (!data.exists()) { data.mkdir(); }
        data = new File(getDataFolder()+"/config/","");
        if (!data.exists()) { data.mkdir(); }
        data = new File(getDataFolder()+"/","othernames.db");
        if (data.exists()) { data.renameTo(new File(getDataFolder()+"/data/","othernames.db")); }
        data = new File(getDataFolder()+"/","idle.db");
        if (data.exists()) { data.delete(); }
        data = new File(getDataFolder()+"/data/","timeout.db");
        if (data.exists()) { data.delete(); }
    }

    public static boolean isAuthorized(Player player)  { 
        if(authorizedNames.contains(player.getName())) { return true; }
        eBean eBeanClass = eBean.find(player,eBean.Column.authorized,"true");
        if (eBeanClass != null) {
            authorizedNames.add(player.getName()); 
            return true;
        }
        return false;
    }

    public boolean checkPassword(String player, String password) {
        try {
            if(!Config.database_keepalive) { MySQL.connect(); }
            password = Matcher.quoteReplacement(password);
            if (Util.CheckOtherName(player) != player) {
                player = Util.CheckOtherName(player);
            }
            if(Util.CheckScript("checkpassword",Config.script_name, player.toLowerCase(), password,null,null)) return true;
            if(!Config.database_keepalive) { MySQL.close(); }
        }
        catch (SQLException e) {
            Util.Logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
            Stop("ERRORS in checking password. Plugin will NOT work. Disabling it.");
        }
        return false;
    }

    public boolean isWithinRange(int number, int around, int range) {
        int difference = Math.abs(around - number);
        return difference <= range;
    }

    void Stop(String error) {
        Util.Logging.Warning(error);
        getServer().getPluginManager().disablePlugin(((org.bukkit.plugin.Plugin) (this)));
    }

    public boolean register(Player theplayer, String password, String email, String ipAddress) throws IOException, SQLException {
        if(password.length() < Integer.parseInt(Config.password_minimum)) {
            Messages.SendMessage(Message.password_minimum, theplayer, null);
            return false;
        }
        else if(password.length() > Integer.parseInt(Config.password_maximum)) {
            Messages.SendMessage(Message.password_maximum, theplayer, null);
            return false;
        }
        if(!Config.database_keepalive) { MySQL.connect(); }
        String player = theplayer.getName();
        if (!Util.CheckFilter("password",password)) {
            Messages.SendMessage(Message.filter_password, theplayer, null);
        }
        else {
            Util.CheckScript("adduser",Config.script_name,player, password, email, ipAddress);
        }
        if(!Config.database_keepalive) { MySQL.close(); }
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
            Util.Logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return temp;
    } 
    
    void LoadYml(String type) {
        String Language = "English";
        String[] LanguagesAll = new File(getDataFolder()+"/translations/"+type+"/").list();
        boolean Set = false;
        CodeSource src = getClass().getProtectionDomain().getCodeSource();

        if( src != null ) {
            try {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream( jar.openStream());
                ZipEntry ze = null;
                
                while( ( ze = zip.getNextEntry() ) != null ) {
                    String fileName = ze.getName();
                    if( fileName.startsWith("files/translations/"+type+"/") && fileName.endsWith(".yml") )  {
                        fileName = fileName.replace("files/translations/"+type+"/", "");
                        File f = new File(getDataFolder()+"/translations/"+type+"/"+fileName);
                        if ( !f.exists() ) {
                            Util.Logging.Info( fileName+" could not be found in plugins/AuthDB/translations/"+type+"/! Creating "+fileName);
                            DefaultFile(fileName,"translations/"+type);
                        }
                        if((Config.language+".yml").equalsIgnoreCase(fileName))  { 
                            Set = true;
                            Language = fileName; 
                        } 
                    }
                }
            } catch (IOException e) {
                Util.Logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
            }

         }
        if(!Set) {
            for(int i=0; i<LanguagesAll.length; i++) {
                if(Config.language.equalsIgnoreCase(LanguagesAll[i]))  { 
                    Set = true;
                    Language = LanguagesAll[i]; 
                }
            }
        }
        if(!Set) { Util.Logging.Info( "Could not find translation files for "+Config.language+"; defaulting to "+Language); }
        else { Util.Logging.Debug(type+" language set to "+Language); }
        new Config(type,getDataFolder()+"/translations/"+type+"/", Language+".yml");
        
    }    
    
    public boolean isRegistered(String when, String player) {
        boolean dupe = false;
        boolean checkneeded = true;
        //if(Config.debug_enable)
            //Logging.Debug("Running function: isRegistered(String player)");
        if(when.equals("join")) {
            if(!Config.database_keepalive) { MySQL.connect(); }
            Config.HasForumBoard = false;
            try {
                if(Util.CheckScript("checkuser",Config.script_name, player, null, null, null)) {
                    AuthDB_Authed.put(Encryption.md5(player), "yes");
                    dupe = true;
                }
            } catch (SQLException e) {
                Util.Logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
            }
            //if(!Config.HasForumBoard)
                //Stop("Can't find a forum board, stopping the plugin.");
            if(!Config.database_keepalive) { MySQL.close(); }
            if(!dupe)
                AuthDB_Authed.put(Encryption.md5(player), "no");
            return dupe;
        }
        else if(when.equals("command")) {
            if(!Config.database_keepalive) { MySQL.connect(); }
            Config.HasForumBoard = false;
            try {
                if(Util.CheckScript("checkuser",Config.script_name, player.toLowerCase(), null, null, null)) {
                    AuthDB_Authed.put(Encryption.md5(player), "yes");
                    dupe = true;
                }
                else if (Util.CheckOtherName(player) != player) {
                    AuthDB_Authed.put(Encryption.md5(player), "yes");
                    dupe = true;
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                Util.Logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
            }
            //if(!Config.HasForumBoard)
                //Stop("Can't find a forum board, stopping the plugin.");
            if(!Config.database_keepalive) { MySQL.close(); }
            if(!dupe)
                AuthDB_Authed.put(Encryption.md5(player), "no");
            return dupe;
        }
        else {
            if(this.AuthDB_Authed.containsKey(Encryption.md5(player))) {
                //if(Config.debug_enable) Util.Logging.Debug("Found cache registration for "+player);
                String check =AuthDB_Authed.get(Encryption.md5(player));
                if(check.equals("yes")) {
                    //if(Config.debug_enable) Util.Logging.Debug("Cache "+player+" passed with value "+check);
                    checkneeded = false;
                    return true;
                }
                else if(check.equals("no")) {
                    //if(Config.debug_enable) Util.Logging.Debug("Cache "+player+" did NOT pass with value "+check);
                    return false;
                }
            }
            else if(checkneeded) {
                try {
                    if(!Config.database_keepalive) { MySQL.connect(); }
                    Config.HasForumBoard = false;
                    if(Util.CheckScript("checkuser",Config.script_name, player.toLowerCase(), null, null, null)) {
                        AuthDB_Authed.put(Encryption.md5(player), "yes");
                        dupe = true;
                    }
                    //if(!Config.HasForumBoard)
                        //Stop("Can't find a forum board, stopping the plugin.");
                    if(!Config.database_keepalive) { MySQL.close(); }
                    if(!dupe)
                        AuthDB_Authed.put(Encryption.md5(player), "no");
                    return dupe;
                } catch (SQLException e) {
                    Util.Logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
                    Stop("ERRORS in checking user. Plugin will NOT work. Disabling it.");
                }
            }
        }
        return false;
    }


    public boolean checkEmail(String email) {
          Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
          Matcher m = p.matcher(email);
          boolean Matches = m.matches();
          if (Matches)
            return true;
          else
            return false;
    }

      public void storeInventory(Player player, ItemStack[] inventory) throws IOException {
        String inv = "";
        for (short i = 0; i < inventory.length; i = (short)(i + 1)) {
            if(inventory[i] != null) {
                inv += inventory[i].getTypeId() + ":" + inventory[i].getAmount() + ":" + (inventory[i].getData() == null ? "" : Byte.valueOf(inventory[i].getData().getData())) + ":" + inventory[i].getDurability()+",";
            }
            else { inv += "0:0:0:0,"; }
        }
          
          eBean eBeanClass = eBean.find(player);
          eBeanClass.setInventory(inv);
          AuthDB.Database.save(eBeanClass);
      }

    public void disableInventory() {
        Set pl = inventories.keySet();
        Iterator i = pl.iterator();
       while (i.hasNext())
       {
            String player = (String)i.next();
            Player pla = getServer().getPlayer(player);
            if (pl != null)
            pla.getInventory().setContents((ItemStack[])this.inventories.get(player));
       }
        inventories.clear();
    }

    public void UpdateLinkedNames() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            if(Util.CheckOtherName(player.getName()) != player.getName()) {
                player.setDisplayName(Util.CheckOtherName(player.getName()));
            }
        }
    }

    void SetupPluginInformation() {
        PluginName = getDescription().getName();
        PluginVersion = getDescription().getVersion();
        PluginWebsite = getDescription().getWebsite();
        PluginDescrption = getDescription().getDescription();
    }

    public static ItemStack[] getInventory(Player player) {
        eBean eBeanClass = eBean.find(player);
        if (eBeanClass != null) {
            String data = eBeanClass.getInventory();
            if(data != "" && data != null) {
                String[] inv = Util.split(data, ",");
                ItemStack[] inventory = new ItemStack[36];
                
                for(int i=0; i<inv.length - 1; i++) {
                    String line = inv[i];
                    String[] split = line.split(":");
                    if (split.length == 4) {
                      int type = Integer.valueOf(split[0]).intValue();
                      inventory[i] = new ItemStack(type, Integer.valueOf(split[1]).intValue());
    
                      short dur = Short.valueOf(split[3]).shortValue();
                      if (dur > 0)
                          inventory[i].setDurability(dur);
                      byte dd;
                      if (split[2].length() == 0)
                        dd = 0;
                      else
                        dd = Byte.valueOf(split[2]).byteValue();
                      Material mat = Material.getMaterial(type);
                      if (mat == null)
                          inventory[i].setData(new MaterialData(type, dd));
                      else
                          inventory[i].setData(mat.getNewData(dd));
                      i = (short)(i + 1);
                    }
                  }
                
                eBeanClass.setInventory(null);
                AuthDB.Database.save(eBeanClass);
                return inventory;
            }
        }
        return null;
      }

     private void DefaultFile(String name, String folder) {
            File actual = new File(getDataFolder()+"/"+folder+"/", name);
            File direc = new File(getDataFolder()+"/"+folder+"/","");
            if (!direc.exists()) { direc.mkdir(); }
            if (!actual.exists()) {
              java.io.InputStream input = getClass().getResourceAsStream("/files/"+folder+"/" + name);
              if (input != null) {
                  java.io.FileOutputStream output = null;
                try {
                  output = new java.io.FileOutputStream(actual);
                  byte[] buf = new byte[8192];
                  int length = 0;

                  while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                  }

                  System.out.println("["+PluginName+"] Written default setup for " + name);
                } catch (Exception e) {
                  Util.Logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
                } finally {
                  try {
                    if (input != null)
                      input.close();
                  } catch (Exception e) {
                  }
                  try {
                    if (output != null)
                      output.close();
                  }
                  catch (Exception e)
                  {
                  }
                }
              }
            }
          }

}
