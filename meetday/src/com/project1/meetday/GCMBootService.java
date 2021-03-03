package com.project1.meetday;

import java.io.IOException;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.project1.http.Const;
import com.project1.http.FBRelated;
import com.project1.http.FileAccess;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class GCMBootService extends Service {

	private String TAG = "GCMBootService";	
	private static final String Default = "";
	
	ThreadForRegsiterGCM threadForRegister = null; 
	
	GoogleCloudMessaging gcm;
	String regId;	

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}	
	
    @Override  
    public void onCreate() {  
        Log.d(TAG, "onCreate() executed");		
    }  	

	@Override
	public int onStartCommand(Intent pIntent, int flags, int startId) {
	    // TODO Auto-generated method stub
	    
		if(null == threadForRegister){
			threadForRegister = new ThreadForRegsiterGCM();
			threadForRegister.start();
		}
	    return super.onStartCommand(pIntent, flags, startId);
	}    

	public class ThreadForRegsiterGCM extends Thread {
		
		private boolean running = false;
		
		@Override
		public void run() {
		    super.run();
			String local_regid = FileAccess.getStringFromPreferences(getBaseContext(), 
			Default, Const.eUsrType.UsrType_RegId, Const.projinfo.sSharePreferenceName);
		    running = true;
		    
		    int i = 0;
		    while (running) {
		    	Log.d(TAG, "Service running!!" + i++);
		        try {
		            sleep(1000);
		            if(true == checkNetworkConnected()){
		            	Log.d(TAG, "Network is avalible!!");
		        		if(local_regid.equals(Default)){
			    			regId = registerGCM();
			    			Log.d(TAG, "GCM RegId: " + regId);
			    		} else {
			    			regId = local_regid;
			    			Log.d(TAG, "Already Registered with GCM Server!");			    			
			    		}			            	
		            	break;
		            }else{
		            	Log.d(TAG, "Network is unavalible!!");                        	
		            }
		        } catch (InterruptedException e) {
		        	Log.d(TAG, "Service error!!" + i++);
		            e.printStackTrace();
		        }
		    }
		    Log.d(TAG, "Service stop!!");
		}
	}
	
	private boolean checkNetworkConnected() {
		boolean result = false;
		ConnectivityManager CM = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE); 
		if (CM == null) {
			result = false;
		} else {
			NetworkInfo info = CM.getActiveNetworkInfo(); 
			if (info != null && info.isConnected()) {
				if (!info.isAvailable()) {
					result = false;
				} else {
					result = true;
				}
					Log.d(TAG, "[目前連線方式]"+info.getTypeName());
					Log.d(TAG, "[目前連線狀態]"+info.getState());
					Log.d(TAG, "[目前網路是否可使用]"+info.isAvailable());
					Log.d(TAG, "[網路是否已連接]"+info.isConnected());
					Log.d(TAG, "[網路是否已連接 或 連線中]"+info.isConnectedOrConnecting());
					Log.d(TAG, "[網路目前是否有問題 ]"+info.isFailover());
					Log.d(TAG, "[網路目前是否在漫遊中]"+info.isRoaming());
			}
		}
		return result;
	}		
	
    private String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		//regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d(TAG,
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			Log.d(TAG, "RegId already available. RegId: " + regId.length());
		}
		return regId;
	}


	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(getBaseContext());
					}
					regId = gcm.register(Const.projinfo.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					//Const.userinfo.regid = regId;
		        	//FileAccess.putStringInPreferences(getBaseContext(), regId,
		        			//Const.eUsrType.UsrType_RegId, Const.projinfo.sSharePreferenceName);
		        	
					//storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d(TAG, "Error: " + msg);
				}
				Log.d(TAG, "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.d(TAG, "Registered with GCM Server." + msg.length());
			}
		}.execute(null, null, null);
	}	
	
}
