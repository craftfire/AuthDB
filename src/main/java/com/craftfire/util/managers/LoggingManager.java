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
        if (PluginManager.Config.debug_enable) { 
            PluginManager.Plugin.log.info("[" + PluginManager.Plugin.PluginName + "] " + line);
            ToFile(Type.debug, line);
        } 
    }
    
    public void Info(String line) { 
            PluginManager.Plugin.log.info("[" + PluginManager.Plugin.PluginName + "] " + line);
    }
    
    public void Severe(String line) { 
            PluginManager.Plugin.log.severe("[" + PluginManager.Plugin.PluginName + "] " + line);
    }
    
    public void Warning(String line) { 
        PluginManager.Plugin.log.warning("[" + PluginManager.Plugin.PluginName + "] " + line);
    }
    
    public void StackTrace(StackTraceElement[] stack, String function, int linenumber, String classname, String file) { 
       
       Warning("StackTrace Error");
       Warning("Class name: " + classname);
       Warning("File name: " + file);
       Warning("Function name: " + function);
       Warning("Error line: " + linenumber);
       if (PluginManager.Config.logging) {
           DateFormat LogFormat = new SimpleDateFormat(PluginManager.Config.logformat);
           Date date = new Date();
           Warning("Check log file: " + PluginManager.Plugin.getDataFolder() + "\\logs\\error\\" + LogFormat.format(date) + "-error.log"); 
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
        if (PluginManager.Config.logging) {
            File data = new File(PluginManager.Plugin.getDataFolder() + "/logs/", "");
            if (!data.exists()) { data.mkdir(); }
            data = new File(PluginManager.Plugin.getDataFolder() + "/logs/" + type.toString() + "/", "");
            if (!data.exists()) { data.mkdir(); }
            DateFormat LogFormat = new SimpleDateFormat(PluginManager.Config.logformat);
            Date date = new Date();
            data = new File(PluginManager.Plugin.getDataFolder() + "/logs/" + type.toString() + "/" + LogFormat.format(date) + "-" + type.toString() + ".log");
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
                Writer = new FileWriter(PluginManager.Plugin.getDataFolder() + "/logs/" + type.toString() + "/" + LogFormat.format(date) + "-" + type.toString() + ".log",true);
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
