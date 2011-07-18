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

public class zPermissions 
{
    public static boolean HasPlugin = false;
    public static boolean HasPermissionsBukkit = false;
    public static PermissionHandler permissionsHandler;

    public enum Permission {
        command_login ("command.login"),
        command_logout ("command.logout"),
        command_users ("command.users"),
        command_register ("command.register"),
        command_unregister ("command.unregister"),
        command_password ("command.password"),
        command_email ("command.email"),
        command_link ("command.link"),
        command_unlink ("command.unlink"),
        command_admin_logout ("command.admin.logout"),
        command_admin_login ("command.admin.login"),
        command_admin_unregister ("command.admin.unregister"),
        command_admin_password ("command.admin.password"),
        command_admin_reload ("command.admin.reload"),;

        private String permission;
        Permission(String permission) { this.permission = permission; }
    }

    public static boolean IsAllowed(Player player, Permission permission) {
      if(HasPermissionsBukkit) {
            if(player.hasPermission(AuthDB.PluginName.toLowerCase() + "." + permission.permission)) {
                return true;
            }
      }
      else if(HasPlugin) {
          if(permissionsHandler.has(player, AuthDB.PluginName.toLowerCase() + "." + permission.permission)) {
              return true;
          }
      }
      else {
          Permission[] Permissions = Permission.values();
          for(int i=0; i<Permissions.length; i++) {
              if(Permissions[i].toString().equals(permission.toString())) {
                  if(Permissions[i].toString().startsWith(AuthDB.PluginName.toLowerCase()+"."+"admin.")) {
                      if(player.isOp()) {
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
