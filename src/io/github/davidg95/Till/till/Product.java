/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import java.io.Serializable;

/**
 * Class of type product which implements Serializable. This class models a
 * product and will be used in the till system.
 *
 * @author 1301480
 */
public class Product implements Serializable {

    private String productCode;
    private String barcode;
    private String name;
    private double price;
    private int stock;
    private String comments;

    /**
     * Blank constructor.
     */
    public Product() {

    }

    /**
     * Constructor which takes in only a name and comments. This can be used for open
     * products.
     *
     * @param name the name of the product.
     * @param comments the comments.
     */
    public Product(String name, String comments) {
        this.name = name;
        this.comments = comments;
    }
    
    /**
     * Constructor which takes in only a name, comments and product code. This can be used for open
     * products.
     *
     * @param name the name of the product.
     * @param comments the comments.
     * @param productCode the product code.
     */
    public Product(String name, String comments, String productCode) {
        this(name, comments);
        this.productCode = productCode;
    }

    /**
     * Constructor which taken in values for all fields apart from product code.
     *
     * @param name the name for the product.
     * @param price the price for the product.
     * @param stock the initial stock level for the product.
     * @param barcode the barcode of the product.
     * @param comments any comments about the product.
     */
    public Product(String name, String comments, double price, int stock, String barcode) {
        this(name, comments);
        this.price = price;
        this.stock = stock;
        this.barcode = barcode;
    }

    /**
     * Constructor which taken in values for all fields as parameters.
     *
     * @param name the name for the product.
     * @param price the price for the product.
     * @param stock the initial stock level for the product.
     * @param barcode the barcode of the product.
     * @param comments any comments about the product.
     * @param productCode the product code.
     */
    public Product(String name, String comments, double price, int stock, String barcode, String productCode) {
        this(name, comments, price, stock, barcode);
        this.productCode = productCode;
    }

    /**
     * Method to increase the stock level of the product
     *
     * @param stock the stock to add.
     */
    public void addStock(int stock) {
        this.stock += stock;
    }

    /**
     * Method to decrease the stock level of the product
     *
     * @param stock the stock to remove.
     */
    public void removeStock(int stock) {
        this.stock -= stock;
    }

    /**
     * Method to purchase the product and reduce its stock level by 1. this
     * method throws an OutOfStockExceptin if the stock level is 0.
     *
     * @throws OutOfStockException if the stock level is 0.
     */
    public void purchace() throws OutOfStockException {
        if (this.stock > 0) {
            stock--;
        } else {
            throw new OutOfStockException(this.productCode);
        }
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean equals(Product p) {
        return this.productCode.equals(p.productCode);
    }

    @Override
    public String toString() {
        return "Code: " + this.productCode + "\nName: " + this.name + "\nPrice: £" + this.price + "\nStock: " + this.stock;
    }
}
