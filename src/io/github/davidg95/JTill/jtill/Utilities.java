/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

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

    /**
     * Method to calculate the check digit.
     *
     * See https://www.gs1.org/how-calculate-check-digit-manually
     *
     * @param barcode the barcode. Barcodes should be 8, 12, 13 or 14 digits in
     * length.
     * @return the single numerical digit.
     */
    public static int calculateCheckDigit(String barcode) {
        int factor = 3;
        int cumulative = 0;
        char ca[] = barcode.toCharArray();
        for (int i = ca.length - 1; i >= 0; i--) {
            char c = ca[i];
            int v = Integer.parseInt(Character.toString(c));
            cumulative += (v * factor);
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

    /**
     * Method to check if a barcode is valid based on its check digit.
     *
     * See https://www.gs1.org/how-calculate-check-digit-manually
     *
     * @param barcode the barcode to check
     * @return true if it is valid, false if it is not.
     */
    public static boolean validateBarcode(String barcode) {
        if(barcode.length() < 8){
            return true;
        }
        String minusCheck = barcode.substring(0, barcode.length() - 1);
        int checkDigit = Utilities.calculateCheckDigit(minusCheck);
        return barcode.equals(minusCheck + checkDigit);
    }

    /**
     * Check if the barcode is 8 or less or is 12, 13 or 14 digits long.
     *
     * See https://www.gs1.org/how-calculate-check-digit-manually
     *
     * @param barcode the abrcode to check.
     * @return true if it is a valid length, flase if it is not.
     */
    public static boolean validateBarcodeLenth(String barcode) {
        return barcode.length() <= 8 || barcode.length() == 12 || barcode.length() == 13 || barcode.length() == 14;
    }

    private static final KeyStroke escapeStroke
            = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    public static final String dispatchWindowClosingActionMapKey
            = "com.spodding.tackline.dispatch:WINDOW_CLOSING";

    /**
     * Method to add an escape close listener.
     *
     * @param dialog the dialog to add the operation to.
     */
    public static void installEscapeCloseOperation(final JDialog dialog) {
        Action dispatchClosing = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dialog.dispatchEvent(new WindowEvent(
                        dialog, WindowEvent.WINDOW_CLOSING
                ));
            }
        };
        JRootPane root = dialog.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                escapeStroke, dispatchWindowClosingActionMapKey
        );
        root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing
        );
    }
}
