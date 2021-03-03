package com.project1.meetday;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.ServerFuncRun;

public class ConnectToOther {//extends Fragment{
	private Context context;
	//rivate Const.eNotifyType eType;
	
	
	public ConnectToOther(Context context){
		this.context = context;
	}
	
/*public void SendRequest(String uid, String recid) {
		
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.usr_id = uid;
		if(sfr.usr_id == null){
			return ;
		}
		sfr.rec_id = recid;;

		sfr.cmd = Const.Command.Cmd_SendPushToOther;	
		//String usrId = Const.userinfo.uid;
		//Generate room id
		Log.d(this.getClass().getName(), String.valueOf(recid));
		//Toast.makeText(context, "You Ask "+strName + " for Help!", Toast.LENGTH_SHORT).show();
		sfr.message = Const.eNotifyType.NotifyType_RequestConnect+
				"%"+uid+
				"%"+recid+
				"%";
		
		//eType = Const.eNotifyType.NotifyType_RequestConnect;
		Const.userinfo.State_Type = Const.eState.State_WaitAck;
		
		CallInBackground(sfr);
						
	}*/
	
	public void SendConnectError(String uid, String recid) {
		
		//String lst = Const.userinfo.flst;
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.usr_id = uid;
		if(sfr.usr_id == null){
			return ;
		}
		sfr.rec_id = recid;//Const.contactinfo.fid_list[pos];//get_fid(lst, pos+1);
	
		sfr.cmd = Const.Command.Cmd_SendPushToOther;	
		String usrId = uid;
		
		Log.d(this.getClass().getName(), String.valueOf(recid));
		//Toast.makeText(context, "Wait "+recid + " for SendConect!", Toast.LENGTH_SHORT).show();
		sfr.message = Const.eNotifyType.NotifyType_ConnectedOff+
				"%"+usrId+
				"%"+recid+
				"%";
		
		CallInBackground(sfr);
						
	}
	
	public void SendThanks(String uid, String recid, String msg, String roomid, String Idx) {
		
		//String lst = Const.userinfo.flst;
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.usr_id = uid;
		if(sfr.usr_id == null){
			return ;
		}
		sfr.rec_id = recid;//Const.contactinfo.fid_list[pos];//get_fid(lst, pos+1);
	
		sfr.cmd = Const.Command.Cmd_SendPushToOther;	
		String usrId = uid;
		
		Log.d(this.getClass().getName(), String.valueOf(recid));
		//Toast.makeText(context, "Wait "+recid + " for SendConect!", Toast.LENGTH_SHORT).show();
		sfr.message = Const.eNotifyType.NotifyType_Thanks+
				"%"+usrId+
				"%"+recid+
				"%"+msg+
				"%"+roomid+
				"%"+Idx+
				"%";
		
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		Date date = new Date();
		long timeMilli = date.getTime();
		
		String strName = //dbhelper.getNamefromSql(dbrw, Const.hisinfo.getTableName(), recid);
				dbhelper.getdatafromSql(dbrw, Const.hisinfo.getTableName(), "name"," fid", recid);
		Const.SQL_data data = new Const.SQL_data(strName, recid, 
				Long.toString(timeMilli), String.valueOf(Const.History_Type.Htype_SentThanks),
				roomid , msg, Idx);
		dbhelper.insertData(dbrw, Const.hisinfo.getTableName(), data);
		//String callid, String msg, String pattern
		//Const.SQL_data data_card = new Const.SQL_data(roomid, msg, "0");
		//dbhelper.insertData(dbrw, Const.giftcarddb, data_card);
		
		CallInBackground(sfr);
						
	}
	
	/*
	 * para[0]:Sender id
	 * para[1]:Your id
	 * para[2]:Roomid (connect id to apprtc)
	 * para[3]:Nickname
	 * para[4]:TimeinMili
	 * para[5]:X
	 * para[6]:Y
	 * para[7]:Message 
	 */
	
	public void SendAck(String []para) {
		
		//String lst = Const.userinfo.flst;
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.usr_id = para[1];
		if(sfr.usr_id == null){
			return ;
		}
		sfr.rec_id = para[0];//Const.contactinfo.fid_list[pos];//get_fid(lst, pos+1);
	
		sfr.cmd = Const.Command.Cmd_SendPushToOther;	
		//String usrId = para[1];
		
		Log.d(this.getClass().getName(), String.valueOf(para[0]));
		//Toast.makeText(context, "Wait "+recid + " for SendConect!", Toast.LENGTH_SHORT).show();
		sfr.message = Const.eNotifyType.NotifyType_AckRequest+
				"%"+para[0]+
				"%"+para[1]+
				"%"+para[2]+
				"%"+para[3]+
				"%"+para[4]+
				"%"+para[5]+
				"%"+para[6]+
				"%"+para[7]+
				"%";	
		CallInBackground(sfr);
						
	}

	public void SendConnectOn(String []para) {
		
		//String lst = Const.userinfo.flst;
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.usr_id = para[0];
		if(sfr.usr_id == null){
			return ;
		}
		sfr.rec_id = para[1];//Const.contactinfo.fid_list[pos];//get_fid(lst, pos+1);
	
		sfr.cmd = Const.Command.Cmd_SendPushToOther;	
		//String usrId = uid;
		
		Log.d(this.getClass().getName(), String.valueOf(para[1]));
		//Toast.makeText(context, "Wait "+recid + " for SendConect!", Toast.LENGTH_SHORT).show();
		sfr.message = Const.eNotifyType.NotifyType_AckConnectOn+
				"%"+para[0]+
				"%"+para[1]+
				"%"+para[2]+
				"%"+para[3]+
				"%"+para[4]+
				"%"+para[5]+
				"%"+para[6]+
				"%"+para[7]+
				"%";	
		CallInBackground(sfr);
						
	}
	
	public Const.Connect_Info.screen_size getLocalRes(){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		//Const.coninfo.local_size =;
		Const.Connect_Info.screen_size ret = new Const.Connect_Info.screen_size();
		ret.x = size.x;
		ret.y = size.y;
		
		return ret;
	}
	
	public String genRoomId(String usrId, String recid){
		Date date = new Date();
		long timeMilli = date.getTime();
		return usrId + Long.toString(timeMilli) + recid;
	}
		
	public void SendConnect(String strName, String strQues, String usrId, String recid, 
			String roomId, String tablename, Const.Connect_Info.screen_size size, String helpername) {
		
		//String lst = Const.userinfo.flst;
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.usr_id = usrId;//Const.userinfo.uid;
		if(sfr.usr_id == null){
			return ;
		}
		sfr.rec_id = recid;//Const.contactinfo.fid_list[pos];//get_fid(lst, pos+1);

		sfr.cmd = Const.Command.Cmd_SendPushToOther;	

		//String helperName = "";	
		//Generate room id
		Date date = new Date();
		long timeMilli = date.getTime();
		//Const.coninfo.connectid = usrId + Long.toString(timeMilli) + recid;	
			
		//Toast.makeText(context, "You Ask "+strName + " for Help!", Toast.LENGTH_SHORT).show();
		sfr.message = Const.eNotifyType.NotifyType_AskConnect+			
				"%"+usrId+
				"%"+recid+
				"%"+roomId+
				"%"+helpername+				
				"%"+Long.toString(timeMilli)+
				"%"+size.x+
				"%"+size.y+
				"%"+strQues+
				"%";
		
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		//ContentValues cv = new ContentValues();
		//long id;		
		//cv.put("name", strName);
		//cv.put("time", Long.toString(timeMilli));
		//cv.put("fid", recid);
		//cv.put("type", String.valueOf(Const.History_Type.HType_Dial));
		//id = dbrw.insert(tablename, null, cv);
		Const.SQL_data data = new Const.SQL_data(strName, recid, 
				Long.toString(timeMilli), String.valueOf(Const.History_Type.HType_Dial),roomId, strQues);
		dbhelper.insertData(dbrw, Const.hisinfo.getTableName(), data);
		//if (id > 0) {
			//Log.d(this.getClass().getName(),"Success DB");
		//} else {
			//Log.d(this.getClass().getName(),"Fail DB");
		//}			
		//String helperName = dbhelper.getNamefromSql(dbrw, tablename, recid);		
		
		dbrw.close();	
		dbhelper.close();
		//eType = Const.eNotifyType.NotifyType_AskConnect;
		//Const.userinfo.State_Type = Const.eState.State_WaitAck;
		
		CallInBackground(sfr);
			
		//helperPhotoUrl = Const.projinfo.sServerAdr+"upload/user_"+Const.userinfo.conusrid+".jpg";	//Get helper photo to next activity							
		return ;
		
	}
	
	public void SendDisconnect(String roomId, MediaPlayer mp, String uid, String rid){
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.usr_id = uid;
		sfr.rec_id = rid;
		sfr.message = Const.eNotifyType.NotifyType_DisConnect+
				"%"+roomId+
				"%";
		sfr.cmd = Const.Command.Cmd_SendPushToOther;	
		
		mp.stop();
		if(mp!=null){
			mp.stop();
			mp = null;
		}
		//eType = Const.eNotifyType.NotifyType_DisConnect;
		
		CallInBackground(sfr);
	}
	
	public void SendMessage(String uid, String rid, String msg, String name){
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.usr_id = uid;
		sfr.rec_id = rid;
		sfr.message = Const.eNotifyType.NotifyType_SendMsg+
				"%"+name+
				"%"+msg+
				"%"+uid+
				"%";
		sfr.cmd = Const.Command.Cmd_SendPushToOther;

		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		Date date = new Date();
		long timeMilli = date.getTime();
		Const.SQL_data data = new Const.SQL_data(name, rid,
				Long.toString(timeMilli), String.valueOf(Const.History_Type.Htype_SendMessage),
				"0" , msg, "1");
		dbhelper.insertData(dbrw, Const.hisinfo.getTableName(), data);

		if(Const.hisinfo.ladapter!=null){
			//Log.i(TAG, "dfgdgf");
			Const.hisinfo.ladapter.onUpdate(context, dbhelper, Const.hisinfo.getTableName());
		}
		//eType = Const.eNotifyType.NotifyType_DisConnect;
		
		CallInBackground(sfr);

	}
	
	public void SendRespond(boolean connect, String roomId, String uid, String rid, String tablename, 
			Const.Connect_Info.screen_size size){
		ServerFuncRun sfr = new ServerFuncRun();

		sfr.usr_id = uid;
		sfr.rec_id = rid;
		if(connect == true){
			
			sfr.message = Const.eNotifyType.NotifyType_RespondConnect+
					"%"+roomId+
					"%"+"1"+
					"%"+size.x+
					"%"+size.y+
					"%";
		} else {
			sfr.message = Const.eNotifyType.NotifyType_RespondConnect+
					"%"+roomId+
					"%"+"0"+
					"%";
			
			DBHelper dbhelper = new DBHelper(context);
			SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("type", String.valueOf(Const.History_Type.HType_Missed));
			String[] colum = { "_id"};
			Cursor c = dbrw.query(tablename, colum, null, null, null, null, null);
			if (c.getCount() > 0) {
				c.moveToLast();
			} 
			dbhelper.updateData(dbrw, tablename, c.getString(0), cv);
			dbrw.close();
		}
		//eType = Const.eNotifyType.NotifyType_RespondConnect;
		sfr.cmd = Const.Command.Cmd_SendPushToOther;						
		CallInBackground(sfr);
	}
		
	public void DoLogin(){
		
	}
	
	public static void MessageDialog(final Context context, String title, String message) {
	      AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	      dialog.setTitle(title);
	      dialog.setMessage(message);
	      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
	           }
	      });
	      dialog.show();
	} //EOF warningDialog
	
	
	
	public void CallInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
			boolean error = false;
			//public CountDownTimer aCounter = null;
			//private ProgressDialog pdialog;
			//private ServerFuncRun sfr = new ServerFuncRun();
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
					//dialog.dismiss();
					if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){
						//Log.d(this.getClass().getName(), String.valueOf(Const.userinfo.State_Type));
					}else{
						Toast.makeText(context, "Ask for Help Fail", Toast.LENGTH_SHORT).show();		        	
					}
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("CallActivity", "Error: " + msg);
				}
				Log.d("CallActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//pdialog.dismiss();
				if(error)
					Toast.makeText(context, "No Response!!", Toast.LENGTH_SHORT).show();
			}
		}.execute(null, null, null);
	}
}
