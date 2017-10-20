/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author David
 */
public class Condiment implements Serializable {

    private int id;
    private String name;
    private int product;
    private BigDecimal value;
    private int stock;

    public Condiment(int id, String name, int product, BigDecimal value, int stock) {
        this.id = id;
        this.name = name;
        this.product = product;
        this.value = value;
        this.stock = stock;
    }

    public Condiment(String name, int product, BigDecimal value) {
        this.name = name;
        this.product = product;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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
        final Condiment other = (Condiment) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return "Condiment{" + "name=" + name + '}';
    }
}
