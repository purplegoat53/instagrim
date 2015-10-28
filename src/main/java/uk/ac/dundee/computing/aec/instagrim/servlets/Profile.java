/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.ProfileData;

/**
 *
 * @author owner
 */
@WebServlet(name = "Profile", urlPatterns = {"/Settings"})
@MultipartConfig

public class Profile extends HttpServlet {

    private Cluster cluster;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Profile() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        if(lg == null || !lg.getLoginState())
            return;
        
        User user = new User();
        user.setCluster(cluster);
        ProfileData profile = user.getBasicInfo(lg.getUsername());
        
        request.setAttribute("ProfileData", profile);
        
        RequestDispatcher rd = request.getRequestDispatcher("/profile.jsp");
        rd.forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        if(lg == null || !lg.getLoginState()) {
            //error("Not Logged In", response);
            return;
        }
        
        String username = lg.getUsername();       
        
        String submitStr = request.getParameter("submit");
        if(submitStr == null)
        {
            RequestDispatcher rd = request.getRequestDispatcher("/profile.jsp");
            rd.forward(request, response);
            return;
        }

        User um = new User();
        um.setCluster(cluster);
        
        if(submitStr.equals("Upload Avatar")) {
            for (Part part : request.getParts()) {
                if(!part.getName().equals("upfile"))
                    continue;
                
                String type = part.getContentType();

                InputStream is = request.getPart(part.getName()).getInputStream();
                int size = is.available();

                if (size > 0) {
                    byte[] b = new byte[size + 1];
                    is.read(b);

                    um.setAvatar(username, b, type);

                    is.close();
                }
            }
        } else if(submitStr.equals("Update Profile")) {
            String firstName = request.getParameter("first_name");
            if(firstName == null) firstName = "";
            
            String lastName = request.getParameter("last_name");
            if(lastName == null) lastName = "";
            
            String email = request.getParameter("email");
            if(email == null) email = "";
            
            um.setBasicInfo(username, firstName, lastName, email);
        } else if(submitStr.equals("Update Privacy Settings")) {
            String privacyStr = request.getParameter("privacy");
            if(privacyStr == null)
                return;
            
            int privacy = Integer.parseInt(privacyStr);
            um.setPrivacy(username, privacy);
        }
        
        RequestDispatcher rd = request.getRequestDispatcher("/profile.jsp");
        rd.forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
