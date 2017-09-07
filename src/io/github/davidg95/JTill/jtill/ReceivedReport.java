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
    private final int supplierId;
    private List<ReceivedItem> items;

    public ReceivedReport(int id, String invoiceId, int supplierId, List<ReceivedItem> items) {
        this(invoiceId, supplierId);
        this.items = items;
        this.id = id;
    }

    public ReceivedReport(String invoiceId, int supplierId) {
        this.invoiceId = invoiceId;
        this.supplierId = supplierId;
        items = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public int getSupplierId() {
        return supplierId;
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

    @Override
    public String toString() {
        return this.invoiceId;
    }

}
