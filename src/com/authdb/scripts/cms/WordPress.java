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

public class WordPress {
	
	public static String Name = "wordpress";
	public static String ShortName = "wp";
	public static String VersionRange = "3.1.3-3.1.4";
	
  public static void adduser(int checkid,String player, String email, String password, String ipAddress) throws SQLException
  {
	if(checkid == 1)
	{
		long timestamp = System.currentTimeMillis()/1000;
		//
		PreparedStatement ps;
		//
		String passwordhashed = hash(password);
		String realdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date (timestamp*1000));

		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`user_login`,`user_pass`,`user_nicename`,`user_email`,`user_registered`,`display_name`)  VALUES (?,?,?,?,?,?)", 1);
	    ps.setString(1, player); //user_login
		ps.setString(2, passwordhashed); //user_pass
	    ps.setString(3, player); //user_nicename	
	    ps.setString(4, email); //user_email
		ps.setString(5, realdate); //user_registered
		ps.setString(6, player); //display_name
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
		String salt = setting.substring(4, 8);
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