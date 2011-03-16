package com.gmail.contexmoh.authdb.utils;

import java.io.File;

import org.bukkit.util.config.Configuration;

public class Config 
{
	public static boolean allowRegister = GetConfigBoolean("settings.allow-register", true);
	public static boolean forceRegister = GetConfigBoolean("settings.force-register", true);
	public static boolean kickOnBadPassword = GetConfigBoolean("settings.kick-on-bad-password", true);
	public static String forumBoard = GetConfigString("settings.forum-board", "phpBB3");
	public static boolean specialCharactersKick = GetConfigBoolean("illegal-characters.kick", true);
	public static boolean specialCharactersChange = GetConfigBoolean("illegal-characters.change", true);
	public static String specialCharactersList = GetConfigString("illegal-characters.characters", "$^@(#)!+");
	
	///////////////////////////////////////////
	//               Database
	///////////////////////////////////////////
	public static String database_driver =  GetConfigString("database.driver", "com.mysql.jdbc.Driver");
	public static String database_username =  GetConfigString("database.username", "root");
	public static String database_password =  GetConfigString("database.password", "");
	public static String database_port =  GetConfigString("database.port", "3306");
	public static String database_host =  GetConfigString("database.host", "localhost");
	public static String database_database = GetConfigString("database.database", "minecraft_forum");
	public static String database_prefix = GetConfigString("database.prefix", "");
	public static String dbDb = "jdbc:mysql://"+database_host+":"+database_port+"/"+database_database;
	
	///////////////////////////////////////////
	//               settings
	///////////////////////////////////////////
	
		///////////////////////////////////////////
		//               script
		///////////////////////////////////////////
        public static String script_name = GetConfigString("settings.script.name", "PHPBB3");
        public static boolean script_updatestatus = GetConfigBoolean("settings.script.updatestatus", true);
        
        ///////////////////////////////////////////
		//               register
		///////////////////////////////////////////
        public static boolean register_enabled = GetConfigBoolean("settings.register.enabled", true);
        public static boolean register_force = GetConfigBoolean("settings.register.force", true);
        
        ///////////////////////////////////////////
		//               password
		///////////////////////////////////////////
        public static String password_tries = GetConfigString("settings.password.tries", "3");
        public static boolean password_kick = GetConfigBoolean("settings.password.false", true);
        public static boolean password_ban = GetConfigBoolean("settings.password.ban", false);
        
        ///////////////////////////////////////////
		//               session
		///////////////////////////////////////////
        public static String session_time = GetConfigString("settings.session.time", "minutes");
        public static String session_length = GetConfigString("settings.session.length", "60");
        
        ///////////////////////////////////////////
		//               idle
		///////////////////////////////////////////
        public static String idle_time = GetConfigString("settings.idle.time", "seconds");
        public static String idle_length= GetConfigString("settings.idle.length", "30");
        public static boolean idle_kick= GetConfigBoolean("settings.idle.kick", true);
        public static String idle_whitelist= GetConfigString("settings.idle.whitelist", "");
  
        ///////////////////////////////////////////
		//               guests
		///////////////////////////////////////////
        public static boolean guests_commands = GetConfigBoolean("settings.guests.commands", true);
        public static boolean guests_movement = GetConfigBoolean("settings.guests.movement", true);
        public static boolean guests_inventory = GetConfigBoolean("settings.guests.inventory", true);
        public static boolean guests_drops = GetConfigBoolean("settings.guests.drops", true);
        public static boolean guests_health = GetConfigBoolean("settings.guests.health", true);
        public static boolean guests_damage = GetConfigBoolean("settings.guests.damage", true);
        public static boolean guests_interact = GetConfigBoolean("settings.guests.interact", true);
        public static boolean guests_build = GetConfigBoolean("settings.guests.build", true);
        public static boolean guests_chat= GetConfigBoolean("settings.guests.chat", true);
  
        ///////////////////////////////////////////
		//               badcharacters
		///////////////////////////////////////////
        public static boolean badcharacters_kick = GetConfigBoolean("settings.badcharacters.commands", true);
        public static boolean badcharacters_remove = GetConfigBoolean("settings.badcharacters.movement", false);
        public static String badcharacters_characters = GetConfigString("settings.badcharacters.inventory", "$^@(#)!+\\-/");
        
        ///////////////////////////////////////////
		//               geoip
		///////////////////////////////////////////
        public static boolean geoip_enabled = GetConfigBoolean("settings.geopip.enabled", true);
  
	///////////////////////////////////////////
	//               plugins
	///////////////////////////////////////////
	
		///////////////////////////////////////////
		//               CraftIRC
		///////////////////////////////////////////
		public static boolean CraftIRC_enabled = GetConfigBoolean("plugins.CraftIRC.enabled", true);
		public static String CraftIRC_tag = GetConfigString("plugins.CraftIRC.tag", "admin");
		public static String CraftIRC_prefix = GetConfigString("plugins.CraftIRC.prefix", "%b%%green%[{PLUGIN}]%k%%b%");

		    ///////////////////////////////////////////
			//               messages
			///////////////////////////////////////////
			public static boolean CraftIRC_messages_enabled = GetConfigBoolean("plugins.CraftIRC.messages.enabled", true);
			public static boolean CraftIRC_messages_welcome_enabled = GetConfigBoolean("plugins.CraftIRC.messages.welcome", true);
			public static boolean CraftIRC_messages_register_enabled = GetConfigBoolean("plugins.CraftIRC.messages.register", true);
			public static boolean CraftIRC_messages_unregister_enabled = GetConfigBoolean("plugins.CraftIRC.messages.unregister", true);
			public static boolean CraftIRC_messages_login_enabled = GetConfigBoolean("plugins.CraftIRC.messages.login", true);
			public static boolean CraftIRC_messages_email_enabled = GetConfigBoolean("plugins.CraftIRC.messages.email", true);
			public static boolean CraftIRC_messages_username_enabled = GetConfigBoolean("plugins.CraftIRC.messages.username", true);
			public static boolean CraftIRC_messages_password_enabled = GetConfigBoolean("plugins.CraftIRC.messages.password", true);
			public static boolean CraftIRC_messages_idle_enabled = GetConfigBoolean("plugins.CraftIRC.messages.idle", true);
			
	  public static Configuration template = null;

	  public Config(String directory, String filename) {
		  template = new Configuration(new File(directory, filename));
		  template.load();
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
