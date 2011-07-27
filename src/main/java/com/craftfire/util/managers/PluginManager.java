/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.craftfire.util.managers;

import com.authdb.util.databases.MySQL;

public class PluginManager {
    public com.authdb.AuthDB plugin = com.authdb.AuthDB.plugin;
    public com.authdb.util.Util util;
    public com.authdb.util.Config config;
    public MySQL mySQL;
}
