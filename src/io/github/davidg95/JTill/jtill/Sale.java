/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class which models a sale.
 *
 * @author David
 */
public class Sale implements Serializable {

    private int id;
    private List<SaleItem> saleItems;
    private BigDecimal total;
    private Customer customer;
    private Date date;
    private String terminal;
    private boolean cashed;
    private Staff staff;
    private boolean chargeAccount;

    private SaleItem lastAdded;

    /**
     * Constructor which creates a new sale with no items and with the total set
     * to £0.00.
     *
     * @param terminal the name of the terminal the sale is done it.
     * @param s the staff member the sale is done by.
     */
    public Sale(String terminal, Staff s) {
        saleItems = new ArrayList<>();
        total = new BigDecimal("0.00");
        this.terminal = terminal;
        this.staff = s;
    }

    /**
     * Constructor for the sale class. When creating a new sale the Sale(Staff
     * s) constructor should be used, this one is for loading from the database.
     *
     * @param id the sales ID.
     * @param terminal the terminal the sale was done on.
     * @param cashed if the sale has been cashed or not.
     * @param saleItems the items in the sale.
     * @param customer the customer the sale was for. Can be null.
     * @param total the total price of the sale.
     * @param date the data of the sale.
     * @param staff the staff member the sale was done by.
     */
    public Sale(int id, BigDecimal total, Customer customer, Date date, String terminal, boolean cashed, Staff staff, List<SaleItem> saleItems) {
        this.id = id;
        this.total = total;
        this.customer = customer;
        this.date = date;
        this.terminal = terminal;
        this.staff = staff;
        this.saleItems = saleItems;
    }

    /**
     * Constructor for the sale class. When creating a new sale the Sale(Staff
     * s) constructor should be used, this one is for loading from the database.
     *
     * @param id the sales ID.
     * @param terminal the terminal the sale was done on.
     * @param cashed if the sale has been cashed or not.
     * @param customer the customer the sale was for. Can be null.
     * @param total the total price of the sale.
     * @param date the data of the sale.
     * @param staff the staff member the sale was done by.
     */
    public Sale(int id, BigDecimal total, Customer customer, Date date, String terminal, boolean cashed, Staff staff) {
        this.id = id;
        this.total = total;
        this.customer = customer;
        this.date = date;
        this.terminal = terminal;
        this.staff = staff;
    }

    /**
     * This method adds products to the sale. First it will check if the product
     * has already been added. If it has been added then it will check if it is
     * open priced, if it is then it will check if the price is the same, if so
     * then the quantity is increased, if not then it continues checking the
     * products. If it is not open price but exists it will increase the
     * quantity. If it does not exist then it adds a new item.
     *
     * @param i the item to add.
     * @param quantity the quantity to add.
     * @return true if the item was already in the sale and is being re-added,
     * false if it is a new item in the sale.
     */
    public boolean addItem(Item i, int quantity) {
        //First check if the item has already been added
        for (SaleItem item : saleItems) {
            if (item.getItem().getId() == i.getId() && (item.getItem() instanceof Discount) == (i instanceof Discount)) {
                if (i.isOpen()) {
                    if (i.getPrice().compareTo(item.getItem().getPrice()) == 0) {
                        BigDecimal inc = item.increaseQuantity(quantity);
                        this.total = total.add(inc);
                        this.lastAdded = new SaleItem(this, i, quantity);
                        updateTotal();
                        return true; //The product is open price and the same price so increase the quantity and exit
                    } else {
                        continue; //The product is open but a different price so check the next item
                    }
                }
                //Product is not open price and does already exist
                BigDecimal inc = item.increaseQuantity(quantity);
                this.total = total.add(inc);
                this.lastAdded = new SaleItem(this, i, quantity);
                return true;
            }
        }
        //If the item is not already in the sale
        SaleItem item = new SaleItem(this, i, quantity);

        this.total = total.add(item.getPrice());
        saleItems.add(item);
        this.lastAdded = item;
        updateTotal();
        return false;
    }

    /**
     * Method to calculate the change to give back to the customer.
     *
     * @param money the money being handed over.
     * @return the change to give back
     * @throws JTillException if not enough money was handed over.
     */
    public BigDecimal complete(BigDecimal money) throws JTillException {
        if (total.compareTo(money) > 0) {
            throw new JTillException("Not enough change");
        }
        BigDecimal change = total.subtract(money);
        return change.abs();
    }

    /**
     * Method to void an item from the sale. It will first check though the list
     * looking for the item. If the quantity of the item in the last is greater
     * than the quantity being voided, then the quantity of the item in the list
     * is simply reduced. If the quantities are the same then the item is
     * removed from the list.
     *
     * @param si the item to remove from the sale.
     */
    public void voidItem(SaleItem si) {
        for (int i = 0; i < saleItems.size(); i++) {
            if (saleItems.get(i).equals(si)) {
                if (saleItems.get(i).getQuantity() > si.getQuantity()) {
                    saleItems.get(i).decreaseQuantity(si.getQuantity());
                } else {
                    saleItems.remove(i);
                }
                updateTotal();
                return;
            }
        }
    }

    /**
     * Method to void an item from the sale. It will first check though the list
     * looking for the item. If the quantity of the item in the last is greater
     * than the quantity being voided, then the quantity of the item in the list
     * is simply reduced. If the quantities are the same then the item is
     * removed from the list.
     */
    public void voidLastItem() {
        if (lastAdded != null) {
            voidItem(lastAdded);
            lastAdded = null;
        }
    }

    /**
     * This method will half the price of a sale item. If a sale item has a
     * quantity greater than 1, then all the times will be half priced. This
     * will also update the total price of the sale.
     *
     * @param item the item of half price.
     */
    public void halfPriceItem(SaleItem item) {
        for (SaleItem i : saleItems) {
            if (i.getItem().getId() == item.getItem().getId()) {
                if (i.getItem().getPrice().compareTo(new BigDecimal("0.01")) != 0) {
                    if (i.getItem().isOpen()) {
                        if (i.getItem().getPrice().compareTo(item.getItem().getPrice()) == 0) {
                            BigDecimal val = i.getPrice().divide(new BigDecimal("2"), BigDecimal.ROUND_DOWN);
                            i.setPrice(val);
                            updateTotal();
                            return;
                        }
                    } else {
                        BigDecimal val = i.getPrice().divide(new BigDecimal("2"), BigDecimal.ROUND_DOWN);
                        i.setPrice(val);
                        updateTotal();
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    public SaleItem getLastAdded() {
        return lastAdded;
    }

    /**
     * Method to update the total.
     */
    public void updateTotal() {
        total = new BigDecimal("0");
        for (SaleItem item : saleItems) {
            total = total.add(item.getPrice());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int code) {
        this.id = code;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setProducts(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getLineCount() {
        return saleItems.size();
    }

    public int getTotalItemCount() {
        int count = 0;
        for (SaleItem item : saleItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public boolean isChargeAccount() {
        return chargeAccount;
    }

    public void setChargeAccount(boolean chargeAccount) {
        this.chargeAccount = chargeAccount;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public boolean isCashed() {
        return cashed;
    }

    public void setCashed(boolean cashed) {
        this.cashed = cashed;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getSQLInsertStatement() {
        if (this.customer == null) { //If no customer was assigned then set the customer ID to -1
            return this.total
                    + ",-1"
                    + "," + this.date.getTime()
                    + ",'" + this.terminal
                    + "'," + this.cashed
                    + "," + this.staff.getId()
                    + "," + this.chargeAccount;
        } else {
            return this.total
                    + "," + this.customer.getId()
                    + "," + this.date.getTime()
                    + ",'" + this.terminal
                    + "'," + this.cashed
                    + "," + this.staff.getId()
                    + "," + this.chargeAccount;
        }
    }

    public String getSQLUpdateStatement() {
        if (this.customer == null) {
            return "UPDATE SALES"
                    + " SET PRICE=" + this.total
                    + ", CUSTOMER=-1"
                    + ", TIMESTAMP=" + this.date.getTime()
                    + ", TERMINAL='" + this.terminal
                    + "', CASHED=" + this.cashed
                    + ", STAFF=" + this.staff.getId()
                    + ", CHARGE_ACCOUNT=" + this.chargeAccount
                    + " WHERE SALES.ID=" + this.id;
        } else {
            return "UPDATE SALES"
                    + " SET PRICE=" + this.total
                    + ", CUSTOMER=" + this.customer.getId()
                    + ", TIMESTAMP=" + this.date.getTime()
                    + ", TERMINAL='" + this.terminal
                    + "', CASHED=" + this.cashed
                    + ", STAFF=" + this.staff.getId()
                    + ", CHARGE_ACCOUNT=" + this.chargeAccount
                    + " WHERE SALES.ID=" + this.id;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Sale) {
            if (this.getId() == ((Sale) o).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return this.id
                + "\n" + this.saleItems.size()
                + "\n" + this.total.toString();
    }
}
