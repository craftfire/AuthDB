/**          © Copyright 2011 Contex <contexmoh@gmail.com>
	
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

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.authdb.AuthDB;


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
  
  public void onBlockInteract(BlockInteractEvent event) {
    LivingEntity e = event.getEntity();
    if ((e != null) && ((e instanceof HumanEntity)) && 
      (!AuthDB.isAuthorized(e.getEntityId())))
      event.setCancelled(true);
  }
}
