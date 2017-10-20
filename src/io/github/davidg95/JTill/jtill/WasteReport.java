/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author David
 */
public class WasteReport implements Serializable, JTillObject {

    private int id;
    private Date date;
    private List<WasteItem> items;

    public WasteReport(int id, Date date) {
        this(date);
        this.id = id;
    }

    public WasteReport(Date date) {
        this.date = date;
    }

    @Override
    public String getName() {
        return this.date.toString();
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getTotalValue() {
        BigDecimal value = BigDecimal.ZERO;
        for (WasteItem i : items) {
            value = value.add(i.getTotalValue());
        }
        return value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<WasteItem> getItems() {
        return items;
    }

    public void setItems(List<WasteItem> items) {
        this.items = items;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id;
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
        final WasteReport other = (WasteReport) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id + "";
    }

}
