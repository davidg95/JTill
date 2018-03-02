/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import io.github.davidg95.jconn.*;
import io.github.davidg95.jconn.events.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server connection class which handles communication with the server.
 *
 * @author David
 */
public class ServerConnection extends DataConnect implements JConnListener {

    private static final ServerConnection CONNECTION;

    private final JConn conn;

    private UUID uuid;
    private String name;

    static {
        CONNECTION = new ServerConnection();
    }

    /**
     * Blank constructor.
     */
    public ServerConnection() {
        conn = new JConn();
        init();
    }

    public static ServerConnection getInstance() {
        return CONNECTION;
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

    public boolean isConnected() {
        return conn.isUp();
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
     * @throws io.github.davidg95.JTill.jtill.JTillException if there is an
     * error connecting the till.
     */
    public Till connect(String IP, int PORT, String name, UUID uuid) throws IOException, ConnectException, JTillException {
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
            if (ex instanceof JTillException) {
                throw (JTillException) ex;
            }
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
    public Till connectTill(String name, UUID uuid, Staff staff) throws IOException {
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
     * @param barcode the code of the product to remove.
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public void removeProduct(String barcode) throws IOException, ProductNotFoundException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVEPRODUCT").addParam("CODE", barcode));
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
        removeProduct(p.getBarcode());
    }

    /**
     * Method to purchase a product on the server and reduce is stock level by
     * one.
     *
     * @param barcode the product to purchase.
     * @param amount the amount of the product to purchase.
     * @return the new stock level;
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     * @throws OutOfStockException if the product is out of stock.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public int purchaseProduct(String barcode, int amount) throws IOException, ProductNotFoundException, OutOfStockException, SQLException {
        try {
            return (int) conn.sendData(JConnData.create("PURCHASE").addParam("PRODUCT", barcode).addParam("AMOUNT", amount));
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
     * @param barcode the code to search for.
     * @return Product object that matches the code.
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public Product getProduct(String barcode) throws IOException, ProductNotFoundException, SQLException {
        try {
            return (Product) conn.sendData(JConnData.create("GETPRODUCT").addParam("CODE", barcode));
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
    public List<Sale> getSalesInRange(Date start, Date end) throws IOException, SQLException {
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
        s.setPassword(Encryptor.encrypt(s.getPassword()));
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
    public void addWasteReport(List<WasteItem> items) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("ADDWASTEREPORT").addParam("WASTE", items));
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
    public WasteItem addWasteItem(WasteItem wi) throws IOException, SQLException, JTillException {
        try {
            return (WasteItem) conn.sendData(JConnData.create("ADDWASTEITEM").addParam("ITEM", wi));
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
    public WasteReason addWasteReason(WasteReason wr) throws IOException, SQLException {
        try {
            return (WasteReason) conn.sendData(JConnData.create("ADDWASTEREASON").addParam("WASTE", wr));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
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
            return (List) conn.sendData(JConnData.create("GETALLWASTEREASONS"));
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
    public Supplier addSupplier(Supplier s) throws IOException, SQLException {
        try {
            return (Supplier) conn.sendData(JConnData.create("ADDSUPPLIER").addParam("SUPPLIER", s));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
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
    public int getTotalSoldOfItem(String barcode) throws IOException, SQLException {
        try {
            return (int) conn.sendData(JConnData.create("GETTOTALSOLDITEM").addParam("ID", barcode));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getTotalValueSold(String barcode) throws IOException, SQLException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("GETVALUESOLDITEM").addParam("ID", barcode));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public int getTotalWastedOfItem(String barcode) throws IOException, SQLException {
        try {
            return (int) conn.sendData(JConnData.create("GETTOTALWASTEDITEM").addParam("ID", barcode));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getValueWastedOfItem(String barcode) throws IOException, SQLException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("GETVALUEWASTEDITEM").addParam("ID", barcode));
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
    public BigDecimal getValueSpentOnItem(String barcode) throws IOException, SQLException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("GETSPENTONITEM").addParam("ID", barcode));
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
    public List<Sale> getTerminalSales(Date start, Date end, int terminal, boolean uncashedOnly) throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETTERMINALSALES").addParam("START", start).addParam("END", end).addParam("TERMINAL", terminal).addParam("UNCASHEDFLAG", uncashedOnly));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Sale> getAllTerminalSales(int terminal, boolean uncashedOnly) throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("GETALLTERMINALSALES").addParam("TERMINAL", terminal).addParam("UNCASHEDFLAG", uncashedOnly));
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
    public List<Sale> getStaffSales(Staff s) throws IOException, SQLException, StaffNotFoundException {
        try {
            return (List) conn.sendData(JConnData.create("GETSTAFFSALES").addParam("STAFF", s));
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
    public void integrityCheck() throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("INTEGRITYCHECK"));
        } catch (Throwable ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void onReceive(JConnReceiveEvent event) {
        final JConnData data = event.getData();
        if (data.getFlag().equals("LOG")) {
            g.log(data.getParam("MESSAGE"));
        } else if (data.getFlag().equals("SENDDATA")) {
            new Thread() {
                @Override
                public void run() {
                    g.markNewData((String[]) data.getParam("DATA"));
                }
            }.start(); //Search the queue for the reqeust source.
        } else if (data.getFlag().equals("RENAME")) {
            final String name = (String) data.getParam("NAME");
            g.renameTill(name);
        } else if (data.getFlag().equals("LOGOUT")) {
            new Thread() {
                @Override
                public void run() {
                    g.logout();
                }
            }.start();
        } else if (data.getFlag().equals("REQUPDATE")) {
            new Thread() {
                @Override
                public void run() {
                    g.requestUpdate();
                }
            }.start();
        }
    }

    @Override
    public void onConnectionDrop(JConnEvent event) {
        g.connectionDrop();
    }

    @Override
    public void onConnectionEstablish(JConnEvent event) {
        Staff s = g.connectionReestablish();
        try {
            conn.sendData(JConnData.create("RECONNECT").addParam("UUID", uuid).addParam("SITE", name).addParam("STAFF", s));
        } catch (Throwable ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
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
    public void reinitialiseAllTills() throws IOException, JTillException {
        try {
            conn.sendData(JConnData.create("REINITTILLS"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof JTillException) {
                throw (JTillException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
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
            conn.sendData(JConnData.create("ADDRECREP").addParam("REP", rep));
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

    @Override
    public void sendData(int id, String[] data) throws IOException {
        try {
            conn.sendData(JConnData.create("SENDDATA").addParam("ID", id).addParam("DATA", data));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public void sendBuildUpdates() throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("SENDBUILD"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public byte[] downloadTerminalUpdate() throws Exception {
        try {
            return (byte[]) conn.sendData(JConnData.create("DOWNLOADTER"));
        } catch (Throwable ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void logoutTill(int id) throws IOException, JTillException {
        try {
            conn.sendData(JConnData.create("LOGOUTTERMINAL").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new JTillException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Screen> checkInheritance(Screen s) throws IOException, SQLException, JTillException {
        try {
            return (List<Screen>) conn.sendData(JConnData.create("ISINHERITED").addParam("SCREEN", s));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new JTillException(ex.getMessage());
            }
        }
    }

    @Override
    public Staff getTillStaff(int id) throws IOException, JTillException {
        try {
            return (Staff) conn.sendData(JConnData.create("GETTILLSTAFF").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new JTillException(ex.getMessage());
            }
        }
    }

    @Override
    public TillReport zReport(Till terminal, BigDecimal declared, Staff staff) throws IOException, SQLException, JTillException {
        try {
            return (TillReport) conn.sendData(JConnData.create("ZREPORT").addParam("TERMINAL", terminal).addParam("DECLARED", declared).addParam("STAFF", staff));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new JTillException(ex.getMessage());
            }
        }
    }

    @Override
    public TillReport xReport(Till terminal, BigDecimal declared, Staff staff) throws IOException, SQLException, JTillException {
        try {
            return (TillReport) conn.sendData(JConnData.create("XREPORT").addParam("TERMINAL", terminal).addParam("DECLARED", declared).addParam("STAFF", staff));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new JTillException(ex.getMessage());
            }
        }
    }

    @Override
    public void purgeDatabase() throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("PURGE"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public int removeCashedSales() throws IOException, SQLException {
        try {
            return (int) conn.sendData(JConnData.create("REMOVECASHED"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public List<TillReport> getDeclarationReports(int terminal) throws IOException, SQLException {
        try {
            return (List) conn.sendData(JConnData.create("DECLARATIONREPORTS").addParam("TERMINAL", terminal));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public boolean isTillNameUsed(String name) throws IOException, SQLException {
        try {
            return (boolean) conn.sendData(JConnData.create("CHECKTILLNAME").addParam("NAME", name));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeDeclarationReport(int id) throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVEDECLARATIONREPORT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public int getTotalReceivedOfItem(String barcode) throws IOException, SQLException {
        try {
            return (int) conn.sendData(JConnData.create("TOTALRECEIVED").addParam("ID", barcode));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Sale> consolidated(Date start, Date end, int t) throws IOException, SQLException {
        try {
            return (List<Sale>) conn.sendData(JConnData.create("CONSOLIDATED").addParam("START", start).addParam("END", end).addParam("TILL", t));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getRefunds(Date start, Date end, int t) throws IOException, SQLException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("GETREFUNDS").addParam("START", start).addParam("END", end).addParam("TILL", t));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getWastage(Date start, Date end) throws IOException, SQLException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("GETWASTAGE").addParam("START", start).addParam("END", end));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public void submitStockTake(List<Product> products, boolean zeroRest) throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("SUBMITSTOCKTAKE").addParam("PRODUCTS", products).addParam("ZEROREST", zeroRest));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Category> getCategoriesInDepartment(int department) throws IOException, SQLException {
        try {
            return (List<Category>) conn.sendData(JConnData.create("CATSINDEP").addParam("DEP", department));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Product> getProductsInDepartment(int id) throws IOException, SQLException {
        try {
            return (List<Product>) conn.sendData(JConnData.create("GETPRODUCTSINDEPARTMENT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public Condiment addCondiment(Condiment c) throws IOException, SQLException {
        try {
            return (Condiment) conn.sendData(JConnData.create("ADDCONDIMENT").addParam("C", c));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Condiment> getProductsCondiments(String barcode) throws IOException, SQLException {
        try {
            return (List<Condiment>) conn.sendData(JConnData.create("GETPRODUCTSCONDIMENTS").addParam("ID", barcode));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public Condiment updateCondiment(Condiment c) throws IOException, SQLException, JTillException {
        try {
            return (Condiment) conn.sendData(JConnData.create("UPDATECONDIMENT").addParam("C", c));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else if (ex instanceof JTillException) {
                throw new JTillException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeCondiment(int id) throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("REMOVECONDIMENT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public List<SaleItem> getSalesByDepartment(int id) throws IOException, SQLException {
        try {
            return (List<SaleItem>) conn.sendData(JConnData.create("SALESBYDEPARTMENT").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public Order addOrder(Order o) throws IOException, SQLException {
        try {
            return (Order) conn.sendData(JConnData.create("ADDORDER").addParam("ORDER", o));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public void updateOrder(Order o) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("UPDATEORDER").addParam("ORDER", o));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else if (ex instanceof JTillException) {
                throw new JTillException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Order> getAllOrders() throws IOException, SQLException {
        try {
            return (List<Order>) conn.sendData(JConnData.create("GETALLORDERS"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public void deleteOrder(int id) throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("DELETEORDER").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw new IOException(ex.getMessage());
            } else {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    @Override
    public HashMap<String, Object> terminalInit(int id, String[] data) throws IOException {
        try {
            return (HashMap<String, Object>) conn.sendData(JConnData.create("TERMINALINIT").addParam("ID", id).addParam("DATA", data));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void initComplete() throws IOException {
        try {
            conn.sendData(JConnData.create("INITCOMPLETE"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public int getInits() throws IOException {
        try {
            return (int) conn.sendData(JConnData.create("GETINITS"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public boolean isTillConnected(int id) throws IOException {
        try {
            return (boolean) conn.sendData(JConnData.create("ISTILLCONNECTED").addParam("ID", id));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public String performBackup() throws IOException {
        try {
            return (String) conn.sendData(JConnData.create("BACKUP"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<String> getBackupList() throws IOException {
        try {
            return (List) conn.sendData(JConnData.create("LISTBACKUPS"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void clearBackup(String name) throws IOException {
        try {
            conn.sendData(JConnData.create("DELETEBACKUP").addParam("NAME", name));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public Object[] getLicenseInfo() throws IOException {
        try {
            return (Object[]) conn.sendData(JConnData.create("LICENSEINFO"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void submitSQL(String SQL) throws IOException, SQLException {
        try {
            conn.sendData(JConnData.create("SUBMITSQL").addParam("SQL", SQL));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void deleteWasteReason(WasteReason wr) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("DELETEWASTEREASON").addParam("WR", wr));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new JTillException(ex.getMessage());
            }
        }
    }

    @Override
    public List<WasteReason> getUsedWasteReasons() throws IOException, SQLException {
        try {
            return (List<WasteReason>) conn.sendData(JConnData.create("GETUSEDWASTEREASONS"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public RefundReason addRefundReason(RefundReason r) throws IOException, SQLException {
        try {
            return (RefundReason) conn.sendData(JConnData.create("ADDREFUNDREASON").addParam("REASON", r));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void removeRefundReason(RefundReason r) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("REMOVEREFUNDREASON").addParam("REASON", r));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public void updateRefundReason(RefundReason r) throws IOException, SQLException, JTillException {
        try {
            conn.sendData(JConnData.create("UPDATEREFUNDREASON").addParam("REASON", r));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public RefundReason getRefundReason(int id) throws IOException, SQLException, JTillException {
        try {
            return (RefundReason) conn.sendData(JConnData.create("GETREFUNDREASON").addParam("REASON", id));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<RefundReason> getUsedRefundReasons() throws IOException, SQLException {
        try {
            return (List<RefundReason>) conn.sendData(JConnData.create("GETUSEDREFUNDREASONS"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getStaffMemberSales(Date start, Date end, Staff s) throws IOException, SQLException, JTillException {
        try {
            return (BigDecimal) conn.sendData(JConnData.create("GETSTAFFMEMBERSALES"));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Screen> getScreensWithProduct(Product p) throws IOException, SQLException {
        try {
            return (List<Screen>) conn.sendData(JConnData.create("GETSCREENSWITHPRODUCT").addParam("PRODUCT", p));
        } catch (Throwable ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new IOException(ex.getMessage());
            }
        }
    }
}
