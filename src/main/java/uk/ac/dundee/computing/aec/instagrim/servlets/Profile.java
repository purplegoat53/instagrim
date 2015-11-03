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
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.stores.ProfileData;

/**
 *
 * @author owner
 */
@WebServlet(name = "profile", urlPatterns = {"/Profile", "/Profile/*", "/Images/*"})
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
        
        PicModel pm = new PicModel();
        pm.setCluster(cluster);
        
        RequestDispatcher rd = request.getRequestDispatcher("/profile.jsp");
        
        String user = null;
        String args[] = Convertors.SplitRequestPath(request);
        if(args.length < 2) {
            return;
        } else if(args.length == 2) {
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
            if(lg == null || !lg.getLoginState())
                return;
            
            user = lg.getUsername();
            request.setAttribute("IsUser", true);
        } else if(args.length >= 3) {
            user = args[2];
            request.setAttribute("IsUser", false);
        }
        
        User um = new User();
        um.setCluster(cluster);
        ProfileData profile = um.getBasicInfo(user);
        request.setAttribute("ProfileData", profile);
        request.setAttribute("User", user);
        
        java.util.LinkedList<Pic> lsPics = pm.getPicsForUser(user);
        request.setAttribute("Pics", lsPics);
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
    /*protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }*/

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
