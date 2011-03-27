/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

**/
package com.authdb.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.authdb.AuthDB;


public class AuthDBEntityListener extends EntityListener
{
private final AuthDB plugin;

public AuthDBEntityListener(AuthDB instance)
{
   this.plugin = instance;
}

public void onEntityTarget(EntityTargetEvent event)
{
	
  if (((event.getEntity() instanceof Player)) && AuthDB.isAuthorized(event.getEntity().getEntityId()) == false)
	   event.setCancelled(true);
  else if (((event.getEntity() instanceof Monster)) && (event.getTarget() instanceof Player) && AuthDB.isAuthorized(event.getTarget().getEntityId()) == false)
	   event.setCancelled(true);
}

public void onEntityDamage(EntityDamageEvent event) 
	{
	   if (((event.getEntity() instanceof Player)) && AuthDB.isAuthorized(event.getEntity().getEntityId()) == false)
		   event.setCancelled(true);
	}
}
