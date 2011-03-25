/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
**/
package com.authdb.scripts.forum;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class PunBB {
	
  public static void adduser(int checkid,String player, String email, String password, String ipAddress) throws SQLException
  {
	if(checkid == 1)
	{
		long timestamp = System.currentTimeMillis()/1000;
		//
		PreparedStatement ps;
		//
		String salt = Encryption.hash(12,"none",33, 126);
		String hash = hash("create",player,password,salt);
		
		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`group_id`,`username`,`password`,`salt`,`email`,`registered`,`registration_ip`,`last_visit`)  VALUES (?,?,?,?,?,?,?,?)", 1);
	    ps.setInt(1, 3); //group_id
		ps.setString(2, player); //username
	    ps.setString(3, hash); //password
	    ps.setString(4, salt); //salt
		ps.setString(5, email); //email
		ps.setLong(6, timestamp); //registered
		ps.setString(7, ipAddress); //registration_ip
		ps.setLong(8, timestamp); //last_visit
		///
	    ps.executeUpdate();
	    
	    /*
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"config"+"` SET `config_value` = '" + userid + "' WHERE `config_name` = 'newest_user_id'");
	    ps.executeUpdate();
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"config"+"` SET `config_value` = '" + player + "' WHERE `config_name` = 'newest_username'");
	    ps.executeUpdate();
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"config"+"` SET `config_value` = config_value+1 WHERE `config_name` = 'num_users'");
	    ps.executeUpdate();*/
	}
 }

    public static String hash(String action,String player,String password, String thesalt) throws SQLException {
    	if(action.equals("find"))
    	{
  	try {
  		String salt = MySQL.getfromtable(Config.database_prefix+"users", "`salt`", "username", player);
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
  		return Encryption.SHA1(salt + Encryption.SHA1(password));
  	}
}