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
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;


public class Joomla {
	
  public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException
  {
	long timestamp = System.currentTimeMillis()/1000;
	if(checkid == 1)
	{
		String hash = hash(player,password);
		//
		PreparedStatement ps;
		//
		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`name`,`username`,`email`,`password`,`usertype`,`block`,`gid`,`registerDate`,`lastvisitDate`)  VALUES (?,?,?,?,?,?,?,?,?)", 1);
	    ps.setString(1, player); //name
	    ps.setString(2, player); //username
	    ps.setString(3, email); //email
	    ps.setString(4, hash); //password
		ps.setString(5, "Registered"); //usertype
		ps.setInt(6, 0); //block
		ps.setInt(7, 18); //gid
		ps.setString(8, "NOW()"); //registerDate
		ps.setString(9, "NOW()"); //lastvisitDate
		ps.executeUpdate();
	}
	else if(checkid == 2)
	{
		String hash = hash(player,password);
		int userid;
		//
		PreparedStatement ps;
		///
		ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"members"+"` (`member_name`,`date_registered`,`last_login`,`real_name`,`passwd`,`email_address`,`member_ip`,`member_ip2`,`lngfile`,`buddy_list`,`pm_ignore_list`,`message_labels`,`personal_text`,`website_title`,`website_url`,`location`,`icq`,`msn`,`signature`,`avatar`,`usertitle`,`secret_question`,`additional_groups`,`openid_uri`,`ignore_boards`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
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
		ps.executeUpdate();
		
		userid = MySQL.countitall(Config.database_prefix+"members");
		ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"settings"+"` SET `value` = '" + player + "' WHERE `variable` = 'latestRealName'");
		ps.executeUpdate();
		ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"settings"+"` SET `value` = '" + userid + "' WHERE `variable` = 'latestMember'");
		ps.executeUpdate();
		ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"settings"+"` SET `value` = '" + timestamp + "' WHERE `variable` = 'memberlist_updated'");
		ps.executeUpdate();
	    ps = MySQL.mysql.prepareStatement("UPDATE `"+Config.database_prefix+"settings"+"` SET `value` = value+1 WHERE `variable` = 'totalMembers'");
	    ps.executeUpdate();
	}
  }
	

public static boolean check_hash(String passwd,String dbEntry) {
if (passwd==null || dbEntry==null || dbEntry.length()==0)
throw new IllegalArgumentException();
String[] arr = dbEntry.split(":",2);
if (arr.length==2) {
// new format as {HASH}:{SALT}
String cryptpass = arr[0];
String salt = arr[1];

return Encryption.md5(passwd+salt).equals(cryptpass);
} else {
// old format as {HASH} just like PHPbb and many other apps
String cryptpass = dbEntry;

return Encryption.md5(passwd).equals(cryptpass); 
}
}

static Random _rnd;

public static String hash(String username, String passwd) {
StringBuffer saltBuf = new StringBuffer();
if (_rnd==null) _rnd=new SecureRandom();
int i;
for (i=0;i<32;i++) {
saltBuf.append(Integer.toString(_rnd.nextInt(36),36));
}
String salt = saltBuf.toString();

return Encryption.md5(passwd+salt)+":"+salt;
}

/** Takes the MD5 hash of a sequence of ASCII or LATIN1 characters,
* and returns it as a 32-character lowercase hex string.
*
* Equivalent to MySQL's MD5() function 
* and to perl's Digest::MD5::md5_hex(),
* and to PHP's md5().
*
* Does no error-checking of the input, but only uses the low 8 bits
* from each input character.
*/
/*
private static String md5(String data) {
byte[] bdata = new byte[data.length()]; int i; byte[] hash;

for (i=0;i<data.length();i++) bdata[i]=(byte)(data.charAt(i)&0xff );

try {
MessageDigest md5er = MessageDigest.getInstance("MD5");
hash = md5er.digest(bdata);
} catch (GeneralSecurityException e) { throw new RuntimeException(e); }

StringBuffer r = new StringBuffer(32);
for (i=0;i<hash.length;i++) {
String x = Integer.toHexString(hash[i]&0xff);
if (x.length()<2) r.append("0");
r.append(x);
}
return r.toString(); 
} */
}