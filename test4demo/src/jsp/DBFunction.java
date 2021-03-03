package jsp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.omg.CORBA.portable.OutputStream;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

//import org.apache.commons.mail.DefaultAuthenticator;
//import org.apache.commons.mail.Email;
//import org.apache.commons.mail.EmailException;
//import org.apache.commons.mail.HtmlEmail;

public class DBFunction {
	JDBCMysql test = null;
	 public DBFunction() {
		 test = new JDBCMysql(); 
	 }
	 
	 /*
	  * args[0]: datatype
	  * args[1]: data
	  * args[2]: pass
	  * args[3]: ip
	  */
	 
	 public void DB_Login(HttpServletResponse response, String ... args) 
			 throws AddressException, MessagingException{	 
		 String msg="";
		 String fidlst="";	
		 Const.RRet ret = Const.RRet.RRET_SUCCESS;
		 int UsrId;
		 if(Const.eUsrType.valueOf(args[0]) == Const.eUsrType.UsrType_FBId){
			 //System.out.println(args[2]);
			 if(args[2].equals(Const.univeralpass)){
				 ret = Const.RRet.RRET_SUCCESS;
			 }
		 } else  if(Const.eUsrType.valueOf(args[0]) == Const.eUsrType.UsrType_Gmail){
			 if(args[2].equals(Const.univeralpass)){
				 ret = Const.RRet.RRET_SUCCESS;
			 }
		 } else if(Const.eUsrType.valueOf(args[0]) == Const.eUsrType.UsrType_Fone){
			 ret = test.Do_Log_In(args);
		 }
		if(ret == Const.RRet.RRET_SUCCESS){
			UsrId = test.Get_UsrId(Const.eUsrType.valueOf(args[0]), args[1]);
			System.out.println(UsrId);
			String name = "";
			if(UsrId==0){
				ret = Const.RRet.RRET_LOGIN_FAIL;
				msg = ret + "%";
			} else {
				ret = test.Update_UsrInfo(UsrId, "user", Const.eUsrType.UsrType_IP, args[3]);
				name = test.Get_UsrInfo(Const.eUsrType.UsrType_Email, "user", UsrId);
				//test.Update_IP(name, ip);
				fidlst=test.Get_Friend(UsrId);		
				String regId = test.Get_UsrInfo(Const.eUsrType.UsrType_RegId, UsrId);
				String fbid = test.Get_UsrInfo(Const.eUsrType.UsrType_FBId, UsrId);
				String gmail = test.Get_UsrInfo(Const.eUsrType.UsrType_Gmail, UsrId);
				msg= ret+"%"+fidlst+"&"+UsrId+"&"+regId;//+"&"+fbid+"&"+gmail;
			}
			System.out.println(args[1]);
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				if(ret == Const.RRet.RRET_SUCCESS){
					response.setHeader("REFRESH","2;URL=Welcome.jsp?name="+name+"&fidlst="+fidlst);
				} 
			}catch(Exception e){
				
			}
			
		}else{
			msg=ret+"%";
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","2;URL=index.jsp");
			}catch(Exception e){
				
			}
		}
	 }
	 
	 public void DB_Login(String name, String pass, String ip, HttpServletResponse response){
		String msg="";
		String fidlst="";		
		Const.RRet ret = test.Do_Log_In(name, pass);
		
		if(ret == Const.RRet.RRET_SUCCESS){
			int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
			ret = test.Update_UsrInfo(UsrId, "user", Const.eUsrType.UsrType_IP, ip);
			//test.Update_IP(name, ip);
			fidlst=test.Get_Friend(UsrId);		
			String regId = test.Get_UsrInfo(Const.eUsrType.UsrType_RegId, UsrId);
			String fbid = test.Get_UsrInfo(Const.eUsrType.UsrType_FBId, UsrId);
			String gmail = test.Get_UsrInfo(Const.eUsrType.UsrType_Gmail, UsrId);
			msg= ret+"%"+fidlst+"&"+UsrId+"&"+regId;//+"&"+fbid+"&"+gmail;
	
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","2;URL=Welcome.jsp?name="+name+"&fidlst="+fidlst);
			}catch(Exception e){
				
			}
			
		}else{
			msg=ret+"%";
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","2;URL=index.jsp");
			}catch(Exception e){
				
			}
		}
	 }

	 
	 public void DB_Logout(String name,  HttpServletResponse response)
	 {	 
		 String msg="";
		 int uid;
		if(isInteger(name)==true){
			uid = Integer.valueOf(name);
		}else{
			uid = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
		}
		 Const.RRet ret = test.Do_Log_Out(name);
		if(ret == Const.RRet.RRET_SUCCESS){			
			response.setContentType("text/html");
			try{
				msg=ret+"%";
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","2;URL=index.jsp");
			}catch(Exception e){
				
			}
		}else{
			msg=ret+"%";
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","2;URL=index.jsp");
			}catch(Exception e){
				
			}	
		}
	 }

	 /*
	  * args[0]: datatype
	  * args[1]: data
	  * args[2]: pass
	  */
	 
	 public void DB_Register(HttpServletResponse response, String ... args) 
			 throws AddressException, MessagingException{	 
		 String msg="";
		 int id = test.Do_Search(Const.eUsrType.valueOf(args[0]), args[1]);	
		 if(id != 0 ){
			 msg = Const.RRet.RRET_USER_EXIST_FAIL+"%"+id;	
		 }else{			 
			 Const.RRet ret = test.Do_Add_User(args[1], args[2]);
			 int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, args[1]);
			 test.Update_UsrInfo(UsrId, "user", Const.eUsrType.valueOf(args[0]), args[1]);
			 msg = ret+"%"+UsrId;	
		 }
		 
		 try{
			 response.setContentType("text/html");
			 PrintWriter out = response.getWriter();
			 out.println(msg);
			 response.setHeader("REFRESH","2;URL=index.jsp");
		 }catch(Exception e){
			 
		 }
	 }
	 
	 public void DB_Register(String name,  String pass, String fone, String nick, HttpServletResponse response, String ... args) 
			 throws AddressException, MessagingException
	 {	 
		 String msg="";
		 Const.RRet ret = test.Do_Add_User(name, pass);
		 
		 if(ret == Const.RRet.RRET_SUCCESS){
			 int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
			 if(fone != null){
				 ret = test.Update_UsrInfo(UsrId, "user", Const.eUsrType.UsrType_Fone, fone);
			 }	
			 if(nick != null){
				 ret = test.Update_UsrInfo(UsrId, "user",  Const.eUsrType.UsrType_NickName, nick);
			 }
			 if(args.length!=0)
				 test.Update_UsrInfo(UsrId, "user", Const.eUsrType.valueOf(args[0]), args[1]);
			
			 //Random rand = new Random();
			
			 //int  num = rand.nextInt(999) + 1;
			
			 String num = RandomString.generateRandomString(10);
			
			 test.Activate_List_Add(name, num);
			
			 MailRelated mrl = new MailRelated();
			
			 mrl.Mail_Reg(name, pass, num);
		 
			 msg=ret+"%"+UsrId;
			 /*if(mrl.Mail_Reg(name, pass, num)==true){
				 msg=ret+"%";
			 }else{
				 msg=ret+"%";
			 }
			*/
			/*String subject="Welcome to PreviewGuide";
			String message = "<html><head><title>Register</title></head><body>Thank you for your registration <br>"
					+ "Name:"+name+ "<br> Pass:"+pass+"</body></html>"; 

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
			    email.setFrom(authuser, "PreviewGuide Center");
			    email.setMsg(message); 
			    email.addTo(name, "Dear Member");
			    email.send();
			    System.out.println("Email Sent"); 
			} catch (EmailException e) {
				System.out.println("fail"); 
			    e.printStackTrace();
			}	
			*/
		 }else if (ret == Const.RRet.RRET_USER_EXIST_FAIL){
			 int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
			 if(args.length!=0)
				 test.Update_UsrInfo(UsrId, "user", Const.eUsrType.valueOf(args[0]), args[1]);
			 msg=ret+"%"+UsrId;
		 } else {
			 msg=ret+"%";
		 }
		
		 try{
			 response.setContentType("text/html");
			 PrintWriter out = response.getWriter();
			 out.println(msg);
			 response.setHeader("REFRESH","2;URL=index.jsp");
		 }catch(Exception e){
			 
		 }

		 
		 
	 }
	 
	 public void DB_ForgetPwd(String name,  HttpServletResponse response) throws AddressException, MessagingException
	 {	 
		String msg="";
		int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
		
		if(UsrId == 0){
			msg = Const.RRet.RRET_FORGETPWD_FAIL+"%";
		} else {
			
			String pass = test.Get_UsrInfo(Const.eUsrType.UsrType_Pass, UsrId);//test.Retrieve_Pwd(name);
			
			MailRelated mrl = new MailRelated();
			if(pass != null && test.Get_UsrInfo(Const.eUsrType.UsrType_ActStatus, UsrId).equals("1") ){
				if(mrl.Mail_ForgetPwd(name, pass)==true){			
					msg = Const.RRet.RRET_SUCCESS+"%";	
				}else{
					msg = Const.RRet.RRET_FORGETPWD_FAIL+"%";	
				}
			}else{
				msg = Const.RRet.RRET_FORGETPWD_FAIL+"%";
			}		
		}
				
		try{
			response.setContentType("text/html");
			
			PrintWriter out = response.getWriter();
			out.println( msg );
			response.setHeader("REFRESH","2;URL=index.jsp");
		 }catch(Exception e){
			 
		 }
		/*String subject="PreviewGuide Send Password";
		String message = "<html><head><title>SendPass</title></head><body>Hi, "+name+" Your Password: "+pass+"</body></html>"; 

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
		    email.setFrom(authuser, "Preview Guide Center");
		    email.setMsg(message); 
		    email.addTo(name, "Dear Member");
		    email.send();
		    System.out.println("�l��o�e���\"); 
		} catch (EmailException e) {
			System.out.println("fail"); 
		    e.printStackTrace();
		}	

		*/	 
		 
	}
	
	 
	 public Const.RRet DB_Addfreind(String UsrId,  String search_id, HttpServletResponse response, 
			 HttpServletRequest request, String ... args){
		
		 
		String msg=" ";
		int UsrIdadded = 0;
		if(isInteger(search_id)==true){
			UsrIdadded = Integer.valueOf(search_id);
		}else{
			UsrIdadded = test.Get_UsrId(Const.eUsrType.UsrType_SearchId, search_id);
		}
		//int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, nameadd);		
		//String nameadded = test.Get_UsrInfo(Const.eUsrType.UsrType_Email, UsrId)//test.Get_UserName(test.do_search(search_id));
		//boolean ret = test.Do_Add_Friend(UsrId, UsrIdadded);	
		Const.RRet ret = test.Do_Add_Friend(Integer.valueOf(UsrId), UsrIdadded);
		System.out.println("ret :" +ret); 		
		if( ret == Const.RRet.RRET_SUCCESS){			
			msg=ret+"%"+UsrIdadded+"/"+test.Get_UsrInfo(Const.eUsrType.UsrType_NickName, UsrIdadded);	
		}else{
			msg=ret+"%"+UsrIdadded+"/"+UsrId;
		}
			
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		if(ret == Const.RRet.RRET_SUCCESS){
			test.Do_Add_Friend(UsrIdadded, Integer.valueOf(UsrId));
			String message1 = Const.eNotifyType.NotifyType_AddFriend+
					"%"+UsrId+
					"%"+test.Get_UsrInfo(Const.eUsrType.UsrType_NickName, Integer.valueOf(UsrId))+
					"%";
			String message2 = Const.eNotifyType.NotifyType_AddFriend+
					"%"+Integer.toString(UsrIdadded)+
					"%"+test.Get_UsrInfo(Const.eUsrType.UsrType_NickName, UsrIdadded)+
					"%";
			System.out.println("message1 :" +message1+message2); 
			String senddata;
			try {
				senddata = new String(message1.getBytes("ISO-8859-1"), "UTF-8");
				DB_SendPushToOther( UsrId,  UsrIdadded, message1, request, response, false);
				DB_SendPushToOther( Integer.toString(UsrIdadded), Integer.valueOf(UsrId),  message2, request, response, false);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		if(args.length==0){
			try{		
				PrintWriter out = response.getWriter();
				out.println( msg );
				//response.setHeader("REFRESH","2;URL=Welcome.jsp?name="+nameadd);
			}catch(Exception e){
				
			}
		}
			
		return ret;

	 }
	 
	 public void DB_AddfreindList(String UsrId, String Datalist, Const.eUsrType datatype, 
			 HttpServletResponse response, HttpServletRequest request){		
		 	String msg=msg = Const.RRet.RRET_SUCCESS+"%";;
			String retlst="";
			while(Datalist.contains("/")){
				//ServerFuncRun sfr = new ServerFuncRun();
				int tmp = Datalist.indexOf("/");
				String str = Datalist.substring(0, tmp);
				System.out.println("str :" +str); 
				int UsrIdadded = test.Get_UsrId(datatype, str);
				System.out.println("UsrIdadded :" +UsrIdadded); 
				Const.RRet ret = DB_Addfreind(UsrId,  String.valueOf(UsrIdadded), response, request, "No Msg");
				//Const.RRet ret = test.Do_Add_Friend(Integer.valueOf(UsrId), UsrIdadded);
				if(ret==Const.RRet.RRET_SUCCESS){
					retlst = retlst + String.valueOf(UsrIdadded)+"/";
				}
				Datalist = Datalist.substring(tmp+1);
			}
			System.out.println("retlst :" +retlst);
			try{		
				PrintWriter out = response.getWriter();
				out.println( msg );
				//response.setHeader("REFRESH","2;URL=Welcome.jsp?name="+nameadd);
			}catch(Exception e){
				
			}
		 }
	 
	 
	 public void DB_ChangePwd(String name,  String pass, HttpServletResponse response){
			 
			String msg=" ";
			int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
			Const.RRet ret = test.Update_UsrInfo(UsrId, "user",  Const.eUsrType.UsrType_Pass, pass);
			
			if(ret == Const.RRet.RRET_SUCCESS){
				ret =test.Do_Log_Out(name);
				//msg="Change Successful!!";
				msg=ret+"%";
				System.out.println("Logout Successful! "+name);
			}else{
				msg=Const.RRet.RRET_CHANGEPWD_FAIL+"%";
				System.out.println("Logout Fail!");
			}
			
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","2;URL=index.jsp");
			}catch(Exception e){
				
			}	
	 }
	 
	 public void DB_Do_Upload_Photo2(String usr_id, String imgstr, HttpServletRequest request, HttpServletResponse response) throws IOException{
		 
		 	 
		 String loadpath=request.getSession().getServletContext().getRealPath("/")+"upload";
		 byte[] imageByteArray;// = Base64.decode(arg0, arg1);//decode(imgstr);
		 sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();    
		 imageByteArray = decoder.decodeBuffer(imgstr); 
		 FileOutputStream imagefile = new FileOutputStream(loadpath+"/user_"+usr_id+".jpg");
		 imagefile.write(imageByteArray);
		 imagefile.close();

		 String msg = Const.RRet.RRET_SUCCESS+"%";
		 
		try{
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(msg);
			response.setHeader("REFRESH","2;URL=index.jsp");
		}catch(Exception e){
			
		}
		 
		 //String image = request.getParameter("image");
			//byte[] result = Base64.decode(image); 
			//String loadpath=request.getSession().getServletContext().getRealPath("/")+"upload";
			//FileOutputStream out = new FileOutputStream(loadpath+"/xxx.jpg");  
			//out.write(result);  
			//out.close();
	 }
	 
	 public void DB_UpdateUsrData(int id, Const.eUsrType datatype_set, String content,  HttpServletRequest request, HttpServletResponse response){
		 	
		String msg="";
		Const.RRet ret=Const.RRet.RRET_SUCCESS;
		
		if(datatype_set == Const.eUsrType.UsrType_RegId){
			//System.out.println("DB_UpdateUsrData S"+id);
			String dbreg = test.Get_UsrInfo(datatype_set, id);
			if(dbreg!=null){
				//System.out.println("DB_UpdateUsrData Successful! "+dbreg);
				//System.out.println("DB_UpdateUsrData S"+content);
				if(!dbreg.equals(content)){
					String message1 = Const.eNotifyType.NotifyType_Logout+"%"+id;
					String senddata;
					System.out.println("DB_UpdateUsrData Successful! "+id);
					try {
						senddata = new String(message1.getBytes("ISO-8859-1"), "UTF-8");
						//DB_SendPushToOther( String.valueOf("0"),  uid, message1, request, response, false);
						DB_SendPushToOther(dbreg, message1, request, response, false);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
	
		} else if (datatype_set == Const.eUsrType.UsrType_SearchId || datatype_set == Const.eUsrType.UsrType_Fone){
			int uid = test.Do_Search(datatype_set, content);	
			if(uid != 0 ){
				ret = Const.RRet.RRET_USER_EXIST_FAIL;
				msg = Const.RRet.RRET_USER_EXIST_FAIL+"%"+id;	
			}else{
				msg = Const.RRet.RRET_SUCCESS+"%";	
			}
		
		}
		
		if(ret == Const.RRet.RRET_SUCCESS){
			ret = test.Update_UsrInfo(id, "user", datatype_set, content);
		}		
		
		if(ret == Const.RRet.RRET_SUCCESS){
			msg=ret+"%";
			System.out.println("DB_UpdateUsrData Successful! ");
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","10;URL=index.jsp");
			}catch(Exception e){
				
			}
		}else{
			msg=Const.RRet.RRET_UPDATE_INFO_FAIL+"%";
			System.out.println("DB_UpdateUsrData Fail!");
		}
	 }
	 
	 public void DB_AddExpertData(HttpServletResponse response, String ... args) throws SQLException{
		 	
		String msg=" ";
		Const.RRet ret = test.Do_Add_Expert(args);
		
		if(ret == Const.RRet.RRET_SUCCESS){
			msg=ret+"%";
			System.out.println("DB_UpdateExpertData Successful! ");

		}else{
			msg=String.valueOf(ret);//Const.RRet.RRET_UPDATE_INFO_FAIL+"%";
			System.out.println("DB_UpdateUsrData Fail!");
		}
		try{
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(msg);
			response.setHeader("REFRESH","10;URL=AddExpert.jsp");
		}catch(Exception e){
			
		}
	 }
	 
	 public void DB_GetUsrData(int id, Const.eUsrType datatype_get, HttpServletResponse response){
		String msg="";
		//String fidlst="";		
		String ret = test.Get_UsrInfo(datatype_get, id);	

		if(ret != null){		
			msg= Const.RRet.RRET_SUCCESS+"%"+ret;
			try{
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				//response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","10;URL=index.jsp");
			}catch(Exception e){
				
			}
			
		}else{
			msg=Const.RRet.RRET_GETUSRDATA_FAIL+"%";
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","2;URL=index.jsp");
			}catch(Exception e){
				
			}
		}
	 }
	 
	 public void DB_GetUsrData_List(int id, Const.eUsrType datatype_get, HttpServletResponse response){
		String msg="";
		//String fidlst="";		
		String ret = "";//test.Get_UsrInfo(datatype_get, id);	
		String fidlst = test.Get_Friend(id);
		
		while(fidlst.contains("/")){
			//ServerFuncRun sfr = new ServerFuncRun();
			int tmp = fidlst.indexOf("/");
			String str = fidlst.substring(0, tmp);
			ret = ret + test.Get_UsrInfo(datatype_get, Integer.valueOf(str))+"/";
			fidlst = fidlst.substring(tmp+1);
		}	
		
		if(ret != null){		
			msg= Const.RRet.RRET_SUCCESS+"%"+ret;
			try{
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				//response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","10;URL=index.jsp");
			}catch(Exception e){
				
			}
			
		}else{
			msg=Const.RRet.RRET_GETUSRDATA_FAIL+"%";
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","2;URL=index.jsp");
			}catch(Exception e){
				
			}
		}
	 }
	 
	 public void DB_GetExpert_List(int id, HttpServletResponse response){
			String msg="";
			String explst="";		
			//Const.RRet ret = test.Do_Log_In(name, pass);
			//Get_Expert_FromLoc
			//
			/*String elist = "";//test.Get_UsrInfo(datatype_get, id);	
			String nlist="";
			String tlist="";
			String lst = test.Get_Expert_FromLoc(id);
			
			while(lst.contains("/")){
				//ServerFuncRun sfr = new ServerFuncRun();
				int tmp = lst.indexOf("/");
				String str = lst.substring(0, tmp);
				System.out.println("str :" +str); 
				elist = elist + test.Get_UsrInfo(Const.eUsrType.UsrType_ExpertEId, "expert",Integer.valueOf(str))+"/";
				nlist = nlist + test.Get_UsrInfo(Const.eUsrType.UsrType_ExpertName, "expert",Integer.valueOf(str))+"/";
				//System.out.println("nlist :" +nlist);
				tlist = tlist + test.Get_UsrInfo(Const.eUsrType.UsrType_ExpertTel, "expert",Integer.valueOf(str))+"/";
				lst = lst.substring(tmp+1);
			}*/	
			
			if(1==1){
				//int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
				//ret = test.Update_UsrInfo(UsrId, Const.eUsrType.UsrType_IP, ip);
				//test.Update_IP(name, ip);
				explst=test.Get_Expert(id);			
				msg= Const.RRet.RRET_SUCCESS+"%"+explst;
				try{
					response.setContentType("text/html;charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					out.println(msg);
					response.setHeader("REFRESH","2;URL=index.jsp");
				}catch(Exception e){
					
				}
				
			}
		 }
	 
	 public void DB_GetFBF_List(String  fblist, HttpServletResponse response){
			String msg="";
			String retlst="";		
			//Const.RRet ret = test.Do_Log_In(name, pass);
			//Get_Expert_FromLoc
			//
			//String lst = test.Get_Expert_FromLoc(id);
			
			while(fblist.contains("/")){
				//ServerFuncRun sfr = new ServerFuncRun();
				int tmp = fblist.indexOf("/");
				String str = fblist.substring(0, tmp);
				System.out.println("str :" +str); 
				test.Get_UsrId(Const.eUsrType.UsrType_FBId, str);
				retlst = retlst + test.Get_UsrId(Const.eUsrType.UsrType_FBId, str)+"/";
				//nlist = nlist + test.Get_UsrInfo(Const.eUsrType.UsrType_ExpertName, "expert",Integer.valueOf(str))+"/";
				//System.out.println("nlist :" +nlist);
				//tlist = tlist + test.Get_UsrInfo(Const.eUsrType.UsrType_ExpertTel, "expert",Integer.valueOf(str))+"/";
				fblist = fblist.substring(tmp+1);
			}	
			
			if(1==1){
				//int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
				//ret = test.Update_UsrInfo(UsrId, Const.eUsrType.UsrType_IP, ip);
				//test.Update_IP(name, ip);
				//explst=test.Get_Expert(id);			
				msg= Const.RRet.RRET_SUCCESS+"%"+retlst;
				try{
					response.setContentType("text/html;charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					out.println(msg);
					response.setHeader("REFRESH","2;URL=index.jsp");
				}catch(Exception e){
					
				}
				
			}
		 }
	 
	 public void DB_GetUsrData_Login(int id, HttpServletResponse response){
			String msg="";
			//String fidlst="";		
			String ret = test.Get_UsrInfo(Const.eUsrType.UsrType_NickName, id)+"/"+
					test.Get_UsrInfo(Const.eUsrType.UsrType_Fone, id)+"/"+
							test.Get_UsrInfo(Const.eUsrType.UsrType_SearchId, id)+"/"+
							test.Get_UsrInfo(Const.eUsrType.UsrType_FBId, id)+"/"+
							test.Get_UsrInfo(Const.eUsrType.UsrType_Gmail, id)+"/";	

			if(ret != null){		
				msg= Const.RRet.RRET_SUCCESS+"%"+ret;
				try{
					response.setContentType("text/html;charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
					//response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					out.println(msg);
					response.setHeader("REFRESH","10;URL=index.jsp");
				}catch(Exception e){
					
				}
				
			}else{
				msg=Const.RRet.RRET_GETUSRDATA_FAIL+"%";
				try{
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					out.println(msg);
					response.setHeader("REFRESH","2;URL=index.jsp");
				}catch(Exception e){
					
				}
			}
		 }
	 
	 /*******
	 public void DB_GetUsrData(int id, HttpServletResponse response){
				 
			String msg=" ";
			String name = null;
			String ipaddr = null;
			String flist = null;
			String phone = null;
			String nick = null;
			/*
			if(test.Get_UserName(id)!=null)
				name = test.Get_UserName(id);
			int UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, name);
			if(test.Get_Friend(UsrId)!=null)
				flist = test.Get_Friend(UsrId);
			if(test.Get_ipaddr(name)!=null)
				ipaddr = test.Get_ipaddr(name);
			if(test.Get_Phone(name)!=null)
				phone = test.Get_Phone(name);
			if(test.Get_nick(name)!=null)
				nick = test.Get_nick(name);
			*/
	 /*******
			//data.flist = test.Get_Friend(data.name);
			//data.ipaddr = test.Get_ipaddr(data.name);
			//data.phone = test.Get_Phone(data.name);
			name = test.Get_UsrInfo(Const.eUsrType.UsrType_Pass, id);
			ipaddr = test.Get_UsrInfo(Const.eUsrType.UsrType_IP, id);
			phone = test.Get_UsrInfo(Const.eUsrType.UsrType_Fone, id);
			nick = test.Get_UsrInfo(Const.eUsrType.UsrType_NickName, id);
			flist = test.Get_Friend(id);
			
			msg= Const.RRet.RRET_SUCCESS+"%"+nick;//+"%"+data.ipaddr+"%"+data.phone;
			//System.out.println(msg);
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","2;URL=Show_Info.jsp?name="+name+"&ipaddr="+ipaddr+"&phone="+phone+"&nick="+nick+"&flist="+flist);
			}catch(Exception e){
				
			}
			
			//test.Update_Pwd(name, pass);	
	 }
	 ****///
	 
	 public void DB_Do_Search(String name, Const.eUsrType datatype, HttpServletResponse response){
		 
		String msg="";
		int id = test.Do_Search(datatype, name);	
		if(id != 0 ){
			msg = Const.RRet.RRET_SUCCESS+"%"+id;	
		}else{
			msg = Const.RRet.RRET_DOSEARCH_FAIL+"%";	
		}
		
		try{
			response.setContentType("text/html");
		
			PrintWriter out = response.getWriter();
			out.println( msg );
			//response.setHeader("REFRESH","2;URL=index.jsp");
		}catch(Exception e){
			 
		}
	 }
	 
	 public void DB_SendPushToOther(String from_id, int rec_id, String userMessage, 
			 HttpServletRequest request, HttpServletResponse response, boolean output) throws IOException{	 	
		 	URL url = null;
			try {
				url = new URL(Const.FCM_SERVER);//("https://gcm-http.googleapis.com/gcm/send");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Authorization", "key="+Const.GOOGLE_SERVER_KEY);
	        
	        conn.setDoOutput(true);
	        
	        int UsrId=0;
			if(isInteger(from_id)==true){
				UsrId = Integer.valueOf(from_id);
			}else{
				UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, from_id);
			}
			
		 	//String nickname = test.Get_UsrInfo(Const.eUsrType.UsrType_NickName, UsrId);
		 	String regId = test.Get_UsrInfo(Const.eUsrType.UsrType_RegId, rec_id);
		 	System.out.println("regId: " + regId);
		 	String postData = "{ \"registration_ids\": [ \"" + regId + "\" ], " +
	                "\"delay_while_idle\": true, " +
	                "\"data\": {\"tickerText\":\"My Ticket\", " +
	                "\"contentTitle\":\"My Title\", " +
	                "\"message\": \""+userMessage+"\"}}";
		 	
		 	conn.connect();
	        java.io.OutputStream os = conn.getOutputStream();
	        os.write(postData.getBytes("UTF-8"));
	        System.out.println(conn.getResponseCode());	        
	        InputStream stream = (InputStream) conn.getInputStream();
	        InputStreamReader isReader = new InputStreamReader(stream); 
	        //put output stream into a string
	        BufferedReader br = new BufferedReader(isReader);
	        String line;
	        while((line = br.readLine()) != null){
	            //System.out.println(line);
	        }


	        os.flush();
	        os.close();

			if(output == true){
				String msg ="";
				if(conn.getResponseCode()==200)
					msg = Const.RRet.RRET_SUCCESS + "%";
				else
					msg = Const.RRet.RRET_FAIL + "%";
				try{
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();		
					out.println(msg);
					response.setHeader("REFRESH","10;URL=index.jsp");
				}catch(Exception e){
					
				}
			}
	        
	 }
	 /*
	 public void DB_SendPushToOther(String from_id, int rec_id, String userMessage, 
			 HttpServletRequest request, HttpServletResponse response, boolean output){
		 	
		Result result = null;	
	 	System.out.println(from_id);
		//String share = request.getParameter("shareRegId");
	 	int UsrId=0;
		if(isInteger(from_id)==true){
			UsrId = Integer.valueOf(from_id);
		}else{
			UsrId = test.Get_UsrId(Const.eUsrType.UsrType_Email, from_id);
		}
	 	//String nickname = test.Get_UsrInfo(Const.eUsrType.UsrType_NickName, UsrId);
	 	String regId = test.Get_UsrInfo(Const.eUsrType.UsrType_RegId, rec_id);
		// GCM RedgId of Android device to send push notification
	 	//String sendmsg = nickname + ": " + userMessage;
		String msg ="";
		//if (share != null && !share.isEmpty()) {
		
		try {
			Sender sender = new Sender(Const.GOOGLE_SERVER_KEY);
			Message message = new Message.Builder().addData(Const.MESSAGE_KEY, userMessage).build();//new Message.Builder().timeToLive(30)
					//.delayWhileIdle(true).addData(Const.MESSAGE_KEY, userMessage).build();
			System.out.println("regId: " + regId);
			result = sender.send(message, regId, 1);
			msg = Const.RRet.RRET_SUCCESS + "%" + result.toString();
			request.setAttribute("pushStatus", result.toString());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			request.setAttribute("pushStatus",
					"RegId required: " + ioe.toString());
			msg = Const.RRet.RRET_FAIL + "%" + ioe.toString();
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("pushStatus", e.toString());
			msg = Const.RRet.RRET_FAIL + "%" + e.toString();
		}
		if(output == true){
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();		
				out.println(msg);
				response.setHeader("REFRESH","10;URL=index.jsp");
			}catch(Exception e){
				
			}
		}
			//request.getRequestDispatcher("index.jsp")
				//	.forward(request, response);
			//}
			
			 
	 }
	 */
	 public void DB_SendPushToOther(String regId, String userMessage, 
			 HttpServletRequest request, HttpServletResponse response, boolean output) throws IOException{
		 	
		Result result = null;	
	 	URL url = null;
		try {
			url = new URL(Const.FCM_SERVER);//("https://gcm-http.googleapis.com/gcm/send");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "key="+Const.GOOGLE_SERVER_KEY);
        
        conn.setDoOutput(true);
		
        //String regId = test.Get_UsrInfo(Const.eUsrType.UsrType_RegId, rec_id);
	 	System.out.println("regId: " + regId);
	 	String postData = "{ \"registration_ids\": [ \"" + regId + "\" ], " +
                "\"delay_while_idle\": true, " +
                "\"data\": {\"tickerText\":\"My Ticket\", " +
                "\"contentTitle\":\"My Title\", " +
                "\"message\": \""+userMessage+"\"}}";
	 	
	 	conn.connect();
        java.io.OutputStream os = conn.getOutputStream();
        os.write(postData.getBytes("UTF-8"));
        System.out.println(conn.getResponseCode());	        
        InputStream stream = (InputStream) conn.getInputStream();
        InputStreamReader isReader = new InputStreamReader(stream); 
        //put output stream into a string
        BufferedReader br = new BufferedReader(isReader);
        String line;
        while((line = br.readLine()) != null){
            System.out.println(line);
        }


        os.flush();
        os.close();

		if(output == true){
			String msg ="";
			if(conn.getResponseCode()==200)
				msg = Const.RRet.RRET_SUCCESS + "%";
			else
				msg = Const.RRet.RRET_FAIL + "%";
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();		
				out.println(msg);
				response.setHeader("REFRESH","10;URL=index.jsp");
			}catch(Exception e){
				
			}
		}
        
        /*
		try {
			Sender sender = new Sender(Const.GOOGLE_SERVER_KEY);
			Message message = new Message.Builder().addData(Const.MESSAGE_KEY, userMessage).build();//new Message.Builder().timeToLive(30)
					//.delayWhileIdle(true).addData(Const.MESSAGE_KEY, userMessage).build();
			System.out.println("regId: " + regId);
			result = sender.send(message, regId, 1);
			msg = Const.RRet.RRET_SUCCESS + "%" + result.toString();
			request.setAttribute("pushStatus", result.toString());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			request.setAttribute("pushStatus",
					"RegId required: " + ioe.toString());
			msg = Const.RRet.RRET_FAIL + "%" + ioe.toString();
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("pushStatus", e.toString());
			msg = Const.RRet.RRET_FAIL + "%" + e.toString();
		}
		if(output == true){
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();		
				out.println(msg);
				response.setHeader("REFRESH","10;URL=index.jsp");
			}catch(Exception e){
				
			}
		}
			//request.getRequestDispatcher("index.jsp")
				//	.forward(request, response);
			//}
			
			 */
	 }
	 
	 
	 private  boolean isInteger(String s) {
		    try { 
		        Integer.parseInt(s); 
		    } catch(NumberFormatException e) { 
		        return false; 
		    } catch(NullPointerException e) {
		        return false;
		    }
		    // only got here if we didn't return false
		    return true;
	 }

}