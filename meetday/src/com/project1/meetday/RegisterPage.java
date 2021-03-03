package com.project1.meetday;

import com.project1.http.Const;
import com.project1.http.FileAccess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RegisterPage extends Activity {
	private static ImageButton Registerbutton;
	private static EditText login_email, login_pwd, login_checkpwd, login_phone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.register_page);
		
		//String mystring = settingsActivity.getString("mystring", "");
		login_email = (EditText) findViewById(R.id.register_useremail_edit);
		login_pwd = (EditText) findViewById(R.id.register_userpassword_edit);
		login_checkpwd = (EditText) findViewById(R.id.register_checkuserpassword_edit);
		
		
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
		
		login_checkpwd.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s){
				// Abstract Method of TextWatcher Interface.
			}
		
			public void beforeTextChanged(CharSequence s,int start, int count, int after){
				// Abstract Method of TextWatcher Interface.
			}
			
			public void onTextChanged(CharSequence s,int start, int before, int count){
				if(login_checkpwd.getText().length() == 0){
					login_checkpwd.setBackgroundResource(R.drawable.box_check);
				}else{
					login_checkpwd.setBackgroundResource(R.drawable.box_select);
				}
			}
		});
		
		Registerbutton = (ImageButton) findViewById(R.id.register_gotomainpage_btn);
		Registerbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
										
		        Toast.makeText(getApplicationContext(), 
        		//"Name    :" + login_firstname.getText().toString() + login_lastname.getText().toString() + "\r\n" +
        		"Email   :" + login_email.getText().toString() + "\r\n" +
        		"Password:" + login_pwd.getText().toString() + "\r\n" +
        		"CheckPassword:" + login_checkpwd.getText().toString(),
        		Toast.LENGTH_SHORT).show();
		          
		        String pwdstring = login_pwd.getText().toString();
		        String checkpwdstring = login_checkpwd.getText().toString();
		        
		        if(pwdstring.compareTo(checkpwdstring)==0){
					if(login_email != null && login_pwd !=null){
						ConnectToServer con = new ConnectToServer(getBaseContext());
						con.DoRegister(login_email.getEditableText().toString(), 
								login_pwd.getEditableText().toString());					
					}
		        	
					
					while(Const.lastcmdret.equals("")){
						;
					}
					Log.e("REG", "lastcmdret: " + Const.lastcmdret);
					String[] data = FileAccess.parseDataToPara(Const.lastcmdret, "%");
					if(data[0].equals(Const.Command.Cmd_Register.toString())&&data[1].equals(Const.RRet.RRET_SUCCESS.toString())){
						Intent i = new Intent();
						i.setClass(RegisterPage.this, SignUpPage.class);
						//con.DoGetPhoto(personPhotoUrl);
						i.putExtra(SignUpPage.USERID, data[2]);
						i.putExtra(SignUpPage.NICKNAME, login_email.getEditableText().toString());
						startActivity(i);	
						RegisterPage.this.finish();
					} else if(data[0].equals(Const.Command.Cmd_Register.toString())&&data[1].equals(Const.RRet.RRET_USER_EXIST_FAIL.toString())){
						AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterPage.this);
				        dialog.setTitle("Register Failed!!!");
				        dialog.setMessage("User Exist or Format Fail");
				        dialog.setPositiveButton(R.string.ok_label,new DialogInterface.OnClickListener(){
				            public void onClick(DialogInterface dialoginterface, int i){
				            }
				        });
				        dialog.show();
					}
					Const.lastcmdret = "";
				
		        }else{
		        	AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterPage.this);
			        dialog.setTitle("Register Failed!!!");
			        dialog.setMessage("your password entered incorrectly");
			        dialog.setPositiveButton(R.string.ok_label,new DialogInterface.OnClickListener(){
			            public void onClick(DialogInterface dialoginterface, int i){
			            }
			        });
			        dialog.show();
		        }
			}
        });
	}
}
