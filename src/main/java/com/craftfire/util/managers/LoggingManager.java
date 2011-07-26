/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.craftfire.util.managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.authdb.util.Util;

public class LoggingManager {
    PluginManager PluginManager = new PluginManager();

    public static enum Type {
        error, debug, info, warning, servere;
    }

    public void Debug(String line) {
        if (PluginManager.config.debug_enable) {
            PluginManager.plugin.log.info("[" + PluginManager.plugin.pluginName + "] " + line);
            ToFile(Type.debug, line);
        }
    }

    public void Info(String line) {
            PluginManager.plugin.log.info("[" + PluginManager.plugin.pluginName + "] " + line);
    }

    public void Severe(String line) {
            PluginManager.plugin.log.severe("[" + PluginManager.plugin.pluginName + "] " + line);
    }

    public void Warning(String line) {
        PluginManager.plugin.log.warning("[" + PluginManager.plugin.pluginName + "] " + line);
    }
    
    public void mySQL(String query) {
        Debug("Executing MySQL query: " + query);
    }

    public void StackTrace(StackTraceElement[] stack, String function, int linenumber, String classname, String file) {
        Warning("StackTrace Error");
        Warning("Class name: " + classname);
        Warning("File name: " + file);
        Warning("Function name: " + function);
        Warning("Error line: " + linenumber);
        if (PluginManager.config.logging_enabled) {
            DateFormat LogFormat = new SimpleDateFormat(PluginManager.config.logformat);
            Date date = new Date();
            Warning("Check log file: " + PluginManager.plugin.getDataFolder() + "\\logs\\error\\" + LogFormat.format(date) + "-error.log");
        } else {
            Warning("Enable logging in the config to get more information about the error.");
        }

        logError("--------------------------- STACKTRACE ERROR ---------------------------");
        logError("Class name: " + classname);
        logError("File name: " + file);
        logError("Function name: " + function);
        logError("Error line: " + linenumber);
        logError("--------------------------- STACKTRACE START ---------------------------");
        for (int i = 0; i < stack.length; i++) {
        	logError(stack[i].toString());
        }
        logError("---------------------------- STACKTRACE END ----------------------------");
    }
    
    public void error(String error) {
    	Warning(error);
    	logError(error);
    }

    public void logError(String error) {
        ToFile(Type.error, error);
    }
    
    public void timeUsage(long time, String string) {
        Util.logging.Debug("Took " + (time / 1000) + " seconds (" + time + "ms) to " + string + ".");
    }

    private void ToFile(Type type, String line) {
        if (PluginManager.config.logging_enabled) {
            File data = new File(PluginManager.plugin.getDataFolder() + "/logs/", "");
            if (!data.exists()) {
                if (data.mkdir()) {
                    Util.logging.Debug("Created missing directory: " + PluginManager.plugin.getDataFolder() + "/logs/");
                }
            }
            data = new File(PluginManager.plugin.getDataFolder() + "/logs/" + type.toString() + "/", "");
            if (!data.exists()) {
                if (data.mkdir()) {
                    Util.logging.Debug("Created missing directory: " + PluginManager.plugin.getDataFolder() + "/logs/" + type.toString());
                }
            }
            DateFormat LogFormat = new SimpleDateFormat(PluginManager.config.logformat);
            Date date = new Date();
            data = new File(PluginManager.plugin.getDataFolder() + "/logs/" + type.toString() + "/" + LogFormat.format(date) + "-" + type.toString() + ".log");
            if (!data.exists()) {
                try {
                    data.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                }
            }
            FileWriter Writer;
            try {
                DateFormat StringFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date TheDate = new Date();
                Writer = new FileWriter(PluginManager.plugin.getDataFolder() + "/logs/" + type.toString() + "/" + LogFormat.format(date) + "-" + type.toString() + ".log", true);
                BufferedWriter Out = new BufferedWriter(Writer);
                Out.write(StringFormat.format(TheDate) + " - " + line + "\n");
                Out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            }
        }
    }
}
