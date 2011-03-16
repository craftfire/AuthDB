package com.gmail.contexmoh.authdb.plugins;

import org.bukkit.entity.Player;

import com.gmail.contexmoh.authdb.AuthDB;
import com.gmail.contexmoh.authdb.utils.Config;
import com.gmail.contexmoh.authdb.utils.Messages;
import com.gmail.contexmoh.authdb.utils.Utils;

public class zCraftIRC {  
public static void SendMessage(String type,Player player)
	{
		if(AuthDB.craftircHandle != null && Config.CraftIRC_enabled)
		{
			if(type.equals("CraftIRC_message_status_join")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+Utils.replaceStrings(Messages.CraftIRC_message_status_join,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("CraftIRC_message_status_quit")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+Utils.replaceStrings(Messages.CraftIRC_message_status_quit,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("CraftIRC_message_register_success")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+Utils.replaceStrings(Messages.CraftIRC_message_register_success,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("CraftIRC_message_register_failure")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+Utils.replaceStrings(Messages.CraftIRC_message_register_failure,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("CraftIRC_message_register_registered")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+Utils.replaceStrings(Messages.CraftIRC_message_register_registered,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("CraftIRC_message_username_renamed")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+Utils.replaceStrings(Messages.CraftIRC_message_username_renamed,player,""), Config.CraftIRC_tag);
			}
			else if(type.equals("CraftIRC_message_username_badcharacters"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+Utils.replaceStrings(Messages.CraftIRC_message_username_badcharacters,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("CraftIRC_message_password_success"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+Utils.replaceStrings(Messages.CraftIRC_message_password_success,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("CraftIRC_message_password_failure"))
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+Utils.replaceStrings(Messages.CraftIRC_message_password_failure,player,""),Config.CraftIRC_tag);
			}
			else if(type.equals("connect")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+"%b%"+AuthDB.pluginname+" "+AuthDB.pluginversion+"%b% has started successfully.", Config.CraftIRC_tag);
			}
			else if(type.equals("disconnnect")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(Config.CraftIRC_prefix+" "+"%b%"+AuthDB.pluginname+" "+AuthDB.pluginversion+"%b% has stopped successfully.", Config.CraftIRC_tag);
			}
		}
	}
	
}