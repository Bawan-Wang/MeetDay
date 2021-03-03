package com.project1.meetday;

import java.util.Date;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore.Audio;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.FileAccess;
import com.project1.http.NotificationAccess;
//import com.example.notificationsound.MainActivity;
//import com.example.notificationsound.R;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	//private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	//KeyguardManager keymanger = null;
	 public static final int MEG_INVALIDATE = 9527; //自訂一個號碼
	
	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				for (int i = 0; i < 3; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				//Const.eNotifyType eType = parsenotification((String) extras.get(Const.MESSAGE_KEY));
				Log.d(this.getClass().getName(), (String) extras.get(Const.projinfo.MESSAGE_KEY));
				//RetrieveDataFromNotify((String) extras.get(Const.projinfo.MESSAGE_KEY), this);
				/*
				if(eType == Const.eNotifyType.NotifyType_AskConnect){	
					if(Const.State_Type == Const.eState.State_Idle)
						RetrieveDataFromNoti(eType,(String) extras.get(Const.MESSAGE_KEY), this);
						//HelperConnectNotify((String) extras.get(Const.MESSAGE_KEY), this);
				} else if(eType == Const.eNotifyType.NotifyType_RespondConnect){					
					if(Const.State_Type == Const.eState.State_AskCallForHelp){
//						HelperRespNotify((String) extras.get(Const.MESSAGE_KEY), this);
						if(true == RetrieveDataFromNoti(eType, (String) extras.get(Const.MESSAGE_KEY), this)){
							Intent broadcastIntent  = new Intent();
							broadcastIntent.setAction(AskerConnectPage.BROADCAST_GETHELPRESPONSE);
							broadcastIntent.putExtra("String", "Helper respose");
							sendBroadcast(broadcastIntent);	
							Log.i(TAG, "Received: True");
						}else{
							Log.i(TAG, "Received: False");
						}
				    }
					//sendNotification((String) extras.get(Const.MESSAGE_KEY));
				} else if(eType == Const.eNotifyType.NotifyType_DisConnect){
					if(Const.State_Type == Const.eState.State_RespondConnect)
						RetrieveDataFromNoti(eType, (String) extras.get(Const.MESSAGE_KEY), this);
					//sendNotification((String) extras.get(Const.MESSAGE_KEY));
				} else if (eType == Const.eNotifyType.NotifyType_AddFriend){
					RetrieveDataFromNoti(eType, (String) extras.get(Const.MESSAGE_KEY), this);
					//AddfriendNotify((String) extras.get(Const.MESSAGE_KEY), this);
				}
				Log.i(TAG, "Received: " + extras.toString());
				*/
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	/*
	@SuppressWarnings("deprecation")
	public void sendNotification_new(Context caller, Const.eNotifyType type, String message) {
		
		Intent intent;
		NotificationManager notifier = 
				(NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);
		Builder builder = new Notification.Builder(caller);
		
		//if(Const.uid == null || Const.bFrontRunnig == false){
			intent = new Intent(caller, NotificationStart.class);
		//} else 
			//intent = new Intent(caller, MainPage.class);
			//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		switch(type){	
		case NotifyType_Thanks:
		case NotifyType_MissedCall:
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//Const.Notify_State = Const.eNotifyType.NotifyType_MissedCall;
			break;
		case NotifyType_Ringing:
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
			break;
		case NotifyType_ConnectedOn:
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
			break;
		default:
			break;
		}
		
		try {
	   		
			PendingIntent contentIndent = PendingIntent.getActivity(
					caller, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contentIndent)
				   .setSmallIcon(R.drawable.search)
				   .setLargeIcon(
						   BitmapFactory.decodeResource(caller.getResources(),
							R.drawable.logo_icon))
				   .setTicker(message) 
				   .setWhen(System.currentTimeMillis())
				   .setAutoCancel(true)
				   .setContentTitle("MeetDay ")
				   //.setOngoing(true)
				   .setContentText(message);		    
		    
			Notification notification = builder.getNotification();

			//notification.flags = Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			//notification.flags |= Notification.FLAG_INSISTENT;// | Notification.FLAG_AUTO_CANCEL;
			//notification.flags |= Notification.FLAG_CANCEL_CURRENT;
			//notification.setLatestEventInfo(caller, "MeetDay ", message, contentIndent);
			notifier.notify(1, notification);
			//i++;
			
	    }catch (Exception e) {

		}
	}
	*/
	
	@SuppressWarnings("deprecation")
	public void sendNotification(String message) {
	
		try {		
			//Intent alarmIntent = new Intent("android.intent.action.MAIN");
			//alarmIntent.setClass(this, HelperConnectPage .class);
            //alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
			Intent intent;
			NotificationManager notificationManager = 
					(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Builder builder = new Notification.Builder(this);
			
			//if(Const.userinfo.uid == null){
				//Log.d(TAG, "Start@@@@@");
				 intent = new Intent(getApplicationContext(), HelperConnectPage.class); 
			//} else {
				//Log.d(TAG, Const.userinfo.uid);
				// intent = new Intent(getApplicationContext(), HelperConnectPage.class); 
			//}			
			//final Intent intentM = new Intent(getApplicationContext(), MainPage.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);
			//intent.putExtra(HelperConnectPage.HELPER_ROOMID, Const.coninfo.connectid);
			intent.putExtra(HelperConnectPage.HELPER_MESSAGE, message);
			//intent.putExtra(HelperConnectPage.HELPER_ASKERPHOTO, Const.projinfo.sServerAdr+
					//"upload/user_"+Const.coninfo.conusrid+".jpg");
			//final TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext()); // ���???TaskStackBuilder
			//stackBuilder.addParentStack(WelcomPage.class); // ???�??????????????????Activity���???????�????????????Activity???????????蝟�???Activity(Parents)??????���???????
			//stackBuilder.addNextIntent(intent); // ???�??????Activity???Intent
			//PendingIntent contentIndent = stackBuilder.getPendingIntent(i, PendingIntent.FLAG_UPDATE_CURRENT); // ??????PendingIntent
			PendingIntent contentIndent = PendingIntent.getActivity(
					this, 0, intent,
					PendingIntent.FLAG_ONE_SHOT);
			
			long[] vibratepattern = {100, 400, 500, 400};
			
			Notification notification = builder.setContentIntent(contentIndent)
					.setSmallIcon(R.drawable.missedcall)
					.setLargeIcon(
							BitmapFactory.decodeResource(this.getResources(),
									R.drawable.search))
					.setTicker(message) 
					.setWhen(System.currentTimeMillis())
					.setAutoCancel(true)
					.setContentTitle("MeetDay ")
					.setContentText(message)
					.setSound(Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6"))
					.setOngoing(true)
					.setVibrate(vibratepattern)
					.setLights(NOTIFICATION_ID, NOTIFICATION_ID, NOTIFICATION_ID).build();

			
			
//			Notification notification = builder.getNotification();

			notification.flags = Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			// notification.defaults |= Notification.DEFAULT_SOUND;
			//notification.sound = Uri
					//.parse("file:///sdcard/Notifications/hangout_ringtone.m4a");
			notification.sound = Uri.withAppendedPath(
					Audio.Media.INTERNAL_CONTENT_URI, "6");
			//notification.sound = Uri.parse("android.resource://"
					//+ getPackageName() + "/" + R.raw.koko);
			// ��????????????�???

			// ??????
			notification.defaults |= Notification.DEFAULT_VIBRATE; // ??????????????????? �???
																	// try catch
			// long[] vibrate = {0,100,200,900,100};
			// notification.vibrate = vibrate;

			// ?????????
			notification.defaults |= Notification.DEFAULT_LIGHTS; // 隞���??????
			// notification.ledARGB = 0xff00ff00;
			// notification.ledOnMS = 300;
			// notification.ledOffMS = 1000;
			// notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//			notification.setLatestEventInfo(this, "MeetDay ", message, contentIndent);
			// ???i??????��???????Notification
			notificationManager.notify(1, notification);
			//i++;
			//wl.release();		
			// --
		} catch (Exception e) {

		}
	}
	/*
	public  void sendNotification_test(Context caller, Class<?> activityToLaunch, String title, String msg, int numberOfEvents,boolean sound, boolean flashLed, boolean vibrate,int iconID) {
	    NotificationManager notifier = (NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);

	    final Notification notify = new Notification(iconID, "", System.currentTimeMillis());

	    //Builder builder = new Notification.Builder(this);

	    try {
	    	   		
		    notify.icon = iconID;
		    notify.tickerText = title;
		    notify.when = System.currentTimeMillis();
		    notify.number = numberOfEvents;
		    notify.flags |= Notification.FLAG_AUTO_CANCEL;
		    if (sound) notify.defaults |= Notification.DEFAULT_SOUND;
	
		    if (flashLed) {
		    // add lights
		        notify.flags |= Notification.FLAG_SHOW_LIGHTS;
		        notify.ledARGB = Color.CYAN;
		        notify.ledOnMS = 500;
		        notify.ledOffMS = 500;
		    }
	
		    if (vibrate) {
		        notify.vibrate = new long[] {100, 200, 300};
		    }
	
		    //Intent toLaunch = new Intent(caller, HelperConnectPage.class);
		    //toLaunch.putExtra(HelperConnectPage.HELPER_ROOMID, Const.connectid);
		    //toLaunch.putExtra(HelperConnectPage.HELPER_MESSAGE, msg);
		    //toLaunch.putExtra(HelperConnectPage.HELPER_ASKERPHOTO, helperPhotoUrl);
		    //toLaunch.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		    //toLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    //toLaunch.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		    //PendingIntent intentBack = PendingIntent.getActivity(caller, 0, toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
		    
		    //notify.setLatestEventInfo(caller, title, msg, intentBack);   
		    notifier.notify(1, notify);
		    i = i+1;
		    //startActivity(toLaunch);
		    
		    
		    
	    }catch (Exception e) {

		}
	}
	
	private void sendNotification_old(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainPage.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.person_30x30)
				.setContentTitle("GCM Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
	}
	
	private Const.eNotifyType parsenotification(String message){
		//String rmsg = null;
		char tmp[] = new char[30];
		char lst[] = new char[200];
		//Log.d(this.getClass().getName(),message);
		message.getChars(0, message.indexOf('%'), tmp, 0);
		message.getChars(message.indexOf('%')+1, message.length(), lst, 0);
		Const.eNotifyType eType = Const.eNotifyType.valueOf(String.valueOf(tmp).trim());
		return eType;

	}
	*/
	
	
	private void RetrieveDataFromNotify(String message, Context context){
		String[] para;
		String content;
		//int i = 0;
		//boolean ret = false;
		char temp[] = new char[30];
		char data[] = new char[message.length()];
		
		message.getChars(0, message.indexOf('%'), temp, 0);	
		message.getChars(message.indexOf('%')+1, message.length(), data, 0);
		Const.eNotifyType eType = Const.eNotifyType.valueOf(String.valueOf(temp).trim());
		
		content = String.valueOf(data).trim();
		/*int num = content.length() - content.replace("%", "").length();
		para = new String[num];
		
		while(content.contains("%")){
			int tmp = content.indexOf("%");
			String str = content.substring(0, tmp);
			para[i] = str;
			content = content.substring(tmp+1);			
			Log.d(this.getClass().getName()+"RetrieveDataFromNoti",para[i]);
			i++;
		}*/
		
		para = FileAccess.parseDataToPara(content, "%");
		
		//Log.d(this.getClass().getName()+"RetrieveDataFromNoti",para[0]);
		Log.d(this.getClass().getName()+"RetrieveDataFromNoti",String.valueOf(eType));
		if(Const.userinfo.get_current_state() == Const.eState.State_RequestConnect){
			if(eType!=Const.eNotifyType.NotifyType_AckConnectOn){
				Const.userinfo.set_current_state(Const.eState.State_Idle);
			}
		}
		switch(eType){	
			case NotifyType_AskConnect:
				if(Const.userinfo.get_current_state() == Const.eState.State_Idle){
					HelperSendAck(para, context);
					Const.userinfo.set_current_state(Const.eState.State_RequestConnect);
					//HelperPageStart(para, context);
				}
					
			break;
			case NotifyType_RespondConnect:
				if(Const.userinfo.get_current_state() == Const.eState.State_AskCallForHelp){
					if(true == HelperRespond(para, context)){
						Log.d(this.getClass().getName(), para[2]);
						Intent broadcastIntent  = new Intent();
						broadcastIntent.setAction(AskerConnectPage.BROADCAST_GETHELPRESPONSE);					
						broadcastIntent.putExtra("String", "Helper respose");
						broadcastIntent.putExtra(AskerConnectPage.ASKER_REMOTERESOLUTIONX, para[2]);
						broadcastIntent.putExtra(AskerConnectPage.ASKER_REMOTERESOLUTIONY, para[3]);
						sendBroadcast(broadcastIntent);	
						Log.i(TAG, "Received: True");
					}else{
						Const.userinfo.set_current_state(Const.eState.State_Idle);
						DBHelper dbhelper = new DBHelper(context);
						if(Const.hisinfo.ladapter!=null)
							Const.hisinfo.ladapter.onUpdate(context, dbhelper, Const.hisinfo.getTableName());
						//Const.userinfo.Location_Type = Const.eLocation.Location_Record;
						Intent broadcastIntent  = new Intent();
						broadcastIntent.setAction(AskerConnectPage.BROADCAST_GETHELPRESPONSE);					
						broadcastIntent.putExtra("String", "Stop");
						//broadcastIntent.putExtra(AskerConnectPage.ASKER_REMOTERESOLUTIONX, para[2]);
						//broadcastIntent.putExtra(AskerConnectPage.ASKER_REMOTERESOLUTIONY, para[3]);
						sendBroadcast(broadcastIntent);	
						Log.i(TAG, "Received: False");
					}
			    }
				break;
			case NotifyType_DisConnect:
				if(Const.userinfo.get_current_state() == Const.eState.State_RespondConnect &&
						HelperConnectPage.helpact!= null ){
					Log.i(TAG, "Received: NotifyType_DisConnect"+Const.userinfo.get_current_state());
					StopConnect(context);
				} else if (	Const.userinfo.get_current_state() == Const.eState.State_WaitAsker || 
						Const.userinfo.get_current_state() == Const.eState.State_RequestConnect ){
					//Const.userinfo.State_Type = Const.eState.State_Idle; 
					Const.userinfo.set_current_state(Const.eState.State_Idle);
				} else if ( Const.userinfo.get_current_state() == Const.eState.State_Connecting){
					CancelPreview();
				}
		
				break;
			case NotifyType_AddFriend:
				AddfriendNotification(para, context);
				break;
			case NotifyType_RequestConnect:
				if(Const.userinfo.get_current_state() == Const.eState.State_Idle){
					HelperSendAck(para, context);
				}
				break;
			case NotifyType_AckRequest:
				if(Const.userinfo.get_current_state() == Const.eState.State_WaitAck){
					Const.userinfo.set_current_state(Const.eState.State_AskCallForHelp);
					//Const.userinfo.State_Type = Const.eState.State_AskCallForHelp;
					if(AskerConnectPage.aCounter_NoAck!=null)
						AskerConnectPage.aCounter_NoAck.cancel();
					AskerSendAck(para, context);
					//HelperSendAck(para, context);
				}
				break;	
			case NotifyType_AckConnectOn:
				if(Const.userinfo.get_current_state() == Const.eState.State_RequestConnect){
					HelperPageStart(para, context);
				}			
				break;
			case NotifyType_ConnectedOff:
				if(Const.userinfo.get_current_state() == Const.eState.State_Connecting){
					//HelperSendAck(para, context);
					//HelperPageStart(para, context);
					CancelPreview();
				}
					
			break;
			case NotifyType_Thanks:
				ThanksNotification(para, context);					
			break;
			case NotifyType_Logout:
				DoLogout(context, content);
			break;
			case NotifyType_SendMsg:
				GetMessage(para, context);
			break;
			default:
				break;
		}
		
		//return ret;
	}
	
	private void DoLogout(Context context, String content){
		String phone_list = FileAccess.getStringFromPreferences(context, "null", 
    			Const.eUsrType.UsrType_PhoneList, Const.projinfo.sSharePreferenceName);
		String uid = FileAccess.getStringFromPreferences(context, "null",
				Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
		if(phone_list.equals("null") || !content.equals(uid)){
			return;
		}	
		FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_Pass, 
				Const.projinfo.sSharePreferenceName);
		if(Const.userinfo.get_front_running()){
			Intent broadcastIntent  = new Intent();
			broadcastIntent.setAction(MainPage.BROADCAST_GETHELPRESPONSE);					
			sendBroadcast(broadcastIntent);	
			//Intent intent = new Intent();
			//intent.setClass(context, StartPage.class);
			//intent.putExtra(StartPage.Start_type, "Unbind");
			//intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);	
			//context.startActivity(intent);
		} else {
			FileAccess.deleteStringInPreferences(context, Const.eUsrType.UsrType_FreindList, 
					Const.projinfo.sSharePreferenceName);
		}
	}
	
	/*
	 * para[0]:senderId 
	 * para[1]:recid
	 * para[2]:msg
	 * para[3]:roomid
	 * para[4]:pattern
	 */
	private void ThanksNotification(String[] para, final Context context){		
		DBHelper dbhelper = new DBHelper(this);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		String show_name = //dbhelper.getNamefromSql(dbrw, Const.hisinfo.getTableName(), para[0]);
				dbhelper.getdatafromSql(dbrw, Const.hisinfo.getTableName(), "name"," fid", para[0]);
		NotificationAccess.sendNotification(context, Const.eNotifyType.NotifyType_Thanks, 
				show_name+":"+para[2]);
		Date date = new Date();
		long timeMilli = date.getTime();
		
		//Log.d("XXXX,",para[3]);
		//String strName = dbhelper.getNamefromSql(dbrw, Const.hisinfo.getTableName(), recid);
		Const.SQL_data data = new Const.SQL_data(show_name, para[0], 
				Long.toString(timeMilli), String.valueOf(Const.History_Type.HType_RecieveThanks),para[3],
				para[2], para[4]);
		dbhelper.insertData(dbrw, Const.hisinfo.getTableName(), data);
		//Const.SQL_data data_card = new Const.SQL_data(para[3], para[2], "0");
		//dbhelper.insertData(dbrw, Const.giftcarddb, data_card);
		if(Const.hisinfo.ladapter!=null)
			Const.hisinfo.ladapter.onUpdate(context, dbhelper, Const.hisinfo.getTableName());
	}
	
	private void AddfriendNotification(String[] para, final Context context){		

		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		
		if(para[0].equals(FileAccess.getStringFromPreferences(context, "", 
    			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName))){
			Log.e(TAG, "Can't Add youself!!");
		} else {
			Const.SQL_data data = new Const.SQL_data(para[1], para[0]);
			dbhelper.insertData(dbrw, Const.contactinfo.getTableName(), data);
			
			String flst = FileAccess.getStringFromPreferences(context, "", 
	    			Const.eUsrType.UsrType_FreindList, Const.projinfo.sSharePreferenceName);
			
			flst = flst + para[0]+"/";
			
			FileAccess.putStringInPreferences(context, flst, 
	    			Const.eUsrType.UsrType_FreindList, Const.projinfo.sSharePreferenceName);
			
	        //Const.userinfo.fnum++;   
			if(Const.contactinfo.ladapter!=null){
				Const.contactinfo.ladapter.onUpdate(context, dbhelper, Const.contactinfo.getTableName());
				Const.contactinfo.ladapter.notifyDataSetChanged();
			}

			dbrw.close();
		}
		
	}
	
	private void GetMessage(String[] para, final Context context){		

		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		Const.SQL_data data;

		NotificationAccess.sendNotification(context, Const.eNotifyType.NotifyType_SendMsg,
				para[0]+": " + para[1]);
		
		Date date = new Date();
		long timeMilli = date.getTime();
		
		data = new Const.SQL_data(para[0], para[2], String.valueOf(timeMilli),
				String.valueOf(Const.History_Type.Htype_GetMessage),"0",para[1], "1");

		dbhelper.insertData(dbrw, Const.hisinfo.getTableName(), data);

		Intent broadcastIntent  = new Intent();
		broadcastIntent.setAction(ChatActivity.BROADCAST_GETMESSAGE_CALLBACK);
		broadcastIntent.putExtra("fid", para[2]);
		sendBroadcast(broadcastIntent);

		if(Const.hisinfo.ladapter!=null){
			//Log.i(TAG, "dfgdgf");
			Const.hisinfo.ladapter.onUpdate(context, dbhelper, Const.hisinfo.getTableName());
		}
		dbrw.close();
		
	}
	
    
	
	private void StopConnect(Context context){
		
		String show_name="";// = HelperConnectPage.showname;
		show_name = HelperConnectPage.showname;
		/*if(HelperConnectPage.helpact!=null){
			show_name = HelperConnectPage.showname;
			if(HelperConnectPage.vib != null){
				HelperConnectPage.vib.cancel();
				//HelperConnectPage.vib = null;
			}
			//HelperConnectPage.vib.cancel();
			HelperConnectPage.mp.stop();
			HelperConnectPage.kl.reenableKeyguard();				
			//Const.userinfo.State_Type = Const.eState.State_Idle;
			HelperConnectPage.aCounter.cancel();
			HelperConnectPage.helpact.finish();
		}*/
		
		//MainPage.Mainact.finish();

		NotificationAccess.sendNotification(context, Const.eNotifyType.NotifyType_MissedCall, 
				show_name+": Missed Call");
		
		DBHelper dbhelper = new DBHelper(this);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		ContentValues cv = new ContentValues();	
		cv.put("type", String.valueOf(Const.History_Type.HType_Missed));
		String[] colum = { "_id"};
		Cursor c = dbrw.query(Const.hisinfo.getTableName(), colum, null, null, null, null, null);
		if (c.getCount() > 0) {
			c.moveToLast();
		} 
		dbhelper.updateData(dbrw, Const.hisinfo.getTableName(), c.getString(0), cv);		
		
		if(Const.hisinfo.ladapter!=null){
			//Log.i(TAG, "dfgdgf");
			Const.hisinfo.ladapter.onUpdate(context, dbhelper, Const.hisinfo.getTableName());
		}
		dbrw.close();
		//Log.i(TAG, "hgfhgfhf");
		if(Const.userinfo.get_front_running()== false){
			Log.d(this.getClass().getName(),"StopConnect");
			Intent Intent = new Intent("android.intent.action.MAIN");
			Intent.setClass(context, WelcomPage.class);
			Intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);		
			//context.startActivity(Intent);
		} else {
			//Log.i(TAG, "ghfghfh");
			//Log.d(this.getClass().getName(),"AAAAAAAAAAAAAAA");
			//MainPage.Mainact.finish();
			//context.startActivity(Intent);
		}
		Const.userinfo.set_current_state(Const.eState.State_Idle);
					
		Intent broadcastIntent  = new Intent();
		broadcastIntent.setAction(HelperConnectPage.BROADCAST_GETHELPRESPONSE);					
		sendBroadcast(broadcastIntent);	
	}
	
	/*
	 * para[0]:Roomid (connect id to apprtc)
	 * para[1]:1/0
	 * para[2]:X
	 * para[3]:Y
	 */
	private boolean HelperRespond(String []para, Context context){
		boolean ret = false;
		String roomID = "";	
		roomID = para[0];			
		if(!roomID.equals(AskerConnectPage.roomId)){
			Log.d(this.getClass().getName(),roomID+"/"+AskerConnectPage.ASKER_ROOMID);
			ret = false;
		} else {
			if(para[1].equals("1")){
				//Const.userinfo.State_Type = Const.eState.State_Connecting;
				ret = true;			
			}else{
				//Const.userinfo.State_Type = Const.eState.State_Idle;
				ret = false;
			}
		}
		
		if(AskerConnectPage.mp != null){
			AskerConnectPage.mp.stop();
			//AskerConnectPage.mp = null;
		}

		Intent Intent = new Intent("android.intent.action.MAIN");
		Intent.setClass(context, MainPage.class);
		Intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);			
		if(AskerConnectPage.aCounter_NoAnswer!=null)
			AskerConnectPage.aCounter_NoAnswer.cancel();
		if(AskerConnectPage.askact != null)
			AskerConnectPage.askact.finish();
		if(ret == false){		
			//context.startActivity(Intent);
		}
		return ret;
	}
	
	private void CancelPreview(){
		Intent broadcastIntent  = new Intent();
		broadcastIntent.setAction(AskerConnectPage.BROADCAST_GETHELPRESPONSE);					
		broadcastIntent.putExtra("String", "Stop");
		//broadcastIntent.putExtra(AskerConnectPage.ASKER_REMOTERESOLUTIONX, para[2]);
		//broadcastIntent.putExtra(AskerConnectPage.ASKER_REMOTERESOLUTIONY, para[3]);
		sendBroadcast(broadcastIntent);	
		broadcastIntent  = new Intent();
		broadcastIntent.setAction(CallActivity.BROADCAST_GETHELPRESPONSE);
		sendBroadcast(broadcastIntent);
	}
	
	private void HelperSendAck(String []para, Context context){		
		ConnectToOther con = new ConnectToOther(context);		
		con.SendAck(para);
	}
	
	private void AskerSendAck(String []para, Context context){		
		ConnectToOther con = new ConnectToOther(context);		
		con.SendConnectOn(para);
	}
	/*
	 * para[0]:Sender id
	 * para[1]:Your id
	 * para[2]:Roomid (connect id to apprtc)
	 * para[3]:Nickname
	 * para[4]:TimeinMili
	 * para[5]:X
	 * para[6]:Y
	 * para[7]:Message 
	 */
	private void HelperPageStart(String []para, Context context){

		String msg = para[7];
		String uid = null;
		Const.SQL_data data;
		//Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		//Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		//calendar.setTimeInMillis(Long.parseLong(para[4]));
		//Log.i(TAG, "HelperPageStart @ " + SystemClock.elapsedRealtime());
		//if(Const.userinfo.uid == null){
		uid = FileAccess.getStringFromPreferences(getBaseContext(), null,
				Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
		//} else {
			//uid = Const.userinfo.uid;
		//}	
		if(!uid.equals(para[1])){
			return;
		}
		
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		
		/*String localname = dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(), para[0]);
		if(localname.equals(para[3]) != true){
			Log.i(TAG, "HelperPageStart @ " + localname+para[3]);
			ContentValues cv = new ContentValues();	
			cv.put("name", para[3]);
			dbhelper.updateData(dbrw, Const.contactinfo.getTableName(), localname, cv);
	        Const.contactinfo.ladapter.onUpdate(dbhelper.getnumfromSql(dbrw, 
	        		Const.contactinfo.getTableName()), 
	        		context, dbhelper, Const.contactinfo.getTableName());
		}*/
		//} 
		
		
		Date date = new Date();
		long timeMilli = date.getTime();
		Log.d(this.getClass().getName()+"HelperConnectNotify",timeMilli+"/"+Long.parseLong(para[4]));
		if(0==1){//((timeMilli-Long.parseLong(para[4]))>20000){
			Log.d(this.getClass().getName()+"HelperConnectNotify",timeMilli+"/"+Long.parseLong(para[4]));
			data = new Const.SQL_data(para[3], para[0], para[4],
					String.valueOf(Const.History_Type.HType_Missed), para[2], para[7]);
		} else {
			/*
			//String title, String msg, int numberOfEvents,boolean sound, boolean flashLed, boolean vibrate,int iconID
			sendNotification_test(context, 
									HelperConnectPage.class,
									"MeetDay ",
									msg,
									1,
									true,
									false,
									true,
									0
									);
			*/
			data = new Const.SQL_data(para[3], para[0], para[4],
					String.valueOf(Const.History_Type.HType_Incoming),para[2], para[7]);
			PowerManager pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
			@SuppressWarnings("deprecation")
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | 
					PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
			//獲取PowerManager.WakeLock對象,後面的參數|表示同時傳入兩個值,最後的是LogCat裡用的Tag		
			//得到鍵盤鎖管理器對象	
			Log.d(this.getClass().getName()+"HelperConnectNotify","wlaq");
			wl.acquire();

			Const.Connect_Info.screen_size size = Const.getLocalRes(context);
			
			Intent alarmIntent = new Intent(this, HelperConnectPage.class);//("android.intent.action.MAIN");
		
	        //alarmIntent.setClass(context, HelperConnectPage.class);
	        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        //alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        //alarmIntent.putExtra("AlarmID", intent.getIntExtra("AlarmID", -1));
	        alarmIntent.putExtra(HelperConnectPage.HELPER_LOCALRESOLUTIONX, String.valueOf(size.x));
	        alarmIntent.putExtra(HelperConnectPage.HELPER_LOCALRESOLUTIONY, String.valueOf(size.y));
	        alarmIntent.putExtra(HelperConnectPage.HELPER_REMOTERESOLUTIONX, para[5]);
	        alarmIntent.putExtra(HelperConnectPage.HELPER_REMOTERESOLUTIONY, para[6]);
	        alarmIntent.putExtra(HelperConnectPage.HELPER_USERID, uid);
	        alarmIntent.putExtra(HelperConnectPage.HELPER_ASKERID, para[0]);
	        alarmIntent.putExtra(HelperConnectPage.HELPER_ROOMID, para[2]);
	        alarmIntent.putExtra(HelperConnectPage.HELPER_MESSAGE, msg);
	        alarmIntent.putExtra(HelperConnectPage.HELPER_ASKERNAME, para[3]);
	        alarmIntent.putExtra(HelperConnectPage.HELPER_ASKERPHOTO, 
	        		Const.projinfo.sServerAdr+"upload/user_"+para[0]+".jpg");
	        // Start the popup activity              
	        wl.release();
	        Log.d(this.getClass().getName()+"HelperConnectNotify","wlrl");
	        if(Const.userinfo.get_front_running()){
	        	if(MainPage.isON != true)
	        		Log.d(this.getClass().getName()+"HelperConnectNotify","MainPage.isON Not");
	        	else
	        		Log.d(this.getClass().getName()+"HelperConnectNotify","MainPage.isON");
	        	//MainPage.Mainact.finish();
			}
	        
			dbhelper.insertData(dbrw, Const.hisinfo.getTableName(), data);
			dbrw.close();
	        
	        try {
	        	Const.userinfo.set_current_state(Const.eState.State_RespondConnect);
	        	context.startActivity(alarmIntent);    	        	
	        } catch (Exception e) { 	
			}
	        
		}
		

		
	}
	
}

