/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

**/

package com.authdb.listeners;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.afforess.backpack.BackpackManager;
import com.afforess.backpack.BackpackPlayer;
import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Messages;
import com.authdb.util.Util;

public class AuthDBPlayerListener extends PlayerListener
{
  private final AuthDB plugin;
  boolean sessionallow;
  int Schedule;

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
		if (Util.checkUsernameCharacters(name) == false && Util.CheckWhitelist("badcharacters",player) == false)
	    {
		  if(Config.debug_enable) Util.Debug("The player is not in the whitelist and has bad characters in his/her name");
	      if(Config.badcharacters_kick) Messages.SendMessage("AuthDB_message_badcharacters_kicked", player, event);
	     // else if(Config.badcharacters_remove) Messages.SendMessage("AuthDB_message_badcharacters_renamed", player, event);
	    }
	}
}

public boolean CheckIdle(Player player) throws IOException
{
	if(Config.debug_enable) 
		Util.Debug("Launching function: CheckIdle(Player player))");
	if (AuthDB.isAuthorized(player.getEntityId()) == false && this.plugin.IdleTask("check",player, ""+Schedule))
	{
		Messages.SendMessage("AuthDB_message_idle_kick", player, null);
		return true;
	}
	return false;
}

  public void onPlayerJoin(PlayerJoinEvent event)
  {
	final Player player = event.getPlayer();
	try {
		if(Config.session_length != "0" || Config.session_length != null)
		{
			long timestamp = System.currentTimeMillis()/1000;
			if(this.plugin.IdleTask("check2",player, "") == true)
			{ 
				long storedtime = Long.parseLong(this.plugin.db2.get(Encryption.md5(player.getName()+Util.GetIP(player))));
				if(Config.debug_enable) 
					Util.Debug("Found session for "+player.getName()+", timestamp: "+storedtime);
				long timedifference = timestamp - storedtime;
				if(Config.debug_enable) 
					Util.Debug("Difference: "+timedifference);
				if(timedifference > Config.session_seconds) { sessionallow = false; }
				else { sessionallow = true; }
				
			}
			else { sessionallow = false; }
		}
			
		try {
		    if(Config.idle_kick == true && Util.CheckWhitelist("idle",player) == false && sessionallow == false)
		    {
		    	if(Config.debug_enable) 
		    		Util.Debug("Idle time is: "+Config.idle_ticks);
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
		    		if(Config.debug_enable) 
		    			Util.Debug(player.getName()+" added to the IdleTaskList");
		    	this.plugin.updateDb();
		    }
		if(Config.custom_enabled) if(Config.custom_encryption == null) { player.sendMessage("§4YOUR PASSWORD WILL NOT BE ENCRYPTED, PLEASE BE ADWARE THAT THIS SERVER STORES THE PASSWORDS IN PLAINTEXT."); }
	     if(event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1)
	     {
	    	 player.setHealth(20);
	    	 player.teleportTo(player.getWorld().getSpawnLocation());
	     }
		
		if(sessionallow)
		{
			Messages.SendMessage("AuthDB_message_login_session", player,null);
			long thetimestamp = System.currentTimeMillis()/1000;
			this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
			this.plugin.authorize(event.getPlayer().getEntityId());
		}
		else if (this.plugin.isRegistered(player.getName())) {
		    if(Config.HasBackpack) 
		    { 
		    	BackpackPlayer BackpackPlayer = BackpackManager.getBackpackPlayer((Player)player); 
		    	BackpackPlayer.createBackpack();
		    	this.plugin.storeInventory(player.getName(), BackpackPlayer.getContents()); 
		    }
		    else { this.plugin.storeInventory(player.getName(), player.getInventory().getContents()); }
		    player.getInventory().clear();
		     plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {  @Override public void run() { if(!AuthDB.isAuthorized(player.getEntityId())) { if(player.getInventory() != null) {  player.getInventory().clear(); } } } } , 20);
			 if(Util.ToLoginMethod(Config.login_method).equals("prompt"))
			 {
				 player.sendMessage("§fAuth§bDB§f > Welcome §b"+player.getName()+"§f! Please enter your password:");
			 }
			 else
			 {
				 Messages.SendMessage("AuthDB_message_welcome_user", player,null);
			 }
		 } else if (Config.register_force) {
				    if(Config.HasBackpack) 
				    { 
				    	BackpackPlayer BackpackPlayer = BackpackManager.getBackpackPlayer((Player)player);
				    	BackpackPlayer.createBackpack();
				    	this.plugin.storeInventory(player.getName(), BackpackPlayer.getInventory().getContents()); 
				    }
				    else { this.plugin.storeInventory(player.getName(), player.getInventory().getContents()); }
					   player.getInventory().clear();
					  Messages.SendMessage("AuthDB_message_welcome_guest", player,null);
		  }
		 else if (!Config.register_force) { 
					  Messages.SendMessage("AuthDB_message_welcome_guest", player,null);
			 }
		 else {
				long thetimestamp = System.currentTimeMillis()/1000;
				this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
		    this.plugin.authorize(event.getPlayer().getEntityId());
		  }
		} catch (IOException e) {
		  Util.Log("severe","["+AuthDB.pluginname+"] Inventory file error:");
		  player.kickPlayer("inventory protection kicked");
		   e.printStackTrace();
		player.sendMessage(Color.red + "Error happend, report to admins!");
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  public void onPlayerQuit(PlayerQuitEvent event)
  {
	// plugin.getServer().getScheduler().scheduleSyncDelayedTask;
     Player player = event.getPlayer();
     Messages.SendMessage("AuthDB_message_left_server", player,null);
     if(event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1)
     {
    	 player.setHealth(20);
    	 player.teleportTo(player.getWorld().getSpawnLocation());
     }
    // Util.Debug("PLAYER HEALTH:"+event.getPlayer().getHealth());
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
		long thetimestamp = System.currentTimeMillis()/1000;
		this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
     this.plugin.unauthorize(player.getEntityId());
	 if (CheckGuest(player,Config.guests_inventory) == false && this.plugin.isRegistered(player.getName()) == false)
	  {
		 ItemStack[] theinv = new ItemStack[36];
		 player.getInventory().setContents(theinv);
	  }
  }

  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
  {
    String[] split = event.getMessage().split(" ");
	Player player = event.getPlayer();

    if (split[0].equals("/login")) {
        if (split.length == 3) {
      	  if (this.plugin.checkPassword(split[1], split[2])) {
      	         ItemStack[] inv = this.plugin.getInventory(player.getName());
      	        if (inv != null) { player.getInventory().setContents(inv); }
    			long thetimestamp = System.currentTimeMillis()/1000;
    			this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
      	        this.plugin.authorize(player.getEntityId());
      			long timestamp = System.currentTimeMillis()/1000;
      			this.plugin.db3.put(Encryption.md5(player.getName()), "yes");
  	 			this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
  				if(Config.debug_enable) 
  					Util.Debug("Session started for "+player.getName());
      		    Messages.SendMessage("AuthDB_message_login_success", player,null);
      		} 
      	    else
      		{
      			  Messages.SendMessage("AuthDB_message_login_failure", player,null);
      	    }
  }
        else if (!this.plugin.isRegistered(player.getName()))
  		  Messages.SendMessage("AuthDB_message_login_notregistered", player,null);
  	  else if (AuthDB.isAuthorized(player.getEntityId())) {			  
				  Messages.SendMessage("AuthDB_message_login_authorized", player,null);
      }
      else if (split.length < 2) {
				  Messages.SendMessage("AuthDB_message_login_usage", player,null);
      }
      else if (this.plugin.checkPassword(player.getName(), split[1])) {
         ItemStack[] inv = this.plugin.getInventory(player.getName());
        if (inv != null) { player.getInventory().setContents(inv); }
		long thetimestamp = System.currentTimeMillis()/1000;
		this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
        this.plugin.authorize(player.getEntityId());
		long timestamp = System.currentTimeMillis()/1000;
		this.plugin.db3.put(Encryption.md5(player.getName()), "yes");
		this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
		if(Config.debug_enable) 
			Util.Debug("Session started for "+player.getName());
	    Messages.SendMessage("AuthDB_message_login_success", player,null);
	} else
	{
      /* ItemStack[] inv = this.plugin.getInventory(player.getName());
	      if (inv != null)
	      {
	    	  player.getInventory().setContents(inv);
	      } */
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
				  Messages.SendMessage("AuthDB_message_email_invalid", player,null);
      else {
        try {
           if (split.length >= 3) { this.plugin.register(player.getName(), split[1], split[2],Util.GetIP(player)); }
          else { this.plugin.register(player.getName(), split[1],Util.GetIP(player)); }
          ItemStack[] inv = this.plugin.getInventory(player.getName());
         if (inv != null)
            player.getInventory().setContents(inv);
			long timestamp = System.currentTimeMillis()/1000;
			this.plugin.db3.put(Encryption.md5(player.getName()), "yes");
			this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
			if(Config.debug_enable)  { Util.Debug("Session started for "+player.getName()); }
			this.plugin.authorize(player.getEntityId());
			long thetimestamp = System.currentTimeMillis()/1000;
			this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
  			Location temploc = event.getPlayer().getLocation();
  			while(temploc.getBlock().getTypeId() == 0) { temploc.setY(temploc.getY() - 1); }
  			temploc.setY(temploc.getY() + 1);
  			event.getPlayer().teleport(temploc);
  			
			Messages.SendMessage("AuthDB_message_register_success", player,null);
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
     } 
	 else if (!AuthDB.isAuthorized(player.getEntityId())) 
     {
	  if (!CheckGuest(player,Config.guests_commands))
	  {
	      event.setMessage("/iamnotloggedin");
	      event.setCancelled(true);
	  }
    }
  }

  public void onPlayerMove(PlayerMoveEvent event)
  {
    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId())) 
    {
    	  if (!CheckGuest(event.getPlayer(),Config.guests_movement))
      	  {
      	    event.setCancelled(true);
      	    event.getPlayer().teleportTo(event.getFrom());
      	  }
    }
  }

  public void onPlayerChat(PlayerChatEvent event)
  {
    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
    {
      if(Util.ToLoginMethod(Config.login_method).equals("prompt"))
      {
    	  String[] split = event.getMessage().split(" ");
    	  Player player = event.getPlayer();
    	  if (this.plugin.isRegistered(player.getName())) 
    	  {
	      	  if (AuthDB.isAuthorized(player.getEntityId())) {			  
	    				  Messages.SendMessage("AuthDB_message_login_authorized", player,null);
	          }
	          else if (split.length > 1) {
	    				  player.sendMessage("§bPlease type in the password for "+player.getName());
	          }
	          else if (this.plugin.checkPassword(player.getName(), split[0])) {
	            ItemStack[] inv = this.plugin.getInventory(player.getName());
	            if (inv != null) { player.getInventory().setContents(inv); }
	            this.plugin.authorize(player.getEntityId());
	    		long timestamp = System.currentTimeMillis()/1000;
	    		this.plugin.db3.put(Encryption.md5(player.getName()), "yes");
	    		this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
	    		if(Config.debug_enable) 
	    			Util.Debug("Session started for "+player.getName());
	    	    Messages.SendMessage("AuthDB_message_login_success", player,null);
	    	} else {
	          /* ItemStack[] inv = this.plugin.getInventory(player.getName());
	    	      if (inv != null)
	    	      {
	    	    	  player.getInventory().setContents(inv);
	    	      } */
	    		  Messages.SendMessage("AuthDB_message_login_failure", player,null);
	          }
	          if(Config.debug_enable) 
	        	  Util.Debug(player.getName()+" login ********");
	          event.setMessage(" has logged in!");
	          event.setCancelled(true);  
    	  }
    	  event.setMessage("");
    	  event.setCancelled(true);  
      }
      if (!CheckGuest(event.getPlayer(),Config.guests_chat))
  	  {
  	      event.setCancelled(true);
  	  }
    }
  }

  public void onPlayerPickupItem(PlayerPickupItemEvent event) 
  {
	    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
	    {
			 if (!CheckGuest(event.getPlayer(),Config.guests_pickup))
			  {
				 event.setCancelled(true);
			  }
	    }
  }
  
  public void onPlayerInteract(PlayerInteractEvent event)
  {
	    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
	    {
			 if (!CheckGuest(event.getPlayer(),Config.guests_interact))
			  {
				 event.setCancelled(true);
			  }
	    }
  }
  
  public void onPlayerDropItem(PlayerDropItemEvent event) 
  {
	    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
	    {
			 if (this.plugin.isRegistered(event.getPlayer().getName()))
			 {
				 event.setCancelled(true);
			 }
			 else if (!CheckGuest(event.getPlayer(),Config.guests_drop))
			  {
				 event.setCancelled(true);
			  }
	    }
  }
  

  public void onPlayerRespawn(PlayerRespawnEvent event) 
  {
	     if(event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1)
	     {
	    	 event.getPlayer().setHealth(20);
	    	 event.getPlayer().teleportTo(event.getPlayer().getWorld().getSpawnLocation());
	     }
  }
  
	public boolean CheckGuest(Player player,boolean what)
	{
	 if(what)
	 {
	  if (!this.plugin.isRegistered(player.getName()))
	  {
		      return true;
	  }
	 }
	 return false;
	}
}