/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

**/

package com.authdb.listeners;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Messages;
import com.authdb.util.Util;

import com.afforess.backpack.BackpackManager;
import com.afforess.backpack.BackpackPlayer;

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
	Player player = event.getPlayer();
	if(Config.filter_kick || Config.filter_rename)
	{
		if(Config.debug_enable) Util.Debug("Kick on bad characters: "+Config.filter_kick+" | Remove bad characters: "+Config.filter_rename);
		String name = player.getName();
		if (Util.CheckFilter("username",name) == false && Util.CheckWhitelist("username",player) == false)
	    {
		  if(Config.debug_enable) Util.Debug("The player is not in the whitelist and has bad characters in his/her name");
	      if(Config.filter_kick) Messages.SendMessage("AuthDB_message_filter_username", player, event);
	     // else if(Config.filter_rename) Messages.SendMessage("AuthDB_message_filter_renamed", player, event);
	    }
	}
	if(player.getName().length() < Integer.parseInt(Config.username_minimum))
	{
		Messages.SendMessage("AuthDB_message_username_minimum", player, event);
	}
	else if(player.getName().length() > Integer.parseInt(Config.username_maximum))
	{
		Messages.SendMessage("AuthDB_message_username_maximum", player, event);
	}
    if(Config.link_rename && Util.CheckOtherName(player.getName()) != player.getName())
    {
        Util.RenamePlayer(player,Util.CheckOtherName(player.getName()));
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
	if(Config.link_rename && Util.CheckOtherName(player.getName()) != player.getName())
    {
    	String message = event.getJoinMessage();
    	message = message.replaceAll(player.getName(), player.getDisplayName());
    	event.setJoinMessage(message);
    }
	this.plugin.AuthPasswordTriesDB.put(player.getName(),"0");
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
		else if (this.plugin.isRegistered("join",player.getName()) || this.plugin.isRegistered("join",Util.CheckOtherName(player.getName()))) 
		{
		    if(Config.HasBackpack) 
		    { 
		    	BackpackPlayer BackpackPlayer = BackpackManager.getBackpackPlayer((Player)player); 
		    	BackpackPlayer.createBackpack();
		    	this.plugin.storeInventory(player.getName(), BackpackPlayer.getContents()); 
		    }
		    else 
		    { 
		    	this.plugin.storeInventory(player.getName(), player.getInventory().getContents()); 
		    }
		    player.getInventory().clear();
		     plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {  @Override public void run() { if(!AuthDB.isAuthorized(player.getEntityId())) { if(player.getInventory() != null) {  player.getInventory().clear(); } } } } , 20);
			 if(Util.ToLoginMethod(Config.login_method).equals("prompt"))
			 {
				 Messages.SendMessage("AuthDB_message_login_prompt", player,null);
			 }
			 else
			 {
				 Messages.SendMessage("AuthDB_message_login_default", player,null);
			 }
		 } 
		else if (Config.register_force) 
		{
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
		if(Config.session_start.equals("logoff"))
			this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+thetimestamp);
		this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
		this.plugin.unauthorize(player.getEntityId());
		
	 if (CheckGuest(player,Config.guests_inventory) == false && this.plugin.isRegistered("quit",player.getName()) == false && this.plugin.isRegistered("quit",Util.CheckOtherName(player.getName())) == false)
	  {
		 ItemStack[] theinv = new ItemStack[36];
		 player.getInventory().setContents(theinv);
	  }
  }
  
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
  {
    String[] split = event.getMessage().split(" ");
	Player player = event.getPlayer();

    if (split[0].equals("/login"))
    {
        if (this.plugin.isRegistered("command",player.getName()) == false || this.plugin.isRegistered("command",Util.CheckOtherName(player.getName())) == false)
        {
        	Messages.SendMessage("AuthDB_message_login_notregistered", player,null);
        }
  	    else if (AuthDB.isAuthorized(player.getEntityId())) 
  	    {			  
  	    	Messages.SendMessage("AuthDB_message_login_authorized", player,null);
        }
  	    else if (split.length < 2) 
  	    {
  	    	Messages.SendMessage("AuthDB_message_login_usage", player,null);
  	    }
        else if (this.plugin.checkPassword(player.getName(), split[1])) 
        {
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
	    if(Config.debug_enable) Util.Debug(player.getName()+" login ********");
	    event.setMessage("/login ******");
	    event.setCancelled(true);
     }
    else if (split[0].equals("/link"))
    {
    	if(Config.link_enabled)
    	{
	        if (split.length == 3) 
	        {
	        	if(Util.CheckOtherName(player.getName()).equals(player.getName()))
	        	{
			  	    if (this.plugin.checkPassword(split[1], split[2])) 
			      	{
		      	        ItemStack[] inv = this.plugin.getInventory(player.getName());
		      	        if (inv != null) { player.getInventory().setContents(inv); }
		    			long thetimestamp = System.currentTimeMillis()/1000;
		    			this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
		      	        this.plugin.authorize(player.getEntityId());
		      			long timestamp = System.currentTimeMillis()/1000;
		      			this.plugin.db3.put(Encryption.md5(player.getName()), "yes");
		  	 			this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
		  	 			this.plugin.AuthOtherNamesDB.put(player.getName(),split[1]);
	  	 				Util.ToFile("write",  player.getName(), split[1]);
		  				if(Config.debug_enable) { Util.Debug("Session started for "+player.getName()); }
		  				if(Config.link_rename) { player.setDisplayName(split[1]); }
		      		    Messages.SendMessage("AuthDB_message_link_success", player,null);
			  		} 
			  	    else { Messages.SendMessage("AuthDB_message_link_failure", player,null); }
	        	}
	        	else { Messages.SendMessage("AuthDB_message_link_exists", player,null); }
	        }
	        else { Messages.SendMessage("AuthDB_message_link_usage", player,null); }
		    if(Config.debug_enable) Util.Debug(player.getName()+" link ******** ********");
		    event.setMessage("/link ****** ********");
		    event.setCancelled(true);
    	}
     }
    else if (split[0].equals("/unlink"))
    {
    	if(Config.unlink_enabled)
	    	{
	        if (split.length == 3) 
	        {
	        	if(Util.CheckOtherName(player.getName()).equals(player.getDisplayName()))
	        	{
			  	    if (this.plugin.checkPassword(split[1], split[2])) 
			      	{
		      	        ItemStack[] inv = this.plugin.getInventory(player.getName());
		      	        if (inv != null) { player.getInventory().setContents(inv); }
		      	        this.plugin.unauthorize(player.getEntityId());
		      	        this.plugin.db2.remove(Encryption.md5(player.getName()+Util.GetIP(player)));
		      	        this.plugin.db3.remove(Encryption.md5(player.getName()));
		  	 			this.plugin.AuthOtherNamesDB.remove(player.getName());
	  	 				Util.ToFile("remove", player.getName(), null);
	  	 				if(Config.unlink_rename) { player.setDisplayName(player.getName()); }
		      		    Messages.SendMessage("AuthDB_message_unlink_success", player,null);
			  		} 
			  	    else { Messages.SendMessage("AuthDB_message_unlink_failure", player,null); }
	        	}
	        	else { Messages.SendMessage("AuthDB_message_unlink_nonexist", player,null); }
	        }
	        else { Messages.SendMessage("AuthDB_message_unlink_usage", player,null); }
		    if(Config.debug_enable) Util.Debug(player.getName()+" unlink ******** ********");
		    event.setMessage("/unlink ****** ********");
		    event.setCancelled(true);
	    }
     }
	else if (split[0].equals("/register")) {
		Boolean email = true;
		if(Config.custom_emailfield == null || Config.custom_emailfield == "") { email = false; }
      if (!Config.register_enabled)
		  Messages.SendMessage("AuthDB_message_register_disabled", player,null);
      else if (this.plugin.isRegistered("register-command",player.getName()) || this.plugin.isRegistered("register-command",Util.CheckOtherName(player.getName())))
		  Messages.SendMessage("AuthDB_message_register_registered", player,null);
      else if (split.length < 2) {
				  Messages.SendMessage("AuthDB_message_register_usage", player,null);
      }
      else if (split.length < 3 && email)
      {
				  Messages.SendMessage("AuthDB_message_email_required", player,null);
      }
       else if ((split.length >= 3 && email) && (!this.plugin.checkEmail(split[2])))
				  Messages.SendMessage("AuthDB_message_email_invalid", player,null);
      else {
        try {
           if (split.length >= 3 || ( !email && split.length >= 2 )) 
           { 
        	   String themail = null;
        	   if(!email) { themail = null; }
        	   else { themail = split[2]; }
        	if(this.plugin.register(player, split[1], themail,Util.GetIP(player)))
        	{
	            ItemStack[] inv = this.plugin.getInventory(player.getName());
	            if (inv != null) { player.getInventory().setContents(inv); }
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
        	}
        }
        }catch (IOException e) {
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
	      if(Util.ToLoginMethod(Config.login_method).equals("prompt") && (this.plugin.isRegistered("chat",event.getPlayer().getName()) || this.plugin.isRegistered("chat",Util.CheckOtherName(event.getPlayer().getName()))))
	      {
	    	  String[] split = event.getMessage().split(" ");
	    	  Player player = event.getPlayer();
	    	  if (this.plugin.isRegistered("chatprompt",player.getName()) || this.plugin.isRegistered("chatprompt",Util.CheckOtherName(player.getName()))) 
	    	  {
		      	  if (AuthDB.isAuthorized(player.getEntityId())) {			  
		    				  Messages.SendMessage("AuthDB_message_login_authorized", player,null);
		          }
		          else if (split.length > 1) {
		    				  player.sendMessage("§bPlease type in the password for "+Util.CheckOtherName(player.getName()));
		          }
		          else if (this.plugin.checkPassword(player.getName(), split[0]) || this.plugin.checkPassword(Util.CheckOtherName(player.getName()), split[0])) {
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
	      else if(!CheckGuest(event.getPlayer(),Config.guests_chat))
		  {
		      event.setCancelled(true);
		  }
    }
  }
  
  public void onPlayerKick(PlayerKickEvent event) 
  {
      Util.Debug("KICK1: "+event.getReason());
      Util.Debug("KICK2: "+event.getLeaveMessage());
      Util.Debug("KICK3: "+event.getType());
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
			 if (this.plugin.isRegistered("dropitem",event.getPlayer().getName()) || this.plugin.isRegistered("dropitem",Util.CheckOtherName(event.getPlayer().getName())))
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
	  if (this.plugin.isRegistered("checkguest",player.getName()) == false || this.plugin.isRegistered("checkguest",Util.CheckOtherName(player.getName())) == false)
	  {
		      return true;
	  }
	 }
	 return false;
	}
}