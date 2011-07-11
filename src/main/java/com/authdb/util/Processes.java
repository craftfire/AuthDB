package com.authdb.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.authdb.AuthDB;
import com.authdb.util.databases.eBean;

public class Processes 
{
    public static boolean Logout(Player player)
    {
        if(AuthDB.isAuthorized(player))
        {
            if(AuthDB.AuthTimeDB.containsKey(player.getName()))
            { 
                AuthDB.AuthTimeDB.remove(player.getName()); 
            }
                AuthDB.authorizedNames.remove(player.getName()); 
                eBean eBeanClass = eBean.find(player, eBean.Column.authorized, "true");
                if (eBeanClass != null)
                {
                    Util.Debug("IS AUTHED");
                    eBeanClass.setAuthorized("false");
                    AuthDB.Database.save(eBeanClass);
                }
            if(AuthDB.db3.containsKey(Encryption.md5(player.getName())))
            { 
                AuthDB.db3.remove(Encryption.md5(player.getName())); 
            }
            if(AuthDB.db2.containsKey(Encryption.md5(player.getName()+Util.GetIP(player))))
            { 
                AuthDB.db2.remove(Encryption.md5(player.getName()+Util.GetIP(player))); 
            }
            return true;
        }
        return false;
    }
    
    public static boolean Login(Player player)
    {
        if(!AuthDB.isAuthorized(player))
        {
            long timestamp = Util.TimeStamp();
            if(!AuthDB.AuthTimeDB.containsKey(player.getName()))
            { 
                AuthDB.AuthTimeDB.put(player.getName(), ""+timestamp);
            }
            AuthDB.authorizedNames.add(player.getName());
            eBean eBeanClass = eBean.find(player);
            if (eBeanClass == null)
            {
                eBeanClass = new eBean();
                eBeanClass.setPlayer(player);
                eBeanClass.setAuthorized("true");
            }
            AuthDB.Database.save(eBeanClass);
            if(!AuthDB.db3.containsKey(Encryption.md5(player.getName())))
            { 
                AuthDB.db3.put(Encryption.md5(player.getName()), "yes");
            }
            if(AuthDB.db2.containsKey(Encryption.md5(player.getName()+Util.GetIP(player))))
            { 
                AuthDB.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
            }
            ItemStack[] inv = AuthDB.getInventory(player);
            if (inv != null) { player.getInventory().setContents(inv); }
            return true;
        }
        return false;
    }
    
    public boolean Link(Player player, String name)
    {
        if(AuthDB.isAuthorized(player))
        {
            ItemStack[] inv = AuthDB.getInventory(player);
            if (inv != null) { player.getInventory().setContents(inv); }
            long timestamp = Util.TimeStamp();
            if(!AuthDB.AuthTimeDB.containsKey(player.getName()))
            { 
                AuthDB.AuthTimeDB.put(player.getName(), ""+timestamp);
            }
            AuthDB.authorizedNames.add(player.getName());
            eBean eBeanClass = eBean.find(player);
            if (eBeanClass == null)
            {
                eBeanClass = new eBean();
                eBeanClass.setPlayer(player);
                eBeanClass.setAuthorized("true");
                eBeanClass.setOthername(name);
            }
            if(!AuthDB.db2.containsKey(Encryption.md5(player.getName()+Util.GetIP(player))))
            { 
                AuthDB.db2.put(Encryption.md5(player.getName()+Util.GetIP(player)), ""+timestamp);
            }
            if(!AuthDB.db3.containsKey(Encryption.md5(player.getName())))
            { 
                AuthDB.db3.put(Encryption.md5(player.getName()), "yes");
            }
            if(!AuthDB.AuthOtherNamesDB.containsKey(Encryption.md5((player.getName()))))
            { 
                AuthDB.AuthOtherNamesDB.put(player.getName(),name);
            }
            if(Config.link_rename) { player.setDisplayName(name); }
            return true;
        }
        return false;
    }
}
