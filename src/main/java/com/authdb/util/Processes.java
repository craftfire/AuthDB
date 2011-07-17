package com.authdb.util;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.authdb.AuthDB;
import com.authdb.util.databases.eBean;

public class Processes {
    public static boolean Logout(Player player) {
        if(AuthDB.isAuthorized(player)) {
            if(AuthDB.AuthTimeDB.containsKey(player.getName())) {
                AuthDB.AuthTimeDB.remove(player.getName()); 
            }
            
            AuthDB.authorizedNames.remove(player.getName()); 
            eBean eBeanClass = eBean.CheckPlayer(player);
            eBeanClass.setAuthorized("false");
            AuthDB.Database.save(eBeanClass);
                
            if(AuthDB.db3.containsKey(Encryption.md5(player.getName()))) {
                AuthDB.db3.remove(Encryption.md5(player.getName())); 
            }
            if(AuthDB.db2.containsKey(Encryption.md5(player.getName()+Util.GetIP(player)))) {
                AuthDB.db2.remove(Encryption.md5(player.getName()+Util.GetIP(player))); 
            }
            if(AuthDB.AuthDBSpamMessage.containsKey(player.getName())) {
                AuthDB.Server.getScheduler().cancelTask(AuthDB.AuthDBSpamMessage.get(player.getName()));
                AuthDB.AuthDBSpamMessage.remove(player.getName());
                AuthDB.AuthDBSpamMessageTime.remove(player.getName());
            }
            try {
                if(AuthDB.plugin.TimeoutTask("check",player, "0"))
                 {
                    int TaskID = Integer.parseInt(AuthDB.plugin.TimeoutGetTaskID(player));
                    Util.Debug(player.getName()+" is in the TimeoutTaskList with ID: "+TaskID);
                    if(AuthDB.plugin.TimeoutTask("remove",player, "0")) {
                        Util.Debug(player.getName()+" was removed from the TimeoutTaskList");
                        AuthDB.Server.getScheduler().cancelTask(TaskID);
                    }
                    else { Util.Debug("Could not remove "+player.getName()+" from the timeout list."); }
                 }
                else { Util.Debug("Could not find "+player.getName()+" in the timeout list, no need to remove."); }
                AuthDB.plugin.updateDb();
            } catch (IOException e) {
                Util.Debug("Error with the timeout list, can't cancel task?");
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    
    public static boolean Login(Player player) {
        if(!AuthDB.isAuthorized(player)) {
            long timestamp = Util.TimeStamp();
            if(!AuthDB.AuthTimeDB.containsKey(player.getName())) {
                AuthDB.AuthTimeDB.put(player.getName(), ""+timestamp);
            }
            AuthDB.authorizedNames.add(player.getName());
            eBean eBeanClass = eBean.CheckPlayer(player);
            eBeanClass.setAuthorized("true");
            eBeanClass.setRegistred("true");
            AuthDB.Database.save(eBeanClass);
            if(!AuthDB.db3.containsKey(Encryption.md5(player.getName()))) {
                AuthDB.db3.put(Encryption.md5(player.getName()), "yes");
            }
            if(AuthDB.db2.containsKey(Encryption.md5(player.getName()+Util.GetIP(player)))) {
                AuthDB.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
            }
            ItemStack[] inv = AuthDB.getInventory(player);
            if (inv != null) { player.getInventory().setContents(inv); }
            return true;
        }
        return false;
    }
    
    public boolean Link(Player player, String name) {
        if(AuthDB.isAuthorized(player)) {
            ItemStack[] inv = AuthDB.getInventory(player);
            if (inv != null) { player.getInventory().setContents(inv); }
            long timestamp = Util.TimeStamp();
            if(!AuthDB.AuthTimeDB.containsKey(player.getName())) {
                AuthDB.AuthTimeDB.put(player.getName(), ""+timestamp);
            }
            AuthDB.authorizedNames.add(player.getName());
            
            eBean eBeanClass = eBean.CheckPlayer(player);
            eBeanClass.setAuthorized("true");
            AuthDB.Database.save(eBeanClass);
            
            if(!AuthDB.db2.containsKey(Encryption.md5(player.getName()+Util.GetIP(player)))) {
                AuthDB.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
            }
            if(!AuthDB.db3.containsKey(Encryption.md5(player.getName()))) {
                AuthDB.db3.put(Encryption.md5(player.getName()), "yes");
            }
            if(!AuthDB.AuthOtherNamesDB.containsKey(Encryption.md5((player.getName())))) {
                AuthDB.AuthOtherNamesDB.put(player.getName(),name);
            }
            if(Config.link_rename) { player.setDisplayName(name); }
            return true;
        }
        return false;
    }
}
