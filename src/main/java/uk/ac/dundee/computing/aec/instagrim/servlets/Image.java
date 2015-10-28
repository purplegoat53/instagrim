package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.function.Function;
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

/**
 * Servlet implementation class Image
 */
@WebServlet(urlPatterns = {
    "/ImageData",
    "/ImageData/*",
    "/ThumbData/*",
    "/AvatarData/*",
    "/Images",
    "/Images/*",
    "/Image",
    "/Image/*",
    "/Home"
})
@MultipartConfig

public class Image extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Image() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
        
        
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        if(args.length <= 1) {
            error("Not Enough Arguments", response);
            return;
        }
        
        // TODO: should have a page for public images
        // FIX: make it less messy
        
        String imageCommand = args[1];
        if(imageCommand.equals("ImageData") && args.length >= 3) {
            DisplayImageData(Convertors.DISPLAY_PROCESSED, args[2], request, response);
        } else if(imageCommand.equals("ThumbData") && args.length >= 3) {
            DisplayImageData(Convertors.DISPLAY_THUMB, args[2], request, response);
        } else if(imageCommand.equals("AvatarData") && args.length >= 3) {
            DisplayAvatarData(args[2], request, response);
        } else if(imageCommand.equals("Images") && args.length >= 3)
            DisplayImageList(args[2], request, response);
        else if(imageCommand.equals("Image") && args.length >= 3) {
            if(args.length > 3)
                ManageImage(args[2], args[3], request, response);
            else
                DisplayImage(args[2], request, response);
        } else if(imageCommand.equals("Home"))
            GoHome(request, response);
        else
            error("Bad Command", response);
    }
    
    private void GoHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd;
        
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        if(lg == null || !lg.getLoginState())
            rd = request.getRequestDispatcher("/");
        else
            rd = request.getRequestDispatcher("/Images/" + lg.getUsername());
        
        rd.forward(request, response);
    }
    
    private void ManageImage(String image, String operation, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel pm = new PicModel();
        pm.setCluster(cluster);
        
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        if(lg == null || !lg.getLoginState()) {
            error("Not Logged In", response);
            return;
        }
        
        String user = lg.getUsername();
        
        if(operation.equals("Delete")) {
            pm.removePic(java.util.UUID.fromString(image), user);
            //TODO: inform user on error
            response.sendRedirect("/Instagrim/Home"); //FIX: crude
            //RequestDispatcher rd = request.getRequestDispatcher("/userspics.jsp");
            //rd.forward(request, response);
        }
        
        error("Invalid Operation", response);
    }

    private void DisplayImageList(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User);
        RequestDispatcher rd = request.getRequestDispatcher("/userspics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);
    }

    private void DisplayImage(String image, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/userspic.jsp");
        request.setAttribute("PicID", image);
        rd.forward(request, response);
    }
    
    private void DisplayImageData(int type, String Image, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        
        Pic p = tm.getPic(type, java.util.UUID.fromString(Image));
        
        if(!p.isPublic()) {
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
            if(lg == null || !lg.getLoginState() || !lg.getUsername().equals(p.getUser()))
                return;
        }
        
        OutputStream out = response.getOutputStream();

        response.setContentType(p.getType());
        response.setContentLength(p.getLength());
        
        InputStream is = new ByteArrayInputStream(p.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
            out.write(buffer, 0, length);
        }
        out.close();
    }
    
    
    private void DisplayAvatarData(String user, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User um = new User();
        um.setCluster(cluster);
        
        Pic avatar = um.getAvatar(user);
        
        if(avatar == null)
        {
            RequestDispatcher rd = request.getRequestDispatcher("/noavatar.png");
            rd.forward(request, response);
            return;
        }
        
        OutputStream out = response.getOutputStream();

        response.setContentType(avatar.getType());
        response.setContentLength(avatar.getLength());
        
        InputStream is = new ByteArrayInputStream(avatar.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
            out.write(buffer, 0, length);
        }
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        if(lg == null) {
            error("Not Logged In", response);
            return;
        }
        
        String isPublicStr = request.getParameter("public");
        boolean isPublic = (isPublicStr != null && isPublicStr.equals("1"));
        
        for (Part part : request.getParts()) {
            String type = part.getContentType();
            String filename = part.getSubmittedFileName();
            
            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();
            String username = "majed"; //FIX: for some reason we're uploading to this guys account,
                                        //     if we're not logged in
            if (lg.getLoginState()){
                username = lg.getUsername();
            }
            
            if (i > 0) {
                byte[] b = new byte[i + 1];
                is.read(b);
                
                PicModel tm = new PicModel();
                tm.setCluster(cluster);
                tm.insertPic(b, type, filename, username, isPublic);

                is.close();
            }
            
            RequestDispatcher rd = request.getRequestDispatcher("/upload.jsp");
            rd.forward(request, response);
        }

    }

    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have an error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
    }
}
