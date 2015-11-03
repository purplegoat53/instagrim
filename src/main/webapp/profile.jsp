<%-- 
    Document   : Profile
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<%@page import="uk.ac.dundee.computing.aec.instagrim.lib.Convertors" %>
<!DOCTYPE html>
<html>
    <head>
        <title>InstaGrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <%@include file="header.jsp"%>
        <main>
            <%  ProfileData profile = (ProfileData)request.getAttribute("ProfileData");
                if(profile != null) {
                    boolean isUser = (boolean)request.getAttribute("IsUser");
                    if(profile.getPrivacy() == 0 || isUser) {
                        String name = (profile.getFirstName().length() <= 0 ? (String)request.getAttribute("User") : profile.getFirstName());
                        if(isUser == true) { %>
            <h2 class="profile_name"><p><img class="avatar" src="/Instagrim/AvatarData/<%= lg.getUsername() %>">Hi, <%= Convertors.Escape(name) %></p></h2>
            <%          } else { %>
            <h2 class="profile_name"><p><img class="avatar" src="/Instagrim/AvatarData/<%= lg.getUsername() %>"><%= Convertors.Escape(name) %></p></h2>
            <%          }
                    
                        java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>)request.getAttribute("Pics");
                        if (lsPics == null) {
            %>
            <p>No pictures found</p>
            <%
                        } else {
                            Iterator<Pic> iterator;
                            iterator = lsPics.iterator();
                            while (iterator.hasNext()) {
                                Pic p = (Pic) iterator.next();
            %>
            <a href="/Instagrim/Image/<%=p.getSUUID()%>"><img src="/Instagrim/ThumbData/<%=p.getSUUID()%>" class="thumbpics"></a>
            <%              }
                        }
                    } else { %>
            <p>Profile private</p>
            <%      }
                } else { %>
            <p>No such user</p>
            <%  }
            %>
        </main>
        <%@include file="footer.jsp"%>
    </body>
</html>
