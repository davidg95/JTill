/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

/**
 *
 * @author David
 */
public class Category {

    private int ID;
    private String name;

    public Category(int ID, String name) {
        this(name);
        this.ID = ID;
    }

    public Category(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    @Override
    public String toString() {
        return this.name;
    }
}
