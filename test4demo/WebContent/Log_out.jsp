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
<form name="Logout" method="post" action="DBRelated">
<input type="submit" value="Logout"/>
<%
String name= request.getParameter("name");
out.println("Your ID："+name+"<br>");

Const.Command cmd = Const.Command.Cmd_Logout;//Command.valueOf("Cmd_Login");

session.setAttribute("cmd",cmd);

%>
</form>
</body>
</html>