/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author David
 */
public class UpdateChecker {

    public static final String UPDATE_CHECK_URL = "https://jggcomputers.ddns.net/jtill/";

    public static final String SERVER_UPDATE_DOWNLOAD = "https://jggcomputers.ddns.net/repo/public/jtillserverinstaller.exe";

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

    public static void downloadServerUpdate() throws Exception {
        URL website = new URL(SERVER_UPDATE_DOWNLOAD);
        try (InputStream in = website.openStream()) {
            Path targetPath = new File(System.getProperty("java.io.tmpdir") + File.separator + "serverinstaller.exe").toPath();
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            Runtime.getRuntime().exec(System.getProperty("java.io.tmpdir") + File.separator + "serverinstaller.exe", null, new File(System.getProperty("java.io.tmpdir")));
            System.exit(0);
        }
    }
}
