package com.project1.meetday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.project1.http.Const;
import com.project1.http.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryDetailPage extends Activity {
	
	public static final String DETAIL_FID =
		    "com.project1.detail.FID";
	
	private ListView listView;
	private String[] arr;
	private static final int[] mPics = new int[]{
		R.drawable.answer, R.drawable.missedcall, R.drawable.callout, R.drawable.sendgifts, R.drawable.recievegifts
	};
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	private SimpleAdapter adapter;
	private String fid;
	private Context context;
	private DBHelper dbhelper;
	private SQLiteDatabase dbrw;
	private String[] timeArrary, strHistoryType;
	private String TAG = "HistoryDetailPage";
	//private String[] 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.historydetail_page);
		this.context = getBaseContext();
		dbhelper = new DBHelper(context);
		dbrw = dbhelper.getWritableDatabase();

		arr = getResources().getStringArray(R.array.historyDetail);
		final Intent intent = getIntent();
		fid = intent.getStringExtra(DETAIL_FID);
		
		listView = (ListView) findViewById(R.id.HistoryDetail_listView);
		//String num = dbhelper.getnumfromSql(dbrw, Const.hisinfo.getTableName(),fid);
		String[] col = {"time", "type", "msg"};
		Cursor cur = dbrw.query(Const.hisinfo.getTableName(), col, "fid=" + "'" + fid
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			//Log.d(this.getClass().getName(), cur.getCount());
			timeArrary = new String[cur.getCount()];
			strHistoryType = new String[cur.getCount()];
			cur.moveToLast();
			for(int i=0; i<cur.getCount(); i++){
				 HashMap<String,Object> item = new HashMap<String,Object>();
				 String show="";
				 if(cur.getString(1).equals(String.valueOf(Const.History_Type.HType_Dial))){
					 show = arr[2];
					 item.put( "pic", mPics[2]);
				 } else if (cur.getString(1).equals(String.valueOf(Const.History_Type.HType_Incoming))){
					 show = arr[0];
					 item.put( "pic", mPics[0]);
				 } else if (cur.getString(1).equals(String.valueOf(Const.History_Type.HType_Missed))){
					 show = arr[1];
					 item.put( "pic", mPics[1]);
				 } else if (cur.getString(1).equals(String.valueOf(Const.History_Type.Htype_SentThanks))){
					 show = arr[3];
					 item.put( "pic", mPics[3]);
				 } else if (cur.getString(1).equals(String.valueOf(Const.History_Type.HType_RecieveThanks))){
					 show = arr[4];
					 item.put( "pic", mPics[4]);
				 } else if (cur.getString(1).equals(String.valueOf(Const.History_Type.Htype_GetMessage))){
					 show = cur.getString(2);
					 item.put( "pic", mPics[4]);
				 } else if (cur.getString(1).equals(String.valueOf(Const.History_Type.Htype_SendMessage))){
					 show = cur.getString(2);
					 item.put( "pic", mPics[3]);
				 }

				 item.put("string", show + " " + LoaderAdapter.parseTime(cur.getString(0)));
				 list.add(item);
				 timeArrary[i] = cur.getString(0);
				 strHistoryType[i] = cur.getString(1);
				 cur.moveToPrevious();
				 //item.put( "pic", mPics[i]);
			}
			//return cur.getString(0);
		}
		cur.close();

		adapter = new SimpleAdapter(this, list, R.layout.historydetaillist, new String[] { "pic","string"},new int[] { R.id.PhoneSteteIcon, R.id.PhoneSteteInfo} );
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("TAG", timeArrary[position]);
				Log.d(TAG, strHistoryType[position]);
				if (strHistoryType[position].equals("Htype_SentThanks") || strHistoryType[position].equals("HType_RecieveThanks")) {
					Intent nextIntent = new Intent();
					nextIntent.setClass(HistoryDetailPage.this, RecieveThxPage.class);
					nextIntent.putExtra(RecieveThxPage.DETAIL_TIMEID, timeArrary[position]);
					startActivity(nextIntent);
				}
			}
		});
	}
	
	@Override
	public void onDestroy() {
		dbrw.close();
		dbhelper.close();
		Log.d(this.getClass().getName(),"On onDestroy");		
		super.onDestroy();	
	}
	
	@Override
	public void onStop() {
		Log.d(this.getClass().getName(),"On onStop");		
		super.onStop();	
	}
	
	@Override
	public void onBackPressed() {
		   Log.d(this.getClass().getName(), "onBackPressed Called");
		   finish();
		   //super.onDestroy();
	}
}
