/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 * StaffNotFoundException which can be thrown if a staff member was not found.
 *
 * @author David
 */
public class StaffNotFoundException extends Exception {

    private final String id;

    public StaffNotFoundException(String id) {
        super();
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Staff " + id + " could not be found";
    }

    @Override
    public String toString() {
        return "Staff " + id + " could not be found";
    }
}
