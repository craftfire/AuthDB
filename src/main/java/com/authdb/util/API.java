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
import com.authdb.scripts.forum.IPB;
import com.authdb.scripts.forum.PunBB;
import com.authdb.scripts.forum.SMF;
import com.authdb.scripts.forum.Vanilla;
import com.authdb.scripts.forum.XenForo;
import com.authdb.scripts.forum.bbPress;
import com.authdb.scripts.forum.MyBB;
import com.authdb.scripts.forum.phpBB;
import com.authdb.scripts.forum.vBulletin;
import com.authdb.util.databases.MySQL;

public class API {
	public static String GetScript(String what, Player player, String extra) throws SQLException
    {
    	String script = Config.script_name;
		String GroupName = "fail";
		String GroupID = "0";
		String UserID = "0";
		String IsBanned = "";
		String BanReason = "";
		String BannedToDate = "";
	    if(Config.custom_enabled)
	    {
	    }
	    else if(script.equals(phpBB.Name) || script.equals(phpBB.ShortName))
		{
			if(Util.CheckVersionInRange(phpBB.VersionRange))
	    	{
				//phpbb3
				if(what.equals("getgroup"))
				{
					GroupID = MySQL.getfromtable(Config.database_prefix+"users", "`group_id`", "username",player.getName());
					GroupName = MySQL.getfromtable(Config.database_prefix+"groups", "`group_name`", "group_id", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"banlist", "`ban_id`", "ban_ip",extra);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`user_id`", "username_clean",player.getName().toLowerCase());
						IsBanned = MySQL.getfromtable(Config.database_prefix+"banlist", "`ban_id`", "ban_userid",UserID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"banlist", "`ban_reason`", "ban_ip",extra);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`users_id`", "username_clean",player.getName().toLowerCase());
						BanReason = MySQL.getfromtable(Config.database_prefix+"banlist", "`ban_reason`", "ban_userid",UserID);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"banlist", "`ban_end`", "ban_ip",extra);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("NULL") || BannedToDate.equals("0")) { return "perma"; }
							else { return BanReason+",unix"; }
						}
						else { return "nodate"; }
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`user_id`", "username_clean",player.getName().toLowerCase());
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"banlist", "`ban_end`", "ban_userid",UserID);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("NULL") || BannedToDate.equals("0")) { return "perma"; }
							else { return BanReason+",unix"; }
						}
						else { return "nodate"; }
					}
				}
	    	}
			else if(Util.CheckVersionInRange(phpBB.VersionRange2))
	    	{
				//phpbb2
				if(what.equals("getgroup"))
				{
					UserID = MySQL.getfromtable(Config.database_prefix+"users", "`users_id`", "username",player.getName());
					GroupID = MySQL.getfromtable(Config.database_prefix+"users_group", "`group_id`", "user_id",UserID);
					GroupName = MySQL.getfromtable(Config.database_prefix+"groups", "`group_name`", "group_id", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						extra = Util.hexToString(extra);
						IsBanned = MySQL.getfromtable(Config.database_prefix+"banlist", "`ban_id`", "ban_ip", extra);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`user_id`", "username",player.getName());
						IsBanned = MySQL.getfromtable(Config.database_prefix+"banlist", "`ban_id`", "ban_userid",UserID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					
				}
				else if(what.equals("banreason"))
				{
					//no ban reason defined
					return "noreason";
				}
				else if(what.equals("bannedtodate"))
				{
					//no date
					return "nodate";
				}
	    	}
		}
	    else if(script.equals(SMF.Name) || script.equals(SMF.ShortName))
		{
			if(Util.CheckVersionInRange(SMF.VersionRange))
	    	{
				//smf1
				if(what.equals("getgroup"))
				{
					GroupID = MySQL.getfromtable(Config.database_prefix+"members", "`ID_GROUP`", "memberName",player.getName());
					if(UserID.equals("0"))
					{
						GroupID = MySQL.getfromtable(Config.database_prefix+"members", "`ID_POST_GROUP`", "memberName",player.getName());
					}
					GroupName = MySQL.getfromtable(Config.database_prefix+"membersgroups", "`groupName`", "ID_GROUP", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					//next version, need to check if between 0 and 255.
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						//
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"members", "`ID_MEMBER`", "memberName",player.getName());
						String BanGroup = MySQL.getfromtable(Config.database_prefix+"ban_items", "`ID_BAN_GROUP`", "ID_MEMBER",UserID);
						BanReason = MySQL.getfromtable(Config.database_prefix+"ban_groups", "`reason`", "ID_BAN_GROUP",BanGroup);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						//
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"members", "`ID_MEMBER`", "memberName",player.getName());
						String BanGroup = MySQL.getfromtable(Config.database_prefix+"ban_items", "`ID_BAN_GROUP`", "ID_MEMBER",UserID);
						BanReason = MySQL.getfromtable(Config.database_prefix+"ban_groups", "`expire_time`", "ID_BAN_GROUP",BanGroup);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("NULL")) { return "perma"; }
							else { return BanReason+",unix"; }
						}
						else { return "nodate"; }
					}
				}
	    	}
			else if(Util.CheckVersionInRange(SMF.VersionRange2))
	    	{
				//smf2
				if(what.equals("getgroup"))
				{
					GroupID = MySQL.getfromtable(Config.database_prefix+"members", "`id_group`", "member_name",player.getName());
					if(UserID.equals("0"))
					{
						GroupID = MySQL.getfromtable(Config.database_prefix+"members", "`id_post_group`", "member_name",player.getName());
					}
					GroupName = MySQL.getfromtable(Config.database_prefix+"membersgroups", "`group_name`", "id_group", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						//next version, need to check if between 0 and 255.
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`user_id`", "username",player.getName());
						IsBanned = MySQL.getfromtable(Config.database_prefix+"ban_items", "`id_ban`", "id_member",UserID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						//
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"members", "`id_member`", "member_name",player.getName());
						String BanGroup = MySQL.getfromtable(Config.database_prefix+"ban_items", "`id_ban_group`", "id_member",UserID);
						BanReason = MySQL.getfromtable(Config.database_prefix+"ban_groups", "`reason`", "id_ban_group",BanGroup);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						//
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"members", "`id_member`", "member_name",player.getName());
						String BanGroup = MySQL.getfromtable(Config.database_prefix+"ban_items", "`id_ban_group`", "id_member",UserID);
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"ban_groups", "`expire_time`", "id_ban_group",BanGroup);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("NULL")) { return "perma"; }
							else { return BanReason+",unix"; }
						}
						else { return "nodate"; }
					}
				}
	    	}
		}
	    else if(script.equals(MyBB.Name) || script.equals(MyBB.ShortName))
		{
			if(Util.CheckVersionInRange(MyBB.VersionRange))
	    	{
				if(what.equals("getgroup"))
				{
					GroupID = MySQL.getfromtable(Config.database_prefix+"users", "`usersgroup`", "username",player.getName());
					GroupName = MySQL.getfromtable(Config.database_prefix+"usersgroups", "`title`", "gid", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"banfliters", "`fid`", "filter",extra);
						if(IsBanned.equals("fail")) 
						{
							 String delimiter = "\\.";
							 String tempIP = "";
							 String[] temp = extra.split(delimiter);
							 int counter = 0;
							 while(counter > (temp.length - 1))
							 {
								 tempIP += temp[counter]+".";
								 counter++;
							 }
							 tempIP += "*";
							 IsBanned = MySQL.getfromtable(Config.database_prefix+"banfliters", "`fid`", "filter",tempIP);
							 if(IsBanned.equals("fail")) { return "false"; }
						}
						return "true";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`uid`", "username",player.getName());
						IsBanned = MySQL.getfromtable(Config.database_prefix+"banned", "`dateline`", "uid",UserID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						//no reason
						return "noreason";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`uid`", "username",player.getName());
						BanReason = MySQL.getfromtable(Config.database_prefix+"banned", "`reason`", "uid", UserID);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						//no date
						return "nodate";
					}
					else
					{	
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`uid`", "username",player.getName());
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"banned", "`lifted`", "uid", UserID);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("0")) { return "perma"; }
							else { return BannedToDate+",unix"; }
						}
						else { return "nodate"; }
					}
				}
	    	}
		}
	    else if(script.equals(vBulletin.Name) || script.equals(vBulletin.ShortName))
		{
			if(Util.CheckVersionInRange(vBulletin.VersionRange))
	    	{
				//VB3
				if(what.equals("getgroup"))
				{
					GroupID = MySQL.getfromtable(Config.database_prefix+"user", "`usergroupid`", "username",player.getName());
					GroupName = MySQL.getfromtable(Config.database_prefix+"usergroup", "`title`", "usergroupid", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"setting", "`datatype`", "varname", "value", "banip", extra);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`userid`", "username",player.getName());
						IsBanned = MySQL.getfromtable(Config.database_prefix+"userban", "`bandate`", "userid",UserID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						return "noreason";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`userid`", "username",player.getName());
						BanReason = MySQL.getfromtable(Config.database_prefix+"userban", "`reason`", "userid",UserID);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						return "nodate";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`userid`", "username",player.getName());
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"userban", "`liftdate`", "userid",UserID);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("0")) { return "perma"; }
							else { return BanReason+",unix"; }
						}
						else { return "nodate"; }
					}
				}
	    	}
			else if(Util.CheckVersionInRange(vBulletin.VersionRange2))
	    	{
				//VB4
				if(what.equals("getgroup"))
				{
					GroupID = MySQL.getfromtable(Config.database_prefix+"user", "`usergroupid`", "username",player.getName());
					GroupName = MySQL.getfromtable(Config.database_prefix+"usergroup", "`title`", "usergroupid", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"setting", "`datatype`", "varname", "value", "banip", extra);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`userid`", "username",player.getName());
						IsBanned = MySQL.getfromtable(Config.database_prefix+"userban", "`bandate`", "userid",UserID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						return "noreason";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`userid`", "username",player.getName());
						BanReason = MySQL.getfromtable(Config.database_prefix+"userban", "`bandate`", "userid",UserID);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						return "nodate";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`userid`", "username",player.getName());
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"userban", "`liftdate`", "userid",UserID);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("0")) { return "perma"; }
							else { return BanReason+",unix"; }
						}
						else { return "nodate"; }
					}
				}
	    	}
		}
	    else if(script.equals(Drupal.Name) || script.equals(Drupal.ShortName))
		{
			if(Util.CheckVersionInRange(Drupal.VersionRange))
	    	{
				//drupal 6
				if(what.equals("getgroup"))
				{
					UserID = MySQL.getfromtable(Config.database_prefix+"users", "`uid`", "name",player.getName());
					GroupID = MySQL.getfromtable(Config.database_prefix+"users_roles", "`rid`", "uid",UserID);
					GroupName = MySQL.getfromtable(Config.database_prefix+"role", "`name`", "rid", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"access", "`type`", "mask",extra);
						if(IsBanned.equals("fail")) 
						{
							 String delimiter = "\\.";
							 String tempIP = "";
							 String[] temp = extra.split(delimiter);
							 int counter = 0;
							 while(counter > (temp.length - 1))
							 {
								 tempIP += temp[counter]+".";
								 counter++;
							 }
							 tempIP += "%";
							 IsBanned = MySQL.getfromtable(Config.database_prefix+"access", "`type`", "mask",tempIP);
							 if(IsBanned.equals("fail")) { return "false"; }
						}
						return "true";
					}
					else
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"access", "`type`", "mask",player.getName());
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					
				}
				else if(what.equals("banreason"))
				{
					//no ban reason on ban
					return "noreason";
				}
				else if(what.equals("bannedtodate"))
				{
					//no date set on ban, it's perma
					return "nodate";
				}
	    	}
			else if(Util.CheckVersionInRange(Drupal.VersionRange2))
	    	{
				//drupal 7
				if(what.equals("getgroup"))
				{
					UserID = MySQL.getfromtable(Config.database_prefix+"users", "`uid`", "name",player.getName());
					GroupID = MySQL.getfromtable(Config.database_prefix+"users_roles", "`rid`", "uid",UserID);
					GroupName = MySQL.getfromtable(Config.database_prefix+"role", "`name`", "rid", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"blocked_ips", "`iid`", "ip",extra);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					else
					{
						IsBanned = MySQL.getfromtable2(Config.database_prefix+"users", "`uid`", "name","status", player.getName(), "0");
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
				else if(what.equals("banreason"))
				{
					//no ban reason on ban
					return "noreason";
				}
				else if(what.equals("bannedtodate"))
				{
					//no date on ban, it's perma
					return "nodate";
				}
	    	}
			
		}
	    else if(script.equals(Joomla.Name) || script.equals(Joomla.ShortName))
		{
			if(Util.CheckVersionInRange(Joomla.VersionRange))
	    	{
				//1.5
				if(what.equals("getgroup"))
				{
					GroupID = MySQL.getfromtable(Config.database_prefix+"users", "`gid`", "username",player.getName());
					GroupName = MySQL.getfromtable(Config.database_prefix+"core_acl_aro_groups", "`name`", "id", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						//String BanID = MySQL.getfromtable(Config.database_prefix+"plugins", "`params`", "name", "System - Ban IP Address");
						IsBanned = MySQL.getfromtablelike(Config.database_prefix+"plugins", "`name`", "element","params", "ban", extra);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					else
					{
						return "false";
					}
				}
				else if(what.equals("banreason"))
				{
					//no reason yet on ban
					return "noreason";
				}
				else if(what.equals("bannedtodate"))
				{
					//extension needed
					return "nodate";
				}
	    	}
			else if(Util.CheckVersionInRange(Joomla.VersionRange2))
	    	{
				//1.6
				if(what.equals("getgroup"))
				{
					UserID = MySQL.getfromtable(Config.database_prefix+"users", "`id`", "username",player.getName());
					GroupID = MySQL.getfromtable(Config.database_prefix+"user_usergroup_map", "`group_id`", "user_id",UserID);
					GroupName = MySQL.getfromtable(Config.database_prefix+"usergroups", "`title`", "id", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					//Not built in, need a extension.
				}
				else if(what.equals("banreason"))
				{
					//no reason yet on ban
					return "noreason";
				}
				else if(what.equals("bannedtodate"))
				{
					//no date yet
					return "nodate";
				}
	    	}
		}
	    else if(script.equals(Vanilla.Name) || script.equals(Vanilla.ShortName))
		{
	    	if(Util.CheckVersionInRange(Vanilla.VersionRange))
	    	{
	    		if(what.equals("getgroup"))
				{
					UserID = MySQL.getfromtable(Config.database_prefix+"user", "`UserID`", "Name",player.getName());
					GroupID = MySQL.getfromtable(Config.database_prefix+"userrole", "`RoleID`", "UserID",UserID);
					GroupName = MySQL.getfromtable(Config.database_prefix+"role", "`Name`", "RoleID", GroupID);
				}
	    		else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						//find addon
					}
					else
					{
						String BanID = MySQL.getfromtable(Config.database_prefix+"role", "`RoleID`", "Name", "Banned");
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`UserID`", "Name",player.getName());
						IsBanned = MySQL.getfromtable2(Config.database_prefix+"userrole", "`UserID`", "UserID","RoleID", UserID, BanID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
	    		else if(what.equals("banreason"))
				{
	    			//no reason on ban
					return "noreason";
				}
	    		else if(what.equals("bannedtodate"))
				{
	    			//no date on ban, just a group
	    			return "nodate";
				}
	    		
	    	}
			else if(Util.CheckVersionInRange(Vanilla.VersionRange2))
	    	{
				if(what.equals("getgroup"))
				{
					UserID = MySQL.getfromtable(Config.database_prefix+"user", "`UserID`", "Name",player.getName());
					GroupID = MySQL.getfromtable(Config.database_prefix+"userrole", "`RoleID`", "UserID",UserID);
					GroupName = MySQL.getfromtable(Config.database_prefix+"role", "`Name`", "RoleID", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						//find addon
					}
					else
					{
						String BanID = MySQL.getfromtable(Config.database_prefix+"role", "`RoleID`", "Name", "Banned");
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`UserID`", "Name",player.getName());
						IsBanned = MySQL.getfromtable2(Config.database_prefix+"userrole", "`UserID`", "UserID","RoleID", UserID, BanID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
				else if(what.equals("banreason"))
				{
					//noreason on ban given
					return "noreason";
				}
				else if(what.equals("bannedtodate"))
				{
					//no date on ban, just a group
					return "nodate";
				}
	    	}
		}
	    else if(script.equals(PunBB.Name) || script.equals(PunBB.ShortName))
		{
			if(Util.CheckVersionInRange(PunBB.VersionRange))
	    	{
				if(what.equals("getgroup"))
				{
					GroupID = MySQL.getfromtable(Config.database_prefix+"users", "`group_id`", "username",player.getName());
					GroupName = MySQL.getfromtable(Config.database_prefix+"groups", "`g_title`", "g_id", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"bans", "`ban_creator`", "ip", extra);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
					else
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"bans", "`ban_creator`", "username", player.getName());
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						BanReason = MySQL.getfromtable(Config.database_prefix+"bans", "`message`", "ip", extra);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
					else
					{
						BanReason = MySQL.getfromtable(Config.database_prefix+"bans", "`message`", "username", player.getName());
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"bans", "`expire`", "ip", extra);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("0") || BannedToDate.equals("NULL")) { return "perma"; }
							else { return BanReason+",unix"; }
						}
						else { return "nodate"; }
					}
					else
					{
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"bans", "`expire`", "username", player.getName());
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("0") || BannedToDate.equals("NULL")) { return "perma"; }
							else { return BanReason+",unix"; }
						}
						else { return "nodate"; }
					}
				}
	    	}
		}
	    else if(script.equals(XenForo.Name) || script.equals(XenForo.ShortName))
		{
			if(Util.CheckVersionInRange(XenForo.VersionRange))
	    	{
				if(what.equals("getgroup"))
				{
					//next version
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"ip_match", "`match_type`", "ip",extra);
						if(IsBanned.equals("fail")) 
						{
							 String delimiter = "\\.";
							 String tempIP = "";
							 String[] temp = extra.split(delimiter);
							 int counter = 0;
							 while(counter > (temp.length - 1))
							 {
								 tempIP += temp[counter]+".";
								 counter++;
							 }
							 tempIP += "*";
							 IsBanned = MySQL.getfromtable(Config.database_prefix+"ip_match", "`match_type`", "ip",tempIP);
							 if(IsBanned.equals("fail")) { return "false"; }
						}
						return "true";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`user_id`", "username",player.getName());
						IsBanned = MySQL.getfromtable(Config.database_prefix+"user_ban", "`ban_date`", "user_id",UserID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						return "noreason";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`user_id`", "username",player.getName());
						BanReason = MySQL.getfromtable(Config.database_prefix+"user_ban", "`user_reason`", "user_id",UserID);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						return "nodate";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"user", "`user_id`", "username",player.getName());
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"user_ban", "`end_date`", "user_id",UserID);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("0") || BannedToDate.equals("NULL")) { return "perma"; }
							else { return BanReason+",unix"; }
						}
						else { return "nodate"; }
					}
				}
	    	}
		}
	    else if(script.equals(bbPress.Name) || script.equals(bbPress.ShortName))
		{
			if(Util.CheckVersionInRange(bbPress.VersionRange))
	    	{
				if(what.equals("getgroup"))
				{
					//next version: http://buddypress.org/
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						//Next version, need to install addon: http://bbpress.org/plugins/topic/bbpress-moderation-suite/
					}
					else
					{
						//Next version, need to install addon: http://bbpress.org/plugins/topic/bbpress-moderation-suite/
					}
				}
				else if(what.equals("banreason"))
				{
					//no reason yet, add addon
					return "noreason";
				}
				else if(what.equals("bannedtodate"))
				{
					//no date yet, add addon
					return "nodate";
				}
	    	}
		}
	    else if(script.equals(DLE.Name) || script.equals(DLE.ShortName))
		{
			if(Util.CheckVersionInRange(DLE.VersionRange))
	    	{
				if(what.equals("getgroup"))
				{
					GroupID = MySQL.getfromtable(Config.database_prefix+"users", "`users_group`", "name",player.getName());
					GroupName = MySQL.getfromtable(Config.database_prefix+"usergroups", "`group_name`", "id", GroupID);
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						IsBanned = MySQL.getfromtable(Config.database_prefix+"banned", "`date`", "ip",extra);
						if(IsBanned.equals("fail")) 
						{
							 String delimiter = "\\.";
							 String tempIP = "";
							 String[] temp = extra.split(delimiter);
							 int counter = 0;
							 while(counter > (temp.length - 1))
							 {
								 tempIP += temp[counter]+".";
								 counter++;
							 }
							 tempIP += "*";
							 IsBanned = MySQL.getfromtable(Config.database_prefix+"banned", "`date`", "ip",tempIP);
							 if(IsBanned.equals("fail")) { return "false"; }
						}
						else { return "true"; }
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`user_id`", "name",player.getName());
						IsBanned = MySQL.getfromtable(Config.database_prefix+"banned", "`date`", "users_id",UserID);
						if(IsBanned.equals("fail")) { return "false"; }
						else { return "true"; }
					}
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						BanReason = MySQL.getfromtable(Config.database_prefix+"banned", "`descr`", "ip",extra);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else
						{
							 String delimiter = "\\.";
							 String tempIP = "";
							 String[] temp = extra.split(delimiter);
							 int counter = 0;
							 while(counter > (temp.length - 1))
							 {
								 tempIP += temp[counter]+".";
								 counter++;
							 }
							 tempIP += "*";
							 IsBanned = MySQL.getfromtable(Config.database_prefix+"banned", "`date`", "ip",tempIP);
							 if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						}
						return "noreason";
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`user_id`", "name",player.getName());
						BanReason = MySQL.getfromtable(Config.database_prefix+"banned", "`descr`", "users_id",UserID);
						if(BanReason != "fail" && BanReason != "" && BanReason != null) { return BanReason; }
						else { return "noreason"; }
					}
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"banned", "`days`", "ip",extra);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("0") || BannedToDate.equals("NULL")) { return "perma"; }
							else 
							{ 
								int StartUnix =Integer.parseInt( MySQL.getfromtable(Config.database_prefix+"banned", "`date`", "ip",extra));
								StartUnix += Integer.parseInt(BannedToDate) * 86400;
								return StartUnix+",unix";
							}
						}
						else { return "nodate"; }
					}
					else
					{
						UserID = MySQL.getfromtable(Config.database_prefix+"users", "`user_id`", "name",player.getName());
						BannedToDate = MySQL.getfromtable(Config.database_prefix+"banned", "`days`", "users_id",UserID);
						if(BannedToDate != "fail") 
						{ 
							if(BannedToDate == null || BannedToDate.equals("0") || BannedToDate.equals("NULL")) { return "perma"; }
							else 
							{ 
								int StartUnix =Integer.parseInt( MySQL.getfromtable(Config.database_prefix+"banned", "`date`", "users_id",UserID));
								StartUnix += Integer.parseInt(BannedToDate) * 86400;
								return StartUnix+",unix";
							}
						}
						else { return "nodate"; }
					}
				}
	    	}
		}
	    else if(script.equals(IPB.Name) || script.equals(IPB.ShortName))
		{
			if(Util.CheckVersionInRange(IPB.VersionRange))
	    	{
				if(what.equals("getgroup"))
				{
					//next version
				}
				else if(what.equals("checkifbanned"))
				{
					if(player == null)
					{
						//next version
					}
					else
					{
						//next version
					}
				}
				else if(what.equals("banreason"))
				{
					if(player == null)
					{
						//
					}
					else
					{
						
					}
					//nothing yet
					return "noreason";
				}
				else if(what.equals("bannedtodate"))
				{
					if(player == null)
					{
						//
					}
					else
					{
						
					}
					//install IPB on local
					return "nodate";
				}
	    	}
		}
	    if(what.equals("getgroup"))
		{
	    	return GroupName.toLowerCase();
		}
		return "fail";  
	}
}
