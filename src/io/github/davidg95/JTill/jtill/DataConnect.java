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
     * @param code the code of the product to purchase.
     * @param amount the amount of the product to purchase.
     * @return the new stock level.
     * @throws IOException if there was a network error.
     * @throws SQLException if there was an error purchasing the product.
     * @throws OutOfStockException if the product is out of stock.
     * @throws ProductNotFoundException if the product was not found.
     */
    public int purchaseProduct(int code, int amount) throws IOException, ProductNotFoundException, OutOfStockException, SQLException;

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

    //Customer
    public Customer addCustomer(Customer customer) throws IOException, SQLException;

    public void removeCustomer(int id) throws IOException, CustomerNotFoundException, SQLException;

    public void removeCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException;

    public Customer getCustomer(int id) throws IOException, CustomerNotFoundException, SQLException;

    public List<Customer> getCustomerByName(String name) throws IOException, CustomerNotFoundException, SQLException;

    public int getCustomerCount() throws IOException, SQLException;

    public List<Customer> getAllCustomers() throws IOException, SQLException;

    public Customer updateCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException;

    public List<Customer> customerLookup(String terms) throws IOException, SQLException;

    //Sale
    public Sale addSale(Sale s) throws IOException, SQLException;

    public List<Sale> getAllSales() throws IOException, SQLException;

    public Sale getSale(int id) throws IOException, SQLException, SaleNotFoundException;

    public List<Sale> getSalesInRange(Time start, Time end) throws IOException, SQLException;

    public Sale updateSale(Sale s) throws IOException, SQLException, SaleNotFoundException;

    public void suspendSale(Sale sale, Staff staff) throws IOException;

    public Sale resumeSale(Staff s) throws IOException;

    public List<Sale> getUncashedSales(String t) throws IOException, SQLException;

    //Staff
    public Staff addStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    public void removeStaff(int id) throws IOException, StaffNotFoundException, SQLException;

    public void removeStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    public Staff getStaff(int id) throws IOException, StaffNotFoundException, SQLException;

    public List<Staff> getAllStaff() throws IOException, SQLException;

    public Staff updateStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    public int staffCount() throws IOException, StaffNotFoundException, SQLException;

    public Staff login(String username, String password) throws IOException, LoginException, SQLException;

    public Staff tillLogin(int id) throws IOException, LoginException, SQLException;

    public void logout(Staff s) throws IOException, StaffNotFoundException;

    public void tillLogout(Staff s) throws IOException, StaffNotFoundException;

    //Category
    public Category addCategory(Category c) throws IOException, SQLException;

    public Category updateCategory(Category c) throws IOException, SQLException, CategoryNotFoundException;

    public void removeCategory(Category c) throws IOException, SQLException, CategoryNotFoundException;

    public void removeCategory(int id) throws IOException, SQLException, CategoryNotFoundException;

    public Category getCategory(int id) throws IOException, SQLException, CategoryNotFoundException;

    public List<Category> getAllCategorys() throws IOException, SQLException;

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

    //Voucher
    public Voucher addVoucher(Voucher v) throws IOException, SQLException;

    public void removeVoucher(Voucher v) throws IOException, SQLException, VoucherNotFoundException;

    public void removeVoucher(int id) throws IOException, SQLException, VoucherNotFoundException;

    public Voucher getVoucher(int id) throws IOException, SQLException, VoucherNotFoundException;

    public Voucher updateVoucher(Voucher v) throws IOException, SQLException, VoucherNotFoundException;

    public List<Voucher> getAllVouchers() throws IOException, SQLException;

    public void close() throws IOException;

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
