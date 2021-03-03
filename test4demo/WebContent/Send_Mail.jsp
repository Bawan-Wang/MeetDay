<%@ page language="java" %>
<%@ page import="java.util.*" %>  
<%@ page import="javax.mail.*" %>  
<%@ page import="javax.mail.internet.*" %>  
<%@ page import="javax.activation.*" %>  
<%@ page import="org.apache.commons.mail.DefaultAuthenticator" %>
<%@ page import="org.apache.commons.mail.Email" %>
<%@ page import="org.apache.commons.mail.EmailException" %>
<%@ page import="org.apache.commons.mail.HtmlEmail" %>
<%@ page import="java.sql.*,java.util.*,java.text.*,java.text.SimpleDateFormat" %>  
<%@ page contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>
</head>
<body>
 
<% 
 /* Properties props = new Properties(); 
  props.put("mail.smtp.host", "localhost"); //SMTP Server 
  props.put("mail.smtp.port", "587" );
  Session s = Session.getInstance(props,null); 

  
  MimeMessage message = new MimeMessage(s); 

  InternetAddress from = new InternetAddress("ycw517@gmail.com","ycw517");  //From 
  //InternetAddress from = new InternetAddress("bryan@from.com.tw","不來ㄣ","big5");  //From含中文字 
  message.setFrom(from); 
  InternetAddress to = new InternetAddress("yuki.wu@aver.com");  //To 
  message.addRecipient(Message.RecipientType.TO, to); 
  
  message.setSubject("Test from JavaMail."); 
  //message.setSubject("中文標題","big5");  //中文標題需指定編碼，以避免亂碼 
  message.setText("Hello from JavaMail!");  //純文字內文 
  //message.setContent("<html><body><font color=red>電子報</font><br><img src=http://www.test.com.tw/test.jpg></body></html>","text/html;charset=big5");  //HTML內文 

  // Next two lines are specific to Yahoo 
  //Store store = s.getStore("pop3"); 
  //store.connect("pop.mail.yahoo.com", "yahooUsername", "yahooPassword"); 

  Transport.send(message); 

  // Next line is specific to Yahoo 
  //store.close(); 
*/

String subject="測試使用 Gmail SMTP SSL發信";
String message = "<html><head><title>測試</title></head><body>這是一封測試信，收到請自行刪除</body></html>"; 

Email email = new HtmlEmail(); 
String authuser = "ycw517@gmail.com"; 
String authpwd = "edencc755";
email.setHostName("smtp.gmail.com");
email.setSmtpPort(465); 
email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
email.setDebug(true);
email.setSSL(true);
email.setSslSmtpPort("465");
email.setCharset("UTF-8");
email.setSubject(subject);
try {
    email.setFrom("ycw517@gmail.com", "網站客服中心");
    email.setMsg(message); 
    email.addTo("yuki.wu@aver.com", "親愛的會員");
    email.send();
    out.println("郵件發送成功"); 
} catch (EmailException e) {
	out.println("fail"); 
    e.printStackTrace();
}	
%> 

<html> 
<p align="center">Mail has been sent.</p> 
</html>
</body>
</html>