package com.project1.meetday;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project1.apprtc.ConnectProcessing;
import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.ImageLoader;
import com.project1.http.NotificationAccess;
import com.project1.http.PictUtil;

@SuppressWarnings("deprecation")
public class HelperConnectPage extends Activity{

	public static Activity helpact;
	
	//	private ImageView personalImage;
	public static final String HELPER_ROOMID =
	    "com.project1.helper.ROOMID";
	public static final String HELPER_MESSAGE =
		    "com.project1.helper.MESSAGE";
	public static final String HELPER_ASKERPHOTO =
		    "com.project1.helper.ASKERPHOTO";
	public static final String HELPER_ASKERNAME =
		    "com.project1.helper.ASKERNAME";
	public static final String HELPER_ASKERID =
		    "com.project1.helper.ASKERID";
	public static final String HELPER_USERID =
		    "com.project1.helper.USERID";
	public static final String HELPER_LOCALRESOLUTIONX =
		    "com.project1.helper.HELPER_LOCALRESOLUTIONX";
	public static final String HELPER_LOCALRESOLUTIONY =
		    "com.project1.helper.HELPER_LOCALRESOLUTIONY";
	public static final String HELPER_REMOTERESOLUTIONX =
		    "com.project1.helper.HELPER_REMOTERESOLUTIONX";
	public static final String HELPER_REMOTERESOLUTIONY =
		    "com.project1.helper.HELPER_REMOTERESOLUTIONY";
	public static final String BROADCAST_GETHELPRESPONSE =
			"com.project1.helper.GETHELPRESPOSE";
	public static CountDownTimer aCounter = null;
	public static Vibrator vib = null;
	public static MediaPlayer mp = null;
	public static KeyguardLock kl = null;
	public static String showname = null;

	private LinearLayout lilayoutHelpConnect;
	private ImageView personalPhotoImage = null;
	private TextView messageTxt = null;
	private TextView shownameTxt = null;
	private Bitmap bmpConnectBtn = null, bmpDisconnectBtn = null;
	private ImageButton disconnectButton = null;
	private ImageButton connectButton = null;
	private String roomId = null;
	private String message = null;
	//private String showname = null;
	private String askerPhoto = null;
	private String conusrid = null;
	private String userid = null;
	//private 
	private KeyguardManager km;
	private Const.Connect_Info coninfo = null;

	//private KeyguardLock kl;
	//private Vibrator vib;
	//private MediaPlayer mp;
	private IntentFilter mIntentFilter;   
	private ConnectProcessing connectProcessing; 
	
	private final String TAG = "HelperConnectPage";
	
//	private GifView gif1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);

	   if(Const.userinfo.get_current_state()== Const.eState.State_Idle){
		   HelperConnectPage.this.finish();
		   Intent i = new Intent();
		    i.setClass(HelperConnectPage.this, WelcomPage.class);
			i.putExtra(MainPage.MAINPAGE_LOCATION, String.valueOf(Const.eLocation.Location_Record));
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			return;
	   }
		   //return;
	   
	   helpact = this;
	   
	    // Get setting keys to callActivity.
	    connectProcessing = new ConnectProcessing(helpact);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.helperconnect_page);	   
	    
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BROADCAST_GETHELPRESPONSE);
		registerReceiver(mResponse, mIntentFilter);	
	    
	    Log.d(this.getClass().getName()+"HelperConnectNotify","HelperConnectPage");
	    
        long[] vibrate1 = new long[] { 10, 2000, 1500};
	    mp = MediaPlayer.create(this, R.raw.sound_clip);
	    vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    vib.vibrate(vibrate1, 0);
	    mp.setLooping(true);
	    mp.setVolume(1f, 1f);
	    mp.start();
	    /* ���eyGuardManager撠情 */
        km = (KeyguardManager)this.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);     

        /* ���eyguardLock撠情 */
        kl = km.newKeyguardLock(TAG);               
        kl.disableKeyguard();

		lilayoutHelpConnect = (LinearLayout)findViewById(R.id.lilayoutHelpConnect);
	    personalPhotoImage = (ImageView)findViewById(R.id.personal_photo);
	    messageTxt = (TextView)findViewById(R.id.helperProblemContent);
	    messageTxt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BELLB.TTF")); 
	    shownameTxt = (TextView)findViewById(R.id.helperShowNickname);
	    shownameTxt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BELLB.TTF")); 
	    disconnectButton = (ImageButton)findViewById(R.id.helperDisconnectButton);
	    connectButton = (ImageButton)findViewById(R.id.helperCallButton);
	    
	    
	    // Get Intent parameters.
	    final Intent intent = getIntent();
	    roomId = intent.getStringExtra(HELPER_ROOMID);
	    message = intent.getStringExtra(HELPER_MESSAGE);
	    askerPhoto = intent.getStringExtra(HELPER_ASKERPHOTO);
	    showname = intent.getStringExtra(HELPER_ASKERNAME);
	    conusrid = intent.getStringExtra(HELPER_ASKERID);
	    userid = intent.getStringExtra(HELPER_USERID);
	    coninfo = new Const.Connect_Info(Integer.valueOf(intent.getStringExtra(HELPER_LOCALRESOLUTIONX)),
	    		Integer.valueOf(intent.getStringExtra(HELPER_LOCALRESOLUTIONY)),
	    		Integer.valueOf(intent.getStringExtra(HELPER_REMOTERESOLUTIONX)),
	    		Integer.valueOf(intent.getStringExtra(HELPER_REMOTERESOLUTIONY)));
	    coninfo.uid = userid;
	    coninfo.rid = conusrid;
		coninfo.remotename = showname;
	    //KeyguardManager km = intent.get
    	ImageLoader mImageLoader = new ImageLoader(this);
		mImageLoader.DisplayImage(askerPhoto,personalPhotoImage,false);
		
		messageTxt.setText(message);
		shownameTxt.setText(showname);
				 
		aCounter = new CountDownTimer(33000,1000){
            
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
            	HelpRespondConnect(false);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                //mTextView.setText("seconds remaining:"+millisUntilFinished/1000);
            }
            
        };
        aCounter.start();
		
        allocateBmp();
        
		connectButton = (ImageButton) findViewById(R.id.helperCallButton);
		connectButton.setImageBitmap(bmpConnectBtn);
		connectButton.setOnClickListener(new OnClickListener(){
			@Override

			public void onClick(View arg0) {	
				aCounter.cancel();
				HelpRespondConnect(true);
			}
        });
		
		disconnectButton = (ImageButton) findViewById(R.id.helperDisconnectButton);
		disconnectButton.setImageBitmap(bmpDisconnectBtn);
		disconnectButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {	
				aCounter.cancel();
				HelpRespondConnect(false);
			}
        });
		
//		gif1 = (GifView)findViewById(R.id.gifview01);
//	    gif1.setGifImage(R.drawable.flying);
	    
	}
	
	@Override
	public void onBackPressed() {
		aCounter.cancel();
		HelpRespondConnect(false);
	}
	
	@Override
    protected void onDestroy() {
		unregisterReceiver(mResponse);
		//NotificationAccess.removenotification(this);
		releaseBmp();
		releaseViews();
		System.gc();
		Log.i(TAG, "onDestory()............");
        super.onDestroy(); 
        return;
    }

	private void allocateBmp() {
		bmpConnectBtn = PictUtil.getLocalBitmap(this, R.drawable.pickup, 2);
		bmpDisconnectBtn = PictUtil.getLocalBitmap(this, R.drawable.pickoff, 2);
	}	
	
	private void releaseBmp() {
		if(bmpConnectBtn != null){
			bmpConnectBtn.recycle();
		}
		if(bmpDisconnectBtn != null){
			bmpDisconnectBtn.recycle();
		}	
	}	
	
	private void releaseViews() {
		if(messageTxt != null){
			messageTxt.setBackground(null);
		}
		if(personalPhotoImage != null){
			personalPhotoImage.setImageResource(0);
			personalPhotoImage.setImageBitmap(null);
		}			
		if(connectButton != null) {
			connectButton.setImageBitmap(null);
			connectButton.setBackground(null);
		}
		if(disconnectButton != null) {
			disconnectButton.setImageBitmap(null);
			disconnectButton.setBackground(null);
		}
		if(lilayoutHelpConnect != null) {
			lilayoutHelpConnect.setBackground(null);
		}
	}	
	
	@Override
	protected void onStart(){
		NotificationAccess.sendNotification(this, Const.eNotifyType.NotifyType_Ringing_Callee,
				showname + ":" + getResources().getString(R.string.hist_incoming));
		//showname + ":"+"Incoming call");
		super.onStart();
	}
	
    @Override
    protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause()............");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()............");
    }
    
	@Override
	protected void onStop() {		
		//unregisterReceiver(mResponse);
		//finish();
		Log.i(TAG, "onStop()............");
	    super.onStop();
	}
	
	@Override
	protected void onRestart() {
		//unregisterReceiver(mResponse);
		Log.i(TAG, "onRestart()............");
	    super.onRestart();
	}
	
	private void HelpRespondConnect(boolean connect){
		//String uid = null;
		mp.stop();
		vib.cancel();
		vib = null;
		mp = null;
		kl.reenableKeyguard();
		ConnectToOther con = new ConnectToOther(getBaseContext());          
		//Const.Connect_Info.screen_size size = Const.getLocalRes(getBaseContext());
		con.SendRespond(connect, roomId, userid, conusrid, Const.hisinfo.getTableName(), 
				coninfo.local_size);
				
		HelperConnectPage.this.finish();
		if(true == connect){
			//To be helper
			//this.finish();	
			coninfo.roomid = roomId;
			connectProcessing.connectToRoom(roomId, 0, 2, coninfo);
			Const.userinfo.set_current_state(Const.eState.State_Connecting);
		} else {
			Intent i = new Intent();	
    		DBHelper dbhelper = new DBHelper(this);
    		if(Const.hisinfo.ladapter!=null)
    			Const.hisinfo.ladapter.onUpdate(this, dbhelper, Const.hisinfo.getTableName());	
			if(Const.userinfo.get_front_running() == false){
				//this.finish();
				HelperConnectPage.this.finish();
				i.setClass(HelperConnectPage.this, WelcomPage.class);
				i.putExtra(MainPage.MAINPAGE_LOCATION, String.valueOf(Const.eLocation.Location_Record));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			} else if (MainPage.isON == false){
				//this.finish();
				if(MainPage.Mainact != null)
					MainPage.Mainact.finish(); 
				i.setClass(HelperConnectPage.this, MainPage.class);
				i.putExtra(MainPage.MAINPAGE_LOCATION, String.valueOf(Const.eLocation.Location_Record));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);	
				//this.finish();
			} else {

			}				
			Const.userinfo.set_current_state(Const.eState.State_Idle);
			NotificationAccess.removenotification(this);
		}
		//finish();
		
	}
	
	private BroadcastReceiver mResponse = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(BROADCAST_GETHELPRESPONSE)) {
				aCounter.cancel();
				mp.stop();
				vib.cancel();
				//vib = null;
				//mp = null;
				kl.reenableKeyguard();
				finish();
				return;		
			} 				
		
		}
	};   	
	

}
