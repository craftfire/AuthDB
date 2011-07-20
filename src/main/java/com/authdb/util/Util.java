/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;


import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.authdb.AuthDB;
import com.authdb.scripts.Custom;
import com.authdb.scripts.cms.DLE;
import com.authdb.scripts.cms.Drupal;
import com.authdb.scripts.cms.Joomla;
import com.authdb.scripts.forum.BBPress;
import com.authdb.scripts.forum.IPB;
import com.authdb.scripts.forum.MyBB;
import com.authdb.scripts.forum.PhpBB;
import com.authdb.scripts.forum.PunBB;
import com.authdb.scripts.forum.SMF;
import com.authdb.scripts.forum.XenForo;
import com.authdb.scripts.forum.Vanilla;
import com.authdb.scripts.forum.VBulletin;
import com.authdb.util.Messages.Message;
import com.authdb.util.databases.MySQL;
import com.authdb.util.databases.EBean;
import com.authdb.util.databases.EBean.Column;
import com.craftfire.util.managers.LoggingManager;
import com.craftfire.util.managers.PlayerManager;

import com.mysql.jdbc.Blob;

public class Util {
    public static LoggingManager logging = new LoggingManager();
    public static PlayerManager authDBPlayer = new PlayerManager();
    public static com.craftfire.util.managers.PlayerManager craftFirePlayer = new com.craftfire.util.managers.PlayerManager();
    static int schedule = 1;
    public static boolean checkScript(String type, String script, String player, String password,
    String email, String ipAddress) throws SQLException {
        if (Config.database_ison) {
            String usertable = null, usernamefield = null, passwordfield = null, saltfield = "";
            boolean bans = false;
            PreparedStatement ps = null;
            int number = 0;
            if (Config.custom_enabled) {
                if (type.equals("checkuser")) {
                    String check = MySQL.getfromtable(Config.custom_table, "*", Config.custom_userfield, player);
                    if (check != "fail") {
                        Config.hasForumBoard = true;
                        return true;
                    }
                    return false;
                } else if (type.equals("checkpassword")) {
                    EBean eBeanClass = EBean.find(player);
                    String storedPassword = eBeanClass.getPassword();
                    if (Custom.check_hash(password, storedPassword)) { return true; }
                    String hash = MySQL.getfromtable(Config.custom_table, "`" + Config.custom_passfield + "`", "" + Config.custom_userfield + "", player);
                    EBean.checkPassword(player, hash);
                    if (Custom.check_hash(password, hash)) { return true; }
                    return false;
                } else if (type.equals("syncpassword")) {
                    String hash = MySQL.getfromtable(Config.custom_table, "`" + Config.custom_passfield + "`", "" + Config.custom_userfield + "", player);
                    EBean.checkPassword(player, hash);
                    return true;
                } else if (type.equals("adduser")) {
                    Custom.adduser(player, email, password, ipAddress);
                } else if (type.equals("numusers")) {
                    ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `" + Config.custom_table + "`");
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) { logging.Info(rs.getInt("countit") + " user registrations in database"); }
                }
            } else if (script.equals(PhpBB.Name) || script.equals(PhpBB.ShortName)) {
                usertable = "users";
                //bantable = "banlist";
                if (checkVersionInRange(PhpBB.VersionRange)) {
                    usernamefield = "username_clean";
                    passwordfield = "user_password";
                    /*useridfield = "user_id";
                    banipfield = "ban_ip";
                    bannamefield = "ban_userid";
                    banreasonfield = "";*/
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && PhpBB.check_hash(password, storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "",
                        "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (PhpBB.check_hash(password, hash)) { return true; }
                    }
                    /*else if (type.equals("checkban")) {
                        String check = "fail";
                        if (ipAddress != null) {
                            String userid = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + useridfield + "`", "" + usernamefield + "", player);
                              check = MySQL.getfromtable(Config.script_tableprefix + "" + bantable + "", "`" + banipfield + "`", "" + bannamefield + "", userid);
                        }
                        else {
                            check = MySQL.getfromtable(Config.script_tableprefix + "" + bantable + "", "`" + banipfield + "`", "" + banipfield + "", ipAddress);
                        }
                        if (check != "fail") { return true; }
                          else { return false; }
                    } */
                } else if (checkVersionInRange(PhpBB.VersionRange2)) {
                    usernamefield = "username";
                    passwordfield = "user_password";
                    Config.hasForumBoard = true;
                    bans = true;
                    number = 2;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && PhpBB.check_hash(password, storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "",
                        "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (PhpBB.check_hash(password, hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                     PhpBB.adduser(number, player, email, password, ipAddress);
                     return true;
                }
            } else if (script.equals(SMF.Name) || script.equals(SMF.ShortName)) {
                usertable = "members";
                if (checkVersionInRange(SMF.VersionRange)) {
                    usernamefield = "memberName";
                    passwordfield = "passwd";
                    saltfield = "passwordSalt";
                    Config.hasForumBoard = true;
                    bans = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && SMF.check_hash(SMF.hash(1, player, password), storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (SMF.check_hash(SMF.hash(1, player, password), hash)) { return true; }
                    }
                } else if (checkVersionInRange(SMF.VersionRange2) || checkVersionInRange("2.0")
                || checkVersionInRange("2.0.0") || checkVersionInRange("2.0.0.0")) {
                    usernamefield = "member_name";
                    passwordfield = "passwd";
                    Config.hasForumBoard = true;
                    bans = true;
                    number = 2;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && SMF.check_hash(SMF.hash(2, player, password), storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (SMF.check_hash(SMF.hash(2, player, password), hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                     SMF.adduser(number, player, email, password, ipAddress);
                     return true;
                }
            } else if (script.equals(MyBB.Name) || script.equals(MyBB.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(MyBB.VersionRange)) {
                    saltfield = "salt";
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    bans = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && MyBB.check_hash(MyBB.hash("find", player, password, ""), storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (MyBB.check_hash(MyBB.hash("find", player, password, ""), hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                     MyBB.adduser(number, player, email, password, ipAddress);
                     return true;
                }
            } else if (script.equals(VBulletin.Name) || script.equals(VBulletin.ShortName)) {
                usertable = "user";
                if (checkVersionInRange(VBulletin.VersionRange)) {
                    saltfield = "salt";
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    bans = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && VBulletin.check_hash(VBulletin.hash("find", player, password, ""), storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (VBulletin.check_hash(VBulletin.hash("find", player, password, ""), hash)) { return true; }
                    }
                } else if (checkVersionInRange(VBulletin.VersionRange2)) {
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    bans = true;
                    number = 2;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && VBulletin.check_hash(VBulletin.hash("find", player, password, ""), storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (VBulletin.check_hash(VBulletin.hash("find", player, password, ""), hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                     VBulletin.adduser(number, player, email, password, ipAddress);
                     return true;
                }
            } else if (script.equals(Drupal.Name) || script.equals(Drupal.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(Drupal.VersionRange)) {
                    usernamefield = "name";
                    passwordfield = "pass";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && Encryption.md5(password).equals(storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (Encryption.md5(password).equals(hash)) { return true; }
                    }
                } else if (checkVersionInRange(Drupal.VersionRange2)) {
                    usernamefield = "name";
                    passwordfield = "pass";
                    Config.hasForumBoard = true;
                    number = 2;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && storedPassword.equals(Drupal.user_check_password(password, storedPassword))) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (hash.equals(Drupal.user_check_password(password, hash))) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                     Drupal.adduser(number, player, email, password, ipAddress);
                     return true;
                }
            } else if (script.equals(Joomla.Name) || script.equals(Joomla.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(Joomla.VersionRange)) {
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && Joomla.check_hash(password, storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (Joomla.check_hash(password, hash)) { return true; }
                    }
                } else if (checkVersionInRange(Joomla.VersionRange2)) {
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    number = 2;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && Joomla.check_hash(password, storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (Joomla.check_hash(password, hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                     Joomla.adduser(number, player, email, password, ipAddress);
                     return true;
                }
            } else if (script.equals(Vanilla.Name) || script.equals(Vanilla.ShortName)) {
                if (checkVersionInRange(Vanilla.VersionRange)) {
                    usertable = "User";
                    usernamefield = "Name";
                    passwordfield = "Password";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && Vanilla.check_hash(password, storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (Vanilla.check_hash(password, hash)) { return true; }
                    }
                } else if (checkVersionInRange(Vanilla.VersionRange2)) {
                    usertable = "user";
                    usernamefield = "Name";
                    passwordfield = "Password";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        if (Vanilla.check_hash(password, hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                    String emailcheck =  MySQL.getfromtable(Config.script_tableprefix + usertable, "`Email`", "Email", email);
                    if (emailcheck.equals("fail")) {
                        Vanilla.adduser(number, player, email, password, ipAddress);
                        return true;
                    }
                    return false;
                }
            } else if (script.equals(PunBB.Name) || script.equals(PunBB.ShortName)) {
                usertable = "users";
                //bantable = "bans";
                if (checkVersionInRange(PunBB.VersionRange)) {
                //    bannamefield = "username";
                    saltfield = "salt";
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && PunBB.check_hash(PunBB.hash("find", player, password, ""), storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (PunBB.check_hash(PunBB.hash("find", player, password, ""), hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                    PunBB.adduser(number, player, email, password, ipAddress);
                    return true;
                }
            } else if (script.equals(XenForo.Name) || script.equals(XenForo.ShortName)) {
                usertable = "user";
                if (checkVersionInRange(XenForo.VersionRange)) {
                    String userid = MySQL.getfromtable(Config.script_tableprefix + usertable, "`user_id`", "username", player);
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        Blob hash = MySQL.getfromtableBlob(Config.script_tableprefix + "user_authenticate", "`data`", "user_id", userid);
                        int offset = -1;
                        int chunkSize = 1024;
                        long blobLength = hash.length();
                        if (chunkSize > blobLength) {
                        chunkSize = (int) blobLength;
                        }
                        char buffer[] = new char[chunkSize];
                        StringBuilder stringBuffer = new StringBuilder();
                        Reader reader = new InputStreamReader(hash.getBinaryStream());

                        try {
                            while ((offset = reader.read(buffer)) != -1) {
                            stringBuffer.append(buffer, 0, offset);
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                        }
                        String cache = stringBuffer.toString();
                        String thehash = forumCacheValue(cache, "hash");
                        String thesalt = forumCacheValue(cache, "salt");
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        String storedSalt = eBeanClass.getSalt();
                        if (storedPassword != null && storedSalt != null && XenForo.check_hash(XenForo.hash(1, storedSalt, password), storedPassword)) { return true; }
                        EBean.checkSalt(player, thesalt);
                        EBean.checkPassword(player, thehash);
                        if (XenForo.check_hash(XenForo.hash(1, thesalt, password), thehash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                    XenForo.adduser(number, player, email, password, ipAddress);
                    return true;
                } else if (Config.hasForumBoard && type.equals("syncpassword") && !Config.custom_enabled) {
                    String userid = MySQL.getfromtable(Config.script_tableprefix + usertable, "`user_id`", "username", player);
                    Blob hash = MySQL.getfromtableBlob(Config.script_tableprefix + "user_authenticate", "`data`", "user_id", userid);
                    int offset = -1;
                    int chunkSize = 1024;
                    long blobLength = hash.length();
                    if (chunkSize > blobLength) {
                    chunkSize = (int) blobLength;
                    }
                    char buffer[] = new char[chunkSize];
                    StringBuilder stringBuffer = new StringBuilder();
                    Reader reader = new InputStreamReader(hash.getBinaryStream());

                    try {
                        while ((offset = reader.read(buffer)) != -1) {
                        stringBuffer.append(buffer, 0, offset);
                        }
                    } catch (IOException e) {
                        logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                    }
                    String cache = stringBuffer.toString();
                    String thehash = forumCacheValue(cache, "hash");
                    EBean.checkPassword(player, thehash);
                    return true;
                } else if (Config.hasForumBoard && type.equals("syncsalt") && !Config.custom_enabled && saltfield != null && saltfield != "") {
                    String userid = MySQL.getfromtable(Config.script_tableprefix + usertable, "`user_id`", "username", player);
                    Blob hash = MySQL.getfromtableBlob(Config.script_tableprefix + "user_authenticate", "`data`", "user_id", userid);
                    int offset = -1;
                    int chunkSize = 1024;
                    long blobLength = hash.length();
                    if (chunkSize > blobLength) {
                    chunkSize = (int) blobLength;
                    }
                    char buffer[] = new char[chunkSize];
                    StringBuilder stringBuffer = new StringBuilder();
                    Reader reader = new InputStreamReader(hash.getBinaryStream());

                    try {
                        while ((offset = reader.read(buffer)) != -1) {
                        stringBuffer.append(buffer, 0, offset);
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        logging.StackTrace(e.getStackTrace(),
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        Thread.currentThread().getStackTrace()[1].getLineNumber(),
                        Thread.currentThread().getStackTrace()[1].getClassName(),
                        Thread.currentThread().getStackTrace()[1].getFileName());
                    }
                    String cache = stringBuffer.toString();
                    String thesalt = forumCacheValue(cache, "salt");
                    EBean.checkSalt(player, thesalt);
                    return true;
                }
            } else if (script.equals(BBPress.Name) || script.equals(BBPress.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(BBPress.VersionRange)) {
                    usernamefield = "user_login";
                    passwordfield = "user_pass";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && BBPress.check_hash(password, storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "",
                        "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (BBPress.check_hash(password, hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                    BBPress.adduser(number, player, email, password, ipAddress);
                    return true;
                }
            } else if (script.equals(DLE.Name) || script.equals(DLE.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(DLE.VersionRange)) {
                    usernamefield = "name";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && DLE.check_hash(DLE.hash(password), storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "",
                        "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (DLE.check_hash(DLE.hash(password), hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                    DLE.adduser(number, player, email, password, ipAddress);
                    return true;
                }
            } else if (script.equals(IPB.Name) || script.equals(IPB.ShortName)) {
                usertable = "members";
                if (checkVersionInRange(IPB.VersionRange)) {
                    saltfield = "members_pass_salt";
                    usernamefield = "members_l_username";
                    passwordfield = "members_pass_hash";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        player = player.toLowerCase();
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && IPB.check_hash(IPB.hash("find", player, password, null), storedPassword)) { return true; }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "",
                        passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (IPB.check_hash(IPB.hash("find", player, password, null), hash)) { return true; }
                    }

                }
                if (type.equals("adduser")) {
                    player = player.toLowerCase();
                    IPB.adduser(number, player, email, password, ipAddress);
                     return true;
                }
            }
          /*  else if (script.equals(Config.Script11_name) || script.equals(Config.Script11_shortname)) {
                usertable = "users";
                if (checkVersionInRange(Config.Script11_versionrange)) {
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equals("checkpassword")) {
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        if (XE.check_hash(password, hash)) { return true; }
                    }
                }
                if (type.equals("adduser")) {
                     XE.adduser(number, player, email, password, ipAddress);
                     return true;
                }
            } */
            if (!Config.hasForumBoard) {
                if (!Config.custom_enabled) {
                    String tempVers = Config.script_version;
                    Config.script_version = scriptVersion();
                    logging.Info("\n" +
                            "|-----------------------------------------------------------------------------|\n" +
                            "|--------------------------------AUTHDB WARNING-------------------------------|\n" +
                            "|-----------------------------------------------------------------------------|\n" +
                            "| COULD NOT FIND A COMPATIBLE SCRIPT VERSION,                                 |\n" +
                            "| PLEASE CHECK YOUR SCRIPT VERSION AND TRY AGAIN.PLUGIN MAY OR MAY NOT WORK.  |\n" +
                            "| YOUR SCRIPT VERSION FOR " + Config.script_name + " HAVE BEEN SET FROM " + tempVers + " TO " + Config.script_version + "             |\n" +
                            "| FOR A LIST OF SCRIPT VERSIONS,                                              |\n" +
                            "| CHECK: http://wiki.bukkit.org/AuthDB_(Plugin)#Scripts_Supported             |\n" +
                            "|-----------------------------------------------------------------------------|");

                }
            }
            if (Config.hasForumBoard && type.equals("checkuser") && !Config.custom_enabled) {
                EBean eBeanClass = EBean.find(player, Column.registred, "true");
                if (eBeanClass != null) { return true; }
                String check = MySQL.getfromtable(Config.script_tableprefix + usertable, "*", usernamefield, player);
                if (check != "fail") { return true; }
            }
            /*else if (Config.hasForumBoard && type.equals("checkban") && !Config.custom_enabled && bantable != null) {
                String check = MySQL.getfromtable(Config.script_tableprefix + bantable, "*", bannamefield, player);
                if (check != "fail") { return true; }
            }*/ else if (Config.hasForumBoard && type.equals("numusers") && !Config.custom_enabled) {
                if (script.equals(PhpBB.Name) || script.equals(PhpBB.ShortName)) {
                ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"
                 + Config.script_tableprefix + usertable + "` WHERE  `group_id` !=6");
                } else {
                ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"
                 + Config.script_tableprefix + usertable + "`");
                }
                ResultSet rs = ps.executeQuery();
                if (rs.next()) { logging.Info(rs.getInt("countit") + " user registrations in database"); }
            } else if (Config.hasForumBoard && type.equals("syncpassword") && !Config.custom_enabled) {
                String hash = MySQL.getfromtable(Config.script_tableprefix + usertable, "`" + passwordfield + "`", usernamefield, player);
                EBean.checkPassword(player, hash);
                return true;
            } else if (Config.hasForumBoard && type.equals("syncsalt") && !Config.custom_enabled && saltfield != null && saltfield != "") {
                String salt = MySQL.getfromtable(Config.script_tableprefix + usertable, "`" + saltfield + "`", usernamefield, player);
                EBean.checkSalt(player, salt);
                return true;
            }
        }
        return false;
    }

    static String scriptVersion() {
        String script = Config.script_name;
        if (script.equals(PhpBB.Name) || script.equals(PhpBB.ShortName)) { return split(PhpBB.LatestVersionRange, "-")[1]; }
        else if (script.equals(SMF.Name) || script.equals(SMF.ShortName)) { return split(SMF.LatestVersionRange, "-")[1]; }
        else if (script.equals(MyBB.Name) || script.equals(MyBB.ShortName)) { return split(MyBB.LatestVersionRange, "-")[1]; }
        else if (script.equals(VBulletin.Name) || script.equals(VBulletin.ShortName)) { return split(VBulletin.LatestVersionRange, "-")[1]; }
        else if (script.equals(Drupal.Name) || script.equals(Drupal.ShortName)) { return split(Drupal.LatestVersionRange, "-")[1]; }
        else if (script.equals(Joomla.Name) || script.equals(Joomla.ShortName)) { return split(Joomla.LatestVersionRange, "-")[1]; }
        else if (script.equals(Vanilla.Name) || script.equals(Vanilla.ShortName)) { return split(Vanilla.LatestVersionRange, "-")[1]; }
        else if (script.equals(PunBB.Name) || script.equals(PunBB.ShortName)) { return split(PunBB.LatestVersionRange, "-")[1]; }
        else if (script.equals(XenForo.Name) || script.equals(XenForo.ShortName)) { return split(XenForo.LatestVersionRange, "-")[1]; }
        else if (script.equals(BBPress.Name) || script.equals(BBPress.ShortName)) { return split(BBPress.LatestVersionRange, "-")[1]; }
        else if (script.equals(DLE.Name) || script.equals(DLE.ShortName)) { return split(DLE.LatestVersionRange, "-")[1]; }
        else if (script.equals(IPB.Name) || script.equals(IPB.ShortName)) { return split(IPB.LatestVersionRange, "-")[1]; }
        return null;
    }

    public static String[] split(String string, String delimiter) {
        String[] split = string.split(delimiter);
        return split;
    }

    static void fillChatField(Player player, String text) {
        for (int i = 0; i < 20; i++) { player.sendMessage(""); }
        player.sendMessage(text);
    }

    static void spamText(final Player player, final String text, final int delay, final int show) {
        if (Config.login_delay > 0 && !AuthDB.AuthDB_SpamMessage.containsKey(player.getName())) {
            schedule = AuthDB.server.getScheduler().scheduleAsyncRepeatingTask(AuthDB.plugin, new Runnable() {
            @Override
            public void run() {
                if (AuthDB.isAuthorized(player) && AuthDB.AuthDB_SpamMessage.containsKey(player.getName())) {
                    AuthDB.server.getScheduler().cancelTask(AuthDB.AuthDB_SpamMessage.get(player.getName()));
                    AuthDB.AuthDB_SpamMessage.remove(player.getName());
                    AuthDB.AuthDB_SpamMessageTime.remove(player.getName());
                } else {
                    if (!AuthDB.AuthDB_SpamMessage.containsKey(player.getName())) { AuthDB.AuthDB_SpamMessage.put(player.getName(), schedule); }
                    if (!AuthDB.AuthDB_SpamMessageTime.containsKey(player.getName())) { AuthDB.AuthDB_SpamMessageTime.put(player.getName(), timeStamp()); }
                    if ((AuthDB.AuthDB_SpamMessageTime.get(player.getName()) + show) <= timeStamp()) {
                        AuthDB.server.getScheduler().cancelTask(AuthDB.AuthDB_SpamMessage.get(player.getName()));
                        AuthDB.AuthDB_SpamMessage.remove(player.getName());
                        AuthDB.AuthDB_SpamMessageTime.remove(player.getName());
                    }
                    String message = replaceStrings(text, player, null);
                    if (Config.link_rename && !checkOtherName(player.getName()).equals(player.getName())) {
                        message = message.replaceAll(player.getName(), player.getDisplayName());
                        player.sendMessage(message);
                    }
                    else {
                        player.sendMessage(message);
                    }
                    fillChatField(player, message);
                }
            } }, delay, toTicks("seconds", "1"));
        }
    }

    public static long timeStamp() {
        return System.currentTimeMillis()/1000;
    }

    boolean checkingBan(String usertable, String useridfield, String usernamefield, String username, String bantable, String banipfield, String bannamefield, String ipAddress) throws SQLException {
        String check = "fail";
        if (ipAddress == null) {
            String userid = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + useridfield + "`", "" + usernamefield + "", username);
              check = MySQL.getfromtable(Config.script_tableprefix + "" + bantable + "", "`" + banipfield + "`", "" + bannamefield + "", userid);
        }
        else {
            check = MySQL.getfromtable(Config.script_tableprefix + "" + bantable + "", "`" + banipfield + "`", "" + bannamefield + "", ipAddress);
        }
        if (check != "fail") {
            return true;
        } else {
            return false;
        }
    }

    public static String forumCache(String cache, String player, int userid, String nummember, String activemembers, String newusername, String newuserid, String extrausername) {
        StringTokenizer st = new StringTokenizer(cache, ":");
        int i = 0;
        List<String> array = new ArrayList<String>();
        while (st.hasMoreTokens()) { array.add(st.nextToken() + ":"); }
        StringBuffer newcache = new StringBuffer();
        while (array.size() > i) {
            if (array.get(i).equals("\"" + nummember + "\";s:") && nummember != null) {
                String temp = array.get(i + 2);
                temp = removeChar(temp, '"');
                temp = removeChar(temp, ':');
                temp = removeChar(temp, 's');
                temp = removeChar(temp, ';');
                temp = temp.trim();
                int tempnum = Integer.parseInt(temp) + 1;
                String templength = "" + tempnum;
                temp = "\"" + tempnum + "\"" + ";s";
                array.set(i + 1, templength.length() + ":");
                array.set(i + 2, temp + ":");
            } else if (array.get(i).equals("\"" + newusername + "\";s:") && newusername != null) {
                array.set(i + 1, player.length() + ":");
                array.set(i + 2, "\"" + player + "\"" + ";s" + ":");
            } else if (array.get(i).equals("\"" + extrausername + "\";s:") && extrausername != null) {
                array.set(i + 1, player.length() + ":");
                array.set(i + 2, "\"" + player + "\"" + ";s" + ":");
            } else if (array.get(i).equals("\"" + activemembers + "\";s:") && activemembers != null) {
                String temp = array.get(i + 2);
                temp = removeChar(temp, '"');
                temp = removeChar(temp, ':');
                temp = removeChar(temp, 's');
                temp = removeChar(temp, ';');
                temp = temp.trim();
                int tempnum = Integer.parseInt(temp) + 1;
                String templength = "" + tempnum;
                temp = "\"" + tempnum + "\"" + ";s";
                array.set(i + 1, templength.length() + ":");
                array.set(i + 2, temp + ":");
            } else if (array.get(i).equals("\"" + newuserid + "\";s:") && newuserid != null) {
                String dupe = "" + userid;
                array.set(i + 1, dupe.length() + ":");
                array.set(i + 2, "\"" + userid + "\"" + ";" + "}");
            }
            newcache.append(array.get(i));
            i++;
        }
        return newcache.toString();
    }

    public static String forumCacheValue(String cache, String value) {
        StringTokenizer st = new StringTokenizer(cache, ":");
        int i = 0;
        List<String> array = new ArrayList<String>();
        while (st.hasMoreTokens()) { array.add(st.nextToken() + ":"); }
        while (array.size() > i) {
            if (array.get(i).equals("\"" + value + "\";s:") && value != null) {
                String temp = array.get(i + 2);
                temp = removeChar(temp, '"');
                temp = removeChar(temp, ':');
                temp = removeChar(temp, 's');
                temp = removeChar(temp, ';');
                temp = temp.trim();
                return temp;
            }
            i++;
        }
        return "no";
    }

    public static boolean checkVersionInRange(String versionrange) {
        String version = Config.script_version;
        String[] versions= version.split("\\.");
        String[] versionss= versionrange.split("\\-");
        String[] versionrange1= versionss[0].split("\\.");
        String[] versionrange2= versionss[1].split("\\.");
        if (versionrange1.length == versions.length) {
            int a = Integer.parseInt(versionrange1[0]);
            int b = Integer.parseInt(versionrange2[0]);
            int c = Integer.parseInt(versions[0]);
            if (a <= c && b >= c) {
                int d = b - c;
                if (d > 0) {
                    return true;
                } else if (d == 0) {
                    int a2 = Integer.parseInt(versionrange1[1]);
                    int b2 = Integer.parseInt(versionrange2[1]);
                    int c2 = Integer.parseInt(versions[1]);
                    if (a2 <= c2 && b2 >= c2) {
                        if (versionrange1.length == 2) { return true; }
                        else if (versionrange1.length > 2) {
                            int d2 = b2 - c2;
                            if (d2 > 0) {
                                return true;
                            } else if (d2 == 0) {
                                int a3 = Integer.parseInt(versionrange1[2]);
                                int b3 = Integer.parseInt(versionrange2[2]);
                                int c3 = Integer.parseInt(versions[2]);
                                if (a3 <= c3 && b3 >= c3) {
                                    if (versionrange1.length != 4) { return true; }
                                    else if (versionrange1.length == 4) {
                                        int d3 = b3 - c3;
                                        if (d3 > 0) {
                                            return true;
                                        } else if (d3 == 0) {
                                            int a4 = Integer.parseInt(versionrange1[3]);
                                            int b4 = Integer.parseInt(versionrange2[3]);
                                            int c4 = Integer.parseInt(versions[3]);
                                            if (a4 <= c4 && b4 >= c4) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void postInfo(String b407f35cb00b96936a585c4191fc267a, String f13a437cb9b1ac68b49d597ed7c4bfde, String cafd6e81e3a478a7fe0b40e7502bf1f, String fcf2204d0935f0a8ef1853662b91834e, String aa25d685b171d7874222c7080845932, String fac8b1115d09f0d816a0671d144d49e, String e98695d728198605323bb829d6ea4de, String d89570db744fe029ca696f09d34e1, String fe75a95090e70155856937ae8d0482, String a6118cfc6befa19cada1cddc32d36a3, String d440b827e9c17bbd51f2b9ac5c97d6, String c284debb7991b2b5fcfd08e9ab1e5, int d146298d6d3e1294bbe4121f26f02800) throws IOException {
        String d68d8f3c6398544b1cdbeb4e5f39f0 = "1265a15461038989925e0ced2799762c";
        String e5544ab05d8c25c1a5da5cd59144fb = Encryption.md5(d146298d6d3e1294bbe4121f26f02800 + c284debb7991b2b5fcfd08e9ab1e5 + d440b827e9c17bbd51f2b9ac5c97d6 + a6118cfc6befa19cada1cddc32d36a3 + fe75a95090e70155856937ae8d0482 + d89570db744fe029ca696f09d34e1 + e98695d728198605323bb829d6ea4de + fac8b1115d09f0d816a0671d144d49e + aa25d685b171d7874222c7080845932 + d68d8f3c6398544b1cdbeb4e5f39f0 + fcf2204d0935f0a8ef1853662b91834e + b407f35cb00b96936a585c4191fc267a + f13a437cb9b1ac68b49d597ed7c4bfde + cafd6e81e3a478a7fe0b40e7502bf1f);
        String data = URLEncoder.encode("b407f35cb00b96936a585c4191fc267a", "UTF-8") + "=" + URLEncoder.encode(b407f35cb00b96936a585c4191fc267a, "UTF-8");
        data += "&" + URLEncoder.encode("f13a437cb9b1ac68b49d597ed7c4bfde", "UTF-8") + "=" + URLEncoder.encode(f13a437cb9b1ac68b49d597ed7c4bfde, "UTF-8");
        data += "&" + URLEncoder.encode("9cafd6e81e3a478a7fe0b40e7502bf1f", "UTF-8") + "=" + URLEncoder.encode(cafd6e81e3a478a7fe0b40e7502bf1f, "UTF-8");
        data += "&" + URLEncoder.encode("58e5544ab05d8c25c1a5da5cd59144fb", "UTF-8") + "=" + URLEncoder.encode(e5544ab05d8c25c1a5da5cd59144fb, "UTF-8");
        data += "&" + URLEncoder.encode("fcf2204d0935f0a8ef1853662b91834e", "UTF-8") + "=" + URLEncoder.encode(fcf2204d0935f0a8ef1853662b91834e, "UTF-8");
        data += "&" + URLEncoder.encode("3aa25d685b171d7874222c7080845932", "UTF-8") + "=" + URLEncoder.encode(aa25d685b171d7874222c7080845932, "UTF-8");
        data += "&" + URLEncoder.encode("6fac8b1115d09f0d816a0671d144d49e", "UTF-8") + "=" + URLEncoder.encode(fac8b1115d09f0d816a0671d144d49e, "UTF-8");
        data += "&" + URLEncoder.encode("5e98695d728198605323bb829d6ea4de", "UTF-8") + "=" + URLEncoder.encode(e98695d728198605323bb829d6ea4de, "UTF-8");
        data += "&" + URLEncoder.encode("189d89570db744fe029ca696f09d34e1", "UTF-8") + "=" + URLEncoder.encode(d89570db744fe029ca696f09d34e1, "UTF-8");
        data += "&" + URLEncoder.encode("70fe75a95090e70155856937ae8d0482", "UTF-8") + "=" + URLEncoder.encode(fe75a95090e70155856937ae8d0482, "UTF-8");
        data += "&" + URLEncoder.encode("9a6118cfc6befa19cada1cddc32d36a3", "UTF-8") + "=" + URLEncoder.encode(a6118cfc6befa19cada1cddc32d36a3, "UTF-8");
        data += "&" + URLEncoder.encode("94d440b827e9c17bbd51f2b9ac5c97d6", "UTF-8") + "=" + URLEncoder.encode(d440b827e9c17bbd51f2b9ac5c97d6, "UTF-8");
        data += "&" + URLEncoder.encode("234c284debb7991b2b5fcfd08e9ab1e5", "UTF-8") + "=" + URLEncoder.encode(c284debb7991b2b5fcfd08e9ab1e5, "UTF-8");
        data += "&" + URLEncoder.encode("41d68d8f3c6398544b1cdbeb4e5f39f0", "UTF-8") + "=" + URLEncoder.encode(d68d8f3c6398544b1cdbeb4e5f39f0, "UTF-8");
        data += "&" + URLEncoder.encode("d146298d6d3e1294bbe4121f26f02800", "UTF-8") + "=" + URLEncoder.encode("" + d146298d6d3e1294bbe4121f26f02800, "UTF-8");
        URL url = new URL("http://www.craftfire.com/stats.php");
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(2000);
        conn.setRequestProperty("X-AuthDB", e5544ab05d8c25c1a5da5cd59144fb);
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        wr.close();
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        rd.close();
    }

    public static int toTicks(String time, String length) {
       logging.Debug("Launching function: toTicks(String time, String length) - " + time + ":" + length);
        time = time.toLowerCase();
        int lengthint = Integer.parseInt(length);
        if (time.equals("days") || time.equals("day") || time.equals("d")) { return lengthint * 1728000; }
        else if (time.equals("hours") || time.equals("hour") || time.equals("hr") || time.equals("hrs") || time.equals("h")) { return lengthint * 72000; }
        else if (time.equals("minute") || time.equals("minutes") || time.equals("min") || time.equals("mins") || time.equals("m")) { return lengthint * 1200; }
        else if (time.equals("seconds") || time.equals("seconds") || time.equals("sec") || time.equals("s")) { return lengthint * 20; }
        return 0;
    }

    public static int toSeconds(String time, String length) {
        logging.Debug("Launching function: toSeconds(String time, String length) - " + time + ":" + length);
        time = time.toLowerCase();
        int lengthint = Integer.parseInt(length);
        if (time.equals("days") || time.equals("day") || time.equals("d"))
            return lengthint * 86400;
        else if (time.equals("hours") || time.equals("hour") || time.equals("hr") || time.equals("hrs") || time.equals("h"))
            return lengthint * 3600;
        else if (time.equals("minute") || time.equals("minutes") || time.equals("min") || time.equals("mins") || time.equals("m"))
            return lengthint * 60;
        else if (time.equals("seconds") || time.equals("seconds") || time.equals("sec") || time.equals("s"))
            return lengthint;
        return 0;
    }

    public static int stringToTicks(String string) {
        String[] split = string.split(" ");
        String length = split[0];
        String time = split[1].toLowerCase();
        int lengthint = Integer.parseInt(length);
        logging.Debug("Launching function: FullStringToSeconds(String time, String length) - " + time + ":" + length);
        if (time.equals("days") || time.equals("day") || time.equals("d"))
            return lengthint * 1728000;
        else if (time.equals("hours") || time.equals("hour") || time.equals("hr") || time.equals("hrs") || time.equals("h"))
            return lengthint * 72000;
        else if (time.equals("minute") || time.equals("minutes") || time.equals("min") || time.equals("mins") || time.equals("m"))
            return lengthint * 1200;
        else if (time.equals("seconds") || time.equals("seconds") || time.equals("sec") || time.equals("s"))
            return lengthint * 20;
        return 0;
    }

    public static int stringToSeconds(String string) {
        String[] split = string.split(" ");
        String length = split[0];
        String time = split[1].toLowerCase();
        int lengthint = Integer.parseInt(length);
        logging.Debug("Launching function: StringToSeconds(String time, String length) - " + time + ":" + length);
        if (time.equals("days") || time.equals("day") || time.equals("d")) {
            return lengthint * 86400;
        }
        else if (time.equals("hours") || time.equals("hour") || time.equals("hr") || time.equals("hrs") || time.equals("h")) {
            return lengthint * 3600;
        }
        else if (time.equals("minute") || time.equals("minutes") || time.equals("min") || time.equals("mins") || time.equals("m")) {
            return lengthint * 60;
        }
        else if (time.equals("seconds") || time.equals("seconds") || time.equals("sec") || time.equals("s")) {
            return lengthint;
        }
        return 0;
    }

    public static String toDriver(String dataname) {
        dataname = dataname.toLowerCase();
        if (dataname.equals("mysql"))
            return "com.mysql.jdbc.Driver";

        return "com.mysql.jdbc.Driver";
    }

    public static String toLoginMethod(String method) {
        method = method.toLowerCase();
        if (method.equals("prompt"))
            return method;
        else
            return "normal";
    }

    public static boolean checkWhitelist(String whitelist, Player player) {
        String username = player.getName().toLowerCase();
        logging.Debug("Launching function: checkWhitelist(String whitelist, String username) - " + username);
        StringTokenizer st = null;
        if (whitelist.equals("username")) { st = new StringTokenizer(Config.filter_whitelist, ", "); }
        while (st != null && st.hasMoreTokens()) {
            String whitelistname = st.nextToken().toLowerCase();
            logging.Debug("Whitelist: " + whitelistname);
            if (whitelistname.equals(username)) {
                logging.Debug("FOUND USER IN WHITELIST: " + whitelistname);
                if (whitelist.equals("idle")) { Messages.sendMessage(Message.idle_whitelist, player, null); }
                else if (whitelist.equals("username")) { Messages.sendMessage(Message.filter_whitelist, player, null); }
                return true;
            }
        }
        return false;
    }

    public static void checkIdle(Player player) {
        logging.Debug("Launching function: CheckIdle(Player player)");
        if (!AuthDB.isAuthorized(player)) {
             Messages.sendMessage(Message.kickPlayerIdleLoginMessage, player, null);
        }
    }

    public static long ip2Long(String ip) {
        logging.Debug("Launching function: IP2Long(String IP)");
        long f1, f2, f3, f4;
        String tokens[] = ip.split("\\.");
        if (tokens.length != 4) { return -1; }
        try {
            f1 = Long.parseLong(tokens[0]) << 24;
            f2 = Long.parseLong(tokens[1]) << 16;
            f3 = Long.parseLong(tokens[2]) << 8;
            f4 = Long.parseLong(tokens[3]);
            return f1 + f2 + f3 + f4;
        } catch (Exception e) {
            return -1;
        }

    }

    public static boolean checkFilter(String what, String string) {
        if (what.equals("username")) {
            logging.Debug("Launching function: checkFilter(String what, String string) - " + Config.filter_username);
            int lengtha = string.length();
            int lengthb = Config.filter_username.length();
            int i = 0;
            char thechar1, thechar2;
            while (i < lengtha) {
                thechar1 = string.charAt(i);
                int a = 0;
                while (a < lengthb) {
                    thechar2 = Config.filter_username.charAt(a);
                    //logging.Debug(i + "-" + thechar1 + ":" + a + "-" + thechar2);
                    if (thechar1 == thechar2 || thechar1 == '\'' || thechar1 == '\"') {
                        logging.Debug("FOUND BAD CHARACTER!!: " + thechar2);
                        Config.has_badcharacters = true;
                        return false;
                    }
                    a++;
                }
                i++;
            }
            Config.has_badcharacters = false;
            return true;
        }
        else if (what.equals("password")) {
            logging.Debug("Launching function: checkFilter(String what, String string) - " + Config.filter_password);
            int lengtha = string.length();
            int lengthb = Config.filter_password.length();
            int i = 0;
            char thechar1, thechar2;
            while (i < lengtha) {
                thechar1 = string.charAt(i);
                int a = 0;
                while (a < lengthb) {
                    thechar2 = Config.filter_password.charAt(a);
                    //logging.Debug(i + "-" + thechar1 + ":" + a + "-" + thechar2);
                    if (thechar1 == thechar2 || thechar1 == '\'' || thechar1 == '\"') {
                        logging.Debug("FOUND BAD CHARACTER!!: " + thechar2);
                        return false;
                    }
                    a++;
                }
                i++;
            }
            return true;
        }
        return true;
    }

    public static String fixCharacters(String string) {
        int lengtha = string.length();
        int lengthb = "`~!@#$%^&*()-= + {[]}|\\:;\"<, >.?/".length();
        int i = 0;
        char thechar1, thechar2;
        StringBuffer tempstring = new StringBuffer();;
        while (i < lengtha) {
            thechar1 = string.charAt(i);
            int a = 0;
            while (a < lengthb) {
                thechar2 = "`~!@#$%^&*()-= + {[]}|\\:;\"<, >.?/".charAt(a);
                if (thechar1 == thechar2 || thechar1 == '\'' || thechar1 == '\"') {
                    thechar1 = thechar2;
                }
                a++;
            }
            tempstring.append(thechar1);
            i++;
        }
        return tempstring.toString();
    }

    public static String replaceStrings(String string, Player player, String additional) {
        logging.Debug(("Launching function: replaceStrings(String string, Player player, String additional)"));
        if (!Config.has_badcharacters && Config.database_ison && player != null
        && player.getName().length() > Integer.parseInt(Config.username_minimum)
        && player.getName().length() < Integer.parseInt(Config.username_maximum)) {
            string = string.replaceAll("\\{IP\\}", craftFirePlayer.getIP(player));
            string = string.replaceAll("\\{PLAYER\\}", player.getName());
            string = string.replaceAll("\\{NEWPLAYER\\}", "");
            string = string.replaceAll("&", "§");
            if (!Util.checkOtherName(player.getName()).equals(player.getName())) {
                string = string.replaceAll("\\{DISPLAYNAME\\}", checkOtherName(player.getName()));
            }
        }
        else { string = string.replaceAll("&", Matcher.quoteReplacement("§"));  }
        string = string.replaceAll("\\{USERMIN\\}", Config.username_minimum);
        string = string.replaceAll("\\{USERMAX\\}", Config.username_maximum);
        string = string.replaceAll("\\{PASSMIN\\}", Config.password_minimum);
        string = string.replaceAll("\\{PASSMAX\\}", Config.password_maximum);
        string = string.replaceAll("\\{PLUGIN\\}", AuthDB.pluginName);
        string = string.replaceAll("\\{VERSION\\}", AuthDB.pluginVersion);
        string = string.replaceAll("\\{LOGINTIMEOUT\\}", Config.login_timeout_length + " " + Config.login_timeout_time);
        string = string.replaceAll("\\{REGISTERTIMEOUT\\}", "" + Config.register_timeout_length + " " + Config.register_timeout_time);
        string = string.replaceAll("\\{USERBADCHARACTERS\\}", Matcher.quoteReplacement(Config.filter_username));
        string = string.replaceAll("\\{PASSBADCHARACTERS\\}", Matcher.quoteReplacement(Config.filter_password));
        string = string.replaceAll("\\{NEWLINE\\}", "\n");
        string = string.replaceAll("\\{newline\\}", "\n");
        string = string.replaceAll("\\{N\\}", "\n");
        string = string.replaceAll("\\{n\\}", "\n");

        ///COLORS
        string = string.replaceAll("\\{BLACK\\}", "§0");
        string = string.replaceAll("\\{NAVY\\}", "§1");
        string = string.replaceAll("\\{GREEN\\}", "§2");
        string = string.replaceAll("\\{BLUE\\}", "§3");
        string = string.replaceAll("\\{RED\\}", "§4");
        string = string.replaceAll("\\{PURPLE\\}", "§5");
        string = string.replaceAll("\\{GOLD\\}", "§6");
        string = string.replaceAll("\\{LIGHTGRAY\\}", "§7");
        string = string.replaceAll("\\{GRAY\\}", "§8");
        string = string.replaceAll("\\{DARKPURPLE\\}", "§9");
        string = string.replaceAll("\\{LIGHTGREEN\\}", "§a");
        string = string.replaceAll("\\{LIGHTBLUE\\}", "§b");
        string = string.replaceAll("\\{ROSE\\}", "§c");
        string = string.replaceAll("\\{LIGHTPURPLE\\}", "§d");
        string = string.replaceAll("\\{YELLOW\\}", "§e");
        string = string.replaceAll("\\{WHITE\\}", "§f");

        string = string.replaceAll("\\{BLACK\\}", "§0");
        string = string.replaceAll("\\{DARKBLUE\\}", "§1");
        string = string.replaceAll("\\{DARKGREEN\\}", "§2");
        string = string.replaceAll("\\{DARKTEAL\\}", "§3");
        string = string.replaceAll("\\{DARKRED\\}", "§4");
        string = string.replaceAll("\\{PURPLE\\}", "§5");
        string = string.replaceAll("\\{GOLD\\}", "§6");
        string = string.replaceAll("\\{GRAY\\}", "§7");
        string = string.replaceAll("\\{DARKGRAY\\}", "§8");
        string = string.replaceAll("\\{BLUE\\}", "§9");
        string = string.replaceAll("\\{BRIGHTGREEN\\}", "§a");
        string = string.replaceAll("\\{TEAL\\}", "§b");
        string = string.replaceAll("\\{RED\\}", "§c");
        string = string.replaceAll("\\{PINK\\}", "§d");
        string = string.replaceAll("\\{YELLOW\\}", "§e");
        string = string.replaceAll("\\{WHITE\\}", "§f");

        ///colors
        string = string.replaceAll("\\{black\\}", "§0");
        string = string.replaceAll("\\{navy\\}", "§1");
        string = string.replaceAll("\\{green\\}", "§2");
        string = string.replaceAll("\\{blue\\}", "§3");
        string = string.replaceAll("\\{red\\}", "§4");
        string = string.replaceAll("\\{purple\\}", "§5");
        string = string.replaceAll("\\{gold\\}", "§6");
        string = string.replaceAll("\\{lightgray\\}", "§7");
        string = string.replaceAll("\\{gray\\}", "§8");
        string = string.replaceAll("\\{darkpurple\\}", "§9");
        string = string.replaceAll("\\{lightgreen\\}", "§a");
        string = string.replaceAll("\\{lightblue\\}", "§b");
        string = string.replaceAll("\\{rose\\}", "§c");
        string = string.replaceAll("\\{lightpurple\\}", "§d");
        string = string.replaceAll("\\{yellow\\}", "§e");
        string = string.replaceAll("\\{white\\}", "§f");

        string = string.replaceAll("\\{black\\}", "§0");
        string = string.replaceAll("\\{darkblue\\}", "§1");
        string = string.replaceAll("\\{darkgreen\\}", "§2");
        string = string.replaceAll("\\{darkteal\\}", "§3");
        string = string.replaceAll("\\{darkred\\}", "§4");
        string = string.replaceAll("\\{purple\\}", "§5");
        string = string.replaceAll("\\{gold\\}", "§6");
        string = string.replaceAll("\\{gray\\}", "§7");
        string = string.replaceAll("\\{darkgray\\}", "§8");
        string = string.replaceAll("\\{blue\\}", "§9");
        string = string.replaceAll("\\{brightgreen\\}", "§a");
        string = string.replaceAll("\\{teal\\}", "§b");
        string = string.replaceAll("\\{red\\}", "§c");
        string = string.replaceAll("\\{pink\\}", "§d");
        string = string.replaceAll("\\{yellow\\}", "§e");
        string = string.replaceAll("\\{white\\}", "§f");

        return string;
    }

    public static String removeColors(String toremove) {
        logging.Debug("Launching function: checkWhitelist");
        toremove = toremove.replace("?0", "");
        toremove = toremove.replace("?2", "");
        toremove = toremove.replace("?3", "");
        toremove = toremove.replace("?4", "");
        toremove = toremove.replace("?5", "");
        toremove = toremove.replace("?6", "");
        toremove = toremove.replace("?7", "");
        toremove = toremove.replace("?8", "");
        toremove = toremove.replace("?9", "");
        toremove = toremove.replace("?a", "");
        toremove = toremove.replace("?b", "");
        toremove = toremove.replace("?c", "");
        toremove = toremove.replace("?d", "");
        toremove = toremove.replace("?e", "");
        toremove = toremove.replace("?f", "");
        return toremove;
    }

    public static String removeChar(String s, char c) {
        logging.Debug("Launching function: removeChar(String s, char c)");
      StringBuffer r = new StringBuffer(s.length());
      r.setLength(s.length());
      int current = 0;
      for (int i = 0; i < s.length(); i ++) {
         char cur = s.charAt(i);
         if (cur != c) r.setCharAt(current++, cur);
      }
      return r.toString();
    }

    public static String getRandomString(int length) {
        String charset = "0123456789abcdefghijklmnopqrstuvwxyz";
        Random rand = new Random(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int pos = rand.nextInt(charset.length());
            sb.append(charset.charAt(pos));
        }
        return sb.toString();
    }

    public static String getRandomString2(int length, String charset) {
        Random rand = new Random(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int pos = rand.nextInt(charset.length());
            sb.append(charset.charAt(pos));
        }
        return sb.toString();
    }

    static int randomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static Location landLocation(Location location) {
        while (location.getBlock().getType().getId() == 0) {
            location.setY(location.getY() - 1);
        }
        location.setY(location.getY() + 1);
        return location;
    }

    public static String checkOtherName(String player) {
         if (AuthDB.AuthDB_LinkedNames.containsKey(player))
 {
             return AuthDB.AuthDB_LinkedNames.get(player);
         }
 else if (!AuthDB.AuthDB_LinkedNameCheck.containsKey(player))
 {
             AuthDB.AuthDB_LinkedNameCheck.put(player, "yes");
             EBean eBeanClass = EBean.checkPlayer(player);
             String linkedName = eBeanClass.getLinkedname();
             if (linkedName != null && linkedName.equals("") == false) {
                 AuthDB.AuthDB_LinkedNames.put(player, linkedName);
                 return linkedName;
             }
         }
         return player;
    }

 public static boolean checkIfLoggedIn(Player player) {
     for (Player p : player.getServer().getOnlinePlayers()) {
         if (p.getName().equals(player.getName()) && AuthDB.isAuthorized(p)) {
             return true;
         }
     }
     return false;
 }

    public static String getAction(String action) {
        if (action.toLowerCase().equals("kick")) { return "kick"; }
        else if (action.toLowerCase().equals("ban")) { return "ban"; }
        else if (action.toLowerCase().equals("rename")) { return "rename"; }
        return "kick";
    }


    static int hexToInt(char ch) {
        if (ch >= '0' && ch <= '9') { return ch - '0'; }
        ch = Character.toUpperCase(ch);
        if (ch >= 'A' && ch <= 'F') { return ch - 'A' + 0xA; }
        throw new IllegalArgumentException("Not a hex character: " + ch);
    }

     public static String hexToString(String str) {
          char[] chars = str.toCharArray();
          StringBuffer hex = new StringBuffer();
          for (int i = 0; i < chars.length; i++) { hex.append(Integer.toHexString((int) chars[i])); }
          return hex.toString();
      }

    public static String checkSessionStart (String string) {
        if (string.equals("login")) { return "login"; }
        else if (string.equals("logoff")) { return "logoff"; }
        else { return "login"; }
    }

    static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int twoHalfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) { buf.append((char) ('0' + halfbyte)); }
                else { buf.append((char) ('a' + (halfbyte - 10))); }
                halfbyte = data[i] & 0x0F;
            } while (twoHalfs++< 1);
        }
        return buf.toString();
    }

    static String bytes2hex(byte[] bytes) {
        StringBuffer r = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String x = Integer.toHexString(bytes[i] & 0xff);
            if (x.length() < 2) { r.append("0"); }
            r.append(x);
        }
        return r.toString();
    }
}
