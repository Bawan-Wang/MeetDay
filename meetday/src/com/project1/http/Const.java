package com.project1.http;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.project1.meetday.LoaderAdapter;
import com.project1.meetday.R;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Const {
	public static Proj_Info projinfo = new Proj_Info(); 
	public static Usr_Info userinfo =  new Usr_Info();
	public static Contact_Info contactinfo = new Contact_Info();
	public static History_Info hisinfo = new History_Info();
	public static String giftcarddb = "giftcard";
	public static GoogleApiClient mGoogleApiClient;
	public static ConnectionResult mConnectionResult;
	public static String univeralpass = "UnIvErPaSs";
	public static String lastcmdret = "";
	public static String localFileTmpPath =  PictUtil.getSavePath().getAbsolutePath()+"/localcapfile";
	public static String remoteFileTmpPath =  PictUtil.getSavePath().getAbsolutePath()+"/remotetmpfile";
	public static String localFileTmpPath_Freeze =  PictUtil.getSavePath().getAbsolutePath()+"/localcapfilefz";
	public static String remoteFileTmpPath_Freeze =  PictUtil.getSavePath().getAbsolutePath()+"/remotetmpfilefz";
	public static String localProfilePicPath = PictUtil.getSavePath().getAbsolutePath()+"/profile";
	public static String mScreenshotPath = Environment.getExternalStorageDirectory() + "/Pictures";
	//public static String download_path = "https://play.google.com/apps/testing/com.project1.meetday";
	public static String download_path = "https://fb.me/1792851484310529";
	public static String logo_path = "http://52.69.106.105:8080/Test4Demo/upload/unspecified.jpg";
	public static String officialid = "4";
	public static String officialname = "Meetday";
	public static int refresh_friends = 0;
	public static Boolean debug_mode = true;
	//public static Connect_Info coninfo = new Connect_Info();
	//public static History_Item hisitem[];// = new History_Item();
	//public static CountDownTimer aCounter = null;	
	public static String facebook_id = "1557090727886607";
	public static int[] ThxImg = {
		R.drawable.thanksletter_hands, R.drawable.thanksletter_bro, R.drawable.thanksletter_hug, R.drawable.thanksletter_office
	};	
	public static String[] HelperCato = {
		"台北市 松山區", "台北市 信義區", "台北市 大安區", "台北市 中山區", 
		"台北市 中正區", "台北市 大同區", "台北市 內湖區", "台北市 士林區", "台北市 北投區", 
		"台北市 萬華區", "台北市 文山區", "台北市 南港區", "新北市 中和永和新店", 
		"新北市 板橋新莊土城", "新北市 三重蘆洲", "新北市其他", "大桃園", "大台中", "大台南","大高雄","其他"
	};	
	
	
	public static class Proj_Info {
		public  final String sServerIP ="BAWANCHENTIM517JJCASERVER.INFO";//"52.69.106.105";// "128.199.208.170";//"52.69.106.105";//"128.199.208.170";//
		public  final String sServerPort = "8080";
		public  final String sServerName = "Test4Demo/";
		public  final String sServerAdr = "http://"+sServerIP+":"+sServerPort+"/"+sServerName;
		public  final String sServerApi = sServerAdr+"GCMNotification?shareRegId=1";
		public  final String sServerDBFunc = "DBRelated";
		public 	final String sLocalFile = "/MeetDay";
		public 	final String sSharePreferenceName = "setting";
		public  final String sUpdateUrl = sServerAdr + "UploadServlet";
		public  final String GOOGLE_PROJECT_ID = "259348984193";
		public  final String MESSAGE_KEY = "message";
		public  final String sExpertTable = "expert";
		public  final String sExpertTableFile = "expert.sql";
		public  final String sSMSIP = "202.39.48.216";
		public  final String sSMSUsername = "sylapp7";
		public  final String sSMSPass = "sylappisgood";
		public  final String sSMSrAdr = "http://"+sSMSIP+"/kotsmsapi-1.php";
				//"http://202.39.48.216/kotsmsapi-1.php?username=帳號&password=密碼&dstaddr=0933853653&
//smbody=簡訊王api簡訊測試 &response=http://回報網址/response.php
	}
	
	public static class Log_Info {
		public String data;
		public String pass;
		public Const.eUsrType datatype;
		public Log_Info(){
			
		}
	}
	
	public static Log_Info get_login_data(Context context){
		Log_Info info = new Log_Info();
		String Default = "null";
		if(!FileAccess.getStringFromPreferences(context, Default, 
				Const.eUsrType.UsrType_FBId, Const.projinfo.sSharePreferenceName).toLowerCase().equals(Default)){
			if(FileAccess.getStringFromPreferences(context, Default, 
					Const.eUsrType.UsrType_FB_Status, Const.projinfo.sSharePreferenceName).equals(Const.eSocialCmd.Social_Connect)){
				info.data = FileAccess.getStringFromPreferences(context, Default, 
						Const.eUsrType.UsrType_FBId, Const.projinfo.sSharePreferenceName);
				info.datatype = Const.eUsrType.UsrType_FBId;
				info.pass = Const.univeralpass;
				return info;
			}
		} 		
		if(!FileAccess.getStringFromPreferences(context, Default, 
				Const.eUsrType.UsrType_Gmail, Const.projinfo.sSharePreferenceName).toLowerCase().equals(Default)){
			if(FileAccess.getStringFromPreferences(context, Default, 
					Const.eUsrType.UsrType_Gmail_Status, Const.projinfo.sSharePreferenceName).equals(Const.eSocialCmd.Social_Connect)){
				info.data = FileAccess.getStringFromPreferences(context, Default, 
						Const.eUsrType.UsrType_Gmail, Const.projinfo.sSharePreferenceName);
				info.datatype = Const.eUsrType.UsrType_Gmail;
				info.pass = Const.univeralpass;
				return info;
			}
		} 
		if(!FileAccess.getStringFromPreferences(context, Default, 
				Const.eUsrType.UsrType_Fone, Const.projinfo.sSharePreferenceName).equals(Default)){
			info.data = FileAccess.getStringFromPreferences(context, Default, 
					Const.eUsrType.UsrType_Fone, Const.projinfo.sSharePreferenceName);
			info.datatype = Const.eUsrType.UsrType_Fone;
			info.pass = FileAccess.getStringFromPreferences(context, Default, 
					Const.eUsrType.UsrType_Pass, Const.projinfo.sSharePreferenceName);
		}
		return info;
	}
	
	public static class Usr_Info {
		//private final 
		private ReadWriteLock rwlock = new ReentrantReadWriteLock();
		private boolean blogin_status = false;	
		private boolean bFrontRunnig = false;
		private eState State_Type = eState.State_Idle;		
				
		public void set_login_status(boolean status){
			rwlock.writeLock().lock();
			try{
				this.blogin_status = status;
			} finally {
				rwlock.writeLock().unlock();
			}
		}	
		public boolean get_login_status(){
			boolean ret = false;
			rwlock.writeLock().lock();
			try{
				ret = blogin_status;
			} finally {
				rwlock.writeLock().unlock();
			}
			return ret;
		}	
		
		public void set_front_running(boolean status){
			rwlock.writeLock().lock();
			try{
				this.bFrontRunnig = status;
			} finally {
				rwlock.writeLock().unlock();
			}
		}	
		
		public boolean get_front_running(){
			boolean ret = false;
			rwlock.writeLock().lock();
			try{
				ret = bFrontRunnig;
			} finally {
				rwlock.writeLock().unlock();
			}
			return ret;
		}	
		
		public void set_current_state(eState State_Type){
			rwlock.writeLock().lock();
			try{
				this.State_Type = State_Type;
			} finally {
				rwlock.writeLock().unlock();
			}
		}	
		
		public eState get_current_state(){
			eState ret = null;
			rwlock.writeLock().lock();
			try{
				ret = State_Type;
			} finally {
				rwlock.writeLock().unlock();
			}
			return ret;
		}	
		
		public Usr_Info(){
			
		}

		public Usr_Info(String name, String uid, String flst){
			
		}
	}
	
	public static class Connect_Info implements Parcelable {
		public static class screen_size{
			public int x;
			public int y;
		}
		public screen_size local_size = new screen_size();
		public screen_size client_size = new screen_size();		
		public String uid;
		public String rid;
		public String roomid;
		public String remotename;
		public Connect_Info(int lx, int ly, int rx, int ry){
			local_size.x = lx;
			local_size.y = ly;
			client_size.x = rx;
			client_size.y = ry;
		}
		public Connect_Info(screen_size local_size, screen_size client_size){
			this.local_size = local_size;
			this.client_size = client_size;
		}
		public Connect_Info(Parcel in) {
		    this.local_size.x = in.readInt();
		    this.local_size.y = in.readInt();
		    this.client_size.x = in.readInt();
		    this.client_size.y = in.readInt();
		    this.uid = in.readString();
		    this.rid = in.readString();
		    this.roomid = in.readString();
			this.remotename = in.readString();
		}
		public Connect_Info(){
			
		}
		
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
	    public void writeToParcel(Parcel out, int flags) {
		    out.writeInt(this.local_size.x);
		    out.writeInt(this.local_size.y);
		    out.writeInt(this.client_size.x);
		    out.writeInt(this.client_size.y);
			out.writeString(this.uid);
		    out.writeString(this.rid);
		    out.writeString(this.roomid);
			out.writeString(this.remotename);
	    }
		
		@SuppressWarnings("rawtypes")
		public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		    public Connect_Info createFromParcel(Parcel in) {
		        return new Connect_Info(in);
		    }

		    public Connect_Info[] newArray(int size) {
		        return new Connect_Info[size];
		    }
		};
	}
	
	public static class Contact_Info{
		public  LoaderAdapter ladapter = null;
		private final String tablename = "contact";
		private boolean bDataReady = false;
		private ReadWriteLock rwlock = new ReentrantReadWriteLock();
		
		public boolean getDataStatus(){
			boolean ret = false;
			rwlock.writeLock().lock();
			try{
				ret = bDataReady;
			} finally {
				rwlock.writeLock().unlock();
			}
			return ret;			
		}
		public void setDataStatus(boolean bDataReady){
			rwlock.writeLock().lock();
			try{
				this.bDataReady = bDataReady;
			} finally {
				rwlock.writeLock().unlock();
			}		
		}
		public String getTableName(){
			return tablename;
		}
	}
	
	public static class History_Info extends Contact_Info{
		private   final String tablename = "history";
		
		@Override
		public String getTableName(){
			return tablename;
		}
	}
	
	
	public static boolean isNetworkConnected(Context c){
	    ConnectivityManager connectivityManager 
	            = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();    
	}
	
	public static class SQL_data{
		public String name;
		public String fid;
		public String time;
		public String type;
		public String callid;
		public String duration;
		public String msg;
		public String pattern;
		
		public SQL_data(String name, String fid){
			this.name = name;
			this.fid = fid;
			this.time = null;
			this.type = null;
			this.duration = null;
		}
		
		public SQL_data(String name, String fid, String time, String type){
			this.name = name;
			this.fid = fid;
			this.time = time;
			this.type = type;
			this.duration = null;
		}
		
		public SQL_data(String name, String fid, String time, String type, String callid, String msg){
			this.name = name;
			this.fid = fid;
			this.time = time;
			this.type = type;
			this.callid = callid;
			this.msg = msg;
		}
		
		public SQL_data(String name, String fid, String time, String type, String msg){
			this.name = name;
			this.fid = fid;
			this.time = time;
			this.type = type;
			//this.callid = callid;
			this.msg = msg;
		}
		
		public SQL_data(String name, String fid, String time, String type, String callid, String msg
				, String pattern){
			this.name = name;
			this.fid = fid;
			this.time = time;
			this.type = type;
			this.callid = callid;
			this.msg = msg;
			this.pattern = pattern;
		}
		
		public SQL_data(String callid, String msg, String pattern){
			//this.name = name;
			//this.fid = fid;
			this.pattern = pattern;
			this.msg = msg;
			this.callid = callid;
		}
		
	}
	
	public static String getlocalcode(Context context){
		return context.getResources().getConfiguration().locale.getCountry().toLowerCase();
	}
	
	public static Connect_Info.screen_size getLocalRes(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		Connect_Info.screen_size ret = new Const.Connect_Info.screen_size();
		ret.x = size.x;
		ret.y = size.y;	
		return ret;
	}
	
	public static String getAuthNum(){
		String ret="";
		Random rand = new Random();
		int  n = rand.nextInt(9999) + 1;
		if(n<1000)
			n+=1000;
		ret = String.valueOf(n);
		return ret;
	}
	
	public enum History_Type{
		HType_Dial,
		HType_Incoming,
		HType_Missed,
		HType_RecieveThanks,
		Htype_SentThanks,
		Htype_SendMessage,
		Htype_GetMessage
	}
	
	public enum Command {
		Cmd_Login, 
		Cmd_Logout, 
		Cmd_Register, 
		Cmd_ForgetPwd, 
		Cmd_AddFriend, 
		Cmd_AddFriendList, 
		Cmd_ChangePwd, 
		Cmd_GetUsrData,
		Cmd_DoSearch,
		Cmd_UpdateUsrData,
		Cmd_UpdatePhoto,
		Cmd_GetUsrPhoto,
		Cmd_GetExpertTable,
		Cmd_SendPushToOther,
		Cmd_GetUsrDataList,
		Cmd_GetLoginData,
		Cmd_GetExpertList,
		Cmd_AddExpert,
		Cmd_GetFBFList,
		Cmd_Login_Type,
		Cmd_Register_Type,
		Cmd_SendSMS_Auth
	}
	
	public enum RRet{
		RRET_SUCCESS,
		RRET_FAIL,
		RRET_ADDFRIEND_FAIL,
		RRET_ADDUSER_FAIL,
		RRET_CHANGEPWD_FAIL,
		RRET_CHECK_ACTIVATE_FAIL,
		RRET_DOSEARCH_FAIL,
		RRET_FRIEND_EXIST_FAIL,
		RRET_GETUSRDATA_FAIL,
		RRET_FORGETPWD_FAIL,
		RRET_LOGIN_FAIL,
		RRET_LOGOUT_FAIL,
		RRET_NOT_ACTIVATE_FAIL,
		RRET_PHONE_EXIST_FAIL,
		RRET_REG_FAIL,	
		RRET_SEARCHID_EXIST_FAIL,
		RRET_UPDATE_INFO_FAIL,
		RRET_USER_EXIST_FAIL,
		RRET_USER_FORMAT_FAIL,
		RRET_USER_NOT_EXIST_FAIL,
		RRET_WRONG_PASSWORD_FAIL
	}
	
	public enum eUsrType{
		UsrType_Email,
		UsrType_Fone,
		UsrType_SearchId,
		UsrType_NickName,
		UsrType_IP,
		UsrType_Pass,
		UsrType_LoginStatus,
		UsrType_ActStatus,
		UsrType_FreindList,
		UsrType_UserId,
		UsrType_UserImage,
		UsrType_RegId,
		UsrType_FriendName,
		UsrType_HelpMsg,
		UsrType_ThanksMsg,
		UsrType_FriendNameList,
		UsrType_FragmentLocation,
		UsrType_ExpertName,
		UsrType_ExpertTel,
		UsrType_ExpertLocId,
		UsrType_ExpertAddr,
		UsrType_ExpertUId,
		UsrType_ExpertEId,
		UsrType_LoginType,
		UsrType_Gmail,
		UsrType_Gmail_Status,
		UsrType_FBId,
		UsrType_FBList,
		UsrType_FB_Status,
		UsrType_PhoneList,
		UsrType_All
	}
	
	public enum eNotifyType {
		NotifyType_AskConnect, 
		NotifyType_AckConnectOn,
		NotifyType_RespondConnect,
		NotifyType_DisConnect,
		NotifyType_AddFriend,
		NotifyType_MissedCall,
		NotifyType_Ringing_Caller,
		NotifyType_Ringing_Callee,
		NotifyType_RequestConnect,
		NotifyType_AckRequest,
		NotifyType_ConnectedOn,
		NotifyType_ConnectedOff,
		NotifyType_Thanks,
		NotifyType_Logout,
		NotifyType_SendMsg
	}
	
	public enum eState {
		State_Idle,
		State_AskCallForHelp, 
		State_RespondConnect,
		State_Connecting,
		State_RequestConnect,
		State_WaitAck,
		State_WaitAsker,
		State_Connected
	}
	
	public enum eLocation {
		Location_Empty,
		Location_Contact, 
		Location_Record,
		Location_Setting,
		Location_Helper
	}
	
	public enum eFragment {
        Fragment_WB,
        Fragment_VB
    }
	
	/*public enum eCallState {
        CState_Idle,
        CState_PenDown,
        Cstate_WaitAck,
        CState_Disconnect,
        CState_Pause
    }*/
	
	public static class eSocialCmd {
        public static  String Social_FB = "FB";
        public static  String Social_Gmail = "Gmail";
        public static  String Social_Login = "Login";
        public static  String Social_Logout = "Logout";
        public static  String Social_Invite = "InVite";
        public static  String Social_Unbind = "Unbind";
        public static  String Social_StartPage = "Start";
        public static  String Social_Personal = "Person";
        public static  String Social_AddFriendPage = "AddFriend";
        public static  String Social_Main = "Main";
        public static  String Social_Phone = "Phone";
        public static  String Social_Connect = "Connect";
        public static  String Social_Unconnect = "Unconnect";
    }
	
	
}
