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

/**
 * Class which holds all server configurations.
 *
 * @author David
 */
public class Settings implements Serializable {

    private static volatile Settings settings;
    private final Properties properties;

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

    public static final String propertiesFile = System.getenv("APPDATA") + "\\JTill Server\\server.properties";

    public Settings() {
        properties = new Properties();
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
     * Method to get a setting.
     *
     * @param key the settings to get.
     * @param defaultValue the value to assign to the setting if it does not
     * already exist.
     * @return the value assigned to the setting.
     */
    public String getSetting(String key, String defaultValue) {
        if (properties.containsKey(key)) {
            saveProperties();
            return properties.getProperty(key);
        } else {
            setSetting(key, defaultValue);
            saveProperties();
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
        saveProperties();
    }

    /**
     * Method to remove a setting.
     *
     * @param key the setting to remove.
     */
    public void removeSetting(String key) {
        properties.remove(key);
        saveProperties();
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
     *
     * @return true if the settings file exists, false if it does not.
     */
    public boolean loadProperties() {
        InputStream in;
        try {
            in = new FileInputStream(propertiesFile);
            properties.load(in);

            in.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
            initProperties();
            return false;
        } catch (IOException ex) {
        }
        return true;
    }

    /**
     * Method to save the settings to the server.properties file.
     */
    public void saveProperties() {
        OutputStream out;

        try {
            out = new FileOutputStream(propertiesFile);
            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }

    /**
     * Fills the settings file with default values.
     */
    private void initProperties() {
        OutputStream out;

        try {
            out = new FileOutputStream(propertiesFile);

            properties.put("db_address", "");
            properties.put("db_username", "");
            properties.put("db_password", "");
            properties.put("max_conn", Integer.toString(DEFAULT_MAX_CONNECTIONS));
            properties.put("max_queue", Integer.toString(DEFAULT_MAX_QUEUE));
            properties.put("port", Integer.toString(DEFAULT_PORT));
            properties.put("AUTO_LOGOUT", "FALSE");
            properties.put("LOGOUT_TIMEOUT", "-1");
            properties.put("MINIMUM_SERVER_LOGIN", "2");
            properties.put("SETTINGS_EDIT", "3");
            properties.put("CURRENCY_SYMBOL", "£");
            properties.put("SITE_NAME", "SITE");
            properties.put("ASK_EMAIL_RECEIPT", "FALSE");
            properties.put("MAX_CACHE_SALES", "20");
            properties.put("UPC_PREFIX", "");
            properties.put("BARCODE_LENGTH", "15");
            properties.put("NEXT_PLU", "0");
            properties.put("NEXT_PRIVATE", "1");
            properties.put("TERMINAL_BACKGROUND", "#000000");
            properties.put("SHOW_ADDRESS_RECEIPT", "TRUE");
            properties.put("SHOW_STAFF_RECEIPT", "TRUE");
            properties.put("SHOW_TERMINAL_RECEIPT", "TRUE");
            properties.put("LOYALTY_VALUE", "0");
            properties.put("LOYALTY_SPEND_VALUE", "0");
            properties.put("UPDATE_STARTUP", "false");
            properties.put("LOGINTYPE", "CODE");
            properties.put("PROMPT_EMAIL_RECEIPT", "false");
            properties.put("UNLOCK_CODE", "OFF");
            properties.put("TERMINAL_BG", "000000");
            properties.put("BORDER_SCREEN_BUTTON", "false");
            properties.put("BORDER_COLOR", "#ff0000");
            properties.store(out, null);

            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }
}
