<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
    Author     : Administrator
--%>

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
        <main>
            <h3>Register as user</h3>
            <% String msg = (String)request.getAttribute("Message");
               if(msg != null) { %>
            <p id="flash_message"><%= msg %></p>
            <% } %>
            <form method="POST" action="Register">
                <table>
                    <tr><td>Username</td><td><input type="text" name="username"></td></tr>
                    <tr><td>Password</td><td><input type="password" name="password"></td></tr>
                </table>
                <br>
                <input type="submit" value="Register"> 
            </form>
        </main>
        <%@include file="footer.jsp"%>
    </body>
</html>