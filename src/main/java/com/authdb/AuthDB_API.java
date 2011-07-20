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
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class AuthDB_API {

    public static boolean CheckBan(Player player) {
        try {
            if (API.getScript("checkifbanned", player, null).equalsIgnoreCase("true")) { return true; }
        }
        catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return false;
    }

    public static boolean CheckBan(String IP) {
        try {
            if (API.getScript("checkifbanned", null, IP).equalsIgnoreCase("true")) { return true; }
        }
        catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return false;
    }

    public static String BanReason(Player player) {
        try {
            return API.getScript("banreason", player, null);
        }
        catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "noreason";
    }

    public static String BanReason(String IP) {
        try {
            return API.getScript("banreason", null, IP);
        }
        catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "noreason";
    }

    public static String BanUnixTimestamp(Player player) {
        try {
            String BanDate = API.getScript("bannedtodate", player, null);
            if (BanDate.equalsIgnoreCase("nodate")) { return "nodate"; } else if (BanDate.equalsIgnoreCase("perma")) { return "perma"; }
            String delimiter = "\\,";
            String[] Split = BanDate.split(delimiter);
            if (Split[1].equalsIgnoreCase("unix")) { return Split[0]; }
        }
        catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "nodate";
    }

    public static String GetGroup(Player player) {
        try {
            return API.getScript("getgroup", player, null);
        }
        catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "fail";
    }

    public static String Unix_Timestamp() {
        try {
            return MySQL.Unix_Timestamp();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "fail";
    }
}
