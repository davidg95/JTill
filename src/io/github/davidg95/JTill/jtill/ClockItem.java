/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author David
 */
public class ClockItem implements Serializable {

    private int id;
    private int staff;
    private Date time;
    private int type;

    /**
     * Indicates that the staff member is clocking on.
     */
    public static final int CLOCK_ON = 0;

    /**
     * Indicates that the staff member is clocking off.
     */
    public static final int CLOCK_OFF = 1;

    public ClockItem(int id, int staff, Date time, int type) {
        this(staff, time, type);
        this.id = id;
    }

    public ClockItem(int staff, Date time, int type) {
        this.staff = staff;
        this.time = time;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStaff() {
        return staff;
    }

    public void setStaff(int staff) {
        this.staff = staff;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.id;
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
        final ClockItem other = (ClockItem) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "ID- " + id + " Staff- " + staff + " Time- " + time + " Type- " + (type == ClockItem.CLOCK_ON ? "ON" : "OFF");
    }
}
