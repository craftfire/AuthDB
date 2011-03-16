package com.gmail.contexmoh.authdb.boards;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.gmail.contexmoh.authdb.AuthDB;
import com.gmail.contexmoh.authdb.utils.Config;
import com.gmail.contexmoh.authdb.utils.MySQL;
import com.gmail.contexmoh.authdb.utils.Utils;



/**
 * Port of phpBB3 password handling to Java. 
 * See phpBB3/includes/functions.php
 * Edited by Contex
 * 
 * @author lars
 * @author Contex
 */
public class phpBB3 {
	
  public static void adduser(String player, String email, String password, String ipAddress) throws SQLException
  {
	long timestamp = System.currentTimeMillis()/1000;
	String hash = phpbb_hash(password);
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
  }
  
  public static boolean checkpassword(String player, String password) throws SQLException
  {	
	String hash = MySQL.getfromtable(Config.database_prefix+"users", "`user_password`", "username_clean", player);
	if(phpbb_check_hash(password,hash)) { return true; }
	else { return false; }
  }
  
  public static boolean checkuser(String player) throws SQLException
  {	
	String check = MySQL.getfromtable(Config.database_prefix+"users", "*", "username_clean", player);
	if(check != "fail") { return true; }
	return false;
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
			random_state = Utils.md5(unique_id() + random_state);
			random += Utils.pack(Utils.md5(random_state));
		}
		random = random.substring(0, count);
	}

	String hash = _hash_crypt_private(password, _hash_gensalt_private(
			random, itoa64));
	if (hash.length() == 34)
		return hash;

	return Utils.md5(password);
  }

  private static String unique_id() {
    return unique_id("c");
  }

  // global $config;
  // private boolean dss_seeded = false;

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

		String output = "$H$";
		output += itoa64.charAt(Math.min(iteration_count_log2
				+ ((AuthDB.PHP_VERSION >= 5) ? 5 : 3), 30));
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

		String m1 = Utils.md5(salt + password);
		String hash = Utils.pack(m1);
		do
		{
			hash = Utils.pack(Utils.md5(hash + password));
		}
		while (--count > 0);

		output = setting.substring(0, 12);
		output += _hash_encode64(hash, 16);

		return output;
	}

	public static boolean phpbb_check_hash(String password, String hash)
	{
		if (hash.length() == 34)
			return _hash_crypt_private(password, hash).equals(hash);
		else
			return Utils.md5(password).equals(hash);
	}
}