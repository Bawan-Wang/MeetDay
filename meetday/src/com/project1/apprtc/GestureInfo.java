package com.project1.apprtc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Path;

public class GestureInfo {
	private Path mPath;
	private int mGestureColor;
    private float mGestureWidth;
    private boolean eraserMode = false;
	
    public GestureInfo(){
    	
    }
    
    public GestureInfo(Path mPath, int mGestureColor, float mGestureWidth,boolean eraserMode){
    	this.mPath = mPath;
    	this.mGestureColor = mGestureColor;
    	this.mGestureWidth = mGestureWidth;
    	this.eraserMode = eraserMode;
    }
    
    public void setPath(Path mPath){
    	this.mPath = mPath;
    }
    
    public Path getPath(){
    	return this.mPath;
    }
    
    public void setColor(int mGestureColor){
    	this.mGestureColor = mGestureColor;
    }
    
    public int getColor(){
    	return this.mGestureColor;
    }
    
    public void setWidth(float mGestureWidth){
    	this.mGestureWidth = mGestureWidth;
    }
    
    public float getWidth(){
    	return this.mGestureWidth;
    }
    
    public void setErase(boolean eraserMode){
    	this.eraserMode = eraserMode;
    }
    
    public boolean getErase(){
    	return this.eraserMode;
    }
}
