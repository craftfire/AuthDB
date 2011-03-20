package com.authdb.scripts;

  import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
  import java.sql.PreparedStatement;
  import java.sql.SQLException;
import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.databases.MySQL;


  public class Custom 
  {
	  public static void adduser(String player, String email, String password, String ipAddress) throws SQLException
	    {
			PreparedStatement ps;
			if(Config.custom_encryption != null)
			{
				try {
					password = Encryption.Encrypt(Config.custom_encryption,password);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//
			ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.custom_table+"` (`"+Config.custom_userfield+"`,`"+Config.custom_passfield+"`)  VALUES (?,?)", 1);
			ps.setString(1, player); //username
			ps.setString(2, password); // password
		    ps.executeUpdate();
	    }
  }