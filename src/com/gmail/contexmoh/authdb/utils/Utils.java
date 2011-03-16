package com.gmail.contexmoh.authdb.utils;


import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.bukkit.entity.Player;

import com.gmail.contexmoh.authdb.AuthDB;

public class Utils
{  
	public static void CheckIdle(Player player)
	{
		if (!AuthDB.isAuthorized(player.getEntityId()))
		{
			 Messages.SendMessage("kickPlayerIdleLoginMessage", player, null);
		}
	} 
	
	public static long IP2Long(String IP) {
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

	
	public static boolean stripUsernameCharacters(String username)
	{
		int lengtha = username.length();
		int lengthb = Config.specialCharactersList.length();
	    int i = 0;
	    char thechar1, thechar2;
	    while(i < lengtha)
	    {
	    	thechar1 = username.charAt(i);
	    	Log("info",""+thechar1);
	    	int a = 0;
	    	while(a < lengthb)
	    	{
	    		thechar2 = Config.specialCharactersList.charAt(a);
	    		Log("info",""+thechar2);
	    		if(thechar1 == thechar2) { return false; }
	    		a++;
	    	}
		    i++;
	    }
		return true;
	}
	
	public static boolean checkUsernameCharacters(String username)
	{
		int lengtha = username.length();
		int lengthb = Config.specialCharactersList.length();
	    int i = 0;
	    char thechar1, thechar2;
	    while(i < lengtha)
	    {
	    	thechar1 = username.charAt(i);
	    	int a = 0;
	    	while(a < lengthb)
	    	{
	    		thechar2 = Config.specialCharactersList.charAt(a);
	    		if(thechar1 == thechar2) { return false; }
	    		a++;
	    	}
		    i++;
	    }
		return true;
	}
	
	public static String replaceStrings(String string, Player player, String additional)
	{
		string = string.replaceAll("\\{IP\\}", GetIP(player));
		string = string.replaceAll("\\{PLAYER\\}", player.getName());
		string = string.replaceAll("\\{NEWPLAYER\\}", additional);
		string = string.replaceAll("\\{PLUGIN\\}", AuthDB.pluginname);
		string = string.replaceAll("\\{VERSION\\}", AuthDB.pluginversion);
		string = string.replaceAll("\\{IDLE_SECONDS\\}", additional);
		return string;
	}
	
	public static String removeColors(String toremove)
	{
		toremove = toremove.replace("�0", "");
		toremove = toremove.replace("�2", "");
		toremove = toremove.replace("�3", "");
		toremove = toremove.replace("�4", "");
		toremove = toremove.replace("�5", "");
		toremove = toremove.replace("�6", "");
		toremove = toremove.replace("�7", "");
		toremove = toremove.replace("�8", "");
		toremove = toremove.replace("�9", "");
		toremove = toremove.replace("�a", "");
		toremove = toremove.replace("�b", "");
		toremove = toremove.replace("�c", "");
		toremove = toremove.replace("�d", "");
		toremove = toremove.replace("�e", "");
		toremove = toremove.replace("�f", "");
		return toremove;
	}
	
	public static String removeChar(String s, char c) {
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