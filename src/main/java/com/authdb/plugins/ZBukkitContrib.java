/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.plugins;

import org.bukkit.entity.Player;
import org.bukkitcontrib.gui.Button;
import org.bukkitcontrib.gui.ChatTextBox;
import org.bukkitcontrib.gui.GenericButton;
import org.bukkitcontrib.gui.GenericPopup;
import org.bukkitcontrib.gui.PopupScreen;
import org.bukkitcontrib.player.ContribPlayer;

import com.authdb.util.Config;

public class ZBukkitContrib {
    public static boolean checkCommand(String command) {
        int counter = 0;
        int counter2 = 0;
        String[] contrib = command.split("\\.");
        while (contrib.length > counter) {
           try {
               Integer.parseInt(contrib[counter]);
               counter2++;
            }
            catch(Exception e) {
                counter2--;
            }
            counter++;
        }
        if (counter2 <= 4 && counter2 >= 2) {
            return true;
            }
        return false;
    }
    
    public void popGUI(Player player) {
        if(Config.hasBukkitContrib) {
            ContribPlayer contribPlayer = (ContribPlayer)player;
            if(contribPlayer.isBukkitContribEnabled()) {
                PopupScreen popup = new GenericPopup();
                contribPlayer.getMainScreen().attachPopupScreen(popup);
                //contribPlayer.getMainScreen().attachWidget((new GenericTexture("http://dl.dropbox.com/u/49805/fire-icon.png")).setX(250).setY(75).setHeight(128).setWidth(64));
                //popup.attachWidget((new GenericTexture("http://dl.dropbox.com/u/49805/fire-icon.png")).setX(50).setY(75).setHeight(64).setWidth(128));
                //popup.attachWidget((new GenericItemWidget(new ItemStack(Material.DIAMOND_SWORD))).setDepth(64).setWidth(64).setHeight(64).setX(250).setY(100));
                //popup.attachWidget((new GenericItemWidget(new ItemStack(Material.COBBLESTONE))).setDepth(64).setWidth(64).setHeight(64).setX(50).setY(100));
                popup.attachWidget((new ChatTextBox()).setWidth(300).setHeight(20).setX(100).setY(30));
                Button button = new GenericButton();
                button.setText("Login");
                button.setCentered(true);
                button.setWidth(200).setHeight(20).setX(100).setY(100);
                button.setHoverText("Click to login!");
                popup.attachWidget(button);
                //popup.attachWidget((new GenericButton()).setText("Login").setWidth(200).setHeight(20).setX(100).setY(100));
            }
        }
    }
}
