/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import java.awt.Color;
import java.io.Serializable;

/**
 * Class of type product which implements Serializable. This class models a
 * product and will be used in the till system.
 *
 * @author 1301480
 */
public class Product implements Serializable {

    private int productCode;
    private String barcode;
    private String name;
    private String shortName;
    private int categoryID;
    private int taxID;
    private double price;
    private double costPrice;
    private int stock;
    private int minStockLevel;
    private int maxStockLevel;
    private int discountID;
    private String comments;
    private boolean button;
    private int color;

    /**
     * Blank constructor.
     */
    public Product() {

    }

    /**
     * Constructor which takes in only a name and comments. This can be used for
     * open products.
     *
     * @param name the name of the product.
     * @param shortName the shortened name of the product.
     * @param categoryID the category this will belong to.
     * @param comments the comments.
     * @param taxID the tax class for this product.
     * @param discountID the discount ID for the product.
     * @param button whether the products will have a button or not.
     * @param color the color of the button.
     */
    public Product(String name, String shortName, int categoryID, String comments, int taxID, int discountID, boolean button, int color) {
        this.name = name;
        this.shortName = shortName;
        this.categoryID = categoryID;
        this.comments = comments;
        this.taxID = taxID;
        this.discountID = discountID;
        this.button = button;
        this.color = color;
    }

    /**
     * Constructor which takes in only a name, comments and product code. This
     * can be used for open products.
     *
     * @param name the name of the product.
     * @param shortName the shortened name of the product.
     * @param categoryID the category this will belong to.
     * @param comments the comments.
     * @param taxID the tax class for this product.
     * @param discountID the discount ID for this product.
     * @param button whether the products will have a button or not.
     * @param color the color of the button.
     * @param productCode the product code.
     */
    public Product(String name, String shortName, int categoryID, String comments, int taxID, int discountID, boolean button, int color, int productCode) {
        this(name, shortName, categoryID, comments, taxID, discountID, button, color);
        this.productCode = productCode;
    }

    /**
     * Constructor which taken in values for all fields apart from product code.
     *
     * @param name the name for the product.
     * @param shortName the shortened name of the product.
     * @param categoryID the category this will belong to.
     * @param price the price for the product.
     * @param taxID the tax class for this product.
     * @param discountID the discount ID for this product.
     * @param button whether the products will have a button or not.
     * @param color the color of the button.
     * @param stock the initial stock level for the product.
     * @param costPrice the cost price of the product.
     * @param barcode the barcode of the product.
     * @param minStock the minimum stock level.
     * @param comments any comments about the product.
     * @param maxStock the maximum stock level.
     */
    public Product(String name, String shortName, int categoryID, String comments, int taxID, int discountID, boolean button, int color, double price, double costPrice, int stock, int minStock, int maxStock, String barcode) {
        this(name, shortName, categoryID, comments, taxID, discountID, button, color);
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
        this.minStockLevel = minStock;
        this.maxStockLevel = maxStock;
        this.barcode = barcode;
    }

    /**
     * Constructor which taken in values for all fields as parameters.
     *
     * @param name the name for the product.
     * @param shortName the shortened name of the product.
     * @param categoryID the category this will belong to.
     * @param price the price for the product.
     * @param taxID the tax class for this product.
     * @param discountID the discount ID for the product.
     * @param button whether the products will have a button or not.
     * @param color the color of the button.
     * @param stock the initial stock level for the product.
     * @param costPrice the cost price of the product.
     * @param barcode the barcode of the product.
     * @param minStock the minimum stock level.
     * @param comments any comments about the product.
     * @param maxStock the maximum stock level.
     * @param productCode the product code.
     */
    public Product(String name, String shortName, int categoryID, String comments, int taxID, int discountID, boolean button, int color, double price, double costPrice, int stock, int minStock, int maxStock, String barcode, int productCode) {
        this(name, shortName, categoryID, comments, taxID, discountID, button, color, price, costPrice, stock, minStock, maxStock, barcode);
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
            throw new OutOfStockException(this.productCode + "");
        }
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getTaxID() {
        return taxID;
    }

    public void setTaxID(int taxID) {
        this.taxID = taxID;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public int getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(int maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public int getDiscountID() {
        return discountID;
    }

    public void setDiscountID(int discountID) {
        this.discountID = discountID;
    }

    public boolean isButton() {
        return button;
    }

    public void setButton(boolean button) {
        this.button = button;
    }

    public int getColorValue() {
        return color;
    }

    public void setColorValue(int color) {
        this.color = color;
    }
    
    public Color getColor(){
        return new Color(color);
    }
    
    public void setColor(Color c){
        this.color = c.getRGB();
    }

    public String getSQLInsertString() {
        return "'" + this.barcode
                + "','" + this.name
                + "'," + this.price
                + "," + this.stock
                + ",'" + this.comments
                + "','" + this.shortName
                + "'," + this.categoryID
                + "," + this.taxID
                + "," + this.costPrice
                + "," + this.minStockLevel
                + "," + this.maxStockLevel
                + ",'" + this.button
                + "'," + this.color
                + "," + this.discountID;
    }

    public String getSQlUpdateString() {
        return "UPDATE PRODUCTS"
                + " SET PRODUCTS.BARCODE='" + this.getBarcode()
                + "', PRODUCTS.NAME='" + this.getName()
                + "', PRODUCTS.PRICE=" + this.getPrice()
                + ", PRODUCTS.STOCK=" + this.getStock()
                + ", PRODUCTS.COMMENTS='" + this.getComments()
                + "', PRODUCTS.SHORT_NAME='" + this.getShortName()
                + "', PRODUCTS.CATEGORY_ID=" + this.getCategoryID()
                + ", PRODUCTS.TAX_ID=" + this.getTaxID()
                + ", PRODUCTS.COST_PRICE=" + this.getCostPrice()
                + ", PRODUCTS.MIN_PRODUCT_LEVEL=" + this.getMinStockLevel()
                + ", PRODUCTS.MAX_PRODUCT_LEVEL=" + this.getMaxStockLevel()
                + ", PRODUCTS.BUTTON=" + this.isButton()
                + ", PRODUCTS.COLOR=" + this.getColorValue()
                + ", PRODUCTS.DISCOUNT_ID=" + this.getDiscountID()
                + " WHERE PRODUCTS.ID=" + this.getProductCode();
    }

    public boolean equals(Product p) {
        return this.productCode == p.productCode;
    }

    @Override
    public String toString() {
        return "Code: " + this.productCode + "\nName: " + this.name + "\nPrice: £" + this.price + "\nStock: " + this.stock;
    }
}
