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

public class PhoneInfoPage extends Activity {
	TextView txtCountry;
	TextView txtCountryNumber;
	Context context;
	private RelativeLayout relayoutPhoneInfo;
	private static EditText countryName, login_phone, login_pwd;
	private String strCountryCode, phone, pass;
	Button set;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phoneinfo_setting);
		
		context = this;

		login_phone = (EditText) findViewById(R.id.phone_edit_in_phoneset);
		login_pwd = (EditText) findViewById(R.id.password_edit_in_phoneset);
		
		TelephonyManager telManager = (TelephonyManager)getSystemService(LoginByPhone.TELEPHONY_SERVICE);
		final String strCountryIso = telManager.getNetworkCountryIso();

		relayoutPhoneInfo = (RelativeLayout) findViewById(R.id.relayoutPhoneInfo);

		set = (Button) findViewById(R.id.login_btn);
		set.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//ConnectToServer con = new ConnectToServer(context);
				 phone = login_phone.getEditableText().toString().trim();
				 pass = login_pwd.getEditableText().toString().trim();
				 if(PhoneNumberUtils.isGlobalPhoneNumber(phone)!=true){
					 Toast.makeText(getApplicationContext(),"Invalid Number!",Toast.LENGTH_SHORT).show();
				 } else {
					 phone = FileAccess.formatphone(phone,"+886");
					 ServerFuncRun sfr = new ServerFuncRun();
	            	 sfr.cmd = Const.Command.Cmd_DoSearch;
 				     //sfr.usr_id = FileAccess.getStringFromPreferences(getBaseContext(), 
 							//null, Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
 				     sfr.data = phone;  
 				     sfr.datatype = Const.eUsrType.UsrType_Fone;
 				     ServerInBackground(sfr);
				 } 				
			}
        });

		countryName = (EditText) findViewById(R.id.country_name_in_phoneset);
//		txtCountryNumber = (TextView) findViewById(R.id.phone_in_phoneset);
		if(strCountryIso.equals("tw")){
//			txtCountry.setText("Taiwan");
//			txtCountryNumber.setText("+886");
			strCountryCode = "+886";
			countryName.setText("Taiwan" + " (" + strCountryCode + ")");
		}
	}	

	@Override
	public void onDestroy() {
		releaseViews();
		super.onDestroy();;
	}

	private void releaseViews() {
		if(login_phone != null) {
			login_phone.setBackground(null);
		}
		if(login_pwd != null) {
			login_pwd.setBackground(null);
		}
		if(countryName != null) {
			countryName.setBackground(null);
		}
		if(set != null){
			set.setBackground(null);
		}
		if(relayoutPhoneInfo != null) {
			relayoutPhoneInfo.setBackground(null);
		}
	}

	private void ServerInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog pdialog;
			//ServerFuncRun sf = new ServerFuncRun();
	        protected void onPreExecute() {
	        	pdialog = ProgressDialog.show(PhoneInfoPage.this, "Connecting", "Wait...",true);				
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
				if(Const.RRet.RRET_DOSEARCH_FAIL == sfr.GetServResult()){
					/*ConnectToServer con = new ConnectToServer(context);
					con.DoUpdateData(FileAccess.getStringFromPreferences(getBaseContext(), 
 							null, Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName), Const.eUsrType.UsrType_Fone, phone);
					con.DoUpdateData(FileAccess.getStringFromPreferences(getBaseContext(), 
 							null, Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName), Const.eUsrType.UsrType_Pass, pass);
					FileAccess.putStringInPreferences(context, 
							phone, Const.eUsrType.UsrType_Fone, 
							Const.projinfo.sSharePreferenceName);	
					FileAccess.putStringInPreferences(context, 
							pass, Const.eUsrType.UsrType_Pass, 
							Const.projinfo.sSharePreferenceName);
					Toast.makeText(context, "Set data success!", Toast.LENGTH_SHORT).show();					
					PersonalPage.Mainact.finish();
					Intent intent = new Intent();
					intent.setClass(context, PersonalPage.class);
					*/
					Intent intent = new Intent();
					String authnum = Const.getAuthNum();
					ConnectToServer con = new ConnectToServer(context);
					con.DoSendSMS(phone, "Meetday Code:"+authnum);
					Log.e("CCCCC",authnum);
					intent.setClass(context, SMSAuthPage.class);	
					//intent.putExtra(SMSAuthPage.PHOTOURL, "");
					intent.putExtra(SMSAuthPage.USERID, FileAccess.getStringFromPreferences(getBaseContext(), 
							null, Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName));
					//intent.putExtra(SMSAuthPage.NICKNAME, "");
					intent.putExtra(SMSAuthPage.AUTHNUM, authnum);
					intent.putExtra(SMSAuthPage.ACCOUNT, phone);
					intent.putExtra(SMSAuthPage.PASSWD, login_pwd.getEditableText().toString());		
					//intent.putExtra(SMSAuthPage.LOGINTYPE, String.valueOf(Const.eUsrType.UsrType_Fone));
					//intent.putExtra(SMSAuthPage.LOGINDATA, phone);
					startActivity(intent);
					PhoneInfoPage.this.finish();
				} else if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){
					Toast.makeText(context, "User Already Exist, Try again!", Toast.LENGTH_SHORT).show();
				} 
			}
		}.execute(null, null, null);
	}
}
