/*
 * This file is part of AuthDB Legacy.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * AuthDB Legacy is licensed under the GNU Lesser General Public License.
 *
 * AuthDB Legacy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB Legacy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.authdb.plugins;

import org.bukkit.entity.Player;

import com.authdb.AuthDB;

import com.nijiko.permissions.PermissionHandler;

public class ZPermissions {
    public static boolean hasPlugin = false;
    public static boolean hasPermissionsBukkit = false;
    public static boolean hasbPermissions = false;
    public static PermissionHandler permissionsHandler;
    static String pluginName = AuthDB.pluginName.toLowerCase();
    static String userPermissions = pluginName + ".command.user.";
    static String adminPermissions = pluginName + ".command.admin.";
    public enum Permission {
        command_register (userPermissions+"register"),
        command_unregister (userPermissions+"unregister"),
        command_login (userPermissions+"login"),
        command_logout (userPermissions+"logout"),
        command_link (userPermissions+"link"),
        command_unlink (userPermissions+"unlink"),
        command_password (userPermissions+"password"),
        command_email (userPermissions+"email"),
        command_users (userPermissions+"users"),
        command_admin_unregister (adminPermissions+"unregister"),
        command_admin_login (adminPermissions+"login"),
        command_admin_logout (adminPermissions+"logout"),
        command_admin_password (adminPermissions+"password"),
        command_admin_reload (adminPermissions+"reload");

        private String permission;
        Permission(String permission) {
            this.permission = permission;
        }
    }

    public static boolean isAllowed(Player player, Permission permission) {
        if (hasPermissionsBukkit || hasbPermissions) {
            if (player.hasPermission(permission.permission)) {
                return true;
            } else if (player.hasPermission(pluginName + ".*")) {
                return true;
            } else if (permission.permission.startsWith(adminPermissions)) {
                if (player.hasPermission(adminPermissions + "*")) {
                    return true;
                }
            } else if (permission.permission.startsWith(userPermissions)) {
                if (player.hasPermission(userPermissions + "*")) {
                    return true;
                }
            }
        } else if (hasPlugin) {
            if (permissionsHandler.has(player, permission.permission)) {
                return true;
            } else if (permissionsHandler.has(player, pluginName + ".*")) {
                return true;
            } else if (permission.permission.startsWith(adminPermissions)) {
                if (permissionsHandler.has(player, adminPermissions + "*")) {
                    return true;
                }
            } else if (permission.permission.startsWith(userPermissions)) {
                if (permissionsHandler.has(player, userPermissions + "*")) {
                    return true;
                }
            }
        } else {
            Permission[] Permissions = Permission.values();
            for (int i=0; i<Permissions.length; i++) {
                if (Permissions[i].toString().equals(permission.toString())) {
                    if (Permissions[i].toString().startsWith(adminPermissions)) {
                        if (player.isOp()) {
                            return true;
                        }
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAllowed(Player player, String permission) {
        if (hasPermissionsBukkit || hasbPermissions) {
            if (player.hasPermission(permission)) {
                return true;
            } else if (player.hasPermission(pluginName + ".*")) {
                    return true;
            } else if (permission.startsWith(adminPermissions)) {
                if (player.hasPermission(adminPermissions + "*")) {
                    return true;
                }
            } else if (permission.startsWith(userPermissions)) {
                if (player.hasPermission(userPermissions + "*")) {
                    return true;
                }
            }
        } else if (hasPlugin) {
            if (permissionsHandler.has(player, permission)) {
                return true;
            } else if (permissionsHandler.has(player, pluginName + ".*")) {
                return true;
            } else if (permission.startsWith(adminPermissions)) {
                if (permissionsHandler.has(player, adminPermissions + "*")) {
                    return true;
                }
            } else if (permission.startsWith(userPermissions)) {
                if (permissionsHandler.has(player, userPermissions + "*")) {
                    return true;
                }
            }
        } else {
            Permission[] Permissions = Permission.values();
            for (int i=0; i<Permissions.length; i++) {
                if (Permissions[i].toString().equals(permission.toString())) {
                    if (Permissions[i].toString().startsWith(adminPermissions)) {
                        if (player.isOp()) {
                            return true;
                        }
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
