/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.listeners;

import java.util.UUID;

import org.bukkit.Material;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.authdb.AuthDB;
import com.authdb.plugins.ZSpout;
import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Processes;
import com.authdb.util.Util;
import com.authdb.util.Messages.Message;

public class AuthDBScreenListener extends ScreenListener {
    private final AuthDB plugin;

    public AuthDBScreenListener(AuthDB instance) {
        plugin = instance;
    }

    public void onButtonClick(ButtonClickEvent event) {
        if(!event.getButton().isVisible())
            return;
        SpoutPlayer player = event.getPlayer();
        if(this.plugin.AuthDB_GUI_PasswordFieldIDs.containsKey(player.getName())) {
            InGameHUD screen = player.getMainScreen();
            PopupScreen popup = screen.getActivePopup();
            if(event.getButton().getText().equalsIgnoreCase("login")) {
                UUID id = this.plugin.AuthDB_GUI_PasswordFieldIDs.get(player.getName());
                Widget widget = popup.getWidget(id);
                TextField textField = (TextField)widget;
                String password = textField.getText();
                if(this.plugin.checkPassword(player.getName(), password)) {
                    for (Widget w : popup.getAttachedWidgets()) {
                        popup.removeWidget(w);
                    }
                    Processes.Login(player);
                    screen.closePopup();
                    popup.setVisible(false);
                    //Messages.sendMessage(Message.login_success, player, null);
                    this.plugin.AuthDB_GUI_PasswordFieldIDs.remove(player.getName());
                    player.sendNotification("Success!", "Logged in!", Material.GOLD_INGOT);
                } else {
                    ZSpout spout = new ZSpout();
                    String temp = AuthDB.AuthDB_PasswordTries.get(player.getName());
                    int tries = Integer.parseInt(temp) + 1;
                    if (tries > Integer.parseInt(Config.login_tries) && Config.login_action.equalsIgnoreCase("kick")) {
                        screen.closePopup();
                        AuthDB.AuthDB_PasswordTries.put(player.getName(), "0");
                        this.plugin.AuthDB_GUI_PasswordFieldIDs.remove(player.getName());
                        player.kickPlayer(Util.replaceStrings(""+Message.login_failure.text, player, null));
                    } else {
                        AuthDB.AuthDB_PasswordTries.put(player.getName(), "" + tries);
                    }
                    spout.wrongPassword(player, screen, popup);
                }
            }
        }
    }
    
    public void onScreenClose(ScreenCloseEvent event) {
        if (!AuthDB.isAuthorized(event.getPlayer())) {
            if (Util.toLoginMethod(Config.login_method).equalsIgnoreCase("prompt")) {
                event.setCancelled(true);
            }
        }
    }
    
}
