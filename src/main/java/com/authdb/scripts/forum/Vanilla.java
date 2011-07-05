/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.scripts.forum;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class Vanilla {
    
    public static String Name = "vanilla";
    public static String ShortName = "van";
    public static String VersionRange = "2.0.17.8-2.0.17.8";
    public static String VersionRange2 = "2.0.17.9-2.0.17.9";
    public static String LatestVersionRange = VersionRange2;
        
    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException
    {
      long timestamp = System.currentTimeMillis()/1000;
          String usertable = null,roletable = null;
          if(checkid == 1)
          {
              usertable = "User";
              roletable = "UserRole";
          }
          else if(checkid == 2)
          {
              usertable = "user";
              roletable = "userrole";
          }
    
        if(checkid == 1 || checkid == 2)
          {
              String passwordhashed = hash(password);
              String randomkey = Util.getRandomString2(12, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
              String realdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date (timestamp*1000));
            ///
              PreparedStatement ps;
              //
              ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+usertable+"` (`Name`,`Password`,`HashMethod`,`Email`,`Gender`,`Preferences`,`Permissions`,`Attributes`,`DateFirstVisit`,`DateLastActive`,`DateInserted`,`DateUpdated`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?)", 1);
            ps.setString(1, player); //Name
              ps.setString(2, passwordhashed); // Password
            ps.setString(3, "Vanilla"); //HashMethod    
            ps.setString(4, email); //Email
              ps.setString(5, "m"); //Gender
              ps.setString(6, "a:1:{s:13:\"Authenticator\";s:8:\"password\";}"); //Preferences
              ps.setString(7, "a:9:{i:0;s:19:\"Garden.SignIn.Allow\";i:1;s:20:\"Garden.Activity.View\";i:2;s:20:\"Garden.Profiles.View\";i:3;s:24:\"Vanilla.Discussions.View\";i:4;s:23:\"Vanilla.Discussions.Add\";i:5;s:20:\"Vanilla.Comments.Add\";s:24:\"Vanilla.Discussions.View\";a:1:{i:0;i:-1;}s:23:\"Vanilla.Discussions.Add\";a:1:{i:0;i:-1;}s:20:\"Vanilla.Comments.Add\";a:1:{i:0;i:-1;}}"); //Permissions
              ps.setString(8, "a:1:{s:12:\"TransientKey\";s:12:\""+randomkey+"\";}"); //Attributes
              ps.setString(9, realdate); //DateFirstVisit
            ps.setString(10, realdate); //DateLastActive
            ps.setString(11, realdate); //DateInserted
            ps.setString(12, realdate); //DateUpdated
            ps.executeUpdate();
            
            int userid = MySQL.countitall(Config.database_prefix+usertable);
            
            ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+roletable+"` (`UserID`,`RoleID`)  VALUES (?,?)", 1);
            ps.setInt(1, userid); //UserID
            ps.setInt(2, 3); //RoleID
            ps.executeUpdate();
            
            ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+roletable+"` (`UserID`,`RoleID`)  VALUES (?,?)", 1);
            ps.setInt(1, userid); //UserID
            ps.setInt(2, 8); //RoleID
            ps.executeUpdate();
              
          }
    }
    

    private static String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    public static String hash(String password) {
      String random_state = unique_id();
      String random = "";
      int count = 6;

      if (random.length() < count)
      {
          random = "";

          for (int i = 0; i < count; i += 16)
          {
              random_state = Encryption.md5(unique_id() + random_state);
              random += Encryption.pack(Encryption.md5(random_state));
          }
          random = random.substring(0, count);
      }

      String hash = _hash_crypt_private(password, _hash_gensalt_private(
              random, itoa64));
      if (hash.length() == 34)
          return hash;

      return Encryption.md5(password);
    }

    private static String unique_id() {
      return unique_id("c");
    }

    private static String unique_id(String extra) {
      return "1234567890abcdef";
    }

    private static String _hash_gensalt_private(String input, String itoa64) {
      return _hash_gensalt_private(input, itoa64, 6);
    }

      private static String _hash_gensalt_private(String input, String itoa64,
              int iteration_count_log2)
      {
          if (iteration_count_log2 < 4 || iteration_count_log2 > 31)
          {
              iteration_count_log2 = 8;
          }
          int PHP_VERSION = 5;
          String output = "$P$";
          output += itoa64.charAt(Math.min(iteration_count_log2
                  + ((PHP_VERSION >= 5) ? 5 : 3), 30));
          output += _hash_encode64(input, 6);

          return output;
      }


    /**
     * Encode hash
     */
      private static String _hash_encode64(String input, int count)
      {
          String output = "";
          int i = 0;

          do
          {
              int value = input.charAt(i++);
              output += itoa64.charAt(value & 0x3f);

              if (i < count)
                  value |= input.charAt(i) << 8;

              output += itoa64.charAt((value >> 6) & 0x3f);

              if (i++ >= count)
                  break;

              if (i < count)
                  value |= input.charAt(i) << 16;

              output += itoa64.charAt((value >> 12) & 0x3f);

              if (i++ >= count)
                  break;

              output += itoa64.charAt((value >> 18) & 0x3f);
          }
          while (i < count);

          return output;
      }

      static String _hash_crypt_private(String password, String setting)
      {
          String output = "*";

          // Check for correct hash
          if (!setting.substring(0, 3).equals("$P$"))
              return output;

          int count_log2 = itoa64.indexOf(setting.charAt(3));
          if (count_log2 < 7 || count_log2 > 30)
              return output;

          int count = 1 << count_log2;
          String salt = setting.substring(4, 12);
          if (salt.length() != 8)
              return output;

          String m1 = Encryption.md5(salt + password);
          String hash = Encryption.pack(m1);
          do
          {
              hash = Encryption.pack(Encryption.md5(hash + password));
          }
          while (--count > 0);

          output = setting.substring(0, 12);
          output += _hash_encode64(hash, 16);

          return output;
      }

    public static boolean check_hash(String password, String hash)
    {
        if (hash.length() == 34)
            return _hash_crypt_private(password, hash).equals(hash);
        else
            return Encryption.md5(password).equals(hash);
    }
  }