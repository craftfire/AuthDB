package com.craftfire.util.managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import com.authdb.util.Util;


public class LoggingManager {

    PluginManager PluginManager = new PluginManager();
    
    public static enum Type {
        error,debug,info,warning,servere;
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
    
    public void StackTrace(StackTraceElement[] stack, String function, int linenumber, String classname, String file) { 
       
       Warning("StackTrace Error");
       Warning("Class name: " + classname);
       Warning("File name: " + file);
       Warning("Function name: " + function);
       Warning("Error line: " + linenumber);
       if (PluginManager.config.logging) {
           DateFormat LogFormat = new SimpleDateFormat(PluginManager.config.logformat);
           Date date = new Date();
           Warning("Check log file: " + PluginManager.plugin.getDataFolder() + "\\logs\\error\\" + LogFormat.format(date) + "-error.log"); 
       }
       else { Warning("Enable logging in the config to get more information about the error."); }
       
       Error("--------------------------- STACKTRACE ERROR ---------------------------");
       Error("Class name: " + classname);
       Error("File name: " + file);
       Error("Function name: " + function);
       Error("Error line: " + linenumber);
       Error("--------------------------- STACKTRACE START ---------------------------");
       for (int i = 0; i < stack.length; i++) {
           Error(stack[i].toString());
       }
       Error("---------------------------- STACKTRACE END ----------------------------");
    }
    
    private void Error(String error) {
        ToFile(Type.error, error);
    }
    
    private void ToFile(Type type, String line) {
        if (PluginManager.config.logging) {
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
                Writer = new FileWriter(PluginManager.plugin.getDataFolder() + "/logs/" + type.toString() + "/" + LogFormat.format(date) + "-" + type.toString() + ".log",true);
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
