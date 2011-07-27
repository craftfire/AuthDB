/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.util.databases;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Entity()
@Table(name = "authdb_users")
public class EBean {
    public static enum Column {
        playername ("playername"),
        linkednames ("linkednames"),
        password ("password"),
        email ("email"),
        sessiontime ("sessiontime"),
        salt ("salt"),
        inventory ("inventory"),
        armorinventory ("armorinventory"),
        activated ("activated"),
        authorized ("authorized"),
        timeout ("timeout"),
        reloadtime ("reloadtime"),
        registred ("registred"),
        ip ("ip");

        private String name;
        Column(String name) {
            this.name = name;
        }
    }

    public static EBean checkPlayer(String player, boolean save) {
        EBean eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player).findUnique();
        if (eBeanClass == null)  {
            eBeanClass = new EBean();
            eBeanClass.setPlayername(player);
            eBeanClass.setRegistred("false");
            if(save) { save(eBeanClass); }
        }
        return eBeanClass;
    }

    public static EBean checkPlayer(Player player, boolean save) {
        EBean eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player.getName()).findUnique();
        if (eBeanClass == null)  {
            eBeanClass = new EBean();
            eBeanClass.setPlayer(player);
            eBeanClass.setRegistred("false");
            if(save) { save(eBeanClass); }
        }
        return eBeanClass;
    }

    public static void save(EBean eBeanClass) {
        AuthDB.database.save(eBeanClass);
    }

    public static void sync(Player player) {
        try {
            EBean eBeanClass = checkPlayer(player.getName(), true);
            String registred = eBeanClass.getRegistred();
            if (!Util.checkScript("checkuser", Config.script_name, player.getName(), null, null, null)) {
                if (registred != null && registred.equalsIgnoreCase("true")) {
                    Util.logging.Debug("Registred value for " + player.getName() + " in persistence is different than in MySQL, syncing registred value from MySQL.");
                    eBeanClass.setRegistred("false");
                    save(eBeanClass);
                    registred = "false";
                }
            }
            if (registred != null && registred.equalsIgnoreCase("true")) {
                Util.checkScript("syncpassword", Config.script_name, player.getName(), null, null, null);
                Util.checkScript("syncsalt", Config.script_name, player.getName(), null, null, null);
            }
        }
        catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
    }
    
    public static void checkSessiontime(String player, long sessiontime) {
        EBean eBeanClass = checkPlayer(player, true);
        if (eBeanClass.getSessiontime() == 0 || eBeanClass.getSessiontime() != sessiontime) {
            Util.logging.Debug("Session time in persistence is different than in hashmap, syncing session from hashmap.");
            eBeanClass.setSessiontime(sessiontime);
            AuthDB.database.save(eBeanClass);
        }
    }

    public static void checkPassword(String player, String password) {
        EBean eBeanClass = checkPlayer(player, true);
        if (eBeanClass.getPassword() == null || eBeanClass.getPassword().equals(password) == false) {
            Util.logging.Debug("Password in persistence is different than in MySQL, syncing password from MySQL.");
            eBeanClass.setPassword(password);
            AuthDB.database.save(eBeanClass);
        }
    }

    public static void checkSalt(String player, String salt) {
        EBean eBeanClass = checkPlayer(player, true);
        if (eBeanClass.getSalt() == null || eBeanClass.getSalt().equals(salt) == false) {
            Util.logging.Debug("Salt in persistence is different than in MySQL, syncing salt from MySQL.");
            eBeanClass.setSalt(salt);
            AuthDB.database.save(eBeanClass);
        }
    }

    public static void checkIP(String player, String IP) {
        EBean eBeanClass = checkPlayer(player, true);
        if (eBeanClass.getIp() == null || eBeanClass.getIp().equals(IP) == false) {
            Util.logging.Debug("IP in persistence is different than the player's IP, syncing IP's.");
            eBeanClass.setIp(IP);
            AuthDB.database.save(eBeanClass);
        }
    }
    
    public static int getUsers() {
        List<EBean> amount = AuthDB.database.find(EBean.class).findList();
        if (amount.isEmpty()) { return 0; }
        return amount.size();
    }

    public static EBean find(String player) {
        EBean eBeanClass = checkPlayer(player, true);
        eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player).findUnique();
        return eBeanClass;
    }

    public static EBean find(Player player) {
        EBean eBeanClass = checkPlayer(player, true);
        eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player.getName()).findUnique();
        return eBeanClass;
    }

    public static EBean find(Player player, Column column1, String value1) {
        EBean eBeanClass = checkPlayer(player, true);
        eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player.getName()).ieq(column1.name,value1).findUnique();
        return eBeanClass;
    }

    public static boolean find(Player player, Column column1, String value1, Column column2, String value2) {
        EBean eBeanClass = checkPlayer(player, true);
        eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player.getName()).ieq(column1.name,value1).ieq(column2.name,value2).findUnique();
        if (eBeanClass != null) {
            return true;
        }
        return false;
    }

    public static EBean find(String player, Column column1, String value1) {
        EBean eBeanClass = checkPlayer(player, true);
        eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player).ieq(column1.name,value1).findUnique();
        return eBeanClass;
    }

    public static boolean find(String player, Column column1, String value1, Column column2, String value2) {
        EBean eBeanClass = checkPlayer(player, true);
        eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player).ieq(column1.name,value1).ieq(column2.name,value2).findUnique();
        if (eBeanClass != null) {
            return true;
        }
        return false;
    }

    @Id
    private int id;
    @NotNull
    private String playername;
    private String linkedname;
    private String password;
    private String salt;
    private String ip;
    private String email;
    private String inventory;
    private String armorinventory;
    private String activated;
    private String registred;
    private String authorized;
    private int timeoutid;
    private long reloadtime;
    private long sessiontime;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String ply) {
        this.playername = ply;
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(playername);
    }

    public String getEmail() {
       return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPlayer(Player player) {
        this.playername = player.getName();
    }

    public String getAuthorized() {
        return authorized;
    }

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }

    public long getReloadtime() {
        return reloadtime;
    }

    public void setReloadtime(long reloadtime) {
        if(reloadtime != 0) { this.reloadtime = reloadtime; }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLinkedname() {
        return linkedname;
    }

    public void setLinkedname(String linkedname) {
        this.linkedname = linkedname;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inv) {
        this.inventory = inv;
    }

    public String getRegistred() {
        return registred;
    }

    public void setRegistred(String registred) {
        this.registred = registred;
    }

    public int getTimeoutid() {
        return timeoutid;
    }

    public void setTimeoutid(int timeoutid) {
        if(timeoutid != 0) { this.timeoutid = timeoutid; }
    }
    
    public long getSessiontime() {
        return sessiontime;
    }

    public void setSessiontime(long sessiontime) {
        this.sessiontime = sessiontime; 
    }

    public String getArmorinventory() {
        return armorinventory;
    }

    public void setArmorinventory(String armorinv) {
        this.armorinventory = armorinv;
    }
}
