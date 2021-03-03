package com.project1.apprtc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

/**
 * Created by JHWang on 2015/5/12.
 */
public class RemoteGestureView  extends View {
    private boolean mCapturing;
    private static final String TAG = "RemoteGestureView";
    Resources resources;
    ArbitraryGesture remoteArbitraryGesture;
    ArrowGesture remoteArrowGesture;
    private int remoteGestureType;

    public RemoteGestureView(Context context) {
        super(context);
        mCapturing = true;
        resources = getResources();
        setWillNotDraw(false);
        //remoteArbitraryGesture = new ArbitraryGesture(resources);
        //remoteArrowGesture = new ArrowGesture(resources);
        remoteGestureType = 1;
    }

    public void setArbitraryGesture(ArbitraryGesture Gesture){
    	this.remoteArbitraryGesture = Gesture;
    }
    
    public void setArrowGesture(ArrowGesture Gesture){
    	this.remoteArrowGesture = Gesture;
    }
    
    public void setArbitraryGestureColor(boolean isCaller){
        remoteArbitraryGesture.setRemoteGestureColor(isCaller);
    }

    public void setGestureModel(int tmpGestureType){
        remoteGestureType = tmpGestureType;
//        clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.d(TAG, "remoteTouchMove");
//        Log.d(TAG,"drawPath");
//        if(1 == remoteGestureType) {
//            remoteArbitraryGesture.drawArbitraryGesture(canvas);
//        }else if(2 == remoteGestureType){
//            remoteArrowGesture.drawArrowGesture(canvas);
//        }
        remoteArbitraryGesture.drawArbitraryGesture(canvas);
        remoteArrowGesture.drawArrowGesture(canvas);
    }

    public void clear() {
        remoteArbitraryGesture.clear();
        remoteArrowGesture.clear();
        invalidate();
    }

    public void setArbitraryGestureMove(int getX, int getY){
        remoteArbitraryGesture.remoteTouchMove(getX, getY);
        invalidate();
    }

    public void setArbitraryGestureDown(int getX, int getY){
        remoteArbitraryGesture.remoteTouchDown(getX, getY);
        invalidate();
    }

    public void setArbitraryGestureUp(boolean tmpIsSingleTouch, int getX, int getY){
        remoteArbitraryGesture.setSingleTouch(tmpIsSingleTouch);
        invalidate();
    }

    public void setArrowGestureDown(int getX, int getY){
        remoteArrowGesture.setTouchDown(getX, getY);
    }

    public void setArrowGestureUp(int getX, int getY){
        remoteArrowGesture.setTouchUp(getX, getY);
    }


}
