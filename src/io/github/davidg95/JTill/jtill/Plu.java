/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

/**
 *
 * @author David
 */
public class Plu implements Serializable, Cloneable, JTillObject {

    private int id;
    private String code;
    private int product;
    private Product p;

    public Plu(int id, String code, int product) {
        this(code, product);
        this.id = id;
    }

    public Plu(String code, int product) {
        this.code = code;
        this.product = product;
    }

    @Override
    public String getName() {
        return this.code;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getProductID() {
        return product;
    }

    public void setProductID(int product) {
        this.product = product;
    }

    public Product getProduct() {
        return p;
    }

    public void setProduct(Product p) {
        this.p = p;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
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
        final Plu other = (Plu) obj;
        return this.id == other.id;
    }

    @Override
    public Plu clone() {
        try {
            final Plu result = (Plu) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return this.code;
    }

}
