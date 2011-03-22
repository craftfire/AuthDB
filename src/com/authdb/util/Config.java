/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
**/

package com.authdb.util;

import java.io.File;

import org.bukkit.util.config.Configuration;

//import com.ensifera.animosity.craftirc.CraftIRC;


public class Config 
{
	public static String Script1_latest = "3.0.8";
	public static String Script1_versions = "3.0.8";
	public static String Script1_latest2 = "2.0.23";
	public static String Script1_versions2 = "2.0.23";
	public static String Script1_name = "phpbb";
	///
	public static String Script2_latest = "1";
	public static String Script2_latest2 = "2";
	public static String Script2_versions = "1";
	public static String Script2_versions2 = "2";
	public static String Script2_name = "smf";
	///
	public static String Script3_latest = "1.6";
	public static String Script3_versions = "1.6";
	public static String Script3_name = "mybb";
	///
	public static String Script4_latest = "4.1.2";
	public static String Script4_versions = "4.1.2";
	public static String Script4_latest2 = "3.8.7";
	public static String Script4_versions2 = "3.8.7";
	public static String Script4_name = "vb";
	
	public static String Script5_latest = "6.20";
	public static String Script5_versions = "6.20";
	public static String Script5_name = "drupal";
	
	public static String Script6_latest = "1.5.22";
	public static String Script6_versions = "1.5.22";
	public static String Script6_latest2 = "1.6.1";
	public static String Script6_versions2 = "1.6.1";
	public static String Script6_name = "joomla";
	///
	
	///////////////////////////////////////////
	//               GLOBAL
	///////////////////////////////////////////
	public static boolean database_ison;
	public static boolean has_badcharacters;
	public static boolean HasForumBoard;
	
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
		public static String custom_table,custom_userfield,custom_passfield,custom_encryption;
		public static boolean custom_enabled;
		
		///////////////////////////////////////////
		//               register
		///////////////////////////////////////////
		public static boolean register_enabled,register_force;
		
		///////////////////////////////////////////
		//               password
		///////////////////////////////////////////
		public static String password_tries;
		public static boolean password_kick,password_ban;
		
		///////////////////////////////////////////
		//               session
		///////////////////////////////////////////
		public static String session_time,session_length;
		
		///////////////////////////////////////////
		//               idle
		///////////////////////////////////////////
		public static String idle_time = "seconds",idle_length = "30",idle_whitelist="";
		public static boolean idle_kick;
		public static int idle_ticks;
  
		///////////////////////////////////////////
		//               guests
		///////////////////////////////////////////
		public static boolean guests_commands,guests_movement,guests_inventory,guests_drops,guests_health,guests_damage,guests_interact,guests_build,guests_chat;
  
		///////////////////////////////////////////
		//               badcharacters
		///////////////////////////////////////////
		public static boolean badcharacters_kick,badcharacters_remove;
		public static String badcharacters_characters,badcharacters_whitelist="";
		
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
				    autoupdate_enable = GetConfigBoolean("Core.plugin.autoupdate", false);
					debug_enable = GetConfigBoolean("Core.plugin.debugmode", false);
					usagestats_enabled = GetConfigBoolean("Core.plugin.usagestats", true);
				
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
					custom_enabled = GetConfigBoolean("Core.custom.enabled", false);
					custom_table = GetConfigString("Core.custom.table", "users");
					custom_userfield = GetConfigString("Core.custom.userfield", "username");
					custom_passfield = GetConfigString("Core.custom.passfield", "password");
					custom_encryption = GetConfigString("Core.custom.encryption", "").toLowerCase();
					
					
					///////////////////////////////////////////
					//               database
					///////////////////////////////////////////
					database_driver =  GetConfigString("Core.database.driver", "mysql");
					database_username =  GetConfigString("Core.database.username", "root");
					database_password =  GetConfigString("Core.database.password", "");
					database_port =  GetConfigString("Core.database.port", "3306");
					database_host =  GetConfigString("Core.database.host", "localhost");
					database_database = GetConfigString("Core.database.database", "minecraft_forum");
					database_prefix = GetConfigString("Core.database.prefix", "");
					dbDb = "jdbc:mysql://"+database_host+":"+database_port+"/"+database_database;
					
					
					///////////////////////////////////////////
					//               register
					///////////////////////////////////////////
					register_enabled = GetConfigBoolean("Core.register.enabled", true);
					register_force = GetConfigBoolean("Core.register.force", true);
					
					///////////////////////////////////////////
					//               password
					///////////////////////////////////////////
					password_tries = GetConfigString("Core.password.tries", "3");
					password_kick = GetConfigBoolean("Core.password.false", true);
					password_ban = GetConfigBoolean("Core.password.ban", false);
					
					///////////////////////////////////////////
					//               session
					///////////////////////////////////////////
					session_time = GetConfigString("Core.session.time", "minutes");
					session_length = GetConfigString("Core.session.length", "60");
					
					///////////////////////////////////////////
					//               idle
					///////////////////////////////////////////
					idle_time = GetConfigString("Core.idle.time", "seconds");
					idle_length= GetConfigString("Core.idle.length", "30");
					idle_kick= GetConfigBoolean("Core.idle.kick", true);
					idle_whitelist= GetConfigString("Core.idle.whitelist", "");
					idle_ticks = Util.ToTicks(Config.idle_time,Config.idle_length);
			  
					///////////////////////////////////////////
					//               guests
					///////////////////////////////////////////
					guests_commands = GetConfigBoolean("Core.guests.commands", true);
					guests_movement = GetConfigBoolean("Core.guests.movement", true);
					guests_inventory = GetConfigBoolean("Core.guests.inventory", true);
					guests_drops = GetConfigBoolean("Core.guests.drops", true);
					guests_health = GetConfigBoolean("Core.guests.health", true);
					guests_damage = GetConfigBoolean("Core.guests.damage", true);
					guests_interact = GetConfigBoolean("Core.guests.interact", true);
					guests_build = GetConfigBoolean("Core.guests.build", true);
					guests_chat= GetConfigBoolean("Core.guests.chat", true);
			  
					///////////////////////////////////////////
					//               badcharacters
					///////////////////////////////////////////
					badcharacters_kick = GetConfigBoolean("Core.badcharacters.kick", true);
					badcharacters_remove = GetConfigBoolean("Core.badcharacters.remove", false);
					badcharacters_characters = GetConfigString("Core.badcharacters.characters", "$^@(#)!+\\-/");
					badcharacters_whitelist= GetConfigString("Core.badcharacters.whitelist", "");
					
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
					Messages.AuthDB_message_database_failure = Config.GetConfigString("Core.AuthDB.database.failure", "&dDatabase connection failed! Contact admin.");
					///////////////////////////////////////////
					//               welcome
					///////////////////////////////////////////
					Messages.AuthDB_message_welcome_guest = (String)Config.GetConfigString("Core.AuthDB.welcome.guest", "&4Welcome guest! Please register with /register <password> <email>");
					Messages.AuthDB_message_welcome_user = (String)Config.GetConfigString("Core.AuthDB.welcome.user", "&4Welcome back {PLAYER}! Please login with /login <password>");
					
					///////////////////////////////////////////
					//               guest
					///////////////////////////////////////////
					Messages.AuthDB_message_guest_notauthorized = Config.GetConfigString("Core.AuthDB.guest.notauthorized", "&4You are not authorized to do that!");
					
					///////////////////////////////////////////
					//               register
					///////////////////////////////////////////
					Messages.AuthDB_message_register_success = Config.GetConfigString("Core.AuthDB.register.success", "&4You have been registered!");
					Messages.AuthDB_message_register_failure = Config.GetConfigString("Core.AuthDB.register.failure", "&4Error while registering!");
					Messages.AuthDB_message_register_registered = Config.GetConfigString("Core.AuthDB.register.registered", "&4You are already registered!");
					Messages.AuthDB_message_register_disabled = Config.GetConfigString("Core.AuthDB.register.disabled", "&4Registration not allowed!");
					Messages.AuthDB_message_register_usage = Config.GetConfigString("Core.AuthDB.register.usage", "&4Correct usage is: /register <password> <email>");
					
					///////////////////////////////////////////
					//               unregister
					///////////////////////////////////////////
					Messages.AuthDB_message_unregister_success = Config.GetConfigString("Core.AuthDB.unregister.success", "&2Unregistered successfully!");
					Messages.AuthDB_message_unregister_failure = Config.GetConfigString("Core.AuthDB.unregister.failure", "&4An error occurred while unregistering!");
					Messages.AuthDB_message_unregister_usage = Config.GetConfigString("Core.AuthDB.unregister.usage", "&4Correct usage is: /unregister <password>");
					
					///////////////////////////////////////////
					//               login
					///////////////////////////////////////////
					Messages.AuthDB_message_login_success = Config.GetConfigString("Core.AuthDB.login.success", "&2Password accepted. Welcome!");
					Messages.AuthDB_message_login_failure = Config.GetConfigString("Core.AuthDB.login.failure", "&4Password incorrect, please try again.");
					Messages.AuthDB_message_login_authorized = Config.GetConfigString("Core.AuthDB.login.authorized", "&2You are already logged in!");;
					Messages.AuthDB_message_login_notregistered = Config.GetConfigString("Core.AuthDB.login.notregistered", "&4You are not registred yet!");
					Messages.AuthDB_message_login_session= Config.GetConfigString("Core.AuthDB.login.session", "&2Hey, I remember you! You are logged in!");
					Messages.AuthDB_message_login_usage = Config.GetConfigString("Core.AuthDB.login.usage", "&4Correct usage is: /login <password>");
					
					///////////////////////////////////////////
					//               email
					///////////////////////////////////////////
					Messages.AuthDB_message_email_required = Config.GetConfigString("Core.AuthDB.email.required", "&4Email required for registration!");
					Messages.AuthDB_message_email_invalid = Config.GetConfigString("Core.AuthDB.email.invalid", "&4Invalid email! Please try again!");
					
					///////////////////////////////////////////
					//               username
					///////////////////////////////////////////
					Messages.AuthDB_message_badcharacters_renamed = Config.GetConfigString("Core.AuthDB.badcharacters.renamed", "&2{PLAYER} renamed to {PLAYERNEW} due to bad characters: {BADCHARACTERS}.");
					Messages.AuthDB_message_badcharacters_kicked = Config.GetConfigString("Core.AuthDB.badcharacters.kick", "Username contains bad characters: {BADCHARACTERS}!");
					Messages.AuthDB_message_badcharacters_whitelist = Config.GetConfigString("Core.AuthDB.badcharacters.whitelist", "&2{PLAYER} on the bad characters &fwhitelist&2, bypassing restrictions!");
					
					///////////////////////////////////////////
					//               password
					///////////////////////////////////////////
					Messages.AuthDB_message_password_success = Config.GetConfigString("Core.AuthDB.password.success", "&2Password changed successfully!");
					Messages.AuthDB_message_password_failure = Config.GetConfigString("Core.AuthDB.password.failure", "&4Error! Password change failed!");
					Messages.AuthDB_message_password_notregistered = Config.GetConfigString("Core.AuthDB.password.notregistered", "&4Register first!");
					Messages.AuthDB_message_password_usage = Config.GetConfigString("Core.AuthDB.password.usage", "&4Correct usage is: /password <oldpassword> <password>");
					
					///////////////////////////////////////////
					//               idle
					///////////////////////////////////////////
					Messages.AuthDB_message_idle_kick = Config.GetConfigString("Core.AuthDB.idle.kick", "Kicked because you failed to login within {IDLELENGTH} {IDLETIME}.");
					Messages.AuthDB_message_idle_whitelist = Config.GetConfigString("Core.AuthDB.idle.whitelist", "{PLAYER} on the idle whitelist, bypassing restrictions!");
					
				///////////////////////////////////////////
				//               CraftIRC
				///////////////////////////////////////////
					
					///////////////////////////////////////////
					//               status
					///////////////////////////////////////////
					Messages.CraftIRC_message_status_join = Config.GetConfigString("Plugins.CraftIRC.status.guest", "{PLAYER} has joined the server from {PROVINCE}{STATE}, {COUNTRY} ({IP}).");
					Messages.CraftIRC_message_status_quit = Config.GetConfigString("Plugins.CraftIRC.status.user", "{PLAYER} has quit the server.");
					
					///////////////////////////////////////////
					//               register
					///////////////////////////////////////////
					Messages.CraftIRC_message_register_success = Config.GetConfigString("Plugins.CraftIRC.register.success", "{PLAYER} just registered successfully!");
					Messages.CraftIRC_message_register_failure = Config.GetConfigString("Plugins.CraftIRC.register.failure", "{PLAYER} had some errors while registering!");
					Messages.CraftIRC_message_register_registered = Config.GetConfigString("Plugins.CraftIRC.register.registered", "{PLAYER} had a lapse in memory and tried to register again.");
					
					///////////////////////////////////////////
					//               badcharacters
					///////////////////////////////////////////
					Messages.CraftIRC_message_badcharacters_renamed = Config.GetConfigString("Plugins.CraftIRC.badcharacters.renamed", "{PLAYER} renamed to {PLAYERNEW} due to bad characters.");
					Messages.CraftIRC_message_badcharacters_kicked = Config.GetConfigString("Plugins.CraftIRC.badcharacters.kicked", "{PLAYER} was kicked due to bad characters in username!");
					Messages.CraftIRC_message_badcharacters_whitelist = Config.GetConfigString("Plugins.CraftIRC.badcharacters.whitelist", "{PLAYER} on the on bad characters whitelist, bypassing restictions!");
					
					///////////////////////////////////////////
					//               password
					///////////////////////////////////////////
					Messages.CraftIRC_message_password_success = Config.GetConfigString("Plugins.CraftIRC.password.success", "{PLAYER} logged in successfully!");
					Messages.CraftIRC_message_password_failure = Config.GetConfigString("Plugins.CraftIRC.password.failure", "{PLAYER} tried to login with the wrong password!");
					
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
