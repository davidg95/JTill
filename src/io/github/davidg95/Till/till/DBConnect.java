/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import io.github.davidg95.Till.till.Staff.Position;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.derby.jdbc.EmbeddedDriver;

/**
 * Database connection class which handles communication with the database.
 *
 * @author David
 */
public class DBConnect {

    private Connection con;
    private Driver embedded;

    private String address;
    private String username;
    private String password;

    private boolean connected;

    private ResultSet productSet;
    private ResultSet customerSet;
    private ResultSet staffSet;
    private ResultSet discountSet;
    private ResultSet configSet;
    private ResultSet taxSet;
    private ResultSet categorySet;

    private final String all_products = "SELECT * FROM PRODUCTS";
    private final String all_customers = "SELECT * FROM CUSTOMERS";
    private final String all_staff = "SELECT * FROM STAFF";
    private final String all_discounts = "SELECT * FROM DISCOUNTS";
    private final String all_configs = "SELECT * FROM CONFIGS";
    private final String all_tax = "SELECT * FROM TAX";
    private final String all_categorys = "SELECT * FROM CATEGORYS";

    private Statement products_stmt;
    private Statement customers_stmt;
    private Statement staff_stmt;
    private Statement discounts_stmt;
    private Statement configs_stmt;
    private Statement tax_stmt;
    private Statement category_stmt;

    private Statement create_tables_stmt;

    public DBConnect() {

    }

    /**
     * Method to make a new connection with the database.
     *
     * @param database_address the url of the database.
     * @param username username to log on to the database.
     * @param password password to log on to the database.
     * @throws SQLException if there was a log on error.
     */
    public void connect(String database_address, String username, String password) throws SQLException {
        con = DriverManager.getConnection(database_address, username, password);
        this.address = database_address;
        this.username = username;
        this.password = password;
        connected = true;
    }

    public void create(String username, String password) throws SQLException {
        embedded = new EmbeddedDriver();
        DriverManager.registerDriver(embedded);
        con = DriverManager.getConnection("jdbc:derby:TillEmbedded;create=true", "App", "App");

        this.address = "jdbc:derby:TillEmbedded;create=true";
        this.username = "App";
        this.password = "App";
        connected = true;
        createTables();
    }

    private void createTables() throws SQLException {
        String categorys = "create table APP.CATEGORYS\n"
                + "(\n"
                + "	ID VARCHAR(6) not null primary key,\n"
                + "	NAME VARCHAR(20) not null\n"
                + ")";
        String configs = "create table APP.CONFIGS\n"
                + "(\n"
                + "	NAME VARCHAR(20) not null primary key,\n"
                + "	VALUE VARCHAR(20) not null\n"
                + ")";
        String customers = "create table \"APP\".CUSTOMERS\n"
                + "(\n"
                + "	ID VARCHAR(6) not null primary key,\n"
                + "	NAME VARCHAR(50) not null,\n"
                + "	PHONE VARCHAR(15),\n"
                + "	MOBILE VARCHAR(15),\n"
                + "	EMAIL VARCHAR(50),\n"
                + "	ADDRESS_LINE_1 VARCHAR(50),\n"
                + "	ADDRESS_LINE_2 VARCHAR(50),\n"
                + "	TOWN VARCHAR(50),\n"
                + "	COUNTY VARCHAR(50),\n"
                + "	COUNTRY VARCHAR(50),\n"
                + "	POSTCODE VARCHAR(20),\n"
                + "	NOTES VARCHAR(200),\n"
                + "	DISCOUNT_ID VARCHAR(6) not null,\n"
                + "	LOYALTY_POINTS INTEGER not null\n"
                + ")";
        String discounts = "create table \"APP\".DISCOUNTS\n"
                + "(\n"
                + "	ID VARCHAR(6) not null primary key,\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "	PERCENTAGE DOUBLE not null\n"
                + ")";
        String products = "create table \"APP\".PRODUCTS\n"
                + "(\n"
                + "	ID VARCHAR(6) not null primary key,\n"
                + "	BARCODE VARCHAR(20),\n"
                + "	NAME VARCHAR(50) not null,\n"
                + "	PRICE DOUBLE,\n"
                + "	STOCK INTEGER,\n"
                + "	COMMENTS VARCHAR(200),\n"
                + "	SHORT_NAME VARCHAR(50) not null,\n"
                + "	CATEGORY_ID VARCHAR(6) not null,\n"
                + "	TAX_ID VARCHAR(6) not null,\n"
                + "	COST_PRICE DOUBLE,\n"
                + "	MIN_PRODUCT_LEVEL INTEGER,\n"
                + "	MAX_PRODUCT_LEVEL INTEGER,\n"
                + "	DISCOUNT_ID VARCHAR(6) default '000000' not null\n"
                + ")";
        String staff = "create table \"APP\".STAFF\n"
                + "(\n"
                + "	ID VARCHAR(6) not null primary key,\n"
                + "	NAME VARCHAR(50) not null,\n"
                + "	POSITION VARCHAR(20) not null,\n"
                + "	USERNAME VARCHAR(20) not null,\n"
                + "	PASSWORD VARCHAR(20) not null\n"
                + ")";
        String tax = "create table \"APP\".TAX\n"
                + "(\n"
                + "	ID VARCHAR(6) not null primary key,\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "	VALUE DOUBLE not null\n"
                + ")";
        
        Statement stmt = con.createStatement();
        stmt.execute(categorys);
        stmt.execute(configs);
        stmt.execute(customers);
        stmt.execute(discounts);
        stmt.execute(products);
        stmt.execute(staff);
        stmt.execute(tax);
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Method to initialise the database connection. This method will set up the
     * SQL statements and load the data sets.
     *
     * @throws SQLException if there was an SQL error.
     */
    public void initDatabase() throws SQLException {
        products_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        customers_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        staff_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        discounts_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        configs_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        tax_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        category_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        productSet = products_stmt.executeQuery(all_products);
        customerSet = customers_stmt.executeQuery(all_customers);
        staffSet = staff_stmt.executeQuery(all_staff);
        discountSet = discounts_stmt.executeQuery(all_discounts);
        configSet = configs_stmt.executeQuery(all_configs);
        taxSet = tax_stmt.executeQuery(all_tax);
        categorySet = category_stmt.executeQuery(all_categorys);
    }

    /**
     * Method to close the database connection. This will close the data sets
     * and close the connection.
     */
    public void close() {
        try {
            productSet.close();
            customerSet.close();
            staffSet.close();
            discountSet.close();
            configSet.close();
            taxSet.close();
            con.close();
            connected = false;
        } catch (SQLException ex) {

        }
    }

    /**
     * Method to check if the database is currently connected.
     *
     * @return true if it connected, false otherwise.
     */
    public boolean isConnected() {
        return connected;
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        while (productSet.next()) {
            String code = productSet.getString("ID");
            String barcode = productSet.getString("BARCODE");
            String name = productSet.getString("NAME");
            double price = productSet.getDouble("PRICE");
            int stock = productSet.getInt("STOCK");
            String comments = productSet.getString("COMMENTS");
            String shortName = productSet.getString("SHORT_NAME");
            String categoryID = productSet.getString("CATEGORY_ID");
            String taxID = productSet.getString("TAX_ID");
            double costPrice = productSet.getDouble("COST_PRICE");
            int minStock = productSet.getInt("MIN_PRODUCT_LEVEL");
            int maxStock = productSet.getInt("MAX_PRODUCT_LEVEL");
            String discountID = productSet.getString("DISCOUNT_ID");

            Product p = new Product(name, shortName, categoryID, comments, taxID, discountID, price, costPrice, stock, minStock, maxStock, barcode, code);

            products.add(p);
        }

        return products;
    }
    
    private List<Product> getProductsFromResultSet(ResultSet set) throws SQLException{
        List<Product> products = new ArrayList<>();
        while (productSet.next()) {
            String code = productSet.getString("ID");
            String barcode = productSet.getString("BARCODE");
            String name = productSet.getString("NAME");
            double price = productSet.getDouble("PRICE");
            int stock = productSet.getInt("STOCK");
            String comments = productSet.getString("COMMENTS");
            String shortName = productSet.getString("SHORT_NAME");
            String categoryID = productSet.getString("CATEGORY_ID");
            String taxID = productSet.getString("TAX_ID");
            double costPrice = productSet.getDouble("COST_PRICE");
            int minStock = productSet.getInt("MIN_PRODUCT_LEVEL");
            int maxStock = productSet.getInt("MAX_PRODUCT_LEVEL");
            String discountID = productSet.getString("DISCOUNT_ID");

            Product p = new Product(name, shortName, categoryID, comments, taxID, discountID, price, costPrice, stock, minStock, maxStock, barcode, code);

            products.add(p);
        }

        return products;
    }
    
    public void addProduct(Product p) throws SQLException {
        productSet.moveToInsertRow();

        productSet.updateString("ID", p.getProductCode());
        productSet.updateString("BARCODE", p.getBarcode());
        productSet.updateString("NAME", p.getName());
        productSet.updateDouble("PRICE", p.getPrice());
        productSet.updateInt("STOCK", p.getStock());
        productSet.updateString("COMMENTS", p.getComments());
        productSet.updateString("SHORT_NAME", p.getShortName());
        productSet.updateString("CATEGORY_ID", p.getCategoryID());
        productSet.updateString("TAX_ID", p.getTaxID());
        productSet.updateDouble("COST_PRICE", p.getCostPrice());
        productSet.updateInt("MIN_PRODUCT_LEVEL", p.getMinStockLevel());
        productSet.updateInt("MAX_PRODUCT_LEVEL", p.getMaxStockLevel());
        productSet.insertRow();
    }
    
    public void removeProduct(Product p) throws SQLException{
        String query = "DELETE FROM PRODUCTS WHERE PRODUCTS.ID = " + p.getProductCode();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
    }
    
    public Product getProduct(String code) throws SQLException{
        String query = "SELECT * FROM Products WHERE PRODUCTS.ID = " + code;
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);
        
        List<Product> products = getProductsFromResultSet(res);
        return products.get(0);
    }
    
    public Product getProductByBarcode(String barcode) throws SQLException{
        String query = "SELECT * FROM Products WHERE PRODUCTS.BARCODE = " + barcode;
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);
        
        List<Product> products = getProductsFromResultSet(res);
        return products.get(0);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        while (customerSet.next()) {
            String id = customerSet.getString("ID");
            String name = customerSet.getString("NAME");
            String phone = customerSet.getString("PHONE");
            String mobile = customerSet.getString("MOBILE");
            String email = customerSet.getString("EMAIL");
            String address1 = customerSet.getString("ADDRESS_LINE_1");
            String address2 = customerSet.getString("ADDRESS_LINE_2");
            String town = customerSet.getString("TOWN");
            String county = customerSet.getString("COUNTY");
            String country = customerSet.getString("COUNTRY");
            String postcode = customerSet.getString("POSTCODE");
            String notes = customerSet.getString("NOTES");
            String discount = customerSet.getString("DISCOUNT_ID");
            int loyaltyPoints = customerSet.getInt("LOYALTY_POINTS");

            Customer c = new Customer(name, phone, mobile, email, discount, address1, address2, town, county, country, postcode, notes, loyaltyPoints, id);

            customers.add(c);
        }

        return customers;
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staff = new ArrayList<>();
        while (staffSet.next()) {
            String id = staffSet.getString("ID");
            String name = staffSet.getString("NAME");
            String position = staffSet.getString("POSITION");
            String uname = staffSet.getString("USERNAME");
            String pword = staffSet.getString("PASSWORD");

            Position enumPosition;

            if (position.equals(Position.ASSISSTANT.toString())) {
                enumPosition = Position.ASSISSTANT;
            } else if (position.equals(Position.SUPERVISOR.toString())) {
                enumPosition = Position.SUPERVISOR;
            } else if (position.equals(Position.MANAGER.toString())) {
                enumPosition = Position.MANAGER;
            } else {
                enumPosition = Position.AREA_MANAGER;
            }

            Staff s = new Staff(name, enumPosition, uname, pword, id);

            staff.add(s);
        }

        return staff;
    }

    public List<Discount> getAllDiscounts() throws SQLException {
        List<Discount> discounts = new ArrayList<>();
        while (discountSet.next()) {
            String id = discountSet.getString("ID");
            String name = discountSet.getString("NAME");
            double percentage = discountSet.getDouble("PERCENTAGE");

            Discount d = new Discount(id, name, percentage);

            discounts.add(d);
        }

        return discounts;
    }

    public HashMap<String, String> getAllConfigs() throws SQLException {
        HashMap<String, String> configs = new HashMap<>();

        while (configSet.next()) {
            String name = configSet.getString("NAME");
            String value = configSet.getString("VALUE");
            configs.put(name, value);
        }

        if (configs.isEmpty()) {
            configs.put("products", 0 + "");
            configs.put("customers", 0 + "");
            configs.put("staff", 0 + "");
            configs.put("discounts", 0 + "");
            configs.put("tax", 0 + "");
            configs.put("categorys", 0 + "");
        }

        return configs;
    }

    public List<Tax> getAllTax() throws SQLException {
        List<Tax> tax = new ArrayList<>();

        while (taxSet.next()) {
            String id = taxSet.getString("ID");
            String name = taxSet.getString("NAME");
            double value = taxSet.getDouble("VALUE");
            Tax t = new Tax(id, name, value);

            tax.add(t);
        }

        return tax;
    }

    public List<Category> getAllCategorys() throws SQLException {
        List<Category> categorys = new ArrayList<>();

        while (categorySet.next()) {
            String id = categorySet.getString("ID");
            String name = categorySet.getString("NAME");
            Category c = new Category(id, name);
            categorys.add(c);
        }

        return categorys;
    }

    public void updateWholeProducts(List<Product> products) throws SQLException {
        productSet.beforeFirst();

        while (productSet.next()) {
            productSet.deleteRow();
        }

        for (Product p : products) {
            productSet.moveToInsertRow();
            productSet.updateString("ID", p.getProductCode());
            productSet.updateString("BARCODE", p.getBarcode());
            productSet.updateString("NAME", p.getName());
            productSet.updateDouble("PRICE", p.getPrice());
            productSet.updateInt("STOCK", p.getStock());
            productSet.updateString("COMMENTS", p.getComments());
            productSet.updateString("SHORT_NAME", p.getShortName());
            productSet.updateString("CATEGORY_ID", p.getCategoryID());
            productSet.updateString("TAX_ID", p.getTaxID());
            productSet.updateDouble("COST_PRICE", p.getCostPrice());
            productSet.updateInt("MIN_PRODUCT_LEVEL", p.getMinStockLevel());
            productSet.updateInt("MAX_PRODUCT_LEVEL", p.getMaxStockLevel());
            productSet.insertRow();
        }

        products_stmt.close();
        productSet.close();

        products_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        productSet = products_stmt.executeQuery(all_products);
    }

    public void updateWholeCustomers(List<Customer> customers) throws SQLException {
        customerSet.beforeFirst();

        while (customerSet.next()) {
            customerSet.deleteRow();
        }

        for (Customer c : customers) {
            customerSet.moveToInsertRow();
            customerSet.updateString("ID", c.getId());
            customerSet.updateString("NAME", c.getName());
            customerSet.updateString("PHONE", c.getPhone());
            customerSet.updateString("MOBILE", c.getMobile());
            customerSet.updateString("EMAIL", c.getEmail());
            customerSet.updateString("ADDRESS_LINE_1", c.getAddressLine1());
            customerSet.updateString("ADDRESS_LINE_2", c.getAddressLine2());
            customerSet.updateString("TOWN", c.getTown());
            customerSet.updateString("COUNTY", c.getCounty());
            customerSet.updateString("COUNTRY", c.getCountry());
            customerSet.updateString("POSTCODE", c.getPostcode());
            customerSet.updateString("NOTES", c.getNotes());
            customerSet.updateString("DISCOUNT_ID", c.getDiscountID());
            customerSet.updateInt("LOYALTY_POINTS", c.getLoyaltyPoints());
            customerSet.insertRow();
        }

        customers_stmt.close();
        customerSet.close();

        customers_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        customerSet = customers_stmt.executeQuery(all_customers);
    }

    public void updateWholeStaff(List<Staff> staff) throws SQLException {
        staffSet.beforeFirst();

        while (staffSet.next()) {
            staffSet.deleteRow();
        }

        for (Staff s : staff) {
            staffSet.moveToInsertRow();
            staffSet.updateString(1, s.getId());
            staffSet.updateString(2, s.getName());
            staffSet.updateString(3, s.getPosition().toString());
            staffSet.updateString(4, s.getUsername());
            staffSet.updateString(5, s.getPassword());
            staffSet.insertRow();
        }

        staff_stmt.close();
        staffSet.close();

        staff_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        staffSet = staff_stmt.executeQuery(all_staff);
    }

    public void updateWholeDiscounts(List<Discount> discounts) throws SQLException {
        discountSet.beforeFirst();

        while (discountSet.next()) {
            discountSet.deleteRow();
        }

        for (Discount d : discounts) {
            discountSet.moveToInsertRow();
            discountSet.updateString(1, d.getId());
            discountSet.updateString(2, d.getName());
            discountSet.updateDouble(3, d.getPercentage());
            discountSet.insertRow();
        }

        discounts_stmt.close();
        discountSet.close();

        discounts_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        discountSet = discounts_stmt.executeQuery(all_discounts);
    }

    public void updateWholeConfigs(HashMap<String, String> configs) throws SQLException {
        configSet.beforeFirst();

        while (configSet.next()) {
            configSet.deleteRow();
        }

        for (Map.Entry pair : configs.entrySet()) {
            configSet.moveToInsertRow();
            configSet.updateString("NAME", "" + pair.getKey());
            configSet.updateString("VALUE", "" + pair.getValue());
            configSet.insertRow();
        }

        configs_stmt.close();
        configSet.close();

        configs_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        configSet = configs_stmt.executeQuery(all_configs);
    }

    public void updateWholeTax(List<Tax> tax) throws SQLException {
        taxSet.beforeFirst();

        while (taxSet.next()) {
            taxSet.deleteRow();
        }

        for (Tax t : tax) {
            taxSet.moveToInsertRow();
            taxSet.updateString("ID", t.getId());
            taxSet.updateString("NAME", t.getName());
            taxSet.updateDouble("VALUE", t.getValue());
            taxSet.insertRow();
        }

        tax_stmt.close();
        taxSet.close();

        tax_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        taxSet = tax_stmt.executeQuery(all_tax);
    }

    public void updateWholeCategorys(List<Category> categorys) throws SQLException {
        categorySet.beforeFirst();

        while (categorySet.next()) {
            categorySet.deleteRow();
        }

        for (Category c : categorys) {
            categorySet.moveToInsertRow();
            categorySet.updateString("ID", c.getID());
            categorySet.updateString("NAME", c.getName());
            categorySet.insertRow();
        }

        category_stmt.close();
        categorySet.close();

        category_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        categorySet = category_stmt.executeQuery(all_categorys);
    }

    @Override
    public String toString() {
        return "Connected to database " + this.address + "\nOn user " + this.username;
    }
}
