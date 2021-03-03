package com.project1.meetday;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.FileAccess;
import com.project1.http.PictUtil;
import com.project1.http.ServerFuncRun;

import java.util.ArrayList;
import java.util.List;

public class AddFriendNewPage extends Activity {

	public static final String BROADCAST_GETFRIENDNICKNAME =
			"com.project1.addfriend.GETFRIENDNICKNAME";
	public static final String BROADCAST_FRIENDSNICKNAME =
			"com.project1.addfriend.FRIENDSNICKNAME";

	private LinearLayout lilayoutAddFriendPage;
	private RelativeLayout relayoutAddFriendTitle, relayoutListInfo;
	private Bitmap bmpAddFriendPhoneBtn, bmpAddFriendFBBtn, bmpAddFriendGGBtn, bmpSearchFriendBtn;
	private static ImageView inviteGmailButton, inviteFBButton, inviteSMSButton;
	private static ImageView inviteButton;
	private static Button sendbutton;
	AlertDialog.Builder ReplyAdd_dialog;
	AlertDialog.Builder NotFind_dialog;
	private EditText et;
	private Context context;
	private String addid, strFriendName = null;
	private IntentFilter mIntentFilter;
	private static final String TAG = "AddFriendNewPage";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.addfriend_newpage);
		this.context = getBaseContext();

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BROADCAST_GETFRIENDNICKNAME);
		registerReceiver(mResponse, mIntentFilter);

		allocateBmp();

		lilayoutAddFriendPage = (LinearLayout) findViewById(R.id.lilayoutAddFriendPage);
		relayoutAddFriendTitle = (RelativeLayout) findViewById(R.id.relayoutAddFriendTitle);
		relayoutListInfo = (RelativeLayout) findViewById(R.id.relayoutListInfo);

		et = (EditText) findViewById(R.id.Invite_LineEdit);
		inviteButton = (ImageView) findViewById(R.id.InviteButton);
		inviteButton.setImageBitmap(bmpSearchFriendBtn);
		inviteButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ServerFuncRun sfr = new ServerFuncRun();

				String sid = FileAccess.getStringFromPreferences(context, "",
						Const.eUsrType.UsrType_SearchId, Const.projinfo.sSharePreferenceName);
				if (et.getEditableText().toString().equals(sid)) {
//					String notfindstring = "You Can't add your self!";
					String notfindstring = getResources().getString(R.string.add_friend_cant_add_yourself);
//					NotFind_dialog.setTitle("Add yourself");
					NotFind_dialog.setTitle(getResources().getString(R.string.add_friend_add_yourself));
					NotFind_dialog.setMessage(notfindstring);
					NotFind_dialog.show();
				} else {
					sfr.name = FileAccess.getStringFromPreferences(context, null,
							Const.eUsrType.UsrType_Email, Const.projinfo.sSharePreferenceName);
					sfr.cmd = Const.Command.Cmd_DoSearch;
					sfr.data = et.getEditableText().toString().trim();//sfr.search_id = et.getEditableText().toString(); 
					if (PhoneNumberUtils.isGlobalPhoneNumber(sfr.data) == true) {
						TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						String countryCode = tm.getSimCountryIso();
						if (countryCode.equals("tw") || sfr.data.substring(0, 1).equals("0")) {
							sfr.data = FileAccess.formatphone(sfr.data, "+886");
						}
						sfr.datatype = Const.eUsrType.UsrType_Fone;
					} else {
						sfr.datatype = Const.eUsrType.UsrType_SearchId;
					}

					ServerInBackground(sfr);

				}

			}
		});

		inviteSMSButton = (ImageView) findViewById(R.id.InvitePhone_button);
		inviteSMSButton.setImageBitmap(bmpAddFriendPhoneBtn);
		inviteSMSButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(context, SmsInvitePage.class);
//				startActivity(intent);
				String strUsrMeetDayID = FileAccess.getStringFromPreferences(getBaseContext(), "", Const.eUsrType.UsrType_SearchId, Const.projinfo.sSharePreferenceName);
				String strUsrFone = FileAccess.getStringFromPreferences(getBaseContext(), "", Const.eUsrType.UsrType_Fone, Const.projinfo.sSharePreferenceName);
				String strMailContent;
				if (!strUsrMeetDayID.equals("null") && !strUsrFone.equals("null")) {
					strMailContent = getResources().getString(R.string.add_friends_mail_first_sentence) + " " +
							getResources().getString(R.string.add_friends_mail_sentence4url) + " " +
							getResources().getString(R.string.add_friends_mail_second_sentence) + " " +
							getResources().getString(R.string.add_friends_mail_sentence4id) + " " +
							strUsrMeetDayID + " " +
							getResources().getString(R.string.add_friends_mail_third_sentence) + " " +
							getResources().getString(R.string.add_friends_mail_sentence4fone) + " " +
							strUsrFone;
				} else if (!strUsrMeetDayID.equals("null") && strUsrFone.equals("null")) {
//					strMailContent = "Please downloan MeetDay from " + "https://play.google.com/apps/testing/com.project1.meetday" + ", and add me by" + " my MeetDat ID: " + strUsrMeetDayID;
					strMailContent = getResources().getString(R.string.add_friends_mail_first_sentence) + " " +
							getResources().getString(R.string.add_friends_mail_sentence4url) + " " +
							getResources().getString(R.string.add_friends_mail_second_sentence) + " " +
							getResources().getString(R.string.add_friends_mail_sentence4id) + " " +
							strUsrMeetDayID;
				} else if (strUsrMeetDayID.equals("null") && !strUsrFone.equals("null")) {
					strMailContent = getResources().getString(R.string.add_friends_mail_first_sentence) + " " +
							getResources().getString(R.string.add_friends_mail_sentence4url) + " " +
							getResources().getString(R.string.add_friends_mail_second_sentence) + " " +
							getResources().getString(R.string.add_friends_mail_sentence4fone) + " " +
							strUsrFone;
				} else {
					strMailContent = getResources().getString(R.string.add_friends_mail_first_sentence) + " " +
							getResources().getString(R.string.add_friends_mail_sentence4url);
				}
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
				intent.putExtra("subject", getResources().getString(R.string.add_friends_mail_title));
				intent.putExtra("sms_body", strMailContent);
//				intent.putExtra(Intent.EXTRA_TEXT, strMailContent);				
				startActivity(intent);
			}
		});
		
		inviteFBButton = (ImageView) findViewById(R.id.InviteFB_button);
		inviteFBButton.setImageBitmap(bmpAddFriendFBBtn);
		inviteFBButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Const.isNetworkConnected(context)) {
					if (!FileAccess.getStringFromPreferences(context, "null",
							Const.eUsrType.UsrType_FBId, Const.projinfo.sSharePreferenceName).toLowerCase().equals("null")) {
						if (FileAccess.getStringFromPreferences(context, "null",
								Const.eUsrType.UsrType_FB_Status, Const.projinfo.sSharePreferenceName).equals(Const.eSocialCmd.Social_Connect)) {
							Intent intent = new Intent();
							intent.setClass(context, SignInWithSocial.class);
							intent.putExtra(SignInWithSocial.SOCIAL_TYPE, Const.eSocialCmd.Social_FB);
							intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, Const.eSocialCmd.Social_AddFriendPage);
							intent.putExtra(SignInWithSocial.SOCIAL_INOUT, Const.eSocialCmd.Social_Invite);
							startActivity(intent);
						}
					}
				} else {
					Toast.makeText(context, "You are offline!", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		inviteGmailButton = (ImageView) findViewById(R.id.InviteGoogle_button);
		inviteGmailButton.setImageBitmap(bmpAddFriendGGBtn);
		inviteGmailButton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String strUsrMeetDayID = FileAccess.getStringFromPreferences(getBaseContext(), "", Const.eUsrType.UsrType_SearchId, Const.projinfo.sSharePreferenceName);
				String strUsrFone = FileAccess.getStringFromPreferences(getBaseContext(), "", Const.eUsrType.UsrType_Fone, Const.projinfo.sSharePreferenceName);
				String strMailContent;
				if(!strUsrMeetDayID.equals("null") && !strUsrFone.equals("null")){
					strMailContent = getResources().getString(R.string.add_friends_mail_first_sentence) + " " +
							         getResources().getString(R.string.add_friends_mail_sentence4url) + " " +
							         getResources().getString(R.string.add_friends_mail_second_sentence) + " " +
							         getResources().getString(R.string.add_friends_mail_sentence4id) + " " +
							         strUsrMeetDayID + " " +
							         getResources().getString(R.string.add_friends_mail_third_sentence) + " " +
							         getResources().getString(R.string.add_friends_mail_sentence4fone) + " " +
							         strUsrFone;
				}else if(!strUsrMeetDayID.equals("null") && strUsrFone.equals("null")) {
//					strMailContent = "Please downloan MeetDay from " + "https://play.google.com/apps/testing/com.project1.meetday" + ", and add me by" + " my MeetDat ID: " + strUsrMeetDayID;
					strMailContent = getResources().getString(R.string.add_friends_mail_first_sentence) + " " +
							         getResources().getString(R.string.add_friends_mail_sentence4url) + " " +
							         getResources().getString(R.string.add_friends_mail_second_sentence) + " " +
							         getResources().getString(R.string.add_friends_mail_sentence4id) + " " +
							         strUsrMeetDayID;
				}else if(strUsrMeetDayID.equals("null") && !strUsrFone.equals("null")){
					strMailContent = getResources().getString(R.string.add_friends_mail_first_sentence) + " " +
							         getResources().getString(R.string.add_friends_mail_sentence4url) + " " +
							         getResources().getString(R.string.add_friends_mail_second_sentence) + " " +
							         getResources().getString(R.string.add_friends_mail_sentence4fone) + " " +
							         strUsrFone;
				}else {
					strMailContent = getResources().getString(R.string.add_friends_mail_first_sentence) + " " +
							         getResources().getString(R.string.add_friends_mail_sentence4url);
				}
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SENDTO);
				intent.setData(Uri.parse("mailto:"));
			    List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);
			    if (!resInfo.isEmpty()){
			    	List<Intent> targetedShareIntents = new ArrayList<Intent>();
			        for (ResolveInfo info : resInfo) {
			        	Intent targeted = new Intent(Intent.ACTION_SENDTO);
			        	ActivityInfo activityInfo = info.activityInfo;
			        	if (activityInfo.packageName.contains("gm") || activityInfo.name.contains("mail")) {
			        		Log.d(TAG, activityInfo.packageName);
			        		targeted.setData(Uri.parse("mailto:"));
			        		targeted.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.add_friends_mail_title));
			        		targeted.putExtra(Intent.EXTRA_TEXT, strMailContent);
			        		targeted.setPackage(activityInfo.packageName);
			        		targetedShareIntents.add(targeted);
		                }		        	
			        	
			        }
			        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Invite from E-mail");
		            if (chooserIntent == null) {
		                return;
		            }
		            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray());
		            try {
		                startActivity(chooserIntent);
		            } catch (android.content.ActivityNotFoundException ex) {
		                Toast.makeText(context, "Can't find any E-mail", Toast.LENGTH_SHORT).show();
		            }			        
			    }				
			}
		});

		ReplyAdd_dialog = new AlertDialog.Builder(this);
		ReplyAdd_dialog.setIcon(android.R.drawable.ic_dialog_alert);
		ReplyAdd_dialog.setCancelable(false);  
		ReplyAdd_dialog.setPositiveButton(getResources().getString(R.string.add_friend_add), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {  
        	 do_add_friend(((Dialog) dialog).getContext(), addid);
            }  
        }); 
		ReplyAdd_dialog.setNegativeButton(getResources().getString(R.string.add_friend_cancel), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        	// TODO Auto-generated method stub
//        	Toast.makeText(AddFriendNewPage.this, "Debug", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Debug: ");
        	}
        });
		
		NotFind_dialog = new AlertDialog.Builder(this);
		NotFind_dialog.setIcon(android.R.drawable.ic_dialog_alert);
		NotFind_dialog.setCancelable(false);
		NotFind_dialog.setNegativeButton(getResources().getString(R.string.add_friend_confirm), new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int which) {
	        // TODO Auto-generated method stub
	        }
	    });
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mResponse);
		releaseBmp();
		releaseViews();
		System.gc();
		super.onDestroy();
	}

	private void allocateBmp() {
		bmpAddFriendPhoneBtn = PictUtil.getLocalBitmap(this, R.drawable.addfriendphone, 1);
		bmpAddFriendFBBtn = PictUtil.getLocalBitmap(this, R.drawable.addfriendfb, 1);
		bmpAddFriendGGBtn = PictUtil.getLocalBitmap(this, R.drawable.addfriendgoogle, 1);
		bmpSearchFriendBtn = PictUtil.getLocalBitmap(this, R.drawable.setting_search_friend, 1);
	}

	private void releaseBmp() {
		if(bmpAddFriendPhoneBtn != null){
			bmpAddFriendPhoneBtn.recycle();
		}
		if(bmpAddFriendFBBtn != null){
			bmpAddFriendFBBtn.recycle();
		}
		if(bmpAddFriendGGBtn != null){
			bmpAddFriendGGBtn.recycle();
		}
		if(bmpSearchFriendBtn != null){
			bmpSearchFriendBtn.recycle();
		}
	}

	private void releaseViews() {
		if(inviteSMSButton != null) {
			inviteSMSButton.setImageBitmap(null);
		}
		if(inviteFBButton != null) {
			inviteFBButton.setImageBitmap(null);
		}
		if(inviteGmailButton != null) {
			inviteGmailButton.setImageBitmap(null);
		}
		if(et != null) {
			et.setBackground(null);
		}
		if(inviteButton != null) {
			inviteButton.setImageBitmap(null);
			inviteButton.setBackground(null);
		}
		if(relayoutListInfo != null) {
			relayoutListInfo.setBackground(null);
		}
		if(relayoutAddFriendTitle != null) {
			relayoutAddFriendTitle.setBackground(null);
		}
		if(lilayoutAddFriendPage != null) {
			lilayoutAddFriendPage.setBackground(null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_friend_new_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void ServerInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog pdialog;
			//ServerFuncRun sf = new ServerFuncRun();
	        protected void onPreExecute() {
	        	pdialog = ProgressDialog.show(AddFriendNewPage.this, getResources().getString(R.string.add_friend_connect), getResources().getString(R.string.add_friend_wait), true);
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
								strFriendName = dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name", "_id", idlist[i]);
								Log.d(TAG, strFriendName);
								break;
							}
						}
						dbrw.close();
						dbhelper.close();

						if(checkfid == true){
//							String notfindstring = "Friend already exist!";
							String notfindstring;
                            if(strFriendName != null) {
								notfindstring = getResources().getString(R.string.add_friend_your_friend) + " " + strFriendName + " " + getResources().getString(R.string.add_friend_your_friend_exist);
							} else {
								notfindstring = getResources().getString(R.string.add_friend_your_friend) + " " + et.getText().toString() + " " + getResources().getString(R.string.add_friend_your_friend_exist);
							}
							NotFind_dialog.setTitle(getResources().getString(R.string.add_friend_friend_exist_title));
							NotFind_dialog.setMessage(notfindstring);
							NotFind_dialog.show();
							strFriendName = null;
						} else if(ret.equals(FileAccess.getStringFromPreferences(context, "", 
			        			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName))){
							String notfindstring = getResources().getString(R.string.add_friend_cant_add_yourself);
							NotFind_dialog.setTitle(getResources().getString(R.string.add_friend_add_yourself));
							NotFind_dialog.setMessage(notfindstring);
							NotFind_dialog.show();
						} else {
							ConnectToServer con = new ConnectToServer(context);
							con.DoGetUsrData(ret, Const.eUsrType.UsrType_NickName);
//							String addstring = getResources().getString(R.string.add_friend_find_friend_message1) + " " + et.getText().toString() + " " + getResources().getString(R.string.add_friend_find_friend_message2);
//							String tmpName = et.getText().toString();;
//							Log.d(TAG, "test4-1");
//							if(strFriendName != null) {
//								if (strFriendName.equals("") || strFriendName.equals("null")) {
//									tmpName = et.getText().toString();
//								} else {
//									tmpName = strFriendName;
//								}
//							}
//							Log.d(TAG, "test4-2");
//							Log.d(TAG, tmpName);
//							String addstring = getResources().getString(R.string.add_friend_find_friend_message1) + " " + tmpName + " " + getResources().getString(R.string.add_friend_find_friend_message2);
//							ReplyAdd_dialog.setTitle(getResources().getString(R.string.add_friend_find_friend_title));
//							ReplyAdd_dialog.setMessage(addstring);
//							ReplyAdd_dialog.show();
							addid = ret;
						}
					}else{
						String notfindstring = getResources().getString(R.string.add_friend_not_find_message1) + " " + et.getText().toString() + " " +  getResources().getString(R.string.add_friend_not_find_message2);
						NotFind_dialog.setTitle(getResources().getString(R.string.add_friend_not_find_title));
						NotFind_dialog.setMessage(notfindstring);
						NotFind_dialog.show();
					}
//					Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
				} else if(sfr.cmd == Const.Command.Cmd_AddFriend){
					if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){

						Toast.makeText(context, getResources().getString(R.string.add_friend_add_ok), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, getResources().getString(R.string.add_friend_add_fail), Toast.LENGTH_SHORT).show();
					}						        	
				}
			}
		}.execute(null, null, null);
	}
	
	private void do_add_friend(Context context, String fname){
		ServerFuncRun sf = new ServerFuncRun();
		sf.cmd = Const.Command.Cmd_AddFriend;	
		sf.search_id = fname;//et.getEditableText().toString(); 
		sf.name = FileAccess.getStringFromPreferences(context, null, 
    			Const.eUsrType.UsrType_Email, Const.projinfo.sSharePreferenceName);
		sf.usr_id = FileAccess.getStringFromPreferences(context, null, 
    			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
		Log.d(this.getClass().getName(),"Add: "+sf.search_id+"!!!!");
		ServerInBackground(sf);
	}

	private BroadcastReceiver mResponse = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			strFriendName = null;
			if (intent.getAction().equals(BROADCAST_GETFRIENDNICKNAME)) {
				strFriendName = intent.getStringExtra(BROADCAST_FRIENDSNICKNAME);
				String tmpName = et.getText().toString();;
				if(strFriendName != null) {
					if (strFriendName.equals("") || strFriendName.equals("null")) {
						tmpName = et.getText().toString();
					} else {
						tmpName = strFriendName;
					}
				}
				String addstring = getResources().getString(R.string.add_friend_find_friend_message1) + " " + tmpName + " " + getResources().getString(R.string.add_friend_find_friend_message2);
				ReplyAdd_dialog.setTitle(getResources().getString(R.string.add_friend_find_friend_title));
				ReplyAdd_dialog.setMessage(addstring);
				ReplyAdd_dialog.show();
			}
			Log.d(TAG, "receive " + strFriendName);
			strFriendName = null;
		}
	};

}
