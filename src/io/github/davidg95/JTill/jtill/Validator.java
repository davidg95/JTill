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

    public static boolean validateEmail(String email) {
        return !(email.length() < 4 || !email.contains("@") || !email.contains("."));
    }

    public static boolean validatePhone(String phone) {
        return phone.matches("[0-9]+");
    }
}
