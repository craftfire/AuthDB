/*
 * This file is part of AuthDB.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
 * AuthDB is licensed under the GNU Lesser General Public License.
 *
 * AuthDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.authdb;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.craftfire.authdb.plugins.ZPermissions;
import com.craftfire.authdb.util.API;
import com.craftfire.authdb.util.Config;
import com.craftfire.authdb.util.Messages;
import com.craftfire.authdb.util.Util;
import com.craftfire.authdb.util.Messages.Message;
import com.craftfire.authdb.util.databases.MySQL;

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
