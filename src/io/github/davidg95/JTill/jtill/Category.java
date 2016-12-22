/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.awt.Color;
import java.io.Serializable;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author David
 */
public class Category implements Serializable {

    private int ID;
    private String name;
    private Time startSell;
    private Time endSell;
    private boolean timeRestrict;
    private int minAge;
    private boolean button;
    private int color;

    public Category(int ID, String name, Time startSell, Time endSell, boolean timeRestrict, int minAge, boolean button, int color) {
        this(name, startSell, endSell, timeRestrict, minAge, button, color);
        this.ID = ID;
    }

    public Category(String name, Time startSell, Time endSell, boolean timeRestrict, int minAge, boolean button, int color) {
        this.name = name;
        this.timeRestrict = timeRestrict;
        this.startSell = startSell;
        this.endSell = endSell;
        if (!timeRestrict) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                this.startSell = new Time(sdf.parse("00:00:00").getTime());
                this.endSell = new Time(sdf.parse("00:00:00").getTime());
            } catch (ParseException ex) {
            }
        }
        this.minAge = minAge;
        this.button = button;
        this.color = color;
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

    public Time getStartSell() {
        return startSell;
    }

    public void setStartSell(Time startSell) {
        this.startSell = startSell;
    }

    public Time getEndSell() {
        return endSell;
    }

    public void setEndSell(Time endSell) {
        this.endSell = endSell;
    }

    public boolean isTimeRestrict() {
        return timeRestrict;
    }

    public void setTimeRestrict(boolean timeRestrict) {
        this.timeRestrict = timeRestrict;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public boolean isButton() {
        return button;
    }

    public void setButton(boolean button) {
        this.button = button;
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
                + "','" + this.startSell.toString()
                + "','" + this.endSell.toString()
                + "','" + this.timeRestrict
                + "','" + this.button
                + "'," + this.color
                + "," + this.minAge;
    }

    public String getSQLUpdateString() {
        if (this.isTimeRestrict()) {
            return "UPDATE CATEGORYS"
                    + " SET NAME='" + this.getName()
                    + "', SELL_START='" + this.getStartSell()
                    + "', SELL_END='" + this.getEndSell()
                    + "', TIME_RESTRICT=" + this.isTimeRestrict()
                    + ", BUTTON=" + this.isButton()
                    + ", COLOR=" + this.getColorValue()
                    + ", MINIMUM_AGE=" + this.getMinAge()
                    + " WHERE CATEGORYS.ID=" + this.getID();
        } else {
            return "UPDATE CATEGORYS"
                    + " SET NAME='" + this.getName()
                    + "', SELL_START=" + this.getStartSell()
                    + ", SELL_END=" + this.getEndSell()
                    + ", TIME_RESTRICT=" + this.isTimeRestrict()
                    + ", BUTTON=" + this.isButton()
                    + ", COLOR=" + this.getColorValue()
                    + ", MINIMUM_AGE=" + this.getMinAge()
                    + " WHERE CATEGORYS.ID=" + this.getID();
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
