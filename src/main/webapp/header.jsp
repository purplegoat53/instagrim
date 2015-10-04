<%-- 
    Document   : header
    Created on : 22-Sep-2015, 16:15:10
    Author     : owner
--%>

        <%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
        <header>
            <h1>InstaGrim</h1>
            <h2>Your world in Black and White</h2>
        </header>
        <nav>
            <ul>
                <li><a href="/Instagrim/upload.jsp">Upload</a></li>
                <%
                        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                        if (lg != null) {
                            if (lg.getLoginState()) {
                %>
                <li><a href="/Instagrim/Home">Your Images</a></li>
                <li><a href="/Instagrim/Logout">Logout</a></li>
                <%          }
                        } else {
                %>
                <li><a href="/Instagrim/register.jsp">Register</a></li>
                <li><a href="/Instagrim/login.jsp">Login</a></li>
                <%
                        }
                %>
            </ul>
        </nav>
