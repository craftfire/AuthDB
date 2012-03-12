package com.authdb.util.threads;

import org.bukkit.entity.Player;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Util;
import com.authdb.util.databases.EBean;
import com.authdb.util.encryption.Encryption;

public class LoginThread implements Runnable {
	
    private Player player;
    
    public LoginThread(Player player) {
        this.player = player;
    }

    public void run() {
    	 if (!AuthDB.isAuthorized(this.player)) {
             long timestamp = Util.timeStamp();
             if (!AuthDB.AuthDB_AuthTime.containsKey(this.player.getName())) {
                 AuthDB.AuthDB_AuthTime.put(this.player.getName(), timestamp);
             }
             AuthDB.authorizedNames.add(this.player.getName());
             AuthDB.AuthDB_PasswordTries.put(this.player.getName(), "0");
             EBean eBeanClass = EBean.checkPlayer(this.player, true);
             eBeanClass.setAuthorized("true");
             eBeanClass.setRegistered("true");
             if (!AuthDB.AuthDB_Authed.containsKey(Encryption.md5(this.player.getName()))) {
                 AuthDB.AuthDB_Authed.put(Encryption.md5(this.player.getName()), "yes");
             }
             if (Config.session_enabled) {
                 if (!AuthDB.AuthDB_Sessions.containsKey(Encryption.md5(this.player.getName() + Util.craftFirePlayer.getIP(this.player)))) {
                     AuthDB.AuthDB_Sessions.put(Encryption.md5(this.player.getName() + Util.craftFirePlayer.getIP(this.player)), timestamp);
                     Util.logging.Debug("Session started for " + this.player.getName());
                 }
                 eBeanClass.setSessiontime(timestamp);
             }
             AuthDB.database.save(eBeanClass);
             Util.craftFirePlayer.setInventoryFromStorage(this.player);
         }
    }
}