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

    public static boolean UPPER_AND_LOWER = false;
    public static boolean LETTERS_AND_NUMBERS = false;

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

    /**
     * Method to check a password length.
     *
     * @param password the password.
     * @return true if it is valid, false if it is not.
     */
    public static boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }
        if (UPPER_AND_LOWER) {
            boolean hasUpper = false;
            boolean hasLower = false;
            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    hasUpper = true;
                }
                if (Character.isLowerCase(c)) {
                    hasLower = true;
                }
            }
            if (!hasUpper || !hasLower) {
                return false;
            }
        }
        if (LETTERS_AND_NUMBERS) {
            boolean hasLetter = false;
            boolean hasNumber = false;
            for (char c : password.toCharArray()) {
                if (Character.isLetter(c)) {
                    hasLetter = true;
                }
                if (Character.isDigit(c)) {
                    hasNumber = true;
                }
            }
            if (!hasLetter || !hasNumber) {
                return false;
            }
        }
        return true;
    }
}
