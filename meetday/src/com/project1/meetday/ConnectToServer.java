package com.project1.meetday;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.project1.http.Const;
import com.project1.http.FileAccess;
import com.project1.http.ServerFuncRun;

	public class ConnectToServer {
	private Context context;
	private String TAG = "ConnectToServer";
	//rivate Const.eNotifyType eType;
		
	public ConnectToServer(Context context){
		this.context = context;
	}
	
	public void DoLogin(Const.eUsrType datatype, String data, String pass){
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_Login_Type;
		sfr.datatype = datatype;
		sfr.data = data;
		sfr.pass = pass;
		ConnectInBackground(sfr);
	}
	
	public void DoLogout(String name, String pass){
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_Logout;
		sfr.name = name;
		//sfr.pass = pass;
		ConnectInBackground(sfr);
	}
	
	public void DoRegister(String name, String pass, String ... args){
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_Register;
		sfr.name = name;
		sfr.pass = pass;
		
		 if(args.length!=0){
			 sfr.datatype = Const.eUsrType.valueOf(args[0]);
			 sfr.data=args[1];
			 //ret = test.Update_UsrInfo(UsrId, "user", Const.eUsrType.UsrType_FBId, args[1]);
		 } 
		//Log.d(this.getClass().getName(),pass);
		//ConnectInBackground(sfr);
		//sfr.DoServerFunc();
		GetDataInBackground(sfr);
	}
	
	public void DoRegister_Type(String ... args){
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_Register_Type;
		//sfr.name = name;
		sfr.pass = args[2];
		
		 //if(args.length!=0){
		 sfr.datatype = Const.eUsrType.valueOf(args[0]);
		 sfr.data=args[1];
			 //ret = test.Update_UsrInfo(UsrId, "user", Const.eUsrType.UsrType_FBId, args[1]);
		 //} 
		//Log.d(this.getClass().getName(),pass);
		//ConnectInBackground(sfr);
		//sfr.DoServerFunc();
		GetDataInBackground(sfr);
	}
	
	public void DoGetUsrInfofromServ(String uid){		
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_GetLoginData;
		sfr.usr_id = uid;
        GetDataInBackground(sfr);
        DoGetPhoto(Const.projinfo.sServerAdr+"upload/user_"+uid+".jpg");
	}
	
	public void DoGetUsrData(String uid, Const.eUsrType type){		
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_GetUsrData;
		sfr.usr_id = uid;
		sfr.datatype = type;
        GetDataInBackground(sfr);
	}
	
	public void DoSendSMS(String dstaddr, String smbody){		
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_SendSMS_Auth;
		//sfr.usr_id = uid;
		//sfr.datatype = type;
        sfr.dstaddr = dstaddr;
        sfr.smbody = smbody;
		GetDataInBackground(sfr);
	}
	
	public void DoAddFriendList(String uid, String fblst, Const.eUsrType type){		
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_AddFriendList;
		sfr.usr_id = uid;
		sfr.data = fblst;
		sfr.datatype = type;
        GetDataInBackground(sfr);
        //DoGetPhoto(Const.projinfo.sServerAdr+"upload/user_"+uid+".jpg");
	}
	
	public void DoGetPhoto(String url){
    	ServerFuncRun sfr1 = new ServerFuncRun();	 	
    	sfr1.cmd=Const.Command.Cmd_GetUsrPhoto;
    	sfr1.url = url;//Const.projinfo.sServerAdr+"upload/user_"+uid+".jpg";
		//sfr1.usr_id = uid;
		ConnectInBackground(sfr1);
	}
	
	public void DoUpdateData(String uid, Const.eUsrType datatype, String data){
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_UpdateUsrData;
		sfr.usr_id = uid;
		sfr.datatype = datatype;
		sfr.data = data;
		ConnectInBackground(sfr);
		//sfr.DoServerFunc();
	}
	
	public void DoUpdatePhoto(String uid, String image){
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_UpdatePhoto;
		sfr.usr_id = uid;
		//sfr.datatype = datatype;
		sfr.imageStr = image;
		ConnectInBackground(sfr);
		//sfr.DoServerFunc();
	}
	
	public void ConnectInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
	        protected void onPreExecute() {
	        }
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";				
				try {				
					if(Looper.myLooper() == null)
						Looper.prepare();
					
					sfr.DoServerFunc();
				
					while(sfr.GetServResult() == null)
						;
					if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){
						if(sfr.cmd == Const.Command.Cmd_Login_Type){
							Const.userinfo.set_login_status(true);
						}
						//Log.d(this.getClass().getName(), String.valueOf(Const.userinfo.State_Type));
					}else{
						Toast.makeText(context, "Connect to Server Fail", Toast.LENGTH_SHORT).show();		        	
					}
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					Log.d(TAG, "Error: " + msg);
				}
				Log.d(TAG, "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//pdialog.dismiss();
			}
		}.execute(null, null, null);
	}
	
	public void GetDataInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
	        protected void onPreExecute() {
	        	//pdialog = ProgressDialog.show(WelcomPage.this, "Connecting", "Wait...",true);				
	            //this.pdialog.setMessage("Progress start");
	            //this.pdialog.show();
	        }
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";				
				try {				
					if(Looper.myLooper() == null)
						Looper.prepare();
					
					sfr.DoServerFunc();
				
					while(sfr.GetServResult() == null)
						;
					if(sfr.cmd == Const.Command.Cmd_Register){
						Const.lastcmdret = sfr.cmd +"%" +sfr.GetServResult()+"%"+sfr.GetUserData()+"%";
					}
					if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){
						if(sfr.cmd == Const.Command.Cmd_GetLoginData){
							String[] data = FileAccess.parseDataToPara(sfr.GetUserData(), "/");
							FileAccess.putStringInPreferences(context, data[0], 
					    			Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
					    	FileAccess.putStringInPreferences(context, data[2], 
					    			Const.eUsrType.UsrType_SearchId, Const.projinfo.sSharePreferenceName);
					    	FileAccess.putStringInPreferences(context, data[1], 
					    			Const.eUsrType.UsrType_Fone, Const.projinfo.sSharePreferenceName);
					    	if(!data[3].toLowerCase().equals("null")){
					    		if(FileAccess.getStringFromPreferences(context, "null",Const.eUsrType.UsrType_FBId,
					    				Const.projinfo.sSharePreferenceName).toLowerCase().equals("null")){
						    		FileAccess.putStringInPreferences(context, data[3], 
						    				Const.eUsrType.UsrType_FBId, Const.projinfo.sSharePreferenceName);
					    		}
					    		if(FileAccess.getStringFromPreferences(context, "null",Const.eUsrType.UsrType_FB_Status,
					    				Const.projinfo.sSharePreferenceName).toLowerCase().equals("null")){
						    		FileAccess.putStringInPreferences(context, Const.eSocialCmd.Social_Unconnect, 
						    				Const.eUsrType.UsrType_FB_Status, Const.projinfo.sSharePreferenceName);
					    		}
					    	}
					    	if(!data[4].toLowerCase().equals("null")){
					    		if(FileAccess.getStringFromPreferences(context, "null",Const.eUsrType.UsrType_Gmail,
					    				Const.projinfo.sSharePreferenceName).toLowerCase().equals("null")){
						    		FileAccess.putStringInPreferences(context, data[4], 
						    				Const.eUsrType.UsrType_Gmail, Const.projinfo.sSharePreferenceName);
					    		}
					    		if(FileAccess.getStringFromPreferences(context, "null",Const.eUsrType.UsrType_Gmail_Status,
					    				Const.projinfo.sSharePreferenceName).toLowerCase().equals("null")){
						    		FileAccess.putStringInPreferences(context, Const.eSocialCmd.Social_Unconnect, 
						    				Const.eUsrType.UsrType_Gmail_Status, Const.projinfo.sSharePreferenceName);
					    		}
					    	}
						} else if (sfr.cmd == Const.Command.Cmd_Register_Type){
							//Const.lastcmdret = 
							//String uid = sfr.GetUserData();							
							//FileAccess.putStringInPreferences(context, uid, 
					    		//	Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
						} else if (sfr.cmd == Const.Command.Cmd_GetFBFList){
							String fbfid = sfr.GetUserData();
						} else if (sfr.cmd == Const.Command.Cmd_AddFriendList){
							String flist = sfr.GetUserData();
						} else if (sfr.cmd == Const.Command.Cmd_GetUsrData){
							//send broadcast to pass data
							String fname = sfr.GetUserData();
							Log.d(TAG, fname);
							Intent broadcastIntent  = new Intent();
							broadcastIntent.setAction(AddFriendNewPage.BROADCAST_GETFRIENDNICKNAME);
							broadcastIntent.putExtra(AddFriendNewPage.BROADCAST_FRIENDSNICKNAME, fname);
							context.sendBroadcast(broadcastIntent);
						} 
						
					} else if (Const.RRet.RRET_USER_EXIST_FAIL == sfr.GetServResult()){
						Toast.makeText(context, "User Already Exist", Toast.LENGTH_SHORT).show();		        	
					} else {
						Toast.makeText(context, "Connect to Server Fail", Toast.LENGTH_SHORT).show();	
					}
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					Log.d(TAG, "Error: " + msg);
				}
				Log.d(TAG, "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//pdialog.dismiss();
			}
		}.execute(null, null, null);
	}
}
