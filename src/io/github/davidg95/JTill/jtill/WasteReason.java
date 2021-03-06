/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

/**
 *
 * @author David
 */
public class WasteReason implements Serializable {

    private int id;
    private String reason;
    private int priviledgeLevel;

    public WasteReason(int id, String reason, int priviledgeLevel) {
        this.reason = reason;
        this.priviledgeLevel = priviledgeLevel;
        this.id = id;
    }

    public String getName() {
        return this.reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getPriviledgeLevel() {
        return priviledgeLevel;
    }

    public void setPriviledgeLevel(int priviledgeLevel) {
        this.priviledgeLevel = priviledgeLevel;
    }

    /**
     * Save the WasteReason to the database.
     *
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.get().updateWasteReason(this);
        } catch (JTillException ex) {
            DataConnect.get().addWasteReason(this);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WasteReason other = (WasteReason) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return id + " - " + reason;
    }
}
