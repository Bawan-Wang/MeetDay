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
<form name="RegisterForm" method="post" action="DBRelated">
<p>
Your ID�G
<input type="text" name="name">

</p>

<p>

Your Password�G

<input type="password" name="pass">

</p>

<p>
 Your phone#�G
     <input type="text" name="phone">
</p>

<%
 
Const.Command cmd = Const.Command.Cmd_Register;//Command.valueOf("Cmd_Login");

session.setAttribute("cmd",cmd);

%>

 <p>

     <input type="Submit" value="Submit">

     <input type="Reset" value="Reset">

 </p>

</form>
</body>
</html>