package com.gmail.contexmoh.authdb.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.contexmoh.authdb.AuthDB;


public class AuthDBEntityListener extends BlockListener
{
private final AuthDB plugin;

public AuthDBEntityListener(AuthDB instance)
{
   this.plugin = instance;
}

public void onEntityDamage(EntityDamageEvent event) 
	{
	     if (((event.getEntity() instanceof Player)) && 
	     (!this.plugin.isAuthorized(event.getEntity().getEntityId())))
	      event.setCancelled(true);
	}
}
