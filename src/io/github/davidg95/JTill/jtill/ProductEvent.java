/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 * Models a ProductEvent of a product getting added to a sale.
 *
 * @author David
 */
public class ProductEvent {

    private final Product product;

    /**
     * Creates a new ProductEvent.
     *
     * @param p the product that was added.
     */
    public ProductEvent(Product p) {
        this.product = p;
    }

    /**
     * Gets the product that was added.
     *
     * @return the product that was added.
     */
    public Product getProduct() {
        return product;
    }

    @Override
    public String toString() {
        return "ProductEvent{" + "product=" + product + '}';
    }
}
