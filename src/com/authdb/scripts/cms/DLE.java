/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/
package com.authdb.scripts.cms;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.databases.MySQL;

public class DLE {
		
	public static String Name = "datalife engine";
	public static String ShortName = "dle";
	public static String VersionRange = "9.2-9.2";

    public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException
    {
		if(checkid == 1)
	    {
	long timestamp = System.currentTimeMillis()/1000;
	String hash = hash(password);
	//
	PreparedStatement ps;
	//
	ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`email`,`password`,`name`,`lastdate`,`reg_date`,`logged_ip`,`info`,`signature`,`favorites`,`xfields`)  VALUES (?,?,?,?,?,?,?,?,?,?)", 1);
	ps.setString(1, email); //email
	ps.setString(2, hash); // password
	ps.setString(3, player); //name
	ps.setLong(4, timestamp); //lastdate
	ps.setLong(5, timestamp); //reg_date
	ps.setString(6, ipAddress); //logged_ip
	//need to add these, it's complaining about not default is set.
	ps.setString(7, ""); //info
	ps.setString(8, ""); //signature
	ps.setString(9, ""); //favorites
	ps.setString(10, ""); //xfields
	ps.executeUpdate();
	    }
    }
  	
    public static String hash(String password) throws SQLException {
	try {
		return passwordHash(password);
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  	return "fail";
    }

  	public static boolean check_hash(String passwordhash, String hash)
  	{
  		if(passwordhash.equals(hash)) return true;
  		else return false;
  	}
  	
  	public static String passwordHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException
  	{
  	return Encryption.md5(Encryption.md5(password));
  	}
}