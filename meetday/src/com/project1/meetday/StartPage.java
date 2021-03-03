package com.project1.meetday;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.project1.http.Const;
import com.project1.http.FBRelated;
import com.project1.http.FileAccess;
import com.project1.http.PictUtil;

//import info.androidhive.gpluslogin.R;
//import info.androidhive.gpluslogin.MainActivity.LoadProfileImage;

public class StartPage extends Activity implements //OnClickListener,
ConnectionCallbacks, OnConnectionFailedListener{


	public static final String BROADCAST_GETHELPRESPONSE =
			"com.project1.start.GETHELPRESPOSE";
	public static final String FB_ID =
			"com.project1.start.FB_ID";
	public static final String Start_personPhotoUrl =
			"com.project1.start.Start_personPhotoUrl";
	public static final String Start_personName =
			"com.project1.start.Start_personName";
	public static final String Start_email =
			"com.project1.start.Start_email";
	public static final String Start_type =
			"com.project1.start.Start_type";
	public static final String Start_phone =
			"com.project1.start.Start_phone";
	public static final String Start_pwd =
			"com.project1.start.Start_pwd";
	
	public static Activity Mainact = null;

	private LinearLayout lilayoutStartPage;
	private ImageView login_imagetitle, login_imagetitleword, login_top_leftText, login_top_rightText, settingItemText;
	private IntentFilter mIntentFilter; 
//	private static ImageButton btnPhoneLogin;
	private static ImageButton gmailbutton;
	private static ImageButton fbbutton, button;
	private static TextView    loginStateTxt;
//	private static ImageButton logbutton, signupbutton;
	//private LoginButton loginButton;
    private static String TAG = "StartPage";

    private Context context;
    private static final int RC_SIGN_IN = 0;
	// Logcat tag
	//private static final String TAG = "MainActivity";

	// Profile pic image size in pixels
	private static final int PROFILE_PIC_SIZE = 230;

	// Google client to interact with Google API
	//private GoogleApiClient mGoogleApiClient;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	//private ConnectionResult mConnectionResult;   
    
    //CallbackManager callbackManager;
    private AccessToken accessToken;
	private CallbackManager callbackManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = getApplicationContext();
		
		Mainact = this;

		lilayoutStartPage = (LinearLayout) findViewById(R.id.lilayoutStartPage);
		login_imagetitle = (ImageView) findViewById(R.id.login_imagetitle);
		login_imagetitleword = (ImageView) findViewById(R.id.login_imagetitleword);
		login_top_leftText = (ImageView) findViewById(R.id.login_top_leftText);
		login_top_rightText = (ImageView) findViewById(R.id.login_top_rightText);
		settingItemText = (ImageView) findViewById(R.id.settingItemText);

		Intent intent = getIntent();
		final String type = intent.getStringExtra(Start_type);
		
        Const.mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
        .addApi(Plus.API, new Plus.PlusOptions.Builder().build()) 
        .addScope(Plus.SCOPE_PLUS_LOGIN).build(); 
		
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BROADCAST_GETHELPRESPONSE);
		registerReceiver(mResponse, mIntentFilter);	 
		
		if(type==null){
			setContentView(R.layout.start_page);
		}else if(type.equals(Const.eSocialCmd.Social_Unbind)){
			setContentView(R.layout.logout_page);
			
			button = (ImageButton) findViewById(R.id.logout_imagetitle);
			button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					if (Const.isNetworkConnected(context)){					
						intent.setClass(context, SignInWithSocial.class);
						intent.putExtra(SignInWithSocial.SOCIAL_TYPE, "");
						intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, Const.eSocialCmd.Social_Main);
						intent.putExtra(SignInWithSocial.SOCIAL_INOUT, Const.eSocialCmd.Social_Logout);						
						startActivity(intent);
						StartPage.this.finish();
					} else {
						Toast.makeText(context, getResources().getString(R.string.login_offline), Toast.LENGTH_LONG).show();
					}
					
				}
			});
			
			return ;
		}
		
		PictUtil.deleteFile(Const.localProfilePicPath);
		
		callbackManager = CallbackManager.Factory.create();
		
		loginStateTxt = (TextView) findViewById(R.id.login_top_middleText);
		loginStateTxt.setText(R.string.login_method);
		
		
//		btnPhoneLogin = (ImageButton)findViewById(R.id.phone_btn);
//		btnPhoneLogin.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				if (Const.isNetworkConnected(context)){
//					Intent intent = new Intent();
//					intent.setClass(context, LoginByPhone.class);
//					startActivity(intent);
//				} else {
//					Toast.makeText(context, getResources().getString(R.string.login_offline), Toast.LENGTH_LONG).show();
//				}
//
//			}
//		});
		
		fbbutton = (ImageButton) findViewById(R.id.fb_btn);
		fbbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				//i.setClass(StartPage.this, SignInWithFB.class);
				//startActivity(i);	
				if (Const.isNetworkConnected(context)){
					//if(type==null){
					intent.setClass(context, SignInWithSocial.class);
					intent.putExtra(SignInWithSocial.SOCIAL_TYPE, Const.eSocialCmd.Social_FB);
					intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, Const.eSocialCmd.Social_StartPage);
					intent.putExtra(SignInWithSocial.SOCIAL_INOUT, Const.eSocialCmd.Social_Login);
					//} else if(type == Const.eSocialCmd.Social_Unbind){
						//Intent intent = new Intent();
						//intent.setClass(context, SignInWithSocial.class);
						//intent.putExtra(SignInWithSocial.SOCIAL_TYPE, "");
						//intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, Const.eSocialCmd.Social_Main);
						//intent.putExtra(SignInWithSocial.SOCIAL_INOUT, Const.eSocialCmd.Social_Logout);
					//}
					startActivity(intent);
				} else {
					Toast.makeText(context, getResources().getString(R.string.login_offline), Toast.LENGTH_LONG).show();
				}
				
			}
		});

		gmailbutton = (ImageButton) findViewById(R.id.gmail_btn);
		gmailbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Log.d(TAG, "Gmail");
				Intent intent = new Intent();
				if (Const.isNetworkConnected(context)){
					//signInWithGplus();					
					//if(type == null){
					intent.setClass(context, SignInWithSocial.class);
					intent.putExtra(SignInWithSocial.SOCIAL_TYPE, Const.eSocialCmd.Social_Gmail);
					intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, Const.eSocialCmd.Social_StartPage);
					intent.putExtra(SignInWithSocial.SOCIAL_INOUT, Const.eSocialCmd.Social_Login);

					//} else if(type == Const.eSocialCmd.Social_Unbind){
						//intent.setClass(context, SignInWithSocial.class);
						//intent.putExtra(SignInWithSocial.SOCIAL_TYPE, "Google");
						//intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, "Main");
						//intent.putExtra(SignInWithSocial.SOCIAL_INOUT, "Login");
					//}
					startActivity(intent);
				} else {
					Toast.makeText(context, getResources().getString(R.string.login_offline), Toast.LENGTH_LONG).show();
				}

//				signInWithGplus();
			}
		});
    
		//loginButton = (LoginButton) findViewById(R.id.login_button);
		} 

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
	
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		Const.mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (Const.mGoogleApiClient.isConnected()) {
			Const.mGoogleApiClient.disconnect();
		}
	}

    protected void onDestroy() {
		unregisterReceiver(mResponse);
		releaseViews();
		System.gc();
		Log.i(TAG, "onDestory()............");
        super.onDestroy(); 
        return;
    }

	private void releaseViews() {
		if(settingItemText != null) {
			settingItemText.setBackground(null);
		}
//		if(btnPhoneLogin != null) {
//			btnPhoneLogin.setBackground(null);
//		}
		if(fbbutton != null) {
			fbbutton.setBackground(null);
		}
		if(gmailbutton != null) {
			gmailbutton.setBackground(null);
		}
		if(login_top_rightText != null) {
			login_top_rightText.setBackground(null);
		}
		if(login_top_leftText != null) {
			login_top_leftText.setBackground(null);
		}
		if(loginStateTxt != null) {
			loginStateTxt.setBackground(null);
		}
		if(login_imagetitleword != null) {
			login_imagetitleword.setBackground(null);
		}
		if(login_imagetitle != null) {
			login_imagetitle.setBackground(null);
		}
		if(lilayoutStartPage != null) {
			lilayoutStartPage.setBackground(null);
		}
	}

	/**
	 * Method to resolve any signin errors,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	 * */


	//@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			Const.mConnectionResult = result;

		}

	}

	//@Override
	public void onConnected(Bundle arg0) {

	}



	//@Override
	public void onConnectionSuspended(int arg0) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_page, menu);
		return true;
	}


	private void loginInBackground(final ConnectToServer con, final String personPhotoUrl, final String personName, 
			final String email, final Const.eUsrType type, final String senddata) {
		new AsyncTask<Void, Void, String>() {
		private ProgressDialog pdialog;
		//private ServerFuncRun sfr = new ServerFuncRun();
        protected void onPreExecute() {
        	//pdialog = ProgressDialog.show(StartPage.this, getResources().getString(R.string.login_connecting), getResources().getString(R.string.login_wait),true);
        }
		@Override
		protected String doInBackground(Void... params) {
			String msg = "";
			try {				
				if(Looper.myLooper() == null)
					Looper.prepare();
				
				while(Const.lastcmdret.equals("")){
					;
				}
				Log.e(TAG, "lastcmdret: " + Const.lastcmdret);
				String[] data = FileAccess.parseDataToPara(Const.lastcmdret, "%");
				if(data[0].equals(Const.Command.Cmd_Register.toString())&&data[1].equals(Const.RRet.RRET_SUCCESS.toString())){
					Intent i = new Intent();
					i.setClass(StartPage.this, SignUpPage.class);
					con.DoGetPhoto(personPhotoUrl);		
					i.putExtra(SignUpPage.PHOTOURL, personPhotoUrl);
					i.putExtra(SignUpPage.USERID, data[2]);
					i.putExtra(SignUpPage.NICKNAME, personName);
	        		i.putExtra(SignUpPage.ACCOUNT, email);
	        		i.putExtra(SignUpPage.PASSWD, Const.univeralpass);
	        		i.putExtra(SignUpPage.LOGINTYPE, String.valueOf(type));
	        		i.putExtra(SignUpPage.LOGINDATA, senddata);
					startActivity(i);	
				} else if(data[0].equals(Const.Command.Cmd_Register.toString())&&data[1].equals(Const.RRet.RRET_USER_EXIST_FAIL.toString())){
					//FBRelated.GetFBFIDList(StartPage.this);
					//FBRelated fbr = new FBRelated();
					//fbr.SetLoginPara(email, Const.univeralpass, "login");
					//fbr.startList(StartPage.this, true);
					Intent intent = new Intent();
	        		intent.setClass(context, LoginPage.class);
	        		intent.putExtra(LoginPage.ACCOUNT, email);
	        		intent.putExtra(LoginPage.PASSWD, Const.univeralpass);
	        		intent.putExtra(LoginPage.REDIRECT, Const.eSocialCmd.Social_Login);
	        		intent.putExtra(LoginPage.LOGINTYPE, String.valueOf(type));
	        		intent.putExtra(LoginPage.LOGINDATA, senddata);
	        		startActivity(intent);

				}
				Const.lastcmdret = "";
			} catch (Exception ex) {
				msg = "Error :" + ex.getMessage();
				//Log.d("RegisterActivity", "Error: " + msg);
			}
			//Log.d("RegisterActivity", "AsyncTask completed: " + msg);
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {
			//pdialog.dismiss();
			StartPage.this.finish();
		}
		}.execute(null, null, null);
	}

	private BroadcastReceiver mResponse = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(BROADCAST_GETHELPRESPONSE)) {
				String type = intent.getStringExtra(Start_type);	
				String email = intent.getStringExtra(Start_email);
				String name = intent.getStringExtra(Start_personName);
				String url = intent.getStringExtra(Start_personPhotoUrl);
				PictUtil.deleteFile(Const.localProfilePicPath);
				ConnectToServer con = new ConnectToServer(context);
				if(type.equals(Const.eSocialCmd.Social_FB)){
					String fbid = intent.getStringExtra(FB_ID);
					FBRelated.GetFBFIDList(context);
					FileAccess.putStringInPreferences(context,
							fbid, Const.eUsrType.UsrType_FBId,
							Const.projinfo.sSharePreferenceName);
		    		FileAccess.putStringInPreferences(context, Const.eSocialCmd.Social_Connect, 
		    				Const.eUsrType.UsrType_FB_Status, Const.projinfo.sSharePreferenceName);
					con.DoRegister(email, Const.univeralpass,
							String.valueOf(Const.eUsrType.UsrType_FBId), fbid);
					//con.DoRegister(fbid, Const.univeralpass, String.valueOf(Const.eUsrType.UsrType_FBId), fbid);	
					loginInBackground(con, url, name, email, Const.eUsrType.UsrType_FBId, fbid);
				} else if(type.equals(Const.eSocialCmd.Social_Gmail)){		
					FileAccess.putStringInPreferences(context, 
							email, Const.eUsrType.UsrType_Gmail, 
							Const.projinfo.sSharePreferenceName);
		    		FileAccess.putStringInPreferences(context, Const.eSocialCmd.Social_Connect, 
		    				Const.eUsrType.UsrType_Gmail_Status, Const.projinfo.sSharePreferenceName);
					con.DoRegister(email, Const.univeralpass, String.valueOf(Const.eUsrType.UsrType_Gmail), email);	
					//con.DoRegister(email, Const.univeralpass, String.valueOf(Const.eUsrType.UsrType_Gmail), email);
					loginInBackground(con, url, name ,email, Const.eUsrType.UsrType_Gmail, email);	
				} else if(type.equals(Const.eSocialCmd.Social_Phone)){	
					String phone = intent.getStringExtra(Start_phone);
					String pwd = intent.getStringExtra(Start_pwd);
					FileAccess.putStringInPreferences(context, 
							phone, Const.eUsrType.UsrType_Fone, 
							Const.projinfo.sSharePreferenceName);
					con.DoRegister_Type(phone, pwd, String.valueOf(Const.eUsrType.UsrType_Fone), phone);									
					//loginInBackground(con, "", "" ,phone);	
				}
			} 				
		
		}
	};
	
}
