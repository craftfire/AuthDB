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
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetType;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.authdb.AuthDB;
import com.authdb.util.Config;

public class ZSpout {
    private int width = 200;
    private int height = 20;

    public boolean checkGUI(Player player) {
        if(Config.hasSpout) {
            SpoutPlayer spoutPlayer = (SpoutPlayer)player;
            if(spoutPlayer.isSpoutCraftEnabled()) {
                popGUI(spoutPlayer);
                return true;
            }
        }
        return false;
    }

    public void popGUI(SpoutPlayer spoutPlayer) {
        PopupScreen popup = new GenericPopup();
        InGameHUD screen = spoutPlayer.getMainScreen();
        screen.attachPopupScreen(popup);
        showGUI(screen, false);

        Label pleaseLogin = new GenericLabel();
        pleaseLogin.setText("Please enter your password");
        int offset = 20;
        pleaseLogin.setHeight(height).setWidth(width).setX((screen.getWidth()-width)/2 + offset + 3).setY((screen.getHeight()-height)/2 - (offset*2));
        popup.attachWidget(AuthDB.plugin, pleaseLogin);

        Button button = new GenericButton("Login");
        button.setHeight(height).setWidth(width).setX((screen.getWidth()-width)/2).setY((screen.getHeight()-height)/2 + (offset / 2));
        popup.attachWidget(AuthDB.plugin, button);

        TextField textField = new GenericTextField();
        textField.setHeight(height).setWidth(width).setX((screen.getWidth()-width)/2).setY((screen.getHeight()-height)/2-offset);
        AuthDB.AuthDB_GUI_PasswordFieldIDs.put(spoutPlayer.getName(), textField.getId());
        AuthDB.AuthDB_GUI_ScreenIDs.put(spoutPlayer.getName(), screen.getId());
        popup.attachWidget(AuthDB.plugin, textField);
    }

    public void showGUI(InGameHUD screen, boolean show) {
        screen.getArmorBar().setVisible(show);
        screen.getArmorBar().setDirty(true);
        screen.getBubbleBar().setVisible(show);
        screen.getBubbleBar().setDirty(true);
        screen.getChatBar().setVisible(show);
        screen.getChatBar().setDirty(true);
        screen.getChatTextBox().setVisible(show);
        screen.getChatTextBox().setDirty(true);
        screen.getHealthBar().setVisible(show);
        screen.getHealthBar().setDirty(true);
    }

    public void wrongPassword(Player player, InGameHUD screen, PopupScreen popup) {
        String extra = "";
        if (Config.login_action.equalsIgnoreCase("kick")) {
            int tries = Integer.parseInt(Config.login_tries) - Integer.parseInt(AuthDB.AuthDB_PasswordTries.get(player.getName())) + 1;
            extra = " " + tries + " tries left before a kick.";
        }
        boolean set = false;
        for (Widget w : popup.getAttachedWidgets()) {
            if(w.getType().equals(WidgetType.Label)) {
                Label wrongPassword = (Label)w;
                if(wrongPassword.getText().startsWith("Wrong password")) {
                    wrongPassword.setText("Wrong password, try again." + extra);
                    wrongPassword.setDirty(true);
                    set = true;
                    break;
                }
            }
        }
        if (!set) {
            Label wrongPassword = new GenericLabel();
            wrongPassword.setText("Wrong password, try again." + extra);
            wrongPassword.setTextColor(new Color(1.0F, 0, 0, 1.0F));
            int offset = 20;
            wrongPassword.setHeight(height).setWidth(width).setX((screen.getWidth()-width)/2 - offset - 3).setY((screen.getHeight()-height)/2 + (offset*2));
            popup.attachWidget(AuthDB.plugin, wrongPassword);
        }
    }
}
