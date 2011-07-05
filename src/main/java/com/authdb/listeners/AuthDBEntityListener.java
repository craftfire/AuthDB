/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.listeners;

import org.bukkit.entity.Animals;
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
           Player p = (Player)event.getEntity();
           if(this.plugin.AuthTimeDB.containsKey(p.getName()))
           {
               long timestamp = System.currentTimeMillis()/1000;
               long difference = timestamp - Integer.parseInt(this.plugin.AuthTimeDB.get(p.getName()));
               if(difference < 5)
               {
                   Util.Debug("Time difference: "+difference+", canceling damage.");
                   event.setCancelled(true);
               }
           }
           if (event instanceof EntityDamageByEntityEvent)
           {
               EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
               if ((e.getDamager() instanceof Animals) || (e.getDamager() instanceof Monster))
               {
                   if (event.getEntity() instanceof Player)
                   {
                      if (!CheckGuest(p,Config.guests_health))
                        {
                            event.setCancelled(true);
                        }
                   }
               }
               else if (e.getDamager() instanceof Player)
               {
                    if (e.getEntity() instanceof Player)
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
               else
               {
                   if (!CheckGuest(p,Config.guests_health))
                     {
                       event.setCancelled(true);
                     }
                   else if (this.plugin.isRegistered("health",p.getName()) == true && AuthDB.isAuthorized(p.getEntityId()) == false)
                   {
                       event.setCancelled(true);
                   }
               }
           }
           else 
           { 
               if (this.plugin.isRegistered("health",p.getName()) == true && AuthDB.isAuthorized(p.getEntityId()) == false)
               {
                   event.setCancelled(true);
                   return;
               }
           }
       }
        else if ((event.getEntity() instanceof Animals) || (event.getEntity() instanceof Monster))
        {
            if (!(event instanceof EntityDamageByEntityEvent)) { return; }
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
                if ((e.getDamager() instanceof Player))
                {
                Player t = (Player)e.getDamager();
                  if(!CheckGuest(t,Config.guests_mobdamage)) { event.setCancelled(true); }
                }
        }
    }

    public boolean CheckGuest(Player player,boolean what)
    {
     if(what == true && this.plugin.isRegistered("checkguest",player.getName()) == false)
     {
              return true;
     }
     else if (this.plugin.isRegistered("checkguest",player.getName()) == true && AuthDB.isAuthorized(player.getEntityId()) == true)
     {
        return true; 
     }
     return false;
    }
}
