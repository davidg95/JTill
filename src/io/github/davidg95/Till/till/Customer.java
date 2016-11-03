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

    private String name;
    private String address;
    private String id;
    private String phone;

    public Customer() {

    }

    public Customer(String name, String address, String id, String phone) {
        this(name, address, phone);
        this.id = id;
    }
    
    public Customer(String name, String address, String phone){
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
    
    @Override
    public String toString(){
        return "ID: " + this.id + "\nName: " + this.name + "\nAddress: " + this.address + "\nPhone Number: " + this.phone;
    }

}
