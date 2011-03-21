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

package com.authdb.listeners;

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

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Util;

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
		if(Config.debug_enable) Util.Debug("Kick on badcharacters: "+Config.badcharacters_kick+" | Remove bad characters: "+Config.badcharacters_remove);
		Player player = event.getPlayer();
		String name = player.getName();
		if (Util.checkUsernameCharacters(name) == false && Util.CheckWhitelist(name) == false)
	    {
		  if(Config.debug_enable) Util.Debug("The player is not in the whitelist and has bad characters in his/her name");
	      if(Config.badcharacters_kick) Messages.SendMessage("AuthDB_message_badcharacters_kicked", player, event);
	      else if(Config.badcharacters_remove) Messages.SendMessage("AuthDB_message_badcharacters_renamed", player, event);
	    }
	}
}
int Schedule;

public boolean CheckIdle(Player player) throws IOException
{
	if(Config.debug_enable) Util.Debug("Launching function: CheckIdle(Player player))");
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
	    if(Config.idle_kick == true && Util.CheckWhitelist(player.getDisplayName()) == false)
	    {
	    	if(Config.debug_enable) Util.Debug("Idle time is: "+Config.idle_ticks);
	    	Schedule = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    		@Override
	    		public void run() 
	    		{ 
	    			try {
						CheckIdle(player);
					} catch (IOException e) {
						Util.Log("warning", "Error checking if player was in the idle list");
						e.printStackTrace();
					} 
	    		} }, Config.idle_ticks);
	    	if(this.plugin.IdleTask("add",player, ""+Schedule))
	    		if(Config.debug_enable) Util.Debug(player.getName()+" added to the IdleTaskList");
	    	this.plugin.updateDb();
	    }
	if(Config.custom_enabled) if(Config.custom_encryption == null) { player.sendMessage("§4YOUR PASSWORD WILL NOT BE ENCRYPTED, PLEASE BE ADWARE THAT THIS SERVER STORES THE PASSWORDS IN PLAINTEXT."); }
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
      Util.Log("severe","["+AuthDB.pluginname+"] Inventory file error:");
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
			if(Config.debug_enable) Util.Debug(player.getName()+" is in the IdleTaskList with ID: "+TaskID);
			if(this.plugin.IdleTask("remove",player, "0"))
			{
				if(Config.debug_enable) Util.Debug(player.getName()+" was removed from the IdleTaskList");
				plugin.getServer().getScheduler().cancelTask(TaskID);
			}
			else { if(Config.debug_enable) Util.Debug("Could not remove "+player.getName()+" from the idle list."); }
		 }
		else { if(Config.debug_enable) Util.Debug("Could not find "+player.getName()+" in the idle list, no need to remove."); }
		this.plugin.updateDb();
	} catch (IOException e) {
		if(Config.debug_enable) Util.Debug("Error with the Idle list, can't cancel task?");
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
      if (!this.plugin.isRegistered(player.getName()))
  		  Messages.SendMessage("AuthDB_message_login_ notregistered", player,null);
  	  else if (AuthDB.isAuthorized(player.getEntityId())) {			  
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
      if(Config.debug_enable) Util.Debug(player.getName()+" login ********");
     event.setMessage("/login ******");
      event.setCancelled(true);
     }
	else if (split[0].equals("/register")) {
      if (!Config.register_enabled)
		  Messages.SendMessage("AuthDB_message_register_disabled", player,null);
      else if (this.plugin.isRegistered(player.getName()))
		  Messages.SendMessage("AuthDB_message_register_registered", player,null);
      else if (split.length < 2) {
				  Messages.SendMessage("AuthDB_message_register_usage", player,null);
      }
      else if (split.length < 3)
				  Messages.SendMessage("AuthDB_message_email_required", player,null);
       else if ((split.length >= 3) && (!this.plugin.checkEmail(split[2])))
				  Messages.SendMessage("AuthDB_message_email_badcharacters", player,null);
      else {
        try {
           if (split.length >= 3)
             this.plugin.register(player.getName(), split[1], split[2],Util.GetIP(player));
          else
            this.plugin.register(player.getName(), split[1],Util.GetIP(player));
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
      if(Config.debug_enable) Util.Debug(player.getName()+" register ********");
      event.setMessage("/register *****");
       event.setCancelled(true);
     } else if (!AuthDB.isAuthorized(player.getEntityId())) {
      event.setMessage("/iamnotloggedin");
      event.setCancelled(true);
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