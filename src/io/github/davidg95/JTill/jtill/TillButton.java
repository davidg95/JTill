/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

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
    private String color;
    private String fontColor;
    private int width;
    private int height;
    private int x;
    private int y;
    private int accessLevel;
    private String link;

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
    /**
     * Indicates that the button is to log off.
     */
    public static final int LOGOFF = 6;
    /**
     * Indicates that this button is for the payment screen.
     */
    public static final int PAYMENT = 7;
    /**
     * Indicates that this button is a void button.
     */
    public static final int VOID = 8;
    /**
     * Indicates that the button opens a web link.
     */
    public static final int LINK = 9;

    public TillButton(String name, int item, int type, int screen, String color, String fontColor, int width, int height, int x, int y, int accessLevel, String link) {
        this.name = name;
        this.item = item;
        this.screen = screen;
        this.color = color;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.type = type;
        this.fontColor = fontColor;
        this.accessLevel = accessLevel;
        this.link = link;
    }

    public TillButton(String name, int item, int type, int screen, String color, String fontColor, int id, int width, int height, int x, int y, int accessLevel, String link) {
        this(name, item, type, screen, color, fontColor, width, height, x, y, accessLevel, link);
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

    public String getColorValue() {
        return color;
    }

    public void setColorValue(String color) {
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

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Save a button to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.dataconnect.updateButton(this);
        } catch (JTillException ex) {
            DataConnect.dataconnect.addButton(this);
        }
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + item
                + "," + type
                + ",'" + this.color
                + "','" + this.fontColor
                + "'," + this.screen
                + "," + this.width
                + "," + this.height
                + "," + this.x
                + "," + this.y
                + "," + this.accessLevel
                + ",'" + this.link + "'";
    }

    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(0, 2), 16),
                Integer.valueOf(colorStr.substring(2, 4), 16),
                Integer.valueOf(colorStr.substring(4, 6), 16));
    }

    public static String rbg2Hex(Color c) {
        String hRed = Integer.toHexString(c.getRed());
        String hGreen = Integer.toHexString(c.getGreen());
        String hBlue = Integer.toHexString(c.getBlue());
        if (hRed.length() == 1) {
            hRed += hRed;
        }
        if (hGreen.length() == 1) {
            hGreen += hGreen;
        }
        if (hBlue.length() == 1) {
            hBlue += hBlue;
        }
        String hex = hRed + hGreen + hBlue;
        return hex;
    }

    public String getSQLUpdateString() {
        return "UPDATE BUTTONS"
                + " SET NAME='" + this.getName()
                + "', PRODUCT=" + this.getItem()
                + ", TYPE=" + this.getType()
                + ", COLOR='" + this.getColorValue()
                + "',FONT_COLOR='" + this.getFontColor()
                + "', SCREEN_ID=" + this.getScreen()
                + ", WIDTH=" + this.getWidth()
                + ", HEIGHT=" + this.getHeight()
                + ", XPOS=" + this.getX()
                + ", YPOS=" + this.getY()
                + ", ACCESS_LEVEL=" + this.getAccessLevel()
                + ", LINK='" + this.getLink()
                + "' WHERE BUTTONS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
