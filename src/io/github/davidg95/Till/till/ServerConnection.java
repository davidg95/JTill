/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    private String site;

    /**
     * Blank constructor.
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
        obIn = new ObjectInputStream(socket.getInputStream());
        obOut = new ObjectOutputStream(socket.getOutputStream());
        obOut.flush();
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
     */
    public void removeProduct(String code) throws IOException, ProductNotFoundException {
        out.println("REMOVEPRODUCT," + code);

        String input = in.readLine();

        if (input.equals("FAIL")) {
            throw new ProductNotFoundException(code);
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
     */
    public void purchaseProduct(String code) throws IOException, ProductNotFoundException, OutOfStockException {
        out.println("PURCHASE," + code);

        String input = in.readLine();

        if (input.equals("NOTFOUND")) {
            throw new ProductNotFoundException(code);
        } else if (input.equals("STOCK")) {
            throw new OutOfStockException(code);
        }
    }

    /**
     * Method to get a product from the server based on its product code.
     *
     * @param code the code to search for.
     * @return Product object that matches the code.
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the product was not found.
     */
    public Product getProduct(String code) throws IOException, ProductNotFoundException {
        try {
            out.println("GETPRODUCT," + code);

            Object o = obIn.readObject();

            if (o instanceof Product) {
                return (Product) o;
            } else if (o instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) o;
            }
        } catch (ClassNotFoundException ex) {
        }
        throw new ProductNotFoundException(code);
    }

    /**
     * Method to get a product by barcode.
     *
     * @param barcode the barcode to search for.
     * @return Product object that matches the barcode.
     * @throws IOException if there was an error connecting.
     * @throws ProductNotFoundException if the barcode was not found.
     */
    public Product getProductByBarCode(String barcode) throws IOException, ProductNotFoundException {
        try {
            out.println("GETPRODUCTBARCODE," + barcode);

            Object o = obIn.readObject();

            if (o instanceof Product) {
                return (Product) o;
            } else if (o instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) o;
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
     */
    public List<Product> getAllProducts() throws IOException {
        try {
            out.println("GETALLPRODUCTS");

            Object o = obIn.readObject();

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
     */
    public void removeCustomer(String id) throws IOException, CustomerNotFoundException {
        out.println("REMOVECUSTOMER," + id);

        String input = in.readLine();

        if (input.equals("FAIL")) {
            throw new CustomerNotFoundException(id);
        }
    }

    /**
     * Method to get a customer from the server.
     *
     * @param id the id of the customer to get.
     * @return Customer object that matches the id.
     * @throws IOException if there was an error connecting.
     * @throws CustomerNotFoundException if the customer could not be found.
     */
    public Customer getCustomer(String id) throws IOException, CustomerNotFoundException {
        try {
            out.println("GETCUSTOMER," + id);

            Object o = obIn.readObject();

            if (o instanceof Customer) {
                return (Customer) o;
            } else if (o instanceof CustomerNotFoundException) {
                throw (CustomerNotFoundException) o;
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
     */
    public List<Customer> getAllCustomers() throws IOException {
        try {
            out.println("GETALLCUSTOMERS");

            Object o = obIn.readObject();

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
    }

    /**
     * Method to close the connection to the server.
     */
    public void close() {
        out.println("CONNTERM");
        isConnected = false;
    }
}
