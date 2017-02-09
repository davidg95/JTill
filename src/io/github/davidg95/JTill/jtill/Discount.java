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
public class Discount implements Serializable, Item {

    private int id;
    private String name;
    private double percentage;
    private BigDecimal price;
    private boolean open;

    public Discount(int id, String name, double percentage, BigDecimal price) {
        this(name, percentage, price);
        this.id = id;
    }

    public Discount(String name, double percentage, BigDecimal price) {
        this.name = name;
        this.percentage = percentage;
        this.price = price;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String name) {
        this.name = name;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.percentage
                + "," + this.price.doubleValue();
    }

    public String getSQLUpdateString() {
        return "UPDATE DISCOUNTS"
                + " SET NAME='" + this.getName()
                + "', PERCENTAGE=" + this.getPercentage()
                + ", PRICE=" + this.price.doubleValue()
                + " WHERE DISCOUNTS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public BigDecimal getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }
}
