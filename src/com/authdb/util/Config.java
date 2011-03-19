/**          © Copyright 2011 Contex <contexmoh@gmail.com>
	
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
	public static String script_name1 = "phpbb3";
	public static String script_name2 = "smf1";
	public static String script_name3 = "smf2";
	public static String script_name4 = "mybb1_6";
	public static String script_name5 = "vb4_1";
	
	///////////////////////////////////////////
	//               GLOBAL
	///////////////////////////////////////////
	public static boolean has_badcharacters;
	
	///////////////////////////////////////////
	//               Database
	///////////////////////////////////////////
	public static String database_driver, database_username,database_password,database_port,database_host,database_database,database_prefix,dbDb;
	
	///////////////////////////////////////////
	//               settings
	///////////////////////////////////////////
	
		///////////////////////////////////////////
		//               debug
		///////////////////////////////////////////
		public static boolean debug_enable;
	
		///////////////////////////////////////////
		//               script
		///////////////////////////////////////////
		public static String script_name;
		public static boolean script_updatestatus;
		
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
		public static String badcharacters_characters;
		
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
				//               settings
				///////////////////////////////////////////
				
					///////////////////////////////////////////
					//               script
					///////////////////////////////////////////
					debug_enable = GetConfigBoolean("settings.debug.enabled", false);
				
					///////////////////////////////////////////
					//               script
					///////////////////////////////////////////
					script_name = GetConfigString("settings.script.name", "phpbb3").toLowerCase();
					script_updatestatus = GetConfigBoolean("settings.script.updatestatus", true);
					
					///////////////////////////////////////////
					//               database
					///////////////////////////////////////////
					database_driver =  GetConfigString("settings.database.driver", "mysql");
					database_username =  GetConfigString("settings.database.username", "root");
					database_password =  GetConfigString("settings.database.password", "");
					database_port =  GetConfigString("settings.database.port", "3306");
					database_host =  GetConfigString("settings.database.host", "localhost");
					database_database = GetConfigString("settings.database.database", "minecraft_forum");
					database_prefix = GetConfigString("settings.database.prefix", "");
					dbDb = "jdbc:mysql://"+database_host+":"+database_port+"/"+database_database;
					
					
					///////////////////////////////////////////
					//               register
					///////////////////////////////////////////
					register_enabled = GetConfigBoolean("settings.register.enabled", true);
					register_force = GetConfigBoolean("settings.register.force", true);
					
					///////////////////////////////////////////
					//               password
					///////////////////////////////////////////
					password_tries = GetConfigString("settings.password.tries", "3");
					password_kick = GetConfigBoolean("settings.password.false", true);
					password_ban = GetConfigBoolean("settings.password.ban", false);
					
					///////////////////////////////////////////
					//               session
					///////////////////////////////////////////
					session_time = GetConfigString("settings.session.time", "minutes");
					session_length = GetConfigString("settings.session.length", "60");
					
					///////////////////////////////////////////
					//               idle
					///////////////////////////////////////////
					idle_time = GetConfigString("settings.idle.time", "seconds");
					idle_length= GetConfigString("settings.idle.length", "30");
					idle_kick= GetConfigBoolean("settings.idle.kick", true);
					idle_whitelist= GetConfigString("settings.idle.whitelist", "");
					idle_ticks = Util.ToTicks(Config.idle_time,Config.idle_length);
			  
					///////////////////////////////////////////
					//               guests
					///////////////////////////////////////////
					guests_commands = GetConfigBoolean("settings.guests.commands", true);
					guests_movement = GetConfigBoolean("settings.guests.movement", true);
					guests_inventory = GetConfigBoolean("settings.guests.inventory", true);
					guests_drops = GetConfigBoolean("settings.guests.drops", true);
					guests_health = GetConfigBoolean("settings.guests.health", true);
					guests_damage = GetConfigBoolean("settings.guests.damage", true);
					guests_interact = GetConfigBoolean("settings.guests.interact", true);
					guests_build = GetConfigBoolean("settings.guests.build", true);
					guests_chat= GetConfigBoolean("settings.guests.chat", true);
			  
					///////////////////////////////////////////
					//               badcharacters
					///////////////////////////////////////////
					badcharacters_kick = GetConfigBoolean("settings.badcharacters.kick", true);
					badcharacters_remove = GetConfigBoolean("settings.badcharacters.remove", false);
					badcharacters_characters = GetConfigString("settings.badcharacters.characters", "$^@(#)!+\\-/");
					
					///////////////////////////////////////////
					//               geoip
					///////////////////////////////////////////
					geoip_enabled = GetConfigBoolean("settings.geopip.enabled", true);
			  
				///////////////////////////////////////////
				//               plugins
				///////////////////////////////////////////
				
					///////////////////////////////////////////
					//               CraftIRC
					///////////////////////////////////////////
					CraftIRC_enabled = GetConfigBoolean("plugins.CraftIRC.enabled", true);
					CraftIRC_tag = GetConfigString("plugins.CraftIRC.tag", "admin");
					CraftIRC_prefix = GetConfigString("plugins.CraftIRC.prefix", "%b%%green%[{PLUGIN}]%k%%b%");

						///////////////////////////////////////////
						//               messages
						///////////////////////////////////////////
						CraftIRC_messages_enabled = GetConfigBoolean("plugins.CraftIRC.messages.enabled", true);
						CraftIRC_messages_welcome_enabled = GetConfigBoolean("plugins.CraftIRC.messages.welcome", true);
						CraftIRC_messages_register_enabled = GetConfigBoolean("plugins.CraftIRC.messages.register", true);
						CraftIRC_messages_unregister_enabled = GetConfigBoolean("plugins.CraftIRC.messages.unregister", true);
						CraftIRC_messages_login_enabled = GetConfigBoolean("plugins.CraftIRC.messages.login", true);
						CraftIRC_messages_email_enabled = GetConfigBoolean("plugins.CraftIRC.messages.email", true);
						CraftIRC_messages_username_enabled = GetConfigBoolean("plugins.CraftIRC.messages.username", true);
						CraftIRC_messages_password_enabled = GetConfigBoolean("plugins.CraftIRC.messages.password", true);
						CraftIRC_messages_idle_enabled = GetConfigBoolean("plugins.CraftIRC.messages.idle", true);
						
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
					Messages.AuthDB_message_welcome_guest = (String)Config.GetConfigString("messages.AuthDB.welcome.guest", "&4Welcome guest! Please register with /register <password> <email>");
					Messages.AuthDB_message_welcome_user = (String)Config.GetConfigString("messages.AuthDB.welcome.user", "&4Welcome back {PLAYER}! Please login with /login <password>");
					
					///////////////////////////////////////////
					//               register
					///////////////////////////////////////////
					Messages.AuthDB_message_register_success = Config.GetConfigString("messages.AuthDB.register.success", "&4You have been registered!");
					Messages.AuthDB_message_register_failure = Config.GetConfigString("messages.AuthDB.register.failure", "&4Error while registering!");
					Messages.AuthDB_message_register_registered = Config.GetConfigString("messages.AuthDB.register.registered", "&4You are already registered!");
					Messages.AuthDB_message_register_disabled = Config.GetConfigString("messages.AuthDB.register.disabled", "&4Registration not allowed!");
					Messages.AuthDB_message_register_usage = Config.GetConfigString("messages.AuthDB.register.usage", "&4Correct usage is: /register <password> <email>");
					
					///////////////////////////////////////////
					//               unregister
					///////////////////////////////////////////
					Messages.AuthDB_message_unregister_success = Config.GetConfigString("messages.AuthDB.unregister.success", "&2Unregistered successfully!");
					Messages.AuthDB_message_unregister_failure = Config.GetConfigString("messages.AuthDB.unregister.failure", "&4An error occurred while unregistering!");
					Messages.AuthDB_message_unregister_usage = Config.GetConfigString("messages.AuthDB.unregister.usage", "&4Correct usage is: /unregister <password>");
					
					///////////////////////////////////////////
					//               login
					///////////////////////////////////////////
					Messages.AuthDB_message_login_success = Config.GetConfigString("messages.AuthDB.login.success", "&2Password accepted. Welcome!");
					Messages.AuthDB_message_login_failure = Config.GetConfigString("messages.AuthDB.login.failure", "&4Password incorrect, please try again.");
					Messages.AuthDB_message_login_authorized = Config.GetConfigString("messages.AuthDB.login.authorized", "&2You are already logged in!");
					Messages.AuthDB_message_login_session= Config.GetConfigString("messages.AuthDB.login.session", "&2Hey, I remember you! You are logged in!");
					Messages.AuthDB_message_login_usage = Config.GetConfigString("messages.AuthDB.login.usage", "&4Correct usage is: /login <password>");
					
					///////////////////////////////////////////
					//               email
					///////////////////////////////////////////
					Messages.AuthDB_message_email_required = Config.GetConfigString("messages.AuthDB.email.required", "&4Email required for registration!");
					Messages.AuthDB_message_email_badcharacters = Config.GetConfigString("messages.AuthDB.email.badcharacters", "&4Email contains bad characters: {BADCHARACTERS}!");
					
					///////////////////////////////////////////
					//               username
					///////////////////////////////////////////
					Messages.AuthDB_message_badcharacters_renamed = Config.GetConfigString("messages.AuthDB.badcharacters.renamed", "&2{PLAYER} renamed to {PLAYERNEW} due to bad characters: {BADCHARACTERS}.");
					Messages.AuthDB_message_badcharacters_kicked = Config.GetConfigString("messages.AuthDB.badcharacters.kicked", "Username contains bad characters: {BADCHARACTERS}!");
					Messages.AuthDB_message_badcharacters_whitelist = Config.GetConfigString("messages.AuthDB.badcharacters.whitelist", "{PLAYER} on the on bad characters whitelist, bypassing restictions!");
					
					///////////////////////////////////////////
					//               password
					///////////////////////////////////////////
					Messages.AuthDB_message_password_success = Config.GetConfigString("messages.AuthDB.password.success", "&2Password changed successfully!");
					Messages.AuthDB_message_password_failure = Config.GetConfigString("messages.AuthDB.password.failure", "&4Error! Password change failed!");
					Messages.AuthDB_message_password_notregistered = Config.GetConfigString("messages.AuthDB.password.notregistered", "&4Register first!");
					Messages.AuthDB_message_password_usage = Config.GetConfigString("messages.AuthDB.password.usage", "&4Correct usage is: /password <oldpassword> <password>");
					
					///////////////////////////////////////////
					//               idle
					///////////////////////////////////////////
					Messages.AuthDB_message_idle_kick = Config.GetConfigString("messages.AuthDB.idle.kick", "Kicked because you failed to login within {IDLELENGTH} {IDLETIME}.");
					
				///////////////////////////////////////////
				//               CraftIRC
				///////////////////////////////////////////
					
					///////////////////////////////////////////
					//               status
					///////////////////////////////////////////
					Messages.CraftIRC_message_status_join = Config.GetConfigString("messages.CraftIRC.status.guest", "{PLAYER} has joined the server from {PROVINCE}{STATE}, {COUNTRY} ({IP}).");
					Messages.CraftIRC_message_status_quit = Config.GetConfigString("messages.CraftIRC.status.user", "{PLAYER} has quit the server.");
					
					///////////////////////////////////////////
					//               register
					///////////////////////////////////////////
					Messages.CraftIRC_message_register_success = Config.GetConfigString("messages.CraftIRC.register.success", "{PLAYER} just registered successfully!");
					Messages.CraftIRC_message_register_failure = Config.GetConfigString("messages.CraftIRC.register.failure", "{PLAYER} had some errors while registering!");
					Messages.CraftIRC_message_register_registered = Config.GetConfigString("messages.CraftIRC.register.registered", "{PLAYER} had a lapse in memory and tried to register again.");
					
					///////////////////////////////////////////
					//               badcharacters
					///////////////////////////////////////////
					Messages.CraftIRC_message_badcharacters_renamed = Config.GetConfigString("messages.CraftIRC.badcharacters.renamed", "{PLAYER} renamed to {PLAYERNEW} due to bad characters.");
					Messages.CraftIRC_message_badcharacters_kicked = Config.GetConfigString("messages.CraftIRC.badcharacters.kicked", "{PLAYER} was kicked due to bad characters in username!");
					Messages.CraftIRC_message_badcharacters_whitelist = Config.GetConfigString("messages.CraftIRC.badcharacters.whitelist", "{PLAYER} on the on bad characters whitelist, bypassing restictions!");
					
					///////////////////////////////////////////
					//               password
					///////////////////////////////////////////
					Messages.CraftIRC_message_password_success = Config.GetConfigString("messages.CraftIRC.password.success", "{PLAYER} logged in successfully!");
					Messages.CraftIRC_message_password_failure = Config.GetConfigString("messages.CraftIRC.password.failure", "{PLAYER} tried to login with the wrong password!");
					
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
