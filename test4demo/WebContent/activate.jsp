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
String name= request.getParameter("name");
String reg_mode= request.getParameter("mode");
String num= request.getParameter("num");
int mode = Integer.valueOf(reg_mode);
JDBCMysql test = new JDBCMysql(); 
int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);

if(test.Get_UsrInfo(Const.eUsrType.UsrType_ActStatus, UsrId).compareTo("1") != 0){
	if (test.Check_ActCode(name, num) == Const.RRet.RRET_SUCCESS){		
		test.Update_UsrInfo(UsrId, "user",  Const.eUsrType.UsrType_ActStatus, "1");
		//test.Update_Act_Status(name, true);
		test.Activate_List_Del(name);
		out.println("<br> You Are Activated!! <br>");
	}
}
%>
</body>
</html>