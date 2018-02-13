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
 * Class which models a category.
 *
 * @author David
 */
public class Category implements Serializable {

    private int ID;
    private Department department;
    private String name;

    private BigDecimal sales = BigDecimal.ZERO;

    /**
     * Constructor which takes in all values.
     *
     * @param ID the id.
     * @param name the name.
     * @param dep the department the category belongs to.
     */
    public Category(int ID, String name, Department dep) {
        this(name, dep);
        this.ID = ID;
    }

    /**
     * Constructor which takes in all values except id.
     *
     * @param name the name.
     * @param dep the department the category belongs to.
     */
    public Category(String name, Department dep) {
        this.name = name;
        this.department = dep;
    }

    public int getId() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addToSales(BigDecimal toAdd) {
        sales = sales.add(toAdd);
    }

    public BigDecimal getSales() {
        return sales;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Gets all products in the category.
     *
     * @return a List of all products in the category.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws JTillException if the category was not found.
     */
    public List<Product> getProductsInCategory() throws IOException, SQLException, JTillException {
        return DataConnect.get().getProductsInCategory(ID);
    }

    /**
     * Save the category to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.get().updateCategory(this);
        } catch (JTillException ex) {
            DataConnect.get().addCategory(this);
        }
    }

    /**
     * Method to get all the categories.
     *
     * @return a List of all the categories.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public static List<Category> getAll() throws IOException, SQLException {
        return DataConnect.get().getAllCategorys();
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.department.getId();
    }

    public String getSQLUpdateString() {
        return "UPDATE CATEGORYS"
                + " SET NAME='" + this.getName()
                + "', DEPARTMENT=" + this.getDepartment().getId()
                + " WHERE CATEGORYS.ID=" + this.getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.ID;
        return hash;
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
        final Category other = (Category) obj;
        return this.ID == other.ID;
    }

    @Override
    public String toString() {
        return this.ID + " - " + this.name;
    }
}
