<%@page import="jsp.*,java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>
</head>
<body>
<form name="" method="post" action="DBRelated">
<p>
 Friend Name：
     <input type="text" name="nameadded">
</p>

<%
 
Const.Command cmd = Const.Command.Cmd_AddFriend;//Command.valueOf("Cmd_Login");

String name= request.getParameter("name");

//out.println("<br> Hi "+name+" Welcome <br>");

//session.setAttribute("name",name);
session.setAttribute("cmd",cmd);

//out.println("<br> Friend List: <br>");


%>
<input type="submit" value="Add"/>
</form>
</body>
</html>