/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.*;
import java.net.*;

/**
 *
 * @author David
 */
public class UpdateChecker {

    public static final String UPDATE_CHECK_URL = "http://jggcomputers.ddns.net/jtill/";

    public static final String SERVER_UPDATE_DOWNLOAD = "http://jggcomputers.ddns.net/repo/public/jtillserverinstaller.exe";

    public static byte[] getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString().getBytes();
    }

    public static String checkForUpdate() throws Exception {
        return new String(getHTML(UPDATE_CHECK_URL));
    }

    public static byte[] downloadServerUpdate() throws Exception {
        return getHTML(SERVER_UPDATE_DOWNLOAD);
    }
}
