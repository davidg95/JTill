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
    private Item item;
    private int quantity;
    private BigDecimal price;
    private Sale sale;

    public SaleItem(Sale sale, Item item, int quantity, int id, BigDecimal price) {
        this(sale, item, quantity);
        this.id = id;
        this.price = price;
        this.price = price.setScale(2);
    }

    public SaleItem(Sale sale, Item item, int quantity) {
        this.sale = sale;
        this.item = item;
        this.quantity = quantity;
        this.price = item.getPrice().multiply(new BigDecimal(Integer.toString(quantity)));
        this.price = price.setScale(2);
    }

    public SaleItem(Sale sale, Product product) {
        this(sale, product, 1);
    }

    /**
     * Method to increase the quantity of the item.
     *
     * @param quantity the quantity to add.
     * @return the new quantity.
     */
    public BigDecimal increaseQuantity(int quantity) {
        this.quantity += quantity;
        BigDecimal inc = item.getPrice().multiply(new BigDecimal(Integer.toString(quantity)));
        this.price = this.price.add(inc);
        return inc;
    }

    /**
     * Method to decrease the quantity of the item.
     *
     * @param quantity the quantity to remove.
     * @return the new quantity.
     */
    public BigDecimal decreaseQuantity(int quantity) {
        this.quantity -= quantity;
        BigDecimal dec = item.getPrice().multiply(new BigDecimal(Integer.toString(quantity)));
        this.price = this.price.subtract(dec);
        return dec;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.price = this.item.getPrice().multiply(new BigDecimal(Integer.toString(quantity)));
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

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public String getSQLInsertStatement() {
        String type = "product";
        if (this.item instanceof Discount) {
            type = "discount";
        }
        return this.item.getId()
                + ",'" + type
                + "'," + this.quantity
                + "," + this.price.doubleValue()
                + "," + this.sale.getId();
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
        return this.item.getId() == other.item.getId() && (this.item instanceof Discount) == (other.item instanceof Discount);
    }

    @Override
    public String toString() {
        DecimalFormat df;
        if (item.getPrice().compareTo(BigDecimal.ZERO) > 1) {
            df = new DecimalFormat("#.00");
        } else {
            df = new DecimalFormat("0.00");
        }
        if (this.getItem().getName().length() < 4) {
            return "Qty. " + this.getQuantity() + "\t" + this.getItem().getName() + "\t\t\t£" + df.format(this.getPrice());
        } else {
            return "Qty. " + this.getQuantity() + "\t" + this.getItem().getName() + "\t£" + df.format(this.getPrice());
        }
    }
}
