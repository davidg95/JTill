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
public class SaleItem implements Serializable {

    private Product product;
    private int quantity;
    private BigDecimal price;

    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice().multiply(new BigDecimal(Integer.toString(quantity)));
    }

    public SaleItem(Product product) {
        this(product, 1);
    }

    public BigDecimal increaseQuantity(int quantity) {
        this.quantity += quantity;
        BigDecimal inc = product.getPrice().multiply(new BigDecimal(Integer.toString(quantity)));
        this.price = this.price.add(inc);
        return inc;
    }

    public BigDecimal decreaseQuantity(int quantity) {
        this.quantity -= quantity;
        BigDecimal dec = product.getPrice().multiply(new BigDecimal(Integer.toString(quantity)));
        this.price = this.price.subtract(dec);
        return dec;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return this.product.getShortName()
                + "\n" + this.quantity
                + "\n" + this.price;
    }
}
