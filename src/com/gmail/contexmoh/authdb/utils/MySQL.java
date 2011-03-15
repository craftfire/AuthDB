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
	public static boolean dbPostTopic = AuthDB.Config.getBoolean("settings.post-topic", true);
	public static String dbDriver =  AuthDB.Config.getString("database.driver", "com.mysql.jdbc.Driver");
	public static String dbUsername =  AuthDB.Config.getString("database.username", "root");
	public static String dbPassword =  AuthDB.Config.getString("database.password", "");
	public static String dbPort =  AuthDB.Config.getString("database.port", "3306");
	public static String dbHost =  AuthDB.Config.getString("database.host", "localhost");
	public static String dbDatabase = AuthDB.Config.getString("database.database", "minecraft_forum");
	public static String forumPrefix = AuthDB.Config.getString("database.prefix", "");
	public static String dbDb = "jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbDatabase;
	
	public static Connection mysql = null;
	
	public static void close() { if (mysql != null) try { mysql.close(); } catch (SQLException localSQLException) { } }
	
	public static void connect() throws ClassNotFoundException, SQLException
	{
		Class.forName(dbDriver);
		mysql = DriverManager.getConnection(dbDb + "?autoReconnect=true&user=" + dbUsername + "&password=" + dbPassword);
		PreparedStatement ps = null;
		if(AuthDB.forumBoard.equals(AuthDB.forumBoard1)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+forumPrefix+"users"+"`"); }
		else if(AuthDB.forumBoard.equals(AuthDB.forumBoard2)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+forumPrefix+"members"+"`"); }
		else if(AuthDB.forumBoard.equals(AuthDB.forumBoard3)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+forumPrefix+"members"+"`"); }
		else if(AuthDB.forumBoard.equals(AuthDB.forumBoard4)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+forumPrefix+"users"+"`"); }
		else if(AuthDB.forumBoard.equals(AuthDB.forumBoard5)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+forumPrefix+"user"+"`"); }
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