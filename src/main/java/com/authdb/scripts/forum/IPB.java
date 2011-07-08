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

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class IPB {

    public static String Name = "ipboard";
    public static String ShortName = "ipb";
    public static String VersionRange = "3.1.3-3.1.4";
    public static String LatestVersionRange = VersionRange;

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException
    {
        if(checkid == 1)
        {
            long timestamp = System.currentTimeMillis()/1000;
            String salt = Encryption.hash(5,"./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",0,0);
            String hash = hash("create",player,password, salt);
            //
            PreparedStatement ps;
            //
            ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.script_tableprefix+"members"+"` (`name`,`member_group_id`,`email`,`joined`,`ip_address`,`allow_admin_mails`,`last_visit`,`last_activity`,`ignored_users`,`members_display_name`,`members_seo_name`,`members_l_display_name`,`members_l_username`,`members_pass_hash`,`members_pass_salt`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
            ps.setString(1, player); //name
            ps.setInt(2, 3); // member_group_id
            ps.setString(3, email); //email
            ps.setLong(4, timestamp); //joined
            ps.setString(5, ipAddress); //ip_address
            ps.setInt(6, 1); //allow_admin_mails
            ps.setLong(7, timestamp); //last_visit
            ps.setLong(8, timestamp); //last_activity
            ps.setString(9, "a:0:{}"); //ignored_users
            ps.setString(10, player); //members_display_name
            ps.setString(11, player.toLowerCase()); //members_seo_name
            ps.setString(12, player); //members_l_display_name
            ps.setString(13, player.toLowerCase()); //members_l_username
            ps.setString(14, hash); //members_pass_hash
            ps.setString(15, salt); //members_pass_salt
            ps.executeUpdate();

            int userid = MySQL.countitall(Config.script_tableprefix+"members");
            ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.script_tableprefix+"pfields_content"+"` (`member_id`)  VALUES (?)", 1);
            ps.setInt(1, userid); //member_id
            ps.executeUpdate();
            ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.script_tableprefix+"profile_portal"+"` (`pp_member_id`)  VALUES (?)", 1);
            ps.setInt(1, userid); //pp_member_id
            ps.executeUpdate();
            String oldcache =  MySQL.getfromtable(Config.script_tableprefix+"cache_store", "`cs_value`", "cs_key", "stats");
            String newcache = Util.ForumCache(oldcache, player, userid, "mem_count", null, "last_mem_name", "last_mem_id", "last_mem_name_seo");
            ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"cache_store"+"` SET `cs_value` = '" + newcache + "' WHERE `cs_key` = 'stats'");
            ps.executeUpdate();
        }
    }

    public static String hash(String action,String player,String password, String thesalt) throws SQLException {
        if(action.equals("find"))
        {
              try {
                  String salt = MySQL.getfromtable(Config.script_tableprefix+"members", "`members_pass_salt`", "members_l_username", player.toLowerCase());
                  return passwordHash(password, salt);
              } catch (NoSuchAlgorithmException e) {
                  e.printStackTrace();
              } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
              }
        }
        else if(action.equals("create"))
        {
            try {
                return passwordHash(password, thesalt);
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
      return "fail";
    }

      public static boolean check_hash(String passwordhash, String hash)
      {
          if(passwordhash.equals(hash)) return true;
          else return false;
      }

      public static String passwordHash(String password, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException
      {
      return Encryption.md5(Encryption.md5(salt) + Encryption.md5(password));
      }
}
