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

public class PunBB {
    public static String Name = "punbb";
    public static String ShortName = "punbb";
    public static String VersionRange = "1.3.4-1.3.5";
    public static String LatestVersionRange = VersionRange;

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
        if (checkid == 1) {
            long timestamp = System.currentTimeMillis()/1000;
            //
            PreparedStatement ps;
            //
            String salt = Encryption.hash(12, "none", 33, 126);
            String hash = hash("create", player, password, salt);

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`group_id`, `username`, `password`, `salt`, `email`, `registered`, `registration_ip`, `last_visit`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setInt(1, 3); // group_id
            ps.setString(2, player); // username
            ps.setString(3, hash); // password
            ps.setString(4, salt); // salt
            ps.setString(5, email); // email
            ps.setLong(6, timestamp); // registered
            ps.setString(7, ipAddress); // registration_ip
            ps.setLong(8, timestamp); // last_visit
            //
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            /*
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "config" + "` SET `config_value` = '" + userid + "' WHERE `config_name` = 'newest_user_id'");
            ps.executeUpdate();
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "config" + "` SET `config_value` = '" + player + "' WHERE `config_name` = 'newest_username'");
            ps.executeUpdate();
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "config" + "` SET `config_value` = config_value + 1 WHERE `config_name` = 'num_users'");
            ps.executeUpdate();
            */
        }
    }

    public static String hash(String action, String player,String password, String thesalt) throws SQLException {
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
        return Encryption.SHA1(salt + Encryption.SHA1(password));
    }
}
