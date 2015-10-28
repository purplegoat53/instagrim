<%-- 
    Document   : profile
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
        <main>
            <h3>Profile</h3>
            <b>Avatar</b>
            <table>
                <tr>
                    <form method="POST" enctype="multipart/form-data" action="Profile">
                        <td><img class="avatar" src="/Instagrim/AvatarData/<%= lg.getUsername() %>"></td>
                        <td>
                            <input type="file" name="upfile"><br>
                            <input type="submit" name="submit" value="Upload Avatar">
                        </td>
                    </form>
                </tr>
            </table>
            <br>
            <% ProfileData profile = (ProfileData)request.getAttribute("ProfileData"); %>
            <b>Basic Information</b>
            <table>
                <form method="POST" enctype="multipart/form-data" action="Profile">
                    <tr><td>Forename</td><td><input type="text" name="first_name" value="<%= profile.getFirstName() %>"></td></tr>
                    <tr><td>Surname</td><td><input type="text" name="last_name" value="<%= profile.getLastName() %>"></td></tr>
                    <tr><td>Email</td><td><input type="text" name="email" value="<%= profile.getEmail() %>"></td></tr>
                    <tr>
                        <td/>
                        <td>
                            <input type="submit" name="submit" value="Update Profile">
                        </td>
                    </tr>
                </form>
            </table>
            <br>
            <b>Privacy</b>
            <table>
                <form method="POST" enctype="multipart/form-data" action="Profile">
                    <tr>
                        <td>Privacy Setting</td>
                        <td>
                            <select>
                                <option name="privacy" value="0" <%= (profile.getPrivacy() == 0 ? "selected" : "") %>>Picture List and Profile Public</option>
                                <option name="privacy" value="1" <%= (profile.getPrivacy() == 1 ? "selected" : "") %>>Only Picture List Public</option>
                                <option name="privacy" value="2" <%= (profile.getPrivacy() == 2 ? "selected" : "") %>>Everything Private</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td/>
                        <td>
                            <input type="submit" name="submit" value="Update Privacy Settings">
                        </td>
                    </tr>
                </form>
            </table>
        </main>
        <%@include file="footer.jsp"%>
    </body>
</html>
