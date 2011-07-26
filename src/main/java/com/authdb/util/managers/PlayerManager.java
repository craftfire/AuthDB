package com.authdb.util.managers;

import org.bukkit.entity.Player;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.EBean;

public class PlayerManager {
    public long sessionTime(Player player) {
        long sessionTime = 0;
        if(Config.session_enabled) {
            String check = Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player));
            if (AuthDB.AuthDB_Sessions.containsKey(check)) {
                long temp = AuthDB.AuthDB_Sessions.get(check);
                if(temp != 0) {
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
