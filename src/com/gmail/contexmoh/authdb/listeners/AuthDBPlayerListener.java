package com.gmail.contexmoh.authdb.listeners;

import java.awt.Color;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.contexmoh.authdb.AuthDB;
import com.gmail.contexmoh.authdb.utils.Messages;
import com.gmail.contexmoh.authdb.utils.Utils;

public class AuthDBPlayerListener extends PlayerListener
{
  private final AuthDB plugin;
  int seconds = 5;
  Timer IdleTimer;

  public AuthDBPlayerListener(AuthDB instance)
  {
   this.plugin = instance;
  }

public void onPlayerLogin(PlayerLoginEvent event)
{
	if(Utils.specialCharactersKick || Utils.specialCharactersChange)
	{
		Player player = event.getPlayer();
		String name = player.getName();
		if (!Utils.checkUsernameCharacters(name))
	    {
	      if(Utils.specialCharactersKick) Messages.SendMessage("checkUsernameCharactersMessage", player, event);
	      //else if(Utils.specialCharactersChange) Messages.SendMessage("changeUsernameMessage", player, event);
	    }
	}
}


	  /*
	Player player = event.getPlayer();
	if (!this.plugin.isAuthorized(player.getEntityId()))
	{
		 Messages.SendMessage("kickPlayerIdleLoginMessage", player, null);
	}
	IdleTimer.cancel();
	return null;
	*/
public TimerTask CheckIdle(Player player)
{
	if (!this.plugin.isAuthorized(player.getEntityId()))
	{
		 Messages.SendMessage("kickPlayerIdleLoginMessage", player, null);
	}
	IdleTimer.cancel();
	return null;
}

  public void onPlayerJoin(PlayerEvent event)
  {
				Player player = event.getPlayer();
    try {
    	IdleTimer = new Timer(player.getName());
    	IdleTimer.schedule(CheckIdle(player), seconds);
   if (this.plugin.isRegistered(player.getName())) {
        this.plugin.storeInventory(player.getName(), player.getInventory().getContents());
         player.getInventory().clear();
       Messages.SendMessage("loginMessage", player,null);
     } else if (Utils.forceRegister) {
        this.plugin.storeInventory(player.getName(), player.getInventory().getContents());
       player.getInventory().clear();
					Messages.SendMessage("registerMessage", player,null);
      } else {
        this.plugin.authorize(event.getPlayer().getEntityId());
      }
    } catch (IOException e) {
      Utils.Log("severe","["+AuthDB.pluginname+"] Inventory file error:");
      player.kickPlayer("inventory protection kicked");
       e.printStackTrace();
    player.sendMessage(Color.red + "Error happend, report to admins!");
    }
  }

  public void onPlayerQuit(PlayerEvent event)
  {
     Player player = event.getPlayer();
   ItemStack[] inv = this.plugin.getInventory(player.getName());
    if ((inv != null) && (!this.plugin.isAuthorized(player.getEntityId()))) {
     player.getInventory().setContents(inv);
     player.kickPlayer("inventory protection kicked");
    }
    this.plugin.unauthorize(player.getEntityId());
  }

  public void onPlayerCommandPreprocess(PlayerChatEvent event)
  {
    String[] split = event.getMessage().split(" ");
			//int derp = split[0].compareTo("login");
              Player player = event.getPlayer();
    if (split[0].equals("/login")) {
      if (this.plugin.isAuthorized(player.getEntityId())) {			  
				  Messages.SendMessage("authorizedMessage", player,null);
      }
      else if (split.length < 2) {
				  Messages.SendMessage("loginUsageMessage", player,null);
      }
      else if (this.plugin.checkPassword(player.getName(), split[1])) {
         ItemStack[] inv = this.plugin.getInventory(player.getName());
        if (inv != null)
         player.getInventory().setContents(inv);
        this.plugin.authorize(player.getEntityId());
			      Messages.SendMessage("passwordAcceptedMessage", player,null);
				 } else if (Utils.kickOnBadPassword) {
       ItemStack[] inv = this.plugin.getInventory(player.getName());
      if (inv != null)
          player.getInventory().setContents(inv);
				  Messages.SendMessage("badPasswordMessage", player,null);
      } else {
				  Messages.SendMessage("badPasswordMessage", player,null);
      }
     event.setMessage("/login ******");
      event.setCancelled(true);
     }
				else if (split[0].equals("/register")) {
      if (split.length < 2) {
				  Messages.SendMessage("registerUsageMessage", player,null);
      }
      else if (this.plugin.isRegistered(player.getName()))
				  Messages.SendMessage("alreadyRegisteredMessage", player,null);
      else if (!Utils.allowRegister)
				  Messages.SendMessage("registrationNotAllowedMessage", player,null);
      else if (split.length < 3)
				  Messages.SendMessage("emailRequiredMessage", player,null);
       else if ((split.length >= 3) && (!this.plugin.checkEmail(split[2])))
				  Messages.SendMessage("emailUnexpectedMessage", player,null);
      else {
        try {
           if (split.length >= 3)
             this.plugin.register(player.getName(), split[1], split[2],Utils.GetIP(player));
          else
            this.plugin.register(player.getName(), split[1],Utils.GetIP(player));
          ItemStack[] inv = this.plugin.getInventory(player.getName());
         if (inv != null)
            player.getInventory().setContents(inv);
					Messages.SendMessage("registeredMessage", player,null);
          this.plugin.authorize(player.getEntityId());
        } catch (IOException e) {
					Messages.SendMessage("registerErrorMessage", player,null);
          e.printStackTrace();
        } catch (SQLException e) {
					Messages.SendMessage("registerErrorMessage", player,null);
          e.printStackTrace();
        }
      }
      event.setMessage("/register *****");
       event.setCancelled(true);
     } else if (!this.plugin.isAuthorized(player.getEntityId())) {
      event.setCancelled(true);
     event.setMessage("");
    }
  }

  public void onPlayerMove(PlayerMoveEvent event)
  {
    if (!this.plugin.isAuthorized(event.getPlayer().getEntityId())) {
      event.setCancelled(true);
     event.getPlayer().teleportTo(event.getFrom());
    }
  }

  public void onPlayerChat(PlayerChatEvent event)
  {
    if (!this.plugin.isAuthorized(event.getPlayer().getEntityId()))
       event.setCancelled(true);
  }

  public void onPlayerItem(PlayerItemEvent event)
  {
    if (!this.plugin.isAuthorized(event.getPlayer().getEntityId()))
      event.setCancelled(true);
  }
}