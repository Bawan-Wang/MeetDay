package com.project1.meetday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.FileAccess;
import com.project1.http.ImageLoader;

public class FriendCardDialogActivity extends Activity {
    private final String TAG = this.getClass().getName();

    private Context mContext;
    private Intent mIntent;

    private RelativeLayout mRelativeMessage;
    private RelativeLayout mRelativeCollaborate;
    private TextView mTextViewName;
//    private TextView mTextViewChat;
//    private TextView mTextViewCall;
    private ImageView mImageViewIcon, mImageMessage, mImageCollabrare;
    View.OnTouchListener mColorChangeOnTouchListener;

    private String mRid, mTitle, mMessage;
    private String mLocalUid, mLocalName;
    private DBHelper mDbhelper;
    private SQLiteDatabase mDbrw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_friend_card);
        mContext = getApplicationContext();
        mIntent = getIntent();

        mRid = mIntent.getStringExtra(AskerPage.ASK_PAGE_RID);
        mTitle = mIntent.getStringExtra(AskerPage.ASK_PAGE_TITLE);
        mMessage = FileAccess.getStringFromPreferences(mContext,
                getString(R.string.def_questiontext),
                Const.eUsrType.UsrType_HelpMsg, Const.projinfo.sSharePreferenceName);

        mLocalUid = FileAccess.getStringFromPreferences(mContext, null,
                Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
        mLocalName = FileAccess.getStringFromPreferences(mContext, null,
                Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);

        mDbhelper = new DBHelper(mContext);
        mDbrw = mDbhelper.getWritableDatabase();

        mImageViewIcon = (ImageView) findViewById(R.id.imageViewIcon);
        //mImageViewIcon.setImageResource();
        ImageLoader mImageLoader = new ImageLoader(this);
        mImageLoader.DisplayImage(Const.projinfo.sServerAdr + "upload/user_" + mRid + ".jpg", mImageViewIcon, false);

        mTextViewName = (TextView) findViewById(R.id.textViewName);
        mRelativeMessage = (RelativeLayout) findViewById(R.id.layout_message);
        mRelativeCollaborate = (RelativeLayout) findViewById(R.id.layout_collaborate);
        mImageMessage = (ImageView) findViewById(R.id.imgMessage);
        mImageCollabrare = (ImageView) findViewById(R.id.imgCollaborate);
//        mTextViewChat = (TextView) findViewById(R.id.textViewChat);
//        mTextViewCall = (TextView) findViewById(R.id.textViewCall);
        mTextViewName.setText(mTitle);

        mRelativeMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Log.i(TAG, "Click Chat");
                Intent chatIntent = new Intent();
                chatIntent.setClass(mContext, ChatActivity.class);
                chatIntent.putExtra(HistoryPage.HISTORY_PAGE_RID, mRid);
                chatIntent.putExtra(HistoryPage.HISTORY_PAGE_TITLE, mTitle);
                chatIntent.putExtra(HistoryPage.HISTORY_PAGE_MESSAGE, mMessage);
                startActivity(chatIntent);
                finish();
            }
        });

//        mTextViewChat.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //Log.i(TAG, "Click Chat");
//                Intent chatIntent = new Intent();
//                chatIntent.setClass(mContext, ChatActivity.class);
//                chatIntent.putExtra(HistoryPage.HISTORY_PAGE_RID, mRid);
//                chatIntent.putExtra(HistoryPage.HISTORY_PAGE_TITLE, mTitle);
//                chatIntent.putExtra(HistoryPage.HISTORY_PAGE_MESSAGE, mMessage);
//                startActivity(chatIntent);
//                finish();
//            }
//        });

        mRelativeCollaborate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Log.i(TAG, "Click Call");
                CallConnection(mTitle, mMessage, false);
                finish();
            }
        });

//        mTextViewCall.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //Log.i(TAG, "Click Call");
//                CallConnection(mTitle, mMessage, false);
//                finish();
//            }
//        });

        mColorChangeOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //Log.d(TAG, "Pressed");
                        v.setBackgroundColor(Color.parseColor("#5a937f"));
                        return false; // return false for pass event to OnClickListener
                    case MotionEvent.ACTION_UP:
                        //Log.d(TAG, "Released");
//                        v.setBackgroundColor(Color.parseColor("#73c3a7"));
                        //v.setBackgroundResource(R.drawable.friend_card_botton);
                        return false; // return false for pass event to OnClickListener
                }
                return false;
            }
        };
//        mTextViewCall.setOnTouchListener(mColorChangeOnTouchListener);
//        mTextViewChat.setOnTouchListener(mColorChangeOnTouchListener);
        mRelativeMessage.setOnTouchListener(mColorChangeOnTouchListener);
        mRelativeCollaborate.setOnTouchListener(mColorChangeOnTouchListener);
    }

    @Override
    public void onDestroy() {
        mImageMessage.setBackground(null);
        mImageCollabrare.setBackground(null);
        super.onDestroy();
    }

    public void CallConnection(final String title, final String message, boolean ismeetday) {
        String Content = message;

        if(Content.length()==0){
            ConnectToOther.MessageDialog(mContext,"Error", "Lack of Question Content!");
        } else if(!Const.isNetworkConnected(mContext)){
            ConnectToOther.MessageDialog(mContext,"Error", "You are offline!");
        } else if(!Const.userinfo.get_login_status()){
            ConnectToOther.MessageDialog(mContext,"Error", "You are not Logged in! Try again!");
        } else {
            String rid;
            if(ismeetday){
                rid = Const.officialid;
            } else {
                rid = mRid;
            }
            if(rid.equals(mLocalUid)){
                ConnectToOther.MessageDialog(mContext,"Error", "You can't call yourself!");
                return;
            }
            ConnectToOther con = new ConnectToOther(mContext);

            String roomid = con.genRoomId(mLocalUid, rid);
            Const.Connect_Info.screen_size size = Const.getLocalRes(mContext);
            Log.d(this.getClass().getName(),rid);
            //String helperName = dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(), rid);
            String localName = FileAccess.getStringFromPreferences(mContext, null,
                    Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
            con.SendConnect(title, Content, mLocalUid, rid, roomid,
                    Const.hisinfo.getTableName(),size, localName);

            String helperName = "";//dbhelper.getNamefromSql(dbrw,
            //Const.contactinfo.getTableName(), rid);
            //dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid", rid);
            if(ismeetday){
                helperName = Const.officialname;
            } else {
                helperName = //dbhelper.getNamefromSql(dbrw,
                        //Const.contactinfo.getTableName(), rid);
                        mDbhelper.getdatafromSql(mDbrw, Const.contactinfo.getTableName(), "name"," fid", rid);
            }
            if(helperName != null){
                Intent intent = new Intent();
                intent.setClass(mContext, AskerConnectPage.class);
                intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONX, String.valueOf(size.x));
                intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONY, String.valueOf(size.y));
                intent.putExtra(AskerConnectPage.ASKER_HELPERID, rid);
                intent.putExtra(AskerConnectPage.ASKER_LOCALID, mLocalUid);
                intent.putExtra(AskerConnectPage.ASKER_ROOMID, roomid);
                intent.putExtra(AskerConnectPage.ASKER_MESSAGE, Content);
                intent.putExtra(AskerConnectPage.ASKER_HELPERNAME, helperName);
                intent.putExtra(AskerConnectPage.ASKER_HELPERPHOTO,
                        Const.projinfo.sServerAdr + "upload/user_" + rid + ".jpg");
                startActivity(intent);
            }
        }
    }
}
