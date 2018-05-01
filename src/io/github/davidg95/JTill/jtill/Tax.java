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
import java.util.Objects;

/**
 *
 * @author David
 */
public class Tax implements Serializable {

    private String name;
    private double value;

    private BigDecimal sales = BigDecimal.ZERO;
    private BigDecimal payable = BigDecimal.ZERO;

    public Tax(String name, double value) {
        this.name = name;
        this.value = value;
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
                + " SET tvalue=" + this.getValue()
                + " WHERE tname='" + this.getName() + "'";
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
        final Tax other = (Tax) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return this.name + " %" + this.value;
    }

}
