/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class Till implements Serializable, Cloneable {

    private int id;
    private UUID uuid;
    private String name;
    private BigDecimal uncashedTakings;
    private boolean connected;
    private Date lastContact;
    private int defaultScreen;

    private boolean sendData;

    public Till(String name, UUID uuid, int defaultScreen) {
        this.name = name;
        this.uncashedTakings = new BigDecimal("0");
        uncashedTakings = uncashedTakings.setScale(2);
        this.uuid = uuid;
        this.defaultScreen = defaultScreen;
    }

    public Till(String name, BigDecimal uncashedTakings, int id, UUID uuid, int defaultScreen) {
        this(name, uuid, defaultScreen);
        this.uncashedTakings = uncashedTakings;
        this.id = id;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUncashedTakings(BigDecimal uncashedTakings) {
        this.uncashedTakings = uncashedTakings;
    }

    public void addTakings(BigDecimal val) {
        this.uncashedTakings = uncashedTakings.add(val);
    }

    /**
     * Checks if the terminal is currently connected. If there is no connection
     * to the server, the last known state will be returned.
     *
     * @return the connection state of the terminal as a boolean.
     */
    public boolean isConnected() {
        try {
            return DataConnect.dataconnect.isTillConnected(id);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING, "Unable to get connection to server, returning last know connection state for terminal " + name, ex);
            return connected;
        }
    }

    /**
     * Request that the terminal download data from server. The terminal will
     * download the data marked in the data[] array.
     *
     * @param data the String array containing the flags for the data to
     * download.
     * @throws IOException if there is a network error.
     */
    public void sendData(String data[]) throws IOException {
        DataConnect.dataconnect.sendData(id, data);
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Date getLastContact() {
        return lastContact;
    }

    public void setLastContact(Date lastContact) {
        this.lastContact = lastContact;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getDefaultScreen() {
        return defaultScreen;
    }

    public void setDefaultScreen(int defaultScreen) {
        this.defaultScreen = defaultScreen;
    }

    public boolean isSendData() {
        return sendData;
    }

    public void setSendData(boolean sendData) {
        this.sendData = sendData;
    }

    /**
     * Get Sales for this terminal.
     *
     * @param uncashedOnly pass in true if only uncashed sales should be
     * returned.
     * @return a List of sales.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public List<Sale> getTerminalSales(boolean uncashedOnly) throws IOException, SQLException {
        return DataConnect.dataconnect.getTerminalSales(id, uncashedOnly);
    }

    /**
     * Save the till to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.dataconnect.updateTill(this);
        } catch (JTillException ex) {
            DataConnect.dataconnect.addTill(this);
        }
    }

    /**
     * Get all the tills.
     *
     * @return a List of all the tills.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public static List<Till> getAll() throws IOException, SQLException {
        return DataConnect.dataconnect.getAllTills();
    }

    /**
     * Reinitialise all connected tills.
     *
     * @throws IOException if there is a network error.
     * @throws JTillException if there are terminals already receiving data.
     */
    public static void reinitAll() throws IOException, JTillException {
        DataConnect.dataconnect.reinitialiseAllTills();
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "','" + this.uuid.toString()
                + "'," + this.uncashedTakings
                + "," + this.defaultScreen;
    }

    public String getSQLUpdateString() {
        return "UPDATE TILLS"
                + " SET NAME='" + this.name
                + "', UUID='" + this.uuid.toString()
                + "', UNCASHED=" + this.uncashedTakings
                + ", DEFAULT_SCREEN=" + this.defaultScreen
                + " WHERE TILLS.ID=" + this.id;
    }

    @Override
    public Till clone() {
        try {
            final Till result = (Till) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Till) {
            if (this.getId() == ((Till) o).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name;
    }
}
