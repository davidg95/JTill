/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author David
 */
public class Category {

    private int ID;
    private String name;
    private Time startSell;
    private Time endSell;
    private boolean timeRestrict;
    private int minAge;

    public Category(int ID, String name, Time startSell, Time endSell, boolean timeRestrict, int minAge) {
        this(name, startSell, endSell, timeRestrict, minAge);
        this.ID = ID;
    }

    public Category(String name, Time startSell, Time endSell, boolean timeRestrict, int minAge) {
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

    public String getSQLInsertString() {
        return "'" + this.name
                + "','" + this.startSell.toString()
                + "','" + this.endSell.toString()
                + "','" + this.timeRestrict
                + "'," + this.minAge;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
