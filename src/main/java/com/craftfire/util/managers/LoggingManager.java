package com.craftfire.util.managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class LoggingManager {

    PluginManager PluginManager = new PluginManager();
    
    public static enum Type {
        error,debug,info,warning,servere;
    }
    
    public void Debug(String line) { 
        if(PluginManager.Config.debug_enable) { 
            PluginManager.Plugin.log.info("["+PluginManager.Plugin.PluginName+"] "+line);
            ToFile(Type.debug, line);
        } 
    }
    
    public void Info(String line) { 
            PluginManager.Plugin.log.info("["+PluginManager.Plugin.PluginName+"] "+line);
    }
    
    public void Severe(String line) { 
            PluginManager.Plugin.log.severe("["+PluginManager.Plugin.PluginName+"] "+line);
    }
    
    public void Warning(String line) { 
        PluginManager.Plugin.log.warning("["+PluginManager.Plugin.PluginName+"] "+line);
}
    
    public void StackTrace(StackTraceElement[] stack) { 
       Error("--------------------------- STACKTRACE START ---------------------------");
       for(int i = 0; i < stack.length; i++) {
           Error(stack[i].toString());
       }
       Error("---------------------------- STACKTRACE END ----------------------------");
    }
    
    private void Error(String error) {
        PluginManager.Plugin.log.warning("["+PluginManager.Plugin.PluginName+"] "+error);
        ToFile(Type.error, error);
    }
    
    private void ToFile(Type type, String line) {
        
        File data = new File(PluginManager.Plugin.getDataFolder()+"/logs/","");
        if (!data.exists()) { data.mkdir(); }
        data = new File(PluginManager.Plugin.getDataFolder()+"/logs/"+type.toString()+"/","");
        if (!data.exists()) { data.mkdir(); }
        DateFormat LogFormat = new SimpleDateFormat(PluginManager.Config.logformat);
        Date date = new Date();
        data = new File(PluginManager.Plugin.getDataFolder()+"/logs/"+type.toString()+"/"+LogFormat.format(date)+"-"+type.toString()+".log");
        if ( !data.exists() ) {
            try {
                data.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        FileWriter Writer;
        try {
            DateFormat StringFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date TheDate = new Date();
            Writer = new FileWriter(PluginManager.Plugin.getDataFolder()+"/logs/"+type.toString()+"/"+LogFormat.format(date)+"-"+type.toString()+".log",true);
            BufferedWriter Out = new BufferedWriter(Writer);
            Out.write(StringFormat.format(TheDate)+" - "+line+"\n");
            Out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
