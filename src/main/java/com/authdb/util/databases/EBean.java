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
package com.authdb.util.databases;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Util;
import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.sql.SQLException;
import java.util.List;

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
        equipment ("equipment"),
        activated ("activated"),
        authorized ("authorized"),
        timeout ("timeout"),
        reloadtime ("reloadtime"),
        registered ("registered"),
        ip ("ip");

        private String name;
        Column(String name) {
            this.name = name;
        }
    }

    public static EBean checkPlayer(String player, boolean save) {
        EBean eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player).findUnique();
        if (eBeanClass == null) {
            eBeanClass = new EBean();
            eBeanClass.setPlayername(player);
            eBeanClass.setRegistered("false");
            if (save) { save(eBeanClass); }
            sync(player);
        }
        return eBeanClass;
    }

    public static EBean checkPlayer(Player player, boolean save) {
        EBean eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player.getName()).findUnique();
        if (eBeanClass == null) {
            eBeanClass = new EBean();
            eBeanClass.setPlayer(player);
            eBeanClass.setRegistered("false");
            if (save) { save(eBeanClass); }
            sync(player);
        }
        return eBeanClass;
    }

    public static void save(EBean eBeanClass) {
        AuthDB.database.save(eBeanClass);
    }
    
    public static void sync(Player player) {
        sync(player.getName());
    }

    public static void sync(String player) {
        try {
            Util.logging.Debug("Running Sync for user: " + player);
            if (!Config.database_keepalive) {
                Util.databaseManager.connect();
            }
            EBean eBeanClass = EBean.checkPlayer(player, true);
            String registered = eBeanClass.getRegistered();
            if (!Util.checkOtherName(player).equals(player)) {
                eBeanClass.setRegistered("true");
                AuthDB.database.save(eBeanClass);
                registered = "true";
            } else if (Util.checkScript("checkuser", Config.script_name, Util.checkOtherName(player), null, null, null)) {
                eBeanClass.setRegistered("true");
                AuthDB.database.save(eBeanClass);
                registered = "true";
            } else {
                if (registered != null && registered.equalsIgnoreCase("true")) {
                    Util.logging.Debug("Registered value for " + player + " in persistence is different than in MySQL, syncing registered value from MySQL.");
                    eBeanClass.setRegistered("false");
                    AuthDB.database.save(eBeanClass);
                    registered = "false";
                }
            }
            if (registered != null && registered.equalsIgnoreCase("true")) {
                Util.checkScript("syncpassword", Config.script_name, Util.checkOtherName(player), null, null, null);
                Util.checkScript("syncsalt", Config.script_name, Util.checkOtherName(player), null, null, null);
            }
            if (!Config.database_keepalive) {
                Util.databaseManager.close();
            }
        } catch (SQLException e) {
            //Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
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
            Util.logging.Debug("IP in persistence is different than the player's IP, removing session and syncing IP's.");
            eBeanClass.setSessiontime(0);
            eBeanClass.setIp(IP);
            AuthDB.database.save(eBeanClass);
        }
    }
    
    public static int getUsers() {
        List<EBean> amount = AuthDB.database.find(EBean.class).findList();
        if (amount.isEmpty()) {
            return 0;
        }
        return amount.size();
    }

    public static int getAmount(String field, String value) {
        List<EBean> amount = AuthDB.database.find(EBean.class).where().ieq(field, value).findList();
        Util.logging.Debug("Found " + amount.size() + " results for value " + value + " in field " + field);
        if (amount.isEmpty()) {
            return 0;
        }
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
        eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player.getName()).ieq(column1.name, value1).findUnique();
        return eBeanClass;
    }

    public static boolean find(Player player, Column column1, String value1, Column column2, String value2) {
        EBean eBeanClass = checkPlayer(player, true);
        eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player.getName()).ieq(column1.name, value1).ieq(column2.name, value2).findUnique();
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
        eBeanClass = AuthDB.database.find(EBean.class).where().ieq("playername", player).ieq(column1.name, value1).ieq(column2.name, value2).findUnique();
        if (eBeanClass != null) {
            return true;
        }
        return false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotNull
    private String playername;
    private String linkedname;
    private String password;
    private String salt;
    private String ip;
    private String email;
    @Length(max = 600)
    private String inventory;
    private String equipment;
    private String activated;
    private String registered;
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
        if (reloadtime != 0) {
            this.reloadtime = reloadtime;
        }
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

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public int getTimeoutid() {
        return timeoutid;
    }

    public void setTimeoutid(int timeoutid) {
        if (timeoutid != 0) {
            this.timeoutid = timeoutid;
        }
    }
    
    public long getSessiontime() {
        return sessiontime;
    }

    public void setSessiontime(long sessiontime) {
        this.sessiontime = sessiontime; 
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
}
