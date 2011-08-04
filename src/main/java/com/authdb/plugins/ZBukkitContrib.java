/**
(C) Copyright 2011 CraftFire <dev@craftfire.com>
Contex <contex@craftfire.com>, Wulfspider <wulfspider@craftfire.com>

This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/
or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
**/

package com.authdb.plugins;

public class ZBukkitContrib {
    public static boolean checkCommand(String command) {
        int counter = 0;
        int counter2 = 0;
        String[] contrib = command.split("\\.");
        while (contrib.length > counter) {
           try {
               Integer.parseInt(contrib[counter]);
               counter2++;
            }
            catch(Exception e) {
                counter2--;
            }
            counter++;
        }
        if (counter2 <= 4 && counter2 >= 2) {
            return true;
            }
        return false;
    }
}
