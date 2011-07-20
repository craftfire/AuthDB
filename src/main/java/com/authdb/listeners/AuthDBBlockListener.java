/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.authdb.util.Messages.Message;

public class AuthDBBlockListener extends BlockListener {
    private final AuthDB plugin;

    public AuthDBBlockListener(AuthDB instance) {
        plugin = instance;
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        if (!AuthDB.isAuthorized(event.getPlayer()) && !checkGuest(event.getPlayer(), Config.guests_build)) {
            event.setCancelled(true);
        }
    }

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

    public boolean checkGuest(Player player, boolean what) {
        if (what && (this.plugin.isRegistered("checkguest", player.getName()) == false || this.plugin.isRegistered("checkguest", Util.checkOtherName(player.getName())) == false)) {
            return true;
        } else if (Config.protection_notify && this.plugin.isRegistered("checkguest", player.getName()) == false || this.plugin.isRegistered("checkguest", Util.checkOtherName(player.getName())) == false) {
            if (!this.plugin.AuthDB_RemindLogin.containsKey(player.getName())) {
                this.plugin.AuthDB_RemindLogin.put(player.getName(), Util.timeStamp() + Config.protection_delay);
                Messages.sendMessage(Message.guest_notauthorized, player, null);
            } else {
                if (this.plugin.AuthDB_RemindLogin.get(player.getName()) < Util.timeStamp()) {
                    Messages.sendMessage(Message.guest_notauthorized, player, null);
                    this.plugin.AuthDB_RemindLogin.put(player.getName(), Util.timeStamp() + Config.protection_delay);
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
