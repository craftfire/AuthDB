/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.authdb.AuthDB;
import com.authdb.util.Util;

import com.nijiko.permissions.PermissionHandler;

public class zPermissions 
{
    public static boolean HasPlugin = false;
    public static PermissionHandler permissionsHandler;

    public enum Permission
    {
        command_logoff ("command.logoff"),
        command_reload ("command.reload"),
        command_users ("command.users");

        private String permission;
        Permission(String permission) { this.permission = permission; }
    }

    public static boolean IsAllowed(Player player, Permission permission)
    {
      if(HasPlugin)
      {
          Util.Debug("HAS PLUGIN");
          if(permissionsHandler.has(player, AuthDB.AuthDBPlugin.name.name().toLowerCase() + "." + permission.name())) return true;
          else if (player.isOp()) return true;
      }
      else if (player.isOp()) return true;
      return false;
    }


}
