/*
 * This file is part of AuthDB.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
 * AuthDB is licensed under the GNU Lesser General Public License.
 *
 * AuthDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.authdb.scripts.forum;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.craftfire.authdb.util.Config;
import com.craftfire.authdb.util.encryption.Encryption;
import com.craftfire.authdb.util.Util;
import com.craftfire.authdb.util.databases.MySQL;
import com.craftfire.authdb.util.databases.EBean;

public class VBulletin {
    public static String Name = "vbulletin";
    public static String ShortName = "vb";
    public static String VersionRange = "3.0.0-3.8.7";
    public static String VersionRange2 = "4.0.0-4.1.5";
    public static String LatestVersionRange = VersionRange2;

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
        long timestamp = System.currentTimeMillis() / 1000;
        if (checkid == 1) {
            String salt = Encryption.hash(30, "none", 33, 126);
            String passwordhashed = hash("create", player, password, salt);
            String passworddate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date (timestamp*1000));
            PreparedStatement ps;
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user" + "` (`usergroupid`, `password`, `passworddate`, `email`, `showvbcode`, `joindate`, `lastvisit`, `lastactivity`, `reputationlevelid`, `options`, `ipaddress`, `salt`, `username`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setString(1, "2"); // usergroupid
            ps.setString(2, passwordhashed); // password
            ps.setString(3, passworddate); // passworddate
            ps.setString(4, email); // email
            ps.setString(5, "1"); // showvbcode
            ps.setLong(6, timestamp); // joindate
            ps.setLong(7, timestamp); // lastvisit
            ps.setLong(8, timestamp); // lastactivity
            ps.setString(9, "5"); // reputationlevelid
            ps.setString(10, "45108311"); //options
            ps.setLong(11, Util.ip2Long(ipAddress)); // ipaddress
            ps.setString(12, salt); // salt
            ps.setString(13, player); // username
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            int userid = MySQL.countitall(Config.script_tableprefix + "user");
            String oldcache =  MySQL.getfromtable(Config.script_tableprefix + "datastore", "`data`", "title", "userstats");
            String newcache = Util.forumCache(oldcache, player, userid, "numbermembers", "activemembers", "newusername", "newuserid", null, "newuserid");
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "datastore" + "` SET `data` = '" + newcache + "' WHERE `title` = 'userstats'");
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
        } else if (checkid == 2) {
            String salt = Encryption.hash(30, "none", 33, 126);
            String passwordhashed = hash("create", player, password, salt);
            String passworddate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date (timestamp*1000));
            //int userid;
            PreparedStatement ps;
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user" + "` (`usergroupid`, `password`, `passworddate`, `email`, `showvbcode`, `joindate`, `lastvisit`, `lastactivity`, `reputationlevelid`, `options`, `ipaddress`, `salt`, `username`, `usertitle`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setString(1, "2"); // usergroupid
            ps.setString(2, passwordhashed); // password
            ps.setString(3, passworddate); // passworddate
            ps.setString(4, email); // email
            ps.setString(5, "1"); // showvbcode
            ps.setLong(6, timestamp); // joindate
            ps.setLong(7, timestamp); // lastvisit
            ps.setLong(8, timestamp); // lastactivity
            ps.setString(9, "5"); // reputationlevelid
            ps.setString(10, "45091927"); // options
            ps.setString(11, ipAddress); // ipaddress
            ps.setString(12, salt); // salt
            ps.setString(13, player); // username
            ps.setString(14, "Junior Member"); // usertitle
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            int userid = MySQL.countitall(Config.script_tableprefix + "user");
            String oldcache =  MySQL.getfromtable(Config.script_tableprefix + "datastore", "`data`", "title", "userstats");
            String newcache = Util.forumCache(oldcache, player, userid, "numbermembers", "activemembers", "newusername", "newuserid", null, "newuserid");
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "datastore" + "` SET `data` = '" + newcache + "' WHERE `title` = 'userstats'");
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
        }
    }

    public static String hash(String action, String player, String password, String thesalt) throws SQLException {
        if (action.equalsIgnoreCase("find")) {
            try {
                EBean eBeanClass = EBean.checkPlayer(player, true);
                String StoredSalt = eBeanClass.getSalt();
                return passwordHash(password, StoredSalt);
            } catch (NoSuchAlgorithmException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            } catch (UnsupportedEncodingException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
        } else if (action.equalsIgnoreCase("create")) {
            try {
                return passwordHash(password, thesalt);
            } catch (NoSuchAlgorithmException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            } catch (UnsupportedEncodingException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
        }
        return "fail";
    }

    public static boolean check_hash(String passwordhash, String hash) {
        if (passwordhash.equals(hash)) {
            return true;
        } else {
            return false;
        }
    }

    public static String passwordHash(String password, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return Encryption.md5(Encryption.md5(password) + salt);
    }
}
