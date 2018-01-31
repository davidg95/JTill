/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 *
 * @author David
 */
public class Condiment implements Serializable {

    private int id;
    private int product;
    private Product product_con;

    public Condiment(int id, int product, Product product_con) {
        this.id = id;
        this.product = product;
        this.product_con = product_con;
    }

    public Condiment(int product, Product product_con) {
        this.product_con = product_con;
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public Product getProduct_con() {
        return product_con;
    }

    public void setProduct_con(Product product_con) {
        this.product_con = product_con;
    }

    public void save() throws IOException, SQLException {
        try {
            DataConnect.get().updateCondiment(this);
        } catch (JTillException ex) {
            DataConnect.get().addCondiment(this);
        }
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
        return "£" + new DecimalFormat("0.00").format(product_con.getSellingPrice()) + "\t\t" + product_con.getLongName();
    }
}
