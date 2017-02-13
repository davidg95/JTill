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
public class ServerConnection implements DataConnectInterface {

    private Socket socket;
    private ObjectInputStream obIn;
    private ObjectOutputStream obOut;

    private boolean isConnected;
    private final String site;

    private GUIInterface g;
    private IncomingThread in;

    private final Semaphore sem;

    /**
     * Blank constructor.
     *
     * @param site the name of this site to send to the server.
     */
    public ServerConnection(String site) {
        isConnected = false;
        this.site = site;
        sem = new Semaphore(1);
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
//        in = new IncomingThread(g, obIn, obOut);
//        in.start();
    }

    @Override
    public void suspendSale(Sale sale, Staff staff) throws IOException {
        obOut.writeObject(ConnectionData.create("SUSPENDSALE", sale, staff));
    }

    @Override
    public Sale resumeSale(Staff s) throws IOException {
        try {
            obOut.writeObject(ConnectionData.create("RESUMESALE", s));

            Sale sale = (Sale) obIn.readObject();
            return sale;
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

            Object o = obIn.readObject();

            if (o instanceof SQLException) {
                throw (SQLException) o;
            }

            return (BigDecimal) o;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void sendEmail(String message) throws IOException {
        obOut.writeObject(ConnectionData.create("EMAIL", message));
    }

    @Override
    public void emailReceipt(String email, Sale sale) throws IOException {
        obOut.writeObject(ConnectionData.create("EMAILRECEIPT", email, sale));
    }

    public class IncomingThread extends Thread {

        private final GUIInterface g;
        private final ObjectInputStream obIn;
        private final ObjectOutputStream obOut;

        private boolean running = true;

        private ConnectionData data;

        public IncomingThread(GUIInterface g, ObjectInputStream obIn, ObjectOutputStream obOut) {
            super("Incoming Thread");
            this.g = g;
            this.obIn = obIn;
            this.obOut = obOut;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    data = (ConnectionData) obIn.readObject();
                    String flag = data.getFlag();

                    switch (flag) {
                        case "LOG":
                            g.log((String) data.getData());
                            break;
                        case "CONN":
                            g.setClientLabel((String) data.getData());
                            break;
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public void stopRun() {
            running = false;
        }
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
        obOut.writeObject(ConnectionData.create("NEWPRODUCT", p));
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

            String input;
            try {
                input = (String) obIn.readObject();
            } catch (IOException ex) {
                sem.release();
                throw ex;
            }

            if (input.equals("FAIL")) {
                Object o;
                try {
                    o = obIn.readObject();
                } catch (IOException ex) {
                    throw ex;
                } finally {
                    sem.release();
                }
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVEPRODUCT", p.getId()));

            String input;
            try {
                input = (String) obIn.readObject();
            } catch (IOException ex) {
                sem.release();
                throw ex;
            }

            if (input.equals("FAIL")) {
                Object o;
                try {
                    o = obIn.readObject();
                } catch (IOException ex) {
                    throw ex;
                } finally {
                    sem.release();
                }
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

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                sem.release();
                throw ex;
            }

            if (o instanceof String) {
                input = (String) o;
            } else {
                sem.release();
                return (int) o;
            }

            if (input.equals("FAIL")) {
                try {
                    o = obIn.readObject();
                } catch (IOException ex) {
                    throw ex;
                } finally {
                    sem.release();
                }
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPRODUCT", code));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPRODUCTBARCODE", barcode));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATEPRODUCT", p));

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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("CHECKBARCODE", barcode));

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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("SETSTOCK", code + "," + stock));

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETPRODUCTSDISCOUNT", p));

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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("GETPRODUCTCOUNT"));

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
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
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
        obOut.writeObject(ConnectionData.create("NEWCUSTOMER", customer));
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
            String input;
            try {
                input = (String) obIn.readObject();
            } catch (IOException ex) {
                sem.release();
                throw ex;
            }

            if (input.equals("FAIL")) {
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVECUSTOMER", c.getId()));

            String input;
            try {
                input = (String) obIn.readObject();
            } catch (IOException ex) {
                sem.release();
                throw ex;
            }

            if (input.equals("FAIL")) {
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETCUSTOMER", id));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETCUSTOMERBYNAME", name));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("GETCUSTOMERCOUNT"));

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

    @Override
    public Customer updateCustomer(Customer c) throws IOException, SQLException, CustomerNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATECUSTOMER", c));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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

    @Override
    public List<Customer> customerLookup(String terms) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("CUSTOMERLOOKUP", terms));

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
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    /**
     * Method to send a sale to the server.
     *
     * @param s the sale to send.
     * @throws IOException if there was an error connecting.
     */
    @Override
    public void addSale(Sale s) throws IOException {
        obOut.writeObject(ConnectionData.create("ADDSALE", s));
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

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
    public Sale updateSale(Sale s) throws IOException, SaleNotFoundException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATESALE", s));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
    public List<Sale> getSalesInRange(Time start, Time end) throws IOException, SQLException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETSALESDATERANGE", start, end));

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

    /**
     * Method to add a new member of staff to the system.
     *
     * @param s the new member of staff to add.
     * @throws IOException if there was a server communication error.
     */
    @Override
    public void addStaff(Staff s) throws IOException {
        obOut.writeObject(ConnectionData.create("ADDSTAFF", s));
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

            String input;
            try {
                input = (String) obIn.readObject();
            } catch (IOException ex) {
                sem.release();
                throw ex;
            }

            if (input.equals("FAIL")) {
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("REMOVESTAFF", s.getId()));

            String input;
            try {
                input = (String) obIn.readObject();
            } catch (IOException ex) {
                sem.release();
                throw ex;
            }

            if (input.equals("FAIL")) {
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETSTAFF", id));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("TILLLOGIN", id));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
     * @param s the staff to log out.
     * @throws IOException if there was a server communication error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    @Override
    public void logout(Staff s) throws IOException, StaffNotFoundException {
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("LOGOUT", s));

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
            throw new StaffNotFoundException(s.getId() + "");
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
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("TILLLOGOUT", s));

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
            throw new StaffNotFoundException(s.getId() + "");
        }
    }

    @Override
    public void addCategory(Category c) throws IOException {
        obOut.writeObject(ConnectionData.create("ADDCATEGORY", c));
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

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVECATEGORY", c.getID()));
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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVECATEGORY", id));
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETCATEGORY", id));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
        obOut.writeObject(ConnectionData.create("ADDDISCOUNT", d));
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

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVEDISCOUNT", d.getId()));

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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVEDISCOUNT", id));

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETDISCOUNT", id));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
    public void addTax(Tax t) throws IOException {
        obOut.writeObject(ConnectionData.create("ADDTAX", t));
    }

    @Override
    public void removeTax(Tax t) throws IOException, TaxNotFoundException, SQLException {
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVETAX", t.getId()));

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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVETAX", id));

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETTAX", id));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATETAX", t));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
    public void addVoucher(Voucher v) throws IOException {
        obOut.writeObject(ConnectionData.create("ADDVOUCHER", v));
    }

    @Override
    public void removeVoucher(Voucher v) throws IOException, VoucherNotFoundException, SQLException {
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVEVOUCHER", v.getId()));

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
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVEVOUCHER", id));

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETVOUCHER", id));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATEVOUCHER", v));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETALLVOUCHERS"));

            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }

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
            obOut.writeObject(ConnectionData.create("CONNTERM"));
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
        obOut.writeObject(ConnectionData.create("ADDSCREEN", s));
    }

    @Override
    public void addButton(TillButton b) throws IOException, SQLException {
        obOut.writeObject(ConnectionData.create("ADDBUTTON", b));
    }

    @Override
    public void removeScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVESCREEN", s));
        try {
            if (((String) obIn.readObject()).equals("FAIL")) {
                throw new ScreenNotFoundException("Screen " + s + " could not be found");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw ex;
        } finally {
            sem.release();
        }
    }

    @Override
    public void removeButton(TillButton b) throws IOException, SQLException, ButtonNotFoundException {
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        obOut.writeObject(ConnectionData.create("REMOVEBUTTON", b));
        try {
            if (((String) obIn.readObject()).equals("FAIL")) {
                throw new ButtonNotFoundException("Button " + b + " could not be found");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw ex;
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
            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }
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
    public TillButton getButton(int b) throws IOException, SQLException, ButtonNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("GETBUTTON", b));
            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }
            if (o instanceof TillButton) {
                return (TillButton) o;
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
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATESCREEN", s));
            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }
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
    public TillButton updateButton(TillButton b) throws IOException, SQLException, ButtonNotFoundException {
        try {
            try {
                sem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            obOut.writeObject(ConnectionData.create("UPDATEBUTTON", b));
            Object o;
            try {
                o = obIn.readObject();
            } catch (IOException ex) {
                throw ex;
            } finally {
                sem.release();
            }
            if (o instanceof TillButton) {
                return (TillButton) o;
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
            } else if (o instanceof SQLException) {
                throw (SQLException) o;
            } else if (o instanceof ScreenNotFoundException) {
                throw (ScreenNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
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
}
