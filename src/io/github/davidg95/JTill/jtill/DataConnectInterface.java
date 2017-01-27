/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author David
 */
public interface DataConnectInterface {

    public boolean isConnected();

    public void setGUI(GUIInterface g);

    //Product
    public void addProduct(Product p) throws IOException, SQLException;

    public void removeProduct(int code) throws IOException, ProductNotFoundException, SQLException;

    public void removeProduct(Product p) throws IOException, ProductNotFoundException, SQLException;

    public int purchaseProduct(int code, int amount) throws IOException, ProductNotFoundException, OutOfStockException, SQLException;

    public Product getProduct(int code) throws IOException, ProductNotFoundException, SQLException;

    public int getProductCount() throws IOException, SQLException;

    public List<Product> getAllProducts() throws IOException, SQLException;

    public Product updateProduct(Product p) throws IOException, ProductNotFoundException, SQLException;

    public boolean checkBarcode(String barcode) throws IOException, SQLException;

    public Product getProductByBarcode(String barcode) throws IOException, ProductNotFoundException, SQLException;

    public void setStock(int code, int stock) throws IOException, ProductNotFoundException, SQLException;

    public List<Discount> getProductsDiscount(Product p) throws IOException, SQLException;

    public List<Product> productLookup(String terms) throws IOException, SQLException;

    //Customer
    public void addCustomer(Customer customer) throws IOException, SQLException;

    public void removeCustomer(int id) throws IOException, CustomerNotFoundException, SQLException;

    public void removeCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException;

    public Customer getCustomer(int id) throws IOException, CustomerNotFoundException, SQLException;

    public List<Customer> getCustomerByName(String name) throws IOException, CustomerNotFoundException, SQLException;

    public int getCustomerCount() throws IOException, SQLException;

    public List<Customer> getAllCustomers() throws IOException, SQLException;

    public Customer updateCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException;

    //Sale
    public void addSale(Sale s) throws IOException, SQLException;

    public List<Sale> getAllSales() throws IOException, SQLException;

    public Sale getSale(int id) throws IOException, SQLException, SaleNotFoundException;

    public List<Sale> getSalesInRange(Date start, Date end) throws IOException, SQLException;

    public Sale updateSale(Sale s) throws IOException, SQLException, SaleNotFoundException;

    //Staff
    public void addStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    public void removeStaff(int id) throws IOException, StaffNotFoundException, SQLException;

    public void removeStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    public Staff getStaff(int id) throws IOException, StaffNotFoundException, SQLException;

    public List<Staff> getAllStaff() throws IOException, SQLException;

    public Staff updateStaff(Staff s) throws IOException, StaffNotFoundException, SQLException;

    public int staffCount() throws IOException, StaffNotFoundException, SQLException;

    public Staff login(String username, String password) throws IOException, LoginException, SQLException;

    public Staff tillLogin(int id) throws IOException, LoginException, SQLException;

    public void logout(int id) throws IOException, StaffNotFoundException;

    public void tillLogout(int id) throws IOException, StaffNotFoundException;

    //Category
    public void addCategory(Category c) throws IOException, SQLException;

    public Category updateCategory(Category c) throws IOException, SQLException, CategoryNotFoundException;

    public void removeCategory(Category c) throws IOException, SQLException, CategoryNotFoundException;

    public void removeCategory(int id) throws IOException, SQLException, CategoryNotFoundException;

    public Category getCategory(int id) throws IOException, SQLException, CategoryNotFoundException;

    public List<Category> getAllCategorys() throws IOException, SQLException;

    public List<Product> getProductsInCategory(int id) throws IOException, SQLException, CategoryNotFoundException;

    //Discount
    public void addDiscount(Discount d) throws IOException, SQLException;

    public Discount updateDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException;

    public void removeDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException;

    public void removeDiscount(int id) throws IOException, SQLException, DiscountNotFoundException;

    public Discount getDiscount(int id) throws IOException, SQLException, DiscountNotFoundException;

    public List<Discount> getAllDiscounts() throws IOException, SQLException;

    //Tax
    public void addTax(Tax t) throws IOException, SQLException;

    public void removeTax(Tax t) throws IOException, SQLException, TaxNotFoundException;

    public void removeTax(int id) throws IOException, SQLException, TaxNotFoundException;

    public Tax getTax(int id) throws IOException, SQLException, TaxNotFoundException;

    public Tax updateTax(Tax t) throws IOException, SQLException, TaxNotFoundException;

    public List<Tax> getAllTax() throws IOException, SQLException;

    //Voucher
    public void addVoucher(Voucher v) throws IOException, SQLException;

    public void removeVoucher(Voucher v) throws IOException, SQLException, VoucherNotFoundException;

    public void removeVoucher(int id) throws IOException, SQLException, VoucherNotFoundException;

    public Voucher getVoucher(int id) throws IOException, SQLException, VoucherNotFoundException;

    public Voucher updateVoucher(Voucher v) throws IOException, SQLException, VoucherNotFoundException;

    public List<Voucher> getAllVouchers() throws IOException, SQLException;

    public void close();

    //Screens
    public void addScreen(Screen s) throws IOException, SQLException;

    public void addButton(Button b) throws IOException, SQLException;

    public void removeScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException;

    public void removeButton(Button b) throws IOException, SQLException, ButtonNotFoundException;

    public Screen getScreen(int s) throws IOException, SQLException, ScreenNotFoundException;

    public Button getButton(int b) throws IOException, SQLException, ButtonNotFoundException;

    public Screen updateScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException;

    public Button updateButton(Button b) throws IOException, SQLException, ButtonNotFoundException;

    public List<Screen> getAllScreens() throws IOException, SQLException;

    public List<Button> getAllButtons() throws IOException, SQLException;

    public List<Button> getButtonsOnScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException;

    public void deleteAllScreensAndButtons() throws IOException, SQLException;
}
