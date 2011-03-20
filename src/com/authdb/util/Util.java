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

package com.authdb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.authdb.AuthDB;
import com.authdb.util.databases.MySQL;

public class Util
{  
	
	public static void NumberUsers() throws ClassNotFoundException, SQLException
	{
        MySQL.connect();
		PreparedStatement ps = null;
		if(Config.script_name.equals(Config.script_name1)) { ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"users"+"`"); }
		else if(Config.script_name.equals(Config.script_name2)) { ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"members"+"`"); }
		else if(Config.script_name.equals(Config.script_name3)) { ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"members"+"`"); }
		else if(Config.script_name.equals(Config.script_name4)) { ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"users"+"`"); }
		else if(Config.script_name.equals(Config.script_name5)) { ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"user"+"`"); }
		ResultSet rs = ps.executeQuery();
		if (rs.next()) { Util.Log("info", rs.getInt("countit") + " user registrations in database"); }
		MySQL.close();
	}
	public static String CheckVersion(String script,String latest, int length)
	{
		String[] latestsplit= Pattern.compile(".").split(Config.script_version);
		if(latestsplit.length != length)
		{
			Util.Debug("The length of the version number is not equal to the length of what is required: "+length+". Setting to latest version of script: "+script+" "+latest);
			return latest;
		}
		return Config.script_version;
	}
	
	public static void PostInfo(String b407f35cb00b96936a585c4191fc267a, String f13a437cb9b1ac68b49d597ed7c4bfde, String cafd6e81e3a478a7fe0b40e7502bf1f) throws IOException {
		String e5544ab05d8c25c1a5da5cd59144fb = Util.md5(b407f35cb00b96936a585c4191fc267a+f13a437cb9b1ac68b49d597ed7c4bfde+cafd6e81e3a478a7fe0b40e7502bf1f);
		String data = URLEncoder.encode("b407f35cb00b96936a585c4191fc267a", "UTF-8") + "=" + URLEncoder.encode(b407f35cb00b96936a585c4191fc267a, "UTF-8");
		data += "&" + URLEncoder.encode("f13a437cb9b1ac68b49d597ed7c4bfde", "UTF-8") + "=" + URLEncoder.encode(f13a437cb9b1ac68b49d597ed7c4bfde, "UTF-8");
		data += "&" + URLEncoder.encode("9cafd6e81e3a478a7fe0b40e7502bf1f", "UTF-8") + "=" + URLEncoder.encode(cafd6e81e3a478a7fe0b40e7502bf1f, "UTF-8");
		data += "&" + URLEncoder.encode("58e5544ab05d8c25c1a5da5cd59144fb", "UTF-8") + "=" + URLEncoder.encode(e5544ab05d8c25c1a5da5cd59144fb, "UTF-8");
		URL url = new URL("http://moincraft.com/plugins/AuthDB/stats.php");
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("X-AuthDB", e5544ab05d8c25c1a5da5cd59144fb);
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();
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
	
	public static boolean CheckWhitelist(String username)
	{
		username = username.toLowerCase();
		if(Config.debug_enable) Debug("Launching function: CheckWhitelist(String username) - "+username);
	    StringTokenizer st = new StringTokenizer(Config.idle_whitelist,",");
	    while (st.hasMoreTokens()) 
	    { 
	    	String whitelistname = st.nextToken().toLowerCase();
	    	if(Config.debug_enable) Debug("Whitelist: "+whitelistname);
	    	if(whitelistname.equals(username)) 
	    	{
	    		if(Config.debug_enable) Debug("FOUND USER IN WHITELIST: "+whitelistname);
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
	    		if(Config.debug_enable) Debug(i+"-"+thechar1+":"+a+"-"+thechar2);
	    		if(thechar1 == thechar2) 
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
		if(!Config.has_badcharacters)
		{
			string = string.replaceAll("\\{IP\\}", GetIP(player));
			string = string.replaceAll("\\{PLAYER\\}", player.getName());
			string = string.replaceAll("\\{NEWPLAYER\\}", "");
		}
		string = string.replaceAll("\\{PLUGIN\\}", AuthDB.pluginname);
		string = string.replaceAll("\\{VERSION\\}", AuthDB.pluginversion);
		string = string.replaceAll("\\{IDLELENGTH\\}", Config.idle_length);
		string = string.replaceAll("\\{IDLETIME\\}", Config.idle_time);
		string = string.replaceAll("\\{BADCHARACTERS\\}",Matcher.quoteReplacement(Config.badcharacters_characters));
		string = string.replaceAll("\\{PROVINCE\\}", "");
		string = string.replaceAll("\\{STATE\\}", "");
		string = string.replaceAll("\\{COUNTRY\\}", "");
		string = string.replaceAll("\\{AGE\\}", "");
		string = string.replaceAll("&", "§");
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
	
	public static String fetch_user_salt(int length)
	{
		String salt = "";
		for (int i = 0; i < length; i++)
		{
			salt += (char)(randomNumber(33, 126));
		}
		return salt;
	}

	
    private static int randomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1) ) + min;
    }

	private static final String charset2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static String getRandomString2(int length) {
	    Random rand = new Random(System.currentTimeMillis());
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < length; i++) {
	        int pos = rand.nextInt(charset2.length());
	        sb.append(charset2.charAt(pos));
	    }
	    return sb.toString();
	}
	
	
	public static void Log(String type, String what)
	{
		if(type.equals("severe")) AuthDB.log.severe("["+AuthDB.pluginname+"] "+what);
		else if(type.equals("info")) AuthDB.log.info("["+AuthDB.pluginname+"] "+what);
		else if(type.equals("warning")) AuthDB.log.warning("["+AuthDB.pluginname+"] "+what);
	}

	public static String md5Hash(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	}
	
	public static String md5(String data)
	{
		try
		{
			byte[] bytes = data.getBytes("ISO-8859-1");
			MessageDigest md5er = MessageDigest.getInstance("MD5");
			byte[] hash = md5er.digest(bytes);
			return bytes2hex(hash);
		}
		catch (GeneralSecurityException e)
		{
			throw new RuntimeException(e);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static String GetIP(Player player)
	{
		return player.getAddress().getAddress().toString().substring(1);
	}
	
    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
    MessageDigest md;
    md = MessageDigest.getInstance("SHA-1");
    byte[] sha1hash = new byte[40];
    md.update(text.getBytes("iso-8859-1"), 0, text.length());
    sha1hash = md.digest();
    return convertToHex(sha1hash);
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
	
    private static String convertToHex(byte[] data) { 
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

	private static String bytes2hex(byte[] bytes)
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

	public static String pack(String hex)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < hex.length(); i += 2)
		{
			char c1 = hex.charAt(i);
			char c2 = hex.charAt(i + 1);
			char packed = (char) (hexToInt(c1) * 16 + hexToInt(c2));
			buf.append(packed);
		}
		return buf.toString();
	}
	
	
}