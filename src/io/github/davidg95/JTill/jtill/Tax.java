/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class Tax implements Serializable {

    private int id;
    private String name;
    private double value;

    private BigDecimal sales = BigDecimal.ZERO;
    private BigDecimal payable = BigDecimal.ZERO;

    public Tax(int id, String name, double value) {
        this(name, value);
        this.id = id;
    }

    public Tax(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void addToSales(BigDecimal toAdd) {
        sales = sales.add(toAdd);
    }

    public BigDecimal getSales() {
        return sales;
    }

    public void addToPayable(BigDecimal toAdd) {
        payable = payable.add(toAdd);
    }

    public BigDecimal getPayable() {
        return payable;
    }

    /**
     * SAve the tax to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.dataconnect.updateTax(this);
        } catch (JTillException ex) {
            DataConnect.dataconnect.addTax(this);
        }
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.value;
    }

    public String getSQLUpdateString() {
        return "UPDATE TAX"
                + " SET NAME='" + this.getName()
                + "', VALUE=" + this.getValue()
                + " WHERE TAX.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name + " " + this.value + "%";
    }

}
