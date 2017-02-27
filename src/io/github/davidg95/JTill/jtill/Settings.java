/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author David
 */
public class Settings implements Serializable {

    private static Settings settings;

    private final HashMap<String, String> settingsMap;
    private final Properties properties;

    public static int PORT;
    public static int MAX_CONNECTIONS;
    public static int MAX_QUEUE;

    public static final int DEFAULT_PORT = 52341;
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_MAX_QUEUE = 10;

    public Settings() {
        settingsMap = new HashMap<>();
        properties = new Properties();
    }

    public static Settings getInstance() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    public static Settings getNewInstance() {
        settings = new Settings();
        return settings;
    }

    public void setSetting(String key, String value) {
        settingsMap.put(key, value);
    }

    public String getSetting(String key, String init) {
        if (!settingsMap.containsKey(key)) {
            settingsMap.put(key, init);
        }
        return settingsMap.get(key);
    }

    public String getSetting(String key) {
        return getSetting(key, "");
    }

    public HashMap getMap() {
        return this.settingsMap;
    }

    public void loadProperties() {
        InputStream in;

        try {
            in = new FileInputStream("server.properties");

            properties.load(in);
            Set<Object> keySet = properties.keySet();
            Iterator<Object> keySetIterator = keySet.iterator();
            while (keySetIterator.hasNext()) {
                String key = (String) keySetIterator.next();
                String value = properties.getProperty(key);
                setSetting(key, value);
            }

            DBConnect.hostName = getSetting("host");
            PORT = Integer.parseInt(getSetting("port", Integer.toString(DEFAULT_PORT)));
            MAX_CONNECTIONS = Integer.parseInt(getSetting("max_conn", Integer.toString(DEFAULT_MAX_CONNECTIONS)));
            MAX_QUEUE = Integer.parseInt(getSetting("max_queue", Integer.toString(DEFAULT_MAX_QUEUE)));
            DBConnect.DB_ADDRESS = getSetting("db_address", "jdbc:derby:TillEmbedded;");
            DBConnect.DB_USERNAME = getSetting("db_username", "APP");
            DBConnect.DB_PASSWORD = getSetting("db_password", "App");
            DBConnect.MAIL_SERVER = getSetting("mail.smtp.host");
            DBConnect.OUTGOING_MAIL_ADDRESS = getSetting("OUTGOING_MAIL_ADDRESS");
            DBConnect.MAIL_ADDRESS = getSetting("MAIL_ADDRESS");

            setSetting("port", Integer.toString(PORT));
            setSetting("max_conn", Integer.toString(MAX_CONNECTIONS));
            setSetting("max_queue", Integer.toString(MAX_QUEUE));
            setSetting("db_address", DBConnect.DB_ADDRESS);
            setSetting("db_username", DBConnect.DB_USERNAME);
            setSetting("db_password", DBConnect.DB_PASSWORD);
            setSetting("mail.smtp.host", DBConnect.MAIL_SERVER);
            setSetting("OUTGOING_MAIL_ADDRESS", DBConnect.OUTGOING_MAIL_ADDRESS);
            setSetting("MAIL_ADDRESS", DBConnect.MAIL_ADDRESS);

            keySet = getMap().keySet();
            keySetIterator = keySet.iterator();
            while (keySetIterator.hasNext()) {
                Object key = keySetIterator.next();
                String value = getSetting(key.toString());
                properties.put(key, value);
            }

            in.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
            saveProperties();
        } catch (IOException ex) {
        }
    }

    public void saveProperties() {
        OutputStream out;

        try {
            out = new FileOutputStream("server.properties");

            DBConnect.hostName = InetAddress.getLocalHost().getHostName();
            Set<String> keySet = getMap().keySet();
            Iterator<String> keySetIterator = keySet.iterator();
            while (keySetIterator.hasNext()) {
                String key = keySetIterator.next();
                String value = getSetting(key);
                properties.put(key, value);
            }

            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }

    public Properties getProperties() {
        return this.properties;
    }
}
