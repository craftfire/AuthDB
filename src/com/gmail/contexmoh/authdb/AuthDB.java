/**
 * Copyright (C) 2011 Contex <contexmoh@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/
package com.gmail.contexmoh.authdb;

import java.io.BufferedWriter;
import java.io.File;
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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.gmail.contexmoh.authdb.boards.SMF1;
import com.gmail.contexmoh.authdb.boards.SMF2;
import com.gmail.contexmoh.authdb.boards.myBB1_6;
import com.gmail.contexmoh.authdb.boards.phpBB3;
import com.gmail.contexmoh.authdb.boards.vB4_1;
import com.gmail.contexmoh.authdb.listeners.AuthDBBlockListener;
import com.gmail.contexmoh.authdb.listeners.AuthDBEntityListener;
import com.gmail.contexmoh.authdb.listeners.AuthDBPlayerListener;
import com.gmail.contexmoh.authdb.plugins.zCraftIRC;
import com.gmail.contexmoh.authdb.utils.Config;
import com.gmail.contexmoh.authdb.utils.Messages;
import com.gmail.contexmoh.authdb.utils.MySQL;
import com.gmail.contexmoh.authdb.utils.Utils;


public class AuthDB extends JavaPlugin {
	//
	public static String pluginname = "AuthDB";
	public static String pluginversion = "1.4";
	public static CraftIRC craftircHandle = null;
	//
	private final AuthDBPlayerListener playerListener = new AuthDBPlayerListener(this);
	private final AuthDBBlockListener blockListener = new AuthDBBlockListener(this);
	private final AuthDBEntityListener entityListener = new AuthDBEntityListener(this);
	private static List<Integer> authorizedIds = new ArrayList();
	//dadada
	public static HashMap<String, String> db = new HashMap();
	public static String dbFileName = "auths.db";
	public static Logger log = Logger.getLogger("Minecraft");
	public HashMap<String, ItemStack[]> inventories = new HashMap();

	public void onDisable() 
	{
		zCraftIRC.SendMessage("disconnect",null);
		disableInventory();
		this.authorizedIds.clear();
		this.db.clear();
		MySQL.close();
	 }


	public void onEnable() 
	{
		Config TheMessages = new Config("messages","plugins/"+pluginname+"/", "messages.yml");
		/*if (null == getConfiguration().getKeys("messages")) 
		{
		    Utils.Log("info", "messages.yml could not be found in plugins/AuthDB/ -- disabling!");
		    getServer().getPluginManager().disablePlugin(((Plugin) (this)));
		    return;
		}*/
		Config TheConfig = new Config("config","plugins/"+pluginname+"/", "config.yml");
		if (null == getConfiguration().getKeys("settings")) 
		{
		    Utils.Log("info", "config.yml could not be found in plugins/AuthDB/ -- disabling!");
		    getServer().getPluginManager().disablePlugin(((Plugin) (this)));
		    return;
		}
		Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
		if (checkCraftIRC != null && Config.CraftIRC_enabled == true) {
		    try {
		        	Utils.Log("info", "CraftIRC Support Enabled"); 
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
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM, this.playerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, this.blockListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, this.blockListener, Event.Priority.Lowest, this);
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
		Utils.Log("info", pluginname + " plugin " + pluginversion + " is enabled");
		if(Config.debug_enable) Utils.Log("info", "Debug is ENABLED, get ready for some heavy spam");
		Utils.Log("info", pluginname + " is developed by Contex | contexmoh@gmail.com");
	}

    public static boolean isAuthorized(int id) { return authorizedIds.contains(Integer.valueOf(id)); }
    public void unauthorize(int id) { AuthDB.authorizedIds.remove(Integer.valueOf(id)); } 
    public void authorize(int id) { AuthDB.authorizedIds.add(Integer.valueOf(id)); }
	public boolean checkPassword(String player, String password) 
	 {
		try 
		{
			if(Config.script_name.equals(Config.script_name1))  { if(phpBB3.checkpassword(player.toLowerCase(), password)) return true; }
			else if(Config.script_name.equals(Config.script_name2))  { if(SMF1.checkpassword(player.toLowerCase(), password)) return true; }
			else if(Config.script_name.equals(Config.script_name3))  { if(SMF2.checkpassword(player.toLowerCase(), password)) return true; }
			else if(Config.script_name.equals(Config.script_name4))  { if(myBB1_6.checkpassword(player.toLowerCase(), password)) return true; }
			else if(Config.script_name.equals(Config.script_name5))  { if(vB4_1.checkpassword(player.toLowerCase(), password)) return true; }
			else { Stop("Can't check password, stopping plugin."); }
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
		Utils.Log("warning",error);
		getServer().getPluginManager().disablePlugin(((org.bukkit.plugin.Plugin) (this)));
	}
	
	public void register(String player, String password, String ipAddress) throws IOException, SQLException { register(player, password, "", ipAddress); }

	public void register(String player, String password, String email, String ipAddress) throws IOException, SQLException 
	{
		if(Config.script_name.equals(Config.script_name1)) { phpBB3.adduser(player, email, password, ipAddress); }
		else if(Config.script_name.equals(Config.script_name2)) { SMF1.adduser(player, email, password, ipAddress); }
		else if(Config.script_name.equals(Config.script_name3)) { SMF2.adduser(player, email, password, ipAddress); }
		else if(Config.script_name.equals(Config.script_name4)) { myBB1_6.adduser(player, email, password, ipAddress); }
		else if(Config.script_name.equals(Config.script_name5)) { vB4_1.adduser(player, email, password, ipAddress); }
		else { Stop("Can't register user, disabling plugin.");  }
	}

	public boolean isRegistered(String player) {
		try {
			if(Config.script_name.equals(Config.script_name1)) { if(phpBB3.checkuser(player.toLowerCase())) { return true; } }
			else if(Config.script_name.equals(Config.script_name2)) { if(SMF1.checkuser(player.toLowerCase())) { return true; } }
			else if(Config.script_name.equals(Config.script_name3)) { if(SMF2.checkuser(player.toLowerCase())) { return true; } }
			else if(Config.script_name.equals(Config.script_name4)) { if(myBB1_6.checkuser(player.toLowerCase())) { return true; } }
			else if(Config.script_name.equals(Config.script_name5)) { if(vB4_1.checkuser(player.toLowerCase())) { return true; } }
			else { Stop("Can't find a forum board, stopping the plugin."); }
		} catch (SQLException e) 
		{
			e.printStackTrace();
			Stop("ERRORS in checking user. Plugin will NOT work. Disabling it.");
		}
		return false;
	}


	public boolean checkEmail(String email)
	{
		return !email.contains("'");
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
	
	public void updateDb() throws IOException 
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(getDataFolder(), this.dbFileName)));
		Set keys = AuthDB.db.keySet();
		Iterator i = keys.iterator();
		while (i.hasNext()) 
		{
			String key = (String)i.next();
			bw.append(key + ":" + (String)AuthDB.db.get(key));
			bw.newLine();
		}
		bw.close();
	}
	
	public ItemStack[] getInventory(String player) 
	{
		File f = new File(getDataFolder(), player + "_inv");
		if (inventories.containsKey(player.toLowerCase())) 
		{
			if ((f.exists()) && (!f.delete()))
				Utils.Log("warning", "Unable to delete user inventory file: " + player + "_inv");
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
			Utils.Log("warning", "Unable to delete user inventory file: " + player + "_inv");
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
}
