/*
 * This file is part of AuthDB <http://www.authdb.com/>.
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
package com.authdb.util;

import com.authdb.AuthDB;
import com.authdb.util.Messages.Message;
import com.authdb.util.databases.EBean;
import com.authdb.util.encryption.Encryption;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Processes {
    public static void Quit(Player player) {
        Util.craftFirePlayer.setInventoryFromStorage(player);
        Logout(player, false);
    }

    public static boolean Logout(Player player, boolean storeInventory) {
        if (AuthDB.isAuthorized(player)) {
            if (AuthDB.AuthDB_AuthTime.containsKey(player.getName())) {
                AuthDB.AuthDB_AuthTime.remove(player.getName());
            }

            AuthDB.authorizedNames.remove(player.getName());
            EBean eBeanClass = EBean.checkPlayer(player, true);
            eBeanClass.setAuthorized("false");

            if (AuthDB.AuthDB_Authed.containsKey(Encryption.md5(player.getName()))) {
                AuthDB.AuthDB_Authed.remove(Encryption.md5(player.getName()));
            }
            if (AuthDB.AuthDB_Sessions.containsKey(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)))) {
                AuthDB.AuthDB_Sessions.remove(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)));
            }
            if (AuthDB.AuthDB_SpamMessage.containsKey(player.getName())) {
                AuthDB.server.getScheduler().cancelTask(AuthDB.AuthDB_SpamMessage.get(player.getName()));
                AuthDB.AuthDB_SpamMessage.remove(player.getName());
                AuthDB.AuthDB_SpamMessageTime.remove(player.getName());
            }
            if (AuthDB.AuthDB_Timeouts.containsKey(player.getName())) {
                int TaskID = AuthDB.AuthDB_Timeouts.get(player.getName());
                Util.logging.Debug(player.getName() + " is in the TimeoutTaskList with ID: " + TaskID);
                eBeanClass.setTimeoutid(0);
                if (AuthDB.AuthDB_Timeouts.remove(player.getName()) != null) {
                    Util.logging.Debug(player.getName() + " was removed from the TimeoutTaskList");
                    AuthDB.server.getScheduler().cancelTask(TaskID);
                } else {
                    Util.logging.Debug("Could not remove " + player.getName() + " from the timeout list.");
                }
            } else {
                Util.logging.Debug("Could not find " + player.getName() + " in the timeout list, no need to remove.");
            }
            AuthDB.database.save(eBeanClass);
            if (storeInventory) {
                try {
                    Util.craftFirePlayer.storeInventory(player, player.getInventory().getContents(), player.getInventory().getArmorContents());
                } catch (IOException e) {
                    Util.logging.Severe("[" + AuthDB.pluginName + "] Inventory file error:");
                    player.kickPlayer("Inventory protection kicked.");
                    Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                }
                player.getInventory().clear();
            }
            Util.logging.Debug("Logged out player: " + player.getName());
            AuthDB.AuthDB_loggedOut.put(player.getName(), true);
            return true;
        }
        return false;
    }

    public static void Login(Player player) {
        if (!AuthDB.isAuthorized(player)) {
            long timestamp = Util.timeStamp();
            if (!AuthDB.AuthDB_AuthTime.containsKey(player.getName())) {
                AuthDB.AuthDB_AuthTime.put(player.getName(), timestamp);
            }
            AuthDB.authorizedNames.add(player.getName());
            AuthDB.AuthDB_PasswordTries.put(player.getName(), "0");
            EBean eBeanClass = EBean.checkPlayer(player, true);
            eBeanClass.setAuthorized("true");
            eBeanClass.setRegistered("true");
            if (!AuthDB.AuthDB_Authed.containsKey(Encryption.md5(player.getName()))) {
                AuthDB.AuthDB_Authed.put(Encryption.md5(player.getName()), "yes");
            }
            if (Config.session_enabled) {
                if (!AuthDB.AuthDB_Sessions.containsKey(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)))) {
                    AuthDB.AuthDB_Sessions.put(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)), timestamp);
                    Util.logging.Debug("Session started for " + player.getName());
                }
                eBeanClass.setSessiontime(timestamp);
            }
            AuthDB.database.save(eBeanClass);
            Util.craftFirePlayer.setInventoryFromStorage(player);
        }
    }

    public static boolean Link(Player player, String name) {
        if (!AuthDB.isAuthorized(player) && Config.link_enabled) {
            EBean eBeanClass = EBean.checkPlayer(player, true);
            eBeanClass.setLinkedname(name);
            AuthDB.database.save(eBeanClass);
            if (!AuthDB.AuthDB_LinkedNames.containsKey((player.getName()))) {
                AuthDB.AuthDB_LinkedNames.put(player.getName(), name);
            }
            if (AuthDB.AuthDB_LinkedNameCheck.containsKey(player.getName())) {
                AuthDB.AuthDB_LinkedNameCheck.remove(player.getName());
            }
            if (Config.link_rename) {
                Messages.sendMessage(Message.link_renamed, player, null);
                Util.craftFirePlayer.renamePlayer(player, name);
            }
            Login(player);
            return true;
        }
        return false;
    }

    public static boolean Unlink(Player player, String name) {
        if (AuthDB.isAuthorized(player) && Config.unlink_enabled) {
            if (AuthDB.AuthDB_LinkedNames.containsKey((player.getName()))) {
                AuthDB.AuthDB_LinkedNames.remove(player.getName());
            }
            if (AuthDB.AuthDB_LinkedNameCheck.containsKey(player.getName())) {
                AuthDB.AuthDB_LinkedNameCheck.remove(player.getName());
            }
            if (Config.unlink_rename) {
                Messages.sendMessage(Message.unlink_renamed, player, null);
                Util.craftFirePlayer.renamePlayer(player, player.getName());
            }
            EBean eBeanClass = EBean.checkPlayer(player, true);
            eBeanClass.setLinkedname("");
            eBeanClass.setSessiontime(0);
            eBeanClass.setRegistered("false");
            AuthDB.database.save(eBeanClass);
            Messages.sendMessage(Message.register_welcome, player, null);
            Logout(player, true);
            return true;
        }
        return false;
    }
}
