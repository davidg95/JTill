/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

/**
 *
 * @author David
 */
public class WasteReason implements Serializable {

    private int id;
    private String reason;

    public WasteReason(String reason) {
        this.reason = reason;
    }

    public WasteReason(int id, String reason) {
        this(reason);
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
        return "ID=" + id + ", Reason=" + reason;
    }
}
