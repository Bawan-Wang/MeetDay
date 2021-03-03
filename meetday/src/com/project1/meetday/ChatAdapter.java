package com.project1.meetday;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.ImageLoader;
import com.project1.http.PictUtil;

import java.io.File;
import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private final String TAG = this.getClass().getName();

    public static final String CHAT_ADAPTER_MSG =
            "com.project1.meetday.ChatAdapter.MSG";

    private Context context = null;
    private List<ChatEntity> chatList = null;
    private LayoutInflater inflater = null;
    private int COME_MSG = 0;
    private int TO_MSG = 1;

    ImageLoader mImageLoader;
    private String mReceiverIconPath;
    private File mImgReceiverFile;
    Bitmap mSenderBitmap;
    Bitmap mReceiverBitmap;

    public ChatAdapter(Context context,List<ChatEntity> chatList, String rid){
        this.context = context;
        this.chatList = chatList;
        inflater = LayoutInflater.from(this.context);

        mImageLoader = new ImageLoader(this.context);
        mReceiverIconPath = mImageLoader.GetCacheFile(Const.projinfo.sServerAdr + "upload/user_" + rid + ".jpg");
        mImgReceiverFile = new File(mReceiverIconPath);
        if(mImgReceiverFile.exists()){
            mReceiverBitmap = BitmapFactory.decodeFile(mImgReceiverFile.getAbsolutePath());
        }
        else{
            mReceiverBitmap = null;
        }
        mSenderBitmap = PictUtil.loadFromFile(Const.localProfilePicPath);
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ChatEntity entity = chatList.get(position);
        if (entity.isComeMsg())
        {
            return COME_MSG;
        }else{
            return TO_MSG;
        }
    }

    @Override
    public int getViewTypeCount(){
        // we have two type of view
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatHolder chatHolder = null;
        final String msg = chatList.get(position).getContent();
        if (convertView == null) {
            chatHolder = new ChatHolder();
            if (chatList.get(position).isComeMsg()) {
                convertView = inflater.inflate(R.layout.chat_from_item, null);
            }else {
                convertView = inflater.inflate(R.layout.chat_to_item, null);
            }
            chatHolder.timeTextView = (TextView) convertView.findViewById(R.id.tv_time);
            chatHolder.contentTextView = (TextView) convertView.findViewById(R.id.tv_content);
            // Show menu of each chatting message by long-click
            chatHolder.contentTextView.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v) {
                    Log.i(TAG, "Show menu of each chatting message by long-click");
                    Intent chatMsgIntent = new Intent(context, ChatMsgMenu.class);
                    chatMsgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    chatMsgIntent.putExtra(CHAT_ADAPTER_MSG, msg);
                    context.startActivity(chatMsgIntent);
                    v.setBackgroundColor(Color.parseColor("#ebebeb"));
                    return false;
                }
            });

            chatHolder.contentTextView.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //Log.d(TAG, "Pressed");
                            v.setBackgroundColor(Color.parseColor("#DDDDDD"));
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            //Log.d(TAG, "Released");
                            v.setBackgroundColor(Color.parseColor("#ebebeb"));
                            break;
                    }
                    return false; // return false for pass event to OnLongClickListener
                }
            });

            chatHolder.userImageView = (ImageView) convertView.findViewById(R.id.iv_user_image);
            convertView.setTag(chatHolder);
        }else {
            chatHolder = (ChatHolder)convertView.getTag();
        }
        chatHolder.timeTextView.setText(chatList.get(position).getChatTime());
        chatHolder.contentTextView.setText(chatList.get(position).getContent());
        //chatHolder.userImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.person));
        // road person icon
        if (chatList.get(position).isComeMsg()) {
            if(mReceiverBitmap != null){
                chatHolder.userImageView.setImageBitmap(mReceiverBitmap);
            }
            else{
                Log.e(TAG, "mImgReceiverFile does not exists");
                chatHolder.userImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.person));
            }
        }
        else{
            if(mSenderBitmap != null){
                chatHolder.userImageView.setImageBitmap(mSenderBitmap);
            }
            else{
                chatHolder.userImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.person));
            }
        }

        return convertView;
    }

    private class ChatHolder{
        private TextView timeTextView;
        private ImageView userImageView;
        private TextView contentTextView;
    }
}
