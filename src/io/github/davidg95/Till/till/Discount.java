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

    private String id;
    private String name;
    private double percentage;

    public Discount(String id, String name, double percentage) {
        this(name, percentage);
        this.id = id;
    }

    public Discount(String name, double percentage) {
        this.name = name;
        this.percentage = percentage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @Override
    public String toString() {
        return "ID: " + this.id + "\nName: " + this.name + "\nPercentage: " + this.percentage;
    }
}
