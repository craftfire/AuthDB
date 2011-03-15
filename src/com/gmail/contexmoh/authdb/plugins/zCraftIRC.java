package com.gmail.contexmoh.authdb.plugins;

import org.bukkit.entity.Player;

import com.gmail.contexmoh.authdb.AuthDB;
import com.gmail.contexmoh.authdb.utils.Utils;




public class zCraftIRC {  
	public static boolean useCraftIRC = AuthDB.Config.getBoolean("plugins.CraftIRC.use", false);
	public static boolean CraftIRCpost = AuthDB.Config.getBoolean("plugins.CraftIRC.post-messages", true);
	public static String CraftIRCtag = AuthDB.Config.getString("plugins.CraftIRC.tag", "admin");
	public static String CraftIRCprefix = AuthDB.Config.getString("plugins.CraftIRC.prefix", "%b%%green%[AuthDB]%k%%b%");

	public static String CraftIRCpasswordAcceptedMessage = AuthDB.Config.getString("messages.CraftIRC.password-accepted-message","{PLAYER} logged in sucessfully!");
	public static String CraftIRCalreadyRegisteredMessage = AuthDB.Config.getString("messages.CraftIRC.already-registered-message","{PLAYER} tried to login when he/she was already registred!");
	public static String CraftIRCregisteredMessage = AuthDB.Config.getString("messages.CraftIRC.registered-message", "{PLAYER} tried to login when he/she was already registred!");
	public static String CraftIRCregisterErrorMessage = AuthDB.Config.getString("messages.CraftIRC.register-error-message","{PLAYER} has registered a new user!");
	public static String CraftIRCbadPasswordMessage = AuthDB.Config.getString("messages.CraftIRC.bad-password-message","{PLAYER} typed the wrong password for his/her username!");
	public static String CraftIRCjoinMessage = AuthDB.Config.getString("messages.CraftIRC.join-message","{PLAYER} has joined the server with the IP {IP}");
	public static String CraftIRCcheckUsernameCharactersMessage =  AuthDB.Config.getString("messages.CraftIRC.name-unexpected-message","{PLAYER} was kicked due to unexpected characters in his name!");
	public static String CraftIRCchangeUsernameMessage =  AuthDB.Config.getString("messages.CraftIRC.username-change","{PLAYER} was kicked due to unexpected characters in his name!");
	public static void SendMessage(String type,Player player)
	{
		if(AuthDB.craftircHandle != null && useCraftIRC && CraftIRCpost)
		{
			if(type.equals("passwordAcceptedMessage")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+Utils.replaceStrings(CraftIRCpasswordAcceptedMessage,player,""), CraftIRCtag);
			}
			else if(type.equals("alreadyRegisteredMessage")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+Utils.replaceStrings(CraftIRCalreadyRegisteredMessage,player,""), CraftIRCtag);
			}
			else if(type.equals("registeredMessage")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+Utils.replaceStrings(CraftIRCregisteredMessage,player,""), CraftIRCtag);
			}
			else if(type.equals("registerErrorMessage")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+Utils.replaceStrings(CraftIRCregisterErrorMessage,player,""), CraftIRCtag);
			}
			else if(type.equals("badPasswordMessage")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+Utils.replaceStrings(CraftIRCbadPasswordMessage,player,""), CraftIRCtag);
			}
			else if(type.equals("checkUsernameCharactersMessage")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+Utils.replaceStrings(CraftIRCcheckUsernameCharactersMessage,player,""), CraftIRCtag);
			}
			else if(type.equals("joinMessage"))
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+Utils.replaceStrings(CraftIRCjoinMessage,player,""),CraftIRCtag);
			}
			else if(type.equals("changeUsernameMessage"))
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+Utils.replaceStrings(CraftIRCchangeUsernameMessage,player,""),CraftIRCtag);
			}
			else if(type.equals("connect")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+"%b%"+AuthDB.pluginname+" "+AuthDB.pluginversion+"%b% has started successfully.", CraftIRCtag);
			}
			else if(type.equals("disconnnect")) 
			{
				AuthDB.craftircHandle.sendMessageToTag(CraftIRCprefix+" "+"%b%"+AuthDB.pluginname+" "+AuthDB.pluginversion+"%b% has stopped successfully.", CraftIRCtag);
			}
		}
	}
	
}