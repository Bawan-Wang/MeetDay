<%@page import="jsp.*,java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Retrieve from form1</title>
</head>
<body>
<h3>Get Data via form</h3>

<p>
<%
  

   String name= request.getParameter("name");

   String pass=request.getParameter("pass");

   //String work= request.getParameter("work");


   //if(name.length()<2||name.length()>20)

      //out.println("Length Error!!");

   //else 

  // { 

      out.println("Your ID："+name+"<br>");

        out.println("Your Pass："+pass+"<br>");

      //out.println("Your Job："+work+"<br>");


      session.setAttribute("name",name);

  // }

   JDBCMysql jsql = new JDBCMysql();
   if(jsql.Do_Log_In(name, pass) == Const.RRet.RRET_SUCCESS){
	   out.println("Log In Success");
   } else {
	   out.println("Log In Fail (Wrong Passwd)");
   }
	   
   jsql.SelectTable();
%>


<% 
//Connection c = MySQL.connect();
//out.print(c);
//MySQL.close(c);
//JDBCMysql jsql = new JDBCMysql();
//jsql.SelectTable(); 
%>
<br>

<!--(pass)(work)"pass to test1.jsp-->


</p>

<hr>
<a href="test1.jsp?pass=<%=pass%>">Submit</a>
<!--<p><font size="2">*"ID(uid)"save session</font></p>-->

<!--<p><font size="2">*"Passwd(pass)"and"Work(work)"link</font></p>-->

</body>
</html>