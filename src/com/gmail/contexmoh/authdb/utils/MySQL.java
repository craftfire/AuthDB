package com.gmail.contexmoh.authdb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gmail.contexmoh.authdb.AuthDB;
import com.mysql.jdbc.PreparedStatement;


public class MySQL
{
	public static Connection mysql = null;
	
	public static void close() { if (mysql != null) try { mysql.close(); } catch (SQLException localSQLException) { } }
	
	public static void connect() throws ClassNotFoundException, SQLException
	{
		Class.forName(Config.database_driver);
		mysql = DriverManager.getConnection(Config.dbDb + "?autoReconnect=true&user=" + Config.database_username + "&password=" + Config.database_password);
		PreparedStatement ps = null;
		if(AuthDB.forumBoard.equals(AuthDB.forumBoard1)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"users"+"`"); }
		else if(AuthDB.forumBoard.equals(AuthDB.forumBoard2)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"members"+"`"); }
		else if(AuthDB.forumBoard.equals(AuthDB.forumBoard3)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"members"+"`"); }
		else if(AuthDB.forumBoard.equals(AuthDB.forumBoard4)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"users"+"`"); }
		else if(AuthDB.forumBoard.equals(AuthDB.forumBoard5)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"user"+"`"); }
		ResultSet rs = ps.executeQuery();
		if (rs.next()) { Utils.Log("info", rs.getInt("countit") + " user registrations in database"); }
	}
	
	public static int countitall(String table) throws SQLException
	{
		String query = "SELECT LAST_INSERT_ID() FROM `"+table+"` LIMIT 1";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		if (rs.next()) { return rs.getInt(1); }
		else { return 0; }
	}
	public static String getfromtable(String table,String column1,String column2,String value) throws SQLException
	{
		String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"'";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		if (rs.next()) { return rs.getString(1); }
		else { return "fail"; }
	}
}