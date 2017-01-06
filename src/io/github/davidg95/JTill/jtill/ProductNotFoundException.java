/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 * ProductNotFoundException which can be thrown if a product code was not found.
 *
 * @author 1301480
 */
public class ProductNotFoundException extends Exception {

    private final String message;

    public ProductNotFoundException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }

}
