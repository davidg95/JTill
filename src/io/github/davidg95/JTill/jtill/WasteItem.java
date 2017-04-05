/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

/**
 *
 * @author David
 */
public class WasteItem implements Serializable, JTillObject {

    private int id;
    private Product product;
    private int quantity;
    private int reason;

    public WasteItem(int id, Product product, int quantity, int reason) {
        this(product, quantity, reason);
        this.id = id;
    }

    public WasteItem(Product product, int quantity, int reason) {
        this.product = product;
        this.quantity = quantity;
        this.reason = reason;
    }

    @Override
    public String getName() {
        return this.product.getName();
    }

    @Override
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
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
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
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id + " - " + reason;
    }

}
