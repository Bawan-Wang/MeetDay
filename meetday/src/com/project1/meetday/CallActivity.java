/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.project1.meetday;

import com.project1.apprtc.AppRTCAudioManager;
import com.project1.apprtc.AppRTCClient;
import com.project1.apprtc.AppRTCClient.RoomConnectionParameters;
import com.project1.apprtc.AppRTCClient.SignalingParameters;
import com.project1.apprtc.CpuMonitor;
import com.project1.apprtc.DirectRTCClient;
import com.project1.apprtc.PeerConnectionClient;
import com.project1.apprtc.PeerConnectionClient.PeerConnectionParameters;
import com.project1.apprtc.UnhandledExceptionHandler;
import com.project1.apprtc.WebSocketRTCClient;
import com.project1.apprtc.util.LooperExecutor;
import com.project1.http.Const;
import com.project1.http.FileAccess;
import com.project1.http.NotificationAccess;
import com.project1.http.PictUtil;
import com.project1.meetday.BaseFragment.eCallActivityCmd;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import org.webrtc.Camera2Enumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;

import java.io.File;
import java.io.IOException;

/**
 * Activity for peer connection call setup, call waiting
 * and call view.
 */
public class CallActivity extends AppCompatActivity
    implements AppRTCClient.SignalingEvents,
      PeerConnectionClient.PeerConnectionEvents,
        BaseFragment.OnFragmentEvents {

  public static final String EXTRA_ROOMID =
      "org.appspot.apprtc.ROOMID";
  public static final String EXTRA_LOOPBACK =
      "org.appspot.apprtc.LOOPBACK";
  public static final String EXTRA_VIDEO_CALL =
      "org.appspot.apprtc.VIDEO_CALL";
  public static final String EXTRA_CAMERA2 =
      "org.appspot.apprtc.CAMERA2";
  public static final String EXTRA_VIDEO_WIDTH =
      "org.appspot.apprtc.VIDEO_WIDTH";
  public static final String EXTRA_VIDEO_HEIGHT =
      "org.appspot.apprtc.VIDEO_HEIGHT";
  public static final String EXTRA_VIDEO_FPS =
      "org.appspot.apprtc.VIDEO_FPS";
  public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
      "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
  public static final String EXTRA_VIDEO_BITRATE =
      "org.appspot.apprtc.VIDEO_BITRATE";
  public static final String EXTRA_VIDEOCODEC =
      "org.appspot.apprtc.VIDEOCODEC";
  public static final String EXTRA_HWCODEC_ENABLED =
      "org.appspot.apprtc.HWCODEC";
  public static final String EXTRA_CAPTURETOTEXTURE_ENABLED =
      "org.appspot.apprtc.CAPTURETOTEXTURE";
  public static final String EXTRA_AUDIO_BITRATE =
      "org.appspot.apprtc.AUDIO_BITRATE";
  public static final String EXTRA_AUDIOCODEC =
      "org.appspot.apprtc.AUDIOCODEC";
  public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
      "org.appspot.apprtc.NOAUDIOPROCESSING";
  public static final String EXTRA_AECDUMP_ENABLED =
      "org.appspot.apprtc.AECDUMP";
  public static final String EXTRA_OPENSLES_ENABLED =
      "org.appspot.apprtc.OPENSLES";
  public static final String EXTRA_DISABLE_BUILT_IN_AEC =
      "org.appspot.apprtc.DISABLE_BUILT_IN_AEC";
  public static final String EXTRA_DISABLE_BUILT_IN_AGC =
      "org.appspot.apprtc.DISABLE_BUILT_IN_AGC";
  public static final String EXTRA_DISABLE_BUILT_IN_NS =
      "org.appspot.apprtc.DISABLE_BUILT_IN_NS";
  public static final String EXTRA_DISPLAY_HUD =
      "org.appspot.apprtc.DISPLAY_HUD";
  public static final String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
  public static final String EXTRA_CMDLINE =
      "org.appspot.apprtc.CMDLINE";
  public static final String EXTRA_RUNTIME =
      "org.appspot.apprtc.RUNTIME";
  public static final String EXTRA_ISROLEPLAY =
      "org.appspot.apprtc.ISROLEPLAY";
  public static final String EXTRA_CONNECTINFO =
      "org.appspot.apprtc.CONNECTINFO";
  public static final String BROADCAST_GETHELPRESPONSE =
      "com.project1.helper.GETHELPRESPOSE";
  private static final String TAG = "CallRTCClient";

  // List of mandatory application permissions.
  private static final String[] MANDATORY_PERMISSIONS = {
    "android.permission.MODIFY_AUDIO_SETTINGS",
    "android.permission.RECORD_AUDIO",
    "android.permission.INTERNET"
  };

  // Peer connection statistics callback period in ms.
  private static final int STAT_CALLBACK_PERIOD = 1000;
  // Local preview screen position before call is connected.
  private static final int LOCAL_X_CONNECTING = 0;
  private static final int LOCAL_Y_CONNECTING = 0;
  private static final int LOCAL_WIDTH_CONNECTING = 100;
  private static final int LOCAL_HEIGHT_CONNECTING = 100;
  // Local preview screen position after call is connected.
  private static final int LOCAL_X_CONNECTED = 72;
  private static final int LOCAL_Y_CONNECTED = 72;
  private static final int LOCAL_WIDTH_CONNECTED = 25;
  private static final int LOCAL_HEIGHT_CONNECTED = 25;
  // Remote video screen position
  private static final int REMOTE_X = 0;
  private static final int REMOTE_Y = 0;
  private static final int REMOTE_WIDTH = 100;
  private static final int REMOTE_HEIGHT = 100;

  private PeerConnectionClient peerConnectionClient = null;
  private AppRTCClient appRtcClient;
  private SignalingParameters signalingParameters;
  private AppRTCAudioManager audioManager = null;
  private EglBase rootEglBase;
  private MySurfaceView localRender;
  private SurfaceViewRenderer remoteRender;
  private PercentFrameLayout localRenderLayout;
  private PercentFrameLayout remoteRenderLayout;
  private ScalingType scalingType;
  private Toast logToast;
  private boolean commandLineRun;
  private int runTimeMs;
  private boolean activityRunning;
  private RoomConnectionParameters roomConnectionParameters;
  private PeerConnectionParameters peerConnectionParameters;
  private boolean iceConnected;
  private boolean isError;
//  private boolean callControlFragmentVisible = true;
  private long callStartedTimeMs = 0;
  private boolean isRolePlay = false;
  private boolean isGestInit = false;
  private boolean enableSwitchBoard = false;
  private boolean isFront = true;
  public BaseFragment.FragmentInfo fragmentInfo;
  private Const.Connect_Info connectinfo;
  private IntentFilter mIntentFilter;

  private Bitmap bmpShowImage = null;
  private static int RESULT_LOAD_IMAGE = 1;
  static final int REQUEST_IMAGE_CAPTURE = 2;
    
  private CountDownTimer aCounter_Connect = null;
  //private ImageView imageView;
  private short screenWidth, screenHeight;

  // Controls
  private CallFragment callFragment;
  private WhiteBoardFragment wbFragment;
  private HudFragment hudFragment;
  private CpuMonitor cpuMonitor;

  private BaseFragment.eCallActivityCmd cstate = BaseFragment.eCallActivityCmd.Cmd_Normal;
  private KeyguardManager km;
  private KeyguardLock kl = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler(
        new UnhandledExceptionHandler(this));

    // Set window styles for fullscreen-window size. Needs to be done before
    // adding content.
//    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(
        LayoutParams.FLAG_FULLSCREEN
        | LayoutParams.FLAG_KEEP_SCREEN_ON
        | LayoutParams.FLAG_DISMISS_KEYGUARD
        | LayoutParams.FLAG_SHOW_WHEN_LOCKED
        | LayoutParams.FLAG_TURN_SCREEN_ON);
    getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    setContentView(R.layout.activity_call);

    final int appFlags = this.getApplicationInfo().flags;
    Const.debug_mode = (appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

    iceConnected = false;
    signalingParameters = null;
    scalingType = ScalingType.SCALE_ASPECT_FILL;

    // Create UI controls.
    localRender = (MySurfaceView) findViewById(R.id.local_video_view);
    remoteRender = (SurfaceViewRenderer) findViewById(R.id.remote_video_view);
    localRenderLayout = (PercentFrameLayout) findViewById(R.id.local_video_layout);
    remoteRenderLayout = (PercentFrameLayout) findViewById(R.id.remote_video_layout);
	//imageView = (ImageView) findViewById(R.id.imgView_Show);
    callFragment = new CallFragment();
    wbFragment = new WhiteBoardFragment();
    hudFragment = new HudFragment();

    // Show/hide call control fragment on view click.
    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        toggleCallControlFragmentVisibility();
      }
    };

    localRender.setOnClickListener(listener);
    remoteRender.setOnClickListener(listener);

    // Create video renderers.
    rootEglBase = EglBase.create();
    localRender.init(rootEglBase.getEglBaseContext(), null);
    remoteRender.init(rootEglBase.getEglBaseContext(), null);
    localRender.setZOrderMediaOverlay(true);
    updateVideoView();

    // Check for mandatory permissions.
    for (String permission : MANDATORY_PERMISSIONS) {
      if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
          if (Const.debug_mode) {
              logAndToast("Permission " + permission + " is not granted");
          }
          setResult(RESULT_CANCELED);
          finish();
          return;
      }
    }

    mIntentFilter = new IntentFilter();
    mIntentFilter.addAction(BROADCAST_GETHELPRESPONSE);
    registerReceiver(mResponse, mIntentFilter);

    // Get Intent parameters.
    final Intent intent = getIntent();
    Uri roomUri = intent.getData();
    if (roomUri == null) {
        if (Const.debug_mode) {
            logAndToast(getString(R.string.missing_url));
        }else{
            logAndToast(getString(R.string.comm_connected_error));
        }
        Log.e(TAG, "Didn't get any URL in intent!");
        setResult(RESULT_CANCELED);
        finish();
        return;
    }
    isRolePlay = intent.getBooleanExtra(EXTRA_ISROLEPLAY, false);
    String roomId = intent.getStringExtra(EXTRA_ROOMID);
    if (roomId == null || roomId.length() == 0) {
        if (Const.debug_mode) {
            logAndToast(getString(R.string.missing_url));
        }else{
            logAndToast("Connected error!");
        }
        Log.e(TAG, "Incorrect room ID in intent!");
        setResult(RESULT_CANCELED);
        finish();
        return;
    }
    connectinfo = intent.getParcelableExtra(EXTRA_CONNECTINFO);
    //Log.d(this.getClass().getName(), "fsdfdsfd"+connectinfo.client_size.x);

    boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
    boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);

    boolean useCamera2 = Camera2Enumerator.isSupported()
        && intent.getBooleanExtra(EXTRA_CAMERA2, true);

    peerConnectionParameters = new PeerConnectionParameters(
        intent.getBooleanExtra(EXTRA_VIDEO_CALL, true),
        isRolePlay,
        loopback,
        tracing,
        useCamera2,
        intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0),
        intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0),
        intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
        intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0),
        intent.getStringExtra(EXTRA_VIDEOCODEC),
        intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
        intent.getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false),
        intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0),
        intent.getStringExtra(EXTRA_AUDIOCODEC),
        intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
        intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
        intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
        (Const.Connect_Info) intent.getParcelableExtra(EXTRA_CONNECTINFO));
    commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
    runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

    // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
    // standard WebSocketRTCClient.
    if (loopback || !DirectRTCClient.IP_PATTERN.matcher(roomId).matches()) {
      appRtcClient = new WebSocketRTCClient(this, new LooperExecutor());
    } else {
      Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
      appRtcClient = new DirectRTCClient(this);
    }
    // Create connection parameters.
    roomConnectionParameters = new RoomConnectionParameters(
        roomUri.toString(), roomId, loopback);

    // Create CPU monitor
    cpuMonitor = new CpuMonitor(this);
    hudFragment.setCpuMonitor(cpuMonitor);

    // Send intent arguments to fragments.
    fragmentInfo = new BaseFragment.FragmentInfo(0);
    fragmentInfo.setPlayEnable(true);
    FragmentAction(callFragment, fragmentInfo);
    FragmentAction(wbFragment, fragmentInfo);   
    startCall();

    getScreenSize();

    // For command line execution run connection for <runTimeMs> and exit.
    if (commandLineRun && runTimeMs > 0) {
      (new Handler()).postDelayed(new Runnable() {
        @Override
        public void run() {
          disconnect(false);
        }
      }, runTimeMs);
    }

    peerConnectionClient = PeerConnectionClient.getInstance();
    if (loopback) {
      PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
      options.networkIgnoreMask = 0;
      peerConnectionClient.setPeerConnectionFactoryOptions(options);
    }
    peerConnectionClient.createPeerConnectionFactory(
        CallActivity.this, peerConnectionParameters, CallActivity.this);
        
	aCounter_Connect = new CountDownTimer(15000,1000){
        
        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
        	onIceDisconnected();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            //mTextView.setText("seconds remaining:"+millisUntilFinished/1000);
        }
        
    };
    aCounter_Connect.start();
  }

  // Activity interfaces
  @Override
  public void onPause() {
    super.onPause();
    Log.e(TAG, "onPause()............" + getRolePlayStatus());
    activityRunning = false;
    if (peerConnectionClient != null && getRolePlayStatus()) {
      peerConnectionClient.stopVideoSource(false);
    }
    cpuMonitor.pause();
    if(iceConnected && peerConnectionClient != null){
        cstate = fragmentInfo.getCallActivityStatus();
        fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Pause);
    	peerConnectionClient.sendStringByDataChannel("INPAUSESTATE");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.e(TAG, "onResume()............" + getRolePlayStatus());
    mIntentFilter = new IntentFilter();
    mIntentFilter.addAction(BROADCAST_GETHELPRESPONSE);
    registerReceiver(mResponse, mIntentFilter);
    activityRunning = true;
    if (peerConnectionClient != null && getRolePlayStatus()) {
        boolean isVideoPlay = callFragment.getVideoPlayFlag();
        if (isVideoPlay) {//Add by Bawan, for fixing Eq-87
            peerConnectionClient.startVideoSource(false);
        }
    }
    cpuMonitor.resume();
    if (Const.eFragment.Fragment_WB == fragmentInfo.getCurFrag()) {
    	fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
    	Log.d(TAG, "set CallActivityStatus as normal");
    }
    if(iceConnected && peerConnectionClient != null){
        fragmentInfo.setCallActivityStatus(cstate);
        if(cstate==BaseFragment.eCallActivityCmd.Cmd_PictureSelet || cstate == BaseFragment.eCallActivityCmd.Cmd_TakePicture){
        	fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
        	kl.reenableKeyguard();
        }
        peerConnectionClient.sendStringByDataChannel("INNORMALSTATE");
    }
  }

@Override
protected void onStart(){
    NotificationAccess.sendNotification(this, Const.eNotifyType.NotifyType_ConnectedOn, "Connected On");
    super.onStart();
}
  
  @Override
  protected void onDestroy() {
    //disconnect(false);
	unregisterReceiver(mResponse);
	NotificationAccess.removenotification(this);
    Log.e(TAG, "onDestroy()............");
    if (logToast != null) {
      logToast.cancel();
    }
    activityRunning = false;
    rootEglBase.release();
    System.gc();
    super.onDestroy();
  } 
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  	super.onActivityResult(requestCode, resultCode, data);
  	
  	String picturePath ;
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			cursor.close();
			
			File file = new File(picturePath);
			int datalength = (int) file.length();
			if(datalength > 512 *1024){
                if(bmpShowImage != null) {
                    bmpShowImage.recycle();
                }
                bmpShowImage = PictUtil.decodeFile(file);
                PictUtil.saveToFile(this, Const.localFileTmpPath, bmpShowImage);
//				Bitmap b = PictUtil.decodeFile(file);
//				PictUtil.saveToFile(this, Const.localFileTmpPath, b);
				sendLocalTmpFile();
				wbFragment.onShowImageFile(Const.localFileTmpPath);
			} else {
				peerConnectionClient.sendFilegByDataChannel("SendFile", picturePath);
				wbFragment.onShowImageFile(picturePath);
				File file_local = new File(Const.localFileTmpPath);
				try {
					FileAccess.copy(file, file_local);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			//wbFragment.onShowImageFile(picturePath);
		} else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			  picturePath = Const.localFileTmpPath;
			  File file = new File(picturePath);
			  while(!file.exists()){
				  file = new File(picturePath);
			  }
			  try {
				Thread.sleep(100);
			  } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
			//String imagecompressed = PictUtil.getSavePath().getAbsolutePath()+"/ttt";
			//File image = new File(picturePath);
            bmpShowImage = PictUtil.decodeFile(file);
//			Bitmap b = PictUtil.decodeFile(file);
			//File file = new File(imagecompressed);
            PictUtil.saveToFile(this, Const.localFileTmpPath, bmpShowImage);
//			PictUtil.saveToFile(this, Const.localFileTmpPath, b);
			sendLocalTmpFile();
//			peerConnectionClient.sendFilegByDataChannel("SendFile", Const.localFileTmpPath);
			wbFragment.onShowImageFile(Const.localFileTmpPath);
			
		}
  
		fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
  }

  private void getScreenSize() {
     Resources res = getResources();
     DisplayMetrics metrics = res.getDisplayMetrics();
     screenWidth = (short)metrics.widthPixels;
     screenHeight = (short)metrics.heightPixels;
     Log.d(TAG, "Width:" + screenWidth + "\n");
     Log.d(TAG, "Heigth:" + screenHeight + "\n");
     Log.d(TAG, "DPI:" + metrics.densityDpi + "\n");
     if (screenWidth < screenHeight) {
        Log.d(TAG, "Straight\n");
     } else {
        Log.d(TAG, "Horizontal\n");
     }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
	  //Do nothing.
	  return true;
  }  
  
  // CallFragment.OnCallEvents interface implementation.
  @Override
  public void onCallHangUp() {
    disconnecMessage();
//    disconnect();
  }

  @Override
  public void onTakePicture() {
	  Log.e(TAG, "TakePicture"+fragmentInfo.getCallActivityStatus());
	  if(fragmentInfo.getCallActivityStatus()!=BaseFragment.eCallActivityCmd.Cmd_Normal || !getIsOtherFrontEnd()){
		  logAndToast(getResources().getString(R.string.comm_dialog_wait) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_someones_reply));
	  } else {
		  fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_TakePicture);
		  Log.d(TAG, "set CallActivityStatus as TakePicture");
		  km = (KeyguardManager)this.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);     
          kl = km.newKeyguardLock(TAG);               
          kl.disableKeyguard();  
		  dispatchTakePictureIntent();
		  //fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Pause);
	  }	  
  }

  @Override
  public void onCleanWhiteBoardPicture() {
	  if(iceConnected){	  
		  peerConnectionClient.sendStringByDataChannel("CLRWBPICTURE");
	  }	  
  }
  
  @Override
  public void onSelectPicture() {
      if(iceConnected){
          if(fragmentInfo.getCallActivityStatus()!=BaseFragment.eCallActivityCmd.Cmd_Normal || !getIsOtherFrontEnd()){
//              logAndToast("You are waiting for others!");
              logAndToast(getResources().getString(R.string.comm_dialog_wait) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_someones_reply));
          } else {
              fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_PictureSelet);
              Log.d(TAG, "set CallActivityStatus as PictureSelet");
              km = (KeyguardManager)this.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
              kl = km.newKeyguardLock(TAG);
              kl.disableKeyguard();
              dispatchPlaybackPictureIntent();
              //fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Pause);
          }
      }
  }
  
  @Override
  public void onEraseGesture() {
	  if(iceConnected){
		  peerConnectionClient.sendStringByDataChannel("ERASE");
	  }
  }
  /*
  @Override
  public void onShowImageFile(String path) {
	  File file = new File(path); 
		if (file == null){
			//Log.e("xxxxx", "+++ !!!!File is NULL");
		} else {
			int datalength = (int) file.length();
			imageView.setBackgroundColor(Color.BLACK);
			if(datalength > 512*1024)
				imageView.setImageBitmap(PictUtil.decodeFile(file));
			else
				imageView.setImageBitmap(BitmapFactory.decodeFile(path));
		}	  
  }
  
  @Override
  public void onCleanFile(boolean isShowFile) {
	  if(isShowFile){
		  imageView.setImageResource(android.R.color.transparent);
		  imageView.setBackgroundColor(Color.TRANSPARENT);
	  } else {
		  imageView.setImageResource(android.R.color.transparent);
		  imageView.setBackgroundColor(Color.LTGRAY);
	  }
    //imageView.setImageResource(android.R.color.transparent);
  	//imageView.setBackgroundColor(Color.LTGRAY);
  	//isShowFile = false;
  }
  */

  @Override
  public void onRequestRoleSwitch() {
	  if(!getIsOtherFrontEnd()){
		  logAndToast(getResources().getString(R.string.comm_dialog_wait) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_someones_reply));
		  return;
	  }
	  if(iceConnected){
		  fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_WaitAck);
		  peerConnectionClient.sendStringByDataChannel("ROLESWITCH_CHECK_REQUIST");
	  }
  }
  
  public void responseRoleSwitch() {
	  if(eCallActivityCmd.Cmd_Normal == fragmentInfo.getCallActivityStatus()){
		  Log.d(TAG, "CallActivityStatus is Cmd_Normal");
		  peerConnectionClient.sendStringByDataChannel("ROLESWITCH_CHECK_RESPONSE");
	  }else{
		  Log.d(TAG, "CallActivityStatus is not Cmd_Normal");
		  peerConnectionClient.sendStringByDataChannel("ROLESWITCH_RESPONSE_REJECT");
	  }
  }
  
//  @Override
//  public void onRoleSwitch() {
  public void callRoleSwitch() {  
	  if(iceConnected){
		  fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_RoleSwitch);
		  Log.d(TAG, "set CallActivityStatus as RoleSwitch");
		  RoleSwitchDialog roleSwitchDialog = RoleSwitchDialog.newInstance(true);
		  roleSwitchDialog.show(getFragmentManager(), "dialog");
	  }
  }
  
  private void doRoleSwitch(){
	  if(!getIsOtherFrontEnd()){
		  logAndToast(getResources().getString(R.string.comm_dialog_wait) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_someones_reply));
		  return;
	  }
	  if(iceConnected){
		boolean RolePlay = getRolePlayStatus();	
	    if (RolePlay) {
			peerConnectionClient.sendStringByDataChannel("ROLEPLAY");
			peerConnectionClient.stopVideoSource(true);
			logAndToast(getResources().getString(R.string.comm_dialog_show_remote_preview1) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_show_remote_preview2));
		} else {
			peerConnectionClient.sendStringByDataChannel("ROLESTOP");
			peerConnectionClient.startVideoSource(true);
			logAndToast(getResources().getString(R.string.comm_dialog_show_my_preview));
	    }
	    callFragment.onCleanFile();
	    callFragment.setPauseAndCaptureIcons(true);
	    fragmentInfo.setPlayEnable(true);
	    setRolePlayStatus(!RolePlay);
	  }
  }
  
	public void localRSDoPositiveClick() {
	    peerConnectionClient.sendStringByDataChannel("ROLESWITCH_REQUIST");
	    logAndToast(getResources().getString(R.string.comm_dialog_wait_remote_response));
	}

	public void remoteRSDoPositiveClick() {
		doRoleSwitch();
	}	

	public void remoteRSDoNegativeClick() {
		peerConnectionClient.sendStringByDataChannel("ROLESWITCH_RESPONSE_REJECT");
		logAndToast(getResources().getString(R.string.comm_dialog_dont_change));
	}

	public void responseRoleSwitchDialog() {
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_RoleSwitch);
                RoleSwitchDialog roleSwitchDialog = RoleSwitchDialog.newInstance(false);
                roleSwitchDialog.show(getFragmentManager(), "dialog");
            }
        });
	} //EOF warningDialog    	

	public void localRSOnPause(){
		Log.d(TAG, "local roleSwitchDialog onPause");
	    if (Const.eFragment.Fragment_VB == fragmentInfo.getCurFrag()) {
	    	fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
	    	Log.d(TAG, "set CallActivityStatus as normal");
	    }		
	}

	public void localRSOnResume(){
		Log.d(TAG, "local roleSwitchDialog OnResume");
	}	

	public void remoteRSOnPause(Boolean isOk){
		Log.d(TAG, "remote roleSwitchDialog onPause");
	    if (Const.eFragment.Fragment_VB == fragmentInfo.getCurFrag()) {
	    	fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
	    	Log.d(TAG, "set CallActivityStatus as normal");
	    }
	    if(!isOk){
	    	remoteRSDoNegativeClick();
	    }
	}

	public void remoteRSOnResume(){
		Log.d(TAG, "remote roleSwitchDialog OnResume");
	}		
	
	public void getResponseMessage(final String Message) {
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast(Message);
            }
        });
	}	
	
  @Override
  public void onVideoPause() {
	  if(!getIsOtherFrontEnd()){
		  logAndToast(getResources().getString(R.string.comm_dialog_wait) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_someones_reply));
		  return;
	  }
	  if(iceConnected){
		boolean RolePlay = getRolePlayStatus();
		boolean isVideoPlay = callFragment.getVideoPlayFlag();   
		if (isVideoPlay) {
		    fragmentInfo.setPlayEnable(true);
		    peerConnectionClient.sendStringByDataChannel("PAUSE");
		    callFragment.onCleanFile();
		    fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
		} else {
		    fragmentInfo.setPlayEnable(false);	
		    peerConnectionClient.sendStringByDataChannel("RUN");
		    if(RolePlay){
			    sendCurFrame();
			    callFragment.onShowImageFile(Const.localFileTmpPath_Freeze);
		    }
		    //fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Freeze);
		}
		if(RolePlay){
			updateVideoSource(isVideoPlay);
		}
	  }
	//imageView.setImageBitmap(BitmapFactory.decodeFile(Const.localFileTmpPath));
  }

    @Override
  public void onRequestChangFragment() {
	  if(!getIsOtherFrontEnd()){
		  logAndToast(getResources().getString(R.string.comm_dialog_wait) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_someones_reply));
		  return;
	  }
	  if(iceConnected){
		  //cstate = Const.eCallState.Cstate_WaitAck;
		  fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_WaitAck);
		  peerConnectionClient.sendStringByDataChannel("CHANGEBOARD_REQUEST");
	  }
	  Log.e(TAG, "onRequestChangFragment"+fragmentInfo.getCallActivityStatus());
  }
  
  public void remoteReponseChangFragment() {
	  if(BaseFragment.eCallActivityCmd.Cmd_Normal == fragmentInfo.getCallActivityStatus()){
		  if (Const.eFragment.Fragment_WB == fragmentInfo.getCurFrag()){
			  //Change from WB to VB
			  changeFragment(BaseFragment.VIDEO_BOARD);			  
		  }else{
			  //Change from VB to WB
			  changeFragment(BaseFragment.WHITE_BOARD);
		  }
	  }else if(BaseFragment.eCallActivityCmd.Cmd_PictureSelet == fragmentInfo.getCallActivityStatus()) {
		  peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 1);  
      }else if(BaseFragment.eCallActivityCmd.Cmd_TakePicture == fragmentInfo.getCallActivityStatus()){
    	  peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 2);  
	  }else if(BaseFragment.eCallActivityCmd.Cmd_RoleSwitch == fragmentInfo.getCallActivityStatus()){
    	  peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 3);  
	  }else if(BaseFragment.eCallActivityCmd.Cmd_PenDown == fragmentInfo.getCallActivityStatus()){
    	  peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 4);  
	  }else{
		  Log.d(TAG, "Unexpected status in CallActivity");
	  }
	  
  }  
  
  private void requestSwitch2VideoDialog() {
	runOnUiThread(new Runnable() {
		@Override
		public void run() {
		    if(eCallActivityCmd.Cmd_Normal == fragmentInfo.getCallActivityStatus()){
		    	fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_BoardSwitch);
		    	peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 5); 
				BoardSwitchDialog boardSwitchDialog = BoardSwitchDialog.newInstance(connectinfo.remotename);
				boardSwitchDialog.show(getFragmentManager(), "dialog");	
		    }else if(eCallActivityCmd.Cmd_BoardSwitch == fragmentInfo.getCallActivityStatus()){
		    	peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 5); 
		    }else{
				  Log.d(TAG, "Unexpected status in CallActivity");
			}
//			new AlertDialog.Builder(CallActivity.this)
//				.setMessage("Request to switch to white board.")
//		        .setPositiveButton("OK",
//		            new DialogInterface.OnClickListener() {
//		              @Override
//		              public void onClick(DialogInterface dialog, int whichButton) {
//		                dialog.cancel();
//		                setSwitchBoard(true);
//		                remoteReponseChangFragment();
//		              }
//		            }
//		        )
//		        .setNegativeButton("Reject",
//		            new DialogInterface.OnClickListener() {
//		              @Override
//		              public void onClick(DialogInterface dialog, int whichButton) {
//		                dialog.cancel();
//		                peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 4); 
//		              }
//		            }
//		        )
//		        .create().show();
	      }
	});
  }   
  
  public void boardSwitchPositiveClick() {
	  if(!getIsOtherFrontEnd()){
		  logAndToast(getResources().getString(R.string.comm_dialog_wait) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_someones_reply));
		  peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 4);
		  fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
		  return;
	  }
	  fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
	  Log.d(TAG, "set CallActivityStatus as normal");	  
      setSwitchBoard(true);
      remoteReponseChangFragment();	  
  }
  
  public void boardSwitchNegativeClick() {
      peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 4); 
  }

  public void boardSwitchOnPause(Boolean isOk){
	fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
	Log.d(TAG, "set CallActivityStatus as normal");
    if(!isOk){
    	boardSwitchNegativeClick();
    }	
  } 
  
//  @Override
//  public void onChangeFragment(int index) {
  public void changeFragment(int index) {
	  if(iceConnected){  
		fragmentInfo.iCmd = BaseFragment.CMD_BOARD;
		if (BaseFragment.WHITE_BOARD == index) {
		  FragmentAction(wbFragment, fragmentInfo);
		  peerConnectionClient.sendStringByDataChannel("WB");
		  fragmentInfo.setCurFrag(Const.eFragment.Fragment_WB);
		  Log.d(TAG, "Set Fragment_WB");
		} else if (BaseFragment.VIDEO_BOARD == index) {
		  FragmentAction(callFragment, fragmentInfo);
		  if(getSwitchBoard() == false){
			  setSwitchBoard(true);
		  }
		  peerConnectionClient.sendStringByDataChannel("VB");
		  fragmentInfo.setCurFrag(Const.eFragment.Fragment_VB);
		  Log.d(TAG, "Set Fragment_VB");
		}
	  }
  }
  
//	@Override
//	public void onChangeFragment(int index) {
//		if(iceConnected){  
//		    fragmentInfo.iCmd = BaseFragment.CMD_BOARD;
//		    if (BaseFragment.WHITE_BOARD == index) {
//				FragmentAction(wbFragment, fragmentInfo);
//				peerConnectionClient.sendStringByDataChannel("WB");
//				fragmentInfo.setCurFrag(Const.eFragment.Fragment_WB);
//				Log.d(TAG, "Set Fragment_WB");
//		    } else if (BaseFragment.VIDEO_BOARD == index) {
//		    	FragmentAction(callFragment, fragmentInfo);
//		    	peerConnectionClient.sendStringByDataChannel("VB");
//		    	fragmentInfo.setCurFrag(Const.eFragment.Fragment_VB);
//		    	Log.d(TAG, "Set Fragment_VB");
//			}
//		}
//	}  
  
  @Override
  public void onClrScreenCmd() {
	  if(iceConnected){
		  peerConnectionClient.sendStringByDataChannel("Clear screen");
	  }
  }

  @Override
  public void onDrawArbitraryGestureMove(int x, int y) {
	  if(iceConnected){
		  peerConnectionClient.sendGestureByDataChannel("TouchMove:", x, y);
	  }
  }

  @Override
  public void onDrawArbitraryGestureDown(int x, int y) {
	  if(iceConnected){
		  //cstate = Const.eCallState.CState_PenDown;
		  fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_PenDown);
		  peerConnectionClient.sendGestureByDataChannel("TouchDown:", x, y);
	  }
  }

  //  @Override
  public void onDrawArbitraryGestureUp(boolean tmpIsSingleTouch, int x, int y) {
	  if(iceConnected){
		  //Log.e(TAG,"onDrawArbitraryGestureUp"+tmpIsSingleTouch+x+y);
	    if (tmpIsSingleTouch) {
	      peerConnectionClient.sendGestureByDataChannel("RealSingleTouch:", x, y);
	    } else {
	      peerConnectionClient.sendGestureByDataChannel("NonSingleTouch:", x, y);
	    }
	    //cstate = Const.eCallState.CState_Idle;
	    fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
	  }
  }

  public void onSetGestureModel(int gestureType) {
	  if(iceConnected){	  
		  if(peerConnectionClient != null){
			  peerConnectionClient.sendModelOrStautsByDataChannel("SetGestureModel:", gestureType);
		  }
	  }	  
  }

  @Override
  public void onDrawArrowGestureDown(int x, int y) {
	  if(iceConnected){
		  peerConnectionClient.sendGestureByDataChannel("ArrowDown:", x, y);
	  }
  }

  @Override
  public void onDrawArrowGestureUp(int x, int y) {
	  if(iceConnected){
		  peerConnectionClient.sendGestureByDataChannel("ArrowUp:", x, y);
	  }
  }
  
  @Override
  public void onCameraSwitch() {
	  if(iceConnected && getRolePlayStatus()){
	    if (peerConnectionClient != null) {
//	        peerConnectionClient.switchCamera();
	    }
	  }
  }
    
  @Override
  public void onUndoGesture() {
    if (peerConnectionClient != null) {
    	peerConnectionClient.sendStringByDataChannel("UndoGesture");
    }
  }   
  
//  @Override
//  public void onPenGesture() {
//    if (peerConnectionClient != null) {
//    	peerConnectionClient.sendStringByDataChannel("PenGesture");
//    }
//  }  
  
  @Override
  public void onCaptureFormatChange(int width, int height, int framerate) {
    if (peerConnectionClient != null) {
      peerConnectionClient.changeCaptureFormat(width, height, framerate);
    }
  }

  private BroadcastReceiver mResponse = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // TODO Auto-generated method stub
      if (intent.getAction().equals(BROADCAST_GETHELPRESPONSE)) {
        disconnect(false);
      }

    }
  };

  @Override
  public void onToggleMic() {
    if (peerConnectionClient != null) {
      fragmentInfo.micEnabled = !fragmentInfo.micEnabled;
      peerConnectionClient.setAudioEnabled(fragmentInfo.micEnabled);
    }
  }

  // Helper functions.
//  private void toggleCallControlFragmentVisibility() {
//    if (!iceConnected || !callFragment.isAdded()) {
//      return;
//    }
//    // Show/hide call control fragment
//    callControlFragmentVisible = !callControlFragmentVisible;
//    FragmentTransaction ft = getFragmentManager().beginTransaction();
//    if (callControlFragmentVisible) {
//      ft.show(callFragment);
//      ft.show(hudFragment);
//    } else {
//      ft.hide(callFragment);
//      ft.hide(hudFragment);
//    }
//    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//    ft.commit();
//  }
  
  private void sendCurFrame(){
      if (iceConnected) {
          FileAccess.deletefile(Const.localFileTmpPath_Freeze);
          localRender.setShouldTakePic(true);
          File file = new File(Const.localFileTmpPath_Freeze);
          while (!file.exists()) {
              file = new File(Const.localFileTmpPath_Freeze);
          }
          try {
              Thread.sleep(300);
          } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
          peerConnectionClient.sendFilegByDataChannel("SendFile", Const.localFileTmpPath_Freeze);
      }
  }

  private void sendLocalTmpFile(){
	  if(iceConnected){	  
		  File file = new File(Const.localFileTmpPath);
		  while(!file.exists()){
			  file = new File(Const.localFileTmpPath);
		  }
		  try {
			Thread.sleep(100);
		  } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  peerConnectionClient.sendFilegByDataChannel("SendFile", Const.localFileTmpPath);
	  }
  }
  
	private void dispatchTakePictureIntent() {
	    File file = new File(Const.localFileTmpPath);
	    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
	    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
	}

	private void dispatchPlaybackPictureIntent(){
		Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
  private void FragmentAction(final BaseFragment mNewFragment, final BaseFragment.FragmentInfo tmpFragmentInfo) {
    runOnUiThread(new Runnable() {
        @Override
        public synchronized void run() {
            Log.d(TAG, "tmpFragmentInfo.iCmd." + tmpFragmentInfo.iCmd);
            if (null == mNewFragment) {
                BaseFragment currentFragment = (BaseFragment) getFragmentManager().findFragmentById(R.id.call_fragment_container);
                if (currentFragment instanceof BaseFragment) {
                    currentFragment.updateFragment(tmpFragmentInfo);
                }
            } else {
                if (BaseFragment.CMD_BOARD == tmpFragmentInfo.iCmd) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.call_fragment_container, mNewFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        }
    });
  }

  private void updateVideoSource(final boolean isVideoPlay) {
    if (/*isRolePlay && */peerConnectionClient != null) {
      if (isVideoPlay) {
        peerConnectionClient.startVideoSource(false);
      } else {
        peerConnectionClient.stopVideoSource(false);
      }
    }
  }

  private void updateVideoView() {
//    remoteRenderLayout.setPosition(REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT);
//    remoteRender.setScalingType(scalingType);
//    remoteRender.setMirror(false);
//
//    if (iceConnected) {
//      localRenderLayout.setPosition(
//          LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED);
//      localRender.setScalingType(ScalingType.SCALE_ASPECT_FIT);
//    } else {
      localRenderLayout.setPosition(
          LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING);
      localRender.setScalingType(scalingType);
//    }
    localRender.setMirror(false);

    localRender.requestLayout();
//    remoteRender.requestLayout();
  }

  private void startCall() {
    if (appRtcClient == null) {
      Log.e(TAG, "AppRTC client is not allocated for a call.");
      return;
    }
    callStartedTimeMs = System.currentTimeMillis();

    // Start room connection.
    if(Const.debug_mode) {
        logAndToast(getString(R.string.connecting_to,
                roomConnectionParameters.roomUrl));
    }else{
        logAndToast(getString(R.string.comm_connect));
    }
    appRtcClient.connectToRoom(roomConnectionParameters);

    // Create and audio manager that will take care of audio routing,
    // audio modes, audio device enumeration etc.
    audioManager = AppRTCAudioManager.create(this, new Runnable() {
        // This method will be called each time the audio state (number and
        // type of devices) has been changed.
        @Override
        public void run() {
          onAudioManagerChangedState();
        }
      }
    );
    // Store existing audio settings and change audio mode to
    // MODE_IN_COMMUNICATION for best possible VoIP performance.
    Log.d(TAG, "Initializing the audio manager...");
    audioManager.init();
  }

  // Should be called from UI thread
  private void callConnected() {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    Log.i(TAG, "Call connected: delay=" + delta + "ms");
    if (peerConnectionClient == null || isError) {
      Log.w(TAG, "Call is connected in closed or error state");
      return;
    }
    //imageView.setBackgroundColor(Color.BLACK);
    peerConnectionClient.stopVideoSource(true);
    peerConnectionClient.startVideoSource(true);
    if (!getRolePlayStatus()){
    	peerConnectionClient.stopVideoSource(true);
    	//callFragment.setRolePlay(false);
    } else {
    	//callFragment.setRolePlay(true);
    }
    // Update video view.
    updateVideoView();
    // Enable statistics callback.
    peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
    setGestureinit(true);
    NotificationAccess.sendNotification(this, Const.eNotifyType.NotifyType_ConnectedOn, "Connected On");   
  }

  private void onAudioManagerChangedState() {
    // TODO(henrika): disable video if AppRTCAudioManager.AudioDevice.EARPIECE
    // is active.
  }

  // Disconnect from remote resources, dispose of local resources, and exit.
  private void disconnect(final boolean isSuccess) {
    if(bmpShowImage != null) {
        bmpShowImage.recycle();
        bmpShowImage = null;
    }
    activityRunning = false;
    if (appRtcClient != null) {
      appRtcClient.disconnectFromRoom();
      appRtcClient = null;
    }
    if (peerConnectionClient != null) {
      peerConnectionClient.close();
      peerConnectionClient = null;
    }
    if (localRender != null) {
      localRender.release();
      localRender = null;
    }
    if (remoteRender != null) {
      remoteRender.release();
      remoteRender = null;
    }
    if (audioManager != null) {
      audioManager.close();
      audioManager = null;
    }
    if (iceConnected && !isError) {
//		Intent intent = new Intent(this, ReviewThanksPage.class);
//		intent.putExtra(ReviewThanksPage.LOCAL_ID, connectinfo.uid);
//		intent.putExtra(ReviewThanksPage.SEND_ID, connectinfo.rid);
//		intent.putExtra(ReviewThanksPage.ROOM_ID, connectinfo.roomid);
//		intent.putExtra(ReviewThanksPage.ISROLEPLAY_FALG, isRolePlay);
//		startActivity(intent);
      setResult(RESULT_OK);
    } else {
      setResult(RESULT_CANCELED);
    }
    if(aCounter_Connect!=null){
    	aCounter_Connect.cancel();
    }

    //Const.userinfo.Location_Type = Const.eLocation.Location_Record;
//    Intent intent = new Intent(this, ReviewThanksPage.class);
//    intent.putExtra(ReviewThanksPage.LOCAL_ID, connectinfo.uid);
//    intent.putExtra(ReviewThanksPage.SEND_ID, connectinfo.rid);
//    intent.putExtra(ReviewThanksPage.ROOM_ID, connectinfo.roomid);
//    intent.putExtra(ReviewThanksPage.ISROLEPLAY_FALG, isRolePlay);
//    startActivity(intent);
//    if (isSuccess) {
//      Intent intent = new Intent(this, ReviewThanksPage.class);
//      intent.putExtra(ReviewThanksPage.LOCAL_ID, connectinfo.uid);
//      intent.putExtra(ReviewThanksPage.SEND_ID, connectinfo.rid);
//      intent.putExtra(ReviewThanksPage.ROOM_ID, connectinfo.roomid);
//      intent.putExtra(ReviewThanksPage.ISCALLER_FALG, isRolePlay);
//      startActivity(intent);
//      //this.finish();
//    } else {
//      if (isRolePlay) {
//        //Log.e(TAG, "hdhhdfhff");
//        MainPage.Mainact.finish();
//        Intent intent = new Intent(this, MainPage.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(MainPage.MAINPAGE_LOCATION,
//            String.valueOf(Const.eLocation.Location_Record));
//        startActivity(intent);
//      } else {
//        if (MainPage.Mainact != null) {
//          //Log.e(TAG, "DFSFDSDFD");
//          //MainPage.Mainact.finish();
//          //this.finish();
//        }
//        if (Const.userinfo.get_front_running() == false) {
//          //Log.e(TAG, "vdsvds");
////          finish();
////          Intent intent = new Intent(this, WelcomPage.class);
////          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////          startActivity(intent);
//        	 android.os.Process.killProcess(android.os.Process.myPid());
//        } else if (MainPage.isON != true) {
//          //MainPage.Mainact.finish();
//          Intent intent = new Intent(this, MainPage.class);
//          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//          intent.putExtra(MainPage.MAINPAGE_LOCATION,
//              String.valueOf(Const.eLocation.Location_Record));
//          //startActivity(intent);
//        } else {
//          Intent intent = new Intent(this, MainPage.class);
//          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//          intent.putExtra(MainPage.MAINPAGE_LOCATION,
//              String.valueOf(Const.eLocation.Location_Record));
//          //startActivity(intent);
//        }
//      }
//      DBHelper dbhelper = new DBHelper(this);
//      if (Const.hisinfo.ladapter != null)
//        Const.hisinfo.ladapter.onUpdate(this, dbhelper, Const.hisinfo.getTableName());
//    }
    Const.userinfo.set_current_state(Const.eState.State_Idle);
    //Const.userinfo.State_Type = Const.eState.State_Idle;
    //Log.e(TAG, "gdfgdgf");
//    CallActivity.this.finish();
    finish();
  }

  private void disconnecMessage() {
    new AlertDialog.Builder(this)
        .setIcon(R.drawable.disconnect)
        .setMessage(R.string.comm_disconnect)
        .setPositiveButton(R.string.comm_quit_hangup,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                if (isRolePlay) {
                  disconnect(true);
                } else {
                  disconnect(false);
                }
              }
            }
        )
        .setNegativeButton(R.string.comm_quit_keep,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
              }
            }
        )
        .create().show();
  }

  private void disconnectWithErrorMessage(final String errorMessage) {
    Log.e(TAG, "Critical error: " + errorMessage);
    if (commandLineRun || !activityRunning) {
      Log.e(TAG, "Critical error: " + errorMessage);
      disconnect(false);
    } else {
      new AlertDialog.Builder(this)
          .setTitle(getText(R.string.channel_error_title))
          .setMessage(errorMessage)
          .setCancelable(false)
          .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
              dialog.cancel();
              disconnect(false);
            }
          }).create().show();
    }
    ConnectToOther con = new ConnectToOther(this);
    con.SendConnectError(connectinfo.uid, connectinfo.rid);
  }

  // Log |msg| and Toast about it.
  private void logAndToast(String msg) {
    Log.d(TAG, msg);
    if (logToast != null) {
      logToast.cancel();
    }
    logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    logToast.show();
  }

  private void reportError(final String description) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!isError) {
          isError = true;
          disconnectWithErrorMessage(description);
        }
      }
    });
  }

  // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
  // All callbacks are invoked from websocket signaling looper thread and
  // are routed to UI thread.
  private void onConnectedToRoomInternal(final SignalingParameters params) {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;

    signalingParameters = params;
    if(Const.debug_mode) {
        logAndToast("Creating peer connection, delay=" + delta + "ms");
    }
    peerConnectionClient.createPeerConnection(rootEglBase.getEglBaseContext(),
        localRender, remoteRender, signalingParameters);

    if (signalingParameters.initiator) {
        if(Const.debug_mode) {
            logAndToast("Creating OFFER...");
        }
      // Create offer. Offer SDP will be sent to answering client in
      // PeerConnectionEvents.onLocalDescription event.
      peerConnectionClient.createOffer();
    } else {
      if (params.offerSdp != null) {
        peerConnectionClient.setRemoteDescription(params.offerSdp);
        if(Const.debug_mode) {
            logAndToast("Creating ANSWER...");
        }
        // Create answer. Answer SDP will be sent to offering client in
        // PeerConnectionEvents.onLocalDescription event.
        peerConnectionClient.createAnswer();
      }
      if (params.iceCandidates != null) {
        // Add remote ICE candidates from room.
        for (IceCandidate iceCandidate : params.iceCandidates) {
          peerConnectionClient.addRemoteIceCandidate(iceCandidate);
        }
      }
    }
  }

  @Override
  public void onConnectedToRoom(final SignalingParameters params) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        onConnectedToRoomInternal(params);
      }
    });
  }

  @Override
  public void onRemoteDescription(final SessionDescription sdp) {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                return;
            }
            if (Const.debug_mode) {
                logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
            }
            peerConnectionClient.setRemoteDescription(sdp);
            if (!signalingParameters.initiator) {
                if (Const.debug_mode) {
                    logAndToast("Creating ANSWER...");
                }
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
        }
    });
  }

  @Override
  public void onRemoteIceCandidate(final IceCandidate candidate) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (peerConnectionClient == null) {
          Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
          return;
        }
        peerConnectionClient.addRemoteIceCandidate(candidate);
      }
    });
  }

  @Override
  public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (peerConnectionClient == null) {
          Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
          return;
        }
        peerConnectionClient.removeRemoteIceCandidates(candidates);
      }
    });
  }

  @Override
  public void onChannelClose() {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            if (Const.debug_mode) {
                logAndToast("Remote end hung up; dropping PeerConnection");
            } else {
                logAndToast(getString(R.string.comm_hangup_from_remote));
            }
            disconnect(false);
        }
    });
  }

  @Override
  public void onChannelError(final String description) {
    reportError(description);
  }

  // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
  // Send local peer connection SDP and ICE candidates to remote party.
  // All callbacks are invoked from peer connection client looper thread and
  // are routed to UI thread.
  @Override
  public void onLocalDescription(final SessionDescription sdp) {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              if (appRtcClient != null) {
                  if(Const.debug_mode) {
                      logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms");
                  }
                  if (signalingParameters.initiator) {
                      appRtcClient.sendOfferSdp(sdp);
                  } else {
                      appRtcClient.sendAnswerSdp(sdp);
                }
            }
        }
    });
  }

  @Override
  public void onIceCandidate(final IceCandidate candidate) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (appRtcClient != null) {
          appRtcClient.sendLocalIceCandidate(candidate);
        }
      }
    });
  }

  @Override
  public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (appRtcClient != null) {
          appRtcClient.sendLocalIceCandidateRemovals(candidates);
        }
      }
    });
  }

  @Override
  public void onIceConnected() {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            if (Const.debug_mode) {
                logAndToast("ICE connected, delay=" + delta + "ms");
            } else {
                logAndToast(getString(R.string.comm_connected_success));
            }
            iceConnected = true;
            Const.userinfo.set_current_state(Const.eState.State_Connected);
            if (aCounter_Connect != null)
                aCounter_Connect.cancel();
            callConnected();
        }
    });
  }

  @Override
  public void onIceDisconnected() {
      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              if (Const.debug_mode) {
                  logAndToast("ICE disconnected");
              } else {
                  logAndToast(getString(R.string.comm_miss_connection));
              }
              iceConnected = false;
              disconnect(false);
          }
      });
  }

  @Override
  public void onPeerConnectionClosed() {
  }

  @Override
  public void onPeerConnectionStatsReady(final StatsReport[] reports) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!isError && iceConnected) {
          //hudFragment.updateEncoderStatistics(reports);
        }
      }
    });
  }

  @Override
  public void onPeerConnectionError(final String description) {
    reportError(description);
  }

  @Override
  public void onGetMessage(final String description) {
    boolean singleTouch = false;
    String[] after1Split, after2Split;
    boolean tmpRole;
    BaseFragment.FragmentInfo tmpfragmentInfo = new BaseFragment.FragmentInfo(0);
    
    Log.d(TAG, "onGetMessage " + description);
    switch (description) {  	
      case "cmdID:CHANGEBOARD_REQUEST":
    	if(fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_Normal ||
    			fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_WaitAck ||
    			fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_Freeze){   
	    	if(!getRolePlayStatus() || getSwitchBoard()){
	    		remoteReponseChangFragment();
	    	}else{
	    		requestSwitch2VideoDialog();
	    	}
    	}
        break;
      case "cmdID:INPAUSESTATE":
    	  setIsOtherFrontEnd(false);
	    if (peerConnectionClient != null && getRolePlayStatus()) {
	        peerConnectionClient.stopVideoSource(false);
	      }
    	  break;
      case "cmdID:INNORMALSTATE":
    	  setIsOtherFrontEnd(true);
	    if (peerConnectionClient != null && getRolePlayStatus()) {
            boolean isVideoPlay = callFragment.getVideoPlayFlag();
            if (isVideoPlay) {//Add by Bawan, for fixing Eq-87
                peerConnectionClient.startVideoSource(false);
            }
	      }
    	  break;	  
      case "REJECT_GETCallACTIVITYSTATUS:1":
      case "REJECT_GETCallACTIVITYSTATUS:2":
      case "REJECT_GETCallACTIVITYSTATUS:3":
      case "REJECT_GETCallACTIVITYSTATUS:4":
      case "REJECT_GETCallACTIVITYSTATUS:5":  
	    after1Split = description.split(":");
	    int tmpIntCmd = Integer.valueOf(after1Split[1]);
	    if(tmpIntCmd == 1) {
//	    	getResponseMessage("Reject change board because it is seleting picture now");
            getResponseMessage(connectinfo.remotename + " " + getResources().getString(R.string.comm_dialog_send_a_picture));
	    }else if(tmpIntCmd == 2) {
//	    	getResponseMessage("Reject change board because it is taking picture now");
            getResponseMessage(connectinfo.remotename + " " + getResources().getString(R.string.comm_dialog_take_a_picture));
	    }else if(tmpIntCmd == 3) {
//	    	getResponseMessage("Reject change board because it is doing role switch now");
            getResponseMessage(getResources().getString(R.string.comm_dialog_wait_remote_response));
	    }else if(tmpIntCmd == 4) {
//	    	getResponseMessage("Reject change board");
            getResponseMessage(connectinfo.remotename + " " + getResources().getString(R.string.comm_dialog_reject_change_board));
	    }else if(tmpIntCmd == 5) {
//	    	getResponseMessage("Wait response.");
            getResponseMessage(getResources().getString(R.string.comm_dialog_wait) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_someones_reply));
	    }
	    if(tmpIntCmd!=5){	    		        
	    	if(fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_Pause && cstate == 
	    			BaseFragment.eCallActivityCmd.Cmd_WaitAck){
	    		cstate = BaseFragment.eCallActivityCmd.Cmd_Normal; 
	    	} else {
	    		fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
	    	}
	    }
    	break;
      case "cmdID:WB":
    	if(fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_Normal||
    			fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_WaitAck||
    			fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_Freeze){
	        Log.d(TAG, "Set remote Fragment_WB");
	        fragmentInfo.setCurFrag(Const.eFragment.Fragment_WB);
	        tmpfragmentInfo.iCmd = BaseFragment.CMD_BOARD;
	        FragmentAction(wbFragment, tmpfragmentInfo);
	        fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
    	}
        break;
      case "cmdID:VB":
		  if(fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_Normal||
	    			fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_WaitAck||
	    			fragmentInfo.getCallActivityStatus()==BaseFragment.eCallActivityCmd.Cmd_Freeze){
	        Log.d(TAG, "Set remote Fragment_VB");
			if(getSwitchBoard() == false){
				setSwitchBoard(true);
			}          
	        fragmentInfo.setCurFrag(Const.eFragment.Fragment_VB);
	        tmpfragmentInfo.iCmd = BaseFragment.CMD_BOARD;      
	        FragmentAction(callFragment, tmpfragmentInfo);
	        fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);  
		  }
        break;
      case "cmdID:RUN":
    	//isRolePlay = callFragment.getRolePlay();   
	  if(fragmentInfo.getCallActivityStatus()!=BaseFragment.eCallActivityCmd.Cmd_Pause){
	        if(getRolePlayStatus()){
	        	//updateVideoSource(false);
	        	sendCurFrame();
	        	peerConnectionClient.stopVideoSource(false);
	        }
	        fragmentInfo.setPlayEnable(false);
	    	tmpfragmentInfo.iCmd = BaseFragment.CMD_VIDEO_PAUSE;
	        FragmentAction(null, tmpfragmentInfo);
	        //fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Freeze);
	  }
        break;
      case "cmdID:PAUSE":
    	  if(fragmentInfo.getCallActivityStatus()!=BaseFragment.eCallActivityCmd.Cmd_Pause){
	        if(getRolePlayStatus()){
	        	//updateVideoSource(true);
	        	peerConnectionClient.startVideoSource(true);
	        }
	        fragmentInfo.setPlayEnable(true);
	        tmpfragmentInfo.iCmd = BaseFragment.CMD_VIDEO_START;
	        FragmentAction(null, tmpfragmentInfo);
	        fragmentInfo.setCallActivityStatus(BaseFragment.eCallActivityCmd.Cmd_Normal);
    	  }
        break;
      case "cmdID:Clear screen":
        tmpfragmentInfo.iCmd = BaseFragment.CMD_BOARD_CLEAR;
        FragmentAction(null, tmpfragmentInfo);
        break;
      case "SetGestureModel:1":
      case "SetGestureModel:2":
        tmpfragmentInfo.iCmd = BaseFragment.CMD_GESTURE;
        after1Split = description.split(":");
        tmpfragmentInfo.setDrawInfo(singleTouch, Integer.valueOf(after1Split[1]), 0);
        FragmentAction(null, tmpfragmentInfo);
        break;
      case "cmdID:ROLESWITCH_CHECK_REQUIST":
    	  responseRoleSwitch();
    	  break;
      case "cmdID:ROLESWITCH_CHECK_RESPONSE":
    	  //cstate = Const.eCallState.CState_Idle;
    	  callRoleSwitch();
    	  break;
      case "cmdID:ROLESWITCH_REQUIST":
    	  if(fragmentInfo.getCallActivityStatus()!=BaseFragment.eCallActivityCmd.Cmd_Pause){
    		  responseRoleSwitchDialog();
    	  } else {
    		  peerConnectionClient.sendModelOrStautsByDataChannel("REJECT_GETCallACTIVITYSTATUS:", 4);
    	  }
    	  break;
      case "cmdID:ROLESWITCH_RESPONSE_REJECT":
    	  getResponseMessage(connectinfo.remotename + " " + getResources().getString(R.string.comm_dialog_dont_change));
    	  break;
      case "cmdID:ROLEPLAY":
    	  if(fragmentInfo.getCallActivityStatus()!=BaseFragment.eCallActivityCmd.Cmd_Pause){   		
	          //updateVideoSource(true);
	    	  tmpRole = getRolePlayStatus();
	    	  setRolePlayStatus(!tmpRole);
	    	  //isRolePlay = !isRolePlay;
	    	  peerConnectionClient.startVideoSource(true);
	    	  getResponseMessage(getResources().getString(R.string.comm_dialog_show_my_preview));
	          tmpfragmentInfo.iCmd = BaseFragment.CMD_ROLE_PLAY;
	          FragmentAction(null, tmpfragmentInfo);
	          fragmentInfo.setPlayEnable(true);
    	  }
          break;
      case "cmdID:ROLESTOP":
    	  if(fragmentInfo.getCallActivityStatus()!=BaseFragment.eCallActivityCmd.Cmd_Pause){
	          //updateVideoSource(true);
	    	  //isRolePlay = !isRolePlay;
	    	  tmpRole = getRolePlayStatus();
	    	  setRolePlayStatus(!tmpRole);
	    	  peerConnectionClient.stopVideoSource(true);
	    	  getResponseMessage(getResources().getString(R.string.comm_dialog_show_remote_preview1) + " " + connectinfo.remotename + getResources().getString(R.string.comm_dialog_show_remote_preview2));
	          tmpfragmentInfo.iCmd = BaseFragment.CMD_ROLE_STOP;
	          FragmentAction(null, tmpfragmentInfo);
	          fragmentInfo.setPlayEnable(true);
    	  }
          break;
      case "cmdID:CLRWBPICTURE":
    	  tmpfragmentInfo.iCmd = BaseFragment.CMD_CLR_PICTURE;  
    	  FragmentAction(null, tmpfragmentInfo);    	  
    	  break;
      case "cmdID:ShowFile":
    	  tmpfragmentInfo.iCmd = BaseFragment.CMD_SHOW_FILE;  
    	  FragmentAction(null, tmpfragmentInfo);
    	  //wbFragment.onShowImageFile(peerConnectionClient.getFilePath());
    	  break;  
      case "cmdID:UndoGesture":
    	  tmpfragmentInfo.iCmd = BaseFragment.CMD_UNDO_GESTURE;  
    	  FragmentAction(null, tmpfragmentInfo);
    	  //wbFragment.onShowImageFile(peerConnectionClient.getFilePath());
    	  break;    
      case "cmdID:ERASE":
    	  tmpfragmentInfo.iCmd = BaseFragment.CMD_ERASE;  
    	  FragmentAction(null, tmpfragmentInfo);
    	  //wbFragment.onShowImageFile(peerConnectionClient.getFilePath());
    	  break; 
//      case "cmdID:PenGesture":
//    	  fragmentInfo.iCmd = BaseFragment.CMD_PEN_GESTURE;  
//    	  FragmentAction(null, fragmentInfo);
//    	  //wbFragment.onShowImageFile(peerConnectionClient.getFilePath());
//    	  break;      	  
      default:
        after1Split = description.split(":");
        after2Split = after1Split[1].split(",");

        switch (after1Split[0]) {
          case "TouchDown":
        	  tmpfragmentInfo.iCmd = BaseFragment.CMD_TOUCH_DOWN;
            break;
          case "TouchMove":
        	  tmpfragmentInfo.iCmd = BaseFragment.CMD_TOUCH_MOVE;
            break;
          case "RealSingleTouch":
        	  tmpfragmentInfo.iCmd = BaseFragment.CMD_SINGLE_TOUCH;
            singleTouch = true;
            break;
          case "NonSingleTouch":
        	  tmpfragmentInfo.iCmd = BaseFragment.CMD_N_SINGLE_TOUCH;         
            break;
          case "ArrowDown":
        	  tmpfragmentInfo.iCmd = BaseFragment.CMD_ARROW_DOWN;
            break;
          case "ArrowUp":
        	  tmpfragmentInfo.iCmd = BaseFragment.CMD_ARROW_UP;
            break;
          case "SendFile":
        	  tmpfragmentInfo.iCmd = BaseFragment.CMD_SEND_FILE;    	  
	      	Log.e("DataFile", Integer.valueOf(after1Split[1]).toString());
	      	peerConnectionClient.setDataFileSize(Integer.valueOf(after1Split[1]));    
	      	if (Const.eFragment.Fragment_VB == fragmentInfo.getCurFrag()) {
	      		peerConnectionClient.setDataPath(Const.remoteFileTmpPath_Freeze);
	      	} else {
	      		peerConnectionClient.setDataPath(Const.remoteFileTmpPath);
	      	}
    	  	break;          
        }
              
        if(tmpfragmentInfo.iCmd != BaseFragment.CMD_SEND_FILE){
        	int trans_x = Integer.valueOf(after2Split[0])
  	              * connectinfo.local_size.x / connectinfo.client_size.x;
        	int trans_y = Integer.valueOf(after2Split[1])
  	              * connectinfo.local_size.y / connectinfo.client_size.y;
        	tmpfragmentInfo.setDrawInfo(singleTouch, trans_x, trans_y);
            FragmentAction(null, tmpfragmentInfo);       	   	
        }       
        break;              
    }
  }
  
  public boolean getRolePlayStatus(){
	  return isRolePlay;
  }
  
  public void setRolePlayStatus(boolean play){  
	  isRolePlay = play;
	  Log.e("setRolePlayStatus", String.valueOf(isRolePlay));
  }
  
  public boolean getGestureinit(){
	  return isGestInit;
  }
  
  private void setGestureinit(boolean init){
	  isGestInit = init;
  }
  
  public boolean getSwitchBoard(){
	  return enableSwitchBoard;
  }
  
  private void setSwitchBoard(boolean switchBoard){
	  enableSwitchBoard = switchBoard;
  }
  
  public boolean getIsOtherFrontEnd(){
	  return isFront;
  }
  
  private void setIsOtherFrontEnd(boolean Front){
	  isFront = Front;
  }

  public boolean getIceConnected() {return iceConnected;}

}
