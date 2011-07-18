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
import java.util.Random;

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class SMF {

    public static String VersionRange = "1.1.1-1.1.13";
    public static String VersionRange2 = "2.0.0.0-2.0.0.5";
    public static String LatestVersionRange = VersionRange2;
    public static String Name = "simple machines";
    public static String ShortName = "smf";

  public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException {
    long timestamp = System.currentTimeMillis()/1000;
    if(checkid == 1) {
        Random r = new Random();
        int randint = r.nextInt(1000000);
        String salt = Encryption.md5(""+randint);
        salt = salt.substring(0, 4);
        String hash = hash(1,player,password);
        int userid;
        //
        PreparedStatement ps;
        //
        ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.script_tableprefix+"members"+"` (`memberName`,`dateRegistered`,`lastLogin`,`realName`,`passwd`,`emailAddress`,`memberIP`,`memberIP2`,`lngfile`,`buddy_list`,`pm_ignore_list`,`messageLabels`,`personalText`,`websiteTitle`,`websiteUrl`,`location`,`ICQ`,`MSN`,`signature`,`avatar`,`usertitle`,`secretQuestion`,`additionalGroups`,`passwordSalt`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
        ps.setString(1, player); //memberName
        ps.setLong(2, timestamp); //dateRegistered
        ps.setLong(3, timestamp); //lastLogin
        ps.setString(4, player); //realName
        ps.setString(5, hash); //passwd
        ps.setString(6, email); //emailAddress
        ps.setString(7, ipAddress); //memberIP
        ps.setString(8, ipAddress); //memberIP2
        ///need to add these, it's complaining about not default is set.
        ps.setString(9, ""); //lngfile
        ps.setString(10, ""); //buddy_list
        ps.setString(11, ""); //pm_ignore_list
        ps.setString(12, ""); //messageLabels
        ps.setString(13, ""); //personalText
        ps.setString(14, ""); //websiteTitle
        ps.setString(15, ""); //websiteUrl
        ps.setString(16, ""); //location
        ps.setString(17, ""); //ICQ
        ps.setString(18, ""); //MSN
        ps.setString(19, ""); //signature
        ps.setString(20, ""); //avatar
        ps.setString(21, ""); //usertitle
        ps.setString(22, ""); //secretQuestion
        ps.setString(23, ""); //additionalGroups
        ps.setString(24, salt); //passwordSalt
        ps.executeUpdate();

        userid = MySQL.countitall(Config.script_tableprefix+"members");
        ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"settings"+"` SET `value` = '"+player+"' WHERE `variable` = 'latestRealName'");
        ps.executeUpdate();
        ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"settings"+"` SET `value` = '"+userid+"' WHERE `variable` = 'latestMember'");
        ps.executeUpdate();
        ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"settings"+"` SET `value` = '"+timestamp+"' WHERE `variable` = 'memberlist_updated'");
        ps.executeUpdate();
        ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"settings"+"` SET `value` = value+1 WHERE `variable` = 'totalMembers'");
        ps.executeUpdate();
    }
    else if(checkid == 2) {
        Random r = new Random();
        int randint = r.nextInt(1000000);
        String salt = Encryption.md5(""+randint);
        salt = salt.substring(0, 4);
        String hash = hash(2,player,password);
        int userid;
        //
        PreparedStatement ps;
        ///
        ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.script_tableprefix+"members"+"` (`member_name`,`date_registered`,`last_login`,`real_name`,`passwd`,`email_address`,`member_ip`,`member_ip2`,`lngfile`,`buddy_list`,`pm_ignore_list`,`message_labels`,`personal_text`,`website_title`,`website_url`,`location`,`icq`,`msn`,`signature`,`avatar`,`usertitle`,`secret_question`,`additional_groups`,`openid_uri`,`ignore_boards`,`password_salt`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
        ps.setString(1, player); //member_name
        ps.setLong(2, timestamp); //date_registered
        ps.setLong(3, timestamp); //last_login
        ps.setString(4, player); //real_name
        ps.setString(5, hash); //passwd
        ps.setString(6, email); //email_address
        ps.setString(7, ipAddress); //memberIP
        ps.setString(8, ipAddress); //memberIP2
        //need to add these, it's complaining about not default is set.
        ps.setString(9, ""); //lngfile
        ps.setString(10, ""); //buddy_list
        ps.setString(11, ""); //pm_ignore_list
        ps.setString(12, ""); //message_labels
        ps.setString(13, ""); //personal_text
        ps.setString(14, ""); //website_title
        ps.setString(15, ""); //website_url
        ps.setString(16, ""); //location
        ps.setString(17, ""); //ICQ
        ps.setString(18, ""); //MSN
        ps.setString(19, ""); //signature
        ps.setString(20, ""); //avatar
        ps.setString(21, ""); //usertitle
        ps.setString(22, ""); //secret_question
        ps.setString(23, ""); //additional_groups
        ps.setString(24, ""); //openid_uri
        ps.setString(25, ""); //ignore_boards
        ps.setString(26, salt); //password_salt
        ps.executeUpdate();

        userid = MySQL.countitall(Config.script_tableprefix+"members");
        ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"settings"+"` SET `value` = '"+player+"' WHERE `variable` = 'latestRealName'");
        ps.executeUpdate();
        ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"settings"+"` SET `value` = '"+userid+"' WHERE `variable` = 'latestMember'");
        ps.executeUpdate();
        ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"settings"+"` SET `value` = '"+timestamp+"' WHERE `variable` = 'memberlist_updated'");
        ps.executeUpdate();
        ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.script_tableprefix+"settings"+"` SET `value` = value+1 WHERE `variable` = 'totalMembers'");
        ps.executeUpdate();
    }
  }

  public static String hash(int checkid, String player, String password) {
    if(checkid == 1) {
          try {
              String temp = player+password;
            return Encryption.SHA1(temp);
        } catch (NoSuchAlgorithmException e) {
            Util.Logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        } catch (UnsupportedEncodingException e) {
            Util.Logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
    }
    else if(checkid == 2) {
          try {
              String temp = player+password;
            return Encryption.SHA1(temp);
        } catch (NoSuchAlgorithmException e) {
            Util.Logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        } catch (UnsupportedEncodingException e) {
            Util.Logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
    }
    return "fail";
  }

    public static boolean check_hash(String passwordhash, String hash) {
        if(passwordhash.equals(hash)) {
            return true;
        } else {
            return false;
        }
    }
}
