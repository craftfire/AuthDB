package com.gmail.contexmoh.authdb.listeners;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.gmail.contexmoh.authdb.AuthDB;


public class AuthDBEntityListener extends BlockListener
{
private final AuthDB plugin;

public AuthDBEntityListener(AuthDB instance)
{
   this.plugin = instance;
}

public void onBlockPlace(BlockPlaceEvent event) {
    if (!this.plugin.isAuthorized(event.getPlayer().getEntityId()))
     event.setCancelled(true);
}

public void onBlockDamage(BlockDamageEvent event) {
    if (!this.plugin.isAuthorized(event.getPlayer().getEntityId()))
     event.setCancelled(true);
}
}
