/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Class which models a SaleItem.
 *
 * @author David
 */
public class SaleItem implements Serializable {

    private int id;
    private Product product;
    private int quantity;
    private BigDecimal individualPrice;
    private BigDecimal individualCost;
    private BigDecimal individualTax;
    
    private String name; //The name variable for the list box on the terminal.

    public SaleItem(int id, Product product, int quantity, BigDecimal individualPrice, BigDecimal individualCost, BigDecimal individualTax) {
        this(product, quantity, individualPrice, individualCost, individualTax);
        this.id = id;
    }

    public SaleItem(Product product, int quantity, BigDecimal individualPrice, BigDecimal individualCost, BigDecimal individualTax) {
        this.product = product;
        this.quantity = quantity;
        this.individualPrice = individualPrice;
        this.individualCost = individualCost;
        this.individualTax = individualTax;
        this.name = product.getShortName();
    }

    public String getName(){
        return name;
    }
    public BigDecimal getTotalPrice() {
        return individualPrice.multiply(new BigDecimal(quantity));
    }

    public BigDecimal getTotalCost() {
        return individualCost.multiply(new BigDecimal(quantity));
    }

    public BigDecimal getTotalTax() {
        return individualTax.multiply(new BigDecimal(quantity));
    }
    
    public int increaceQuantity(int amount){
        return quantity += amount;
    }
    
    public int decreaceQuantity(int amount){
        return quantity -= amount;
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
    }

    public BigDecimal getIndividualPrice() {
        return individualPrice;
    }

    public void setIndividualPrice(BigDecimal individualPrice) {
        this.individualPrice = individualPrice;
    }

    public BigDecimal getIndividualCost() {
        return individualCost;
    }

    public void setIndividualCost(BigDecimal individualCost) {
        this.individualCost = individualCost;
    }

    public BigDecimal getIndividualTax() {
        return individualTax;
    }

    public void setIndividualTax(BigDecimal individualTax) {
        this.individualTax = individualTax;
    }

    public boolean isRefundItem() {
        return quantity < 0;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.id;
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
        final SaleItem other = (SaleItem) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return product.getLongName() + " - " + this.quantity + " x £" + this.getIndividualPrice();
    }

    public String getSQLInsertStatement() {
        return "'" + this.product.getBarcode()
                + "'," + this.quantity
                + "," + this.individualPrice.doubleValue()
                + "," + this.individualTax.doubleValue()
                + "," + this.individualCost;
    }
}
