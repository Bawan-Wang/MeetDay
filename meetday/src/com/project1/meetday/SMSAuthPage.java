package com.project1.meetday;

import java.util.ArrayList;
import java.util.List;

import com.project1.http.Const;
import com.project1.http.FileAccess;
import com.project1.http.ImageLoader;
import com.project1.http.PictUtil;
import com.project1.http.ServerFuncRun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SMSAuthPage extends Activity{
	public static final String USERID =
		    "com.project1.userid";
	public static final String ACCOUNT =
		    "com.project1.account";
	public static final String AUTHNUM =
		    "com.project1.authnum";
	public static final String PASSWD =
		    "com.project1.passwd";
	
	private static ImageButton gotomainpage;
	private static EditText signup_nickname;
	private static ImageView iv = null;
	private static Intent intent;
	
	private final static int PICK_FROM_GALLERY=0x123; 
	private final static int PICK_FROM_GET=0x124; 
	
	private String userid, account,authnum,passwd;
	private Context context;
	private boolean photosaved = false;
	private String TAG = "SMSAuthPage";
	private static EditText auth_num;
	private TextView mTextView;
	private Button submit;
	private String type_code;
	private CountDownTimer counter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.smsauth);
		
		auth_num = (EditText) findViewById(R.id.auth_edit);
        Intent i = getIntent();
		userid = i.getStringExtra(USERID);
		account = i.getStringExtra(ACCOUNT);
		authnum = i.getStringExtra(AUTHNUM);
		passwd = i.getStringExtra(PASSWD);
		this.context = getApplicationContext();
		
		
		mTextView = (TextView)findViewById(R.id.countdown_clock);

		counter = new CountDownTimer(90000,1000){

            @Override
            public void onFinish() {
                mTextView.setText("Time's Up!");
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                SMSAuthPage.this.finish();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                mTextView.setText("seconds remaining:"+millisUntilFinished/1000);
            }

        };
        counter.start();
		
        submit = (Button) findViewById(R.id.submit_btn);
        submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//ConnectToServer con = new ConnectToServer(context);
				type_code = auth_num.getEditableText().toString().trim();
				if(type_code.equals(authnum)){
					if(userid!=null){
						redirecttopage(false);
					} else {
						redirecttopage(true);
					}
					Toast.makeText(context, "Correct Code!!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "Wrong Code, Please Try Again!!", Toast.LENGTH_LONG).show();
				}
				counter.cancel();
				SMSAuthPage.this.finish();
			}
        });        
	}
	
	private void redirecttopage(boolean isRegister){
		Intent intent = new Intent();
		ServerFuncRun sfr = new ServerFuncRun();
		if(isRegister){		
			sfr.cmd = Const.Command.Cmd_Register_Type;
			sfr.pass = passwd;
		} else {
			//ConnectToServer con = new ConnectToServer(context);
			//con.DoUpdateData(userid, Const.eUsrType.UsrType_Fone, account);
			sfr.cmd = Const.Command.Cmd_UpdateUsrData;
			sfr.usr_id = userid;
		}	
		sfr.datatype = Const.eUsrType.UsrType_Fone;
		sfr.data = account;//login_phone.getEditableText().toString();
		ServerInBackground(sfr);
	}
	
	private void ServerInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog pdialog;
	        protected void onPreExecute() {
	        	//pdialog = ProgressDialog.show(SMSAuthPage.this, "Connecting", "Wait...",true);				
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
				//pdialog.dismiss();				
				Intent intent = new Intent();				
				if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){
					FileAccess.putStringInPreferences(context, 
							account, Const.eUsrType.UsrType_Fone, 
							Const.projinfo.sSharePreferenceName);	
					if(Const.Command.Cmd_Register_Type == sfr.cmd){
						String uid = sfr.GetUserData();
						intent.setClass(SMSAuthPage.this, SignUpPage.class);	
						intent.putExtra(SignUpPage.PHOTOURL, "");
						intent.putExtra(SignUpPage.USERID, uid);
						intent.putExtra(SignUpPage.NICKNAME, "");
						intent.putExtra(SignUpPage.ACCOUNT, account);
						intent.putExtra(SignUpPage.PASSWD, passwd);		
						intent.putExtra(SignUpPage.LOGINTYPE, String.valueOf(Const.eUsrType.UsrType_Fone));
						intent.putExtra(SignUpPage.LOGINDATA, account);
						if(StartPage.Mainact!=null){
							StartPage.Mainact.finish();
						}
					} else if(Const.Command.Cmd_UpdateUsrData == sfr.cmd){
						if(PersonalPage.Mainact!=null){
							PersonalPage.Mainact.finish();
						}
						intent = new Intent(context, PersonalPage.class);
					}
					//StartPage.Mainact.finish();
					//SMSAuthPage.this.finish();
					startActivity(intent);
				} else if(Const.RRet.RRET_USER_EXIST_FAIL == sfr.GetServResult()){
					Toast.makeText(context, "User Already Exist, Try again!", Toast.LENGTH_SHORT).show();
				} else if(Const.RRet.RRET_WRONG_PASSWORD_FAIL == sfr.GetServResult()){
					Toast.makeText(context, "Wrong account or password!", Toast.LENGTH_SHORT).show();
				} 
			}
		}.execute(null, null, null);
	}
}
