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
package com.authdb.scripts.cms;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;


public class Drupal {
	
  public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException
  {
	long timestamp = System.currentTimeMillis()/1000;
	if(checkid == 1)
	{
		String hash = Encryption.md5(password);
		//
		PreparedStatement ps;
		//
		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`name`,`pass`,`mail`,`created`,`access`,`login`,`status`,`init`)  VALUES (?,?,?,?,?,?,?,?)", 1);
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
		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`name`,`pass`,`mail`,`created`,`login`,`status`,`init`)  VALUES (?,?,?,?,?,?,?)", 1);
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
				random_state = Encryption.SHA256(unique_id() + random_state);
				random += Encryption.pack(Encryption.SHA256(random_state));
			}
			random = random.substring(0, count);
		}

		String hash = _hash_crypt_private(password, _hash_gensalt_private(
				random, itoa64));
		if (hash.length() == 55)
			return hash;

		return Encryption.SHA256(password);
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
			String output = "$S$";
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
			if (!setting.substring(0, 3).equals("$S$"))
				return output;

			int count_log2 = itoa64.indexOf(setting.charAt(3));
			if (count_log2 < 7 || count_log2 > 30)
				return output;

			int count = 1 << count_log2;
			String salt = setting.substring(4, 8);
			if (salt.length() != 8)
				return output;

			String m1 = Encryption.SHA512(salt + password);
			String hash = Encryption.pack(m1);
			do
			{
				hash = Encryption.pack(Encryption.SHA512(hash + password));
			}
			while (--count > 0);

			output = setting.substring(0, 12);
			output += _hash_encode64(hash, 16);

			return output;
		}

		public static boolean check_hash(String password, String hash)
		{
			if (hash.length() == 55)
				return _hash_crypt_private(password, hash).equals(hash);
			else
				return Encryption.SHA512(password).equals(hash);
		}
}