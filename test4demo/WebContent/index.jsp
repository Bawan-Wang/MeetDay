<%@page import="jsp.*,java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>
</head>
<body>

<form name="" method="post" action="DBRelated">

<a href="register.jsp?">Register</a>
<a href="forgetpwd.jsp?">Forget Password</a>

<p>
 Your ID：
     <input type="text" name="name">
</p>

<p>
 Your Password：
     <input type="password" name="pass">
</p>

<%
 
Const.Command cmd = Const.Command.Cmd_Login;//Command.valueOf("Cmd_Login");

//String name= request.getParameter("name");

//out.println("<br> Hi "+name+" Welcome <br>");

//session.setAttribute("name",name);
session.setAttribute("cmd",cmd);

//out.println("<br> Friend List: <br>");


%>
 <p>

     <input type="Submit" value="Log In">

     <input type="Reset" value="Reset">

 </p>
</form>
</body>
</html>