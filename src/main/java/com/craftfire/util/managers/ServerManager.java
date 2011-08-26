/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.craftfire.util.managers;

import java.util.Random;

public class ServerManager {
    PluginManager pluginManager = new PluginManager();
    public String getServerId() {
        return Integer.toHexString(pluginManager.plugin.getServer().getServerName().hashCode());
    }

    //private String sessionId = Long.toHexString(new Random().nextLong());

    public String getSessionId() {
        return Long.toHexString(new Random().nextLong());
    }
}
