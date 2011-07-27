package com.craftfire.util.managers;

public class DatabaseManager {
    PluginManager PluginManager = new PluginManager();
    
    public String getDatabaseType() {
        String type = PluginManager.config.database_type.toLowerCase();
        if (type.equalsIgnoreCase("mysql")) {
            return "mysql";
        } else if (type.equalsIgnoreCase("ebean")) {
            return "ebean";
        }
        return "mysql";
    }
    
    public void connect() {
        if (getDatabaseType().equalsIgnoreCase("mysql")) {
            PluginManager.mySQL.connect();
        }
    }
    
    public void close() {
        if (getDatabaseType().equalsIgnoreCase("mysql")) {
            PluginManager.mySQL.close();
        }
    }
}
