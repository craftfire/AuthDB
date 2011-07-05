/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.scripts.cms;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import com.authdb.util.Config;
import com.authdb.util.Encryption;
import com.authdb.util.Util;
import com.authdb.util.databases.MySQL;

public class Drupal {
    
    public static String VersionRange = "6.20-6.20";
    public static String VersionRange2 = "7.0-7.0";
    public static String LatestVersionRange = VersionRange2;
    public static String Name = "drupal";
    public static String ShortName = "dru";
    
  public static void adduser(int checkid, String player, String email, String password, String ipAddress) throws SQLException
  {
    long timestamp = System.currentTimeMillis()/1000;
    if(checkid == 1)
    {
        String hash = Encryption.md5(password);
        //
        PreparedStatement ps;
        //
        ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`name`,`pass`,`mail`,`created`,`access`,`login`,`status`,`init`)  VALUES (?,?,?,?,?,?,?,?)", 1);
        ps.setString(1, player); //name
        ps.setString(2, hash); //pass
        ps.setString(3, email); //mail
        ps.setLong(4, timestamp); //created
        ps.setLong(5, timestamp); //access
        ps.setLong(6, timestamp); //login
        ps.setInt(7, 1); //status
        ps.setString(8, email); //init
        ps.executeUpdate();
    }
    
    else if(checkid == 2)
    {
        String hash = user_hash_password(password,0);
        //
        PreparedStatement ps;
        //
        ps = MySQL.mysql.prepareStatement("INSERT INTO `"+Config.database_prefix+"users"+"` (`name`,`pass`,`mail`,`created`,`login`,`status`,`init`)  VALUES (?,?,?,?,?,?,?)", 1);
        ps.setString(1, player); //name
        ps.setString(2, hash); //pass
        ps.setString(3, email); //mail
        ps.setLong(4, timestamp); //created
        ps.setLong(5, timestamp); //login
        ps.setInt(6, 1); //status
        ps.setString(7, email); //init
        ps.executeUpdate();
    }
  }
    
      private static String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      private static final int DRUPAL_MIN_HASH_COUNT =  7;
      private static final int DRUPAL_MAX_HASH_COUNT = 30;
      private static final int DRUPAL_HASH_COUNT     = 14;
      private static final int DRUPAL_HASH_LENGTH    = 55;
     
     
      public static String hash(String password) {
        String random_state = unique_id();
        String random = "";
        int count = 6;

        if (random.length() < count)
        {
            random = "";

            for (int i = 0; i < count; i += 16)
            {
                random_state = Encryption.SHA256(unique_id() + random_state);
                random += Encryption.pack(Encryption.SHA256(random_state));
            }
            random = random.substring(0, count);
        }

        String hash = _hash_crypt_private(password, _hash_gensalt_private(
                random, itoa64));
        if (hash.length() == 55)
            return hash;

        return Encryption.SHA256(password);
      }
      
      private static String password_base64_encode(String input, int count)
       {
        String  output = "";
        int i = 0, value;
        do
         {
          value = input.charAt(i++);
          output += itoa64.charAt(value & 0x3f);

          if (i < count) value |= input.charAt(i) << 8;

          output += itoa64.charAt((value >> 6) & 0x3f);

          if(i++ >= count) break;

          if(i < count) value |= input.charAt(i) << 16;

          output += itoa64.charAt((value >> 12) & 0x3f);

          if (i++ >= count) break;

          output += itoa64.charAt((value >> 18) & 0x3f);
         } 
        while (i < count);

        return output;
       }    
      
      private static int password_get_count_log2(String setting) { return itoa64.indexOf(setting.charAt(3)); }
      
      private static int password_enforce_log2_boundaries(int count_log2)
       {
        if (count_log2 < DRUPAL_MIN_HASH_COUNT) {
         return DRUPAL_MIN_HASH_COUNT;
        }
        else if (count_log2 > DRUPAL_MAX_HASH_COUNT) {
         return DRUPAL_MAX_HASH_COUNT;
        }

        return (int) count_log2;
       }

      private static String unique_id() {
        return unique_id("c");
      }
      
      private static String password_generate_salt(int count_log2)
       {
        String output = "$S$";
        // Ensure that $count_log2 is within set bounds.
        count_log2 = password_enforce_log2_boundaries(count_log2);
        // We encode the final log2 iteration count in base 64.

        output += itoa64.charAt(count_log2);
        // 6 bytes is the standard salt for a portable phpass hash.
        byte randomBytes[] = new byte[6];
        new Random(System.currentTimeMillis()).nextBytes(randomBytes); //replacing drupal_random_bytes() function

        output += password_base64_encode(new String(randomBytes), 6);
        return output;
       }
      
      private static String password_crypt(String algo, String password, String setting)
       {
        // The first 12 characters of an existing hash are its setting string.
        setting = setting.substring(0, 12);

        if (setting.charAt(0) != '$' || setting.charAt(2) != '$') return null; //throw new IllegalArgumentException("Bad setting !");

        int count_log2 = password_get_count_log2(setting);

        // Hashes may be imported from elsewhere, so we allow != DRUPAL_HASH_COUNT
        if (count_log2 < DRUPAL_MIN_HASH_COUNT || count_log2 > DRUPAL_MAX_HASH_COUNT) return null; //throw new RuntimeException("Bad Hash count : " + count_log2);

        String salt = setting.substring(4, 12); 
        // Hashes must have an 8 character salt.
        if (salt.length() != 8) return null; //throw new RuntimeException("Bad salt length : " + salt.length());

        // Convert the base 2 logarithm into an integer.
        int count = 1 << count_log2;

        // We rely on the hash() function being available in PHP 5.2+.

        String hash;
        try
         {
          hash = Encryption.Encrypt(algo, salt+password);
          
          do 
           {
            hash = Encryption.Encrypt(algo, hash + password);
           } 
          while (--count>=0);
         }
        catch(Exception e) { e.printStackTrace(); return null; }

        int len  = hash.length();
        String output =  setting + password_base64_encode(hash, len);
        // _password_base64_encode() of a 16 byte MD5 will always be 22 characters.
        // _password_base64_encode() of a 64 byte sha512 will always be 86 characters.
        int expected = (int) (12 + Math.ceil((8 * len) / 6));

        //Util.Debug("HASH DERP:"+output);
        Util.Debug("TEST 2"+password_base64_encode(hash, len));
        Util.Debug("TEST 2"+password_base64_encode(hash, len).length());
        Util.Debug("HASH DERP:"+output.substring(0, 55));
        Util.Debug("DERP 1 : "+output.length());
        Util.Debug("DERP 2 : "+expected);
        Util.Debug("FASCE:"+(output.length() == expected) != null ? output.substring(0, DRUPAL_HASH_LENGTH) : null);
        return (output.length() == expected) ? output.substring(0, DRUPAL_HASH_LENGTH) : null;
       }  
      
      
      public static String user_hash_password(String password, int count_log2)
       {
        if(count_log2<0 || count_log2 >DRUPAL_MAX_HASH_COUNT)
         count_log2 = DRUPAL_HASH_COUNT; // Use the standard iteration count.

        return password_crypt("sha512", password, password_generate_salt(count_log2));
       } 
      
      
      public static boolean user_check_password(String password, String crypted_password)
       {
        String real_hash;
        if (crypted_password.substring(0, 2).equals("U$"))
         {
          // This may be an updated password from user_update_7000(). Such hashes
          // have 'U' added as the first character and need an extra md5().
          real_hash = crypted_password.substring(1); 
          password = Encryption.md5(password);
         }
        else
         {
          real_hash = crypted_password;
         }

        String type = real_hash.substring(0, 3), hash; 


        if(type.equals("$S$")) // A normal Drupal 7 password using sha512.
         hash = password_crypt("sha512", password, real_hash);
        else
         if(type.equals("$H$") || type.equals("$P$")) //a PHPBB3 pass, or an imported password or from an earlier Drupal version.
          hash = password_crypt("md5", password, real_hash);
         else
          return false;

        return real_hash == hash; //what do they do here ?!
       }  
      
      private static String unique_id(String extra) {
        return "1234567890abcdef";
      }

      private static String _hash_gensalt_private(String input, String itoa64) {
        return _hash_gensalt_private(input, itoa64, 6);
      }

        private static String _hash_gensalt_private(String input, String itoa64,
                int iteration_count_log2)
        {
            if (iteration_count_log2 < 4 || iteration_count_log2 > 31)
            {
                iteration_count_log2 = 8;
            }
            int PHP_VERSION = 5;
            String output = "$S$";
            output += itoa64.charAt(Math.min(iteration_count_log2
                    + ((PHP_VERSION >= 5) ? 5 : 3), 30));
            output += _hash_encode64(input, 6);

            return output;
        }


      /**
       * Encode hash
       */
        private static String _hash_encode64(String input, int count)
        {
            String output = "";
            int i = 0;

            do
            {
                int value = input.charAt(i++);
                output += itoa64.charAt(value & 0x3f);

                if (i < count)
                    value |= input.charAt(i) << 8;

                output += itoa64.charAt((value >> 6) & 0x3f);

                if (i++ >= count)
                    break;

                if (i < count)
                    value |= input.charAt(i) << 16;

                output += itoa64.charAt((value >> 12) & 0x3f);

                if (i++ >= count)
                    break;

                output += itoa64.charAt((value >> 18) & 0x3f);
            }
            while (i < count);

            return output;
        }

        static String _hash_crypt_private(String password, String setting)
        {
            String output = "*";

            // Check for correct hash
            if (!setting.substring(0, 3).equals("$S$"))
                return output;

            int count_log2 = itoa64.indexOf(setting.charAt(3));
            if (count_log2 < 7 || count_log2 > 30)
                return output;

            int count = 1 << count_log2;
            String salt = setting.substring(4, 8);
            if (salt.length() != 8)
                return output;

            String m1 = Encryption.SHA512(salt + password);
            String hash = Encryption.pack(m1);
            do
            {
                hash = Encryption.pack(Encryption.SHA512(hash + password));
            }
            while (--count > 0);

            output = setting.substring(0, 12);
            output += _hash_encode64(hash, 16);

            return output;
        }

        public static boolean check_hash(String password, String hash)
        {
            if (hash.length() == 55)
                return _hash_crypt_private(password, hash).equals(hash);
            else
                return Encryption.SHA512(password).equals(hash);
        }
}