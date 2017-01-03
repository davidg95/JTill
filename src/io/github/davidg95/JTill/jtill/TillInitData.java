/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

/**
 *
 * @author David
 */
public class TillInitData implements Serializable {
    public boolean autoLogout;
    public int logoutTimeout;
    
    public TillInitData(boolean autoLogout, int logoutTimeout){
        this.autoLogout = autoLogout;
        this.logoutTimeout = logoutTimeout;
    }
    
    public boolean isAutoLogout(){
        return autoLogout;
    }
    
    public void setAutoLogout(boolean autoLogout){
        this.autoLogout = autoLogout;
    }
    
    public int getLogoutTimeout(){
        return logoutTimeout;
    }
    
    public void setLogoutTimeout(int logoutTimeout){
        this.logoutTimeout = logoutTimeout;
    }
}
