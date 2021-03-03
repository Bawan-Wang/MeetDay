package com.project1.meetday;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatHistory {
    private final String TAG = this.getClass().getName();

    public static final int NO_CHAT_HISTORY = -2;
    public static final int UNKNOWN = -1;
    public static final int SEND = 0;
    public static final int RECEIVE = 1;
    public static final int CALL_OUT = 2;
    public static final int CALL_IN_ANSWERED = 3;
    public static final int CALL_IN_MISSED = 4;

    private String mName[];
    private String mTime[]; // from Long
    private String mFid[];
    private String mMsg[];
    private int mDirection[];
    private int mMsgNumber = 0;

    public ChatHistory(int msg_number){
        mMsgNumber = msg_number;
        if(mMsgNumber > 0){
            mName = new String[mMsgNumber];
            mTime = new String[mMsgNumber];
            mFid = new String[mMsgNumber];
            mMsg = new String[mMsgNumber];
            mDirection = new int[mMsgNumber];
        }
        else{
            Log.e(TAG, "msg_number <= 0");
            mMsgNumber = 0;
        }
    }

    public int getmMsgNumber(){
        return mMsgNumber;
    }

    public int setChatHistory(int id, String name, String time, String fid, String msg, int direction){
        if(mMsgNumber > 0){
            if(id < mMsgNumber){
                mName[id] = name;
                mTime[id] = time;
                mFid[id] = fid;
                mMsg[id] = msg;
                mDirection[id] = direction;
                return 0;
            }
            else{
                Log.e(TAG, "ChatHistory id overflow");
                return -1;
            }
        }
        else{
            Log.e(TAG, "ChatHistory not initialization");
            return -1;
        }
    }

    public String getName(int id){
        if(id < mMsgNumber && mMsgNumber > 0){
            return mName[id];
        }
        else{
            Log.e(TAG, "ChatHistory not initialization or overflow");
            return null;
        }
    }

    public String getTime(int id){
        if(id < mMsgNumber && mMsgNumber > 0){
            return mTime[id];
        }
        else{
            Log.e(TAG, "ChatHistory not initialization or overflow");
            return null;
        }
    }

    public String getMsg(int id){
        if(id < mMsgNumber && mMsgNumber > 0){
            return mMsg[id];
        }
        else{
            Log.e(TAG, "ChatHistory not initialization or overflow");
            return null;
        }
    }

    public int getDirection(int id){
        if(id < mMsgNumber && mMsgNumber > 0){
            return mDirection[id];
        }
        else{
            return NO_CHAT_HISTORY;
        }
    }

    public String getFormatData(int id){
        if(id < mMsgNumber && mMsgNumber > 0){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date(Long.parseLong(getTime(id))); // parse format data from long number
            return sdf.format(date);
        }
        else{
            Log.e(TAG, "ChatHistory not initialization or overflow");
            return null;
        }
    }
}
