<%@page import="jsp.*,java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
	String pushStatus = "";
	Object pushStatusObj = request.getAttribute("pushStatus");

	if (pushStatusObj != null) {
		pushStatus = pushStatusObj.toString();
	}
%>
<%
Const.Command cmd = Const.Command.Cmd_SendPushToOther;//Command.valueOf("Cmd_Login");

//String name= request.getParameter("name");

//out.println("<br> Hi "+name+" Welcome <br>");

//session.setAttribute("name",name);
session.setAttribute("cmd",cmd);

//out.println("<br> Friend List: <br>");

%>
<head>
<title>Google Cloud Messaging (GCM) Server in PHP</title>
</head>
<body>

	<h1>Google Cloud Messaging (GCM) Server in Java</h1>

	<form action="DBRelated" method="post">
		<p>
 		Your ID：
     	<input type="text" name="id">
		</p>
		
		<p>
 		Other ID：
     	<input type="text" name="rec_id">
		</p>
		
		<div>
			<textarea rows="2" name="message" cols="23"
				placeholder="Message to transmit via GCM"></textarea>
		</div>
		<div>
			<input type="submit" value="Send Push Notification via GCM" />
		</div>
	</form>
	<p>
		<h3>
			<%=pushStatus%>
			
		</h3>
	</p>
</body>
</html>
