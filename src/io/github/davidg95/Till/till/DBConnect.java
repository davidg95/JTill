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
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private final Semaphore productSem;
    private final Semaphore customerSem;
    private final Semaphore staffSem;
    private final Semaphore discountSem;
    private final Semaphore taxSem;
    private final Semaphore categorySem;

    public DBConnect() {
        productSem = new Semaphore(1);
        customerSem = new Semaphore(1);
        staffSem = new Semaphore(1);
        discountSem = new Semaphore(1);
        taxSem = new Semaphore(1);
        categorySem = new Semaphore(1);
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
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "     SELL_START TIME,\n"
                + "     SELL_END TIME,\n"
                + "     TIME_RESTRICT BOOLEAN not null,\n"
                + "     BUTTON BOOLEAN not null,\n"
                + "     COLOR INT,\n"
                + "     MINIMUM_AGE INT not null\n"
                + ")";
        String discounts = "create table \"APP\".DISCOUNTS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "	PERCENTAGE DOUBLE not null\n"
                + ")";
        String tax = "create table \"APP\".TAX\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "	VALUE DOUBLE not null\n"
                + ")";
        String configs = "create table APP.CONFIGS\n"
                + "(\n"
                + "	NAME INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	VALUE VARCHAR(20) not null\n"
                + ")";
        String customers = "create table \"APP\".CUSTOMERS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
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
                + "	DISCOUNT_ID INT references DISCOUNTS(ID),\n"
                + "	LOYALTY_POINTS INTEGER not null\n"
                + ")";
        String products = "create table \"APP\".PRODUCTS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	BARCODE VARCHAR(20),\n"
                + "	NAME VARCHAR(50) not null,\n"
                + "     OPEN_PRICE BOOLEAN not null,\n"
                + "	PRICE DOUBLE,\n"
                + "	STOCK INTEGER,\n"
                + "	COMMENTS VARCHAR(200),\n"
                + "	SHORT_NAME VARCHAR(50) not null,\n"
                + "	CATEGORY_ID INT not null references CATEGORYS(ID),\n"
                + "	TAX_ID INT not null references TAX(ID),\n"
                + "	COST_PRICE DOUBLE,\n"
                + "	MIN_PRODUCT_LEVEL INTEGER,\n"
                + "	MAX_PRODUCT_LEVEL INTEGER,\n"
                + "     BUTTON BOOLEAN not null,\n"
                + "     COLOR INT,\n"
                + "	DISCOUNT_ID INT not null references DISCOUNTS(ID)\n"
                + ")";
        String staff = "create table \"APP\".STAFF\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(50) not null,\n"
                + "	POSITION VARCHAR(20) not null,\n"
                + "	USERNAME VARCHAR(20) not null,\n"
                + "	PASSWORD VARCHAR(20) not null\n"
                + ")";

        Statement stmt = con.createStatement();
        stmt.execute(tax);
        stmt.execute(categorys);
        stmt.execute(discounts);
        stmt.execute(configs);
        stmt.execute(customers);
        stmt.execute(products);
        stmt.execute(staff);

        String addCategory = "INSERT INTO CATEGORYS (NAME, TIME_RESTRICT, BUTTON, MINIMUM_AGE) VALUES ('Default','FALSE',false,0)";
        String addTax = "INSERT INTO TAX (NAME, VALUE) VALUES ('ZERO',0.0)";
        String addDiscount = "INSERT INTO DISCOUNTS (NAME, PERCENTAGE) VALUES ('NONE',0.0)";
        stmt.executeUpdate(addCategory);
        stmt.executeUpdate(addTax);
        stmt.executeUpdate(addDiscount);
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

    }

    /**
     * Method to close the database connection. This will close the data sets
     * and close the connection.
     */
    public void close() {
        try {
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
        String query = "SELECT * FROM PRODUCTS";
        Statement stmt = con.createStatement();
        List<Product> products;
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            ResultSet set = stmt.executeQuery(query);
            products = new ArrayList<>();
            while (set.next()) {
                int code = set.getInt("ID");
                String barcode = set.getString("BARCODE");
                String name = set.getString("NAME");
                boolean open = set.getBoolean("OPEN_PRICE");
                double price = set.getDouble("PRICE");
                int stock = set.getInt("STOCK");
                String comments = set.getString("COMMENTS");
                String shortName = set.getString("SHORT_NAME");
                int categoryID = set.getInt("CATEGORY_ID");
                int taxID = set.getInt("TAX_ID");
                double costPrice = set.getDouble("COST_PRICE");
                int minStock = set.getInt("MIN_PRODUCT_LEVEL");
                int maxStock = set.getInt("MAX_PRODUCT_LEVEL");
                boolean button = set.getBoolean("BUTTON");
                int color = set.getInt("COLOR");
                int discountID = set.getInt("DISCOUNT_ID");

                Product p = new Product(name, shortName, categoryID, comments, taxID, discountID, button, color, open, price, costPrice, stock, minStock, maxStock, barcode, code);

                products.add(p);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }

        return products;
    }

    private List<Product> getProductsFromResultSet(ResultSet set) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (set.next()) {
            int code = set.getInt("ID");
            String barcode = set.getString("BARCODE");
            String name = set.getString("NAME");
            boolean open = set.getBoolean("OPEN_PRICE");
            double price = set.getDouble("PRICE");
            int stock = set.getInt("STOCK");
            String comments = set.getString("COMMENTS");
            String shortName = set.getString("SHORT_NAME");
            int categoryID = set.getInt("CATEGORY_ID");
            int taxID = set.getInt("TAX_ID");
            double costPrice = set.getDouble("COST_PRICE");
            int minStock = set.getInt("MIN_PRODUCT_LEVEL");
            int maxStock = set.getInt("MAX_PRODUCT_LEVEL");
            boolean button = set.getBoolean("BUTTON");
            int color = set.getInt("COLOR");
            int discountID = set.getInt("DISCOUNT_ID");

            Product p = new Product(name, shortName, categoryID, comments, taxID, discountID, button, color, open, price, costPrice, stock, minStock, maxStock, barcode, code);

            products.add(p);
        }

        return products;
    }

    /**
     * Method to add a new product to the database.
     *
     * @param p the new product to add.
     * @throws SQLException if there was an error adding the product to the
     * database.
     */
    public void addProduct(Product p) throws SQLException {
        String query = "INSERT INTO PRODUCTS (BARCODE, NAME, OPEN_PRICE, PRICE, STOCK, COMMENTS, SHORT_NAME, CATEGORY_ID, TAX_ID, COST_PRICE, MIN_PRODUCT_LEVEL, MAX_PRODUCT_LEVEL, BUTTON, COLOR, DISCOUNT_ID) VALUES (" + p.getSQLInsertString() + ")";
        try (Statement stmt = con.createStatement()) {
            try {
                productSem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                stmt.executeUpdate(query);
            } catch (SQLException ex) {
                throw ex;
            } finally {
                productSem.release();
            }
        }
    }

    public void updateProduct(Product p) throws SQLException, ProductNotFoundException {
        String query = p.getSQlUpdateString();
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        if (value == 0) {
            throw new ProductNotFoundException(p.getProductCode() + "");
        }
    }

    /**
     * Method to check if a barcode already exists in the database.
     *
     * @param barcode the barcode to check.
     * @return true or false indicating whether the barcode already exists.
     * @throws SQLException if there was an error checking the barcode.
     */
    public boolean checkBarcode(String barcode) throws SQLException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.BARCODE = '" + barcode + "'";
        ResultSet res;
        List<Product> lp;
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            res = stmt.executeQuery(query);
            lp = getProductsFromResultSet(res);
            res.close();
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }

        return !lp.isEmpty();
    }

    /**
     * Method to remove a product from the database.
     *
     * @param p the product to remove.
     * @throws SQLException if there was an error removing the product.
     * @throws ProductNotFoundException if the product was not found.
     */
    public void removeProduct(Product p) throws SQLException, ProductNotFoundException {
        String query = "DELETE FROM PRODUCTS WHERE PRODUCTS.ID = " + p.getProductCode();
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        if (value == 0) {
            throw new ProductNotFoundException(p.getProductCode() + "");
        }
    }

    /**
     * Method to remove a product from the database.
     *
     * @param id the product to remove.
     * @throws SQLException if there was an error removing the product.
     * @throws ProductNotFoundException if the product code was not found.
     */
    public void removeProduct(int id) throws SQLException, ProductNotFoundException {
        String query = "DELETE FROM PRODUCTS WHERE PRODUCTS.ID = " + id + "";
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        if (value == 0) {
            throw new ProductNotFoundException(id + "");
        }
    }

    /**
     * Method to purchase a product and reduce its stock level by 1.
     *
     * @param code the code of the product to purchase.
     * @return the new stock level.
     * @throws SQLException if there was an error purchasing the product.
     * @throws OutOfStockException if the product is out of stock.
     * @throws ProductNotFoundException if the product was not found.
     */
    public int purchaseProduct(int code) throws SQLException, OutOfStockException, ProductNotFoundException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.ID=" + code;
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res = stmt.executeQuery(query);
        try {
            while (res.next()) {
                int stock = res.getInt("STOCK");
                res.close();
                if (stock > 0) {
                    stock--;
                } else {
                    throw new OutOfStockException(code + "");
                }
                String update = "UPDATE PRODUCTS SET STOCK=" + stock + " WHERE PRODUCTS.ID=" + code;
                stmt = con.createStatement();
                stmt.executeUpdate(update);
                return stock;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        throw new ProductNotFoundException(code + "");
    }

    /**
     * Method to get a product by its code.
     *
     * @param code the product to get.
     * @return the Product that matches the code.
     * @throws SQLException if there was an error getting the product.
     * @throws ProductNotFoundException if the product could not be found.
     */
    public Product getProduct(String code) throws SQLException, ProductNotFoundException {
        String query = "SELECT * FROM Products WHERE PRODUCTS.ID = '" + code + "'";
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Product> products;

        try {
            ResultSet res = stmt.executeQuery(query);

            products = getProductsFromResultSet(res);
            if (products.isEmpty()) {
                throw new ProductNotFoundException(code);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        return products.get(0);
    }

    /**
     * Method to get a product by its barcode.
     *
     * @param barcode the barcode to search.
     * @return the product that matches the barcode.
     * @throws SQLException if there was an error getting the product.
     * @throws ProductNotFoundException if the product could not be found.
     */
    public Product getProductByBarcode(String barcode) throws SQLException, ProductNotFoundException {
        String query = "SELECT * FROM Products WHERE PRODUCTS.BARCODE = '" + barcode + "'";
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Product> products;

        try {
            ResultSet res = stmt.executeQuery(query);

            products = getProductsFromResultSet(res);
            if (products.isEmpty()) {
                throw new ProductNotFoundException(barcode);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        return products.get(0);
    }

    /**
     * Method to set the stock level of a product.
     *
     * @param code the product to set.
     * @param stock the new stock level.
     * @throws SQLException if there was an error setting the stock.
     * @throws ProductNotFoundException if the product could not be found.
     */
    public void setStock(int code, int stock) throws SQLException, ProductNotFoundException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.ID=" + code;
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                res.close();
                String update = "UPDATE PRODUCTS SET STOCK=" + stock + " WHERE PRODUCTS.ID='" + code + "'";
                stmt = con.createStatement();
                stmt.executeUpdate(update);
                return;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        throw new ProductNotFoundException(code + "");
    }

    public List<Discount> getProductsDiscount(Product p) throws SQLException {
        String query = "SELECT * FROM DISCOUNTS, PRODUCTS WHERE PRODUCTS.ID = " + p.getProductCode() + " AND PRODUCTS.DISCOUNT_ID = DISCOUNTS.ID";
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Discount> discounts;
        try {
            ResultSet res = stmt.executeQuery(query);
            discounts = getDiscountsFromResultSet(res);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
            discountSem.release();
        }
        return discounts;
    }

    public int getProductCount() throws SQLException {
        String query = "SELECT * FROM PRODUCTS";
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res;
        try {
            res = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }

        List<Product> products = getProductsFromResultSet(res);

        return products.size();
    }

    //Customer Methods
    public List<Customer> getAllCustomers() throws SQLException {
        String query = "SELECT * FROM CUSTOMERS";
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Customer> customers;
        try {
            ResultSet set = stmt.executeQuery(query);
            customers = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                String phone = set.getString("PHONE");
                String mobile = set.getString("MOBILE");
                String email = set.getString("EMAIL");
                String address1 = set.getString("ADDRESS_LINE_1");
                String address2 = set.getString("ADDRESS_LINE_2");
                String town = set.getString("TOWN");
                String county = set.getString("COUNTY");
                String country = set.getString("COUNTRY");
                String postcode = set.getString("POSTCODE");
                String notes = set.getString("NOTES");
                int discount = set.getInt("DISCOUNT_ID");
                int loyaltyPoints = set.getInt("LOYALTY_POINTS");

                Customer c = new Customer(name, phone, mobile, email, discount, address1, address2, town, county, country, postcode, notes, loyaltyPoints, id);

                customers.add(c);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }

        return customers;
    }

    public List<Customer> getCustomersFromResultSet(ResultSet set) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            String phone = set.getString("PHONE");
            String mobile = set.getString("MOBILE");
            String email = set.getString("EMAIL");
            String address1 = set.getString("ADDRESS_LINE_1");
            String address2 = set.getString("ADDRESS_LINE_2");
            String town = set.getString("TOWN");
            String county = set.getString("COUNTY");
            String country = set.getString("COUNTRY");
            String postcode = set.getString("POSTCODE");
            String notes = set.getString("NOTES");
            int discount = set.getInt("DISCOUNT_ID");
            int loyaltyPoints = set.getInt("LOYALTY_POINTS");

            Customer c = new Customer(name, phone, mobile, email, discount, address1, address2, town, county, country, postcode, notes, loyaltyPoints, id);

            customers.add(c);
        }

        return customers;
    }

    /**
     * Method to add a new product to the database.
     *
     * @param c the new customer to add.
     * @throws SQLException if there was an error adding the customer to the
     * database.
     */
    public void addCustomer(Customer c) throws SQLException {
        String query = "INSERT INTO CUSTOMERS (NAME, PHONE, MOBILE, EMAIL, ADDRESS_LINE_1, ADDRESS_LINE_2, TOWN, COUNTY, COUNTRY, POSTCODE, NOTES, DISCOUNT_ID, LOYALTY_POINTS) VALUES (" + c.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }
    }

    public void updateCustomer(Customer c) throws SQLException, CustomerNotFoundException {
        String query = c.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }
        if (value == 0) {
            throw new CustomerNotFoundException(c.getId() + "");
        }
    }

    public void removeCustomer(Customer c) throws SQLException, CustomerNotFoundException {
        String query = "DELETE FROM CUSTOMERS WHERE CUSTOMERS.ID = " + c.getId();
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }
        if (value == 0) {
            throw new CustomerNotFoundException(c.getId() + "");
        }
    }

    public void removeCustomer(int id) throws SQLException, CustomerNotFoundException {
        String query = "DELETE FROM CUSTOMERS WHERE CUSTOMERS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }
        if (value == 0) {
            throw new CustomerNotFoundException(id + "");
        }
    }

    public Customer getCustomer(int id) throws SQLException, CustomerNotFoundException {
        String query = "SELECT * FROM CUSTOMERS WHERE CUSTOMERS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Customer> customers;
        try {
            ResultSet res = stmt.executeQuery(query);

            customers = getCustomersFromResultSet(res);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }

        if (customers.isEmpty()) {
            throw new CustomerNotFoundException(id + "");
        }
        return customers.get(0);
    }

    public int getCustomerCount() throws SQLException {
        String query = "SELECT * FROM CUSTOMERS";
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res;
        try {
            res = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }

        List<Customer> customers = getCustomersFromResultSet(res);

        return customers.size();
    }

    //Staff Methods
    public List<Staff> getAllStaff() throws SQLException {
        String query = "SELECT * FROM STAFF";
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Staff> staff;
        try {
            ResultSet set = stmt.executeQuery(query);
            staff = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                String position = set.getString("POSITION");
                String uname = set.getString("USERNAME");
                String pword = set.getString("PASSWORD");

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
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }

        return staff;
    }

    public List<Staff> getStaffFromResultSet(ResultSet set) throws SQLException {
        List<Staff> staff = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            String position = set.getString("POSITION");
            String uname = set.getString("USERNAME");
            String pword = set.getString("PASSWORD");

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

    public void addStaff(Staff s) throws SQLException {
        String query = "INSERT INTO STAFF (NAME, POSITION, USERNAME, PASSWORD) VALUES (" + s.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }
    }

    public void updateStaff(Staff s) throws SQLException, StaffNotFoundException {
        String query = s.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }
        if (value == 0) {
            throw new StaffNotFoundException(s.getId() + "");
        }
    }

    public void removeStaff(Staff s) throws SQLException, StaffNotFoundException {
        String query = "DELETE FROM STAFF WHERE STAFF.ID = " + s.getId();
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }
        if (value == 0) {
            throw new StaffNotFoundException(s.getId() + "");
        }
    }

    public void removeStaff(int id) throws SQLException, StaffNotFoundException {
        String query = "DELETE FROM STAFF WHERE STAFF.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }
        if (value == 0) {
            throw new StaffNotFoundException(id + "");
        }
    }

    public Staff getStaff(int id) throws SQLException, StaffNotFoundException {
        String query = "SELECT * FROM STAFF WHERE STAFF.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Staff> staff;
        try {
            ResultSet set = stmt.executeQuery(query);

            staff = getStaffFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }

        if (staff.isEmpty()) {
            throw new StaffNotFoundException(id + "");
        }

        return staff.get(0);
    }

    public Staff login(String username, String password) throws SQLException, LoginException {
        String query = "SELECT * FROM STAFF WHERE STAFF.USERNAME = '" + username + "'";
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res;
        try {
            res = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }

        List<Staff> staff = getStaffFromResultSet(res);

        if (staff.isEmpty()) {
            throw new LoginException(username + " could not be found");
        }

        Staff s = staff.get(0);

        if (s.getPassword().equals(password)) {
            return s;
        }

        throw new LoginException("Incorrect Password");
    }

    public int staffCount() throws SQLException {
        String query = "SELECT * FROM STAFF";
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res;
        try {
            res = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }

        List<Staff> staff = getStaffFromResultSet(res);

        return staff.size();
    }

    //Discount Methods
    public List<Discount> getAllDiscounts() throws SQLException {
        String query = "SELECT * FROM DISCOUNTS";
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Discount> discounts;
        try {
            ResultSet set = stmt.executeQuery(query);
            discounts = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                double percentage = set.getDouble("PERCENTAGE");

                Discount d = new Discount(id, name, percentage);

                discounts.add(d);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }

        return discounts;
    }

    public List<Discount> getDiscountsFromResultSet(ResultSet set) throws SQLException {
        List<Discount> discounts = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            double percentage = set.getDouble("PERCENTAGE");

            Discount d = new Discount(id, name, percentage);

            discounts.add(d);
        }

        return discounts;
    }

    public void addDiscount(Discount d) throws SQLException {
        String query = "INSERT INTO DISCOUNTS (NAME, PERCENTAGE) VALUES (" + d.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }
    }

    public void updateDiscount(Discount d) throws SQLException, DiscountNotFoundException {
        String query = d.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }
        if (value == 0) {
            throw new DiscountNotFoundException(d.getId() + "");
        }
    }

    public void removeDiscount(Discount d) throws SQLException, DiscountNotFoundException {
        String query = "DELETE FROM DISCOUNTS WHERE DISCOUNTS.ID = " + d.getId();
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }
        if (value == 0) {
            throw new DiscountNotFoundException(d.getId() + "");
        }
    }

    public void removeDiscount(int id) throws SQLException, DiscountNotFoundException {
        String query = "DELETE FROM DISCOUNTS WHERE DISCOUNTS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }
        if (value == 0) {
            throw new DiscountNotFoundException(id + "");
        }
    }

    public Discount getDiscount(int id) throws SQLException, DiscountNotFoundException {
        String query = "SELECT * FROM DISCOUNTS WHERE DISCOUNTS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Discount> discounts;
        try {
            ResultSet set = stmt.executeQuery(query);

            discounts = getDiscountsFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }

        if (discounts.isEmpty()) {
            throw new DiscountNotFoundException(id + "");
        }

        return discounts.get(0);
    }

    //Tax Methods
    public List<Tax> getAllTax() throws SQLException {
        String query = "SELECT * FROM TAX";
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Tax> tax;
        try {
            ResultSet set = stmt.executeQuery(query);
            tax = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                double value = set.getDouble("VALUE");
                Tax t = new Tax(id, name, value);

                tax.add(t);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }

        return tax;
    }

    public List<Tax> getTaxFromResultSet(ResultSet set) throws SQLException {
        List<Tax> tax = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            double value = set.getDouble("VALUE");
            Tax t = new Tax(id, name, value);

            tax.add(t);
        }

        return tax;
    }

    public void addTax(Tax t) throws SQLException {
        String query = "INSERT INTO TAX (NAME, VALUE) VALUES (" + t.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }
    }

    public void updateTax(Tax t) throws SQLException, TaxNotFoundException {
        String query = t.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }
        if (value == 0) {
            throw new TaxNotFoundException(t.getId() + "");
        }
    }

    public void removeTax(Tax t) throws SQLException, TaxNotFoundException {
        String query = "DELETE FROM TAX WHERE TAX.ID = " + t.getId();
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }
        if (value == 0) {
            throw new TaxNotFoundException(t.getId() + "");
        }
    }

    public void removeTax(int id) throws SQLException, TaxNotFoundException {
        String query = "DELETE FROM TAX WHERE TAX.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }
        if (value == 0) {
            throw new TaxNotFoundException(id + "");
        }
    }

    public Tax getTax(int id) throws SQLException, TaxNotFoundException {
        String query = "SELECT * FROM TAX WHERE TAX.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Tax> tax;
        try {
            ResultSet set = stmt.executeQuery(query);

            tax = getTaxFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }

        if (tax.isEmpty()) {
            throw new TaxNotFoundException(id + "");
        }

        return tax.get(0);
    }

    //Category Methods
    public List<Category> getAllCategorys() throws SQLException {
        String query = "SELECT * FROM CATEGORYS";
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Category> categorys;
        try {
            ResultSet set = stmt.executeQuery(query);
            categorys = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                Time startSell = set.getTime("SELL_START");
                Time endSell = set.getTime("SELL_END");
                boolean timeRestrict = set.getBoolean("TIME_RESTRICT");
                int minAge = set.getInt("MINIMUM_AGE");
                boolean button = set.getBoolean("BUTTON");
                int color = set.getInt("COLOR");
                Category c = new Category(id, name, startSell, endSell, timeRestrict, minAge, button, color);
                categorys.add(c);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }

        return categorys;
    }

    public List<Category> getCategorysFromResultSet(ResultSet set) throws SQLException {
        List<Category> categorys = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            Time startSell = set.getTime("SELL_START");
            Time endSell = set.getTime("SELL_END");
            boolean timeRestrict = set.getBoolean("TIME_RESTRICT");
            int minAge = set.getInt("MINIMUM_AGE");
            boolean button = set.getBoolean("BUTTON");
            int color = set.getInt("COLOR");
            Category c = new Category(id, name, startSell, endSell, timeRestrict, minAge, button, color);
            categorys.add(c);
        }
        return categorys;
    }

    public void addCategory(Category c) throws SQLException {
        String query = "INSERT INTO CATEGORYS (NAME, SELL_START, SELL_END, TIME_RESTRICT, BUTTON, COLOR, MINIMUM_AGE) VALUES (" + c.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }
    }

    public void updateCategory(Category c) throws SQLException, CategoryNotFoundException {
        String query = c.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }
        if (value == 0) {
            throw new CategoryNotFoundException(c.getID() + "");
        }
    }

    public void removeCategory(Category c) throws SQLException, CategoryNotFoundException {
        String query = "DELETE FROM CATEGORYS WHERE CATEGORYS.ID = " + c.getID();
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }
        if (value == 0) {
            throw new CategoryNotFoundException(c.getID() + "");
        }
    }

    public void removeCategory(int id) throws SQLException, CategoryNotFoundException {
        String query = "DELETE FROM CATEGORYS WHERE CATEGORYS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }
        if (value == 0) {
            throw new CategoryNotFoundException(id + "");
        }
    }

    public Category getCategory(int id) throws SQLException, CategoryNotFoundException {
        String query = "SELECT * FROM CATEGORYS WHERE CATEGORYS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Category> categorys;
        try {
            ResultSet set = stmt.executeQuery(query);

            categorys = getCategorysFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }

        if (categorys.isEmpty()) {
            throw new CategoryNotFoundException(id + "");
        }

        return categorys.get(0);
    }

    public List<Product> getProductsInCategory(int id) throws SQLException {
        String query = "SELECT * FROM PRODUCTS, CATEGORYS WHERE CATEGORYS.ID = PRODUCTS.CATEGORY_ID AND CATEGORYS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Product> products;
        try {
            ResultSet set = stmt.executeQuery(query);

            products = getProductsFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
            productSem.release();
        }

        return products;
    }

    @Override
    public String toString() {
        if (connected) {
            return "Connected to database " + this.address + "\nOn user " + this.username;
        } else {
            return "Not connected to database";
        }
    }
}
