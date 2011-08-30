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
import com.authdb.util.encryption.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;
import com.authdb.util.databases.EBean;

public class MyBB {

    public static String VersionRange = "1.6-1.6";
    public static String LatestVersionRange = VersionRange;
    public static String Name = "mybb";
    public static String ShortName = "mybb";

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
    if (checkid == 1) {
        long timestamp = System.currentTimeMillis()/1000;
        String salt = Encryption.hash(8, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 0, 0);
        String hash = hash("create", player, password, salt);
        //
        PreparedStatement ps;
        //
        Util.logging.mySQL("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`username`, `password`, `salt`, `email`, `regdate`, `lastactive`, `lastvisit`, `regip`, `longregip`, `signature`, `buddylist`, `ignorelist`, `pmfolders`, `notepad`, `usernotes`, `usergroup`)  VALUES (" + player + ", " + hash + ", " + salt + ", " + email + ", " + timestamp + ", " + timestamp + ", " + timestamp + ", " + ipAddress + ", " + Util.ip2Long(ipAddress) + ", '', '', '', '', '', '', '')");
        ps = MySQL.mysql.prepareStatement("INSERT INTO `" + Config.script_tableprefix + "users" + "` (`username`, `password`, `salt`, `email`, `regdate`, `lastactive`, `lastvisit`, `regip`, `longregip`, `signature`, `buddylist`, `ignorelist`, `pmfolders`, `notepad`, `usernotes`, `usergroup`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
        ps.setString(1, player); //username
        ps.setString(2, hash); // password
        ps.setString(3, salt); //salt
        ps.setString(4, email); //email
        ps.setLong(5, timestamp); //regdate
        ps.setLong(6, timestamp); //lastactive
        ps.setLong(7, timestamp); //lastvisit
        ps.setString(8, ipAddress); //regip
        ps.setLong(9, Util.ip2Long(ipAddress));
        //need to add these, it's complaining about not default is set.
        ps.setString(10, ""); //signature
        ps.setString(11, ""); //buddylist
        ps.setString(12, ""); //ignorelist
        ps.setString(13, ""); //pmfolders
        ps.setString(14, ""); //notepad
        ps.setString(15, ""); //usernotes
        ps.setString(16, "5");//usergroup
        ps.executeUpdate();
        ps.close();

        int userid = MySQL.countitall(Config.script_tableprefix + "users");
        String oldcache =  MySQL.getfromtable(Config.script_tableprefix + "datastore", "`data`", "title", "userstats");
        String newcache = Util.forumCache(oldcache, player, userid, "numusers", null, "lastusername", "lastuid", null);
        ps = MySQL.mysql.prepareStatement("UPDATE `" + Config.script_tableprefix + "datacache" + "` SET `cache` = '" + newcache + "' WHERE `title` = 'stats'");
        ps.executeUpdate();
        ps.close();
        }
    }

    public static String hash(String action, String player, String password, String thesalt) throws SQLException {
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
          return Encryption.md5(Encryption.md5(salt) + Encryption.md5(password));
      }
}
