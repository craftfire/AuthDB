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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.authdb.util.Messages.Message;

public class AuthDBBlockListener implements Listener {
    private final AuthDB plugin;

    public AuthDBBlockListener(AuthDB instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!AuthDB.isAuthorized(event.getPlayer()) && !checkGuest(event.getPlayer(), Config.guests_build)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockDamage(BlockDamageEvent event) {
        if (!AuthDB.isAuthorized(event.getPlayer()) && !checkGuest(event.getPlayer(), Config.guests_destroy)) {
            event.setCancelled(true);
        }
    }

    /*
    public void onBlockDamage(BlockBreakEvent event) {
        if (!AuthDB.isAuthorized(event.getPlayer().getEntityId())) {
            if (!checkGuest(event.getPlayer(), Config.guests_destroy)) {
                event.setCancelled(true);
            }
        }
    }*/

    boolean checkGuest(Player player, boolean what) {
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
        } else {
            if (Config.protection_notify && this.plugin.AuthDB_RemindLogin.containsKey(player.getName())) {
                this.plugin.AuthDB_RemindLogin.remove(player.getName());
            }
        }
        return false;
    }
}
