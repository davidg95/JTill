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
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which holds all server configurations.
 *
 * @author David
 */
public class Settings implements Serializable {

    private static Settings settings;
    private final Properties properties;
    private final Properties meta;

    /**
     * The default port number of 52341.
     */
    public static final int DEFAULT_PORT = 52341;
    /**
     * The default maximum connections of 10.
     */
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    /**
     * The default maximum queued connections of 10.
     */
    public static final int DEFAULT_MAX_QUEUE = 10;
    /**
     * The default database address.
     */
    public static final String DEFAULT_ADDRESS = "jdbc:derby:TillEmbedded;";
    /**
     * The default database username.
     */
    public static final String DEFAULT_USERNAME = "APP";
    /**
     * The default database password.
     */
    public static final String DEFAULT_PASSWORD = "App";

    public Settings() {
        properties = new Properties();
        meta = new Properties();
    }

    /**
     * Returns an instance of the Settings. If an instance has not been created
     * already, it will create one.
     *
     * @return Settings object.
     */
    public static Settings getInstance() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    /**
     * Method to get a setting.
     *
     * @param key the setting to get.
     * @return the value associated with the setting.
     */
    public String getSetting(String key) {
        return properties.getProperty(key);
    }

    /**
     * Method to get the help message for a setting.
     *
     * @param key the settings to get the message for.
     * @return the help message.
     */
    public String getSettingMeta(String key) {
        return meta.getProperty(key);
    }

    /**
     * Method to get a setting.
     *
     * @param key the settings to get.
     * @param defaultValue the value to assign to the setting if it does not
     * already exist.
     * @return the value assigned to the setting.
     */
    public String getSetting(String key, String defaultValue) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        } else {
            setSetting(key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Method to set a setting.
     *
     * @param key the setting to set.
     * @param value the value to assign to it.
     */
    public void setSetting(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Method to remove a setting.
     *
     * @param key the setting to remove.
     */
    public void removeSetting(String key) {
        properties.remove(key);
    }

    /**
     * Gets the properties object.
     *
     * @return the Properties object.
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Method to load the settings from the server.properties file.
     */
    public void loadProperties() {
        InputStream in;
        try {
            in = new FileInputStream("server.properties");
            properties.load(in);

            in.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
            initProperties();
        } catch (IOException ex) {
        }

        try {
            in = new FileInputStream("server-meta.properties");
            meta.load(in);

            in.close();
        } catch (FileNotFoundException ex) {
            initMeta();
        } catch (IOException ex) {
        }
    }

    /**
     * Method to save the settings to the server.properties file.
     */
    public void saveProperties() {
        OutputStream out;

        try {
            out = new FileOutputStream("server.properties");
            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }

        try {
            out = new FileOutputStream("server-meta.properties");
            meta.store(out, null);
            out.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    /**
     * Fills the settings file with default values.
     */
    private void initProperties() {
        OutputStream out;

        try {
            out = new FileOutputStream("server.properties");

            setSetting("db_address", DEFAULT_ADDRESS);
            setSetting("db_username", DEFAULT_USERNAME);
            setSetting("db_password", DEFAULT_PASSWORD);
            setSetting("max_conn", Integer.toString(DEFAULT_MAX_CONNECTIONS));
            setSetting("max_queue", Integer.toString(DEFAULT_MAX_QUEUE));
            setSetting("port", Integer.toString(DEFAULT_PORT));
            setSetting("AUTO_LOGOUT", "FALSE");
            setSetting("LOGOUT_TIMEOUT", "-1");
            setSetting("MINIMUM_SERVER_LOGIN", "2");
            setSetting("SETTINGS_EDIT", "3");

            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }

    private void initMeta() {
        OutputStream out;

        try {
            out = new FileOutputStream("server-meta.properties");

            setSetting("db_address", "The address for the database.");
            setSetting("db_username", "The username for logging onto the database.");
            setSetting("db_password", "The password for logging onto the database.");
            setSetting("max_conn", "The maximum allowed number of connections to the server at any one time.");
            setSetting("max_queue", "The maximum number of connections to queue before blocking new connections if the maximum has been reached.");
            setSetting("port", "The port number for the server.");
            setSetting("AUTO_LOGOUT", "Whether staff should be automatically logged out after a sale or not.\nTRUE or FALSE.");
            setSetting("LOGOUT_TIMEOUT", "Not supported yet.");
            setSetting("MINIMUM_SERVER_LOGIN", "The lowest position allowed for a member of staff to log into the server manager.");
            setSetting("SETTINGS_EDIT", "The lowest position allowed for a member of staff to edit these settings.");

            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }
}
