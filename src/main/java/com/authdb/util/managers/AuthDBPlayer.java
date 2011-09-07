

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