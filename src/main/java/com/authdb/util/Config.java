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
		public static String custom_table,custom_userfield,custom_passfield,custom_encryption,custom_emailfield,custom_;
		public static boolean custom_enabled,custom_salt;
		
		///////////////////////////////////////////
		//               register
		///////////////////////////////////////////
		public static boolean register_enabled,register_force;
		
		///////////////////////////////////////////
		//               login
		///////////////////////////////////////////
		public static String login_method,login_tries;
		public static boolean login_kick,login_ban;
		
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
		public static String session_time,session_length,session_start;
		public static int session_seconds;
		
		///////////////////////////////////////////
		//               idle
		///////////////////////////////////////////
		public static String idle_time = "seconds",idle_length = "30",idle_whitelist="";
		public static boolean idle_kick;
		public static int idle_ticks;
		
		///////////////////////////////////////////
		//               guests
		///////////////////////////////////////////
		public static boolean guests_commands,guests_movement,guests_inventory,guests_drop,guests_pickup,guests_health,guests_mobdamage,guests_interact,guests_build,guests_destroy,guests_chat,guests_mobtargeting,guests_pvp;
  
		///////////////////////////////////////////
		//               filter
		///////////////////////////////////////////
		public static boolean filter_kick,filter_rename;
		public static String filter_username,filter_password,filter_whitelist="";
		
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
					//database_username = Util.fixCharacters(database_username);
					database_password =  GetConfigString("Core.database.password", "root");
					//database_password = Util.fixCharacters(database_password);
					database_port =  GetConfigString("Core.database.port", "3306");
					database_host =  GetConfigString("Core.database.host", "localhost");
					database_database = GetConfigString("Core.database.name", "forum");
					database_prefix = GetConfigString("Core.database.prefix", "");
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
					custom_table = GetConfigString("Core.customdb.table", "authdb_users");
					custom_userfield = GetConfigString("Core.customdb.userfield", "username");
					custom_passfield = GetConfigString("Core.customdb.passfield", "password");
					custom_emailfield = GetConfigString("Core.customdb.emailfield", "");
					custom_encryption = GetConfigString("Core.customdb.encryption", "").toLowerCase();
					
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
					login_kick = GetConfigBoolean("Core.login.kick", true);
					login_ban = GetConfigBoolean("Core.login.ban", false);
					
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
					session_time = GetConfigString("Core.session.time", "minutes");
					session_length = GetConfigString("Core.session.length", "60");
					session_seconds = Util.ToSeconds(session_time,session_length);
					session_start = GetConfigString("Core.session.start", "login");
					session_start = Util.CheckSessionStart(session_start);
					
					///////////////////////////////////////////
					//               idle
					///////////////////////////////////////////
					idle_time = GetConfigString("Core.idle.time", "seconds");
					idle_length= GetConfigString("Core.idle.length", "30");
					idle_kick= GetConfigBoolean("Core.idle.kick", true);
					idle_whitelist= GetConfigString("Core.idle.whitelist", "");
					idle_ticks = Util.ToTicks(idle_time,idle_length);
					
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
					filter_kick = GetConfigBoolean("Core.filter.kick", true);
					filter_rename = GetConfigBoolean("Core.filter.rename", false);
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
					Messages.AuthDB_message_login_session= Config.GetConfigString("Core.login.session", "<lightgreen>, I remember you! You are logged in!");
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
