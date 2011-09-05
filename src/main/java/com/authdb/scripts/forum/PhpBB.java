/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.scripts.forum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.authdb.util.Config;
import com.authdb.util.encryption.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

/**
 * Port of phpBB3 password handling to Java.
 * See phpBB3/includes/functions.php
 * Edited by Contex
 *
 * @author lars
 * @author Contex
 */
public class PhpBB {
    public static String Name = "phpbb";
    public static String ShortName = "phpbb";
    public static String VersionRange = "3.0.0-3.0.8";
    public static String VersionRange2 = "2.0.0-2.0.23";
    public static String LatestVersionRange = VersionRange;

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
        if (checkid==1) {
            String hash = phpbb_hash(password);
            long timestamp = System.currentTimeMillis()/1000;
            int userid;
            //
            PreparedStatement ps;
            //
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`username`, `username_clean`, `user_password`, `user_email`, `group_id`, `user_timezone`, `user_dst`, `user_lang`, `user_type`, `user_regdate`, `user_new`, `user_lastvisit`, `user_permissions`, `user_sig`, `user_occ`, `user_interests`, `user_ip`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setString(1, player);
            ps.setString(2, player.toLowerCase());
            ps.setString(3, hash);
            ps.setString(4, email);
            ps.setString(5, "2"); // group
            ps.setString(6, "0.00"); // timezone
            ps.setString(7, "0"); // dst
            ps.setString(8, "en"); // lang
            ps.setString(9, "0"); // user_type
            ps.setLong(10, timestamp); // user_regdate
            ps.setString(11, "1"); // usernew
            ps.setLong(12, timestamp); // user_lastvisit
            // TODO: Need to add these, it's complaining about default is not set.
            ps.setString(13, ""); // user_permissions
            ps.setString(14, ""); // user_sig
            ps.setString(15, ""); // user_occ
            ps.setString(16, ""); // user_interests
            //
            ps.setString(17, ipAddress); // user_ip
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            userid = MySQL.countitall(Config.script_tableprefix + "users");

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user_group" + "` (`group_id`, `user_id`, `group_leader`, `user_pending`)  VALUES (?, ?, ?, ?)", 1);
            ps.setInt(1, 2);
            ps.setInt(2, userid);
            ps.setInt(3, 0);
            ps.setInt(4, 0);
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user_group" + "` (`group_id`, `user_id`, `group_leader`, `user_pending`)  VALUES (?, ?, ?, ?)", 1);
            ps.setInt(1, 7);
            ps.setInt(2, userid);
            ps.setInt(3, 0);
            ps.setInt(4, 0);
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "config" + "` SET `config_value` = '" + userid + "' WHERE `config_name` = 'newest_user_id'");
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "config" + "` SET `config_value` = '" + player + "' WHERE `config_name` = 'newest_username'");
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();
            Util.logging.mySQL(ps.toString());
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "config" + "` SET `config_value` = config_value + 1 WHERE `config_name` = 'num_users'");
            ps.executeUpdate();
            ps.close();
        } else if (checkid == 2) {
            String hash = Encryption.md5(password);
            long timestamp = System.currentTimeMillis()/1000;
            //
            PreparedStatement ps;
            //
            int userid = 0;

            String query = "SELECT user_id FROM `" + Config.script_tableprefix + "users" + "` ORDER BY `user_id` DESC LIMIT 0, 1";
            Statement stmt = MySQL.mysql.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                userid = rs.getInt(1);
            } else {
                Util.logging.error("Could not get the latest user ID from users table, ERROR!");
            }
            rs.close();
            stmt.close();
            userid += 1;

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`user_active`, `username`, `user_password`, `user_lastvisit`, `user_regdate`, `user_email`, `user_id`)  VALUES (?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setInt(1, 1); // user_active
            ps.setString(2, player.toLowerCase()); // username
            ps.setString(3, hash); // user_password
            ps.setLong(4, timestamp); // user_lastvisit
            ps.setLong(5, timestamp); // user_regdate
            ps.setString(6, email); // user_email
            ps.setInt(7, userid); // user_id
            //
            Util.logging.mySQL(ps.toString());
            ps.executeUpdate();
            ps.close();

            userid = MySQL.countitall(Config.script_tableprefix + "users");

            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user_group" + "` (`group_id`, `user_id`, `user_pending`)  VALUES (?, ?, ?)", 1);
            ps.setInt(1, 3);
            ps.setInt(2, userid);
            ps.setInt(3, 0);
            ps.executeUpdate();
            Util.logging.mySQL(ps.toString());
            ps.close();
            stmt.close();

            /*
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "config" + "` SET `config_value` = '" + userid + "' WHERE `config_name` = 'newest_user_id'");
            ps.executeUpdate();
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "config" + "` SET `config_value` = '" + player + "' WHERE `config_name` = 'newest_username'");
            ps.executeUpdate();
            ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "config" + "` SET `config_value` = config_value + 1 WHERE `config_name` = 'num_users'");
            ps.executeUpdate();*/
        }
    }

    private static String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String phpbb_hash(String password) {
        String random_state = unique_id();
        StringBuffer random = new StringBuffer();
        int count = 6;
        String temp = "";

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
        StringBuffer output = new StringBuffer("$H$");
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
        if (!setting.substring(0, 3).equals("$H$")) {
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

        return output;
    }

    public static boolean check_hash(String password, String hash) {
        if (hash.length() == 34) {
            return _hash_crypt_private(password, hash).equals(hash);
        } else {
            return Encryption.md5(password).equals(hash);
        }
    }
}
