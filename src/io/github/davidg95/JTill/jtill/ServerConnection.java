/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import io.github.davidg95.jconn.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server connection class which handles communication with the server.
 *
 * @author David
 */
public class ServerConnection implements DataConnect, JConnListener {

    private final JConn conn;

    private GUIInterface g;

    private final List<Runnable> waitingToSend;
    private final StampedLock lock;

    private UUID uuid;
    private String name;

    /**
     * Blank constructor.
     */
    public ServerConnection() {
        waitingToSend = new LinkedList<>();
        lock = new StampedLock();
        conn = new JConn();
        init();
    }

    public Properties getSettings() throws IOException {
        try {
            return (Properties) conn.sendData(JConnData.create("PROPERTIES"));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    private void init() {
        conn.registerListener(this);
    }

    public void addToWaiting(Runnable run) {
        final long stamp = lock.writeLock();
        try {
            waitingToSend.add(run);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Method to connect to the server.
     *
     * @param IP the server IP address.
     * @param PORT the server port number.
     * @param name the name of the terminal.
     * @param uuid the name of the terminal.
     * @return this connection till object.
     * @throws IOException if there was an error connecting.
     * @throws java.net.ConnectException if there was an error connecting.
     */
    public Till connect(String IP, int PORT, String name, UUID uuid) throws IOException, ConnectException {
        try {
            conn.connect(IP, PORT, true);
            this.uuid = uuid;
            this.name = name;
            g.showModalMessage("Server", "Waitng for confirmation");
            final Till t = (Till) conn.sendData(JConnData.create("CONNECT").addParam("UUID", uuid).addParam("NAME", name));
            if (t == null) {
                g.disallow();
            } else {
                g.allow(t);
            }
            return t;
        } catch (Throwable ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new IOException("Class error (Update may be required)");
    }

    public void connectAsRemote(String IP, int PORT) throws IOException {
        conn.connect(IP, PORT, true);
    }

    /**
     * Makes a connection but doesn't wait for server confirmation
     *
     * @param IP the server IP address.
     * @param PORT the server port number.
     * @throws IOException if there was an error connecting.
     * @throws java.net.ConnectException if there was an error connecting.
     */
    public void connectNoPermission(String IP, int PORT) throws IOException, ConnectException {
        try {
            conn.sendData(JConnData.create("NOPERM"));
        } catch (Throwable ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void suspendSale(Sale sale, Staff staff) throws IOException {
        try {
            conn.sendData(JConnData.create("SUSPENDSALE").addParam("SALE", sale).addParam("STAFF", staff));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public Sale resumeSale(Staff s) throws IOException {
        try {
            return (Sale) conn.sendData(JConnData.create("RESUMESALE").addParam("STAFF", s));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public void assisstance(String message) throws IOException {
        try {
            conn.sendData(JConnData.create("ASSISSTANCE").addParam("MESSAGE", message));
        } catch (Throwable ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BigDecimal getTillTakings(int terminal) throws IOException, SQLException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("TAKINGS").addParam("TERMINAL", terminal));
        } catch (Throwable ex) {
            throw new SQLException(ex.getMessage());
        }
    }

    @Override
    public void sendEmail(String message) throws IOException {
        try {
            conn.sendData(JConnData.create("SENDEMAIL").addParam("MESSAGE", message));
        } catch (Throwable ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean emailReceipt(String email, Sale sale) throws IOException {
        try {
            return (boolean) conn.sendData(JConnData.create("SENDRECEIPT").addParam("EMAIL", email).addParam("SALE", sale));
        } catch (Throwable ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Till addTill(Till t) throws IOException, SQLException {
        try {
            return (Till) conn.sendData(JConnData.create("ADDTILL").addParam("TILL", t));
        } catch (Throwable ex) {
            throw new SQLException(ex.getMessage());
        }
    }

    @Override
    public void removeTill(int id) throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVETILL").addParam("ID", id));
        } catch (Throwable ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Till getTill(int id) throws IOException, SQLException, JTillException {
        try {
            return (Till) conn.sendData(JConnData.create("GETTILL").addParam("TERMINAL", id));
        } catch (Throwable ex) {
            throw new JTillException(ex.getMessage());
        }
    }

    @Override
    public List<Till> getAllTills() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLTILLS"));
        } catch (Throwable ex) {
            throw new SQLException(ex.getMessage());
        }
    }

    @Override
    public Till connectTill(String name, UUID uuid) throws IOException {
        try {
            return (Till) conn.sendData(JConnData.create("CONNECTTILL").addParam("NAME", name).addParam("UUID", uuid));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public List<Till> getConnectedTills() throws IOException {
        try {
            return (List) conn.sendData(JConnData.create("GETCONNECTEDTILLS"));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public List<Sale> getUncashedSales(String t) throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("UNCASHEDSALES").addParam("NAME", t));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public void setSetting(String key, String value) throws IOException {
        try {
            conn.sendData(JConnData.create("SETSETTING").addParam("KEY", key).addParam("VALUE", value));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public String getSetting(String key) throws IOException {
        try {
            return (String) conn.sendData(JConnData.create("GETSETTING").addParam("KEY", key));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Method to add a new product to the server.
     *
     * @param p the product to add.
     * @return the Product that was added.
     * @throws IOException if there was an error connecting.
     * @throws SQLException if there was a database error.
     */
    @Override
    public Product addProduct(Product p) throws IOException, SQLException {
        try {
            return (Product) conn.sendData(JConnData.create("NEWPRODUCT").addParam("PRODUCT", p));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Method to remove a product from the server.
     *
     * @param code the code of the product to remove.
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public void removeProduct(int code) throws IOException, ProductNotFoundException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVEPRODUCT").addParam("CODE", code));
        } catch (Throwable ex) {
            if (ex instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeProduct(Product p) throws IOException, ProductNotFoundException, SQLException {
        removeProduct(p.getId());
    }

    /**
     * Method to purchase a product on the server and reduce is stock level by
     * one.
     *
     * @param id the product to purchase.
     * @param amount the amount of the product to purchase.
     * @return the new stock level;
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     * @throws OutOfStockException if the product is out of stock.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public int purchaseProduct(int id, int amount) throws IOException, ProductNotFoundException, OutOfStockException, SQLException {
        try {
            return (int) conn.sendData(JConnData.create("PURCHASE").addParam("PRODUCT", id).addParam("AMOUNT", amount));
        } catch (Throwable ex) {
            if (ex instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) ex;
            } else if (ex instanceof OutOfStockException) {
                throw (OutOfStockException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to get a product from the server based on its product code.
     *
     * @param code the code to search for.
     * @return Product object that matches the code.
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public Product getProduct(int code) throws IOException, ProductNotFoundException, SQLException {
        try {
            return (Product) conn.sendData(JConnData.create("GETPRODUCT").addParam("CODE", code));
        } catch (Throwable ex) {
            if (ex instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to get a product by barcode.
     *
     * @param barcode the barcode to search for.
     * @return Product object that matches the barcode.
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the barcode was not found.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public Product getProductByBarcode(String barcode) throws IOException, ProductNotFoundException, SQLException {
        try {
            return (Product) conn.sendData(JConnData.create("GETPRODUCTBARCODE").addParam("BARCODE", barcode));
        } catch (Throwable ex) {
            if (ex instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Product updateProduct(Product p) throws IOException, SQLException, ProductNotFoundException {
        try {
            return (Product) conn.sendData(JConnData.create("UPDATEPRODUCT").addParam("PRODUCT", p));
        } catch (Throwable ex) {
            if (ex instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public boolean checkBarcode(String barcode) throws IOException, SQLException {
        try {
            return (boolean) conn.sendData(JConnData.create("CHECKBARCODE").addParam("BARCODE", barcode));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to get a List of all products on the server.
     *
     * @return List of type product.
     * @throws IOException if there was an error connecting.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public List<Product> getAllProducts() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLPRODUCTS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Product> productLookup(String terms) throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("PRODUCTLOOKUP").addParam("TERMS", terms));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to add a new customer to the server.
     *
     * @param customer the new customer to add.
     * @return the Customer that was added.
     * @throws IOException if there was an error connecting.
     * @throws SQLException if there was a database error.
     */
    @Override
    public Customer addCustomer(Customer customer) throws IOException, SQLException {
        try {
            return (Customer) conn.sendData(JConnData.create("ADDCUSTOMER").addParam("CUSTOMER", customer));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to remove a customer from the server.
     *
     * @param id the id of the customer to remove.
     * @throws IOException if there was an error connecting.
     * @throws CustomerNotFoundException if the customer could not be found.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public void removeCustomer(int id) throws IOException, CustomerNotFoundException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVECUSTOMER").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof CustomerNotFoundException) {
                throw (CustomerNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeCustomer(Customer c) throws IOException, SQLException, CustomerNotFoundException {
        removeCustomer(c.getId());
    }

    /**
     * Method to get a customer from the server.
     *
     * @param id the id of the customer to get.
     * @return Customer object that matches the id.
     * @throws IOException if there was an error connecting.
     * @throws CustomerNotFoundException if the customer could not be found.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public Customer getCustomer(int id) throws IOException, CustomerNotFoundException, SQLException {
        try {
            return (Customer) conn.sendData(JConnData.create("GETCUSTOMER").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof CustomerNotFoundException) {
                throw (CustomerNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Customer> getCustomerByName(String name) throws IOException, CustomerNotFoundException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETCUSTOMERBYNAME").addParam("NAME", name));
        } catch (Throwable ex) {
            if (ex instanceof CustomerNotFoundException) {
                throw (CustomerNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to get a List of all customers on the server.
     *
     * @return List of type Customer.
     * @throws IOException if there was an error connecting.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public List<Customer> getAllCustomers() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLCUSTOMERS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to update a customer.
     *
     * @param c the customer not update.
     * @return the customer object that was updated.
     * @throws IOException if there was a server error.
     * @throws SQLException if there was a database error.
     * @throws CustomerNotFoundException if the customer does not already exist
     * in the database.
     */
    @Override
    public Customer updateCustomer(Customer c) throws IOException, SQLException, CustomerNotFoundException {
        try {
            return (Customer) conn.sendData(JConnData.create("UPDATECUSTOMER").addParam("CUSTOMER", c));
        } catch (Throwable ex) {
            if (ex instanceof CustomerNotFoundException) {
                throw (CustomerNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Search the customers which match the search terms.
     *
     * @param terms the terms to search for.
     * @return a list of customers which match the terms.
     * @throws IOException if there was a server error.
     * @throws SQLException if there was a database error.
     */
    @Override
    public List<Customer> customerLookup(String terms) throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("CUSTOMERLOOKUP").addParam("TERMS", terms));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to send a sale to the server.
     *
     * @param s the sale to send.
     * @return the Sale that was added.
     * @throws IOException if there was an error connecting.
     * @throws SQLException if there was a database error.
     */
    @Override
    public Sale addSale(Sale s) throws IOException, SQLException {
        try {
            return (Sale) conn.sendData(JConnData.create("ADDSALE").addParam("SALE", s));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    public void sendSales(List<Sale> sales) throws Throwable {
        conn.sendData(JConnData.create("SENDSALES").addParam("SALES", sales));
    }

    @Override
    public List<Sale> getAllSales() throws IOException, SQLException {
        try {
            return (List<Sale>) conn.sendData(JConnData.create("GETALLSALES"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Sale getSale(int id) throws IOException, SQLException, JTillException {
        try {
            return (Sale) conn.sendData(JConnData.create("GETSALE").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Sale updateSale(Sale s) throws IOException, JTillException, SQLException {
        try {
            return (Sale) conn.sendData(JConnData.create("UPDATESALE").addParam("SALE", s));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Sale> getSalesInRange(Time start, Time end) throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETSALEDATERANGE").addParam("START", start).addParam("END", end));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to add a new member of staff to the system.
     *
     * @param s the new member of staff to add.
     * @return the Staff that is being added.
     * @throws IOException if there was a server communication error.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public Staff addStaff(Staff s) throws IOException, SQLException {
        try {
            return (Staff) conn.sendData(JConnData.create("ADDSTAFF").addParam("STAFF", s));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to remove a member of staff from the system.
     *
     * @param id the id of the staff to remove.
     * @throws IOException if there was a server communication error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public void removeStaff(int id) throws IOException, StaffNotFoundException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVESTAFF").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeStaff(Staff s) throws IOException, SQLException, StaffNotFoundException {
        s.setPassword(Encryptor.encrypt(s.getPassword()));
        removeStaff(s.getId());
    }

    /**
     * Method to get a member of staff.
     *
     * @param id the id of the member of staff to find.
     * @return member of staff.
     * @throws IOException if there was a server communication error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public Staff getStaff(int id) throws IOException, StaffNotFoundException, SQLException {
        try {
            return (Staff) conn.sendData(JConnData.create("GETSTAFF").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to get a List of all the staff on the system.
     *
     * @return List of type staff.
     * @throws IOException if there was a server communication error.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public List<Staff> getAllStaff() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLSTAFF"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Staff updateStaff(Staff s) throws IOException, SQLException, StaffNotFoundException {
        try {
            return (Staff) conn.sendData(JConnData.create("UPDATESTAFF").addParam("STAFF", s));
        } catch (Throwable ex) {
            if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public int getStaffCount() throws IOException, SQLException {
        try {
            return (int) conn.sendData(JConnData.create("GETSTAFFCOUNT"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to log a member of staff onto the system.
     *
     * @param username the username of the staff to login in.
     * @param password the password of the staff to log in.
     * @return the member of staff that has logged in.
     * @throws IOException if there was a server communication error.
     * @throws LoginException if there was an error logging in.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public Staff login(String username, String password) throws IOException, LoginException, SQLException {
        try {
            return (Staff) conn.sendData(JConnData.create("LOGIN").addParam("USERNAME", username).addParam("PASSWORD", Encryptor.encrypt(password)));
        } catch (Throwable ex) {
            if (ex instanceof LoginException) {
                throw (LoginException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to log a member of staff into a till system.
     *
     * @param id the id to log in.
     * @return the member of staff that has logged in.
     * @throws IOException if there was a server communication error.
     * @throws LoginException if there was an error logging in.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public Staff tillLogin(int id) throws IOException, LoginException, SQLException {
        try {
            return (Staff) conn.sendData(JConnData.create("TILLLOGIN").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof LoginException) {
                throw (LoginException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to log a member of staff out the system.
     *
     * @param s the staff to log out.
     * @throws IOException if there was a server communication error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    @Override
    public void logout(Staff s) throws IOException, StaffNotFoundException {
        try {
            conn.sendData(JConnData.create("LOGOUT").addParam("STAFF", s));
        } catch (Throwable ex) {
            if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to log a member of staff out a till.
     *
     * @param s the staff to log out.
     * @throws IOException if there was a server communication error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    @Override
    public void tillLogout(Staff s) throws IOException, StaffNotFoundException {
        conn.sendData(JConnData.create("TILLLOGOUT").addParam("STAFF", s), (JConnData reply) -> {
        });
    }

    @Override
    public Category addCategory(Category c) throws IOException, SQLException {
        try {
            return (Category) conn.sendData(JConnData.create("ADDCATEGORY").addParam("CATEGORY", c));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Category updateCategory(Category c) throws IOException, SQLException, JTillException {
        try {
            return (Category) conn.sendData(JConnData.create("UPDATECATEGORY").addParam("CATEGORY", c));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeCategory(Category c) throws IOException, JTillException, SQLException {
        removeCategory(c.getId());
    }

    @Override
    public void removeCategory(int id) throws IOException, JTillException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVECATEGORY").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Category getCategory(int id) throws IOException, SQLException, JTillException {
        try {
            return (Category) conn.sendData(JConnData.create("GETCATEGORY").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Category> getAllCategorys() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLCATEGORYS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Product> getProductsInCategory(int id) throws IOException, SQLException, JTillException {
        try {
            return (List) conn.sendData(JConnData.create("GETPRODUCTSINCATEGORY").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Discount addDiscount(Discount d) throws IOException, SQLException {
        try {
            return (Discount) conn.sendData(JConnData.create("ADDDISCOUNT").addParam("DISCOUNT", d));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Discount updateDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException {
        try {
            return (Discount) conn.sendData(JConnData.create("UPDATEDISCOUNT").addParam("DISCOUNT", d));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof DiscountNotFoundException) {
                throw (DiscountNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeDiscount(Discount d) throws IOException, DiscountNotFoundException, SQLException {
        removeDiscount(d.getId());
    }

    @Override
    public void removeDiscount(int id) throws IOException, DiscountNotFoundException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVEDISCOUNT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof DiscountNotFoundException) {
                throw (DiscountNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Discount getDiscount(int id) throws IOException, SQLException, DiscountNotFoundException {
        try {
            return (Discount) conn.sendData(JConnData.create("GETDISCOUNT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof DiscountNotFoundException) {
                throw (DiscountNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Discount> getAllDiscounts() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLDISCOUNTS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Tax addTax(Tax t) throws IOException, SQLException {
        try {
            return (Tax) conn.sendData(JConnData.create("ADDTAX").addParam("TAX", t));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeTax(Tax t) throws IOException, JTillException, SQLException {
        removeTax(t.getId());
    }

    @Override
    public void removeTax(int id) throws IOException, JTillException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVETAX").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Tax getTax(int id) throws IOException, SQLException, JTillException {
        try {
            return (Tax) conn.sendData(JConnData.create("GETTAX").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Tax updateTax(Tax t) throws IOException, SQLException, JTillException {
        try {
            return (Tax) conn.sendData(JConnData.create("UPDATETAX").addParam("TAX", t));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Tax> getAllTax() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLTAX"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Product> getProductsInTax(int id) throws IOException, SQLException, JTillException {
        try {
            return (List) conn.sendData(JConnData.create("GETPRODUCTSINTAX").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Method to close the connection to the server.
     *
     * @throws IOException if there is a network error.
     */
    @Override
    public void close() throws IOException {
        conn.endConnection();
    }

    @Override
    public String toString() {
        return conn.toString();
    }

    @Override
    public Screen addScreen(Screen s) throws IOException, SQLException {
        try {
            return (Screen) conn.sendData(JConnData.create("ADDSCREEN").addParam("SCREEN", s));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public TillButton addButton(TillButton b) throws IOException, SQLException {
        try {
            return (TillButton) conn.sendData(JConnData.create("ADDBUTTON").addParam("BUTTON", b));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            conn.sendData(JConnData.create("REMOVESCREEN").addParam("SCREEN", s));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof ScreenNotFoundException) {
                throw (ScreenNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeButton(TillButton b) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVEBUTTON").addParam("BUTTON", b));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Screen getScreen(int s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            return (Screen) conn.sendData(JConnData.create("GETSCREEN").addParam("ID", s));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof ScreenNotFoundException) {
                throw (ScreenNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public TillButton getButton(int b) throws IOException, SQLException, JTillException {
        try {
            return (TillButton) conn.sendData(JConnData.create("GETBUTTON").addParam("ID", b));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Screen updateScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            return (Screen) conn.sendData(JConnData.create("UPDATESCREEN").addParam("SCREEN", s));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof ScreenNotFoundException) {
                throw (ScreenNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public TillButton updateButton(TillButton b) throws IOException, SQLException, JTillException {
        try {
            return (TillButton) conn.sendData(JConnData.create("UPDATEBUTTON").addParam("BUTTON", b));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Screen> getAllScreens() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLSCREENS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<TillButton> getAllButtons() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLBUTTONS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<TillButton> getButtonsOnScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            return (List) conn.sendData(JConnData.create("GETBUTTONSONSCREEN").addParam("SCREEN", s));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof ScreenNotFoundException) {
                throw (ScreenNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void deleteAllScreensAndButtons() throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("DELETEALLSCREENSANDBUTTONS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void setGUI(GUIInterface g) {
        this.g = g;
    }

    @Override
    public Plu addPlu(Plu plu) throws IOException, SQLException {
        try {
            return (Plu) conn.sendData(JConnData.create("ADDPLU").addParam("PLU", plu));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removePlu(int id) throws IOException, JTillException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVEPLU").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removePlu(Plu p) throws IOException, JTillException, SQLException {
        removePlu(p.getId());
    }

    @Override
    public Plu getPlu(int id) throws IOException, JTillException, SQLException {
        try {
            return (Plu) conn.sendData(JConnData.create("GETPLU").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Plu getPluByCode(String code) throws IOException, JTillException, SQLException {
        try {
            return (Plu) conn.sendData(JConnData.create("GETPLUBYCODE").addParam("CODE", code));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Plu> getAllPlus() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLPLUS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Plu updatePlu(Plu p) throws IOException, JTillException, SQLException {
        try {
            return (Plu) conn.sendData(JConnData.create("UPDATEPLU").addParam("PLU", p));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public boolean isTillLoggedIn(Staff s) throws IOException, StaffNotFoundException, SQLException {
        try {
            return (boolean) conn.sendData(JConnData.create("ISTILLLOGGEDON").addParam("STAFF", s));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public boolean checkUsername(String username) throws IOException, SQLException {
        try {
            return (boolean) conn.sendData(JConnData.create("CHECKUSERNAME").addParam("USERNAME", username));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public String getSetting(String key, String value) throws IOException {
        try {
            return (String) conn.sendData(JConnData.create("GETSETTING").addParam("KEY", key).addParam("VALUE", value));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public GUIInterface getGUI() {
        return this.g;
    }

    @Override
    public WasteReport addWasteReport(WasteReport wr) throws IOException, SQLException, JTillException {
        try {
            return (WasteReport) conn.sendData(JConnData.create("ADDWASTEREPORT").addParam("WASTE", wr));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeWasteReport(int id) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVEWASTEREPORT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public WasteReport getWasteReport(int id) throws IOException, SQLException, JTillException {
        try {
            return (WasteReport) conn.sendData(JConnData.create("GETWASTEREPORT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<WasteReport> getAllWasteReports() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLWASTEREPORTS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public WasteReport updateWasteReport(WasteReport wr) throws IOException, SQLException, JTillException {
        try {
            return (WasteReport) conn.sendData(JConnData.create("UPDATEWASTEREPORT").addParam("WASTE", wr));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public WasteItem addWasteItem(WasteReport wr, WasteItem wi) throws IOException, SQLException, JTillException {
        try {
            return (WasteItem) conn.sendData(JConnData.create("ADDWASTEITEM").addParam("WASTE", wr).addParam("ITEM", wi));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeWasteItem(int id) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVEWASTEITEM").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public WasteItem getWasteItem(int id) throws IOException, SQLException, JTillException {
        try {
            return (WasteItem) conn.sendData(JConnData.create("GETWASTEITEM").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<WasteItem> getAllWasteItems() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLWASTEITEMS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public WasteItem updateWasteItem(WasteItem wi) throws IOException, SQLException, JTillException {
        try {
            return (WasteItem) conn.sendData(JConnData.create("UPDATEWASTEITEM").addParam("WASTE", wi));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public WasteReason addWasteReason(WasteReason wr) throws IOException, SQLException, JTillException {
        try {
            return (WasteReason) conn.sendData(JConnData.create("ADDWASTEREASON").addParam("WASTE", wr));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeWasteReason(int id) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVEWASTEREASON").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public WasteReason getWasteReason(int id) throws IOException, SQLException, JTillException {
        try {
            return (WasteReason) conn.sendData(JConnData.create("GETWASTEREASON").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<WasteReason> getAllWasteReasons() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLWASTEREASON"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public WasteReason updateWasteReason(WasteReason wr) throws IOException, SQLException, JTillException {
        try {
            return (WasteReason) conn.sendData(JConnData.create("UPDATEWASTEREASON"));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Supplier addSupplier(Supplier s) throws IOException, SQLException, JTillException {
        try {
            return (Supplier) conn.sendData(JConnData.create("ADDSUPPLIER"));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeSupplier(int id) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVESUPPLIER"));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Supplier getSupplier(int id) throws IOException, SQLException, JTillException {
        try {
            return (Supplier) conn.sendData(JConnData.create("GETSUPPLIER").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Supplier> getAllSuppliers() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLSUPPLIERS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Supplier updateSupplier(Supplier s) throws IOException, SQLException, JTillException {
        try {
            return (Supplier) conn.sendData(JConnData.create("UPDATESUPPLIER").addParam("SUPPLIER", s));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Department addDepartment(Department d) throws IOException, SQLException {
        try {
            return (Department) conn.sendData(JConnData.create("ADDDEPARTMENT").addParam("DEPARTMENT", d));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeDepartment(int id) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVEDEPARTMENT"));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Department getDepartment(int id) throws IOException, SQLException, JTillException {
        try {
            return (Department) conn.sendData(JConnData.create("GETDEPARTMENT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Department> getAllDepartments() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLDEPARTMENTS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Department updateDepartment(Department d) throws IOException, SQLException, JTillException {
        try {
            return (Department) conn.sendData(JConnData.create("UPDATEDEPARTMENT").addParam("DEPARTMENT", d));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public SaleItem addSaleItem(Sale s, SaleItem i) throws IOException, SQLException {
        try {
            return (SaleItem) conn.sendData(JConnData.create("ADDSALEITEM").addParam("SALE", s).addParam("ITEM", i));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeSaleItem(int id) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVESALEITEMS").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public SaleItem getSaleItem(int id) throws IOException, SQLException, JTillException {
        try {
            return (SaleItem) conn.sendData(JConnData.create("GETSALEITEM").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<SaleItem> getAllSaleItems() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLSALEITEMS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<SaleItem> submitSaleItemQuery(String q) throws IOException {
        try {
            return (List) conn.sendData(JConnData.create("SUBMITSALEITEMQUERY").addParam("QUERY", q));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public SaleItem updateSaleItem(SaleItem i) throws IOException, SQLException, JTillException {
        try {
            return (SaleItem) conn.sendData(JConnData.create("UPDATESALEITEM").addParam("ITEM", i));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public int getTotalSoldOfItem(int id) throws IOException, SQLException, ProductNotFoundException {
        try {
            return (int) conn.sendData(JConnData.create("GETTOTALSOLDITEM").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getTotalValueSold(int id) throws IOException, SQLException, ProductNotFoundException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("GETVALUESOLDITEM").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public int getTotalWastedOfItem(int id) throws IOException, SQLException, ProductNotFoundException {
        try {
            return (int) conn.sendData(JConnData.create("GETTOTALWASTEDITEM").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getValueWastedOfItem(int id) throws IOException, SQLException, ProductNotFoundException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("GETVALUEWASTEDITEM").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void addReceivedItem(ReceivedItem i, int report) throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("ADDRECEIVEDITEM").addParam("ITEM", i).addParam("REP", report));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getValueSpentOnItem(int id) throws IOException, SQLException, ProductNotFoundException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("GETSPENTONITEM").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<SaleItem> getSaleItemsSearchTerms(int depId, int catId, Date start, Date end) throws IOException, SQLException {
        return null;
    }

    @Override
    public void clockOn(int id) throws IOException, SQLException, StaffNotFoundException {
        try {
            conn.sendData(JConnData.create("CLOCKON").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void clockOff(int id) throws IOException, SQLException, StaffNotFoundException {
        try {
            conn.sendData(JConnData.create("CLOCKOFF").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<ClockItem> getAllClocks(int id) throws IOException, SQLException, StaffNotFoundException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLCLOCKS").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void clearClocks(int id) throws IOException, SQLException, StaffNotFoundException {
        try {
            conn.sendData(JConnData.create("CLEARCLOCKS").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Trigger addTrigger(Trigger t) throws IOException, SQLException {
        try {
            return (Trigger) conn.sendData(JConnData.create("ADDTRIGGER").addParam("TRIGGER", t));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<DiscountBucket> getDiscountBuckets(int id) throws IOException, SQLException, DiscountNotFoundException {
        try {
            return (List<DiscountBucket>) conn.sendData(JConnData.create("GETDISCOUNTBUCKETS").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof DiscountNotFoundException) {
                throw (DiscountNotFoundException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeTrigger(int id) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVETRIGGER").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Discount> getValidDiscounts() throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETVALIDDISCOUNTS"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public DiscountBucket addBucket(DiscountBucket b) throws IOException, SQLException {
        try {
            return (DiscountBucket) conn.sendData(JConnData.create("ADDBUCKET").addParam("BUCKET", b));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeBucket(int id) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVEBUCKET").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Trigger> getBucketTriggers(int id) throws IOException, SQLException, JTillException {
        try {
            return (List) conn.sendData(JConnData.create("GETBUCKETTRIGGERS").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Trigger updateTrigger(Trigger t) throws IOException, SQLException, JTillException {
        try {
            return (Trigger) conn.sendData(JConnData.create("UPDATETRIGGER").addParam("TRIGGER", t));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public DiscountBucket updateBucket(DiscountBucket b) throws IOException, SQLException, JTillException {
        try {
            return (DiscountBucket) conn.sendData(JConnData.create("UPDATEBUCKET").addParam("BUCKET", b));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Sale> getUncachedTillSales(int id) throws IOException, JTillException {
        try {
            return (List) conn.sendData(JConnData.create("GETUNCASHEDTILLSALES").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Product addProductAndPlu(Product p, Plu pl) throws IOException, SQLException {
        try {
            return (Product) conn.sendData(JConnData.create("ADDPRODUCTANDPLU").addParam("PRODUCT", p).addParam("PLU", pl));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Plu getPluByProduct(int id) throws IOException, JTillException {
        try {
            return (Plu) conn.sendData(JConnData.create("GETPLUBYPRODUCT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<SaleItem> searchSaleItems(int department, int category, Date start, Date end) throws IOException, SQLException, JTillException {
        try {
            return (List) conn.sendData(JConnData.create("SEARCHSALEITEMS").addParam("DEP", department).addParam("CAT", category).addParam("START", start).addParam("END", end));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Sale> getTerminalSales(int terminal, boolean uncashedOnly) throws IOException, SQLException, JTillException {
        try {
            return (List) conn.sendData(JConnData.create("GETTERMINALSALES").addParam("TERMINAL", terminal).addParam("UNCASHEDFLAG", uncashedOnly));
        } catch (Throwable ex) {
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void cashUncashedSales(int terminal) throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("CASHUNCASHEDSALES").addParam("TERMINAL", terminal));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Product> getProductsAdvanced(String WHERE) throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETPRODUCTSADVANCED").addParam("WHERE", WHERE));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Sale> getStaffSales(Staff s) throws IOException, StaffNotFoundException {
        try {
            return (List) conn.sendData(JConnData.create("GETSTAFFSALES").addParam("STAFF", s));
        } catch (Throwable ex) {
            if (ex instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public HashMap integrityCheck() throws IOException, SQLException {
        try {
            return (HashMap) conn.sendData(JConnData.create("INTEGRITYCHECK"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void onReceive(JConnData data) {
        if (data.getFlag().equals("LOG")) {
            g.log(data.getParam("MESSAGE"));
        } else if (data.getFlag().equals("REINIT")) {
            new Thread() {
                @Override
                public void run() {
                    g.initTill();
                }
            }.start(); //Search the queue for the reqeust source.
        } else if (data.getFlag().equals("RENAME")) {
            final String name = (String) data.getParam("NAME");
            g.renameTill(name);
        } else if (data.getFlag().equals("LOGOUT")) {
            g.logout();
        }
    }

    @Override
    public void onConnectionDrop(JConnEvent event) {
        g.connectionDrop();
    }

    @Override
    public void onConnectionEstablish(JConnEvent event) {
        g.connectionReestablish();
        try {
            conn.sendData(JConnData.create("RECONNECT").addParam("UUID", uuid).addParam("SITE", name));
        } catch (Throwable ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        final long stamp = lock.readLock();
        try {
            waitingToSend.forEach((run) -> {
                try {
                    run.run();
                } catch (Throwable ex) {

                }
            });
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public void onServerGracefulEnd() {
        g.connectionDrop();
    }

    @Override
    public Object[] databaseInfo() throws IOException, SQLException {
        try {
            return (Object[]) conn.sendData(JConnData.create("DATABASEINFO"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Till updateTill(Till t) throws IOException, SQLException, JTillException {
        try {
            return (Till) conn.sendData(JConnData.create("UPDATETILL").addParam("TILL", t));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new JTillException(ex.getMessage());
            }
        }
    }

    @Override
    public File getLoginBackground() throws IOException {
        try {
            return (File) conn.sendData(JConnData.create("GETBGIMAGE"));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public void reinitialiseAllTills() throws IOException {
        try {
            conn.sendData(JConnData.create("REINITTILLS"));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public int clearSalesData() throws IOException, SQLException {
        try {
            return (int) conn.sendData(JConnData.create("CLEARSALES"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public void addReceivedReport(ReceivedReport rep) throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("ADDRECREP").addParam("RECREP", rep));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public List<ReceivedReport> getAllReceivedReports() throws IOException, SQLException {
        try {
            return (List<ReceivedReport>) conn.sendData(JConnData.create("GETRECREP"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public ReceivedReport updateReceivedReport(ReceivedReport rr) throws IOException, SQLException {
        try {
            return (ReceivedReport) conn.sendData(JConnData.create("UPDATERECREP").addParam("RECREP", rr));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }
}
