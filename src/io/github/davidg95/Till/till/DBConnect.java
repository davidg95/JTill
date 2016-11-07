/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import io.github.davidg95.Till.till.Staff.Position;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Database connection class which handles communication with the database.
 *
 * @author David
 */
public class DBConnect {

    private Connection con;

    private String address;
    private String username;
    private String password;

    private boolean connected;

    private ResultSet productSet;
    private ResultSet customerSet;
    private ResultSet staffSet;

    private final String all_products = "SELECT * FROM Products";
    private final String all_customers = "SELECT * FROM Customers";
    private final String all_staff = "SELECT * FROM Staff";

    private Statement products_stmt;
    private Statement customers_stmt;
    private Statement staff_stmt;

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
    
    public String getAddress(){
        return address;
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getPassword(){
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
        productSet = products_stmt.executeQuery(all_products);
        customerSet = customers_stmt.executeQuery(all_customers);
        staffSet = staff_stmt.executeQuery(all_staff);
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
            String code = productSet.getString(1);
            String barcode = productSet.getString(2);
            String name = productSet.getString(3);
            double price = productSet.getDouble(4);
            int stock = productSet.getInt(5);
            String comments = productSet.getString(6);

            Product p = new Product(name, comments, price, stock, barcode, code);

            products.add(p);
        }

        return products;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        while (customerSet.next()) {
            String id = customerSet.getString(1);
            String add = customerSet.getString(2);
            String name = customerSet.getString(3);
            String phone = customerSet.getString(4);

            Customer c = new Customer(name, add, id, phone);

            customers.add(c);
        }

        return customers;
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staff = new ArrayList<>();
        while (staffSet.next()) {
            String id = staffSet.getString(1);
            String name = staffSet.getString(2);
            String position = staffSet.getString(3);
            String uname = staffSet.getString(4);
            String pword = staffSet.getString(5);

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

    public void addProduct(Product p) throws SQLException {
        productSet.moveToInsertRow();

        productSet.updateString(1, p.getProductCode());
        productSet.updateString(2, p.getBarcode());
        productSet.updateString(3, p.getName());
        productSet.updateDouble(4, p.getPrice());
        productSet.updateInt(5, p.getStock());
        productSet.updateString(6, p.getComments());
        productSet.insertRow();
    }

    public void updateWholeProducts(List<Product> products) throws SQLException {
        productSet.beforeFirst();

        while (productSet.next()) {
            productSet.deleteRow();
        }

        for (Product p : products) {
            productSet.moveToInsertRow();
            productSet.updateString(1, p.getProductCode());
            productSet.updateString(2, p.getBarcode());
            productSet.updateString(3, p.getName());
            productSet.updateDouble(4, p.getPrice());
            productSet.updateInt(5, p.getStock());
            productSet.updateString(6, p.getComments());
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
            customerSet.updateString(1, c.getId());
            customerSet.updateString(2, c.getAddress());
            customerSet.updateString(3, c.getName());
            customerSet.updateString(4, c.getPhone());
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

    @Override
    public String toString() {
        return this.address;
    }
}
