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
 Expert Name�G
     <input type="text" name="name">
</p>

<p>
 Expert Tel�G
     <input type="text" name="tel">
</p>

<p>
 Expert Addr�G
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
      <option value="">�п��</option>
      <option value="0">�x�_�� �Q�s��</option>
      <option value="1">�x�_�� �H�q��</option>
      <option value="2">�x�_�� �j�w��</option>
      <option value="3">�x�_�� ���s��</option>
      <option value="4">�x�_�� ������</option>
      <option value="5">�x�_�� �j�P��</option>
      <option value="6">�x�_�� �����</option>
      <option value="7">�x�_�� �h�L��</option>
      <option value="8">�x�_�� �_���</option>
      <option value="9">�x�_�� �U�ذ�</option>
      <option value="10">�x�_�� ��s��</option>
      <option value="11">�x�_�� �n���</option>
      <option value="12">�s�_�� ���M�éM�s��</option>
      <option value="13">�s�_�� �O���s���g��</option>
      <option value="14">�s�_�� �T��Ī�w</option>
      <option value="15">�s�_����L</option>
      <option value="16">���</option>
      <option value="17">�x��</option>
      <option value="18">�x�n</option>
      <option value="19">����</option>
      <option value="20">��L</option>
    </select>
 
 	<select id="Cop" name="copr">
 	  <option value="">�O�_�X�@</option>
 	  <option value="1">�O</option>
      <option value="0">�_</option>
 	</select>
 
 <p>

     <input type="Submit" value="Add">

     <input type="Reset" value="Reset">

 </p>

</body>
</html>