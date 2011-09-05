/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.authdb.plugins.ZPermissions;
import com.authdb.util.API;
import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.authdb.util.Messages.Message;
import com.authdb.util.databases.MySQL;

public class AuthDB_API {
    public boolean CheckBan(Player player) {
        try {
            if (API.getScript("checkifbanned", player, null).equalsIgnoreCase("true")) {
                return true;
            }
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return false;
    }

    public boolean CheckBan(String IP) {
        try {
            if (API.getScript("checkifbanned", null, IP).equalsIgnoreCase("true")) {
                return true;
            }
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return false;
    }

    public String BanReason(Player player) {
        try {
            return API.getScript("banreason", player, null);
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "noreason";
    }

    public String BanReason(String IP) {
        try {
            return API.getScript("banreason", null, IP);
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "noreason";
    }

    public String BanUnixTimestamp(Player player) {
        try {
            String BanDate = API.getScript("bannedtodate", player, null);
            if (BanDate.equalsIgnoreCase("nodate")) {
                return "nodate";
            } else if (BanDate.equalsIgnoreCase("perma")) {
                return "perma";
            }
            String delimiter = "\\,";
            String[] Split = BanDate.split(delimiter);
            if (Split[1].equalsIgnoreCase("unix")) {
                return Split[0];
            }
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "nodate";
    }

    public String GetGroup(Player player) {
        try {
            return API.getScript("getgroup", player, null);
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "fail";
    }

    public String Unix_Timestamp() {
        try {
            return MySQL.Unix_Timestamp();
        } catch (SQLException e) {
            // TODO: Auto-generated catch block
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return "fail";
    }

    public boolean banPlayer(Player player) {
        try {
            if (API.getScript("banplayer", player, null).equalsIgnoreCase("true")) {
                return true;
            }
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return false;
    }

    public boolean banIP(String ip) {
        try {
            if (API.getScript("banip", null, ip).equalsIgnoreCase("true")) {
                return true;
            }
        } catch (SQLException e) {
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return false;
    }

    public String getConfigPath() {
        return AuthDB.plugin.configFolder;
    }
    public File getDataFolder() {
        return AuthDB.plugin.getDataFolder();
    }
    public String getCommandsLanguage() {
        return Config.language_commands;
    }
    public String getMessagesLanguage() {
        return Config.language_messages;
    }
    public boolean hasPermissions(Player player, String permission) {
        return ZPermissions.isAllowed(player, permission);
    }
    public void noPermission(Player player) {
        Messages.sendMessage(Message.protection_denied, player, null);
    }
}
