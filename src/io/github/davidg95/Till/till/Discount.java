/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

/**
 *
 * @author David
 */
public class Discount {

    private int id;
    private String name;
    private double percentage;

    public Discount(int id, String name, double percentage) {
        this(name, percentage);
        this.id = id;
    }

    public Discount(String name, double percentage) {
        this.name = name;
        this.percentage = percentage;
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

    public void setType(String name) {
        this.name = name;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    
    public String getSQLInsertString(){
        return "'" + this.name
                + "'," + this.percentage;
    }
    
    public String getSQLUpdateString(){
        return "UPDATE DISCOUNTS"
                + " SET NAME='" + this.getName()
                + "', PERCENTAGE=" + this.getPercentage()
                + " WHERE DISCOUNTS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
