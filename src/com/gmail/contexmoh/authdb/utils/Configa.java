package com.gmail.contexmoh.authdb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;


public class Configa
{
  private final static Yaml yaml = new Yaml(new SafeConstructor());
  static Map data;

  public static boolean loadConfig(String Config)
  {
    File file = new File(Config+".yml");
    FileInputStream rx = null;
    try {
      rx = new FileInputStream(file);
    }
    catch (FileNotFoundException e) {
    	Utils.Log("severe",""+e);
      return false;
    }
    data = ((Map)yaml.load(new UnicodeReader(rx)));
    try {
      rx.close();
    } catch (IOException e) {
      Utils.Log("severe",""+e);
      return false;
    }
    return true;
  }

  public boolean writeConfig(String Config) 
  {
    File file = new File(Config+".yml");
    FileWriter tx = null;
    try {
      tx = new FileWriter(file, false);
    } catch (IOException e) {
      Utils.Log("severe",""+e);
      return false;
    }
    try {
      tx.write(this.yaml.dump(data));
      tx.flush();
    } catch (IOException e) {
      Utils.Log("severe",""+e);
      return false;
    } finally {
      try {
        tx.close();
      } catch (IOException e) {
        Utils.Log("severe",""+e);
        return false;
      }
    }
    return true;
  }

  public static boolean AddConfigValue(String Config,String ConfigNode, String ConfigData) 
  {
	if(!CheckConfigValue(Config,ConfigNode)) return false;
	data.put(ConfigNode, ConfigData);
	return true;
  }

  public static boolean CheckConfigValue(String Config,String ConfigNode)
  {
    if (!loadConfig(Config)) return false;
    Map ConfigNodes = (Map)data.get(ConfigNode);
    if (ConfigNodes == null) return false;
    return true;
  }
  public static boolean DeleteConfigValue(String Config,String ConfigNode)
  {
    if (!loadConfig(Config)) return false;
    Map ConfigNodes = (Map)data.get(ConfigNode);
    if (ConfigNodes == null) return false;
    ConfigNodes.remove(ConfigNode);
    data.put(ConfigNode, ConfigNodes); 
    return true;
  }
  public static boolean RenameConfigValue(String Config,String FromConfigNode,String ToConfigNode, String ConfigData)
  {
    if (!loadConfig(Config)) return false;
    Map ConfigNodes = (Map)data.get(FromConfigNode);
    if (ConfigNodes == null) return false;
    ConfigNodes.remove(FromConfigNode);
    data.put(ToConfigNode, ConfigData);
    return true;
  }
}