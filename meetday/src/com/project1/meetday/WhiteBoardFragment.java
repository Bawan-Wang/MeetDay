package com.project1.meetday;

import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.RelativeLayout;

import com.project1.http.PictUtil;
/**
 * Created by JHWang on 2016/3/25.
 */
//public class WhiteBoardFragment extends Fragment {
public class WhiteBoardFragment extends BaseFragment {

    private static final String TAG = "WhiteBoardFragment";
    private RelativeLayout relayoutWhiteBoard;
    private LinearLayout lilayoutBottom;
    private ImageButton wbCloseButton, wbCaptureButton, wbSaveButton;
    private Bitmap bmpCloseBtn = null, bmpCaptureBtn = null, bmpSaveBtn = null;
    //private ImageView imageView;
    private boolean isShowFile = false;
    private boolean isShowFile_Remote = false;
    
    @SuppressWarnings("deprecation")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        controlView = inflater.inflate(R.layout.fragment_whiteboard, container, false);
        context = getActivity();

        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) controlView.findViewById(R.id.WB_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");
 
        createDrawingBoard(R.id.white_board_container);

        relayoutWhiteBoard= (RelativeLayout) controlView.findViewById(R.id.relayoutWhiteBoard);
        lilayoutBottom= (LinearLayout) controlView.findViewById(R.id.linLayoutWBBottom);
        imageView = (ImageView) controlView.findViewById(R.id.wbimgView);
        //imageViewShow = (ImageView) controlView.findViewById(R.id.wbimgView_Show);
        if(!isShowFile){
        	//Log.e(TAG,"WhiteBoardFragment:" + "WhiteBoardFragment" );
        	onCleanFile();
        } else {
        	//Log.e(TAG,"WhiteBoardFragment:" + "afassafafasf" );
        	if(!isShowFile_Remote){
        		/*fragmentEvents.*/onShowImageFile(filepath_local);
        	} else {
        		/*fragmentEvents.*/onShowImageFile(filepath_remote);
        	}
        }
        
        allocateBmp();        
        
        wbCloseButton = (ImageButton) controlView.findViewById(R.id.button_wbcall_disconnect);
        wbCloseButton.setImageBitmap(bmpCloseBtn);
        wbCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentEvents.onCallHangUp();
            }
        });

        wbCaptureButton = (ImageButton) controlView.findViewById(R.id.button_wb_capture);
        wbCaptureButton.setImageBitmap(bmpCaptureBtn);
        wbCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentEvents.onTakePicture();
            }
        });        
        
        wbSaveButton = (ImageButton) controlView.findViewById(R.id.button_wbstore);
        wbSaveButton.setImageBitmap(bmpSaveBtn);
        wbSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	SavePicture(context);
            }
        });
           
        //setRolePlayStatusInFragment();
        return controlView;
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
		if(bmpCaptureBtn == null) {
			bmpCaptureBtn = PictUtil.getLocalBitmap(context, R.drawable.ic_add_a_photo_green_48dp, 1);
		}
		if(bmpSaveBtn == null){
			bmpSaveBtn = PictUtil.getLocalBitmap(context, R.drawable.ic_save_green_48dp, 1);
		}
	}      
    
	private void releaseBmp() {
		if(bmpCloseBtn != null){
			bmpCloseBtn.recycle();
        }
		if(bmpCaptureBtn != null){
			bmpCaptureBtn.recycle();
        }
		if(bmpSaveBtn != null){
			bmpSaveBtn.recycle();
        }
	}	
	
	private void releaseViews() {
    	if(wbCloseButton != null) {
    		wbCloseButton.setImageBitmap(null);
            wbCloseButton.setBackground(null);
    	}
    	if(wbCaptureButton != null) {
    		wbCaptureButton.setImageBitmap(null);
            wbCaptureButton.setBackground(null);
    	}
    	if(wbSaveButton != null) {
    		wbSaveButton.setImageBitmap(null);
            wbSaveButton.setBackground(null);
    	}
        if(relayoutWhiteBoard != null){
            relayoutWhiteBoard.setBackground(null);
        }
        if(lilayoutBottom != null){
            lilayoutBottom.setBackground(null);
        }
	}    

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_white_board, menu);
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
            case R.id.action_vb:
//                fragmentEvents.onChangeFragment(VIDEO_BOARD);
            	fragmentEvents.onRequestChangFragment();
                return true;
            case R.id.action_pic:
//            	fragmentEvents.onSelectPicture();
                return true;
            case R.id.function_white:
            	fragmentEvents.onCleanWhiteBoardPicture();
            	onCleanFile();
            	return true;
            case R.id.function_picture:
            	fragmentEvents.onSelectPicture();
            	return true; 
           /* case R.id.function_eraser:
            	if(isShowFile){
            		localGestureView.setArbitraryGestureErase(Color.TRANSPARENT);
            	} else {
            		localGestureView.setArbitraryGestureErase(Color.LTGRAY);
            	}
            	return true;
            	*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateFragment(FragmentInfo fragmentInfo) {
  	  
  	  switch(fragmentInfo.iCmd) {
  	      case CMD_CLR_PICTURE:
  	    	  onCleanFile();
  		      break;
          case CMD_SHOW_FILE:
          	onShowImageFile(filepath_remote);
          	isShowFile_Remote = true;
          	break;    
  	      default:
  	    	  break;
  	  } 

        super.updateFragment(fragmentInfo);
    }    
    
    @Override
    protected void onShowImageFile(String path) {
    	isShowFile = true;
    	if(path.equals(filepath_remote)){
    		isShowFile_Remote = true;
    	} else {
    		isShowFile_Remote = false;
    	}		
		super.onShowImageFile(path);	  
    }
    
    @Override
    protected void onCleanFile() {
        imageView.setImageResource(android.R.color.transparent);
    	imageView.setBackgroundColor(Color.LTGRAY);
    	isShowFile = false;
        imageView.setImageBitmap(null);
        if(bmpRemotePic != null) {
            bmpRemotePic.recycle();
            bmpRemotePic = null;
        }
    	//fragmentEvents.onCleanFile(isShowFile);
    }
    
}
