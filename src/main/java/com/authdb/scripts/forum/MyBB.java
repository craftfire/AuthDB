/*
 * This file is part of AuthDB <http://www.authdb.com/>.
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
package com.authdb.scripts.forum;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.authdb.util.Config;
import com.authdb.util.encryption.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;
import com.authdb.util.databases.EBean;

public class MyBB {
    public static String Name = "mybb";
    public static String ShortName = "mybb";
    public static String VersionRange = "1.6.0-1.6.4";
    public static String LatestVersionRange = VersionRange;

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
    if (checkid == 1) {
        long timestamp = System.currentTimeMillis()/1000;
        String salt = Encryption.hash(8, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 0, 0);
        String hash = hash("create", player, password, salt);
        //
        PreparedStatement ps;
        //
        ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`username`, `password`, `salt`, `email`, `regdate`, `lastactive`, `lastvisit`, `regip`, `longregip`, `signature`, `buddylist`, `ignorelist`, `pmfolders`, `notepad`, `usernotes`, `usergroup`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
        ps.setString(1, player); // username
        ps.setString(2, hash); // password
        ps.setString(3, salt); // salt
        ps.setString(4, email); // email
        ps.setLong(5, timestamp); // regdate
        ps.setLong(6, timestamp); // lastactive
        ps.setLong(7, timestamp); // lastvisit
        ps.setString(8, ipAddress); // regip
        ps.setLong(9, Util.ip2Long(ipAddress));
        // TODO: Need to add these, it's complaining about default is not set.
        ps.setString(10, ""); // signature
        ps.setString(11, ""); // buddylist
        ps.setString(12, ""); // ignorelist
        ps.setString(13, ""); // pmfolders
        ps.setString(14, ""); // notepad
        ps.setString(15, ""); // usernotes
        ps.setString(16, "2"); // usergroup
        Util.logging.mySQL(ps.toString());
        ps.executeUpdate();
        ps.close();

        int userid = MySQL.countitall(Config.script_tableprefix + "users");
        String oldcache =  MySQL.getfromtable(Config.script_tableprefix + "datacache", "`cache`", "title", "stats");
        String newcache = Util.forumCache(oldcache, player, userid, "numusers", null, "lastusername", "lastuid", null, "lastusername");
        ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "datacache" + "` SET `cache` = '" + newcache + "' WHERE `title` = 'stats'");
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
        return Encryption.md5(Encryption.md5(salt) + Encryption.md5(password));
    }
}
