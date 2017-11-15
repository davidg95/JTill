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
        val = val.replace("-", "");
        if (val.matches("[0-9]+")) {
            return true;
        }
        return false;
    }

    /**
     * Method to check if a string value is an email or not.
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 4;
    }

    public static int CalculateCheckDigit(String barcode) {
        int factor = 3;
        int cumulative = 0;
        for (char c : barcode.toCharArray()) {
            int v = Integer.parseInt(Character.toString(c));
            cumulative = (v * factor);
            if (factor == 3) {
                factor = 1;
            } else {
                factor = 3;
            }
        }
        if (cumulative % 10 == 0) {
            return 0;
        }
        int offset = cumulative % 10;
        return 10 - offset;
    }
}
