/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Class of type product which implements Serializable. This class models a
 * product and will be used in the till system.
 *
 * @author 1301480
 */
public class Product implements Serializable, Cloneable {

    private String barcode;
    private String orderCode;
    private String longName;
    private String shortName;

    private Category category;
    private Tax tax;

    private List<Condiment> condiments;
    private int maxCon;
    private int minCon;
    private List<Condiment> saleCondiments = new LinkedList<>();

    private boolean open;
    private double scale;
    private String scaleName;
    private BigDecimal priceLimit;
    private double costPercentage;

    private BigDecimal price;
    private BigDecimal costPrice;
    private int packSize;
    private boolean priceIncVat;

    private int stock;
    private int minStockLevel;
    private int maxStockLevel;
    private boolean trackStock;

    private String comments;
    private String ingredients;

    /**
     * Constructor which takes in only a name and comments. This is used for
     * open products.
     *
     * @param name the name of the product.
     * @param shortName the shortened name of the product.
     * @param barcode the barcode.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to.
     * @param comments the comments.
     * @param tax the tax class for this product.
     * @param scale the value of the scale.
     * @param scaleName the name of the scale.
     * @param costPercentage the cost percentage.
     * @param price the price limit.
     * @param ingredients the products ingredients.
     */
    public Product(String name, String shortName, String barcode, String order_code, Category category, String comments, Tax tax, double scale, String scaleName, double costPercentage, BigDecimal price, String ingredients) {
        this.longName = name;
        this.shortName = shortName;
        this.orderCode = order_code;
        this.category = category;
        this.comments = comments;
        this.tax = tax;
        this.open = true;
        this.trackStock = false;
        this.barcode = barcode;
        this.scale = scale;
        this.scaleName = scaleName;
        this.packSize = 1;
        this.costPercentage = costPercentage;
        this.priceLimit = price;
        this.ingredients = ingredients;
    }

    /**
     * Constructor which taken in values for all fields apart from product code.
     *
     * @param name the name for the product.
     * @param shortName the shortened name of the product.
     * @param barcode the barcode.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to
     * @param price the price for the product.
     * @param tax the tax class for this product.
     * @param stock the initial stock level for the product.
     * @param costPrice the cost price of the product.
     * @param priceIncVat whether the price is inclusive of VAT or not.
     * @param packSize the pack size.
     * @param minStock the minimum stock level.
     * @param comments any comments about the product.
     * @param maxStock the maximum stock level.
     * @param maxCon the maximum condiments.
     * @param minCon the minimum condiments.
     * @param trackStock if the stock should be tracked or not
     * @param ingredients the products ingredients.
     */
    public Product(String name, String shortName, String barcode, String order_code, Category category, String comments, Tax tax, BigDecimal price, BigDecimal costPrice, boolean priceIncVat, int packSize, int stock, int minStock, int maxStock, int maxCon, int minCon, boolean trackStock, String ingredients) {
        this.longName = name;
        this.shortName = shortName;
        this.orderCode = order_code;
        this.category = category;
        this.comments = comments;
        this.tax = tax;
        this.open = false;
        this.scale = 1;
        this.scaleName = "";
        this.barcode = barcode;
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
        this.minStockLevel = minStock;
        this.maxStockLevel = maxStock;
        this.packSize = packSize;
        this.priceIncVat = priceIncVat;
        this.maxCon = maxCon;
        this.minCon = minCon;
        this.trackStock = trackStock;
        this.ingredients = ingredients;
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
    public void purchase() throws OutOfStockException {
        if (this.stock > 0) {
            stock--;
        } else {
            throw new OutOfStockException(this.barcode);
        }
    }

    /**
     * Calculates the price from the scale.
     *
     * @param value the value.
     * @return the price.
     */
    public double priceFromScale(double value) {
        return this.scale * value;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String name) {
        this.longName = name;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public double getCostPercentage() {
        return costPercentage;
    }

    public void setCostPercentage(double costPercentage) {
        this.costPercentage = costPercentage;
    }

    /**
     * Gets the cost price for an open product.
     *
     * @return the cost price as a BigDecimal.
     */
    public BigDecimal getOpenCost() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        return this.price.divide(new BigDecimal(100), 2, 6).multiply(new BigDecimal(this.getCostPercentage()));
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        this.trackStock = false;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public int getPackSize() {
        return packSize;
    }

    public void setPackSize(int packSize) {
        this.packSize = packSize;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public String getScaleName() {
        return scaleName;
    }

    public void setScaleName(String scaleName) {
        this.scaleName = scaleName;
    }

    public BigDecimal getIndividualCost() {
        if (!open) {
            return this.costPrice.divide(new BigDecimal(this.packSize), 2, BigDecimal.ROUND_HALF_EVEN);
        } else {
            return this.getOpenCost();
        }
    }

    public boolean isPriceIncVat() {
        return priceIncVat;
    }

    public void setPriceIncVat(boolean priceIncVat) {
        this.priceIncVat = priceIncVat;
    }

    /**
     * The selling price with VAT added on.
     *
     * @param price the price the item sells at.
     * @return selling price as a BigDecimal.
     */
    public BigDecimal getSellingPrice(BigDecimal price) {
        if (priceIncVat || open) {
            return price;
        }
        BigDecimal vat = price.multiply(new BigDecimal(this.tax.getValue()).divide(new BigDecimal(100)));
        return price.add(vat);
    }

    /**
     * Calculates the vat for one unit of this product,
     *
     * @param price the price the item sells at.
     * @return the VAT as a BigDecimal.
     */
    public BigDecimal calculateVAT(BigDecimal price) {
        if (priceIncVat || open) {
            return price.divide(new BigDecimal(100), 2, 6).multiply(new BigDecimal(tax.getValue()));
        }
        return getSellingPrice(price).subtract(price);
    }

    public List<Condiment> getCondiments() {
        return condiments;
    }

    public void setCondiments(List<Condiment> condiments) {
        this.condiments = condiments;
    }

    public int getMaxCon() {
        return maxCon;
    }

    public void setMaxCon(int maxCon) {
        this.maxCon = maxCon;
    }

    public int getMinCon() {
        return minCon;
    }

    public void setMinCon(int minCon) {
        this.minCon = minCon;
    }

    public void setSaleCondiments(List<Condiment> c) {
        saleCondiments = c;
    }

    public List<Condiment> getSaleCondiments() {
        return saleCondiments;
    }

    public BigDecimal getPriceLimit() {
        return priceLimit;
    }

    public void setPriceLimit(BigDecimal priceLimit) {
        this.priceLimit = priceLimit;
    }

    public boolean isTrackStock() {
        if (open) {
            return false;
        }
        return trackStock;
    }

    public void setTrackStock(boolean trackStock) {
        if (open) {
            this.trackStock = false;
        } else {
            this.trackStock = trackStock;
        }
    }

    public Department getDepartment() {
        return this.category.getDepartment();
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSQLInsertString() {
        return "'" + this.orderCode
                + "','" + this.longName
                + "'," + this.open
                + "," + this.price
                + "," + this.stock
                + ",'" + this.comments
                + "','" + this.shortName
                + "'," + this.category.getId()
                + "," + this.tax.getId()
                + "," + this.costPrice
                + "," + this.packSize
                + "," + this.minStockLevel
                + "," + this.maxStockLevel
                + ",'" + this.barcode
                + "'," + this.scale
                + ",'" + this.scaleName
                + "'," + this.priceIncVat
                + "," + this.priceLimit
                + "," + this.trackStock
                + "," + this.costPercentage
                + ",'" + this.ingredients + "'";
    }

    public String getSQlUpdateString() {
        return "UPDATE PRODUCTS"
                + " SET pORDER_CODE='" + this.getOrderCode()
                + "', pNAME='" + this.getLongName()
                + "', OPEN_PRICE=" + this.isOpen()
                + ", pPRICE=" + this.getPrice()
                + ", pSTOCK=" + this.getStock()
                + ", pCOMMENTS='" + this.getComments()
                + "', pSHORT_NAME='" + this.getShortName()
                + "', pcategory=" + this.getCategory().getId()
                + ", ptax=" + this.getTax().getId()
                + ", pCOST_PRICE=" + this.getCostPrice()
                + ", pPACK_SIZE=" + this.getPackSize()
                + ", pmin_level=" + this.getMinStockLevel()
                + ", pmax_level=" + this.getMaxStockLevel()
                + ", pSCALE=" + this.getScale()
                + ", pSCALE_NAME='" + this.getScaleName()
                + "', pINCVAT=" + this.isPriceIncVat()
                + ", pMAXCON=" + this.getMaxCon()
                + ", pMINCON=" + this.getMinCon()
                + ", pLIMIT=" + this.getPriceLimit()
                + ", pTRACK_STOCK=" + this.isTrackStock()
                + ", pingredients='" + this.getIngredients()
                + "', pcost_percentage=" + this.getCostPercentage()
                + " WHERE BARCODE='" + this.getBarcode() + "'";
    }

    /**
     * Saves the product to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.get().updateProduct(this);
        } catch (ProductNotFoundException ex) {
            DataConnect.get().addProduct(this);
        }
    }

    /**
     * Get a list of all the products.
     *
     * @return a List of all the products.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public static List<Product> getAll() throws IOException, SQLException {
        return DataConnect.get().getAllProducts();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Product) {
            return (this.barcode == null ? ((Product) o).getBarcode() == null : this.barcode.equals(((Product) o).getBarcode()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.barcode);
        return hash;
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
        return this.shortName;
    }
}
