/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

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
    private int type;
    private int screen;
    private int color;
    private int width;
    private int height;
    private int x;
    private int y;

    /**
     * Indicates that the color of the button is blue.
     *
     * Value: 1.
     */
    public static final int BLUE = 1;
    /**
     * Indicates that the color of the button is red.
     *
     * Value: 2.
     */
    public static final int RED = 2;
    /**
     * Indicates that the color of the button is green.
     *
     * Value: 3.
     */
    public static final int GREEN = 3;
    /**
     * Indicates that the color of the button is yellow.
     *
     * Value: 4.
     */
    public static final int YELLOW = 4;
    /**
     * Indicates that the color of the button is orange.
     *
     * Value: 5.
     */
    public static final int ORANGE = 5;
    /**
     * Indicates that the color of the button is purple.
     *
     * Value: 6.
     */
    public static final int PURPLE = 6;
    /**
     * Indicates that the color of the button is white.
     *
     * Value: 7.
     */
    public static final int WHITE = 7;
    /**
     * Indicates that the color of the button is black.
     *
     * Value: 8.
     */
    public static final int BLACK = 8;

    /**
     * Indicates that the button is for a product.
     *
     * Value: 1.
     */
    public static final int ITEM = 1;
    /**
     * Indicates that the button is for another screen.
     *
     * Value: 2.
     */
    public static final int SCREEN = 2;
    /**
     * Indicates that the button is a space.
     *
     * Value: 3.
     */
    public static final int SPACE = 3;
    /**
     * Indicates the button is to go back to the previous screen.
     */
    public static final int BACK = 4;
    /**
     * Indicates the button is to go to the tills main screen.
     */
    public static final int MAIN = 5;

    public TillButton(String name, int item, int type, int screen, int color, int width, int height, int x, int y) {
        this.name = name;
        this.item = item;
        this.screen = screen;
        this.color = color;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public TillButton(String name, int item, int type, int screen, int color, int id, int width, int height, int x, int y) {
        this(name, item, type, screen, color, width, height, x, y);
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + item
                + "," + type
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
                + ", TYPE=" + this.getType()
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
