<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>
</head>
<SCRIPT language="JavaScript">

     function submit1()

        {

         document.forms["form1"].action="test2.jsp";

        document.form1.submit();

        }

        

        function edit1()

        {

          document.forms["form1"].action="test3.jsp";

       document.form1.submit();

        }

</SCRIPT>

<body>

<h3>(uid)link(hobby)and(work)"</h3>

<%

   String name=(String)session.getAttribute("name");

   String pass=request.getParameter("pass");

   //String work= request.getParameter("work");

   out.println("Your ID is："+name+"<br>");

   out.println("Your Passwd is："+pass+"<br>");

   //out.println("Your JOB is："+work+"<br>");


   session.setAttribute("pass",pass);

   //session.setAttribute("work",work);

%>

<form name="form1" method="post">

 <input type="hidden" name="name" value="<%=name%>">

 <input type="hidden" name="hobby" value="<%=pass%>">
 
 <h3>Are U sure?</h3>

 <input type="Button" name="Submit" value="Yes" onClick="javascript:submit1()">

 <input type="Button" name="Edit" value="Edit" onClick="javascript:edit1()">

</form>

<hr>

<p><font size="2">*(pass)(work)"session</font></p>

<p><font size="2">*(uid)"</font></p>

</body>

</html>