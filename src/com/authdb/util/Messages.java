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
		public static String AuthDB_message_welcome_guest;
		
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
		public static String AuthDB_message_login_default,AuthDB_message_login_prompt,AuthDB_message_login_success,AuthDB_message_login_failure,AuthDB_message_login_authorized,AuthDB_message_login_notregistered,AuthDB_message_login_session,AuthDB_message_login_usage;
		
		///////////////////////////////////////////
		//               link
		///////////////////////////////////////////
		public static String AuthDB_message_link_success,AuthDB_message_link_failure,AuthDB_message_link_exists,AuthDB_message_link_usage;
		
		///////////////////////////////////////////
		//               unlink
		///////////////////////////////////////////
		public static String AuthDB_message_unlink_success,AuthDB_message_unlink_failure,AuthDB_message_unlink_nonexist,AuthDB_message_unlink_usage;
		
		///////////////////////////////////////////
		//               email
		///////////////////////////////////////////
		public static String AuthDB_message_email_required,AuthDB_message_email_invalid;
		
		///////////////////////////////////////////
		//               badcharacters
		///////////////////////////////////////////
		public static String AuthDB_message_badcharacters_renamed,AuthDB_message_badcharacters_name,AuthDB_message_badcharacters_password,AuthDB_message_badcharacters_whitelist;
		
		///////////////////////////////////////////
		//               username
		///////////////////////////////////////////
		public static String AuthDB_message_username_minimum,AuthDB_message_username_maximum;
		
		///////////////////////////////////////////
		//               password
		///////////////////////////////////////////
		public static String AuthDB_message_password_minimum,AuthDB_message_password_maximum,AuthDB_message_password_success,AuthDB_message_password_failure,AuthDB_message_password_notregistered,AuthDB_message_password_usage;
		
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
			else if(type.equals("AuthDB_message_login_default")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_login_default,player,null));
			}
			else if(type.equals("AuthDB_message_login_prompt")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_login_prompt,player,null));
			}
			else if(type.equals("AuthDB_message_login_success")) 
			{
				AuthDB.AuthPasswordTriesDB.put(player.getName(), "0");
				player.sendMessage(Util.replaceStrings(AuthDB_message_login_success,player,null));
			}
			else if(type.equals("AuthDB_message_login_failure")) 
			{
				String temp = AuthDB.AuthPasswordTriesDB.get(player.getName());
				int tries = Integer.parseInt(temp) + 1;
  			    if(tries > Integer.parseInt(Config.login_tries) && Config.login_kick == true)
  			    { 
  			    	player.kickPlayer(Util.replaceStrings(AuthDB_message_login_failure,player,null));
  			    	AuthDB.AuthPasswordTriesDB.put(player.getName(),"0");
  			    }
  			    else 
  			    { 
  			    	AuthDB.AuthPasswordTriesDB.put(player.getName(),""+tries);
  			    	player.sendMessage(Util.replaceStrings(AuthDB_message_login_failure,player,null)); 
  			    }
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
			else if(type.equals("AuthDB_message_link_success")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_link_success,player,null));
			}
			else if(type.equals("AuthDB_message_link_failure")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_link_failure,player,null));
			}
			else if(type.equals("AuthDB_message_link_exists")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_link_exists,player,null));
			}
			else if(type.equals("AuthDB_message_link_usage")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_link_usage,player,null));
			}
			else if(type.equals("AuthDB_message_unlink_success")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_unlink_success,player,null));
			}
			else if(type.equals("AuthDB_message_unlink_failure")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_unlink_failure,player,null));
			}
			else if(type.equals("AuthDB_message_unlink_nonexist")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_unlink_nonexist,player,null));
			}
			else if(type.equals("AuthDB_message_unlink_usage")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_unlink_usage,player,null));
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
				//player.setDisplayName(Util.CheckBadCharacters("username",player.getName()));
				player.sendMessage(Util.replaceStrings(AuthDB_message_badcharacters_renamed,player,null));
			}
			else if(type.equals("AuthDB_message_badcharacters_name")) 
			{
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Util.replaceStrings(AuthDB_message_badcharacters_name,player,null));
			}
			else if(type.equals("AuthDB_message_badcharacters_password")) 
			{
				if(Config.badcharacters_kick)
				{
					player.kickPlayer(Util.replaceStrings(AuthDB_message_badcharacters_password,player,null));
				}
				else
				{
					player.sendMessage(Util.replaceStrings(AuthDB_message_badcharacters_password,player,null));
				}
			}
			else if(type.equals("AuthDB_message_badcharacters_whitelist")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_badcharacters_whitelist,player,null));
			}
			else if(type.equals("AuthDB_message_username_minimum")) 
			{
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Util.replaceStrings(AuthDB_message_username_minimum,player,null));
			}
			else if(type.equals("AuthDB_message_username_maximum")) 
			{
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Util.replaceStrings(AuthDB_message_username_maximum,player,null));
			}
			else if(type.equals("AuthDB_message_password_minimum")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_password_minimum,player,null));
			}
			else if(type.equals("AuthDB_message_password_maximum")) 
			{
				player.sendMessage(Util.replaceStrings(AuthDB_message_password_maximum,player,null));
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