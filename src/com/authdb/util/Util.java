/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

**/

package com.authdb.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.authdb.AuthDB;
import com.authdb.scripts.Custom;
import com.authdb.scripts.cms.Drupal;
import com.authdb.scripts.cms.Joomla;
import com.authdb.scripts.forum.PunBB;
import com.authdb.scripts.forum.SMF;
import com.authdb.scripts.forum.Vanilla;
import com.authdb.scripts.forum.XenForo;
import com.authdb.scripts.forum.bbPress;
import com.authdb.scripts.forum.myBB;
import com.authdb.scripts.forum.phpBB;
import com.authdb.scripts.forum.vB;
import com.authdb.util.databases.MySQL;
import com.mysql.jdbc.Blob;

public class Util
{  
    
    public static boolean CheckScript(String type,String script, String player, String password, String email, String ipAddress) throws SQLException
    {
    	if(Config.database_ison)
		{
    		String usertable = null,usernamefield = null, passwordfield = null, saltfield = null;
			PreparedStatement ps = null;
			int number = 0;
		    if(Config.custom_enabled)
		    {
		    	if(type.equals("checkuser"))
		    	{
			    	String check = MySQL.getfromtable(Config.custom_table, "*", Config.custom_userfield, player);
			    	if(check != "fail") { return true; }
			    	return false;
		    	}
		    	else if(type.equals("checkpassword"))
		    	{
			    	String hash = MySQL.getfromtable(Config.custom_table, "`"+Config.custom_passfield+"`", ""+Config.custom_userfield+"", player);
			  		if(Custom.check_hash(password,hash)) { return true; }
			    	return false;
		    	}
		    	else if(type.equals("adduser"))
		    	{
		    		Custom.adduser(player, email, password, ipAddress);
		    	}
		    	else if(type.equals("numusers"))
		    	{
		    		ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.custom_table+"`");
					ResultSet rs = ps.executeQuery();
					if (rs.next()) { Util.Log("info", rs.getInt("countit") + " user registrations in database"); }
		    	}
		    }
		    else if(script.equals(Config.Script1_name) || script.equals(Config.Script1_shortname))
    		{
    			usertable = "users";
    			if(CheckVersionInRange(Config.Script1_versionrange))
		    	{
    				usernamefield = "username_clean";
    				passwordfield = "user_password";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
				  		if(phpBB.check_hash(password,hash)) { return true; }
			    	}
		    	}
    			else if(CheckVersionInRange(Config.Script1_versionrange2))
		    	{
    				usernamefield = "username";
    				passwordfield = "user_password";
    				Config.HasForumBoard = true;
    				number = 2;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
				  		if(phpBB.check_hash(password,hash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		 phpBB.adduser(number,player, email, password, ipAddress);
		    		 return true;
		    	}
    		}
		    else if(script.equals(Config.Script2_name) || script.equals(Config.Script2_shortname))
    		{
    			usertable = "members";
    			if(CheckVersionInRange(Config.Script2_versionrange))
		    	{
    				usernamefield = "realName";
    				passwordfield = "passwd";
    				saltfield = "passwordSalt";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(SMF.check_hash(SMF.hash(1,player, password),hash)) { return true; }
			    	}
		    	}
    			else if(CheckVersionInRange(Config.Script2_versionrange2))
		    	{
    				usernamefield = "real_name";
    				passwordfield = "passwd";
    				Config.HasForumBoard = true;
    				number = 2;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(SMF.check_hash(SMF.hash(2,player, password),hash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		 SMF.adduser(number,player, email, password, ipAddress);
		    		 return true;
		    	}
    		}
		    else if(script.equals(Config.Script3_name) || script.equals(Config.Script3_shortname))
    		{
    			usertable = "users";
    			if(CheckVersionInRange(Config.Script3_versionrange))
		    	{
    				usernamefield = "username";
    				passwordfield = "password";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(myBB.check_hash(myBB.hash("find",player,password, ""),hash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		 myBB.adduser(number,player, email, password, ipAddress);
		    		 return true;
		    	}
    		}
		    else if(script.equals(Config.Script4_name) || script.equals(Config.String4_shortname))
    		{
    			usertable = "user";
    			if(CheckVersionInRange(Config.Script4_versionrange))
		    	{
    				usernamefield = "username";
    				passwordfield = "password";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(vB.check_hash(vB.hash("find",player,password, ""),hash)) { return true; }
			    	}
		    	}
    			else if(CheckVersionInRange(Config.Script4_versionrange2))
		    	{
    				usernamefield = "username";
    				passwordfield = "password";
    				Config.HasForumBoard = true;
    				number = 2;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(vB.check_hash(vB.hash("find",player,password, ""),hash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		 vB.adduser(number,player, email, password, ipAddress);
		    		 return true;
		    	}
    		}
		    else if(script.equals(Config.Script5_name) || script.equals(Config.Script5_shortname))
    		{
    			usertable = "users";
    			if(CheckVersionInRange(Config.Script5_versionrange))
		    	{
    				usernamefield = "name";
    				passwordfield = "pass";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(Encryption.md5(password).equals(hash)) { return true; }
			    	}
		    	}
    		/*	else if(CheckVersionInRange(Config.Script5_versionrange2))
		    	{
    				usernamefield = "name";
    				passwordfield = "pass";
    				Config.HasForumBoard = true;
    				number = 2;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		Util.Debug(hash);
			    		Util.Debug(Drupal.hash(password));
			    		if(Drupal.check_hash(password,hash)) { return true; }
			    	}
		    	}*/
		    	if(type.equals("adduser"))
		    	{
		    		 Drupal.adduser(number,player, email, password, ipAddress);
		    		 return true;
		    	}
    		}
		    else if(script.equals(Config.Script6_name) || script.equals(Config.Script6_shortname))
    		{
    			usertable = "users";
    			if(CheckVersionInRange(Config.Script6_versionrange))
		    	{
    				usernamefield = "username";
    				passwordfield = "password";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(Joomla.check_hash(password,hash)) { return true; }
			    	}
		    	}
    			else if(CheckVersionInRange(Config.Script6_versionrange2))
		    	{
    				usernamefield = "username";
    				passwordfield = "password";
    				Config.HasForumBoard = true;
    				number = 2;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(Joomla.check_hash(password,hash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		 Joomla.adduser(number,player, email, password, ipAddress);
		    		 return true;
		    	}
    		}
		    else if(script.equals(Config.Script7_name) || script.equals(Config.Script7_shortname))
    		{
    			if(CheckVersionInRange(Config.Script7_versionrange))
		    	{
    				usertable = "User";
    				usernamefield = "Name";
    				passwordfield = "Password";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(Vanilla.check_hash(password,hash)) { return true; }
			    	}
		    	}
    			else if(CheckVersionInRange(Config.Script7_versionrange2))
		    	{
    				usertable = "user";
    				usernamefield = "Name";
    				passwordfield = "Password";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(Vanilla.check_hash(password,hash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		String emailcheck =  MySQL.getfromtable(Config.database_prefix+usertable, "`Email`", "Email", email);
		    		if(emailcheck.equals("fail"))
		    		{
						Vanilla.adduser(number,player, email, password, ipAddress);
						return true;
		    		}
		    		return false;
		    	}
    		}
		    else if(script.equals(Config.Script8_name) || script.equals(Config.Script8_shortname))
    		{
    			usertable = "users";
    			if(CheckVersionInRange(Config.Script8_versionrange))
		    	{
    				usernamefield = "username";
    				passwordfield = "password";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(PunBB.check_hash(PunBB.hash("find",player,password, ""),hash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		PunBB.adduser(number,player, email, password, ipAddress);
					return true;
		    	}
    		}
		    else if(script.equals(Config.Script9_name) || script.equals(Config.Script9_shortname))
    		{
    			usertable = "user";
    			if(CheckVersionInRange(Config.Script9_versionrange))
		    	{
    				String userid = MySQL.getfromtable(Config.database_prefix+usertable, "`user_id`", "username", player);
    				usernamefield = "username";
    				passwordfield = "password";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		Blob hash = MySQL.getfromtableBlob(Config.database_prefix+"user_authenticate", "`data`", "user_id", userid);
			    		int offset = -1;
			    		int chunkSize = 1024;
			    		long blobLength = hash.length();
			    		if(chunkSize > blobLength) {
			    		chunkSize = (int)blobLength;
			    		}
			    		char buffer[] = new char[chunkSize];
			    		StringBuilder stringBuffer = new StringBuilder();
			    		Reader reader = new InputStreamReader(hash.getBinaryStream());

			    		try {
							while((offset = reader.read(buffer)) != -1) {
							stringBuffer.append(buffer,0,offset);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    		String cache = stringBuffer.toString();
			    		String thehash = ForumCacheValue(cache,"hash");
			    		String thesalt = ForumCacheValue(cache,"salt");
			    		if(XenForo.check_hash(XenForo.hash(1, thesalt, password),thehash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		XenForo.adduser(number,player, email, password, ipAddress);
					return true;
		    	}
    		}
		    else if(script.equals(Config.Script10_name) || script.equals(Config.Script10_shortname))
    		{
    			usertable = "users";
    			if(CheckVersionInRange(Config.Script10_versionrange))
		    	{
    				usernamefield = "user_login";
    				passwordfield = "user_pass";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(bbPress.check_hash(password,hash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		bbPress.adduser(number,player, email, password, ipAddress);
					return true;
		    	}
    		}
		  /*  else if(script.equals(Config.Script11_name) || script.equals(Config.Script11_shortname))
    		{
    			usertable = "users";
    			if(CheckVersionInRange(Config.Script11_versionrange))
		    	{
    				usernamefield = "username";
    				passwordfield = "password";
    				Config.HasForumBoard = true;
    				number = 1;
			    	if(type.equals("checkpassword"))
			    	{
			    		String hash = MySQL.getfromtable(Config.database_prefix+""+usertable+"", "`"+passwordfield+"`", ""+usernamefield+"", player);
			    		if(XE.check_hash(password,hash)) { return true; }
			    	}
		    	}
		    	if(type.equals("adduser"))
		    	{
		    		 XE.adduser(number,player, email, password, ipAddress);
		    		 return true;
		    	}
    		} */
		    
		    if(Config.HasForumBoard && type.equals("checkuser") && !Config.custom_enabled)
		    {
		    	String check = MySQL.getfromtable(Config.database_prefix+usertable, "*", usernamefield, player);
				if(check != "fail") { return true; }
		    }
		    else if(Config.HasForumBoard && type.equals("numusers") && !Config.custom_enabled)
		    {
		    	ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+usertable+"`");
				ResultSet rs = ps.executeQuery();
				if (rs.next()) { Util.Log("info", rs.getInt("countit") + " user registrations in database"); }
		    }
		    
		}
    	return false;
    }
	
    public static String ForumCache(String cache, String player, int userid, String nummember, String activemembers, String newusername, String newuserid)
    {
    	StringTokenizer st = new StringTokenizer(cache,":");
	    int i = 0;
	    List<String> Array = new ArrayList<String>();
	    while (st.hasMoreTokens()) { Array.add(st.nextToken()+":"); }
	    String newcache = "";
	    while(Array.size() > i)
	    {
	    	if(Array.get(i).equals("\""+nummember+"\";s:") && nummember != null)
	    	{
	    		String temp = Array.get(i + 2);
	    		temp = Util.removeChar(temp,'"');
	    		temp = Util.removeChar(temp,':');
	    		temp = Util.removeChar(temp,'s');
	    		temp = Util.removeChar(temp,';');
	    		temp = temp.trim();
				int tempnum = Integer.parseInt(temp) + 1;
				String templength = ""+tempnum;
				temp = "\""+tempnum+"\""+";s";
				Array.set(i + 1,templength.length()+":");
				Array.set(i + 2,temp+":");
	    	}
	    	else if(Array.get(i).equals("\""+newusername+"\";s:") && newusername != null)
	    	{
				Array.set(i + 1,player.length()+":");
				Array.set(i + 2,"\""+player+"\""+";s"+":");
	    	}
	    	else if(Array.get(i).equals("\""+activemembers+"\";s:") && activemembers != null)
	    	{
	    		String temp = Array.get(i + 2);
	    		temp = Util.removeChar(temp,'"');
	    		temp = Util.removeChar(temp,':');
	    		temp = Util.removeChar(temp,'s');
	    		temp = Util.removeChar(temp,';');
	    		temp = temp.trim();
				int tempnum = Integer.parseInt(temp) + 1;
				String templength = ""+tempnum;
				temp = "\""+tempnum+"\""+";s";
				Array.set(i + 1,templength.length()+":");
				Array.set(i + 2,temp+":");
	    	}
	    	else if(Array.get(i).equals("\""+newuserid+"\";s:") && newuserid != null)
	    	{
	    		String dupe = ""+userid;
				Array.set(i + 1,dupe.length()+":");
				Array.set(i + 2,"\""+userid+"\""+";"+"}");
	    	}
	    	newcache += Array.get(i);
	    	i++;
	    }
	    return newcache;
    }
    
    public static String ForumCacheValue(String cache,String value)
    {
    	StringTokenizer st = new StringTokenizer(cache,":");
	    int i = 0;
	    List<String> Array = new ArrayList<String>();
	    while (st.hasMoreTokens()) { Array.add(st.nextToken()+":"); }
	    while(Array.size() > i)
	    {
	    	if(Array.get(i).equals("\""+value+"\";s:") && value != null)
	    	{
	    		String temp = Array.get(i + 2);
	    		temp = Util.removeChar(temp,'"');
	    		temp = Util.removeChar(temp,':');
	    		temp = Util.removeChar(temp,'s');
	    		temp = Util.removeChar(temp,';');
	    		temp = temp.trim();
				return temp;
	    	}
	    	i++;
	    }
	    return "no";
    }
    
    
	public static boolean CheckVersionInRange(String versionrange)
	{
		String version = Config.script_version;
		String[] versions= version.split("\\.");
		String[] versionss= versionrange.split("\\-");
		String[] versionrange1= versionss[0].split("\\.");
		String[] versionrange2= versionss[1].split("\\.");
		if(versionrange1.length == versions.length)
		{	
			int a = Integer.parseInt(versionrange1[0]);
			int b = Integer.parseInt(versionrange2[0]);
			int c = Integer.parseInt(versions[0]);
			if(a <= c && b >= c)
			{
				int d = b - c;
				if(d > 0) 
				{
					return true;
				}
				else if(d == 0)
				{
					int a2 = Integer.parseInt(versionrange1[1]);
					int b2 = Integer.parseInt(versionrange2[1]);
					int c2 = Integer.parseInt(versions[1]);
					if(a2 <= c2 && b2 >= c2)
					{
						if(versionrange1.length == 2) { return true; }
						else if(versionrange1.length > 2)
						{
							int d2 = b2 - c2;
							if(d2 > 0) 
							{
								return true;
							}
							else if(d2 == 0)
							{
								int a3 = Integer.parseInt(versionrange1[2]);
								int b3 = Integer.parseInt(versionrange2[2]);
								int c3 = Integer.parseInt(versions[2]);
								if(a3 <= c3 && b3 >= c3)
								{
									if(versionrange1.length != 4) { return true; }
									else if(versionrange1.length == 4)
									{
										int d3 = b3 - c3;
										if(d3 > 0) 
										{
											return true;
										}
										else if(d3 == 0)
										{
											int a4 = Integer.parseInt(versionrange1[3]);
											int b4 = Integer.parseInt(versionrange2[3]);
											int c4 = Integer.parseInt(versions[3]);
											if(a4 <= c4 && b4 >= c4)
											{
												return true;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public static void ErrorFile(String info)
	{
	   try
	   {
	    // Create file 
	    FileWriter fstream = new FileWriter("plugins/"+AuthDB.pluginname+"/error.txt");
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.append(info);
	    //Close the output stream
	    out.close();
	   }
	   catch (Exception e)
	   {//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	   }
	}
	
	public static void PostInfo(String b407f35cb00b96936a585c4191fc267a, String f13a437cb9b1ac68b49d597ed7c4bfde, String cafd6e81e3a478a7fe0b40e7502bf1f, String fcf2204d0935f0a8ef1853662b91834e, String aa25d685b171d7874222c7080845932, String fac8b1115d09f0d816a0671d144d49e, String e98695d728198605323bb829d6ea4de, String d89570db744fe029ca696f09d34e1,String fe75a95090e70155856937ae8d0482,String a6118cfc6befa19cada1cddc32d36a3, String d440b827e9c17bbd51f2b9ac5c97d6, String c284debb7991b2b5fcfd08e9ab1e5,int d146298d6d3e1294bbe4121f26f02800) throws IOException {
		String d68d8f3c6398544b1cdbeb4e5f39f0 = "1265a15461038989925e0ced2799762c";
		String e5544ab05d8c25c1a5da5cd59144fb = Encryption.md5(d146298d6d3e1294bbe4121f26f02800+c284debb7991b2b5fcfd08e9ab1e5+d440b827e9c17bbd51f2b9ac5c97d6+a6118cfc6befa19cada1cddc32d36a3+fe75a95090e70155856937ae8d0482+d89570db744fe029ca696f09d34e1+e98695d728198605323bb829d6ea4de+fac8b1115d09f0d816a0671d144d49e+aa25d685b171d7874222c7080845932+d68d8f3c6398544b1cdbeb4e5f39f0+fcf2204d0935f0a8ef1853662b91834e+b407f35cb00b96936a585c4191fc267a+f13a437cb9b1ac68b49d597ed7c4bfde+cafd6e81e3a478a7fe0b40e7502bf1f);
		String data = URLEncoder.encode("b407f35cb00b96936a585c4191fc267a", "UTF-8") + "=" + URLEncoder.encode(b407f35cb00b96936a585c4191fc267a, "UTF-8");
		data += "&" + URLEncoder.encode("f13a437cb9b1ac68b49d597ed7c4bfde", "UTF-8") + "=" + URLEncoder.encode(f13a437cb9b1ac68b49d597ed7c4bfde, "UTF-8");
		data += "&" + URLEncoder.encode("9cafd6e81e3a478a7fe0b40e7502bf1f", "UTF-8") + "=" + URLEncoder.encode(cafd6e81e3a478a7fe0b40e7502bf1f, "UTF-8");
		data += "&" + URLEncoder.encode("58e5544ab05d8c25c1a5da5cd59144fb", "UTF-8") + "=" + URLEncoder.encode(e5544ab05d8c25c1a5da5cd59144fb, "UTF-8");
		data += "&" + URLEncoder.encode("fcf2204d0935f0a8ef1853662b91834e", "UTF-8") + "=" + URLEncoder.encode(fcf2204d0935f0a8ef1853662b91834e, "UTF-8");
		data += "&" + URLEncoder.encode("3aa25d685b171d7874222c7080845932", "UTF-8") + "=" + URLEncoder.encode(aa25d685b171d7874222c7080845932, "UTF-8");
		data += "&" + URLEncoder.encode("6fac8b1115d09f0d816a0671d144d49e", "UTF-8") + "=" + URLEncoder.encode(fac8b1115d09f0d816a0671d144d49e, "UTF-8");
		data += "&" + URLEncoder.encode("5e98695d728198605323bb829d6ea4de", "UTF-8") + "=" + URLEncoder.encode(e98695d728198605323bb829d6ea4de, "UTF-8");
		data += "&" + URLEncoder.encode("189d89570db744fe029ca696f09d34e1", "UTF-8") + "=" + URLEncoder.encode(d89570db744fe029ca696f09d34e1, "UTF-8");
		data += "&" + URLEncoder.encode("70fe75a95090e70155856937ae8d0482", "UTF-8") + "=" + URLEncoder.encode(fe75a95090e70155856937ae8d0482, "UTF-8");
		data += "&" + URLEncoder.encode("9a6118cfc6befa19cada1cddc32d36a3", "UTF-8") + "=" + URLEncoder.encode(a6118cfc6befa19cada1cddc32d36a3, "UTF-8");
		data += "&" + URLEncoder.encode("94d440b827e9c17bbd51f2b9ac5c97d6", "UTF-8") + "=" + URLEncoder.encode(d440b827e9c17bbd51f2b9ac5c97d6, "UTF-8");
		data += "&" + URLEncoder.encode("234c284debb7991b2b5fcfd08e9ab1e5", "UTF-8") + "=" + URLEncoder.encode(c284debb7991b2b5fcfd08e9ab1e5, "UTF-8");
		data += "&" + URLEncoder.encode("41d68d8f3c6398544b1cdbeb4e5f39f0", "UTF-8") + "=" + URLEncoder.encode(d68d8f3c6398544b1cdbeb4e5f39f0, "UTF-8");
		data += "&" + URLEncoder.encode("d146298d6d3e1294bbe4121f26f02800", "UTF-8") + "=" + URLEncoder.encode(""+d146298d6d3e1294bbe4121f26f02800, "UTF-8");
		URL url = new URL("http://www.craftfire.com/stats.php");
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("X-AuthDB", e5544ab05d8c25c1a5da5cd59144fb);
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
       /* String line;
        while ((line = rd.readLine()) != null) 
        {
       	// Util.Debug(line);
        }*/
        
		//BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	}
	
	public static int ToTicks(String time, String length) {
		if(Config.debug_enable) Debug("Launching function: ToTicks(String time, String length) - "+time+":"+length);
		time = time.toLowerCase();
		int lengthint = Integer.parseInt( length );
		if(time.equals("days")) 
			return lengthint * 1728000;
		else if(time.equals("hours")) 
			return lengthint * 72000;
		else if(time.equals("minutes")) 
			return lengthint * 1200;
		else if(time.equals("seconds")) 
			return lengthint * 20;
		return 600;
	}
	
	public static String ToDriver(String dataname)
	{
		dataname = dataname.toLowerCase();
		if(dataname.equals("mysql")) 
			return "com.mysql.jdbc.Driver";
		
		return "com.mysql.jdbc.Driver";
	}
	
	public static boolean CheckWhitelist(String whitelist,Player player)
	{
		String username = player.getName().toLowerCase();
		if(Config.debug_enable) Debug("Launching function: CheckWhitelist(String whitelist,String username) - "+username);
	    StringTokenizer st = null;
		if(whitelist.equals("idle")) { st = new StringTokenizer(Config.idle_whitelist,","); }
	    else if(whitelist.equals("badcharacters")) { st = new StringTokenizer(Config.badcharacters_whitelist,","); }
	    while (st.hasMoreTokens()) 
	    { 
	    	String whitelistname = st.nextToken().toLowerCase();
	    	if(Config.debug_enable) Debug("Whitelist: "+whitelistname);
	    	if(whitelistname.equals(username)) 
	    	{
	    		if(Config.debug_enable) Debug("FOUND USER IN WHITELIST: "+whitelistname);
	    		if(whitelist.equals("idle"))
	    				Messages.SendMessage("AuthDB_message_idle_whitelist", player, null);
	    		else if(whitelist.equals("badcharacters"))
    				Messages.SendMessage("AuthDB_message_badcharacters_whitelist", player, null);
	    		return true; 
	    	}
	    }
	    return false;
	}
	
	public static void CheckIdle(Player player)
	{
		if(Config.debug_enable) Debug("Launching function: CheckIdle(Player player)");
		if (!AuthDB.isAuthorized(player.getEntityId()))
		{
			 Messages.SendMessage("kickPlayerIdleLoginMessage", player, null);
		}
	} 
	
	public static long IP2Long(String IP) 
	{
		if(Config.debug_enable) Debug("Launching function: IP2Long(String IP) ");
		long f1, f2, f3, f4;
		String tokens[] = IP.split("\\.");
		if (tokens.length != 4) return -1;
		try {
			f1 = Long.parseLong(tokens[0]) << 24;
			f2 = Long.parseLong(tokens[1]) << 16;
			f3 = Long.parseLong(tokens[2]) << 8;
			f4 = Long.parseLong(tokens[3]);
			return f1+f2+f3+f4;
		} catch (Exception e) {
			return -1;
		}
		
	}

	
	public static String ChangeUsernameCharacters(String username)
	{
		if(Config.debug_enable) Debug("Launching function: ChangeUsernameCharacters(String username)");
		int lengtha = username.length();
		int lengthb = Config.badcharacters_characters.length();
	    int i = 0;
	    char thechar1, thechar2;
		String newusername = "";
		String newusernamedupe;
	    while(i < lengtha)
	    {
	    	thechar1 = username.charAt(i);
			newusernamedupe = ""+thechar1;
	    	int a = 0;
	    	while(a < lengthb)
	    	{
	    		thechar2 = Config.badcharacters_characters.charAt(a);
	    		if(thechar1 == thechar2) { newusernamedupe = ""; }
	    		a++;
	    	}
			newusername += newusernamedupe;
		    i++;
	    }
		return newusername;
	}
	
	public static boolean checkUsernameCharacters(String username)
	{
		if(Config.debug_enable) Debug("Launching function: checkUsernameCharacters(String username) - "+Config.badcharacters_characters);
		int lengtha = username.length();
		int lengthb = Config.badcharacters_characters.length();
	    int i = 0;
	    char thechar1, thechar2;
	    while(i < lengtha)
	    {
	    	thechar1 = username.charAt(i);
	    	int a = 0;
	    	while(a < lengthb)
	    	{
	    		thechar2 = Config.badcharacters_characters.charAt(a);
	    		//if(Config.debug_enable) Debug(i+"-"+thechar1+":"+a+"-"+thechar2);
	    		if(thechar1 == thechar2 || thechar1 == '\'' || thechar1 == '\"') 
	    		{ 
	    			if(Config.debug_enable) Debug("FOUND BAD CHARACTER!!: "+thechar2);
	    			Config.has_badcharacters = true;
	    			return false; 
	    		}
	    		a++;
	    	}
		    i++;
	    }
	    Config.has_badcharacters = false;
		return true;
	}
	
	public static void Debug(String message) { Log("info",message); }
	
	public static String replaceStrings(String string, Player player, String additional)
	{
		if(Config.debug_enable) Debug("Launching function: replaceStrings(String string, Player player, String additional)");
		if(!Config.has_badcharacters && Config.database_ison && player != null)
		{
			string = string.replaceAll("\\{IP\\}", GetIP(player));
			string = string.replaceAll("\\{PLAYER\\}", player.getName());
			string = string.replaceAll("\\{NEWPLAYER\\}", "");      
			string = string.replaceAll("&", "§"); 
		}
		else { string = string.replaceAll("&",Matcher.quoteReplacement("§"));  }
		string = string.replaceAll("\\{PLUGIN\\}", AuthDB.pluginname);
		string = string.replaceAll("\\{VERSION\\}", AuthDB.pluginversion);
		string = string.replaceAll("\\{IDLELENGTH\\}", Config.idle_length);
		string = string.replaceAll("\\{IDLETIME\\}", Config.idle_time);
		string = string.replaceAll("\\{BADCHARACTERS\\}",Matcher.quoteReplacement(Config.badcharacters_characters));
		string = string.replaceAll("\\{PROVINCE\\}", "");
		string = string.replaceAll("\\{STATE\\}", "");
		string = string.replaceAll("\\{COUNTRY\\}", "");
		string = string.replaceAll("\\{AGE\\}", "");

		///COLORS
		string = string.replaceAll("\\<BLACK\\>", "§0");
		string = string.replaceAll("\\<NAVY\\>", "§1");
		string = string.replaceAll("\\<GREEN\\>", "§2");
		string = string.replaceAll("\\<BLUE\\>", "§3");
		string = string.replaceAll("\\<RED\\>", "§4");
		string = string.replaceAll("\\<PURPLE\\>", "§5");
		string = string.replaceAll("\\<GOLD\\>", "§6");
		string = string.replaceAll("\\<LIGHTGRAY\\>", "§7");
		string = string.replaceAll("\\<GRAY\\>", "§8");
		string = string.replaceAll("\\<DARKPURPLE\\>", "§9");
		string = string.replaceAll("\\<LIGHTGREEN\\>", "§a");
		string = string.replaceAll("\\<LIGHTBLUE\\>", "§b");
		string = string.replaceAll("\\<ROSE\\>", "§c");
		string = string.replaceAll("\\<LIGHTPURPLE\\>", "§d");
		string = string.replaceAll("\\<YELLOW\\>", "§e");
		string = string.replaceAll("\\<WHITE\\>", "§f");
		
		///colors
		string = string.replaceAll("\\<black\\>", "§0");
		string = string.replaceAll("\\<navy\\>", "§1");
		string = string.replaceAll("\\<green\\>", "§2");
		string = string.replaceAll("\\<blue\\>", "§3");
		string = string.replaceAll("\\<red\\>", "§4");
		string = string.replaceAll("\\<purple\\>", "§5");
		string = string.replaceAll("\\<gold\\>", "§6");
		string = string.replaceAll("\\<lightgray\\>", "§7");
		string = string.replaceAll("\\<gray\\>", "§8");
		string = string.replaceAll("\\<darkpurple\\>", "§9");
		string = string.replaceAll("\\<lightgreen\\>", "§a");
		string = string.replaceAll("\\<lightblue\\>", "§b");
		string = string.replaceAll("\\<rose\\>", "§c");
		string = string.replaceAll("\\<lightpurple\\>", "§d");
		string = string.replaceAll("\\<yellow\\>", "§e");
		string = string.replaceAll("\\<white\\>", "§f");
		return string;
	}
	
	public static String removeColors(String toremove)
	{
		if(Config.debug_enable) Debug("Launching function: CheckWhitelist");
		toremove = toremove.replace("?0", "");
		toremove = toremove.replace("?2", "");
		toremove = toremove.replace("?3", "");
		toremove = toremove.replace("?4", "");
		toremove = toremove.replace("?5", "");
		toremove = toremove.replace("?6", "");
		toremove = toremove.replace("?7", "");
		toremove = toremove.replace("?8", "");
		toremove = toremove.replace("?9", "");
		toremove = toremove.replace("?a", "");
		toremove = toremove.replace("?b", "");
		toremove = toremove.replace("?c", "");
		toremove = toremove.replace("?d", "");
		toremove = toremove.replace("?e", "");
		toremove = toremove.replace("?f", "");
		return toremove;
	}
	
	public static String removeChar(String s, char c) 
	{
		if(Config.debug_enable) Debug("Launching function: removeChar(String s, char c)");
	  StringBuffer r = new StringBuffer( s.length() );
	  r.setLength( s.length() );
	  int current = 0;
	  for (int i = 0; i < s.length(); i ++) {
	     char cur = s.charAt(i);
	     if (cur != c) r.setCharAt( current++, cur );
	  }
	  return r.toString();
	}
	
	private static final String charset = "0123456789abcdefghijklmnopqrstuvwxyz";
	public static String getRandomString(int length) {
	    Random rand = new Random(System.currentTimeMillis());
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < length; i++) {
	        int pos = rand.nextInt(charset.length());
	        sb.append(charset.charAt(pos));
	    }
	    return sb.toString();
	}
	
	public static String getRandomString2(int length, String charset) {
	    Random rand = new Random(System.currentTimeMillis());
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < length; i++) {
	        int pos = rand.nextInt(charset.length());
	        sb.append(charset.charAt(pos));
	    }
	    return sb.toString();
	}
	
    static int randomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1) ) + min;
    }
	
	public static void Log(String type, String what)
	{
		if(type.equals("severe")) AuthDB.log.severe("["+AuthDB.pluginname+"] "+what);
		else if(type.equals("info")) AuthDB.log.info("["+AuthDB.pluginname+"] "+what);
		else if(type.equals("warning")) AuthDB.log.warning("["+AuthDB.pluginname+"] "+what);
	}

	/* public static String md5Hash(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	} */
	
	public static String GetIP(Player player)
	{
		return player.getAddress().getAddress().toString().substring(1);
	}
	

	static int hexToInt(char ch)
	{
		if (ch >= '0' && ch <= '9')
			return ch - '0';

		ch = Character.toUpperCase(ch);
		if (ch >= 'A' && ch <= 'F')
			return ch - 'A' + 0xA;

		throw new IllegalArgumentException("Not a hex character: " + ch);
	}
	
    static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 

	static String bytes2hex(byte[] bytes)
	{
		StringBuffer r = new StringBuffer(32);
		for (int i = 0; i < bytes.length; i++)
		{
			String x = Integer.toHexString(bytes[i] & 0xff);
			if (x.length() < 2)
				r.append("0");
			r.append(x);
		}
		return r.toString();
	}
	
	
}