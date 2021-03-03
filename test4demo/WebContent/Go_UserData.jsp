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
	<input type="submit" value="Check"/>
	</form>
	<%
 
Const.Command cmd = Const.Command.Cmd_GetUsrData;//Command.valueOf("Cmd_Login");
String usr_id = request.getParameter("id");
//String id= request.getParameter("name");

out.println("<br> Hi "+usr_id+" Welcome <br>");

session.setAttribute("id",usr_id);
session.setAttribute("cmd",cmd);




%>
</body>
</html>