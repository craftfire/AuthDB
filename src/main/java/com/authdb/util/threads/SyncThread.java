package com.authdb.util.threads;

import com.authdb.util.Config;
import com.authdb.util.Util;
import com.authdb.util.databases.EBean;

import java.sql.SQLException;

public class SyncThread implements Runnable {
	
    private String player;
    
    public SyncThread(String player) {
        this.player = player;
    }

    public void run() {
    	try {
            Util.logging.Debug("Running Sync thread for user: " + this.player);
            if (!Config.database_keepalive) {
                Util.databaseManager.connect();
            }
            EBean eBeanClass = EBean.checkPlayer(this.player, true);
            String registered = eBeanClass.getRegistered();
            if (!Util.checkOtherName(this.player).equals(this.player)) {
                eBeanClass.setRegistered("true");
                eBeanClass.save(eBeanClass);
                registered = "true";
            } else if (Util.checkScript("checkuser", Config.script_name, Util.checkOtherName(this.player), null, null, null)) {
                eBeanClass.setRegistered("true");
                eBeanClass.save(eBeanClass);
                registered = "true";
            } else {
                if (registered != null && registered.equalsIgnoreCase("true")) {
                    Util.logging.Debug("Registered value for " + this.player + " in persistence is different than in MySQL, syncing registered value from MySQL.");
                    eBeanClass.setRegistered("false");
                    eBeanClass.save(eBeanClass);
                    registered = "false";
                }
            }
            if (registered != null && registered.equalsIgnoreCase("true")) {
                Util.checkScript("syncpassword", Config.script_name, Util.checkOtherName(this.player), null, null, null);
                Util.checkScript("syncsalt", Config.script_name, Util.checkOtherName(this.player), null, null, null);
            }
            if (!Config.database_keepalive) {
                Util.databaseManager.close();
            }
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
    }
}