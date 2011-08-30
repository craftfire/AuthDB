/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.scripts.forum;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.authdb.util.Config;
import com.authdb.util.encryption.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;
import com.authdb.util.databases.EBean;

public class MyBB {

    public static String VersionRange = "1.6.0-1.6.4";
    public static String LatestVersionRange = VersionRange;
    public static String Name = "mybb";
    public static String ShortName = "mybb";

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
        if (checkid == 1) {
            long timestamp = System.currentTimeMillis()/1000;
            String passwordhashed = hash(password);
            String randomkey = Util.getRandomString2(12, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
            java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
            java.sql.Time sqlTime = new java.sql.Time(new java.util.Date().getTime());
            String regtime = sqlDate + " " + sqlTime;
            //
            PreparedStatement ps;
            //
            Util.logging.mySQL("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`user_login`, `user_pass`, `user_nicename`, `user_email`, `user_registered`, `user_status`, `display_name`, `user_url`)  VALUES ('" + player + "', '" + passwordhashed + "', '" + player.toLowerCase() + "', '" + email + "', '" + regtime + "', '0', '" + player + "', '')");
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`user_login`, `user_pass`, `user_nicename`, `user_email`, `user_registered`, `user_status`, `display_name`, `user_url`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setString(1, player); //user_login
            ps.setString(2, passwordhashed); // user_pass
            ps.setString(3, player.toLowerCase()); //user_nicename
            ps.setString(4, email); //user_email
            ps.setString(5, regtime); //user_registered
            ps.setInt(6, 0); //user_status
            ps.setString(7, player); //display_name
            //need to add these, it's complaining about not default is set.
            ps.setString(8, ""); //user_url
            ps.executeUpdate();
            ps.close();
    
            int userid = MySQL.countitall(Config.script_tableprefix + "users");
            Util.logging.mySQL("INSERT INTO `" + Config.script_tableprefix + "usermeta" + "` (`user_id`, `meta_key`, `meta_value`)  VALUES ('" + userid + "', '" + Config.script_tableprefix + "capabilities', 'a:1:{s:6:\"member\";b:1;}')");
            ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "usermeta" + "` (`user_id`, `meta_key`, `meta_value`)  VALUES (?, ?, ?)", 1);
            ps.setInt(1, userid); //user_id
            ps.setString(2, Config.script_tableprefix + "capabilities"); //meta_key
            ps.setString(3, "a:1:{s:6:\"member\";b:1;}"); //meta_value
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

          for (int i = 0; i < count; i+=16) {
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
          output.append(itoa64.charAt(Math.min(iteration_count_log2
                 + ((PHP_VERSION >= 5) ? 5 : 3), 30)));
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
