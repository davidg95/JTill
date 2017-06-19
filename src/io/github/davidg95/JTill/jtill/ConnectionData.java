/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

/**
 * Object for storing data to be sent from client to server or vice-versa. The
 * flag indicates that the data is for. The data array contains any data that is
 * being sent.
 *
 * @author David
 */
public class ConnectionData implements Serializable, Cloneable {

    /**
     * The flag to indicate what the data is for.
     */
    private final String flag;
    /**
     * The array which contains any data for the transfer.
     */
    private final Object[] data;

    /**
     * Constructor which creates a ConnectionData object with no data, only a
     * flag.
     *
     * @param flag the flag.
     */
    public ConnectionData(String flag) {
        this.flag = flag;
        this.data = null;
    }

    /**
     * Constructor which creates a ConnectionData object with a flag and data.
     *
     * @param flag the flag to assign.
     * @param data the data to assign.
     */
    public ConnectionData(String flag, Object... data) {
        this.flag = flag;
        this.data = data;
    }

    /**
     * Static method to create a ConnectionData object with a flag and no data.
     *
     * @param flag the flag to use.
     * @return the ConnectionData object.
     */
    public static ConnectionData create(String flag) {
        return new ConnectionData(flag);
    }

    /**
     * Static method to create a ConnectionData object with a flag and data. the
     * amount of data variable and vary.
     *
     * @param flag the flag.
     * @param data the data, this is a variable length parameter.
     * @return the ConnectionData object.
     */
    public static ConnectionData create(String flag, Object... data) {
        return new ConnectionData(flag, data);
    }

    /**
     * Method to get the flag for this object.
     *
     * @return the flag as a String.
     */
    public String getFlag() {
        return flag;
    }

    /**
     * Method to get the data for this object.
     *
     * @return the data as an Object array.
     */
    public Object[] getData() {
        return data;
    }

    /**
     * Method to clone the ConnectionData object.
     *
     * @return a copy of the object.
     * @throws CloneNotSupportedException if cloning is not supported.
     */
    @Override
    public ConnectionData clone() throws CloneNotSupportedException {
        try {
            final ConnectionData result = (ConnectionData) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    /**
     * ToString method which displays the flag and how many data elements there
     * are.
     *
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        return "Flag- " + this.flag
                + "\n Data- " + this.data.length;
    }
}
