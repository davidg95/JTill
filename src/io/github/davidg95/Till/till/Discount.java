/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import java.util.List;

/**
 *
 * @author David
 */
public class Discount {

    private String id;
    private Type type;
    private double percentage;
    private List<String> barcodes;

    public enum Type {
        PERCENTAGE_OFF, BOGOF, MAX_AND_MATCH
    }

    public Discount(String id, Type type, double percentage, List<String> barcodes) {
        this(type, percentage, barcodes);
        this.id = id;
    }

    public Discount(Type type, double percentage, List<String> barcodes) {
        this.type = type;
        this.percentage = percentage;
        this.barcodes = barcodes;
    }
    
    public void addBarcode(String barcode){
        this.barcodes.add(barcode);
    }
    
    public void removeBarcode(String barcode){
        this.barcodes.remove(barcode);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
    }
    
    @Override
    public String toString() {
        return "ID: " + this.id + "\nType: " + this.type.toString() + "\nPercentage: " + this.percentage + "%\nBarcodes: " + this.barcodes.size();
    }
}
