package com.craftfire.util.general;

public class GeneralUtil {
    public String toDriver(String dataname) {
        dataname = dataname.toLowerCase();
        if (dataname.equalsIgnoreCase("mysql")) {
            return "com.mysql.jdbc.Driver";
        }
        else if (dataname.equalsIgnoreCase("ebean")) {
            return "ebean";
        }

        return "com.mysql.jdbc.Driver";
    }
    
}
