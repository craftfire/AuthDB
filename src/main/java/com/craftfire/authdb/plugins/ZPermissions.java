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
package com.craftfire.authdb.plugins;

import org.bukkit.entity.Player;

import com.craftfire.authdb.AuthDB;

public class ZPermissions {
    static String pluginName = AuthDB.pluginName.toLowerCase();
    static String userPermissions = pluginName + ".command.user.";
    static String adminPermissions = pluginName + ".command.admin.";
    public enum ZPermission {
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
        ZPermission(String permission) {
            this.permission = permission;
        }
    }

    public static boolean isAllowed(Player player, ZPermission permission) {
        return isAllowed(player, permission.permission);
    }

    public static boolean isAllowed(Player player, String permission) {
         return AuthDB.getPermissions().has(player, permission);
    }
}
