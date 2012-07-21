/*
 * This file is part of AuthDB Legacy.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * AuthDB Legacy is licensed under the GNU Lesser General Public License.
 *
 * AuthDB Legacy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB Legacy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.authdb.util;

import com.authdb.AuthDB;
import com.authdb.scripts.Custom;
import com.authdb.scripts.cms.DLE;
import com.authdb.scripts.cms.Drupal;
import com.authdb.scripts.cms.Joomla;
import com.authdb.scripts.cms.WordPress;
import com.authdb.scripts.forum.*;
import com.authdb.util.Messages.Message;
import com.authdb.util.databases.EBean;
import com.authdb.util.databases.MySQL;
import com.authdb.util.encryption.Encryption;
import com.craftfire.util.general.GeneralUtil;
import com.craftfire.util.managers.CraftFireManager;
import com.craftfire.util.managers.DatabaseManager;
import com.craftfire.util.managers.LoggingManager;
import com.craftfire.util.managers.ServerManager;
import com.mysql.jdbc.Blob;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;

public class Util {
    public static LoggingManager logging = new LoggingManager();
    public static CraftFireManager craftFire = new CraftFireManager();
    public static DatabaseManager databaseManager = new DatabaseManager();
    public static GeneralUtil gUtil = new GeneralUtil();
    public static com.authdb.util.managers.PlayerManager authDBplayer = new com.authdb.util.managers.PlayerManager();
    public static com.craftfire.util.managers.PlayerManager craftFirePlayer = new com.craftfire.util.managers.PlayerManager();
    public static ServerManager server = new ServerManager();
    static int schedule = 1;
    public static boolean checkScript(String type, String script, String player, String password,
                                      String email, String ipAddress) throws SQLException {
        boolean caseSensitive = false;
        if (Util.databaseManager.getDatabaseType().equalsIgnoreCase("ebean")) {
            EBean eBeanClass = EBean.checkPlayer(player, true);
            if (type.equalsIgnoreCase("checkuser")) {
                if (eBeanClass.getRegistered().equalsIgnoreCase("true")) {
                    return true;
                }
                return false;
            } else if (type.equalsIgnoreCase("checkpassword")) {
                String storedPassword = eBeanClass.getPassword();
                if (Encryption.SHA512(password).equals(storedPassword)) {
                    return true;
                }
                return false;
            } else if (type.equalsIgnoreCase("adduser")) {
                Custom.adduser(player, email, password, ipAddress);
                eBeanClass.setEmail(email);
                eBeanClass.setPassword(Encryption.SHA512(password));
                eBeanClass.setRegistered("true");
                eBeanClass.setIp(ipAddress);
            } else if (type.equalsIgnoreCase("numusers")) {
                int amount = EBean.getUsers();
                logging.Info(amount + " user registrations in database");
            }
        } else if (Config.database_ison) {
            String usertable = null, usernamefield = null, passwordfield = null, saltfield = "";
            boolean bans = false;
            PreparedStatement ps = null;
            int number = 0;
            if (Config.custom_enabled) {
                if (type.equalsIgnoreCase("checkuser")) {
                    String check = MySQL.getfromtable(Config.custom_table, Config.custom_userfield, Config.custom_userfield, player);
                    if (check != "fail") {
                        Config.hasForumBoard = true;
                        return true;
                    }
                    return false;
                } else if (type.equalsIgnoreCase("checkpassword")) {
                    EBean eBeanClass = EBean.find(player);
                    String storedPassword = eBeanClass.getPassword();
                    if (Custom.check_hash(password, storedPassword)) {
                        return true;
                    }
                    String hash = MySQL.getfromtable(Config.custom_table, "`" + Config.custom_passfield + "`", "" + Config.custom_userfield + "", player);
                    EBean.checkPassword(player, hash);
                    if (Custom.check_hash(password, hash)) {
                        return true;
                    }
                    return false;
                } else if (type.equalsIgnoreCase("syncpassword")) {
                    String hash = MySQL.getfromtable(Config.custom_table, "`" + Config.custom_passfield + "`", "" + Config.custom_userfield + "", player);
                    EBean.checkPassword(player, hash);
                    return true;
                } else if (type.equalsIgnoreCase("adduser")) {
                    Custom.adduser(player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                } else if (type.equalsIgnoreCase("numusers")) {
                    ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `" + Config.custom_table + "`");
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        logging.Info(rs.getInt("countit") + " user registrations in database");
                    }
                }
            } else if (script.equalsIgnoreCase(PhpBB.Name) || script.equalsIgnoreCase(PhpBB.ShortName)) {
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
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && PhpBB.check_hash(password, storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "",
                                "`" + passwordfield + "`", "" + usernamefield + "", player.toLowerCase());
                        EBean.checkPassword(player, hash);
                        if (PhpBB.check_hash(password, hash)) {
                            return true;
                        }
                    }
                    /*else if (type.equalsIgnoreCase("checkban")) {
                        String check = "fail";
                        if (ipAddress != null) {
                            String userid = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + useridfield + "`", "" + usernamefield + "", player);
                            check = MySQL.getfromtable(Config.script_tableprefix + "" + bantable + "", "`" + banipfield + "`", "" + bannamefield + "", userid);
                        } else {
                            check = MySQL.getfromtable(Config.script_tableprefix + "" + bantable + "", "`" + banipfield + "`", "" + banipfield + "", ipAddress);
                        }
                        if (check != "fail") {
                            return true;
                        } else {
                            return false;
                        }
                    } */
                } else if (checkVersionInRange(PhpBB.VersionRange2)) {
                    usernamefield = "username_clean"; // TODO: use equalsIgnoreCase to allow for all variations?
                    passwordfield = "user_password";
                    Config.hasForumBoard = true;
                    bans = true;
                    number = 2;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && PhpBB.check_hash(password, storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "",
                                "`" + passwordfield + "`", "" + usernamefield + "", player.toLowerCase());
                        EBean.checkPassword(player, hash);
                        if (PhpBB.check_hash(password, hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    PhpBB.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(SMF.Name) || script.equalsIgnoreCase(SMF.ShortName)) {
                usertable = "members";
                if (checkVersionInRange(SMF.VersionRange)) {
                    usernamefield = "memberName";
                    passwordfield = "passwd";
                    saltfield = "passwordSalt";
                    Config.hasForumBoard = true;
                    caseSensitive = true;
                    bans = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && SMF.check_hash(SMF.hash(1, player, password), storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (SMF.check_hash(SMF.hash(1, player, password), hash)) {
                            return true;
                        }
                    }
                } else if (checkVersionInRange(SMF.VersionRange2) || checkVersionInRange("2.0-2.0")
                        || checkVersionInRange("2.0.0-2.0.0")) {
                    usernamefield = "member_name";
                    passwordfield = "passwd";
                    saltfield = "password_salt";
                    Config.hasForumBoard = true;
                    caseSensitive = true;
                    bans = true;
                    number = 2;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && SMF.check_hash(SMF.hash(2, player, password), storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (SMF.check_hash(SMF.hash(2, player, password), hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    SMF.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(MyBB.Name) || script.equalsIgnoreCase(MyBB.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(MyBB.VersionRange)) {
                    saltfield = "salt";
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    bans = true;
                    caseSensitive = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && MyBB.check_hash(MyBB.hash("find", player, password, ""), storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (MyBB.check_hash(MyBB.hash("find", player, password, ""), hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    MyBB.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(VBulletin.Name) || script.equalsIgnoreCase(VBulletin.ShortName)) {
                usertable = "user";
                if (checkVersionInRange(VBulletin.VersionRange)) {
                    saltfield = "salt";
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    caseSensitive = true;
                    bans = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && VBulletin.check_hash(VBulletin.hash("find", player, password, ""), storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (VBulletin.check_hash(VBulletin.hash("find", player, password, ""), hash)) {
                            return true;
                        }
                    }
                } else if (checkVersionInRange(VBulletin.VersionRange2)) {
                    saltfield = "salt";
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    bans = true;
                    number = 2;
                    caseSensitive = true;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && VBulletin.check_hash(VBulletin.hash("find", player, password, ""), storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (VBulletin.check_hash(VBulletin.hash("find", player, password, ""), hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    VBulletin.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(Drupal.Name) || script.equalsIgnoreCase(Drupal.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(Drupal.VersionRange)) {
                    usernamefield = "name";
                    passwordfield = "pass";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && Encryption.md5(password).equals(storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (Encryption.md5(password).equals(hash)) {
                            return true;
                        }
                    }
                } else if (checkVersionInRange(Drupal.VersionRange2)) {
                    usernamefield = "name";
                    passwordfield = "pass";
                    Config.hasForumBoard = true;
                    number = 2;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && storedPassword.equals(Drupal.user_check_password(password, storedPassword))) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (hash.equals(Drupal.user_check_password(password, hash))) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    Drupal.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(Joomla.Name) || script.equalsIgnoreCase(Joomla.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(Joomla.VersionRange)) {
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    caseSensitive = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && Joomla.check_hash(password, storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (Joomla.check_hash(password, hash)) {
                            return true;
                        }
                    }
                } else if (checkVersionInRange(Joomla.VersionRange2)) {
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    caseSensitive = true;
                    number = 2;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && Joomla.check_hash(password, storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (Joomla.check_hash(password, hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    Joomla.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(Vanilla.Name) || script.equalsIgnoreCase(Vanilla.ShortName)) {
                if (checkVersionInRange(Vanilla.VersionRange)) {
                    usertable = "User";
                    usernamefield = "Name";
                    passwordfield = "Password";
                    caseSensitive = true;
                    if (Vanilla.check() == 2) {
                        usertable = usertable.toLowerCase();
                    }
                    Config.hasForumBoard = true;
                    number = Vanilla.check();
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && Vanilla.check_hash(password, storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (Vanilla.check_hash(password, hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    String emailcheck =  MySQL.getfromtable(Config.script_tableprefix + usertable, "`" + usernamefield + "`", "Email", email);
                    if (emailcheck.equalsIgnoreCase("fail")) {
                        Vanilla.adduser(number, player, email, password, ipAddress);
                        EBean.sync(player);
                        return true;
                    }
                    return false;
                }
            } else if (script.equalsIgnoreCase(PunBB.Name) || script.equalsIgnoreCase(PunBB.ShortName)) {
                usertable = "users";
                //bantable = "bans";
                if (checkVersionInRange(PunBB.VersionRange)) {
                    //bannamefield = "username";
                    saltfield = "salt";
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    caseSensitive = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && PunBB.check_hash(PunBB.hash("find", player, password, ""), storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (PunBB.check_hash(PunBB.hash("find", player, password, ""), hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    PunBB.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(XenForo.Name) || script.equalsIgnoreCase(XenForo.ShortName)) {
                usertable = "user";
                if (checkVersionInRange(XenForo.VersionRange) || checkVersionInRange(XenForo.VersionRange2)) {
                    String userid = MySQL.getfromtable(Config.script_tableprefix + usertable, "`user_id`", "username", player);
                    usernamefield = "username";
                    passwordfield = "password";
                    caseSensitive = true;
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        Blob hash = MySQL.getfromtableBlob(Config.script_tableprefix + "user_authenticate", "`data`", "user_id", userid);
                        if (hash != null) {
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
                            String thesalt = forumCacheValue(cache, "salt");
                            EBean eBeanClass = EBean.find(player);
                            String storedPassword = eBeanClass.getPassword();
                            String storedSalt = eBeanClass.getSalt();
                            if (storedPassword != null && storedSalt != null && XenForo.check_hash(XenForo.hash(1, storedSalt, password), storedPassword)) {
                                return true;
                            }
                            EBean.checkSalt(player, thesalt);
                            EBean.checkPassword(player, thehash);
                            if (XenForo.check_hash(XenForo.hash(1, thesalt, password), thehash)) {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    if (checkVersionInRange(XenForo.VersionRange)) {
                        XenForo.adduser(1, player, email, password, ipAddress);
                        EBean.sync(player);
                    } else if (checkVersionInRange(XenForo.VersionRange2)) {
                        XenForo.adduser(2, player, email, password, ipAddress);
                        EBean.sync(player);
                    }
                    return true;
                } else if (Config.hasForumBoard && type.equalsIgnoreCase("syncpassword") && !Config.custom_enabled) {
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
                } else if (Config.hasForumBoard && type.equalsIgnoreCase("syncsalt") && !Config.custom_enabled && saltfield != null && saltfield != "") {
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
                        // TODO: Auto-generated catch block
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
            } else if (script.equalsIgnoreCase(BBPress.Name) || script.equalsIgnoreCase(BBPress.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(BBPress.VersionRange)) {
                    usernamefield = "user_login";
                    passwordfield = "user_pass";
                    Config.hasForumBoard = true;
                    caseSensitive = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && BBPress.check_hash(password, storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "",
                                "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (BBPress.check_hash(password, hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    BBPress.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(DLE.Name) || script.equalsIgnoreCase(DLE.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(DLE.VersionRange)) {
                    usernamefield = "name";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && DLE.check_hash(DLE.hash(password), storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "",
                                "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (DLE.check_hash(DLE.hash(password), hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    DLE.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(IPB.Name) || script.equalsIgnoreCase(IPB.ShortName)) {
                usertable = "members";
                if (checkVersionInRange(IPB.VersionRange)) {
                    usernamefield = "members_l_username";
                    passwordfield = "members_pass_hash";
                    saltfield = "members_pass_salt";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && IPB.check_hash(IPB.hash("find", player, password.toLowerCase(), null), storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player.toLowerCase());
                        EBean.checkPassword(player, hash);
                        if (IPB.check_hash(IPB.hash("find", player.toLowerCase(), password, null), hash)) {
                            return true;
                        }
                    }

                }
                if (type.equalsIgnoreCase("adduser")) {
                    IPB.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } else if (script.equalsIgnoreCase(WordPress.Name) || script.equalsIgnoreCase(WordPress.ShortName)) {
                usertable = "users";
                if (checkVersionInRange(WordPress.VersionRange)) {
                    usernamefield = "user_login";
                    passwordfield = "user_pass";
                    Config.hasForumBoard = true;
                    caseSensitive = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        EBean eBeanClass = EBean.find(player);
                        String storedPassword = eBeanClass.getPassword();
                        if (storedPassword != null && WordPress.check_hash(password, storedPassword)) {
                            return true;
                        }
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        EBean.checkPassword(player, hash);
                        if (WordPress.check_hash(password, hash)) {
                            return true;
                        }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                    WordPress.adduser(number, player, email, password, ipAddress);
                    EBean.sync(player);
                    return true;
                }
            } /* else if (script.equalsIgnoreCase(Config.Script11_name) || script.equalsIgnoreCase(Config.Script11_shortname)) {
                usertable = "users";
                if (checkVersionInRange(Config.Script11_versionrange)) {
                    usernamefield = "username";
                    passwordfield = "password";
                    Config.hasForumBoard = true;
                    number = 1;
                    if (type.equalsIgnoreCase("checkpassword")) {
                        String hash = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + passwordfield + "`", "" + usernamefield + "", player);
                        if (XE.check_hash(password, hash)) { return true; }
                    }
                }
                if (type.equalsIgnoreCase("adduser")) {
                     XE.adduser(number, player, email, password, ipAddress);
                     return true;
                }
            } */
            if (!Config.hasForumBoard) {
                if (!Config.custom_enabled) {
                    String tempVers = Config.script_version;
                    Config.script_version = scriptVersion();
                    logging.Info(System.getProperty("line.separator")
                            + "|-----------------------------------------------------------------------------|" + System.getProperty("line.separator")
                            + "|--------------------------------AUTHDB WARNING-------------------------------|" + System.getProperty("line.separator")
                            + "|-----------------------------------------------------------------------------|" + System.getProperty("line.separator")
                            + "| COULD NOT FIND A COMPATIBLE SCRIPT VERSION!                                 |" + System.getProperty("line.separator")
                            + "| PLEASE CHECK YOUR SCRIPT VERSION AND TRY AGAIN. PLUGIN MAY OR MAY NOT WORK. |" + System.getProperty("line.separator")
                            + "| YOUR SCRIPT VERSION FOR " + Config.script_name + " HAS BEEN SET FROM " + tempVers + " TO " + Config.script_version + "             |" + System.getProperty("line.separator")
                            + "| FOR A LIST OF SCRIPT VERSIONS,                                              |" + System.getProperty("line.separator")
                            + "| CHECK: http://wiki.bukkit.org/AuthDB#Scripts_Supported                      |" + System.getProperty("line.separator")
                            + "|-----------------------------------------------------------------------------|");

                }
            }
            if (!caseSensitive && player != null) {
                player = player.toLowerCase();
            }
            if (Config.hasForumBoard && type.equalsIgnoreCase("checkuser") && !Config.custom_enabled) {
                //EBean eBeanClass = EBean.find(player, Column.registered, "true");
                //if (eBeanClass != null) { return true; }
                String check = MySQL.getfromtable(Config.script_tableprefix + usertable, usernamefield, usernamefield, player);
                if (check != "fail") { return true; }
                return false;
            } /*else if (Config.hasForumBoard && type.equalsIgnoreCase("checkban") && !Config.custom_enabled && bantable != null) {
                String check = MySQL.getfromtable(Config.script_tableprefix + bantable, "*", bannamefield, player);
                if (check != "fail") { return true; }
            }*/ else if (Config.hasForumBoard && type.equalsIgnoreCase("numusers") && !Config.custom_enabled) {
                if (script.equalsIgnoreCase(PhpBB.Name) || script.equalsIgnoreCase(PhpBB.ShortName)) {
                    ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"
                            + Config.script_tableprefix + usertable + "` WHERE  `group_id` !=6");
                } else {
                    ps = (PreparedStatement) MySQL.mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"
                            + Config.script_tableprefix + usertable + "`");
                }
                ResultSet rs = ps.executeQuery();
                if (rs.next()) { logging.Info(rs.getInt("countit") + " user registrations in database"); }
            } else if (Config.hasForumBoard && type.equalsIgnoreCase("syncpassword") && !Config.custom_enabled) {
                String hash = MySQL.getfromtable(Config.script_tableprefix + usertable, "`" + passwordfield + "`", usernamefield, player);
                EBean.checkPassword(player, hash);
                return true;
            } else if (Config.hasForumBoard && type.equalsIgnoreCase("syncsalt") && !Config.custom_enabled && saltfield != null && saltfield != "") {
                String salt = MySQL.getfromtable(Config.script_tableprefix + usertable, "`" + saltfield + "`", usernamefield, player);
                EBean.checkSalt(player, salt);
                return true;
            }
        }
        return false;
    }

    static String scriptVersion() {
        String script = Config.script_name;
        if (script.equalsIgnoreCase(PhpBB.Name) || script.equalsIgnoreCase(PhpBB.ShortName)) {
            return split(PhpBB.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(SMF.Name) || script.equalsIgnoreCase(SMF.ShortName)) {
            return split(SMF.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(MyBB.Name) || script.equalsIgnoreCase(MyBB.ShortName)) {
            return split(MyBB.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(VBulletin.Name) || script.equalsIgnoreCase(VBulletin.ShortName)) {
            return split(VBulletin.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(Drupal.Name) || script.equalsIgnoreCase(Drupal.ShortName)) {
            return split(Drupal.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(Joomla.Name) || script.equalsIgnoreCase(Joomla.ShortName)) {
            return split(Joomla.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(Vanilla.Name) || script.equalsIgnoreCase(Vanilla.ShortName)) {
            return split(Vanilla.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(PunBB.Name) || script.equalsIgnoreCase(PunBB.ShortName)) {
            return split(PunBB.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(XenForo.Name) || script.equalsIgnoreCase(XenForo.ShortName)) {
            return split(XenForo.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(BBPress.Name) || script.equalsIgnoreCase(BBPress.ShortName)) {
            return split(BBPress.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(DLE.Name) || script.equalsIgnoreCase(DLE.ShortName)) {
            return split(DLE.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(IPB.Name) || script.equalsIgnoreCase(IPB.ShortName)) {
            return split(IPB.LatestVersionRange, "-")[1];
        } else if (script.equalsIgnoreCase(WordPress.Name) || script.equalsIgnoreCase(WordPress.ShortName)) {
            return split(WordPress.LatestVersionRange, "-")[1];
        }
        return null;
    }

    public static String[] split(String string, String delimiter) {
        String[] split = string.split(delimiter);
        return split;
    }

    static void fillChatField(Player player, String text) {
        for (int i = 0; i < 20; i++) {
            player.sendMessage("");
        }
        player.sendMessage(text);
    }

    static void spamText(final Player player, final String text, final int delay, final int show) {
        //if (Config.login_delay > 0 && !AuthDB.AuthDB_SpamMessage.containsKey(player.getName())) {
        if (Config.login_delay > 0) {
            schedule = AuthDB.server.getScheduler().scheduleSyncDelayedTask(AuthDB.plugin, new Runnable() {
                @Override
                public void run() {
                    /*
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
                    */
                    String message = replaceStrings(text, player, null);
                    if (Config.link_rename && !checkOtherName(player.getName()).equals(player.getName())) {
                        message = message.replaceAll(player.getName(), player.getDisplayName());
                        player.sendMessage(message);
                    } else {
                        player.sendMessage(message);
                    }
                } }, delay);
        }
    }

    public static long timeStamp() {
        return System.currentTimeMillis()/1000;
    }

    public static long timeMS() {
        return System.currentTimeMillis();
    }

    boolean checkingBan(String usertable, String useridfield, String usernamefield, String username, String bantable, String banipfield, String bannamefield, String ipAddress) throws SQLException {
        String check = "fail";
        if (ipAddress == null) {
            String userid = MySQL.getfromtable(Config.script_tableprefix + "" + usertable + "", "`" + useridfield + "`", "" + usernamefield + "", username);
            check = MySQL.getfromtable(Config.script_tableprefix + "" + bantable + "", "`" + banipfield + "`", "" + bannamefield + "", userid);
        } else {
            check = MySQL.getfromtable(Config.script_tableprefix + "" + bantable + "", "`" + banipfield + "`", "" + bannamefield + "", ipAddress);
        }
        if (check != "fail") {
            return true;
        } else {
            return false;
        }
    }

    public static String forumCache(String cache, String player, int userid, String nummember, String activemembers, String newusername, String newuserid, String extrausername, String lastvalue) {
        StringTokenizer st = new StringTokenizer(cache, ":");
        int i = 0;
        List<String> array = new ArrayList<String>();
        while (st.hasMoreTokens()) { array.add(st.nextToken() + ":"); }
        StringBuffer newcache = new StringBuffer();
        while (array.size() > i) {
            if (array.get(i).equals("\"" + nummember + "\";i:") && nummember != null) {
                String temp = array.get(i + 1);
                temp = removeChar(temp, '"');
                temp = removeChar(temp, ':');
                temp = removeChar(temp, 's');
                temp = removeChar(temp, ';');
                temp = temp.trim();
                int tempnum = Integer.parseInt(temp) + 1;
                if (lastvalue.equalsIgnoreCase(nummember)) {
                    temp = tempnum + ";}";
                } else {
                    temp = tempnum + ";s:";
                }
                array.set(i + 1, temp);
            } else if (array.get(i).equals("\"" + newusername + "\";s:") && newusername != null) {
                array.set(i + 1, player.length() + ":");
                if (lastvalue.equalsIgnoreCase(newusername)) {
                    array.set(i + 2, "\"" + player + "\"" + ";}");
                } else {
                    array.set(i + 2, "\"" + player + "\"" + ";s" + ":");
                }
            } else if (array.get(i).equals("\"" + extrausername + "\";s:") && extrausername != null) {
                array.set(i + 1, player.length() + ":");
                if (lastvalue.equalsIgnoreCase(extrausername)) {
                    array.set(i + 2, "\"" + player + "\"" + ";}");
                } else {
                    array.set(i + 2, "\"" + player + "\"" + ";s" + ":");
                }
            } else if (array.get(i).equals("\"" + activemembers + "\";s:") && activemembers != null) {
                String temp = array.get(i + 2);
                temp = removeChar(temp, '"');
                temp = removeChar(temp, ':');
                temp = removeChar(temp, 's');
                temp = removeChar(temp, ';');
                temp = temp.trim();
                int tempnum = Integer.parseInt(temp) + 1;
                String templength = "" + tempnum;
                if (lastvalue.equalsIgnoreCase(activemembers)) {
                    temp = "\"" + tempnum + "\"" + ";}";
                } else {
                    temp = "\"" + tempnum + "\"" + ";s:";
                }
                array.set(i + 1, templength.length() + ":");
                array.set(i + 2, temp);
            } else if (array.get(i).equals("\"" + newuserid + "\";s:") && newuserid != null) {
                String dupe = "" + userid;
                array.set(i + 1, dupe.length() + ":");
                if (lastvalue.equalsIgnoreCase(newuserid)) {
                    array.set(i + 2, "\"" + userid + "\"" + ";}");
                } else {
                    array.set(i + 2, "\"" + userid + "\"" + ";s:");
                }
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
        String[] versions = version.split("\\.");
        String[] versionss = versionrange.split("\\-");
        String[] versionrange1= versionss[0].split("\\.");
        String[] versionrange2= versionss[1].split("\\.");
        Util.logging.Debug("Config version: " + version);
        Util.logging.Debug("Version range: " + versionrange);
        Util.logging.Debug("Version range 1: " + versionss[0]);
        Util.logging.Debug("Version range 2: " + versionss[1]);
        if (version.equals(versionss[0]) || version.equals(versionss[1])) {
            Util.logging.Debug("Version checking PASSED at first check.");
            return true;
        }
        if (versionrange1.length == versions.length) {
            int a = Integer.parseInt(versionrange1[0]);
            int b = Integer.parseInt(versionrange2[0]);
            int c = Integer.parseInt(versions[0]);
            Util.logging.Debug("Version checking: a = " + a + ", b = " + b + ", c = " + c);
            if (a <= c && b >= c) {
                int d = b - c;
                Util.logging.Debug("Version checking: d = " + d);
                if (d > 0) {
                    Util.logging.Debug("Version checking PASSED at second check.");
                    return true;
                } else if (d == 0) {
                    int a2 = Integer.parseInt(versionrange1[1]);
                    int b2 = Integer.parseInt(versionrange2[1]);
                    int c2 = Integer.parseInt(versions[1]);
                    Util.logging.Debug("Version checking: a2 = " + a2 + ", b2 = " + b2 + ", c2 = " + c2);
                    if (a2 <= c2 && b2 >= c2) {
                        if (versionrange1.length == 2) {
                            Util.logging.Debug("Version checking PASSED at third check.");
                            return true;
                        } else if (versionrange1.length > 2) {
                            int d2 = b2 - c2;
                            Util.logging.Debug("Version checking: d2 = " + d2);
                            if (d2 > 0) {
                                Util.logging.Debug("Version checking PASSED at fourth check.");
                                return true;
                            } else if (d2 == 0) {
                                int a3 = Integer.parseInt(versionrange1[2]);
                                int b3 = Integer.parseInt(versionrange2[2]);
                                int c3 = Integer.parseInt(versions[2]);
                                Util.logging.Debug("Version checking: a3 = " + a3 + ", b3 = " + b3 + ", c3 = " + c3);
                                if ((a3 <= c3 && b3 >= c3) || (b3 >= c3 && versionrange1.length != 4)) {
                                    if (versionrange1.length != 4) {
                                        Util.logging.Debug("Version checking PASSED at fifth check.");
                                        return true;
                                    } else if (versionrange1.length == 4) {
                                        int d3 = b3 - c3;
                                        Util.logging.Debug("Version checking: d3 = " + d3);
                                        if (d3 > 0) {
                                            Util.logging.Debug("Version checking PASSED at sixth check.");
                                            return true;
                                        } else if (d3 == 0) {
                                            int a4 = Integer.parseInt(versionrange1[3]);
                                            int b4 = Integer.parseInt(versionrange2[3]);
                                            int c4 = Integer.parseInt(versions[3]);
                                            Util.logging.Debug("Version checking: a4 = " + a4 + ", b4 = " + b4 + ", c4 = " + c4);
                                            if (a4 <= c4 && b4 >= c4) {
                                                Util.logging.Debug("Version checking PASSED at seventh check.");
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
        Util.logging.Debug("Version checking DID NOT PASS.");
        return false;
    }

    public static int toTicks(String time, String length) {
        logging.Debug("Launching function: toTicks(String time, String length) - " + time + ":" + length);
        time = time.toLowerCase();
        int lengthint = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            return lengthint * 1728000;
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") || time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            return lengthint * 72000;
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") || time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            return lengthint * 1200;
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            return lengthint * 20;
        }
        return 0;
    }

    public static int toSeconds(String time, String length) {
        logging.Debug("Launching function: toSeconds(String time, String length) - " + time + ":" + length);
        time = time.toLowerCase();
        int lengthint = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            return lengthint * 86400;
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") || time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            return lengthint * 3600;
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") || time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            return lengthint * 60;
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            return lengthint;
        }
        return 0;
    }

    public static int stringToTicks(String string) {
        String[] split = string.split(" ");
        String length = split[0];
        String time = split[1].toLowerCase();
        int lengthint = Integer.parseInt(length);
        logging.Debug("Launching function: FullStringToSeconds(String time, String length) - " + time + ":" + length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            return lengthint * 1728000;
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") || time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            return lengthint * 72000;
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") || time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            return lengthint * 1200;
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            return lengthint * 20;
        }
        return 0;
    }

    public static int stringToSeconds(String string) {
        String[] split = string.split(" ");
        String length = split[0];
        String time = split[1].toLowerCase();
        int lengthint = Integer.parseInt(length);
        logging.Debug("Launching function: StringToSeconds(String time, String length) - " + time + ":" + length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            return lengthint * 86400;
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") || time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            return lengthint * 3600;
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") || time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            return lengthint * 60;
        } else if (time.equalsIgnoreCase("second") || time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            return lengthint;
        }
        return 0;
    }

    public static String toLoginMethod(String method) {
        method = method.toLowerCase();
        if (method.equalsIgnoreCase("prompt")) {
            return method;
        } else {
            return "normal";
        }
    }

    public static boolean checkWhitelist(String whitelist, Player player) {
        String username = player.getName().toLowerCase();
        logging.Debug("Launching function: checkWhitelist(String whitelist, String username) - " + username);
        StringTokenizer st = null;
        if (whitelist.equalsIgnoreCase("username")) {
            st = new StringTokenizer(Config.filter_whitelist, ",");
        } else if (whitelist.equalsIgnoreCase("guest")) {
            st = new StringTokenizer(Config.guests_whitelist, ",");
        }
        while (st != null && st.hasMoreTokens()) {
            String whitelistname = st.nextToken().toLowerCase();
            logging.Debug("Whitelist: " + whitelistname);
            if (whitelistname.equals(username)) {
                logging.Debug("Found user in whitelist: " + whitelistname);
                if (whitelist.equalsIgnoreCase("username")) {
                    Messages.sendMessage(Message.filter_whitelist, player, null);
                }
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
        if (tokens.length != 4) {
            return -1;
        }
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
        if (what.equalsIgnoreCase("username")) {
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
                    if (thechar1 == thechar2 || thechar1 == '\'' || thechar1 == '\"') {
                        Util.logging.Info(string + " has bad characters in his/her name: " + thechar2);
                        Config.has_badcharacters = true;
                        return false;
                    }
                    a++;
                }
                i++;
            }
            Config.has_badcharacters = false;
            Util.logging.Debug(string + " does not have bad characters in his/her name.");
            return true;
        } else if (what.equalsIgnoreCase("password")) {
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
        StringBuffer tempstring = new StringBuffer();
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
        long start = Util.timeMS();
        logging.Debug(("Launching function: replaceStrings(String string, Player player, String additional)"));
        String extra = "";
        if (additional != null) {
            extra = additional;
        }
        if (!Config.has_badcharacters && Config.database_ison && player != null && player.getName().length() >= Integer.parseInt(Config.username_minimum) && player.getName().length() <= Integer.parseInt(Config.username_maximum) && extra.equalsIgnoreCase("login") == false) {
            string = string.replaceAll("\\{IP\\}", craftFirePlayer.getIP(player));
            string = string.replaceAll("\\{PLAYER\\}", player.getName());
            string = string.replaceAll("\\{NEWPLAYER\\}", "");
            string = string.replaceAll("\\{PLAYERNEW\\}", "");
            string = string.replaceAll("&", "§");
            if (!Util.checkOtherName(player.getName()).equals(player.getName())) {
                string = string.replaceAll("\\{DISPLAYNAME\\}", checkOtherName(player.getName()));
            }
        } else {
            string = string.replaceAll("&", Matcher.quoteReplacement("§"));
        }
        String email = "";
        if (Config.custom_emailrequired) {
            email = "email";
        }

        // Replacement variables
        string = string.replaceAll("\\{USERMIN\\}", Config.username_minimum);
        string = string.replaceAll("\\{USERMAX\\}", Config.username_maximum);
        string = string.replaceAll("\\{PASSMIN\\}", Config.password_minimum);
        string = string.replaceAll("\\{PASSMAX\\}", Config.password_maximum);
        string = string.replaceAll("\\{PLUGIN\\}", AuthDB.pluginName);
        string = string.replaceAll("\\{VERSION\\}", AuthDB.pluginVersion);
        string = string.replaceAll("\\{LOGINTIMEOUT\\}", Config.login_timeout_length + " " + replaceTime(Config.login_timeout_length, Config.login_timeout_time));
        string = string.replaceAll("\\{REGISTERTIMEOUT\\}", "" + Config.register_timeout_length + " " + replaceTime(Config.register_timeout_length, Config.register_timeout_time));
        string = string.replaceAll("\\{USERBADCHARACTERS\\}", Matcher.quoteReplacement(Config.filter_username));
        string = string.replaceAll("\\{PASSBADCHARACTERS\\}", Matcher.quoteReplacement(Config.filter_password));
        string = string.replaceAll("\\{EMAILREQUIRED\\}", email);
        string = string.replaceAll("\\{NEWLINE\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{newline\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{N\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{n\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{NL\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{nl\\}", System.getProperty("line.separator"));
        // Commands
        string = string.replaceAll("\\{REGISTERCMD\\}", Config.commands_user_register + " (" + Config.aliases_user_register + ")");
        string = string.replaceAll("\\{LINKCMD\\}", Config.commands_user_link + " (" + Config.aliases_user_link + ")");
        string = string.replaceAll("\\{UNLINKCMD\\}", Config.commands_user_unlink + " (" + Config.aliases_user_unlink + ")");
        string = string.replaceAll("\\{LOGINCMD\\}", Config.commands_user_login + " (" + Config.aliases_user_login + ")");

        // Uppercase colors
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

        // Lowercase colors
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

        long stop = Util.timeMS();
        Util.logging.Debug("Took " + ((stop - start) / 1000) + " seconds (" + (stop - start) + "ms) to replace tags.");

        return string;
    }

    public static String replaceTime(String length, String time) {
        int lengthint = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            if (lengthint > 1) {
                return Messages.time_days;
            } else {
                return Messages.time_day;
            }
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") || time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            if (lengthint > 1) {
                return Messages.time_hours;
            } else {
                return Messages.time_hour;
            }
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") || time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            if (lengthint > 1) {
                return Messages.time_minutes;
            } else {
                return Messages.time_minute;
            }
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            if (lengthint > 1) {
                return Messages.time_seconds;
            } else {
                return Messages.time_second;
            }
        } else if (time.equalsIgnoreCase("milliseconds") || time.equalsIgnoreCase("millisecond") || time.equalsIgnoreCase("milli") || time.equalsIgnoreCase("ms")) {
            if (lengthint > 1) {
                return Messages.time_milliseconds;
            } else {
                return Messages.time_millisecond;
            }
        }
        return time;
    }

    public static String removeColors(String toremove) {
        long start = Util.timeMS();
        logging.Debug("Launching function: removeColors");
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

        long stop = Util.timeMS();
        Util.logging.Debug("Took " + ((stop - start) / 1000) + " seconds (" + (stop - start) + "ms) to replace colors.");

        return toremove;
    }

    public static String removeChar(String s, char c) {
        logging.Debug("Launching function: removeChar(String s, char c)");
        StringBuffer r = new StringBuffer(s.length());
        r.setLength(s.length());
        int current = 0;
        for (int i = 0; i < s.length(); i++) {
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

    public static int randomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static Location landLocation(Location location) {
        while (location.getBlock().getType().getId() == 0) {
            location.setY(location.getY() - 1);
        }
        location.setY(location.getY() + 2);
        return location;
    }

    public static String checkOtherName(String player) {
        if (AuthDB.AuthDB_LinkedNames.containsKey(player)) {
            return AuthDB.AuthDB_LinkedNames.get(player);
        } else if (!AuthDB.AuthDB_LinkedNameCheck.containsKey(player)) {
            AuthDB.AuthDB_LinkedNameCheck.put(player, "yes");
            EBean eBeanClass = EBean.checkPlayer(player, true);
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
            if (p.getName().equalsIgnoreCase(player.getName()) && AuthDB.isAuthorized(p)) {
                return true;
            }
        }
        return false;
    }

    public static String getAction(String action) {
        if (action.toLowerCase().equalsIgnoreCase("kick")) {
            return "kick";
        } else if (action.toLowerCase().equalsIgnoreCase("ban")) {
            return "ban";
        } else if (action.toLowerCase().equalsIgnoreCase("rename")) {
            return "rename";
        }
        return "kick";
    }


    public static int hexToInt(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        ch = Character.toUpperCase(ch);
        if (ch >= 'A' && ch <= 'F') {
            return ch - 'A' + 0xA;
        }
        throw new IllegalArgumentException("Not a hex character: " + ch);
    }

    public static String hexToString(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public static String checkSessionStart (String string) {
        if (string.equalsIgnoreCase("login")) {
            return "login";
        } else if (string.equalsIgnoreCase("logoff")) {
            return "logoff";
        } else {
            return "login";
        }
    }

    public static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int twoHalfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            }
            while (twoHalfs++< 1);
        }
        return buf.toString();
    }

    public static String bytes2hex(byte[] bytes) {
        StringBuffer r = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String x = Integer.toHexString(bytes[i] & 0xff);
            if (x.length() < 2) {
                r.append("0");
            }
            r.append(x);
        }
        return r.toString();
    }
}