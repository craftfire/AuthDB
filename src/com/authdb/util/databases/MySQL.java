/**          (C) Copyright 2011 Contex <contexmoh@gmail.com>
	
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
	
	public static void connect() throws SQLException, ClassNotFoundException
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
		
		if(Config.debug_enable) Util.Debug("MySQL: "+Config.dbDb + "?user=" + Config.database_username + "&password=" + Config.database_password);
		mysql = DriverManager.getConnection(Config.dbDb + "?user=" + Config.database_username + "&password=" + Config.database_password);
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
	public static String getfromtable(String table,String column1,String column2,String value) throws SQLException
	{
		try {
			MySQL.connect();
		} catch (ClassNotFoundException e) {
			Util.Debug("Cannot connect to MySQL server:");
			e.printStackTrace();
		}
		String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"'";
		Statement stmt = mysql.createStatement();
		ResultSet rs = stmt.executeQuery( query );
		String dupe = "fail";
		if (rs.next()) { dupe = rs.getString(1); }
		close();
		return dupe;
	}
}