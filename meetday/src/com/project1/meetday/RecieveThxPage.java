package com.project1.meetday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.PictUtil;

public class RecieveThxPage extends Activity {
	
	public static final String DETAIL_TIMEID =
		    "com.project1.detail.TIMEID";

	private LinearLayout lilayoutRecieveThx;
	private RelativeLayout relayoutRecieveThxTitle;
	private Bitmap      BmpThxCardBtn = null, bmpReciThxImg = null;
	private ImageButton thxCardInfo_Btn;
	private ImageView   reciThx_Img;
	private TextView    reciThx_Txt;
	private String 		timeid;
	private Context context;
	private DBHelper dbhelper;
	private SQLiteDatabase dbrw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		setContentView(R.layout.recievethx_page);
		this.context = getBaseContext();
		dbhelper = new DBHelper(context);
		dbrw = dbhelper.getWritableDatabase();	
		
		final Intent intent = getIntent();
		timeid = intent.getStringExtra(DETAIL_TIMEID);

		lilayoutRecieveThx = (LinearLayout)findViewById(R.id.lilayoutRecieveThx);
		relayoutRecieveThxTitle = (RelativeLayout)findViewById(R.id.relayoutRecieveThxTitle);

		//dbhelper.getmsgfromSql(dbrw, Const.hisinfo.getTableName(), timeid);
		reciThx_Txt     = (TextView)    findViewById(R.id.recithx_txt);
		reciThx_Img     = (ImageView) findViewById(R.id.recithx_img);
		thxCardInfo_Btn = (ImageButton) findViewById(R.id.thxcardinfo_btn);
		
		reciThx_Txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 
		//reciThx_Txt.setText(dbhelper.getmsgfromSql(dbrw, Const.hisinfo.getTableName(), timeid));
		reciThx_Txt.setText(dbhelper.getdatafromSql(dbrw, Const.hisinfo.getTableName(), "msg", "time", timeid));
		//String thximg = dbhelper.getpatternfromSql(dbrw, Const.hisinfo.getTableName(), timeid);
		String thximg = dbhelper.getdatafromSql(dbrw, Const.hisinfo.getTableName(), "pattern", "time", timeid);
		
		allocateBmp(Const.ThxImg[Integer.valueOf(thximg)]);
		thxCardInfo_Btn.setImageBitmap(BmpThxCardBtn);
		reciThx_Img.setImageBitmap(bmpReciThxImg);
//		reciThx_Img.setBackgroundResource(Const.ThxImg[Integer.valueOf(thximg)]);
		
		thxCardInfo_Btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//String callid = dbhelper.getcallidfromSql(dbrw, Const.hisinfo.getTableName(), timeid);
				String callid = dbhelper.getdatafromSql(dbrw, Const.hisinfo.getTableName(), "callid", "time", timeid);
				String time = dbhelper.gettimeidfromSql(dbrw, Const.hisinfo.getTableName(), callid, timeid);
				//Log.d("XXXX",callid+"/"+time);
        		Intent nextIntent = new Intent();
        		nextIntent.setClass(RecieveThxPage.this, RecieveThxInfo.class);	
        		nextIntent.putExtra(RecieveThxInfo.DETAIL_TIMEID, time);
        		startActivity(nextIntent);				
			}
        });		
	}
	
	@Override
	public void onDestroy() {
		releaseBmp();
		releaseImgView();
		System.gc();
		Log.d(this.getClass().getName(),"On onDestroy");		
		super.onDestroy();	
	}

	private void allocateBmp(final int iReciThxImg) {
		BmpThxCardBtn = PictUtil.getLocalBitmap(this, R.drawable.thxcardinfo, 1);
		bmpReciThxImg = PictUtil.getLocalBitmap(this, iReciThxImg, 1);
	}	
	
	private void releaseBmp() {
		if(BmpThxCardBtn != null){
			BmpThxCardBtn.recycle();
		}
		if(bmpReciThxImg != null){
			bmpReciThxImg.recycle();
		}		
	}	
	
	private void releaseImgView() {
		if(thxCardInfo_Btn != null) {
		    thxCardInfo_Btn.setImageBitmap(null);
			thxCardInfo_Btn.setBackground(null);
	    }
		if(reciThx_Img != null) {
		    reciThx_Img.setImageBitmap(null);
		}
		if(relayoutRecieveThxTitle != null){
			relayoutRecieveThxTitle.setBackground(null);
		}
		if(lilayoutRecieveThx != null){
			lilayoutRecieveThx.setBackground(null);
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
