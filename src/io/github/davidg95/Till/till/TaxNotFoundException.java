/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

/**
 * TaxNotFoundException.
 *
 * @author David
 */
public class TaxNotFoundException extends Exception {

    private final String id;

    public TaxNotFoundException(String id) {
        super();
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Tax ID " + this.id + " could not be found";
    }

    @Override
    public String toString() {
        return "Tax ID " + this.id + " could not be found";
    }
}
