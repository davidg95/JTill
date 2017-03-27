/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import org.apache.derby.jdbc.EmbeddedDriver;

/**
 * Database connection class which handles communication with the database.
 *
 * @author David
 */
public class DBConnect implements DataConnect {

    private static final Logger LOG = Logger.getGlobal();

    /**
     * The database connection.
     */
    private Connection con;
    /**
     * The database driver.
     */
    private Driver embedded;

    //Database credentials
    public String address;
    public String username;
    public String password;

    private boolean connected; //Connection flag

    //Concurrent locks
    private final Semaphore productSem;
    private final Semaphore customerSem;
    private final Semaphore staffSem;
    private final Semaphore discountSem;
    private final Semaphore taxSem;
    private final Semaphore categorySem;
    private final Semaphore saleSem;
    private final Semaphore suspendSem;
    private final Semaphore screensSem;
    private final Semaphore tillSem;
    private final Semaphore pluSem;
    private final Semaphore wasteSem;
    private final Semaphore wasteItemSem;
    private final StampedLock wasteReasonLock;
    private final StampedLock supplierLock;

    public static String hostName;

    private GUIInterface g;

    private volatile HashMap<Staff, Sale> suspendedSales;
    private final Settings systemSettings;

    private final List<Staff> loggedIn;
    private final Semaphore loggedInSem;

    private final LogFileHandler handler;

    private final ObservableList<Till> connectedTills;

    private final String symbol;

    /**
     * Constructor which initialises the concurrent locks.
     */
    public DBConnect() {
        productSem = new Semaphore(1);
        customerSem = new Semaphore(1);
        staffSem = new Semaphore(1);
        discountSem = new Semaphore(1);
        taxSem = new Semaphore(1);
        categorySem = new Semaphore(1);
        saleSem = new Semaphore(1);
        suspendSem = new Semaphore(1);
        screensSem = new Semaphore(1);
        tillSem = new Semaphore(1);
        pluSem = new Semaphore(1);
        wasteSem = new Semaphore(1);
        wasteItemSem = new Semaphore(1);
        wasteReasonLock = new StampedLock();
        supplierLock = new StampedLock();
        suspendedSales = new HashMap<>();
        systemSettings = Settings.getInstance();
        loggedIn = new ArrayList<>();
        loggedInSem = new Semaphore(1);
        connectedTills = FXCollections.observableArrayList();
        connectedTills.addListener((ListChangeListener.Change<? extends Till> c) -> {
            g.updateTills();
        });
        handler = LogFileHandler.getInstance();
        Logger.getGlobal().addHandler(handler);
        symbol = Settings.getInstance().getSetting("CURRENCY_SYMBOL");
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
        LOG.log(Level.INFO, "Connecting to database {0}", database_address);
        con = DriverManager.getConnection(database_address, username, password);
        con.setAutoCommit(false);
        this.address = database_address;
        this.username = username;
        this.password = password;
        connected = true;
    }

    /**
     * Method which creates the database.
     *
     * @param address the database address.
     * @param username the database username.
     * @param password the database password.
     * @throws SQLException if there was a creation error.
     */
    public void create(String address, String username, String password) throws SQLException {
        LOG.log(Level.INFO, "The database does not exists, so it is getting created");
        embedded = new EmbeddedDriver();
        DriverManager.registerDriver(embedded);
        con = DriverManager.getConnection(address, username, password);
        con.setAutoCommit(false);

        this.address = address;
        this.username = username;
        this.password = password;
        connected = true;
        createTables();
    }

    private void createTables() throws SQLException {
        LOG.log(Level.INFO, "Creating tables");
        String tills = "create table APP.TILLS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "     UNCASHED DOUBLE not null\n"
                + ")";
        String categorys = "create table APP.CATEGORYS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "     SELL_START TIME,\n"
                + "     SELL_END TIME,\n"
                + "     TIME_RESTRICT BOOLEAN not null,\n"
                + "     MINIMUM_AGE INT not null\n"
                + ")";
        String plus = "create table APP.PLUS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "     CODE VARCHAR(20))";
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
        String sales = "create table APP.SALES\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "     PRICE DOUBLE,\n"
                + "     CUSTOMER int,\n"
                + "     DISCOUNT int,\n"
                + "     TIMESTAMP bigint,\n"
                + "     TERMINAL VARCHAR(20),\n"
                + "     CASHED boolean not null,\n"
                + "     STAFF int,\n"
                + "     CHARGE_ACCOUNT boolean\n"
                + ")";
        String saleItems = "create table APP.SALEITEMS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "     PRODUCT_ID INT not null references PRODUCTS(ID),\n"
                + "	TYPE VARCHAR(15),\n"
                + "     QUANTITY INT not null,\n"
                + "     PRICE double not null,\n"
                + "     SALE_ID INT not null references SALES(ID)\n"
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
                + "	LOYALTY_POINTS INTEGER,\n"
                + "     MONEY_DUE DOUBLE\n"
                + ")";
        String products = "create table \"APP\".PRODUCTS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	PLU INTEGER not null references PLUS(ID),\n"
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
                + "	MAX_PRODUCT_LEVEL INTEGER\n"
                + ")";
        String discounts = "create table \"APP\".DISCOUNTS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "	PLU INTEGER,\n"
                + "	PERCENTAGE DOUBLE not null,\n"
                + "	PRICE DOUBLE not null,\n"
                + "     TRIGGER INTEGER not null references PRODUCTS(ID)"
                + ")";
        String staff = "create table \"APP\".STAFF\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(50) not null,\n"
                + "	POSITION INTEGER not null,\n"
                + "	USERNAME VARCHAR(20) not null,\n"
                + "	PASSWORD VARCHAR(20) not null\n"
                + ")";
        String screens = "create table \"APP\".SCREENS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "         GENERATED ALWAYS AS IDENTITY\n"
                + "         (START WITH 1, INCREMENT BY 1),\n"
                + "     NAME VARCHAR(50) not null,\n"
                + "     POSITION INTEGER,\n"
                + "     COLOR INT\n"
                + ")";
        String buttons = "create table \"APP\".BUTTONS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "         GENERATED ALWAYS AS IDENTITY\n"
                + "         (START WITH 1, INCREMENT BY 1),\n"
                + "     NAME VARCHAR(50) not null,\n"
                + "     PRODUCT INT not null,\n"
                + "     COLOR INT,\n"
                + "     SCREEN_ID INT not null references SCREENS(ID)\n"
                + ")";
        String wasteReports = "create table \"APP\".WASTEREPORTS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "         GENERATED ALWAYS AS IDENTITY\n"
                + "         (START WITH 1, INCREMENT BY 1),\n"
                + "     VALUE DOUBLE,\n"
                + "     TIMESTAMP bigint\n"
                + ")";
        String wasteReasons = "create table \"APP\".WASTEREASONS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "         GENERATED ALWAYS AS IDENTITY\n"
                + "         (START WITH 1, INCREMENT BY 1),\n"
                + "     REASON VARCHAR(30)\n"
                + ")";
        String wasteItems = "create table \"APP\".WASTEITEMS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "         GENERATED ALWAYS AS IDENTITY\n"
                + "         (START WITH 1, INCREMENT BY 1),\n"
                + "     REPORT_ID INT not null references WASTEREPORTS(ID),\n"
                + "     PRODUCT INT not null references PRODUCTS(ID),\n"
                + "     QUANTITY INT,\n"
                + "     REASON INT not null references WASTEREASONS(ID)\n"
                + ")";
        String suppliers = "create table \"APP\".SUPPLIERS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "       GENERATED ALWAYS AS IDENTITY\n"
                + "         (START WITH 1, INCREMENT BY 1),\n"
                + "     NAME VARCHAR(30),\n"
                + "     ADDRESS VARCHAR(100),\n"
                + "     PHONE VARCHAR(20)\n"
                + ")";

        Statement stmt = con.createStatement();
        try {
            stmt.execute(tills);
            LOG.log(Level.INFO, "Created tills table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(tax);
            LOG.log(Level.INFO, "Created tax table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(plus);
            LOG.log(Level.INFO, "Created plus table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(categorys);
            LOG.log(Level.INFO, "Created categorys table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(configs);
            LOG.log(Level.INFO, "Created configs table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(sales);
            LOG.log(Level.INFO, "Created sales table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(customers);
            LOG.log(Level.INFO, "Created customers table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(products);
            LOG.log(Level.INFO, "Created products table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(discounts);
            LOG.log(Level.INFO, "Created discounts table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(saleItems);
            LOG.log(Level.INFO, "Created saleItems table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(staff);
            LOG.log(Level.INFO, "Created staff table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(screens);
            LOG.log(Level.INFO, "Created screens table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(buttons);
            LOG.log(Level.INFO, "Created buttons table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(wasteReports);
            LOG.log(Level.INFO, "Created waste reports table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(wasteReasons);
            LOG.log(Level.INFO, "Created table waste reasons");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(wasteItems);
            LOG.log(Level.INFO, "Created table waste items");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }
        try {
            stmt.execute(suppliers);
            LOG.log(Level.INFO, "Created table suppliers");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            error(ex);
        }

        String addCategory = "INSERT INTO CATEGORYS (NAME, TIME_RESTRICT, MINIMUM_AGE) VALUES ('Default','FALSE',0)";
        String addTax = "INSERT INTO TAX (NAME, VALUE) VALUES ('ZERO',0.0)";
        String addReason = "INSERT INTO WASTEREASONS (REASON) VALUES ('DEFAULT')";
        stmt.executeUpdate(addCategory);
        stmt.executeUpdate(addTax);
        stmt.executeUpdate(addReason);
    }

    private void error(SQLException ex) {
        JOptionPane.showMessageDialog(null, ex, "Database error", JOptionPane.ERROR_MESSAGE);
        LOG.log(Level.SEVERE, null, ex);
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
     * Method to close the database connection. This will close the data sets
     * and close the connection.
     */
    @Override
    public void close() {
        try {
            LOG.log(Level.INFO, "Disconnecting from database");
            con.close();
            LOG.log(Level.INFO, "Database disconnected");
            connected = false;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "ERROR DISCONNECTING DATABASE");
        }
    }

    /**
     * Method to check if the database is currently connected.
     *
     * @return true if it connected, false otherwise.
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        String query = "SELECT p.ID as pId, p.PLU, p.NAME as pName, p.OPEN_PRICE, p.PRICE, p.STOCK, p.COMMENTS, p.SHORT_NAME, p.CATEGORY_ID, p.TAX_ID, p.COST_PRICE, p.MIN_PRODUCT_LEVEL, p.MAX_PRODUCT_LEVEL, c.ID as cId, c.NAME as cName, c.SELL_START, c.SELL_END, c.TIME_RESTRICT, c.MINIMUM_AGE, pl.ID as plId, pl.CODE as plCode, t.ID as tId, t.NAME as tName, t.VALUE as tValue FROM PRODUCTS p, CATEGORYS c, PLUS pl, TAX t WHERE p.CATEGORY_ID = c.ID AND p.PLU = pl.ID AND p.TAX_ID = t.ID";
        Statement stmt = con.createStatement();
        List<Product> products = new ArrayList<>();
        try {
            productSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            products = new ArrayList<>();
            while (set.next()) {
                int code = set.getInt("pId");
                int pluID = set.getInt("plId");
                String pluCode = set.getString("plCode");
                Plu plu = new Plu(pluID, pluCode);
                String name = set.getString("pName");
                boolean open = set.getBoolean("OPEN_PRICE");
                BigDecimal price = new BigDecimal(Double.toString(set.getDouble("PRICE")));
                int stock = set.getInt("STOCK");
                String comments = set.getString("COMMENTS");
                String shortName = set.getString("SHORT_NAME");
                int id = set.getInt("cId");
                String catName = set.getString("cName");
                Time start = set.getTime("SELL_START");
                Time end = set.getTime("SELL_END");
                boolean restrict = set.getBoolean("TIME_RESTRICT");
                int minAge = set.getInt("MINIMUM_AGE");
                Category category = new Category(id, catName, start, end, restrict, minAge);
                int taxID = set.getInt("tId");
                String taxName = set.getString("tName");
                double taxValue = set.getDouble("tValue");
                Tax tax = new Tax(taxID, taxName, taxValue);
                BigDecimal costPrice = new BigDecimal(Double.toString(set.getDouble("COST_PRICE")));
                int minStock = set.getInt("MIN_PRODUCT_LEVEL");
                int maxStock = set.getInt("MAX_PRODUCT_LEVEL");

                Product p = new Product(name, shortName, category, comments, tax, open, price, costPrice, stock, minStock, maxStock, plu, code);

                products.add(p);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            productSem.release();
        }
        return products;
    }

    private List<Product> getProductsFromResultSet(ResultSet set) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (set.next()) {
            int code = set.getInt("pId");
            int pluID = set.getInt("plId");
            String pluCode = set.getString("plCode");
            Plu plu = new Plu(pluID, pluCode);
            String name = set.getString("pName");
            boolean open = set.getBoolean("OPEN_PRICE");
            BigDecimal price = new BigDecimal(Double.toString(set.getDouble("PRICE")));
            int stock = set.getInt("STOCK");
            String comments = set.getString("COMMENTS");
            String shortName = set.getString("SHORT_NAME");
            int categoryID = set.getInt("cId");
            String catName = set.getString("cName");
            Time start = set.getTime("SELL_START");
            Time end = set.getTime("SELL_END");
            boolean restrict = set.getBoolean("TIME_RESTRICT");
            int minAge = set.getInt("MINIMUM_AGE");
            Category category = new Category(categoryID, catName, start, end, restrict, minAge);
            int taxID = set.getInt("tId");
            String taxName = set.getString("tName");
            double taxValue = set.getDouble("tValue");
            Tax tax = new Tax(taxID, taxName, taxValue);
            BigDecimal costPrice = new BigDecimal(Double.toString(set.getDouble("COST_PRICE")));
            int minStock = set.getInt("MIN_PRODUCT_LEVEL");
            int maxStock = set.getInt("MAX_PRODUCT_LEVEL");

            Product p = new Product(name, shortName, category, comments, tax, open, price, costPrice, stock, minStock, maxStock, plu, code);

            products.add(p);
        }
        return products;
    }

    /**
     * Method to add a new product to the database.
     *
     * @param p the new product to add.
     * @return the product that was added.
     * @throws SQLException if there was an error adding the product to the
     * database.
     */
    @Override
    public Product addProduct(Product p) throws SQLException {
        String query = "INSERT INTO PRODUCTS (PLU, NAME, OPEN_PRICE, PRICE, STOCK, COMMENTS, SHORT_NAME, CATEGORY_ID, TAX_ID, COST_PRICE, MIN_PRODUCT_LEVEL, MAX_PRODUCT_LEVEL) VALUES (" + p.getSQLInsertString() + ")";
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            productSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                p.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            productSem.release();
        }
        LOG.log(Level.INFO, "New Product {0} added", p.getId());
        return p;
    }

    @Override
    public Product updateProduct(Product p) throws SQLException, ProductNotFoundException {
        String query = p.getSQlUpdateString();
        Statement stmt = con.createStatement();
        int value;
        try {
            productSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new ProductNotFoundException("Product id " + p.getId() + " could not be found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            productSem.release();
        }
        LOG.log(Level.INFO, "Product {0} updated", p.getId());
        return p;
    }

    /**
     * Method to check if a barcode already exists in the database.
     *
     * @param barcode the barcode to check.
     * @return true or false indicating whether the barcode already exists.
     * @throws SQLException if there was an error checking the barcode.
     */
    @Override
    public boolean checkBarcode(String barcode) throws SQLException {
        String query = "SELECT * FROM PLUS WHERE CODE = '" + barcode + "'";
        ResultSet res;
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
            res = stmt.executeQuery(query);
            if (res.next()) {
                res.close();
                return false;
            }
            res.close();
            return true;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            productSem.release();
        }
        return false;
    }

    /**
     * Method to remove a product from the database.
     *
     * @param p the product to remove.
     * @throws SQLException if there was an error removing the product.
     * @throws ProductNotFoundException if the product was not found.
     */
    @Override
    public void removeProduct(Product p) throws SQLException, ProductNotFoundException {
        removeProduct(p.getId());
    }

    /**
     * Method to remove a product from the database.
     *
     * @param id the product to remove.
     * @throws SQLException if there was an error removing the product.
     * @throws ProductNotFoundException if the product code was not found.
     */
    @Override
    public void removeProduct(int id) throws SQLException, ProductNotFoundException {
        String query = "DELETE FROM PRODUCTS WHERE PRODUCTS.ID = " + id + "";
        String iQuery = "DELETE FROM WASTEITEMS WHERE PRODUCT = " + id;
        Statement stmt = con.createStatement();
        int value;
        try {
            wasteItemSem.acquire();
            stmt.executeUpdate(iQuery);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteItemSem.release();
        }
        try {
            productSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new ProductNotFoundException(id + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            productSem.release();
        }

        LOG.log(Level.INFO, "Product {0} removed", id);
    }

    /**
     * Method to purchase a product and reduce its stock level by 1.
     *
     * @param p the product to purchase.
     * @param amount the amount of the product to purchase.
     * @return the new stock level.
     * @throws SQLException if there was an error purchasing the product.
     * @throws OutOfStockException if the product is out of stock.
     * @throws ProductNotFoundException if the product was not found.
     */
    @Override
    public int purchaseProduct(Product p, int amount) throws SQLException, OutOfStockException, ProductNotFoundException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.ID=" + p.getId();
        Statement stmt = con.createStatement();
        try {
            LOG.log(Level.INFO, "Purchase product {0}", p.getId());
            productSem.acquire();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                int stock = res.getInt("STOCK");
                res.close();
                stock -= amount;
                String update = "UPDATE PRODUCTS SET STOCK=" + stock + " WHERE PRODUCTS.ID=" + p.getId();
                stmt = con.createStatement();
                stmt.executeUpdate(update);
                if (stock < p.getMinStockLevel()) {
                    LOG.log(Level.WARNING, "{0} is below minimum stock level", p.getId());
                    g.logWarning("WARNING- Product " + p.getId() + " is below is minimum level!");
                }
                return stock;
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            productSem.release();
        }
        throw new ProductNotFoundException(p.getId() + " could not be found");
    }

    /**
     * Method to get a product by its code.
     *
     * @param code the product to get.
     * @return the Product that matches the code.
     * @throws SQLException if there was an error getting the product.
     * @throws ProductNotFoundException if the product could not be found.
     */
    @Override
    public Product getProduct(int code) throws SQLException, ProductNotFoundException {
        String query = "SELECT p.ID as pId, p.PLU, p.NAME as pName, p.OPEN_PRICE, p.PRICE, p.STOCK, p.COMMENTS, p.SHORT_NAME, p.CATEGORY_ID, p.TAX_ID, p.COST_PRICE, p.MIN_PRODUCT_LEVEL, p.MAX_PRODUCT_LEVEL, c.ID as cId, c.NAME as cName, c.SELL_START, c.SELL_END, c.TIME_RESTRICT, c.MINIMUM_AGE, pl.ID as plId, pl.CODE as plCode, t.ID as tId, t.NAME as tName, t.VALUE as tValue FROM PRODUCTS p, CATEGORYS c, PLUS pl, TAX t WHERE p.CATEGORY_ID = c.ID AND p.PLU = pl.ID AND p.TAX_ID = t.ID AND p.ID=" + code;
        Statement stmt = con.createStatement();

        List<Product> products = new ArrayList<>();

        try {
            LOG.log(Level.INFO, "Get product {0}", code);
            productSem.acquire();
            ResultSet res = stmt.executeQuery(query);

            products = getProductsFromResultSet(res);
            if (products.isEmpty()) {
                throw new ProductNotFoundException("Product " + code + " could not be found");
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
    @Override
    public Product getProductByBarcode(String barcode) throws SQLException, ProductNotFoundException {
        String query = "SELECT p.ID as pId, p.PLU, p.NAME as pName, p.OPEN_PRICE, p.PRICE, p.STOCK, p.COMMENTS, p.SHORT_NAME, p.CATEGORY_ID, p.TAX_ID, p.COST_PRICE, p.MIN_PRODUCT_LEVEL, p.MAX_PRODUCT_LEVEL, c.ID as cId, c.NAME as cName, c.SELL_START, c.SELL_END, c.TIME_RESTRICT, c.MINIMUM_AGE, pl.ID as plId, pl.CODE as plCode, t.ID as tId, t.NAME as tName, t.VALUE as tValue FROM PRODUCTS p, CATEGORYS c, PLUS pl, TAX t WHERE p.CATEGORY_ID = c.ID AND p.PLU = pl.ID AND p.TAX_ID = t.ID AND pl.CODE='" + barcode + "'";
        Statement stmt = con.createStatement();
        List<Product> products = new ArrayList<>();

        try {
            LOG.log(Level.INFO, "Get Product {0}", barcode);
            productSem.acquire();
            ResultSet res = stmt.executeQuery(query);
            products = getProductsFromResultSet(res);
            if (products.isEmpty()) {
                throw new ProductNotFoundException(barcode + " could not be found");
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            productSem.release();
        }
        return products.get(0);
    }

    @Override
    public List<Discount> getProductsDiscount(Product p) throws SQLException {
        String query = "SELECT * FROM DISCOUNTS, PRODUCTS WHERE PRODUCTS.ID = " + p.getId() + " AND PRODUCTS.DISCOUNT_ID = DISCOUNTS.ID";
        Statement stmt = con.createStatement();
        List<Discount> discounts = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Get discounts for product {0}", p.getId());
            productSem.acquire();
            discountSem.acquire();
            ResultSet res = stmt.executeQuery(query);
            discounts = getDiscountsFromResultSet(res);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            productSem.release();
            discountSem.release();
        }
        return discounts;
    }

    @Override
    public List<Product> productLookup(String terms) throws IOException, SQLException {
        List<Product> products = this.getAllProducts();
        List<Product> newList = new ArrayList<>();

        products.stream().filter((p) -> (p.getLongName().toLowerCase().contains(terms.toLowerCase()) || p.getName().toLowerCase().contains(terms.toLowerCase()))).forEachOrdered((p) -> {
            newList.add(p);
        });

        return newList;
    }

    //Customer Methods
    @Override
    public List<Customer> getAllCustomers() throws SQLException {
        String query = "SELECT * FROM CUSTOMERS";
        Statement stmt = con.createStatement();
        List<Customer> customers = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Get all customers");
            customerSem.acquire();
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
                int loyaltyPoints = set.getInt("LOYALTY_POINTS");
                BigDecimal moneyDue = new BigDecimal(Double.toString(set.getDouble("MONEY_DUE")));
                Customer c = new Customer(id, name, phone, mobile, email, address1, address2, town, county, country, postcode, notes, loyaltyPoints, moneyDue);

                customers.add(c);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
            int loyaltyPoints = set.getInt("LOYALTY_POINTS");
            BigDecimal moneyDue = new BigDecimal(set.getDouble("MONEY_DUE"));

            Customer c = new Customer(id, name, phone, mobile, email, address1, address2, town, county, country, postcode, notes, loyaltyPoints, moneyDue);

            customers.add(c);
        }

        return customers;
    }

    /**
     * Method to add a new product to the database.
     *
     * @param c the new customer to add.
     * @return the customer that was added.
     * @throws SQLException if there was an error adding the customer to the
     * database.
     */
    @Override
    public Customer addCustomer(Customer c) throws SQLException {
        String query = "INSERT INTO CUSTOMERS (NAME, PHONE, MOBILE, EMAIL, ADDRESS_LINE_1, ADDRESS_LINE_2, TOWN, COUNTY, COUNTRY, POSTCODE, NOTES, LOYALTY_POINTS, MONEY_DUE) VALUES (" + c.getSQLInsertString() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            LOG.log(Level.INFO, "Add customer {0}", c.getId());
            customerSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                c.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            customerSem.release();
        }
        return c;
    }

    @Override
    public Customer updateCustomer(Customer c) throws SQLException, CustomerNotFoundException {
        String query = c.getSQLUpdateString();
        Statement stmt = con.createStatement();
        int value;
        try {
            LOG.log(Level.INFO, "Update customer {0}", c.getId());
            customerSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new CustomerNotFoundException(c.getId() + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            customerSem.release();
        }
        return c;
    }

    @Override
    public void removeCustomer(Customer c) throws SQLException, CustomerNotFoundException {
        removeCustomer(c.getId());
    }

    @Override
    public void removeCustomer(int id) throws SQLException, CustomerNotFoundException {
        String query = "DELETE FROM CUSTOMERS WHERE CUSTOMERS.ID = " + id;
        Statement stmt = con.createStatement();
        int value;
        try {
            LOG.log(Level.INFO, "Remove customer {0}", id);
            customerSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new CustomerNotFoundException(id + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            customerSem.release();
        }
    }

    @Override
    public Customer getCustomer(int id) throws SQLException, CustomerNotFoundException {
        String query = "SELECT * FROM CUSTOMERS WHERE CUSTOMERS.ID = " + id;
        Statement stmt = con.createStatement();
        List<Customer> customers = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Get customer {0}", id);
            customerSem.acquire();
            ResultSet res = stmt.executeQuery(query);
            customers = getCustomersFromResultSet(res);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            customerSem.release();
        }
        if (customers.isEmpty()) {
            throw new CustomerNotFoundException("Customer " + id + " could not be found");
        }
        return customers.get(0);
    }

    @Override
    public List<Customer> getCustomerByName(String name) throws SQLException, CustomerNotFoundException {
        String query = "SELECT * FROM CUSTOMERS WHERE CUSTOMERS.NAME = " + name;
        Statement stmt = con.createStatement();
        List<Customer> customers = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Get customer {0}", name);
            customerSem.acquire();
            ResultSet res = stmt.executeQuery(query);
            customers = getCustomersFromResultSet(res);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            customerSem.release();
        }
        if (customers.isEmpty()) {
            throw new CustomerNotFoundException("Customer " + name + " could not be found");
        }
        return customers;
    }

    @Override
    public List<Customer> customerLookup(String terms) throws IOException, SQLException {
        String query = "SELECT * FROM CUSTOMERS";
        Statement stmt = con.createStatement();
        List<Customer> customers = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Search customers for {0}", terms);
            customerSem.acquire();
            ResultSet res = stmt.executeQuery(query);
            customers = getCustomersFromResultSet(res);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            customerSem.release();
        }

        List<Customer> newList = new ArrayList<>();

        customers.stream().filter((c) -> (c.getName().toLowerCase().contains(terms.toLowerCase()))).forEachOrdered((c) -> {
            newList.add(c);
        });

        return newList;
    }

    //Staff Methods
    @Override
    public List<Staff> getAllStaff() throws SQLException {
        String query = "SELECT * FROM STAFF";
        Statement stmt = con.createStatement();
        List<Staff> staff = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Get all staff");
            staffSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            staff = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                int position = set.getInt("POSITION");
                String uname = set.getString("USERNAME");
                String pword = set.getString("PASSWORD");
                Staff s = new Staff(id, name, position, uname, pword);
                staff.add(s);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
            int position = set.getInt("POSITION");
            String uname = set.getString("USERNAME");
            String pword = set.getString("PASSWORD");

            Staff s = new Staff(id, name, position, uname, pword);

            staff.add(s);
        }

        return staff;
    }

    @Override
    public Staff addStaff(Staff s) throws SQLException {
        String query = "INSERT INTO STAFF (NAME, POSITION, USERNAME, PASSWORD) VALUES (" + s.getSQLInsertString() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            LOG.log(Level.INFO, "Add staff {0}", s.getId());
            staffSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                s.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            staffSem.release();
        }
        return s;
    }

    @Override
    public Staff updateStaff(Staff s) throws SQLException, StaffNotFoundException {
        String query = s.getSQLUpdateString();
        Statement stmt = con.createStatement();
        int value;
        try {
            LOG.log(Level.INFO, "Update staff {0}", s.getId());
            staffSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new StaffNotFoundException(s.getId() + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            staffSem.release();
        }
        return s;
    }

    @Override
    public void removeStaff(Staff s) throws SQLException, StaffNotFoundException {
        removeStaff(s.getId());
    }

    @Override
    public void removeStaff(int id) throws SQLException, StaffNotFoundException {
        String query = "DELETE FROM STAFF WHERE STAFF.ID = " + id;
        Statement stmt = con.createStatement();
        int value;
        try {
            LOG.log(Level.INFO, "Remove staff {0}", id);
            staffSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new StaffNotFoundException(id + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            staffSem.release();
        }
    }

    @Override
    public Staff getStaff(int id) throws SQLException, StaffNotFoundException {
        String query = "SELECT * FROM STAFF WHERE STAFF.ID = " + id;
        Statement stmt = con.createStatement();
        List<Staff> staff = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Get staff {0}", id);
            staffSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            staff = getStaffFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            staffSem.release();
        }

        if (staff.isEmpty()) {
            throw new StaffNotFoundException(id + "");
        }

        return staff.get(0);
    }

    @Override
    public Staff login(String username, String password) throws SQLException, LoginException {
        String query = "SELECT * FROM STAFF WHERE STAFF.USERNAME = '" + username.toLowerCase() + "'";
        Statement stmt = con.createStatement();
        List<Staff> staff = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Login Staff {0}", username);
            staffSem.acquire();
            ResultSet res = stmt.executeQuery(query);
            staff = getStaffFromResultSet(res);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            staffSem.release();
        }

        if (staff.isEmpty()) {
            throw new LoginException(username + " could not be found");
        }

        Staff s = staff.get(0);

        if (s.getPassword().equals(password)) {
            return s;
        }

        throw new LoginException("Incorrect Password");
    }

    @Override
    public int getStaffCount() throws SQLException {
        String query = "SELECT * FROM STAFF";
        Statement stmt = con.createStatement();
        List<Staff> staff = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Get staff count");
            staffSem.acquire();
            ResultSet res = stmt.executeQuery(query);
            staff = getStaffFromResultSet(res);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            staffSem.release();
        }

        return staff.size();
    }

    //Discount Methods
    @Override
    public List<Discount> getAllDiscounts() throws SQLException {
        String query = "SELECT * FROM DISCOUNTS";
        Statement stmt = con.createStatement();
        List<Discount> discounts = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Get all discounts");
            discountSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            discounts = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                double percentage = set.getDouble("PERCENTAGE");
                BigDecimal price = new BigDecimal(Double.toString(set.getDouble("PRICE")));
                Product p = null;
                try {
                    p = this.getProduct(set.getInt("TRIGGER"));
                } catch (ProductNotFoundException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                Discount d = new Discount(id, name, percentage, price, p);

                discounts.add(d);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
            BigDecimal price = new BigDecimal(Double.toString(set.getDouble("PRICE")));
            Product p = null;
            try {
                p = this.getProduct(set.getInt("DISCOUNTS.TRIGGER"));
            } catch (ProductNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            Discount d = new Discount(id, name, percentage, price, p);

            discounts.add(d);
        }

        return discounts;
    }

    @Override
    public Discount addDiscount(Discount d) throws SQLException {
        String query = "INSERT INTO DISCOUNTS (NAME, PERCENTAGE, PRICE, TRIGGER) VALUES (" + d.getSQLInsertString() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            LOG.log(Level.INFO, "Add discount {0}", d.getId());
            discountSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                d.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            discountSem.release();
        }
        return d;
    }

    @Override
    public Discount updateDiscount(Discount d) throws SQLException, DiscountNotFoundException {
        String query = d.getSQLUpdateString();
        Statement stmt = con.createStatement();
        int value;
        try {
            LOG.log(Level.INFO, "Update discount {0}", d.getId());
            discountSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new DiscountNotFoundException(d.getId() + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            discountSem.release();
        }
        return d;
    }

    @Override
    public void removeDiscount(Discount d) throws SQLException, DiscountNotFoundException {
        removeDiscount(d.getId());
    }

    @Override
    public void removeDiscount(int id) throws SQLException, DiscountNotFoundException {
        String query = "DELETE FROM DISCOUNTS WHERE DISCOUNTS.ID = " + id;
        Statement stmt = con.createStatement();
        int value;
        try {
            LOG.log(Level.INFO, "Remove discount {0}", id);
            value = stmt.executeUpdate(query);
            discountSem.acquire();
            if (value == 0) {
                throw new DiscountNotFoundException(id + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            discountSem.release();
        }
    }

    @Override
    public Discount getDiscount(int id) throws SQLException, DiscountNotFoundException {
        String query = "SELECT * FROM DISCOUNTS WHERE DISCOUNTS.ID = " + id;
        Statement stmt = con.createStatement();
        List<Discount> discounts = new ArrayList<>();
        try {
            LOG.log(Level.INFO, "Get discount {0}", id);
            discountSem.acquire();
            ResultSet set = stmt.executeQuery(query);

            discounts = getDiscountsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            discountSem.release();
        }

        if (discounts.isEmpty()) {
            throw new DiscountNotFoundException(id + "");
        }

        return discounts.get(0);
    }

    //Tax Methods
    @Override
    public List<Tax> getAllTax() throws SQLException {
        String query = "SELECT * FROM TAX";
        Statement stmt = con.createStatement();
        List<Tax> tax = new ArrayList<>();
        try {
            taxSem.acquire();
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
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
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

    @Override
    public Tax addTax(Tax t) throws SQLException {
        String query = "INSERT INTO TAX (NAME, VALUE) VALUES (" + t.getSQLInsertString() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            taxSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                t.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            taxSem.release();
        }
        return t;
    }

    @Override
    public Tax updateTax(Tax t) throws SQLException, TaxNotFoundException {
        String query = t.getSQLUpdateString();
        Statement stmt = con.createStatement();
        int value;
        try {
            taxSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new TaxNotFoundException(t.getId() + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            taxSem.release();
        }
        return t;
    }

    @Override
    public void removeTax(Tax t) throws SQLException, TaxNotFoundException {
        removeTax(t.getId());
    }

    @Override
    public void removeTax(int id) throws SQLException, TaxNotFoundException {
        List<Product> products = this.getProductsInTax(id);
        final Tax DEFAULT_TAX = this.getTax(1);
        for (Product p : products) {
            p.setTax(DEFAULT_TAX);
            try {
                this.updateProduct(p);
            } catch (ProductNotFoundException ex) {
            }
        }
        String query = "DELETE FROM TAX WHERE TAX.ID = " + id;
        Statement stmt = con.createStatement();
        int value;
        try {
            taxSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new TaxNotFoundException(id + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            taxSem.release();
        }
    }

    @Override
    public Tax getTax(int id) throws SQLException, TaxNotFoundException {
        String query = "SELECT * FROM TAX WHERE TAX.ID = " + id;
        Statement stmt = con.createStatement();
        List<Tax> tax = new ArrayList<>();
        try {
            taxSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            tax = getTaxFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            taxSem.release();
        }

        if (tax.isEmpty()) {
            throw new TaxNotFoundException(id + "");
        }

        return tax.get(0);
    }

    @Override
    public List<Product> getProductsInTax(int id) throws SQLException {
        String query = "SELECT * FROM PRODUCTS, TAX WHERE TAX.ID = PRODUCTS.TAX_ID AND TAX.ID = " + id;
        Statement stmt = con.createStatement();
        List<Product> products = new ArrayList<>();
        try {
            taxSem.acquire();
            productSem.acquire();
            ResultSet set = stmt.executeQuery(query);

            products = getProductsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            taxSem.release();
            productSem.release();
        }

        return products;
    }

    //Category Methods
    @Override
    public List<Category> getAllCategorys() throws SQLException {
        String query = "SELECT * FROM CATEGORYS";
        Statement stmt = con.createStatement();
        List<Category> categorys = new ArrayList<>();
        try {
            categorySem.acquire();
            ResultSet set = stmt.executeQuery(query);
            categorys = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                Time startSell = set.getTime("SELL_START");
                Time endSell = set.getTime("SELL_END");
                boolean timeRestrict = set.getBoolean("TIME_RESTRICT");
                int minAge = set.getInt("MINIMUM_AGE");
                Category c = new Category(id, name, startSell, endSell, timeRestrict, minAge);
                categorys.add(c);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
            Category c = new Category(id, name, startSell, endSell, timeRestrict, minAge);
            categorys.add(c);
        }
        return categorys;
    }

    @Override
    public Category addCategory(Category c) throws SQLException {
        String query = "INSERT INTO CATEGORYS (NAME, SELL_START, SELL_END, TIME_RESTRICT, MINIMUM_AGE) VALUES (" + c.getSQLInsertString() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            categorySem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                c.setID(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            categorySem.release();
        }
        return c;
    }

    @Override
    public Category updateCategory(Category c) throws SQLException, CategoryNotFoundException {
        String query = c.getSQLUpdateString();
        Statement stmt = con.createStatement();
        int value;
        try {
            categorySem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new CategoryNotFoundException(c.getId() + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            categorySem.release();
        }
        return c;
    }

    @Override
    public void removeCategory(Category c) throws SQLException, CategoryNotFoundException {
        removeCategory(c.getId());
    }

    @Override
    public void removeCategory(int id) throws SQLException, CategoryNotFoundException {
        List<Product> products = getProductsInCategory(id);
        final Category DEFAULT_CATEGORY = getCategory(1);
        for (Product p : products) {
            p.setCategory(DEFAULT_CATEGORY);
            try {
                updateProduct(p);
            } catch (ProductNotFoundException ex) {
            }
        }
        String query = "DELETE FROM CATEGORYS WHERE CATEGORYS.ID = " + id;
        Statement stmt = con.createStatement();
        int value;
        try {
            categorySem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new CategoryNotFoundException(id + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            categorySem.release();
        }
    }

    @Override
    public Category getCategory(int id) throws SQLException, CategoryNotFoundException {
        String query = "SELECT * FROM CATEGORYS WHERE CATEGORYS.ID = " + id;
        Statement stmt = con.createStatement();
        List<Category> categorys = new ArrayList<>();
        try {
            categorySem.acquire();
            ResultSet set = stmt.executeQuery(query);
            categorys = getCategorysFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            categorySem.release();
        }

        if (categorys.isEmpty()) {
            throw new CategoryNotFoundException(id + "");
        }

        return categorys.get(0);
    }

    @Override
    public List<Product> getProductsInCategory(int id) throws SQLException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.CATEGORY_ID = " + id;
        Statement stmt = con.createStatement();
        List<Product> products = new ArrayList<>();
        try {
            productSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            products = getProductsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            productSem.release();
        }

        return products;
    }

    public List<Sale> getSalesFromResultSet(ResultSet set) throws SQLException {
        List<Sale> sales = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            BigDecimal price = new BigDecimal(Double.toString(set.getDouble("PRICE")));
            int customerid = set.getInt("cId");
            String name = set.getString("cName");
            String phone = set.getString("PHONE");
            String mobile = set.getString("MOBILE");
            String email = set.getString("EMAIL");
            String add_1 = set.getString("ADDRESS_LINE_1");
            String add_2 = set.getString("ADDRESS_LINE_2");
            String town = set.getString("TOWN");
            String county = set.getString("COUNTRY");
            String country = set.getString("COUNTRY");
            String postcode = set.getString("POSTCODE");
            String notes = set.getString("NOTES");
            int loyalty_points = set.getInt("LOYALTY_POINTS");
            BigDecimal money_due = new BigDecimal(set.getDouble("MONEY_DUE"));
            Customer customer = new Customer(customerid, name, phone, mobile, email, add_1, add_2, town, county, country, postcode, notes, loyalty_points, money_due);
            Date date = new Date(set.getLong("TIMESTAMP"));
            String terminal = set.getString("TERMINAL");
            boolean cashed = set.getBoolean("CASHED");
            int sId = set.getInt("stId");
            String stName = set.getString("stName");
            int position = set.getInt("POSITION");
            String stUsername = set.getString("USERNAME");
            String stPassword = set.getString("PASSWORD");
            Staff staff = new Staff(sId, stName, position, stUsername, stPassword);
            Sale s = new Sale(id, price, customer, date, terminal, cashed, staff);
            s.setProducts(getItemsInSale(s));
            sales.add(s);
        }
        return sales;
    }

    @Override
    public Sale addSale(Sale s) throws SQLException {
        String query = "INSERT INTO SALES (PRICE, CUSTOMER, TIMESTAMP, TERMINAL, CASHED, STAFF, CHARGE_ACCOUNT) VALUES (" + s.getSQLInsertStatement() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            saleSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                s.setId(id);
            }
            for (SaleItem p : s.getSaleItems()) {
                addSaleItem(s, p);
                try {
                    if (p.getItem() instanceof Product) {
                        purchaseProduct((Product) p.getItem(), p.getQuantity());
                    }
                } catch (OutOfStockException ex) {
                    g.log(ex);
                } catch (ProductNotFoundException ex) {
                }
            }
            if (s.isChargeAccount()) {
                new Thread("Charge To Account") {
                    @Override
                    public void run() {
                        chargeCustomerAccount(s.getCustomer(), s.getTotal());
                    }
                }.start();
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            saleSem.release();
        }
        return s;
    }

    private void chargeCustomerAccount(Customer c, BigDecimal amount) {
        c.setMoneyDue(c.getMoneyDue().add(amount));
        try {
            updateCustomer(c);
        } catch (SQLException | CustomerNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<Sale> getAllSales() throws SQLException {
        String query = "SELECT s.ID as sId, PRICE, s.CUSTOMER as sCus, DISCOUNT, TIMESTAMP, TERMINAL, CASHED, STAFF, CHARGE_ACCOUNT, c.ID as cId, c.NAME as cName, PHONE, MOBILE, EMAIL, ADDRESS_LINE_1, ADDRESS_LINE_2, TOWN, COUNTY, COUNTRY, POSTCODE, NOTES, LOYALTY_POINTS, MONEY_DUE, st.ID as stId, st.NAME as stName, POSITION, USERNAME, PASSWORD FROM SALES s, CUSTOMERS c , STAFF st WHERE c.ID = s.CUSTOMER AND st.ID = s.STAFF";
        Statement stmt = con.createStatement();
        List<Sale> sales = new ArrayList<>();
        try {
            saleSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            sales = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                BigDecimal price = new BigDecimal(Double.toString(set.getDouble("PRICE")));
                int customerid = set.getInt("cId");
                String name = set.getString("cName");
                String phone = set.getString("PHONE");
                String mobile = set.getString("MOBILE");
                String email = set.getString("EMAIL");
                String add_1 = set.getString("ADDRESS_LINE_1");
                String add_2 = set.getString("ADDRESS_LINE_2");
                String town = set.getString("TOWN");
                String county = set.getString("COUNTRY");
                String country = set.getString("COUNTRY");
                String postcode = set.getString("POSTCODE");
                String notes = set.getString("NOTES");
                int loyalty_points = set.getInt("LOYALTY_POINTS");
                BigDecimal money_due = new BigDecimal(set.getDouble("MONEY_DUE"));
                Customer customer = new Customer(customerid, name, phone, mobile, email, add_1, add_2, town, county, country, postcode, notes, loyalty_points, money_due);
                Date date = new Date(set.getLong("TIMESTAMP"));
                String terminal = set.getString("TERMINAL");
                boolean cashed = set.getBoolean("CASHED");
                int sId = set.getInt("stId");
                String stName = set.getString("stName");
                int position = set.getInt("POSITION");
                String stUsername = set.getString("USERNAME");
                String stPassword = set.getString("PASSWORD");
                Staff staff = new Staff(sId, stName, position, stUsername, stPassword);
                Sale s = new Sale(id, price, customer, date, terminal, cashed, staff);
                s.setProducts(getItemsInSale(s));
                sales.add(s);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            saleSem.release();
        }

        return sales;
    }

    @Override
    public BigDecimal getTillTakings(String t) throws SQLException {
        String query = "SELECT * FROM SALES WHERE SALES.CASHED = FALSE AND SALES.TERMINAL = '" + t + "'";
        Statement stmt = con.createStatement();
        BigDecimal result = new BigDecimal("0");
        try {
            saleSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            while (set.next()) {
                int id = set.getInt("ID");
                BigDecimal price = new BigDecimal(Double.toString(set.getDouble("PRICE")));
                int customerid = set.getInt("CUSTOMER");
                Customer customer = null;
                try {
                    customer = getCustomer(customerid);
                } catch (CustomerNotFoundException ex) {
                }
                Date date = new Date(set.getLong("TIMESTAMP"));
                String terminal = set.getString("TERMINAL");
                boolean cashed = set.getBoolean("CASHED");
                int sId = set.getInt("STAFF");
                Staff staff = null;
                try {
                    staff = getStaff(sId);
                } catch (StaffNotFoundException ex) {
                    LOG.log(Level.WARNING, null, ex);
                }
                Sale s = new Sale(id, price, customer, date, terminal, cashed, staff);
                s.setProducts(getItemsInSale(s));
                if (!s.isCashed()) {
                    result = result.add(s.getTotal());
                    s.setCashed(true);
                    try {
                        updateSaleNoSem(s);
                    } catch (SaleNotFoundException ex) {
                        LOG.log(Level.WARNING, null, ex);
                    }
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            saleSem.release();
        }
        return result;
    }

    @Override
    public List<Sale> getUncashedSales(String t) throws SQLException {
        String query = "SELECT s.ID as sId, PRICE, s.CUSTOMER as sCus, DISCOUNT, TIMESTAMP, TERMINAL, CASHED, STAFF, CHARGE_ACCOUNT, c.ID as cId, c.NAME as cName, PHONE, MOBILE, EMAIL, ADDRESS-LINE_1, ADDRESS_LINE_2, TOWN, COUNTY, COUNTRY, POSTCODE, NOTES, LOYALTY_POINTS, MONEY_DUE, st.ID as stId, st.NAME as stName, POSITION, USERNAME, PASSWORD FROM SALES s, CUSTOMERS c , STAFF st WHERE c.ID = s.CUSTOMER AND st.ID = s.STAFF AND CASHED = FALSE AND TERMINAL = '" + t + "'";
        Statement stmt = con.createStatement();
        List<Sale> sales = new ArrayList<>();
        try {
            saleSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            sales = getSalesFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            saleSem.release();
        }

        return sales;
    }

    private void addSaleItem(Sale s, SaleItem p) throws SQLException {
        try {
            p.setSale(s);
            String secondQuery = "INSERT INTO SALEITEMS (PRODUCT_ID, TYPE, QUANTITY, PRICE, SALE_ID) VALUES (" + p.getSQLInsertStatement() + ")";
            Statement sstmt = con.createStatement();
            sstmt.executeUpdate(secondQuery);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        }
    }

    private List<SaleItem> getItemsInSale(Sale sale) throws SQLException {
        String query = "SELECT * FROM APP.SALEITEMS WHERE SALEITEMS.SALE_ID = " + sale.getId();
        Statement stmt = con.createStatement();
        List<SaleItem> items;
        ResultSet set = stmt.executeQuery(query);
        items = getSaleItemsFromResultSet(set, sale);
        return items;
    }

    private List<SaleItem> getSaleItemsFromResultSet(ResultSet set, Sale sale) throws SQLException {
        List<SaleItem> sales = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            Item item = null;
            String type = set.getString("TYPE");
            if (type.equals("product")) {
                try {
                    item = getProduct(set.getInt("PRODUCT_ID"));
                } catch (ProductNotFoundException ex) {
                    LOG.log(Level.WARNING, null, ex);
                }
            } else {
                try {
                    item = getDiscount(set.getInt("PRODUCT_ID"));
                } catch (DiscountNotFoundException ex) {
                    LOG.log(Level.WARNING, null, ex);
                }
            }
            int quantity = set.getInt("QUANTITY");
            BigDecimal price = new BigDecimal(Double.toString(set.getDouble("PRICE")));
            SaleItem s = new SaleItem(sale, item, quantity, id, price);
            sales.add(s);
        }
        return sales;
    }

    @Override
    public Sale getSale(int id) throws SQLException, SaleNotFoundException {
        String query = "SELECT s.ID as sId, PRICE, s.CUSTOMER as sCus, DISCOUNT, TIMESTAMP, TERMINAL, CASHED, STAFF, CHARGE_ACCOUNT, c.ID as cId, c.NAME as cName, PHONE, MOBILE, EMAIL, ADDRESS-LINE_1, ADDRESS_LINE_2, TOWN, COUNTY, COUNTRY, POSTCODE, NOTES, LOYALTY_POINTS, MONEY_DUE, st.ID as stId, st.NAME as stName, POSITION, USERNAME, PASSWORD FROM SALES s, CUSTOMERS c , STAFF st WHERE c.ID = s.CUSTOMER AND st.ID = s.STAFF";
        Statement stmt = con.createStatement();
        List<Sale> sales = new ArrayList<>();
        try {
            saleSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            sales = getSalesFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            saleSem.release();
        }

        if (sales.isEmpty()) {
            throw new SaleNotFoundException(id + "");
        }

        return sales.get(0);
    }

    @Override
    public Sale updateSale(Sale s) throws SQLException, SaleNotFoundException {
        String query = s.getSQLUpdateStatement();
        Statement stmt = con.createStatement();
        int value;
        try {
            saleSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new SaleNotFoundException(s.getId() + "");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            saleSem.release();
        }
        return s;
    }

    public Sale updateSaleNoSem(Sale s) throws SQLException, SaleNotFoundException {
        String query = s.getSQLUpdateStatement();
        Statement stmt = con.createStatement();
        int value;
        try {
            value = stmt.executeUpdate(query);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        }
        if (value == 0) {
            throw new SaleNotFoundException(s.getId() + "");
        }
        return s;
    }

    @Override
    public List<Sale> getSalesInRange(Time start, Time end) throws SQLException, IllegalArgumentException {
        if (start.after(end)) {
            throw new IllegalArgumentException("Start date needs to be before end date");
        }
        List<Sale> s = getAllSales();
        List<Sale> sales = new ArrayList<>();

        s.stream().filter((sale) -> (sale.getDate().after(start) && sale.getDate().before(start))).forEachOrdered((sale) -> {
            sales.add(sale);
        });

        return sales;
    }

    @Override
    public String toString() {
        if (connected) {
            return "Connected to database " + this.address + "\nOn user " + this.username;
        } else {
            return "Not connected to database";
        }
    }

    @Override
    public Staff tillLogin(int id) throws IOException, LoginException, SQLException {
        String query = "SELECT * FROM STAFF WHERE STAFF.ID = " + id;
        Statement stmt = con.createStatement();
        List<Staff> staff = new ArrayList<>();
        try {
            staffSem.acquire();
            ResultSet res = stmt.executeQuery(query);
            staff = getStaffFromResultSet(res);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, "There has been an error logging " + id + " into the system", ex);
        } finally {
            staffSem.release();
        }

        if (staff.isEmpty()) {
            throw new LoginException(id + " could not be found");
        }

        Staff s = staff.get(0);

        try {
            loggedInSem.acquire();

            if (loggedIn.contains(s)) {
                loggedInSem.release();
                throw new LoginException("You are already logged in elsewhere");
            }

            loggedIn.add(s);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, "There has been an error loggin " + id + " into the system", ex);
        } finally {
            loggedInSem.release();
        }

        return s;
    }

    @Override
    public void logout(Staff s) throws IOException, StaffNotFoundException {

    }

    @Override
    public void tillLogout(Staff s) throws IOException, StaffNotFoundException {
        try {
            loggedInSem.acquire();

            loggedIn.remove(s);
            return;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, "There has been an error loggin " + s.getId() + " out of the system", ex);
        } finally {
            loggedInSem.release();
        }
        throw new IOException("Error logging out");
    }

    private List<Screen> getScreensFromResultSet(ResultSet set) throws SQLException {
        List<Screen> screens = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            int order = set.getInt("POSITION");
            int color = set.getInt("COLOR");
            Screen s = new Screen(name, order, color, id);

            screens.add(s);
        }

        return screens;
    }

    private List<TillButton> getButtonsFromResultSet(ResultSet set) throws SQLException {
        List<TillButton> buttons = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            Product p = null;
            try {
                p = getProduct(set.getInt("PRODUCT"));
            } catch (ProductNotFoundException ex) {
                LOG.log(Level.WARNING, null, ex);
            }
            Screen s = null;
            try {
                s = getScreen(set.getInt("SCREEN_ID"));
            } catch (ScreenNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            int color = set.getInt("COLOR");
            TillButton b = new TillButton(name, p, s, color, id);

            buttons.add(b);
        }

        return buttons;
    }

    @Override
    public Screen addScreen(Screen s) throws SQLException {
        String query = "INSERT INTO SCREENS (NAME, POSITION, COLOR) VALUES (" + s.getSQLInsertString() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            screensSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                s.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }
        return s;
    }

    @Override
    public TillButton addButton(TillButton b) throws SQLException {
        String query = "INSERT INTO BUTTONS (NAME, PRODUCT, COLOR, SCREEN_ID) VALUES (" + b.getSQLInsertString() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            screensSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                b.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }
        return b;
    }

    @Override
    public void removeScreen(Screen s) throws SQLException, ScreenNotFoundException {
        String query = "DELETE FROM SCREENS WHERE SCREENS.ID = " + s.getId();
        String buttonsQuery = "DELETE FROM BUTTONS WHERE BUTTONS.SCREEN_ID = " + s.getId();
        Statement stmt = con.createStatement();
        int value;
        try {
            screensSem.acquire();
            value = stmt.executeUpdate(query);
            stmt.executeUpdate(buttonsQuery);
            if (value == 0) {
                throw new ScreenNotFoundException("Screen " + s + " could not be found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }
    }

    @Override
    public void removeButton(TillButton b) throws SQLException, ButtonNotFoundException {
        String query = "DELETE FROM BUTTONS WHERE BUTTONS.ID = " + b.getId();
        Statement stmt = con.createStatement();
        int value;
        try {
            screensSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new ButtonNotFoundException("Button " + b + " could not be found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }
    }

    @Override
    public Screen getScreen(int s) throws SQLException, ScreenNotFoundException {
        String query = "SELECT * FROM SCREENS WHERE SCREENS.ID = " + s;
        Statement stmt = con.createStatement();
        List<Screen> screens = new ArrayList<>();
        try {
            screensSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            screens = getScreensFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }
        if (screens.isEmpty()) {
            throw new ScreenNotFoundException("Screen " + s + " could not be found");
        }
        return screens.get(0);
    }

    private Screen getScreenNoSem(int id) throws SQLException, ScreenNotFoundException {
        String query = "SELECT * FROM SCREENS WHERE SCREENS.ID = " + id;
        Statement stmt = con.createStatement();
        List<Screen> screens;
        try {
            ResultSet set = stmt.executeQuery(query);
            screens = getScreensFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
        }
        if (screens.isEmpty()) {
            throw new ScreenNotFoundException("Screen " + id + " could not be found");
        }
        return screens.get(0);
    }

    @Override
    public TillButton getButton(int b) throws SQLException, ButtonNotFoundException {
        String query = "SELECT * FROM SCREENS WHERE BUTTONS.ID = " + b;
        Statement stmt = con.createStatement();
        List<TillButton> buttons = new ArrayList<>();
        try {
            screensSem.acquire();
            ResultSet set = stmt.executeQuery(query);

            buttons = getButtonsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }

        if (buttons.isEmpty()) {
            throw new ButtonNotFoundException("Button " + b + " could not be found");
        }

        return buttons.get(0);
    }

    @Override
    public Screen updateScreen(Screen s) throws SQLException, ScreenNotFoundException {
        String query = s.getSQLUpdateString();
        Statement stmt = con.createStatement();
        int value;
        try {
            screensSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new ScreenNotFoundException("Screen " + s + " could not be found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }
        return s;
    }

    @Override
    public TillButton updateButton(TillButton b) throws SQLException, ButtonNotFoundException {
        String query = b.getSQLUpdateString();
        Statement stmt = con.createStatement();
        int value;
        try {
            screensSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new ButtonNotFoundException("Button " + b + " could not be found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }
        return b;
    }

    @Override
    public List<Screen> getAllScreens() throws SQLException {
        String query = "SELECT * FROM SCREENS";
        Statement stmt = con.createStatement();
        List<Screen> screens = new ArrayList<>();
        try {
            screensSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            screens = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                int order = set.getInt("POSITION");
                int color = set.getInt("COLOR");
                Screen s = new Screen(name, order, color, id);

                screens.add(s);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }

        return screens;
    }

    @Override
    public List<TillButton> getAllButtons() throws SQLException {
        String query = "SELECT * FROM BUTTONS";
        Statement stmt = con.createStatement();
        List<TillButton> buttons = new ArrayList<>();
        try {
            screensSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            buttons = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                Product p = null;
                try {
                    p = getProduct(set.getInt("PRODUCT"));
                } catch (ProductNotFoundException ex) {
                    LOG.log(Level.WARNING, null, ex);
                }
                int color = set.getInt("COLOR");
                Screen s = null;
                try {
                    s = getScreenNoSem(set.getInt("SCREEN_ID"));
                } catch (ScreenNotFoundException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                TillButton b = new TillButton(name, p, s, color, id);

                buttons.add(b);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }

        return buttons;
    }

    @Override
    public List<TillButton> getButtonsOnScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        String query = "SELECT * FROM BUTTONS WHERE BUTTONS.SCREEN_ID=" + s.getId();
        Statement stmt = con.createStatement();
        List<TillButton> buttons = new ArrayList<>();
        try {
            screensSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            buttons = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                Item i = null;
                if (!name.equals("[SPACE]")) {
                    try {
                        i = getProduct(set.getInt("PRODUCT"));
                    } catch (ProductNotFoundException ex) {
                        LOG.log(Level.WARNING, null, ex);
                    }
                }
                int color = set.getInt("COLOR");
                TillButton b = new TillButton(name, i, s, color, id);

                buttons.add(b);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            screensSem.release();
        }

        return buttons;
    }

    @Override
    public void deleteAllScreensAndButtons() throws IOException, SQLException {
        try {
            String buttons = "DROP TABLE BUTTONS";
            String screens = "DROP TABLE SCREENS";
            Statement stmt = con.createStatement();
            stmt.execute(buttons);
            stmt.execute(screens);
            String cscreens = "create table \"APP\".SCREENS\n"
                    + "(\n"
                    + "     ID INT not null primary key\n"
                    + "         GENERATED ALWAYS AS IDENTITY\n"
                    + "         (START WITH 1, INCREMENT BY 1),\n"
                    + "     NAME VARCHAR(50) not null,\n"
                    + "     POSITION INTEGER,\n"
                    + "     COLOR INT\n"
                    + ")";
            String cbuttons = "create table \"APP\".BUTTONS\n"
                    + "(\n"
                    + "     ID INT not null primary key\n"
                    + "         GENERATED ALWAYS AS IDENTITY\n"
                    + "         (START WITH 1, INCREMENT BY 1),\n"
                    + "     NAME VARCHAR(50) not null,\n"
                    + "     PRODUCT INT not null,\n"
                    + "     COLOR INT,\n"
                    + "     SCREEN_ID INT not null references SCREENS(ID)\n"
                    + ")";
            stmt.execute(cscreens);
            LOG.log(Level.INFO, "Created screens table");
            stmt.execute(cbuttons);
            LOG.log(Level.INFO, "Created buttons table");
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        }
    }

    @Override
    public void setGUI(GUIInterface g) {
        this.g = g;
    }

    @Override
    public void suspendSale(Sale sale, Staff staff) throws IOException {
        try {
            suspendSem.acquire();
            suspendedSales.put(staff, sale);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            suspendSem.release();
        }
    }

    @Override
    public Sale resumeSale(Staff s) throws IOException {
        try {
            suspendSem.acquire();
            Sale sale = suspendedSales.get(s);
            suspendedSales.remove(s);
            return sale;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            suspendSem.release();
        }
        throw new IOException("Error retrieveing sale");
    }

    @Override
    public void assisstance(String message) throws IOException {
        g.showMessage("Assisstance", message);
        g.log(message);
    }

    @Override
    public void sendEmail(String email) throws IOException {
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("jtill", "honorsproject");
            }
        };
        Session session = Session.getDefaultInstance(systemSettings.getProperties(), auth);

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(systemSettings.getSetting("OUTGOING_MAIL_ADDRESS")));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(systemSettings.getSetting("MAIL_ADDRESS")));
            message.setSubject("TILL REPORT");
            message.setText(email);
            Transport.send(message);
        } catch (AddressException ex) {
            System.out.println(ex);
        } catch (MessagingException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void emailReceipt(String email, Sale sale) throws IOException, AddressException, MessagingException {
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("jtill", "honorsproject");
            }
        };
        Session session = Session.getDefaultInstance(systemSettings.getProperties(), auth);

        MimeMessage message = new MimeMessage(session);

        String text = "";

        text += "Here is your receipt from your recent purchase\n";
        text += "Sale ID: " + sale.getId() + "\n";
        text += "Time: " + sale.getDate().toString() + "\n";
        text = sale.getSaleItems().stream().map((i) -> i.getItem().getName() + "\t" + i.getQuantity() + "\t" + symbol + i.getPrice() + "\n").reduce(text, String::concat);

        text += "Total: " + symbol + sale.getTotal() + "\n";
        if (sale.isChargeAccount()) {
            text += "You will be invoiced for this sale\n";
        }
        text += "You were served by " + sale.getStaff().getName() + "\n";
        text += "Thank you for your custom";

        message.setFrom(new InternetAddress(systemSettings.getSetting("OUTGOING_MAIL_ADDRESS")));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject("Receipt for sale " + sale.getId());
        message.setText(text);
        Transport.send(message);
    }

    private List<Till> getTillsFromResultSet(ResultSet set) throws SQLException {
        List<Till> tills = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            double d = set.getDouble("UNCASHED");
            BigDecimal uncashed = new BigDecimal(Double.toString(d));

            Till t = new Till(name, uncashed, id);

            tills.add(t);
        }

        return tills;
    }

    @Override
    public Till addTill(Till t) throws IOException, SQLException {
        String query = "INSERT INTO TILLS (NAME, UNCASHED) VALUES (" + t.getSQLInsertString() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            tillSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                t.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            tillSem.release();
        }
        return t;
    }

    @Override
    public void removeTill(int id) throws IOException, SQLException, TillNotFoundException {
        String query = "DELETE FROM TILLS WHERE TILLS.ID = " + id;
        Statement stmt = con.createStatement();
        int value;
        try {
            tillSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new TillNotFoundException(id + " could not be found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            tillSem.release();
        }
    }

    @Override
    public Till getTill(int id) throws IOException, SQLException, TillNotFoundException {
        String query = "SELECT * FROM TILLS WHERE TILLS.ID = " + id;

        Statement stmt = con.createStatement();
        List<Till> tills = new ArrayList<>();
        try {
            tillSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            tills = getTillsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            tillSem.release();
        }

        if (tills.isEmpty()) {
            throw new TillNotFoundException(id + " could not be found");
        }

        return tills.get(0);
    }

    @Override
    public List<Till> getAllTills() throws SQLException {
        String query = "SELECT * FROM TILLS";
        Statement stmt = con.createStatement();
        List<Till> tills = new ArrayList<>();
        try {
            tillSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            tills = getTillsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            tillSem.release();
        }

        return tills;
    }

    @Override
    public Till connectTill(String t) {
        try {
            Till till = this.getTillByName(t);
            g.addTill(till);
            connectedTills.add(till);
            return till;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "There has been an error adding a till to the database", ex);
        } catch (TillNotFoundException ex) {
            boolean result = g.showYesNoMessage("New Till", "Allow till " + t + " to connect?");

            if (result) {
                Till newTill = new Till(t);
                try {
                    addTill(newTill);
                } catch (IOException | SQLException ex1) {
                    LOG.log(Level.SEVERE, "There has been an error connecting a till the server", ex);
                }
                g.addTill(newTill);
                connectedTills.add(newTill);
                return newTill;
            }
        }
        return null;
    }

    @Override
    public void disconnectTill(Till t) {
        connectedTills.remove(t);
    }

    @Override
    public List<Till> getConnectedTills() {
        return connectedTills;
    }

    private Till getTillByName(String t) throws SQLException, TillNotFoundException {
        String query = "SELECT * FROM TILLS WHERE TILLS.NAME = '" + t + "'";

        Statement stmt = con.createStatement();
        List<Till> tills = new ArrayList<>();
        try {
            tillSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            tills = getTillsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            tillSem.release();
        }

        if (tills.isEmpty()) {
            throw new TillNotFoundException(t + " could not be found");
        }

        return tills.get(0);
    }

    @Override
    public void setSetting(String key, String value) {
        systemSettings.setSetting(key, value);
    }

    @Override
    public String getSetting(String key) {
        return systemSettings.getSetting(key);
    }

    private List<Plu> getPlusFromResultSet(ResultSet set) throws SQLException {
        List<Plu> plus = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String code = set.getString("CODE");
            plus.add(new Plu(id, code));
        }
        return plus;
    }

    @Override
    public Plu addPlu(Plu plu) throws IOException, SQLException {
        String query = "INSERT INTO APP.PLUS (CODE) values ('" + plu.getCode() + "')";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            pluSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                plu.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            pluSem.release();
        }

        return plu;
    }

    @Override
    public void removePlu(int id) throws IOException, JTillException, SQLException {
        String query = "DELETE FROM PLUS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        int value = 0;
        try {
            pluSem.acquire();
            stmt.executeUpdate(query);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            pluSem.release();
        }
        if (value == 0) {
            throw new JTillException(id + " could not be found");
        }
    }

    @Override
    public void removePlu(Plu p) throws IOException, JTillException, SQLException {
        removePlu(p.getId());
    }

    @Override
    public Plu getPlu(int id) throws IOException, JTillException, SQLException {
        String query = "SELECT * FROM PLUS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        List<Plu> plus = new ArrayList<>();
        try {
            pluSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            plus = getPlusFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            pluSem.release();
        }

        if (plus.isEmpty()) {
            throw new JTillException(id + " could not be found");
        }

        return plus.get(0);
    }

    @Override
    public Plu getPluByCode(String code) throws IOException, JTillException, SQLException {
        String query = "SELECT * FROM PLUS WHERE CODE='" + code + "'";
        Statement stmt = con.createStatement();
        List<Plu> plus = new ArrayList<>();
        try {
            pluSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            plus = getPlusFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            pluSem.release();
        }

        if (plus.isEmpty()) {
            throw new JTillException("Plu " + code + " not found");
        }

        return plus.get(0);
    }

    @Override
    public List<Plu> getAllPlus() throws IOException, SQLException {
        String query = "SELECT * FROM PLUS";
        Statement stmt = con.createStatement();
        List<Plu> plus = new ArrayList<>();
        try {
            pluSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            plus = getPlusFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            pluSem.release();
        }

        return plus;
    }

    @Override
    public Plu updatePlu(Plu p) throws IOException, JTillException, SQLException {
        String query = "UPDATE PLUS SET CODE='" + p.getCode() + "' WHERE ID=" + p.getId();
        Statement stmt = con.createStatement();
        int value;
        try {
            pluSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new JTillException("Plu " + p.getId() + " not found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            pluSem.release();
        }
        return p;
    }

    @Override
    public boolean isTillLoggedIn(Staff s) throws IOException, StaffNotFoundException, SQLException {
        return loggedIn.contains(s);
    }

    @Override
    public boolean checkUsername(String username) throws IOException, SQLException {
        String query = "SELECT * FROM STAFF WHERE USERNAME='" + username.toLowerCase() + "'";
        Statement stmt = con.createStatement();

        try {
            staffSem.acquire();
            ResultSet set = stmt.executeQuery(query);

            return set.next();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            staffSem.release();
        }
        throw new IOException("Error checking username");
    }

    @Override
    public String getSetting(String key, String value) throws IOException {
        return systemSettings.getSetting(key, value);
    }

    @Override
    public Settings getSettingsInstance() throws IOException {
        return systemSettings;
    }

    @Override
    public GUIInterface getGUI() {
        return this.g;
    }

    private List<WasteReport> getWasteReportsFromResultSet(ResultSet set) throws SQLException {
        List<WasteReport> wrs = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            BigDecimal value = new BigDecimal(Double.toString(set.getDouble("VALUE")));
            Date date = new Date(set.getLong("TIMESTAMP"));
            wrs.add(new WasteReport(id, value, date));
        }
        return wrs;
    }

    @Override
    public WasteReport addWasteReport(WasteReport wr) throws IOException, SQLException, JTillException {
        String query = "INSERT INTO APP.WASTEREPORTS (VALUE, TIMESTAMP) values (" + wr.getTotalValue().doubleValue() + "," + wr.getDate().getTime() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            wasteSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                wr.setId(id);
            }
            for (WasteItem wi : wr.getItems()) {
                addWasteItem(wr, wi);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteSem.release();
        }

        return wr;
    }

    @Override
    public void removeWasteReport(int id) throws IOException, SQLException, JTillException {
        String query = "DELETE FROM WASTEREPORTS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        int value = 0;
        try {
            wasteSem.acquire();
            stmt.executeUpdate(query);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteSem.release();
        }
        if (value == 0) {
            throw new JTillException(id + " could not be found");
        }
    }

    @Override
    public WasteReport getWasteReport(int id) throws IOException, SQLException, JTillException {
        String query = "SELECT * FROM WASTEREPORTS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        List<WasteReport> wrs = new ArrayList<>();
        try {
            wasteSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            wrs = getWasteReportsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteSem.release();
        }

        if (wrs.isEmpty()) {
            throw new JTillException(id + " could not be found");
        }

        WasteReport wr = wrs.get(0);
        wr.setItems(getWasteItemsInReport(id));
        return wr;
    }

    private List<WasteItem> getWasteItemsInReport(int id) throws SQLException {
        String query = "SELECT * FROM WASTEITEMS WHERE REPORT_ID=" + id;
        Statement stmt = con.createStatement();
        List<WasteItem> wis = new ArrayList<>();
        try {
            wasteItemSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            wis = getWasteItemsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteItemSem.release();
        }

        return wis;
    }

    @Override
    public List<WasteReport> getAllWasteReports() throws IOException, SQLException {
        String query = "SELECT * FROM WASTEREPORTS";
        Statement stmt = con.createStatement();
        List<WasteReport> wrs = new ArrayList<>();
        try {
            wasteSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            wrs = getWasteReportsFromResultSet(set);
            for (WasteReport wr : wrs) {
                wr.setItems(getWasteItemsInReport(wr.getId()));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteSem.release();
        }

        return wrs;
    }

    @Override
    public WasteReport updateWasteReport(WasteReport wr) throws IOException, SQLException, JTillException {
        String query = "UPDATE WASTEREPORTS SET VALUE=" + wr.getTotalValue().doubleValue() + ", TIMESTAMP=" + wr.getDate().getTime() + " WHERE ID=" + wr.getId();
        Statement stmt = con.createStatement();
        int value;
        try {
            wasteSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new JTillException("Waste Report " + wr.getId() + " not found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteSem.release();
        }
        return wr;
    }

    private List<WasteItem> getWasteItemsFromResultSet(ResultSet set) throws SQLException {
        List<WasteItem> wis = new ArrayList<>();
        while (set.next()) {
            try {
                int id = set.getInt("ID");
                Product p = this.getProduct(set.getInt("PRODUCT"));
                int quantity = set.getInt("QUANTITY");
                WasteReason wreason = null;
                try {
                    wreason = this.getWasteReason(set.getInt("REASON"));
                } catch (IOException | JTillException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                wis.add(new WasteItem(id, p, quantity, wreason));
            } catch (ProductNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return wis;
    }

    @Override
    public WasteItem addWasteItem(WasteReport wr, WasteItem wi) throws IOException, SQLException, JTillException {
        String query = "INSERT INTO APP.WASTEITEMS (REPORT_ID, PRODUCT, QUANTITY, REASON) values (" + wr.getId() + "," + wi.getProduct().getId() + "," + wi.getQuantity() + "," + wi.getReason().getId() + ")";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            wasteItemSem.acquire();
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                wi.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteItemSem.release();
        }

        return wi;
    }

    @Override
    public void removeWasteItem(int id) throws IOException, SQLException, JTillException {
        String query = "DELETE FROM WASTEITEMS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        int value = 0;
        try {
            wasteItemSem.acquire();
            stmt.executeUpdate(query);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteItemSem.release();
        }
        if (value == 0) {
            throw new JTillException(id + " could not be found");
        }
    }

    @Override
    public WasteItem getWasteItem(int id) throws IOException, SQLException, JTillException {
        String query = "SELECT * FROM WASTEITEMS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        List<WasteItem> wis = new ArrayList<>();
        try {
            wasteItemSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            wis = getWasteItemsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteItemSem.release();
        }

        if (wis.isEmpty()) {
            throw new JTillException(id + " could not be found");
        }

        return wis.get(0);
    }

    @Override
    public List<WasteItem> getAllWasteItems() throws IOException, SQLException {
        String query = "SELECT * FROM WASTEITEMS";
        Statement stmt = con.createStatement();
        List<WasteItem> wis = new ArrayList<>();
        try {
            wasteItemSem.acquire();
            ResultSet set = stmt.executeQuery(query);
            wis = getWasteItemsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteItemSem.release();
        }

        return wis;
    }

    @Override
    public WasteItem updateWasteItem(WasteItem wi) throws IOException, SQLException, JTillException {
        String query = "UPDATE WASTEREPORTS SET PRODUCT=" + wi.getProduct().getId() + ", quantity=" + wi.getQuantity() + ", REASON='" + wi.getReason() + "', WHERE ID=" + wi.getId();
        Statement stmt = con.createStatement();
        int value;
        try {
            wasteItemSem.acquire();
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new JTillException("Waste Report " + wi.getId() + " not found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            wasteItemSem.release();
        }
        return wi;
    }

    private List<WasteReason> getWasteReasonsFromResultSet(ResultSet set) throws SQLException {
        List<WasteReason> wrs = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String reason = set.getString("REASON");
            wrs.add(new WasteReason(id, reason));
        }
        return wrs;
    }

    @Override
    public WasteReason addWasteReason(WasteReason wr) throws IOException, SQLException, JTillException {
        String query = "INSERT INTO APP.WASTEREASONS (REASON) values ('" + wr.getReason() + "')";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        long stamp = wasteReasonLock.writeLock();
        try {
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                wr.setId(id);
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            wasteReasonLock.unlockWrite(stamp);
        }

        return wr;
    }

    @Override
    public void removeWasteReason(int id) throws IOException, SQLException, JTillException {
        String query = "DELETE FROM WATSEREASONS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        int value = 0;
        long stamp = wasteReasonLock.writeLock();
        try {
            stmt.executeUpdate(query);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            wasteReasonLock.unlockWrite(stamp);
        }
        if (value == 0) {
            throw new JTillException(id + " could not be found");
        }
    }

    @Override
    public WasteReason getWasteReason(int id) throws IOException, SQLException, JTillException {
        String query = "SELECT * FROM WASTEREASONS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        List<WasteReason> wrs = new ArrayList<>();
        long stamp = wasteReasonLock.readLock();
        try {
            ResultSet set = stmt.executeQuery(query);
            wrs = getWasteReasonsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            wasteReasonLock.unlockRead(stamp);
        }

        if (wrs.isEmpty()) {
            throw new JTillException(id + " could not be found");
        }

        return wrs.get(0);
    }

    @Override
    public List<WasteReason> getAllWasteReasons() throws IOException, SQLException {
        String query = "SELECT * FROM WASTEREASONS";
        Statement stmt = con.createStatement();
        List<WasteReason> wrs = new ArrayList<>();
        long stamp = wasteReasonLock.readLock();
        try {
            ResultSet set = stmt.executeQuery(query);
            wrs = getWasteReasonsFromResultSet(set);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            wasteReasonLock.unlockRead(stamp);
        }

        return wrs;
    }

    @Override
    public WasteReason updateWasteReason(WasteReason wr) throws IOException, SQLException, JTillException {
        String query = "UPDATE WASTEREASONS SET REASON='" + wr.getReason() + "'";
        Statement stmt = con.createStatement();
        int value;
        long stamp = wasteReasonLock.writeLock();
        try {
            value = stmt.executeUpdate(query);
            if (value == 0) {
                throw new JTillException("Waste Reason " + wr.getId() + " not found");
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            wasteReasonLock.unlockWrite(stamp);
        }
        return wr;
    }

    @Override
    public Supplier addSupplier(Supplier s) throws IOException, SQLException, JTillException {
        String query = "INSERT INTO SUPPLIERS (NAME, ADDRESS, PHONE) VALUES ('" + s.getName() + "','" + s.getAddress() + "','" + s.getContactNumber() + "')";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        long stamp = supplierLock.writeLock();
        try {
            stmt.executeUpdate();
            ResultSet set = stmt.getGeneratedKeys();
            while (set.next()) {
                int id = set.getInt(1);
                s.setId(id);
            }
            con.commit();
            return s;
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            supplierLock.unlockWrite(stamp);
        }
    }

    @Override
    public void removeSupplier(int id) throws IOException, SQLException, JTillException {
        String query = "DELETE FROM SUPPLIERS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        long stamp = supplierLock.writeLock();
        try {
            stmt.executeUpdate(query);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            supplierLock.unlockWrite(stamp);
        }
    }

    @Override
    public Supplier getSupplier(int id) throws IOException, SQLException, JTillException {
        String query = "SELECT * FROM SUPPLIERS WHERE ID=" + id;
        Statement stmt = con.createStatement();
        long stamp = supplierLock.readLock();
        try {
            ResultSet set = stmt.executeQuery(query);
            while (set.next()) {
                String name = set.getString("NAME");
                String addrs = set.getString("ADDRESS");
                String phone = set.getString("PHONE");
                Supplier sup = new Supplier(id, name, addrs, phone);
                return sup;
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            supplierLock.unlockRead(stamp);
        }
        throw new JTillException("Supplier ID " + id + " not found");
    }

    @Override
    public List<Supplier> getAllSuppliers() throws IOException, SQLException {
        String query = "SELECT * FROM SUPPLIERS";
        Statement stmt = con.createStatement();
        long stamp = supplierLock.readLock();
        try {
            ResultSet set = stmt.executeQuery(query);
            List<Supplier> suppliers = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                String addrs = set.getString("ADDRESS");
                String phone = set.getString("PHONE");
                Supplier sup = new Supplier(id, name, addrs, phone);
                suppliers.add(sup);
            }
            return suppliers;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            supplierLock.unlockRead(stamp);
        }
    }

    @Override
    public Supplier updateSupplier(Supplier s) throws IOException, SQLException, JTillException {
        String query = "UPDATE SUPPLIERS SET NAME='" + s.getName() + "' ADDRESS='" + s.getAddress() + "', PHONE='" + s.getContactNumber() + "'";
        Statement stmt = con.createStatement();
        long stamp = supplierLock.writeLock();
        try {
            stmt.executeUpdate(query);
            con.commit();
            return s;
        } catch (SQLException ex) {
            con.rollback();
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            supplierLock.unlockWrite(stamp);
        }
    }
}
