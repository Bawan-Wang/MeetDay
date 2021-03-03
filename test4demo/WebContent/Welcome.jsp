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
<%String name= request.getParameter("name"); %>
<a href="Log_out.jsp?name=<%=name %>">Logout</a>

<%
JDBCMysql test = new JDBCMysql(); 

out.println("<br> Hi "+name+" Welcome <br>");

session.setAttribute("name",name);

out.println("<br> Friend List: <br>");
int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
String xx=test.Get_Friend(UsrId);
//System.out.println(xx);
String flist="";
int num = 0;
int[] list;
list = new int[test.Get_FriendNum(UsrId)];

while(xx.contains("/")){	
	int tmp = xx.indexOf("/");
	String str=xx.substring(0, tmp);
	//Const.Command cmd = Const.Command.Cmd_GetUsrData;
	//session.setAttribute("cmd",cmd);
	//session.setAttribute("id",str);
	%>
	<a href="Go_UserData.jsp?id=<%=Integer.valueOf(str)%>"><%out.println("<br>("+test.Get_UsrInfo(Const.eUsrType.UsrType_NickName, Integer.valueOf(str))+") <br>"); %></a>
	<%
	//out.println("<br>("+test.Get_UserName(Integer.valueOf(str))+") <br>");	
	list[num] = Integer.valueOf(str);
	flist=flist.concat(test.Get_UsrInfo(Const.eUsrType.UsrType_Email, Integer.valueOf(str))+"/");
	xx=xx.substring(tmp+1);
	num++;
}

out.println("<br> You Have "+num+" Friends!! <br>");
//System.out.println(flist);

%>
<a href="Add_friend.jsp?name=<%=name %>">Add Friend</a>


</body>

</html>