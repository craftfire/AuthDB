package com.gmail.contexmoh.authdb.utils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import com.gmail.contexmoh.authdb.plugins.zCraftIRC;

public class Messages
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
		public static String AuthDB_message_welcome_guest = Config.GetConfigString("messages.AuthDB.welcome.guest", "&4Welcome guest! Please register with /register <password> <email>");
		public static String AuthDB_message_welcome_user = Config.GetConfigString("messages.AuthDB.welcome.user", "&4Welcome back {PLAYER}! Please login with /login <password>");
		
		///////////////////////////////////////////
		//               register
		///////////////////////////////////////////
		public static String AuthDB_message_register_success = Config.GetConfigString("messages.AuthDB.register.success", "&4You have been registered!");
		public static String AuthDB_message_register_failure = Config.GetConfigString("messages.AuthDB.register.failure", "&4Error while registering!");
		public static String AuthDB_message_register_registered = Config.GetConfigString("messages.AuthDB.register.registered", "&4You are already registered!");
		public static String AuthDB_message_register_disabled = Config.GetConfigString("messages.AuthDB.register.disabled", "&4Registration not allowed!");
		public static String AuthDB_message_register_usage = Config.GetConfigString("messages.AuthDB.register.usage", "&4Correct usage is: /register <password> <email>");
		
		///////////////////////////////////////////
		//               unregister
		///////////////////////////////////////////
		public static String AuthDB_message_unregister_success = Config.GetConfigString("messages.AuthDB.unregister.success", "&2Unregistered successfully!");
		public static String AuthDB_message_unregister_failure = Config.GetConfigString("messages.AuthDB.unregister.failure", "&4An error occurred while unregistering!");
		public static String AuthDB_message_unregister_usage = Config.GetConfigString("messages.AuthDB.unregister.usage", "&4Correct usage is: /unregister <password>");
		
		///////////////////////////////////////////
		//               login
		///////////////////////////////////////////
		public static String AuthDB_message_login_success = Config.GetConfigString("messages.AuthDB.login.success", "&2Password accepted. Welcome!");
		public static String AuthDB_message_login_failure = Config.GetConfigString("messages.AuthDB.login.failure", "&4Password incorrect, please try again.");
		public static String AuthDB_message_login_authorized = Config.GetConfigString("messages.AuthDB.login.authorized", "&2Hey, I remember you! You are logged in.");
		public static String AuthDB_message_login_usage = Config.GetConfigString("messages.AuthDB.login.usage", "&4Correct usage is: /login <password>");
		
		///////////////////////////////////////////
		//               email
		///////////////////////////////////////////
		public static String AuthDB_message_email_required = Config.GetConfigString("messages.AuthDB.email.required", "&4Email required for registration!");
		public static String AuthDB_message_email_badcharacters = Config.GetConfigString("messages.AuthDB.email.badcharacters", "&4Email contains bad characters: {BADCHARACTERS}!");
		
		///////////////////////////////////////////
		//               username
		///////////////////////////////////////////
		public static String AuthDB_message_username_renamed = Config.GetConfigString("messages.AuthDB.username.renamed", "&2{PLAYER} renamed to {PLAYERNEW} due to bad characters: {BADCHARACTERS}.");
		public static String AuthDB_message_username_badcharacters = Config.GetConfigString("messages.AuthDB.username.badcharacters", "&4Username contains bad characters: {BADCHARACTERS}!");
		
		///////////////////////////////////////////
		//               password
		///////////////////////////////////////////
		public static String AuthDB_message_password_success = Config.GetConfigString("messages.AuthDB.password.success", "&2Password changed successfully!");
		public static String AuthDB_message_password_failure = Config.GetConfigString("messages.AuthDB.password.failure", "&4Error! Password change failed!");
		public static String AuthDB_message_password_notregistered = Config.GetConfigString("messages.AuthDB.password.notregistered", "&4Register first!");
		public static String AuthDB_message_password_usage = Config.GetConfigString("messages.AuthDB.password.usage", "&4Correct usage is: /password <oldpassword> <password>");
		
		///////////////////////////////////////////
		//               idle
		///////////////////////////////////////////
		public static String AuthDB_message_idle_kick = Config.GetConfigString("messages.AuthDB.idle.kick", "Kicked because you failed to login within {IDLELENGTH} {IDLETIME}.");
		
	///////////////////////////////////////////
	//               CraftIRC
	///////////////////////////////////////////
		
		///////////////////////////////////////////
		//               status
		///////////////////////////////////////////
		public static String CraftIRC_message_status_join = Config.GetConfigString("messages.CraftIRC.status.guest", "{PLAYER} has joined the server from {PROVINCE}{STATE}, {COUNTRY} ({IP}).");
		public static String CraftIRC_message_status_quit = Config.GetConfigString("messages.CraftIRC.status.user", "{PLAYER} has quit the server.");
		
		///////////////////////////////////////////
		//               register
		///////////////////////////////////////////
		public static String CraftIRC_message_register_success = Config.GetConfigString("messages.CraftIRC.register.success", "{PLAYER} just registered successfully!");
		public static String CraftIRC_message_register_failure = Config.GetConfigString("messages.CraftIRC.register.failure", "{PLAYER} had some errors while registering!");
		public static String CraftIRC_message_register_registered = Config.GetConfigString("messages.CraftIRC.register.registered", "{PLAYER} had a lapse in memory and tried to register again.");
		
		///////////////////////////////////////////
		//               username
		///////////////////////////////////////////
		public static String CraftIRC_message_username_renamed = Config.GetConfigString("messages.CraftIRC.username.renamed", "{PLAYER} renamed to {PLAYERNEW} due to bad characters.");
		public static String CraftIRC_message_username_badcharacters = Config.GetConfigString("messages.CraftIRC.username.badcharacters", "{PLAYER} was kicked due to bad characters in username!");
		
		///////////////////////////////////////////
		//               password
		///////////////////////////////////////////
		public static String CraftIRC_message_password_success = Config.GetConfigString("messages.CraftIRC.password.success", "{PLAYER} logged in successfully!");
		public static String CraftIRC_message_password_failure = Config.GetConfigString("messages.CraftIRC.password.failure", "{PLAYER} tried to login with the wrong password!");
		
		
	public static void SendMessage(String type,Player player,PlayerLoginEvent event){
		zCraftIRC.SendMessage(type,player);
		if(type.equals("AuthDB_message_welcome_guest")) 
		{
			player.sendMessage(AuthDB_message_welcome_guest);
		}
		else if(type.equals("AuthDB_message_welcome_user")) 
		{
			player.sendMessage(AuthDB_message_welcome_user);
		}
		else if(type.equals("AuthDB_message_register_success")) 
		{
			player.sendMessage(AuthDB_message_register_success);
		}
		else if(type.equals("AuthDB_message_register_failure")) 
		{
			player.sendMessage(AuthDB_message_register_failure);
		}
		else if(type.equals("AuthDB_message_register_registered")) 
		{
			player.sendMessage(AuthDB_message_register_registered);
		}
		else if(type.equals("AuthDB_message_register_disabled")) 
		{
			player.sendMessage(AuthDB_message_register_disabled);
		}
		else if(type.equals("AuthDB_message_register_usage")) 
		{
			player.sendMessage(AuthDB_message_register_usage);
		}
		else if(type.equals("AuthDB_message_unregister_success")) 
		{
			player.sendMessage(AuthDB_message_unregister_success);
		}
		else if(type.equals("AuthDB_message_unregister_failure")) 
		{
			player.sendMessage(AuthDB_message_unregister_failure);
		}
		else if(type.equals("AuthDB_message_unregister_usage")) 
		{
			player.sendMessage(AuthDB_message_unregister_usage);
		}
		else if(type.equals("AuthDB_message_login_success")) 
		{
			player.sendMessage(AuthDB_message_login_success);
		}
		else if(type.equals("AuthDB_message_login_failure")) 
		{
			player.sendMessage(AuthDB_message_login_failure);
		}
		else if(type.equals("AuthDB_message_login_authorized")) 
		{
			player.sendMessage(AuthDB_message_login_authorized);
		}
		else if(type.equals("AuthDB_message_login_usage")) 
		{
			player.sendMessage(AuthDB_message_login_usage);
		}
		else if(type.equals("AuthDB_message_email_required")) 
		{
			player.sendMessage(AuthDB_message_email_required);
		}
		else if(type.equals("AuthDB_message_email_badcharacters")) 
		{
			player.sendMessage(AuthDB_message_email_badcharacters);
		}
		else if(type.equals("AuthDB_message_username_renamed")) 
		{
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, AuthDB_message_username_renamed);
		}
				else if(type.equals("AuthDB_message_username_badcharacters")) 
		{
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, AuthDB_message_username_badcharacters);
		}
		else if(type.equals("AuthDB_message_password_success")) 
		{
			player.sendMessage(AuthDB_message_password_success);
		}
		else if(type.equals("AuthDB_message_password_failure")) 
		{
			player.sendMessage(AuthDB_message_password_failure);
		}
		else if(type.equals("AuthDB_message_password_notregistered")) 
		{
			player.sendMessage(AuthDB_message_password_notregistered);
		}
		else if(type.equals("AuthDB_message_password_usage")) 
		{
			player.sendMessage(AuthDB_message_password_usage);
		}
		else if(type.equals("AuthDB_message_idle_kick"))
		{
			player.kickPlayer("AuthDB_message_idle_kick");
		}
	}
}