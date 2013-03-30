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
            loggingManager.Debug("Could not find any database type to connect to! (mysql etc)...");
        }
    }

    public void close() {
        if (getDatabaseType().equalsIgnoreCase("mysql")) {
            loggingManager.Debug("Closing MySQL connection....");
            PluginManager.mySQL.close();
        } else {
            loggingManager.Debug("Could not find any database type to close! (mysql etc)...");
        }
    }
}
