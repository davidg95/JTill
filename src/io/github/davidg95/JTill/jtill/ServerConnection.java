/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server connection class which handles communication with the server.
 *
 * @author David
 */
public class ServerConnection implements DataConnect {

    private Socket socket;
    private ObjectInputStream obIn;
    private ObjectOutputStream obOut;

    private boolean isConnected;
    private String site;

    private GUIInterface g;

    private final Semaphore sem;

    /**
     * Blank constructor.
     */
    public ServerConnection() {
        isConnected = false;
        sem = new Semaphore(1);
    }

    /**
     * Method to connect to the server.
     *
     * @param IP the server IP address.
     * @param PORT the server port number.
     * @param site the name of the terminal.
     * @throws IOException if there was an error connecting.
     * @throws java.net.ConnectException if there was an error connecting.
     */
    public void connect(String IP, int PORT, String site) throws IOException, ConnectException {
        try {
            socket = new Socket();
            this.site = site;
            socket.connect(new InetSocketAddress(IP, PORT), 2000);
            obOut = new ObjectOutputStream(socket.getOutputStream());
            obOut.flush();
            obIn = new ObjectInputStream(socket.getInputStream());
            obOut.writeObject(site);
            g.showModalMessage("Server", "Waing for confirmation");
            Object o = obIn.readObject();
            ConnectionData data = (ConnectionData) o;
            g.hideModalMessage();
            if (data.getFlag().equals("DISALLOW")) {
                g.disallow();
            } else {
                g.allow();
            }
            isConnected = true;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        socket = new Socket();

        socket.connect(new InetSocketAddress(IP, PORT), 2000);
        obOut = new ObjectOutputStream(socket.getOutputStream());
        obOut.flush();
        obOut.writeObject("NOPERM");
        obIn = new ObjectInputStream(socket.getInputStream());
        isConnected = true;
    }

    @Override
    public void suspendSale(Sale sale, Staff staff) throws IOException {
        try {
            obOut.writeObject(ConnectionData.create("SUSPENDSALE", sale, staff));
            ConnectionData data = (ConnectionData) obIn.readObject();
            if (data.getFlag().equals("SUSPEND")) {

            } else if (data.getFlag().equals("FAIL")) {
                throw new IOException(data.getData().toString());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Sale resumeSale(Staff s) throws IOException {
        try {
            obOut.writeObject(ConnectionData.create("RESUMESALE", s));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("RESUME")) {
                return (Sale) data.getData();
            } else {
                throw new IOException(data.getData().toString());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void assisstance(String message) throws IOException {
        obOut.writeObject(ConnectionData.create("ASSISSTANCE", message));
    }

    @Override
    public BigDecimal getTillTakings(String terminal) throws IOException, SQLException {
        try {
            obOut.writeObject(ConnectionData.create("TAKINGS", site));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("GET")) {
                return (BigDecimal) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void sendEmail(String message) throws IOException {
        try {
            obOut.writeObject(ConnectionData.create("EMAIL", message));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                throw new IOException(data.getData().toString());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void emailReceipt(String email, Sale sale) throws IOException {
        try {
            obOut.writeObject(ConnectionData.create("EMAILRECEIPT", email, sale));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                throw new IOException(data.getData().toString());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public Till addTill(Till t) throws IOException, SQLException {
        try {
            obOut.writeObject(ConnectionData.create("ADDTILL", t));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("ADD")) {
                return (Till) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void removeTill(int id) throws IOException, SQLException {
        try {
            obOut.writeObject(ConnectionData.create("REMOVETILL", id));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Till getTill(int id) throws IOException, SQLException, TillNotFoundException {
        try {
            obOut.writeObject(ConnectionData.create("GETTILL", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("GET")) {
                return (Till) data.getData();
            } else {
                if (data.getData() instanceof TillNotFoundException) {
                    throw (TillNotFoundException) data.getData();
                } else if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Till> getAllTills() throws IOException, SQLException {
        try {
            obOut.writeObject(ConnectionData.create("GETALLTILLS"));

            Object o = obIn.readObject();

            if (o instanceof List) {
                return (List<Till>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean connectTill(String t) throws IOException {
        try {
            obOut.writeObject(ConnectionData.create("CONNECTTILL", t));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("CONNECT")) {
                return (boolean) data.getData();
            } else {
                throw new IOException(data.getData().toString());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Sale> getUncashedSales(String t) throws IOException, SQLException {
        try {
            obOut.writeObject(ConnectionData.create("UNCASHEDSALES", t));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("GET")) {
                return (List) data.getData();
            }
            if (data.getData() instanceof SQLException) {
                throw (SQLException) data.getData();
            }
            throw new IOException(data.getData().toString());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void setSetting(String key, String value) throws IOException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("SETSETTING", key, value));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                throw new IOException(data.getData().toString());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
    }

    @Override
    public String getSettings(String key) throws IOException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETSETTING", key));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (String) data.getData();
            } else {
                throw new IOException(data.getData().toString());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
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
     * @return the Product that was added.
     * @throws IOException if there was an error connecting.
     * @throws SQLException if there was a database error.
     */
    @Override
    public Product addProduct(Product p) throws IOException, SQLException {
        try {
            obOut.writeObject(ConnectionData.create("NEWPRODUCT", p));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Product) data.getData();
            }
            if (data.getData() instanceof SQLException) {
                throw (SQLException) data.getData();
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVEPRODUCT", code));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ProductNotFoundException) {
                    throw (ProductNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
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
     * @param code the code of the product to purchase.
     * @param amount the amount of the product to purchase.
     * @return the new stock level;
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     * @throws OutOfStockException if the product is out of stock.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public int purchaseProduct(int code, int amount) throws IOException, ProductNotFoundException, OutOfStockException, SQLException {
        String input = null;
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("PURCHASE", code, amount));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (int) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ProductNotFoundException) {
                    throw (ProductNotFoundException) data.getData();
                } else if (data.getData() instanceof OutOfStockException) {
                    throw (OutOfStockException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPRODUCT", code));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Product) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ProductNotFoundException) {
                    throw (ProductNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPRODUCTBARCODE", barcode));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Product) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ProductNotFoundException) {
                    throw (ProductNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } finally {
            sem.release();
        }
        throw new ProductNotFoundException(barcode);
    }

    @Override
    public Product updateProduct(Product p) throws IOException, SQLException, ProductNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATEPRODUCT", p));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Product) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ProductNotFoundException) {
                    throw (ProductNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return p;
    }

    @Override
    public boolean checkBarcode(String barcode) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("CHECKBARCODE", barcode));

            ConnectionData data = (ConnectionData) obIn.readObject();
            if (data.getFlag().equals("SUCC")) {
                return (boolean) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return false;
    }

    @Override
    public List<Discount> getProductsDiscount(Product p) throws IOException, SQLException {
        List<Discount> discounts = null;
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPRODUCTSDISCOUNT", p));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (List) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return discounts;
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLPRODUCTS"));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            return (List<Product>) o;
        } catch (ClassNotFoundException ex) {
        }
        return new ArrayList<>();
    }

    @Override
    public List<Product> productLookup(String terms) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("PRODUCTLOOKUP", terms));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (List) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return new ArrayList<>();
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("NEWCUSTOMER", customer));
            ConnectionData data = (ConnectionData) obIn.readObject();
            if (data.getFlag().equals("SUCC")) {
                return (Customer) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVECUSTOMER", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof CustomerNotFoundException) {
                    throw (CustomerNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETCUSTOMER", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Customer) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof CustomerNotFoundException) {
                    throw (CustomerNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
        } finally {
            sem.release();
        }
        throw new CustomerNotFoundException("Customer " + id + " could not be found");
    }

    @Override
    public List<Customer> getCustomerByName(String name) throws IOException, CustomerNotFoundException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETCUSTOMERBYNAME", name));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (List) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof CustomerNotFoundException) {
                    throw (CustomerNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
        } finally {
            sem.release();
        }
        throw new CustomerNotFoundException("Customer " + name + " could not be found");
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLCUSTOMERS"));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            return (List<Customer>) o;
        } catch (ClassNotFoundException ex) {
        }
        return new ArrayList<>();
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATECUSTOMER", c));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Customer) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof CustomerNotFoundException) {
                    throw (CustomerNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("CUSTOMERLOOKUP", terms));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (List) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return new ArrayList<>();
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("ADDSALE", s));
            ConnectionData data = (ConnectionData) obIn.readObject();
            if (data.getFlag().equals("SUCC")) {
                return (Sale) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public List<Sale> getAllSales() throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLSALES"));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETSALE", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Sale) data.getData();
            } else {
                if (data.getData() instanceof SaleNotFoundException) {
                    throw (SaleNotFoundException) data.getData();
                } else if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public Sale updateSale(Sale s) throws IOException, SaleNotFoundException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATESALE", s));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Sale) data.getData();
            } else {
                if (data.getData() instanceof SaleNotFoundException) {
                    throw (SaleNotFoundException) data.getData();
                } else if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public List<Sale> getSalesInRange(Time start, Time end) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETSALESDATERANGE", start, end));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (List) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
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
     * @return the Staff that is being added.
     * @throws IOException if there was a server communication error.
     * @throws java.sql.SQLException if there was a database error.
     */
    @Override
    public Staff addStaff(Staff s) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("ADDSTAFF", s));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Staff) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVESTAFF", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof StaffNotFoundException) {
                    throw (StaffNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
    }

    @Override
    public void removeStaff(Staff s) throws IOException, SQLException, StaffNotFoundException {
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETSTAFF", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Staff) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof StaffNotFoundException) {
                    throw (StaffNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
        } finally {
            sem.release();
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLSTAFF"));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATESTAFF", s));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Staff) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof StaffNotFoundException) {
                    throw (StaffNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public int getStaffCount() throws IOException, SQLException {
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("STAFFCOUNT"));
        String input = "";
        try {
            input = (String) obIn.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw ex;
        } finally {
            sem.release();
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("LOGIN", username, password));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Staff) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof LoginException) {
                    throw (LoginException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
        } finally {
            sem.release();
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("TILLLOGIN", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Staff) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof LoginException) {
                    throw (LoginException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
        } finally {
            sem.release();
        }
        return null;
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("LOGOUT", s));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof StaffNotFoundException) {
                    throw (StaffNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
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
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("TILLLOGOUT", s));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof StaffNotFoundException) {
                    throw (StaffNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
    }

    @Override
    public Category addCategory(Category c) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("ADDCATEGORY", c));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Category) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public Category updateCategory(Category c) throws IOException, SQLException, CategoryNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATECATEGORY", c));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Category) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof CategoryNotFoundException) {
                    throw (CategoryNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public void removeCategory(Category c) throws IOException, CategoryNotFoundException, SQLException {
        removeCategory(c.getID());
    }

    @Override
    public void removeCategory(int id) throws IOException, CategoryNotFoundException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVECATEGORY", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof CategoryNotFoundException) {
                    throw (CategoryNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
    }

    @Override
    public Category getCategory(int id) throws IOException, SQLException, CategoryNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETCATEGORY", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Category) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof CategoryNotFoundException) {
                    throw (CategoryNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {

        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public List<Category> getAllCategorys() throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLCATEGORYS"));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

            if (o instanceof List) {
                return (List<Category>) o;
            } else {
                throw (SQLException) o;

            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public List<Product> getProductsInCategory(int id) throws IOException, SQLException, CategoryNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPRODUCTSINCATEGORY"));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (List) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof CategoryNotFoundException) {
                    throw (CategoryNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public Discount addDiscount(Discount d) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("ADDDISCOUNT", d));
            ConnectionData data = (ConnectionData) obIn.readObject();
            if (data.getFlag().equals("SUCC")) {
                return (Discount) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public Discount updateDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATEDISCOUNT", d));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Discount) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof DiscountNotFoundException) {
                    throw (DiscountNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public void removeDiscount(Discount d) throws IOException, DiscountNotFoundException, SQLException {
        removeDiscount(d.getId());
    }

    @Override
    public void removeDiscount(int id) throws IOException, DiscountNotFoundException, SQLException {
        try {
            try {
                sem.acquire();

            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVEDISCOUNT", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof DiscountNotFoundException) {
                    throw (DiscountNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
    }

    @Override
    public Discount getDiscount(int id) throws IOException, SQLException, DiscountNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETDISCOUNT", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Discount) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof DiscountNotFoundException) {
                    throw (DiscountNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public List<Discount> getAllDiscounts() throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLDISCOUNTS"));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
    public Tax addTax(Tax t) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("ADDTAX", t));
            ConnectionData data = (ConnectionData) obIn.readObject();
            if (data.getFlag().equals("SUCC")) {
                return (Tax) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public void removeTax(Tax t) throws IOException, TaxNotFoundException, SQLException {
        removeTax(t.getId());
    }

    @Override
    public void removeTax(int id) throws IOException, TaxNotFoundException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVETAX", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof TaxNotFoundException) {
                    throw (TaxNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
    }

    @Override
    public Tax getTax(int id) throws IOException, SQLException, TaxNotFoundException {
        try {
            try {
                sem.acquire();

            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETTAX", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Tax) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof TaxNotFoundException) {
                    throw (TaxNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public Tax updateTax(Tax t) throws IOException, SQLException, TaxNotFoundException {
        try {
            try {
                sem.acquire();

            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATETAX", t));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Tax) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof TaxNotFoundException) {
                    throw (TaxNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public List<Tax> getAllTax() throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLTAX"));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
    public List<Product> getProductsInTax(int id) throws IOException, SQLException, TaxNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPRODUCTSINTAX", id));

            ConnectionData data;

            try {
                data = (ConnectionData) obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

            if (data.getFlag().equals("SUCC")) {
                return (List) data.getData();
            } else {
                if (data.getData() instanceof TaxNotFoundException) {
                    throw (TaxNotFoundException) data.getData();
                } else {
                    throw (SQLException) data.getData();
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    /**
     * Method to close the connection to the server.
     *
     * @throws IOException if there is a network error.
     */
    @Override
    public void close() throws IOException {
        obOut.writeObject(ConnectionData.create("CONNTERM"));
        isConnected = false;
    }

    @Override
    public String toString() {
        return "Connect to JTill Server\nServer Address: " + socket.getInetAddress().toString() + " on port " + socket.getPort();
    }

    @Override
    public Screen addScreen(Screen s) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("ADDSCREEN", s));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Screen) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public TillButton addButton(TillButton b) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("ADDBUTTON", b));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (TillButton) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public void removeScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVESCREEN", s));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ScreenNotFoundException) {
                    throw (ScreenNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
    }

    @Override
    public void removeButton(TillButton b) throws IOException, SQLException, ButtonNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVEBUTTON", b));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ButtonNotFoundException) {
                    throw (ButtonNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
    }

    @Override
    public Screen getScreen(int s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETSCREEN", s));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Screen) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ScreenNotFoundException) {
                    throw (ScreenNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public TillButton getButton(int b) throws IOException, SQLException, ButtonNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETBUTTON", b));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (TillButton) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ButtonNotFoundException) {
                    throw (ButtonNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public Screen updateScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATESCREEN", s));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Screen) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ScreenNotFoundException) {
                    throw (ScreenNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public TillButton updateButton(TillButton b) throws IOException, SQLException, ButtonNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATEBUTTON", b));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (TillButton) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ButtonNotFoundException) {
                    throw (ButtonNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public List<Screen> getAllScreens() throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLSCREENS"));
            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }
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
    public List<TillButton> getAllButtons() throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLBUTTONS"));
            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }
            if (o instanceof List) {
                return (List<TillButton>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<TillButton> getButtonsOnScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETBUTTONSONSCREEN", s));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (List) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ScreenNotFoundException) {
                    throw (ScreenNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public void deleteAllScreensAndButtons() throws IOException, SQLException {
        obOut.writeObject(ConnectionData.create("DROPSCREENSANDBUTTONS"));
    }

    @Override
    public void setGUI(GUIInterface g) {
        this.g = g;
    }

    @Override
    public Plu addPlu(Plu plu) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("ADDPLU", plu));
            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Plu) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public void removePlu(int id) throws IOException, JTillException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVEPLU", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof TaxNotFoundException) {
                    throw (JTillException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
    }

    @Override
    public void removePlu(Plu p) throws IOException, JTillException, SQLException {
        removePlu(p.getId());
    }

    @Override
    public Plu getPlu(int id) throws IOException, JTillException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPLU", id));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Plu) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof StaffNotFoundException) {
                    throw (JTillException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
        } finally {
            sem.release();
        }
        throw new JTillException(id + " not found");
    }

    @Override
    public Plu getPluByCode(String code) throws IOException, JTillException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPLUBYCODE", code));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Plu) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof ProductNotFoundException) {
                    throw (JTillException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
        } finally {
            sem.release();
        }
        throw new JTillException(code + " could not be found");
    }

    @Override
    public List<Plu> getAllPlus() throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLPLUS"));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

            if (o instanceof List) {
                return (List<Plu>) o;
            } else {
                throw (SQLException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Plu updatePlu(Plu p) throws IOException, JTillException, SQLException {
        try {
            try {
                sem.acquire();

            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATEPLU", p));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("SUCC")) {
                return (Plu) data.getData();
            } else {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof TaxNotFoundException) {
                    throw (JTillException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        return null;
    }

    @Override
    public boolean isTillLoggedIn(Staff s) throws IOException, StaffNotFoundException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("ISLOGGEDTILL", s));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else if (data.getData() instanceof StaffNotFoundException) {
                    throw (StaffNotFoundException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            } else {
                return (boolean) data.getData() == true;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        throw new IOException("Class Error (Update may be required)");
    }

    @Override
    public boolean checkUsername(String username) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("CHECKUSER", username));

            ConnectionData data = (ConnectionData) obIn.readObject();

            if (data.getFlag().equals("FAIL")) {
                if (data.getData() instanceof SQLException) {
                    throw (SQLException) data.getData();
                } else {
                    throw new IOException(data.getData().toString());
                }
            } else {
                return (boolean) data.getData();
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem.release();
        }
        throw new IOException("Class error (Update may be required)");
    }
}
