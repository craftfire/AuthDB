package com.authdb;

import java.sql.SQLException;
import java.util.regex.Matcher;

import org.bukkit.entity.Player;

import com.authdb.util.API;
import com.authdb.util.Config;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class AuthDB_API {
	
    public static boolean CheckBan(Player player)
    {
    	try 
    	{
    		if(API.GetScript("checkifbanned", player, null).equals("true")) { return true; }
		} 
    	catch (SQLException e) 
    	{
    		e.printStackTrace();
    	}
    	return false;
    }
    
    public static boolean CheckBan(String IP)
    {
    	try 
    	{
    		if(API.GetScript("checkifbanned", null, IP).equals("true")) { return true; }
		} 
    	catch (SQLException e) 
    	{
    		e.printStackTrace();
    	}
    	return false;
    }
    
    public static String BanReason(Player player)
    {
    	try 
    	{
    		return API.GetScript("banreason", player, null);
		} 
    	catch (SQLException e) 
    	{
    		e.printStackTrace();
    	}
    	return "noreason";
    }
    
    public static String BanReason(String IP)
    {
    	try 
    	{
    		return API.GetScript("banreason", null, IP);
		} 
    	catch (SQLException e) 
    	{
    		e.printStackTrace();
    	}
    	return "noreason";
    }
    
    public static String BanUnixTimestamp(Player player)
    {
    	try 
    	{
    		String BanDate = API.GetScript("bannedtodate", player, null);
    		if(BanDate.equals("nodate")) { return "nodate"; }
    		else if(BanDate.equals("perma")) { return "perma"; }
    		String delimiter = "\\,";
			String[] Split = BanDate.split(delimiter);
			if(Split[1].equals("unix")) { return Split[0]; }
		} 
    	catch (SQLException e) 
    	{
    		e.printStackTrace();
    	}
    	return "nodate";
    }
    
    public static String GetGroup(Player player)
    {
    	try 
    	{
    		return API.GetScript("getgroup", player, null);
		} 
    	catch (SQLException e) 
    	{
    		e.printStackTrace();
    	}
    	return "fail";
    }
    
    public static String Unix_Timestamp()
    {
    	try {
			return MySQL.Unix_Timestamp();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
    }
}
