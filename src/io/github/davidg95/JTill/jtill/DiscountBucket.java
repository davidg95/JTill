/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.util.List;

/**
 * Class which models a discounts trigger bucket. If any bucket in a discount is
 * activated, then the discount applies to the sale.
 *
 * @author David
 */
public class DiscountBucket implements Serializable {

    private int id; //The bucket id.
    private int discount; //The discount the bucket applies to.
    private int requiredTriggers; //The number of required triggers to activate the discount.
    private int currentTriggers;
    
    private List<Trigger> triggers;

    /**
     * Creates a discount bucket.
     *
     * @param discount the discount the bucket is for.
     * @param requiredTriggers the number of triggers to activate the bucket.
     */
    public DiscountBucket(int discount, int requiredTriggers) {
        this.discount = discount;
        this.requiredTriggers = requiredTriggers;
    }

    /**
     * Creates a discount bucket.
     *
     * @param id the bucket id.
     * @param discount the discount the bucket is for.
     * @param requiredTriggers the number of triggers to activate the bucket.
     */
    public DiscountBucket(int id, int discount, int requiredTriggers) {
        this(discount, requiredTriggers);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getRequiredTriggers() {
        return requiredTriggers;
    }

    public void setRequiredTriggers(int requiredTriggers) {
        this.requiredTriggers = requiredTriggers;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }
    
    public void addHit(){
        currentTriggers++;
    }
    
    public void reset(){
        currentTriggers = 0;
    }

    public int getCurrentTriggers() {
        return currentTriggers;
    }

    public void setCurrentTriggers(int currentTriggers) {
        this.currentTriggers = currentTriggers;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DiscountBucket other = (DiscountBucket) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "DiscountBucket{" + "id=" + id + ", discount=" + discount + ", requiredTriggers=" + requiredTriggers + '}';
    }

}
