/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 * DiscountNotFoundException which can be thrown if a discount is now found.
 *
 * @author David
 */
public class DiscountNotFoundException extends Exception {

    private final String message;

    public DiscountNotFoundException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "Discount " + this.message + " could not be found.";
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
