/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 * VoucherNotFoundException.
 *
 * @author 1301480
 */
public class VoucherNotFoundException extends Exception {

    private final String message;

    public VoucherNotFoundException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
