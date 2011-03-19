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

import org.bukkit.entity.Entity;
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
}

public void onEntityDamage(EntityDamageEvent event) 
	{
	   if (((event.getEntity() instanceof Player)) && AuthDB.isAuthorized(event.getEntity().getEntityId()) == false)
		   event.setCancelled(true);
	}
}
