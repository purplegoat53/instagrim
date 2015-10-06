/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "Login", urlPatterns = {"/Login", "/Logout"})
public class Login extends HttpServlet {

    Cluster cluster = null;

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] args = Convertors.SplitRequestPath(request);
        if(args.length <= 1 || !args[1].equals("Logout")) {
            response.sendError(404);
            return;
        }
        
        HttpSession session = request.getSession();
        session.setAttribute("LoggedIn", null);
        
        response.sendRedirect("/Instagrim/");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] args = Convertors.SplitRequestPath(request);
        if(args.length <= 1 || !args[1].equals("Login")) {
            response.sendError(404);
            return;
        }
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        User us = new User();
        us.setCluster(cluster);
        boolean isValid = us.IsValidUser(username, password);
        HttpSession session = request.getSession();
        System.out.println("Session in servlet "+session);
        if (isValid){
            LoggedIn lg = new LoggedIn();
            lg.setLoginState(true);
            lg.setUsername(username);
            
            session.setAttribute("LoggedIn", lg);
            response.sendRedirect("/Instagrim/Home");
        } else {
            response.sendRedirect("/Instagrim/login.jsp");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
