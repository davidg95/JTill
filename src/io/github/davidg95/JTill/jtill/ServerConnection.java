/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server connection class which handles communication with the server.
 *
 * @author David
 */
public class ServerConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
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
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        obOut = new ObjectOutputStream(socket.getOutputStream());
        obOut.flush();
        obIn = new ObjectInputStream(socket.getInputStream());
        out.println(site);
        isConnected = true;
    }

    /**
     * Method to return true of false indicating if there is a connection or
     * not.
     *
     * @return true if there is a connection, false otherwise.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Method to add a new product to the server.
     *
     * @param p the product to add.
     * @throws IOException if there was an error connecting.
     */
    public void addNewProduct(Product p) throws IOException {
        out.println("NEWPRODUCT");
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
    public void removeProduct(int code) throws IOException, ProductNotFoundException, SQLException {
        try {
            out.println("REMOVEPRODUCT," + code);

            String input = in.readLine();

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
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     * @throws OutOfStockException if the product is out of stock.
     * @throws java.sql.SQLException if there was a database error.
     */
    public void purchaseProduct(int code) throws IOException, ProductNotFoundException, OutOfStockException, SQLException {
        try {
            out.println("PURCHASE," + code);

            String input = in.readLine();

            if (input.equals("FAIL")) {
                Object o = obIn.readObject();
                if (o instanceof ProductNotFoundException) {
                    throw (ProductNotFoundException) o;
                } else if (o instanceof SQLException) {
                    throw (SQLException) o;
                } else if (o instanceof OutOfStockException) {
                    throw (OutOfStockException) o;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
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
    public Product getProduct(int code) throws IOException, ProductNotFoundException, SQLException {
        try {
            out.println("GETPRODUCT," + code);

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
        throw new ProductNotFoundException(code + "");
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
    public Product getProductByBarCode(String barcode) throws IOException, ProductNotFoundException, SQLException {
        try {
            out.println("GETPRODUCTBARCODE," + barcode);

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

    /**
     * Method to get the total number of products on the server.
     *
     * @return int value representing how many products are on the server.
     * @throws IOException if there was an error connecting.
     */
    public int getProductCount() throws IOException {
        out.println("GETPRODUCTCOUNT");

        String input = in.readLine();

        return Integer.parseInt(input);
    }

    /**
     * Method to get a List of all products on the server.
     *
     * @return List of type product.
     * @throws IOException if there was an error connecting.
     * @throws java.sql.SQLException if there was a database error.
     */
    public List<Product> getAllProducts() throws IOException, SQLException {
        try {
            out.println("GETALLPRODUCTS");

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
    public void addNewCustomer(Customer customer) throws IOException {
        out.println("NEWCUSTOMER");
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
    public void removeCustomer(String id) throws IOException, CustomerNotFoundException, SQLException {
        try {
            out.println("REMOVECUSTOMER," + id);

            String input = in.readLine();

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
    public Customer getCustomer(String id) throws IOException, CustomerNotFoundException, SQLException {
        try {
            out.println("GETCUSTOMER," + id);

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
        throw new CustomerNotFoundException(id);
    }

    /**
     * Method to get the total number of customer on the server.
     *
     * @return int value representing how many customers are on the server.
     * @throws IOException if there was an error connecting.
     */
    public int getCustomerCount() throws IOException {
        out.println("GETCUSTOMERCOUNT");

        String input = in.readLine();

        return Integer.parseInt(input);
    }

    /**
     * Method to get a List of all customers on the server.
     *
     * @return List of type Customer.
     * @throws IOException if there was an error connecting.
     * @throws java.sql.SQLException if there was a database error.
     */
    public List<Customer> getAllCustomers() throws IOException, SQLException {
        try {
            out.println("GETALLCUSTOMERS");

            Object o = obIn.readObject();

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            return (List<Customer>) o;
        } catch (ClassNotFoundException ex) {
        }
        return new ArrayList<>();
    }

    /**
     * Method to send a sale to the server.
     *
     * @param s the sale to send.
     * @throws IOException if there was an error connecting.
     */
    public void addSale(Sale s) throws IOException {
        out.println("ADDSALE");
        obOut.writeObject(s);
        obOut.flush();
    }

    /**
     * Method to add a new member of staff to the system.
     *
     * @param s the new member of staff to add.
     * @throws IOException if there was a server communication error.
     */
    public void addNewStaff(Staff s) throws IOException {
        out.println("ADDSTAFF");
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
    public void removeStaff(int id) throws IOException, StaffNotFoundException, SQLException {
        try {
            out.println("REMOVESTAFF," + id);

            String input = in.readLine();

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
    public Staff getStaff(int id) throws IOException, StaffNotFoundException, SQLException {
        try {
            out.println("GETSTAFF," + id);

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
     * Method to get the total number of staff on the system.
     *
     * @return int value representing the total number of staff.
     * @throws IOException if there was a server communication error.
     */
    @Deprecated
    public int getStaffCount() throws IOException {
        out.println("GETSTAFFCOUNT");

        String input = in.readLine();

        return Integer.parseInt(input);
    }

    /**
     * Method to get a List of all the staff on the system.
     *
     * @return List of type staff.
     * @throws IOException if there was a server communication error.
     * @throws java.sql.SQLException if there was a database error.
     */
    public List<Staff> getAllStaff() throws IOException, SQLException {
        try {
            out.println("GETALLSTAFF");

            Object o = obIn.readObject();

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            return (List<Staff>) o;
        } catch (ClassNotFoundException ex) {

        }
        return new ArrayList<>();
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
    public Staff login(String username, String password) throws IOException, LoginException, SQLException {
        try {
            out.println("LOGIN," + username + "," + password);

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
    public Staff tillLogin(int id) throws IOException, LoginException, SQLException {
        try {
            out.println("TILLLOGIN," + id);

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
    public void logout(int id) throws IOException, StaffNotFoundException {
        out.println("LOGOUT," + id);

        String input = in.readLine();

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
    public void tillLogout(int id) throws IOException, StaffNotFoundException {
        out.println("TILLLOGOUT," + id);

        String input = in.readLine();

        if (input.equals("FAIL")) {
            throw new StaffNotFoundException(id + "");
        }
    }

    public List<Category> getCategoryButtons() throws IOException {
        try {
            out.println("GETCATBUTTONS");

            Object o = obIn.readObject();

            List<Category> categorys = (List<Category>) o;

            return categorys;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Product> getProductButtons(int catId) throws IOException, SQLException {
        try {
            out.println("GETPRODUCTBUTTONS," + catId);

            Object o = obIn.readObject();

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            List<Product> products = (List<Product>) o;

            return products;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Method to close the connection to the server.
     */
    public void close() {
        out.println("CONNTERM");
        isConnected = false;
    }
}
