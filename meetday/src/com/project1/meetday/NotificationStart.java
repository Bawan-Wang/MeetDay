package com.project1.meetday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.project1.http.Const;
import com.project1.http.FBRelated;
import com.project1.http.FileAccess;
import com.project1.http.ServerFuncRun;

public class NotificationStart extends Activity{
	
	public static final String NOTIFY_TYPE =
			"com.project1.notification.TYPE";
    
	static TextView register;
	static SpannableString spannableString;
	public static final String Default = "null";
	
	//GoogleCloudMessaging gcm;
	Context context;

	static final String TAG = "NotificationStart";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		//setContentView(R.layout.welcom_page);
		//context = getApplicationContext();
		context = this;
		//String name = FileAccess.getStringFromPreferences(this, Default, Const.eUsrType.UsrType_Email,
				//Const.projinfo.sSharePreferenceName);//settings.getString("name", Default);
		//String pass = FileAccess.getStringFromPreferences(this, Default, Const.eUsrType.UsrType_Pass,
				//Const.projinfo.sSharePreferenceName);//settings.getString("pass", Default);
		//String local_regid = FileAccess.getStringFromPreferences(context, Default, Const.eUsrType.UsrType_RegId, Const.sSharePreferenceName);
		//Log.d(this.getClass().getName(),"FFFFFF");
		//Const.userinfo.Location_Type = Const.eLocation.Location_Record;
		if(Const.userinfo.get_front_running() == false){
			ServerFuncRun sfr = new ServerFuncRun();
			Const.Log_Info info = Const.get_login_data(context);
			if(info!=null){
				sfr.datatype = info.datatype;
				sfr.data = info.data;
				sfr.pass = info.pass;
				sfr.cmd = Const.Command.Cmd_Login_Type;
				sfr.DoServerFunc();
				if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){			
			        Intent i = new Intent();
					i.setClass(NotificationStart.this, WelcomPage.class);
					i.putExtra(MainPage.MAINPAGE_LOCATION, String.valueOf(Const.eLocation.Location_Record));
					startActivity(i);
					NotificationStart.this.finish();
				}
			}
		} else {
			//if(MainPage.Mainact != null)
				//MainPage.Mainact.finish();		
			//Log.d(this.getClass().getName(),"vsdvdvsvsd");
			Log.d(TAG, "onCreate");
			Intent i = new Intent();
			i.setClass(NotificationStart.this, MainPage.class);		
			i.putExtra(MainPage.MAINPAGE_LOCATION, String.valueOf(Const.eLocation.Location_Record));
			if(MainPage.Mainact!=null){
				String location =  FileAccess.getStringFromPreferences(this, 
						String.valueOf(Const.eLocation.Location_Helper), 
						Const.eUsrType.UsrType_FragmentLocation, Const.projinfo.sSharePreferenceName);
				if(location != String.valueOf(Const.eLocation.Location_Helper)){
					MainPage.Mainact.finish();
					startActivity(i);
				}					
			} else {
				startActivity(i);
			}
				
			NotificationStart.this.finish();
		}
		
		//initViewPager();
		      
 
	}
	
	@Override
	public void onBackPressed() {

	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestory()............");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()............");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()............");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()............");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()............");
    }
	
	
}