/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author David
 */
public class Till implements Serializable, Cloneable, JTillObject {

    private int id;
    private final String name;
    private BigDecimal uncashedTakings;

    public Till(String name) {
        this.name = name;
        this.uncashedTakings = new BigDecimal("0");
        uncashedTakings = uncashedTakings.setScale(2);
    }

    public Till(String name, BigDecimal uncashedTakings) {
        this(name);
        this.uncashedTakings = uncashedTakings;
        this.uncashedTakings = this.uncashedTakings.setScale(2);
    }

    public Till(String name, BigDecimal uncashedTakings, int id) {
        this(name, uncashedTakings);
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getUncashedTakings() {
        return uncashedTakings;
    }

    public void setUncashedTakings(BigDecimal uncashedTakings) {
        this.uncashedTakings = uncashedTakings;
    }

    public void addTakings(BigDecimal val) {
        this.uncashedTakings = uncashedTakings.add(val);
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.uncashedTakings;
    }

    public String getSQLUpdateString() {
        return "UPDATE TILLS"
                + " SET NAME='" + this.name
                + "' UNCASHED=" + this.uncashedTakings
                + " WHERE TILLS.ID=" + this.id;
    }

    @Override
    public Till clone() {
        try {
            final Till result = (Till) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Till) {
            if (this.getId() == ((Till) o).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name;
    }
}
