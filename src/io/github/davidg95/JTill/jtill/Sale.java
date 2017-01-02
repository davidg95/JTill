/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class Sale implements Serializable {

    private int code;
    private List<Integer> products;
    private double total;
    private int customer;
    private long time;

    public Sale() {
        products = new ArrayList<>();
        customer = -1;
    }

    public Sale(Customer c) {
        products = new ArrayList<>();
        this.customer = c.getId();
    }

    public Sale(int code, double total, int customer, long time) {
        this.code = code;
        this.total = total;
        this.customer = customer;
        this.time = time;
    }

    public int addItem(Product p) {
        products.add(p.getProductCode());
        total += p.getPrice();
        int count = 0;
        for (int pr : products) {
            if (pr == p.getProductCode()) {
                count++;
            }
        }
        return count;
    }

    public void complete() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Integer> getProducts() {
        return products;
    }

    public void setProducts(List<Integer> products) {
        this.products = products;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public int getItemCount() {
        return products.size();
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public String getSQLInsertStatement() {
        return this.total
                + "," + this.customer
                + ",'" + new Time(this.time).toString() + "'";
    }
}
