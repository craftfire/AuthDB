/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

**/
package com.authdb.util;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerEvent;

import com.authdb.AuthDB;
import com.authdb.plugins.zCraftIRC;

public class Messages
{
///////////////////////////////////////////
//  messages
///////////////////////////////////////////	

	///////////////////////////////////////////
	//               AuthDB
	///////////////////////////////////////////
		///////////////////////////////////////////
		//               database
		///////////////////////////////////////////
		public static String AuthDB_message_database_failure;
		
		///////////////////////////////////////////
		//               welcome
		///////////////////////////////////////////
		public static String AuthDB_message_welcome_guest,AuthDB_message_welcome_user;
		
		///////////////////////////////////////////
		//               guest
		///////////////////////////////////////////
		public static String AuthDB_message_guest_notauthorized;
		
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
		public static String AuthDB_message_login_success,AuthDB_message_login_failure,AuthDB_message_login_authorized,AuthDB_message_login_notregistered,AuthDB_message_login_session,AuthDB_message_login_usage;
		
		///////////////////////////////////////////
		//               email
		///////////////////////////////////////////
		public static String AuthDB_message_email_required,AuthDB_message_email_invalid;
		
		///////////////////////////////////////////
		//               username
		///////////////////////////////////////////
		public static String AuthDB_message_badcharacters_renamed,AuthDB_message_badcharacters_kicked,AuthDB_message_badcharacters_whitelist;
		
		///////////////////////////////////////////
		//               password
		///////////////////////////////////////////
		public static String AuthDB_message_password_success,AuthDB_message_password_failure,AuthDB_message_password_notregistered,AuthDB_message_password_usage;
		
		///////////////////////////////////////////
		//               idle
		///////////////////////////////////////////
		public static String AuthDB_message_idle_kick,AuthDB_message_idle_whitelist;
		
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
		//               password
		///////////////////////////////////////////
		public static String CraftIRC_message_password_success,CraftIRC_message_password_failure;
		
		///////////////////////////////////////////
		//               idle
		///////////////////////////////////////////
		public static String CraftIRC_message_idle_kicked,CraftIRC_message_idle_whitelist;
		
		///////////////////////////////////////////
		//               badcharacters
		///////////////////////////////////////////
		public static String CraftIRC_message_badcharacters_renamed,CraftIRC_message_badcharacters_kicked,CraftIRC_message_badcharacters_whitelist;
	
		
	public static void SendMessage(String type,Player player,PlayerLoginEvent event)
	{
		zCraftIRC.SendMessage(type,player);
		if(type.equals("AuthDB_message_database_failure")) 
		{
			AuthDB.Server.broadcastMessage(Util.replaceStrings(AuthDB_message_database_failure,null,null));
		}
		else if(Config.database_ison)
		{
			if(type.equals("AuthDB_message_welcome_guest")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_welcome_guest,player,null));
			}
			else if(type.equals("AuthDB_message_welcome_user")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_welcome_user,player,null));
			}
			else if(type.equals("AuthDB_message_guest_notauthorized")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_guest_notauthorized,player,null));
			}
			else if(type.equals("AuthDB_message_register_success")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_register_success,player,null));
			}
			else if(type.equals("AuthDB_message_register_failure")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_register_failure,player,null));
			}
			else if(type.equals("AuthDB_message_register_registered")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_register_registered,player,null));
			}
			else if(type.equals("AuthDB_message_register_disabled")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_register_disabled,player,null));
			}
			else if(type.equals("AuthDB_message_register_usage")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_register_usage,player,null));
			}
			else if(type.equals("AuthDB_message_unregister_success")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_unregister_success,player,null));
			}
			else if(type.equals("AuthDB_message_unregister_failure")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_unregister_failure,player,null));
			}
			else if(type.equals("AuthDB_message_unregister_usage")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_unregister_usage,player,null));
			}
			else if(type.equals("AuthDB_message_login_success")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_login_success,player,null));
			}
			else if(type.equals("AuthDB_message_login_failure")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_login_failure,player,null));
			}
			else if(type.equals("AuthDB_message_login_authorized")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_login_authorized,player,null));
			}
			else if(type.equals("AuthDB_message_login_notregistered")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_login_notregistered,player,null));
			}
			else if(type.equals("AuthDB_message_login_session")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_login_session,player,null));
			}
			else if(type.equals("AuthDB_message_login_usage")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_login_usage,player,null));
			}
			else if(type.equals("AuthDB_message_email_required")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_email_required,player,null));
			}
			else if(type.equals("AuthDB_message_email_invalid")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_email_invalid,player,null));
			}
			else if(type.equals("AuthDB_message_badcharacters_renamed")) 
			{
				player.setDisplayName(Util.ChangeUsernameCharacters(player.getName()));
				player.sendMessage(Util.replaceStrings(AuthDB_message_badcharacters_renamed,player,null));
			}
			else if(type.equals("AuthDB_message_badcharacters_kicked")) 
			{
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Util.replaceStrings(AuthDB_message_badcharacters_kicked,player,null));
			}
			else if(type.equals("AuthDB_message_badcharacters_whitelist")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_badcharacters_whitelist,player,null));
			}
			else if(type.equals("AuthDB_message_password_success")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_password_success,player,null));
			}
			else if(type.equals("AuthDB_message_password_failure")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_password_failure,player,null));
			}
			else if(type.equals("AuthDB_message_password_notregistered")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_password_notregistered,player,null));
			}
			else if(type.equals("AuthDB_message_password_usage")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_password_usage,player,null));
			}
			else if(type.equals("AuthDB_message_idle_kick"))
			{
				player.kickPlayer(Util.replaceStrings(AuthDB_message_idle_kick,player,null));
			}
			else if(type.equals("AuthDB_message_idle_whitelist"))
			{
				//player.sendMessage(Util.replaceStrings(AuthDB_message_idle_whitelist,player,null));
			}
		}
		else { Messages.SendMessage("AuthDB_message_database_failure", null, null); }
	}
}