package com.project1.meetday;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.project1.http.Const;
import com.project1.http.FileAccess;
import com.project1.http.ServerFuncRun;

import java.util.ArrayList;
import java.util.HashMap;

//import android.content.Context;
//import android.content.SharedPreferences;

public class PersonalPage extends Activity implements 
ConnectionCallbacks, OnConnectionFailedListener{
	public static Activity Mainact = null;

	private LinearLayout lilayoutPersonalPage;
	private TextView PersonalInfoTextView;
	private static final int[] arr = new int[]{ R.string.sett_id, R.string.sett_fb, R.string.sett_email,
		R.string.sett_phone_number, R.string.sett_logout};
//	String[] arr = new String[]{ "ID", "Email","Phone Number" };
	
	ListView listView;
	int i;
	String strInput;
	String[] usrdata;
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	private SimpleAdapter adapter;
	//ArrayList<String> data; 
	private ServerFuncRun sfr = new ServerFuncRun();
	private Context context;
	private String localid;
	//private IntentFilter mIntentFilter; 
	AlertDialog.Builder ReplyAdd_dialog;
	
	private String TAG = "Personal";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.personal_page);			
		this.context = getBaseContext();
		Mainact = this;
		//mIntentFilter = new IntentFilter();
		//mIntentFilter.addAction(BROADCAST_GETHELPRESPONSE);
		//registerReceiver(mResponse, mIntentFilter);	 
		
		localid = FileAccess.getStringFromPreferences(getBaseContext(), 
					null, Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
		//callbackManager = CallbackManager.Factory.create();
		//settings = getSharedPreferences("setting",Context.MODE_PRIVATE);
		usrdata = new String[]{
				FileAccess.getStringFromPreferences(getBaseContext(), "", Const.eUsrType.UsrType_SearchId, Const.projinfo.sSharePreferenceName),
				FileAccess.getStringFromPreferences(getBaseContext(), "", Const.eUsrType.UsrType_FBId, Const.projinfo.sSharePreferenceName),
				FileAccess.getStringFromPreferences(getBaseContext(), "", Const.eUsrType.UsrType_Gmail, Const.projinfo.sSharePreferenceName),
				FileAccess.getStringFromPreferences(getBaseContext(), "", Const.eUsrType.UsrType_Fone, Const.projinfo.sSharePreferenceName),
				""};
		
		/*data = new ArrayList<String>();  
        for (i = 0; i < 3; i++) {  
            data.add(arr[i]+usrdata[i]);  
        }*/
		
		if(!Const.userinfo.get_login_status()){
			arr[4] = R.string.sett_login;
		}
        
        for(int i=0; i<arr.length; i++){
			 HashMap<String,Object> item = new HashMap<String,Object>();
			 //item.put("pic", null);
//			 item.put( "string", arr[i]/*+usrdata[i]*/);
			 if(usrdata[i].equals("") || usrdata[i].toLowerCase().equals("null")){
				 if( i == 1|| i == 2){
					 item.put( "string", getResources().getString(arr[i]) + " : " + getString(R.string.sett_n_verified));
				 } else if(i==4){
					 item.put( "string", getResources().getString(arr[i]) );
				 } else {
					 item.put( "string", getResources().getString(arr[i]) + " : " + getString(R.string.sett_n_set));
				 }
			 }else{
				 if( i == 1|| i == 2){
					 item.put( "string", getResources().getString(arr[i]) + " : " + getString(R.string.sett_verified));
				 } else {
					 item.put( "string", getResources().getString(arr[i]) + " : " + usrdata[i] );
				 }				 
			 }
			 list.add( item );
		}

		lilayoutPersonalPage = (LinearLayout) this.findViewById(R.id.lilayoutPersonalPage);
		//adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
		adapter = new SimpleAdapter(this, list, R.layout.personalinfo, new String[] {"string"},new int[] {R.id.personalinfoItemText} );
		listView = (ListView) this.findViewById(R.id.listView1);	
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				String tmpString = null;
				switch(arg2){
					case 0:
						tmpString = usrdata[0];
						if(tmpString.equals("") ||   tmpString.toLowerCase().equals("null")){
							openOptionsDialog(getResources().getString(arr[arg2])+":", usrdata, arg2);
//							openOptionsDialog(getResources().getString(R.string.sett_id_hint)+":", usrdata, arg2);
						}
						break;
					case 1:
						tmpString = usrdata[1];
						if(tmpString.equals("")||   tmpString.toLowerCase().equals("null")){
							if (Const.isNetworkConnected(context)){
								Intent intent = new Intent();
								intent.setClass(context, SignInWithSocial.class);
								intent.putExtra(SignInWithSocial.SOCIAL_TYPE, Const.eSocialCmd.Social_FB);
								intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, Const.eSocialCmd.Social_Personal);
								intent.putExtra(SignInWithSocial.SOCIAL_INOUT, Const.eSocialCmd.Social_Login);
								startActivity(intent);
								PersonalPage.this.finish();
							} else {
//								Toast.makeText(context, "You are offline!", Toast.LENGTH_LONG).show();
								Toast.makeText(context, getResources().getString(R.string.login_offline), Toast.LENGTH_LONG).show();
							}
							//openOptionsDialog(getResources().getString(arr[arg2])+":", usrdata, arg2);
						}
						break;
					case 2:
						tmpString = usrdata[2];
						if(tmpString.equals("")||   tmpString.toLowerCase().equals("null")){
							if (Const.isNetworkConnected(context)){
								//signInWithGplus();
								Intent intent = new Intent();
								intent.setClass(context, SignInWithSocial.class);
								intent.putExtra(SignInWithSocial.SOCIAL_TYPE, Const.eSocialCmd.Social_Gmail);
								intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, Const.eSocialCmd.Social_Personal);
								intent.putExtra(SignInWithSocial.SOCIAL_INOUT, Const.eSocialCmd.Social_Login);
								startActivity(intent);
								PersonalPage.this.finish();
							} else {
//								Toast.makeText(context, "You are offline!", Toast.LENGTH_LONG).show();
								Toast.makeText(context, getResources().getString(R.string.login_offline), Toast.LENGTH_LONG).show();
							}
							//openOptionsDialog(getResources().getString(arr[arg2])+":", usrdata, arg2);
						}
						break;
					
					case 3:
						tmpString = usrdata[3];;
//						openOptionsDialog(getResources().getString(arr[arg2])+":", usrdata, arg2);
						if(tmpString.equals("")||   tmpString.toLowerCase().equals("null")){
							Intent intent = new Intent();
							intent.setClass(context, PhoneInfoPage.class);
							startActivity(intent);
							//openOptionsDialog(getResources().getString(arr[arg2])+":", usrdata, arg2);
						}					

						break;
					case 4:
						//openOptionsDialog(getResources().getString(arr[arg2])+":", usrdata, arg2);
						if(arr[4]==R.string.sett_login){
							
						} else if(arr[4]==R.string.sett_logout){
//							String addstring = "Are you sure you want to log out?";
							String addstring = getResources().getString(R.string.sett_to_log_out);
							ReplyAdd_dialog.setTitle(getResources().getString(R.string.sett_log_out));
							ReplyAdd_dialog.setMessage(addstring);
							ReplyAdd_dialog.show();
						}
						break;	
				}
	
				//openOptionsDialog(listView.getItemAtPosition(arg2).toString(), arg2);
				//listView.setAdapter(adapter);
			}
		});
		
		PersonalInfoTextView = (TextView )findViewById(R.id.PersonalInfoTextView);
		PersonalInfoTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BELLB.TTF")); 
		
        Const.mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
        .addApi(Plus.API, new Plus.PlusOptions.Builder().build()) 
        .addScope(Plus.SCOPE_PLUS_LOGIN).build(); 
		
		ReplyAdd_dialog = new AlertDialog.Builder(this);
		ReplyAdd_dialog.setIcon(android.R.drawable.ic_dialog_alert);
		ReplyAdd_dialog.setCancelable(false);  
		ReplyAdd_dialog.setPositiveButton(getResources().getString(R.string.sett_OK), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {  
			Intent intent = new Intent();
			intent.setClass(context, SignInWithSocial.class);
			intent.putExtra(SignInWithSocial.SOCIAL_TYPE, "");
			intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, Const.eSocialCmd.Social_Personal);
			intent.putExtra(SignInWithSocial.SOCIAL_INOUT, Const.eSocialCmd.Social_Logout);
			startActivity(intent);
			PersonalPage.this.finish();
            }  
        }); 
		
		ReplyAdd_dialog.setNegativeButton(getResources().getString(R.string.sett_Cancel), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	// TODO Auto-generated method stub
	        	//Toast.makeText(PersonalPage.this, "Debug", Toast.LENGTH_SHORT).show();
	       	}
	    });
        
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
		//unregisterReceiver(mResponse);
		releaseViews();
		Log.i(TAG, "onDestory()............");
        super.onDestroy(); 
        return;
    }

    private void releaseViews() {
		if(listView != null) {
			listView.setBackground(null);
		}
		if(lilayoutPersonalPage != null) {
			lilayoutPersonalPage.setBackground(null);
		}
	}
		//@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}
			Const.mConnectionResult = result;
	}

	//@Override
	public void onConnected(@Nullable Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	
	
	private void openOptionsDialog(String xMessage, final String[] personalinfo, final int idx) {
	      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	      final EditText input = new EditText(this);
	      dialog.setTitle(xMessage);
	      if(!personalinfo[idx].equals("null")){
	    	  input.setText(personalinfo[idx]);
	      }
	      if(0 == idx){
	    	  input.setHint(R.string.sett_id_hint);
	      }
	      dialog.setView(input);	
		      dialog.setPositiveButton(getResources().getString(R.string.sett_OK), new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
            		boolean isSetOK = false;
              	    strInput = input.getText().toString();
            		//Toast.makeText(getApplicationContext(),strInput,Toast.LENGTH_SHORT).show();	            		
            		adapter.notifyDataSetChanged();	  
            		sfr.cmd = Const.Command.Cmd_UpdateUsrData;
				    sfr.usr_id = FileAccess.getStringFromPreferences(getBaseContext(), 
							null, Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
				    sfr.data = strInput;  
            		switch(idx){
	        			case 0:
	        				//ConnectToServer con = new ConnectToServer(context);
	        				//con.DoUpdateData(FileAccess.getStringFromPreferences(getBaseContext(), 
	    						//	null, Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName), Const.eUsrType.UsrType_SearchId, strInput);
	        				strInput = strInput.trim();
	        				if(!android.text.TextUtils.isDigitsOnly(strInput) && FileAccess.isStringorDigit(strInput)){
	        					sfr.datatype = Const.eUsrType.UsrType_SearchId;
	            				sfr.DoServerFunc();
	            				if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
	            			    	FileAccess.putStringInPreferences(getBaseContext(), strInput, 
	            			    			Const.eUsrType.UsrType_SearchId, Const.projinfo.sSharePreferenceName);
	            			    	isSetOK = true;
	            				} else if(sfr.GetServResult() == Const.RRet.RRET_USER_EXIST_FAIL){
//	            					Toast.makeText(getApplicationContext(),"Id has already been set by others!",Toast.LENGTH_SHORT).show();
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.sett_id_used),Toast.LENGTH_SHORT).show();
	            				}
	        				} else {
//	        					Toast.makeText(getApplicationContext(),"Wrong format",Toast.LENGTH_SHORT).show();
								Toast.makeText(getApplicationContext(),getResources().getString(R.string.sett_wrong_format), Toast.LENGTH_SHORT).show();
	        				}
	        				
	        				break;
	        			case 2:
	        				
	        				//sfr.datatype = Const.eUsrType.UsrType_Email;
	        				//sfr.DoServerFunc();
	        				//if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
	        					//edit.putString("name", strInput);
	        				//}
	        				
	        				//return;
	        				break;
	        			case 3:	
	        				/*
							 strInput = strInput.trim();
							 if(PhoneNumberUtils.isGlobalPhoneNumber(strInput)!=true){
								 Toast.makeText(getApplicationContext(),"Invalid Number!",Toast.LENGTH_SHORT).show();
							 } else {
								 if(strInput.indexOf("+")!=-1){
									 
								 } else {
									 //TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
									  String countryCode = Const.getlocalcode(context);
									  //String locale = context.getResources().getConfiguration().locale.getCountry(); 
								      //String countryCode = tm.getSimCountryIso();
								      System.out.println("....g3..."+countryCode);
								      if(countryCode.equals("tw") || strInput.substring(0, 1).equals("0")){
								    	  sfr.data = FileAccess.formatphone(strInput,"+886");
								      }							      
								 }
	            				sfr.datatype = Const.eUsrType.UsrType_Fone;
	            				sfr.DoServerFunc();
	            				if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
	            			    	FileAccess.putStringInPreferences(getBaseContext(), sfr.data, 
	            			    			Const.eUsrType.UsrType_Fone, Const.projinfo.sSharePreferenceName);
	            				} 
							 }		*/					 
	        				break;
            		} 		
            		if(isSetOK){
                		list.clear();
                        for(int iIdx=0; iIdx<arr.length; iIdx++){
                        	HashMap<String,Object> item = new HashMap<String,Object>();
                        	if(idx == iIdx){
                        		item.put( "string", getResources().getString(arr[iIdx]) + " : " + strInput);
                        	}else{
                        		item.put( "string", getResources().getString(arr[iIdx]) + " : " + personalinfo[iIdx]);
                        	}
                        	list.add( item );
               		    }
            		}
            		
	            }
            } );
		      dialog.setNegativeButton(getResources().getString(R.string.sett_Cancel), new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
	  	                  //openOptionsDialog("嚙踝蕭謕蕭嚙踝蕭謕�蕭嚙踐�蕭豲嚙踝蕭謕嚙踝蕭謕巨嚙踝蕭謕�蕭嚙踐��蕭嚙踐�蕭豯佗蕭��蕭謕蕭豲嚙踝蕭謕蕭豲");
	           }
	      });
	      dialog.show();
	   } //EOF openOptionsDialog


}