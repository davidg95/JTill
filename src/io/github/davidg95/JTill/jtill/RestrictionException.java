/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 * RestrictionException which can be thrown if a sale is restricted by age or
 * time.
 *
 * @author David
 */
public class RestrictionException extends Exception {

    private final String message;

    public RestrictionException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

}
