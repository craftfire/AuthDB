/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.util.databases;


import java.sql.SQLException;

import com.authdb.AuthDB;
import com.authdb.util.Config;
import com.authdb.util.Util;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Entity()
@Table(name = "authdb_users")
public class eBean {

    public static enum Column {
        playername ("playername"),
        linkednames ("linkednames"),
        password ("password"),
        email ("email"),
        salt ("salt"),
        inventory ("inventory"),
        armorinventory ("armorinventory"),
        activated ("activated"),
        authorized ("authorized"),
        timeout ("timeout"),
        reloadtime ("reloadtime"),
        ip ("ip");

        private String name;
        Column(String name) { this.name = name; }
    }
    
    public static eBean CheckPlayer(String player) {
        eBean eBeanClass = AuthDB.Database.find(eBean.class).where().ieq("playername", player).findUnique();
        if (eBeanClass == null)  {
            eBeanClass = new eBean();
            eBeanClass.setPlayername(player);
            save(eBeanClass);
        }
        return eBeanClass;
    }
    
    public static eBean CheckPlayer(Player player) {
        eBean eBeanClass = AuthDB.Database.find(eBean.class).where().ieq("playername", player.getName()).findUnique();
        if (eBeanClass == null)  {
            eBeanClass = new eBean();
            eBeanClass.setPlayer(player);
            save(eBeanClass);
        }
        return eBeanClass;
    }
    
    public static void save(eBean eBeanClass) {
        AuthDB.Database.save(eBeanClass);
    }
    
    public static void sync(Player player) {
        try  { 
            eBean eBeanClass = CheckPlayer(player.getName());
            String registred = eBeanClass.getRegistred();
            if(registred != null && registred.equalsIgnoreCase("true")) {
                Util.CheckScript("syncpassword", Config.script_name, player.getName(), null, null, null); 
                Util.CheckScript("syncsalt", Config.script_name, player.getName(), null, null, null);
            }
        } 
        catch (SQLException e) { e.printStackTrace(); }
    }
    
    public static void CheckPassword(String player, String password) {
        eBean eBeanClass = CheckPlayer(player);
        if(eBeanClass.getPassword() == null || eBeanClass.getPassword().equals(password) == false) {
            Util.Debug("Password in persistence is different than in MySQL, syncing password from MySQL.");
            eBeanClass.setPassword(password);
            AuthDB.Database.save(eBeanClass);
        }
    }
    
    public static void CheckSalt(String player, String salt) {
        eBean eBeanClass = CheckPlayer(player);
        if(eBeanClass.getSalt() == null || eBeanClass.getSalt().equals(salt) == false) {
            Util.Debug("Salt in persistence is different than in MySQL, syncing salt from MySQL.");
            eBeanClass.setSalt(salt);
            AuthDB.Database.save(eBeanClass);
        }
    }
    
    public static void CheckIP(String player, String IP) {
        eBean eBeanClass = CheckPlayer(player);
        if(eBeanClass.getIp() == null || eBeanClass.getIp().equals(IP) == false) {
            Util.Debug("IP in persistence is different than the player's IP, syncing IP's.");
            eBeanClass.setIp(IP);
            AuthDB.Database.save(eBeanClass);
        }
    }
    
    public static eBean find(String player) {
        eBean eBeanClass = CheckPlayer(player);
        eBeanClass = AuthDB.Database.find(eBean.class).where().ieq("playername", player).findUnique();
        return eBeanClass;
    }
    
    public static eBean find(Player player) {
        eBean eBeanClass = CheckPlayer(player);
        eBeanClass = AuthDB.Database.find(eBean.class).where().ieq("playername", player.getName()).findUnique();
        return eBeanClass;
    }
    
    public static eBean find(Player player, Column column1, String value1) {
        eBean eBeanClass = CheckPlayer(player);
        eBeanClass = AuthDB.Database.find(eBean.class).where().ieq("playername", player.getName()).ieq(column1.name,value1).findUnique();
        return eBeanClass;
    }
    
    public static boolean find(Player player, Column column1, String value1, Column column2, String value2) {
        eBean eBeanClass = CheckPlayer(player);
        eBeanClass = AuthDB.Database.find(eBean.class).where().ieq("playername", player.getName()).ieq(column1.name,value1).ieq(column2.name,value2).findUnique();
        if (eBeanClass != null) {
           return true;
        }
        return false;
    }
    
    @Id
    private int id;
    @NotNull
    private String playername;
    private String linkednames;
    private String password;
    private String salt;
    private String ip;
    private String email;
    private String inventory;
    private String armorinventory; 
    private String activated; 
    private String registred;
    private String authorized;
    private long timeout;
    private long reloadtime;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String ply) {
        this.playername = ply;
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(playername);
    }

    public String getEmail() {
       return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPlayer(Player player) {
        this.playername = player.getName();
    }

    public String getAuthorized(){
        return authorized;
    }

    public void setAuthorized(String authorized){
        this.authorized = authorized;
    }
    
    public long getReloadtime(){
        return reloadtime;
    }

    public void setReloadtime(long reloadtime){
        this.reloadtime = reloadtime;
    }
    
    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
    
    public String getSalt(){
        return salt;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }
    
    public String getActivated(){
        return activated;
    }

    public void setActivated(String activated){
        this.activated = activated;
    }
    
    public String getIp(){
        return ip;
    }

    public void setIp(String ip){
        this.ip = ip;
    }
    
    public String getLinkednames(){
        return linkednames;
    }

    public void setLinkednames(String linkednames){
        this.linkednames = linkednames;
    }
    
    public String getInventory(){
        return inventory;
    }

    public void setInventory(String inventory){
        this.inventory = inventory;
    }
    
    public String getRegistred(){
        return registred;
    }

    public void setRegistred(String registred){
        this.registred = registred;
    }
    
    public long getTimeout(){
        return timeout;
    }

    public void setTimeout(long timeout){
        this.timeout = timeout;
    }
    
    public String getArmorinventory(){
        return armorinventory;
    }

    public void setArmorinventory(String armorinventory){
        this.armorinventory = armorinventory;
    }
}