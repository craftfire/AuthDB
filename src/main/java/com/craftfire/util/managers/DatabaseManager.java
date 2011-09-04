/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.craftfire.util.managers;

public class DatabaseManager {
    PluginManager PluginManager = new PluginManager();
    LoggingManager loggingManager = new LoggingManager();

    public String getDatabaseType() {
        String type = PluginManager.config.database_type.toLowerCase();
        loggingManager.Debug("Database type in config: " + type);
        if (type.equalsIgnoreCase("mysql")) {
            loggingManager.Debug("Database type set to MySQL");
            return "mysql";
        } else if (type.equalsIgnoreCase("ebean")) {
            loggingManager.Debug("Database type set to eBean");
            return "ebean";
        }
        loggingManager.Debug("Database type set to MySQL");
        return "mysql";
    }

    public void connect() {
        if (getDatabaseType().equalsIgnoreCase("mysql")) {
            loggingManager.Debug("Connecting to MySQL....");
            PluginManager.mySQL.connect();
        } else {
            loggingManager.Debug("Could not find any database type to connect to! (mysql etc)....");
        }
    }

    public void close() {
        if (getDatabaseType().equalsIgnoreCase("mysql")) {
            loggingManager.Debug("Closing MySQL connection....");
            PluginManager.mySQL.close();
        } else {
            loggingManager.Debug("Could not find any database type to close! (mysql etc)....");
        }
    }
    
}
