package com.project1.meetday;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.project1.http.Const;
import com.project1.http.FBRelated;
import com.project1.http.FileAccess;
import com.project1.http.ServerFuncRun;

public class SignInWithSocial extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener{

	public static final String SOCIAL_TYPE =
		    "com.project1.social.SOCIAL_TYPE";
	public static final String SOCIAL_ACTIVITY =
		    "com.project1.social.SOCIAL_ACTIVITY";
	public static final String SOCIAL_INOUT =
		    "com.project1.social.SOCIAL_INOUT";
	
//	private static ImageButton logbutton, signupbutton;
	//private LoginButton loginButton;
    private static String TAG = "SignInWithSocial";

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
	private String socailact,socailtype,socialinout;
	private AppInviteDialog mInvititeDialog;
	//Const.eSocialCmd eact, etype , einout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = getApplicationContext();
		
		//setContentView(R.layout.start_page);
		
		
		//PictUtil.deleteFile(Const.localProfilePicPath);
		
		callbackManager = CallbackManager.Factory.create();

	    final Intent intent = getIntent();
	    socailtype = intent.getStringExtra(SOCIAL_TYPE);
	    socailact = intent.getStringExtra(SOCIAL_ACTIVITY);
	    socialinout = intent.getStringExtra(SOCIAL_INOUT);
		Log.d(TAG, "socailact"+socailact);
	    
		if(!Const.isNetworkConnected(context)){
			Toast.makeText(context, "You are offline!", Toast.LENGTH_LONG).show();
		} else {
			if(socialinout.equals(Const.eSocialCmd.Social_Login)){
				if(socailtype.equals(Const.eSocialCmd.Social_Gmail)){
					//FileAccess.clearApplicationData(context);
					//signOutFromGplus();
					signInWithGplus();
				} else if(socailtype.equals(Const.eSocialCmd.Social_FB)){
					doFBConnect();
				}
			} else if(socialinout.equals(Const.eSocialCmd.Social_Logout)){
				if(socailtype.equals(Const.eSocialCmd.Social_Gmail)){
					googlelogout();
				} else if(socailtype.equals(Const.eSocialCmd.Social_FB)){
					facebookLogout();
				} else {
					//revokeGplusAccess();
					if(!FileAccess.getStringFromPreferences(context,
							"null", Const.eUsrType.UsrType_Gmail,
							Const.projinfo.sSharePreferenceName).toLowerCase().equals("null")){
						Log.d("LG", "LFGOOGLE");
						signOutFromGplus();
						//revokeGplusAccess();
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_Gmail, 
								Const.projinfo.sSharePreferenceName);
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_Gmail_Status, 
								Const.projinfo.sSharePreferenceName);
					}
					if(!FileAccess.getStringFromPreferences(context,
							"null", Const.eUsrType.UsrType_FBId,
							Const.projinfo.sSharePreferenceName).toLowerCase().equals("null")){
						Log.d("LG", "LGFB.");
						LoginManager.getInstance().logOut();
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_FBId, 
								Const.projinfo.sSharePreferenceName);
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_FB_Status, 
								Const.projinfo.sSharePreferenceName);
					}
					/*if(socailact.equals("Main")){
						String reg = FileAccess.getStringFromPreferences(context, "", 
								Const.eUsrType.UsrType_RegId, Const.projinfo.sSharePreferenceName);
						FileAccess.clearApplicationData(context);
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_Email, 
								Const.projinfo.sSharePreferenceName);
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_Pass, 
								Const.projinfo.sSharePreferenceName);
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_FreindList, 
								Const.projinfo.sSharePreferenceName);
						FileAccess.putStringInPreferences(context, reg, Const.eUsrType.UsrType_RegId, 
								Const.projinfo.sSharePreferenceName);
						gotoStartPage();
						SignInWithSocial.this.finish();
					} else*/ {
						ServerFuncRun sfr = new ServerFuncRun();
						sfr.cmd = Const.Command.Cmd_Logout;
						sfr.name = 	FileAccess.getStringFromPreferences(context,
								"", Const.eUsrType.UsrType_Email,
								Const.projinfo.sSharePreferenceName);
						ServerInBackground(sfr);
					}
				}
			} else if(socialinout.equals(Const.eSocialCmd.Social_Invite)){
				openDialogInvite(this);
			}
		}

		
		//loginStateTxt = (TextView) findViewById(R.id.login_top_middleText);
		//loginStateTxt.setText(R.string.login_method);

        
		//loginButton = (LoginButton) findViewById(R.id.login_button);
        Const.mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
        .addApi(Plus.API, new Plus.PlusOptions.Builder().build()) 
        .addScope(Plus.SCOPE_PLUS_LOGIN).build(); 
		
	}

	public void openDialogInvite(final Activity activity) {
		//String appLinkUrl, previewImageUrl;

		//appLinkUrl = "https://www.mydomain.com/myapplink";
		//previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";
        String AppURl = Const.download_path;//"https://fb.me/421570...5709";  //Generated from //fb developers

        String previewImageUrl = Const.logo_path;

    if (AppInviteDialog.canShow()) {
        AppInviteContent content = new AppInviteContent.Builder()
                .setApplinkUrl(AppURl).setPreviewImageUrl(previewImageUrl)
                .build();

        AppInviteDialog appInviteDialog = new AppInviteDialog(activity);
        appInviteDialog.registerCallback(callbackManager,
                new FacebookCallback<AppInviteDialog.Result>() {
                    @Override
                    public void onSuccess(AppInviteDialog.Result result) {
                        Log.d("Invitation", "Invitation Sent Successfully");
                        finish();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Log.d("Invitation", "Error Occured");
                    }
                });

        appInviteDialog.show(content);
    }

}
	
	private void facebookLoginFlow() {
		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult loginResult) {

				accessToken = loginResult.getAccessToken();

				Log.d("FB", "access token got.");

				//send request and call graph api

				GraphRequest request = GraphRequest.newMeRequest(
						accessToken,
						new GraphRequest.GraphJSONObjectCallback() {

							@Override
							public void onCompleted(JSONObject object, GraphResponse response) {

								String personPhotoUrl = "https://graph.facebook.com/" + object.optString("id")+"/picture?type=large&width=230";
								String reg_name;
								
								Log.d("FB", "complete");
								Log.d("FB", object.optString("name"));
								Log.d("FB", object.optString("email"));
								Log.d("FB", object.optString("id"));
								
								Intent broadcastIntent  = new Intent();
								
								if (object.has("email")){
									reg_name = object.optString("email");
								} else {
									Log.d("FB", "No email, Reg in With FB ID");
									reg_name = object.optString("id");
								}

		                            /* make the API call */
								if(socailact.equals(Const.eSocialCmd.Social_StartPage)){
									broadcastIntent.setAction(StartPage.BROADCAST_GETHELPRESPONSE);		
									broadcastIntent.putExtra(StartPage.FB_ID, object.optString("id"));					
									broadcastIntent.putExtra(StartPage.Start_type, Const.eSocialCmd.Social_FB);
									broadcastIntent.putExtra(StartPage.Start_email, reg_name);
									broadcastIntent.putExtra(StartPage.Start_personName, object.optString("name"));
									broadcastIntent.putExtra(StartPage.Start_personPhotoUrl, personPhotoUrl);
									sendBroadcast(broadcastIntent);	
									SignInWithSocial.this.finish();
								} else if(socailact.equals(Const.eSocialCmd.Social_Personal)){
									ServerFuncRun sfr = new ServerFuncRun();
									sfr.cmd = Const.Command.Cmd_DoSearch;
									sfr.datatype = Const.eUsrType.UsrType_FBId;
									sfr.data = object.optString("id");
									ServerInBackground(sfr);
								} else if(socailact.equals(Const.eSocialCmd.Social_AddFriendPage)){
									String fbid = FileAccess.getStringFromPreferences(getBaseContext(), 
											"", Const.eUsrType.UsrType_FBId, Const.projinfo.sSharePreferenceName);
									if(object.optString("id").equals(fbid)){
										//openDialogInvite(this);	
									}
								}
								//sendBroadcast(broadcastIntent);	
								//SignInWithSocial.this.finish();
							}							
						});

				Bundle parameters = new Bundle();
				parameters.putString("fields", "id,name,link,email");
				request.setParameters(parameters);
				request.executeAsync();
			}

			@Override
			public void onCancel() {
				// App code
				Log.d("FB", "CANCEL");
				SignInWithSocial.this.finish();
			}


			@Override
			public void onError(FacebookException exception) {
				// App code

				Log.d("FB", exception.toString());
			}
		});
	}

	private void facebookLogout(){
		LoginManager.getInstance().logOut();
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_Logout;
		sfr.name = 	FileAccess.getStringFromPreferences(context,
				"", Const.eUsrType.UsrType_Email,
				Const.projinfo.sSharePreferenceName);;
		ServerInBackground(sfr);
		FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_FBId, 
				Const.projinfo.sSharePreferenceName);
		FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_FB_Status, 
				Const.projinfo.sSharePreferenceName);
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
	
    @Override
    public void onBackPressed() {
       // TODO Auto-generated method stub
    	Log.d(TAG, "onBackPressed");
        //SignInWithSocial.this.finish();
        super.onBackPressed();               
    }
    
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		if(socailtype.equals(Const.eSocialCmd.Social_Gmail) &&  socailact.equals(Const.eSocialCmd.Social_StartPage))
			Const.mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (Const.mGoogleApiClient.isConnected()) {
			Const.mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Method to resolve any signin errors,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	 * */
	private void resolveSignInError() {
		Log.d(TAG, "resolveSignInError");
		if (Const.mConnectionResult.hasResolution()) {
			Log.d(TAG, "XXX");
			try {
				mIntentInProgress = true;
				Const.mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				Const.mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			Const.mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	
	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {      
		
		super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
        
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!Const.mGoogleApiClient.isConnecting()) {
				Const.mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

		// Get user's information
		getProfileInformation();
	}

	/**
	 * Fetching user's information name, email, profile pic
	 * */
	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(Const.mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(Const.mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				String email = Plus.AccountApi.getAccountName(Const.mGoogleApiClient);
			
				personPhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2)
						+ PROFILE_PIC_SIZE;
				

				Intent broadcastIntent  = new Intent();
				
				if(socailact.equals(Const.eSocialCmd.Social_StartPage)){
					broadcastIntent.setAction(StartPage.BROADCAST_GETHELPRESPONSE);								
					broadcastIntent.putExtra(StartPage.Start_type, Const.eSocialCmd.Social_Gmail);
					broadcastIntent.putExtra(StartPage.Start_email, email);
					broadcastIntent.putExtra(StartPage.Start_personName, personName);
					broadcastIntent.putExtra(StartPage.Start_personPhotoUrl, personPhotoUrl);
					sendBroadcast(broadcastIntent);
					SignInWithSocial.this.finish();
				} else if(socailact.equals(Const.eSocialCmd.Social_Personal)){
					ServerFuncRun sfr = new ServerFuncRun();
					//String fbid = intent.getStringExtra(FB_ID);
					sfr.cmd = Const.Command.Cmd_DoSearch;
					sfr.datatype = Const.eUsrType.UsrType_Gmail;
					sfr.data = email;
					ServerInBackground(sfr);
				}
				//sendBroadcast(broadcastIntent);	
				
				Log.e(TAG, "Name: " + personName + ", plusProfile: "
						+ personGooglePlusProfile + ", email: " + email
						+ ", Image: " + personPhotoUrl);
										
			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Const.mGoogleApiClient.connect();
		//updateUI(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_page, menu);
		return true;
	}


	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
		Log.d(TAG, "SING");
		if (!Const.mGoogleApiClient.isConnecting()) {
			Log.d(TAG, "SIGC");
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	
	private void googlelogout(){
		signOutFromGplus();
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_Logout;
		sfr.name = 	FileAccess.getStringFromPreferences(context,
				"", Const.eUsrType.UsrType_Email,
				Const.projinfo.sSharePreferenceName);
		ServerInBackground(sfr);
		FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_Gmail, 
				Const.projinfo.sSharePreferenceName);
		FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_Gmail_Status, 
				Const.projinfo.sSharePreferenceName);
	}
	
	/**
	 * Sign-out from google
	 * */
	private void signOutFromGplus() {
		if (Const.mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(Const.mGoogleApiClient);
			Const.mGoogleApiClient.disconnect();
			//Const.mGoogleApiClient.connect();
			//updateUI(false);
		}
	}

	/**
	 * Revoking access from google
	 * */
	private void revokeGplusAccess() {
		if (Const.mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(Const.mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(Const.mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e(TAG, "User access revoked!");
							Const.mGoogleApiClient.connect();
							//updateUI(false);
						}

					});
		}
	}

	/**
	 * Background Async task to load user profile picture from url
	 * */
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public LoadProfileImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}
	
	private void doFBConnect(){
		LoginManager.getInstance().logInWithReadPermissions(SignInWithSocial.this, Arrays.asList("public_profile", "user_friends", "email"));
		facebookLoginFlow();
	}
	
	private void ServerInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog pdialog;
			//ServerFuncRun sf = new ServerFuncRun();
	        protected void onPreExecute() {
	        	//pdialog = ProgressDialog.show(PersonalPage.this, "Connecting", "Wait...",true);				
	        }
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				if(!socailact.equals(Const.eSocialCmd.Social_Main)){
					try {				
						if(Looper.myLooper() == null)
							Looper.prepare();
						sfr.DoServerFunc();
						while(sfr.GetServResult() == null)
							;
						
					} catch (Exception ex) {
						msg = "Error :" + ex.getMessage();
						Log.d("ServerInBackground", "Error: " + msg);
					}
				}
				Log.d("ServerInBackground", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//pdialog.dismiss();
				//if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){
				if(Const.Command.Cmd_DoSearch == sfr.cmd){
					if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
						MessageDialog(SignInWithSocial.this,"Error", "Your account has been connectted to other device!");
						if(Const.eUsrType.UsrType_FBId == sfr.datatype)
							LoginManager.getInstance().logOut();
						if(Const.eUsrType.UsrType_Gmail == sfr.datatype)
							signOutFromGplus();
					}else{
						String localid = FileAccess.getStringFromPreferences(getBaseContext(), 
								null, Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
						ConnectToServer con = new ConnectToServer(context);
						con.DoUpdateData(localid, sfr.datatype, sfr.data);
						if(Const.eUsrType.UsrType_FBId == sfr.datatype){
							FBRelated.GetFBFIDList(context);
							FileAccess.putStringInPreferences(context,
									sfr.data, Const.eUsrType.UsrType_FBId,
									Const.projinfo.sSharePreferenceName);
				    		FileAccess.putStringInPreferences(context, Const.eSocialCmd.Social_Connect, 
				    				Const.eUsrType.UsrType_FB_Status, Const.projinfo.sSharePreferenceName);
						} else if (Const.eUsrType.UsrType_Gmail == sfr.datatype){
							FileAccess.putStringInPreferences(context, 
									sfr.data, Const.eUsrType.UsrType_Gmail, 
									Const.projinfo.sSharePreferenceName);
				    		FileAccess.putStringInPreferences(context, Const.eSocialCmd.Social_Connect, 
				    				Const.eUsrType.UsrType_Gmail_Status, Const.projinfo.sSharePreferenceName);
						}
						MessageDialog(SignInWithSocial.this,"Info", "Your account has been connectted!");
					}

				} else if(Const.Command.Cmd_Logout == sfr.cmd){
					if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS || socailact.equals(Const.eSocialCmd.Social_Main)){
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_Email, 
								Const.projinfo.sSharePreferenceName);
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_Pass, 
								Const.projinfo.sSharePreferenceName);
						FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_FreindList, 
								Const.projinfo.sSharePreferenceName);
						Const.contactinfo.ladapter = null;
						Const.contactinfo.setDataStatus(false);
						gotoStartPage();					
					} else {
						gotoPersonalPage();
					}
					SignInWithSocial.this.finish();
				}
				//SignInWithSocial.this.finish();
			}
		}.execute(null, null, null);
	
	}
	
	private void gotoPersonalPage(){
		Intent intent = new Intent();
		intent.setClass(context, PersonalPage.class);
		startActivity(intent);
		//Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();	
	}
	
	private void gotoStartPage(){
		Intent intent = new Intent();
		intent.setClass(context, StartPage.class);
		startActivity(intent);
		if(MainPage.Mainact!=null){
			MainPage.Mainact.finish();
		}
		//Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();	
	}
	
	public void MessageDialog(final Context context, String title, String message) {
	      AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	      dialog.setTitle(title);
	      dialog.setMessage(message);
	      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
	            	gotoPersonalPage();
	            	SignInWithSocial.this.finish();
	           }
	      });
	      dialog.show();
	} //EOF warningDialog
	
}