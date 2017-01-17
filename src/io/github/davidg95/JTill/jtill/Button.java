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
public class Button implements Serializable {

    private int id;
    private String name;
    private int order;
    private int product_id;
    private int screen_id;
    private int color;

    public Button(String name, int product_id, int order, int screen_id, int color) {
        this.name = name;
        this.product_id = product_id;
        this.order = order;
        this.screen_id = screen_id;
        this.color = color;
    }

    public Button(String name, int product_id, int order, int screen_id, int color, int id) {
        this(name, product_id, order, screen_id, color);
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

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getScreen_id() {
        return screen_id;
    }

    public void setScreen_id(int screen_id) {
        this.screen_id = screen_id;
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
                + "," + this.product_id
                + "," + this.color
                + "," + this.screen_id;
    }

    public String getSQLUpdateString() {
        return "UPDATE BUTTONS"
                + " SET NAME='" + this.getName()
                + "', POSITION=" + this.getOrder()
                + ", PRODUCT=" + this.getProduct_id()
                + ", COLOR=" + this.getColorValue()
                + ", SCREEN_ID=" + this.getScreen_id()
                + " WHERE BUTTONS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
