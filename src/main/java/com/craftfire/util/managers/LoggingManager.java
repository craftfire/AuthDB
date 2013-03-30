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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.plugin.Plugin;

import com.craftfire.authdb.util.Util;
import com.craftfire.authdb.util.databases.MySQL;

public class LoggingManager {
    PluginManager PluginManager = new PluginManager();
    String logFolder = "plugins/AuthDB/logs/";
    private String latestQuery = "";

    public static enum Type {
        error, debug, info, warning, servere;
    }

    /**
     * Prints debug messages if enabled to console and file.
     *
     * @param line is the line to be printed.
     * @deprecated This method will removed in the near future and replaced with debug(String line, String pluginName).
     */
    @Deprecated
    public void Debug(String line) {
        if (PluginManager.config.debug_enable) {
            PluginManager.plugin.log.info("[" + PluginManager.plugin.pluginName + "] " + line);
            ToFile(Type.debug, "[" + PluginManager.plugin.pluginName + "] " + line, logFolder);
        }
    }

    /**
     * Prints debug messages if enabled to console and file.
     *
     * @param line is the line to be printed.
     */
    public void debug(String line) {
        if (PluginManager.config.debug_enable) {
            PluginManager.plugin.log.info("[AuthDB] " + line);
            ToFile(Type.debug, "[AuthDB] " + line, logFolder);
        }
    }

    /**
     * Print to console with info level.
     *
     * @param line is the line to be printed.
     */
    public void info(String line) {
        PluginManager.plugin.log.info("[AuthDB] " + line);
    }

    /**
     * Print to console with severe level.
     *
     * @param line is the line to be printed.
     */
    public void severe(String line) {
        PluginManager.plugin.log.severe("[AuthDB] " + line);
    }

    /**
     * Print to console with warning level.
     *
     * @param line is the line to be printed.
     */
    public void warning(String line) {
        PluginManager.plugin.log.warning("[AuthDB] " + line);
    }

    /**
     * Print to console with info level.
     *
     * @param line is the line to be printed.
     * @deprecated This method will removed in the near future and replaced with info(String line, String pluginName).
     */
    @Deprecated
    public void Info(String line) {
            PluginManager.plugin.log.info("[" + PluginManager.plugin.pluginName + "] " + line);
    }

    /**
     * Print to console with severe level.
     *
     * @param line is the line to be printed.
     * @deprecated This method will removed in the near future and replaced with severe(String line, String pluginName).
     */
    @Deprecated
    public void Severe(String line) {
            PluginManager.plugin.log.severe("[" + PluginManager.plugin.pluginName + "] " + line);
    }

    /**
     * Prints out a nice advanced warning into the console.
     *
     * @param line is the line to be printed.
     * @deprecated This method will removed in the near future and replaced with advancedWarning(String line, String pluginName).
     */
    @Deprecated
    public void advancedWarning(String line) {
        PluginManager.plugin.log.warning("[" + PluginManager.plugin.pluginName + "]" + System.getProperty("line.separator")
        + "|-----------------------------------------------------------------------------|" + System.getProperty("line.separator")
        + "|--------------------------------AUTHDB WARNING-------------------------------|" + System.getProperty("line.separator")
        + "|-----------------------------------------------------------------------------|" + System.getProperty("line.separator")
        + "| " + line.toUpperCase() + System.getProperty("line.separator")
        + "|-----------------------------------------------------------------------------|");
    }

    /**
     * Prints out a plain warning into the console.
     *
     * @param line is the line to be printed.
     * @deprecated This method will removed in the near future and replaced with plainWarning(String line, String pluginName).
     */
    @Deprecated
    public void plainWarning(String line) {
        PluginManager.plugin.log.warning("[" + PluginManager.plugin.pluginName + "] " + line);
    }

    /**
     * Prints out a nice advanced warning into the console.
     *
     * @param line is the line to be printed.
     * @param pluginName is the prefix of the messages, for example [pluginName] line.
     */
    public void advancedWarning(String line, String pluginName) {
        PluginManager.plugin.log.warning("[" + pluginName + "]" + System.getProperty("line.separator")
        + "|-----------------------------------------------------------------------------|" + System.getProperty("line.separator")
        + "|--------------------------------AUTHDB WARNING-------------------------------|" + System.getProperty("line.separator")
        + "|-----------------------------------------------------------------------------|" + System.getProperty("line.separator")
        + "| " + line.toUpperCase() + System.getProperty("line.separator")
        + "|-----------------------------------------------------------------------------|");
    }

    /**
     * Prints out a plain warning into the console.
     *
     * @param line is the line to be printed.
     * @param pluginName is the prefix of the messages, for example [pluginName] line.
     */
    public void plainWarning(String line, String pluginName) {
        PluginManager.plugin.log.warning("[" + pluginName + "] " + line);
    }

    /**
     * Send the MySQL query to debug.
     *
     * @param query is the query to be printed.
     */
    public void mySQL(String query) {
        latestQuery = query;
        Debug("Executing MySQL query: " + query);
    }

    public void StackTrace(StackTraceElement[] stack, String function, int linenumber, String classname, String file) {
        advancedWarning("StackTrace Error");
        plainWarning("Class name: " + classname);
        plainWarning("File name: " + file);
        plainWarning("Function name: " + function);
        plainWarning("Error line: " + linenumber);
        if (PluginManager.config.logging_enabled) {
            DateFormat LogFormat = new SimpleDateFormat(PluginManager.config.logformat);
            Date date = new Date();
            plainWarning("Check log file: " + PluginManager.plugin.getDataFolder() + "\\logs\\error\\" + LogFormat.format(date) + "-error.log");
        } else {
            plainWarning("Enable logging in the config to get more information about the error.");
        }

        logError("--------------------------- STACKTRACE ERROR ---------------------------");
        logError("Class name: " + classname);
        logError("File name: " + file);
        logError("Function name: " + function);
        logError("Error line: " + linenumber);
        logError("AuthDB version: " + PluginManager.plugin.pluginVersion);
        logError("Keep alive: " + PluginManager.config.database_keepalive);
        logError("MySQL connection: " + PluginManager.mySQL.isConnected());
        logError("Latest query: " + latestQuery);
        if (PluginManager.config.custom_enabled) {
            logError("Script: Custom");
            logError("Custom table: " + PluginManager.config.custom_table);
            if (PluginManager.config.custom_emailrequired) {
                logError("Custom emailfield: " + PluginManager.config.custom_emailfield);
            }
            logError("Custom passfield: " + PluginManager.config.custom_passfield);
            logError("Custom userfield: " + PluginManager.config.custom_userfield);
            logError("Custom encryption: " + PluginManager.config.custom_encryption);
            logError("Custom table schema:");
            Statement st;
            try {
                st = MySQL.mysql.createStatement();
                String sql = "SELECT * FROM " + PluginManager.config.custom_table;
                mySQL(sql);
                ResultSet rs = st.executeQuery(sql);
                ResultSetMetaData metaData = rs.getMetaData();
                int rowCount = metaData.getColumnCount();
                logError("Table Name : " + metaData.getTableName(2));
                logError("Column\tType(size)");
                for (int i = 0; i < rowCount; i++) {
                    logError(metaData.getColumnName(i + 1) + "\t" + metaData.getColumnTypeName(i + 1) + "(" + metaData.getColumnDisplaySize(i + 1) + ")");
                }
            } catch (SQLException e) {
                logError("Failed while getting MySQL table schema.");
            }
        } else {
            logError("Script: " + PluginManager.config.script_name);
            logError("Script version: " + PluginManager.config.script_version);
            logError("Table prefix: " + PluginManager.config.script_tableprefix);
        }
        Plugin[] plugins = PluginManager.plugin.getServer().getPluginManager().getPlugins();
        int counter = 0;
        StringBuffer pluginsList = new StringBuffer();
        while (plugins.length > counter) {
            pluginsList.append(plugins[counter].getDescription().getName() + " " + plugins[counter].getDescription().getVersion() + ", ");
            counter++;
        }
        logError("Plugins: " + pluginsList.toString());
        logError("--------------------------- STACKTRACE START ---------------------------");
        for (int i = 0; i < stack.length; i++) {
            logError(stack[i].toString());
        }
        logError("---------------------------- STACKTRACE END ----------------------------");
    }

    public void stackTrace(String pluginName, StackTraceElement[] stack, String function, int linenumber, String classname, String file) {
        advancedWarning("StackTrace Error", pluginName);
        plainWarning("Class name: " + classname, pluginName);
        plainWarning("File name: " + file, pluginName);
        plainWarning("Function name: " + function, pluginName);
        plainWarning("Error line: " + linenumber, pluginName);
        if (PluginManager.config.logging_enabled) {
            DateFormat LogFormat = new SimpleDateFormat(PluginManager.config.logformat);
            Date date = new Date();
            plainWarning("Check log file: " + PluginManager.plugin.getDataFolder() + "\\logs\\error\\" + LogFormat.format(date) + "-error.log", pluginName);
        } else {
            plainWarning("Enable logging in the config to get more information about the error.", pluginName);
        }

        logError("--------------------------- STACKTRACE ERROR ---------------------------", pluginName);
        logError("Class name: " + classname, pluginName);
        logError("File name: " + file, pluginName);
        logError("Function name: " + function, pluginName);
        logError("Error line: " + linenumber, pluginName);
        logError("--------------------------- STACKTRACE START ---------------------------", pluginName);
        for (int i = 0; i < stack.length; i++) {
            logError(stack[i].toString());
        }
        logError("---------------------------- STACKTRACE END ----------------------------", pluginName);
    }

    public void error(String error) {
        plainWarning(error);
        logError(error);
    }

    public void error(String error, String pluginName) {
        plainWarning(error, pluginName);
        logError(error, pluginName);
    }

    public void logError(String error) {
        ToFile(Type.error, error, logFolder);
    }

    public void logError(String error, String pluginName) {
        ToFile(Type.error, "[" + pluginName + "] " + error, logFolder);
    }

    public void timeUsage(long time, String string) {
        Util.logging.debug("Took " + (time / 1000) + " seconds (" + time + "ms) to " + string + ".");
    }

    private void ToFile(Type type, String line, String logFolder) {
        if (PluginManager.config.logging_enabled) {
            File data = new File(logFolder, "");
            if (!data.exists()) {
                if (data.mkdir()) {
                    Util.logging.debug("Created missing directory: " + logFolder);
                }
            }
            data = new File(logFolder + type.toString() + "/", "");
            if (!data.exists()) {
                if (data.mkdir()) {
                    Util.logging.debug("Created missing directory: " + logFolder + type.toString());
                }
            }
            DateFormat LogFormat = new SimpleDateFormat(PluginManager.config.logformat);
            Date date = new Date();
            data = new File(logFolder + type.toString() + "/" + LogFormat.format(date) + "-" + type.toString() + ".log");
            if (!data.exists()) {
                try {
                    data.createNewFile();
                } catch (IOException e) {
                    // TODO: Auto-generated catch block
                    Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                }
            }
            FileWriter Writer;
            try {
                DateFormat StringFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date TheDate = new Date();
                Writer = new FileWriter(logFolder + type.toString() + "/" + LogFormat.format(date) + "-" + type.toString() + ".log", true);
                BufferedWriter Out = new BufferedWriter(Writer);
                Out.write(StringFormat.format(TheDate) + " - " + line + System.getProperty("line.separator"));
                Out.close();
            } catch (IOException e) {
                // TODO: Auto-generated catch block
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
        }
    }
}
