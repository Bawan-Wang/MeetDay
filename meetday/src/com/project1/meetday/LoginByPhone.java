package com.project1.meetday;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project1.http.Const;
import com.project1.http.FileAccess;
import com.project1.http.ServerFuncRun;

public class LoginByPhone extends Activity {

//	TextView txtCountryNumber;
	Context context;
	private RelativeLayout relayoutPhoneLogin;
	private TextView txtCountry;
	private String strCountryCode, strPhoneNum;
	private static EditText countryName, login_phone, login_pwd;
	private String phone;
	Button login,signup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_login);
		
		TelephonyManager telManager = (TelephonyManager)getSystemService(LoginByPhone.TELEPHONY_SERVICE);
		final String strCountryIso = telManager.getNetworkCountryIso();
		context = this;

		relayoutPhoneLogin = (RelativeLayout) findViewById(R.id.relayoutPhoneLogin);
		login_phone = (EditText) findViewById(R.id.phone_edit);
		login_pwd = (EditText) findViewById(R.id.password_edit);

		countryName = (EditText) findViewById(R.id.country_name);
		if(strCountryIso.equals("tw")){
			strCountryCode = "+886";
			countryName.setText("Taiwan" + " (" + strCountryCode + ")");
//			login_phone.setHint(strCountryCode);
		}

		login = (Button) findViewById(R.id.login_btn);
		login.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//ConnectToServer con = new ConnectToServer(context);
				 phone = login_phone.getEditableText().toString().trim();
				 if(PhoneNumberUtils.isGlobalPhoneNumber(phone)!=true){
//					 Toast.makeText(getApplicationContext(),"Invalid Number!",Toast.LENGTH_SHORT).show();
					 Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_no_invalid_number),Toast.LENGTH_SHORT).show();
				 } else {
					 phone = FileAccess.formatphone(phone,"+886");
					ServerFuncRun sfr = new ServerFuncRun();
					sfr.cmd = Const.Command.Cmd_Login_Type;
					sfr.pass =login_pwd.getEditableText().toString();
					sfr.datatype = Const.eUsrType.UsrType_Fone;
					sfr.data = phone;//login_phone.getEditableText().toString();
					ServerInBackground(sfr);
				 } 
			}
        });
		
		signup = (Button) findViewById(R.id.sign_up_btn);
		signup.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//ConnectToServer con = new ConnectToServer(context);
				 phone = login_phone.getEditableText().toString().trim();
				 if(PhoneNumberUtils.isGlobalPhoneNumber(phone)!=true){
//					 Toast.makeText(getApplicationContext(),"Invalid Number!",Toast.LENGTH_SHORT).show();
					 Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_no_invalid_number),Toast.LENGTH_SHORT).show();
				 } else {
					 phone = FileAccess.formatphone(phone,"+886");
					ServerFuncRun sfr = new ServerFuncRun();
					sfr.cmd = Const.Command.Cmd_DoSearch;//Const.Command.Cmd_Register_Type;
					//sfr.pass =login_pwd.getEditableText().toString();
					sfr.datatype = Const.eUsrType.UsrType_Fone;
					sfr.data = phone;//login_phone.getEditableText().toString();
					ServerInBackground(sfr);
				 } 
			}
        });

	}

	public void onDestroy() {
		releaseViews();
		super.onDestroy();
	}

	private void releaseViews() {
		if(countryName != null) {
			countryName.setBackground(null);
		}
		if(login_phone != null) {
			login_phone.setBackground(null);
		}
		if(login_pwd != null) {
			login_pwd.setBackground(null);
		}
		if(signup != null) {
			signup.setBackground(null);
		}
		if(login != null) {
			login.setBackground(null);
		}
		if(relayoutPhoneLogin != null) {
			relayoutPhoneLogin.setBackground(null);
		}
	}

	private void ServerInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog pdialog;
			//ServerFuncRun sf = new ServerFuncRun();
	        protected void onPreExecute() {
	        	pdialog = ProgressDialog.show(LoginByPhone.this, "Connecting", "Wait...",true);				
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
					
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("ServerInBackground", "Error: " + msg);
				}
				Log.d("ServerInBackground", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//Toast.makeText(getApplicationContext(),
					//	"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						//.show();
				pdialog.dismiss();				
				Intent intent = new Intent();				
				if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){	
					if(Const.Command.Cmd_Login_Type == sfr.cmd){   
						FileAccess.putStringInPreferences(context, 
								phone, Const.eUsrType.UsrType_Fone, 
								Const.projinfo.sSharePreferenceName);
				        intent.setClass(LoginByPhone.this, LoginPage.class);
				        intent.putExtra(LoginPage.ACCOUNT, phone);
				        intent.putExtra(LoginPage.PASSWD, login_pwd.getEditableText().toString());
				        intent.putExtra(LoginPage.REDIRECT, Const.eSocialCmd.Social_Login);
				        intent.putExtra(LoginPage.LOGINTYPE, String.valueOf(Const.eUsrType.UsrType_Fone));
				        intent.putExtra(LoginPage.LOGINDATA, phone);
						//startActivity(intent);
						//LoginByPhone.this.finish();		
				        StartPage.Mainact.finish();
				        LoginByPhone.this.finish();
						startActivity(intent);
					} else if(Const.Command.Cmd_DoSearch == sfr.cmd){
						Toast.makeText(context, "User Already Exist, Try again!", Toast.LENGTH_LONG).show();
					}
				} else if(Const.RRet.RRET_USER_EXIST_FAIL == sfr.GetServResult()){
					Toast.makeText(context, "User Already Exist, Try again!", Toast.LENGTH_SHORT).show();
				} else if(Const.RRet.RRET_WRONG_PASSWORD_FAIL == sfr.GetServResult()){
					Toast.makeText(context, "Wrong account or password!", Toast.LENGTH_SHORT).show();
				} else if(Const.RRet.RRET_DOSEARCH_FAIL == sfr.GetServResult()){
					if(/*Const.Command.Cmd_Register_Type*/Const.Command.Cmd_DoSearch == sfr.cmd){
						//String uid = sfr.GetUserData();
						String authnum = Const.getAuthNum();
						ConnectToServer con = new ConnectToServer(context);
						con.DoSendSMS(phone, "Meetday Code:"+authnum);
						Log.e("CCCCC",authnum);
						intent.setClass(LoginByPhone.this, SMSAuthPage.class);	
						intent.putExtra(SMSAuthPage.AUTHNUM, authnum);
						intent.putExtra(SMSAuthPage.ACCOUNT, phone);
						intent.putExtra(SMSAuthPage.PASSWD, login_pwd.getEditableText().toString());		
						//intent.putExtra(SMSAuthPage.USERID, "0");
				        LoginByPhone.this.finish();
						startActivity(intent);
					}	
				}
			}
		}.execute(null, null, null);
	}
	
}
