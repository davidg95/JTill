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
public class Sale implements Serializable, JTillObject, Cloneable {

    private int id;
    private List<SaleItem> saleItems;
    private BigDecimal total;
    private int customer;
    private Date date;
    private int terminal;
    private boolean cashed;
    private int staff;
    private boolean chargeAccount;

    private SaleItem lastAdded;

    private transient final List<ProductListener> listeners;

    /**
     * Constructor which creates a new sale with no items and with the total set
     * to £0.00.
     *
     * @param terminal the name of the terminal the sale is done it.
     * @param s the staff member the sale is done by.
     */
    public Sale(int terminal, int s) {
        saleItems = new ArrayList<>();
        total = new BigDecimal("0.00");
        this.terminal = terminal;
        this.staff = s;
        listeners = new ArrayList<>();
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
    public Sale(int id, BigDecimal total, int customer, Date date, int terminal, boolean cashed, int staff, List<SaleItem> saleItems) {
        this.id = id;
        this.total = total;
        this.customer = customer;
        this.date = date;
        this.terminal = terminal;
        this.staff = staff;
        this.saleItems = saleItems;
        listeners = new ArrayList<>();
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
    public Sale(int id, BigDecimal total, int customer, Date date, int terminal, boolean cashed, int staff) {
        this.id = id;
        this.total = total;
        this.customer = customer;
        this.date = date;
        this.terminal = terminal;
        this.staff = staff;
        listeners = new ArrayList<>();
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
            int type;
            if (i instanceof Product) {
                type = SaleItem.PRODUCT;
            } else {
                type = SaleItem.DISCOUNT;
            }
            if (item.getItem() == i.getId() && item.getType() == type) {
                //The item has been added
                if (i.isOpen()) {
                    if (i.getPrice().compareTo(item.getPrice()) == 0) {
                        item.increaseQuantity(quantity);
                        this.total = total.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                        if (i instanceof Discount) {
                            this.lastAdded = new SaleItem(this.id, i.getId(), quantity, i.getPrice(), SaleItem.DISCOUNT);
                        } else {
                            this.lastAdded = new SaleItem(this.id, i.getId(), quantity, i.getPrice(), SaleItem.PRODUCT);
                        }
                        updateTotal();
                        return true; //The product is open price and the same price so increase the quantity and exit
                    } else {
                        continue; //The product is open but a different price so check the next item
                    }
                }
                //Product is not open price and does already exist
                item.increaseQuantity(quantity);
                item.setName(i.getName());
                item.setTotalPrice(i.getPrice().multiply(new BigDecimal(item.getQuantity())).toString());
                this.total = total.add(item.getPrice().multiply(new BigDecimal(quantity)));
                if (i instanceof Discount) {
                    this.lastAdded = new SaleItem(this.id, i.getId(), quantity, item.getPrice(), SaleItem.DISCOUNT);
                } else {
                    this.lastAdded = new SaleItem(this.id, i.getId(), quantity, item.getPrice(), SaleItem.PRODUCT);
                }
                return true;
            }
        }
        //If the item is not already in the sale
        SaleItem item;
        if (i instanceof Discount) {
            item = new SaleItem(this.id, i.getId(), quantity, i.getPrice(), SaleItem.DISCOUNT);
        } else {
            item = new SaleItem(this.id, i.getId(), quantity, i.getPrice(), SaleItem.PRODUCT);
        }

        this.total = total.add(item.getPrice().multiply(new BigDecimal(quantity)));
        item.setName(i.getName());
        item.setTotalPrice(i.getPrice().multiply(new BigDecimal(item.getQuantity())).toString());
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
            if (i.getItem() == item.getItem()) {
                if (i.getPrice().compareTo(new BigDecimal("0.01")) != 0) { //Check it is not at 1p.
                    if (i.getPrice().compareTo(item.getPrice()) == 0) {
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
        saleItems.forEach((item) -> {
            total = total.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        });
    }

    @Override
    public String getName() {
        return this.date.toString();
    }

    @Override
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

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public int getLineCount() {
        return saleItems.size();
    }

    public int getTotalItemCount() {
        int count = 0;
        count = saleItems.stream().map((item) -> item.getQuantity()).reduce(count, Integer::sum);
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

    public int getTerminal() {
        return terminal;
    }

    public void setTerminal(int terminal) {
        this.terminal = terminal;
    }

    public boolean isCashed() {
        return cashed;
    }

    public void setCashed(boolean cashed) {
        this.cashed = cashed;
    }

    public int getStaff() {
        return staff;
    }

    public void setStaff(int staff) {
        this.staff = staff;
    }

    public void addListener(ProductListener pl) {
        listeners.add(pl);
    }

    public void notifyAllListeners(ProductEvent pe, int itemQuantity) {
        listeners.forEach((pl) -> {
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < itemQuantity; i++) {
                        pl.onProductAdd(pe);
                    }
                }
            }.start();
        });
    }

    public void complete() {
        listeners.forEach((pl) -> {
            pl.killListener();
        });
    }

    public String getSQLInsertStatement() {
        if (this.customer == 0) { //If no customer was assigned then set the customer ID to -1
            return this.total
                    + ",-1"
                    + "," + this.date.getTime()
                    + "," + this.terminal
                    + "," + this.cashed
                    + "," + this.staff
                    + "," + this.chargeAccount;
        } else {
            return this.total
                    + "," + this.customer
                    + "," + this.date.getTime()
                    + "," + this.terminal
                    + "," + this.cashed
                    + "," + this.staff
                    + "," + this.chargeAccount;
        }
    }

    public String getSQLUpdateStatement() {
        if (this.customer == 0) {
            return "UPDATE SALES"
                    + " SET PRICE=" + this.total
                    + ", CUSTOMER=-1"
                    + ", TIMESTAMP=" + this.date.getTime()
                    + ", TERMINAL=" + this.terminal
                    + ", CASHED=" + this.cashed
                    + ", STAFF=" + this.staff
                    + ", CHARGE_ACCOUNT=" + this.chargeAccount
                    + " WHERE SALES.ID=" + this.id;
        } else {
            return "UPDATE SALES"
                    + " SET PRICE=" + this.total
                    + ", CUSTOMER=" + this.customer
                    + ", TIMESTAMP=" + this.date.getTime()
                    + ", TERMINAL=" + this.terminal
                    + ", CASHED=" + this.cashed
                    + ", STAFF=" + this.staff
                    + ", CHARGE_ACCOUNT=" + this.chargeAccount
                    + " WHERE SALES.ID=" + this.id;
        }
    }

    @Override
    public Sale clone() {
        try {
            final Sale result = (Sale) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
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
        return this.id + " - " + this.saleItems.size() + " - £" + this.total.toString();
    }
}
