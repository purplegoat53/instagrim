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
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.stores.ProfileData;

/**
 *
 * @author Administrator
 */
public class User {
    Cluster cluster;
    
    public User() {
        
    }
    
    public boolean registerUser(String username, String password) {
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
    
    public boolean isValidUser(String username, String password) {
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
    
    public void setAvatar(String username, byte[] rawData, String contentType) {
        try (Session session = cluster.connect("instagrim")) {
            ByteBuffer rawBuf = ByteBuffer.wrap(rawData);
                    
            PreparedStatement psSetAvatar = session.prepare("update userprofiles set avatar=?, avatarlength=?, avatartype=? where login=?");
            BoundStatement bsSetAvatar = new BoundStatement(psSetAvatar);
            session.execute(bsSetAvatar.bind(rawBuf, rawData.length, contentType, username));
        }
    }
    
    public Pic getAvatar(String username) {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select avatar, avatarlength, avatartype from userprofiles where login=?");
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute(boundStatement.bind(username));
        
        if(rs.isExhausted())
            return null;
        
        Row row = rs.one();
        ByteBuffer bb = row.getBytes("avatar");
        if(bb == null)
            return null;
        
        Pic pic = new Pic();
        pic.setPic(bb, row.getInt("avatarlength"), row.getString("avatartype"));
        return pic;
    }
    
    public void setBasicInfo(String username, String firstName, String lastName, String email) {
        try (Session session = cluster.connect("instagrim")) {
            PreparedStatement psSetBasic = session.prepare("update userprofiles set first_name=?, last_name=?, email=? where login=?");
            BoundStatement bsSetBasic = new BoundStatement(psSetBasic);
            session.execute(bsSetBasic.bind(firstName, lastName, email, username));
        }
    }
    
    public ProfileData getBasicInfo(String username) {
        try (Session session = cluster.connect("instagrim")) {
            PreparedStatement psGetBasic = session.prepare("select first_name, last_name, email, privacy from userprofiles where login=?");
            BoundStatement bsGetBasic = new BoundStatement(psGetBasic);
            ResultSet rs = session.execute(bsGetBasic.bind(username));
            
            if(rs.isExhausted())
                return null;
            
            Row row = rs.one();
            ProfileData profile = new ProfileData();
            profile.setFirstName(row.getString("first_name"));
            profile.setLastName(row.getString("last_name"));
            profile.setEmail(row.getString("email"));
            profile.setPrivacy(row.getInt("privacy"));
            return profile;
        }
    }
    
    public void setPrivacy(String username, int privacy)
    {
        try (Session session = cluster.connect("instagrim")) {
            PreparedStatement psSetPrivacy = session.prepare("update userprofiles set privacy=? where login=?");
            BoundStatement bsSetPrivacy = new BoundStatement(psSetPrivacy);
            session.execute(bsSetPrivacy.bind(privacy, username));
        }
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
}
