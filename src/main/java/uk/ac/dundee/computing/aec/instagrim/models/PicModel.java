package uk.ac.dundee.computing.aec.instagrim.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 CREATE TABLE Tweets (
 user varchar,
 interaction_time timeuuid,
 tweet varchar,
 PRIMARY KEY (user,interaction_time)
 ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;

import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
//import uk.ac.dundee.computing.aec.stores.TweetStore;

public class PicModel {

    Cluster cluster;

    public void PicModel() {

    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public void insertPic(byte[] rawData, String type, String name, String user) {
        String types[] = Convertors.SplitFiletype(type);
        java.util.UUID picID = Convertors.getTimeUUID();

        ByteBuffer rawBuf = ByteBuffer.wrap(rawData);
        
        byte [] thumbData = resizePic(rawData, types[1]);
        ByteBuffer thumbBuf = ByteBuffer.wrap(thumbData);
        
        byte[] processedData = decolourPic(rawData, types[1]);
        ByteBuffer processedBuf = ByteBuffer.wrap(processedData);
        
        try (Session session = cluster.connect("instagrim")) {
            PreparedStatement psInsertPic = session.prepare("insert into pics (picid, image, thumb, processed, user, interaction_time, imagelength, thumblength, processedlength, type, name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertPicToUser = session.prepare("insert into userpiclist (picid, user, pic_added) values(?,?,?)");
            
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            BoundStatement bsInsertPicToUser = new BoundStatement(psInsertPicToUser);
            
            Date dateAdded = new Date();
            session.execute(bsInsertPic.bind(picID, rawBuf, thumbBuf, processedBuf, user, dateAdded, rawData.length, thumbData.length, processedData.length, type, name));
            session.execute(bsInsertPicToUser.bind(picID, user, dateAdded));
        }
    }
    
    public void removePic(java.util.UUID picid, String user) {
        try (Session session = cluster.connect("instagrim")) {
            PreparedStatement psGetPicUser = session.prepare("select user, interaction_time from pics where picid=?");
            BoundStatement bsGetPicUser = new BoundStatement(psGetPicUser);
            ResultSet rs = session.execute(bsGetPicUser.bind(picid));
            if(rs.isExhausted()) {
                return; //TODO: proper error reporting
            }

            // there shouldn't be more than one
            Row row = rs.one();
            String actual_user = row.getString("user");
            if(!actual_user.equals(user)) {
                return;
            }

            PreparedStatement psDeletePic = session.prepare("delete from pics where picid=?");
            PreparedStatement psDeletePicFromUser = session.prepare("delete from userpiclist where user=? and pic_added=?");
            BoundStatement bsDeletePic = new BoundStatement(psDeletePic);
            BoundStatement bsDeletePicFromUser = new BoundStatement(psDeletePicFromUser);

            session.execute(bsDeletePic.bind(picid));
            session.execute(bsDeletePicFromUser.bind(user, row.getDate("interaction_time")));
        }
    }

    public byte[] resizePic(byte [] rawData, String type) {
        try {
            InputStream inputStream = new ByteArrayInputStream(rawData);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            BufferedImage bufferedThumbnail = createThumbnail(bufferedImage);
            
            byte[] imageInByte;
            try (ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedThumbnail, type, thumbnailStream);
                thumbnailStream.flush();
                imageInByte = thumbnailStream.toByteArray();
            }
            
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }
    
    public byte[] decolourPic(byte [] rawData, String type) {
        try {
            InputStream inputStream = new ByteArrayInputStream(rawData);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            BufferedImage bufferedProcessed = createProcessed(bufferedImage);
            
            byte[] imageInByte;
            try (ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedProcessed, type, thumbnailStream);
                thumbnailStream.flush();
                imageInByte = thumbnailStream.toByteArray();
            }
            
            return imageInByte;
        } catch (IOException et) {
            
        }
        return null;
    }
    
    public static final int THUMB_SIZE = 128;
    
    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Method.SPEED, THUMB_SIZE, OP_ANTIALIAS, OP_GRAYSCALE);
        
        // square the image and add black borders
        
        BufferedImage result = new BufferedImage(THUMB_SIZE, THUMB_SIZE, BufferedImage.TYPE_INT_RGB);
        
        Graphics g = result.getGraphics();
        g.setColor(java.awt.Color.BLACK);
        g.fillRect(0, 0, THUMB_SIZE, THUMB_SIZE);
        
        int x = (THUMB_SIZE / 2) - (img.getWidth() / 2);
        int y = (THUMB_SIZE / 2) - (img.getHeight() / 2);
        
        g.drawImage(img, x, y, null);
        g.dispose();
        
        return apply(result, OP_ANTIALIAS, OP_GRAYSCALE);
    }
    
    public static BufferedImage createProcessed(BufferedImage img) {
        int width = img.getWidth()-1;
        return resize(img, Method.SPEED, width, OP_ANTIALIAS, OP_GRAYSCALE);
    }
   
    public java.util.LinkedList<Pic> getPicsForUser(String user) {
        try(Session session = cluster.connect("instagrim")) {
            java.util.LinkedList<Pic> pics = new java.util.LinkedList<>();
            
            PreparedStatement ps = session.prepare("select picid from userpiclist where user=?");
            BoundStatement boundStatement = new BoundStatement(ps);
            ResultSet rs = session.execute(boundStatement.bind(user));

            if (rs.isExhausted()) {
                return null;
            } else {
                for (Row row : rs) {
                    Pic pic = new Pic();
                    pic.setUUID(row.getUUID("picid"));
                    pics.add(pic);
                }
            }

            return pics;
        }
    }

    public Pic getPic(int imageType, java.util.UUID picID) {
        try(Session session = cluster.connect("instagrim")) {
            ByteBuffer imageBuf = null;
            String type = null;
            int length = 0;
            
            PreparedStatement ps = null;
            if (imageType == Convertors.DISPLAY_IMAGE) {
                ps = session.prepare("select image, imagelength, type from pics where picid=?");
            } else if (imageType == Convertors.DISPLAY_THUMB) {
                ps = session.prepare("select thumb, imagelength, thumblength, type from pics where picid=?");
            } else if (imageType == Convertors.DISPLAY_PROCESSED) {
                ps = session.prepare("select processed,processedlength,type from pics where picid=?");
            }

            BoundStatement boundStatement = new BoundStatement(ps);
            ResultSet rs = session.execute(boundStatement.bind(picID));

            if (rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : rs) {
                    if (imageType == Convertors.DISPLAY_IMAGE) {
                        imageBuf = row.getBytes("image");
                        length = row.getInt("imagelength");
                    } else if (imageType == Convertors.DISPLAY_THUMB) {
                        imageBuf = row.getBytes("thumb");
                        length = row.getInt("thumblength");
                    } else if (imageType == Convertors.DISPLAY_PROCESSED) {
                        imageBuf = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }

                    type = row.getString("type");
                }
            }
            
            Pic pic = new Pic();
            pic.setPic(imageBuf, length, type);
            return pic;
        }
    }

}
