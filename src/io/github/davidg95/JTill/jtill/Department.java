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
import java.util.List;

/**
 *
 * @author David
 */
public class Department implements Serializable {

    private int id;
    private String name;

    private BigDecimal sales = BigDecimal.ZERO;

    public Department(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Department(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.id;
        return hash;
    }

    public void addToSales(BigDecimal toAdd) {
        sales = sales.add(toAdd);
    }

    public BigDecimal getSales() {
        return sales;
    }

    /**
     * Gets all products in the department.
     *
     * @return a List of all products in the department.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public List<Product> getProductsInDepartment() throws IOException, SQLException {
        return DataConnect.get().getProductsInDepartment(id);
    }

    /**
     * Save the department to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.get().updateDepartment(this);
        } catch (JTillException ex) {
            DataConnect.get().addDepartment(this);
        }
    }

    /**
     * Method to get all the departments from the database.
     *
     * @return a List of all the departments.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public static List<Department> getAll() throws IOException, SQLException {
        return DataConnect.get().getAllDepartments();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Department other = (Department) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }

}
