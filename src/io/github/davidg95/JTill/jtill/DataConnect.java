/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 * Interface for modelling a class which deals with data connections
 *
 * @author David
 */
public abstract class DataConnect {

    private static DataConnect dataconnect;

    public GUIInterface g;

    public static DataConnect get() {
        return dataconnect;
    }

    public static void set(DataConnect dc) {
        dataconnect = dc;
    }

    /**
     * Set a reference to the GUI.
     *
     * @param g the GUI.
     */
    public void setGUI(GUIInterface g) {
        this.g = g;
    }

    /**
     * Get the GUI.
     *
     * @return the GUI.
     */
    public GUIInterface getGUI() {
        return g;
    }

    /**
     * Perform a database integrity check.
     *
     * @throws java.io.IOException if there was a network error.
     * @throws java.sql.SQLException if there was a database error.
     */
    public abstract void integrityCheck() throws IOException, SQLException;

    public abstract Object[] databaseInfo() throws IOException, SQLException;

    public abstract HashMap<String, Object> terminalInit(int id, String[] data) throws IOException;

    public abstract void initComplete() throws IOException;

    public abstract int getInits() throws IOException;

    /**
     * Send and assistance message to the server.
     *
     * @param message the message to send.
     * @throws IOException if there was an error sending the message.
     */
    public abstract void assisstance(String message) throws IOException;

    /**
     * Send an email to the report email set in the server.
     *
     * @param message the message to send.
     * @throws IOException if there was a network error.
     */
    public abstract void sendEmail(String message) throws IOException;

    /**
     * Email a receipt to a customer.
     *
     * @param email the email to send the receipt tos.
     * @param sale the sale to generate the receipt from.
     * @return true if the email was successful, false if it was not.
     * @throws IOException if there was a network error.
     * @throws javax.mail.internet.AddressException if there was any errors
     * sending the email.
     */
    public abstract boolean emailReceipt(String email, Sale sale) throws IOException, AddressException, MessagingException;

    /**
     * Set a setting.
     *
     * @param key the setting to set.
     * @param value the value for the setting.
     * @throws IOException if there was an error.
     */
    public abstract void setSetting(String key, String value) throws IOException;

    /**
     * Get a setting.
     *
     * @param key the setting to get.
     * @return the value of the setting.
     * @throws IOException if there was an error.
     */
    public abstract String getSetting(String key) throws IOException;

    /**
     * Get a setting. Set it if it does not exist.
     *
     * @param key the setting to get.
     * @param value the value to set if it does not exists.
     * @return the setting value if it does exist.
     * @throws IOException if there was an error.
     */
    public abstract String getSetting(String key, String value) throws IOException;

    //Product
    /**
     * Add a new product to the system.
     *
     * @param p the product to add.
     * @return the product that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract Product addProduct(Product p) throws IOException, SQLException;

    /**
     * Remove a product from the system
     *
     * @param barcode the barcode of the product to remove.
     * @throws ProductNotFoundException if the product was not found.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract void removeProduct(String barcode) throws ProductNotFoundException, IOException, SQLException;

    /**
     * Remove a product from the system
     *
     * @param p the product to remove.
     * @throws ProductNotFoundException if the product was not found.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract void removeProduct(Product p) throws IOException, ProductNotFoundException, SQLException;

    /**
     * Method to purchase a product and reduce its stock level by 1.
     *
     * @param barcode the product to purchase.
     * @param amount the amount of the product to purchase.
     * @return the new stock level.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error purchasing the product.
     * @throws OutOfStockException if the product is out of stock.
     * @throws ProductNotFoundException if the product was not found.
     */
    public abstract int purchaseProduct(String barcode, int amount) throws IOException, ProductNotFoundException, OutOfStockException, SQLException;

    /**
     * Method to get a product by its code.
     *
     * @param barcode the barcode of the product to search.
     * @return the Product that matches the code.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error getting the product.
     * @throws ProductNotFoundException if the product could not be found.
     */
    public abstract Product getProduct(String barcode) throws IOException, ProductNotFoundException, SQLException;

    /**
     * Method to get all the products on the system.
     *
     * @return a List of all products.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Product> getAllProducts() throws IOException, SQLException;

    /**
     * Method to update a product.
     *
     * @param p the product to update.
     * @return the product after getting updated.
     * @throws IOException if there was a network error.
     * @throws ProductNotFoundException if the product could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract Product updateProduct(Product p) throws IOException, ProductNotFoundException, SQLException;

    /**
     * Method to check if a barcode is already in use.
     *
     * @param barcode the barcode to check
     * @return true if it is getting used, false otherwise.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract boolean checkBarcode(String barcode) throws IOException, SQLException;

    /**
     * Method to get a product by its barcode.
     *
     * @param barcode the barcode to search.
     * @return the product that matches the barcode.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error getting the product.
     * @throws ProductNotFoundException if the product could not be found.
     */
    public abstract Product getProductByBarcode(String barcode) throws IOException, ProductNotFoundException, SQLException;

    /**
     * Method to search for a product.
     *
     * @param terms the search terms.
     * @return a List of products matching the terms.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error getting the product.
     */
    public abstract List<Product> productLookup(String terms) throws IOException, SQLException;

    //Customer
    /**
     * Method to add a customer to the system.
     *
     * @param customer the customer to add.
     * @return the customer that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract Customer addCustomer(Customer customer) throws IOException, SQLException;

    /**
     * Method to remove a customer from the system.
     *
     * @param id the id of the customer to remove.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract void removeCustomer(int id) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to remove a customer from the system.
     *
     * @param c the customer to remove.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if the customer could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract void removeCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to get a customer from the system.
     *
     * @param id the id of the customer to get.
     * @return the customer that matches the id.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if the customer could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract Customer getCustomer(int id) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to search for customers with the given name.
     *
     * @param name the name to search for.
     * @return a List of call customers matching the name.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if no customer could be found.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Customer> getCustomerByName(String name) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to get a list of all the customers in the system.
     *
     * @return a List of all the customers in the system.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Customer> getAllCustomers() throws IOException, SQLException;

    /**
     * Method to update a customer in the system.
     *
     * @param c the customer to update.
     * @return the customer that was updated.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if the customer could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract Customer updateCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to search for customers that have details matching the given
     * terms.
     *
     * @param terms the terms to search.
     * @return a List of customers matching the terms.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Customer> customerLookup(String terms) throws IOException, SQLException;

    //Sale
    /**
     * Method to add a sale to the system.
     *
     * @param s the sale to add.
     * @return the sale that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract Sale addSale(Sale s) throws IOException, SQLException;

    /**
     * Method to get all the sales in the system.
     *
     * @return a List of all the sales in the system.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Sale> getAllSales() throws IOException, SQLException;

    /**
     * Method to get a sale in the system.
     *
     * @param id the id of the sale to get.
     * @return the sale matching the id.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the sale could not be found.
     */
    public abstract Sale getSale(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get all sales within the specified date range.
     *
     * @param start the start date.
     * @param end the end date.
     * @return a List of all sales within the range.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Sale> getSalesInRange(Date start, Date end) throws IOException, SQLException;

    /**
     * Method to update a sale in the database.
     *
     * @param s the sale to update.
     * @return the sale that was updated.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the sale could not be found.
     */
    public abstract Sale updateSale(Sale s) throws IOException, SQLException, JTillException;

    /**
     * Method to suspend a sale. This will add the sale to a list of suspended
     * sales that can then be continues from another terminal.
     *
     * @param sale the sale to suspend.
     * @param staff the staff member the sales belongs to.
     * @throws IOException if there was a network error.
     */
    public abstract void suspendSale(Sale sale, Staff staff) throws IOException;

    /**
     * Method to resume a sale. This method will remove the sale from the list
     * of suspended sales.
     *
     * @param s the member of staff who wants to retrieve a sale.
     * @return the sale that was suspended.
     * @throws IOException if there was a network error.
     */
    public abstract Sale resumeSale(Staff s) throws IOException;

    /**
     * Method to get all uncashed sales from a given terminal with name t.
     *
     * @param t the name of the terminal to get the sales for.
     * @return a List of all the sales which have not been cashed.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Sale> getUncashedSales(String t) throws IOException, SQLException;

    //Sale Items
    /**
     * Add a new sale item.
     *
     * @param s the sale the item is from.
     * @param i the sale item to add.
     * @return the sale item with its ID assigned.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract SaleItem addSaleItem(Sale s, SaleItem i) throws IOException, SQLException;

    /**
     * Remove a sale item.
     *
     * @param id the id of the item to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the item was not found.
     */
    public abstract void removeSaleItem(int id) throws IOException, SQLException, JTillException;

    /**
     * Get a sale item.
     *
     * @param id the sale item to get.
     * @return the sale item.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the item was not found.
     */
    public abstract SaleItem getSaleItem(int id) throws IOException, SQLException, JTillException;

    /**
     * Get all sale items.
     *
     * @return a List of all the sale items.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<SaleItem> getAllSaleItems() throws IOException, SQLException;

    /**
     * Method to get all ale items using the given search terms, nulls are
     * allowed.
     *
     * @param depId the department id.
     * @param catId the category id.
     * @param start the start date.
     * @param end the end date.
     * @return a list of sale items matching the search terms.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<SaleItem> getSaleItemsSearchTerms(int depId, int catId, Date start, Date end) throws IOException, SQLException;

    /**
     * Method which gets the total units sold of a particular item.
     *
     * @param barcode the barcode of the product to search.
     * @return the total number of units of that item sold.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract int getTotalSoldOfItem(String barcode) throws IOException, SQLException;

    /**
     * Method which gets the total value sold of a particular item.
     *
     * @param barcode the barcode of the product to search.
     * @return the amount of money taken selling this item.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract BigDecimal getTotalValueSold(String barcode) throws IOException, SQLException;

    /**
     * Method to submit a query to the sale items table.
     *
     * @param query the WHERE clause.
     * @return a List.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<SaleItem> submitSaleItemQuery(String query) throws IOException, SQLException;

    /**
     * Update a sale item.
     *
     * @param i the item to update.
     * @return the item after being updated.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the item was not found.
     */
    public abstract SaleItem updateSaleItem(SaleItem i) throws IOException, SQLException, JTillException;

    //Staff
    /**
     * Method to add a member of staff.
     *
     * @param s the member of staff to add.
     * @return the member of staff that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract Staff addStaff(Staff s) throws IOException, SQLException;

    /**
     * Method to remove a member of staff from the system.
     *
     * @param id the id of the staff to remove.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract void removeStaff(int id) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to remove a member of staff from the system.
     *
     * @param s the member of staff to remove.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract void removeStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to get a member of staff from the system.
     *
     * @param id the member of staff to get.
     * @return the member of staff matching the id.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract Staff getStaff(int id) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to get all staff in the system.
     *
     * @return a List of all the staff in the system.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Staff> getAllStaff() throws IOException, SQLException;

    /**
     * Method to get the amount of staff in the system
     *
     * @return int value representing the amount of staff.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract int getStaffCount() throws IOException, SQLException;

    /**
     * Method to update a member of staff.
     *
     * @param s the member of staff to update.
     * @return the member of staff that was updated.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract Staff updateStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to check if a username is already in use. Case is ignored.
     *
     * @param username the username to check.
     * @return true if it is in use, false otherwise.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract boolean checkUsername(String username) throws IOException, SQLException;

    /**
     * Method to lo a member of staff into the system using the username and
     * password. This should not be used for logging into th terminals.
     *
     * @param username the username of the staff member.
     * @param password the password of the staff member.
     * @return the member of staff that matches the details.
     * @throws IOException if there was a network error.
     * @throws LoginException if there was an error logging in. This can be
     * because of incorrect username or password.
     * @throws SQLException if there was a database error.
     */
    public abstract Staff login(String username, String password) throws IOException, LoginException, SQLException;

    /**
     * Method to log a member of staff into a till. This method only allows a
     * member of staff to be logged in once at a time.
     *
     * @param id the id of the member of staff to log in.
     * @return the member of staff that logged in.
     * @throws IOException if there was a network error.
     * @throws LoginException if there was an error logging in. This could be
     * because of an incorrect id or they might already be logged in else where.
     * @throws SQLException if there was a database error.
     */
    public abstract Staff tillLogin(int id) throws IOException, LoginException, SQLException;

    /**
     * Method to check if a member of staff is logged into a till.
     *
     * @param s the staff member to check.
     * @return boolean indicating whether they are logged in or not.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the staff member could not be found.
     * @throws SQLException if there was a database error.
     */
    public abstract boolean isTillLoggedIn(Staff s) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to log a member of staff out of the system. this should not be
     * used for logging out of the tills.
     *
     * @param s the member of staff logging out.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    public abstract void logout(Staff s) throws IOException, StaffNotFoundException;

    /**
     * Method to log a member of staff out of the tills.
     *
     * @param s the member of staff to log out.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    public abstract void tillLogout(Staff s) throws IOException, StaffNotFoundException;

    /**
     * Method to get the sales for a member of staff.
     *
     * @param s the member of staff to get sales for.
     * @return the list of sales.
     * @throws IOException if there was a network error.
     * @throws java.sql.SQLException if there is a database error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    public abstract List<Sale> getStaffSales(Staff s) throws IOException, SQLException, StaffNotFoundException;

    //Category
    /**
     * Method to add a category to the system.
     *
     * @param c the category to add.
     * @return the category that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract Category addCategory(Category c) throws IOException, SQLException;

    /**
     * Method to update a category in the system.
     *
     * @param c the category to update.
     * @return the category that was updated.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the category could not be found.
     */
    public abstract Category updateCategory(Category c) throws IOException, SQLException, JTillException;

    /**
     * Method to remove a category from the system.
     *
     * @param c the category to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the category could not be found.
     */
    public abstract void removeCategory(Category c) throws IOException, SQLException, JTillException;

    /**
     * Method to remove a category from the system.
     *
     * @param id the id of the category to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the category could not be found.
     */
    public abstract void removeCategory(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get a category from the system.
     *
     * @param id the id of the category to get.
     * @return the category that matches the id.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the category could not be found.
     */
    public abstract Category getCategory(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get all the categories in the system.
     *
     * @return a List of all the categories.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Category> getAllCategorys() throws IOException, SQLException;

    /**
     * Method to get all the products in a specific category.
     *
     * @param id the id of the category to get the products for.
     * @return a List of all the products in the category.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the category could not be found.
     */
    public abstract List<Product> getProductsInCategory(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get all the products in a department.
     *
     * @param id the department to search.
     * @return the list of products.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Product> getProductsInDepartment(int id) throws IOException, SQLException;

    //Department
    /**
     * Add a new department to the system.
     *
     * @param d the department to add.
     * @return the department that was added with the id that was assigned.
     * @throws IOException if there was a network error
     * @throws SQLException if there was a database error.
     */
    public abstract Department addDepartment(Department d) throws IOException, SQLException;

    /**
     * Remove a department from the system.
     *
     * @param id the id of the department to remove.
     * @throws IOException if there was a network error
     * @throws SQLException if there was a database error.
     * @throws JTillException if the department was not found.
     */
    public abstract void removeDepartment(int id) throws IOException, SQLException, JTillException;

    /**
     * Get a department from the system.
     *
     * @param id the id of the department to get.
     * @return the department that matches the id.
     * @throws IOException if there was a network error
     * @throws SQLException if there was a database error.
     * @throws JTillException if the department was not found.
     */
    public abstract Department getDepartment(int id) throws IOException, SQLException, JTillException;

    /**
     * Gets all the department on the system.
     *
     * @return a list of all the departments.
     * @throws IOException if there was a network error
     * @throws SQLException if there was a database error.
     */
    public abstract List<Department> getAllDepartments() throws IOException, SQLException;

    /**
     * Update a department on the system.
     *
     * @param d the department to update.
     * @return the department that was updated.
     * @throws IOException if there was a network error
     * @throws SQLException if there was a database error.
     * @throws JTillException if the department was not found.
     */
    public abstract Department updateDepartment(Department d) throws IOException, SQLException, JTillException;

    //Discount
    /**
     * Method to add a discount to the system.
     *
     * @param d the discount to add.
     * @return the discount with its id assigned from the database.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract Discount addDiscount(Discount d) throws IOException, SQLException;

    /**
     * Method to update a discount in the system.
     *
     * @param d the discount to update.
     * @return the updated discount.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws DiscountNotFoundException if the discount was not found.
     */
    public abstract Discount updateDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException;

    /**
     * Method to remove a discount from the system.
     *
     * @param d the discount to remove
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws DiscountNotFoundException if the discount was not found.
     */
    public abstract void removeDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException;

    /**
     * Method to remove a discount from the system.
     *
     * @param id the discount id to remove
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws DiscountNotFoundException if the discount was not found.
     */
    public abstract void removeDiscount(int id) throws IOException, SQLException, DiscountNotFoundException;

    /**
     * Method to get a discount from the system.
     *
     * @param id the discount to get.
     * @return the discount.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws DiscountNotFoundException if the discount was not found.
     */
    public abstract Discount getDiscount(int id) throws IOException, SQLException, DiscountNotFoundException;

    /**
     * Method to get all the discounts from the system.
     *
     * @return a list of all the discounts.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Discount> getAllDiscounts() throws IOException, SQLException;

    /**
     * Method to get all current valid discounts.
     *
     * @return list of valid discounts
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Discount> getValidDiscounts() throws IOException, SQLException;

    //Tax
    /**
     * Add a new tax to the system.
     *
     * @param t the new tax to add.
     * @return the tax with its ID assigned.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract Tax addTax(Tax t) throws IOException, SQLException;

    /**
     * Method to remove a tax from the system.
     *
     * @param t the tax to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the tax was not found.
     */
    public abstract void removeTax(Tax t) throws IOException, SQLException, JTillException;

    /**
     * Method to remove a tax from the system given its ID.
     *
     * @param id the ID of the tax to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the tax was not found.
     */
    public abstract void removeTax(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get a tax from the system.
     *
     * @param id the ID of the tax to get.
     * @return the Tax.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the tax was not found.
     */
    public abstract Tax getTax(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to update a tax on the system
     *
     * @param t the Tax to update.
     * @return the tax after getting updated.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the tax was not found.
     */
    public abstract Tax updateTax(Tax t) throws IOException, SQLException, JTillException;

    /**
     * Method to get a list of all the tax on the system.
     *
     * @return a List of type Tax.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Tax> getAllTax() throws IOException, SQLException;

    /**
     * Method to get all the products that belong to a specific tax.
     *
     * @param id the id of the tax to search.
     * @return a List of type Product.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the Tax was not found.
     */
    public abstract List<Product> getProductsInTax(int id) throws IOException, SQLException, JTillException;

    //Screens
    /**
     * Method to add a new till screen.
     *
     * @param s the new screen to add.
     * @return the screen after being added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract Screen addScreen(Screen s) throws IOException, SQLException;

    /**
     * Method to add a new screen button.
     *
     * @param b the button to add.
     * @return the button after it was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract TillButton addButton(TillButton b) throws IOException, SQLException;

    /**
     * Method to remove a screen from the database. This will also remove any
     * buttons on that screen from the database as well.
     *
     * @param s the screen to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws ScreenNotFoundException if the screen was not found.
     */
    public abstract void removeScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException;

    /**
     * Method to remove a button from the database.
     *
     * @param b the button to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the button was not found.
     */
    public abstract void removeButton(TillButton b) throws IOException, SQLException, JTillException;

    /**
     * Method to get a screen from the server.
     *
     * @param s the ID of the screen to get.
     * @return the Screen that matches the ID.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws ScreenNotFoundException if the screen was not found.
     */
    public abstract Screen getScreen(int s) throws IOException, SQLException, ScreenNotFoundException;

    /**
     * Method to get a button from the server.
     *
     * @param b the ID of the button to get.
     * @return the Button that matches the ID.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the button was not found.
     */
    public abstract TillButton getButton(int b) throws IOException, SQLException, JTillException;

    /**
     * Method to update a screen on the server.
     *
     * @param s the screen to update.
     * @return the screen after being updated.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws ScreenNotFoundException if the screen was not found.
     */
    public abstract Screen updateScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException;

    /**
     * Method to update a button on the server.
     *
     * @param b the button to update.
     * @return the button after being updated.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the button was not found.
     */
    public abstract TillButton updateButton(TillButton b) throws IOException, SQLException, JTillException;

    /**
     * Method to get all screens on the database.
     *
     * @return a List of type Screen.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Screen> getAllScreens() throws IOException, SQLException;

    /**
     * Method to get all the buttons on the database.
     *
     * @return a List of type Button.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<TillButton> getAllButtons() throws IOException, SQLException;

    /**
     * Method to get all buttons for a certain screen.
     *
     * @param s the screen to get buttons for.
     * @return a List of type TillButton.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws ScreenNotFoundException if the screen could not be found.
     */
    public abstract List<TillButton> getButtonsOnScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException;

    /**
     * gets screens which inherit this screen.
     *
     * @param s the screen to check.
     * @return a list of the child screens.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws JTillException if there is an error.
     */
    public abstract List<Screen> checkInheritance(Screen s) throws IOException, SQLException, JTillException;

    //Tills
    /**
     * Method to add a new till to the system.
     *
     * @param t the new Till to add.
     * @return the till with its ID assigned.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract Till addTill(Till t) throws IOException, SQLException;

    /**
     * Method to remove a till from the server.
     *
     * @param id the ID of the till to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the till was not found.
     */
    public abstract void removeTill(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get a till from the server.
     *
     * @param id the ID of the till to get.
     * @return the till that matches the ID.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the till was not found.
     */
    public abstract Till getTill(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to update a till.
     *
     * @param t the till to update.
     * @return the updated till.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if there was an error updating the till.
     */
    public abstract Till updateTill(Till t) throws IOException, SQLException, JTillException;

    /**
     * Method to get all tills on the server.
     *
     * @return a List of type Till.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Till> getAllTills() throws IOException, SQLException;

    /**
     * Check if a till is connected.
     *
     * @param id the till id.
     * @return true or false indicating if it is connected.
     * @throws IOException if there is a network error.
     */
    public abstract boolean isTillConnected(int id) throws IOException;

    /**
     * Method to connect a till.
     *
     * @param name the name of the till to connect.
     * @param uuid the uuid of the till to connect.
     * @param staff the logged in staff member, can be null.
     * @return the Till object.
     * @throws IOException if there was a network error.
     * @throws JTillException if the till is already connected.
     */
    public abstract Till connectTill(String name, UUID uuid, Staff staff) throws IOException, JTillException;

    /**
     * Method to get all current connected tills.
     *
     * @return a List of type Till.
     * @throws IOException if there was a network error.
     */
    public abstract List<Till> getConnectedTills() throws IOException;

    /**
     * Method to get uncashed till sales.
     *
     * @param id the id of the till to get takings for.
     * @return a List of uncashed sales.
     * @throws IOException if there was a network error.
     * @throws java.sql.SQLException of there is a database error.
     * @throws JTillException if there was another error.
     */
    public abstract List<Sale> getUncachedTillSales(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to check if a till name is being used.
     *
     * @param name the name to check.
     * @return true if it is getting used, false if it is not.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract boolean isTillNameUsed(String name) throws IOException, SQLException;

    //Waste Report
    /**
     * Method to add a new waste report to the system.
     *
     * @param items the items to add.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if there was an error with the report.
     */
    public abstract void addWasteReport(List<WasteItem> items) throws IOException, SQLException, JTillException;

    //Waste Item
    /**
     * Method to add a waste item to the system.
     *
     * @param wi the waste item to add.
     * @return the waste item with an ID assigned.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if there was an error with the waste item.
     */
    public abstract WasteItem addWasteItem(WasteItem wi) throws IOException, SQLException, JTillException;

    /**
     * Method to remove a waste item from the system.
     *
     * @param id the waste item to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the waste item could not be found.
     */
    public abstract void removeWasteItem(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get a waste item from the system.
     *
     * @param id the waste item to get.
     * @return the waste item.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the waste item could not be found.
     */
    public abstract WasteItem getWasteItem(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get all waste items.
     *
     * @return list of all waste items.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<WasteItem> getAllWasteItems() throws IOException, SQLException;

    /**
     * Method to get the total units wasted of a certain product.
     *
     * @param barcode the barcode to search.
     * @return the total units wasted.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract int getTotalWastedOfItem(String barcode) throws IOException, SQLException;

    /**
     * Method to get the total value wasted of a certain product.
     *
     * @param barcode the barcode to search.
     * @return the value wasted.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public abstract BigDecimal getValueWastedOfItem(String barcode) throws IOException, SQLException;

    /**
     * Method to add a waste reason.
     *
     * @param wr the WasteReason to add.
     * @return the WasteReason that was added with its ID assigned by the
     * database.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract WasteReason addWasteReason(WasteReason wr) throws IOException, SQLException;

    /**
     * Method to remove a waste reason.
     *
     * @param id the waste reason to remove.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the waste reason was not found.
     */
    public abstract void removeWasteReason(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get a waste reason.
     *
     * @param id the waste reason to get.
     * @return the WasteReason.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the waste reason was not found.
     */
    public abstract WasteReason getWasteReason(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get all WasteReasons.
     *
     * @return a List of all the WasteReasons.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<WasteReason> getAllWasteReasons() throws IOException, SQLException;

    /**
     * Method to update a WasteReason.
     *
     * @param wr the WasteReason.
     * @return the WasteReason after being updated.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the waste reason was not found.
     */
    public abstract WasteReason updateWasteReason(WasteReason wr) throws IOException, SQLException, JTillException;

    /**
     * Method to add a Supplier.
     *
     * @param s the Supplier to add.
     * @return the Supplier after being added.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract Supplier addSupplier(Supplier s) throws IOException, SQLException;

    /**
     * Method to remove a Supplier.
     *
     * @param id the supplier to remove.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the Supplier was not found.
     */
    public abstract void removeSupplier(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get a Supplier.
     *
     * @param id the supplier to get.
     * @return the Supplier.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the Supplier was not found.
     */
    public abstract Supplier getSupplier(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to get all suppliers.
     *
     * @return a List of the Suppliers.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Supplier> getAllSuppliers() throws IOException, SQLException;

    /**
     * Method to update a Supplier.
     *
     * @param s the Supplier to update.
     * @return the Supplier after being updated.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the Supplier was not found.
     */
    public abstract Supplier updateSupplier(Supplier s) throws IOException, SQLException, JTillException;

    /**
     * Method to add a received item to the database.
     *
     * @param i the item to add.
     * @param report the report id.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract void addReceivedItem(ReceivedItem i, int report) throws IOException, SQLException;

    /**
     * Gets the total spend on an item.
     *
     * @param barcode the item to get.
     * @return the total spent on the item.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract BigDecimal getValueSpentOnItem(String barcode) throws IOException, SQLException;

    /**
     * Get the total amount received of this item.
     *
     * @param barcode the item ID.
     * @return the total amount received.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract int getTotalReceivedOfItem(String barcode) throws IOException, SQLException;

    /**
     * Clocks on a member of staff.
     *
     * @param id the id to clock on.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws StaffNotFoundException if the member of staff was not found.
     */
    public abstract void clockOn(int id) throws IOException, SQLException, StaffNotFoundException;

    /**
     * Clocks off a member of staff.
     *
     * @param id the id to clock off.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws StaffNotFoundException if the member of staff was not found.
     */
    public abstract void clockOff(int id) throws IOException, SQLException, StaffNotFoundException;

    /**
     * Method to get all clock items for a staff member.
     *
     * @param id the staff member to get.
     * @return a list of all clock items.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws StaffNotFoundException if the member of staff was not found.
     */
    public abstract List<ClockItem> getAllClocks(int id) throws IOException, SQLException, StaffNotFoundException;

    /**
     * Method to clear the clock entries for a member of staff.
     *
     * @param id the staff member to clear.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws StaffNotFoundException if the member of staff was not found.
     */
    public abstract void clearClocks(int id) throws IOException, SQLException, StaffNotFoundException;

    /**
     * Method to add a new trigger.
     *
     * @param t the trigger to add.
     * @return the trigger that was added.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract Trigger addTrigger(Trigger t) throws IOException, SQLException;

    /**
     * Method to get a list of all a discounts buckets.
     *
     * @param id the discount id to get the buckets for.
     * @return a List of all the buckets.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws DiscountNotFoundException if the discount was not found.
     */
    public abstract List<DiscountBucket> getDiscountBuckets(int id) throws IOException, SQLException, DiscountNotFoundException;

    /**
     * Method to remove a trigger.
     *
     * @param id the trigger to remove.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the trigger was not found.
     */
    public abstract void removeTrigger(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to update a trigger.
     *
     * @param t the trigger.
     * @return thr trigger.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the trigger was not found.
     */
    public abstract Trigger updateTrigger(Trigger t) throws IOException, SQLException, JTillException;

    /**
     * Method to add a new DiscountBucket.
     *
     * @param b the bucket to add.
     * @return the discount bucket.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract DiscountBucket addBucket(DiscountBucket b) throws IOException, SQLException;

    /**
     * Method to remove a discount bucket.
     *
     * @param id the bucket to remove.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the bucket was not found.
     */
    public abstract void removeBucket(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to update a discount bucket.
     *
     * @param b the bucket to updated.
     * @return the bucket.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the bucket was not found.
     */
    public abstract DiscountBucket updateBucket(DiscountBucket b) throws IOException, SQLException, JTillException;

    /**
     * Method to get all the triggers for a bucket.
     *
     * @param id the bucket to get.
     * @return a list of all the triggers.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if the bucket was not found.
     */
    public abstract List<Trigger> getBucketTriggers(int id) throws IOException, SQLException, JTillException;

    /**
     * Method to search SaleItems.
     *
     * @param department the department to include.
     * @param category the category to include.
     * @param start the start date.
     * @param end the end date.
     * @return list of SaleItems.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     * @throws JTillException if there was an error in the search.
     */
    public abstract List<SaleItem> searchSaleItems(int department, int category, Date start, Date end) throws IOException, SQLException, JTillException;

    /**
     * Method to get a terminals sales.
     *
     * @param start the start date.
     * @param end the end date.
     * @param terminal the terminal to get sales for. 0 for all.
     * @param uncashedOnly true if you only want uncashed sales, false for all.
     * @return a List of the terminals sales.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Sale> getTerminalSales(Date start, Date end, int terminal, boolean uncashedOnly) throws IOException, SQLException;

    /**
     * Method to get all of a terminals sales.
     *
     * @param terminal the terminal to get sales for.
     * @param uncashedOnly true if you only want uncashed sales, false for all.
     * @return a List of the terminals sales.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Sale> getAllTerminalSales(int terminal, boolean uncashedOnly) throws IOException, SQLException;

    /**
     * Method to search the Products table with a customer WHERE clause.
     *
     * @param WHERE the WHERE clause for the database query.
     * @return a List of Products.
     * @throws IOException if there was a networking error.
     * @throws SQLException if there was a database error.
     */
    public abstract List<Product> getProductsAdvanced(String WHERE) throws IOException, SQLException;

    /**
     * Get the login screen background image.
     *
     * @return the JavaFX image.
     * @throws IOException if there was an error.
     */
    public abstract File getLoginBackground() throws IOException;

    /**
     * Method to reinitialise all tills.
     *
     * @throws IOException if there was an error.
     * @throws JTillException if there are active inits.
     */
    public abstract void reinitialiseAllTills() throws IOException, JTillException;

    /**
     * Clear all sales data to date.
     *
     * @return the amount of records deleted.
     * @throws IOException if there is an error.
     * @throws java.sql.SQLException if there is a database error.
     */
    public abstract int clearSalesData() throws IOException, SQLException;

    /**
     * Purges the database, removing all sales, received and wastage data.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract void purgeDatabase() throws IOException, SQLException;

    /**
     * Add a ReceivedReport to the system.
     *
     * @param rep the report.
     * @throws IOException if there is an error.
     * @throws java.sql.SQLException if there is a database error.
     */
    public abstract void addReceivedReport(ReceivedReport rep) throws IOException, SQLException;

    /**
     * Get all received reports.
     *
     * @return List of ReceivedReports.
     * @throws IOException if there is an error.
     * @throws java.sql.SQLException if there is a database error.
     */
    public abstract List<ReceivedReport> getAllReceivedReports() throws IOException, SQLException;

    /**
     * Updates a ReceivedReport.
     *
     * @param rr the ReceivedReport to update
     * @return the updated ReceivedReport.
     * @throws IOException if there is an error.
     * @throws java.sql.SQLException if there is a database error.
     */
    public abstract ReceivedReport updateReceivedReport(ReceivedReport rr) throws IOException, SQLException;

    /**
     * Send data to a till.
     *
     * @param id the id of the till.
     * @param data the data to request.
     * @throws IOException if there is an error.
     */
    public abstract void sendData(int id, String[] data) throws IOException;

    /**
     * Send build updates to tills.
     *
     * @throws IOException if there is an error.
     * @throws java.sql.SQLException if there is a database error.
     */
    public abstract void sendBuildUpdates() throws IOException, SQLException;

    /**
     * Download terminal update.
     *
     * @return the update file.
     * @throws IOException if there is an error.
     * @throws java.sql.SQLException if there is a database error.
     */
    public abstract byte[] downloadTerminalUpdate() throws Exception;

    /**
     * Log a staff member our of the till.
     *
     * @param id the id of the till.
     * @throws IOException if there is a network error.
     * @throws JTillException if there is an error.
     */
    public abstract void logoutTill(int id) throws IOException, JTillException;

    /**
     * Gets the member of staff if any logged on to the till.
     *
     * @param id the till id.
     * @return the Staff member, can be null.
     * @throws IOException if there is a network error.
     * @throws JTillException if there is an error.
     */
    public abstract Staff getTillStaff(int id) throws IOException, JTillException;

    /**
     * Get all sales for the current session. This will reset the readings.
     *
     * @param terminal the terminal to take a report for.
     * @param declared the declared takings.
     * @param staff the user who completed the report.
     * @return the report.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws JTillException if there is an error.
     */
    public abstract TillReport zReport(Till terminal, BigDecimal declared, Staff staff) throws IOException, SQLException, JTillException;

    /**
     * Get all sales for the current session.
     *
     * @param terminal the terminal to take a report for.
     * @param declared the declared takings.
     * @param staff the user who completed the report.
     * @return the report.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws JTillException if there is an error.
     */
    public abstract TillReport xReport(Till terminal, BigDecimal declared, Staff staff) throws IOException, SQLException, JTillException;

    /**
     * Method to remove cashed sales from the database.
     *
     * @return the number of sales removed.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract int removeCashedSales() throws IOException, SQLException;

    /**
     * Method to get till declaration reports.
     *
     * @param terminal the terminal to get reports from, -1 for all.
     * @return a list of declaration reports.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract List<TillReport> getDeclarationReports(int terminal) throws IOException, SQLException;

    /**
     * Method to remove a declaration report.
     *
     * @param id the report to remove.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract void removeDeclarationReport(int id) throws IOException, SQLException;

    /**
     * Consolidated report.
     *
     * @param start the start time.
     * @param end the end time.
     * @param t the terminal or -1 for all.
     * @return list of sales.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract List<Sale> consolidated(Date start, Date end, int t) throws IOException, SQLException;

    /**
     * Get total refunds for a terminal within a date range.
     *
     * @param start the start date.
     * @param end the end date.
     * @param t the terminal, -1 for all.
     * @return the total value of refunds.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract BigDecimal getRefunds(Date start, Date end, int t) throws IOException, SQLException;

    /**
     * Get the total wastage value.
     *
     * @param start the start period.
     * @param end the end period.
     * @return the value of the wastage.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract BigDecimal getWastage(Date start, Date end) throws IOException, SQLException;

    /**
     * Submit a stock take report.
     *
     * @param products the products to update.
     * @param zeroRest true if products not in the list should be set to zero,
     * false if they should be left.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract void submitStockTake(List<Product> products, boolean zeroRest) throws IOException, SQLException;

    /**
     * Get all categories in a department.
     *
     * @param department the department to search.
     * @return a list of categories.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract List<Category> getCategoriesInDepartment(int department) throws IOException, SQLException;

    /**
     * Adds a new condiments.
     *
     * @param c the condiment.
     * @return the condiment.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract Condiment addCondiment(Condiment c) throws IOException, SQLException;

    /**
     * Get a products condiments.
     *
     * @param barcode the barcode.
     * @return a list of condiments.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract List<Condiment> getProductsCondiments(String barcode) throws IOException, SQLException;

    /**
     * Updates a condiment.
     *
     * @param c the condiment to update.
     * @return the updated condiment.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws io.github.davidg95.JTill.jtill.JTillException if the condiment
     * was not found.
     */
    public abstract Condiment updateCondiment(Condiment c) throws IOException, SQLException, JTillException;

    /**
     * Removes a condiment.
     *
     * @param id the condiment to remove.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract void removeCondiment(int id) throws IOException, SQLException;

    /**
     * Get sales by department.
     *
     * @param id the department id.
     * @return the List of sale items.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract List<SaleItem> getSalesByDepartment(int id) throws IOException, SQLException;

    /**
     * Add an order to the database. This does NOT send the order.
     *
     * @param o the order.
     * @return the order with its ID.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract Order addOrder(Order o) throws IOException, SQLException;

    /**
     * Update an order.
     *
     * @param o the order to update.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws io.github.davidg95.JTill.jtill.JTillException if the order was
     * not found.
     */
    public abstract void updateOrder(Order o) throws IOException, SQLException, JTillException;

    /**
     * Get all orders.
     *
     * @return all orders.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract List<Order> getAllOrders() throws IOException, SQLException;

    /**
     * Remove an order.
     *
     * @param id the order id.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract void deleteOrder(int id) throws IOException, SQLException;

    /**
     * Perform a backup of the database on the server.
     *
     * @return the filepath of the backup.
     * @throws IOException if there was a file error.
     */
    public abstract String performBackup() throws IOException;

    /**
     * Get a list of backups that have been taken.
     *
     * @return the list of backups.
     * @throws IOException if there is a file error.
     */
    public abstract List<String> getBackupList() throws IOException;

    /**
     * Method to delete a backup.
     *
     * @param name the backup to delete, pass in null to delete all.
     * @throws IOException if there is a file error.
     */
    public abstract void clearBackup(String name) throws IOException;

    /**
     * Get license info.
     *
     * @return the license info.
     * @throws IOException if there is a network error.
     */
    public abstract Object[] getLicenseInfo() throws IOException;

    /**
     * Submit an SQL statement to the database.
     *
     * @param SQL the SQl statement to submit.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is an SQL error.
     */
    public abstract void submitSQL(String SQL) throws IOException, SQLException;

    /**
     * Marks a waste reason as deleted. this waste reason will still exist in
     * the database and in report, but cannot be used.
     *
     * @param wr the waste reason.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws io.github.davidg95.JTill.jtill.JTillException if the waste reason
     * was not found.
     */
    public abstract void deleteWasteReason(WasteReason wr) throws IOException, SQLException, JTillException;

    /**
     * Get all used waste reasons, this return waste reasons that have not been
     * marked as deleted.
     *
     * @return a List of type WasteReason.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract List<WasteReason> getUsedWasteReasons() throws IOException, SQLException;

    /**
     * Add a new refund reason.
     *
     * @param r the refund reason to add.
     * @return the added refund reason.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract RefundReason addRefundReason(RefundReason r) throws IOException, SQLException;

    /**
     * Remove a refund reason. This method marks the reason as deleted, it will
     * still remain in the database.
     *
     * @param r the refund reason to remove.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws JTillException if the reason was not found.
     */
    public abstract void removeRefundReason(RefundReason r) throws IOException, SQLException, JTillException;

    /**
     * Update a refund reason.
     *
     * @param r the refund reason to update.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws JTillException if the reason was not found.
     */
    public abstract void updateRefundReason(RefundReason r) throws IOException, SQLException, JTillException;

    /**
     * Get a refund reason.
     *
     * @param id the id of the refund reason.
     * @return the refund reason to get.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws JTillException if the reason was not found.
     */
    public abstract RefundReason getRefundReason(int id) throws IOException, SQLException, JTillException;

    /**
     * Get all used refund reasons, this will return a list of reasons not
     * marked as deleted.
     *
     * @return a List of refund reasons.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract List<RefundReason> getUsedRefundReasons() throws IOException, SQLException;

    /**
     * Get all the sales for a staff member.
     *
     * @param start the start date.
     * @param end the end date.
     * @param s the staff member.
     * @return the staff members sales.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws JTillException if the staff member was not found.
     */
    public abstract BigDecimal getStaffMemberSales(Date start, Date end, Staff s) throws IOException, SQLException, JTillException;

    /**
     * Method to get all screens with this product on them.
     *
     * @param p the product.
     * @return a List of Screens.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public abstract List<Screen> getScreensWithProduct(Product p) throws IOException, SQLException;
}
