/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.stores;

/**
 *
 * @author owner
 */
public class ProfileData {
    
    String firstName;
    String lastName;
    String email;
    
    public void ProfileData(){
        
    }
    
    public void setFirstName(String name){
        this.firstName = name;
    }
    
    public String getFirstName(){
        return this.firstName;
    }
    
    public void setLastName(String name){
        this.lastName = name;
    }
    
    public String getLastName(){
        return this.lastName;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public String getEmail(){
        return this.email;
    }
}
