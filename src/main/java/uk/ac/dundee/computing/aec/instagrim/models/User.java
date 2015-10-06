/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 *
 * @author Administrator
 */
public class User {
    Cluster cluster;
    
    public User(){
        
    }
    
    public boolean RegisterUser(String username, String password) {
        String encodedPassword = null;
        
        try {
            encodedPassword = AeSimpleSHA1.SHA1(password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return false;
        }
        
        Session session = cluster.connect("instagrim");
        // TODO: "if not exists" performance hit, better way?
        PreparedStatement ps = session.prepare("insert into userprofiles (login, password) values(?, ?) if not exists");
       
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute(boundStatement.bind(username, encodedPassword));
        
        return rs.one().getBool("[applied]");
    }
    
    public boolean IsValidUser(String username, String password) {
        String encodedPassword = null;
        
        try {
            encodedPassword = AeSimpleSHA1.SHA1(password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return false;
        }
        
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select password from userprofiles where login =?");
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute(boundStatement.bind(username));
        
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return false;
        } else {
            for (Row row : rs) {       
                String StoredPass = row.getString("password");
                if (StoredPass.compareTo(encodedPassword) == 0)
                    return true;
            }
        }
    
        return false;  
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
}
