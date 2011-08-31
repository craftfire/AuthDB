/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.craftfire.util.managers;

import org.bukkit.entity.Player;

public class CraftFirePlayer {    

	private String name;
	private String linkedname;
	private String password;
	private String email;
	private String ip;
    
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