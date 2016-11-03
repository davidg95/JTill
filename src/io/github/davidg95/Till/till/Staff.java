/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

/**
 *
 * @author David
 */
public class Staff {

    private String name;
    private String id;
    private Position position;
    private String username;
    private String password;

    private boolean tillLogin = false;
    private boolean login = false;

    public enum Position {
        ASSISSTANT, SUPERVISOR, MANAGER, AREA_MANAGER
    }

    public Staff(String name, Position position, String username, String password) {
        this.name = name;
        this.position = position;
        this.username = username;
        this.password = password;
    }

    public Staff(String name, Position position, String username, String password, String id) {
        this(name, position, username, password);
        this.id = id;
    }

    public void login() throws LoginException {
        if (!tillLogin) {
            tillLogin = true;
            return;
        }
        throw new LoginException("You are already logged in elsewhere");
    }

    public void login(String password) throws LoginException {
        if (!login) {
            if (this.password.equals(password)) {
                login = true;
                return;
            }
            throw new LoginException("Bad password");
        }
        throw new LoginException("You are already logged in elsewhere");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
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

    @Override
    public String toString() {
        return "ID: " + this.id + "\nName: " + this.name + "\nUsername: " + this.username + "\nPosition: " + this.position.toString();
    }
}
