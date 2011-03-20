/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
**/
package com.authdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;
import com.ensifera.animosity.craftirc.CraftIRC;


public class AuthDB extends JavaPlugin {
	//
	PluginDescriptionFile pluginFile = getDescription();
	public static String pluginname = "AuthDB";
	public static String pluginversion = "2.0.0";
	public static CraftIRC craftircHandle = null;
	//
	private final AuthDBPlayerListener playerListener = new AuthDBPlayerListener(this);
	private final AuthDBBlockListener blockListener = new AuthDBBlockListener(this);
	private final AuthDBEntityListener entityListener = new AuthDBEntityListener(this);
	private static List<Integer> authorizedIds = new ArrayList();
	public static HashMap<String, String> db = new HashMap();
	public static String idleFileName = "idle.db";
	public static Logger log = Logger.getLogger("Minecraft");
	public HashMap<String, ItemStack[]> inventories = new HashMap();

	public void onDisable() 
	{
		zCraftIRC.SendMessage("disconnect",null);
		disableInventory();
		authorizedIds.clear();
		db.clear();
		MySQL.close();
	 }


	public void onEnable() 
	{
		if(Config.usagestats_enabled)
		{
			try { Util.PostInfo(getServer().getName(),getServer().getVersion(),pluginversion); } 
			catch (IOException e1) { if(Config.debug_enable) Util.Debug("Could not send data to main server."); }
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
		Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
		if (checkCraftIRC != null && Config.CraftIRC_enabled == true) {
		    try {
		        	Util.Log("info", "CraftIRC Support Enabled"); 
		        	craftircHandle = (CraftIRC) checkCraftIRC;
		        	zCraftIRC.SendMessage("connect",null);
		        } 
		    catch (ClassCastException ex) {
		    	ex.printStackTrace();
		    	Stop("Error in looking for CraftIRC");
		    }
		}
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM, this.playerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Lowest, this);
	    pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.playerListener, Event.Priority.Lowest, this);
	    pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.playerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, this.blockListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, this.blockListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.BLOCK_INTERACT, this.blockListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, this.entityListener, Event.Priority.Lowest, this);
		
		try { MySQL.connect(); } 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			Stop("ERRORS in the ClassNotFoundException. Plugin will NOT work. Disabling it.");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			Stop("ERRORS in the SQLException. Plugin will NOT work. Disabling it.");
		}
		try {
			Util.NumberUsers();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Util.Log("info", pluginname + " plugin " + pluginversion + " is enabled");
		if(Config.debug_enable) Util.Log("info", "Debug is ENABLED, get ready for some heavy spam");
		if(Config.custom_enabled) if(Config.custom_encryption == null) Util.Log("info", "**WARNING** SERVER IS RUNNING WITH NO ENCRYPTION: PASSWORDS ARE STORED IN PLAINTEXT");
		Util.Log("info", pluginname + " is developed by Contex <contex@authdb.com> and Wulfspider <wulfspider@authdb.com>");
	}

    public static boolean isAuthorized(int id) { return authorizedIds.contains(Integer.valueOf(id)); }
    public void unauthorize(int id) { AuthDB.authorizedIds.remove(Integer.valueOf(id)); } 
    public void authorize(int id) { AuthDB.authorizedIds.add(Integer.valueOf(id)); }
	public boolean checkPassword(String player, String password) 
	 {
		try 
		{
			try {
				MySQL.connect();
			} catch (ClassNotFoundException e) {
				Util.Debug("Cannot connect to MySQL server:");
				e.printStackTrace();
			}
			if(Util.CheckPassword(Config.script_name, player.toLowerCase(), password)) return true;
			else { Stop("Can't check password, stopping plugin."); }
			MySQL.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			Stop("ERRORS in checking password. Plugin will NOT work. Disabling it.");
		}
		return false;
	}
	
	void Stop(String error)
	{
		Util.Log("warning",error);
		getServer().getPluginManager().disablePlugin(((org.bukkit.plugin.Plugin) (this)));
	}
	
	public void register(String player, String password, String ipAddress) throws IOException, SQLException { register(player, password, "", ipAddress); }

	public void register(String player, String password, String email, String ipAddress) throws IOException, SQLException 
	{
		try {
			MySQL.connect();
		} catch (ClassNotFoundException e) {
			Util.Debug("Cannot connect to MySQL server:");
			e.printStackTrace();
		}
		Util.AddUser(player, email, password, ipAddress);
		MySQL.close();
	}

	public boolean isRegistered(String player) {
		try {
			
			Util.Debug("Running function: isRegistered(String player)");
			boolean dupe = false;
			try {
				MySQL.connect();
			} catch (ClassNotFoundException e) {
				Util.Debug("Cannot connect to MySQL server:");
				e.printStackTrace();
			}
			if(Util.CheckUser(Config.script_name, player.toLowerCase())) dupe = true;
			else { Stop("Can't find a forum board, stopping the plugin."); }
			Util.Debug("No user!");
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
		return (String)this.db.get(player.getName().toLowerCase());
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
			if(this.db.containsKey(player.getName().toLowerCase()))
				return true;
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
