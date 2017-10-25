/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author David
 */
public class ReceivedReport implements Serializable {

    private int id;
    private final String invoiceId;
    private final Supplier supplier;
    private List<ReceivedItem> items;
    private boolean paid;

    public ReceivedReport(int id, String invoiceId, Supplier supplier, boolean paid) {
        this(invoiceId, supplier);
        this.id = id;
        this.paid = paid;
    }

    public ReceivedReport(String invoiceId, Supplier supplier) {
        this.invoiceId = invoiceId;
        this.supplier = supplier;
        items = new LinkedList<>();
        paid = false;
    }

    public int getId() {
        return id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public List<ReceivedItem> getItems() {
        return items;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItems(List<ReceivedItem> items) {
        this.items = items;
    }

    public void addItem(ReceivedItem item) {
        this.items.add(item);
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public String toString() {
        return this.invoiceId;
    }

}
