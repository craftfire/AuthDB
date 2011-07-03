package com.authdb.plugins;

public class zBukkitContrib {
    public static boolean CheckCommand(String command)
    {
        int counter = 0;
        int counter2 = 0;
        String[] contrib = command.split(".");
        while(contrib.length > counter)
        {
           try
            {
               Integer.parseInt(contrib[counter]);
               counter2++;
            }
            catch( Exception e )
            {
                counter2--;
            }
            counter++;
        }
        if(counter2 <= 4 || counter2 >= 2) 
            return true;
        return false;
    }
}
