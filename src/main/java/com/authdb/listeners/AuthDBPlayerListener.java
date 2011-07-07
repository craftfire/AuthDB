/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.listeners;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.authdb.AuthDB;
import com.authdb.plugins.zBukkitContrib;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Messages;
import com.authdb.util.Messages.Message;
import com.authdb.util.Util;

import com.afforess.backpack.BackpackManager;
import com.afforess.backpack.BackpackPlayer;

public class AuthDBPlayerListener extends PlayerListener
{
  private final AuthDB plugin;
  boolean sessionallow;
  int Schedule;

  public AuthDBPlayerListener(AuthDB instance)
  {
   this.plugin = instance;
  }

public void onPlayerLogin(PlayerLoginEvent event)
{
    Player player = event.getPlayer();
    if (Config.session_protect && Util.CheckIfLoggedIn(player))
    {
        Messages.SendMessage(Message.session_protected, player, event);
    }
    if(Config.filter_action.equals("kick") || Config.filter_action.equals("rename"))
    {
        String name = player.getName();
        if (Util.CheckFilter("username",name) == false && Util.CheckWhitelist("username",player) == false)
        {
          Util.Debug("The player is not in the whitelist and has bad characters in his/her name");
          if(Config.filter_action.equals("kick")) Messages.SendMessage(Message.filter_username, player, event);
        }
    }
    if(player.getName().length() < Integer.parseInt(Config.username_minimum))
    {
        Messages.SendMessage(Message.username_minimum, player, event);
    }
    else if(player.getName().length() > Integer.parseInt(Config.username_maximum))
    {
        Messages.SendMessage(Message.username_maximum, player, event);
    }
    if(Config.link_rename && Util.CheckOtherName(player.getName()) != player.getName())
    {
        Util.RenamePlayer(player,Util.CheckOtherName(player.getName()));
    }
}

public boolean CheckTimeout(Player player) throws IOException
{
    Util.Debug("Launching function: CheckTimeout(Player player))");
    if (AuthDB.isAuthorized(player.getEntityId()) == false && this.plugin.TimeoutTask("check",player, ""+Schedule))
    {
        Messages.SendMessage(Message.idle_kick, player, null);
        return true;
    }
    return false;
}

  public void onPlayerJoin(PlayerJoinEvent event)
  { 
    final Player player = event.getPlayer();
    player.teleport(Util.LandLocation(player.getLocation()));
    if(Config.link_rename && Util.CheckOtherName(player.getName()) != player.getName())
    {
        String message = event.getJoinMessage();
        message = message.replaceAll(player.getName(), player.getDisplayName());
        event.setJoinMessage(message);
    }
    this.plugin.AuthPasswordTriesDB.put(player.getName(),"0");
    try {
        if(Config.session_length != 0)
        {
            long timestamp = System.currentTimeMillis()/1000;
            if(this.plugin.TimeoutTask("check2",player, "") == true)
            { 
                long storedtime = Long.parseLong(this.plugin.db2.get(Encryption.md5(player.getName()+Util.GetIP(player))));
                Util.Debug("Found session for "+player.getName()+", timestamp: "+storedtime);
                long timedifference = timestamp - storedtime;
                Util.Debug("Difference: "+timedifference);
                if(timedifference > Config.session_length) { sessionallow = false; }
                else { sessionallow = true; }
                
            }
            else { sessionallow = false; }
        }
            
        try {
            if(Config.login_timeout > 0 && sessionallow == false)
            {
                    Util.Debug("Timeout time is: "+Config.login_timeout);
                    Schedule = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() 
                    { 
                        try {
                            CheckTimeout(player);
                        } catch (IOException e) {
                            Util.Log("warning", "Error checking if player was in the timeout list");
                            e.printStackTrace();
                        } 
                    } }, Config.login_timeout);
                if(this.plugin.TimeoutTask("add",player, ""+Schedule))
                    Util.Debug(player.getName()+" added to the CheckTimeoutTaskList");
                this.plugin.updateDb();
            }
        if(Config.custom_enabled) if(Config.custom_encryption == null) { player.sendMessage("§4YOUR PASSWORD WILL NOT BE ENCRYPTED, PLEASE BE ADWARE THAT THIS SERVER STORES THE PASSWORDS IN PLAINTEXT."); }
         if(event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1)
         {
             player.setHealth(20);
             player.teleport(player.getWorld().getSpawnLocation());
         }
        
        if(sessionallow)
        {
            Messages.SendMessage(Message.session_valid, player,null);
            long thetimestamp = System.currentTimeMillis()/1000;
            this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
            this.plugin.authorize(event.getPlayer().getEntityId());
        }
        else if (this.plugin.isRegistered("join",player.getName()) || this.plugin.isRegistered("join",Util.CheckOtherName(player.getName()))) 
        {
            if(Config.HasBackpack) 
            { 
                BackpackPlayer BackpackPlayer = BackpackManager.getBackpackPlayer((Player)player); 
                BackpackPlayer.createBackpack();
                this.plugin.storeInventory(player.getName(), BackpackPlayer.getContents()); 
            }
            else 
            { 
                this.plugin.storeInventory(player.getName(), player.getInventory().getContents()); 
            }
            player.getInventory().clear();
             plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {  @Override public void run() { if(!AuthDB.isAuthorized(player.getEntityId())) { if(player.getInventory() != null) {  player.getInventory().clear(); } } } } , 20);
             if(Util.ToLoginMethod(Config.login_method).equals("prompt"))
             {
                 Messages.SendMessage(Message.login_prompt, player,null);
             }
             else
             {
                 Messages.SendMessage(Message.login_default, player,null);
             }
         } 
        else if (Config.register_force) 
        {
            if(Config.HasBackpack) 
            { 
                BackpackPlayer BackpackPlayer = BackpackManager.getBackpackPlayer((Player)player);
                BackpackPlayer.createBackpack();
                this.plugin.storeInventory(player.getName(), BackpackPlayer.getInventory().getContents()); 
            }
            else { this.plugin.storeInventory(player.getName(), player.getInventory().getContents()); }
               player.getInventory().clear();
              Messages.SendMessage(Message.welcome_guest, player,null);
          }
         else if (!Config.register_force) { 
                      Messages.SendMessage(Message.welcome_guest, player,null);
             }
         else {
                long thetimestamp = System.currentTimeMillis()/1000;
                this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
                this.plugin.authorize(event.getPlayer().getEntityId());
          }
        } catch (IOException e) {
          Util.Log("severe","["+AuthDB.pluginname+"] Inventory file error:");
          player.kickPlayer("inventory protection kicked");
           e.printStackTrace();
        player.sendMessage(Color.red + "Error happend, report to admins!");
        }
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }

  public void onPlayerQuit(PlayerQuitEvent event)
  {
     Player player = event.getPlayer();
     Messages.SendMessage(Message.left_server, player,null);
     if(AuthDB.AuthDBSpamMessage.containsKey(player.getName())) 
     { 
         AuthDB.Server.getScheduler().cancelTask(AuthDB.AuthDBSpamMessage.get(player.getName())); 
         AuthDB.AuthDBSpamMessage.remove(player.getName());
         AuthDB.AuthDBSpamMessageTime.remove(player.getName());
     }
     if(event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1)
     {
         player.setHealth(20);
         player.teleport(player.getWorld().getSpawnLocation());
     }
     try {
        if(this.plugin.TimeoutTask("check",player, "0"))
         {
            int TaskID = Integer.parseInt(this.plugin.TimeoutGetTaskID(player));
            Util.Debug(player.getName()+" is in the TimeoutTaskList with ID: "+TaskID);
            if(this.plugin.TimeoutTask("remove",player, "0"))
            {
                Util.Debug(player.getName()+" was removed from the TimeoutTaskList");
                plugin.getServer().getScheduler().cancelTask(TaskID);
            }
            else { Util.Debug("Could not remove "+player.getName()+" from the timeout list."); }
         }
        else { Util.Debug("Could not find "+player.getName()+" in the timeout list, no need to remove."); }
        this.plugin.updateDb();
    } catch (IOException e) {
        Util.Debug("Error with the timeout list, can't cancel task?");
        e.printStackTrace();
    }
        long thetimestamp = System.currentTimeMillis()/1000;
        if(Config.session_start.equals("logoff"))
            this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+thetimestamp);
        this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
        this.plugin.unauthorize(player.getEntityId());
        
     if (CheckGuest(player,Config.guests_inventory) == false && this.plugin.isRegistered("quit",player.getName()) == false && this.plugin.isRegistered("quit",Util.CheckOtherName(player.getName())) == false)
      {
         ItemStack[] theinv = new ItemStack[36];
         player.getInventory().setContents(theinv);
      }
  }
  
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
  {
      String Contrib = event.getMessage();
      Contrib = Contrib.replaceAll("/", "");
      Util.Debug(""+zBukkitContrib.CheckCommand("2.0.0"));
      if(!zBukkitContrib.CheckCommand(Contrib))
      {
        String[] split = event.getMessage().split(" ");
        Player player = event.getPlayer();
    
        if (split[0].equals("/login"))
        {
            if (this.plugin.isRegistered("command",player.getName()) == false || this.plugin.isRegistered("command",Util.CheckOtherName(player.getName())) == false)
            {
                Messages.SendMessage(Message.login_notregistered, player,null);
            }
            else if (AuthDB.isAuthorized(player.getEntityId())) 
            {              
                Messages.SendMessage(Message.login_authorized, player,null);
            }
            else if (split.length < 2) 
            {
                Messages.SendMessage(Message.login_usage, player,null);
            }
            else if (this.plugin.checkPassword(player.getName(), split[1])) 
            {
                ItemStack[] inv = this.plugin.getInventory(player.getName());
                if (inv != null) { player.getInventory().setContents(inv); }
                long thetimestamp = System.currentTimeMillis()/1000;
                this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
                this.plugin.authorize(player.getEntityId());
                long timestamp = System.currentTimeMillis()/1000;
                this.plugin.db3.put(Encryption.md5(player.getName()), "yes");
                this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
                Util.Debug("Session started for "+player.getName());
                Messages.SendMessage(Message.login_success, player,null);
            } 
            else
            {
              Messages.SendMessage(Message.login_failure, player,null);
            }
            Util.Debug(player.getName()+" login ********");
            event.setMessage("/login ******");
            event.setCancelled(true);
         }
        else if (split[0].equals("/link"))
        {
            if(Config.link_enabled)
            {
                if (split.length == 3) 
                {
                    if(Util.CheckOtherName(player.getName()).equals(player.getName()))
                    {
                          if (this.plugin.checkPassword(split[1], split[2])) 
                          {
                              ItemStack[] inv = this.plugin.getInventory(player.getName());
                              if (inv != null) { player.getInventory().setContents(inv); }
                              long thetimestamp = System.currentTimeMillis()/1000;
                              this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
                              this.plugin.authorize(player.getEntityId());
                              long timestamp = System.currentTimeMillis()/1000;
                              this.plugin.db3.put(Encryption.md5(player.getName()), "yes");
                              this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
                              this.plugin.AuthOtherNamesDB.put(player.getName(),split[1]);
                              Util.ToFile("write",  player.getName(), split[1]);
                              Util.Debug("Session started for "+player.getName());
                              if(Config.link_rename) { player.setDisplayName(split[1]); }
                              Messages.SendMessage(Message.link_success, player,null);
                          } 
                          else { Messages.SendMessage(Message.link_failure, player,null); }
                    }
                    else { Messages.SendMessage(Message.link_exists, player,null); }
                }
                else { Messages.SendMessage(Message.link_usage, player,null); }
                Util.Debug(player.getName()+" link ******** ********");
                event.setMessage("/link ****** ********");
                event.setCancelled(true);
            }
         }
        else if (split[0].equals("/unlink"))
        {
            if(Config.unlink_enabled)
                {
                if (split.length == 3) 
                {
                    if(Util.CheckOtherName(player.getName()).equals(player.getDisplayName()))
                    {
                          if (this.plugin.checkPassword(split[1], split[2])) 
                          {
                              ItemStack[] inv = this.plugin.getInventory(player.getName());
                              if (inv != null) { player.getInventory().setContents(inv); }
                              this.plugin.unauthorize(player.getEntityId());
                              this.plugin.db2.remove(Encryption.md5(player.getName()+Util.GetIP(player)));
                              this.plugin.db3.remove(Encryption.md5(player.getName()));
                               this.plugin.AuthOtherNamesDB.remove(player.getName());
                               Util.ToFile("remove", player.getName(), null);
                               if(Config.unlink_rename) { player.setDisplayName(player.getName()); }
                              Messages.SendMessage(Message.unlink_success, player,null);
                          } 
                          else { Messages.SendMessage(Message.unlink_failure, player,null); }
                    }
                    else { Messages.SendMessage(Message.unlink_nonexist, player,null); }
                }
                else { Messages.SendMessage(Message.unlink_usage, player,null); }
                Util.Debug(player.getName()+" unlink ******** ********");
                event.setMessage("/unlink ****** ********");
                event.setCancelled(true);
            }
         }
        else if (split[0].equals("/register")) {
            Boolean email = true;
            if(Config.custom_emailfield == null || Config.custom_emailfield == "") { email = false; }
          if (!Config.register_enabled)
              Messages.SendMessage(Message.register_disabled, player,null);
          else if (this.plugin.isRegistered("register-command",player.getName()) || this.plugin.isRegistered("register-command",Util.CheckOtherName(player.getName())))
              Messages.SendMessage(Message.register_registered, player,null);
          else if (split.length < 2) {
                      Messages.SendMessage(Message.register_usage, player,null);
          }
          else if (split.length < 3 && email)
          {
                      Messages.SendMessage(Message.email_required, player,null);
          }
           else if ((split.length >= 3 && email) && (!this.plugin.checkEmail(split[2])))
                      Messages.SendMessage(Message.email_invalid, player,null);
          else {
            try {
               if (split.length >= 3 || ( !email && split.length >= 2 )) 
               { 
                   String themail = null;
                   if(!email) { themail = null; }
                   else { themail = split[2]; }
                if(this.plugin.register(player, split[1], themail,Util.GetIP(player)))
                {
                    ItemStack[] inv = this.plugin.getInventory(player.getName());
                    if (inv != null) { player.getInventory().setContents(inv); }
                    long timestamp = System.currentTimeMillis()/1000;
                    this.plugin.db3.put(Encryption.md5(player.getName()), "yes");
                    this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
                    Util.Debug("Session started for "+player.getName());
                    this.plugin.authorize(player.getEntityId());
                    long thetimestamp = System.currentTimeMillis()/1000;
                    this.plugin.AuthTimeDB.put(player.getName(), ""+thetimestamp);
                      Location temploc = event.getPlayer().getLocation();
                      while(temploc.getBlock().getTypeId() == 0) { temploc.setY(temploc.getY() - 1); }
                      temploc.setY(temploc.getY() + 1);
                      event.getPlayer().teleport(temploc);
                      
                    Messages.SendMessage(Message.register_success, player,null);
                }
            }
            }catch (IOException e) {
                        Messages.SendMessage(Message.register_failure, player,null);
              e.printStackTrace();
            } catch (SQLException e) {
                        Messages.SendMessage(Message.register_failure, player,null);
              e.printStackTrace();
            }
          }
          Util.Debug(player.getName()+" register ********");
          event.setMessage("/register *****");
          event.setCancelled(true);
         } 
         else if (!AuthDB.isAuthorized(player.getEntityId())) 
         {
          if (!CheckGuest(player,Config.guests_commands))
          {
              event.setMessage("/iamnotloggedin");
              event.setCancelled(true);
          }
        }
     }
     else
     {
         Util.Debug("BukkitContrib is trying to check for SP client with command: "+event.getMessage());
     }
  }

  public void onPlayerMove(PlayerMoveEvent event)
  {
    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId())) 
    {
          if (!CheckGuest(event.getPlayer(),Config.guests_movement))
            {
              event.setCancelled(true);
              event.getPlayer().teleport(event.getFrom());
            }
    }
  }

  public void onPlayerChat(PlayerChatEvent event)
  {
    if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
    {
          if(Util.ToLoginMethod(Config.login_method).equals("prompt") && (this.plugin.isRegistered("chat",event.getPlayer().getName()) || this.plugin.isRegistered("chat",Util.CheckOtherName(event.getPlayer().getName()))))
          {
              String[] split = event.getMessage().split(" ");
              Player player = event.getPlayer();
              if (this.plugin.isRegistered("chatprompt",player.getName()) || this.plugin.isRegistered("chatprompt",Util.CheckOtherName(player.getName()))) 
              {
                    if (AuthDB.isAuthorized(player.getEntityId())) {              
                              Messages.SendMessage(Message.login_authorized, player,null);
                  }
                  else if (split.length > 1) {
                              player.sendMessage("§bPlease type in the password for "+Util.CheckOtherName(player.getName()));
                  }
                  else if (this.plugin.checkPassword(player.getName(), split[0]) || this.plugin.checkPassword(Util.CheckOtherName(player.getName()), split[0])) {
                    ItemStack[] inv = this.plugin.getInventory(player.getName());
                    if (inv != null) { player.getInventory().setContents(inv); }
                    this.plugin.authorize(player.getEntityId());
                    long timestamp = System.currentTimeMillis()/1000;
                    this.plugin.db3.put(Encryption.md5(player.getName()), "yes");
                    this.plugin.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
                    Util.Debug("Session started for "+player.getName());
                    Messages.SendMessage(Message.login_success, player,null);
                } else {
                  /* ItemStack[] inv = this.plugin.getInventory(player.getName());
                      if (inv != null)
                      {
                          player.getInventory().setContents(inv);
                      } */
                      Messages.SendMessage(Message.login_failure, player,null);
                  }
                  Util.Debug(player.getName()+" login ********");
                  event.setMessage(" has logged in!");
                  event.setCancelled(true);  
              }
              event.setMessage("");
              event.setCancelled(true);  
          }
          else if(!CheckGuest(event.getPlayer(),Config.guests_chat))
          {
              event.setCancelled(true);
          }
    }
  }
  
  public void onPlayerPickupItem(PlayerPickupItemEvent event) 
  {
        if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
        {
             if (!CheckGuest(event.getPlayer(),Config.guests_pickup))
              {
                 event.setCancelled(true);
              }
        }
  }
  
  public void onPlayerInteract(PlayerInteractEvent event)
  {
        if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
        {
             if (!CheckGuest(event.getPlayer(),Config.guests_interact))
              {
                 event.setCancelled(true);
              }
        }
  }
  
  public void onPlayerDropItem(PlayerDropItemEvent event) 
  {
        if (!AuthDB.isAuthorized(event.getPlayer().getEntityId()))
        {
             if (this.plugin.isRegistered("dropitem",event.getPlayer().getName()) || this.plugin.isRegistered("dropitem",Util.CheckOtherName(event.getPlayer().getName())))
             {
                 event.setCancelled(true);
             }
             else if (!CheckGuest(event.getPlayer(),Config.guests_drop))
              {
                 event.setCancelled(true);
              }
        }
  }
  
  public void onPlayerRespawn(PlayerRespawnEvent event) 
  {
         if(event.getPlayer().getHealth() == 0 || event.getPlayer().getHealth() == -1)
         {
             event.getPlayer().setHealth(20);
             event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
         }
  }
  
    public boolean CheckGuest(Player player,boolean what)
    {
     if(what)
     {
      if (this.plugin.isRegistered("checkguest",player.getName()) == false || this.plugin.isRegistered("checkguest",Util.CheckOtherName(player.getName())) == false)
      {
              return true;
      }
     }
     return false;
    }
}
