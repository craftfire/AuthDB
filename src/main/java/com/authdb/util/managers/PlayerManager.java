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
                if (sessionTime != 0) {
                    AuthDB.AuthDB_Sessions.put(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)), sessionTime);
                }
            }
        }
        return sessionTime;
    }
}
