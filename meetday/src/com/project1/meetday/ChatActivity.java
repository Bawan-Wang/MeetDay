package com.project1.meetday;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.FileAccess;
import com.project1.http.PictUtil;

import java.util.ArrayList;
import java.util.List;

import static com.project1.meetday.R.id.mid_list;
import static com.project1.meetday.R.id.top_bar;

/**
 * Created by JHWang on 11/7/2016.
 */
public class ChatActivity extends Activity {
    public static final String BROADCAST_GETMESSAGE_CALLBACK =
            "com.project1.meetday.ChatHistory.BROADCAST_GETMESSAGE_CALLBACK";

    private String TAG = "ChatActivity";

    private Context mContext;
    private Intent mIntent;

    private TextView mFriendNameTxv;
    private EditText mMessageEditText;
    private ImageButton mPhoneButton;
    private Bitmap bmpPhoneBtn = null, bmpSendBtn = null;

    private String mRid, mTitle, mMessage;
	private String mLocalUid, mLocalName;
    private DBHelper mDbhelper;
    private SQLiteDatabase mDbrw;

    private View.OnClickListener mCallClickListener, mSendClickListen;
    private String mTrimMessage;

    private ConnectToOther mCon;

    private ListView mHistoryMessage;
    private List<ChatEntity> mChatList = null;
    private ChatAdapter mChatAdapter = null;
    private ChatHistory mChatHistory = null;

    // for get message callback by Broadcast
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mResponse;

    // for dynamic adjust the height of history list
    private View mChildOfContent;
    private int mFullHeight, mNowHeight;
    private RelativeLayout mTopBar;
    private RelativeLayout mMidList;
    private LinearLayout mBarBottom;
    private int mTopBarHeight, mBarBottomHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mContext = getApplicationContext();
        mIntent = getIntent();

        FrameLayout content = (FrameLayout) findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mTopBar = (RelativeLayout)findViewById(top_bar);
        mBarBottom = (LinearLayout)findViewById(R.id.bar_bottom);
        mMidList = (RelativeLayout)findViewById(mid_list);

        // set the height of mMidList first
        mFullHeight = getUsableHeight();
        mNowHeight = mFullHeight;
        mTopBarHeight  = getViewHeight(mTopBar);
        mBarBottomHeight  = getViewHeight(mBarBottom);
        Log.d(TAG, "top_bar height = " + mTopBarHeight);
        Log.d(TAG, "bar_bottom height = " + mBarBottomHeight);
        setViewHeight(mMidList, mFullHeight - mTopBarHeight - mBarBottomHeight);

        mRid = mIntent.getStringExtra(HistoryPage.HISTORY_PAGE_RID);
        mTitle = mIntent.getStringExtra(HistoryPage.HISTORY_PAGE_TITLE);
        mMessage = mIntent.getStringExtra(HistoryPage.HISTORY_PAGE_MESSAGE);

        mLocalUid = FileAccess.getStringFromPreferences(mContext, null,
                        Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
        mLocalName = FileAccess.getStringFromPreferences(mContext, null,
                        Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);

        mFriendNameTxv = (TextView) findViewById(R.id.chat_text_view);
        mMessageEditText = (EditText) findViewById(R.id.et_sendmessage);
        mPhoneButton = (ImageButton) findViewById(R.id.button_phone);

        mHistoryMessage = (ListView) findViewById(R.id.list_history_message);

        mFriendNameTxv.setText(mTitle);

        mCon = new ConnectToOther(mContext);

        allocateBmp();

        mCallClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Click phone button");
                Log.d(TAG, "mRid = " + mRid);
                Log.d(TAG, "mTitle = " + mTitle);
                Log.d(TAG, "mMessage = " + mMessage);
                CallConnection(mRid, mTitle, mMessage);
            }
        };

        mSendClickListen = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTrimMessage != null){
                    //Log.d(TAG, mTrimMessage);
                    mCon.SendMessage(mLocalUid, mRid, mTrimMessage, mLocalName);
                    mMessageEditText.setText("");
                    updateChatHistory();
                }
            }
        };

        mPhoneButton.setImageBitmap(bmpPhoneBtn);
        mPhoneButton.setOnClickListener(mCallClickListener);

        mDbhelper = new DBHelper(mContext);
        mDbrw = mDbhelper.getWritableDatabase();

        mMessageEditText.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTrimMessage = mMessageEditText.getText().toString().trim();
                if(mTrimMessage.length() == 0){
                    //Log.d(TAG, "Clear Text");
                    mPhoneButton.setImageBitmap(bmpPhoneBtn);
                    mPhoneButton.setOnClickListener(mCallClickListener);
                }
                else{
                    mPhoneButton.setImageBitmap(bmpSendBtn);
                    mPhoneButton.setOnClickListener(mSendClickListen);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // do nothing
            }
        });

        // initialization for chatting history list view
        mChatList = new ArrayList<ChatEntity>();
        mChatAdapter = new ChatAdapter(mContext, mChatList, mRid);
        mHistoryMessage.setAdapter(mChatAdapter);

        // read chatting history from DB
        updateChatHistory();

        // setting broadcast receiver
        mResponse = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_GETMESSAGE_CALLBACK)) {
                    Log.d(TAG, "Receive message callback");
                    String fid = intent.getStringExtra("fid");
                    Log.d(TAG, "fid = " + fid);
                    if(fid.equals(mRid)){
                        updateChatHistory();
                    }
                }
            }
        };
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCAST_GETMESSAGE_CALLBACK);
        registerReceiver(mResponse, mIntentFilter);

        // Listen the layout change. If soft keyboard pop out, the layout will change
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                Log.d(TAG, "Layout change, usable height  = " + getUsableHeight());
                if(getUsableHeight() != mNowHeight){
                    mNowHeight = getUsableHeight();
                    setViewHeight(mMidList, getUsableHeight() - mTopBarHeight - mBarBottomHeight);
                }
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        releaseBmp();
        unregisterReceiver(mResponse);
    }

    private void allocateBmp() {
        if(bmpPhoneBtn == null) {
            //bmpPhoneBtn = PictUtil.getLocalBitmap(mContext, R.drawable.ic_phone_white_48dp, 1);
            bmpPhoneBtn = PictUtil.getLocalBitmap(mContext, R.drawable.ic_border_color_white_24dp, 1);
        }
        if(bmpSendBtn == null){
            bmpSendBtn = PictUtil.getLocalBitmap(mContext, R.drawable.ic_send_white_48dp, 1);
        }
    }

    private void releaseBmp() {
        if(bmpPhoneBtn != null){
            bmpPhoneBtn.recycle();
        }
        if(bmpSendBtn!= null){
            bmpSendBtn.recycle();
        }
    }

    public void CallConnection(final String rid, final String title, final String message) {
        {
            String Content = message;
            if(Content.length()==0){
                ConnectToOther.MessageDialog(mContext,"Error", "Lack of Question Content!");
            } else if(!Const.isNetworkConnected(mContext)){
                ConnectToOther.MessageDialog(mContext,"Error", "You are offline!");
            } else if(!Const.userinfo.get_login_status()){
                ConnectToOther.MessageDialog(mContext,"Error", "You are not Logged in! Try again!");
            } else {
                String localuid = FileAccess.getStringFromPreferences(mContext, null,
                        Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
                String roomid = mCon.genRoomId(localuid, rid);
                Const.Connect_Info.screen_size size = Const.getLocalRes(mContext);
                String localName = FileAccess.getStringFromPreferences(mContext, null,
                        Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
                mCon.SendConnect(title, Content,localuid, rid,
                        roomid, Const.hisinfo.getTableName(), size, localName);

                String helperName;
                if(mDbhelper.checkexistfromSql(mDbrw, Const.contactinfo.getTableName(), rid)){
                    helperName = //dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(), rid);
                            mDbhelper.getdatafromSql(mDbrw, Const.contactinfo.getTableName(), "name"," fid", rid);
                } else {
                    helperName = //dbhelper.getNamefromSql(dbrw, Const.hisinfo.getTableName(), rid);
                            mDbhelper.getdatafromSql(mDbrw, Const.hisinfo.getTableName(), "name"," fid", rid);
                }

                Intent intent = new Intent();
                intent.setClass(mContext, AskerConnectPage.class);
                intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONX, String.valueOf(size.x));
                intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONY, String.valueOf(size.y));
                intent.putExtra(AskerConnectPage.ASKER_HELPERID, rid);
                intent.putExtra(AskerConnectPage.ASKER_LOCALID, localuid);
                intent.putExtra(AskerConnectPage.ASKER_ROOMID, roomid);
                intent.putExtra(AskerConnectPage.ASKER_MESSAGE, Content);
                intent.putExtra(AskerConnectPage.ASKER_HELPERNAME, helperName);
                intent.putExtra(AskerConnectPage.ASKER_HELPERPHOTO, Const.projinfo.sServerAdr+
                        "upload/user_"+rid+".jpg");
                startActivity(intent);
                updateChatHistory();
            }
        }
    }

    public void updateChatHistory(){
        mChatHistory = mDbhelper.getChatHistory(mDbrw, Const.hisinfo.getTableName(), "msg", " fid", mRid);
        mChatList.clear();
        if(mChatHistory != null){
            Log.d(TAG, "Mssage Number: " + Integer.toString(mChatHistory.getmMsgNumber()));
            for(int i = 0; i < mChatHistory.getmMsgNumber(); i++){
                //Log.d(TAG, "Msg = " + mChatHistory.getMsg(i));
                //Log.d(TAG, "Direction = " + mChatHistory.getDirection(i));
                ChatEntity chatEntity = new ChatEntity();
                if(mChatHistory.getDirection(i) == ChatHistory.RECEIVE){
                    chatEntity.setComeMsg(true);
                    chatEntity.setContent(mChatHistory.getMsg(i));
                    chatEntity.setChatTime(mChatHistory.getFormatData(i));
                }
                else if(mChatHistory.getDirection(i) == ChatHistory.SEND){
                    chatEntity.setComeMsg(false);
                    chatEntity.setContent(mChatHistory.getMsg(i));
                    chatEntity.setChatTime(mChatHistory.getFormatData(i));
                }
                else if(mChatHistory.getDirection(i) == ChatHistory.CALL_OUT){
                    chatEntity.setComeMsg(false);
                    chatEntity.setContent(getResources().getString(R.string.chat_msg_call_out));
                    chatEntity.setChatTime(mChatHistory.getFormatData(i));
                }
                else if(mChatHistory.getDirection(i) == ChatHistory.CALL_IN_ANSWERED){
                    chatEntity.setComeMsg(true);
                    chatEntity.setContent(getResources().getString(R.string.chat_msg_call_in_answered));
                    chatEntity.setChatTime(mChatHistory.getFormatData(i));
                }
                else if(mChatHistory.getDirection(i) == ChatHistory.CALL_IN_MISSED){
                    chatEntity.setComeMsg(true);
                    chatEntity.setContent(getResources().getString(R.string.chat_msg_call_in_missed));
                    chatEntity.setChatTime(mChatHistory.getFormatData(i));
                }
                else{
                    Log.e(TAG, "Not chatting history");
                }
                mChatList.add(chatEntity);
            }
            Log.d(TAG, "mChatList.size() = " + Integer.toString(mChatList.size()));
            mHistoryMessage.setSelection(mChatAdapter.getCount() -1); // auto scroll to the latest message
        }
        else{
            Log.i(TAG, "No chatting history");
        }
    }

    private int getUsableHeight(){
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        //Log.d(TAG, "getUsableHeight: top = " + Integer.toString(r.top));
        //Log.d(TAG, "getUsableHeight: bottom = " + Integer.toString(r.bottom));
        return (r.bottom - r.top);
    }

    private int getViewHeight(View view){
        view.measure(0, 0);
        return view.getMeasuredHeight();
    }

    private void setViewHeight(View view, int height){
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        linearParams.height = height;
        view.setLayoutParams(linearParams);
        Log.d(TAG, "setViewHeight = " + height);
    }
}
