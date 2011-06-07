/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

**/
package com.authdb.util.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.mysql.jdbc.Blob;


public class MySQL
{
	public static Connection mysql = null;
	
	public static boolean check()
	{
		try {
			mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
		} catch (SQLException e) {
			Util.Log("warning", "MYSQL CANNOT CONNECT!!!");
			AuthDB.Server.broadcastMessage("");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void close() { if (mysql != null) try { mysql.close(); } catch (SQLException localSQLException) { } }
	
	public static void connect()
	{
		try {
			Class.forName(Util.ToDriver(Config.database_driver));
		} catch (ClassNotFoundException e) {
			Config.database_ison = false;
			Util.Log("warning", "CANNOT FIND DATABASE DRIVER!!!");
			Messages.SendMessage("AuthDB_message_database_failure", null, null);
			e.printStackTrace();
		}
		
		if(Config.debug_enable)
		{
			Util.Debug("Lauching function: connect()");
			Util.Debug("MySQL: "+Config.dbDb);
			Util.Debug("MySQL driver: "+Config.database_driver);
			Util.Debug("MySQL username: "+Config.database_username);
			Util.Debug("MySQL password: "+Config.database_password);
			Util.Debug("MySQL host: "+Config.database_host);
			Util.Debug("MySQL port: "+Config.database_port);
			Util.Debug("MySQL database: "+Config.database_database);
			Util.Debug("MySQL prefix: "+Config.database_prefix);
		}
		
		if(Config.debug_enable) Util.Debug("MySQL: "+Config.dbDb + "?user=" + Config.database_username + "&password=" + Config.database_password);
		try {
			Config.database_ison = true;
			
			mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
		} catch (SQLException e) {
			Config.database_ison = false;
			Util.Log("warning", "MYSQL CANNOT CONNECT!!!");
			Messages.SendMessage("AuthDB_message_database_failure", null, null);
			e.printStackTrace();
		}
	}
	
	public static int countitall(String table) throws SQLException
	{
		String query = "SELECT LAST_INSERT_ID() FROM `"+table+"` LIMIT 1";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		int dupe = 0;
		if (rs.next()) { dupe = rs.getInt(1); }
		return dupe;
	}
	
	public static String getfromtable(String table,String column1,String column2, String column3, String value, String value2) throws SQLException
	{
		String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"' AND `"+column3+"` LIKE '%"+value2+"'%";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		String dupe = "fail";
		if (rs.next()) { dupe = rs.getString(1); }
		return dupe;
	}
	
	public static String getfromtable2(String table,String column1,String column2, String column3, String value, String value2) throws SQLException
	{
		String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"' AND `"+column3+"` = '"+value2+"'";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		String dupe = "fail";
		if (rs.next()) { dupe = rs.getString(1); }
		return dupe;
	}
	
	public static String getfromtablelike(String table,String column1,String column2, String column3, String value, String value2) throws SQLException
	{
		String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"' AND `"+column3+"` LIKE '%"+value2+"'%";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		String dupe = "fail";
		if (rs.next()) { dupe = rs.getString(1); }
		return dupe;
	}
	
	public static String getfromtable(String table,String column1,String column2,String value) throws SQLException
	{
		String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"'";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		String dupe = "fail";
		if (rs.next()) { dupe = rs.getString(1); }
		return dupe;
	}
	
	public static String Unix_Timestamp() throws SQLException
	{
		String query = "SELECT UNIX_TIMESTAMP()";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		String dupe = "fail";
		if (rs.next()) { dupe = rs.getString(1); }
		return dupe;
	}
	
	public static Blob getfromtableBlob(String table,String column1,String column2,String value) throws SQLException
	{
		String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"'";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		Blob dupe = null;
		if (rs.next()) { dupe = (Blob) rs.getBlob(1); }
		return dupe;
	}
}