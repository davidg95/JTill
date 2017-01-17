/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author David
 */
public class Screen implements Serializable {

    private int id;
    private String name;
    private int order;
    private int color;

    public Screen(String name, int order, int color) {
        this.name = name;
        this.order = order;
        this.color = color;
    }

    public Screen(String name, int order, int color, int id) {
        this(name, order, color);
        this.id = id;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getColorValue() {
        return color;
    }

    public void setColorValue(int color) {
        this.color = color;
    }

    public Color getColor() {
        return new Color(color);
    }

    public void setColor(Color c) {
        this.color = c.getRGB();
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.order
                + "," + this.color;
    }

    public String getSQLUpdateString() {
        return "UPDATE SCREENS"
                + " SET NAME='" + this.getName()
                + "', POSITION=" + this.getOrder()
                + ", COLOR=" + this.getColorValue()
                + " WHERE SCREENS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return name;
    }

}
