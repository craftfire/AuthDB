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

import com.authdb.util.Config;
import com.authdb.util.Util;
import com.authdb.util.databases.EBean;
import com.authdb.util.databases.MySQL;
import com.authdb.util.encryption.Encryption;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class WBB {
    public static String Name = "wolflab burning board";
    public static String ShortName = "wbb";
    public static String VersionRange = "2.1.0-2.1.0";
    public static String LatestVersionRange = VersionRange;

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
        if (checkid == 1) {
            long timestamp = System.currentTimeMillis()/1000;
            //
            PreparedStatement ps;
            //
            String salt = null;
            try {
                salt = Encryption.SHA1(timestamp + UUID.randomUUID().toString());
            } catch (NoSuchAlgorithmException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            } catch (UnsupportedEncodingException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }

            String passwordhashed = hash("create", player, password, salt);

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user" + "` (`username`, `email`, `password`, `salt`, `registrationDate`, `registrationIpAddress`, `rankID`)  VALUES (?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setString(1, player); //username
            ps.setString(2, email); //email
            ps.setString(3, passwordhashed); //password
            ps.setString(4, salt); //salt
            ps.setLong(5, timestamp); //registrationDate
            ps.setString(6, ipAddress); //registrationIpAddress
            ps.setInt(7, 4); //RankID
            ps.executeUpdate();
            Util.logging.mySQL(ps.toString());
            ps.close();

            int userid = MySQL.countitall(Config.script_tableprefix + "user");

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefixextra + "user" + "` (`userID`, `boardLastVisitTime`, `boardLastActivityTime`)  VALUES (?, ?, ?)", 1);
            ps.setInt(1, userid); //userID
            ps.setLong(2, timestamp); //boardLastVisitTime
            ps.setLong(3, timestamp); //boardLastActivityTime
            ps.executeUpdate();
            Util.logging.mySQL(ps.toString());
            ps.close();

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix  + "user_option_value` (`userID`, `userOption1`, `userOption2`, `userOption3`, `userOption4`, `userOption5`, `userOption16`, `userOption18`, `userOption19`, `userOption20`, `userOption22`, `userOption23`, `userOption24`, `userOption25`, `userOption27`, `userOption28`, `userOption29`, `userOption30`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setInt(1, userid); // userID
            ps.setInt(2, 1); // userOption1
            ps.setInt(3, 1); // userOption2
            ps.setInt(4, 1); // userOption3
            ps.setInt(5, 1); // userOption4
            ps.setInt(6, 1); // userOption5
            ps.setInt(7, 1); // userOption16
            ps.setInt(8, 1); // userOption18
            ps.setInt(9, 1); // userOption19
            ps.setInt(10, 1); // userOption20
            ps.setInt(11, 1); // userOption22
            ps.setInt(12, 1); // userOption23
            ps.setInt(13, 1); // userOption24
            ps.setInt(14, 1); // userOption25
            ps.setInt(15, 1); // userOption27
            ps.setInt(16, 1); // userOption28
            ps.setInt(17, 1); // userOption29
            ps.setInt(18, 1); // userOption30
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix  + "user_to_groups` (`userID`, `groupID`)  VALUES (?, ?)", 1);
            ps.setInt(1, userid); // userID
            ps.setInt(2, 1); // groupID
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix  + "user_to_groups` (`userID`, `groupID`)  VALUES (?, ?)", 1);
            ps.setInt(1, userid); // userID
            ps.setInt(2, 2); // groupID
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
        }
    }

    public static String hash(String action, String player, String password, String salt) {
        if (action.equalsIgnoreCase("find")) {
            try {
                EBean eBeanClass = EBean.checkPlayer(player, true);
                String StoredSalt = eBeanClass.getSalt();
                return Encryption.SHA1(StoredSalt + Encryption.SHA1(StoredSalt + Encryption.SHA1(password)));
            } catch (NoSuchAlgorithmException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            } catch (UnsupportedEncodingException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
        } else if (action.equalsIgnoreCase("create")) {
            try {
                return Encryption.SHA1(salt + Encryption.SHA1(salt + Encryption.SHA1(password)));
            } catch (NoSuchAlgorithmException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            } catch (UnsupportedEncodingException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
        }
        
       /* try {
            return Encryption.SHA1(salt + Encryption.SHA1(salt + Encryption.SHA1(password)));
        } catch (NoSuchAlgorithmException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        } catch (UnsupportedEncodingException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }*/
        return "fail";
    }

    public static boolean check_hash(String passwordhash, String hash) {
        return passwordhash.equals(hash);
    }

}
