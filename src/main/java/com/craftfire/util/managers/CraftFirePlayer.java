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
package com.craftfire.util.managers;

import org.bukkit.entity.Player;

public abstract interface CraftFirePlayer extends Player {
    public abstract String getName();
    
    public abstract void setName(String name);

    public abstract String getLinkedName();

    public abstract void setLinkedName(String linkedname);

    public abstract String getPassword();

    public abstract void setPassword(String password);

    public abstract String getEmail();

    public abstract void setEmail(String email);

    public abstract String getIP();

    public abstract void setIP(String IP);
    
    public abstract boolean isRegistred();
}
