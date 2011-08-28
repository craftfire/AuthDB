/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.craftfire.util.managers;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkitcontrib.BukkitContrib;

import com.authdb.util.databases.EBean;

public class PlayerManager {
    PluginManager pluginManager = new PluginManager();
    LoggingManager loggingManager = new LoggingManager();
    
    public void setInventoryFromStorage(Player player) {
        ItemStack[] inv = getInventory(player);
        if (inv != null) {
            player.getInventory().setContents(inv);
        }
        inv = getArmorInventory(player);
        if (inv != null) {
            player.getInventory().setArmorContents(inv);
        }
    }
    
    public void storeInventory(Player player, ItemStack[] inventory, ItemStack[] armorinventory) throws IOException {
        StringBuffer inv = new StringBuffer();
        StringBuffer armorinv = new StringBuffer();
        for (short i = 0; i < inventory.length; i = (short)(i + 1)) {
            if (inventory[i] != null) {
                inv.append(inventory[i].getTypeId() + ":" + inventory[i].getAmount() + ":" + (inventory[i].getData() == null ? "0" : Byte.valueOf(inventory[i].getData().getData())) + ":" + inventory[i].getDurability() + ",");
            } else { inv.append("0:0:0:0,"); }
        }
        
        loggingManager.Debug("Sucessfully stored " + player.getName() + "'s inventory: " + inv);
        
        for (short i = 0; i < armorinventory.length; i = (short)(i + 1)) {
            if (armorinventory[i] != null) {
                armorinv.append(armorinventory[i].getTypeId() + ":" + armorinventory[i].getAmount() + ":" + (armorinventory[i].getData() == null ? "0" : Byte.valueOf(armorinventory[i].getData().getData())) + ":" + armorinventory[i].getDurability() + ",");
            } else { armorinv.append("0:0:0:0,"); }
        }
        
        loggingManager.Debug("Sucessfully stored " + player.getName() + "'s armor inventory: " + armorinv);

          EBean eBeanClass = EBean.find(player);
          eBeanClass.setInventory(inv.toString());
          eBeanClass.setEquipment(armorinv.toString());
          pluginManager.plugin.database.save(eBeanClass);
      }
    
    public ItemStack[] getInventory(Player player) {
        EBean eBeanClass = EBean.find(player);
        if (eBeanClass != null) {
            String data = eBeanClass.getInventory();
            if (data != "" && data != null) {
                String[] inv = pluginManager.util.split(data, ",");
                ItemStack[] inventory;
                if (pluginManager.config.hasBackpack) { inventory = new ItemStack[252]; }
                else { inventory = new ItemStack[36]; }
                
                for (int i=0; i<inv.length; i++) {
                    String line = inv[i];
                    String[] split = line.split(":");
                    if (split.length == 4) {
                      int type = Integer.valueOf(split[0]).intValue();
                      inventory[i] = new ItemStack(type, Integer.valueOf(split[1]).intValue());
    
                      short dur = Short.valueOf(split[3]).shortValue();
                      if (dur > 0) {
                          inventory[i].setDurability(dur);
                      }
                      byte dd;
                      if (split[2].length() == 0) {
                        dd = 0;
                      } else {
                        dd = Byte.valueOf(split[2]).byteValue();
                      }
                      Material mat = Material.getMaterial(type);
                      if (mat == null) {
                          inventory[i].setData(new MaterialData(type, dd));
                      } else {
                          inventory[i].setData(mat.getNewData(dd));
                      }
                    }
                  }
                eBeanClass.setInventory(null);
                pluginManager.plugin.database.save(eBeanClass);
                return inventory;
            }
        }
        return null;
      }
    
    public ItemStack[] getArmorInventory(Player player) {
        EBean eBeanClass = EBean.find(player);
        if (eBeanClass != null) {
            String data = eBeanClass.getEquipment();
            if (data != "" && data != null) {
                String[] inv = pluginManager.util.split(data, ", ");
                ItemStack[] inventory = new ItemStack[4];
                for (int i=0; i<inv.length; i++) {
                    String line = inv[i];
                    String[] split = line.split(":");
                    if (split.length == 4) {
                      int type = Integer.valueOf(split[0]).intValue();
                      inventory[i] = new ItemStack(type, Integer.valueOf(split[1]).intValue());
                      short dur = Short.valueOf(split[3]).shortValue();
                      if (dur > 0) {
                          inventory[i].setDurability(dur);
                      }
                      byte dd;
                      if (split[2].length() == 0) {
                        dd = 0;
                      } else {
                        dd = Byte.valueOf(split[2]).byteValue();
                      }
                      Material mat = Material.getMaterial(type);
                      if (mat == null) {
                          inventory[i].setData(new MaterialData(type, dd));
                      } else {
                          inventory[i].setData(mat.getNewData(dd));
                      }
                    }
                  }
                eBeanClass.setEquipment(null);
                pluginManager.plugin.database.save(eBeanClass);
                return inventory;
            }
        }
        return null;
      }
    
    public void clearArmorinventory(Player player) {
        final PlayerInventory i = player.getInventory();
        i.setHelmet(null);
        i.setChestplate(null);
        i.setLeggings(null);
        i.setBoots(null);
    }

    public void renamePlayer(Player player, String name) {
        if(pluginManager.config.hasBukkitContrib) {
            BukkitContrib.getAppearanceManager().setGlobalTitle(player, name);
        }
        player.setDisplayName(name);
    }

    public String getIP(Player player) {
        return player.getAddress().getAddress().toString().substring(1);
    }
}
