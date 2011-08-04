/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.util;

import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.authdb.scripts.cms.DLE;
import com.authdb.scripts.cms.Drupal;
import com.authdb.scripts.cms.Joomla;
import com.authdb.scripts.cms.WordPress;
import com.authdb.scripts.forum.IPB;
import com.authdb.scripts.forum.PunBB;
import com.authdb.scripts.forum.SMF;
import com.authdb.scripts.forum.Vanilla;
import com.authdb.scripts.forum.XenForo;
import com.authdb.scripts.forum.BBPress;
import com.authdb.scripts.forum.MyBB;
import com.authdb.scripts.forum.PhpBB;
import com.authdb.scripts.forum.VBulletin;
import com.authdb.util.databases.MySQL;

public class API {
    public static String getScript(String what, Player player, String extra) throws SQLException {
        String script = Config.script_name;
        String GroupName = "fail";
        String GroupID = "0";
        String UserID = "0";
        String IsBanned = "";
        String BanReason = "";
        String BannedToDate = "";
        if (script.equalsIgnoreCase(PhpBB.Name) || script.equalsIgnoreCase(PhpBB.ShortName)) {
            if (Util.checkVersionInRange(PhpBB.VersionRange)) {
                //phpbb3
                if (what.equalsIgnoreCase("getgroup")) {
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "users", "`group_id`", "username",player.getName());
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "groups", "`group_name`", "group_id", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banlist", "`ban_id`", "ban_ip",extra);
                        if (IsBanned.equalsIgnoreCase("fail")) {
                            return "false";
                        } else {
                            return "true";
                        }
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`user_id`", "username_clean",player.getName().toLowerCase());
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banlist", "`ban_id`", "ban_userid",UserID);
                        if (IsBanned.equalsIgnoreCase("fail")) {
                            return "false";
                        } else {
                            return "true";
                        }
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banlist", "`ban_reason`", "ban_ip",extra);
                        if (IsBanned.equalsIgnoreCase("fail")) {
                            return "false";
                        } else {
                            return "true";
                        }
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`users_id`", "username_clean",player.getName().toLowerCase());
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "banlist", "`ban_reason`", "ban_userid",UserID);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) {
                            return BanReason;
                        } else {
                            return "noreason";
                        }
                    }
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    if (player == null) {
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "banlist", "`ban_end`", "ban_ip",extra);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("NULL") || BannedToDate.equalsIgnoreCase("0")) {
                                return "perma";
                            } else {
                                return BannedToDate + ",unix";
                            }
                        } else {
                            return "nodate";
                        }
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`user_id`", "username_clean",player.getName().toLowerCase());
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "banlist", "`ban_end`", "ban_userid",UserID);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("NULL") || BannedToDate.equalsIgnoreCase("0")) {
                                return "perma";
                            } else {
                                return BannedToDate + ",unix";
                            }
                        } else {
                            return "nodate";
                        }
                    }
                }
            } else if (Util.checkVersionInRange(PhpBB.VersionRange2)) {
                //phpbb2
                if (what.equalsIgnoreCase("getgroup")) {
                    UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`users_id`", "username",player.getName());
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "users_group", "`group_id`", "user_id",UserID);
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "groups", "`group_name`", "group_id", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        extra = Util.hexToString(extra);
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banlist", "`ban_id`", "ban_ip", extra);
                        if (IsBanned.equalsIgnoreCase("fail")) {
                            return "false";
                        } else {
                            return "true";
                        }
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`user_id`", "username",player.getName());
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banlist", "`ban_id`", "ban_userid",UserID);
                        if (IsBanned.equalsIgnoreCase("fail")) {
                            return "false";
                        } else {
                            return "true";
                        }
                    }

                } else if (what.equalsIgnoreCase("banreason")) {
                    //no ban reason defined
                    return "noreason";
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    //no date
                    return "nodate";
                }
            }
        } else if (script.equalsIgnoreCase(SMF.Name) || script.equalsIgnoreCase(SMF.ShortName)) {
            if (Util.checkVersionInRange(SMF.VersionRange)) {
                //smf1
                if (what.equalsIgnoreCase("getgroup")) {
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "members", "`ID_GROUP`", "memberName",player.getName());
                    if (UserID.equalsIgnoreCase("0")) {
                        GroupID = MySQL.getfromtable(Config.script_tableprefix + "members", "`ID_POST_GROUP`", "memberName",player.getName());
                    }
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "membersgroups", "`groupName`", "ID_GROUP", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    return "";
                    //next version, need to check if between 0 and 255.
                } else if (what.equalsIgnoreCase("banreason")) {
                    if (player == null) {
                        return "";
                        //
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "members", "`ID_MEMBER`", "memberName",player.getName());
                        String BanGroup = MySQL.getfromtable(Config.script_tableprefix + "ban_items", "`ID_BAN_GROUP`", "ID_MEMBER",UserID);
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "ban_groups", "`reason`", "ID_BAN_GROUP",BanGroup);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) {
                            return BanReason;
                        } else {
                            return "noreason";
                        }
                    }
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    if (player == null) {
                        return "";
                        //
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "members", "`ID_MEMBER`", "memberName",player.getName());
                        String BanGroup = MySQL.getfromtable(Config.script_tableprefix + "ban_items", "`ID_BAN_GROUP`", "ID_MEMBER",UserID);
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "ban_groups", "`expire_time`", "ID_BAN_GROUP",BanGroup);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("NULL")) { return "perma"; }
                            else { return BannedToDate + ",unix"; }
                        }
                        else { return "nodate"; }
                    }
                }
            } else if (Util.checkVersionInRange(SMF.VersionRange2) || Util.checkVersionInRange("2.0") || Util.checkVersionInRange("2.0.0") || Util.checkVersionInRange("2.0.0.0")) {
                //smf2
                if (what.equalsIgnoreCase("getgroup")) {
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "members", "`id_group`", "member_name",player.getName());
                    if (UserID.equalsIgnoreCase("0")) {
                        GroupID = MySQL.getfromtable(Config.script_tableprefix + "members", "`id_post_group`", "member_name",player.getName());
                    }
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "membersgroups", "`group_name`", "id_group", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        return "";
                        //next version, need to check if between 0 and 255.
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`user_id`", "username",player.getName());
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "ban_items", "`id_ban`", "id_member",UserID);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }

                } else if (what.equalsIgnoreCase("banreason")) {
                    if (player == null) {
                        return "";
                        //
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "members", "`id_member`", "member_name",player.getName());
                        String BanGroup = MySQL.getfromtable(Config.script_tableprefix + "ban_items", "`id_ban_group`", "id_member",UserID);
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "ban_groups", "`reason`", "id_ban_group",BanGroup);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        else { return "noreason"; }
                    }
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    if (player == null) {
                        return "";
                        //
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "members", "`id_member`", "member_name",player.getName());
                        String BanGroup = MySQL.getfromtable(Config.script_tableprefix + "ban_items", "`id_ban_group`", "id_member",UserID);
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "ban_groups", "`expire_time`", "id_ban_group",BanGroup);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("NULL")) { return "perma"; }
                            else { return BannedToDate + ",unix"; }
                        } else { return "nodate"; }
                    }
                }
            }
        } else if (script.equalsIgnoreCase(MyBB.Name) || script.equalsIgnoreCase(MyBB.ShortName)) {
            if (Util.checkVersionInRange(MyBB.VersionRange)) {
                if (what.equalsIgnoreCase("getgroup")) {
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "users", "`usersgroup`", "username",player.getName());
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "usersgroups", "`title`", "gid", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banfliters", "`fid`", "filter",extra);
                        if (IsBanned.equalsIgnoreCase("fail")) {
                             String delimiter = "\\.";
                             StringBuffer tempIP = new StringBuffer();
                             String[] temp = extra.split(delimiter);
                             int counter = 0;
                             while (counter > (temp.length - 1))
                             {
                                 tempIP.append(temp[counter] + ".");
                                 counter++;
                             }
                             tempIP.append("*");
                             IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banfliters", "`fid`", "filter",tempIP.toString());
                             if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        }
                        return "true";
                    }  else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`uid`", "username",player.getName());
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banned", "`dateline`", "uid",UserID);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    if (player == null) {
                        //no reason
                        return "noreason";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`uid`", "username",player.getName());
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "banned", "`reason`", "uid", UserID);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        else { return "noreason"; }
                    }
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    if (player == null) {
                        //no date
                        return "nodate";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`uid`", "username",player.getName());
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "banned", "`lifted`", "uid", UserID);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("0")) { return "perma"; }
                            else { return BannedToDate + ",unix"; }
                        }
                        else { return "nodate"; }
                    }
                }
            }
        } else if (script.equalsIgnoreCase(VBulletin.Name) || script.equalsIgnoreCase(VBulletin.ShortName)) {
            if (Util.checkVersionInRange(VBulletin.VersionRange)) {
                //VB3
                if (what.equalsIgnoreCase("getgroup")) {
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "user", "`usergroupid`", "username",player.getName());
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "usergroup", "`title`", "usergroupid", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "setting", "`datatype`", "varname", "value", "banip", extra);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "user", "`userid`", "username",player.getName());
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "userban", "`bandate`", "userid",UserID);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    if (player == null) {
                        return "noreason";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "user", "`userid`", "username",player.getName());
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "userban", "`reason`", "userid",UserID);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        else { return "noreason"; }
                    }
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    if (player == null) {
                        return "nodate";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "user", "`userid`", "username",player.getName());
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "userban", "`liftdate`", "userid",UserID);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("0")) { return "perma"; }
                            else { return BannedToDate + ",unix"; }
                        }
                        else { return "nodate"; }
                    }
                }
            } else if (Util.checkVersionInRange(VBulletin.VersionRange2)) {
                //VB4
                if (what.equalsIgnoreCase("getgroup")) {
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "user", "`usergroupid`", "username",player.getName());
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "usergroup", "`title`", "usergroupid", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "setting", "`datatype`", "varname", "value", "banip", extra);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "user", "`userid`", "username",player.getName());
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "userban", "`bandate`", "userid",UserID);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    if (player == null) {
                        return "noreason";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "user", "`userid`", "username",player.getName());
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "userban", "`bandate`", "userid",UserID);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        else { return "noreason"; }
                    }
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    if (player == null) {
                        return "nodate";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "user", "`userid`", "username",player.getName());
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "userban", "`liftdate`", "userid",UserID);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("0")) { return "perma"; }
                            else { return BannedToDate + ",unix"; }
                        }
                        else { return "nodate"; }
                    }
                }
            }
        } else if (script.equalsIgnoreCase(Drupal.Name) || script.equalsIgnoreCase(Drupal.ShortName)) {
            if (Util.checkVersionInRange(Drupal.VersionRange)) {
                //drupal 6
                if (what.equalsIgnoreCase("getgroup")) {
                    UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`uid`", "name",player.getName());
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "users_roles", "`rid`", "uid",UserID);
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "role", "`name`", "rid", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "access", "`type`", "mask",extra);
                        if (IsBanned.equalsIgnoreCase("fail")) {
                             String delimiter = "\\.";
                             StringBuffer tempIP = new StringBuffer();
                             String[] temp = extra.split(delimiter);
                             int counter = 0;
                             while (counter > (temp.length - 1))
                             {
                                 tempIP.append(temp[counter] + ".");
                                 counter++;
                             }
                             tempIP.append("%");
                             IsBanned = MySQL.getfromtable(Config.script_tableprefix + "access", "`type`", "mask",tempIP.toString());
                             if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        }
                        return "true";
                    } else {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "access", "`type`", "mask",player.getName());
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }

                } else if (what.equalsIgnoreCase("banreason")) {
                    //no ban reason on ban
                    return "noreason";
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    //no date set on ban, it's perma
                    return "nodate";
                }
            } else if (Util.checkVersionInRange(Drupal.VersionRange2)) {
                //drupal 7
                if (what.equalsIgnoreCase("getgroup")) {
                    UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`uid`", "name",player.getName());
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "users_roles", "`rid`", "uid",UserID);
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "role", "`name`", "rid", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "blocked_ips", "`iid`", "ip",extra);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    } else {
                        IsBanned = MySQL.getfromtable2(Config.script_tableprefix + "users", "`uid`", "name", "status", player.getName(), "0");
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    //no ban reason on ban
                    return "noreason";
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    //no date on ban, it's perma
                    return "nodate";
                }
            }

        } else if (script.equalsIgnoreCase(Joomla.Name) || script.equalsIgnoreCase(Joomla.ShortName)) {
            if (Util.checkVersionInRange(Joomla.VersionRange)) {
                //1.5
                if (what.equalsIgnoreCase("getgroup")) {
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "users", "`gid`", "username",player.getName());
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "core_acl_aro_groups", "`name`", "id", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        //String BanID = MySQL.getfromtable(Config.script_tableprefix + "plugins", "`params`", "name", "System - Ban IP Address");
                        IsBanned = MySQL.getfromtablelike(Config.script_tableprefix + "plugins", "`name`", "element", "params", "ban", extra);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    } else {
                        return "false";
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    //no reason yet on ban
                    return "noreason";
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    //extension needed
                    return "nodate";
                }
            } else if (Util.checkVersionInRange(Joomla.VersionRange2)) {
                //1.6
                if (what.equalsIgnoreCase("getgroup")) {
                    UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`id`", "username",player.getName());
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "user_usergroup_map", "`group_id`", "user_id",UserID);
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "usergroups", "`title`", "id", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    //Not built in, need a extension.
                    return "";
                } else if (what.equalsIgnoreCase("banreason")) {
                    //no reason yet on ban
                    return "noreason";
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    //no date yet
                    return "nodate";
                }
            }
        } else if (script.equalsIgnoreCase(Vanilla.Name) || script.equalsIgnoreCase(Vanilla.ShortName)) {
            if (Util.checkVersionInRange(Vanilla.VersionRange)) {
                String userTable = "User";
                String userRoleTable = "UserRole";
                String roleTable = "Role";
                if (Vanilla.check() == 2) {
                    userTable = userTable.toLowerCase();
                    userRoleTable = userRoleTable.toLowerCase();
                    roleTable = roleTable.toLowerCase();
                }
                if (what.equalsIgnoreCase("getgroup")) {
                    UserID = MySQL.getfromtable(Config.script_tableprefix + userTable, "`UserID`", "Name",player.getName());
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + userRoleTable, "`RoleID`", "UserID",UserID);
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + roleTable, "`Name`", "RoleID", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        return "";
                        //find addon
                    } else {
                        String BanID = MySQL.getfromtable(Config.script_tableprefix + roleTable, "`RoleID`", "Name", "Banned");
                        UserID = MySQL.getfromtable(Config.script_tableprefix + userTable, "`UserID`", "Name",player.getName());
                        IsBanned = MySQL.getfromtable2(Config.script_tableprefix + userRoleTable, "`UserID`", "UserID", "RoleID", UserID, BanID);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    //no reason on ban
                    return "noreason";
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    //no date on ban, just a group
                    return "nodate";
                }

            }
        } else if (script.equalsIgnoreCase(PunBB.Name) || script.equalsIgnoreCase(PunBB.ShortName)) {
            if (Util.checkVersionInRange(PunBB.VersionRange)) {
                if (what.equalsIgnoreCase("getgroup")) {
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "users", "`group_id`", "username",player.getName());
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "groups", "`g_title`", "g_id", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "bans", "`ban_creator`", "ip", extra);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    } else {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "bans", "`ban_creator`", "username", player.getName());
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    if (player == null) {
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "bans", "`message`", "ip", extra);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        else { return "noreason"; }
                    } else {
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "bans", "`message`", "username", player.getName());
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        else { return "noreason"; }
                    }
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    if (player == null) {
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "bans", "`expire`", "ip", extra);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("0") || BannedToDate.equalsIgnoreCase("NULL")) { return "perma"; }
                            else { return BannedToDate + ",unix"; }
                        } else { return "nodate"; }
                    } else {
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "bans", "`expire`", "username", player.getName());
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("0") || BannedToDate.equalsIgnoreCase("NULL")) { return "perma"; }
                            else { return BannedToDate + ",unix"; }
                        }
                        else { return "nodate"; }
                    }
                }
            }
        } else if (script.equalsIgnoreCase(XenForo.Name) || script.equalsIgnoreCase(XenForo.ShortName)) {
            if (Util.checkVersionInRange(XenForo.VersionRange)) {
                if (what.equalsIgnoreCase("getgroup")) {
                    return "";
                    //next version
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "ip_match", "`match_type`", "ip",extra);
                        if (IsBanned.equalsIgnoreCase("fail")) {
                             String delimiter = "\\.";
                             StringBuffer tempIP = new StringBuffer();
                             String[] temp = extra.split(delimiter);
                             int counter = 0;
                             while (counter > (temp.length - 1)) {
                                 tempIP.append(temp[counter] + ".");
                                 counter++;
                             }
                             tempIP.append("*");
                             IsBanned = MySQL.getfromtable(Config.script_tableprefix + "ip_match", "`match_type`", "ip",tempIP.toString());
                             if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        }
                        return "true";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "user", "`user_id`", "username",player.getName());
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "user_ban", "`ban_date`", "user_id",UserID);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    if (player == null) {
                        return "noreason";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "user", "`user_id`", "username",player.getName());
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "user_ban", "`user_reason`", "user_id",UserID);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        else { return "noreason"; }
                    }
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    if (player == null) {
                        return "nodate";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "user", "`user_id`", "username",player.getName());
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "user_ban", "`end_date`", "user_id",UserID);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("0") || BannedToDate.equalsIgnoreCase("NULL")) { return "perma"; }
                            else { return BannedToDate + ",unix"; }
                        } else { return "nodate"; }
                    }
                }
            }
        } else if (script.equalsIgnoreCase(BBPress.Name) || script.equalsIgnoreCase(BBPress.ShortName)) {
            if (Util.checkVersionInRange(BBPress.VersionRange)) {
                if (what.equalsIgnoreCase("getgroup")) {
                    return "";
                    //next version: http://buddypress.org/
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        return "";
                        //Next version, need to install addon: http://bbpress.org/plugins/topic/bbpress-moderation-suite/
                    } else {
                        return "";
                        //Next version, need to install addon: http://bbpress.org/plugins/topic/bbpress-moderation-suite/
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    //no reason yet, add addon
                    return "noreason";
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    //no date yet, add addon
                    return "nodate";
                }
            }
        } else if (script.equalsIgnoreCase(DLE.Name) || script.equalsIgnoreCase(DLE.ShortName)) {
            if (Util.checkVersionInRange(DLE.VersionRange)) {
                if (what.equalsIgnoreCase("getgroup")) {
                    GroupID = MySQL.getfromtable(Config.script_tableprefix + "users", "`users_group`", "name",player.getName());
                    GroupName = MySQL.getfromtable(Config.script_tableprefix + "usergroups", "`group_name`", "id", GroupID);
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banned", "`date`", "ip",extra);
                        if (IsBanned.equalsIgnoreCase("fail")) {
                             String delimiter = "\\.";
                             StringBuffer tempIP = new StringBuffer();
                             String[] temp = extra.split(delimiter);
                             int counter = 0;
                             while (counter > (temp.length - 1)) {
                                 tempIP.append(temp[counter] + ".");
                                 counter++;
                             }
                             tempIP.append("*");
                             IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banned", "`date`", "ip",tempIP.toString());
                             if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        } else { return "true"; }
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`user_id`", "name",player.getName());
                        IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banned", "`date`", "users_id",UserID);
                        if (IsBanned.equalsIgnoreCase("fail")) { return "false"; }
                        else { return "true"; }
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    if (player == null) {
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "banned", "`descr`", "ip",extra);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        else {
                             String delimiter = "\\.";
                             StringBuffer tempIP = new StringBuffer();
                             String[] temp = extra.split(delimiter);
                             int counter = 0;
                             while (counter > (temp.length - 1)) {
                                 tempIP.append(temp[counter] + ".");
                                 counter++;
                             }
                             tempIP.append("*");
                             IsBanned = MySQL.getfromtable(Config.script_tableprefix + "banned", "`date`", "ip",tempIP.toString());
                             if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        }
                        return "noreason";
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`user_id`", "name",player.getName());
                        BanReason = MySQL.getfromtable(Config.script_tableprefix + "banned", "`descr`", "users_id",UserID);
                        if (BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
                        else { return "noreason"; }
                    }
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    if (player == null) {
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "banned", "`days`", "ip",extra);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("0") || BannedToDate.equalsIgnoreCase("NULL")) { return "perma"; }
                            else {
                                int StartUnix =Integer.parseInt(MySQL.getfromtable(Config.script_tableprefix + "banned", "`date`", "ip",extra));
                                StartUnix += Integer.parseInt(BannedToDate) * 86400;
                                return StartUnix + ",unix";
                            }
                        } else { return "nodate"; }
                    } else {
                        UserID = MySQL.getfromtable(Config.script_tableprefix + "users", "`user_id`", "name",player.getName());
                        BannedToDate = MySQL.getfromtable(Config.script_tableprefix + "banned", "`days`", "users_id",UserID);
                        if (BannedToDate != "fail") {
                            if (BannedToDate == null || BannedToDate.equalsIgnoreCase("0") || BannedToDate.equalsIgnoreCase("NULL")) { return "perma"; }
                            else {
                                int StartUnix =Integer.parseInt(MySQL.getfromtable(Config.script_tableprefix + "banned", "`date`", "users_id",UserID));
                                StartUnix += Integer.parseInt(BannedToDate) * 86400;
                                return StartUnix + ",unix";
                            }
                        } else { return "nodate"; }
                    }
                }
            }
        } else if ((script.equalsIgnoreCase(IPB.Name) || script.equalsIgnoreCase(IPB.ShortName)) && Util.checkVersionInRange(IPB.VersionRange)) {
            if (what.equalsIgnoreCase("getgroup")) {
                //next version
                return "";
            } else if (what.equalsIgnoreCase("checkifbanned")) {
                if (player == null) {
                    //next version
                    return "";
                } else {
                    //next version
                    return "";
                }
            } else if (what.equalsIgnoreCase("banreason")) {
                if (player == null) {
                    //
                    return "";
                }
                //nothing yet
                return "noreason";
            } else if (what.equalsIgnoreCase("bannedtodate")) {
                if (player == null) {
                    //
                    return "";
                }
                //install IPB on local
                return "nodate";
            }
        }
		else if (script.equalsIgnoreCase(WordPress.Name) || script.equalsIgnoreCase(WordPress.ShortName)) {
            if (Util.checkVersionInRange(WordPress.VersionRange)) {
                if (what.equalsIgnoreCase("getgroup")) {
                    return "";
                    //next version: http://buddypress.org/
                } else if (what.equalsIgnoreCase("checkifbanned")) {
                    if (player == null) {
                        return "";
                        //Next version, need to install addon: http://bbpress.org/plugins/topic/bbpress-moderation-suite/
                    } else {
                        return "";
                        //Next version, need to install addon: http://bbpress.org/plugins/topic/bbpress-moderation-suite/
                    }
                } else if (what.equalsIgnoreCase("banreason")) {
                    //no reason yet, add addon
                    return "noreason";
                } else if (what.equalsIgnoreCase("bannedtodate")) {
                    //no date yet, add addon
                    return "nodate";
                }
            }
		}
        if (what.equalsIgnoreCase("getgroup")) {
            return GroupName.toLowerCase();
        }
        return "fail";
    }

}