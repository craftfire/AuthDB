/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

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
import com.authdb.util.Messages.Message;
import com.craftfire.util.managers.LoggingManager;

import com.mysql.jdbc.Blob;

public class MySQL
{
    static LoggingManager Logging = new LoggingManager();
    public static Connection mysql = null;

    public static boolean check() {
        try {
            mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
        } catch (SQLException e) {
            if(Config.debug_enable) {
                Logging.Warning("MYSQL CANNOT CONNECT!!!");
                Messages.SendMessage(Message.database_failure, null, null);
                Util.Logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                return false;
            } else {
                Logging.Warning("Cannot connect to MySQL host: "+Config.database_host);
                Logging.Warning("Access denied, check if the password/username is correct and that remote connection is enabled if the MySQL database is located on another host then your server.");
                Messages.SendMessage(Message.database_failure, null, null);
                return false;
            }
        }
        return true;
    }

    public static void close() { if (mysql != null) try { mysql.close(); } catch (SQLException localSQLException) { } }

    public static void connect() {
        try {
            Class.forName(Util.ToDriver(Config.database_driver));
        } catch (ClassNotFoundException e) {
            Config.database_ison = false;
            Logging.Warning("CANNOT FIND DATABASE DRIVER!!!");
            Messages.SendMessage(Message.database_failure, null, null);
            Util.Logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }

        if(Config.debug_enable) {
            Util.Logging.Debug("Lauching function: connect()");
            Util.Logging.Debug("MySQL: "+Config.dbDb);
            Util.Logging.Debug("MySQL driver: "+Config.database_driver);
            Util.Logging.Debug("MySQL username: "+Config.database_username);
            Util.Logging.Debug("MySQL password: "+Config.database_password);
            Util.Logging.Debug("MySQL host: "+Config.database_host);
            Util.Logging.Debug("MySQL port: "+Config.database_port);
            Util.Logging.Debug("MySQL database: "+Config.database_database);
            if(!Config.custom_enabled) {
                Util.Logging.Debug("MySQL prefix: "+Config.script_tableprefix);
            }
        }

        Util.Logging.Debug("MySQL: "+Config.dbDb+"?user="+Config.database_username+"&password="+Config.database_password);
        try {
            Config.database_ison = true;

            mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
        } catch (SQLException e) {
            Config.database_ison = false;
            if(Config.debug_enable) {
                Logging.Warning("MYSQL CANNOT CONNECT!!!");
                Messages.SendMessage(Message.database_failure, null, null);
                Logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
                //Util.Logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            } else {
                Logging.Warning("MySQL cannot connect to the specified host: "+Config.database_host);
                Logging.Warning("Acces denied, check if the password/username is correct and that remote connection is enabled if the MySQL database is located on another host then your server.");
                Messages.SendMessage(Message.database_failure, null, null);
            }
        }
    }

    public static int countitall(String table) throws SQLException {
        String query = "SELECT LAST_INSERT_ID() FROM `"+table+"` LIMIT 1";
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int dupe = 0;
        if (rs.next()) { 
            dupe = rs.getInt(1); 
        }
        return dupe;
    }

    public static void query(String query) throws SQLException {
        Statement stmt = mysql.createStatement();
        stmt.executeUpdate(query);
    }

    public static String getfromtable(String table,String column1,String column2, String column3, String value, String value2) throws SQLException {
        String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"' AND `"+column3+"` LIKE '%"+value2+"'% LIMIT 1";
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) { 
            dupe = rs.getString(1); 
        }
        return dupe;
    }

    public static String getfromtable2(String table,String column1,String column2, String column3, String value, String value2) throws SQLException {
        String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"' AND `"+column3+"` = '"+value2+"' LIMIT 1";
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) { 
            dupe = rs.getString(1); 
        }
        return dupe;
    }

    public static String getfromtablelike(String table,String column1,String column2, String column3, String value, String value2) throws SQLException {
        String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"' AND `"+column3+"` LIKE '%"+value2+"'% LIMIT 1";
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) { 
            dupe = rs.getString(1);
        }
        return dupe;
    }

    public static String getfromtable(String table,String column1,String column2,String value) throws SQLException {
        String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"' LIMIT 1";
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) { 
            dupe = rs.getString(1); 
        }
        return dupe;
    }

    public static String Unix_Timestamp() throws SQLException {
        String query = "SELECT UNIX_TIMESTAMP()";
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) { 
            dupe = rs.getString(1); 
        }
        return dupe;
    }

    public static Blob getfromtableBlob(String table,String column1,String column2,String value) throws SQLException {
        String query = "SELECT "+column1+" FROM `"+table+"` WHERE `"+column2+"` = '"+value+"' LIMIT 1";
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        Blob dupe = null;
        if (rs.next()) { 
            dupe = (Blob) rs.getBlob(1); 
        }
        return dupe;
    }
}
