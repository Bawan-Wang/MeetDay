package com.project1.apprtc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.project1.meetday.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.PorterDuff;

/**
 * Created by JHWang on 2015/5/17.
 */
public class ArbitraryGesture {
    private Paint mPaint;
    private Path mPath;
    private GestureInfo RInfo;
    private static final boolean GESTURE_RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;
    private final Rect mInvalidRect = new Rect();
//    private boolean mCapturing;
    private int mGestureColor;
    private int mInvalidateExtraBorder;
    private float mGestureWidth;
    private float mX;
    private float mY;
    private float mCurveEndX;
    private float mCurveEndY;
    private float mCurveStartX;
    private float mCurveStartY;
    private float mDistanceInX;
    private float mDistanceInY;
    private static final String TAG = "GestureView";
    private boolean isSingleTouch = false;
    private int mHoldBmpX;
    private int mHoldBmpY;
    private int iBmpWidth;
    private int iBmpHeight;
    private Resources resources;
//    private Bitmap bmpCircle;

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>(); 
    private Map<Path, Integer> colorsMap = new HashMap<Path,Integer>();
    private Map<Path, Boolean> eraseMap = new HashMap<Path,Boolean>();
    private Map<Path, Float> widthMap = new HashMap<Path,Float>();
    boolean eraserMode = false;
    private boolean isTouchDown = false;
    private boolean isClear = false;
   
    public ArbitraryGesture(Resources tmpResource){
        mPaint = new Paint();
        mPath = new Path();
//        mCapturing = true;
        mGestureColor = Color.BLUE;
        mInvalidateExtraBorder = 10;
        mGestureWidth = 8f;
        mX = -1;
        mY = -1;
        mHoldBmpX = -100;
        mHoldBmpY = -100;
        iBmpWidth = 140;
        iBmpHeight = 140;
        resources = tmpResource;
//        bmpCircle = BitmapFactory.decodeResource(resources, R.drawable.circle);
        init();
    }

    public void releaseArbitraryGesture() {
    	paths.clear();
    	paths.trimToSize();
    	undonePaths.clear();
    	undonePaths.trimToSize();
    	colorsMap.clear();
    	eraseMap.clear();
    	mPaint = null;
    	mPath = null;
    }
    
    private void init() {
        mPaint.setAntiAlias(GESTURE_RENDERING_ANTIALIAS);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mGestureWidth);
        mPaint.setDither(DITHER_FLAG);
        mPath.reset();
    }

    public void setLocalGestureColor(boolean isCaller){
        if(isCaller){
            mGestureColor = Color.BLUE;
        }else{
            mGestureColor = Color.GREEN;
        }
        mPaint.setColor(mGestureColor);
        mPath.reset();
    }
    
    public void setLocalGestureColor(int color){
    	eraserMode = false;
        mGestureColor = color;
        mGestureWidth = 8f;
    }
    
    public void setErase(int color){
    	eraserMode = true;
    	mGestureColor = color;
    	mGestureWidth = 30f;
    	//mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));  	
    	//mPaint.setAlpha(0x80);
    }

    public void setRemoteGestureColor(boolean isCaller){
        if(isCaller){
            mGestureColor = Color.GREEN;
        }else{
            mGestureColor = Color.BLUE;
        }
        mPaint.setColor(mGestureColor);
        mPath.reset();
    }

    public void setSingleTouch(boolean tmpSingleTouch){
    	if(!isClear){
        	paths.add(mPath);
            colorsMap.put(mPath, mGestureColor);
            eraseMap.put(mPath, eraserMode);
            widthMap.put(mPath, mGestureWidth);
            isSingleTouch = tmpSingleTouch;  
            isClear = false;
            RInfo = new GestureInfo(mPath, mGestureColor, mGestureWidth, eraserMode);
    	} else {
    		RInfo = null;
    	}
        //mPath_R = mPath;       
        mPath = new Path();
        mPath.reset();
    }

    public void addRemotePath(GestureInfo info){
    	paths.add(info.getPath());
        colorsMap.put(info.getPath(), info.getColor());
        eraseMap.put(info.getPath(), info.getErase());
        widthMap.put(info.getPath(), info.getWidth());
    }
    
    public boolean getSingleTouch(){
        return isSingleTouch;
    }

    public float getMoveX(){
        return mX;
    }

    public float getMoveY(){
        return mY;
    }

    public float getCurveEndX(){
        return mCurveEndX;
    }

    public float getCurveEndY(){
        return mCurveEndY;
    }

    public void clear() {
    	if(isTouchDown){
    		isClear = true;
    	}
        mPath.rewind();
        paths = new ArrayList<Path>();
        // Repaints the entire view.
        //mHoldBmpX = -100;
        //mHoldBmpY = -100;
        isSingleTouch = false;
        System.gc();
    }
    
    protected void drawArbitraryGesture(Canvas canvas) {  	
       for (Path p : paths){
            mPaint.setColor(colorsMap.get(p));
            mPaint.setStrokeWidth(widthMap.get(p));           
            if(eraseMap.get(p)){          	
            	mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));            	
            } else {
            	mPaint.setXfermode(null);
            }
            canvas.drawPath(p, mPaint);
       }
//        if(isSingleTouch) {
//            Log.d(TAG, "drawCircle");
//            mHoldBmpX = (int)mCurveStartX;
//            mHoldBmpY = (int)mCurveStartY;
//            drawImage(canvas, bmpCircle, mHoldBmpX, mHoldBmpY, iBmpWidth, iBmpHeight,0, 0);
            //canvas.drawPath(mPath, mPaint);
//        }else{
//            Log.d(TAG,"drawPath");
//            drawImage(canvas, bmpCircle, mHoldBmpX, mHoldBmpY, iBmpWidth, iBmpHeight,0, 0);
            //canvas.drawPath(mPath, mPaint);
            mPaint.setColor(mGestureColor);
//        }
        if(eraserMode || isClear){
        	mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
        	mPaint.setXfermode(null);
        }
        mPaint.setStrokeWidth(mGestureWidth);
        canvas.drawPath(mPath, mPaint);
    }

    public void localTouchDown(MotionEvent event) {
        touchDown(event.getX(), event.getY());       
    }

    public Rect localTouchMove(MotionEvent event) {
        Rect areaToRefresh = touchMove(event.getX(), event.getY());
        return areaToRefresh;
    }


    public void localTouchUp(MotionEvent event, boolean b) {
    	if(!isClear){
			paths.add(mPath);
		    colorsMap.put(mPath, mGestureColor);
		    eraseMap.put(mPath, eraserMode);
		    widthMap.put(mPath, mGestureWidth);
		    isClear = false;
    	}
        touchUp(event.getX(), event.getY());
        mPath = new Path();      
    }

    public Rect remoteTouchMove(int getX, int getY) {
        Rect areaToRefresh = touchMove((float)getX, (float)getY);
        return areaToRefresh;
    }

    public void remoteTouchDown(int getX, int getY) {
        touchDown((float)getX, (float)getY);
    }

    private void touchDown(float getX, float getY){
        float x = getX;
        float y = getY;

        mPath.reset();
        mX = x;
        mY = y;
        mPath.moveTo(x, y);

        final int border = mInvalidateExtraBorder;
        mInvalidRect.set((int) x - border, (int) y - border, (int) x + border,
                (int) y + border);

        mCurveStartX = mCurveEndX = x;
        mCurveStartY = mCurveEndY = y;

        isSingleTouch = false;
        isTouchDown = true;
        isClear = false;
    }

    private Rect touchMove(float getX, float getY) {
        Rect areaToRefresh = null;

        final float x = getX;
        final float y = getY;

        final float previousX = mX;
        final float previousY = mY;

        areaToRefresh = mInvalidRect;

        // start with the curve end
        final int border = mInvalidateExtraBorder;
        areaToRefresh.set((int) mCurveEndX - border, (int) mCurveEndY - border,
                (int) mCurveEndX + border, (int) mCurveEndY + border);

        float cX = mCurveEndX = (x + previousX) / 2;
        float cY = mCurveEndY = (y + previousY) / 2;

        mPath.quadTo(previousX, previousY, cX, cY);

        // union with the control point of the new curve
        areaToRefresh.union((int) previousX - border, (int) previousY - border,
                (int) previousX + border, (int) previousY + border);

        mX = x;
        mY = y;

        isSingleTouch = false;

        return areaToRefresh;
    }

    private void touchUp(float getX, float getY){
        final float x = getX;
        final float y = getY;
        float fTmpDistanceInX = (x - mCurveStartX);
        float fTmpDistanceInY = (y - mCurveStartY);
        mDistanceInX = (fTmpDistanceInX > 0)? fTmpDistanceInX : -1*fTmpDistanceInX;
        mDistanceInY = (fTmpDistanceInY > 0)? fTmpDistanceInY : -1*fTmpDistanceInY;
//        if(mDistanceInX < 10 && mDistanceInY < 10){
//            isSingleTouch = true;
//        }else{
//            isSingleTouch = false;
//        }
        isTouchDown = false;
        isClear = false;
    }

//    private void drawImage(Canvas canvas, Bitmap blt, int iSrcX, int iSrcY, int w, int h, int bx, int by) {
//        Rect src = new Rect();
//        Rect dst = new Rect();
//        src.left = bx;
//        src.top = by;
//        src.right = bx + w;
//        src.bottom = by + h;
//        dst.left = iSrcX;
//        dst.top = iSrcY;
//        dst.right = iSrcX + w;
//        dst.bottom = iSrcY + h;
//        canvas.save();
//        canvas.drawBitmap(blt, src, dst, null);
//        canvas.restore();
//        src = null;
//        dst = null;
//    }
   
    public int getPathSize(){
    	return paths.size();
    }
    
    public int getPathSizeUnDo(){
    	return undonePaths.size();
    }
    
    public void UndoMove(){
    	undonePaths.add(paths.remove(paths.size()-1));
    }
    
    public void RedoMove(){
    	paths.add(undonePaths.remove(undonePaths.size()-1)); 
    }
    
    public GestureInfo getRInfo(){
    	return RInfo;
    }
    
}
