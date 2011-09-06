/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

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
