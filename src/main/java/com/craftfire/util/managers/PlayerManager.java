/*
 * This file is part of AuthDB.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
 * AuthDB is licensed under the GNU Lesser General Public License.
 *
 * AuthDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.util.managers;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

import com.craftfire.authdb.util.databases.EBean;

public class PlayerManager {
    PluginManager pluginManager = new PluginManager();
    LoggingManager loggingManager = new LoggingManager();

    public void setInventoryFromStorage(Player player) {
        ItemStack[] inv = getInventory(player);
        if (inv != null) {
            loggingManager.debug("Sucessfully restored " + player.getName() + "'s inventory: " + inv.toString());
            player.getInventory().setContents(inv);
        }
        inv = getArmorInventory(player);
        if (inv != null) {
            loggingManager.debug("Sucessfully restored " + player.getName() + "'s armor inventory: " + inv.toString());
            player.getInventory().setArmorContents(inv);
        }
    }

    public void storeInventory(Player player, ItemStack[] inventory, ItemStack[] armorinventory) throws IOException {
        StringBuffer inv = new StringBuffer();
        StringBuffer armorinv = new StringBuffer();
        for (short i = 0; i < inventory.length; i = (short)(i + 1)) {
            if (inventory[i] != null) {
                StringBuffer enchantment = new StringBuffer();
                Iterator<Entry<Enchantment, Integer>> enchantments = inventory[i].getEnchantments().entrySet().iterator();
                while (enchantments.hasNext()) {
                    Entry<Enchantment, Integer> key = enchantments.next();
                    Enchantment enc = key.getKey();
                    enchantment.append(enc.getId() + "=" + inventory[i].getEnchantmentLevel(enc) + "-");
                }
                if (enchantment.length() == 0) {
                    enchantment.append("0");
                }
                inv.append(inventory[i].getTypeId() + ":" + inventory[i].getAmount() + ":" + (inventory[i].getData() == null ? "0" : Byte.valueOf(inventory[i].getData().getData())) + ":" + inventory[i].getDurability() + ":" + enchantment + ",");
            } else {
                inv.append("0:0:0:0:0,");
            }
        }

        loggingManager.debug("Sucessfully stored " + player.getName() + "'s inventory: " + inv.toString());

        for (short i = 0; i < armorinventory.length; i = (short)(i + 1)) {
            if (armorinventory[i] != null) {
                String enchantment = "";
                Iterator<Entry<Enchantment, Integer>> enchantments = armorinventory[i].getEnchantments().entrySet().iterator();
                while (enchantments.hasNext()) {
                    Entry<Enchantment, Integer> key = enchantments.next();
                    Enchantment enc = key.getKey();
                    enchantment += enc.getId() + "=" + armorinventory[i].getEnchantmentLevel(enc) + "-";
                }
                if (enchantment.isEmpty()) {
                    enchantment = "0";
                }
                armorinv.append(armorinventory[i].getTypeId() + ":" + armorinventory[i].getAmount() + ":" + (armorinventory[i].getData() == null ? "0" : Byte.valueOf(armorinventory[i].getData().getData())) + ":" + armorinventory[i].getDurability() + ":" + enchantment + ",");
            } else {
                armorinv.append("0:0:0:0:0,");
            }
        }

        loggingManager.debug("Sucessfully stored " + player.getName() + "'s armor inventory: " + armorinv.toString());

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
                inventory = new ItemStack[36];
                for (int i=0; i<inv.length; i++) {
                    String line = inv[i];
                    String[] split = line.split(":");
                    if (split.length == 5) {
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
                        if (!split[4].equals("0")) {
                            String[] enchatments = split[4].split("-");
                            for (int a=0; a<enchatments.length; a++) {
                                String[] enchOptions = enchatments[a].split("=");
                                Enchantment ench = Enchantment.getById(Integer.parseInt(enchOptions[0]));
                                int level = Integer.parseInt(enchOptions[1]);
                                inventory[i].addUnsafeEnchantment(ench, level);
                            }
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
                String[] inv = pluginManager.util.split(data, ",");
                ItemStack[] inventory = new ItemStack[4];
                for (int i=0; i<inv.length; i++) {
                    String line = inv[i];
                    String[] split = line.split(":");
                    if (split.length == 5) {
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
                        if (!split[4].equals("0")) {
                            String[] enchatments = split[4].split("-");
                            for (int a=0; a<enchatments.length; a++) {
                                String[] enchOptions = enchatments[a].split("=");
                                Enchantment ench = Enchantment.getById(Integer.parseInt(enchOptions[0]));
                                int level = Integer.parseInt(enchOptions[1]);
                                inventory[i].addEnchantment(ench, level);
                            }
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
        player.setDisplayName(name);
    }

    public String getIP(Player player) {
        if (player.getAddress() != null) {
            return player.getAddress().getAddress().getHostAddress();
        } else {
            return "";
        }
    }
}
