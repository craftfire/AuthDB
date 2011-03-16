/**
 * Copyright (C) 2011 Contex <contexmoh@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/  
package com.gmail.contexmoh.authdb.boards;

  import java.io.UnsupportedEncodingException;
  import java.security.NoSuchAlgorithmException;
  import java.sql.PreparedStatement;
  import java.sql.SQLException;

import com.gmail.contexmoh.authdb.utils.Config;
import com.gmail.contexmoh.authdb.utils.MySQL;
import com.gmail.contexmoh.authdb.utils.Utils;


  public class vB4_1 {
  	
    public static void adduser(String player, String email, String password, String ipAddress) throws SQLException
    {
  	long timestamp = System.currentTimeMillis()/1000;
  	String salt = Utils.fetch_user_salt(30);
  	String passwordhashed = hash("create",player,password, salt);
  	String passworddate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date (timestamp*1000));
  //	int userid;
  	///
  	PreparedStatement ps;
  	//
  	ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"user"+"` (`usergroupid`,`password`,`passworddate`,`email`,`showvbcode`,`joindate`,`lastvisit`,`lastactivity`,`reputationlevelid`,`options`,`ipaddress`,`salt`,`username`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
    ps.setString(1, "2"); //usergroupid
  	ps.setString(2, passwordhashed); // password
    ps.setString(3, passworddate); //passworddate
    ps.setString(4, email); //email
  	ps.setString(5, "1"); //showvbcode
  	ps.setLong(6, timestamp); //joindate
  	ps.setLong(7, timestamp); //lastvisit
  	ps.setLong(8, timestamp); //lastactivity
  	ps.setString(9, "5"); //reputationlevelid
	ps.setString(10, "45108311"); //options
	ps.setLong(11, Utils.IP2Long(ipAddress)); //ipaddress
	ps.setString(12, salt); //salt
	ps.setString(13, player); //username
    ps.executeUpdate();
     
   /*  userid = MySQL.countitall(ForumAuth.forumPrefix+"user");
    String oldcache =  MySQL.getfromtable(ForumAuth.forumPrefix+"datastore", "`data`", "title", "userstats");
    Utils.Log("info",oldcache);
    StringTokenizer st = new StringTokenizer(oldcache,":");
    int i = 0, usernamelength = player.length();
    String numusers, lastuid, lastusername, totalusers = "", newcache = "";
    while (st.hasMoreTokens()) {
    	if(i == 5) 
    	{ 
    		st.nextToken();
    		newcache += usernamelength+":";
    	}
    	else if(i == 6) 
    	{ 
    		numusers = st.nextToken();
    		numusers = Utils.removeChar(numusers,'"');
			numusers = Utils.removeChar(numusers,'s');
			numusers = Utils.removeChar(numusers,';');
			numusers = numusers.trim();
			int numuserNumber = Integer.parseInt(numusers) + 1;
			totalusers += numuserNumber;
			numusers = "\""+numuserNumber+"\""+";s";
			newcache += numusers+":";
    	}
    	else if(i == 13) 
    	{ 
    		st.nextToken();
    		newcache += usernamelength+":";
    	}
    	else if(i == 14) 
    	{ 
    		st.nextToken();
    		lastusername = "\""+player+"\";s";
    		newcache += lastusername+":";
    	}
    	else if(i == 17) 
    	{ 
    		String dupe = "";
    		dupe += userid;
    		st.nextToken();
    		newcache += dupe.length()+":";
    	}
    	else if(i == 18) 
    	{ 
    		 st.nextToken();
    		lastuid = "\""+userid+"\";}";
    		newcache += lastuid;
    	}
      else
      {  
    	  newcache += st.nextToken()+":"; 
      }
   //   Utils.Log("info",i+"-"+st.nextToken()+":"); 
      i++;
    }
    StringTokenizer st2 = new StringTokenizer(newcache,":");
    String newcache2 = "";
    while (st2.hasMoreTokens()) {
    	if(i == 5) 
    	{ 
    		st.nextToken();
    		newcache2 += totalusers.length()+":";
    	}
        else
        {  
      	  newcache2 += st.nextToken()+":"; 
        }
    }
    
      ps = MySQL.mysql.prepareStatement("UPDATE `"+ForumAuth.forumPrefix+"datastore"+"` SET `data` = '" + newcache2 + "' WHERE `title` = 'userstats'");
      ps.executeUpdate();
      */
    }
    public static boolean checkpassword(String player, String password) throws SQLException
    {
  	String hash = MySQL.getfromtable(Config.database_prefix+"user", "`password`", "username", player);
  	String salt = "";
  	if(check_hash(hash("find",player,password, salt),hash)) { return true; }
  	else { return false; }
    }
    
    public static boolean checkuser(String player) throws SQLException
    {	
  	String check = MySQL.getfromtable(Config.database_prefix+"user", "*", "username", player);
  	if(check != "fail") { return true; }
  	return false;
    }
  	
    public static String hash(String action,String player,String password, String thesalt) throws SQLException {
    	if(action.equals("find"))
    	{
  	try {
  		String salt = MySQL.getfromtable(Config.database_prefix+"user", "`salt`", "username", player);
  		Utils.Log("info", salt);
  		Utils.Log("info", passwordHash(password, salt));
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

  	private static boolean check_hash(String passwordhash, String hash)
  	{
  		if(passwordhash.equals(hash)) return true;
  		else return false;
  	}
  	
  	public static String passwordHash(String password, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException
  	{
  	return Utils.md5Hash(Utils.md5Hash(password)+salt);
  	}
}