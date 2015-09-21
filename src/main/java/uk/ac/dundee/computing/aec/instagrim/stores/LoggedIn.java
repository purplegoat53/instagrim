/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.stores;

/**
 *
 * @author Administrator
 */
public class LoggedIn {
    boolean loggedin = false;
    String username = null;
    
    public void LoggedIn(){
        
    }
    public void setUsername(String name){
        this.username = name;
    }
    public String getUsername(){
        return this.username;
    }
    public void setLoginState(boolean loggedin){
        this.loggedin = loggedin;
    }
    public boolean getLoginState(){
        return this.loggedin;
    }
}
