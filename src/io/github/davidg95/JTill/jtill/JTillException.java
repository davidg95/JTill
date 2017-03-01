/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 * JTillException which can be throws for general errors.
 *
 * @author David
 */
public class JTillException extends Exception {

    /**
     * Constructor for the exception.
     *
     * @param message the reason for the exception.
     */
    public JTillException(String message) {
        super(message);
    }
}
