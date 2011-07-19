package com.craftfire.util.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class PlayerManager {

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
        return player.getAddress().getAddress().toString().substring(1);
    }
}
