/**
 * Copyright (C) 2011 Contex <contexmoh@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.gmail.contexmoh.authdb.listeners;

import java.awt.Color;
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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.contexmoh.authdb.AuthDB;
import com.gmail.contexmoh.authdb.utils.Config;
import com.gmail.contexmoh.authdb.utils.Messages;
import com.gmail.contexmoh.authdb.utils.Utils;

public class AuthDBPlayerListener extends PlayerListener
{
  private final AuthDB plugin;

  public AuthDBPlayerListener(AuthDB instance)
  {
   this.plugin = instance;
  }

public void onPlayerLogin(PlayerLoginEvent event)
{
	if(Config.badcharacters_kick || Config.badcharacters_remove)
	{
		if(Config.debug_enable) Utils.Debug("Kick on badcharacters: "+Config.badcharacters_kick+" | Remove bad characters: "+Config.badcharacters_remove);
		Player player = event.getPlayer();
		String name = player.getName();
		if (Utils.checkUsernameCharacters(name) == false && Utils.CheckWhitelist(name) == false)
	    {
		  if(Config.debug_enable) Utils.Debug("The player is not in the whitelist and has bad characters in his/her name");
	      if(Config.badcharacters_kick) Messages.SendMessage("AuthDB_message_badcharacters_kicked", player, event);
	      else if(Config.badcharacters_remove) Messages.SendMessage("AuthDB_message_badcharacters_renamed", player, event);
	    }
	}
}
int Schedule;

public boolean CheckIdle(Player player) throws IOException
{
	if(Config.debug_enable) Utils.Debug("Launching function: CheckIdle(Player player))");
	if (AuthDB.isAuthorized(player.getEntityId()) == false && this.plugin.IdleTask("check",player, ""+Schedule))
	{
		Messages.SendMessage("AuthDB_message_idle_kick", player, null);
		return true;
	}
	return false;
}

  public void onPlayerJoin(PlayerEvent event)
  {
	final Player player = event.getPlayer();
    try {
	    if(Config.idle_kick == true && Utils.CheckWhitelist(player.getDisplayName()) == false)
	    {
	    	if(Config.debug_enable) Utils.Debug("Idle time is: "+Config.idle_ticks);
	    	Schedule = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    		@Override
	    		public void run() 
	    		{ 
	    			try {
						CheckIdle(player);
					} catch (IOException e) {
						Utils.Log("warning", "Error checking if player was in the idle list");
						e.printStackTrace();
					} 
	    		} }, Config.idle_ticks);
	    	if(this.plugin.IdleTask("add",player, ""+Schedule))
	    		if(Config.debug_enable) Utils.Debug(player.getName()+" added to the IdleTaskList");
	    	this.plugin.updateDb();
	    }
   if (this.plugin.isRegistered(player.getName())) {
        this.plugin.storeInventory(player.getName(), player.getInventory().getContents());
         player.getInventory().clear();
       Messages.SendMessage("AuthDB_message_welcome_user", player,null);
     } else if (Config.register_force) {
        this.plugin.storeInventory(player.getName(), player.getInventory().getContents());
       player.getInventory().clear();
		Messages.SendMessage("AuthDB_message_welcome_guest", player,null);
      }
     else if (!Config.register_force) { Messages.SendMessage("AuthDB_message_welcome_guest", player,null); }
     else {
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
	// plugin.getServer().getScheduler().scheduleSyncDelayedTask;
     Player player = event.getPlayer();
     try {
		if(this.plugin.IdleTask("check",player, "0"))
		 {
			int TaskID = Integer.parseInt(this.plugin.IdleGetTaskID(player));
			if(Config.debug_enable) Utils.Debug(player.getName()+" is in the IdleTaskList with ID: "+TaskID);
			if(this.plugin.IdleTask("remove",player, "0"))
			{
				if(Config.debug_enable) Utils.Debug(player.getName()+" was removed from the IdleTaskList");
				plugin.getServer().getScheduler().cancelTask(TaskID);
			}
			else { if(Config.debug_enable) Utils.Debug("Could not remove "+player.getName()+" from the idle list."); }
		 }
		else { if(Config.debug_enable) Utils.Debug("Could not find "+player.getName()+" in the idle list, no need to remove."); }
		this.plugin.updateDb();
	} catch (IOException e) {
		if(Config.debug_enable) Utils.Debug("Error with the Idle list, can't cancel task?");
		e.printStackTrace();
	}
   ItemStack[] inv = this.plugin.getInventory(player.getName());
    if ((inv != null) && (!AuthDB.isAuthorized(player.getEntityId()))) {
     //player.getInventory().setContents(inv);
     //player.kickPlayer("inventory protection kicked");
    }
    //this.plugin.unauthorize(player.getEntityId());
  }

  public void onPlayerCommandPreprocess(PlayerChatEvent event)
  {
    String[] split = event.getMessage().split(" ");
	Player player = event.getPlayer();
    if (split[0].equals("/login")) {
      if (AuthDB.isAuthorized(player.getEntityId())) {			  
				  Messages.SendMessage("AuthDB_message_login_authorized", player,null);
      }
      else if (split.length < 2) {
				  Messages.SendMessage("AuthDB_message_login_usage", player,null);
      }
      else if (this.plugin.checkPassword(player.getName(), split[1])) {
         ItemStack[] inv = this.plugin.getInventory(player.getName());
        if (inv != null)
         player.getInventory().setContents(inv);
        this.plugin.authorize(player.getEntityId());
	    Messages.SendMessage("AuthDB_message_login_success", player,null);
	} else if (Config.password_kick) {
       ItemStack[] inv = this.plugin.getInventory(player.getName());
      if (inv != null)
          player.getInventory().setContents(inv);
				  Messages.SendMessage("AuthDB_message_login_failure", player,null);
      }
     event.setMessage("/login ******");
      event.setCancelled(true);
     }
				else if (split[0].equals("/register")) {
      if (split.length < 2) {
				  Messages.SendMessage("AuthDB_message_register_usage", player,null);
      }
      else if (this.plugin.isRegistered(player.getName()))
				  Messages.SendMessage("AuthDB_message_register_registered", player,null);
      else if (!Config.register_enabled)
				  Messages.SendMessage("AuthDB_message_register_disabled", player,null);
      else if (split.length < 3)
				  Messages.SendMessage("AuthDB_message_email_required", player,null);
       else if ((split.length >= 3) && (!this.plugin.checkEmail(split[2])))
				  Messages.SendMessage("AuthDB_message_email_badcharacters", player,null);
      else {
        try {
           if (split.length >= 3)
             this.plugin.register(player.getName(), split[1], split[2],Utils.GetIP(player));
          else
            this.plugin.register(player.getName(), split[1],Utils.GetIP(player));
          ItemStack[] inv = this.plugin.getInventory(player.getName());
         if (inv != null)
            player.getInventory().setContents(inv);
					Messages.SendMessage("AuthDB_message_register_success", player,null);
          this.plugin.authorize(player.getEntityId());
        } catch (IOException e) {
					Messages.SendMessage("AuthDB_message_register_failure", player,null);
          e.printStackTrace();
        } catch (SQLException e) {
					Messages.SendMessage("AuthDB_message_register_failure", player,null);
          e.printStackTrace();
        }
      }
      event.setMessage("/register *****");
       event.setCancelled(true);
     } else if (!AuthDB.isAuthorized(player.getEntityId())) {
      event.setCancelled(true);
     event.setMessage("");
    }
  }

  public void onPlayerMove(PlayerMoveEvent event)
  {
    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId())) {
      event.setCancelled(true);
     event.getPlayer().teleportTo(event.getFrom());
    }
  }

  public void onPlayerChat(PlayerChatEvent event)
  {
    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
       event.setCancelled(true);
  }

  public void onPlayerItem(PlayerItemEvent event)
  {
    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
      event.setCancelled(true);
  }
  public void onPlayerPickupItem(PlayerPickupItemEvent event) 
  {
	    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
	      event.setCancelled(true);
  }

  public void onPlayerRespawn(PlayerRespawnEvent event) 
  {
	 // AuthDB.deleteInventory(event.getPlayer().getName());
  }
}