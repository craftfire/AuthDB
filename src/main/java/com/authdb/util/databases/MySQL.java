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

import com.mysql.jdbc.Blob;

import com.authdb.util.Config;
import com.authdb.util.Messages;
import com.authdb.util.Util;
import com.authdb.util.Messages.Message;
import com.craftfire.util.managers.LoggingManager;

public class MySQL {
    static LoggingManager logging = new LoggingManager();
    public static Connection mysql = null;

    public static boolean check() {
        try {
            mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
        } catch (SQLException e) {
            if (Config.debug_enable) {
                logging.error("MYSQL CANNOT CONNECT!!!");
                Messages.sendMessage(Message.database_failure, null, null);
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                return false;
            } else {
                logging.error("Cannot connect to MySQL host: " + Config.database_host);
                logging.error("Access denied, check if the password/username is correct and that remote connection is enabled if the MySQL database is located on another host then your server.");
                Messages.sendMessage(Message.database_failure, null, null);
                return false;
            }
        }
        return true;
    }

    public static void close() {
        if (mysql != null) {
            try {
                mysql.close();
            } catch (SQLException localSQLException) {
                Util.logging.StackTrace(localSQLException.getStackTrace(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                Thread.currentThread().getStackTrace()[1].getClassName(),
                Thread.currentThread().getStackTrace()[1].getFileName());
            }
        }
    }

    public static void connect() {
        try {
            Class.forName(Util.toDriver(Config.database_type));
        } catch (ClassNotFoundException e) {
            Config.database_ison = false;
            logging.error("CANNOT FIND DATABASE DRIVER!!!");
            Messages.sendMessage(Message.database_failure, null, null);
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }

        if (Config.debug_enable) {
            Util.logging.Debug("Lauching function: connect()");
            Util.logging.Debug("MySQL: " + Config.dbDb);
            Util.logging.Debug("MySQL driver: " + Config.database_type);
            Util.logging.Debug("MySQL username: " + Config.database_username);
            Util.logging.Debug("MySQL password: " + Config.database_password);
            Util.logging.Debug("MySQL host: " + Config.database_host);
            Util.logging.Debug("MySQL port: " + Config.database_port);
            Util.logging.Debug("MySQL database: " + Config.database_database);
            if (!Config.custom_enabled) {
                Util.logging.Debug("MySQL prefix: " + Config.script_tableprefix);
            }
        }

        Util.logging.Debug("MySQL: " + Config.dbDb + "?user=" + Config.database_username + "&password=" + Config.database_password);
        try {
            Config.database_ison = true;
            mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
        } catch (SQLException e) {
            Config.database_ison = false;
            if (Config.debug_enable) {
                logging.error("MYSQL CANNOT CONNECT!!!");
                Messages.sendMessage(Message.database_failure, null, null);
                logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
            } else {
                logging.error("MySQL cannot connect to the specified host: " + Config.database_host);
                logging.error("Access denied, check if the password/username is correct and that remote connection is enabled if the MySQL database is located on another host then your server.");
                Messages.sendMessage(Message.database_failure, null, null);
            }
        }
    }

    public static int countitall(String table) throws SQLException {
        String query = "SELECT LAST_INSERT_ID() FROM `" + table + "` LIMIT 1";
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int dupe = 0;
        if (rs.next()) {
            dupe = rs.getInt(1);
        }
        stmt.close();
        return dupe;
    }

    public static void query(String query) throws SQLException {
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }
    
    public static String getQuery(String query) {
        Util.logging.mySQL(query);
        String dupe = "fail";
        Statement stmt;
        try {
            stmt = mysql.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                dupe = rs.getString(1);
            }
            stmt.close();
        } catch (SQLException e) {
            return "fail";
        }
        return dupe;
    }

    public static String getfromtable(String table,String column1,String column2, String column3, String value, String value2) throws SQLException {
        String query = "SELECT " + column1 + " FROM `" + table + "` WHERE `" + column2 + "` = '" + value + "' AND `" + column3 + "` LIKE '%" + value2 + "'% LIMIT 1";
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) {
            dupe = rs.getString(1);
        }
        stmt.close();
        return dupe;
    }

    public static String getfromtable2(String table,String column1,String column2, String column3, String value, String value2) throws SQLException {
        String query = "SELECT " + column1 + " FROM `" + table + "` WHERE `" + column2 + "` = '" + value + "' AND `" + column3 + "` = '" + value2 + "' LIMIT 1";
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) {
            dupe = rs.getString(1);
        }
        stmt.close();
        return dupe;
    }

    public static String getfromtablelike(String table,String column1,String column2, String column3, String value, String value2) throws SQLException {
        String query = "SELECT " + column1 + " FROM `" + table + "` WHERE `" + column2 + "` = '" + value + "' AND `" + column3 + "` LIKE '%" + value2 + "'% LIMIT 1";
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) {
            dupe = rs.getString(1);
        }
        stmt.close();
        return dupe;
    }

    public static String getfromtable(String table,String column1,String column2,String value) throws SQLException {
        String query = "SELECT " + column1 + " FROM `" + table + "` WHERE `" + column2 + "` = '" + value + "' LIMIT 1";
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) {
            dupe = rs.getString(1);
        }
        stmt.close();
        return dupe;
    }

    public static String Unix_Timestamp() throws SQLException {
        String query = "SELECT UNIX_TIMESTAMP()";
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        String dupe = "fail";
        if (rs.next()) {
            dupe = rs.getString(1);
        }
        stmt.close();
        return dupe;
    }

    public static Blob getfromtableBlob(String table,String column1,String column2,String value) throws SQLException {
        String query = "SELECT " + column1 + " FROM `" + table + "` WHERE `" + column2 + "` = '" + value + "' LIMIT 1";
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        Blob dupe = null;
        if (rs.next()) {
            dupe = (Blob) rs.getBlob(1);
        }
        stmt.close();
        return dupe;
    }
}
