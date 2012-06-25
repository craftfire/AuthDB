/*
 * This file is part of AuthDB Legacy.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * AuthDB Legacy is licensed under the GNU Lesser General Public License.
 *
 * AuthDB Legacy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB Legacy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.authdb.util.managers;

import java.io.Serializable;

import org.bukkit.entity.Player;

public class AuthDBPlayer implements Serializable {
    private String name;
    private String linkedname;
    private String password;
    private String email;
    private String ip;

    public AuthDBPlayer(Player player) {
        this.name = player.getName();
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkedName() {
        return this.linkedname;
    }

    public void setLinkedName(String linkedname) {
        this.linkedname = linkedname;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIP() {
        return this.ip;
    }

    public void setIP(Player player) {
        this.ip = player.getAddress().getAddress().toString().substring(1);
    }
}