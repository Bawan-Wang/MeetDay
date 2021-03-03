package com.project1.meetday;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.FileAccess;

public class MySimpleAdapterHelper extends SimpleAdapter {

    private ArrayList<HashMap<String, Object>> results;
    private Context mContext;
    private final static String CALL = "android.intent.action.CALL";
    private String[] eid;
    private String msg;
    
	public MySimpleAdapterHelper(Context context, ArrayList<HashMap<String, Object>> data,
    		int resource, String[] from, int[] to, String[] eid, String msg) {
        super(context, data, resource, from, to);
        this.results = data;
        this.mContext = context;
        this.eid = eid;
        this.msg = msg;
        
    }

    public View getView(final int position, View view, ViewGroup parent){

        Typeface localTypeface1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/BELLB.TTF");
        if (view == null) {
        	view = LayoutInflater.from(mContext).inflate(
					R.layout.helperlist, null);
        }
        TextView tt = (TextView) view.findViewById(R.id.helperlist_txt);
        tt.setText(results.get(position).get("helperList").toString());
        tt.setTypeface(localTypeface1);
        TextView bt = (TextView) view.findViewById(R.id.helperlistnum_txt);
        bt.setText(results.get(position).get("helperListNum").toString());
        bt.setTypeface(localTypeface1);
        ImageButton mImageButton = (ImageButton) view.findViewById(R.id.helpercowork_img);
        String tempStrImg = results.get(position).get("helper_cowork_img").toString();
        if(tempStrImg.equals("camera")){
        	mImageButton.setImageResource(R.drawable.helper_cowork_camera);
        }else{
        	mImageButton.setImageResource(R.drawable.helper_noncowork_camera);
        }
       
        
        view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callPhoneDialog(position);
			}
        	
        });
        
		mImageButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.v("Helper", "button click ----> !!!");
				callRTCDialog(position, msg);
			}
        });        
        
        return view;
    }


	private void callPhoneDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
		HashMap<String,Object> item = new HashMap<String,Object>();
		item = (HashMap<String,Object>) results.get(position);
		String helper = (String)item.get("helperList"); 
		final String helperNumber = (String)item.get("helperListNum");        
        builder.setMessage(helperNumber);
        builder.setTitle(mContext.getResources().getString(R.string.dialog_call) + " " + helper);
        builder.setPositiveButton(R.string.dialog_call, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent call = new Intent(CALL, Uri.parse("tel:" + helperNumber));
				mContext.startActivity(call);					
			}

		});
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

		});
        
        builder.create().show();
	}	
    
	private void callRTCDialog(final int position, String message) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
		HashMap<String,Object> item = new HashMap<String,Object>();
		item = (HashMap<String,Object>) results.get(position);
		final String helper = (String)item.get("helperList"); 
		final EditText input = new EditText(this.mContext);
	    AlertDialog.Builder dialog = new AlertDialog.Builder(this.mContext);
	    input.setText(message);
	    dialog.setView(input);
	    dialog.setTitle(helper);
		//builder.setMessage("Open Real-Time Communication!");
        //builder.setTitle(mContext.getResources().getString(R.string.dialog_call) + " " + helper);
        dialog.setPositiveButton(R.string.dialog_call, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub		
            	String Content = input.getText().toString();
            	if(Content.length()==0){
            		ConnectToOther.MessageDialog(mContext, "Error", "Lack of Question Content!");
            	} else if (!Const.isNetworkConnected(mContext)){
            		ConnectToOther.MessageDialog(mContext, "Error", "You are offline!");
            	} else {
            		//String rid = "";	            		
            		//ConnectToOther con = new ConnectToOther(getActivity());	            		
            		//con.SendRequest(Const.userinfo.uid,rid);
            		//con.Connect(title, Content, rid);
            		ConnectToOther con = new ConnectToOther(mContext);
            		String rid = String.valueOf(Integer.valueOf(eid[position])-10000);
            		String localuid = FileAccess.getStringFromPreferences(mContext, "0", 
                			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
            		String roomid = con.genRoomId(localuid, rid);
            		Const.Connect_Info.screen_size size = Const.getLocalRes(mContext);
            		String localName = FileAccess.getStringFromPreferences(mContext, null, 
                			Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
            		con.SendConnect(helper, Content,localuid, rid, 
            				roomid, Const.hisinfo.getTableName(), size, localName);
            		
            		String helperName = helper;

            		
            		//Const.userinfo.State_Type = Const.eState.State_WaitAck;
            		
            		//Log.d(this.getClass().getName(), String.valueOf(size.x));
            		Intent intent = new Intent();
            		intent.setClass(mContext, AskerConnectPage.class);
            		intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONX, String.valueOf(size.x));
            		intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONY, String.valueOf(size.y));
            		intent.putExtra(AskerConnectPage.ASKER_HELPERID, rid);
            		intent.putExtra(AskerConnectPage.ASKER_LOCALID, localuid);
            		intent.putExtra(AskerConnectPage.ASKER_ROOMID, roomid);
            		intent.putExtra(AskerConnectPage.ASKER_MESSAGE, Content);
            		intent.putExtra(AskerConnectPage.ASKER_HELPERNAME, helperName);
            		intent.putExtra(AskerConnectPage.ASKER_HELPERPHOTO,Const.projinfo.sServerAdr+
            				"upload/user_"+rid+".jpg");
            		mContext.startActivity(intent);
            		//HistoryPage.this.getActivity().finish();
            	}
			}

		});
        dialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

		});
        
        dialog.create().show();
	}	
}