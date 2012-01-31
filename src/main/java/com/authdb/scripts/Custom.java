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
package com.authdb.scripts;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.authdb.util.Config;
import com.authdb.util.encryption.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class Custom {
    public static void adduser(String player, String email, String password, String ipAddress) throws SQLException {
        if (!Config.database_keepalive) { 
            Util.databaseManager.connect();
        }
        PreparedStatement ps;
        if (Config.custom_encryption != null) {
            try {
                password = Encryption.encrypt(Config.custom_encryption, password);
            } catch (NoSuchAlgorithmException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            } catch (UnsupportedEncodingException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
        }
        //
        String query;
        if (Config.custom_emailrequired) {
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.custom_table + "` (`" + Config.custom_userfield + "`,`" + Config.custom_passfield + "`,`" + Config.custom_emailfield + "`)  VALUES (?,?,?)", 1);
            ps.setString(1, player); //username
            ps.setString(2, password); // password
            ps.setString(3, email); // email
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
        } else {
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.custom_table + "` (`" + Config.custom_userfield + "`,`" + Config.custom_passfield + "`)  VALUES (?,?)", 1);
            ps.setString(1, player); //username
            ps.setString(2, password); // password
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
        }
        if (!Config.database_keepalive) { 
            Util.databaseManager.close();
        }
    }

    public static String hash(String player, String password) {
        return password;
    }

    public static boolean check_hash(String passwordhash, String hash) {
        try {
            if (Encryption.encrypt(Config.custom_encryption, passwordhash).equals(hash)) {
                return true;
            }
        } catch (NoSuchAlgorithmException e) {
        Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
    } catch (UnsupportedEncodingException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return false;
    }

    public static String saltIt(String password) {
        return password;
    }
}
