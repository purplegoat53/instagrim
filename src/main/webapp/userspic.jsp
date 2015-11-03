<%-- 
    Document   : userspic
    Created on : 22-Sep-2015, 17:30:03
    Author     : owner
--%>

<%@page import="uk.ac.dundee.computing.aec.instagrim.lib.Convertors"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>InstaGrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <%@include file="header.jsp"%>
        <% Pic pic = (Pic)request.getAttribute("Pic"); %>
        <main>
            <% if(pic != null) { 
                   boolean isOwner = (lg != null && lg.getUsername().equals(pic.getUser()));
                   if(pic.isPublic() || isOwner) { %>
            <img src="/Instagrim/ImageData/<%= pic.getSUUID() %>" width=100%>
            <p>
                <%     if(isOwner) { %>
                <a href="/Instagrim/Image/<%= pic.getSUUID() %>/Delete">Delete</a><br>
                <form method="GET" action="/Instagrim/Image/<%= pic.getSUUID() %>/UpdatePrivacy">
                    Allow everyone to view: <input type="checkbox" name="public" value="1" <%= pic.isPublic() ? "checked" : "" %>>
                    <input type="submit" value="Update Privacy">
                </form>
                <%     } else { %>
                <a href="/Instagrim/Profile/<%= Convertors.Escape(pic.getUser()) %>"><%= Convertors.Escape(pic.getUser()) %></a><br>
                <%     } %>
            </p>
            <%     } else { %>
            <p>Picture not public</p>
            <%     } %>
            <% } else { %>
            <p>Picture not found</p>
            <% } %>
        </main>
        <%@include file="footer.jsp"%>
    </body>
</html>
