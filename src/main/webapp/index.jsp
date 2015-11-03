<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.models.PicModel"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts"%>
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
            <%
                PicModel tm = new PicModel();
                tm.setCluster(CassandraHosts.getCluster());
                java.util.LinkedList<Pic> lsPics = tm.getPublicPics();
                
                Iterator<Pic> iterator = lsPics.iterator();
                while (iterator.hasNext()) {
                    Pic p = (Pic) iterator.next();
            %>
            <a href="/Instagrim/Image/<%=p.getSUUID()%>"><img src="/Instagrim/ThumbData/<%=p.getSUUID()%>" class="thumbpics"></a>
            <%
                }
            %>
        </main>
        <%@include file="footer.jsp"%>
    </body>
</html>
