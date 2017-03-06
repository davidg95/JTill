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
    private Item item;
    private Screen screen;
    private int color;

    public TillButton(String name, Item item, Screen screen, int color) {
        this.name = name;
        this.item = item;
        this.screen = screen;
        this.color = color;
    }

    public TillButton(String name, Item item, Screen screen, int color, int id) {
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        this.name = item.getName();
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
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
        int pId;
        if (this.item == null) {
            pId = -1;
        } else {
            pId = this.item.getId();
        }
        return "'" + this.name
                + "'," + pId
                + "," + this.color
                + "," + this.screen.getId();
    }

    public String getSQLUpdateString() {
        int iId;
        if (this.item == null) {
            iId = -1;
        } else {
            iId = this.item.getId();
        }
        return "UPDATE BUTTONS"
                + " SET NAME='" + this.getName()
                + "', PRODUCT=" + iId
                + ", COLOR=" + this.getColorValue()
                + ", SCREEN_ID=" + this.getScreen().getId()
                + " WHERE BUTTONS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
