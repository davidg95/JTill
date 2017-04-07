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
public class TillButton implements Serializable {

    private int id;
    private String name;
    private int item;
    private int screen;
    private int color;

    public TillButton(String name, int item, int screen, int color) {
        this.name = name;
        this.item = item;
        this.screen = screen;
        this.color = color;
    }

    public TillButton(String name, int item, int screen, int color, int id) {
        this(name, item, screen, color);
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

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getScreen() {
        return screen;
    }

    public void setScreen(int screen) {
        this.screen = screen;
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
                + "'," + item
                + "," + this.color
                + "," + this.screen;
    }

    public String getSQLUpdateString() {
        return "UPDATE BUTTONS"
                + " SET NAME='" + this.getName()
                + "', PRODUCT=" + this.getItem()
                + ", COLOR=" + this.getColorValue()
                + ", SCREEN_ID=" + this.getScreen()
                + " WHERE BUTTONS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
