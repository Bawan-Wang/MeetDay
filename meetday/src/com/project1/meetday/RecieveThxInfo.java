package com.project1.meetday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.DBHelper;

import java.util.Calendar;

public class RecieveThxInfo extends Activity {
	
	public static final String DETAIL_TIMEID =
		    "com.project1.detail.TIMEID";

	private LinearLayout lilayoutRecieveInfo;
	private TextView    name_Txt, date_Txt, time_Txt, recordproblem_Txt;	
	private String 		timeid;
	private Context context;
	private DBHelper dbhelper;
	private SQLiteDatabase dbrw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.recievethxinfo_page);	
		this.context = getBaseContext();
		dbhelper = new DBHelper(context);
		dbrw = dbhelper.getWritableDatabase();	
		
		final Intent intent = getIntent();
		timeid = intent.getStringExtra(DETAIL_TIMEID);

		lilayoutRecieveInfo = (LinearLayout) findViewById(R.id.lilayoutRecieveInfo);
		name_Txt          = (TextView) findViewById(R.id.name_txt);
		name_Txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 
		date_Txt          = (TextView) findViewById(R.id.date_txt);
		date_Txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 
		time_Txt          = (TextView) findViewById(R.id.time_txt);
		time_Txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 
		recordproblem_Txt = (TextView) findViewById(R.id.recordproblem_txt);
		recordproblem_Txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 
		//recordproblem_Txt.setText(dbhelper.getmsgfromSql(dbrw, Const.hisinfo.getTableName(), timeid));
		recordproblem_Txt.setText(dbhelper.getdatafromSql(dbrw, Const.hisinfo.getTableName(), "msg", "time",timeid));
		//name_Txt.setText(dbhelper.getnamefromSql(dbrw, Const.hisinfo.getTableName(), timeid));
		name_Txt.setText(dbhelper.getdatafromSql(dbrw, Const.hisinfo.getTableName(), "name", "time",timeid));
		Calendar calendar = Calendar.getInstance();
		//hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
		//Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTimeInMillis(Long.parseLong(timeid));
		date_Txt.setText(Integer.toString(calendar.get(Calendar.YEAR))+"."
				+Integer.toString(calendar.get(Calendar.MONTH)+1)+"."+
				Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
		time_Txt.setText(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))+":"+
				String.format("%02d", calendar.get(Calendar.MINUTE)));
	}
	
	@Override
	public void onDestroy() {
		Log.d(this.getClass().getName(),"On onDestroy");
		releaseViews();
		System.gc();
		super.onDestroy();	
	}

	private void releaseViews() 	{
		if(name_Txt != null){
			name_Txt.setBackground(null);
		}
		if(date_Txt != null){
			date_Txt.setBackground(null);
		}
		if(time_Txt != null){
			time_Txt.setBackground(null);
		}
		if(recordproblem_Txt != null){
			recordproblem_Txt.setBackground(null);
		}
		if(lilayoutRecieveInfo != null){
			lilayoutRecieveInfo.setBackground(null);
		}
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
