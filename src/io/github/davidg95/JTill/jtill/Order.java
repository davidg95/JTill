/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author David
 */
public class Order {

    private int id;
    private Supplier supplier;
    private boolean sent;
    private Date sendDate;
    private List<OrderItem> items;
    private boolean received;

    public Order(int id, Supplier supplier, boolean sent, Date sendDate, List<OrderItem> items, boolean received) {
        this(supplier, sent, sendDate, items);
        this.id = id;
        this.received = received;
    }

    public Order(Supplier supplier, boolean sent, Date sendDate, List<OrderItem> items) {
        this.supplier = supplier;
        this.sent = sent;
        this.sendDate = sendDate;
        this.items = items;
        received = false;
    }

    public Order(Supplier s, List<OrderItem> items) {
        this.supplier = s;
        this.items = items;
        sent = false;
        this.sendDate = new Date(0);
        received = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getValue() {
        BigDecimal val = BigDecimal.ZERO;
        for (OrderItem i : items) {
            val = val.add(i.getPrice());
        }
        return val;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", supplier=" + supplier + ", sent=" + sent + (sent ? ", sendDate=" + sendDate : "") + ", £" + getValue() + '}';
    }
}
