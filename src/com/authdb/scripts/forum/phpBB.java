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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;



/**
 * Port of phpBB3 password handling to Java. 
 * See phpBB3/includes/functions.php
 * Edited by Contex
 * 
 * @author lars
 * @author Contex
 */
public class phpBB {
	
	public static boolean check(int checkid)
	{
		String name = null, latest = null, Version = null;
		String[] versions = null;
		if(checkid == 1)
		{
			name = Config.Script1_name;
			latest = Config.Script1_latest;
			versions = new String[] {Config.Script1_versions};
			Version = Util.CheckVersion(name,latest, 3);
		}
		else if(checkid == 2)
		{
			name = Config.Script1_name;
			latest = Config.Script1_latest;
			versions = new String[] {Config.Script1_versions};
			Version = Util.CheckVersion(name,latest, 3);
		}
		if(Arrays.asList(versions).contains(Version))
		{
			if(Config.debug_enable) Util.Debug("Version: "+Version+" is in the list of supported versions of this script ("+name+")");
			return true;
		}
		else 
		{ 
			Util.Log("warning","Version: "+Version+" is NOT in the list of supported versions of this script ("+name+") Setting to latest version of script: "+name+" "+latest); 
			Config.script_version = latest;
			return true;
		}
	}
	
  public static void adduser(String player, String email, String password, String ipAddress) throws SQLException
  {
	if(check(1))
	{
		String hash = phpbb_hash(password);
		long timestamp = System.currentTimeMillis()/1000;
		int userid;
		//
		PreparedStatement ps;
		//

		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`username`,`username_clean`,`user_password`,`user_email`,`group_id`,`user_timezone`,`user_dst`,`user_lang`,`user_type`,`user_regdate`,`user_new`,`user_lastvisit`,`user_permissions`,`user_sig`,`user_occ`,`user_interests`,`user_ip`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
	    ps.setString(1, player);
		ps.setString(2, player.toLowerCase());
	    ps.setString(3, hash);
	    ps.setString(4, email);
		ps.setString(5, "2"); //group
		ps.setString(6, "0.00"); //timezone
		ps.setString(7, "0"); //dst
		ps.setString(8, "en"); //lang
		ps.setString(9, "0"); //user_type
		ps.setLong(10, timestamp); //user_regdate
		ps.setString(11, "1"); //usernew
		ps.setLong(12, timestamp); //user_lastvisit
		//need to add these, it's complaining about not default is set.
		ps.setString(13, ""); //user_permissions
		ps.setString(14, ""); //user_sig
		ps.setString(15, ""); //user_occ
		ps.setString(16, ""); //user_interests
		///
		ps.setString(17, ipAddress); //user_ip
	    ps.executeUpdate();
	    
	    userid = MySQL.countitall(Config.database_prefix+"users");
	    
		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"user_group"+"` (`group_id`,`user_id`,`group_leader`,`user_pending`)  VALUES (?,?,?,?)", 1);
	    ps.setInt(1, 2);
		ps.setInt(2, userid);
	    ps.setInt(3, 0);
	    ps.setInt(4, 0);
	    ps.executeUpdate();
	    
		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"user_group"+"` (`group_id`,`user_id`,`group_leader`,`user_pending`)  VALUES (?,?,?,?)", 1);
	    ps.setInt(1, 7);
		ps.setInt(2, userid);
	    ps.setInt(3, 0);
	    ps.setInt(4, 0);
	    ps.executeUpdate();
	    
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"config"+"` SET `config_value` = '" + userid + "' WHERE `config_name` = 'newest_user_id'");
	    ps.executeUpdate();
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"config"+"` SET `config_value` = '" + player + "' WHERE `config_name` = 'newest_username'");
	    ps.executeUpdate();
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"config"+"` SET `config_value` = config_value+1 WHERE `config_name` = 'num_users'");
	    ps.executeUpdate();
	    MySQL.close();
	}
	else if(check(2))
	{
		String hash = Encryption.md5(password);
		long timestamp = System.currentTimeMillis()/1000;
		int userid;
		//
		PreparedStatement ps;
		//

		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`user_active`,`username`,`user_password`,`user_lastvisit`,`user_regdate`,`user_email`)  VALUES (?,?,?,?,?,?)", 1);
	    ps.setInt(1, 1); //user_active
		ps.setString(2, player.toLowerCase()); //username
	    ps.setString(3, hash); //user_password
	    ps.setLong(4, timestamp); //user_lastvisit
		ps.setLong(5, timestamp); //user_regdate
		ps.setString(6, email); //user_email
		///
	    ps.executeUpdate();
	    
	    userid = MySQL.countitall(Config.database_prefix+"users");
	    
		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"user_group"+"` (`group_id`,`user_id`,`user_pending`)  VALUES (?,?,?)", 1);
	    ps.setInt(1, 3);
		ps.setInt(2, userid);
	    ps.setInt(3, 0);
	    ps.executeUpdate();
	    /*
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"config"+"` SET `config_value` = '" + userid + "' WHERE `config_name` = 'newest_user_id'");
	    ps.executeUpdate();
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"config"+"` SET `config_value` = '" + player + "' WHERE `config_name` = 'newest_username'");
	    ps.executeUpdate();
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"config"+"` SET `config_value` = config_value+1 WHERE `config_name` = 'num_users'");
	    ps.executeUpdate();*/
	    MySQL.close();
	}
 }

	
  private static String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  
  public static String phpbb_hash(String password) {
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
		String output = "$H$";
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
		if (!setting.substring(0, 3).equals("$H$"))
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