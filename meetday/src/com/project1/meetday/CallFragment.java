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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.project1.http.PictUtil;

/**
 * Fragment for call control.
 */
public class CallFragment extends BaseFragment {

  private static final String TAG = "CallFragment";
  private LinearLayout lilayoutVBBottom;
  private Bitmap bmpCloseBtn = null, bmpPauseSelectBtn = null, bmpPauseNotSelectBtn = null,  bmpCaptureSelectBtn = null, bmpCaptureNotSelectBtn = null;
  private ImageButton closeButton = null;
  private ImageButton pasueButton = null;
  private ImageButton captureButton = null;
  private boolean videoPlay = true;
  //private boolean isShowFreezeFile = false;
  //private boolean isShowFreezeFile_Remote = false;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    controlView = inflater.inflate(R.layout.fragment_call, container, false);

    //Log.e(TAG,"CallFragment:" + "CallFragment");
    context = getActivity();
    
    setHasOptionsMenu(true);
    Toolbar toolbar = (Toolbar) controlView.findViewById(R.id.VB_toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    toolbar.setTitle("");

    createDrawingBoard(R.id.video_board_container);
    imageView = (ImageView) controlView.findViewById(R.id.callimgView);

	if(!isShowFreezeFile){
	  //Log.e(TAG,"WhiteBoardFragment:" + "WhiteBoardFragment" );
	  onCleanFile();
	} else {
	  //Log.e(TAG,"WhiteBoardFragment:" + "afassafafasf" );
	  if(!isShowFreezeFile_Remote){
   		/*fragmentEvents.*/onShowImageFile(filepath_local_freeze);
	  } else {
     	/*fragmentEvents.*/onShowImageFile(filepath_remote_freeze);
	  }
	}

    allocateBmp();


	lilayoutVBBottom = (LinearLayout) controlView.findViewById(R.id.lilayoutVBBottom);
    closeButton = (ImageButton) controlView.findViewById(R.id.button_call_disconnect);
//    closeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_call_end_red_48dp));
    closeButton.setImageBitmap(bmpCloseBtn);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fragmentEvents.onCallHangUp();
      }
    });
    
    pasueButton = (ImageButton) controlView.findViewById(R.id.button_pause);
    captureButton = (ImageButton) controlView.findViewById(R.id.button_store);
    if(((CallActivity)getActivity()).fragmentInfo.getPlayEnabled()){
    	videoPlay = true;
//    	pasueButton.setImageResource(R.drawable.ic_pause_circle_filled_green_48dp);
//    	captureButton.setImageResource(R.drawable.ic_add_a_photo_transparent_48dp);
    }else{
    	videoPlay = false;
//    	pasueButton.setImageResource(R.drawable.ic_play_circle_filled_green_48dp);
//    	captureButton.setImageResource(R.drawable.ic_add_a_photo_green_48dp);
    }
    setPauseAndCaptureIcons(videoPlay);
    pasueButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	if(((CallActivity)getActivity()).getIsOtherFrontEnd()){
                boolean tmpVal = videoPlay;
                videoPlay = !tmpVal;
                setPauseAndCaptureIcons(videoPlay);
                //          if (videoPlay) {
//                  pasueButton.setImageResource(R.drawable.ic_pause_circle_filled_green_48dp);
//                  captureButton.setImageResource(R.drawable.ic_add_a_photo_transparent_48dp);
//                } else {
//                  pasueButton.setImageResource(R.drawable.ic_play_circle_filled_green_48dp);
//                  captureButton.setImageResource(R.drawable.ic_add_a_photo_green_48dp);
//                }
                fragmentEvents.onVideoPause();
        	}else{
				//Show waiting message
				fragmentEvents.onVideoPause();
			}
        }
      });    
    captureButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
    	  if(!videoPlay) {
    		  SavePicture(context);
    	  } else {
    		  Toast.makeText(context, getResources().getString(R.string.comm_dialog_cant_save_picture), Toast.LENGTH_SHORT).show();
    	  }
      }
    });   

    //setRolePlayStatusInFragment();
    
    return controlView;
  }

  @Override
  public void onDestroyView() {
	  Log.d(TAG,"onDestroyView");
	  super.onDestroyView();
  }

  @Override
  public void onDestroy() {
	releaseBmp();
	releaseViews();
  	System.gc();
  	Log.d(TAG,"onDestroy" + Debug.getNativeHeapSize() );
  	super.onDestroy();
  }  

	private void allocateBmp() {
		if(bmpCloseBtn == null) {
			bmpCloseBtn = PictUtil.getLocalBitmap(context, R.drawable.ic_call_end_red_48dp, 1);
		}
		if(bmpPauseSelectBtn == null) {
			bmpPauseSelectBtn = PictUtil.getLocalBitmap(context, R.drawable.ic_pause_circle_filled_green_48dp, 1);
		}
		if(bmpPauseNotSelectBtn == null) {
			bmpPauseNotSelectBtn = PictUtil.getLocalBitmap(context, R.drawable.ic_play_circle_filled_green_48dp, 1);
		}
		if(bmpCaptureSelectBtn == null) {
			bmpCaptureSelectBtn = PictUtil.getLocalBitmap(context, R.drawable.ic_add_a_photo_green_48dp, 1);
		}
		if(bmpCaptureNotSelectBtn == null) {
			bmpCaptureNotSelectBtn = PictUtil.getLocalBitmap(context, R.drawable.ic_add_a_photo_transparent_48dp, 1);
		}
	}      
    
	private void releaseBmp() {
		if(bmpCloseBtn != null){
			bmpCloseBtn.recycle();
		}
		if(bmpPauseSelectBtn != null){
			bmpPauseSelectBtn.recycle();
		}
		if(bmpPauseNotSelectBtn != null){
			bmpPauseNotSelectBtn.recycle();
		}
		if(bmpCaptureSelectBtn != null){
			bmpCaptureSelectBtn.recycle();
		}
		if(bmpCaptureNotSelectBtn != null){
			bmpCaptureNotSelectBtn.recycle();
		}		
	}	
	
	private void releaseViews() {
    	if(closeButton != null) {
    		closeButton.setImageBitmap(null);
			closeButton.setBackground(null);
    	}
    	if(pasueButton != null) {
    		pasueButton.setImageBitmap(null);
			pasueButton.setBackground(null);
    	}
    	if(captureButton != null) {
    		captureButton.setImageBitmap(null);
			captureButton.setBackground(null);
    	}
		if(lilayoutVBBottom != null) {
			lilayoutVBBottom.setBackground(null);
		}
	}    
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	  inflater.inflate(R.menu.menu_video_board, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    // TODO Auto-generated method stub
    super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_wb:
//        fragmentEvents.onChangeFragment(WHITE_BOARD);
        fragmentEvents.onRequestChangFragment();
        return true;
      case R.id.action_role_switch:
//    	  fragmentEvents.onRoleSwitch();
    	  fragmentEvents.onRequestRoleSwitch();
    	  //fragmentEvents.onCameraSwitch();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void updateFragment(FragmentInfo fragmentInfo) {
	  
	  switch (fragmentInfo.iCmd) {
	      case CMD_VIDEO_PAUSE:
	          videoPlay = false;
	          setPauseAndCaptureIcons(videoPlay);
//	          pasueButton.setImageResource(R.drawable.ic_play_circle_filled_green_48dp);
//	          captureButton.setImageResource(R.drawable.ic_add_a_photo_transparent_48dp);
		      break;
	      case CMD_VIDEO_START:
	          videoPlay = true;
	          setPauseAndCaptureIcons(videoPlay);	          
//	          pasueButton.setImageResource(R.drawable.ic_pause_circle_filled_green_48dp);
//	          captureButton.setImageResource(R.drawable.ic_add_a_photo_green_48dp);
	          break;
	      case CMD_ROLE_PLAY:
	    	  onCleanFile();
	    	  setPauseAndCaptureIcons(true);
	    	  //bIsRolePlay = true;
	    	  break;
	      case CMD_ROLE_STOP:
	    	  onCleanFile();
	    	  setPauseAndCaptureIcons(true);
	    	  //bIsRolePlay = false;
	    	  break;
          case CMD_SHOW_FILE:
          	onShowImageFile(filepath_remote_freeze);
			isShowFreezeFile_Remote = true;
          	//isShowFile_Remote = true;
          	break;	  
	      default:
	    	  break;
	  } 

      super.updateFragment(fragmentInfo);
  }

  @Override
  protected void onShowImageFile(String path) {
    isShowFreezeFile = true;
	  if(path.equals(filepath_remote)){
		  isShowFreezeFile_Remote = true;
	  } else {
		  isShowFreezeFile_Remote = false;
	  }
	  super.onShowImageFile(path);
  }

  public boolean getVideoPlayFlag() {
    return videoPlay;
  }
  
  public void setPauseAndCaptureIcons(Boolean isVideoPlay) {
	  if (isVideoPlay) {
		  pasueButton.setImageBitmap(bmpPauseSelectBtn);
		  captureButton.setImageBitmap(bmpCaptureNotSelectBtn);
//	      pasueButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause_circle_filled_green_48dp));
//	      captureButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_a_photo_transparent_48dp));
//	      pasueButton.setImageResource(R.drawable.ic_pause_circle_filled_green_48dp);
//	      captureButton.setImageResource(R.drawable.ic_add_a_photo_transparent_48dp);
	  } else {
		  pasueButton.setImageBitmap(bmpPauseNotSelectBtn);
		  captureButton.setImageBitmap(bmpCaptureSelectBtn);		  
//	      pasueButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_circle_filled_green_48dp));
//	      captureButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_a_photo_green_48dp));				  
//	      pasueButton.setImageResource(R.drawable.ic_play_circle_filled_green_48dp);
//	      captureButton.setImageResource(R.drawable.ic_add_a_photo_green_48dp);
	  }	  
  }

}
