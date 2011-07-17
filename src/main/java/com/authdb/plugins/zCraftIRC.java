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
import com.authdb.util.Messages.Message;
import com.authdb.util.Util;

public class zCraftIRC {
public static void SendMessage(Message type,Player player) {
        if(AuthDB.craftircHandle != null && Config.CraftIRC_enabled == true) {
            if(type.equals(Message.database_failure)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.AuthDB_message_database_failure,player,""), Config.CraftIRC_tag);
            }
            if(type.equals(Message.welcome_guest) || type.equals(Message.login_prompt) || type.equals(Message.login_normal)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_status_join,player,""), Config.CraftIRC_tag);
            }
            else if(type.equals(Message.left_server)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_status_quit,player,""), Config.CraftIRC_tag);
            }
            else if(type.equals(Message.register_success)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_register_success,player,""), Config.CraftIRC_tag);
            }
            else if(type.equals(Message.register_failure)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_register_failure,player,""), Config.CraftIRC_tag);
            }
            else if(type.equals(Message.register_registered)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_register_registered,player,""), Config.CraftIRC_tag);
            }
            else if(type.equals(Message.password_success)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_password_success,player,""),Config.CraftIRC_tag);
            }
            else if(type.equals(Message.password_failure)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_password_failure,player,""),Config.CraftIRC_tag);
            }
            else if(type.equals(Message.idle_kick)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_idle_kicked,player,""),Config.CraftIRC_tag);
            }
            else if(type.equals(Message.idle_whitelist)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_idle_whitelist,player,""),Config.CraftIRC_tag);
            }
            else if(type.equals(Message.filter_renamed)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_filter_renamed,player,""), Config.CraftIRC_tag);
            }
            else if(type.equals(Message.filter_username)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_filter_kicked,player,""),Config.CraftIRC_tag);
            }
            else if(type.equals(Message.filter_whitelist)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+Util.replaceStrings(Messages.CraftIRC_message_filter_whitelist,player,""),Config.CraftIRC_tag);
            }
            else if(type.equals(Message.OnEnable)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+"%b%"+AuthDB.PluginName+" "+AuthDB.PluginVersion+"%b% has started successfully.", Config.CraftIRC_tag);
            }
            else if(type.equals(Message.OnDisable)) {
                AuthDB.craftircHandle.sendMessageToTag(Util.replaceStrings(Config.CraftIRC_prefix,null,"")+" "+"%b%"+AuthDB.PluginName+" "+AuthDB.PluginVersion+"%b% has stopped successfully.", Config.CraftIRC_tag);
            }
        }
    }

}
