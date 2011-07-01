/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
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
			if(type.equals("AuthDB_message_welcome_guest") || type.equals("AuthDB_message_login_prompt") || type.equals("AuthDB_message_login_default")) 
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
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_filter_renamed,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_badcharacters_kicked"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_filter_kicked,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("AuthDB_message_badcharacters_whitelist"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_filter_whitelist,player,""),Config.CraftIRC_tag);
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