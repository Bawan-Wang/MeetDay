<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>
</head>
<body>
<h3>Session Data</h3>

<%


   String uid=(String)session.getAttribute("uid");

   String pass=(String)session.getAttribute("pass");

   String work=(String)session.getAttribute("work");

%>

<p>

 <font color="#0000FF"><%=uid%></font>Your JOb is<font color="#0000FF"><%=work%></font>Pass<font color="#0000FF"><%=pass%></font>。

</p>


</body>
</html>