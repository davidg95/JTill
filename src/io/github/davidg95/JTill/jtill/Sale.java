/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class Sale implements Serializable {

    private int code;
    private List<SaleItem> saleItems;
    private BigDecimal total;
    private int customer;
    private long time;

    public Sale() {
        saleItems = new ArrayList<>();
        customer = -1;
        total = new BigDecimal("0.00");
    }

    public Sale(Customer c) {
        saleItems = new ArrayList<>();
        this.customer = c.getId();
        total = new BigDecimal("0.00");
    }

    public Sale(int code, BigDecimal total, int customer, long time, List<SaleItem> saleItems) {
        this(code, total, customer, time);
        this.saleItems = saleItems;
    }

    public Sale(int code, BigDecimal total, int customer, long time) {
        this.code = code;
        this.total = total;
        this.customer = customer;
        this.time = time;
    }

    /**
     * This method adds products to the sale. First it will check if the product
     * has already been added. If it has been added then it will check if it is
     * open priced, if it is then it will check if the price is the same, if so
     * then the quantity is increased, if not then it continues checking the
     * products. If it is not open price but exists it will increase the
     * quantity. If it does not exist then it adds a new item.
     *
     * @param p
     * @param quantity
     * @return
     */
    public SaleItem addItem(Product p, int quantity) {
        //First check if the item has already been added
        for (SaleItem item : saleItems) {
            if (item.getProduct().getProductCode() == p.getProductCode()) {
                if (p.isOpen()) {
                    if (p.getPrice().equals(item.getProduct().getPrice())) {
                        BigDecimal inc = item.increaseQuantity(quantity);
                        this.total = total.add(inc);
                        return new SaleItem(this, p, quantity); //The product is open price and the same price so increase the quantity and exit
                    } else {
                        continue; //The product is open but a different price so check the next item
                    }
                }
                //Product is not open price and does already exist
                BigDecimal inc = item.increaseQuantity(quantity);
                this.total = total.add(inc);
                return new SaleItem(this, p, quantity);
            }
        }
        //If the item is not already in the sale
        SaleItem item = new SaleItem(this, p, quantity);

        this.total = total.add(item.getPrice());
        saleItems.add(item);
        return item;
    }

    public void complete() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setProducts(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public int getLineCount() {
        return saleItems.size();
    }

    public int getTotalItemCount() {
        int count = 0;
        for (SaleItem item : saleItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    /**
     * Method to void an item from the sale. It will first check though the list
     * looking for the item. If the quantity of the item in the last is greater
     * than the quantity being voided, then the quantity of the item in the list
     * is simply reduced. If the quantities are the same then the item is
     * removed from the list.
     *
     * @param si
     */
    public void voidItem(SaleItem si) {
        for (SaleItem item : saleItems) {
            if (item.getProduct().getProductCode() == si.getProduct().getProductCode()) {
                if (item.getQuantity() > si.getQuantity()) { //If the quantities are different then reduce the quantity.
                    item.decreaseQuantity(si.getQuantity());
                    updateTotal();
                    return;
                } else if (item.getQuantity() == si.getQuantity()) { //If the quantities are the same then remove the item.
                    saleItems.remove(si);
                    updateTotal();
                    return;
                }
            }
        }
    }

    private void updateTotal() {
        total = new BigDecimal("0");
        for (SaleItem item : saleItems) {
            total = total.add(item.getPrice());
        }
    }

    public String getSQLInsertStatement() {
        return this.total
                + "," + this.customer
                + ",'" + new Time(this.time).toString() + "'";
    }

    @Override
    public String toString() {
        return this.code
                + "\n" + this.saleItems.size()
                + "\n" + this.total.toString();
    }
}
