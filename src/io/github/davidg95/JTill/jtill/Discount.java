/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Class which models a discount.
 *
 * @author David
 */
public class Discount implements Serializable, Cloneable, Item, JTillObject {

    private int id;
    private String name;
    private double percentage;
    private BigDecimal price;
    private boolean open;
    private int trigger;

    /**
     * Constructor for discount which takes in all values.
     *
     * @param id the discounts id.
     * @param name the name.
     * @param percentage the percentage as a double from 0-100.
     * @param price the price.
     * @param trigger the product that triggers this discount.
     */
    public Discount(int id, String name, double percentage, BigDecimal price, int trigger) {
        this(name, percentage, price, trigger);
        this.id = id;
    }

    /**
     * Constructor which takes in all values except id.
     *
     * @param name the name.
     * @param percentage the percentage as a double from 0-100.
     * @param price the price.
     * @param trigger the product that triggers this discount.
     */
    public Discount(String name, double percentage, BigDecimal price, int trigger) {
        this.name = name;
        this.percentage = percentage;
        this.price = price;
        this.trigger = trigger;
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

    public int getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.percentage
                + "," + this.price.doubleValue()
                + "," + this.trigger;
    }

    public String getSQLUpdateString() {
        return "UPDATE DISCOUNTS"
                + " SET NAME='" + this.getName()
                + "', PERCENTAGE=" + this.getPercentage()
                + ", PRICE=" + this.price.doubleValue()
                + ", TRIGGER=" + this.trigger
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

    @Override
    public Item clone() {
        try {
            final Discount result = (Discount) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
}
