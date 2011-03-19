package com.gmail.contexmoh.authdb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;


public class MySQL
{
	public static Connection mysql = null;
	
	public static void close() { if (mysql != null) try { mysql.close(); } catch (SQLException localSQLException) { } }
	
	public static void connect() throws ClassNotFoundException, SQLException
	{
		Class.forName(Utils.ToDriver(Config.database_driver));
		if(Config.debug_enable)
		{
			Utils.Debug("Lauching function: connect()");
			Utils.Debug("MySQL: "+Config.dbDb);
			Utils.Debug("MySQL driver: "+Config.database_driver);
			Utils.Debug("MySQL username: "+Config.database_username);
			Utils.Debug("MySQL password: "+Config.database_password);
			Utils.Debug("MySQL host: "+Config.database_host);
			Utils.Debug("MySQL port: "+Config.database_port);
			Utils.Debug("MySQL database: "+Config.database_database);
			Utils.Debug("MySQL prefix: "+Config.database_prefix);
		}
		
		if(Config.debug_enable) Utils.Debug("MySQL: "+Config.dbDb + "?autoReconnect=true&user=" + Config.database_username + "&password=" + Config.database_password);
		//mysql = DriverManager.getConnection(Config.dbDb + "?autoReconnect=true&user=" + Config.database_username + "&password=" + Config.database_password);
        //Class.forName("com.mysql.jdbc.Driver");
        mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
        mysql.setAutoCommit(true);
		PreparedStatement ps = null;
		if(Config.script_name.equals(Config.script_name1)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"users"+"`"); }
		else if(Config.script_name.equals(Config.script_name2)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"members"+"`"); }
		else if(Config.script_name.equals(Config.script_name3)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"members"+"`"); }
		else if(Config.script_name.equals(Config.script_name4)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"users"+"`"); }
		else if(Config.script_name.equals(Config.script_name5)) { ps = (PreparedStatement) mysql.prepareStatement("SELECT COUNT(*) as `countit` FROM `"+Config.database_prefix+"user"+"`"); }
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