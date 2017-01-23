/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server connection class which handles communication with the server.
 *
 * @author David
 */
public class ServerConnection implements DataConnectInterface {

    private Socket socket;
    private ObjectInputStream obIn;
    private ObjectOutputStream obOut;

    private boolean isConnected;
    private final String site;

    /**
     * Blank constructor.
     *
     * @param site the name of this site to send to the server.
     */
    public ServerConnection(String site) {
        isConnected = false;
        this.site = site;
    }

    static {

    }

    /**
     * Method to connect to the server.
     *
     * @param IP the server IP address.
     * @param PORT the server port number.
     * @throws IOException if there was an error connection.
     */
    public void connect(String IP, int PORT) throws IOException {
        socket = new Socket();

        socket.connect(new InetSocketAddress(IP, PORT), 2000);
        obOut = new ObjectOutputStream(socket.getOutputStream());
        obOut.flush();
        obIn = new ObjectInputStream(socket.getInputStream());
        obOut.writeObject(site);
        isConnected = true;
    }

    /**
     * Method to return true of false indicating if there is a connection or
     * not.
     *
     * @return true if there is a connection, false otherwise.
     */
    @Override
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Method to add a new product to the server.
     *
     * @param p the product to add.
     * @throws IOException if there was an error connecting.
     */
    @Override
    public void addProduct(Product p) throws IOException {
        obOut.writeObject("NEWPRODUCT");
        obOut.writeObject(p);
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
            obOut.writeObject("REMOVEPRODUCT," + code);

            String input = (String) obIn.readObject();

            if (input.equals("FAIL")) {
                Object o = obIn.readObject();
                if (o instanceof ProductNotFoundException) {
                    throw (ProductNotFoundException) o;
                } else if (o instanceof SQLException) {
                    throw (SQLException) o;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void removeProduct(Product p) throws IOException, ProductNotFoundException, SQLException {
        try {
            obOut.writeObject("REMOVEPRODUCT," + p.getProductCode());
            String input = (String) obIn.readObject();

            if (input.equals("FAIL")) {
                Object o = obIn.readObject();
                if (o instanceof ProductNotFoundException) {
                    throw (ProductNotFoundException) o;
                } else if (o instanceof SQLException) {
                    throw (SQLException) o;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method to purchase a product on the server and reduce is stock level by
     * one.
     *
     * @param code the code of the product to purchase.
     * @return the new stock level;
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     * @throws OutOfStockException if the product is out of stock.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public int purchaseProduct(int code) throws IOException, ProductNotFoundException, OutOfStockException, SQLException {
        String input = null;
        try {
            obOut.writeObject("PURCHASE," + code);

            input = (String) obIn.readObject();

            if (input.equals("FAIL")) {
                Object o = obIn.readObject();
                if (o instanceof ProductNotFoundException) {
                    throw (ProductNotFoundException) o;
                } else if (o instanceof SQLException) {
                    throw (SQLException) o;
                } else if (o instanceof OutOfStockException) {
                    throw (OutOfStockException) o;
                } else {
                    throw new ProductNotFoundException(code + " has not been found");
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.parseInt(input);
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
            obOut.writeObject("GETPRODUCT," + code);

            Object o = obIn.readObject();

            if (o instanceof Product) {
                return (Product) o;
            } else if (o instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new ProductNotFoundException("Product " + code + " was not found");
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
            obOut.writeObject("GETPRODUCTBARCODE," + barcode);

            Object o = obIn.readObject();

            if (o instanceof Product) {
                return (Product) o;
            } else if (o instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
        }
        throw new ProductNotFoundException(barcode);
    }

    @Override
    public Product updateProduct(Product p) throws IOException, SQLException, ProductNotFoundException {
        try {
            obOut.writeObject("UPDATEPRODUCT");

            obOut.writeObject(p);

            Object o = obIn.readObject();

            if (o instanceof SQLException) {
                throw (SQLException) o;
            } else if (o instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }

    @Override
    public boolean checkBarcode(String barcode) throws IOException, SQLException {
        obOut.writeObject("CHECKBARCODE," + barcode);

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "USED":
                return true;
            case "NOTUSED":
                return false;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public void setStock(int code, int stock) throws IOException, ProductNotFoundException, SQLException {
        obOut.writeObject(code + "," + stock);

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "FAIL":
                throw new ProductNotFoundException("Product code " + code + " could not be found");
            case "SUCC":
                break;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public List<Discount> getProductsDiscount(Product p) throws IOException, SQLException {
        List<Discount> discounts = null;
        try {
            obOut.writeObject("GETPRODUCTSDISCOUNT");
            obOut.writeObject(p);

            Object o = obIn.readObject();

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            discounts = (List<Discount>) o;

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return discounts;
    }

    /**
     * Method to get the total number of products on the server.
     *
     * @return int value representing how many products are on the server.
     * @throws IOException if there was an error connecting.
     */
    @Override
    public int getProductCount() throws IOException {
        obOut.writeObject("GETPRODUCTCOUNT");

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Integer.parseInt(input);
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
            obOut.writeObject("GETALLPRODUCTS");

            Object o = obIn.readObject();

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            return (List<Product>) o;
        } catch (ClassNotFoundException ex) {
        }
        return new ArrayList<>();
    }

    /**
     * Method to add a new customer to the server.
     *
     * @param customer the new customer to add.
     * @throws IOException if there was an error connecting.
     */
    @Override
    public void addCustomer(Customer customer) throws IOException {
        obOut.writeObject("NEWCUSTOMER");
        obOut.writeObject(customer);
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
            obOut.writeObject("REMOVECUSTOMER," + id);

            String input = (String) obIn.readObject();

            if (input.equals("FAIL")) {
                Object o = obIn.readObject();
                if (o instanceof SQLException) {
                    throw (SQLException) o;
                } else if (o instanceof CustomerNotFoundException) {
                    throw (CustomerNotFoundException) o;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void removeCustomer(Customer c) throws IOException, SQLException, CustomerNotFoundException {
        try {
            obOut.writeObject("REMOVECUSTOMER," + c.getId());

            String input = (String) obIn.readObject();

            if (input.equals("FAIL")) {
                Object o = obIn.readObject();
                if (o instanceof SQLException) {
                    throw (SQLException) o;
                } else if (o instanceof CustomerNotFoundException) {
                    throw (CustomerNotFoundException) o;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            obOut.writeObject("GETCUSTOMER," + id);

            Object o = obIn.readObject();

            if (o instanceof Customer) {
                return (Customer) o;
            } else if (o instanceof CustomerNotFoundException) {
                throw (CustomerNotFoundException) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
        }
        throw new CustomerNotFoundException("Customer " + id + " could not be found");
    }

    @Override
    public List<Customer> getCustomerByName(String name) throws IOException, CustomerNotFoundException, SQLException {
        try {
            obOut.writeObject("GETCUSTOMERBYNAME," + name);

            Object o = obIn.readObject();

            if (o instanceof List) {
                return (List<Customer>) o;
            } else if (o instanceof CustomerNotFoundException) {
                throw (CustomerNotFoundException) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
        }
        throw new CustomerNotFoundException("Customer " + name + " could not be found");
    }

    /**
     * Method to get the total number of customer on the server.
     *
     * @return int value representing how many customers are on the server.
     * @throws IOException if there was an error connecting.
     */
    @Override
    public int getCustomerCount() throws IOException {
        obOut.writeObject("GETCUSTOMERCOUNT");

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Integer.parseInt(input);
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
            obOut.writeObject("GETALLCUSTOMERS");

            Object o = obIn.readObject();

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            return (List<Customer>) o;
        } catch (ClassNotFoundException ex) {
        }
        return new ArrayList<>();
    }

    @Override
    public Customer updateCustomer(Customer c) throws IOException, SQLException, CustomerNotFoundException {
        try {
            obOut.writeObject("UPDATECUSTOMER");
            obOut.writeObject(c);

            Object o = obIn.readObject();
            if (o instanceof Customer) {
                return (Customer) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (CustomerNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Method to send a sale to the server.
     *
     * @param s the sale to send.
     * @throws IOException if there was an error connecting.
     */
    @Override
    public void addSale(Sale s) throws IOException {
        obOut.writeObject("ADDSALE");
        obOut.writeObject(s);
        obOut.flush();
    }

    @Override
    public List<Sale> getAllSales() throws IOException, SQLException {
        try {
            obOut.writeObject("GETALLSALES");
            Object o = obIn.readObject();

            if (o instanceof List) {
                return (List<Sale>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Sale getSale(int id) throws IOException, SQLException, SaleNotFoundException {
        try {
            obOut.writeObject("GETSALE," + id);

            Object o = obIn.readObject();

            if (o instanceof Sale) {
                return (Sale) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (SaleNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Sale> getSalesInRange(Date start, Date end) throws IOException, SQLException {
        try {
            obOut.writeObject("GETSALESDATERANGE");
            obOut.writeObject(start);
            obOut.writeObject(end);

            Object o = obIn.readObject();

            if (o instanceof List) {
                return (List<Sale>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Method to add a new member of staff to the system.
     *
     * @param s the new member of staff to add.
     * @throws IOException if there was a server communication error.
     */
    @Override
    public void addStaff(Staff s) throws IOException {
        obOut.writeObject("ADDSTAFF");
        obOut.writeObject(s);
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
            obOut.writeObject("REMOVESTAFF," + id);

            String input = (String) obIn.readObject();

            if (input.equals("FAIL")) {
                Object o = obIn.readObject();
                if (o instanceof SQLException) {
                    throw (SQLException) o;
                } else if (o instanceof StaffNotFoundException) {
                    throw (StaffNotFoundException) o;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void removeStaff(Staff s) throws IOException, SQLException, StaffNotFoundException {
        try {
            obOut.writeObject("REMOVESTAFF," + s.getId());

            String input = (String) obIn.readObject();

            if (input.equals("FAIL")) {
                Object o = obIn.readObject();
                if (o instanceof SQLException) {
                    throw (SQLException) o;
                } else if (o instanceof StaffNotFoundException) {
                    throw (StaffNotFoundException) o;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            obOut.writeObject("GETSTAFF," + id);

            Object o = obIn.readObject();

            if (o instanceof Staff) {
                return (Staff) o;
            } else if (o instanceof StaffNotFoundException) {
                throw (StaffNotFoundException) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
        }
        throw new StaffNotFoundException(id + "");
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
            obOut.writeObject("GETALLSTAFF");

            Object o = obIn.readObject();

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            return (List<Staff>) o;
        } catch (ClassNotFoundException ex) {

        }
        return new ArrayList<>();
    }

    @Override
    public Staff updateStaff(Staff s) throws IOException, SQLException, StaffNotFoundException {
        try {
            obOut.writeObject("UPDATESTAFF");
            obOut.writeObject(s);

            Object o = obIn.readObject();

            if (o instanceof Staff) {
                return (Staff) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (StaffNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int staffCount() throws IOException, SQLException {
        obOut.writeObject("STAFFCOUNT");
        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (input.equals("FAIL")) {
            throw new SQLException("Database Error");
        } else {
            return Integer.parseInt(input);
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
            obOut.writeObject("LOGIN," + username + "," + password);

            Object o = obIn.readObject();

            if (o instanceof Staff) {
                return (Staff) o;
            } else if (o instanceof LoginException) {
                throw (LoginException) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
        }
        throw new LoginException("Login Error");
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
            obOut.writeObject("TILLLOGIN," + id);

            Object o = obIn.readObject();

            if (o instanceof Staff) {
                return (Staff) o;
            } else if (o instanceof LoginException) {
                throw (LoginException) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
        }
        return null;
    }

    /**
     * Method to log a member of staff out the system.
     *
     * @param id the id to log out.
     * @throws IOException if there was a server communication error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    @Override
    public void logout(int id) throws IOException, StaffNotFoundException {
        obOut.writeObject("LOGOUT," + id);

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (input.equals("FAIL")) {
            throw new StaffNotFoundException(id + "");
        }
    }

    /**
     * Method to log a member of staff out a till.
     *
     * @param id the id to log out.
     * @throws IOException if there was a server communication error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    @Override
    public void tillLogout(int id) throws IOException, StaffNotFoundException {
        obOut.writeObject("TILLLOGOUT," + id);

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (input.equals("FAIL")) {
            throw new StaffNotFoundException(id + "");
        }
    }

    @Override
    public void addCategory(Category c) throws IOException {
        obOut.writeObject("ADDCATEGORY");
        obOut.writeObject(c);
    }

    @Override
    public Category updateCategory(Category c) throws IOException, SQLException, CategoryNotFoundException {
        try {
            obOut.writeObject("UPDATECATEGORY");
            obOut.writeObject(c);

            Object o = obIn.readObject();

            if (o instanceof Category) {
                return (Category) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (CategoryNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void removeCategory(Category c) throws IOException, CategoryNotFoundException, SQLException {
        obOut.writeObject("REMOVECATEGORY," + c.getID());
        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "FAIL":
                throw new CategoryNotFoundException("Category " + c.getName() + " was not found");
            case "SUCC":
                break;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public void removeCategory(int id) throws IOException, CategoryNotFoundException, SQLException {
        obOut.writeObject("REMOVECATEGORY," + id);
        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "FAIL":
                throw new CategoryNotFoundException("Category " + id + " was not found");
            case "SUCC":
                break;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public Category getCategory(int id) throws IOException, SQLException, CategoryNotFoundException {
        try {
            obOut.writeObject("GETCATEGORY," + id);
            Object o = obIn.readObject();

            if (o instanceof Category) {
                return (Category) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (CategoryNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Category> getAllCategorys() throws IOException, SQLException {
        try {
            obOut.writeObject("GETALLCATEGORYS");

            Object o = obIn.readObject();

            if (o instanceof List) {
                return (List<Category>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Product> getProductsInCategory(int id) throws IOException, SQLException, CategoryNotFoundException {
        try {
            obOut.writeObject("GETPRODUCTSINCATEGORY");

            Object o = obIn.readObject();

            if (o instanceof List) {
                return (List<Product>) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (CategoryNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void addDiscount(Discount d) throws IOException {
        obOut.writeObject("ADDDISCOUNT");
        obOut.writeObject(d);
    }

    @Override
    public Discount updateDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException {
        try {
            obOut.writeObject("UPDATEDISCOUNT");
            obOut.writeObject(d);

            Object o = obIn.readObject();

            if (o instanceof Discount) {
                return (Discount) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (DiscountNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void removeDiscount(Discount d) throws IOException, DiscountNotFoundException, SQLException {
        obOut.writeObject("REMOVEDISCOUNT," + d.getId());

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "FAIL":
                throw new DiscountNotFoundException("Discount " + d.getName() + " could not be found");
            case "SUCC":
                break;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public void removeDiscount(int id) throws IOException, DiscountNotFoundException, SQLException {
        obOut.writeObject("REMOVEDISCOUNT," + id);

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "FAIL":
                throw new DiscountNotFoundException("Discount " + id + " could not be found");
            case "SUCC":
                break;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public Discount getDiscount(int id) throws IOException, SQLException, DiscountNotFoundException {
        try {
            obOut.writeObject("GETDISCOUNT," + id);

            Object o = obIn.readObject();

            if (o instanceof Discount) {
                return (Discount) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (DiscountNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Discount> getAllDiscounts() throws IOException, SQLException {
        try {
            obOut.writeObject("GETALLDISCOUNTS");

            Object o = obIn.readObject();

            if (o instanceof List) {
                return (List<Discount>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void addTax(Tax t) throws IOException {
        obOut.writeObject("ADDTAX");
        obOut.writeObject(t);
    }

    @Override
    public void removeTax(Tax t) throws IOException, TaxNotFoundException, SQLException {
        obOut.writeObject("REMOVETAX," + t.getId());

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "FAIL":
                throw new TaxNotFoundException("Tax " + t.getId() + " could not be found");
            case "SUCC":
                break;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public void removeTax(int id) throws IOException, TaxNotFoundException, SQLException {
        obOut.writeObject("REMOVETAX," + id);

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "FAIL":
                throw new TaxNotFoundException("Tax " + id + " could not be found");
            case "SUCC":
                break;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public Tax getTax(int id) throws IOException, SQLException, TaxNotFoundException {
        try {
            obOut.writeObject("GETTAX," + id);

            Object o = obIn.readObject();

            if (o instanceof Tax) {
                return (Tax) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (TaxNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Tax updateTax(Tax t) throws IOException, SQLException, TaxNotFoundException {
        try {
            obOut.writeObject("UPDATETAX");
            obOut.writeObject(t);

            Object o = obIn.readObject();

            if (o instanceof Tax) {
                return (Tax) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (TaxNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Tax> getAllTax() throws IOException, SQLException {
        try {
            obOut.writeObject("GETALLTAX");

            Object o = obIn.readObject();

            if (o instanceof List) {
                return (List<Tax>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void addVoucher(Voucher v) throws IOException {
        obOut.writeObject("ADDVOUCHER");
        obOut.writeObject(v);
    }

    @Override
    public void removeVoucher(Voucher v) throws IOException, VoucherNotFoundException, SQLException {
        obOut.writeObject("REMOVEVOUCHER," + v.getId());

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "FAIL":
                throw new VoucherNotFoundException("Voucher " + v.getName() + " could not be found");
            case "SUCC":
                break;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public void removeVoucher(int id) throws IOException, VoucherNotFoundException, SQLException {
        obOut.writeObject("REMOVEVOUCHER," + id);

        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (input) {
            case "FAIL":
                throw new VoucherNotFoundException("Voucher " + id + " could not be found");
            case "SUCC":
                break;
            default:
                throw new SQLException(input);
        }
    }

    @Override
    public Voucher getVoucher(int id) throws IOException, SQLException, VoucherNotFoundException {
        try {
            obOut.writeObject("GETVOUCHER," + id);

            Object o = obIn.readObject();

            if (o instanceof Voucher) {
                return (Voucher) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (VoucherNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Voucher updateVoucher(Voucher v) throws IOException, SQLException, VoucherNotFoundException {
        try {
            obOut.writeObject("UPDATEVOUCHER");
            obOut.writeObject(v);

            Object o = obIn.readObject();

            if (o instanceof Voucher) {
                return (Voucher) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (VoucherNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Voucher> getAllVouchers() throws IOException, SQLException {
        try {
            obOut.writeObject("GETALLVOUCHERS");

            Object o = obIn.readObject();

            if (o instanceof List) {
                return (List<Voucher>) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Method to close the connection to the server.
     */
    @Override
    public void close() {
        try {
            obOut.writeObject("CONNTERM");
            isConnected = false;
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return "Connect to JTill Server\nServer Address: " + socket.getInetAddress().toString() + " on port " + socket.getPort();
    }

    @Override
    public void addScreen(Screen s) throws IOException, SQLException {
        obOut.writeObject("ADDSCREEN");
        obOut.writeObject(s);
    }

    @Override
    public void addButton(Button b) throws IOException, SQLException {
        obOut.writeObject("ADDBUTTON");
        obOut.writeObject(b);
    }

    @Override
    public void removeScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        obOut.writeObject("REMOVESCREEN");
        obOut.writeObject(s);
        try {
            if (((String) obIn.readObject()).equals("FAIL")) {
                throw new ScreenNotFoundException("Screen " + s + " could not be found");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void removeButton(Button b) throws IOException, SQLException, ButtonNotFoundException {
        obOut.writeObject("REMOVEBUTTON");
        obOut.writeObject(b);
        try {
            if (((String) obIn.readObject()).equals("FAIL")) {
                throw new ButtonNotFoundException("Button " + b + " could not be found");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Screen getScreen(int s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            obOut.writeObject("GETSCREEN," + s);
            Object o = obIn.readObject();
            if (o instanceof Screen) {
                return (Screen) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (ScreenNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Button getButton(int b) throws IOException, SQLException, ButtonNotFoundException {
        try {
            obOut.writeObject("GETBUTTON," + b);
            Object o = obIn.readObject();
            if (o instanceof Button) {
                return (Button) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (ButtonNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Screen updateScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            obOut.writeObject("UPDATESCREEN");
            obOut.writeObject(s);
            Object o = obIn.readObject();
            if (o instanceof Screen) {
                return (Screen) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (ScreenNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Button updateButton(Button b) throws IOException, SQLException, ButtonNotFoundException {
        try {
            obOut.writeObject("UPDATEBUTTON");
            obOut.writeObject(b);
            Object o = obIn.readObject();
            if (o instanceof Button) {
                return (Button) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (ButtonNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Screen> getAllScreens() throws IOException, SQLException {
        try {
            obOut.writeObject("GETALLSCREENS");
            Object o = obIn.readObject();
            if (o instanceof List) {
                return (List<Screen>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Button> getAllButtons() throws IOException, SQLException {
        try {
            obOut.writeObject("GETALLBUTTONS");
            Object o = obIn.readObject();
            if (o instanceof List) {
                return (List<Button>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Button> getButtonsOnScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            obOut.writeObject("GETBUTTONSONSCREEN");
            obOut.writeObject(s);
            obOut.flush();
            Object o = obIn.readObject();
            if (o instanceof List) {
                return (List<Button>) o;
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else {
                throw (ScreenNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void deleteAllScreensAndButtons() throws IOException, SQLException {
        obOut.writeObject("DROPSCREENSANDBUTTONS");
    }
}
