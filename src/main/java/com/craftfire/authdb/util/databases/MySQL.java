/*
 * This file is part of AuthDB.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
 * AuthDB is licensed under the GNU Lesser General Public License.
 *
 * AuthDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.authdb.util.databases;

import java.sql.*;

import com.mysql.jdbc.Blob;

import com.craftfire.authdb.util.Config;
import com.craftfire.authdb.util.Messages;
import com.craftfire.authdb.util.Messages.Message;
import com.craftfire.authdb.util.Util;
import com.craftfire.util.managers.LoggingManager;

public class MySQL {
    static LoggingManager logging = new LoggingManager();
    public static Connection mysql = null;
    public static boolean isConnected = false;

    public static boolean check() {
        try {
            mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
        } catch (SQLException e) {
            if (Config.debug_enable) {
                logging.error("MYSQL CANNOT CONNECT!!!");
                Messages.sendMessage(Message.database_failure, null, null);
                Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
                isConnected = false;
                return false;
            } else {
                logging.error("Cannot connect to MySQL host: " + Config.database_host);
                logging.error("Access denied, check if the password/username is correct and that remote connection is enabled if the MySQL database is located on another host then your server.");
                Messages.sendMessage(Message.database_failure, null, null);
                isConnected = false;
                return false;
            }
        }
        isConnected = true;
        return true;
    }

    public static boolean isConnected() {
        if (Config.database_keepalive) {
            if (isConnected) {
                return true;
            }
            return false;
        } else {
            try {
                mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
            } catch (SQLException e) {
                return false;
            }
            isConnected = true;
            return true;
        }
    }

    public static void close() {
        if (mysql != null) {
            try {
                isConnected = false;
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
            Class.forName(Util.gUtil.toDriver(Config.database_type));
        } catch (ClassNotFoundException e) {
            Config.database_ison = false;
            logging.error("CANNOT FIND DATABASE DRIVER!!!");
            Messages.sendMessage(Message.database_failure, null, null);
            isConnected = false;
            Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
            return;
        }

        if (Config.debug_enable) {
            Util.logging.debug("Lauching function: connect()");
            Util.logging.debug("MySQL: " + Config.dbDb);
            Util.logging.debug("MySQL driver: " + Config.database_type);
            Util.logging.debug("MySQL username: " + Config.database_username);
            Util.logging.debug("MySQL password: " + Config.database_password);
            Util.logging.debug("MySQL host: " + Config.database_host);
            Util.logging.debug("MySQL port: " + Config.database_port);
            Util.logging.debug("MySQL database: " + Config.database_database);
            if (!Config.custom_enabled) {
                Util.logging.debug("MySQL prefix: " + Config.script_tableprefix);
            }
        }

        Util.logging.debug("MySQL: " + Config.dbDb + "?user=" + Config.database_username + "&password=" + Config.database_password);
        try {
            Config.database_ison = true;
            mysql = DriverManager.getConnection(Config.dbDb, Config.database_username, Config.database_password);
        } catch (SQLException e) {
            Config.database_ison = false;
            if (Config.debug_enable) {
                logging.error("MYSQL CANNOT CONNECT!!!");
                Messages.sendMessage(Message.database_failure, null, null);
                isConnected = false;
                logging.StackTrace(e.getStackTrace(),Thread.currentThread().getStackTrace()[1].getMethodName(),Thread.currentThread().getStackTrace()[1].getLineNumber(),Thread.currentThread().getStackTrace()[1].getClassName(),Thread.currentThread().getStackTrace()[1].getFileName());
                return;
            } else {
                logging.error("Cannot connect to MySQL server on " + Config.database_host + "!");
                logging.error("Access denied, check if the password/username is correct and that remote connection is enabled if the MySQL database is located on another host then your server.");
                isConnected = false;
                Messages.sendMessage(Message.database_failure, null, null);
                return;
            }
        }
        isConnected = true;
    }

    public static int getLastID(String table, String where, String field) throws SQLException {
        String query = "SELECT `" + field + "` FROM `" + table + "` WHERE " + where + " ORDER BY `" + field + "` DESC LIMIT 1";
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int dupe = 0;
        if (rs.next()) {
            dupe = rs.getInt(1);
        }
        rs.close();
        stmt.close();
        return dupe;
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
        rs.close();
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
        if (isConnected()) {
            String dupe = "fail";
            Statement stmt;
            try {
                stmt = mysql.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    dupe = rs.getString(1);
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                return "fail";
            }
            return dupe;
        }
        return "fail";
    }

    public static String getfromtable(String table,String column1,String column2, String column3, String value, String value2) throws SQLException {
        String query = "SELECT " + column1 + " FROM `" + table + "` WHERE `" + column2 + "` = '" + value + "' AND `" + column3 + "` LIKE '%" + value2 + "'% LIMIT 1";
        return getQuery(query);
    }

    public static String getfromtable2(String table,String column1,String column2, String column3, String value, String value2) throws SQLException {
        String query = "SELECT " + column1 + " FROM `" + table + "` WHERE `" + column2 + "` = '" + value + "' AND `" + column3 + "` = '" + value2 + "' LIMIT 1";
        return getQuery(query);
    }

    public static String getfromtablelike(String table,String column1,String column2, String column3, String value, String value2) throws SQLException {
        String query = "SELECT " + column1 + " FROM `" + table + "` WHERE `" + column2 + "` = '" + value + "' AND `" + column3 + "` LIKE '%" + value2 + "'% LIMIT 1";
        return getQuery(query);
    }

    public static String getfromtable(String table,String column1,String column2,String value) throws SQLException {
        String query = "SELECT " + column1 + " FROM `" + table + "` WHERE `" + column2 + "` = '" + value + "' LIMIT 1";
        return getQuery(query);
    }

    public static Long getUnixTimestamp() throws SQLException {
        String query = "SELECT UNIX_TIMESTAMP()";
        Util.logging.mySQL(query);
        Statement stmt = mysql.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        long dupe = 0;
        if (rs.next()) {
            dupe = rs.getLong(1);
        }
        rs.close();
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
        rs.close();
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
        rs.close();
        stmt.close();
        return dupe;
    }
}
