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

/**
 * Class which models a customer.
 *
 * @author David
 */
public class Customer implements Serializable {

    private String id;
    private String name;
    private String phone;
    private String mobile;
    private String email;
    private String notes;

    private String addressLine1;
    private String addressLine2;
    private String town;
    private String county;
    private String country;
    private String postcode;

    private BigDecimal moneyDue;
    private BigDecimal maxDebt;

    /**
     * Constructor which takes in all values except for the id.
     *
     * @param name the customers name.
     * @param phone the customers phone number.
     * @param mobile the customers mobile number.
     * @param email the customers email.
     * @param addressLine1 the customers address.
     * @param addressLine2 the customers address.
     * @param town the town.
     * @param county the county.
     * @param country the country.
     * @param postcode the postcode.
     * @param notes any notes.
     * @param moneyDue how much money they are due.
     * @param maxDebt what the maximum debt is for the customer.
     */
    public Customer(String name, String phone, String mobile, String email, String addressLine1, String addressLine2, String town, String county, String country, String postcode, String notes, BigDecimal moneyDue, BigDecimal maxDebt) {
        this.name = name;
        this.phone = phone;
        this.mobile = mobile;
        this.email = email;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.town = town;
        this.county = county;
        this.country = country;
        this.postcode = postcode;
        this.notes = notes;
        this.moneyDue = moneyDue;
        this.maxDebt = maxDebt;
    }

    /**
     * Constructor which takes in all values.
     *
     * @param id the customers ID.
     * @param name the customers name.
     * @param phone the customers phone number.
     * @param mobile the customers mobile number.
     * @param email the customers email.
     * @param addressLine1 the customers address.
     * @param addressLine2 the customers address.
     * @param town the town.
     * @param county the county.
     * @param country the country.
     * @param postcode the postcode.
     * @param notes any notes.
     * @param moneyDue how much money they are due.
     * @param maxDebt what the maximum debt is for the customer.
     */
    public Customer(String id, String name, String phone, String mobile, String email, String addressLine1, String addressLine2, String town, String county, String country, String postcode, String notes, BigDecimal moneyDue, BigDecimal maxDebt) {
        this(name, phone, mobile, email, addressLine1, addressLine2, town, county, country, postcode, notes, moneyDue, maxDebt);
        this.id = id;
    }

    /**
     * Method to add money to the amount the customer is due.
     *
     * @param moneyDue the money to add.
     * @return the money they are now due.
     * @throws JTillException if the customers maximum debt has been exceeded.
     */
    public BigDecimal addMoneyDue(BigDecimal moneyDue) throws JTillException {
        if (this.moneyDue.add(moneyDue).compareTo(this.maxDebt) > 0 && this.maxDebt.compareTo(BigDecimal.ZERO) != 0) {
            throw new JTillException("Maximum debt for customer exceeded");
        }
        this.moneyDue = this.moneyDue.add(moneyDue);
        return this.moneyDue;
    }

    /**
     * Method to remove money from the amount the customer is due.
     *
     * @param remove the money to remove.
     * @return the amount they are now due.
     * @throws JTillException if the customer owes less than the amount entered.
     */
    public BigDecimal removeMoneyDue(BigDecimal remove) throws JTillException {
        if (this.moneyDue.compareTo(remove) == -1) {
            throw new JTillException("They do not owe that much!");
        }
        this.moneyDue = this.moneyDue.subtract(remove);
        return this.moneyDue;
    }

    /**
     * Method to reset the amount of money the customer is due to zero.
     */
    public void resetMoneyDue() {
        this.moneyDue = BigDecimal.ZERO;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public BigDecimal getMoneyDue() {
        return moneyDue;
    }

    public void setMoneyDue(BigDecimal moneyDue) {
        this.moneyDue = moneyDue;
    }

    public BigDecimal getMaxDebt() {
        return maxDebt;
    }

    public void setMaxDebt(BigDecimal maxDebt) {
        this.maxDebt = maxDebt;
    }

    /**
     * Save a customer to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.get().updateCustomer(this);
        } catch (CustomerNotFoundException ex) {
            DataConnect.get().addCustomer(this);
        }
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "','" + this.id
                + "','" + this.phone
                + "','" + this.mobile
                + "','" + this.email
                + "','" + this.addressLine1
                + "','" + this.addressLine2
                + "','" + this.town
                + "','" + this.county
                + "','" + this.country
                + "','" + this.postcode
                + "','" + this.notes
                + "','" + this.moneyDue.doubleValue()
                + "','" + this.maxDebt.doubleValue() + "'";
    }

    public String getSQLUpdateString() {
        return "UPDATE CUSTOMERS"
                + " SET NAME='" + this.getName()
                + "', PHONE='" + this.getPhone()
                + "', MOBILE='" + this.getMobile()
                + "', EMAIL='" + this.getEmail()
                + "', ADDRESS_LINE_1='" + this.getAddressLine1()
                + "', ADDRESS_LINE_2='" + this.getAddressLine2()
                + "', TOWN='" + this.getTown()
                + "', COUNTY='" + this.getCounty()
                + "', COUNTRY='" + this.getCountry()
                + "', POSTCODE='" + this.getPostcode()
                + "', NOTES='" + this.getNotes()
                + "', MONEY_DUE=" + this.getMoneyDue().doubleValue()
                + ", MAX_DEBT=" + this.getMaxDebt().doubleValue()
                + " WHERE CUSTOMERS.ID='" + this.getId() + "'";
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name;
    }

}
