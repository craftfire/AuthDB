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
import com.authdb.util.databases.eBean;

public class vBulletin {

        public static String VersionRange = "3.0.0-3.8.7";
        public static String VersionRange2 = "4.0.0-4.1.2";
        public static String LatestVersionRange = VersionRange2;
        public static String Name = "vbulletin";
        public static String ShortName = "vb";


    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException
    {
      long timestamp = System.currentTimeMillis()/1000;
          if(checkid == 1)
          {
              String salt = Encryption.hash(30,"none",33,126);
              String passwordhashed = hash("create",player,password, salt);
              String passworddate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date (timestamp*1000));
              ///
              PreparedStatement ps;
              //
              ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.script_tableprefix+"user"+"` (`usergroupid`,`password`,`passworddate`,`email`,`showvbcode`,`joindate`,`lastvisit`,`lastactivity`,`reputationlevelid`,`options`,`ipaddress`,`salt`,`username`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
            ps.setString(1, "2"); //usergroupid
              ps.setString(2, passwordhashed); // password
            ps.setString(3, passworddate); //passworddate
            ps.setString(4, email); //email
              ps.setString(5, "1"); //showvbcode
              ps.setLong(6, timestamp); //joindate
              ps.setLong(7, timestamp); //lastvisit
              ps.setLong(8, timestamp); //lastactivity
              ps.setString(9, "5"); //reputationlevelid
            ps.setString(10, "45108311"); //options
            ps.setLong(11, Util.IP2Long(ipAddress)); //ipaddress
            ps.setString(12, salt); //salt
            ps.setString(13, player); //username
            ps.executeUpdate();


            int userid = MySQL.countitall(Config.script_tableprefix+"user");
            String oldcache =  MySQL.getfromtable(Config.script_tableprefix+"datastore", "`data`", "title", "userstats");
            String newcache = Util.ForumCache(oldcache, player, userid, "numbermembers", "activemembers", "newusername", "newuserid", null);
            ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"datastore"+"` SET `data` = '" + newcache + "' WHERE `title` = 'userstats'");
            ps.executeUpdate();

              }
          else if(checkid == 2)
          {
              String salt = Encryption.hash(30,"none",33,126);
              String passwordhashed = hash("create",player,password, salt);
              String passworddate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date (timestamp*1000));
          //    int userid;
              ///
              PreparedStatement ps;
              //
              ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.script_tableprefix+"user"+"` (`usergroupid`,`password`,`passworddate`,`email`,`showvbcode`,`joindate`,`lastvisit`,`lastactivity`,`reputationlevelid`,`options`,`ipaddress`,`salt`,`username`,`usertitle`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
            ps.setString(1, "2"); //usergroupid
              ps.setString(2, passwordhashed); // password
            ps.setString(3, passworddate); //passworddate
            ps.setString(4, email); //email
              ps.setString(5, "1"); //showvbcode
              ps.setLong(6, timestamp); //joindate
              ps.setLong(7, timestamp); //lastvisit
              ps.setLong(8, timestamp); //lastactivity
              ps.setString(9, "5"); //reputationlevelid
            ps.setString(10, "45091927"); //options
            ps.setString(11, ipAddress); //ipaddress
            ps.setString(12, salt); //salt
            ps.setString(13, player); //username
            ps.setString(14, "Junior Member"); //usertitle
            ps.executeUpdate();

            int userid = MySQL.countitall(Config.script_tableprefix+"user");
            String oldcache =  MySQL.getfromtable(Config.script_tableprefix+"datastore", "`data`", "title", "userstats");
            String newcache = Util.ForumCache(oldcache, player, userid, "numbermembers", "activemembers", "newusername", "newuserid", null);
            ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"datastore"+"` SET `data` = '" + newcache + "' WHERE `title` = 'userstats'");
            ps.executeUpdate();
          }
    }

    public static String hash(String action,String player,String password, String thesalt) throws SQLException {
        if(action.equals("find"))
        {
      try {
          eBean eBeanClass = eBean.CheckPlayer(player);
          String StoredSalt = eBeanClass.getSalt();
          return passwordHash(password, StoredSalt);
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
      return Encryption.md5(Encryption.md5(password)+salt);
      }
}
