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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.authdb.util.Config;
import com.authdb.util.encryption.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class Vanilla {
    public static String Name = "vanilla";
    public static String ShortName = "van";
    public static String VersionRange = "2.0.17.8-2.0.18.2";
    public static String LatestVersionRange = VersionRange;
    public static int extraCheck = 0;

    public static int check() {
        if (extraCheck > 0) { return extraCheck; }
        if (Config.script_tableprefix.equalsIgnoreCase("gdn_")) {
            String check = MySQL.getQuery("SELECT * FROM `GDN_User` LIMIT 1");
            if (check.equalsIgnoreCase("fail")) {
                check = MySQL.getQuery("SELECT * FROM `gdn_user` LIMIT 1");
                if (check.equalsIgnoreCase("fail")) {
                    extraCheck = 0;
                } else {
                    Config.script_tableprefix = "gdn_";
                    extraCheck = 2;
                    return 2;
                }
            } else {
                Config.script_tableprefix = "GDN_";
                extraCheck = 1;
                return 1;
            }
        } else {
            extraCheck = 2;
            return 2;
        }
        return extraCheck;
    }

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
        long timestamp = System.currentTimeMillis()/1000;
        String usertable = null, roletable = null;
        if (checkid == 1) {
            usertable = "User";
            roletable = "UserRole";
        } else if (checkid == 2) {
            usertable = "user";
            roletable = "userrole";
        }

        if (checkid == 1 || checkid == 2) {
            String passwordhashed = hash(password);
            String randomkey = Util.getRandomString2(12, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
            String realdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date (timestamp*1000));
            PreparedStatement ps;
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + usertable + "` (`Name`, `Password`, `HashMethod`, `Email`, `Gender`, `Preferences`, `Permissions`, `Attributes`, `DateFirstVisit`, `DateLastActive`, `DateInserted`, `DateUpdated`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setString(1, player); // Name
            ps.setString(2, passwordhashed); // Password
            ps.setString(3, "Vanilla"); // HashMethod
            ps.setString(4, email); // Email
            ps.setString(5, "m"); // Gender
            ps.setString(6, "a:1:{s:13:\"Authenticator\";s:8:\"password\";}"); // Preferences
            ps.setString(7, "a:9:{i:0;s:19:\"Garden.SignIn.Allow\";i:1;s:20:\"Garden.Activity.View\";i:2;s:20:\"Garden.Profiles.View\";i:3;s:24:\"Vanilla.Discussions.View\";i:4;s:23:\"Vanilla.Discussions.Add\";i:5;s:20:\"Vanilla.Comments.Add\";s:24:\"Vanilla.Discussions.View\";a:1:{i:0;i:-1;}s:23:\"Vanilla.Discussions.Add\";a:1:{i:0;i:-1;}s:20:\"Vanilla.Comments.Add\";a:1:{i:0;i:-1;}}"); //Permissions
            ps.setString(8, "a:1:{s:12:\"TransientKey\";s:12:\"" + randomkey + "\";}"); // Attributes
            ps.setString(9, realdate); // DateFirstVisit
            ps.setString(10, realdate); // DateLastActive
            ps.setString(11, realdate); // DateInserted
            ps.setString(12, realdate); // DateUpdated
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            int userid = MySQL.countitall(Config.script_tableprefix + usertable);

            //ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + roletable + "` (`UserID`, `RoleID`)  VALUES (?, ?)", 1);
            //ps.setInt(1, userid); //UserID
            //ps.setInt(2, 3); //RoleID
            //Util.logging.mySQL(ps.toString());
            //ps.executeUpdate();
            //ps.close();

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + roletable + "` (`UserID`, `RoleID`)  VALUES (?, ?)", 1);
            ps.setInt(1, userid); // UserID
            ps.setInt(2, 8); // RoleID
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
        }
    }

    private static String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String hash(String password) {
        String random_state = unique_id();
        StringBuffer random = new StringBuffer();
        String temp = "";
        int count = 6;

        if (random.length() < count) {
            for (int i = 0; i < count; i += 16) {
                random_state = Encryption.md5(unique_id() + random_state);
                random.append(Encryption.pack(Encryption.md5(random_state)));
            }
            temp = random.toString().substring(0, count);
        }

        String hash = _hash_crypt_private(password, _hash_gensalt_private(temp, itoa64));
        if (hash.length() == 34) {
            return hash;
        }
        return Encryption.md5(password);
    }

    private static String unique_id() {
        return "1234567890abcdef";
    }

    private static String _hash_gensalt_private(String input, String itoa64) {
        return _hash_gensalt_private(input, itoa64, 6);
    }

    private static String _hash_gensalt_private(String input, String itoa64, int iteration_count_log2) {
        if (iteration_count_log2 < 4 || iteration_count_log2 > 31) {
            iteration_count_log2 = 8;
        }

        int PHP_VERSION = 5;
        StringBuffer output = new StringBuffer("$P$");
        output.append(itoa64.charAt(Math.min(iteration_count_log2 + ((PHP_VERSION >= 5) ? 5 : 3), 30)));
        output.append(_hash_encode64(input, 6));

        return output.toString();
    }

    /**
     * Encode hash
     */
    private static String _hash_encode64(String input, int count) {
        StringBuffer output = new StringBuffer();
        int i = 0;

        do {
            int value = input.charAt(i++);
            output.append(itoa64.charAt(value & 0x3f));

            if (i < count) {
                value |= input.charAt(i) << 8;
            }

            output.append(itoa64.charAt((value >> 6) & 0x3f));

            if (i++ >= count) {
                break;
            }

            if (i < count) {
                value |= input.charAt(i) << 16;
            }

            output.append(itoa64.charAt((value >> 12) & 0x3f));

            if (i++ >= count) {
                break;
            }

            output.append(itoa64.charAt((value >> 18) & 0x3f));
        }
        while (i < count);

        return output.toString();
    }

    static String _hash_crypt_private(String password, String setting) {
        String output = "*";

        // Check for correct hash
        if (!setting.substring(0, 3).equals("$P$")) {
            return output;
        }

        int count_log2 = itoa64.indexOf(setting.charAt(3));
        if (count_log2 < 7 || count_log2 > 30) {
            return output;
        }

        int count = 1 << count_log2;
        String salt = setting.substring(4, 12);
        if (salt.length() != 8) {
            return output;
        }

        String m1 = Encryption.md5(salt + password);
        String hash = Encryption.pack(m1);
        do {
            hash = Encryption.pack(Encryption.md5(hash + password));
        }
        while (--count > 0);

        output = setting.substring(0, 12) + _hash_encode64(hash, 16);

        return output.toString();
    }

    public static boolean check_hash(String password, String hash) {
        if (hash.length() == 34) {
            return _hash_crypt_private(password, hash).equals(hash);
        } else {
            return Encryption.md5(password).equals(hash);
        }
    }
}
