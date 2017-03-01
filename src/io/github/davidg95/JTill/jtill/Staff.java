/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

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

    public Staff(String name, int position, String username, String password) {
        this.name = name;
        this.position = position;
        this.username = username;
        this.password = password;
    }

    public Staff(int id, String name, int position, String username, String password) {
        this(name, position, username, password);
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

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.position
                + ",'" + this.username.toLowerCase()
                + "','" + this.password + "'";
    }

    public String getSQLUpdateString() {
        return "UPDATE STAFF"
                + " SET NAME='" + this.getName()
                + "', POSITION=" + this.getPosition()
                + ", USERNAME='" + this.getUsername().toLowerCase()
                + "', PASSWORD='" + this.getPassword()
                + "' WHERE STAFF.ID=" + this.getId();
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
        return "ID: " + this.id + " Name: " + this.name;
    }
}
