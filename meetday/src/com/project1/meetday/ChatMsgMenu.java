package com.project1.meetday;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by a8002 on 2017/4/12.
 */

public class ChatMsgMenu extends Activity {
    private final String TAG = this.getClass().getName();

    private Context mContext;
    private Intent mIntent;

    private TextView mTextViewMenu1;
    private String mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_msg_menu);
        mContext = getApplicationContext();
        mIntent = getIntent();

        mMessage = mIntent.getStringExtra(ChatAdapter.CHAT_ADAPTER_MSG);
        mTextViewMenu1 = (TextView) findViewById(R.id.chtMsgMenu1);

        mTextViewMenu1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ClipboardManager clipboard_manager = (ClipboardManager)mContext.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip_data = ClipData.newPlainText("text", mMessage);
                clipboard_manager.setPrimaryClip(clip_data);
                finish();
            }
        });

        mTextViewMenu1.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //Log.d(TAG, "Pressed");
                        v.setBackgroundColor(Color.parseColor("#66CDAA"));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        //Log.d(TAG, "Released");
                        v.setBackgroundResource(R.color.meetday);
                        break;
                }
                return false; // return false for pass event to OnClickListener
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
