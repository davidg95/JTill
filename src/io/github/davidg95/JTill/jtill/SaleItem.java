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
    private boolean refundItem;
    private Product product;

    /**
     * Indicates the sale item is a Product.
     *
     * Value = 1.
     */
    public static final int PRODUCT = 1;

    public SaleItem(int sale, Product item, int quantity, int id, BigDecimal price, BigDecimal tax, BigDecimal cost) {
        this(sale, item, quantity, price, tax, cost);
        this.id = id;
    }

    public SaleItem(int sale, Product item, int quantity, BigDecimal price, BigDecimal tax, BigDecimal cost) {
        this.sale = sale;
        this.product = item;
        this.quantity = quantity;
        this.price = price.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.taxValue = tax.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.cost = cost.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        if (quantity < 0) {
            this.price = this.price.negate();
        }
    }

    /**
     * Method to increase the quantity of the item.
     *
     * @param quantity the quantity to add.
     * @return the new quantity.
     */
    public int increaseQuantity(int quantity) {
        this.cost = cost.divide(new BigDecimal(this.quantity)).multiply(new BigDecimal(this.quantity + quantity));
        this.price = price.divide(new BigDecimal(this.quantity)).multiply(new BigDecimal(this.quantity + quantity));
        this.taxValue = taxValue.divide(new BigDecimal(this.quantity)).multiply(new BigDecimal(this.quantity + quantity));
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

    public String getName() {
        String con = "";
        if (product instanceof Product) {
            for (Condiment c : ((Product) product).getSaleCondiments()) {
                con += "\n    - " + c.getProduct_con().getName();
            }
        }
        return name + con;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product item) {
        this.product = item;
    }

    public String getSQLInsertStatement() {
        return "'" + this.product.getBarcode()
                + "'," + this.quantity
                + "," + this.price.doubleValue()
                + "," + this.taxValue.doubleValue()
                + "," + this.sale
                + "," + this.cost;
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
        return this.product.getBarcode().equals(other.product.getBarcode()) && this.quantity == other.quantity;
    }

    @Override
    public String toString() {
        DecimalFormat df;
        if (price.multiply(new BigDecimal(quantity)).compareTo(BigDecimal.ZERO) > 1) {
            df = new DecimalFormat("#.00");
        } else {
            df = new DecimalFormat("0.00");
        }
        String con = "";
        if (product instanceof Product) {
            for (Condiment c : ((Product) product).getSaleCondiments()) {
                con += "\n    - " + c.getProduct_con().getName();
            }
        }
        return "Qty. " + this.getQuantity() + "\t" + this.getProduct() + con + "\t\t\t£" + df.format(this.getPrice());

    }
}
