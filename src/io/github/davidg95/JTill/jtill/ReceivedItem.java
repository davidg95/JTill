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
public class ReceivedItem implements Serializable{

    private int id;
    private int quantity;
    private BigDecimal price;
    
    private Product product;

    public ReceivedItem(int id, Product product, int quantity, BigDecimal price) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public ReceivedItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = product.getCostPrice().divide(new BigDecimal(product.getPackSize()), 2, 6).multiply(new BigDecimal(quantity));
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
        this.price = this.product.getCostPrice().divide(new BigDecimal(product.getPackSize())).multiply(new BigDecimal(quantity)).setScale(2, 6);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
