/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

**/
package com.authdb.listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Util;


public class AuthDBEntityListener extends EntityListener
{
private final AuthDB plugin;

public AuthDBEntityListener(AuthDB instance)
{
   this.plugin = instance;
}



public void onEntityTarget(EntityTargetEvent event)
{
  if (((event.getEntity() instanceof Monster)) && (event.getTarget() instanceof Player) && AuthDB.isAuthorized(event.getTarget().getEntityId()) == false)
  {
	  Player p = (Player)event.getTarget();
  	  if (!CheckGuest(p,Config.guests_mobtargeting))
  	  {
  	      event.setCancelled(true);
  	  }
  }
}

public void onEntityDamage(EntityDamageEvent event) 
	{
		if (event.getEntity() instanceof Player)
		{
			   if(event.getCause().name().equals("FALL"))
			   {
				   Player p = (Player)event.getEntity();
				   if (!CheckGuest(p,Config.guests_health))
			  	   {
					   event.setCancelled(true);
			  	   }
			   }
			   else if(event.getCause().name().equals("ENTITY_ATTACK"))
			   {
				   Player p = (Player)event.getEntity();
				   EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			  	  if ((e.getEntity() instanceof Player))
			  	  {
			  		Player t = (Player)e.getDamager();
			  		if(!CheckGuest(t,Config.guests_pvp))
			  		{
			  		 if (!CheckGuest(p,Config.guests_health))
				  	  {
				  	      event.setCancelled(true);
				  	  }
			  		}
			  	  }
			   }
		   }
		else if ((event.getEntity() instanceof Animals) || (event.getEntity() instanceof Monster))
		{
			   if (!(event instanceof EntityDamageByEntityEvent)) { return; }
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			Player t = (Player)e.getDamager();
		  	  if ((e.getDamager() instanceof Player) && CheckGuest(t,Config.guests_mobdamage) == false)
		  	  {
		  		event.setCancelled(true);
		  	  }
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
