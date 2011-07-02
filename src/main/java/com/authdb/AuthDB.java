/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.PropertyManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
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
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

import com.ensifera.animosity.craftirc.CraftIRC;

public class AuthDB extends JavaPlugin {
	//
    public static org.bukkit.Server Server;
    public static AuthDB plugin;
	PluginDescriptionFile pluginFile = getDescription();
	public static String pluginname = "AuthDB";
	public static String pluginversion = "2.3.0 indev";
    public static CraftIRC craftircHandle;
	//
	private final AuthDBPlayerListener playerListener = new AuthDBPlayerListener(this);
	private final AuthDBBlockListener blockListener = new AuthDBBlockListener(this);
	private final AuthDBEntityListener entityListener = new AuthDBEntityListener(this);
	private static List<Integer> authorizedIds = new ArrayList<Integer>();
	public static HashMap<String, String> db = new HashMap<String, String>();
	public static HashMap<String, String> db2 = new HashMap<String, String>();
	public static HashMap<String, String> db3 = new HashMap<String, String>();
	public static HashMap<String, String> AuthTimeDB = new HashMap<String, String>();
	public static HashMap<String, String> AuthPasswordTriesDB = new HashMap<String, String>();
	public static HashMap<String, String> AuthOtherNamesDB = new HashMap<String, String>();
	public static String idleFileName = "idle.db";
	public static String otherNamesFileName = "othernames.db";
	public static Logger log = Logger.getLogger("Minecraft");
	public HashMap<String, ItemStack[]> inventories = new HashMap<String, ItemStack[]>();

	public void onDisable() 
	{
		Util.Log("info", pluginname + " plugin " + pluginversion + " has been disabled");
		Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
	    if ((checkCraftIRC != null) && (checkCraftIRC.isEnabled()) && (Config.CraftIRC_enabled == true))
	    	zCraftIRC.SendMessage("disconnect",null);
		disableInventory();
		authorizedIds.clear();
		AuthTimeDB.clear();
		AuthOtherNamesDB.clear();
		AuthPasswordTriesDB.clear();
		db.clear();
		db2.clear();
		db3.clear();
		MySQL.close();
	 }

	public void onEnable() 
	{	 
		/* File file = new File("plugins/"+pluginname+"/AuthDB_Ban.jar");
			
			URLClassLoader clazzLoader = null;
			try {
				clazzLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
			} catch (MalformedURLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
//			System.class.getClassLoader()
			
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(file);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			Enumeration<JarEntry> entries = jarFile.entries();
			
			while (entries.hasMoreElements()) {
				JarEntry element = entries.nextElement();
				if (element.getName().endsWith(".class")) {
					try {
						Class c = clazzLoader.loadClass(element.getName().replaceAll(".class", "").replaceAll("/", "."));
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		*/
		
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
		
		File f = new File("plugins/"+pluginname+"/messages.yml");
		if ( !f.exists() )
		{
		    Util.Log("info", "messages.yml could not be found in plugins/AuthDB/! Creating messages.yml!");
		    DefaultFile("messages.yml");

		    //getServer().getPluginManager().disablePlugin(((Plugin) (this)));
		    //return;
		}
		new Config("messages","plugins/"+pluginname+"/", "messages.yml");
		
		f = new File("plugins/"+pluginname+"/config.yml");
		if ( !f.exists() )
		{
			Util.Log("info", "config.yml could not be found in plugins/AuthDB/! Creating config.yml!");
			DefaultFile("config.yml");
		   // getServer().getPluginManager().disablePlugin(((Plugin) (this)));
		   // return;
		}
		new Config("config","plugins/"+pluginname+"/", "config.yml");

	      final Plugin checkCraftIRC = getServer().getPluginManager().getPlugin("CraftIRC");
	      if ((checkCraftIRC != null) && (Config.CraftIRC_enabled == true)) {
	    	  craftircHandle = ((CraftIRC)checkCraftIRC);
	    	  Util.Log("info","Found supported plugin: " + checkCraftIRC.getDescription().getName());
	    	  this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    		@Override
		    		public void run() { if(checkCraftIRC.isEnabled()) { zCraftIRC.SendMessage("connect", null); } } }, 100);
	    	  
	      }
	      final Plugin Backpack = getServer().getPluginManager().getPlugin("Backpack");
	      if (Backpack != null) { Config.HasBackpack = true; }
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Low, this);
	    pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.playerListener, Event.Priority.Normal, this);
	    pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, this.playerListener, Event.Priority.Normal, this);
	    pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Event.Priority.Normal, this);
	    pm.registerEvent(Event.Type.ENTITY_TARGET, this.entityListener, Event.Priority.Normal, this);

	    PropertyManager TheSettings = new PropertyManager(new File("server.properties"));
		if (TheSettings.getBoolean("online-mode", true)) { Config.OnlineMode = true; }
		UpdateLinkedNames();
	    
		MySQL.connect();
		try 
		{
			Util.CheckScript("numusers",Config.script_name,null,null,null,null);
		} 
		catch (SQLException e) 
		{
			if(Config.custom_enabled)
			{
				String enter = "\n";
				Util.Log("info", "Creating default table schema for "+Config.custom_table);
				Util.Debug(enter+"CREATE TABLE IF NOT EXISTS `"+Config.custom_table+"` ("+enter+"`id` int(4) NOT NULL auto_increment,"+enter+"`username` varchar(40) NOT NULL,"+enter+"`password` varchar(40) NOT NULL,"+enter+"`email` varchar(100) NOT NULL,"+enter+"PRIMARY KEY (`id`),"+enter+"UNIQUE KEY `username` (`username`)"+enter+") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");
				String query = "CREATE TABLE IF NOT EXISTS `"+Config.custom_table+"` (`id` int(4) NOT NULL auto_increment,`username` varchar(40) NOT NULL,`password` varchar(40) NOT NULL,`email` varchar(100) NOT NULL,PRIMARY KEY (`id`),UNIQUE KEY `username` (`username`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
				try
				{
					MySQL.query(query);
		    		Util.Log("info", "Sucessfully created table "+Config.custom_table);
		    		PreparedStatement ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.custom_table+"`");
					ResultSet rs = ps.executeQuery();
					if (rs.next()) { Util.Log("info", rs.getInt("countit") + " user registrations in database"); }
				} 
				catch (SQLException e1) {
					Util.Log("info", "Failed creating user table "+Config.custom_table);
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
        Util.AddOtherNamesToDB();
		
		Util.Log("info", pluginname + " plugin " + pluginversion + " is enabled");
		if(Config.debug_enable) Util.Log("info", "Debug is ENABLED, get ready for some heavy spam");
		if(Config.custom_enabled) if(Config.custom_encryption == null) Util.Log("info", "**WARNING** SERVER IS RUNNING WITH NO ENCRYPTION: PASSWORDS ARE STORED IN PLAINTEXT");
		Util.Log("info", pluginname + " is developed by CraftFire <dev@craftfire.com>");
		
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
			try { Util.PostInfo(getServer().getServerName(),getServer().getVersion(),pluginversion,System.getProperty("os.name"),System.getProperty("os.version"),System.getProperty("os.arch"),System.getProperty("java.version"),thescript,theversion,Plugins,online,max,Server.getPort()); } 
			catch (IOException e1) { if(Config.debug_enable) Util.Debug("Could not send usage stats to main server."); }
		}
	}


    public static boolean isAuthorized(int id) { return authorizedIds.contains(Integer.valueOf(id)); }
    public void unauthorize(int id) { AuthDB.authorizedIds.remove(Integer.valueOf(id)); } 
    public void authorize(int id) { AuthDB.authorizedIds.add(Integer.valueOf(id)); }
	public boolean checkPassword(String player, String password) 
	 {
		try 
		{
			MySQL.connect();
			password = Matcher.quoteReplacement(password);
			if (Util.CheckOtherName(player) != player)
			{
				player = Util.CheckOtherName(player);
			}
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
	 
	public boolean register(Player theplayer, String password, String email, String ipAddress) throws IOException, SQLException 
	{	
		password = Matcher.quoteReplacement(password);
		if(password.length() < Integer.parseInt(Config.password_minimum))
		{
			Messages.SendMessage("AuthDB_message_password_minimum", theplayer, null);
			return false;
		}
		else if(password.length() > Integer.parseInt(Config.password_maximum))
		{
			Messages.SendMessage("AuthDB_message_password_maximum", theplayer, null);
			return false;
		}
		MySQL.connect();
		String player = theplayer.getName();
		if (!Util.CheckFilter("password",password))
		{
			Messages.SendMessage("AuthDB_message_badcharacters_password", theplayer, null);
		}
		else
		{
			Util.CheckScript("adduser",Config.script_name,player, password, email, ipAddress);
		}
		MySQL.close();
		return true;
	}

	public boolean isRegistered(String when, String player) {
		boolean dupe = false;
		boolean checkneeded = true;
		//if(Config.debug_enable) 
			//Util.Debug("Running function: isRegistered(String player)");
		if(when.equals("join"))
		{
			MySQL.connect();
			Config.HasForumBoard = false;
			try {
				if(Util.CheckScript("checkuser",Config.script_name, player.toLowerCase(), null, null, null))
				{
					db3.put(Encryption.md5(player), "yes");
					dupe = true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//if(!Config.HasForumBoard) 
				//Stop("Can't find a forum board, stopping the plugin.");
			MySQL.close();
			if(!dupe)
				db3.put(Encryption.md5(player), "no");
			return dupe;
		}
		else if(when.equals("command"))
		{
			MySQL.connect();
			Config.HasForumBoard = false;
			try {
				if(Util.CheckScript("checkuser",Config.script_name, player.toLowerCase(), null, null, null))
				{
					db3.put(Encryption.md5(player), "yes");
					dupe = true;
				}
				else if (Util.CheckOtherName(player) != player)
				{
					db3.put(Encryption.md5(player), "yes");
					dupe = true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//if(!Config.HasForumBoard) 
				//Stop("Can't find a forum board, stopping the plugin.");
			MySQL.close();
			if(!dupe)
				db3.put(Encryption.md5(player), "no");
			return dupe;
		}
		else
		{
			if(this.db3.containsKey(Encryption.md5(player)))
			{ 
				if(Config.debug_enable) Util.Debug("Found cache registration for "+player);
				String check =db3.get(Encryption.md5(player));
				if(check.equals("yes"))
				{
					//if(Config.debug_enable) Util.Debug("Cache "+player+" passed with value "+check);
					checkneeded = false;
					return true;
				}
				else if(check.equals("no"))
				{
					//if(Config.debug_enable) Util.Debug("Cache "+player+" did NOT pass with value "+check);
					return false;
				}
			}
			else if(checkneeded)
			{
				try {
					MySQL.connect();
					Config.HasForumBoard = false;
					if(Util.CheckScript("checkuser",Config.script_name, player.toLowerCase(), null, null, null))
					{
						db3.put(Encryption.md5(player), "yes");
						dupe = true;
					}
					//if(!Config.HasForumBoard) 
						//Stop("Can't find a forum board, stopping the plugin.");
					MySQL.close();
					if(!dupe)
						db3.put(Encryption.md5(player), "no");
					return dupe;
				} catch (SQLException e) 
				{
					e.printStackTrace();
					Stop("ERRORS in checking user. Plugin will NOT work. Disabling it.");
				}
			}
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

	  public void storeInventory(String player, ItemStack[] theinventory) throws IOException {
	    File inv = new File(getDataFolder(), player + "_inv");
	    if (inv.exists()) { return; }
	    inv.createNewFile();
	    BufferedWriter bw = new BufferedWriter(new FileWriter(inv));
	    for (short i = 0; i < theinventory.length; i = (short)(i + 1)) 
	    {
	    	if(theinventory[i] != null)
	    	{
	    		bw.write(theinventory[i].getTypeId() + ":" + theinventory[i].getAmount() + ":" + (theinventory[i].getData() == null ? "" : Byte.valueOf(theinventory[i].getData().getData())) + ":" + theinventory[i].getDurability());
	    	}
	    	else { bw.write("0:0::0"); }
	    	bw.newLine();
	    }
	    bw.close();
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
		    Set<String> keys = this.db.keySet();
		    Iterator<String> i = keys.iterator();
		    while (i.hasNext()) {
		      String key = (String)i.next();
		      bw.append(key + ":" + (String)this.db.get(key));
		      bw.newLine();
		    }
		    bw.close();
		  }

	
    public void UpdateLinkedNames()
    {
        for (Player player : this.getServer().getOnlinePlayers()) 
        {
            if(Util.CheckOtherName(player.getName()) != player.getName())
            {
                player.setDisplayName(Util.CheckOtherName(player.getName()));
            }
        }
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

	    if (f.exists()) {
	    	ItemStack[] inv;
	    	if(Config.HasBackpack) { inv = new ItemStack[252]; }
	    	else { inv = new ItemStack[36]; }
	      try {
	        Scanner s = new Scanner(f);
	        short i = 0;
	        while (s.hasNextLine()) {
	          String line = s.nextLine();
	          String[] split = line.split(":");
	          if (split.length == 4) {
	            int type = Integer.valueOf(split[0]).intValue();
	            inv[i] = new ItemStack(type, Integer.valueOf(split[1]).intValue());

	            short dur = Short.valueOf(split[3]).shortValue();
	            if (dur > 0)
	              inv[i].setDurability(dur);
	            byte dd;
	            if (split[2].length() == 0)
	              dd = 0;
	            else
	              dd = Byte.valueOf(split[2]).byteValue();
	            Material mat = Material.getMaterial(type);
	            if (mat == null)
	              inv[i].setData(new MaterialData(type, dd));
	            else
	              inv[i].setData(mat.getNewData(dd));
	            i = (short)(i + 1);
	          }
	        }
	        s.close();
	        if (!f.delete())
	        	Util.Log("warning","Unable to delete user inventory file: " + player + "_inv");
	      } catch (IOException e) {
	    	  Util.Log("severe", "Inventory file read error:");
	        e.printStackTrace();
	      }
	      return inv;
	    }

	    return null;
	  }
	
	  public void deleteInventory(String player) {
		    File f = new File(getDataFolder(), player + "_inv");
		    if (f.exists())
		      f.delete();
		  }
		
	 private void DefaultFile(String name) {
		    File actual = new File(getDataFolder(), name);
		    File direc = new File(getDataFolder(),"");
		    if (!direc.exists()) { direc.mkdir(); }
		    if (!actual.exists()) {
		      java.io.InputStream input = getClass().getResourceAsStream("/files/" + name);
		      if (input != null) {
		    	  java.io.FileOutputStream output = null;
		        try
		        {
		          output = new java.io.FileOutputStream(actual);
		          byte[] buf = new byte[8192];
		          int length = 0;

		          while ((length = input.read(buf)) > 0) {
		            output.write(buf, 0, length);
		          }

		          System.out.println("["+pluginname+"] Written default setup for " + name);
		        } catch (Exception e) {
		          e.printStackTrace();
		        } finally {
		          try {
		            if (input != null)
		              input.close();
		          } catch (Exception e) {
		          }
		          try {
		            if (output != null)
		              output.close();
		          }
		          catch (Exception e)
		          {
		          }
		        }
		      }
		    }
		  }
	  
}
