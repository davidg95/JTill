/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 *
 * @author David
 */
public class Utilities {

    /**
     * Method to check if a string value is a number or not.
     *
     * @param val the value to check
     * @return true if it is a number, false otherwise.
     */
    public static boolean isNumber(String val) {
        val = val.replace(".", "");
        if (val.matches("[0-9]+")) {
            return true;
        }
        return false;
    }
}
