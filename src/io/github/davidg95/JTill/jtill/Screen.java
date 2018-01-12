/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private int inherits;
    private int vGap;
    private int hGap;

    private transient GridPane pane;

    public Screen(String name, int width, int height, int inherits, int vgap, int hgap) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.inherits = inherits;
        this.vGap = vgap;
        this.hGap = hgap;
    }

    public Screen(String name, int id, int width, int height, int inherits, int vgap, int hgap) {
        this(name, width, height, inherits, vgap, hgap);
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

    public int getInherits() {
        return inherits;
    }

    public void setInherits(int inherits) {
        this.inherits = inherits;
    }

    /**
     * Get the screens buttons.
     *
     * @return a List of buttons.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws ScreenNotFoundException if the screen was not found.
     */
    public List<TillButton> getButtons() throws IOException, SQLException, ScreenNotFoundException {
        return DataConnect.dataconnect.getButtonsOnScreen(this);
    }

    /**
     * Save the screen to the database.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.dataconnect.updateScreen(this);
        } catch (ScreenNotFoundException ex) {
            DataConnect.dataconnect.addScreen(this);
        }
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.getWidth()
                + "," + this.getHeight()
                + "," + this.getInherits()
                + "," + this.getvGap()
                + "," + this.gethGap();
    }

    public int getvGap() {
        return vGap;
    }

    public void setvGap(int vGap) {
        this.vGap = vGap;
    }

    public int gethGap() {
        return hGap;
    }

    public void sethGap(int hGap) {
        this.hGap = hGap;
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
                + ", INHERITS=" + this.getInherits()
                + ", VGAP=" + this.getvGap()
                + ", HGAP=" + this.gethGap()
                + " WHERE SCREENS.ID=" + this.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Screen)) {
            return false;
        }
        Screen sc = (Screen) o;
        return sc.getId() == this.getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.id;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + this.width;
        hash = 79 * hash + this.height;
        return hash;
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
