package com.project1.apprtc;

import com.project1.http.Const;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by JHWang on 2015/5/11.
 */
public class LocalGestureView extends View {
    private boolean mCapturing;
    private OnDrawEvents drawEvents;
    private static final String TAG = "GestureView";
    Resources resources;
    ArbitraryGesture localArbitraryGesture, remoteArbitraryGesture;
    ArrowGesture localArrowGesture, remoteArrowGesture;
    private int localGestureType, remoteGestureType;

    public interface OnDrawEvents {
        public void onAGActionDown(int x, int y);
        public void onAGActionMove(int x, int y);
        public void onAGActionUp(boolean isSingleTouch);
        public void onArrowGTouchDown(int x, int y);
        public void onArrowGTouchUp(int x, int y);
    }

    public LocalGestureView(Context context, OnDrawEvents events) {
        super(context);
        mCapturing = true;
        drawEvents = events;
        resources = getResources();
        setWillNotDraw(false);
        //localArbitraryGesture = new ArbitraryGesture(resources);
        //localArrowGesture = new ArrowGesture(resources);
        localGestureType = 1;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        remoteArbitraryGesture = new ArbitraryGesture(resources);
    }
      
    public void setArbitraryGesture(ArbitraryGesture localArbitraryGesture){
    	this.localArbitraryGesture = localArbitraryGesture;
    }
    
    public void setArrowGesture(ArrowGesture Gesture){
    	this.localArrowGesture = Gesture;
    }
    
    public void setArrowGesture_Remote(ArrowGesture Gesture){
    	this.remoteArrowGesture = Gesture;
    }

    public void setArbitraryGestureColor(boolean isCaller){
        localArbitraryGesture.setLocalGestureColor(isCaller);
    }
    
    public void setArbitraryGestureColor_Remote(int color){
    	remoteArbitraryGesture.setLocalGestureColor(color);
    }
       
    public void setArbitraryGestureColor(int color){
        localArbitraryGesture.setLocalGestureColor(color);
    }
    
    public void setArbitraryGestureErase (int color){
    	localArbitraryGesture.setErase(color);
    }
    
    public void setArbitraryGestureErase_Remote (int color){
    	remoteArbitraryGesture.setErase(color);
    }
     
    public void setGestureModel(int tmpGestureType){
        localGestureType = tmpGestureType;
        //clear();
    }
    
    public void setGestureModel_Remote(int tmpGestureType){
        remoteGestureType = tmpGestureType;
        //clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
//        if(1 == localGestureType) {
//            localArbitraryGesture.drawArbitraryGesture(canvas);
//        }else if(2 == localGestureType){
//            localArrowGesture.drawArrowGesture(canvas);
//        }
        localArbitraryGesture.drawArbitraryGesture(canvas);
        remoteArbitraryGesture.drawArbitraryGesture(canvas);
        localArrowGesture.drawArrowGesture(canvas);
        remoteArrowGesture.drawArrowGesture(canvas);
        //imageView.setImageBitmap(BitmapFactory.decodeFile(Const.localFileTmpPath));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mCapturing && Const.userinfo.get_current_state() 
        		== Const.eState.State_Connected) {
            processEvent(event);
            Log.d(TAG, "dispatchTouchEvent");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Erases the signature.
     */
    public void clear() {
        localArbitraryGesture.clear();
        remoteArbitraryGesture.clear();
        localArrowGesture.clear();
        remoteArrowGesture.clear();
        invalidate();
    }

    private boolean processEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                if(1 == localGestureType) {
                    localArbitraryGesture.localTouchDown(event);
                    drawEvents.onAGActionDown((int) localArbitraryGesture.getCurveEndX(), (int) localArbitraryGesture.getCurveEndY());
                    invalidate();
                }else if(2 == localGestureType){
                    localArrowGesture.setTouchDownCoordinate(event);
                    drawEvents.onArrowGTouchDown((int)localArrowGesture.getTouchDownX(), (int)localArrowGesture.getTouchDownY());
                }
//                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE");
                if(1 == localGestureType) {
                    Rect rect = localArbitraryGesture.localTouchMove(event);
//                    if (rect != null) {
//                        invalidate(rect);
//                    }
                    drawEvents.onAGActionMove((int)localArbitraryGesture.getMoveX(), (int)localArbitraryGesture.getMoveY());
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                if(1 == localGestureType) {
                    localArbitraryGesture.localTouchUp(event, false);
                    drawEvents.onAGActionUp(localArbitraryGesture.getSingleTouch());                   
                }else if(2 == localGestureType){
                    localArrowGesture.setTouchUpCoordinate(event);
                    drawEvents.onArrowGTouchUp((int)localArrowGesture.getTouchUpX(), (int)localArrowGesture.getTouchUpY());
                }
                invalidate();
//                Log.d(TAG, "ACTION_UP:Send:" + localArbitraryGesture.getSingleTouch());
                return true;

            case MotionEvent.ACTION_CANCEL:
                invalidate();
                return true;
        }
        return false;
    }

    public void setRemoteArbitraryGestureMove(int getX, int getY){
    	remoteArbitraryGesture.remoteTouchMove(getX, getY);
    	//localArbitraryGesture.remoteTouchMove(getX, getY);
    	invalidate();
    }

    public void setRemoteArbitraryGestureDown(int getX, int getY){
    	remoteArbitraryGesture.remoteTouchDown(getX, getY);
    	//if(remotelock)
    		//return;
    	//locallock = true;
    	//localArbitraryGesture.remoteTouchDown(getX, getY);
    	invalidate();
    }

    public void setRemoteArbitraryGestureUp(boolean tmpIsSingleTouch, int getX, int getY){
    	remoteArbitraryGesture.setSingleTouch(tmpIsSingleTouch);
    	//localArbitraryGesture.setSingleTouch(tmpIsSingleTouch);
    	GestureInfo info = remoteArbitraryGesture.getRInfo();
    	if(info!=null){
    		localArbitraryGesture.addRemotePath(info);
    	}   	
    	remoteArbitraryGesture.clear();
    	invalidate();       
    }
    
    public void setRemoteArrowGestureDown(int getX, int getY){
    	//localArrowGesture.setTouchDown(getX, getY);
    	remoteArrowGesture.setTouchDown(getX, getY);
    }

    public void setRemoteArrowGestureUp(int getX, int getY){
    	//localArrowGesture.setTouchUp(getX, getY);
    	remoteArrowGesture.setTouchUp(getX, getY);
    }
    
    public void onClickUndo () { 
    	Log.e(TAG, "onClickUndo:" + localArbitraryGesture.getPathSize());
        if (localArbitraryGesture.getPathSize()>0) {   
        	localArbitraryGesture.UndoMove();
           invalidate();
         }else{

        }
         //toast the user 
    }

    public void onClickRedo (){
       if (localArbitraryGesture.getPathSizeUnDo()>0) { 
    	   localArbitraryGesture.RedoMove(); 
           invalidate();
       } else {

       }
         //toast the user 
    }
    
}
