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
public class RefundReason implements Serializable {

    private int id;
    private String reason;
    private int priviledgeLevel;

    public RefundReason(String reason, int privilageLevel) {
        this.reason = reason;
        this.priviledgeLevel = privilageLevel;
    }

    public RefundReason(int id, String reason, int privilageLevel) {
        this(reason, privilageLevel);
        this.id = id;
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
     * Saves the REfundReason to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.get().updateRefundReason(this);
        } catch (JTillException ex) {
            DataConnect.get().addRefundReason(this);
        }
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
        final RefundReason other = (RefundReason) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.id + " - " + this.reason;
    }
}
