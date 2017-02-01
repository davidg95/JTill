/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Class of type product which implements Serializable. This class models a
 * product and will be used in the till system.
 *
 * @author 1301480
 */
public class Product implements Serializable, Cloneable {

    private int productCode;
    private String barcode;
    private String name;
    private String shortName;
    private int categoryID;
    private int taxID;
    private boolean open;
    private BigDecimal price;
    private BigDecimal costPrice;
    private int stock;
    private int minStockLevel;
    private int maxStockLevel;
    private int discountID;
    private String comments;

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
     * @param open if the price is open.
     */
    public Product(String name, String shortName, int categoryID, String comments, int taxID, int discountID, boolean open) {
        this.name = name;
        this.shortName = shortName;
        this.categoryID = categoryID;
        this.comments = comments;
        this.taxID = taxID;
        this.discountID = discountID;
        this.open = open;
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
     * @param open if the price is open.
     * @param productCode the product code.
     */
    public Product(String name, String shortName, int categoryID, String comments, int taxID, int discountID, boolean open, int productCode) {
        this(name, shortName, categoryID, comments, taxID, discountID, open);
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
     * @param open if the price is open.
     * @param stock the initial stock level for the product.
     * @param costPrice the cost price of the product.
     * @param barcode the barcode of the product.
     * @param minStock the minimum stock level.
     * @param comments any comments about the product.
     * @param maxStock the maximum stock level.
     */
    public Product(String name, String shortName, int categoryID, String comments, int taxID, int discountID, boolean open, BigDecimal price, BigDecimal costPrice, int stock, int minStock, int maxStock, String barcode) {
        this(name, shortName, categoryID, comments, taxID, discountID, open);
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
     * @param open if the price is open.
     * @param stock the initial stock level for the product.
     * @param costPrice the cost price of the product.
     * @param barcode the barcode of the product.
     * @param minStock the minimum stock level.
     * @param comments any comments about the product.
     * @param maxStock the maximum stock level.
     * @param productCode the product code.
     */
    public Product(String name, String shortName, int categoryID, String comments, int taxID, int discountID, boolean open, BigDecimal price, BigDecimal costPrice, int stock, int minStock, int maxStock, String barcode, int productCode) {
        this(name, shortName, categoryID, comments, taxID, discountID, open, price, costPrice, stock, minStock, maxStock, barcode);
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getSQLInsertString() {
        return "'" + this.barcode
                + "','" + this.name
                + "'," + this.open
                + "," + this.price
                + "," + this.stock
                + ",'" + this.comments
                + "','" + this.shortName
                + "'," + this.categoryID
                + "," + this.taxID
                + "," + this.costPrice
                + "," + this.minStockLevel
                + "," + this.maxStockLevel
                + "," + this.discountID;
    }

    public String getSQlUpdateString() {
        return "UPDATE PRODUCTS"
                + " SET PRODUCTS.BARCODE='" + this.getBarcode()
                + "', PRODUCTS.NAME='" + this.getName()
                + "', PRODUCTS.OPEN_PRICE=" + this.isOpen()
                + ", PRODUCTS.PRICE=" + this.getPrice()
                + ", PRODUCTS.STOCK=" + this.getStock()
                + ", PRODUCTS.COMMENTS='" + this.getComments()
                + "', PRODUCTS.SHORT_NAME='" + this.getShortName()
                + "', PRODUCTS.CATEGORY_ID=" + this.getCategoryID()
                + ", PRODUCTS.TAX_ID=" + this.getTaxID()
                + ", PRODUCTS.COST_PRICE=" + this.getCostPrice()
                + ", PRODUCTS.MIN_PRODUCT_LEVEL=" + this.getMinStockLevel()
                + ", PRODUCTS.MAX_PRODUCT_LEVEL=" + this.getMaxStockLevel()
                + ", PRODUCTS.DISCOUNT_ID=" + this.getDiscountID()
                + " WHERE PRODUCTS.ID=" + this.getProductCode();
    }

    public boolean equals(Product p) {
        return this.productCode == p.productCode;
    }

    @Override
    public Product clone() {
        try {
            final Product result = (Product) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Code: " + this.productCode + "\nName: " + this.name + "\nPrice: £" + this.price + "\nStock: " + this.stock;
    }
}
