/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 * Interface for modelling a class which deals with data connections
 *
 * @author David
 */
public interface DataConnect {

    public boolean isConnected();

    public void close() throws IOException;

    public void setGUI(GUIInterface g);

    public void assisstance(String message) throws IOException;

    public BigDecimal getTillTakings(String terminal) throws IOException, SQLException;

    /**
     * Send an email to the report email set in the server.
     *
     * @param message the message to send.
     * @throws IOException if there was a network error.
     */
    public void sendEmail(String message) throws IOException;

    /**
     * Email a receipt to a customer.
     *
     * @param email the email to send the receipt to.
     * @param sale the sale to generate the receipt from.
     * @throws IOException if there was a network error.
     * @throws javax.mail.internet.AddressException if there was any errors
     * sending the email.
     */
    public void emailReceipt(String email, Sale sale) throws IOException, AddressException, MessagingException;

    public void setSetting(String key, String value) throws IOException;

    public String getSettings(String key) throws IOException;

    //Product
    /**
     * Add a new product to the system.
     *
     * @param p the product to add.
     * @return the product that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public Product addProduct(Product p) throws IOException, SQLException;

    /**
     * Remove a product from the system
     *
     * @param code the code of the product to remove.
     * @throws ProductNotFoundException if the product was not found.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public void removeProduct(int code) throws ProductNotFoundException, IOException, SQLException;

    /**
     * Remove a product from the system
     *
     * @param p the product to remove.
     * @throws ProductNotFoundException if the product was not found.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public void removeProduct(Product p) throws IOException, ProductNotFoundException, SQLException;

    /**
     * Method to purchase a product and reduce its stock level by 1.
     *
     * @param p the product to purchase.
     * @param amount the amount of the product to purchase.
     * @return the new stock level.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error purchasing the product.
     * @throws OutOfStockException if the product is out of stock.
     * @throws ProductNotFoundException if the product was not found.
     */
    public int purchaseProduct(Product p, int amount) throws IOException, ProductNotFoundException, OutOfStockException, SQLException;

    /**
     * Method to get a product by its code.
     *
     * @param code the product to get.
     * @return the Product that matches the code.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error getting the product.
     * @throws ProductNotFoundException if the product could not be found.
     */
    public Product getProduct(int code) throws IOException, ProductNotFoundException, SQLException;

    /**
     * Method to get all the products on the system.
     *
     * @return a List of all products.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public List<Product> getAllProducts() throws IOException, SQLException;

    /**
     * Method to update a product.
     *
     * @param p the product to update.
     * @return the product after getting updated.
     * @throws IOException if there was a network error.
     * @throws ProductNotFoundException if the product could not be found.
     * @throws SQLException if there was a database error.
     */
    public Product updateProduct(Product p) throws IOException, ProductNotFoundException, SQLException;

    /**
     * Method to check if a barcode is already in use.
     *
     * @param barcode the barcode to check
     * @return true if it is getting used, false otherwise.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public boolean checkBarcode(String barcode) throws IOException, SQLException;

    /**
     * Method to get a product by its barcode.
     *
     * @param barcode the barcode to search.
     * @return the product that matches the barcode.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error getting the product.
     * @throws ProductNotFoundException if the product could not be found.
     */
    public Product getProductByBarcode(String barcode) throws IOException, ProductNotFoundException, SQLException;

    /**
     * Method to get the discount for a product.
     *
     * @param p the product to get the discount for.
     * @return a List of discounts the product has.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error getting the product.
     */
    public List<Discount> getProductsDiscount(Product p) throws IOException, SQLException;

    /**
     * Method to search for a product.
     *
     * @param terms the search terms.
     * @return a List of products matching the terms.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error getting the product.
     */
    public List<Product> productLookup(String terms) throws IOException, SQLException;

    //Plu
    /**
     * Add a new plu barcode to the system.
     *
     * @param plu the Plu to add.
     * @return the Plu that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public Plu addPlu(Plu plu) throws IOException, SQLException;

    /**
     * Method to remove a plu from the system.
     *
     * @param id the id of the plu to remove.
     * @throws IOException if there was a network error.
     * @throws JTillException if there was an error removing the plu.
     * @throws SQLException if there was a database error.
     */
    public void removePlu(int id) throws IOException, JTillException, SQLException;

    /**
     * Method to remove a plu from the system.
     *
     * @param p the plu to remove.
     * @throws IOException if there was a network error.
     * @throws JTillException if there was an error removing the plu.
     * @throws SQLException if there was a database error.
     */
    public void removePlu(Plu p) throws IOException, JTillException, SQLException;

    /**
     * Method to get a plu from the system.
     *
     * @param id the if of the plu to get.
     * @return the plu that matches the id.
     * @throws IOException if there was a network error.
     * @throws JTillException if there was an error getting the plu.
     * @throws SQLException if there was a database error.
     */
    public Plu getPlu(int id) throws IOException, JTillException, SQLException;

    /**
     * Method to get a plu by its barcode.
     *
     * @param code the barcode of the plu to get
     * @return the plu that matches the barcode.
     * @throws IOException if there was a network error.
     * @throws JTillException if the plu could not be found.
     * @throws SQLException if there was a database error.
     */
    public Plu getPluByCode(String code) throws IOException, JTillException, SQLException;

    /**
     * Method to get all plus on the system.
     *
     * @return a List of all the plus.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public List<Plu> getAllPlus() throws IOException, SQLException;

    /**
     * Method to update a plu on the system.
     *
     * @param p the plu to update.
     * @return the plu that was updated.
     * @throws IOException if there was a network error.
     * @throws JTillException if the plu could not be found.
     * @throws SQLException if there was a database error.
     */
    public Plu updatePlu(Plu p) throws IOException, JTillException, SQLException;

    //Customer
    /**
     * Method to add a customer to the system.
     *
     * @param customer the customer to add.
     * @return the customer that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public Customer addCustomer(Customer customer) throws IOException, SQLException;

    /**
     * Method to remove a customer from the system.
     *
     * @param id the id of the customer to remove.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public void removeCustomer(int id) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to remove a customer from the system.
     *
     * @param c the customer to remove.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if the customer could not be found.
     * @throws SQLException if there was a database error.
     */
    public void removeCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to get a customer from the system.
     *
     * @param id the id of the customer to get.
     * @return the customer that matches the id.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if the customer could not be found.
     * @throws SQLException if there was a database error.
     */
    public Customer getCustomer(int id) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to search for customers with the given name.
     *
     * @param name the name to search for.
     * @return a List of call customers matching the name.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if no customer could be found.
     * @throws SQLException if there was a database error.
     */
    public List<Customer> getCustomerByName(String name) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to get a list of all the customers in the system.
     *
     * @return a List of all the customers in the system.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public List<Customer> getAllCustomers() throws IOException, SQLException;

    /**
     * Method to update a customer in the system.
     *
     * @param c the customer to update.
     * @return the customer that was updated.
     * @throws IOException if there was a network error.
     * @throws CustomerNotFoundException if the customer could not be found.
     * @throws SQLException if there was a database error.
     */
    public Customer updateCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException;

    /**
     * Method to search for customers that have details matching the given
     * terms.
     *
     * @param terms the terms to search.
     * @return a List of customers matching the terms.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public List<Customer> customerLookup(String terms) throws IOException, SQLException;

    //Sale
    /**
     * Method to add a sale to the system.
     *
     * @param s the sale to add.
     * @return the sale that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public Sale addSale(Sale s) throws IOException, SQLException;

    /**
     * Method to get all the sales in the system.
     *
     * @return a List of all the sales in the system.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public List<Sale> getAllSales() throws IOException, SQLException;

    /**
     * Method to get a sale in the system.
     *
     * @param id the id of the sale to get.
     * @return the sale matching the id.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws SaleNotFoundException if the sale could not be found.
     */
    public Sale getSale(int id) throws IOException, SQLException, SaleNotFoundException;

    /**
     * Method to get all sales within the specified date range.
     *
     * @param start the start date.
     * @param end the end date.
     * @return a List of all sales within the range.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public List<Sale> getSalesInRange(Time start, Time end) throws IOException, SQLException;

    /**
     * Method to update a sale in the database.
     *
     * @param s the sale to update.
     * @return the sale that was updated.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws SaleNotFoundException if the sale could not be found.
     */
    public Sale updateSale(Sale s) throws IOException, SQLException, SaleNotFoundException;

    /**
     * Method to suspend a sale. This will add the sale to a list of suspended
     * sales that can then be continues from another terminal.
     *
     * @param sale the sale to suspend.
     * @param staff the staff member the sales belongs to.
     * @throws IOException if there was a network error.
     */
    public void suspendSale(Sale sale, Staff staff) throws IOException;

    /**
     * Method to resume a sale. This method will remove the sale from the list
     * of suspended sales.
     *
     * @param s the member of staff who wants to retrieve a sale.
     * @return the sale that was suspended.
     * @throws IOException if there was a network error.
     */
    public Sale resumeSale(Staff s) throws IOException;

    /**
     * Method to get all uncashed sales from a given terminal with name t.
     *
     * @param t the name of the terminal to get the sales for.
     * @return a List of all the sales which have not been cashed.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public List<Sale> getUncashedSales(String t) throws IOException, SQLException;

    //Staff
    /**
     * Method to add a member of staff.
     *
     * @param s the member of staff to add.
     * @return the member of staff that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public Staff addStaff(Staff s) throws IOException, SQLException;

    /**
     * Method to remove a member of staff from the system.
     *
     * @param id the id of the staff to remove.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public void removeStaff(int id) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to remove a member of staff from the system.
     *
     * @param s the member of staff to remove.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public void removeStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to get a member of staff from the system.
     *
     * @param id the member of staff to get.
     * @return the member of staff matching the id.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public Staff getStaff(int id) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to get all staff in the system.
     *
     * @return a List of all the staff in the system.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public List<Staff> getAllStaff() throws IOException, SQLException;

    /**
     * Method to get the amount of staff in the system
     *
     * @return int value representing the amount of staff.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public int getStaffCount() throws IOException, SQLException;

    /**
     * Method to update a member of staff.
     *
     * @param s the member of staff to update.
     * @return the member of staff that was updated.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the id could not be found.
     * @throws SQLException if there was a database error.
     */
    public Staff updateStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to check if a username is already in use. Case is ignored.
     *
     * @param username the username to check.
     * @return true if it is in use, false otherwise.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public boolean checkUsername(String username) throws IOException, SQLException;

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
    public Staff login(String username, String password) throws IOException, LoginException, SQLException;

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
    public Staff tillLogin(int id) throws IOException, LoginException, SQLException;

    /**
     * Method to check if a member of staff is logged into a till.
     *
     * @param s the staff member to check.
     * @return boolean indicating whether they are logged in or not.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the staff member could not be found.
     * @throws SQLException if there was a database error.
     */
    public boolean isTillLoggedIn(Staff s) throws IOException, StaffNotFoundException, SQLException;

    /**
     * Method to log a member of staff out of the system. this should not be
     * used for logging out of the tills.
     *
     * @param s the member of staff logging out.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    public void logout(Staff s) throws IOException, StaffNotFoundException;

    /**
     * Method to log a member of staff out of the tills.
     *
     * @param s the member of staff to log out.
     * @throws IOException if there was a network error.
     * @throws StaffNotFoundException if the member of staff could not be found.
     */
    public void tillLogout(Staff s) throws IOException, StaffNotFoundException;

    //Category
    /**
     * Method to add a category to the system.
     *
     * @param c the category to add.
     * @return the category that was added.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public Category addCategory(Category c) throws IOException, SQLException;

    /**
     * Method to update a category in the system.
     *
     * @param c the category to update.
     * @return the category that was updated.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws CategoryNotFoundException if the category could not be found.
     */
    public Category updateCategory(Category c) throws IOException, SQLException, CategoryNotFoundException;

    /**
     * Method to remove a category from the system.
     *
     * @param c the category to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws CategoryNotFoundException if the category could not be found.
     */
    public void removeCategory(Category c) throws IOException, SQLException, CategoryNotFoundException;

    /**
     * Method to remove a category from the system.
     *
     * @param id the id of the category to remove.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws CategoryNotFoundException if the category could not be found.
     */
    public void removeCategory(int id) throws IOException, SQLException, CategoryNotFoundException;

    /**
     * Method to get a category from the system.
     *
     * @param id the id of the category to get.
     * @return the category that matches the id.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws CategoryNotFoundException if the category could not be found.
     */
    public Category getCategory(int id) throws IOException, SQLException, CategoryNotFoundException;

    /**
     * Method to get all the categories in the system.
     *
     * @return a List of all the categories.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     */
    public List<Category> getAllCategorys() throws IOException, SQLException;

    /**
     * Method to get all the products in a specific category.
     *
     * @param id the id of the category to get the products for.
     * @return a List of all the products in the category.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was a database error.
     * @throws CategoryNotFoundException if the category could not be found.
     */
    public List<Product> getProductsInCategory(int id) throws IOException, SQLException, CategoryNotFoundException;

    //Discount
    public Discount addDiscount(Discount d) throws IOException, SQLException;

    public Discount updateDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException;

    public void removeDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException;

    public void removeDiscount(int id) throws IOException, SQLException, DiscountNotFoundException;

    public Discount getDiscount(int id) throws IOException, SQLException, DiscountNotFoundException;

    public List<Discount> getAllDiscounts() throws IOException, SQLException;

    //Tax
    public Tax addTax(Tax t) throws IOException, SQLException;

    public void removeTax(Tax t) throws IOException, SQLException, TaxNotFoundException;

    public void removeTax(int id) throws IOException, SQLException, TaxNotFoundException;

    public Tax getTax(int id) throws IOException, SQLException, TaxNotFoundException;

    public Tax updateTax(Tax t) throws IOException, SQLException, TaxNotFoundException;

    public List<Tax> getAllTax() throws IOException, SQLException;

    public List<Product> getProductsInTax(int id) throws IOException, SQLException, TaxNotFoundException;

    //Screens
    public Screen addScreen(Screen s) throws IOException, SQLException;

    public TillButton addButton(TillButton b) throws IOException, SQLException;

    public void removeScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException;

    public void removeButton(TillButton b) throws IOException, SQLException, ButtonNotFoundException;

    public Screen getScreen(int s) throws IOException, SQLException, ScreenNotFoundException;

    public TillButton getButton(int b) throws IOException, SQLException, ButtonNotFoundException;

    public Screen updateScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException;

    public TillButton updateButton(TillButton b) throws IOException, SQLException, ButtonNotFoundException;

    public List<Screen> getAllScreens() throws IOException, SQLException;

    public List<TillButton> getAllButtons() throws IOException, SQLException;

    public List<TillButton> getButtonsOnScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException;

    public void deleteAllScreensAndButtons() throws IOException, SQLException;

    //Tills
    public Till addTill(Till t) throws IOException, SQLException;

    public void removeTill(int id) throws IOException, SQLException, TillNotFoundException;

    public Till getTill(int id) throws IOException, SQLException, TillNotFoundException;

    public List<Till> getAllTills() throws IOException, SQLException;

    public boolean connectTill(String t) throws IOException;
}
