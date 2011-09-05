/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.util.managers;

import org.bukkit.entity.Player;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.encryption.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.EBean;

public class PlayerManager {
    public long sessionTime(Player player) {
        long sessionTime = 0;
        if (Config.session_enabled) {
            String check = Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player));
            if (AuthDB.AuthDB_Sessions.containsKey(check)) {
                long temp = AuthDB.AuthDB_Sessions.get(check);
                if (temp != 0) {
                    sessionTime = temp;
                    EBean.checkSessiontime(player.getName(), sessionTime);
                }
            } else {
                EBean EBeanClass = EBean.checkPlayer(player.getName(), true);
                sessionTime = EBeanClass.getSessiontime();
                if(sessionTime != 0) {
                    AuthDB.AuthDB_Sessions.put(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)), sessionTime);
                }
            }
        }
        return sessionTime;
    }
}
