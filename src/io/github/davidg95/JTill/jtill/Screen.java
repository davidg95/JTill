/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import javafx.scene.layout.GridPane;

/**
 * Models a till screen. Each screen on the till has custom buttons on it.
 *
 * @author David
 */
public class Screen implements Serializable, Cloneable {

    private int id;
    private String name;
    private int width;
    private int height;

    private transient GridPane pane;

    public Screen(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public Screen(String name, int id, int width, int height) {
        this(name, width, height);
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

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.getWidth()
                + "," + this.getHeight();
    }

    public GridPane getPane() {
        return pane;
    }

    public void setPane(GridPane pane) {
        this.pane = pane;
    }

    public String getSQLUpdateString() {
        return "UPDATE SCREENS"
                + " SET NAME='" + this.getName()
                + "', WIDTH=" + this.getWidth()
                + ", HEIGHT=" + this.getHeight()
                + " WHERE SCREENS.ID=" + this.getId();
    }

    @Override
    public Screen clone() {
        try {
            final Screen result = (Screen) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return name;
    }

}
