/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author David
 */
public class WasteItem implements Serializable {

    private int id;
    private Product product;
    private BigDecimal totalValue;
    private int quantity;
    private WasteReason reason;
    private Date timestamp;

    public WasteItem(int id, Product product, int quantity, WasteReason reason, BigDecimal value, Date timestamp) {
        this(product, quantity, reason, timestamp);
        this.id = id;
        this.totalValue = value;
    }

    public WasteItem(Product product, int quantity, WasteReason reason, Date timestamp) {
        this.product = product;
        this.quantity = quantity;
        this.reason = reason;
        this.totalValue = product.getIndividualCost().multiply(new BigDecimal(quantity));
        this.timestamp = timestamp;
    }

    public String getName() {
        return this.product.getShortName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.product.isOpen()) {
            return;
        }
        this.totalValue = this.product.getCostPrice().divide(new BigDecimal(product.getPackSize())).multiply(new BigDecimal(quantity)).setScale(2, 6);
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public WasteReason getReason() {
        return reason;
    }

    public void setReason(WasteReason reason) {
        this.reason = reason;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
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
        final WasteItem other = (WasteItem) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return id + " - " + reason;
    }

}
