/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * Class which models a member of staff.
 *
 * @author David
 */
public class Staff implements Serializable {

    private String name;
    private int id;
    private int position;
    private String username;
    private String password;
    private double wage;
    private double pay;
    private double hours;
    private boolean enabled;

    /**
     * indicates whether they are currently logged into a till.
     */
    private boolean tillLogin;
    /**
     * Indicated whether they are currently logged in to the system but not a
     * till.
     */
    private boolean login;

    /**
     * Indicates that the staff member is an assistant, this has the least
     * permissions set.
     */
    public static final int ASSISSTANT = 1;
    /**
     * Indicates that the staff member is a supervisor, this has more
     * permissions than an assistant.
     */
    public static final int SUPERVISOR = 2;
    /**
     * Indicates that the staff member is a manger, this has more permissions
     * than a supervisor.
     */
    public static final int MANAGER = 3;
    /**
     * Indicates that the staff member is an area manager, they have full
     * access.
     */
    public static final int AREA_MANAGER = 4;

    /**
     * Constructor for the Staff class which takes in all values but id and
     * logged in values.
     *
     * @param name the name of the staff member.
     * @param position the position they have.
     * @param username their username.
     * @param password their password.
     * @param wage the staff members wage.
     * @param enabled if the account is enabled or not.
     */
    public Staff(String name, int position, String username, String password, double wage, boolean enabled) {
        this.name = name;
        this.position = position;
        this.username = username;
        this.password = password;
        this.wage = wage;
        this.enabled = enabled;
    }

    /**
     * Constructor for the Staff class which takes in all values but logged in
     * values.
     *
     * @param id their id.
     * @param name the name of the staff member.
     * @param position the position they have.
     * @param username their username.
     * @param password their password.
     * @param wage the staff members wage.
     * @param enabled if the account is enabled or not.
     */
    public Staff(int id, String name, int position, String username, String password, double wage, boolean enabled) {
        this(name, position, username, password, wage, enabled);
        this.id = id;
    }

    /**
     * Method to log in to a till.
     *
     * @throws LoginException if they are already logged in somewhere else.
     */
    public void login() throws LoginException {
        if (!tillLogin) {
            tillLogin = true;
            return;
        }
        throw new LoginException("You are already logged in elsewhere");
    }

    /**
     * Method to log in to the system.
     *
     * @param password the staff members password, they will not log in if his
     * does not match.
     * @throws LoginException if the log in was not successful, this could be
     * because the password was not recognised or because they are already
     * logged in else where.
     */
    public void login(String password) throws LoginException {
        if (!login) {
            if (this.password.equals(password)) {
                login = true;
                return;
            }
            throw new LoginException("Your credentials were not recognised");
        }
        throw new LoginException("You are already logged in elsewhere");
    }

    /**
     * Method to log out of the system.
     */
    public void logout() {
        login = false;
    }

    /**
     * Method to log out of a till.
     */
    public void tillLogout() {
        tillLogin = false;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return this.login;
    }

    public boolean isTillLoggedIn() {
        return this.tillLogin;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        this.pay = pay;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Save a member of staff to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.get().updateStaff(this);
        } catch (StaffNotFoundException ex) {
            DataConnect.get().addStaff(this);
        }
    }

    /**
     * Get a list of all the staff members sales.
     *
     * @return a List of the staff members sales.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws StaffNotFoundException if the staff member was not found.
     */
    public List<Sale> getSales() throws IOException, SQLException, StaffNotFoundException {
        return DataConnect.get().getStaffSales(this);
    }

    public String getSQLInsertString() {
        String ePass = Encryptor.encrypt(this.password);
        return "'" + this.name
                + "'," + this.position
                + ",'" + this.username.toLowerCase()
                + "','" + ePass
                + "'," + enabled
                + "," + wage;
    }

    public String getSQLUpdateString() {
        String ePass = Encryptor.encrypt(this.password);
        return "UPDATE STAFF"
                + " SET NAME='" + this.getName()
                + "', POSITION=" + this.getPosition()
                + ", USERNAME='" + this.getUsername().toLowerCase()
                + "', PASSWORD='" + ePass
                + "', ENABLED=" + enabled
                + ", WAGE=" + wage
                + " WHERE STAFF.ID=" + this.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Staff) {
            Staff s = (Staff) o;
            return (this.id == s.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name;
    }
}
