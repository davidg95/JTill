/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

/**
 *
 * @author David
 */
public class ConnectionData implements Serializable, Cloneable {

    private final String flag;
    private final Object[] data;

    public ConnectionData(String flag, Object data) {
        this(flag, new Object[]{data});
    }

    public ConnectionData(String flag) {
        this(flag, null);
    }

    public ConnectionData(String flag, Object[] data) {
        this.flag = flag;
        this.data = data;
    }

    public static ConnectionData create(String flag) {
        return new ConnectionData(flag);
    }

    public static ConnectionData create(String flag, Object[] data) {
        return new ConnectionData(flag, data);
    }
    
    public static ConnectionData create(String flag, Object data){
        return new ConnectionData(flag, data);
    }

    public String getFlag() {
        return flag;
    }

    public Object[] getData() {
        return data;
    }

    @Override
    public ConnectionData clone() throws CloneNotSupportedException {
        try {
            final ConnectionData result = (ConnectionData) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Flag- " + this.flag
                + "\n Data- " + this.data.length;
    }
}
