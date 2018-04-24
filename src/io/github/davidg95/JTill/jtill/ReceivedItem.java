/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author David
 */
public class ReceivedItem implements Serializable {

    private int id;
    private int quantity;
    private int packs;
    private BigDecimal total;

    private Product product;

    public ReceivedItem(int id, Product product, int quantity, int packs, BigDecimal total) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.total = total;
        this.packs = packs;
    }

    public ReceivedItem(Product product, int quantity, int packs) {
        this.product = product;
        this.quantity = quantity;
        this.packs = packs;
        updateTotal();
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
        updateTotal();
    }

    public int getPacks() {
        return packs;
    }

    public void setPacks(int packs) {
        this.packs = packs;
        updateTotal();
    }

    public int getTotalAmount() {
        return (this.packs * this.product.getPackSize()) + this.quantity;
    }

    public void updateTotal() {
        this.total = (this.product.getCostPrice().divide(new BigDecimal(product.getPackSize()))).multiply(new BigDecimal(quantity)).setScale(2, 6);
        this.total = this.total.add(this.product.getCostPrice().multiply(new BigDecimal(packs)).setScale(2, 6));
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal price) {
        this.total = price;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.id;
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
        final ReceivedItem other = (ReceivedItem) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return product.getLongName();
    }

}
