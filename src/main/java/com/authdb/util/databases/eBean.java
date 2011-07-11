/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.util.databases;

import java.util.ArrayList;

import com.authdb.AuthDB;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Entity()
@Table(name = "authdb_users")
public class eBean {

    public static enum Column
    {
        authorized ("authorized"),
        reload ("reload"),
        salt ("salt"),
        passwordhash ("passwordhash"),
        othername ("othername"),
        inventory ("inventory");

        private String name;
        Column(String name) { this.name = name; }
    }
    
    public static void SetupPlayer(Player player)
    {
        eBean eBeanClass = eBean.find(player);
        if (eBeanClass == null) 
        {
            
            AuthDB.Database.save(eBeanClass);
        }
    }
    
    public static eBean find(Player player)
    {
        eBean eBeanClass = AuthDB.Database.find(eBean.class).where().ieq("playerName", player.getName()).findUnique();
        return eBeanClass;
    }
    
    public static eBean find(Player player, Column column1, String value1)
    {
        eBean eBeanClass = AuthDB.Database.find(eBean.class).where().ieq("playerName", player.getName()).ieq(column1.name,value1).findUnique();
        return eBeanClass;
    }
    
    public static boolean find(Player player, Column column1, String value1, Column column2, String value2)
    {
        eBean eBeanClass = AuthDB.Database.find(eBean.class).where().ieq("playerName", player.getName()).ieq(column1.name,value1).ieq(column2.name,value2).findUnique();
        if (eBeanClass != null)
        {
           return true;
        }
        return false;
    }
    
    @Id
    private int id;
    @NotNull
    private String playerName;
    @NotEmpty
    private String authorized; 
    private String reload; 
    private String passwordhash; 
    private String salt;
    private String othername;
    private String inventory;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String ply) {
        this.playerName = ply;
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(playerName);
    }

    public void setPlayer(Player player) {
        this.playerName = player.getName();
    }

    public String getAuthorized(){
        return authorized;
    }

    public void setAuthorized(String authorized){
        this.authorized = authorized;
    }
    
    public String getReload(){
        return reload;
    }

    public void setReload(String reload){
        this.reload = reload;
    }
    
    public String getPasswordhash(){
        return passwordhash;
    }

    public void setPasswordhash(String passwordhash){
        this.passwordhash = passwordhash;
    }
    
    public String getSalt(){
        return salt;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }
    
    public String getOthername(){
        return othername;
    }

    public void setOthername(String othername){
        this.othername = othername;
    }
    
    public String getInventory(){
        return inventory;
    }

    public void setInventory(String inventory){
        this.inventory = inventory;
    }
}