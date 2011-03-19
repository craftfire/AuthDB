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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

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
		public static String AuthDB_message_login_success,AuthDB_message_login_failure,AuthDB_message_login_authorized,AuthDB_message_login_session,AuthDB_message_login_usage;
		
		///////////////////////////////////////////
		//               email
		///////////////////////////////////////////
		public static String AuthDB_message_email_required,AuthDB_message_email_badcharacters;
		
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
		public static String CraftIRC_message_badcharacters_renamed,CraftIRC_message_badcharacters_kicked,CraftIRC_message_badcharacters_whitelist;
		
		///////////////////////////////////////////
		//               password
		///////////////////////////////////////////
		public static String CraftIRC_message_password_success,CraftIRC_message_password_failure;
		
	public static void SendMessage(String type,Player player,PlayerLoginEvent event)
	{
		zCraftIRC.SendMessage(type,player);
		if(type.equals("AuthDB_message_welcome_guest")) 
		{
			player.sendMessage(Util.replaceStrings(AuthDB_message_welcome_guest,player,null));
		}
		else if(type.equals("AuthDB_message_welcome_user")) 
		{
			player.sendMessage(Util.replaceStrings(AuthDB_message_welcome_user,player,null));
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
		else if(type.equals("AuthDB_message_email_badcharacters")) 
		{
			player.sendMessage(Util.replaceStrings(AuthDB_message_email_badcharacters,player,null));
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
			//player.sendMessage(Util.replaceStrings(AuthDB_message_badcharacters_whitelist,player,null));
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
	}
}