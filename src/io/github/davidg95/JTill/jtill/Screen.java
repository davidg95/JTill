/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

/**
 * Models a till screen. Each screen on the till has custom buttons on it.
 *
 * @author David
 */
public class Screen implements Serializable {

    private int id;
    private String name;

    public Screen(String name) {
        this.name = name;
    }

    public Screen(String name, int id) {
        this(name);
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

    public String getSQLInsertString() {
        return "'" + this.name + "'";
    }

    public String getSQLUpdateString() {
        return "UPDATE SCREENS"
                + " SET NAME='" + this.getName()
                + "' WHERE SCREENS.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return name;
    }

}
