/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
    private int action;
    private int condition;
    private int conditionValue;
    private long start;
    private long end;

    private List<Trigger> triggers;

    public static final int PERCENTAGE_OFF = 0;
    public static final int MONEY_OFF = 1;

    /**
     * Constructor for discount which takes in all values.
     *
     * @param id the discounts id.
     * @param name the name.
     * @param percentage the percentage as a double from 0-100.
     * @param price the price.
     * @param action the action the discount performs.
     * @param condition the condition that was chosen.
     * @param conditionValue the value for the condition.
     * @param start the start date of the promotion.
     * @param end the end date of the promotion.
     */
    public Discount(int id, String name, double percentage, BigDecimal price, int action, int condition, int conditionValue, long start, long end) {
        this(name, percentage, price, action, condition, conditionValue, start, end);
        this.id = id;
    }

    /**
     * Constructor which takes in all values except id.
     *
     * @param name the name.
     * @param percentage the percentage as a double from 0-100.
     * @param price the price.
     * @param action the action the discount performs.
     * @param condition the condition that was chosen.
     * @param conditionValue the value for the condition.
     */
    public Discount(String name, double percentage, BigDecimal price, int action, int condition, int conditionValue, long start, long end) {
        this.name = name;
        this.percentage = percentage;
        this.price = price;
        this.action = action;
        this.condition = condition;
        this.conditionValue = conditionValue;
        this.start = start;
        this.end = end;
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

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(int conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.percentage
                + "," + this.price.doubleValue()
                + "," + this.action
                + "," + this.condition
                + "," + this.conditionValue
                + "," + this.start
                + "," + this.end;
    }

    public String getSQLUpdateString() {
        return "UPDATE DISCOUNTS"
                + " SET NAME='" + this.getName()
                + "', PERCENTAGE=" + this.getPercentage()
                + ", PRICE=" + this.price.doubleValue()
                + ", ACTION=" + this.action
                + ", CONDITION=" + this.condition
                + ", CONDITIONVALUE=" + this.conditionValue
                + ", STARTT=" + this.start
                + ", ENDT=" + this.end
                + " WHERE DISCOUNTS.ID=" + this.getId();
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

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
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

    @Override
    public String toString() {
        return this.name;
    }
}
