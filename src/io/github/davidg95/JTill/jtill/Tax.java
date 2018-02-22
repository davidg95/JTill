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
import java.util.List;

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
     * Save the tax to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.get().updateTax(this);
        } catch (JTillException ex) {
            DataConnect.get().addTax(this);
        }
    }

    /**
     * Get all the taxes from the database.
     *
     * @return a List of all the tax classes.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public static List<Tax> getAll() throws IOException, SQLException {
        return DataConnect.get().getAllTax();
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.value;
    }

    public String getSQLUpdateString() {
        return "UPDATE TAX"
                + " SET tname='" + this.getName()
                + "', tvalue=" + this.getValue()
                + " WHERE tid=" + this.getId();
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name + " " + this.value + "%";
    }

}
