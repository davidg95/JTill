/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 * CustomerNotFoundException which can be thrown if a customer could not be
 * found.
 *
 * @author David
 */
public class CustomerNotFoundException extends Exception{
    
    private final String id;
    
    public CustomerNotFoundException(String id){
        super();
        this.id = id;
    }
    
    @Override
    public String getMessage(){
        return "Customer " + id + " could not be found";
    }
    
    @Override
    public String toString(){
        return "Customer " + id + " could not be found";
    }

}
