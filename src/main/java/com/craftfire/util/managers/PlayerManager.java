/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.craftfire.util.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkitcontrib.BukkitContrib;

public class PlayerManager {
    PluginManager pluginManager = new PluginManager();
    
    public void setInventoryFromStorage(Player player) {
        ItemStack[] inv = pluginManager.plugin.getInventory(player);
        if (inv != null) {
            player.getInventory().setContents(inv);
        }
        inv = pluginManager.plugin.getArmorInventory(player);
        if (inv != null) {
            player.getInventory().setArmorContents(inv);
        }
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
