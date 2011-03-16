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
		public static String AuthDB_message_welcome_guest,AuthDB_message_welcome_user;
		
		///////////////////////////////////////////
		//               register
		///////////////////////////////////////////
		public static String AuthDB_message_register_success,AuthDB_message_register_failure,AuthDB_message_register_registered,AuthDB_message_register_disabled,AuthDB_message_register_usage;
		
		///////////////////////////////////////////
		//               unregister
		///////////////////////////////////////////
		public static String AuthDB_message_unregister_success,AuthDB_message_unregister_failure,AuthDB_message_unregister_usage;
		
		///////////////////////////////////////////
		//               login
		///////////////////////////////////////////
		public static String AuthDB_message_login_success,AuthDB_message_login_failure,AuthDB_message_login_authorized,AuthDB_message_login_usage;
		
		///////////////////////////////////////////
		//               email
		///////////////////////////////////////////
		public static String AuthDB_message_email_required,AuthDB_message_email_badcharacters;
		
		///////////////////////////////////////////
		//               username
		///////////////////////////////////////////
		public static String AuthDB_message_username_renamed,AuthDB_message_username_badcharacters;
		
		///////////////////////////////////////////
		//               password
		///////////////////////////////////////////
		public static String AuthDB_message_password_success,AuthDB_message_password_failure,AuthDB_message_password_notregistered,AuthDB_message_password_usage;
		
		///////////////////////////////////////////
		//               idle
		///////////////////////////////////////////
		public static String AuthDB_message_idle_kick;
		
	///////////////////////////////////////////
	//               CraftIRC
	///////////////////////////////////////////
		
		///////////////////////////////////////////
		//               status
		///////////////////////////////////////////
		public static String CraftIRC_message_status_join,CraftIRC_message_status_quit;
		
		///////////////////////////////////////////
		//               register
		///////////////////////////////////////////
		public static String CraftIRC_message_register_success,CraftIRC_message_register_failure,CraftIRC_message_register_registered;
		
		///////////////////////////////////////////
		//               username
		///////////////////////////////////////////
		public static String CraftIRC_message_username_renamed,CraftIRC_message_username_badcharacters;
		
		///////////////////////////////////////////
		//               password
		///////////////////////////////////////////
		public static String CraftIRC_message_password_success,CraftIRC_message_password_failure;
		
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