package com.project1.meetday;

import com.project1.http.CommonUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Recieve_BootCompleted extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        //we double check here for only boot complete event
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
           //here we start the service
            Intent gcmServiceIntent = new Intent(context, GCMBootService.class);
            context.startService(gcmServiceIntent);
//           Intent activityIntent = new Intent(context, MainPage.class);
//           activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//           context.startActivity(activityIntent);
       }		

	}

}
