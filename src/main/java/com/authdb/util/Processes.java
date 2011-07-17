package com.authdb.util;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.authdb.AuthDB;
import com.authdb.util.databases.eBean;

public class Processes {
    public static boolean Logout(Player player) {
        if(AuthDB.isAuthorized(player)) {
            if(AuthDB.AuthDB_AuthTime.containsKey(player.getName())) {
                AuthDB.AuthDB_AuthTime.remove(player.getName()); 
            }
            
            AuthDB.authorizedNames.remove(player.getName()); 
            eBean eBeanClass = eBean.CheckPlayer(player);
            eBeanClass.setAuthorized("false");
            AuthDB.Database.save(eBeanClass);
                
            if(AuthDB.AuthDB_Authed.containsKey(Encryption.md5(player.getName()))) {
                AuthDB.AuthDB_Authed.remove(Encryption.md5(player.getName())); 
            }
            if(AuthDB.AuthDB_Sessions.containsKey(Encryption.md5(player.getName()+Util.GetIP(player)))) {
                AuthDB.AuthDB_Sessions.remove(Encryption.md5(player.getName()+Util.GetIP(player))); 
            }
            if(AuthDB.AuthDB_SpamMessage.containsKey(player.getName())) {
                AuthDB.Server.getScheduler().cancelTask(AuthDB.AuthDB_SpamMessage.get(player.getName()));
                AuthDB.AuthDB_SpamMessage.remove(player.getName());
                AuthDB.AuthDB_SpamMessageTime.remove(player.getName());
            }
            if(AuthDB.AuthDB_Timeouts.containsKey(player.getName()))
             {
                int TaskID = AuthDB.AuthDB_Timeouts.get(player.getName());
                Util.Logging.Debug(player.getName()+" is in the TimeoutTaskList with ID: "+TaskID);
                if(AuthDB.AuthDB_Timeouts.remove(player.getName()) != null) {
                    Util.Logging.Debug(player.getName()+" was removed from the TimeoutTaskList");
                    AuthDB.Server.getScheduler().cancelTask(TaskID);
                }
                else { Util.Logging.Debug("Could not remove "+player.getName()+" from the timeout list."); }
             }
            else { Util.Logging.Debug("Could not find "+player.getName()+" in the timeout list, no need to remove."); }
            return true;
        }
        return false;
    }
    
    public static boolean Login(Player player) {
        if(!AuthDB.isAuthorized(player)) {
            long timestamp = Util.TimeStamp();
            if(!AuthDB.AuthDB_AuthTime.containsKey(player.getName())) {
                AuthDB.AuthDB_AuthTime.put(player.getName(), timestamp);
            }
            AuthDB.authorizedNames.add(player.getName());
            eBean eBeanClass = eBean.CheckPlayer(player);
            eBeanClass.setAuthorized("true");
            eBeanClass.setRegistred("true");
            AuthDB.Database.save(eBeanClass);
            if(!AuthDB.AuthDB_Authed.containsKey(Encryption.md5(player.getName()))) {
                AuthDB.AuthDB_Authed.put(Encryption.md5(player.getName()), "yes");
            }
            if(AuthDB.AuthDB_Sessions.containsKey(Encryption.md5(player.getName()+Util.GetIP(player)))) {
                AuthDB.AuthDB_Sessions.put(Encryption.md5(player.getName()+Util.GetIP(player)), timestamp);
            }
            ItemStack[] inv = AuthDB.getInventory(player);
            if (inv != null) { player.getInventory().setContents(inv); }
            return true;
        }
        return false;
    }
    
    public static boolean Link(Player player, String name) {
        if(!AuthDB.isAuthorized(player) && Config.link_enabled) {
            eBean eBeanClass = eBean.CheckPlayer(player);
            String LinkedNames = eBeanClass.getLinkedname();
            if(LinkedNames != null && LinkedNames != "") { eBeanClass.setLinkedname(LinkedNames+","+name); }
            else { eBeanClass.setLinkedname(name); }
            AuthDB.Database.save(eBeanClass);
            if(!AuthDB.AuthDB_LinkedNames.containsKey((player.getName()))) {
                AuthDB.AuthDB_LinkedNames.put(player.getName(),name);
            }
            if(AuthDB.AuthDB_LinkedNameCheck.containsKey(player.getName())) {
                AuthDB.AuthDB_LinkedNameCheck.remove(player.getName());
            }
            if(Config.link_rename) { player.setDisplayName(name); }
            Login(player);
            return true;
        }
        return false;
    }
    
    public static boolean Unlink(Player player, String name) {
        if(AuthDB.isAuthorized(player) && Config.unlink_enabled) {
            if(!AuthDB.AuthDB_LinkedNames.containsKey((player.getName()))) {
                AuthDB.AuthDB_LinkedNames.remove(player.getName());
            }
            if(!AuthDB.AuthDB_LinkedNameCheck.containsKey(player.getName())) {
                AuthDB.AuthDB_LinkedNameCheck.put(player.getName(),"yes");
            }
            if(Config.unlink_rename) { player.setDisplayName(player.getName()); }
            Logout(player);
            return true;
        }
        return false;
    }
}
