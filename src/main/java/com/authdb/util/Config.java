/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.util;

import java.io.File;

import org.bukkit.util.config.Configuration;

//import com.ensifera.animosity.craftirc.CraftIRC;

public class Config 
{
    
    ///
    
    ///////////////////////////////////////////
    //               GLOBAL
    ///////////////////////////////////////////
    public static boolean database_ison;
    public static boolean has_badcharacters;
    public static boolean HasForumBoard,Capitalization;
    public static boolean HasBackpack = false;
    public static boolean OnlineMode = true;
    
    ///////////////////////////////////////////
    //               Database
    ///////////////////////////////////////////
    public static boolean database_keepalive;
    public static String database_driver, database_username,database_password,database_port,database_host,database_database,database_prefix,dbDb;
    
    ///////////////////////////////////////////
    //               Core
    ///////////////////////////////////////////
    
        ///////////////////////////////////////////
        //               plugin
        ///////////////////////////////////////////
        public static boolean autoupdate_enable,debug_enable,usagestats_enabled;
    
        ///////////////////////////////////////////
        //               script
        ///////////////////////////////////////////
        public static String script_name,script_version,script_salt;
        public static boolean script_updatestatus;
        
        ///////////////////////////////////////////
        //               custom
        ///////////////////////////////////////////
        public static String custom_table,custom_userfield,custom_passfield,custom_encryption,custom_emailfield;
        public static boolean custom_enabled,custom_autocreate,custom_salt;
        
        ///////////////////////////////////////////
        //               welcome
        ///////////////////////////////////////////
        public static boolean welcome_enabled;
        public static String welcome_time,welcome_delay,welcome_length;
        public static int welcome_delay_ticks,welcome_length_ticks;
        
        ///////////////////////////////////////////
        //               register
        ///////////////////////////////////////////
        public static boolean register_enabled,register_force;
        
        ///////////////////////////////////////////
        //               login
        ///////////////////////////////////////////
        public static String login_method,login_tries,login_action,login_delay_length,login_delay_time,login_timeout_length,login_timeout_time,login_show_length,login_show_time;
        public static int login_delay,login_timeout,login_show;
        
        ///////////////////////////////////////////
        //               link
        ///////////////////////////////////////////
        public static boolean link_enabled,link_rename;
        
        ///////////////////////////////////////////
        //               ulink
        ///////////////////////////////////////////
        public static boolean unlink_enabled,unlink_rename;
        
        
        ///////////////////////////////////////////
        //               username
        ///////////////////////////////////////////
        public static String username_minimum,username_maximum;
        
        ///////////////////////////////////////////
        //               password
        ///////////////////////////////////////////
        public static String password_minimum,password_maximum;
        
        ///////////////////////////////////////////
        //               session
        ///////////////////////////////////////////
        public static boolean session_protect;
        public static String session_time,session_length,session_start;
        public static int session_seconds;

        ///////////////////////////////////////////
        //               guests
        ///////////////////////////////////////////
        public static boolean guests_commands,guests_movement,guests_inventory,guests_drop,guests_pickup,guests_health,guests_mobdamage,guests_interact,guests_build,guests_destroy,guests_chat,guests_mobtargeting,guests_pvp;
  
        ///////////////////////////////////////////
        //               filter
        ///////////////////////////////////////////
        public static String filter_action,filter_username,filter_password,filter_whitelist="";
        
        ///////////////////////////////////////////
        //               geoip
        ///////////////////////////////////////////
        public static boolean geoip_enabled;
  
    ///////////////////////////////////////////
    //               plugins
    ///////////////////////////////////////////
    
        ///////////////////////////////////////////
        //               CraftIRC
        ///////////////////////////////////////////
        public static boolean CraftIRC_enabled;
        public static String CraftIRC_tag,CraftIRC_prefix;

            ///////////////////////////////////////////
            //               messages
            ///////////////////////////////////////////
            public static boolean CraftIRC_messages_enabled,CraftIRC_messages_welcome_enabled,CraftIRC_messages_register_enabled,CraftIRC_messages_unregister_enabled,CraftIRC_messages_login_enabled,CraftIRC_messages_email_enabled,CraftIRC_messages_username_enabled,CraftIRC_messages_password_enabled,CraftIRC_messages_idle_enabled;
      
      
      public static Configuration template = null;

      public Config(String config, String directory, String filename) {
          template = new Configuration(new File(directory, filename));
          template.load();
            if(config.equals("config")) 
            {
                ///////////////////////////////////////////
                //               Core
                ///////////////////////////////////////////
                
                    ///////////////////////////////////////////
                    //               plugin
                    ///////////////////////////////////////////
                    autoupdate_enable = GetConfigBoolean("Core.plugin.autoupdate", true);
                    debug_enable = GetConfigBoolean("Core.plugin.debugmode", false);
                    usagestats_enabled = GetConfigBoolean("Core.plugin.usagestats", true);
                
                    ///////////////////////////////////////////
                    //               database
                    ///////////////////////////////////////////
                    database_driver =  GetConfigString("Core.database.driver", "mysql");
                    database_username =  GetConfigString("Core.database.username", "root");
                    database_password =  GetConfigString("Core.database.password", "root");
                    database_port =  GetConfigString("Core.database.port", "3306");
                    database_host =  GetConfigString("Core.database.host", "localhost");
                    database_database = GetConfigString("Core.database.name", "forum");
                    database_prefix = GetConfigString("Core.database.prefix", "");
                    database_keepalive = GetConfigBoolean("Core.database.keepalive", false);
                    dbDb = "jdbc:mysql://"+database_host+":"+database_port+"/"+database_database;
                    
                    ///////////////////////////////////////////
                    //               script
                    ///////////////////////////////////////////
                    script_name = GetConfigString("Core.script.name", "phpbb").toLowerCase();
                    script_version = GetConfigString("Core.script.version", "3.0.8");
                    script_updatestatus = GetConfigBoolean("Core.script.updatestatus", true);
                    script_salt = GetConfigString("Core.script.salt", "");
                    
                    ///////////////////////////////////////////
                    //               custom
                    ///////////////////////////////////////////
                    custom_enabled = GetConfigBoolean("Core.customdb.enabled", false);
                    custom_autocreate = GetConfigBoolean("Core.customdb.autocreate", true);
                    custom_table = GetConfigString("Core.customdb.table", "authdb_users");
                    custom_userfield = GetConfigString("Core.customdb.userfield", "username");
                    custom_passfield = GetConfigString("Core.customdb.passfield", "password");
                    custom_emailfield = GetConfigString("Core.customdb.emailfield", "");
                    custom_encryption = GetConfigString("Core.customdb.encryption", "").toLowerCase();
                    
                    ///////////////////////////////////////////
                    //               welcome
                    ///////////////////////////////////////////
                    welcome_enabled = GetConfigBoolean("Core.welcome.enabled", true);
                    welcome_time = GetConfigString("Core.welcome.time", "seconds");
                    welcome_delay = GetConfigString("Core.welcome.delay", "2");
                    welcome_length = GetConfigString("Core.welcome.length", "10");
                    welcome_delay_ticks = Util.ToTicks(welcome_time,welcome_delay);
                    welcome_length_ticks = Util.ToTicks(welcome_time,welcome_length);

                    ///////////////////////////////////////////
                    //               register
                    ///////////////////////////////////////////
                    register_enabled = GetConfigBoolean("Core.register.enabled", true);
                    register_force = GetConfigBoolean("Core.register.force", true);
                    
                    ///////////////////////////////////////////
                    //               login
                    ///////////////////////////////////////////
                    login_method = GetConfigString("Core.login.method", "prompt");
                    login_tries = GetConfigString("Core.login.tries", "3");
                    login_action = Util.GetAction(GetConfigString("Core.filter.action", "kick").toLowerCase());
                    login_delay_length = Util.split(GetConfigString("Core.login.delay", "2 seconds"), " ")[0];
                    login_delay_time = Util.split(GetConfigString("Core.login.delay", "2 seconds"), " ")[1];
                    login_delay = Util.ToTicks(login_delay_time,login_delay_length);
                    login_show_length = Util.split(GetConfigString("Core.login.show", "10 seconds"), " ")[0];
                    login_show_time = Util.split(GetConfigString("Core.login.show", "10 seconds"), " ")[1];
                    login_show = Util.ToSeconds(login_show_time,login_show_length);
                    login_timeout_length = Util.split(GetConfigString("Core.login.timeout", "3 minutes"), " ")[0];
                    login_timeout_time = Util.split(GetConfigString("Core.login.timeout", "3 minutes"), " ")[1];
                    login_timeout = Util.ToTicks(login_timeout_time,login_timeout_length);
                    
                    ///////////////////////////////////////////
                    //               link
                    ///////////////////////////////////////////
                    link_enabled = GetConfigBoolean("Core.link.enabled", true);
                    link_rename = GetConfigBoolean("Core.link.rename", true);
                    
                    ///////////////////////////////////////////
                    //               unlink
                    ///////////////////////////////////////////
                    unlink_enabled = GetConfigBoolean("Core.unlink.enabled", true);
                    unlink_rename = GetConfigBoolean("Core.unlink.rename", true);
                    
                    ///////////////////////////////////////////
                    //               username
                    ///////////////////////////////////////////
                    username_minimum = GetConfigString("Core.username.minimum", "3");
                    username_maximum = GetConfigString("Core.username.maximum", "16");
                    
                    
                    ///////////////////////////////////////////
                    //               password
                    ///////////////////////////////////////////
                    password_minimum = GetConfigString("Core.password.minimum", "6");
                    password_maximum = GetConfigString("Core.password.maximum", "16");
                    
                    ///////////////////////////////////////////
                    //               session
                    ///////////////////////////////////////////
                    session_protect = GetConfigBoolean("Core.session.protect", true);
                    session_time = GetConfigString("Core.session.time", "minutes");
                    session_length = GetConfigString("Core.session.length", "60");
                    session_seconds = Util.ToSeconds(session_time,session_length);
                    session_start = GetConfigString("Core.session.start", "login");
                    session_start = Util.CheckSessionStart(session_start);
                    
                    ///////////////////////////////////////////
                    //               guests
                    ///////////////////////////////////////////
                    guests_commands = GetConfigBoolean("Core.guest.commands", false);
                    guests_movement = GetConfigBoolean("Core.guest.movement", false);
                    guests_inventory = GetConfigBoolean("Core.guest.inventory", false);
                    guests_drop = GetConfigBoolean("Core.guest.drop", false);
                    guests_pickup = GetConfigBoolean("Core.guest.pickup", false);
                    guests_health = GetConfigBoolean("Core.guest.health", false);
                    guests_mobdamage = GetConfigBoolean("Core.guest.mobdamage", false);
                    guests_interact = GetConfigBoolean("Core.guest.interactions", false);
                    guests_build = GetConfigBoolean("Core.guest.building", false);
                    guests_destroy = GetConfigBoolean("Core.guest.destruction", false);
                    guests_chat = GetConfigBoolean("Core.guest.chat", false);
                    guests_mobtargeting = GetConfigBoolean("Core.guest.mobtargeting", false);
                    guests_pvp = GetConfigBoolean("Core.guest.pvp", false);
              
                    ///////////////////////////////////////////
                    //               filter
                    ///////////////////////////////////////////
                    filter_action = Util.GetAction(GetConfigString("Core.filter.action", "kick").toLowerCase());
                    filter_username = GetConfigString("Core.filter.username", "`~!@#$%^&*()-=+{[]}|\\:;\"<,>.?/");
                    filter_password = GetConfigString("Core.filter.password", "&");
                    filter_whitelist= GetConfigString("Core.filter.whitelist", "");
                    
                    ///////////////////////////////////////////
                    //               geoip
                    ///////////////////////////////////////////
                    geoip_enabled = GetConfigBoolean("Core.geopip.enabled", true);
              
                ///////////////////////////////////////////
                //               plugins
                ///////////////////////////////////////////
                
                    ///////////////////////////////////////////
                    //               CraftIRC
                    ///////////////////////////////////////////
                    CraftIRC_enabled = GetConfigBoolean("Plugins.CraftIRC.enabled", true);
                    CraftIRC_tag = GetConfigString("Plugins.CraftIRC.tag", "admin");
                    CraftIRC_prefix = GetConfigString("Plugins.CraftIRC.prefix", "%b%%green%[{PLUGIN}]%k%%b%");

                        ///////////////////////////////////////////
                        //               messages
                        ///////////////////////////////////////////
                        CraftIRC_messages_enabled = GetConfigBoolean("Plugins.CraftIRC.messages.enabled", true);
                        CraftIRC_messages_welcome_enabled = GetConfigBoolean("Plugins.CraftIRC.messages.welcome", true);
                        CraftIRC_messages_register_enabled = GetConfigBoolean("Plugins.CraftIRC.messages.register", true);
                        CraftIRC_messages_unregister_enabled = GetConfigBoolean("Plugins.CraftIRC.messages.unregister", true);
                        CraftIRC_messages_login_enabled = GetConfigBoolean("Plugins.CraftIRC.messages.login", true);
                        CraftIRC_messages_email_enabled = GetConfigBoolean("Plugins.CraftIRC.messages.email", true);
                        CraftIRC_messages_username_enabled = GetConfigBoolean("Plugins.CraftIRC.messages.username", true);
                        CraftIRC_messages_password_enabled = GetConfigBoolean("Plugins.CraftIRC.messages.password", true);
                        CraftIRC_messages_idle_enabled = GetConfigBoolean("Plugins.CraftIRC.messages.idle", true);
                        
            }
            else if(config.equals("messages")) 
            {
            ///////////////////////////////////////////
            //  messages
            ///////////////////////////////////////////    

                ///////////////////////////////////////////
                //               AuthDB
                ///////////////////////////////////////////
                
                    ///////////////////////////////////////////
                    //               welcome
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_database_failure = Config.GetConfigString("Core.database.failure", "<rose>Database connection failed! Access is denied! Contact admin.");
                    ///////////////////////////////////////////
                    //               welcome
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_welcome_guest = (String)Config.GetConfigString("Core.welcome.guest", "<yellow>Welcome <white>guest<yellow>! Please register with /register <password> <email>");
                    //Messages.AuthDB_message_welcome_user = (String)Config.GetConfigString("Core.welcome.user", "<yellow>Welcome back <white>{PLAYER}<yellow>! Please login with /login <password>");
                    
                    ///////////////////////////////////////////
                    //               guest
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_guest_notauthorized = Config.GetConfigString("Core.guest.notauthorized", "<rose>You are not authorized to do that!");
                    
                    ///////////////////////////////////////////
                    //               register
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_register_success = Config.GetConfigString("Core.register.success", "<rose>You have been registered!");
                    Messages.AuthDB_message_register_failure = Config.GetConfigString("Core.register.failure", "<rose>Registration failed!");
                    Messages.AuthDB_message_register_registered = Config.GetConfigString("Core.register.exists", "<rose>You are already registered!");
                    Messages.AuthDB_message_register_disabled = Config.GetConfigString("Core.register.disabled", "<rose>Registration not allowed!");
                    Messages.AuthDB_message_register_usage = Config.GetConfigString("Core.register.usage", "<rose>Correct usage is: /register <password> <email>");
                    
                    ///////////////////////////////////////////
                    //               unregister
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_unregister_success = Config.GetConfigString("Core.unregister.success", "<lightgreen>Unregistered successfully!");
                    Messages.AuthDB_message_unregister_failure = Config.GetConfigString("Core.unregister.failure", "<rose>An error occurred while unregistering!");
                    Messages.AuthDB_message_unregister_usage = Config.GetConfigString("Core.unregister.usage", "<rose>Correct usage is: /unregister <password>");
                    
                    ///////////////////////////////////////////
                    //               login
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_login_default = Config.GetConfigString("Core.login.default", "<yellow>Welcome back <white>{PLAYER}<yellow>! Please use /login <password>");
                    Messages.AuthDB_message_login_prompt = Config.GetConfigString("Core.login.prompt", "Auth<lightblue>DB <grey>> <white>Welcome <lightblue>{PLAYER}<white>! Please enter your password:");
                    Messages.AuthDB_message_login_success = Config.GetConfigString("Core.login.success", "<lightgreen>Password accepted. Welcome!");
                    Messages.AuthDB_message_login_failure = Config.GetConfigString("Core.login.failure", "<rose>Password incorrect, please try again.");
                    Messages.AuthDB_message_login_authorized = Config.GetConfigString("Core.login.authorized", "<lightgreen>You are already logged in!");;
                    Messages.AuthDB_message_login_notregistered = Config.GetConfigString("Core.login.notregistered", "<rose>You are not registred yet!");
                    Messages.AuthDB_message_login_usage = Config.GetConfigString("Core.login.usage", "<rose>Correct usage is: /login <password>");
                    
                    ///////////////////////////////////////////
                    //               link
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_link_success = Config.GetConfigString("Core.link.success", "<lightgreen>You have successfully linked!. You are now logged in");
                    Messages.AuthDB_message_link_failure = Config.GetConfigString("Core.link.failure", "<rose>Error while linking!");
                    Messages.AuthDB_message_link_exists = Config.GetConfigString("Core.link.exists", "<rose>You are already linked to a username!");
                    Messages.AuthDB_message_link_usage = Config.GetConfigString("Core.link.usage", "<rose>Correct usage is: /link <otherusername> <password>");
                        
                    ///////////////////////////////////////////
                    //               unlink
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_unlink_success = Config.GetConfigString("Core.unlink.success", "<lightgreen>You have successfully unlinked!");
                    Messages.AuthDB_message_unlink_failure = Config.GetConfigString("Core.unlink.failure", "<rose>Error while unlinking!");
                    Messages.AuthDB_message_unlink_nonexist = Config.GetConfigString("Core.unlink.nonexist", "<rose>You do not have a linked username!");
                    Messages.AuthDB_message_unlink_usage = Config.GetConfigString("Core.unlink.usage", "<rose>Correct usage is: /unlink <otherusername> <password>");
                    
                    ///////////////////////////////////////////
                    //               email
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_email_required = Config.GetConfigString("Core.email.required", "<rose>Email required for registration!");
                    Messages.AuthDB_message_email_invalid = Config.GetConfigString("Core.email.invalid", "<rose>Invalid email! Please try again!");
                    
                    ///////////////////////////////////////////
                    //               filter
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_filter_renamed = Config.GetConfigString("Core.filter.renamed", "<rose>{PLAYER} renamed to {PLAYERNEW} due to bad characters: {USERBADCHARACTERS}.");
                    Messages.AuthDB_message_filter_username = Config.GetConfigString("Core.filter.username", "<rose>Username contains bad characters: {USERBADCHARACTERS}!");
                    Messages.AuthDB_message_filter_password = Config.GetConfigString("Core.filter.password", "<rose>Password contains bad characters: {PASSBADCHARACTERS}!");
                    Messages.AuthDB_message_filter_whitelist = Config.GetConfigString("Core.filter.whitelist", "<lightgreen>{PLAYER} is on the filter <white>whitelist<lightgreen>, bypassing restrictions!");

                    ///////////////////////////////////////////
                    //               username
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_username_minimum = Config.GetConfigString("Core.username.minimum", "<rose>Your username does not meet the minimum requirement of {USERMIN} characters!");
                    Messages.AuthDB_message_username_maximum = Config.GetConfigString("Core.username.maximum", "<rose>Your username does not meet the maximum requirement of {USERMAX} characters!");

                    ///////////////////////////////////////////
                    //               password
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_password_minimum = Config.GetConfigString("Core.password.minimum", "<rose>Your password does not meet the minimum requirement of {PASSMIN} characters!");
                    Messages.AuthDB_message_password_maximum = Config.GetConfigString("Core.password.maximum", "<rose>Your password does not meet the maximum requirement of {PASSMAX} characters!");
                    Messages.AuthDB_message_password_success = Config.GetConfigString("Core.password.success", "<lightgreen>Password changed successfully!");
                    Messages.AuthDB_message_password_failure = Config.GetConfigString("Core.password.failure", "<rose>Error! Password change failed!");
                    Messages.AuthDB_message_password_notregistered = Config.GetConfigString("Core.password.notregistered", "<rose>Register first!");
                    Messages.AuthDB_message_password_usage = Config.GetConfigString("Core.password.usage", "<rose>Correct usage is: /password <oldpassword> <password>");
                    
                    Messages.AuthDB_message_session_valid = Config.GetConfigString("Core.session.valid", "<lightgreen>, I remember you! You are logged in!");
                    Messages.AuthDB_message_session_protected = Config.GetConfigString("Core.session.protected", "<rose>Sorry, a player with that name is already logged in on this server.");

                    ///////////////////////////////////////////
                    //               idle
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_idle_kick = Config.GetConfigString("Core.idle.kicked", "Kicked because you failed to login within {IDLELENGTH} {IDLETIME}.");
                    Messages.AuthDB_message_idle_whitelist = Config.GetConfigString("Core.idle.whitelist", "<lightgreen>{PLAYER} is on the idle <white>whitelist<lightgreen>, bypassing restrictions!");
                    
                ///////////////////////////////////////////
                //               CraftIRC
                ///////////////////////////////////////////
                    
                    ///////////////////////////////////////////
                    //               status
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_status_join = Config.GetConfigString("Plugins.CraftIRC.status.join", "{PLAYER} has joined the server from {PROVINCE}{STATE}, {COUNTRY} ({IP}).");
                    Messages.CraftIRC_message_status_quit = Config.GetConfigString("Plugins.CraftIRC.status.quit", "{PLAYER} has quit the server.");
                    
                    ///////////////////////////////////////////
                    //               register
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_register_success = Config.GetConfigString("Plugins.CraftIRC.register.success", "{PLAYER} just registered successfully!");
                    Messages.CraftIRC_message_register_failure = Config.GetConfigString("Plugins.CraftIRC.register.failure", "{PLAYER} had some errors while registering!");
                    Messages.CraftIRC_message_register_registered = Config.GetConfigString("Plugins.CraftIRC.register.registered", "{PLAYER} had a lapse in memory and tried to register again.");
                    
                    ///////////////////////////////////////////
                    //               password
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_password_success = Config.GetConfigString("Plugins.CraftIRC.password.success", "{PLAYER} logged in successfully!");
                    Messages.CraftIRC_message_password_failure = Config.GetConfigString("Plugins.CraftIRC.password.failure", "{PLAYER} tried to login with the wrong password!");
                    ///////////////////////////////////////////
                    //               idle
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_idle_kicked = Config.GetConfigString("Plugins.CraftIRC.idle.kicked", "{PLAYER} was kicked due to bad characters in username!");
                    Messages.CraftIRC_message_idle_whitelist = Config.GetConfigString("Plugins.CraftIRC.idle.whitelist", "{PLAYER} is on the on bad characters whitelist, bypassing restictions!");
                    
                    
                    ///////////////////////////////////////////
                    //               filter
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_filter_renamed = Config.GetConfigString("Plugins.CraftIRC.filter.renamed", "{PLAYER} renamed to {PLAYERNEW} due to bad characters.");
                    Messages.CraftIRC_message_filter_kicked = Config.GetConfigString("Plugins.CraftIRC.filter.kicked", "{PLAYER} was kicked due to bad characters in username!");
                    Messages.CraftIRC_message_filter_whitelist = Config.GetConfigString("Plugins.CraftIRC.filter.whitelist", "{PLAYER} is on the on bad characters whitelist, bypassing restictions!");
            
            }

      }

      public static String GetConfigString(String key, String defaultvalue)
      {
        return template.getString(key, defaultvalue);
      }
      
      public static boolean GetConfigBoolean(String key, boolean defaultvalue)
      {
        return template.getBoolean(key, defaultvalue);
      }
      
      public void DeleteConfigValue(String key) 
      {
        template.removeProperty(key);
      }

      public String raw(String key, String line)
      {
        return template.getString(key, line);
      }

      public void save(String key, String line) {
          template.setProperty(key, line);
      }
}
