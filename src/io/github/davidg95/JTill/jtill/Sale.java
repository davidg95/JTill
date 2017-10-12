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
    private volatile int terminal;
    private boolean cashed;
    private int staff;
    private int mop;

    private Customer c;
    private Till t;
    private Staff s;

    private SaleItem lastAdded;

    private final List<ProductListener> listeners;

    public static final int MOP_CASH = 1;
    public static final int MOP_CARD = 2;
    public static final int MOP_CHARGEACCOUNT = 3;
    public static final int MOP_CHEQUE = 4;

    /**
     * Constructor which creates a new sale with no items and with the total set
     * to £0.00.
     *
     * @param terminal the name of the terminal the sale is done it.
     * @param s the staff member the sale is done by.
     */
    public Sale(int terminal, int s) {
        saleItems = new ArrayList<>();
        total = BigDecimal.ZERO;
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
        //Check if it is a refund item.
        boolean isRefundItem = (quantity < 0);
        //Check if it is a product or discount.
        int type;
        if (i instanceof Product) {
            type = SaleItem.PRODUCT;
        } else {
            type = SaleItem.DISCOUNT;
        }
        //First check if the item has already been added
        for (SaleItem item : saleItems) {
            if (item.getItem() == i.getId() && item.getType() == type && item.isRefundItem() == isRefundItem) {
                //The item has been added
                if (i.isOpen()) { //Check if it is open price.
                    if (i.getPrice().compareTo(item.getPrice()) == 0) { //Check if it is the same price.
                        item.increaseQuantity(quantity); //Increace the quantity.
                        this.total = total.add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))); //Update the sale total.
                        if (type == SaleItem.PRODUCT) {
                            final Product product = (Product) i;
                            final BigDecimal money = (i.getPrice().subtract(product.getCostPrice())).setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                            final BigDecimal cost = product.getOpenCost();
                            BigDecimal taxValue = product.calculateVAT();
                            this.lastAdded = new SaleItem(this.id, i.getId(), quantity, i.getPrice(), type, taxValue, cost); //Set this item to the last added.
                        } else {
                            this.lastAdded = new SaleItem(this.id, i.getId(), quantity, i.getPrice(), type, BigDecimal.ZERO, BigDecimal.ZERO); //Set this item to the last added.
                        }
                        updateTotal(); //Update the total.
                        return true;
                    }
                } else {
                    //Product is not open price and does already exist
                    item.increaseQuantity(quantity);
                    item.setName(i.getName()); //Set the name for the list box.
                    if (type == SaleItem.PRODUCT) {
                        final Product product = (Product) i;
                        item.setTotalPrice(product.getSellingPrice().multiply(new BigDecimal(item.getQuantity())).setScale(2).toString()); //Set the total for the list box.
                        this.total = total.add(product.getSellingPrice().multiply(new BigDecimal(quantity))); //Update the total for this sale.
                        final BigDecimal cost = product.getIndividualCost().multiply(new BigDecimal(quantity));
                        final BigDecimal vat = product.calculateVAT().multiply(new BigDecimal(quantity));
                        this.lastAdded = new SaleItem(this.id, i.getId(), quantity, product.getSellingPrice(), type, vat, cost); //Set this item to the last added.
                    } else {
                        item.setTotalPrice(i.getPrice().multiply(new BigDecimal(item.getQuantity())).setScale(2).toString()); //Set the total for the list box.
                        this.total = total.add(item.getPrice().multiply(new BigDecimal(quantity))); //Update the total for this sale.
                        this.lastAdded = new SaleItem(this.id, i.getId(), quantity, i.getPrice(), type, BigDecimal.ZERO, BigDecimal.ZERO); //Set this item to the last added.
                    }
                    return true;
                }
            }
        }
        //If the item is not already in the sale
        SaleItem item;
        if (type == SaleItem.PRODUCT) {
            final Product product = (Product) i;
            this.total = total.add(product.getSellingPrice().multiply(new BigDecimal(quantity))); //Update the sale total.
            final BigDecimal cost = product.getIndividualCost();
            final BigDecimal vat = product.calculateVAT().multiply(new BigDecimal(quantity));
            item = new SaleItem(this.id, i.getId(), quantity, product.getSellingPrice(), type, vat, cost); //Set this item to the last added.
            item.setTotalPrice(product.getSellingPrice().multiply(new BigDecimal(item.getQuantity())).setScale(2).toString()); //Set the total of the item for the list box.
        } else {
            item = new SaleItem(this.id, i.getId(), quantity, i.getPrice(), type, BigDecimal.ZERO, BigDecimal.ZERO); //Set this item to the last added.
            item.setTotalPrice(i.getPrice().multiply(new BigDecimal(item.getQuantity())).setScale(2).toString()); //Set the total of the item for the list box.
            this.total = total.add(item.getPrice().multiply(new BigDecimal(quantity))); //Update the sale total.
        }
        item.setRefundItem(isRefundItem); //Indicate if it is a refund item or not
        item.setName(i.getName()); //Set the name of the item for the list box.
        saleItems.add(item); //Add the item to the list of sale items.
        this.lastAdded = item; //Set this item to the last item added.
        updateTotal(); //Update the total.
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
            if (i.equals(item)) {
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

    public int getCustomerID() {
        return customer;
    }

    public void setCustomerID(int customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return c;
    }

    public void setCustomer(Customer c) {
        this.c = c;
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

    public int getMop() {
        return mop;
    }

    public void setMop(int mop) {
        this.mop = mop;
    }

    public int getTerminalID() {
        return terminal;
    }

    public void setTerminalID(int terminal) {
        this.terminal = terminal;
    }

    public Till getTerminal() {
        return t;
    }

    public void setTerminal(Till t) {
        this.t = t;
        this.terminal = t.getId();
    }

    public boolean isCashed() {
        return cashed;
    }

    public void setCashed(boolean cashed) {
        this.cashed = cashed;
    }

    public int getStaffID() {
        return staff;
    }

    public void setStaffID(int staff) {
        this.staff = staff;
    }

    public Staff getStaff() {
        return s;
    }

    public void setStaff(Staff s) {
        this.s = s;
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
        if (listeners != null) {
            listeners.forEach((pl) -> {
                pl.killListener();
            });
        }
    }

    public String getSQLInsertStatement() {
        if (this.customer == 0) { //If no customer was assigned then set the customer ID to -1
            return this.total
                    + ",-1"
                    + "," + this.date.getTime()
                    + "," + this.terminal
                    + "," + this.cashed
                    + "," + this.staff
                    + "," + this.mop;
        } else {
            return this.total
                    + "," + this.customer
                    + "," + this.date.getTime()
                    + "," + this.terminal
                    + "," + this.cashed
                    + "," + this.staff
                    + "," + this.mop;
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
                    + ", MOP=" + this.mop
                    + " WHERE SALES.ID=" + this.id;
        } else {
            return "UPDATE SALES"
                    + " SET PRICE=" + this.total
                    + ", CUSTOMER=" + this.customer
                    + ", TIMESTAMP=" + this.date.getTime()
                    + ", TERMINAL=" + this.terminal
                    + ", CASHED=" + this.cashed
                    + ", STAFF=" + this.staff
                    + ", MOP=" + this.mop
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
