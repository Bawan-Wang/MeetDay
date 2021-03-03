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

<%
 
//Const.Command cmd = Const.Command.Cmd_GetUsrData;//Command.valueOf("Cmd_Login");
//int usr_id = Integer.valueOf(request.getParameter("id"));
//String id= request.getParameter("name");

//out.println("<br> Hi "+name+" Welcome <br>");

//session.setAttribute("name",name);
//session.setAttribute("cmd",cmd);
String name= request.getParameter("name");
String ipaddr= request.getParameter("ipaddr");
String phone=request.getParameter("phone");
String nick=request.getParameter("nick");

out.println("<br> Show Info: <br>");

out.println("<br> Email: <br>"+name);
out.println("<br> IP: <br>"+ipaddr);
out.println("<br> Phone: <br>"+phone);
out.println("<br> Nick Name: <br>"+nick);



%>


</body>
</html>