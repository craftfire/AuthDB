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

public class Config {

    ///

    ///////////////////////////////////////////
    //               GLOBAL
    ///////////////////////////////////////////
    public static boolean database_ison;
    public static boolean has_badcharacters;
    public static boolean hasForumBoard,capitalization;
    public static boolean hasBackpack = false;
    public static boolean hasBukkitContrib = false;
    public static boolean onlineMode = true;

    ///////////////////////////////////////////
    //               database
    ///////////////////////////////////////////
    public static boolean database_keepalive;
    public static String database_driver, database_username,database_password,database_port,database_host,database_database,dbDb;

    ///////////////////////////////////////////
    //               Core
    ///////////////////////////////////////////

        ///////////////////////////////////////////
        //               plugin
        ///////////////////////////////////////////
        public static boolean autoupdate_enable,debug_enable,usagestats_enabled,logging_enabled;
        public static String language, logformat;

        ///////////////////////////////////////////
        //               script
        ///////////////////////////////////////////
        public static String script_name,script_version,script_salt,script_tableprefix;
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
        public static String register_delay_length,register_delay_time,register_timeout_length,register_timeout_time,register_show_length,register_show_time;
        public static int register_delay,register_timeout,register_show;

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
        public static boolean session_protect, session_enabled;
        public static String session_time,session_thelength,session_start;
        public static int session_length;

        ///////////////////////////////////////////
        //               guests
        ///////////////////////////////////////////
        public static boolean guests_commands,guests_movement,guests_inventory,guests_drop,guests_pickup,guests_health,guests_mobdamage,guests_interact,guests_build,guests_destroy,guests_chat,guests_mobtargeting,guests_pvp;

        ///////////////////////////////////////////
        //               protection
        ///////////////////////////////////////////
        public static boolean protection_notify;
        public static int protection_delay;
        public static String protection_delay_time,protection_delay_length;

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


            ///////////////////////////////////////////
            //               commands
            ///////////////////////////////////////////
            public static String commands_register,commands_link,commands_unlink,commands_login,commands_logout,commands_setspawn,commands_reload;
            public static String aliases_register,aliases_link,aliases_unlink,aliases_login,aliases_logout,aliases_setspawn,aliases_reload;




      public static Configuration template = null;

      public Config(String config, String directory, String filename) {
          template = new Configuration(new File(directory, filename));
          template.load();
            if (config.equalsIgnoreCase("config")) {
                ///////////////////////////////////////////
                //               Core
                ///////////////////////////////////////////

                    ///////////////////////////////////////////
                    //               plugin
                    ///////////////////////////////////////////
                    language = getConfigString("Core.plugin.language", "English");
                    autoupdate_enable = getConfigBoolean("Core.plugin.autoupdate", true);
                    debug_enable = getConfigBoolean("Core.plugin.debugmode", false);
                    usagestats_enabled = getConfigBoolean("Core.plugin.usagestats", true);
                    logformat = getConfigString("Core.plugin.logformat", "yyyy-MM-dd");
                    logging_enabled = getConfigBoolean("Core.plugin.logging", true);

                    ///////////////////////////////////////////
                    //               database
                    ///////////////////////////////////////////
                    database_driver =  getConfigString("Core.database.driver", "mysql");
                    database_username =  getConfigString("Core.database.username", "root");
                    database_password =  getConfigString("Core.database.password", "");
                    database_port =  getConfigString("Core.database.port", "3306");
                    database_host =  getConfigString("Core.database.host", "localhost");
                    database_database = getConfigString("Core.database.name", "forum");
                    database_keepalive = getConfigBoolean("Core.database.keepalive", false);
                    dbDb = "jdbc:mysql://" + database_host + ":" + database_port + "/" + database_database;

                    ///////////////////////////////////////////
                    //               script
                    ///////////////////////////////////////////
                    script_name = getConfigString("Core.script.name", "phpbb").toLowerCase();
                    script_version = getConfigString("Core.script.version", "3.0.8");
                    script_tableprefix = getConfigString("Core.script.tableprefix", "");
                    script_updatestatus = getConfigBoolean("Core.script.updatestatus", true);
                    script_salt = getConfigString("Core.script.salt", "");

                    ///////////////////////////////////////////
                    //               custom
                    ///////////////////////////////////////////
                    custom_enabled = getConfigBoolean("Core.customdb.enabled", false);
                    custom_autocreate = getConfigBoolean("Core.customdb.autocreate", true);
                    custom_table = getConfigString("Core.customdb.table", "authdb_users");
                    custom_userfield = getConfigString("Core.customdb.userfield", "username");
                    custom_passfield = getConfigString("Core.customdb.passfield", "password");
                    custom_emailfield = getConfigString("Core.customdb.emailfield", "");
                    custom_encryption = getConfigString("Core.customdb.encryption", "").toLowerCase();

                    ///////////////////////////////////////////
                    //               welcome
                    ///////////////////////////////////////////
                    welcome_enabled = getConfigBoolean("Core.welcome.enabled", true);
                    welcome_time = getConfigString("Core.welcome.time", "seconds");
                    welcome_delay = getConfigString("Core.welcome.delay", "2");
                    welcome_length = getConfigString("Core.welcome.length", "10");
                    welcome_delay_ticks = Util.toTicks(welcome_time,welcome_delay);
                    welcome_length_ticks = Util.toTicks(welcome_time,welcome_length);

                    ///////////////////////////////////////////
                    //               register
                    ///////////////////////////////////////////
                    register_enabled = getConfigBoolean("Core.register.enabled", true);
                    register_force = getConfigBoolean("Core.register.force", true);
                    register_delay_length = Util.split(getConfigString("Core.register.delay", "4 seconds"), " ")[0];
                    register_delay_time = Util.split(getConfigString("Core.register.delay", "4 seconds"), " ")[1];
                    register_delay = Util.toTicks(register_delay_time,register_delay_length);
                    register_show_length = Util.split(getConfigString("Core.register.show", "10 seconds"), " ")[0];
                    register_show_time = Util.split(getConfigString("Core.register.show", "10 seconds"), " ")[1];
                    register_show = Util.toSeconds(register_show_time,register_show_length);
                    register_timeout_length = Util.split(getConfigString("Core.register.timeout", "3 minutes"), " ")[0];
                    register_timeout_time = Util.split(getConfigString("Core.register.timeout", "3 minutes"), " ")[1];
                    register_timeout = Util.toTicks(register_timeout_time,register_timeout_length);

                    ///////////////////////////////////////////
                    //               login
                    ///////////////////////////////////////////
                    login_method = getConfigString("Core.login.method", "prompt");
                    login_tries = getConfigString("Core.login.tries", "3");
                    login_action = Util.getAction(getConfigString("Core.filter.action", "kick").toLowerCase());
                    login_delay_length = Util.split(getConfigString("Core.login.delay", "4 seconds"), " ")[0];
                    login_delay_time = Util.split(getConfigString("Core.login.delay", "4 seconds"), " ")[1];
                    login_delay = Util.toTicks(login_delay_time,login_delay_length);
                    login_show_length = Util.split(getConfigString("Core.login.show", "10 seconds"), " ")[0];
                    login_show_time = Util.split(getConfigString("Core.login.show", "10 seconds"), " ")[1];
                    login_show = Util.toSeconds(login_show_time,login_show_length);
                    login_timeout_length = Util.split(getConfigString("Core.login.timeout", "3 minutes"), " ")[0];
                    login_timeout_time = Util.split(getConfigString("Core.login.timeout", "3 minutes"), " ")[1];
                    login_timeout = Util.toTicks(login_timeout_time,login_timeout_length);

                    ///////////////////////////////////////////
                    //               link
                    ///////////////////////////////////////////
                    link_enabled = getConfigBoolean("Core.link.enabled", true);
                    link_rename = getConfigBoolean("Core.link.rename", true);

                    ///////////////////////////////////////////
                    //               unlink
                    ///////////////////////////////////////////
                    unlink_enabled = getConfigBoolean("Core.unlink.enabled", true);
                    unlink_rename = getConfigBoolean("Core.unlink.rename", true);

                    ///////////////////////////////////////////
                    //               username
                    ///////////////////////////////////////////
                    username_minimum = getConfigString("Core.username.minimum", "3");
                    username_maximum = getConfigString("Core.username.maximum", "16");


                    ///////////////////////////////////////////
                    //               password
                    ///////////////////////////////////////////
                    password_minimum = getConfigString("Core.password.minimum", "6");
                    password_maximum = getConfigString("Core.password.maximum", "16");

                    ///////////////////////////////////////////
                    //               session
                    ///////////////////////////////////////////
                    session_enabled = getConfigBoolean("Core.session.enabled", false);
                    session_protect = getConfigBoolean("Core.session.protect", true);
                    session_thelength = Util.split(getConfigString("Core.session.length", "1 hour"), " ")[0];
                    session_time = Util.split(getConfigString("Core.session.length", "1 hour"), " ")[1];
                    session_length = Util.toSeconds(session_time,session_thelength);
                    session_start = Util.checkSessionStart(getConfigString("Core.session.start", "login"));

                    ///////////////////////////////////////////
                    //               guests
                    ///////////////////////////////////////////
                    guests_commands = getConfigBoolean("Core.guest.commands", false);
                    guests_movement = getConfigBoolean("Core.guest.movement", false);
                    guests_inventory = getConfigBoolean("Core.guest.inventory", false);
                    guests_drop = getConfigBoolean("Core.guest.drop", false);
                    guests_pickup = getConfigBoolean("Core.guest.pickup", false);
                    guests_health = getConfigBoolean("Core.guest.health", false);
                    guests_mobdamage = getConfigBoolean("Core.guest.mobdamage", false);
                    guests_interact = getConfigBoolean("Core.guest.interactions", false);
                    guests_build = getConfigBoolean("Core.guest.building", false);
                    guests_destroy = getConfigBoolean("Core.guest.destruction", false);
                    guests_chat = getConfigBoolean("Core.guest.chat", false);
                    guests_mobtargeting = getConfigBoolean("Core.guest.mobtargeting", false);
                    guests_pvp = getConfigBoolean("Core.guest.pvp", false);

                    ///////////////////////////////////////////
                    //               protection
                    ///////////////////////////////////////////
                    protection_notify = getConfigBoolean("Core.protection.notify", true);
                    protection_delay_length = Util.split(getConfigString("Core.protection.delay", "3 seconds"), " ")[0];
                    protection_delay_time = Util.split(getConfigString("Core.protection.delay", "3 seconds"), " ")[1];
                    protection_delay = Util.toSeconds(protection_delay_time,protection_delay_length);

                    ///////////////////////////////////////////
                    //               filter
                    ///////////////////////////////////////////
                    filter_action = Util.getAction(getConfigString("Core.filter.action", "kick").toLowerCase());
                    filter_username = getConfigString("Core.filter.username", "`~!@#$%^&*()-= + {[]}|\\:;\"<,>.?/");
                    filter_password = getConfigString("Core.filter.password", "&");
                    filter_whitelist= getConfigString("Core.filter.whitelist", "");

                    ///////////////////////////////////////////
                    //               geoip
                    ///////////////////////////////////////////
                    geoip_enabled = getConfigBoolean("Core.geopip.enabled", true);

                ///////////////////////////////////////////
                //               plugins
                ///////////////////////////////////////////

                    ///////////////////////////////////////////
                    //               CraftIRC
                    ///////////////////////////////////////////
                    CraftIRC_enabled = getConfigBoolean("Plugins.CraftIRC.enabled", true);
                    CraftIRC_tag = getConfigString("Plugins.CraftIRC.tag", "admin");
                    CraftIRC_prefix = getConfigString("Plugins.CraftIRC.prefix", "%b%%green%[{PLUGIN}]%k%%b%");

                        ///////////////////////////////////////////
                        //               messages
                        ///////////////////////////////////////////
                        CraftIRC_messages_enabled = getConfigBoolean("Plugins.CraftIRC.messages.enabled", true);
                        CraftIRC_messages_welcome_enabled = getConfigBoolean("Plugins.CraftIRC.messages.welcome", true);
                        CraftIRC_messages_register_enabled = getConfigBoolean("Plugins.CraftIRC.messages.register", true);
                        CraftIRC_messages_unregister_enabled = getConfigBoolean("Plugins.CraftIRC.messages.unregister", true);
                        CraftIRC_messages_login_enabled = getConfigBoolean("Plugins.CraftIRC.messages.login", true);
                        CraftIRC_messages_email_enabled = getConfigBoolean("Plugins.CraftIRC.messages.email", true);
                        CraftIRC_messages_username_enabled = getConfigBoolean("Plugins.CraftIRC.messages.username", true);
                        CraftIRC_messages_password_enabled = getConfigBoolean("Plugins.CraftIRC.messages.password", true);
                        CraftIRC_messages_idle_enabled = getConfigBoolean("Plugins.CraftIRC.messages.idle", true);

            } else if (config.equalsIgnoreCase("messages")) {
            ///////////////////////////////////////////
            //  messages
            ///////////////////////////////////////////

                ///////////////////////////////////////////
                //               AuthDB
                ///////////////////////////////////////////

                    ///////////////////////////////////////////
                    //               guest
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_reload_success = Config.getConfigString("Core.reload.success", "AuthDB has successfully reloaded!");

                
                    ///////////////////////////////////////////
                    //               welcome
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_database_failure = Config.getConfigString("Core.database.failure", "{RED}database connection failed! Access is denied! Contact admin.");
                    ///////////////////////////////////////////
                    //               welcome
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_welcome_guest = (String)Config.getConfigString("Core.welcome.guest", "{YELLOW}Welcome {WHITE}guest{YELLOW}! Please use {REGISTERCMD} password email or {LINKCMD} otherusername password");
                    //Messages.AuthDB_message_welcome_user = (String)Config.getConfigString("Core.welcome.user", "{YELLOW}Welcome back {WHITE}{PLAYER}{YELLOW}! Please login with /login password");
                    
                    ///////////////////////////////////////////
                    //               register
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_register_success = Config.getConfigString("Core.register.success", "{RED}You have been registered!");
                    Messages.AuthDB_message_register_failure = Config.getConfigString("Core.register.failure", "{RED}Registration failed!");
                    Messages.AuthDB_message_register_exists = Config.getConfigString("Core.register.exists", "{RED}You are already registered!");
                    Messages.AuthDB_message_register_disabled = Config.getConfigString("Core.register.disabled", "{RED}Registration not allowed!");
                    Messages.AuthDB_message_register_usage = Config.getConfigString("Core.register.usage", "{RED}Correct usage is: /register password email");
                    Messages.AuthDB_message_register_timeout = Config.getConfigString("Core.register.timeout", "Kicked because you failed to register within {REGISTERTIMEOUT}.");

                    ///////////////////////////////////////////
                    //               unregister
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_unregister_success = Config.getConfigString("Core.unregister.success", "{BRIGHTGREEN}Unregistered successfully!");
                    Messages.AuthDB_message_unregister_failure = Config.getConfigString("Core.unregister.failure", "{RED}An error occurred while unregistering!");
                    Messages.AuthDB_message_unregister_usage = Config.getConfigString("Core.unregister.usage", "{RED}Correct usage is: /unregister password");

                    ///////////////////////////////////////////
                    //               logout
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_logout_success = Config.getConfigString("Core.logout.success", "Successfully logged out!");
                    Messages.AuthDB_message_logout_failure = Config.getConfigString("Core.logout.failure", "You are not logged in!");
                    Messages.AuthDB_message_logout_admin = Config.getConfigString("Core.logout.admin", "You have been logged out by an admin.");
                    Messages.AuthDB_message_logout_admin_success = Config.getConfigString("Core.logout.adminsuccess", "Successfully logged out player, {PLAYER}.");
                    Messages.AuthDB_message_logout_admin_failure = Config.getConfigString("Core.logout.adminfailure", "You cannot logout player, {PLAYER}! That player is not logged in");
                    Messages.AuthDB_message_logout_admin_notfound = Config.getConfigString("Core.logout.adminnotfound", "Could not find player, {PLAYER}! Please try again.");
                    Messages.AuthDB_message_logout_usage = Config.getConfigString("Core.logout.usage", "{YELLOW}Correct usage is: {WHITE}{LOGOUTCMD}");
                    ///////////////////////////////////////////
                    //               login
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_login_normal = Config.getConfigString("Core.login.normal", "{YELLOW}Welcome back {WHITE}{PLAYER}{YELLOW}! Please use /login password");
                    Messages.AuthDB_message_login_prompt = Config.getConfigString("Core.login.prompt", "{WHITE}Welcome {TEAL}{PLAYER}{WHITE}! Please enter your password:");
                    Messages.AuthDB_message_login_success = Config.getConfigString("Core.login.success", "{BRIGHTGREEN}Password accepted. Welcome!");
                    Messages.AuthDB_message_login_failure = Config.getConfigString("Core.login.failure", "{RED}Password incorrect, please try again.");
                    Messages.AuthDB_message_login_authorized = Config.getConfigString("Core.login.authorized", "{BRIGHTGREEN}You are already logged in!");
                    Messages.AuthDB_message_login_notregistered = Config.getConfigString("Core.login.notregistered", "{RED}You are not registred yet!");
                    Messages.AuthDB_message_login_timeout = Config.getConfigString("Core.login.timeout", "Kicked because you failed to login within {LOGINTIMEOUT}.");
                    Messages.AuthDB_message_login_admin = Config.getConfigString("Core.login.admin", "You have been logged in by an admin.");
                    Messages.AuthDB_message_login_admin_success = Config.getConfigString("Core.login.admin.success", "Successfully logged in player, {PLAYER}.");
                    Messages.AuthDB_message_login_admin_failure = Config.getConfigString("Core.login.adminfailure", "You cannot login player {PLAYER}! That player is already logged in.");
                    Messages.AuthDB_message_login_admin_notfound = Config.getConfigString("Core.login.adminnotfound", "Could not find player, {PLAYER}! Please try again.");
                    Messages.AuthDB_message_login_usage = Config.getConfigString("Core.login.usage", "{RED}Correct usage is: /login password");

                    ///////////////////////////////////////////
                    //               link
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_link_success = Config.getConfigString("Core.link.success", "{BRIGHTGREEN}You have successfully linked!. You are now logged in");
                    Messages.AuthDB_message_link_failure = Config.getConfigString("Core.link.failure", "{RED}Error while linking!");
                    Messages.AuthDB_message_link_exists = Config.getConfigString("Core.link.exists", "{RED}You are already linked to a username!");
                    Messages.AuthDB_message_link_usage = Config.getConfigString("Core.link.usage", "{RED}Correct usage is: /link otherusername password");

                    ///////////////////////////////////////////
                    //               unlink
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_unlink_success = Config.getConfigString("Core.unlink.success", "{BRIGHTGREEN}You have successfully unlinked!");
                    Messages.AuthDB_message_unlink_failure = Config.getConfigString("Core.unlink.failure", "{RED}Error while unlinking!");
                    Messages.AuthDB_message_unlink_nonexist = Config.getConfigString("Core.unlink.nonexist", "{RED}You do not have a linked username!");
                    Messages.AuthDB_message_unlink_usage = Config.getConfigString("Core.unlink.usage", "{RED}Correct usage is: /unlink otherusername password");

                    ///////////////////////////////////////////
                    //               email
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_email_required = Config.getConfigString("Core.email.required", "{RED}Email required for registration!");
                    Messages.AuthDB_message_email_invalid = Config.getConfigString("Core.email.invalid", "{RED}Invalid email! Please try again!");
                    Messages.AuthDB_message_email_badcharacters = Config.getConfigString("Core.email.badcharacters", "{RED}Email contains bad characters! {BADCHARACTERS}!");

                    ///////////////////////////////////////////
                    //               filter
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_filter_renamed = Config.getConfigString("Core.filter.renamed", "{RED}{PLAYER} renamed to {PLAYERNEW} due to bad characters: {USERBADCHARACTERS}.");
                    Messages.AuthDB_message_filter_username = Config.getConfigString("Core.filter.username", "{RED}Username contains bad characters: {USERBADCHARACTERS}!");
                    Messages.AuthDB_message_filter_password = Config.getConfigString("Core.filter.password", "{RED}Password contains bad characters: {PASSBADCHARACTERS}!");
                    Messages.AuthDB_message_filter_whitelist = Config.getConfigString("Core.filter.whitelist", "{BRIGHTGREEN}{PLAYER} is on the filter {WHITE}whitelist{BRIGHTGREEN}, bypassing restrictions!");

                    ///////////////////////////////////////////
                    //               username
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_username_minimum = Config.getConfigString("Core.username.minimum", "{RED}Your username does not meet the minimum requirement of {USERMIN} characters!");
                    Messages.AuthDB_message_username_maximum = Config.getConfigString("Core.username.maximum", "{RED}Your username does not meet the maximum requirement of {USERMAX} characters!");

                    ///////////////////////////////////////////
                    //               password
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_password_minimum = Config.getConfigString("Core.password.minimum", "{RED}Your password does not meet the minimum requirement of {PASSMIN} characters!");
                    Messages.AuthDB_message_password_maximum = Config.getConfigString("Core.password.maximum", "{RED}Your password does not meet the maximum requirement of {PASSMAX} characters!");
                    Messages.AuthDB_message_password_success = Config.getConfigString("Core.password.success", "{BRIGHTGREEN}Password changed successfully!");
                    Messages.AuthDB_message_password_failure = Config.getConfigString("Core.password.failure", "{RED}Error! Password change failed!");
                    Messages.AuthDB_message_password_notregistered = Config.getConfigString("Core.password.notregistered", "{RED}Register first!");
                    Messages.AuthDB_message_password_usage = Config.getConfigString("Core.password.usage", "{RED}Correct usage is: /password oldpassword password");

                    Messages.AuthDB_message_session_valid = Config.getConfigString("Core.session.valid", "{BRIGHTGREEN}Hey, I remember you! You are logged in!");
                    Messages.AuthDB_message_session_protected = Config.getConfigString("Core.session.protected", "{RED}Sorry, a player with that name is already logged in on this server.");

                    ///////////////////////////////////////////
                    //               idle
                    ///////////////////////////////////////////
                    Messages.AuthDB_message_protection_denied = Config.getConfigString("Core.protection.denied", "You do not have permission to use that command.");
                    Messages.AuthDB_message_protection_notauthorized = Config.getConfigString("Core.protection.notauthorized", "{RED}You are not authorized to do that!");
 
                ///////////////////////////////////////////
                //               CraftIRC
                ///////////////////////////////////////////

                    ///////////////////////////////////////////
                    //               status
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_status_join = Config.getConfigString("Plugins.CraftIRC.status.join", "{PLAYER} has joined the server.");
                    Messages.CraftIRC_message_status_quit = Config.getConfigString("Plugins.CraftIRC.status.quit", "{PLAYER} has quit the server.");

                    ///////////////////////////////////////////
                    //               register
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_register_success = Config.getConfigString("Plugins.CraftIRC.register.success", "{PLAYER} just registered successfully!");
                    Messages.CraftIRC_message_register_failure = Config.getConfigString("Plugins.CraftIRC.register.failure", "{PLAYER} had some errors while registering!");
                    Messages.CraftIRC_message_register_registered = Config.getConfigString("Plugins.CraftIRC.register.registered", "{PLAYER} had a lapse in memory and tried to register again.");

                    ///////////////////////////////////////////
                    //               password
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_password_success = Config.getConfigString("Plugins.CraftIRC.password.success", "{PLAYER} logged in successfully!");
                    Messages.CraftIRC_message_password_failure = Config.getConfigString("Plugins.CraftIRC.password.failure", "{PLAYER} tried to login with the wrong password!");
                    ///////////////////////////////////////////
                    //               idle
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_idle_kicked = Config.getConfigString("Plugins.CraftIRC.idle.kicked", "{PLAYER} was kicked due to bad characters in username!");
                    Messages.CraftIRC_message_idle_whitelist = Config.getConfigString("Plugins.CraftIRC.idle.whitelist", "{PLAYER} is on the on bad characters whitelist, bypassing restictions!");


                    ///////////////////////////////////////////
                    //               filter
                    ///////////////////////////////////////////
                    Messages.CraftIRC_message_filter_renamed = Config.getConfigString("Plugins.CraftIRC.filter.renamed", "{PLAYER} renamed to {PLAYERNEW} due to bad characters.");
                    Messages.CraftIRC_message_filter_kicked = Config.getConfigString("Plugins.CraftIRC.filter.kicked", "{PLAYER} was kicked due to bad characters in username!");
                    Messages.CraftIRC_message_filter_whitelist = Config.getConfigString("Plugins.CraftIRC.filter.whitelist", "{PLAYER} is on the on bad characters whitelist, bypassing restictions!");

            } else if (config.equalsIgnoreCase("commands")) {
                commands_register = Config.getConfigString("Core.commands.register", "/register");
                commands_link = Config.getConfigString("Core.commands.link", "/link");
                commands_unlink = Config.getConfigString("Core.commands.unlink", "/unlink");
                commands_login = Config.getConfigString("Core.commands.login", "/login");
                commands_logout = Config.getConfigString("Core.commands.logout", "/logout");
                commands_setspawn = Config.getConfigString("Core.commands.setspawn", "/authdb setspawn");
                commands_reload = Config.getConfigString("Core.commands.reload", "/authdb reload");

                aliases_register = Config.getConfigString("Core.aliases.register", "/r");
                aliases_link = Config.getConfigString("Core.aliases.link", "/li");
                aliases_unlink = Config.getConfigString("Core.aliases.unlink", "/ul");
                aliases_login = Config.getConfigString("Core.aliases.login", "/l");
                aliases_logout = Config.getConfigString("Core.aliases.logout", "/lo");
                aliases_setspawn = Config.getConfigString("Core.aliases.setspawn", "/s");
                aliases_reload = Config.getConfigString("Core.aliases.reload", "/ar");
                save("Core.aliases.reload", "HELLO");
            }
      }

      public static String getConfigString(String key, String defaultvalue) {
        return template.getString(key, defaultvalue);
      }

      public static boolean getConfigBoolean(String key, boolean defaultvalue) {
        return template.getBoolean(key, defaultvalue);
      }

      public void deleteConfigValue(String key) {
        template.removeProperty(key);
      }

      public String raw(String key, String line) {
        return template.getString(key, line);
      }

      public void save(String key, String line) {
          template.setProperty(key, line);
      }
}
