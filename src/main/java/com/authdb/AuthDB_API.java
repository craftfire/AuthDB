/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb;

import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.authdb.util.API;
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
