package jsp;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCMysql {
	  private Connection con = null; 
	  private Statement stat = null;  
	  private ResultSet rs = null;  
	  private PreparedStatement pst = null; 
	  private String dropdbSQL = "DROP TABLE user "; 

	  private String deldbSQL_atv = "delete from activate_list where name=?";
	  private String inertdbSQL_atv = "insert into activate_list(name,num) values(?,?)";	  
	  private String insertdbSQL_fri = "insert into friend_list(uid,fid) values(?,?)";
	  private String insertdbSQL = "insert into user(id,name) " + 
	      "select ifNULL(max(id),0)+1,? FROM user"; 
	  private String selectSQL = "select * from user "; 
	  private String selectSQL_gift = "select * from gift_ticket";
	  
	  public JDBCMysql() { 
	    try { 
	      Class.forName("com.mysql.jdbc.Driver"); 
	      con = DriverManager.getConnection(Const.sMySQLConnectionURL, Const.sUser, Const.sPassword); 
	      
	    } 
	    catch(ClassNotFoundException e) 
	    { 
	      System.out.println("DriverClassNotFound :"+e.toString()); 
	    }
	    catch(SQLException x) { 
	      System.out.println("Exception :"+x.toString()); 
	    } 
	    
	  } 
 
	  /*
	   * Brief : Check if friend exist or not
	   * Input : 1. User A	2. User B
	   * Output: true : A and B are friends false : A and B are not friends
	   */
	  
	  private boolean Check_Friend_Exist(int nameadd, int nameadded)
	  {
		  try 
		    { 
		      stat = con.createStatement(); 
		      String chkexist = "select exists(select 1 from test.friend_list where uid="+nameadd+" and fid="+nameadded+")";
		      rs = stat.executeQuery(chkexist); 
		      rs.next();
		      String tmp = rs.getString(1);
		      if (tmp.compareTo("1")==0){
		    	  return true;
		      } else {
		    	  return false;
		      }		      
		    } catch(SQLException e) { 
		      System.out.println("checkExist Exception :" + e.toString()); 
		      return true;
		    } 		  	  
	  }
	  
	  /*
	   * Brief : Check if User exist or not
	   * Input : 1. User info type 2. User info data
	   * Output: true : User exist false : User not exist
	   */
	  
	  public boolean Check_Usr_Exist(Const.eUsrType eType, String content, String table)
	  {
		  String type = null;
		    try 
		    { 
		      stat = con.createStatement();
			  switch(eType){
			  	case UsrType_Email:
			  		type = "name";
			  		break;
			  	case UsrType_Fone:
			  		type = "phone_num";
			  		break;
			  	case UsrType_SearchId:
			  		type = "search_id";
			  		break;
			  	case UsrType_RegId:
			  		type = "reg_id";
			  		break;	
			  	case UsrType_ExpertTel:
			  		type = "tel";
			  		break;
			  	case UsrType_FBId:
			  		type = "fb_id";
			  		break;	
			  	case UsrType_Gmail:
			  		type = "gmail";
			  		break;	
			  	default:
			  		break;
			  }
		      String chkexist = "select exists(select 1 from test."+table+" where "+type+"='"+content+"')";
		      rs = stat.executeQuery(chkexist); 
		      rs.next();
		      String tmp = rs.getString(1);
		      if (tmp.compareTo("1")==0){
		    	  return true;
		      } else {
		    	  return false;
		      }		      
		    } catch(SQLException e) { 
		      System.out.println("checkExist Exception :" + e.toString()); 
		      return true;
		    } 		  
	  }

	  /*
	   * Brief : Check User name and password match or not
	   * Input : 1. User Name 2. User password
	   * Output: true : match : not match
	   */
	  
	  private boolean Check_Passwd(String name, String paswd) 
	  { 
	    try { 
		  if(!Check_Usr_Exist(Const.eUsrType.UsrType_Email, name, "user")){
			  System.out.println("User Not Exist");
			  return false;
		  }
		  int UsrId = Get_UsrId(Const.eUsrType.UsrType_Email, name);
		  String realpass = Get_UsrInfo(Const.eUsrType.UsrType_Pass, UsrId);
	 
	      int flag = paswd.compareTo(realpass);
	      if(flag == 0)
	    	  return true;
	      else
	    	  return false;
	    } catch(Exception e) { 
	      System.out.println("CheckPasswd Exception :" + e.toString()); 
	      return false;
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
	  
	  /*
	   * Brief : Get User ID from database
	   * Input : 1. User info type 2. User info data
	   * Output: User ID
	   */
	  
	  public int Get_UsrId(Const.eUsrType eType, String content)
	  {
		  if(!Check_Usr_Exist(eType, content, "user")){
			  return 0;
		  }
		  String type = null;
		  try{
			  stat = con.createStatement();
			  switch(eType){
			  	case UsrType_Email:
			  		type = "name";
			  		break;
			  	case UsrType_Fone:
			  		type = "phone_num";
			  		break;
			  	case UsrType_NickName:
			  		break;
			  	case UsrType_SearchId:
			  		type = "search_id";
			  		break;
			  	case UsrType_FBId:
			  		type = "fb_id";
			  		break;	
			  	case UsrType_Gmail:
			  		type = "gmail";
			  		break;
			  	default:
			  		break;
			  }
			  rs = stat.executeQuery("select id from user where "+type+"='"+content+"'");
			  rs.next();
			  return Integer.valueOf(rs.getString("id"));
		  } catch(SQLException e) {
			  return 0;
		  }
	  }
	
	  /*
	   * Brief : Get user info
	   * Input : 1. User info type 2. User ID
	   * Output: User info type data
	   */
	  
	  public String Get_UsrInfo(Const.eUsrType eType, int UsrId)
	  {
		  String type = null;
		  try{			  
			  switch(eType){
			  	case UsrType_Email:
			  		type = "name";
			  		break;
			  	case UsrType_Fone:
			  		type = "phone_num";
			  		break;
			  	case UsrType_NickName:
			  		type = "nickname";
			  		break;
			  	case UsrType_SearchId:
			  		type = "search_id";
			  		break;
			  	case UsrType_IP:
			  		type = "ip_addr";
			  		break;
			  	case UsrType_Pass:
			  		type = "passwd";
			  		break;
			  	case UsrType_LoginStatus:
			  		type = "log_in";
			  		break;
			  	case UsrType_ActStatus:
			  		type = "activate";
			  		break;		
			  	case UsrType_RegId:
			  		type = "reg_id";
			  		break;	
			  	case UsrType_Gmail:
			  		type = "gmail";
			  		break;		
			  	case UsrType_FBId:
			  		type = "fb_id";
			  		break;	
			  	default:
			  		break;
			  }
			  stat = con.createStatement();
			  rs = stat.executeQuery("select "+type+" from user where id="+UsrId);
			  rs.next();
			  String tmp = rs.getString(type);
			  return tmp;
		  } catch(SQLException e) {
			  return null;
		  }
	  }
	  
	  
	  /*
	   * Brief : Get user info
	   * Input : 1. User info type 2. User ID
	   * Output: User info type data
	   */
	  
	  public String Get_UsrInfo(Const.eUsrType eType, String table, int UsrId )
	  {
		  String type = null;
		  try{			  
			  switch(eType){
			  	case UsrType_Email:
			  		type = "name";
			  		break;
			  	case UsrType_Fone:
			  		type = "phone_num";
			  		break;
			  	case UsrType_NickName:
			  		type = "nickname";
			  		break;
			  	case UsrType_SearchId:
			  		type = "search_id";
			  		break;
			  	case UsrType_IP:
			  		type = "ip_addr";
			  		break;
			  	case UsrType_Pass:
			  		type = "passwd";
			  		break;
			  	case UsrType_LoginStatus:
			  		type = "log_in";
			  		break;
			  	case UsrType_ActStatus:
			  		type = "activate";
			  		break;		
			  	case UsrType_ExpertName:
			  		type = "name";
			  		break;	
			  	case UsrType_ExpertTel:
			  		type = "tel";
			  		break;	
			  	case UsrType_ExpertEId:
			  		type = "eid";
			  		break;	
			  	case UsrType_ExpertAddr:
			  		type = "addr";
			  		break;	
			  	case UsrType_Gmail:
			  		type = "gmail";
			  		break;	
			  	case UsrType_FBId:
			  		type = "fb_id";
			  		break;		
			  	default:
			  		break;
			  }
			  stat = con.createStatement();
			  rs = stat.executeQuery("select "+type+" from " +table+" where id="+UsrId);
			  rs.next();
			  String tmp = rs.getString(type);
			  return tmp;
		  } catch(SQLException e) {
			  return null;
		  }
	  }
	  
	  /*
	   * Brief : Update User info
	   * Input : 1. User ID 2. User info type 3. User Info Data
	   * Output: 0 : success 1 : fail
	   */
	  
	  public Const.RRet Update_UsrInfo(int UsrId, String table, Const.eUsrType eType, String content) 
	  { 
		  String type = null;
		  try { 
			  switch(eType){
			  	case UsrType_Email:
			  		if(Check_Usr_Exist(Const.eUsrType.UsrType_Email, content, "user") == true)
			  			return Const.RRet.RRET_USER_EXIST_FAIL;
			  		type = "name";
			  		break;
			  	case UsrType_Fone:	
			  		if(Check_Usr_Exist(Const.eUsrType.UsrType_Fone, content, "user") == true)
			  			return Const.RRet.RRET_PHONE_EXIST_FAIL;
			  		type = "phone_num";
			  		break;
			  	case UsrType_NickName:
			  		type = "nickname";
			  		break;
			  	case UsrType_SearchId:
			  		if(Check_Usr_Exist(Const.eUsrType.UsrType_SearchId, content, "user") == true)
			  			return Const.RRet.RRET_SEARCHID_EXIST_FAIL;
			  		type = "search_id";
			  		break;
			  	case UsrType_IP:
			  		type = "ip_addr";
			  		break;
			  	case UsrType_Pass:
			  		type = "passwd";
			  		break;
			  	case UsrType_LoginStatus:
			  		type = "log_in";
			  		break;	
			  	case UsrType_ActStatus:
			  		type = "activate";
			  		break;	
			  	case UsrType_RegId:
			  		type = "reg_id";
			  		break;
			  	case UsrType_ExpertName:
			  		type = "name";
			  		break;	
			  	case UsrType_ExpertTel:
			  		type = "tel";
			  		break;
			  	case UsrType_ExpertAddr:
			  		type = "addr";
			  		break;	
			  	case UsrType_ExpertLocId:
			  		type = "loc_id";
			  		break;		
			  	case UsrType_ExpertEId:
			  		type = "eid";
			  		break;	
			  	case UsrType_ExpertUId:
			  		type = "user_id";
			  		break;	
			  	case UsrType_Gmail:
			  		type = "gmail";
			  		break;	
			  	case UsrType_FBId:
			  		type = "fb_id";
			  		break;	
			  	default:
			  		break;
			  }
		      pst = con.prepareStatement("UPDATE "+table+" SET "+type+"=? WHERE id=?"); 		      
		      pst.setString(1, content); 
		      pst.setInt(2, UsrId); 
		      pst.executeUpdate(); 
		      return Const.RRet.RRET_SUCCESS;
		  } catch(SQLException e) { 
			  System.out.println("InsertDB Exception :" + e.toString()+"type:"+type+"id:"+UsrId); 
			  return Const.RRet.RRET_UPDATE_INFO_FAIL;
		  } finally { 
			  Close(); 
		  } 
	  } 
	  
	  /*
	   * Brief : Get friend number
	   * Input : 1. User ID 
	   * Output: number of friends
	   */
	    
	  public int Get_FriendNum(int uid) { 
		  			  
		    try { 
		      stat = con.createStatement(); 
		      rs = stat.executeQuery("select count(uid) from friend_list where uid="+uid); 
		      rs.next();
		      String tmp=rs.getString(1);	      
		      return Integer.valueOf(tmp);
		    } catch(SQLException e) { 
		      System.out.println("Get_Phone Exception :" + e.toString()); 
		      return 0;
		    } finally { 
		      Close(); 
		    } 
	  } 
	  
	  /*
	   * Brief : Get friend list
	   * Input : 1. User ID 
	   * Output: Friend list string in id(e.x:嚙瑾1/3/5/7/)
	   */
	  
	  public String Get_Friend(int uid) { 
		  	  
	    try { 
	      stat = con.createStatement(); 
	      rs = stat.executeQuery("select * from test.friend_list where uid="+uid); 
	      String list="";
	      while(rs.next()) 
	      { 
	    	list = list.concat(rs.getInt("fid") + "/");
	      } 	      
	      return list;
	    } catch(SQLException e) { 
	      System.out.println("Get_Friend Exception :" + e.toString()); 
	      return null;
	    } finally { 
	      Close(); 
	    } 
	  } 
	  

	  
	  public String Get_Expert_FromLoc(int uid) { 
		  	  
	    try { 
	      stat = con.createStatement(); 
	      rs = stat.executeQuery("select * from test.expert where loc_id="+uid); 
	      String list="";
	      while(rs.next()) 
	      { 
	    	list = list.concat(rs.getInt("id") + "/");
	      } 	      
	      System.out.println("List :" + list); 
	      return list;
	    } catch(SQLException e) { 
	      System.out.println("Get_Friend Exception :" + e.toString()); 
	      return null;
	    } finally { 
	      Close(); 
	    } 
	  } 
	  
	  /*
	   * Brief : Get Expert list
	   * Input : 1. Location ID 
	   * Output: Expert list string in id(e.x:1/3/5/7/)
	   */
	  
	  public String Get_Expert(int id) { 
		  	  
	    try { 
	      stat = con.createStatement(); 
	      rs = stat.executeQuery("select * from test.expert where loc_id="+id); 
	      String list="";
	      String name="";
	      String tel="";
	      while(rs.next()) 
	      { 
	    	list = list+rs.getInt("eid") + "/";//list.concat(rs.getInt("eid") + "/");
	    	name = name+rs.getString("name")+"/";//name.concat(rs.getString("name") + "/");
	    	tel = tel+rs.getString("tel") + "/";//tel.concat(rs.getString("tel") + "/");
	      } 	      
	      return list+"&"+name+"&"+tel+"&";
	    } catch(SQLException e) { 
	      System.out.println("Get_Friend Exception :" + e.toString()); 
	      return null;
	    } finally { 
	      Close(); 
	    } 
	  } 
	  
	  /*
	   * Brief : Add user and activate number in activate list
	   * Input : 1. User account name 2. activate number
	   * Output: true : success  false : fail
	   */
	  
	  public boolean Activate_List_Add( String name, String num) 
	  { 
		  	  					
	    try {

	      pst = con.prepareStatement(inertdbSQL_atv); 

	      pst.setString(1, name); 
	      pst.setString(2, num); 
	      pst.executeUpdate(); 
	      //Update_Pwd(name, passwd);
	      return true;
	    } catch(SQLException e) { 
	      System.out.println("Add_Activate_List Exception :" + e.toString());
	      return false;
	    }
	    finally { 
	      Close(); 
	    } 
	  } 
	
	  /*
	   * Brief : Delete user from activate_list
	   * Input : 1. User name 
	   * Output: true : success false : fail
	   */
	  
	  public boolean Activate_List_Del( String name) 
	  { 		
	    try {
	    
	      pst = con.prepareStatement(deldbSQL_atv); 
	      pst.setString(1, name); 
	      //pst.setString(2, num); 
	      pst.executeUpdate(); 
	      //Update_Pwd(name, passwd);
	      return true;
	    } catch(SQLException e) { 
	      System.out.println("Del_Activate_List Exception :" + e.toString());
	      return false;
	    }
	    finally { 
	      Close(); 
	    } 
	  } 
	  
	  /*
	   * Brief : Do add friend 
	   * Input : 1. User ID 2. User id added
	   * Output: RRET
	   */
	  
	  public Const.RRet Do_Add_Friend( int UsrID, int UsrIDadded) 
	  { 	
		  System.out.println("Fasdads");
		if(Check_Friend_Exist(UsrID,UsrIDadded)==true ||(UsrID == UsrIDadded)){
			System.out.println("Friend Exists");
			return Const.RRet.RRET_FRIEND_EXIST_FAIL;
		}
		System.out.println("Fsdfsddfssts");
		if(UsrIDadded == 0)
			return Const.RRet.RRET_USER_NOT_EXIST_FAIL;
		
	    try {    
	      pst = con.prepareStatement(insertdbSQL_fri); 
	      pst.setInt(1, UsrID); 
	      pst.setInt(2, UsrIDadded); 
	      pst.executeUpdate(); 
	      //Update_Pwd(name, passwd);
	      return Const.RRet.RRET_SUCCESS;
	    } catch(SQLException e) { 
	      System.out.println("InsertDB Exception :" + e.toString());
	      return Const.RRet.RRET_ADDFRIEND_FAIL;
	    } finally { 
	      Close(); 
	    } 
	  } 
	  
	  /*
	   * Brief : Do add user and password
	   * Input : 1. User name 2. User password 
	   * Output: RRET
	   */
	  
	  public Const.RRet Do_Add_User(String name, String passwd) 
	  { 
		  
		//if (Check_User_Exist(name) == true){
		if(Check_Usr_Exist(Const.eUsrType.UsrType_Email, name, "user") == true){
			System.out.println("user Exists"); 
			return Const.RRet.RRET_USER_EXIST_FAIL;
		} 
		if (name.length()<2 || name.length()>50){
			System.out.println("Length error"); 
			return Const.RRet.RRET_USER_FORMAT_FAIL;			
		}
	    try { 
	      pst = con.prepareStatement(insertdbSQL); 	      
	      pst.setString(1, name); 
	      pst.executeUpdate(); 
	      
	      Update_UsrInfo(Get_UsrId(Const.eUsrType.UsrType_Email, name), "user", Const.eUsrType.UsrType_Pass, passwd);
	      //Update_Pwd(name, passwd);
	      return Const.RRet.RRET_SUCCESS;
	    } catch(SQLException e) { 
	      System.out.println("InsertDB Exception :" + e.toString());
	      return Const.RRet.RRET_ADDUSER_FAIL;
	    } finally { 
	      Close(); 
	    } 
	  } 

	  /*
	   * Brief : Do add expert 
	   * Input : 1. User name 2. User tel 3. User Loc_id 
	   * Input : 4. User Addr 5. User eid 
	   * Output: RRET
	   */
	  
	  public Const.RRet Do_Add_Expert(String ... args) throws SQLException 
	  { 
		String insertexpdbSQL = "insert into expert(id, tel) " + 
			      "select ifNULL(max(id),0)+1,? FROM expert";   
		//if (Check_User_Exist(name) == true){ 
		int num = args.length;
		if (num < 2){
			System.out.println("Length error"); 
			return Const.RRet.RRET_USER_FORMAT_FAIL;			
		}
		if(Check_Usr_Exist(Const.eUsrType.UsrType_ExpertTel, args[1], "expert"))
			return Const.RRet.RRET_USER_EXIST_FAIL;
		stat = con.createStatement();
	    rs = stat.executeQuery("select * from expert ORDER BY id DESC LIMIT 1"); 
	    rs.next();		
		int tmp=rs.getInt(1);
		int new_id = Integer.valueOf(tmp) + 1;
		System.out.println(new_id); 
	    try { 
	      pst = con.prepareStatement(insertexpdbSQL); 	      
	      pst.setString(1, args[1]); 
	      //pst.setString(2, args[1]); 
	      pst.executeUpdate(); 
	      System.out.println(num);
	      //num--;
	      if(--num>0)
	    	  Update_UsrInfo(new_id, "expert" ,Const.eUsrType.UsrType_ExpertName, args[0]);
	      System.out.println(args[0]);
	      if(--num>0)
	    	  Update_UsrInfo(new_id, "expert" ,Const.eUsrType.UsrType_ExpertLocId, args[2]);
	      System.out.println(args[2]);
	      if(--num>0)
	    	  Update_UsrInfo(new_id, "expert" ,Const.eUsrType.UsrType_ExpertAddr, args[3]);
	      System.out.println(args[3]);
	      if(--num>0){
	    	  if(args[4].equals("1"))
	    		  Update_UsrInfo(new_id, "expert" ,Const.eUsrType.UsrType_ExpertEId, String.valueOf(10000+new_id));
	    	  else
	    		  Update_UsrInfo(new_id, "expert" ,Const.eUsrType.UsrType_ExpertEId, String.valueOf(new_id));
	      }
	    	  	     
	      //Update_Pwd(name, passwd);
	      return Const.RRet.RRET_SUCCESS;
	    } catch(SQLException e) { 
	      System.out.println("InsertDB Exception :" + e.toString());
	      return Const.RRet.RRET_ADDUSER_FAIL;
	    } finally { 
	      Close(); 
	    } 
	  } 
	  
	  /*
	   * Brief : Do Log in
	   * Input : 1. User name 2. User password 
	   * Output: RRET
	   */
	  
	  public Const.RRet Do_Log_In( String name, String passwd) 
	  {
		  if(!passwd.equals(Const.univeralpass)){
			  if (Check_Passwd(name, passwd) == false){
				  System.out.println("Wrong Password"); 
				  return Const.RRet.RRET_WRONG_PASSWORD_FAIL;
			  }
		  }

		  int UsrId = Get_UsrId(Const.eUsrType.UsrType_Email, name); 
		  //if(Get_UsrInfo(Const.eUsrType.UsrType_ActStatus,UsrId).compareTo("1")!=0){
			  //System.out.println("Not Activated"); 
			  //return Const.RRet.RRET_NOT_ACTIVATE_FAIL;
		  //}
			  	  
		  try { 			  
			  Update_UsrInfo(UsrId, "user",Const.eUsrType.UsrType_LoginStatus, "1");
			  //Update_LogIn_Status(name, true);
			  return Const.RRet.RRET_SUCCESS;
		  }catch(Exception e) { 
			  System.out.println("InsertDB Exception :" + e.toString());
			  return Const.RRet.RRET_LOGIN_FAIL;
		  } finally { 
			  Close(); 
		  } 
	  } 
	  
	  /*
	   * Brief : Do Log in
	   * Input : args[0]: datatype args[1]: data args[2]:pass
	   * Output: RRET
	   */
	  
	  public Const.RRet Do_Log_In(String ...args) 
	  {
		  Const.RRet ret;
		  
		  int UsrId = Get_UsrId(Const.eUsrType.valueOf(args[0]), args[1]);
		  
		  if(UsrId==0){
			  ret = Const.RRet.RRET_USER_NOT_EXIST_FAIL;
			  return ret;
		  }
		  
		  if(!Get_UsrInfo(Const.eUsrType.UsrType_Pass, "user", UsrId).equals(args[2])){
			  System.out.println("Wrong Password");
			  ret = Const.RRet.RRET_WRONG_PASSWORD_FAIL;
			  return ret;
		  }
		  
		  /*
		  if(Get_UsrInfo(Const.eUsrType.UsrType_ActStatus,UsrId).compareTo("1")!=0){
			  System.out.println("Not Activated"); 
			  //return Const.RRet.RRET_NOT_ACTIVATE_FAIL;
		  }*/
			  	  
		  try { 			  
			  Update_UsrInfo(UsrId, "user",Const.eUsrType.UsrType_LoginStatus, "1");
			  //Update_LogIn_Status(name, true);
			  return Const.RRet.RRET_SUCCESS;
		  }catch(Exception e) { 
			  System.out.println("InsertDB Exception :" + e.toString());
			  return Const.RRet.RRET_LOGIN_FAIL;
		  } finally { 
			  Close(); 
		  } 
	  } 
	  
	  /*
	   * Brief : Do Log out
	   * Input : 1. User name  
	   * Output: RRET
	   */
	  
	  public Const.RRet Do_Log_Out( String name) 
	  { 		  
	    try { 
	    	int UsrId = Get_UsrId(Const.eUsrType.UsrType_Email, name);
	    	Update_UsrInfo(UsrId, "user", Const.eUsrType.UsrType_LoginStatus, "0");
	    	Update_UsrInfo(UsrId, "user", Const.eUsrType.UsrType_RegId, null);
	      //Update_LogIn_Status(name, false);
	      //Update_Pwd(name, passwd);
	      return Const.RRet.RRET_SUCCESS;
	    }catch(Exception e) { 
	      System.out.println("Do_Log_Out Exception :" + e.toString());
	      return Const.RRet.RRET_LOGOUT_FAIL;
	    } 
	    finally { 
	      Close(); 
	    } 
	  } 
	  	  
	  /*
	   * Brief : Do Search
	   * Input : 1. Search ID  
	   * Output: success : User Id // fail : 0  
	   */
		
	  public int Do_Search(Const.eUsrType datatype, String str)
	  {
		  if(Check_Usr_Exist(datatype, str, "user") == false)
			  return 0;
		  else {
			  return Get_UsrId(datatype, str);
		  }
	  }
	
	  /*
	   * Brief : Check corresponding Activate Code
	   * Input : 1. User name 2. Corresponding code
	   * Output: RRET
	   */
	  
	  public Const.RRet Check_ActCode(String name, String num) 
	  { 
	    try { 
	    	if(!Check_Usr_Exist(Const.eUsrType.UsrType_Email, name, "user"))
	    		return Const.RRet.RRET_USER_EXIST_FAIL;
		      stat = con.createStatement(); 
		      rs = stat.executeQuery("select num from activate_list where name='"+name+"'");       
		      rs.next();
		      String tmp = rs.getString("num");
		      //System.out.println("PASSWORD :"+tmp);
		      //System.out.println("PASSWORD :"+paswd);
		      int flag = num.compareTo(tmp);
		      if(flag == 0)
		    	  return Const.RRet.RRET_SUCCESS;
		      else
		    	  return Const.RRet.RRET_CHECK_ACTIVATE_FAIL;
	    } 
	    catch(SQLException e) { 
	      System.out.println("Check_ActCode Exception :" + e.toString()); 
	      return Const.RRet.RRET_CHECK_ACTIVATE_FAIL;
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
	  
	  public void dropTable() 
	  { 
	    try 
	    { 
	      stat = con.createStatement(); 
	      stat.executeUpdate(dropdbSQL); 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("DropDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
 
	  public void SelectTable() 
	  { 
	    try 
	    { 
	      stat = con.createStatement(); 
	      rs = stat.executeQuery(selectSQL); 
	      System.out.println("ID\t\tName\t\tPASSWORD"); 
	      while(rs.next()) 
	      { 
	        System.out.println(rs.getInt("id")+"\t\t"+ 
	            rs.getString("name")+"\t\t"+rs.getString("passwd")); 
	      } 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("DropDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
 
	  public void Select_Gift() 
	  { 
	    try 
	    { 
	      stat = con.createStatement(); 
	      rs = stat.executeQuery(selectSQL_gift); 
	      System.out.println("ticket_type\t\tticket_number\t\tticket_expire_date"); 
	      while(rs.next()) 
	      { 
	        System.out.println(rs.getString("ticket_type")+"\t\t"+ 
	            rs.getString("ticket_number")+"\t\t"+rs.getString("ticket_expire_date")); 
	      } 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("DropDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
	  
	  private void Close() 
	  { 
	    try 
	    { 
	      if(rs!=null) 
	      { 
	        rs.close(); 
	        rs = null; 
	      } 
	      if(stat!=null) 
	      { 
	        stat.close(); 
	        stat = null; 
	      } 
	      if(pst!=null) 
	      { 
	        pst.close(); 
	        pst = null; 
	      } 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("Close Exception :" + e.toString()); 
	    } 
	  } 
	  
	 
	  public void sorrr(String test){
		  //String arr[]={"Hello", "This", "Is" , "Example"};
		  int num = 4;
		  String[] aa = new String[num+1];
		  aa[1]="Hello";
		  aa[3]="This";
		  aa[2]="Is";
		  aa[0]="Example";
		  //System.out.println(aa[0]);
		  //aa[] = {"Hello", "This", "Is" , "Example"};
		  for (int i = 0; i < num + 1; i++){
				if(num > 0){
					//for( int j = i + 1; j < num ; j++){
					if(test.compareTo(aa[i]) < 0){
						for (int j = num ; j > i ; j--){
							aa[j] =  aa[j-1];
						}
						//String temp = Const.name_list[i];
						aa[i] = test;
						break;
					}
					//}		
				}
		  }
		  for(int a = 0;a<5;a++)
			  System.out.println(aa[a]);
	  }
	/*static private int getListNum(String list){
		    int lastIndex = 0;
		    int count = 0;
		    String findStr ="/";
		    
		    while ((lastIndex = list.indexOf(findStr, lastIndex)) != -1) {
		        count++;
		        lastIndex += findStr.length() - 1;
		    }
		    return count;
		}
	  */
	
		
	  public static void main(String[] args) 
	  { 
		  //System.out.println(formatphone("0988 591 977","+886"));
		//String abc = Const.RRet.RRET_SUCCESS+"%"+"5/6/8/9/";
		//String xxx = "1/";
		//int i =abc.indexOf('%');
		//Const.RRet x = null;
		  /*
		JDBCMysql test = new JDBCMysql();
		String msg = "銝�";
		try {
			String senddata = new String(msg.getBytes("ISO-8859-1"), "UTF-8");
			test.Update_UsrInfo(1, "user", Const.eUsrType.UsrType_NickName,msg);
			System.out.println(senddata);
			System.out.println(msg);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String ret = test.Get_UsrInfo(Const.eUsrType.UsrType_NickName, 2);
		String para;
		try {
			para = new String(ret.getBytes("ISO-8859-1"), "UTF-8");
			System.out.println(para+"/"+ret);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		
		try {
			para = new String(ret.getBytes("ISO-8859-1"), "UTF-8");
			System.out.println(para+"/"+ret);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		*/
		  

		//test.SelectTable();
		//abc.getChars(abc.indexOf('%')+1, abc.length(), tmp, 0);
		//System.out.println(abc.indexOf('%'));
		//String xx = String.valueOf(tmp).trim();		
		//System.out.println(xx);//(RandomString.generateRandomString(10));
		//System.out.println(test.Get_Friend("ycw517@hotmail.com"));
		
		//System.out.println("FFF"+getListNum(xxx));
		//int count = xxx.length() - xxx.replace("/", "").length();
		//System.out.println(count);
		//System.out.println(test.do_search("ycw517@hotmail.com"));
		//test.Select_Gift();
		//x = abc.
		/*char[] str1=new char[15];
		abc.getChars(0, i, str1, 0);
		//String characterToString = Character.toString(str1);
		//String fromCharArray = new String(new char[]{str1});
		String xxx=String.valueOf(str1);
		System.out.println(str1);
		//x = x.valueOf(String.valueOf(str1));
		System.out.println(xxx);
		System.out.println(Const.RRet.valueOf(xxx.trim()));
		if(Const.RRet.valueOf(String.valueOf(str1).trim())==Const.RRet.RRET_SUCCESS)
			System.out.println("XXXXXXX\n");
	    //嚙踝蕭搰搰O嚙稻嚙踝蕭嚙窯 
		JDBCMysql test = new JDBCMysql(); 
	    test.dropTable(); 
	    //test.createTable(); 
	    //test.insertTable("yku", "12356");
		//test.Update_Phone("ycw517@gmail.com", "0912721590");
		//;
		//System.out.println(test.Get_UserId("yuki"));
		//test.Add_Friend("ycw517", "ppp");
		String xx=test.Get_Friend("ycw517");
		System.out.println(xx);
		String flist="";
		while(xx.contains("/")){
			int tmp = xx.indexOf("/");
			String str=xx.substring(0, tmp);
			flist=flist.concat(test.Get_UserName(Integer.valueOf(str))+"/");
			xx=xx.substring(tmp+1);
		}
		System.out.println(flist);
		
	    test.SelectTable(); 
	    //boolean tt = test.checkExist("yku2");
	    //if(tt==true)
	    	//System.out.println("CheckPasswd TRue " ); 
	    //else
	    	//System.out.println("CheckPasswd False " ); 
	    //test.checkExist("yku");
	  */
	  } 
}
