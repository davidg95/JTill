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
public class Validator {

    /**
     * Checks that an email contains a . and a @. Also checks that the length is
     * greater than 4.
     *
     * @param email the email to check.
     * @return true if it is valid, false otherwise.
     */
    public static boolean validateEmail(String email) {
        return !(email.length() < 4 || !email.contains("@") || !email.contains("."));
    }

    /**
     * Checks if a phone number is valid by checking that there are no letters.
     *
     * @param phone the phone number to check.
     * @return true if it is valid, false otherwise.
     */
    public static boolean validatePhone(String phone) {
        return phone.matches("[0-9]+");
    }
}
