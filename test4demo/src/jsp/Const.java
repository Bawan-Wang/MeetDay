package jsp;

public class Const {
	public static String sMySQLIP = "127.0.0.1";
	public static String sMySQLPort = "3306";
	public static String sMySQLPath = sMySQLIP + ":" + sMySQLPort;
	public static String sMySQLDatabase = "test";
	public static String sUser = "root";
	public static String sPassword = "vu u. xo6wk4";
	public static String sMySQLConnectionURL = "jdbc:mysql://" + sMySQLPath + "/" + sMySQLDatabase + "?useUnicode=true&characterEncoding=utf-8";
	public static String GOOGLE_SERVER_KEY = "AAAAPGJncYE:APA91bFhSc27rA9vpkCbwo5eQfxevr6Fgl6qj84oLKP7x72t0CHlugg5BkqjFjWj_Sg10E-ivbEi7udVMemNmlv19PVF0J4gpvuqijwBDWa0EOrOUpIXYdV-Kbc2itFX5WGSHOYw9Szb_UZJcSFa6dSFZirF4_mmjg";//"AIzaSyDYaXmDyeXTzxnQyCGcuNgBPRyUvJ_uU6c";
	public static String MESSAGE_KEY = "message";
	public static String univeralpass = "UnIvErPaSs";
	public static String FCM_SERVER = "https://fcm.googleapis.com/fcm/send";
	public static String GCM_SERVER = "https://gcm-http.googleapis.com/gcm/send";
	//+ "user=" + sUser + "&password=" + sPassword;  
	//"jdbc:mysql://127.0.0.1:3306/ncku_emba?" + "user=root&password=79993812"
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
		Cmd_SendPushToOther,
		Cmd_GetUsrDataList,
		Cmd_GetLoginData,
		Cmd_GetExpertList,
		Cmd_AddExpert,
		Cmd_GetFBFList,
		Cmd_Login_Type,
		Cmd_Register_Type
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
		UsrType_FBId,
		UsrType_FBList,
		UsrType_All
	}

	public enum eTableType{
		TableType_User,
		TableType_Friend,
		TableType_ActivateNum,
		TableType_Gift,
		TableType_Expert
	}
	
	public enum eNotifyType {
		NotifyType_AskConnect, 
		NotifyType_RespondConnect,
		NotifyType_DisConnect,
		NotifyType_AddFriend,
		NotifyType_MissedCall,
		NotifyType_Ringing,
		NotifyType_RequestConnect,
		NotifyType_AckRequest,
		NotifyType_ConnectedOn,
		NotifyType_ConnectedOff,
		NotifyType_Thanks,
		NotifyType_Logout
	}
}
