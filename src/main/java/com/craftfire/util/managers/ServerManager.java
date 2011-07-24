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
