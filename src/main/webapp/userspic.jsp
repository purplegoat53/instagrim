<%-- 
    Document   : userspic
    Created on : 22-Sep-2015, 17:30:03
    Author     : owner
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
        <% String picid = (String)request.getAttribute("PicID"); %>
        <main>
            <img src="/Instagrim/ImageData/<%= picid %>" width=100%><br>
            <a href="/Instagrim/Image/<%= picid %>/Delete">Delete</a> ...
        </main>
        <%@include file="footer.jsp"%>
    </body>
</html>
