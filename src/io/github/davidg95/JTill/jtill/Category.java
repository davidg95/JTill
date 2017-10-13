/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Class which models a category.
 *
 * @author David
 */
public class Category implements Serializable, JTillObject {

    private int ID;
    private String name;
    private Time startSell;
    private Time endSell;
    private boolean timeRestrict;
    private int minAge;
    
    private BigDecimal sales = BigDecimal.ZERO;

    /**
     * Constructor which takes in all values.
     *
     * @param ID the id.
     * @param name the name.
     * @param startSell the time which items in this category may be sold.
     * @param endSell the time which items in this category will not be allowed
     * to be sold.
     * @param timeRestrict if the time restrictions should apply.
     * @param minAge the minimum age for items in the category.
     */
    public Category(int ID, String name, Time startSell, Time endSell, boolean timeRestrict, int minAge) {
        this(name, startSell, endSell, timeRestrict, minAge);
        this.ID = ID;
    }

    /**
     * Constructor which takes in all values except id.
     *
     * @param name the name.
     * @param startSell the time which items in this category may be sold.
     * @param endSell the time which items in this category will not be allowed
     * to be sold.
     * @param timeRestrict if the time restrictions should apply.
     * @param minAge the minimum age for items in the category.
     */
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

    /**
     * Returns true or false indicating whether the time passed in is within the
     * selling time of the category.
     *
     * @param t the time to compare.
     * @return true or false.
     */
    public boolean isSellTime(Time t) {
        return t.after(startSell) && t.before(endSell);
    }

    @Override
    public int getId() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
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
    
    public void addToSales(BigDecimal toAdd) {
        sales = sales.add(toAdd);
    }

    public BigDecimal getSales() {
        return sales;
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "','" + this.startSell.toString()
                + "','" + this.endSell.toString()
                + "','" + this.timeRestrict
                + "'," + this.minAge;
    }

    public String getSQLUpdateString() {
        if (this.isTimeRestrict()) {
            return "UPDATE CATEGORYS"
                    + " SET NAME='" + this.getName()
                    + "', SELL_START='" + this.getStartSell().toString()
                    + "', SELL_END='" + this.getEndSell().toString()
                    + "', TIME_RESTRICT=" + this.isTimeRestrict()
                    + ", MINIMUM_AGE=" + this.getMinAge()
                    + " WHERE CATEGORYS.ID=" + this.getId();
        } else {
            return "UPDATE CATEGORYS"
                    + " SET NAME='" + this.getName()
                    + "', SELL_START='" + this.getStartSell().toString()
                    + "', SELL_END='" + this.getEndSell().toString()
                    + "', TIME_RESTRICT=" + this.isTimeRestrict()
                    + ", MINIMUM_AGE=" + this.getMinAge()
                    + " WHERE CATEGORYS.ID=" + this.getId();
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.ID;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Category other = (Category) obj;
        return this.ID == other.ID;
    }
    
    @Override
    public String toString() {
        return this.ID + " - " + this.name;
    }
}
