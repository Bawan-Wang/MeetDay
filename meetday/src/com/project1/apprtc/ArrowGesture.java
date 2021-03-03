package com.project1.apprtc;

import com.project1.meetday.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * Created by JHWang on 2015/5/20.
 */
public class ArrowGesture {
//    Paint mPaint;//
    int mTouchDownX = -100;
    int mTouchDownY = -100;
    int mTouchUpX = -100;
    int mTouchUpY = -100;
    int iBmpWidth = 140;//
    Resources resource;
    Bitmap bmp;

    public ArrowGesture(Resources tmpResource) {
    	//        mPaint = new Paint();//
        resource = tmpResource;
        bmp = BitmapFactory.decodeResource(resource, R.drawable.arrow);
    }

    public void releaseArrowGesture() {
    	bmp.recycle();
    }
    
    public void setTouchDownCoordinate(MotionEvent event){
        mTouchDownX = (int)event.getX();
        mTouchDownY = (int)event.getY();
    }

    public void setTouchUpCoordinate(MotionEvent event){
        mTouchUpX = (int)event.getX();
        mTouchUpY = (int)event.getY();
    }

    public void setTouchDown(int getX, int getY){
        mTouchDownX = getX;
        mTouchDownY = getY;
    }

    public void setTouchUp(int getX, int getY){
        mTouchUpX = getX;
        mTouchUpY = getY;
    }

    public float getTouchDownX(){
        return mTouchDownX;
    }

    public float getTouchDownY(){
        return mTouchDownY;
    }

    public float getTouchUpX(){
        return mTouchUpX;
    }

    public float getTouchUpY(){
        return mTouchUpY;
    }

    public void clear() {
        mTouchDownX = -100;
        mTouchDownY = -100;
        mTouchUpX = -100;
        mTouchUpY = -100;
    }

    public void drawArrowGesture(Canvas canvas) {
        drawImage(canvas, bmp, mTouchUpX, mTouchUpY, iBmpWidth, iBmpWidth, 0, 0);
    }

    public void drawImage(Canvas canvas, Bitmap blt, int iSrcX, int iSrcY, int w, int h, int bx, int by)
    {
        int iArrowWidth, iArrowHeight;
        Rect src = new Rect();
        Rect dst = new Rect();

        iArrowWidth = iBmpWidth/2;
        iArrowHeight = iBmpWidth/2;
        src.left = bx;
        src.top = by;
        src.right = bx + w;
        src.bottom = by + h;
        dst.left = iSrcX;
        dst.top = iSrcY;
        dst.right = iSrcX + w;
        dst.bottom = iSrcY + h;
        canvas.save();
        double alpha, tmpDegree, degree = 0;

        alpha = Math.atan(((double)mTouchUpY - (double)mTouchDownY)/((double)mTouchUpX - (double)mTouchDownX));
        tmpDegree = alpha * (180/Math.PI);
        if(((mTouchUpY - mTouchDownY) >= 0) && ((mTouchUpX - mTouchDownX) >= 0)){
            degree = 90 + tmpDegree;
        }else if(((mTouchUpY - mTouchDownY) >= 0) && ((mTouchUpX - mTouchDownX) < 0)){
            degree = 270 + tmpDegree;
        }else if(((mTouchUpY - mTouchDownY) < 0) && ((mTouchUpX - mTouchDownX) < 0)){
            degree = 270 + tmpDegree;
        }else if(((mTouchUpY - mTouchDownY) < 0) && ((mTouchUpX - mTouchDownX) >= 0)){
            degree = 90 + tmpDegree;
        }
        canvas.rotate((float)degree, (iSrcX+iArrowWidth), (iSrcY+iArrowHeight));
        canvas.drawBitmap(blt, src, dst, null);
        canvas.restore();

        src = null;
        dst = null;
    }

}
