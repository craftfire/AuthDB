  package com.gmail.contexmoh.authdb.boards;

  import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
  import java.sql.PreparedStatement;
  import java.sql.SQLException;

import com.gmail.contexmoh.authdb.utils.MySQL;
import com.gmail.contexmoh.authdb.utils.Utils;


  public class myBB1_6 {
  	
    public static void adduser(String player, String email, String password, String ipAddress) throws SQLException
    {
  	long timestamp = System.currentTimeMillis()/1000;
  	String salt = Utils.getRandomString2(8);
  	String hash = myBB1_6_hash("create",player,password, salt);
  	///int userid;
  	//
  	PreparedStatement ps;
  	//
  	ps = MySQL.mysql.prepareStatement("INSERT INTO `"+MySQL.forumPrefix+"users"+"` (`username`,`password`,`salt`,`email`,`regdate`,`lastactive`,`lastvisit`,`regip`,`longregip`,`signature`,`buddylist`,`ignorelist`,`pmfolders`,`notepad`,`usernotes`,`usergroup`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
    ps.setString(1, player); //username
  	ps.setString(2, hash); // password
    ps.setString(3, salt); //salt
    ps.setString(4, email); //email
  	ps.setLong(5, timestamp); //regdate
  	ps.setLong(6, timestamp); //lastactive
  	ps.setLong(7, timestamp); //lastvisit
  	ps.setString(8, ipAddress); //regip
  	//ps.setLong(9, Utils.IP2Long(ipAddress)); //longregip
	ps.setString(9, "2130706433");
  	//need to add these, it's complaining about not default is set.
	ps.setString(10, ""); //signature
	ps.setString(11, ""); //buddylist
	ps.setString(12, ""); //ignorelist
	ps.setString(13, ""); //pmfolders
	ps.setString(14, ""); //notepad
	ps.setString(15, ""); //usernotes
	ps.setString(16, "5");//usergroup
    ps.executeUpdate();
 
    /*userid = MySQL.countitall(MySQL.forumPrefix+"users");
    String oldcache =  MySQL.getfromtable(MySQL.forumPrefix+"datacache", "`cache`", "title", "stats");
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
    	else if(i == 17) 
    	{ 
    		String dupe = "";
    		dupe += userid;
    		st.nextToken();
    		newcache += dupe.length()+":";
    	}
    	else if(i == 21) 
    	{ 
    		 st.nextToken();
    		lastuid = "\""+userid+"\";}";
    		newcache += lastuid;
    	}
    	else if(i == 24) 
    	{ 
    		st.nextToken();
    		newcache += usernamelength+":";
    	}
    	else if(i == 25) 
    	{ 
    		st.nextToken();
    		lastusername = "\""+player+"\";s";
    		newcache += lastusername+":";
    	}
      else
      {  
    	  newcache += st.nextToken()+":"; 
      }
     Utils.Log("info",i+"-"+st.nextToken()+":"); 
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
      //ps = MySQL.mysql.prepareStatement("UPDATE `"+MySQL.forumPrefix+"datacache"+"` SET `cache` = '" + newcache2 + "' WHERE `title` = 'stats'");
     // ps.executeUpdate();
      */
    }
    public static boolean checkpassword(String player, String password) throws SQLException
    {
  	String hash = MySQL.getfromtable(MySQL.forumPrefix+"users", "`password`", "username", player);
  	String salt = "";
  	if(myBB1_6_check_hash(myBB1_6_hash("find",player,password, salt),hash)) { return true; }
  	else { return false; }
    }
    
    public static boolean checkuser(String player) throws SQLException
    {	
  	String check = MySQL.getfromtable(MySQL.forumPrefix+"users", "*", "username", player);
  	if(check != "fail") { return true; }
  	return false;
    }
  	
    public static String myBB1_6_hash(String action,String player,String password, String thesalt) throws SQLException {
    	if(action.equals("find"))
    	{
  	try {
  		String salt = MySQL.getfromtable(MySQL.forumPrefix+"users", "`salt`", "username", player);
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

  	public static boolean myBB1_6_check_hash(String passwordhash, String hash)
  	{
  		if(passwordhash.equals(hash)) return true;
  		else return false;
  	}
  	
  	public static String passwordHash(String password, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException
  	{
  	return Utils.md5Hash(Utils.md5Hash(salt) + Utils.md5Hash(password));
  	}
}