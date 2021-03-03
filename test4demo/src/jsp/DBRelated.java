package jsp;

//import java.awt.List;
import java.io.IOException;
//import java.io.PrintWriter;




import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.apache.commons.fileupload.DiskFileUpload;


/**
 * Servlet implementation class DBRelated
 */
@WebServlet("/DBRelated")
public class DBRelated extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBFunction dbf = null;    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DBRelated() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		dbf = new DBFunction(); 
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Service of Servlet");	
		Const.Command cmd = null;		
		String usr_id = null;
		String datatype = "", data = "", passwd = "";
		
		
		HttpSession session = request.getSession(false);
		try{
			cmd = (Const.Command)session.getAttribute("cmd");
			usr_id = (String)session.getAttribute("id");
		}catch(Exception e){	
			cmd = Const.Command.valueOf(request.getParameter("cmd"));
			usr_id = request.getParameter("id");
		}
		
		
		System.out.println("Cmd:"+cmd);
		
		switch(cmd){
			case Cmd_Login:
				String name_login=request.getParameter("name");
				String pass_login=request.getParameter("pass");
				String ip = request.getRemoteAddr();			
				System.out.println("name:"+name_login+"  pass:"+pass_login+"  ip:"+ip);
				dbf.DB_Login(name_login, pass_login, ip, response);
				break;
			case Cmd_Login_Type:
				datatype = request.getParameter("datatype");
				data = request.getParameter("data");
				passwd = request.getParameter("pass");
				try {
					dbf.DB_Login(response, datatype, data, passwd, request.getRemoteAddr());				
				} catch (AddressException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (MessagingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				break;
			case Cmd_Register_Type:
				datatype = request.getParameter("datatype");
				data = request.getParameter("data");
				passwd = request.getParameter("pass");
				try {
					dbf.DB_Register(response, datatype, data, passwd);
				} catch (AddressException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case Cmd_Logout:	
				String name_logout;
				try{
					name_logout=(String)session.getAttribute("name");
				}catch(Exception e){	
					name_logout = request.getParameter("name");
				}
				dbf.DB_Logout(name_logout, response);
				break;
			case Cmd_Register:
				String name_reg=request.getParameter("name");
				String pass_reg=request.getParameter("pass");
				String phone_reg=request.getParameter("phone");
				String nick_reg=request.getParameter("nick");
				String reg_type=null;
				reg_type = request.getParameter("datatype");
				String reg_data = request.getParameter("data");
				try {
					if(reg_type!=null){
						dbf.DB_Register(name_reg, pass_reg, phone_reg, nick_reg, response, reg_type,reg_data);
					} else {
						dbf.DB_Register(name_reg, pass_reg, phone_reg, nick_reg, response);
					}
					
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case Cmd_ForgetPwd:
				String name_fgtpwd=request.getParameter("name");
				try {
					dbf.DB_ForgetPwd(name_fgtpwd, response);
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case Cmd_AddFriend:
				//int fid = Integer.valueOf(request.getParameter("id"));
				//String name_frndadd=request.getParameter("name");
				//if(name_frndadd==null)
					//name_frndadd = (String)session.getAttribute("name");
				String name_frndadded=request.getParameter("search_id");
				dbf.DB_Addfreind(usr_id, name_frndadded, response, request);
				//dbf.DB_SendPushToOther(uid_send, uid_receive, senddata, request, response);
				break;			
			case Cmd_AddFriendList:
				//int fid = Integer.valueOf(request.getParameter("id"));
				String flist=request.getParameter("data");
				Const.eUsrType datatype_flist = Const.eUsrType.valueOf(request.getParameter("datatype"));
				dbf.DB_AddfreindList(usr_id, flist, datatype_flist, response, request);
				//dbf.DB_SendPushToOther(uid_send, uid_receive, senddata, request, response);
				break;	
			case Cmd_ChangePwd:
				String name_chpwd=request.getParameter("name");
				String pass_chpwd=request.getParameter("pass");
				dbf.DB_ChangePwd(name_chpwd, pass_chpwd, response);
				break;
			case Cmd_GetUsrData:
				Const.eUsrType datatype_get = Const.eUsrType.valueOf(request.getParameter("datatype"));
				//usr_id = (String)session.getAttribute("id");//Integer.valueOf(request.getParameter("id"));
				dbf.DB_GetUsrData(Integer.valueOf(usr_id), datatype_get, response);
				break;
			case Cmd_GetUsrDataList:
				Const.eUsrType datatype_getlist = Const.eUsrType.valueOf(request.getParameter("datatype"));
				String fidlist = request.getParameter("fidlist");
				//usr_id = (String)session.getAttribute("id");//Integer.valueOf(request.getParameter("id"));
				//dbf.DB_GetUsrData(Integer.valueOf(usr_id), datatype_get, response);
				dbf.DB_GetUsrData_List(Integer.valueOf(usr_id), datatype_getlist, response);
				break;	
			case Cmd_DoSearch:
				Const.eUsrType datatype_search = Const.eUsrType.valueOf(request.getParameter("datatype"));
				String data_search=request.getParameter("data");
				dbf.DB_Do_Search(data_search, datatype_search, response);
				break;
			case Cmd_UpdateUsrData:
				Const.eUsrType datatype_set = Const.eUsrType.valueOf(request.getParameter("datatype"));
				String content = request.getParameter("data");
				String para = new String(content.getBytes("ISO-8859-1"), "UTF-8");
				//System.out.println(para+"/"+content);
				dbf.DB_UpdateUsrData(Integer.valueOf(usr_id), datatype_set, para, request, response);
				break;	
			case Cmd_UpdatePhoto:
				//usr_id = "5";			
				String imgstr = request.getParameter("image");
				//try {
				dbf.DB_Do_Upload_Photo2(usr_id, imgstr, request, response);
				//} catch (FileUploadException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				//} catch (Base64DecodingException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				//}
				
				break;
			case Cmd_SendPushToOther:
				String uid_send = request.getParameter("id");
				int uid_receive = Integer.valueOf(request.getParameter("rec_id"));
				String msg = request.getParameter("message");
				String senddata = new String(msg.getBytes("ISO-8859-1"), "UTF-8");
				dbf.DB_SendPushToOther(uid_send, uid_receive, senddata, request, response, true);
				break;	
			case Cmd_GetLoginData:
				dbf.DB_GetUsrData_Login(Integer.valueOf(usr_id), response);
				break;
			case Cmd_GetExpertList:
				//String name_login=request.getParameter("name");
				//String pass_login=request.getParameter("pass");
				String Loc_id = request.getParameter("loc_id");			
				//System.out.println("name:"+name_login+"  pass:"+pass_login+"  ip:"+ip);
				//dbf.DB_Login(name_login, pass_login, ip, response);
				dbf.DB_GetExpert_List(Integer.valueOf(Loc_id), response);
				break;	
			case Cmd_GetFBFList:
				String Fbf_list = request.getParameter("fblist");			
				//System.out.println("name:"+name_login+"  pass:"+pass_login+"  ip:"+ip);
				//dbf.DB_Login(name_login, pass_login, ip, response);
				dbf.DB_GetFBF_List(Fbf_list, response);
				break;
			case Cmd_AddExpert:
				//String name_login=request.getParameter("name");
				//String pass_login=request.getParameter("pass");
				String expcontent = request.getParameter("name");
				//String senddata = new String(expcontent.getBytes("ISO-8859-1"), "UTF-8");
				String Expert_name = new String(expcontent.getBytes("ISO-8859-1"), "Big5");	
				String Expert_tel = request.getParameter("tel");
				expcontent = request.getParameter("addr");
				String Expert_addr = new String(expcontent.getBytes("ISO-8859-1"), "Big5");
				String Expert_locid = request.getParameter("type");
				String Expert_copr = request.getParameter("copr");
				System.out.println("name:"+Expert_name+"  tel:"+Expert_tel+"  addr:"+Expert_addr);
				System.out.println("name:"+expcontent+"  locid:"+Expert_locid+"  co?:"+Expert_copr);
				//dbf.DB_Login(name_login, pass_login, ip, response);
				try {
					dbf.DB_AddExpertData(response, Expert_name, Expert_tel, Expert_locid, Expert_addr, Expert_copr);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;		
			default:
				break;
		}
	}

}
