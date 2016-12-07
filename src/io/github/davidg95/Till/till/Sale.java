/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class Sale implements Serializable {

    private String code;
    private List<Integer> products;
    private double total;
    private int customer;

    public Sale() {
        products = new ArrayList<>();
    }

    public Sale(Customer c) {
        products = new ArrayList<>();
        this.customer = c.getId();
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

}
