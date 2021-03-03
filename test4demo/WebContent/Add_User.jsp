<%@page import="jsp.*,java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Register</title>
</head>
<body>
<h3>Add User</h3>

<%
  

   String name= request.getParameter("name");

   String pass=request.getParameter("pass");


   if(name.length()<2||name.length()>20)

      out.println("Length Error!!");

   else { 
      //out.println("Your ID："+name+"<br>");

      //out.println("Your Pass："+pass+"<br>");

		JDBCMysql jsql = new JDBCMysql();
		int UsrId = jsql.Get_UsrId(Const.eUsrType.UsrType_Email, name);
		//if(jsql.Add_User(name, pass) == true){
		if(jsql.Do_Add_User(name, pass) == Const.RRet.RRET_SUCCESS){	
			session.setAttribute("name",name);
			out.println("Your Account Has Been Created。<br>");
		} else {
			out.println("Wrong Accont name.<br>");
		}
		//jsql.Add_User(name, pass);
		jsql.SelectTable();      
   }
%>

<a href="index.html?">Back To Home</a>
</body>

</html>