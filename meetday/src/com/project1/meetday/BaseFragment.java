package com.project1.meetday;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.project1.apprtc.ArbitraryGesture;
import com.project1.apprtc.ArrowGesture;
import com.project1.apprtc.LocalGestureView;
import com.project1.http.Const;
import com.project1.http.PictUtil;

import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by JHWang on 2016/3/28.
 */
public class BaseFragment extends Fragment //{
        implements LocalGestureView.OnDrawEvents {

	private static final String TAG = "BaseFragment";
    protected View controlView;
    protected FrameLayout gestureFrameLayout;
    protected LocalGestureView localGestureView;//, remoteGestureView;
    //protected RemoteGestureView remoteGestureView;
    protected eMenuAudioCmd eAudioStatus = eMenuAudioCmd.Cmd_AudioUnmute;
    protected eMenuPenCmd ePenStatus = eMenuPenCmd.Cmd_PenSeleted;
    //protected boolean bIsRolePlay = false;

    protected boolean isShowFreezeFile = false;
    protected boolean isShowFreezeFile_Remote = false;

    protected OnFragmentEvents fragmentEvents;
    final static String ARG_CALLER = "iscaller";

    protected static final int WHITE_BOARD = 0;
    protected static final int VIDEO_BOARD = 1;
    protected static final int GESTURE_PEN = 1;
    protected static final int GESTURE_NAVIGATOR = 2;
    protected static final int CMD_BOARD = 0;
    protected static final int CMD_VIDEO_PAUSE = 1;
    protected static final int CMD_VIDEO_START = 2;
    protected static final int CMD_BOARD_CLEAR = 9;
    protected static final int CMD_GESTURE = 10;
    protected static final int CMD_TOUCH_DOWN = 11;
    protected static final int CMD_TOUCH_MOVE = 12;
    protected static final int CMD_SINGLE_TOUCH = 13;
    protected static final int CMD_N_SINGLE_TOUCH = 14;
    protected static final int CMD_ARROW_DOWN = 15;
    protected static final int CMD_ARROW_UP = 16;
    protected static final int CMD_ROLE_PLAY = 17;
    protected static final int CMD_ROLE_STOP = 18;
    protected static final int CMD_SEND_FILE = 19;
    protected static final int CMD_SHOW_FILE = 20;
    protected static final int CMD_CLR_PICTURE = 21;
    protected static final int CMD_UNDO_GESTURE = 22;
    protected static final int CMD_ERASE = 23;
//    protected static final int CMD_PEN_GESTURE = 24;

    protected String filepath_local = Const.localFileTmpPath;
    protected String filepath_local_freeze = Const.localFileTmpPath_Freeze;
    protected String filepath_remote = Const.remoteFileTmpPath;
    protected String filepath_remote_freeze = Const.remoteFileTmpPath_Freeze;
    protected ImageView imageView;
    protected Context context;
    protected ArbitraryGesture localArbitraryGesture;//, remoteArbitraryGesture ;
    protected ArrowGesture localArrowGesture, remoteArrowGesture;
    protected int color_local = Color.BLUE, color_remote = Color.GREEN;
    //protected boolean isShowFile_Remote = false;
    protected Bitmap bmpRemotePic = null;
    /**
      * Fragment control interface for container activity.
      */
    public interface OnFragmentEvents {
        public void onCallHangUp();
        public void onVideoPause();
//        public void onChangeFragment(int index) ;
        public void onRequestChangFragment();
        public void onClrScreenCmd();
        public void onDrawArbitraryGestureMove(int x, int y);
        public void onDrawArbitraryGestureDown(int x, int y);
        public void onDrawArbitraryGestureUp(boolean tmpIsSingleTouch, int x, int y);
        public void onSetGestureModel(int gestureType);
        public void onDrawArrowGestureDown(int x, int y);
        public void onDrawArrowGestureUp(int x, int y);
        public void onToggleMic();
        public void onRequestRoleSwitch();
//        public void onRoleSwitch();
        public void onCameraSwitch();
        public void onTakePicture();
        public void onCleanWhiteBoardPicture();        
        public void onSelectPicture();
        public void onUndoGesture();
        public void onEraseGesture();
//        public void onPenGesture();
        //public void onShowImageFile(String path);
        //public void onCleanFile(boolean isShowFile);
//    public void onVideoScalingSwitch(ScalingType scalingType);
        public void onCaptureFormatChange(int width, int height, int framerate);
    }

    protected void createDrawingBoard(int iDrawingBoardId) {
        gestureFrameLayout = (FrameLayout) controlView.findViewById(iDrawingBoardId);     
    	localGestureView = new LocalGestureView(context, this);
    	boolean isInit = getIsGestInit();
        if(!isInit){ 
        	localArbitraryGesture = new ArbitraryGesture(localGestureView.getResources());
        	localArrowGesture = new ArrowGesture(localGestureView.getResources()); 
        	remoteArrowGesture = new ArrowGesture(localGestureView.getResources());
        }    
    	localGestureView.setArbitraryGesture(localArbitraryGesture);
    	localGestureView.setArrowGesture(localArrowGesture);
    	localGestureView.setArrowGesture_Remote(remoteArrowGesture);
        //gestureFrameLayout.addView(remoteGestureView);
        gestureFrameLayout.addView(localGestureView);
        if(!isInit){
        	setRolePlayStatusInFragment(getIsRolePlay());
        }
    }

    protected void setRolePlayStatusInFragment(boolean bIsRolePlay) {
    	//boolean bIsRolePlay = getIsRolePlay();//((CallActivity)getActivity()).getRolePlayStatus();
    	//Log.e(TAG,"bIsRolePlay" + String.valueOf(bIsRolePlay));
        if(bIsRolePlay){
        	color_local = Color.BLUE;
        	color_remote = Color.GREEN;
        } else {
        	color_local = Color.GREEN;
        	color_remote = Color.BLUE;
        }
        localGestureView.setArbitraryGestureColor(color_local);
        localGestureView.setArbitraryGestureColor_Remote(color_remote);
        //remoteGestureView.setArbitraryGestureColor(color_remote);
        setGestureModel(ePenStatus);
    }
    
//    @Override
//    public void onStart() {
//        super.onStart();
//        Bundle args = getArguments();
//        if (args != null) {     	
//        	bIsRolePlay = args.getBoolean(ARG_CALLER, true);
//            localGestureView.setArbitraryGestureColor(bIsRolePlay);
//            remoteGestureView.setArbitraryGestureColor(bIsRolePlay);
//        }
//        Log.d(TAG,"bIsRolePlay" + String.valueOf(bIsRolePlay));
//    }

    @Override
    public void onDestroy() {
    	releaseDrawMemory();
    	gestureFrameLayout.removeAllViews();
    	System.gc();
    	Log.d(TAG, "onDestory");
    	super.onDestroy();
    }
    
    public void releaseDrawMemory(){
        if(bmpRemotePic != null) {
            bmpRemotePic.recycle();
            bmpRemotePic = null;
        }
	    if(imageView != null) {
	    	imageView.setImageBitmap(null);
            imageView.setBackground(null);
	    }
	    localArbitraryGesture.releaseArbitraryGesture();
	    localArrowGesture.releaseArrowGesture();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentEvents = (OnFragmentEvents) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((CallActivity)getActivity()).fragmentInfo.micEnabled) {
            eAudioStatus = eMenuAudioCmd.Cmd_AudioUnmute;
            menu.findItem(R.id.action_mic).setIcon(R.drawable.ic_mic_green_24dp);
        }else{
            eAudioStatus = eMenuAudioCmd.Cmd_AudioMute;
            menu.findItem(R.id.action_mic).setIcon(R.drawable.ic_mic_off_green_24dp);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        if (eMenuPenCmd.Cmd_PenSeleted == ePenStatus) {
            menu.findItem(R.id.action_gesture).setIcon(R.drawable.ic_gesture_green_24dp);
            menu.findItem(R.id.action_gesture).setTitle(R.string.action_pen);
            setGestureModel(ePenStatus);
            PenGesture(color_local);
//            menu.findItem(R.id.function_gesture).setIcon(R.drawable.ic_navigation_green_24dp);
//            menu.findItem(R.id.function_gesture).setTitle(R.string.action_navigator);
        } else if (eMenuPenCmd.Cmd_NavigatorSeleted == ePenStatus) {
            menu.findItem(R.id.action_gesture).setIcon(R.drawable.ic_navigation_green_24dp);
            menu.findItem(R.id.action_gesture).setTitle(R.string.action_navigator);
            setGestureModel(ePenStatus);
//            menu.findItem(R.id.function_gesture).setIcon(R.drawable.ic_gesture_green_24dp);
//            menu.findItem(R.id.function_gesture).setTitle(R.string.action_pen);
        } else if (eMenuPenCmd.Cmd_EraserSeleted == ePenStatus){
            menu.findItem(R.id.action_gesture).setIcon(R.drawable.ic_eraser_green_24dp);
            menu.findItem(R.id.action_gesture).setTitle(R.string.action_eraser);
            setGestureModel(ePenStatus);
            EraseGesture();
            fragmentEvents.onEraseGesture();
//            menu.findItem(R.id.function_gesture).setIcon(R.drawable.ic_gesture_green_24dp);
//            menu.findItem(R.id.function_gesture).setTitle(R.string.action_eraser);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mic:
                if(getIceStatus()) {
                    if (eMenuAudioCmd.Cmd_AudioUnmute == eAudioStatus) {
                        eAudioStatus = eMenuAudioCmd.Cmd_AudioMute;
                        item.setIcon(R.drawable.ic_mic_off_green_24dp);
                        fragmentEvents.onToggleMic();
                    } else {
                        eAudioStatus = eMenuAudioCmd.Cmd_AudioUnmute;
                        item.setIcon(R.drawable.ic_mic_green_24dp);
                        fragmentEvents.onToggleMic();
                    }
                }
                return true;
            case R.id.action_gesture:
//            	PenGesture(color_local);
//            	fragmentEvents.onPenGesture();
                return true;
            case R.id.function_pen:
//                if (ePenStatus == eMenuPenCmd.Cmd_PenSeleted) {
//                    ePenStatus = eMenuPenCmd.Cmd_NavigatorSeleted;
////                    setGestureModel(GESTURE_NAVIGATOR);
//                } else {
//                    ePenStatus = eMenuPenCmd.Cmd_PenSeleted;
////                    setGestureModel(GESTURE_PEN);
//                }
                ePenStatus = eMenuPenCmd.Cmd_PenSeleted;
                setGestureModel(ePenStatus);
                PenGesture(color_local);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.function_navigator:
            	ePenStatus = eMenuPenCmd.Cmd_NavigatorSeleted;
                setGestureModel(ePenStatus);
                getActivity().invalidateOptionsMenu();            	
            	return true;
            case R.id.function_eraser:
            	ePenStatus = eMenuPenCmd.Cmd_EraserSeleted;
            	setGestureModel(ePenStatus);
            	EraseGesture();
            	fragmentEvents.onEraseGesture();
            	getActivity().invalidateOptionsMenu();
            	//localGestureView.setArbitraryGestureErase(Color.TRANSPARENT);
            	return true;
            case R.id.function_Undo:
            	UndoGesture();
            	fragmentEvents.onUndoGesture();
            	return true;
            case R.id.function_eraserall:
                ConnectMessageDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setGestureModel(eMenuPenCmd eTmpPenStatus) {
    	int iGestureType = GESTURE_PEN;
    	if(eMenuPenCmd.Cmd_PenSeleted == eTmpPenStatus || eMenuPenCmd.Cmd_EraserSeleted == eTmpPenStatus ){
    		iGestureType = GESTURE_PEN;
    	}else{
    		iGestureType = GESTURE_NAVIGATOR;
    	}
    	if(localGestureView != null){
    		localGestureView.setGestureModel(iGestureType);
    	}
        if(fragmentEvents != null){
        	fragmentEvents.onSetGestureModel(iGestureType);
        }
    }

    private void ConnectMessageDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(getResources().getString(R.string.action_eraser_all));
        dialog.setMessage(getResources().getString(R.string.dialog_eraseall_message));
        dialog.setPositiveButton(getResources().getString(R.string.dialog_eraseall_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                localGestureView.clear();
                fragmentEvents.onClrScreenCmd();
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.dialog_eraseall_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
            }
        });
        dialog.show();
    } //EOF warningDialog

    public void updateFragment(FragmentInfo fragmentInfo) {
    	//Log.e("updateFragment", String.valueOf(fragmentInfo.iCmd));
        switch(fragmentInfo.iCmd){
            case CMD_BOARD_CLEAR:
                remoteClrScreen();
                break;
            case CMD_GESTURE:
            	PenGestureRemote(color_remote);
                remoteSetGestureModel(fragmentInfo.iDrawParamX);
                break;
            case CMD_TOUCH_DOWN:
                drawArbitraryGestureDown(fragmentInfo.iDrawParamX, fragmentInfo.iDrawParamY);
                break;
            case CMD_TOUCH_MOVE:
                drawArbitraryGestureMove(fragmentInfo.iDrawParamX, fragmentInfo.iDrawParamY);
                break;
            case CMD_SINGLE_TOUCH:
                drawArbitraryGestureUp(fragmentInfo.bDrawIsSingleTouch, fragmentInfo.iDrawParamX, fragmentInfo.iDrawParamY);
                break;
            case CMD_N_SINGLE_TOUCH:
                drawArbitraryGestureUp(fragmentInfo.bDrawIsSingleTouch, fragmentInfo.iDrawParamX, fragmentInfo.iDrawParamY);
                break;
            case CMD_ARROW_DOWN:
                drawArrowGestureDown(fragmentInfo.iDrawParamX, fragmentInfo.iDrawParamY);
                break;
            case CMD_ARROW_UP:
                drawArrowGestureUp(fragmentInfo.iDrawParamX, fragmentInfo.iDrawParamY);
                break;
            case CMD_VIDEO_START:
            	onCleanFile();
            	break;
            case CMD_VIDEO_PAUSE:
            	if(getIsRolePlay()) {
                    onShowImageFile(filepath_local_freeze);
                }
            	break;
            case CMD_UNDO_GESTURE:
            	UndoGestureRemote();
            	break;   
            case CMD_ERASE:
            	EraseGestureRemote();
            	break; 	
//            case CMD_PEN_GESTURE:
//            	PenGestureRemote(color_remote);
//            	break;
            default:
            	break;
        }
    }

    @Override
    public void onAGActionDown(int x, int y) {
        fragmentEvents.onDrawArbitraryGestureDown(x, y);
    }

    @Override
    public void onAGActionMove(int x, int y) {
        fragmentEvents.onDrawArbitraryGestureMove(x, y);
    }

    @Override
    public void onAGActionUp(boolean isSingleTouch) {
        fragmentEvents.onDrawArbitraryGestureUp(isSingleTouch, 0, 0);
    }

    @Override
    public void onArrowGTouchDown(int x, int y) {
        fragmentEvents.onDrawArrowGestureDown(x, y);
    }

    @Override
    public void onArrowGTouchUp(int x, int y) {
        fragmentEvents.onDrawArrowGestureUp(x, y);
    }

    public void remoteClrScreen() {
        localGestureView.clear();
    }

    public void remoteSetGestureModel(int gestureType) {
        //remoteGestureView.setGestureModel(gestureType);
    	localGestureView.setGestureModel_Remote(gestureType);
    }

    public void drawArbitraryGestureMove(int x, int y) {
        //remoteGestureView.setRemoteArbitraryGestureMove(x, y);//setArbitraryGestureMove(x, y);
        localGestureView.setRemoteArbitraryGestureMove(x, y);
    }

    public void drawArbitraryGestureDown(int x, int y) {
        //remoteGestureView.setRemoteArbitraryGestureDown(x, y);//setArbitraryGestureDown(x, y);
        localGestureView.setRemoteArbitraryGestureDown(x, y);
    }

    public void drawArbitraryGestureUp(boolean tmpIsSingleTouch, int x, int y) {
        //remoteGestureView.setRemoteArbitraryGestureUp(tmpIsSingleTouch, x, y);//setArbitraryGestureUp(tmpIsSingleTouch, x, y);
        localGestureView.setRemoteArbitraryGestureUp(tmpIsSingleTouch, x, y);
    }

    public void drawArrowGestureDown(int x, int y) {
        //remoteGestureView.setRemoteArrowGestureDown(x, y);//setArrowGestureDown(x, y);
    	localGestureView.setRemoteArrowGestureDown(x, y);
    }

    public void drawArrowGestureUp(int x, int y) {
        //remoteGestureView.setRemoteArrowGestureUp(x, y);//setArrowGestureUp(x, y);
        //remoteGestureView.postInvalidate();
    	localGestureView.setRemoteArrowGestureUp(x, y);//setArrowGestureUp(x, y);
    	localGestureView.postInvalidate();
    }

    public static class FragmentInfo {
        public int iCmd;
        public boolean bDrawIsSingleTouch;
        public int iDrawParamX;
        public int iDrawParamY;
        public boolean micEnabled = true;
        public boolean bPlayEnable  = true;
        public Const.eFragment efrag = Const.eFragment.Fragment_WB;
        public eCallActivityCmd eCallActiviyStatus = eCallActivityCmd.Cmd_Normal;
        private ReadWriteLock rwlock_state = new ReentrantReadWriteLock();
        
        public FragmentInfo(int iTmpCmd){
            iCmd = iTmpCmd;
        }

        public void setDrawInfo(boolean isSingleTouch, int iX, int iY) {
            bDrawIsSingleTouch = isSingleTouch;
            iDrawParamX = iX;
            iDrawParamY = iY;
        }

        public void setPlayEnable(boolean playEnable){
        	bPlayEnable = playEnable;
        }

        public boolean getPlayEnabled(){
            return bPlayEnable;
        }
        
        public Const.eFragment getCurFrag(){
        	return efrag;
        }

        public void setCurFrag(Const.eFragment tmpEfrag){
        	efrag = tmpEfrag;
        }
        
        public void setCallActivityStatus(eCallActivityCmd tmpCallActicityStatus){
        	rwlock_state.writeLock().lock();
			try{
				eCallActiviyStatus = tmpCallActicityStatus;
			} finally {
				rwlock_state.writeLock().unlock();
			}
        }
        
        public eCallActivityCmd getCallActivityStatus() {
        	rwlock_state.writeLock().lock();
        	eCallActivityCmd ret;
			try{
				ret = eCallActiviyStatus;
			} finally {
				rwlock_state.writeLock().unlock();
			}
        	return ret;
        }
        
//        public Boolean isCallActivityStatusNormal() {
//        	Boolean bRetStatus = true;
//        	if(eCallActivityCmd.Cmd_Normal != eCallActiviyStatus) {
//        		bRetStatus = false;
//        	}
//        	return bRetStatus;
//        }
    }
    
    protected void onShowImageFile(String path) {
        File file = new File(path);
        if (file == null) {
            //Log.e("xxxxx", "+++ !!!!File is NULL");
        } else {
            int datalength = (int) file.length();
            imageView.setBackgroundColor(Color.BLACK);
            if(bmpRemotePic != null) {
                bmpRemotePic.recycle();
            }
            if (datalength > 512 * 1024) {
                bmpRemotePic = PictUtil.decodeFile(file);
//    				imageView.setImageBitmap(PictUtil.decodeFile(file));
            } else {
                bmpRemotePic = BitmapFactory.decodeFile(path);
//                imageView.setImageBitmap(BitmapFactory.decodeFile(path));
            }
            imageView.setImageBitmap(bmpRemotePic);
        }
    }
      
      
    protected void onCleanFile() {
          isShowFreezeFile = false;
    	  imageView.setImageResource(android.R.color.transparent);
      	  imageView.setBackgroundColor(Color.TRANSPARENT);
          imageView.setImageBitmap(null);
          if(bmpRemotePic != null) {
              bmpRemotePic.recycle();
              bmpRemotePic = null;
          }
      }
    
    public enum eMenuPenCmd {
        Cmd_PenSeleted,
        Cmd_NavigatorSeleted,
        Cmd_EraserSeleted
    }

    public enum eMenuAudioCmd {
        Cmd_AudioUnmute,
        Cmd_AudioMute
    }

    public enum eCallActivityCmd {
    	Cmd_PictureSelet,
    	Cmd_TakePicture,
    	Cmd_RoleSwitch,
    	Cmd_BoardSwitch,
    	Cmd_Normal,
    	Cmd_PenDown,
    	Cmd_WaitAck,
    	Cmd_Pause,
    	Cmd_Freeze
    }
    
    public void SavePicture(Context context){
    	//Bitmap bitmap = Bitmap.createBitmap(gestureFrameLayout.getWidth(), gestureFrameLayout.getHeight(), Bitmap.Config.ARGB_8888);
        if(getIceStatus()) {
            Bitmap bitmap = PictUtil.viewToBitmap(gestureFrameLayout);
            String tempPicFile = "MeetDay_" + System.currentTimeMillis() + ".jpeg";
            PictUtil.FileIOInBackground(context, Const.mScreenshotPath + "/" + tempPicFile, bitmap);
        }
    }

    protected boolean getIceStatus(){
        return ((CallActivity)getActivity()).getIceConnected();
    }

    protected boolean getIsRolePlay(){
    	return ((CallActivity)getActivity()).getRolePlayStatus();
    }
    
    protected boolean getIsGestInit(){
    	return ((CallActivity)getActivity()).getGestureinit();
    }
    
    protected void UndoGesture(){
    	//if(localArbitraryGesture!=null){
    		localGestureView.onClickUndo();
    	//}
    }

    protected void UndoGestureRemote(){
    	//if(remoteArbitraryGesture!=null){
    		//remoteGestureView.onClickUndo();
    		localGestureView.onClickUndo();
    	//}
    }
 
    protected void EraseGesture(){
    	//if(remoteArbitraryGesture!=null){
    	localGestureView.setArbitraryGestureErase(Color.TRANSPARENT);
    	//}
    }
    
    protected void PenGesture(int color){
    	localGestureView.setArbitraryGestureColor(color);
    }
    
    protected void PenGestureRemote(int color){
    	//remoteGestureView.setArbitraryGestureColor(color);
    	localGestureView.setArbitraryGestureColor_Remote(color);
    }
    
    protected void EraseGestureRemote(){
    	//if(remoteArbitraryGesture!=null){
    	//remoteGestureView.setArbitraryGestureErase(Color.TRANSPARENT);
    	localGestureView.setArbitraryGestureErase_Remote(Color.TRANSPARENT);
    	//}
    }
    
}
