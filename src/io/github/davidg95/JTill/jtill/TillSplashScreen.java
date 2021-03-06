/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.awt.GraphicsEnvironment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 *
 * @author 1301480
 */
public class TillSplashScreen extends JWindow {

    private static final TillSplashScreen WINDOW;
    private final JProgressBar bar;
    private final JLabel prgLabel;

    private static final boolean HEADLESS;

    public TillSplashScreen() {
        JLabel label = new JLabel();
        bar = new JProgressBar();
        prgLabel = new JLabel();
        prgLabel.setText("Starting JTill Server...");
        label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/davidg95/JTill/resources/splashIcon.png")));
        JPanel pan = new JPanel();
        pan.add(label);
        pan.add(bar);
        pan.add(prgLabel);
        add(pan);
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        setBounds(500, 150, 500, 320);
        setLocationRelativeTo(null);
    }

    static {
        HEADLESS = GraphicsEnvironment.isHeadless();
        if (!HEADLESS) {
            WINDOW = new TillSplashScreen();
        } else {
            WINDOW = null;
        }
    }

    public static void showSplashScreen() {
        if (!HEADLESS) {
            WINDOW.setVisible(true);
        }
    }

    public static void hideSplashScreen() {
        if (!HEADLESS) {
            WINDOW.setVisible(false);
        }
    }

    private void setPrgLabel(String text) {
        if (!HEADLESS) {
            prgLabel.setText(text);
        }
    }

    public static void setLabel(String text) {
        if (!HEADLESS) {
            WINDOW.setPrgLabel(text);
        }
    }

    private void addToBar(int val) {
        if (!HEADLESS) {
            bar.setValue(bar.getValue() + val);
            bar.repaint();
        }
    }

    public static void addBar(int val) {
        if (!HEADLESS) {
            WINDOW.addToBar(val);
        }
    }

}
