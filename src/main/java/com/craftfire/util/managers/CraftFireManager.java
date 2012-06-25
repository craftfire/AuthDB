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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.authdb.util.encryption.Encryption;
import com.authdb.util.Util;

public class CraftFireManager {
    LoggingManager loggingManager = new LoggingManager();

    public void postInfo(String b407f35cb00b96936a585c4191fc267a, String f13a437cb9b1ac68b49d597ed7c4bfde, String cafd6e81e3a478a7fe0b40e7502bf1f, String fcf2204d0935f0a8ef1853662b91834e, String aa25d685b171d7874222c7080845932, String fac8b1115d09f0d816a0671d144d49e, String e98695d728198605323bb829d6ea4de, String d89570db744fe029ca696f09d34e1, String fe75a95090e70155856937ae8d0482, String a6118cfc6befa19cada1cddc32d36a3, String d440b827e9c17bbd51f2b9ac5c97d6, String c284debb7991b2b5fcfd08e9ab1e5, int d146298d6d3e1294bbe4121f26f02800) throws IOException {
        String d68d8f3c6398544b1cdbeb4e5f39f0 = "1265a15461038989925e0ced2799762c";
        String e5544ab05d8c25c1a5da5cd59144fb = Encryption.md5(d146298d6d3e1294bbe4121f26f02800 + c284debb7991b2b5fcfd08e9ab1e5 + d440b827e9c17bbd51f2b9ac5c97d6 + a6118cfc6befa19cada1cddc32d36a3 + fe75a95090e70155856937ae8d0482 + d89570db744fe029ca696f09d34e1 + e98695d728198605323bb829d6ea4de + fac8b1115d09f0d816a0671d144d49e + aa25d685b171d7874222c7080845932 + d68d8f3c6398544b1cdbeb4e5f39f0 + fcf2204d0935f0a8ef1853662b91834e + b407f35cb00b96936a585c4191fc267a + f13a437cb9b1ac68b49d597ed7c4bfde + cafd6e81e3a478a7fe0b40e7502bf1f);
        String data = URLEncoder.encode("b407f35cb00b96936a585c4191fc267a", "UTF-8") + "=" + URLEncoder.encode(b407f35cb00b96936a585c4191fc267a, "UTF-8");
        data += "&" + URLEncoder.encode("f13a437cb9b1ac68b49d597ed7c4bfde", "UTF-8") + "=" + URLEncoder.encode(f13a437cb9b1ac68b49d597ed7c4bfde, "UTF-8");
        data += "&" + URLEncoder.encode("9cafd6e81e3a478a7fe0b40e7502bf1f", "UTF-8") + "=" + URLEncoder.encode(cafd6e81e3a478a7fe0b40e7502bf1f, "UTF-8");
        data += "&" + URLEncoder.encode("58e5544ab05d8c25c1a5da5cd59144fb", "UTF-8") + "=" + URLEncoder.encode(e5544ab05d8c25c1a5da5cd59144fb, "UTF-8");
        data += "&" + URLEncoder.encode("fcf2204d0935f0a8ef1853662b91834e", "UTF-8") + "=" + URLEncoder.encode(fcf2204d0935f0a8ef1853662b91834e, "UTF-8");
        data += "&" + URLEncoder.encode("3aa25d685b171d7874222c7080845932", "UTF-8") + "=" + URLEncoder.encode(aa25d685b171d7874222c7080845932, "UTF-8");
        data += "&" + URLEncoder.encode("6fac8b1115d09f0d816a0671d144d49e", "UTF-8") + "=" + URLEncoder.encode(fac8b1115d09f0d816a0671d144d49e, "UTF-8");
        data += "&" + URLEncoder.encode("5e98695d728198605323bb829d6ea4de", "UTF-8") + "=" + URLEncoder.encode(e98695d728198605323bb829d6ea4de, "UTF-8");
        data += "&" + URLEncoder.encode("189d89570db744fe029ca696f09d34e1", "UTF-8") + "=" + URLEncoder.encode(d89570db744fe029ca696f09d34e1, "UTF-8");
        data += "&" + URLEncoder.encode("70fe75a95090e70155856937ae8d0482", "UTF-8") + "=" + URLEncoder.encode(fe75a95090e70155856937ae8d0482, "UTF-8");
        data += "&" + URLEncoder.encode("9a6118cfc6befa19cada1cddc32d36a3", "UTF-8") + "=" + URLEncoder.encode(a6118cfc6befa19cada1cddc32d36a3, "UTF-8");
        data += "&" + URLEncoder.encode("94d440b827e9c17bbd51f2b9ac5c97d6", "UTF-8") + "=" + URLEncoder.encode(d440b827e9c17bbd51f2b9ac5c97d6, "UTF-8");
        data += "&" + URLEncoder.encode("234c284debb7991b2b5fcfd08e9ab1e5", "UTF-8") + "=" + URLEncoder.encode(c284debb7991b2b5fcfd08e9ab1e5, "UTF-8");
        data += "&" + URLEncoder.encode("41d68d8f3c6398544b1cdbeb4e5f39f0", "UTF-8") + "=" + URLEncoder.encode(d68d8f3c6398544b1cdbeb4e5f39f0, "UTF-8");
        data += "&" + URLEncoder.encode("d146298d6d3e1294bbe4121f26f02800", "UTF-8") + "=" + URLEncoder.encode("" + d146298d6d3e1294bbe4121f26f02800, "UTF-8");
        loggingManager.Debug("Preparing usage stats for submission.");
        URL url = new URL("http://www.craftfire.com/stats.php");
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(4000);
        conn.setReadTimeout(4000);
        loggingManager.Debug("Usage stats submission timeout is 4000 ms (4 seconds).");
        conn.setRequestProperty("X-AuthDB", e5544ab05d8c25c1a5da5cd59144fb);
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        loggingManager.Debug("Attempting to submit usage stats.");
        wr.write(data);
        wr.flush();
        wr.close();
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Util.logging.Debug("Successfully sent usage stats to CraftFire.");
        rd.close();
    }
}
