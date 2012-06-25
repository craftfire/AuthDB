/*
 * This file is part of AuthDB Legacy.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * AuthDB Legacy is licensed under the GNU Lesser General Public License.
 *
 * AuthDB Legacy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB Legacy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.authdb.listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.authdb.util.Messages.Message;

public class AuthDBEntityListener implements Listener {
    private final AuthDB plugin;

    public AuthDBEntityListener(AuthDB instance) {
       this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTarget(EntityTargetEvent event) {
        if ((event.getTarget() instanceof Player)) {
            Player player = (Player)event.getTarget();
            if (((event.getEntity() instanceof Monster)) && (event.getTarget() instanceof Player) && plugin.isAuthorized(player) == false) {
                Player p = (Player)event.getTarget();
                if (!checkGuest(p, Config.guests_mobtargeting)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (this.plugin.AuthDB_AuthTime.containsKey(p.getName())) {
                long timestamp = System.currentTimeMillis()/1000;
                long difference = timestamp - this.plugin.AuthDB_AuthTime.get(p.getName());
                if (difference < 5) {
                    Util.logging.Debug("Time difference: " + difference + ", canceling damage.");
                    event.setCancelled(true);
                }
            }
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
                if ((e.getDamager() instanceof Animals) || (e.getDamager() instanceof Monster)) {
                    if (event.getEntity() instanceof Player && !checkGuest(p, Config.guests_health)) {
                        event.setCancelled(true);
                    }
                } else if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                    Player t = (Player)e.getDamager();
                    if ((this.plugin.isRegistered("health", p.getName()) == true && plugin.isAuthorized(p) == false) || (!checkGuest(t, Config.guests_pvp) && !checkGuest(p, Config.guests_health))) {
                        event.setCancelled(true);
                    }
                } else {
                    if (!checkGuest(p, Config.guests_health)) {
                        event.setCancelled(true);
                    } else if (this.plugin.isRegistered("health", p.getName()) == true && plugin.isAuthorized(p) == false) {
                        event.setCancelled(true);
                    }
                }
            } else {
                if (this.plugin.isRegistered("health", p.getName()) == true && plugin.isAuthorized(p) == false) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else if ((event.getEntity() instanceof Animals) || (event.getEntity() instanceof Monster)) {
            if (!(event instanceof EntityDamageByEntityEvent)) {
                return;
            }
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
            if ((e.getDamager() instanceof Player)) {
                Player t = (Player)e.getDamager();
                if (plugin.isAuthorized(t) == false && !checkGuest(t, Config.guests_mobdamage)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    boolean checkGuest(Player player,boolean what) {
        if (what) {
            if (this.plugin.isRegistered("checkguest", player.getName()) == false || this.plugin.isRegistered("checkguest", Util.checkOtherName(player.getName())) == false) {
                return true;
            }
        } else if (Config.protection_notify && !AuthDB.isAuthorized(player)) {
            if (!this.plugin.AuthDB_RemindLogin.containsKey(player.getName())) {
                 this.plugin.AuthDB_RemindLogin.put(player.getName(), Util.timeStamp() + Config.protection_notify_delay);
                 Messages.sendMessage(Message.protection_notauthorized, player, null);
            } else {
                if (this.plugin.AuthDB_RemindLogin.get(player.getName()) < Util.timeStamp()) {
                    Messages.sendMessage(Message.protection_notauthorized, player, null);
                    this.plugin.AuthDB_RemindLogin.put(player.getName(), Util.timeStamp() + Config.protection_notify_delay);
                }
            }
        } else if (this.plugin.isRegistered("checkguest", player.getName()) == true && plugin.isAuthorized(player) == true) {
            if (Config.protection_notify && this.plugin.AuthDB_RemindLogin.containsKey(player.getName())) {
                this.plugin.AuthDB_RemindLogin.remove(player.getName());
            }
            return true;
        } else {
            if (Config.protection_notify && this.plugin.AuthDB_RemindLogin.containsKey(player.getName())) {
                this.plugin.AuthDB_RemindLogin.remove(player.getName());
            }
        }
        return false;
    }
}
