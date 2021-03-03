package com.project1.meetday;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.FileAccess;
import com.project1.http.ImageLoader;

import java.util.Calendar;

public class LoaderAdapter extends BaseAdapter{

	//private static final String TAG = "LoaderAdapter";
	private boolean mBusy = false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	
	private ImageLoader mImageLoader;
	private int mCount;
	private Context mContext;
	private Const.eLocation mLocation;// = Const.eLocation.Location_Contact;
	private String[] urlArrays;
	private String[] nameArrays;
	private String[] timeArrays;
	private String[] typeArrays;
	private String[] fidArrays;	
	//private String[] callidArrays;
	private int[] numArrays;
		
	public LoaderAdapter(Context context, DBHelper dbhelper ,String tablename, Const.eLocation location) {
		//this.mCount = count;
		this.mContext = context;
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		this.mCount = dbhelper.getfidnumerfromSql(dbrw, tablename);
		mLocation = location;	
		setLoadAdapter(context, dbhelper, tablename);
		
		mImageLoader = new ImageLoader(context);
	}
	
	public LoaderAdapter(Context context, DBHelper dbhelper, String tablename) {
		//this.mCount = count;
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		this.mContext = context;		
		this.mCount = dbhelper.getfidnumerfromSql(dbrw, tablename);
		//urlArrays = url;
		urlArrays = new String[mCount];
		nameArrays = new String[mCount];
		fidArrays = new String[mCount];
		String[] idlist;
		//DBHelper dbhelper = new DBHelper(context);
				
		idlist = dbhelper.getidfromSql(dbrw, tablename);		
		for(int i=0; i<mCount; i++){	
			String id = //dbhelper.getfidfromSql(dbrw, tablename,idlist[i]);
			 dbhelper.getdatafromSql(dbrw, tablename, "fid", "_id", 
						idlist[i]);
			fidArrays[i] = id;
			urlArrays[i] = Const.projinfo.sServerAdr+"upload/user_"+id+".jpg";
			nameArrays[i] = dbhelper.getdatafromSql(dbrw, tablename, "name"," fid", id); 
					//dbhelper.getNamefromSql(dbrw, tablename, id);
					//dbhelper.getfidfromSql(dbrw, tablename,idlist[i]));
		}
		//nameArrays = name;
		//dbrw.close();
		mLocation = Const.eLocation.Location_Contact;
		mImageLoader = new ImageLoader(context);
	}
	
	public LoaderAdapter(int count, Context context, DBHelper dbhelper, String tablename, String []friend_lst) {
		this.mCount = count;
		this.mContext = context;
		//urlArrays = url;
		urlArrays = new String[count];
		nameArrays = new String[count];
		fidArrays = new String[count];
		//DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		for(int i=0; i<count; i++){		
			fidArrays[i] = friend_lst[i];
			urlArrays[i] = Const.projinfo.sServerAdr+"upload/user_"+friend_lst[i]+".jpg";
			nameArrays[i] = //dbhelper.getNamefromSql(dbrw, tablename, friend_lst[i]);
					dbhelper.getdatafromSql(dbrw, tablename, "name"," fid", friend_lst[i]);
		}
		//nameArrays = name;
		//dbrw.close();
		mLocation = Const.eLocation.Location_Contact;
		mImageLoader = new ImageLoader(context);
	}
	
	public LoaderAdapter(int count, Context context, String []friend_lst, String []name) {
		this.mCount = count;
		this.mContext = context;
		//urlArrays = url;
		urlArrays = new String[count];
		for(int i=0; i<count; i++){			
			urlArrays[i] = Const.projinfo.sServerAdr+"upload/user_"+friend_lst[i]+".jpg";
			//friend_lst = friend_lst.substring(friend_lst.indexOf("/")+1);
		}
		nameArrays = name;
		mImageLoader = new ImageLoader(context);
	}
	
	public String getlistfid(int position){
		if(fidArrays!=null)
			return fidArrays[position];
		else
			return null;
	}
	
	public ImageLoader getImageLoader(){
		return mImageLoader;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder;
		if (convertView == null) {
			if(mLocation == Const.eLocation.Location_Record){
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.historylist, null);
				viewHolder = new ViewHolder();
				viewHolder.mTextView = (TextView) convertView
						.findViewById(R.id.history_message);
				viewHolder.mTextView_Small = (TextView) convertView
						.findViewById(R.id.history_time);
				viewHolder.mImageView = (ImageView) convertView
						.findViewById(R.id.history_photo);
				viewHolder.mImageButton = (ImageButton) convertView
						.findViewById(R.id.history_icon);						
				//ImageButton button = (ImageButton) view.findViewById(R.id.history_icon);
				//Log.d("XXXX",String.valueOf(position) + "SDSDSDSDS"+fidArrays[position]);
				viewHolder.mImageButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Log.v("History", "button click ----> !!!");
//						Intent i = new Intent();
//						i.setClass(mContext, HistoryDetailPage.class);
//						Integer pos = viewHolder.position;//(Integer)v.getTag();
//						i.putExtra(HistoryDetailPage.DETAIL_FID, fidArrays[pos]);
//						//i.putExtra(HistoryDetailPage.DETAIL_FID, fidArrays[pos]);
//						//Log.d("XXXX",String.valueOf(pos) + "dsfdsf"+fidArrays[pos]);
//						mContext.startActivity(i);
					}
		        });
				
				viewHolder.mTextView_Small.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/BELLB.TTF"));

				//viewHolder.mImageButton.setTag(position);
			} else {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.friendlist_page, null);
				viewHolder = new ViewHolder();
				viewHolder.mTextView = (TextView) convertView
						.findViewById(R.id.FriendName);
				viewHolder.mImageView = (ImageView) convertView
						.findViewById(R.id.FriendImg);
			}
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}		
		viewHolder.position = position;

		String url = "";
		url = urlArrays[position % urlArrays.length];
		//viewHolder.mImageView.setImageResource(R.drawable.person);

		if (!mBusy) {
			mImageLoader.DisplayImage(url, viewHolder.mImageView, false);			
			if(mLocation == Const.eLocation.Location_Record){
				//viewHolder.mTextView_Small.setText(timeArrays[position]);
				viewHolder.mTextView.setText(nameArrays[position]+ " ("+numArrays[position]+ ")");
				parsetypebutton(viewHolder, position);
			} else {
				viewHolder.mTextView.setText(nameArrays[position]);
			}
		} else {
			mImageLoader.DisplayImage(url, viewHolder.mImageView, false);		
			if(mLocation == Const.eLocation.Location_Record){
				//viewHolder.mTextView_Small.setText(timeArrays[position]);
				viewHolder.mTextView.setText(nameArrays[position]+" (" + numArrays[position]+ ")");
				parsetypebutton(viewHolder, position);
			} else {
				viewHolder.mTextView.setText(nameArrays[position]);
			}
		}

		viewHolder.mTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/BELLB.TTF"));

		return convertView;
	}
	
	/*public void onUpdate(int count, Context context, DBHelper dbhelper, String tablename){		
		//Activity activity = (Activity) context;
		this.mCount = count;
		urlArrays = null;
		nameArrays = null;
		urlArrays = new String[count];
		nameArrays = new String[count];
		String[] idlist;
		//DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		idlist = dbhelper.getidfromSql(dbrw, tablename);		
		for(int i=0; i<count; i++){			
			urlArrays[i] = Const.projinfo.sServerAdr+"upload/user_"+
					dbhelper.getfidfromSql(dbrw, tablename,idlist[i])+".jpg";
			nameArrays[i] = dbhelper.getNamefromSql(dbrw, tablename, 
					dbhelper.getfidfromSql(dbrw, tablename,idlist[i]));
		}
		//dbrw.close();	
		setDataChange();				
	}*/
	
	public void onUpdate(Context context, DBHelper dbhelper, String tablename){		
		//Activity activity = (Activity) context;
		//DBHelper dbhelper = new DBHelper(context);		
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		this.mCount = dbhelper.getfidnumerfromSql(dbrw, tablename);
		if(this.mLocation == Const.eLocation.Location_Record)
			setLoadAdapter(context, dbhelper, tablename);
		else{
			urlArrays = null;
			nameArrays = null;
			fidArrays = null;
			urlArrays = new String[mCount];
			nameArrays = new String[mCount];
			fidArrays = new String[mCount];
			//Log.d("XXXX",String.valueOf(mCount));
			String[] idlist;
			//DBHelper dbhelper = new DBHelper(context);
			//SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
			idlist = dbhelper.getidfromSql(dbrw, tablename);		
			for(int i=0; i< mCount; i++){
				String fid =  dbhelper.getdatafromSql(dbrw, tablename, "fid", "_id", idlist[i]);
				fidArrays[i] = dbhelper.getdatafromSql(dbrw, tablename, "fid", "_id" , fid);
						//dbhelper.getfidfromSql(dbrw, tablename, fid);
						//dbhelper.getfidfromSql(dbrw, tablename,idlist[i]));
				//Log.d("XXXX",fidArrays[i]);
				urlArrays[i] = Const.projinfo.sServerAdr+"upload/user_"+
						fid+".jpg";
				nameArrays[i] = dbhelper.getdatafromSql(dbrw, tablename, "name"," fid", fid);
						//dbhelper.getNamefromSql(dbrw, tablename, 
						//dbhelper.getfidfromSql(dbrw, tablename,idlist[i]));
						//fid);
			}
		}
		//Log.d("History", "fsdfdsfsf");
		setDataChange();				
	}
	
	private void setDataChange(){
	       if(Const.userinfo.get_front_running()){//if(MainPage.isON == true){
	        	MainPage.Mainact.runOnUiThread(new Runnable() {
	                 public void run() {
	                	 //Log.d("History", "fgdfgdf");
	                	 notifyDataSetChanged();
	                 }

	            });	
	        } else {
	        	//Log.d("History", "dgdfgdfg");
	        	notifyDataSetChanged();   	
	        }
	}
	
	private void parsetypebutton(ViewHolder viewHolder, int position){	
		if(typeArrays[position].equals(String.valueOf(Const.History_Type.HType_Dial))){		
			viewHolder.mTextView_Small.setText(timeArrays[position] + "" + this.mContext.getResources().getString(R.string.hist_call_out_arrow));
			viewHolder.mTextView_Small.setTextColor(Color.GRAY);
		} else if (typeArrays[position].equals(String.valueOf(Const.History_Type.HType_Incoming))){
			viewHolder.mTextView_Small.setText(timeArrays[position] + " " + this.mContext.getResources().getString(R.string.hist_incoming_arrow));
			viewHolder.mTextView_Small.setTextColor(Color.BLUE);
		} else if (typeArrays[position].equals(String.valueOf(Const.History_Type.HType_Missed))){
			viewHolder.mTextView_Small.setText(timeArrays[position] + " " + this.mContext.getResources().getString(R.string.hist_miss));
			viewHolder.mTextView_Small.setTextColor(Color.RED);
		} else if (typeArrays[position].equals(String.valueOf(Const.History_Type.Htype_SentThanks))){
			viewHolder.mTextView_Small.setText(timeArrays[position] + " " + this.mContext.getResources().getString(R.string.hist_sent_card));
			viewHolder.mTextView_Small.setTextColor(Color.GREEN);
		} else if (typeArrays[position].equals(String.valueOf(Const.History_Type.HType_RecieveThanks))){
			viewHolder.mTextView_Small.setText(timeArrays[position] + " " + this.mContext.getResources().getString(R.string.hist_recv_card));
			viewHolder.mTextView_Small.setTextColor(Color.CYAN);
		} else if (typeArrays[position].equals(String.valueOf(Const.History_Type.Htype_GetMessage))){
			viewHolder.mTextView_Small.setText(timeArrays[position] + " " + this.mContext.getResources().getString(R.string.hist_recv_msg));
			viewHolder.mTextView_Small.setTextColor(Color.DKGRAY);
		} else if (typeArrays[position].equals(String.valueOf(Const.History_Type.Htype_SendMessage))){
			viewHolder.mTextView_Small.setText(timeArrays[position] + " " + this.mContext.getResources().getString(R.string.hist_sent_msg));
			viewHolder.mTextView_Small.setTextColor(Color.DKGRAY);
		}
	}
	
	public static String parseTime(String time){
		Calendar calendar = Calendar.getInstance(/*TimeZone.getTimeZone("UTC")*/);
		//Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTimeInMillis(Long.parseLong(time));

		int mYear = calendar.get(Calendar.YEAR);
		int mMonth = calendar.get(Calendar.MONTH) + 1;
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);
		String hour =  Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		String min =  Integer.toString(calendar.get(Calendar.MINUTE));
		if(hour.length()==1)
			hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
		if(min.length()==1)
			min = String.format("%02d", calendar.get(Calendar.MINUTE));
		
		return mYear+"/"+mMonth+"/"+mDay+" "+hour+":"+min;
	}
	
	static class ViewHolder {
		TextView mTextView;
		TextView mTextView_Small;
		ImageButton mImageButton;
		ImageView mImageView;
		int position;
	}
	
	private void setLoadAdapter(Context context, DBHelper dbhelper ,String tablename){
		SQLiteDatabase dbrw = dbhelper.getWritableDatabase();
		int count = dbhelper.getfidnumerfromSql(dbrw, tablename);
		
		urlArrays = new String[count];
		nameArrays = new String[count];
		timeArrays = new String[count];
		numArrays  = new int[count];
		typeArrays = new String[count];
		fidArrays = new String[count];
				
		int num =  dbhelper.getnumfromSql(dbrw, tablename);//info.num;//c.getCount(); 
		int check = dbhelper.getnumfromSql(dbrw, tablename);//c.getCount(); 
		int i = 0;		
		String itemlist = "";
		String[] fid_list = dbhelper.getdatalstfromSql(dbrw, tablename, "fid"); 
				//dbhelper.getfidfromSql(dbrw, tablename);
		while(num > 0){
			//Log.d("History",String.valueOf(num)+"XXX"+info.fid_list[check-1]);
			if(FileAccess.check_friend_exist(fid_list[check-1], itemlist)){
				//Log.d("History",info.fid_list[check-1]+":"+itemlist);
			} else {
				fidArrays[i] = fid_list[(check-1)];
				urlArrays[i] = Const.projinfo.sServerAdr+"upload/user_"+fid_list[(check-1)]+".jpg";
				if(dbhelper.checkexistfromSql(dbrw, Const.contactinfo.getTableName(), fid_list[check-1]))
					nameArrays[i] = //dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(), 
							//fid_list[check-1]);
							dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid", fid_list[check-1]);
				else
					nameArrays[i] = //dbhelper.getNamefromSql(dbrw, tablename, 
							//fid_list[check-1]);
							dbhelper.getdatafromSql(dbrw, tablename, "name"," fid", fid_list[check-1]);
				String[] col = { "time", "type"};
				Cursor cur = dbrw.query(tablename, col, "fid=" + "'" +
						fid_list[check-1]+ "'", null, null, null, null);
				numArrays[i]  = cur.getCount();
				cur.moveToLast();
				timeArrays[i] = parseTime(cur.getString(0));
				typeArrays[i] = cur.getString(1);

				itemlist = itemlist + fid_list[check-1]  +"/";
				num = num - cur.getCount();
				//Log.d("History", itemlist);
				//Log.d("History", urlArrays[i]+nameArrays[i]+numArrays[i]+timeArrays[i]+typeArrays[i]);
				i++;
			}
			check --;
		}
	}
}
