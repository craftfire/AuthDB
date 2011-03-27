/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

**/
package com.authdb;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.authdb.listeners.AuthDBBlockListener;
import com.authdb.listeners.AuthDBEntityListener;
import com.authdb.listeners.AuthDBPlayerListener;
import com.authdb.plugins.zCraftIRC;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;
import com.ensifera.animosity.craftirc.CraftIRC;


public class AuthDB extends JavaPlugin {
	//
    public static org.bukkit.Server Server;
	PluginDescriptionFile pluginFile = getDescription();
	public static String pluginname = "AuthDB";
	public static String pluginversion = "2.1.0";
    public static CraftIRC craftircHandle;
	//
	private final AuthDBPlayerListener playerListener = new AuthDBPlayerListener(this);
	private final AuthDBBlockListener blockListener = new AuthDBBlockListener(this);
	private final AuthDBEntityListener entityListener = new AuthDBEntityListener(this);
	private static List<Integer> authorizedIds = new ArrayList();
	public static HashMap<String, String> db = new HashMap();
	public static HashMap<String, String> db2 = new HashMap();
	public static String idleFileName = "idle.db";
	public static Logger log = Logger.getLogger("Minecraft");
	public HashMap<String, ItemStack[]> inventories = new HashMap();

	public void onDisable() 
	{
		Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
	    if ((checkCraftIRC != null) && (checkCraftIRC.isEnabled()) && (Config.CraftIRC_enabled == true))
	    	zCraftIRC.SendMessage("disconnect",null);
		disableInventory();
		authorizedIds.clear();
		db.clear();
		db2.clear();
		MySQL.close();
	 }


	public void onEnable() 
	{
		Server = getServer();
		Plugin[] plugins = Server.getPluginManager().getPlugins();
		//Util.Debug(System.getProperty("java.version"));
		/*Util.Debug(System.getProperty("java.io.tmpdir"));
		Util.Debug(System.getProperty("java.library.path"));
		Util.Debug(System.getProperty("java.class.path"));
		Util.Debug(System.getProperty("user.home"));
		Util.Debug(System.getProperty("user.dir"));
		Util.Debug(System.getProperty("user.name"));
		Util.ErrorFile("HELLO"); */
		int counter = 0;
		String Plugins = "";
		while(plugins.length > counter)
		{
			Plugins += plugins[counter].getDescription().getName()+"&_&"+plugins[counter].getDescription().getVersion();
			if(plugins.length != (counter + 1))
				Plugins += "*_*";
			counter++;
		}
		
		Config TheMessages = new Config("messages","plugins/"+pluginname+"/", "messages.yml");
		if (null == TheMessages.GetConfigString("messages", "")) 
		{
		    Util.Log("info", "messages.yml could not be found in plugins/AuthDB/ -- disabling!");
		    getServer().getPluginManager().disablePlugin(((Plugin) (this)));
		    return;
		}
		Config TheConfig = new Config("config","plugins/"+pluginname+"/", "config.yml");
		if (null == getConfiguration().getKeys("Core")) 
		{
		    Util.Log("info", "config.yml could not be found in plugins/AuthDB/ -- disabling!");
		    getServer().getPluginManager().disablePlugin(((Plugin) (this)));
		    return;
		}

	      final Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
	      //Util.Debug(""+checkCraftIRC.isEnabled());
	      if ((checkCraftIRC != null) && (Config.CraftIRC_enabled == true)) {
	    	  craftircHandle = ((CraftIRC)checkCraftIRC);
	    	  Util.Log("info","Found supported plugin: " + checkCraftIRC.getDescription().getName());
	    	  this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    		@Override
		    		public void run() { if(checkCraftIRC.isEnabled()) { zCraftIRC.SendMessage("connect", null); } } }, 100);
	    	  
	      }
		String thescript = "",theversion = "";
		if(Config.custom_enabled) { thescript = "custom"; }
		else 
		{ 
			thescript = Config.script_name; 
			theversion = Config.script_version;
		}
		String online = ""+getServer().getOnlinePlayers().length;
		String max = ""+getServer().getMaxPlayers();
		if(Config.usagestats_enabled)
		{
			try { Util.PostInfo(getServer().getName(),getServer().getVersion(),pluginversion,System.getProperty("os.name"),System.getProperty("os.version"),System.getProperty("os.arch"),System.getProperty("java.version"),thescript,theversion,Plugins,online,max,Server.getPort()); } 
			catch (IOException e1) { if(Config.debug_enable) Util.Debug("Could not send data to main server."); }
		}
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);
	    pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.playerListener, Event.Priority.Normal, this);
	    pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_INTERACT, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, this.entityListener, Event.Priority.Normal, this);
	    pm.registerEvent(Event.Type.ENTITY_TARGET, this.entityListener, Event.Priority.Normal, this);
		
		MySQL.connect();
		try {
			Util.CheckScript("numusers",Config.script_name,null,null,null,null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Util.Log("info", pluginname + " plugin " + pluginversion + " is enabled");
		if(Config.debug_enable) Util.Log("info", "Debug is ENABLED, get ready for some heavy spam");
		if(Config.custom_enabled) if(Config.custom_encryption == null) Util.Log("info", "**WARNING** SERVER IS RUNNING WITH NO ENCRYPTION: PASSWORDS ARE STORED IN PLAINTEXT");
		Util.Log("info", pluginname + " is developed by Contex <contex@craftfire.com> and Wulfspider <wulfspider@craftfire.com>");
	}


    public static boolean isAuthorized(int id) { return authorizedIds.contains(Integer.valueOf(id)); }
    public void unauthorize(int id) { AuthDB.authorizedIds.remove(Integer.valueOf(id)); } 
    public void authorize(int id) { AuthDB.authorizedIds.add(Integer.valueOf(id)); }
	public boolean checkPassword(String player, String password) 
	 {
		try 
		{
				MySQL.connect();
			if(Util.CheckScript("checkpassword",Config.script_name, player.toLowerCase(), password,null,null)) return true;
			MySQL.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			Stop("ERRORS in checking password. Plugin will NOT work. Disabling it.");
		}
		return false;
	}
	
	public boolean isWithinRange(int number, int around, int range){
	    int difference = Math.abs(around - number);
	    return difference <= range;
	}
	
	
	void Stop(String error)
	{
		Util.Log("warning",error);
		getServer().getPluginManager().disablePlugin(((org.bukkit.plugin.Plugin) (this)));
	}
	
	public void register(String player, String password, String ipAddress) throws IOException, SQLException { register(player, password, "", ipAddress); }

	public void register(String player, String password, String email, String ipAddress) throws IOException, SQLException 
	{
		MySQL.connect();
		Util.CheckScript("adduser",Config.script_name,player, password, email, ipAddress);
		MySQL.close();
	}

	public boolean isRegistered(String player) {
		try {
			
			if(Config.debug_enable) 
				Util.Debug("Running function: isRegistered(String player)");
			boolean dupe = false;
			MySQL.connect();
			Config.HasForumBoard = false;
			if(Util.CheckScript("checkuser",Config.script_name, player.toLowerCase(), null, null, null))
			{
				dupe = true;
			}
			if(!Config.HasForumBoard) 
				Stop("Can't find a forum board, stopping the plugin.");
			MySQL.close();
			return dupe;
		} catch (SQLException e) 
		{
			e.printStackTrace();
			Stop("ERRORS in checking user. Plugin will NOT work. Disabling it.");
		}
		return false;
	}


	public boolean checkEmail(String email)
	{
	      Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	      Matcher m = p.matcher(email);
	      boolean Matches = m.matches();
	      if (Matches)
	        return true;
	      else
	        return false;
	}
	
	public void storeInventory(String player, ItemStack[] inventory) throws IOException {
		File inv = new File(getDataFolder(), player + "_inv");
		if (inv.exists()) return;
		inv.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(inv));
		for (short i = 0; i < inventory.length; i = (short)(i + 1)) 
		{
			bw.write(inventory[i].getTypeId() + ":" + inventory[i].getAmount() + ":" + (inventory[i].getData() == null ? "" : Byte.valueOf(inventory[i].getData().getData())) + ":" + inventory[i].getDurability());
			bw.newLine();
		}
		bw.close();
		this.inventories.put(player.toLowerCase(), inventory);
	}
	
	public void disableInventory()
	{
		Set pl = inventories.keySet();
		Iterator i = pl.iterator();
		   while (i.hasNext())
		   {
				String player = (String)i.next();
				Player pla = getServer().getPlayer(player);
				if (pl != null)
				pla.getInventory().setContents((ItemStack[])this.inventories.get(player));
		   }
		inventories.clear();
	}
	
	public String IdleGetTaskID(Player player)
	{
		return (String)db.get(player.getName().toLowerCase());
	} 
	
	public String GetSessionTime(Player player)
	{
		return this.db.get(player.getName());
	} 
	
	  public void updateDb() throws IOException {
		    BufferedWriter bw = new BufferedWriter(new FileWriter(new File(getDataFolder(), idleFileName)));
		    Set keys = this.db.keySet();
		    Iterator i = keys.iterator();
		    while (i.hasNext()) {
		      String key = (String)i.next();
		      bw.append(key + ":" + (String)this.db.get(key));
		      bw.newLine();
		    }
		    bw.close();
		  }

	
	public boolean IdleTask(String type,Player player, String TaskID) throws IOException 
	{
		if(type.equals("add"))
		{
			this.db.put(player.getName().toLowerCase(), TaskID);
			return true;
		}
		
		else if(type.equals("remove"))
		{
			this.db.remove(player.getName());
			return true;
		}
		else if(type.equals("check"))
		{
			if(this.db.containsKey(player.getName().toLowerCase())) { return true; }
		}
		else if(type.equals("add2"))
		{
			this.db2.put(player.getName(), TaskID);
			return true;
		}
		
		else if(type.equals("remove2"))
		{
			this.db2.remove(player.getName());
			return true;
		}
		else if(type.equals("check2"))
		{
			if(this.db2.containsKey(Encryption.md5(player.getName()+Util.GetIP(player)))) { return true; }
		}
		return false;
	}
	
	public ItemStack[] getInventory(String player) 
	{
		File f = new File(getDataFolder(), player + "_inv");
		if (inventories.containsKey(player.toLowerCase())) 
		{
			if ((f.exists()) && (!f.delete()))
				Util.Log("warning", "Unable to delete user inventory file: " + player + "_inv");
			return (ItemStack[])inventories.remove(player.toLowerCase());
		}
		if (f.exists()) 
		{
			ItemStack[] inv = new ItemStack[36];
			try 
			{
			Scanner s = new Scanner(f);
			short i = 0;
			while (s.hasNextLine()) 
			{
				String line = s.nextLine();
				String[] split = line.split(":");
				if (split.length == 4) 
				{
					inv[i] = 
					new ItemStack(Integer.valueOf(split[0]).intValue(), Integer.valueOf(split[1]).intValue(), 
					Short.valueOf(split[3]).shortValue(), split[2].length() == 0 ? null : Byte.valueOf(split[2]));
					i = (short)(i + 1);
				}
			}
			s.close();
			if (!f.delete())
			Util.Log("warning", "Unable to delete user inventory file: " + player + "_inv");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				Stop("ERRORS in Inventory file read error. Plugin will NOT work. Disabling it.");
			}
			return inv;
		}
		return null;
	}
	
	/*  public void deleteInventory(String player) {
		    File f = new File(getDataFolder(),player + "_inv");
		    if (f.exists())
		      f.delete();
		  }
		  */
}
