package com.project1.meetday;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.project1.http.Const;

public class MainPage extends FragmentActivity {
	//implements AskerPage.AskerServerEvent {
	public static final String MAINPAGE_LOCATION =
		    "com.project1.main.LOCATION";
	public static final String BROADCAST_GETHELPRESPONSE =
			"com.project1.start.GETHELPRESPOSE";
	
	public static boolean isON = false;
	public static Activity Mainact = null;
	//public static final String Location = "com.project1.MainPage.Location";
	
	private String TAG = "MainPage";
	private TabHost mTabHost;
    private TabManager mTabManager; 
    private Const.eLocation mLocation = Const.eLocation.Location_Empty;
    private IntentFilter mIntentFilter; 
    AlertDialog.Builder ReplyAdd_dialog;
    AlertDialog dialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.main_page);
		
		Mainact = this;
		getApplicationContext();
		Const.userinfo.set_front_running(true);
		Const.userinfo.set_current_state(Const.eState.State_Idle);
		//Const.userinfo.State_Type = Const.eState.State_Idle;
		
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BROADCAST_GETHELPRESPONSE);
		registerReceiver(mResponse, mIntentFilter);	
		
		//Toast.makeText(this,
				//"Open?" + Const.dbrw.isOpen() + "Ver:" + Const.dbrw.getVersion(),
				//Toast.LENGTH_SHORT).show();
		
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
        //Mark next line to hide the experts page
        //mTabManager.addTab(mTabHost.newTabSpec("hepler").setIndicator(null,this.getResources().getDrawable(R.drawable.helper_tabfront)),HelperPage.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("asker").setIndicator(null,this.getResources().getDrawable(R.drawable.asker_tabfront)),AskerPage.class, null);      
        mTabManager.addTab(mTabHost.newTabSpec("history").setIndicator(null,this.getResources().getDrawable(R.drawable.history_tabfront)),HistoryPage.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("setting").setIndicator(null,this.getResources().getDrawable(R.drawable.setting_tabfront)),SettingPage.class, null);

        //Log.d(TAG, getIntent().getStringExtra(Location));         
        final Intent intent = getIntent();
        mLocation = Const.eLocation.valueOf(intent.getStringExtra(MAINPAGE_LOCATION));
        
        
        if(mLocation == Const.eLocation.Location_Record){
        	mTabHost.setCurrentTab(1);
        } else if(mLocation == Const.eLocation.Location_Setting){
        	mTabHost.setCurrentTab(2);
        } else if(mLocation == Const.eLocation.Location_Contact){
        	mTabHost.setCurrentTab(0);
        } else {
        	mTabHost.setCurrentTab(0);
        }
        setTabColor(mTabHost);
        
        DisplayMetrics dm = new DisplayMetrics();   
        getWindowManager().getDefaultDisplay().getMetrics(dm); 
        int screenWidth = dm.widthPixels;   
           
           
        TabWidget tabWidget = mTabHost.getTabWidget();   //���雓�鞊莎�揭otab���雓�鞊莎�揭���雓�鞊莎�揭���雓�鞊莎�揭
        int count = tabWidget.getChildCount();   //���雓�鞊莎�揭otab���雓�鞊莎�揭���雓�鞊莎�揭���雓�鞊莎�揭���雓�鞊莎�揭���雓����雓�鞊莎�揭
        //if (count > 3) {   //���雓�雓Ｗ�雓����雓�嚙踐�雓嚙賢��雓����雓�嚙踝蕭嚙賢��雓�嚙賭�嚙踐�雓�蕭���雓�嚙賣�嚙踐豱�
            for (int i = 0; i < count; i++) {   
                tabWidget.getChildTabViewAt(i).setMinimumWidth((screenWidth) / count);//���雓嚙賢��雓�楊���雓�頩筑�雓嚙賢��雓�嚙賣嚙賢��揭���雓�鞊莎�揭���雓�嚙踝�嚙賣╡�雓�鞊莎�揭���雓�蕭���雓�鞊莎�揭   
            }   
        //

		//Intent startIntent = new Intent(this, ServerService.class);  
		//startService(startIntent);                 
            
		ReplyAdd_dialog = new AlertDialog.Builder(this);
		ReplyAdd_dialog.setIcon(android.R.drawable.ic_dialog_alert);
		ReplyAdd_dialog.setCancelable(false);  
		ReplyAdd_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
        public void onClick(DialogInterface dialog, int which) {  
			Intent intent = new Intent();
			intent.setClass(MainPage.this, SignInWithSocial.class);
			intent.putExtra(SignInWithSocial.SOCIAL_TYPE, "");
			intent.putExtra(SignInWithSocial.SOCIAL_ACTIVITY, Const.eSocialCmd.Social_Main);
			intent.putExtra(SignInWithSocial.SOCIAL_INOUT, Const.eSocialCmd.Social_Logout);
			//startActivity(intent);
			Intent i = new Intent();
			i.setClass(MainPage.this, StartPage.class);
			i.putExtra(StartPage.Start_type, Const.eSocialCmd.Social_Unbind);
			startActivity(i);
			MainPage.this.finish();
            }  
        }); 
            
	}
	
	public static void setTabColor(TabHost tabhost) {
	    for(int i=0;i<tabhost.getTabWidget().getChildCount();i++) {
//	        tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.background_default); //unselected
	        tabhost.getTabWidget().getChildAt(i).setBackgroundColor(0x73c3a7);
	    }
	    //tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundResource(R.drawable.background_default_select); // selected
	}
	
	/*
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
 
        if (keyCode == KeyEvent.KEYCODE_BACK) { // ���雓�嚙踐�雓����雓�鞊莎�揭^���雓�鞊莎�揭
            new AlertDialog.Builder(MainPage.this)
            .setTitle("���雓����雓�輔���雓�鞊莎�揭")
            .setMessage("���雓����雓�楊���雓�蕭���雓�鞊莎�揭���雓�鞊莎�揭���雓�鞊莎�揭���雓�嚙踐�嚙踐�雓�鞊莎�揭���雓�鞊莎�揭?")
            .setIcon(R.drawable.ic_launcher)
            .setPositiveButton("���雓����雓�楊",
            new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog,int which) {
	                finish();
	            }
            })
            .setNegativeButton("���雓�鞊莎�揭���雓�?",
            new DialogInterface.OnClickListener() {
 
                                @Override
	            public void onClick(DialogInterface dialog,
	                    int which) {
	                // TODO Auto-generated method stub
	 
	            }
            }).show();
        }
        return true;
    }

	@Override
	public void onBackPressed() {
	   Log.d(TAG, "onBackPressed Called");
	   //Const.Location_Type = Const.eLocation.Location_Empty;
	   //Const.userinfo.bFrontRunnig = false;
	   Const.userinfo.set_front_running(false);
	   finish();
	   //super.onDestroy();
	}
 */
	
	@Override
    protected void onDestroy() {
		if(dialog!=null){
			dialog.dismiss();
		}
		releaseViews();
		unregisterReceiver(mResponse);
        super.onDestroy();
        Const.userinfo.set_front_running(false);
        //PictUtil.deleteFile(FileManager.getSaveFilePath());
        //Const.dbrw.close();
        Log.i(TAG, "onDestory()............");
    }

	private void releaseViews() {
		if(mTabHost != null) {
			mTabHost.setBackground(null);
		}
	}

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()............");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isON = true;
        Log.i(TAG, "onResume()............");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()............");
    }

    @Override
    protected void onStop() {
        super.onStop();
        isON = false;
        Log.i(TAG, "onStop()............");
    }
	
    /*
	@Override
	public void onAskerPeerConnection(String usr_Id, String rec_Id, String help_content) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onAskerPeerConnection");
		Intent broadcastIntent  = new Intent();
		broadcastIntent.setAction(ServerService.BROADCAST_USERID);
		broadcastIntent.putExtra("String", usr_Id);
		sendBroadcast(broadcastIntent);	
		broadcastIntent.setAction(ServerService.BROADCAST_RECID);
		broadcastIntent.putExtra("String", rec_Id);
		sendBroadcast(broadcastIntent);	
		broadcastIntent.setAction(ServerService.BROADCAST_PROBLEM_MESSAGE);
		broadcastIntent.putExtra("String", help_content);
		sendBroadcast(broadcastIntent);	
		broadcastIntent.setAction(ServerService.BROADCAST_ASKER_CALL);
		broadcastIntent.putExtra("String", "Asker call");
		sendBroadcast(broadcastIntent);	
	}
	*/
    
	private BroadcastReceiver mResponse = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(BROADCAST_GETHELPRESPONSE)) {
				String addstring = "Your account has been logged out in other device!";
				ReplyAdd_dialog.setTitle("Log Out");
				ReplyAdd_dialog.setMessage(addstring);
				dialog = ReplyAdd_dialog.show();		
				//Intent i = new Intent();
				//i.setClass(MainPage.this, StartPage.class);
				//i.putExtra(StartPage.Start_type, "Unbind");
				//startActivity(i);
				//MainPage.this.finish();
			} 				
		
		}
	};
}
