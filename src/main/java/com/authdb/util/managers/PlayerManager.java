package com.authdb.util.managers;

import org.bukkit.entity.Player;

public class PlayerManager {

    public void ClearArmorinventory(Player player) {
        
    }
    
    public static void RenamePlayer(Player player, String name) {
        player.setDisplayName(name);
    }
    
    public static String GetIP(Player player) {
        return player.getAddress().getAddress().toString().substring(1);
    }
}
