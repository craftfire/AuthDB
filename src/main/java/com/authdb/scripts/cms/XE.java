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
package com.authdb.scripts.cms;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.authdb.util.Config;
import com.authdb.util.Util;
import com.authdb.util.encryption.Encryption;
import com.authdb.util.databases.MySQL;

public class XE {
    public static String Name = "xpressengine";
    public static String ShortName = "xe";
    public static String VersionRange = "1.0.3-1.0.3";
    public static String LatestVersionRange = VersionRange;

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
        long timestamp = System.currentTimeMillis()/1000;
        if (checkid == 1) {
            String hash = Encryption.md5(password);
            //
            PreparedStatement ps;
            //
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`name`,`pass`,`mail`,`created`,`access`,`login`,`status`,`init`)  VALUES (?,?,?,?,?,?,?,?)", 1);
            ps.setString(1, player); //name
            ps.setString(2, hash); //pass
            ps.setString(3, email); //mail
            ps.setLong(4, timestamp); //created
            ps.setLong(5, timestamp); //access
            ps.setLong(6, timestamp); //login
            ps.setInt(7, 1); //status
            ps.setString(8, email); //init
            // TODO: need to add these, it's complaining about default is not set.
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
        }
        /* else if (check(2)) {
            String hash = hash(player,password);
            int userid;
            //
            PreparedStatement ps;
            //
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`name`,`pass`,`mail`,`created`,`login`,`status`,`init`)  VALUES (?,?,?,?,?,?,?)", 1);
            ps.setString(1, player); //name
            ps.setString(2, hash); //pass
            ps.setString(3, email); //mail
            ps.setLong(4, timestamp); //created
            ps.setLong(5, timestamp); //login
            ps.setInt(6, 1); //status
            ps.setString(7, email); //init
            ///need to add these, it's complaining about not default is set.
            ps.executeUpdate();
        } */
    }

    public static boolean check_hash(String passwordhash, String hash) {
        if (passwordhash.equals(hash)) {
            return true;
        } else {
            return false;
        }
    }
}
