/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.util;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.authdb.AuthDB;
import com.authdb.plugins.zCraftIRC;

public class Messages
{
static AuthDB plugin = new AuthDB();
///////////////////////////////////////////
//  messages
///////////////////////////////////////////

    ///////////////////////////////////////////
    //               AuthDB
    ///////////////////////////////////////////
        ///////////////////////////////////////////
        //               database
        ///////////////////////////////////////////
        public static String AuthDB_message_database_failure;

        ///////////////////////////////////////////
        //               welcome
        ///////////////////////////////////////////
        public static String AuthDB_message_welcome_guest;

        ///////////////////////////////////////////
        //               guest
        ///////////////////////////////////////////
        public static String AuthDB_message_guest_notauthorized;

        ///////////////////////////////////////////
        //               register
        ///////////////////////////////////////////
        public static String AuthDB_message_register_success,AuthDB_message_register_failure,AuthDB_message_register_registered,AuthDB_message_register_disabled,AuthDB_message_register_usage;

        ///////////////////////////////////////////
        //               unregister
        ///////////////////////////////////////////
        public static String AuthDB_message_unregister_success,AuthDB_message_unregister_failure,AuthDB_message_unregister_usage;

        ///////////////////////////////////////////
        //               login
        ///////////////////////////////////////////
        public static String AuthDB_message_login_normal,AuthDB_message_login_prompt,AuthDB_message_login_success,AuthDB_message_login_failure,AuthDB_message_login_authorized,AuthDB_message_login_notregistered,AuthDB_message_login_usage;

        ///////////////////////////////////////////
        //               link
        ///////////////////////////////////////////
        public static String AuthDB_message_link_success,AuthDB_message_link_failure,AuthDB_message_link_exists,AuthDB_message_link_usage;

        ///////////////////////////////////////////
        //               unlink
        ///////////////////////////////////////////
        public static String AuthDB_message_unlink_success,AuthDB_message_unlink_failure,AuthDB_message_unlink_nonexist,AuthDB_message_unlink_usage;

        ///////////////////////////////////////////
        //               email
        ///////////////////////////////////////////
        public static String AuthDB_message_email_required,AuthDB_message_email_invalid;

        ///////////////////////////////////////////
        //               filter
        ///////////////////////////////////////////
        public static String AuthDB_message_filter_renamed,AuthDB_message_filter_username,AuthDB_message_filter_password,AuthDB_message_filter_whitelist;

        ///////////////////////////////////////////
        //               username
        ///////////////////////////////////////////
        public static String AuthDB_message_username_minimum,AuthDB_message_username_maximum;

        ///////////////////////////////////////////
        //               password
        ///////////////////////////////////////////
        public static String AuthDB_message_password_minimum,AuthDB_message_password_maximum,AuthDB_message_password_success,AuthDB_message_password_failure,AuthDB_message_password_notregistered,AuthDB_message_password_usage;

        ///////////////////////////////////////////
        //               session
        ///////////////////////////////////////////
        public static String AuthDB_message_session_valid,AuthDB_message_session_protected;

        ///////////////////////////////////////////
        //               idle
        ///////////////////////////////////////////
        public static String AuthDB_message_idle_kick,AuthDB_message_idle_whitelist;

    ///////////////////////////////////////////
    //               CraftIRC
    ///////////////////////////////////////////

        ///////////////////////////////////////////
        //               status
        ///////////////////////////////////////////
        public static String CraftIRC_message_status_join,CraftIRC_message_status_quit;

        ///////////////////////////////////////////
        //               register
        ///////////////////////////////////////////
        public static String CraftIRC_message_register_success,CraftIRC_message_register_failure,CraftIRC_message_register_registered;

        ///////////////////////////////////////////
        //               password
        ///////////////////////////////////////////
        public static String CraftIRC_message_password_success,CraftIRC_message_password_failure;

        ///////////////////////////////////////////
        //               idle
        ///////////////////////////////////////////
        public static String CraftIRC_message_idle_kicked,CraftIRC_message_idle_whitelist;

        ///////////////////////////////////////////
        //               filter
        ///////////////////////////////////////////
        public static String CraftIRC_message_filter_renamed,CraftIRC_message_filter_kicked,CraftIRC_message_filter_whitelist;

    public enum Message {
        database_failure (AuthDB_message_database_failure),
        welcome_guest (AuthDB_message_welcome_guest),
        guest_notauthorized (AuthDB_message_guest_notauthorized),
        register_success (AuthDB_message_register_success),
        register_failure (AuthDB_message_register_failure),
        register_registered (AuthDB_message_register_registered),
        register_disabled (AuthDB_message_register_disabled),
        register_usage (AuthDB_message_register_usage),
        unregister_success (AuthDB_message_unregister_success),
        unregister_failure (AuthDB_message_unregister_failure),
        unregister_usage (AuthDB_message_unregister_usage),
        login_normal (AuthDB_message_login_normal),
        login_prompt (AuthDB_message_login_prompt),
        login_success (AuthDB_message_login_success),
        login_failure (AuthDB_message_login_failure),
        login_authorized (AuthDB_message_login_authorized),
        login_notregistered (AuthDB_message_login_notregistered),
        login_usage (AuthDB_message_login_usage),
        link_success (AuthDB_message_link_success),
        link_failure (AuthDB_message_link_failure),
        link_exists (AuthDB_message_link_exists),
        link_usage (AuthDB_message_link_usage),
        unlink_success (AuthDB_message_unlink_success),
        unlink_failure (AuthDB_message_unlink_failure),
        unlink_nonexist (AuthDB_message_unlink_nonexist),
        unlink_usage (AuthDB_message_unlink_usage),
        email_required (AuthDB_message_email_required),
        email_invalid (AuthDB_message_email_invalid),
        filter_renamed (AuthDB_message_filter_renamed),
        filter_username (AuthDB_message_filter_username),
        filter_password (AuthDB_message_filter_password),
        filter_whitelist (AuthDB_message_filter_whitelist),
        username_minimum (AuthDB_message_username_minimum),
        username_maximum (AuthDB_message_username_maximum),
        password_minimum (AuthDB_message_password_minimum),
        password_maximum (AuthDB_message_password_maximum),
        password_success (AuthDB_message_password_success),
        password_failure (AuthDB_message_password_failure),
        password_notregistered (AuthDB_message_password_notregistered),
        password_usage (AuthDB_message_password_usage),
        session_valid (AuthDB_message_session_valid),
        session_protected (AuthDB_message_session_protected),
        idle_kick (AuthDB_message_idle_kick),
        idle_whitelist (AuthDB_message_idle_whitelist),
        left_server ("fake"),
        kickPlayerIdleLoginMessage ("fake"),
        OnEnable ("fake"),
        OnDisable ("fake");
        private String text;

        Message(String text) {
            this.text = text;
        }
    }

    public static void SendMessage(final Message type,final Player player,PlayerLoginEvent event) {
        zCraftIRC.SendMessage(type,player);
        if(type.equals(Message.database_failure)) {
            AuthDB.Server.broadcastMessage(Util.replaceStrings(AuthDB_message_database_failure,null,null));
        }
        else if(Config.database_ison) {
            if(type.equals(Message.welcome_guest)) {
                if(Config.register_force) {
                    Util.SpamText(player, Message.welcome_guest.text, Config.register_delay, Config.register_show);
                }
                else {
                    player.sendMessage(Util.replaceStrings(AuthDB_message_welcome_guest,player,null));
                }
            }
            else if(type.equals(Message.guest_notauthorized)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_guest_notauthorized,player,null));
            }
            else if(type.equals(Message.register_success)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_register_success,player,null));
            }
            else if(type.equals(Message.register_failure)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_register_failure,player,null));
            }
            else if(type.equals(Message.register_registered)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_register_registered,player,null));
            }
            else if(type.equals(Message.register_disabled)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_register_disabled,player,null));
            }
            else if(type.equals(Message.register_usage)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_register_usage,player,null));
            }
            else if(type.equals(Message.unregister_success)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_unregister_success,player,null));
            }
            else if(type.equals(Message.unregister_failure)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_unregister_failure,player,null));
            }
            else if(type.equals(Message.unregister_usage)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_unregister_usage,player,null));
            }
            else if(type.equals(Message.login_normal)) {
                Util.SpamText(player, Message.login_normal.text, Config.login_delay, Config.login_show);
            }
            else if(type.equals(Message.login_prompt)) {
                Util.SpamText(player, Message.login_prompt.text, Config.login_delay, Config.login_show);
            }
            else if(type.equals(Message.login_success)) {
                /*//BukkitContrib
                Player[] online = Bukkit.getServer().getOnlinePlayers();
                final Player playerz = player;
                final ContribPlayer cplayer = (ContribPlayer)player;
                final AppearanceManager Manager = BukkitContrib.getAppearanceManager();
                final String URLBefore = Manager.getSkinUrl(cplayer, player);
                Util.Logging.Info( "URL Before: "+URLBefore);
                 AuthDB.Server.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                     @Override public void run()
                 {
                     Util.Logging.Info( "RUNNING!");
                     final String URLAfter = Manager.getSkinUrl(cplayer, playerz);
                     Util.Logging.Info( "URL After: "+URLAfter);
                    // Util.Logging.Info( "URL NAO: "+URLAfter);
                     Manager.resetGlobalSkin(playerz);
                     Manager.setGlobalSkin(playerz, URLAfter);
                    // Manager.resetAllSkins();
                 } }, 100);
                /*for (final Player players: online) {
                    final Player playerz = player;
                    final ContribPlayer cplayer = (ContribPlayer)players;
                    final String URL2 = Manager.getSkinUrl(cplayer, players);
                    Util.Logging.Info( "URL2 "+URL2);
                    //Manager.resetAllCloaks() ;
                    //plugin.UpdateSkin();
                    final String URL = Manager.getSkinUrl(cplayer, players);
                    Util.Logging.Info( "URL1: "+URL);
                     AuthDB.Server.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                         @Override public void run()
                     {
                         Util.Logging.Info( "RUNNING!");
                         Manager.setGlobalSkin(players, URL);
                         final String URL2 = Manager.getSkinUrl(cplayer, players);
                         Util.Logging.Info( "URLLLL: "+URL);
                        // Manager.resetAllSkins();
                     } }, 100);
                     */
                    ///Manager.setGlobalSkin(players, URL);
                    //Manager.setGlobalSkin(player, URL);
            //    }
                //end

                AuthDB.AuthDB_PasswordTries.put(player.getName(), "0");
                player.sendMessage(Util.replaceStrings(AuthDB_message_login_success,player,null));
            }
            else if(type.equals(Message.login_failure)) {
                String temp = AuthDB.AuthDB_PasswordTries.get(player.getName());
                int tries = Integer.parseInt(temp) + 1;
                  if(tries > Integer.parseInt(Config.login_tries) && Config.login_action.equals("kick"))
                  {
                      player.kickPlayer(Util.replaceStrings(AuthDB_message_login_failure,player,null));
                      AuthDB.AuthDB_PasswordTries.put(player.getName(),"0");
                  }
                  else
                  {
                      AuthDB.AuthDB_PasswordTries.put(player.getName(),""+tries);
                      player.sendMessage(Util.replaceStrings(AuthDB_message_login_failure,player,null));
                  }
            }
            else if(type.equals(Message.login_authorized)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_login_authorized,player,null));
            }
            else if(type.equals(Message.login_notregistered)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_login_notregistered,player,null));
            }
            else if(type.equals(Message.login_usage)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_login_usage,player,null));
            }
            else if(type.equals(Message.link_success)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_link_success,player,null));
            }
            else if(type.equals(Message.link_failure)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_link_failure,player,null));
            }
            else if(type.equals(Message.link_exists)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_link_exists,player,null));
            }
            else if(type.equals(Message.link_usage)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_link_usage,player,null));
            }
            else if(type.equals(Message.unlink_success)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_unlink_success,player,null));
            }
            else if(type.equals(Message.unlink_failure)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_unlink_failure,player,null));
            }
            else if(type.equals(Message.unlink_nonexist)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_unlink_nonexist,player,null));
            }
            else if(type.equals(Message.unlink_usage)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_unlink_usage,player,null));
            }
            else if(type.equals(Message.email_required)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_email_required,player,null));
            }
            else if(type.equals(Message.email_invalid)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_email_invalid,player,null));
            }
            else if(type.equals(Message.filter_renamed)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_filter_renamed,player,null));
            }
            else if(type.equals(Message.filter_username)) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Util.replaceStrings(AuthDB_message_filter_username,player,null));
            }
            else if(type.equals(Message.filter_password)) {
                if(Config.filter_action.equals("kick")) {
                    player.kickPlayer(Util.replaceStrings(AuthDB_message_filter_password,player,null));
                }
                else {
                    player.sendMessage(Util.replaceStrings(AuthDB_message_filter_password,player,null));
                }
            }
            else if(type.equals(Message.filter_whitelist)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_filter_whitelist,player,null));
            }
            else if(type.equals(Message.username_minimum)) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Util.replaceStrings(AuthDB_message_username_minimum,player,null));
            }
            else if(type.equals(Message.username_maximum)) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Util.replaceStrings(AuthDB_message_username_maximum,player,null));
            }
            else if(type.equals(Message.password_minimum)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_password_minimum,player,null));
            }
            else if(type.equals(Message.password_maximum)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_password_maximum,player,null));
            }
            else if(type.equals(Message.password_success)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_password_success,player,null));
            }
            else if(type.equals(Message.password_failure)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_password_failure,player,null));
            }
            else if(type.equals(Message.password_notregistered)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_password_notregistered,player,null));
            }
            else if(type.equals(Message.password_usage)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_password_usage,player,null));
            }
            else if(type.equals(Message.session_valid)) {
                player.sendMessage(Util.replaceStrings(AuthDB_message_session_valid,player,null));
            }
            else if(type.equals(Message.session_protected)) {
                event.disallow(Result.KICK_OTHER, Util.replaceStrings(AuthDB_message_session_valid,player,null));
            }
            else if(type.equals(Message.idle_kick)) {
                player.kickPlayer(Util.replaceStrings(AuthDB_message_idle_kick,player,null));
            }
            else if(type.equals(Message.idle_whitelist)) {
                //player.sendMessage(Util.replaceStrings(AuthDB_message_idle_whitelist,player,null));
            }
        }
        else { Messages.SendMessage(Message.database_failure, null, null); }
    }
}
