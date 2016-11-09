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
public class Sale implements Serializable{
    private String code;
    private List<Product> products;
    private double total;
    private Customer customer;
    
    public Sale(){
        products = new ArrayList<>();
    }
    
    public Sale(Customer c){
        products = new ArrayList<>();
        this.customer = c;
    }
    
    public int addItem(Product p){
        products.add(p);
        int count = 0;
        for(Product pr: products){
            if(pr.equals(p)){
                count++;
            }
        }
        return count;
    }
    
    public void complete(){
        for(Product p: products){
            total += p.getPrice();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
}
