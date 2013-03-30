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
package com.craftfire.authdb.scripts.cms;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.craftfire.authdb.util.Config;
import com.craftfire.authdb.util.encryption.Encryption;
import com.craftfire.authdb.util.Util;
import com.craftfire.authdb.util.databases.MySQL;

public class DLE {
    public static String Name = "datalifeengine";
    public static String ShortName = "dle";
    public static String VersionRange = "9.2-9.2"; // TODO: Check version 9.3 for changes and add support for.
    public static String LatestVersionRange = VersionRange;

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
        if (checkid == 1) {
            long timestamp = System.currentTimeMillis() / 1000;
            String hash = hash(password);
            PreparedStatement ps;
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`email`, `password`, `name`, `lastdate`, `reg_date`, `logged_ip`, `info`, `signature`, `favorites`, `xfields`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setString(1, email); // email
            ps.setString(2, hash); // password
            ps.setString(3, player); // name
            ps.setLong(4, timestamp); // lastdate
            ps.setLong(5, timestamp); // reg_date
            ps.setString(6, ipAddress); // logged_ip
            // TODO: Need to add these, it's complaining about default is not set.
            ps.setString(7, ""); // info
            ps.setString(8, ""); // signature
            ps.setString(9, ""); // favorites
            ps.setString(10, ""); // xfields
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
        }
    }

    public static String hash(String password) throws SQLException {
        try {
            return passwordHash(password);
        } catch (NoSuchAlgorithmException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        } catch (UnsupportedEncodingException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
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

    public static String passwordHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return Encryption.md5(Encryption.md5(password));
    }
}
