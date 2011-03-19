/**          © Copyright 2011 Contex <contexmoh@gmail.com>
	
	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
**/
package com.authdb.util.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import com.authdb.util.Config;
import com.authdb.util.Util;


public class MySQL
{
	public static Connection mysql = null;
	
	public static void close() { if (mysql != null) try { mysql.close(); } catch (SQLException localSQLException) { } }
	
	public static void connect() throws ClassNotFoundException, SQLException
	{
		Class.forName(Util.ToDriver(Config.database_driver));
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
		
		if(Config.debug_enable) Util.Debug("MySQL: "+Config.dbDb + "?autoReconnect=true&user=" + Config.database_username + "&password=" + Config.database_password);
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
		if (rs.next()) { Util.Log("info", rs.getInt("countit") + " user registrations in database"); }
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