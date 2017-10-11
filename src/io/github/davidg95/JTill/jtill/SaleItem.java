/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Class which models a SaleItem.
 *
 * @author David
 */
public class SaleItem implements Serializable {

    private int id;
    private String name;
    private int quantity;
    private BigDecimal price;
    private String totalPrice;
    private BigDecimal taxValue;
    private BigDecimal cost;
    private int sale;
    private int type;
    private boolean refundItem;
    private int item;
    
    private Item product;

    /**
     * Indicates the sale item is a Product.
     *
     * Value = 1.
     */
    public static final int PRODUCT = 1;
    /**
     * Indicates the sale item is a discount.
     *
     * Value = 2.
     */
    public static final int DISCOUNT = 2;

    public SaleItem(int sale, int item, int quantity, int id, BigDecimal price, int type, BigDecimal tax, BigDecimal cost) {
        this(sale, item, quantity, price, type, tax, cost);
        this.id = id;
    }

    public SaleItem(int sale, int item, int quantity, BigDecimal price, int type, BigDecimal tax, BigDecimal cost) {
        this.sale = sale;
        this.item = item;
        this.quantity = quantity;
        this.price = price.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.type = type;
        this.taxValue = tax.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.cost = cost.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * Method to increase the quantity of the item.
     *
     * @param quantity the quantity to add.
     * @return the new quantity.
     */
    public int increaseQuantity(int quantity) {
        this.quantity += quantity;
        return quantity;
    }

    /**
     * Method to decrease the quantity of the item.
     *
     * @param quantity the quantity to remove.
     * @return the new quantity.
     */
    public int decreaseQuantity(int quantity) {
        this.quantity -= quantity;
        return quantity;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isRefundItem() {
        return refundItem;
    }

    public void setRefundItem(boolean refundItem) {
        this.refundItem = refundItem;
    }

    public BigDecimal getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(BigDecimal taxValue) {
        this.taxValue = taxValue;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Item getProduct() {
        return product;
    }

    public void setProduct(Item product) {
        this.product = product;
    }

    public String getSQLInsertStatement() {
        return this.item
                + "," + type
                + "," + this.quantity
                + "," + this.price.doubleValue()
                + "," + this.taxValue.doubleValue()
                + "," + this.sale;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final SaleItem other = (SaleItem) obj;
        return this.item == other.item && this.type == other.type && this.quantity == other.quantity;
    }

    @Override
    public String toString() {
        DecimalFormat df;
        if (price.multiply(new BigDecimal(quantity)).compareTo(BigDecimal.ZERO) > 1) {
            df = new DecimalFormat("#.00");
        } else {
            df = new DecimalFormat("0.00");
        }
        return "Qty. " + this.getQuantity() + "\t" + this.getItem() + "\t\t\t£" + df.format(this.getPrice());

    }
}
