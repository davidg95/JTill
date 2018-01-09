/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.util.UUID;

/**
 *
 * @author David
 */
public interface GUIInterface {

    public void log(Object o);

    public void logWarning(Object o);

    public void showMessage(String title, String message);

    public boolean showYesNoMessage(String title, String message);

    public void showModalMessage(String title, String message);

    public void hideModalMessage();

    public void addTill(Till t);

    public void allow(Till t);

    public void disallow();

    public void updateTills();

    public void connectionDrop();

    public Staff connectionReestablish();

    public void initTill();

    public void renameTill(String name);

    public Till showTillSetupWindow(String name, UUID uuid) throws JTillException;

    public void logout();

    public void requestUpdate();

    public void markNewData(String[] data);

    public void setClientLabel(int count);
}
