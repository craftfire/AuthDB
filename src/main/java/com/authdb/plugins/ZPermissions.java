/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

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
