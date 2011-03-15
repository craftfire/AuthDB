package com.gmail.contexmoh.authdb.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

import com.gmail.contexmoh.authdb.AuthDB;
import com.gmail.contexmoh.authdb.plugins.zCraftIRC;



public class Messages
{
	static String loginMessage = AuthDB.Config.getString("messages.core.login-message", "Please login with command /login <password>");
	static String authorizedMessage = AuthDB.Config.getString("messages.core.authorized-message", ChatColor.DARK_RED + "You are already authorized!");
	static String loginUsageMessage = AuthDB.Config.getString("messages.core.login-usage-message", ChatColor.DARK_RED + "Correct usage is: /login <password>");
	static String passwordAcceptedMessage = AuthDB.Config.getString("messages.core.password-accepted-message", ChatColor.GREEN + "Password accepted. Welcome!");
	static String badPasswordMessage = AuthDB.Config.getString("messages.core.bad-password-message", "Bad password!");
	static String alreadyRegisteredMessage = AuthDB.Config.getString("messages.core.already-registered-message", ChatColor.GREEN +"You are already registered!");
	static String registrationNotAllowedMessage = AuthDB.Config.getString("messages.core.registration-not-allowed-message", ChatColor.DARK_RED + "Registration not allowed!");
	static String registerMessage = AuthDB.Config.getString("messages.core.register-message",ChatColor.RED + "To play, please register with /register <password> <email>");
	static String registeredMessage = AuthDB.Config.getString("messages.core.registered-message", ChatColor.GREEN +"You are already registered!");
	static String registerUsageMessage = AuthDB.Config.getString("messages.core.register-usage-message", ChatColor.DARK_RED + "Registration not allowed!");
	static String registerErrorMessage = AuthDB.Config.getString("messages.core.register-error-message", ChatColor.GREEN + "You have been registered!");
	static String passwordNotRegisteredMessage = AuthDB.Config.getString("messages.core.password-not-registered-message", ChatColor.DARK_RED + "Register first!");
	static String emailRequiredMessage = AuthDB.Config.getString("messages.core.email-required-message", ChatColor.RED + "Email required for registration!");
	static String emailUnexpectedMessage = AuthDB.Config.getString("messages.core.email-unexpected-message", ChatColor.RED + "Email contains unexpected letters!");
	static String checkUsernameCharactersMessage = AuthDB.Config.getString("messages.core.username-unexpected-message", "Username contains unexpected letters!");
	static String changeUsernameMessage = AuthDB.Config.getString("messages.core.username-change", ChatColor.GREEN +"{PLAYER} renamed to {PLAYERNEW} due to illegal characters.");
	public static void SendMessage(String type,Player player,PlayerLoginEvent event){
		zCraftIRC.SendMessage(type,player);
		if(type.equals("loginMessage")) 
		{
			player.sendMessage(loginMessage);
		}
		else if(type.equals("authorizedMessage")) 
		{
			player.sendMessage(authorizedMessage);
		}
		else if(type.equals("loginUsageMessage")) 
		{
			player.sendMessage(loginUsageMessage);
		}
		else if(type.equals("passwordAcceptedMessage")) 
		{
			player.sendMessage(passwordAcceptedMessage);
		}
		else if(type.equals("badPasswordMessage")) 
		{
			player.sendMessage(badPasswordMessage);
		}
		else if(type.equals("alreadyRegisteredMessage")) 
		{
			player.sendMessage(alreadyRegisteredMessage);
		}
		else if(type.equals("registrationNotAllowedMessage")) 
		{
			player.sendMessage(registrationNotAllowedMessage);
		}
		else if(type.equals("registerMessage")) 
		{
			player.sendMessage(registerMessage);
		}
		else if(type.equals("registeredMessage")) 
		{
			player.sendMessage(registeredMessage);
		}
		else if(type.equals("registerErrorMessage")) 
		{
			player.sendMessage(registerErrorMessage);
		}
		else if(type.equals("passwordNotRegisteredMessage")) 
		{
			player.sendMessage(passwordNotRegisteredMessage);
		}
		else if(type.equals("emailRequiredMessage")) 
		{
			player.sendMessage(emailRequiredMessage);
		}
		else if(type.equals("emailUnexpectedMessage")) 
		{
			player.sendMessage(emailUnexpectedMessage);
		}
		else if(type.equals("checkUsernameCharactersMessage")) 
		{
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, checkUsernameCharactersMessage);
		}
		else if(type.equals("changeUsernameMessage")) 
		{
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, checkUsernameCharactersMessage);
		}
	}
}