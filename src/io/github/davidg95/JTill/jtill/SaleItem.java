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
    private int sale;
    private int type;
    private boolean refundItem;

    private Item item;

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

    public SaleItem(int sale, Item item, int quantity, int id, BigDecimal price, int type, BigDecimal tax) {
        this(sale, item, quantity, price, type, tax);
        this.id = id;
    }

    public SaleItem(int sale, Item item, int quantity, BigDecimal price, int type, BigDecimal tax) {
        this.sale = sale;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.taxValue = tax;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
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

    public String getSQLInsertStatement() {
        return this.item.getId()
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
        return this.item.getId() == other.item.getId() && this.type == other.type;
    }

    @Override
    public String toString() {
        DecimalFormat df;
        if (price.multiply(new BigDecimal(quantity)).compareTo(BigDecimal.ZERO) > 1) {
            df = new DecimalFormat("#.00");
        } else {
            df = new DecimalFormat("0.00");
        }
        return "Qty. " + this.getQuantity() + "\t" + this.getItem().getId() + "\t\t\t£" + df.format(this.getPrice());

    }
}
