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
    private BigDecimal totalValue;
    private Date date;
    private List<WasteItem> items;

    public WasteReport(int id, BigDecimal totalValue, Date date) {
        this(totalValue, date);
        this.id = id;
    }

    public WasteReport(BigDecimal totalValue, Date date) {
        this.totalValue = totalValue;
        this.date = date;
    }

    public WasteReport(Date date) {
        this(BigDecimal.ZERO, date);
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
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
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
