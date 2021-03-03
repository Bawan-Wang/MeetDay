package com.project1.meetday;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.FBRelated;
import com.project1.http.FileAccess;
import com.project1.http.FileManager;
import com.project1.http.FileUtils;
import com.project1.http.PictUtil;
import com.project1.http.ServerFuncRun;

@SuppressLint("HandlerLeak")
public class LoginPage extends Activity {
	
	public static final String ACCOUNT =
		    "com.project1.account";
	public static final String PASSWD =
		    "com.project1.passwd";
	public static final String REDIRECT =
		    "com.project1.redirect";
	public static final String LOGINTYPE =
		    "com.project1.type";
	public static final String LOGINDATA =
		    "com.project1.data";
	
	private static ImageButton logbutton;
	private static EditText login_email, login_pwd;
	private static TextView forgot;
	private static SpannableString spannableString;
	//private static final String Default = "N/A";
	private Context context;
	private String account,passwd,redirect,type,data;
	private DBHelper dbhelper;
	//private SQLiteDatabase dbrw;
	boolean status;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		this.context = getBaseContext();
		final DBHelper dbhelper = new DBHelper(context);
		
		
		Intent intent = getIntent();
		redirect = intent.getStringExtra(REDIRECT);
		//String logintype = FileAccess.getStringFromPreferences(context, "", 
			//	Const.eUsrType.UsrType_LoginType, Const.projinfo.sSharePreferenceName);
		
		if(redirect.equals(Const.eSocialCmd.Social_Login)){		
			//Intent intent = getIntent();
			account = intent.getStringExtra(ACCOUNT);
			passwd = intent.getStringExtra(PASSWD);
			type = intent.getStringExtra(LOGINTYPE);
			data = intent.getStringExtra(LOGINDATA);
			//Log.e(this.getClass().getName(), "login: " + passwd+data);
			ServerFuncRun sfr = new ServerFuncRun();							
			sfr.cmd = Const.Command.Cmd_Login_Type;
			//sfr.name = account;
			sfr.pass = passwd;
			sfr.datatype = Const.eUsrType.valueOf(type);
			sfr.data = data;
			loginInBackground(sfr, dbhelper);	
		} else {
			setContentView(R.layout.login_page);
			login_email = (EditText) findViewById(R.id.login_useremail_edit);
			login_pwd = (EditText) findViewById(R.id.login_userpassword_edit);
				
			logbutton = (ImageButton) findViewById(R.id.login_gotomainpage_btn);
	        logbutton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					 //Thread.sleep(3000);
					ServerFuncRun sfr = new ServerFuncRun();
			        if(login_email != null && login_pwd !=null){								
						sfr.cmd = Const.Command.Cmd_Login;
						sfr.name = login_email.getEditableText().toString();
						sfr.pass = login_pwd.getEditableText().toString();
						loginInBackground(sfr, dbhelper);				
						//status = sfr.GetLoginStatus();
			        }
					
				}
	        });
	        
	      //make "color" (characters 17 to stringLength) display a toast message when touched
	        ClickableSpan clickableSpan = new ClickableSpan() {
	 
	        	 @Override
	         	public void updateDrawState(TextPaint ds) {
	         	    //ds.setColor(ds.linkColor);
	         	    ds.setColor(Color.WHITE);
	         	    ds.setUnderlineText(false);
	         	}
	            
	            @Override
	            public void onClick(View view) {
	            	Intent i = new Intent();
					i.setClass(LoginPage.this, ForgotPage.class);
					startActivity(i);
	            }
	        };
	        
	        forgot = (TextView) findViewById(R.id.forgot_link);
	        forgot.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 

	        Resources res = context.getResources();
	        Configuration conf = res.getConfiguration();
	        String language = conf.locale.getLanguage();
	        int wrodlength = 0;
	        if(language.equals("en")){
	        	wrodlength = 17;
	        }else if(language.equals("zh")){
	        	wrodlength = 5;
	        }        
	        
	        spannableString=new SpannableString(forgot.getText().toString());
	        spannableString.setSpan(clickableSpan, wrodlength, forgot.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //click event
	        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), wrodlength, forgot.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //���??��??
	        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), wrodlength, forgot.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //??��?�蕭蹎改?����僥��?��?��
	        
	        forgot.setText(spannableString);
	        forgot.setMovementMethod(LinkMovementMethod.getInstance());
	        

	        login_email.addTextChangedListener(new TextWatcher(){
				public void afterTextChanged(Editable s){
					// Abstract Method of TextWatcher Interface.
				}
			
				public void beforeTextChanged(CharSequence s,int start, int count, int after){
					// Abstract Method of TextWatcher Interface.
				}
				
				public void onTextChanged(CharSequence s,int start, int before, int count){
					if(login_email.getText().length() == 0){
						login_email.setBackgroundResource(R.drawable.box_email);
					}else{
						login_email.setBackgroundResource(R.drawable.box_select);
					}
				}
			});
	        
	        login_pwd.addTextChangedListener(new TextWatcher(){
				public void afterTextChanged(Editable s){
					// Abstract Method of TextWatcher Interface.
				}
			
				public void beforeTextChanged(CharSequence s,int start, int count, int after){
					// Abstract Method of TextWatcher Interface.
				}
				
				public void onTextChanged(CharSequence s,int start, int before, int count){
					if(login_pwd.getText().length() == 0){
						login_pwd.setBackgroundResource(R.drawable.box_password);
					}else{
						login_pwd.setBackgroundResource(R.drawable.box_select);
					}
				}
			});
		}
	}

	@Override
	public void onDestroy() {
		Log.d(this.getClass().getName(),"On onDestroy");		
		super.onDestroy();	
	}
	
	private Const.RRet sendRegIdtoServ(String uid){
//		if(Const.regid.length() != 162)
		String local_regid = FileAccess.getStringFromPreferences(context, 
				"", Const.eUsrType.UsrType_RegId, Const.projinfo.sSharePreferenceName);
		if(local_regid.length() < 100 || local_regid.length() > 162)
			return Const.RRet.RRET_FAIL;		 
		ServerFuncRun sfr = new ServerFuncRun();		
		
		sfr.cmd=Const.Command.Cmd_UpdateUsrData;
		sfr.usr_id = uid;
        sfr.datatype = Const.eUsrType.UsrType_RegId;
        sfr.data = local_regid;
        //sfr.context = LoginPage.this;
        sfr.DoServerFunc();
        return sfr.GetServResult();
	}	
	
	private void setUsrInfofromServ(String uid){	
		ConnectToServer con = new ConnectToServer(context);
		con.DoGetUsrInfofromServ(uid);	
	}
	
/* Login return data
 * data[0]: friendlist id
 * data[1]: user id
 * data[2]: fbid
 * data[3]: gmail
 */
	private void loginInBackground(final ServerFuncRun sfr, final DBHelper dbhelper) {
		new AsyncTask<Void, Void, String>() {
		private ProgressDialog pdialog;
		//private ServerFuncRun sfr = new ServerFuncRun();
		boolean loginOK = false;
        protected void onPreExecute() {
        	pdialog = ProgressDialog.show(LoginPage.this, "Logging In", "Wait...",true);				
        }
		@Override
		protected String doInBackground(Void... params) {
			String msg = "";
			try {				
				if(Looper.myLooper() == null)
					Looper.prepare();
				
		       // Toast.makeText(getApplicationContext(), 
        		//"Email   :" + sfr.name + "\r\n" +
        		//"Password:" + sfr.pass + "\r\n", 
        		//Toast.LENGTH_SHORT).show();
		        //Log.d(this.getClass().getName(), "sdvdsvdv");				        
		        sfr.DoServerFunc();	
		        while(sfr.GetServResult() == null)
					;
		        
		        //Looper.loop();
				if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
					loginOK = true;
					FileUtils fileUtils = new FileUtils(context);
					fileUtils.deleteFile();
		        	String get = sfr.GetUserData() + "&";
			        String[] data = FileAccess.parseDataToPara(get, "&");
			        //Const.userinfo.flst = data[0];
					//Const.userinfo.uid = data[1];
					if(sendRegIdtoServ(data[1]) == Const.RRet.RRET_SUCCESS){
			        	FileAccess.putStringInPreferences(context, sfr.data, 
			        			Const.eUsrType.UsrType_Email, Const.projinfo.sSharePreferenceName);
			        	FileAccess.putStringInPreferences(context, sfr.pass, 
			        			Const.eUsrType.UsrType_Pass, Const.projinfo.sSharePreferenceName);
			        	String usr_id = FileAccess.getStringFromPreferences(context, "", 
			        			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
			        	//Const.userinfo.name = login_email.getEditableText().toString();
			        	Const.userinfo.set_login_status(true);
			        	PictUtil.deleteFile(FileManager.getSaveFilePath());
						//if(usr_id.compareTo(data[1])!= 0){	  
							FileAccess.putStringInPreferences(context, data[1], 
					    			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
					    	FileAccess.putStringInPreferences(context, data[0], 
					    			Const.eUsrType.UsrType_FreindList, Const.projinfo.sSharePreferenceName);
			        		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
			        		dbhelper.onDropTable(dbrw, Const.hisinfo.getTableName());
							dbhelper.onCreateTable(dbrw, Const.hisinfo.getTableName());
							if(Const.hisinfo.ladapter != null){
								Const.hisinfo.ladapter = null;
							}
							//dbhelper.onDropTable(dbrw, Const.contactinfo.getTableName());
							//dbhelper.onCreateTable(dbrw, Const.contactinfo.getTableName());	
			        		setUsrInfofromServ(data[1]);				
							//dbhelper.onDropTable(dbrw, Const.giftcarddb);
							//dbhelper.onCreateTable(dbrw, Const.giftcarddb);
			        		FileAccess.getContactphoneList(context, getContentResolver());
			        	//}	
						
			        	if(MainPage.Mainact != null)
			        		MainPage.Mainact.finish();
			            Intent i = new Intent();
					    i.setClass(LoginPage.this, MainPage.class);
					    i.putExtra(MainPage.MAINPAGE_LOCATION, 
					    		String.valueOf(Const.eLocation.Location_Contact));
					    startActivity(i);
					    //LoginPage.this.finish();
					} else {
						Const.userinfo.set_login_status(false);
						Log.d("LoginActivity", "Error: ");
					}
			    } else {
			    	Const.userinfo.set_login_status(false);
			    }
				//storeRegistrationId(context, regId);
			} catch (Exception ex) {
				msg = "Error :" + ex.getMessage();
				Log.d(this.getClass().getName(), "Error: " + msg);
			}
			Log.d(this.getClass().getName(), "AsyncTask completed: " + msg);
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {
			pdialog.dismiss();
			if(loginOK == false){
	        	//pdialog.dismiss();
	        	AlertDialog.Builder dialog = new AlertDialog.Builder(LoginPage.this);
		        dialog.setTitle("Login Failed!!!");
		        dialog.setMessage("your account name or password " +
		        		"may have been entered incorrectly");
		        dialog.setPositiveButton(R.string.ok_label,new DialogInterface.OnClickListener(){
		            public void onClick(DialogInterface dialoginterface, int i){
		            }
		        });
		        dialog.show();		
			} else {
				LoginPage.this.finish();
			}			
		}
		}.execute(null, null, null);
	}
	
}
