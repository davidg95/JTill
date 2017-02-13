/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class Sale implements Serializable {

    private int code;
    private List<SaleItem> saleItems;
    private BigDecimal total;
    private Customer customer;
    private Time time;
    private String terminal;
    private boolean cashed;
    private Staff staff;
    private boolean chargeAccount;

    private SaleItem lastAdded;

    public Sale(String terminal, Staff s) {
        saleItems = new ArrayList<>();
        customer = null;
        total = new BigDecimal("0.00");
        this.terminal = terminal;
        this.cashed = false;
        chargeAccount = false;
        this.staff = s;
    }

    public Sale(Customer c, boolean chargeAccount, String terminal, Staff s) {
        saleItems = new ArrayList<>();
        this.customer = c;
        this.chargeAccount = chargeAccount;
        this.terminal = terminal;
        this.cashed = false;
        total = new BigDecimal("0.00");
        this.staff = s;
    }

    public Sale(int code, BigDecimal total, Customer customer, Time time, String terminal, boolean cashed, boolean chargeAccount, Staff s, List<SaleItem> saleItems) {
        this(code, total, customer, time, terminal, cashed, chargeAccount, s);
        this.saleItems = saleItems;
    }

    public Sale(int code, BigDecimal total, Customer customer, Time time, String terminal, boolean cashed, boolean chargeAccount, Staff s) {
        this.code = code;
        this.total = total;
        this.customer = customer;
        this.time = time;
        this.terminal = terminal;
        this.chargeAccount = chargeAccount;
        this.staff = s;
    }

    /**
     * This method adds products to the sale. First it will check if the product
     * has already been added. If it has been added then it will check if it is
     * open priced, if it is then it will check if the price is the same, if so
     * then the quantity is increased, if not then it continues checking the
     * products. If it is not open price but exists it will increase the
     * quantity. If it does not exist then it adds a new item.
     *
     * @param i
     * @param quantity
     * @return true if the item was already in the sale and is being re-added,
     * false if it is a new item in the sale.
     */
    public boolean addItem(Item i, int quantity) {
        //First check if the item has already been added
        for (SaleItem item : saleItems) {
            if (item.getItem().getId() == i.getId() && i.getClass().equals(item.getClass())) {
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

    public void complete() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public void setTime(Time time) {
        this.time = time;
    }

    public Time getTime() {
        return time;
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

    /**
     * Method to void an item from the sale. It will first check though the list
     * looking for the item. If the quantity of the item in the last is greater
     * than the quantity being voided, then the quantity of the item in the list
     * is simply reduced. If the quantities are the same then the item is
     * removed from the list.
     *
     * @param si
     */
    public void voidItem(SaleItem si) {
        for (SaleItem item : saleItems) {
            if (item.getItem().getId() == si.getItem().getId()) {
                if (item.getQuantity() > si.getQuantity()) { //If the quantities are different then reduce the quantity.
                    item.decreaseQuantity(si.getQuantity());
                    updateTotal();
                    return;
                } else if (item.getQuantity() == si.getQuantity()) { //If the quantities are the same then remove the item.
                    saleItems.remove(si);
                    updateTotal();
                    return;
                }
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
        for (SaleItem item : saleItems) {
            if (item.getItem().getId() == lastAdded.getItem().getId()) {
                if (item.getQuantity() > lastAdded.getQuantity()) { //If the quantities are different then reduce the quantity.
                    item.decreaseQuantity(lastAdded.getQuantity());
                    updateTotal();
                    return;
                } else if (item.getQuantity() == lastAdded.getQuantity()) { //If the quantities are the same then remove the item.
                    saleItems.remove(lastAdded);
                    updateTotal();
                    return;
                }
            }
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

    public void updateTotal() {
        total = new BigDecimal("0");
        for (SaleItem item : saleItems) {
            total = total.add(item.getPrice());
        }
    }

    public String getSQLInsertStatement() {
        if (this.customer == null) { //If no customer was assigned then set the customer ID to -1
            return this.total
                    + ",-1"
                    + ",'" + this.time.toString()
                    + "','" + this.terminal
                    + "'," + this.cashed
                    + "," + this.staff.getId()
                    + "," + this.chargeAccount;
        } else {
            return this.total
                    + "," + this.customer.getId()
                    + ",'" + this.time.toString()
                    + "','" + this.terminal
                    + "'," + this.cashed
                    + "," + this.chargeAccount;
        }
    }

    public String getSQLUpdateStatement() {
        if (this.customer == null) {
            return "UPDATE SALES"
                    + " SET PRICE=" + this.total
                    + ", CUSTOMER=-1"
                    + ", TIMESTAMP='" + this.time.toString()
                    + "', TERMINAL='" + this.terminal
                    + "', CASHED=" + this.cashed
                    + ", STAFF=" + this.staff.getId()
                    + ", CHARGE_ACCOUNT=" + this.chargeAccount
                    + " WHERE SALES.ID=" + this.code;
        } else {
            return "UPDATE SALES"
                    + " SET PRICE=" + this.total
                    + ", CUSTOMER=" + this.customer.getId()
                    + ", TIMESTAMP='" + this.time.toString()
                    + "', TERMINAL='" + this.terminal
                    + "', CASHED=" + this.cashed
                    + ", STAFF=" + this.staff.getId()
                    + ", CHARGE_ACCOUNT=" + this.chargeAccount
                    + " WHERE SALES.ID=" + this.code;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Sale) {
            if (this.getCode() == ((Sale) o).getCode()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.code;
        return hash;
    }

    @Override
    public String toString() {
        return this.code
                + "\n" + this.saleItems.size()
                + "\n" + this.total.toString();
    }
}
