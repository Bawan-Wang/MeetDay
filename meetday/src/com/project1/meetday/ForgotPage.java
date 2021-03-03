package com.project1.meetday;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.ServerFuncRun;
public class ForgotPage extends Activity {
	private static ImageButton forgotbutton;
	private static TextView forgot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.forgot_page);
		
		forgotbutton = (ImageButton) findViewById(R.id.forgot_btn);
		forgotbutton.setOnClickListener(alert_show);
		/*forgotbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {		        
				Intent i = new Intent();
				i.setClass(ForgotPage.this, LoginPage.class);
				startActivity(i);
			}
        });*/
		
		forgot = (TextView) findViewById(R.id.forgot_useremail_edit);
		forgot.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BELLB.TTF")); 
		forgot.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s){
				// Abstract Method of TextWatcher Interface.
			}
		
			public void beforeTextChanged(CharSequence s,int start, int count, int after){
				// Abstract Method of TextWatcher Interface.
			}
			
			public void onTextChanged(CharSequence s,int start, int before, int count){
				if(forgot.getText().length() == 0){
					forgot.setBackgroundResource(R.drawable.box_email);
				}else{
					forgot.setBackgroundResource(R.drawable.box_select);
				}
			}
		});
		
	}
	
	private OnClickListener alert_show = new OnClickListener()
	{
	    @Override
	    public void onClick(View v) {
	        // TODO Auto-generated method stub
	        AlertDialog.Builder dialog = new AlertDialog.Builder(ForgotPage.this);
	        
	        ServerFuncRun sfr = new ServerFuncRun();
			sfr.cmd=Const.Command.Cmd_ForgetPwd;
	        sfr.name=forgot.getEditableText().toString();
	        sfr.DoServerFunc();
	        if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
				dialog.setTitle("Successful");
		        dialog.setMessage("your password already send to\n" +
		        		forgot.getText().toString());
	        }

	        dialog.setPositiveButton(R.string.ok_label,new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialoginterface, int i){
	            	Intent p = new Intent();
					p.setClass(ForgotPage.this, LoginPage.class);
					startActivity(p);
	            }
	        });
	        dialog.show();
	    }
	};
}
