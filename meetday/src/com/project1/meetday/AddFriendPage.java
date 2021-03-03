package com.project1.meetday;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.FileAccess;
import com.project1.http.ServerFuncRun;
//import java.util.HashMap;
//import com.example.http.FileAccess;
//import android.widget.SimpleAdapter;
//import android.widget.TextView;

public class AddFriendPage extends Activity {
	private static ImageView searchButton;
	private static ImageView inviteButton;
	private static Button sendbutton;
	AlertDialog.Builder ReplyAdd_dialog;
	AlertDialog.Builder NotFind_dialog;
	
	ListView listView;
	private EditText et;
	
	int textlength=0;
	int addmode=0;
	boolean status = false;
	private ArrayList<String> array_sort= new ArrayList<String>();
	//private ServerFuncRun sfr = new ServerFuncRun();
	private ArrayList<String> contactsListName, contactsListID;
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	private SimpleAdapter adapter;
	private Intent intent;
	private Context context;
	Uri contactsUri;
	String SendEmail;
	private String addid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.addfriend_page);
		this.context = getBaseContext();
		contactsUri=ContactsContract.Contacts.CONTENT_URI;

		Cursor curContacts=getContentResolver().query(contactsUri,null, null, null, null);

        //declare a ArrayList object to store the data that will present to the user
		contactsListName=new ArrayList<String>();
		contactsListID=new ArrayList<String>();

        if(curContacts.getCount()>0){
            while(curContacts.moveToNext()){
            	String contactId = curContacts.getString(curContacts.getColumnIndex(ContactsContract.Contacts._ID));
            	String contactName = curContacts.getString(curContacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            	contactsListName.add(contactName);
            	contactsListID.add(contactId);
            	
            	HashMap<String,Object> item = new HashMap<String,Object>();
   			 	item.put("pic", R.drawable.person);
   			 	item.put( "string", contactName);
   			 	list.add( item );
                //Log.v("uri", "(" + contactId + "): "+contactName );
            }
        }
        curContacts.close(); 

		
		searchButton = (ImageView) findViewById(R.id.Search_button);
		inviteButton = (ImageView) findViewById(R.id.Invite_button);
		listView = (ListView) findViewById(R.id.listView_contact);
		et = (EditText) findViewById(R.id.editText_contact);
		sendbutton = (Button) findViewById(R.id.sendButton);
		
		searchButton.setEnabled(true);
		inviteButton.setEnabled(false);
		listView.setVisibility(View.VISIBLE);
		addmode=0;
		adapter = new SimpleAdapter(this, list, R.layout.friendlist_page, new String[] { "pic","string"},new int[] { R.id.FriendImg, R.id.FriendName} );
		//adapter = new ArrayAdapter<String>(R.layout.friendlist_page,contactsListName);
		listView.setAdapter(adapter);

		inviteButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				searchButton.setEnabled(true);
				inviteButton.setEnabled(false);
				listView.setVisibility(View.VISIBLE);
				addmode=0;
				et.setText("");
				listView.setAdapter(adapter);
			}
		});
		
		searchButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				searchButton.setEnabled(false);
				inviteButton.setEnabled(true);
				listView.setVisibility(View.INVISIBLE);
				addmode=1;
			}
		});
		
		et.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s){
		    // Abstract Method of TextWatcher Interface.
			}
		
			public void beforeTextChanged(CharSequence s,int start, int count, int after){
				// Abstract Method of TextWatcher Interface.
			}
			
			public void onTextChanged(CharSequence s,int start, int before, int count){
				if(addmode==0){
					textlength = et.getText().length();
					array_sort.clear();
					list.clear();
					for (int i=0; i < contactsListName.size(); i++){
					    if (textlength <= contactsListName.get(i).length()){
					    	if(et.getText().toString().equalsIgnoreCase(
							  (String)contactsListName.get(i).subSequence(0,textlength))){
								array_sort.add(contactsListName.get(i));
								
								HashMap<String,Object> item = new HashMap<String,Object>();
				   			 	item.put("pic", R.drawable.person);
				   			 	item.put( "string", contactsListName.get(i));
				   			 	list.add( item );		
				   			//Log.d(this.getClass().getName(),"Add: "+contactsListName.get(i)+"!!!!");
							}
					    }
					}

				listView.setAdapter(adapter/*new ArrayAdapter<String>(AddFriendPage.this,android.R.layout.simple_list_item_1, array_sort)*/);
				}
			}
		});
		
		if(addmode==0){
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					et.setText(list.get(arg2).get("string").toString());				
				}
			
			});
		}
		
		ReplyAdd_dialog = new AlertDialog.Builder(this);
		ReplyAdd_dialog.setIcon(android.R.drawable.ic_dialog_alert);
		ReplyAdd_dialog.setCancelable(false);  
		ReplyAdd_dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {  
        public void onClick(DialogInterface dialog, int which) {  
        	 do_add_friend(((Dialog) dialog).getContext(), addid);
             //Toast.makeText(AddFriendPage.this, "Add User OK!", Toast.LENGTH_SHORT).show();
             /*
			sfr.cmd = Const.Command.Cmd_AddFriend;	
			sfr.search_id = et.getEditableText().toString(); 
			sfr.name = Const.name;
			Log.d(this.getClass().getName(),"Add: "+sfr.search_id+"!!!!");
			sfr.DoServerFunc();
			//status = sfr.GetAddFResult();
			if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
				String ret = sfr.GetUserData(); 
	            String[] tempArray = new String[Const.fnum+1];
	            String[] fidArray = new String[Const.fnum+1];
	            System.arraycopy(Const.name_list, 0, tempArray, 0, Const.fnum);
	            System.arraycopy(Const.fid_list, 0, fidArray, 0, Const.fnum);
	            Const.name_list = tempArray;  	
	            Const.fid_list = fidArray;
				char front[] = new char[15];
				char last[] = new char[30];			
				ret.getChars(0, ret.indexOf('/'), front, 0);
				ret.getChars(ret.indexOf('/')+1, ret.length(), last, 0);	
				FileAccess.FriendSort(((Dialog) dialog).getContext(), String.valueOf(last).trim(), String.valueOf(front).trim(), Const.fnum);
				Const.fnum++;
				Const.refresh_flag = true;
			}
			*/
			//intent = new Intent();
			//intent.setClass(AddFriendPage.this, MainPage.class);
		    //startActivity(intent);
		    //AddFriendPage.this.finish();
            }  
        }); 
		ReplyAdd_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        	// TODO Auto-generated method stub
        	Toast.makeText(AddFriendPage.this, "?�b", Toast.LENGTH_SHORT).show();
        	}
        });
		
		NotFind_dialog = new AlertDialog.Builder(this);
		NotFind_dialog.setIcon(android.R.drawable.ic_dialog_alert);
		NotFind_dialog.setCancelable(false);  
		NotFind_dialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int which) {
	        // TODO Auto-generated method stub
	        }
	    });
		
		sendbutton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ServerFuncRun sfr = new ServerFuncRun();
				if(addmode==0){
					/*
					int num = 0;
					Log.v("uri", "contactsListName.size():" + contactsListName.size());
					for (int i=0; i < contactsListName.size(); i++){
						String Email = getContactEmail((String)contactsListName.get(i));						
						if(Email.equals("NULL")){							
							//Email = NeedEmail(et.getEditableText().toString());
						}else{							
							sfr.cmd = Const.Command.Cmd_DoSearch;	
							sfr.data = Email;//sfr.search_id = et.getEditableText().toString(); 
							sfr.datatype = Const.eUsrType.UsrType_Email;
							//Log.d(this.getClass().getName(),"search : "+sfr.search_id+"!!!!");
							//Log.v("uri", "search : "+sfr.search_id+"!!!!");
							sfr.DoServerFunc();
							if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
								String ret = sfr.GetUserData();
								//Log.d(this.getClass().getName(),"search : "+ret+"!!!!");
								boolean checkfid = false;
								DBHelper dbhelper = new DBHelper(context);
								SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
								String[] idlist;
								idlist = dbhelper.getidfromSql(dbrw, Const.contactinfo.getTableName());
								for (int j = 0; j < dbhelper.getnumfromSql(dbrw, Const.contactinfo.getTableName()); j++) {
									//if(ret.equals(dbhelper.getfidfromSql(dbrw, Const.contactinfo.getTableName(),
											//idlist[i]))){
									if(ret.equals(dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "fid", "_id", 
											idlist[i]))){
										checkfid = true;
										break;
									}

								}
								dbrw.close();
								dbhelper.close();
								if(checkfid == true){
									//String notfindstring = "Friend already exist!";
									//NotFind_dialog.setTitle("Friend Exist");
									//NotFind_dialog.setMessage(notfindstring);
									//NotFind_dialog.show();
								} else {
									do_add_friend(((View) arg0).getContext(), ret);
									Log.v("uri", "getEmail:" + Email);
									num ++;
									//String addstring = "Would you add " + et.getText().toString() + " to Friend List ?";
									//ReplyAdd_dialog.setTitle("Find Friend");
									//ReplyAdd_dialog.setMessage(addstring);
									//ReplyAdd_dialog.show();
								}								
								
							}						
							//InviteDialog(Email, et.getEditableText().toString());
						}
					}
					
					Log.v("uri", "num:" + num);
					*/
					Log.v("uri", "num:" + et.getEditableText().toString());
					String Email = getContactEmail((String)et.getEditableText().toString());//NeedEmail(et.getEditableText().toString());
					InviteDialog(Email, et.getEditableText().toString());
					
				}else{
					String sid = FileAccess.getStringFromPreferences(context, "",
							Const.eUsrType.UsrType_SearchId, Const.projinfo.sSharePreferenceName);
					if(et.getEditableText().toString().equals(sid)){
						String notfindstring = "You Can't add your self!";
						NotFind_dialog.setTitle("Add yourself");
						NotFind_dialog.setMessage(notfindstring);
						NotFind_dialog.show();
					} else {
						sfr.name = FileAccess.getStringFromPreferences(context, null, 
				    			Const.eUsrType.UsrType_Email, Const.projinfo.sSharePreferenceName);
						sfr.cmd = Const.Command.Cmd_DoSearch;	
						sfr.data = et.getEditableText().toString().trim();//sfr.search_id = et.getEditableText().toString(); 
						if(PhoneNumberUtils.isGlobalPhoneNumber(sfr.data)==true){
							 TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
						     String countryCode = tm.getSimCountryIso();
						     if(countryCode.equals("tw")){
						    	 sfr.data = FileAccess.formatphone(sfr.data, "+886");
						     }
							sfr.datatype = Const.eUsrType.UsrType_Fone;
						} else {
							sfr.datatype = Const.eUsrType.UsrType_SearchId;
						}						
						//Log.d(this.getClass().getName(),"search : "+sfr.search_id+"!!!!");
						//Log.v("uri", "search : "+sfr.search_id+"!!!!");
						//sfr.DoServerFunc();
							 /*
							    ContentResolver cr = getContentResolver();
							    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
							            null, null, null);
							    if (cur.getCount() > 0) {
							        while (cur.moveToNext()) {
							            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
							            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));							            
							            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
							            {
							                // Query phone here. Covered next
							                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null); 
							                while (phones.moveToNext()) { 
							                	Log.i("Names", name);
							                         String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							                         Log.i("Number", phoneNumber);
							                        } 
							                phones.close(); 
							            }

							        }
							    }
							*/
						ServerInBackground(sfr);
						
						//Toast.makeText(getApplicationContext(),
							//	et.getText().toString(),
				        		//Toast.LENGTH_SHORT).show();
						
						
					}
				}
			}
		});
	}
	
	private void do_add_friend(Context context, String fname){
		ServerFuncRun sf = new ServerFuncRun();
		//ServerFuncRun sfr = new ServerFuncRun();
		sf.cmd = Const.Command.Cmd_AddFriend;	
		sf.search_id = fname;//et.getEditableText().toString(); 
		sf.name = FileAccess.getStringFromPreferences(context, null, 
    			Const.eUsrType.UsrType_Email, Const.projinfo.sSharePreferenceName);
		sf.usr_id = FileAccess.getStringFromPreferences(context, null, 
    			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
		Log.d(this.getClass().getName(),"Add: "+sf.search_id+"!!!!");
		//sf.DoServerFunc();
		ServerInBackground(sf);
		/*
		sfr.cmd = Const.Command.Cmd_SendPushToOther;
		sfr.usr_id = Const.uid;
		sfr.rec_id = String.valueOf(front).trim();
		sfr.message = Const.eNotifyType.NotifyType_AddFriend+
				"%"+Const.uid+
				"%"+FileAccess.getStringFromPreferences(context, Const.name, Const.eUsrType.UsrType_NickName, Const.sSharePreferenceName);
		sf.DoServerFunc();
		*/
		//status = sfr.GetAddFResult();		
	}
	
	public String getContactEmail(String name) {
		String id = getContactID(name);
		String emailAddress = "NULL";
		Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id,null, null);
	    while (emails.moveToNext()) 
		{                 
			emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			//Log.v("uri", emailAddress);
			return emailAddress;
		}   
		emails.close();
		return emailAddress;
	}
	
	public String getContactID(String name) {
		//Log.v("uri", "Input Name:"+name);
		String id = "NULL";
		for(int i=0; i<contactsListName.size();i++){
			//Log.v("uri", "Date Name:"+contactsListName.get(i));
			if(name.equals(contactsListName.get(i))){
				//Log.v("uri", "Find Name:"+name+ "id:"+ contactsListID.get(i));
				return contactsListID.get(i);
			}
		}
		return id;
	}
	
	public String NeedEmail(final String Name) {
	      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	      final EditText input = new EditText(this);
	      input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
	      dialog.setTitle("Please enter " + Name + "'s Email Address");
	      dialog.setView(input);
	      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
	            	if(android.util.Patterns.EMAIL_ADDRESS.matcher(input.getText().toString()).matches()){
	            		SendEmail = input.getText().toString();
	            		InviteDialog(SendEmail, Name);
	            	}else{
	            		warningDialog("Error", "Please enter a valid e-mail");
	            	}
	           }
	      });
	      dialog.show();
	      return SendEmail;
	} //EOF NeedEmail 
	
	public void InviteDialog(String Email, final String Name) {
	      /*AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	      final EditText input = new EditText(this);
	      dialog.setTitle("Send Email to: "+Email);
	      //dialog.setMessage(xMessage);
	      dialog.setView(input);
	      input.setText("Hi " + Name + ":\n" +
	      		"May I add you to be my contact in \"MeetDay\" App.");
	      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
	            	  et.setText("");
	            	  warningDialog("Success", "Your Message was sent to\n"+ Name +"'s E-mail");
	           }
	      });
	      dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
	                  warningDialog("Cancel", "Cancel Invitation");
	                  et.setText("");
	            }
	      });
	      dialog.show();*/

		  intent = new Intent();
	      intent.setAction(Intent.ACTION_SENDTO);
	      intent.setData(Uri.parse("mailto:"+Email));
	      intent.putExtra(Intent.EXTRA_SUBJECT, "May I add you to be my contact in \"MeetDay\" App.");
	      intent.putExtra(Intent.EXTRA_TEXT, "Hi " + et.getEditableText().toString() + ":\n" +
		      		"May I add you to be my contact in \"MeetDay\" App.");
	      startActivity(intent);

	} //EOF InviteDialog
	
	public void warningDialog(String title, String message) {
	      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	      dialog.setTitle(title);
	      dialog.setMessage(message);
	      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
	           }
	      });
	      dialog.show();
	} //EOF warningDialog
	
	private void ServerInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog pdialog;
			//ServerFuncRun sf = new ServerFuncRun();
	        protected void onPreExecute() {
	        	pdialog = ProgressDialog.show(AddFriendPage.this, "Connecting", "Wait...",true);				
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
				//if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){
				if(Const.Command.Cmd_DoSearch == sfr.cmd){
					if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
						String ret = sfr.GetUserData();
						Log.d(this.getClass().getName(),"search : "+ret+"!!!!");
						boolean checkfid = false;
						DBHelper dbhelper = new DBHelper(getBaseContext());
						SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
						String[] idlist;
						idlist = dbhelper.getidfromSql(dbrw, Const.contactinfo.getTableName());
						for (int i = 0; i < dbhelper.getnumfromSql(dbrw, Const.contactinfo.getTableName()); i++) {
							//if(ret.equals(dbhelper.getfidfromSql(dbrw, Const.contactinfo.getTableName(), idlist[i]))){
							if(ret.equals(dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "fid", "_id", 
									idlist[i]))){
								checkfid = true;
								break;
							}
						}
						dbrw.close();
						dbhelper.close();
						
						if(checkfid == true){
							String notfindstring = "Friend already exist!";
							NotFind_dialog.setTitle("Friend Exist");
							NotFind_dialog.setMessage(notfindstring);
							NotFind_dialog.show();
						} else if(ret.equals(FileAccess.getStringFromPreferences(context, "", 
			        			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName))){
							String notfindstring = "You Can't Add yourself!";
							NotFind_dialog.setTitle("Friend Exist");
							NotFind_dialog.setMessage(notfindstring);
							NotFind_dialog.show();
						} else {
							String addstring = "Would you add " + et.getText().toString() + " to Friend List ?";
							ReplyAdd_dialog.setTitle("Find Friend");
							ReplyAdd_dialog.setMessage(addstring);
							ReplyAdd_dialog.show();
							addid = ret;
						}
					}else{
						String notfindstring = "Not Find " + et.getText().toString() + " please try again!!";
						NotFind_dialog.setTitle("Not Find");
						NotFind_dialog.setMessage(notfindstring);
						NotFind_dialog.show();
					}
					Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();	
				} else if(sfr.cmd == Const.Command.Cmd_AddFriend){
					if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
						/*
						DBHelper dbhelper = new DBHelper(context);
						SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
						String ret = sfr.GetUserData(); 
	          
						char front[] = new char[15];
						char last[] = new char[30];			
						ret.getChars(0, ret.indexOf('/'), front, 0);
						ret.getChars(ret.indexOf('/')+1, ret.length(), last, 0);	
								            
			    		Const.SQL_data data = new Const.SQL_data(String.valueOf(last).trim(), 
			    				String.valueOf(front).trim());
			    		dbhelper.insertData(dbrw, Const.contactinfo.getTableName(), data);

			    		String flst = FileAccess.getStringFromPreferences(context, "", 
			        			Const.eUsrType.UsrType_FreindList, Const.projinfo.sSharePreferenceName);
			    		
			    		flst = flst + String.valueOf(front).trim()+"/";
			    		
			    		FileAccess.putStringInPreferences(context, flst, 
				    			Const.eUsrType.UsrType_FreindList, Const.projinfo.sSharePreferenceName);
			    		

						
						//FileAccess.FriendSort(context, String.valueOf(last).trim(), String.valueOf(front).trim(), Const.userinfo.fnum);
						//Const.userinfo.fnum++;
						Const.contactinfo.ladapter.onUpdate(context, dbhelper, Const.contactinfo.getTableName());
						dbrw.close();
						*/
						Toast.makeText(context, "Add OK", Toast.LENGTH_SHORT).show();							
					} else {
						Toast.makeText(context, "Add Fail", Toast.LENGTH_SHORT).show();
					}						        	
				}
			}
		}.execute(null, null, null);
	}
	
}

