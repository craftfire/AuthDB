package com.craftfire.util.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class PlayerManager {

    public void ClearArmorinventory(Player player) {
        final PlayerInventory i = player.getInventory();
        i.setHelmet(null);
        i.setChestplate(null);
        i.setLeggings(null);
        i.setBoots(null);
        
    }
    
    public void RenamePlayer(Player player, String name) {
        player.setDisplayName(name);
    }
    
    public String GetIP(Player player) {
        return player.getAddress().getAddress().toString().substring(1);
    }
}
