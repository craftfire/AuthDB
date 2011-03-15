package com.gmail.contexmoh.authdb.boards;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.gmail.contexmoh.authdb.utils.MySQL;
import com.gmail.contexmoh.authdb.utils.Utils;


public class SMF1 {
	
  public static void adduser(String player, String email, String password, String ipAddress) throws SQLException
  {
	long timestamp = System.currentTimeMillis()/1000;
	String hash = SMF1_hash(player,password);
	int userid;
	//
	PreparedStatement ps;
	//
	ps = MySQL.mysql.prepareStatement("INSERT INTO `"+MySQL.forumPrefix+"members"+"` (`memberName`,`dateRegistered`,`lastLogin`,`realName`,`passwd`,`emailAddress`,`memberIP`,`memberIP2`,`lngfile`,`buddy_list`,`pm_ignore_list`,`messageLabels`,`personalText`,`websiteTitle`,`websiteUrl`,`location`,`ICQ`,`MSN`,`signature`,`avatar`,`usertitle`,`secretQuestion`,`additionalGroups`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
    ps.setString(1, player); //memberName
    ps.setLong(2, timestamp); //dateRegistered
    ps.setLong(3, timestamp); //lastLogin
    ps.setString(4, player); //realName
	ps.setString(5, hash); //passwd
	ps.setString(6, email); //emailAddress
	ps.setString(7, ipAddress); //memberIP
	ps.setString(8, ipAddress); //memberIP2
	//need to add these, it's complaining about not default is set.
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
	String hash = MySQL.getfromtable(MySQL.forumPrefix+"members", "`passwd`", "realName", player);
	if(SMF1_check_hash(SMF1_hash(player, password),hash)) { return true; }
	else { return false; }
  }
  
  public static boolean checkuser(String player) throws SQLException
  {	
	String check = MySQL.getfromtable(MySQL.forumPrefix+"members", "*", "realName", player);
	if(check != "fail") { return true; }
	return false;
  }
	
  public static String SMF1_hash(String player, String password) {
	try {
		return Utils.SHA1(player+password);
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	return "fail";
  }

	public static boolean SMF1_check_hash(String passwordhash, String hash)
	{
		if(passwordhash.equals(hash)) return true;
		else return false;
	}
}