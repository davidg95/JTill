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
public class Product implements Serializable, Cloneable, Item, JTillObject {

    private int productCode;
    private int order_code;
    private String name;
    private String shortName;

    private String barcode;

    private Category category;
    private Department department;
    private Tax tax;

    private boolean open;
    private double scale;
    private String scaleName;
    private BigDecimal price;
    private BigDecimal costPrice;
    private boolean priceIncVat;
    private int packSize;
    private int stock;
    private int minStockLevel;
    private int maxStockLevel;
    private String comments;

    /**
     * Constructor which takes in only a name and comments. This is used for
     * open products.
     *
     * @param name the name of the product.
     * @param shortName the shortened name of the product.
     * @param barcode the barcode.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to.
     * @param department the department the product belongs to.
     * @param comments the comments.
     * @param tax the tax class for this product.
     * @param scale the value of the scale.
     * @param scaleName the name of the scale.
     * @param cost the cost percentage.
     */
    public Product(String name, String shortName, String barcode, int order_code, Category category, Department department, String comments, Tax tax, double scale, String scaleName, BigDecimal cost) {
        this.name = name;
        this.shortName = shortName;
        this.order_code = order_code;
        this.category = category;
        this.department = department;
        this.comments = comments;
        this.tax = tax;
        this.open = true;
        this.barcode = barcode;
        this.scale = scale;
        this.scaleName = scaleName;
        this.packSize = 1;
        this.costPrice = cost;
    }

    /**
     * Constructor which takes in only a name, comments and product code. This
     * is used for open products.
     *
     * @param name the name of the product.
     * @param shortName the shortened name of the product.
     * @param barcode the barcode.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to.
     * @param department the department the product belongs to.
     * @param comments the comments.
     * @param tax the tax class for this product.
     * @param scale the value of the scale.
     * @param scaleName the name of the scale.
     * @param cost the cost percentage.
     * @param productCode the product code.
     */
    public Product(String name, String shortName, String barcode, int order_code, Category category, Department department, String comments, Tax tax, double scale, String scaleName, BigDecimal cost, int productCode) {
        this(name, shortName, barcode, order_code, category, department, comments, tax, scale, scaleName, cost);
        this.productCode = productCode;
    }

    /**
     * Constructor which taken in values for all fields apart from product code.
     *
     * @param name the name for the product.
     * @param shortName the shortened name of the product.
     * @param barcode the barcode.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to.
     * @param department the department the product belongs to.
     * @param price the price for the product.
     * @param tax the tax class for this product.
     * @param stock the initial stock level for the product.
     * @param costPrice the cost price of the product.
     * @param priceIncVat whether the price is inclusive of VAT or not.
     * @param packSize the pack size.
     * @param minStock the minimum stock level.
     * @param comments any comments about the product.
     * @param maxStock the maximum stock level.
     */
    public Product(String name, String shortName, String barcode, int order_code, Category category, Department department, String comments, Tax tax, BigDecimal price, BigDecimal costPrice, boolean priceIncVat, int packSize, int stock, int minStock, int maxStock) {
        this.name = name;
        this.shortName = shortName;
        this.order_code = order_code;
        this.category = category;
        this.department = department;
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
    }

    /**
     * Constructor which taken in values for all fields as parameters.
     *
     * @param name the name for the product.
     * @param shortName the shortened name of the product.
     * @param barcode the barcode.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to.
     * @param department the department the product belongs to.
     * @param price the price for the product.
     * @param tax the tax class for this product.
     * @param stock the initial stock level for the product.
     * @param costPrice the cost price of the product.
     * @param priceIncVat whether the price is inclusive of VAT or not.
     * @param packSize the pack size.
     * @param minStock the minimum stock level.
     * @param comments any comments about the product.
     * @param maxStock the maximum stock level.
     * @param productCode the product code.
     */
    public Product(String name, String shortName, String barcode, int order_code, Category category, Department department, String comments, Tax tax, BigDecimal price, BigDecimal costPrice, boolean priceIncVat, int packSize, int stock, int minStock, int maxStock, int productCode) {
        this(name, shortName, barcode, order_code, category, department, comments, tax, price, costPrice, priceIncVat, packSize, stock, minStock, maxStock);
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
    public void purchase() throws OutOfStockException {
        if (this.stock > 0) {
            stock--;
        } else {
            throw new OutOfStockException(this.productCode + "");
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

    @Override
    public int getId() {
        return productCode;
    }

    @Override
    public void setId(int productCode) {
        this.productCode = productCode;
    }

    public String getLongName() {
        return name;
    }

    public void setLongName(String name) {
        this.name = name;
    }

    public int getOrder_code() {
        return order_code;
    }

    public void setOrder_code(int order_code) {
        this.order_code = order_code;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
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

    @Override
    public String getName() {
        return shortName;
    }

    @Override
    public void setName(String shortName) {
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

    public BigDecimal getCostPercentage() {
        return costPrice;
    }

    public void setCostPercentage(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    /**
     * Gets the cost price for an open product.
     *
     * @return the cost price as a BigDecimal.
     */
    public BigDecimal getOpenCost() {
        return this.price.divide(new BigDecimal(100), 2, 6).multiply(this.getCostPercentage());
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

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
     * @return selling price as a BigDecimal.
     */
    public BigDecimal getSellingPrice() {
        if (priceIncVat || open) {
            return this.getPrice();
        }
        BigDecimal vat = this.getPrice().multiply(new BigDecimal(this.tax.getValue()).divide(new BigDecimal(100)));
        return this.getPrice().add(vat);
    }

    /**
     * Calculates the vat for one unit of this product,
     *
     * @return the VAT as a BigDecimal.
     */
    public BigDecimal calculateVAT() {
        if (priceIncVat || open) {
            return this.getPrice().divide(new BigDecimal(100), 2, 6).multiply(new BigDecimal(tax.getValue()));
        }
        return getSellingPrice().subtract(getPrice());
    }

    public String getSQLInsertString() {
        return this.order_code
                + ",'" + this.name
                + "'," + this.open
                + "," + this.price
                + "," + this.stock
                + ",'" + this.comments
                + "','" + this.shortName
                + "'," + this.category.getId()
                + "," + this.department.getId()
                + "," + this.tax.getId()
                + "," + this.costPrice
                + "," + this.packSize
                + "," + this.minStockLevel
                + "," + this.maxStockLevel
                + ",'" + this.barcode
                + "'," + this.scale
                + ",'" + this.scaleName
                + "'," + this.priceIncVat;
    }

    public String getSQlUpdateString() {
        return "UPDATE PRODUCTS"
                + " SET PRODUCTS.ORDER_CODE=" + this.getOrder_code()
                + ", PRODUCTS.NAME='" + this.getLongName()
                + "', PRODUCTS.OPEN_PRICE=" + this.isOpen()
                + ", PRODUCTS.PRICE=" + this.getPrice()
                + ", PRODUCTS.STOCK=" + this.getStock()
                + ", PRODUCTS.COMMENTS='" + this.getComments()
                + "', PRODUCTS.SHORT_NAME='" + this.getName()
                + "', PRODUCTS.CATEGORY_ID=" + this.getCategory().getId()
                + ", PRODUCTS.DEPARTMENT_ID=" + this.getDepartment().getId()
                + ", PRODUCTS.TAX_ID=" + this.getTax().getId()
                + ", PRODUCTS.COST_PRICE=" + this.getCostPrice()
                + ", PRODUCTS.PACK_SIZE=" + this.getPackSize()
                + ", PRODUCTS.MIN_PRODUCT_LEVEL=" + this.getMinStockLevel()
                + ", PRODUCTS.MAX_PRODUCT_LEVEL=" + this.getMaxStockLevel()
                + ", PRODUCTS.SCALE=" + this.getScale()
                + ",PRODUCTS.SCALE_NAME='" + this.getScaleName()
                + "', PRODUCTS.INCVAT=" + this.priceIncVat
                + " WHERE PRODUCTS.ID=" + this.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Product) {
            return this.productCode == ((Product) o).getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.productCode;
        return hash;
    }

    @Override
    public Item clone() {
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
