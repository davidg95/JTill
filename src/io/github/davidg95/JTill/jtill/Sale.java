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
    private List<Integer> products;
    private BigDecimal total;
    private int customer;
    private long time;

    public Sale() {
        products = new ArrayList<>();
        customer = -1;
        total = new BigDecimal("0.00");
    }

    public Sale(Customer c) {
        products = new ArrayList<>();
        this.customer = c.getId();
        total = new BigDecimal("0.00");
    }

    public Sale(int code, BigDecimal total, int customer, long time) {
        this.code = code;
        this.total = total;
        this.customer = customer;
        this.time = time;
    }

    public int addItem(Product p, int quantity) {
        int count = 0;
        for (int i = 0; i < quantity; i++) {
            products.add(p.getProductCode());
            total = total.add(p.getPrice());
            count = products.stream().filter((pr) -> (pr == p.getProductCode())).map((_item) -> 1).reduce(count, Integer::sum); //Increaces the count of that product in the sale
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
