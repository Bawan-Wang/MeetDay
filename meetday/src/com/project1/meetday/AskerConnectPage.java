package com.project1.meetday;

import android.app.Activity;
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
import android.widget.Toast;

import com.project1.apprtc.ConnectProcessing;
import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.ImageLoader;
import com.project1.http.NotificationAccess;
import com.project1.http.PictUtil;

@SuppressWarnings("deprecation")
public class AskerConnectPage extends Activity {

	public static Activity askact;
	
//	private ImageView personalImage;
	public static final String ASKER_ROOMID =
	    "com.project1.asker.ROOMID";
	public static final String ASKER_MESSAGE =
		    "com.project1.asker.MESSAGE";
	public static final String ASKER_HELPERPHOTO =
		    "com.project1.asker.HELPERPHOTO";
	public static final String ASKER_HELPERNAME =
		    "com.project1.asker.HELPERNAME";
	public static final String BROADCAST_GETHELPRESPONSE =
			"com.project1.asker.GETHELPRESPOSE";
	public static final String ASKER_HELPERID =
		    "com.project1.asker.HELPERID";
	public static final String ASKER_LOCALID =
		    "com.project1.asker.ASKER_LOCALID";
	public static final String ASKER_LOCALRESOLUTIONX =
		    "com.project1.asker.ASKER_LOCALRESOLUTIONX";
	public static final String ASKER_LOCALRESOLUTIONY =
		    "com.project1.asker.ASKER_LOCALRESOLUTIONY";
	public static final String ASKER_REMOTERESOLUTIONX =
		    "com.project1.asker.ASKER_REMOTERESOLUTIONX";
	public static final String ASKER_REMOTERESOLUTIONY =
		    "com.project1.asker.ASKER_REMOTERESOLUTIONY";
	public static CountDownTimer aCounter_NoAnswer = null;
	public static CountDownTimer aCounter_NoAck = null;
	public static Vibrator vib = null;
	public static MediaPlayer mp = null;
	public static KeyguardLock kl = null;
	public static String roomId = null;
	
    private final String TAG = "AskerConnectPage";

	private LinearLayout lilayoutAskerConnect;
    private Context context;
	private ImageView personalPhotoImage = null;
	private TextView messageTxt = null;
	private TextView shownameTxt = null;
	private ImageButton disconnectButton = null;	
	private String message = null;
	private String helperPhoto = null;	
	private String showname = null;
	private String conusrid = null;
	private String mlocalid = null;
	private Const.Connect_Info coninfo = new Const.Connect_Info();
	//private String localx = null;
	//private String localy = null;
	//private String remotex = null;
	//private String remotey = null;
	
	private ConnectProcessing connectProcessing; 
	
	private IntentFilter mIntentFilter;    

//	private GifView gif1;
    private Bitmap bmpPickOffBtn = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);

	    askact = this;
	    this.context = getApplicationContext();
	   
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	    // Get setting keys to callActivity.
	    connectProcessing = new ConnectProcessing(askact);

	    setContentView(R.layout.askerconnect_page);	   

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BROADCAST_GETHELPRESPONSE);
		registerReceiver(mResponse, mIntentFilter);

		lilayoutAskerConnect = (LinearLayout)findViewById(R.id.lilayoutAskConnect);
	    personalPhotoImage = (ImageView)findViewById(R.id.asker_photo);
	    messageTxt = (TextView)findViewById(R.id.askerProblemContent);
	    messageTxt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 
	    shownameTxt = (TextView)findViewById(R.id.askerShowNickname);
	    shownameTxt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 
	    disconnectButton = (ImageButton)findViewById(R.id.askerDisconnectButton);

	    mp = MediaPlayer.create(this, R.raw.sound_clip);
	    mp.setLooping(true);
	    mp.setVolume(1f, 1f);
	    mp.start();
	    
	    // Get Intent parameters.
	    final Intent intent = getIntent();
	    roomId = intent.getStringExtra(ASKER_ROOMID);
	    message = intent.getStringExtra(ASKER_MESSAGE);
	    helperPhoto = intent.getStringExtra(ASKER_HELPERPHOTO);
	    showname = intent.getStringExtra(ASKER_HELPERNAME);
	    conusrid = intent.getStringExtra(ASKER_HELPERID);
	    mlocalid = intent.getStringExtra(ASKER_LOCALID);
	    coninfo.local_size.x = Integer.valueOf(intent.getStringExtra(ASKER_LOCALRESOLUTIONX));
	    coninfo.local_size.y = Integer.valueOf(intent.getStringExtra(ASKER_LOCALRESOLUTIONY));
		coninfo.remotename = showname;

	    Const.userinfo.set_current_state(Const.eState.State_WaitAck);
	    //Log.d(this.getClass().getName(), intent.getStringExtra(ASKER_LOCALRESOLUTIONX));
	    
    	ImageLoader mImageLoader = new ImageLoader(this);
		mImageLoader.DisplayImage(helperPhoto,personalPhotoImage,false);
		
		messageTxt.setText(message);
		shownameTxt.setText(showname);
		
	    Log.d(TAG, "RoomId: " + roomId);
	    Log.d(TAG, "Message: " + message);    

		//Intent closeIntent = new Intent(this, ServerService.class);  
		//stopService(closeIntent);   	    
	    
	    aCounter_NoAck = new CountDownTimer(12000,1000){
            
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
            	aCounter_NoAnswer.cancel();
            	SendDisConnect();
    												
    			Toast.makeText(context, "No Response!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                //mTextView.setText("seconds remaining:"+millisUntilFinished/1000);
            }
            
        };
        aCounter_NoAck.start();
	    
	    
		aCounter_NoAnswer = new CountDownTimer(30000,1000){
            
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
            	SendDisConnect();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                //mTextView.setText("seconds remaining:"+millisUntilFinished/1000);
            }
            
        };
        aCounter_NoAnswer.start();
		
        allocateBmp();
        
	    disconnectButton = (ImageButton) findViewById(R.id.askerDisconnectButton);
	    disconnectButton.setImageBitmap(bmpPickOffBtn);
	    disconnectButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {	
				aCounter_NoAnswer.cancel();
				SendDisConnect();
			}
        });
	    
//	    gif1 = (GifView)findViewById(R.id.gifview01);
//	    gif1.setGifImage(R.drawable.flying);
	    //gif1.setShowDimension(550, 80);  
	    
	}
	
	@Override
	public void onBackPressed() {
		if(aCounter_NoAck!=null)
			aCounter_NoAck.cancel();
		aCounter_NoAnswer.cancel();
		SendDisConnect();
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	}	
	
	@Override
    protected void onDestroy() {
		unregisterReceiver(mResponse);
		NotificationAccess.removenotification(this);
		releaseBmp();
		releaseViews();
		System.gc();
		Log.i(TAG, "onDestory()............");
        super.onDestroy(); 
        return;
    }

	private void allocateBmp() {
		bmpPickOffBtn = PictUtil.getLocalBitmap(this, R.drawable.pickoff, 2);
	}

	private void releaseBmp() {
		if(bmpPickOffBtn != null){
			bmpPickOffBtn.recycle();
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
		if(disconnectButton != null){
			disconnectButton.setImageBitmap(null);
			disconnectButton.setBackground(null);
		}
		if(lilayoutAskerConnect != null) {
			lilayoutAskerConnect.setBackground(null);
		}
	}	

	@Override
	protected void onStart(){
	    NotificationAccess.sendNotification(this, Const.eNotifyType.NotifyType_Ringing_Caller,
				showname+":" + getResources().getString(R.string.hist_call_out));
				//showname+":"+"Calling out");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		//unregisterReceiver(mResponse);
		//NotificationAccess.removenotification(this);
	    super.onStop();
	}
		
	private void SendDisConnect(){
		if(aCounter_NoAck!=null)
			aCounter_NoAck.cancel();
		if(aCounter_NoAnswer!=null)
			aCounter_NoAnswer.cancel();
		
		ConnectToOther con = new ConnectToOther(this); 
		con.SendDisconnect(roomId, mp, mlocalid, conusrid);
		
		//sfr.DoServerFunc();
		Intent i = new Intent();
		i.setClass(AskerConnectPage.this, MainPage.class);
		i.putExtra(MainPage.MAINPAGE_LOCATION, String.valueOf(Const.eLocation.Location_Record));
		//startActivity(i);		
		Const.userinfo.set_current_state(Const.eState.State_Idle);
		DBHelper dbhelper = new DBHelper(context);
		if(Const.hisinfo.ladapter!=null)
			Const.hisinfo.ladapter.onUpdate(context, dbhelper, Const.hisinfo.getTableName());	
		AskerConnectPage.this.finish();
	}

	private BroadcastReceiver mResponse = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(BROADCAST_GETHELPRESPONSE)) {
				String strResponse = intent.getStringExtra("String");	
				if(!strResponse.equals("Stop")){
					coninfo.client_size.x = Integer.valueOf(intent.getStringExtra(ASKER_REMOTERESOLUTIONX));
				    coninfo.client_size.y = Integer.valueOf(intent.getStringExtra(ASKER_REMOTERESOLUTIONY)); 
					//To be Asker
				    coninfo.uid = mlocalid;
				    coninfo.rid = conusrid;
				    coninfo.roomid = roomId;
				    Const.userinfo.set_current_state(Const.eState.State_Connecting);
					//Const.userinfo.State_Type = Const.eState.State_Connecting;
					connectProcessing.connectToRoom(roomId, 0, 1, coninfo);
				}
				AskerConnectPage.this.finish();
			} 				
		
		}
	};
}
