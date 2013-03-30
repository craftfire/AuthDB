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
package com.craftfire.util.general;

public class GeneralUtil {
    public String toDriver(String dataname) {
        dataname = dataname.toLowerCase();
        if (dataname.equalsIgnoreCase("mysql")) {
            return "com.mysql.jdbc.Driver";
        } else if (dataname.equalsIgnoreCase("ebean")) {
            return "ebean";
        }

        return "com.mysql.jdbc.Driver";
    }
}
