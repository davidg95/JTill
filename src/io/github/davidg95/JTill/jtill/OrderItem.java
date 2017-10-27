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
public class OrderItem implements Serializable {

    private int id;
    private Product product;
    private int quantity;
    private BigDecimal price;

    public OrderItem(int id, Product product, int quantity) {
        this(product, quantity);
        this.id = id;
    }

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = product.getCostPrice().multiply(new BigDecimal(quantity));
    }

    public OrderItem(int id, Product product, int quantity, BigDecimal price) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
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

    public void setProduct(Product p) {
        this.product = p;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.price = product.getPrice().multiply(new BigDecimal(quantity));
    }

    public BigDecimal getPrice() {
        return price;
    }
    
    public int getOrderCode(){
        return product.getOrder_code();
    }
    
    public String getName(){
        return product.getLongName();
    }

    @Override
    public String toString() {
        return "OrderItem{" + "id=" + id + ", p=" + product + ", quantity=" + quantity + '}';
    }
}
