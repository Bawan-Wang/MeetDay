package com.project1.http;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.project1.meetday.AskerConnectPage;
import com.project1.meetday.CallActivity;
import com.project1.meetday.HelperConnectPage;
import com.project1.meetday.NotificationStart;
import com.project1.meetday.R;

public class NotificationAccess {

	@SuppressWarnings("deprecation")
	public static void sendNotification(Context caller, Const.eNotifyType type, String message) {
		
		Intent intent;
		Bitmap bmpLogo = null;
		NotificationManager notifier = 
				(NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);
		Builder builder = new Notification.Builder(caller);
		
		//if(Const.uid == null || Const.bFrontRunnig == false){
			intent = new Intent(caller, NotificationStart.class);
		//} else 
			//intent = new Intent(caller, MainPage.class);
			//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//Notification notification = builder.getNotification();
			
		try {
			
			switch(type){	
			case NotifyType_Thanks:
			case NotifyType_MissedCall:
			case NotifyType_SendMsg:
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//Const.Notify_State = Const.eNotifyType.NotifyType_MissedCall;
				break;
			case NotifyType_Ringing_Caller:
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
				intent = new Intent(caller, AskerConnectPage.class);
				//notification.flags |= Notification.FLAG_NO_CLEAR;
				break;
			case NotifyType_Ringing_Callee:
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
				intent = new Intent(caller, HelperConnectPage.class);
				//notification.flags |= Notification.FLAG_NO_CLEAR;
				break;	
			case NotifyType_ConnectedOn:
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
				intent = new Intent(caller, CallActivity.class);
				break;
			default:
				break;
			}
			
//			PendingIntent contentIndent = PendingIntent.getActivity(
//					caller, 0, intent,
//					PendingIntent.FLAG_UPDATE_CURRENT);
//			builder.setContentIntent(contentIndent)
//				   .setSmallIcon(R.drawable.search)
//				   .setLargeIcon(
//						   BitmapFactory.decodeResource(caller.getResources(),
//							R.drawable.logo_icon))
//				   .setTicker(message) 
//				   .setWhen(System.currentTimeMillis())
//				   .setAutoCancel(true)
//				   .setContentTitle("MeetDay ")
//				   //.setOngoing(true)
//				   .setContentText(message);
			bmpLogo = PictUtil.getLocalBitmap(caller, R.drawable.logo_icon, 1);			
			PendingIntent contentIndent = PendingIntent.getActivity(
					caller, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
					builder.setContentIntent(contentIndent)
				   .setSmallIcon(R.drawable.search)
				   .setLargeIcon(bmpLogo)
				   .setTicker(message) 
				   .setWhen(System.currentTimeMillis())
				   //.setAutoCancel(false)
				   .setContentTitle("MeetDay ")
				   //.setOngoing(true)
				   .setContentText(message);		    			

			Notification notification = builder.getNotification();
			
			//notification.flags = Notification.FLAG_ONGOING_EVENT;
			//notification.flags |= Notification.FLAG_AUTO_CANCEL;
			if(type!= Const.eNotifyType.NotifyType_Thanks && type != Const.eNotifyType.NotifyType_MissedCall &&
					type != Const.eNotifyType.NotifyType_SendMsg){
				builder.setAutoCancel(false);
				notification.flags |= Notification.FLAG_NO_CLEAR;
			} else {
				builder.setAutoCancel(true);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.defaults |= Notification.DEFAULT_VIBRATE;
			}
			//notification.flags |= Notification.FLAG_INSISTENT;// | Notification.FLAG_AUTO_CANCEL;
			//notification.flags |= Notification.FLAG_CANCEL_CURRENT;
			//notification.setLatestEventInfo(caller, "MeetDay ", message, contentIndent);
			notifier.notify(1, notification);
			//i++;
			bmpLogo.recycle();
			System.gc();
			
	    }catch (Exception e) {

		}
	}
	
	public static void removenotification(Context con){
	    NotificationManager nMgr = 
	    		(NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
	    nMgr.cancel(1);
	}
	
}
