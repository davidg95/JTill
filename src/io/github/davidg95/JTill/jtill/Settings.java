/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author David
 */
public class Settings implements Serializable {

    private final HashMap<String, String> settingsMap;

    public Settings() {
        settingsMap = new HashMap<>();
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
}
