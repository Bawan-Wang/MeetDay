<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>
</head>
<%

   String uid=request.getParameter("uid");

   String pass=request.getParameter("pass");

   String work=new String(request.getParameter("work").getBytes("ISO8859_1"),"GBK");

%>
<h3>use form</h3>

<form name="form1" method="post" action="index.jsp">

  <p>Your Name：

     <input type="text" name="uid" value="<%=uid%>">

 </p>

 <p>Your Pass：

     <input type="text" name="pass" value="<%=pass%>">

 </p>

 <p>Your JOb：

     <select name="work">

          <%if(work.equals("student")){%>

        <option value="student" selected>student</option>

        <option value="IT">IT</option>

        <option value="business">business</option>


          <%}else if(work.equals("IT")){%>

        <option value="student">student</option>

        <option value="IT" selected>IT</option>

        <option value="business">business</option>



          <%}else if(work.equals("business")){%>

        <option value="student">student</option>

        <option value="IT">IT</option>

        <option value="business" selected>business</option>


              <%}%>

     </select>

 </p>

 <p>

     <input type="Submit" value="submit">

     <input type="Reset" value="reset">

 </p>

</form>


<body>

</body>
</html>