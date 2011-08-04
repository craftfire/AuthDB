/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.plugins;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.ChatTextBox;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.authdb.util.Config;

public class ZSpout {
    public static boolean popGUI(Player player) {
        if(Config.hasSpout) {
            SpoutPlayer spoutPlayer = (SpoutPlayer)player;
            if(spoutPlayer.isSpoutCraftEnabled() && spoutPlayer.getVersion() >=18) {
                PopupScreen popup = new GenericPopup();
                spoutPlayer.getMainScreen().attachPopupScreen(popup);
                popup.attachWidget((new ChatTextBox()).setWidth(300).setHeight(20).setX(100).setY(30));
                Button button = new GenericButton();
                button.setText("Login");
                button.setCentered(true);
                button.setWidth(200).setHeight(20).setX(100).setY(100);
                button.setHoverText("Click to login!");
                popup.attachWidget(button);
                return true;
            }
        }
        return false;
    }
}
