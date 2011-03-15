package com.gmail.contexmoh.authdb.boards;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.gmail.contexmoh.authdb.utils.MySQL;
import com.gmail.contexmoh.authdb.utils.Utils;


public class SMF2 {
	
  public static void adduser(String player, String email, String password, String ipAddress) throws SQLException
  {
	long timestamp = System.currentTimeMillis()/1000;
	String hash = SMF2_hash(player,password);
	int userid;
	//
	PreparedStatement ps;
	///
	ps = MySQL.mysql.prepareStatement("INSERT INTO `"+MySQL.forumPrefix+"members"+"` (`member_name`,`date_registered`,`last_login`,`real_name`,`passwd`,`email_address`,`member_ip`,`member_ip2`,`lngfile`,`buddy_list`,`pm_ignore_list`,`message_labels`,`personal_text`,`website_title`,`website_url`,`location`,`icq`,`msn`,`signature`,`avatar`,`usertitle`,`secret_question`,`additional_groups`,`openid_uri`,`ignore_boards`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
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
	
	userid = MySQL.countitall(MySQL.forumPrefix+"members");
	ps = MySQL.mysql.prepareStatement("UPDATE `"+MySQL.forumPrefix+"settings"+"` SET `value` = '" + player + "' WHERE `variable` = 'latestRealName'");
	ps.executeUpdate();
	ps = MySQL.mysql.prepareStatement("UPDATE `"+MySQL.forumPrefix+"settings"+"` SET `value` = '" + userid + "' WHERE `variable` = 'latestMember'");
	ps.executeUpdate();
	ps = MySQL.mysql.prepareStatement("UPDATE `"+MySQL.forumPrefix+"settings"+"` SET `value` = '" + timestamp + "' WHERE `variable` = 'memberlist_updated'");
	ps.executeUpdate();
    ps = MySQL.mysql.prepareStatement("UPDATE `"+MySQL.forumPrefix+"settings"+"` SET `value` = value+1 WHERE `variable` = 'totalMembers'");
    ps.executeUpdate();
  }
  
  public static boolean checkpassword(String player, String password) throws SQLException
  {	
	String hash = MySQL.getfromtable(MySQL.forumPrefix+"members", "`passwd`", "real_name", player);
	if(SMF2_check_hash(SMF2_hash(player, password),hash)) { return true; }
	else { return false; }
  }
  
  public static boolean checkuser(String player) throws SQLException
  {	
	String check = MySQL.getfromtable(MySQL.forumPrefix+"members", "*", "real_name", player);
	if(check != "fail") { return true; }
	return false;
  }
	
  public static String SMF2_hash(String player, String password) {
	try {
		return Utils.SHA1(player+password);
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	return "fail";
  }

	public static boolean SMF2_check_hash(String passwordhash, String hash)
	{
		if(passwordhash.equals(hash)) return true;
		else return false;
	}
}