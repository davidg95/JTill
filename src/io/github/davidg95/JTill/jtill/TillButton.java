/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.awt.Color;
import java.io.Serializable;

/**
 * Models a till button.
 *
 * @author David
 */
public class TillButton implements Serializable {

    private int id;
    private String name;
    private int item;
    private int screen;
    private int color;
    private int width;
    private int height;
    private int x;
    private int y;

    public TillButton(String name, int item, int screen, int color, int width, int height, int x, int y) {
        this.name = name;
        this.item = item;
        this.screen = screen;
        this.color = color;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public TillButton(String name, int item, int screen, int color, int id, int width, int height, int x, int y) {
        this(name, item, screen, color, width, height, x, y);
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + item
                + "," + this.color
                + "," + this.screen
                + "," + this.width
                + "," + this.height
                + "," + this.x
                + "," + this.y;
    }

    public String getSQLUpdateString() {
        return "UPDATE BUTTONS"
                + " SET NAME='" + this.getName()
                + "', PRODUCT=" + this.getItem()
                + ", COLOR=" + this.getColorValue()
                + ", SCREEN_ID=" + this.getScreen()
                + ", WIDTH=" + this.getWidth()
                + ", HEIGHT=" + this.getHeight()
                + ", XPOS=" + this.getX()
                + ", YPOS=" + this.getY()
                + " WHERE BUTTONS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
