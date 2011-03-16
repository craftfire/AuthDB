package com.gmail.contexmoh.authdb.listeners;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.gmail.contexmoh.authdb.AuthDB;


public class AuthDBBlockListener extends BlockListener
{
  private final AuthDB plugin;

  public AuthDBBlockListener(AuthDB instance)
  {
	  plugin = instance;
  }

  public void onBlockPlace(BlockPlaceEvent event) {
    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
      event.setCancelled(true);
  }

  public void onBlockDamage(BlockDamageEvent event) {
   if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
      event.setCancelled(true);
  }
}
