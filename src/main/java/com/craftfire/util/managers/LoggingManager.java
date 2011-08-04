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

import com.authdb.util.Config;
import com.authdb.util.Util;

public class LoggingManager {
    PluginManager PluginManager = new PluginManager();
    String logFolder = "plugins/AuthDB/logs/";
    public static enum Type {
        error, debug, info, warning, servere;
    }

    public void Debug(String line) {
        if (PluginManager.config.debug_enable) {
            PluginManager.plugin.log.info("[" + PluginManager.plugin.pluginName + "] " + line);
            ToFile(Type.debug, "[" + PluginManager.plugin.pluginName + "] " + line, logFolder);
        }
    }
    
    public void debug(String line, String pluginName) {
        if (PluginManager.config.debug_enable) {
            PluginManager.plugin.log.info("[" + pluginName + "] " + line);
            ToFile(Type.debug, "[" + pluginName + "] " + line, logFolder);
        }
    }
    
    public void info(String line, String pluginName) {
        PluginManager.plugin.log.info("[" + pluginName + "] " + line);
    }
    
    public void severe(String line, String pluginName) {
        PluginManager.plugin.log.severe("[" + pluginName + "] " + line);
    }
    public void warning(String line, String pluginName) {
        PluginManager.plugin.log.warning("[" + pluginName + "] " + line);
   }

    public void Info(String line) {
            PluginManager.plugin.log.info("[" + PluginManager.plugin.pluginName + "] " + line);
    }

    public void Severe(String line) {
            PluginManager.plugin.log.severe("[" + PluginManager.plugin.pluginName + "] " + line);
    }

    public void advancedWarning(String line) {
         PluginManager.plugin.log.warning("[" + PluginManager.plugin.pluginName + "]\n" +
        "|-----------------------------------------------------------------------------|\n" +
        "|--------------------------------AUTHDB WARNING-------------------------------|\n" +
        "|-----------------------------------------------------------------------------|\n" +
        "| " + line.toUpperCase() + "\n" +
        "|-----------------------------------------------------------------------------|");
    }
    
    public void plainWarning(String line) {
        PluginManager.plugin.log.warning("[" + PluginManager.plugin.pluginName + "] " + line);
   }
    
    public void advancedWarning(String line, String pluginName) {
        PluginManager.plugin.log.warning("[" + pluginName + "]\n" +
       "|-----------------------------------------------------------------------------|\n" +
       "|--------------------------------AUTHDB WARNING-------------------------------|\n" +
       "|-----------------------------------------------------------------------------|\n" +
       "| " + line.toUpperCase() + "\n" +
       "|-----------------------------------------------------------------------------|");
   }
   
   public void plainWarning(String line, String pluginName) {
       PluginManager.plugin.log.warning("[" + pluginName + "] " + line);
  }
    
    public void mySQL(String query) {
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
        Util.logging.Debug("Took " + (time / 1000) + " seconds (" + time + "ms) to " + string + ".");
    }

    private void ToFile(Type type, String line, String logFolder) {
        if (PluginManager.config.logging_enabled) {
            File data = new File(logFolder, "");
            if (!data.exists()) {
                if (data.mkdir()) {
                    Util.logging.Debug("Created missing directory: " + logFolder);
                }
            }
            data = new File(logFolder + type.toString() + "/", "");
            if (!data.exists()) {
                if (data.mkdir()) {
                    Util.logging.Debug("Created missing directory: " + logFolder + type.toString());
                }
            }
            DateFormat LogFormat = new SimpleDateFormat(PluginManager.config.logformat);
            Date date = new Date();
            data = new File(logFolder + type.toString() + "/" + LogFormat.format(date) + "-" + type.toString() + ".log");
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
                Writer = new FileWriter(logFolder + type.toString() + "/" + LogFormat.format(date) + "-" + type.toString() + ".log", true);
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
