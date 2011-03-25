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

package com.authdb.plugins;

import org.bukkit.entity.Player;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Util;

public class zCraftIRC {  
public static void SendMessage(String type,Player player)
	{
		if(AuthDB.craftircHandle != null && Config.CraftIRC_enabled == true)
		{
			if(type.equals("AuthDB_message_database_failure")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.AuthDB_message_database_failure,player,""), Config.CraftIRC_tag);
			}
			if(type.equals("AuthDB_message_welcome_guest") || type.equals("AuthDB_message_welcome_user")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_status_join,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_left_server")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_status_quit,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_register_success")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_register_success,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_register_failure")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_register_failure,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_register_registered")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_register_registered,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_password_success"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_password_success,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_password_failure"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_password_failure,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_idle_kick"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_idle_kicked,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_idle_whitelist"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_idle_whitelist,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_badcharacters_renamed")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_badcharacters_renamed,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_badcharacters_kicked"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_badcharacters_kicked,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_badcharacters_whitelist"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_badcharacters_whitelist,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("connect")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+"%b%"+AuthDB.pluginname+" "+AuthDB.pluginversion+"%b% has started successfully.", Config.CraftIRC_tag);
			}
			else if(type.equals("disconnnect")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+"%b%"+AuthDB.pluginname+" "+AuthDB.pluginversion+"%b% has stopped successfully.", Config.CraftIRC_tag);
			}
		}
	}
	
}