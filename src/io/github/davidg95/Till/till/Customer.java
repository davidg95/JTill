/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

/**
 * Class which models a customer.
 *
 * @author David
 */
public class Customer {

    private String id;
    private String name;
    private String phone;
    private String mobile;
    private String email;
    private String discount_id;
    private int loyaltyPoints;
    private String notes;

    private String addressLine1;
    private String addressLine2;
    private String town;
    private String county;
    private String country;
    private String postcode;

    public Customer() {

    }

    public Customer(String name, String phone, String mobile, String email, String discount, String addressLine1, String addressLine2, String town, String county, String country, String postcode, String notes, int loyalty) {
        this.name = name;
        this.phone = phone;
        this.mobile = mobile;
        this.email = email;
        this.discount_id = discount;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.town = town;
        this.county = county;
        this.country = country;
        this.postcode = postcode;
        this.notes = notes;
        this.loyaltyPoints = loyalty;
    }

    public Customer(String name, String phone, String mobile, String email, String discount, String addressLine1, String addressLine2, String town, String county, String country, String postcode, String notes, int loyaltyPoints, String id) {
        this(name, phone, mobile, email, discount, addressLine1, addressLine2, town, county, country, postcode, notes, loyaltyPoints);
        this.id = id;
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

    public String getDiscountID() {
        return discount_id;
    }

    public void setDiscountID(String discount) {
        this.discount_id = discount;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Customer) {
            if (this.id.equals(((Customer) o).getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + "\nName: " + this.name + "\nPhone Number: " + this.phone;
    }

}
