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
  import java.sql.SQLException;
import java.util.Arrays;

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;


  public class vB {
	  
	  public static boolean check()
		{
			String name = Config.Script4_name;
			String latest = Config.Script4_latest;
			String[] versions = new String[] {Config.Script4_versions};
			String Version = Util.CheckVersion(name,latest, 3);
			if(Arrays.asList(versions).contains(Version))
			{
				Util.Log("warning","Version: "+Version+" is NOT in the list of supported versions of this script ("+name+") Setting to latest version of script: "+name+" "+latest); 
				Config.script_version = latest;
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
  	long timestamp = System.currentTimeMillis()/1000;
  	if(check())
  	{
	  	String salt = Encryption.hash(30,"none",33,126);
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
		ps.setLong(11, Util.IP2Long(ipAddress)); //ipaddress
		ps.setString(12, salt); //salt
		ps.setString(13, player); //username
	    ps.executeUpdate();
	     
	   /*  userid = MySQL.countitall(ForumAuth.forumPrefix+"user");
	    String oldcache =  MySQL.getfromtable(ForumAuth.forumPrefix+"datastore", "`data`", "title", "userstats");
	    Util.Log("info",oldcache);
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
	    		numusers = Util.removeChar(numusers,'"');
				numusers = Util.removeChar(numusers,'s');
				numusers = Util.removeChar(numusers,';');
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
	   //   Util.Log("info",i+"-"+st.nextToken()+":"); 
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
    }
    
    public static String hash(String action,String player,String password, String thesalt) throws SQLException {
    	if(action.equals("find"))
    	{
  	try {
  		String salt = MySQL.getfromtable(Config.database_prefix+"user", "`salt`", "username", player);
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
  	return Encryption.md5(Encryption.md5(password)+salt);
  	}
}