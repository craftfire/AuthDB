/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.scripts.forum;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class XenForo {

    public static String Name = "xenforo";
    public static String ShortName = "xf";
    public static String VersionRange = "1.0.0-1.0.0";
    public static String LatestVersionRange = VersionRange;

  public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
    if (checkid == 1) {
        Random r = new Random();
        int randint = r.nextInt(1000000);
        String salt = Encryption.md5("" + randint);
        salt = Encryption.SHA256(salt.substring(0, 10));

        long timestamp = System.currentTimeMillis()/1000;
        //
        PreparedStatement ps;
        //
        String hash = hash(1, salt, password);

        ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user" + "` (`username`, `email`, `language_id`, `style_id`, `timezone`, `user_group_id`, `secondary_group_ids`, `display_style_group_id`, `permission_combination_id`, `register_date`, `last_activity`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
        ps.setString(1, player); //username
        ps.setString(2, email); //email
        ps.setInt(3, 1); //language_id
        ps.setInt(4, 0); //style_id
        ps.setString(5, "Europe/London"); //timezone
        ps.setInt(6, 2); //user_group_id
        ps.setString(7, ""); //secondary_group_ids - dupe
        ps.setInt(8, 2); //display_style_group_id
        ps.setInt(9, 2); //permission_combination_id
        ps.setLong(10, timestamp); //register_date
        ps.setLong(11, timestamp); //last_activity
        ///
        ps.executeUpdate();
        ps.close();

        int userid = MySQL.countitall(Config.script_tableprefix + "user");

        ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user_privacy" + "` (`user_id`, `allow_post_profile`, `allow_send_personal_conversation`)  VALUES (?, ?, ?)", 1);
        ps.setInt(1, userid); //user_id
        ps.setString(2, "members"); //allow_post_profile
        ps.setString(3, "members"); //allow_send_personal_conversation
        ps.executeUpdate();
        ps.close();

        ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user_option" + "` (`user_id`, `default_watch_state`, `alert_optout`)  VALUES (?, ?, ?)", 1);
        ps.setInt(1, userid); //user_id
        ps.setString(2, "watch_email"); //default_watch_state
        ps.setString(3, ""); //alert_optout
        ps.executeUpdate();
        ps.close();

        String stringdata1 = "a:0:{}";
        byte[] bArr1 = stringdata1.getBytes();
        ByteArrayInputStream bIn1 = new ByteArrayInputStream(bArr1);
        ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user_profile" + "` (`user_id`, `status`, `signature`, `homepage`, `following`, `identities`, `csrf_token`, `about`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?)", 1);
        ps.setInt(1, userid); //user_id
        ps.setString(2, ""); //status - dupe
        ps.setString(3, ""); //signature - dupe
        ps.setString(4, ""); //homepage - dupe
        ps.setString(5, "");  //following - dupe
        ps.setBlob(6, bIn1, bArr1.length); //identities
        ps.setString(7, ""); //csrf_token - dupe
        ps.setString(8, ""); //about - dupe
        ps.executeUpdate();
        ps.close();

        String stringdata = "a:3:{s:4:\"hash\";s:64:\"" + hash + "\";s:4:\"salt\";s:64:\"" + salt + "\";s:8:\"hashFunc\";s:6:\"sha256\";}";
        byte[] bArr = stringdata.getBytes();
        ByteArrayInputStream bIn = new ByteArrayInputStream(bArr);
        ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "user_authenticate" + "` (`user_id`, `scheme_class`, `data`, `remember_key`)  VALUES (?, ?, ?, ?)", 1);
        ps.setInt(1, userid); //user_id
        ps.setString(2, "XenForo_Authentication_Core"); //scheme_class
        ps.setBlob(3, bIn, bArr.length);
        ps.setString(4, ""); //remember_key
        ps.executeUpdate();
        ps.close();
    }
 }

  public static String hash(int checkid, String salt, String password) {
        if (checkid == 1) {
              try {
                return passwordHash(password, salt);
            } catch (NoSuchAlgorithmException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            } catch (UnsupportedEncodingException e) {
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
        } else if (checkid == 2) {
              try {
                return Encryption.SHA1(salt + password);
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
          return Encryption.SHA256(Encryption.SHA256(password) + salt);
      }
}
