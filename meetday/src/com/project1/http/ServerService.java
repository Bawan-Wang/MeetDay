package com.project1.http;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ServerService extends Service {

	private String TAG = "ServerService";

	public static final String BROADCAST_USERID = "com.project1.serverservice.usrid";
	public static final String BROADCAST_RECID = "com.project1.serverservice.rec_id";
	public static final String BROADCAST_PROBLEM_MESSAGE = "com.project1.serverservice.message";
	public static final String BROADCAST_ASKER_CALL = "com.project1.serverservice.askerCall";
	public static final String BROADCAST_SERVERFUNC = "com.project1.serverservice.serverfunc";
	public static final String BROADCAST_SERVERNEW = "com.project1.serverservice.servernew";
	
	private IntentFilter mIntentFilter;
	
	String usrId = null;
	String recId = null;
	String message = null;
	ServerFuncRun servfunc = null;
	
    @Override  
    public void onCreate() {  
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
		mIntentFilter = new IntentFilter();
		//mIntentFilter.addAction(BROADCAST_USERID);
		//mIntentFilter.addAction(BROADCAST_RECID);
		//mIntentFilter.addAction(BROADCAST_PROBLEM_MESSAGE);
		//mIntentFilter.addAction(BROADCAST_ASKER_CALL);
		mIntentFilter.addAction(BROADCAST_SERVERNEW);
		mIntentFilter.addAction(BROADCAST_SERVERFUNC);
		registerReceiver(mReceiver, mIntentFilter);
    }  	
	
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.d(TAG, "onStartCommand() executed");  
        return super.onStartCommand(intent, flags, startId);  
    }      

    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.d(TAG, "onDestroy() executed");  
    } 
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}  

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			

			if (intent.getAction().equals(BROADCAST_USERID)) {
				usrId = intent.getStringExtra("String");
			} else if (intent.getAction().equals(BROADCAST_RECID)) {
				recId = intent.getStringExtra("String");
			} else if (intent.getAction().equals(BROADCAST_PROBLEM_MESSAGE)) {
				message = intent.getStringExtra("String");
			}else if (intent.getAction().equals(BROADCAST_SERVERNEW)) {
				message = intent.getStringExtra("String");
			} else if(intent.getAction().equals(BROADCAST_ASKER_CALL)){
				//String nothing = intent.getStringExtra("String");
		        new Thread(new Runnable() {  
		            @Override  
		            public void run() {  
		            	serverAskerCall(usrId, recId, message);
		            }  
		        }).start();  				
			} else if(intent.getAction().equals(BROADCAST_SERVERFUNC)){
				//String nothing = intent.getStringExtra("String");
		        new Thread(new Runnable() {  
		            @Override  
		            public void run() {  
		            	serverCall(servfunc);
		            }  
		        }).start();  
			}
				
		
		}
	};    
    
	private void serverAskerCall(String usr_Id, String rec_Id, String problem_msg){
		Log.d(TAG, "usr_id" + usr_Id);
		Log.d(TAG, "rec_id" + rec_Id);
		Log.d(TAG, "message" + problem_msg);
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.usr_id = usr_Id;
		sfr.rec_id = rec_Id;
		sfr.message = problem_msg;//"I Need Your Help!";
		sfr.cmd = Const.Command.Cmd_SendPushToOther;
		sfr.DoServerFunc();
		while(sfr.GetServResult() == null)
			;		
	}
	
	private void serverCall(ServerFuncRun sfr){
		//Log.d(TAG, "usr_id" + usr_Id);
		//Log.d(TAG, "rec_id" + rec_Id);
		//Log.d(TAG, "message" + problem_msg);
		//ServerFuncRun sfr = new ServerFuncRun();
		//sfr.usr_id = usr_Id;
		//sfr.rec_id = rec_Id;
		//sfr.message = problem_msg;//"I Need Your Help!";
		//sfr.cmd = Const.Command.Cmd_SendPushToOther;
		sfr.DoServerFunc();
		while(sfr.GetServResult() == null)
			;		
	}
	
	
}
