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
    
    public static TillInitData initData;
    
    public boolean autoLogout;
    public int logoutTimeout;
    public String logonScreenMessage;
    
    public TillInitData(){
        this.autoLogout = false;
        this.logoutTimeout = -1;
        this.logonScreenMessage = "";
    }
    
    static{
        initData = new TillInitData();
    }
    
    public static void staticInit(TillInitData data){
        initData = data;
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

    public String getLogonScreenMessage() {
        return logonScreenMessage;
    }

    public void setLogonScreenMessage(String logonScreenMessage) {
        this.logonScreenMessage = logonScreenMessage;
    }
    
    
}
