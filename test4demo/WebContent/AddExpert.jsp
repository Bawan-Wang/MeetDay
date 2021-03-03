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

<p>
 Expert Name：
     <input type="text" name="name">
</p>

<p>
 Expert Tel：
     <input type="text" name="tel">
</p>

<p>
 Expert Addr：
     <input type="text" name="addr">
</p>

<%
 
Const.Command cmd = Const.Command.Cmd_AddExpert;//Command.valueOf("Cmd_Login");

request.setCharacterEncoding("big5");

//String name= request.getParameter("name");

//out.println("<br> Hi "+name+" Welcome <br>");

//session.setAttribute("name",name);
session.setAttribute("cmd",cmd);

//out.println("<br> Friend List: <br>");


%>

<h1>Expert Location</h1>
    <select id=type name="type">
      <option value="">請選擇</option>
      <option value="0">台北市 松山區</option>
      <option value="1">台北市 信義區</option>
      <option value="2">台北市 大安區</option>
      <option value="3">台北市 中山區</option>
      <option value="4">台北市 中正區</option>
      <option value="5">台北市 大同區</option>
      <option value="6">台北市 內湖區</option>
      <option value="7">台北市 士林區</option>
      <option value="8">台北市 北投區</option>
      <option value="9">台北市 萬華區</option>
      <option value="10">台北市 文山區</option>
      <option value="11">台北市 南港區</option>
      <option value="12">新北市 中和永和新店</option>
      <option value="13">新北市 板橋新莊土城</option>
      <option value="14">新北市 三重蘆洲</option>
      <option value="15">新北市其他</option>
      <option value="16">桃園</option>
      <option value="17">台中</option>
      <option value="18">台南</option>
      <option value="19">高雄</option>
      <option value="20">其他</option>
    </select>
 
 	<select id="Cop" name="copr">
 	  <option value="">是否合作</option>
 	  <option value="1">是</option>
      <option value="0">否</option>
 	</select>
 
 <p>

     <input type="Submit" value="Add">

     <input type="Reset" value="Reset">

 </p>

</body>
</html>