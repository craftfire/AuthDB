/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.listeners;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.authdb.AuthDB;
import com.authdb.plugins.ZSpout;
import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.authdb.util.Messages.Message;
import com.craftfire.util.general.GeneralUtil;

public class AuthDBSpoutListener extends SpoutListener {
    private final AuthDB plugin;

    public AuthDBSpoutListener(AuthDB instance) {
        plugin = instance;
    }

    @Override
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
        super.onSpoutCraftEnable(event);
        Player player = event.getPlayer();
        if (!AuthDB.isAuthorized(player) && this.plugin.isRegistered("join",player.getName())) {
            if (Util.toLoginMethod(Config.login_method).equalsIgnoreCase("prompt")) {
                ZSpout spout = new ZSpout();
                if (spout.checkGUI(player)) {
                    Util.logging.Debug("User has Spout!");
                }
            }
        }
    }
}
