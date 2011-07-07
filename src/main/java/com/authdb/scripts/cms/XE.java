/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.scripts.cms;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.databases.MySQL;

public class XE {
    
    public static String Name = "xpress engine";
    public static String ShortName = "xe";
    public static String VersionRange = "1.0.3-1.0.3";
    public static String LatestVersionRange = VersionRange;
    
  public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException
  {
    long timestamp = System.currentTimeMillis()/1000;
    if(checkid == 1)
    {
        String hash = Encryption.md5(password);
        //
        PreparedStatement ps;
        //
        ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.script_tableprefix+"users"+"` (`name`,`pass`,`mail`,`created`,`access`,`login`,`status`,`init`)  VALUES (?,?,?,?,?,?,?,?)", 1);
        ps.setString(1, player); //name
        ps.setString(2, hash); //pass
        ps.setString(3, email); //mail
        ps.setLong(4, timestamp); //created
        ps.setLong(5, timestamp); //access
        ps.setLong(6, timestamp); //login
        ps.setInt(7, 1); //status
        ps.setString(8, email); //init
        ///need to add these, it's complaining about not default is set.
        ps.executeUpdate();
    }
    /*
    else if(check(2))
    {
        String hash = hash(player,password);
        int userid;
        //
        PreparedStatement ps;
        //
        ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.script_tableprefix+"users"+"` (`name`,`pass`,`mail`,`created`,`login`,`status`,`init`)  VALUES (?,?,?,?,?,?,?)", 1);
        ps.setString(1, player); //name
        ps.setString(2, hash); //pass
        ps.setString(3, email); //mail
        ps.setLong(4, timestamp); //created
        ps.setLong(5, timestamp); //login
        ps.setInt(6, 1); //status
        ps.setString(7, email); //init
        ///need to add these, it's complaining about not default is set.
        ps.executeUpdate();
    } */
  }
  
    public static boolean check_hash(String passwordhash, String hash)
    {
        if(passwordhash.equals(hash)) return true;
        else return false;
    }
}
