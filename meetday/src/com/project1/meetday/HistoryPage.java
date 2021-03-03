package com.project1.meetday;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.FileAccess;
import com.project1.http.ServerFuncRun;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("HandlerLeak")
public class HistoryPage extends Fragment {

	public static final String HISTORY_PAGE_RID =
			"com.project1.meetday.HistoryPage.RID";
	public static final String HISTORY_PAGE_TITLE =
			"com.project1.meetday.HistoryPage.TITLE";
	public static final String HISTORY_PAGE_MESSAGE =
			"com.project1.meetday.HistoryPage.MESSAGE";

	private LinearLayout lilayoutHistoryPage;
	private TextView HistoryTextView;
	private ListView listView;
	private View v;
	//private Const.History_Content info = new Const.History_Content();
	private DBHelper dbhelper;// = new DBHelper(getActivity());
	private SQLiteDatabase dbrw;// = dbhelper.getWritableDatabase();
	private Context context;
	//private SimpleAdapter adapter;
	//private AskerServerEvent askerServerEvent;
	ImageButton mImageButton;
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	
	public interface AskerServerEvent {
		//public void MessageDialog(String title, String message);
		public void Connect(String strName, String strQues);
		//public void ConnectMessageDialog(final String title, String message);
		public void CallInBackground(final ServerFuncRun sfr);
	}
	
	@Override
	public void onAttach(Activity activity) {
	  super.onAttach(activity);
	  //askerServerEvent = (AskerServerEvent) activity;
	}	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(!isAdded()){
			Log.d(this.getClass().getName(),"On isAdded");
		    //return;
		}
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//Const.userinfo.Location_Type = Const.eLocation.Location_Record;
		Log.d(this.getClass().getName(),"On Create");
		this.context = getActivity();
		dbhelper = new DBHelper(context);
		dbrw = dbhelper.getWritableDatabase();		
		FileAccess.putStringInPreferences(context, String.valueOf(Const.eLocation.Location_Record),
				Const.eUsrType.UsrType_FragmentLocation, Const.projinfo.sSharePreferenceName);
		if(Const.isNetworkConnected(context) && !Const.userinfo.get_login_status()){
			//ConnectToOther.MessageDialog(context,"Error", "You are offline!");
			Const.Log_Info info = Const.get_login_data(context);
			if(info!=null){
				ConnectToServer con = new ConnectToServer(context);
				con.DoLogin(info.datatype, info.data, info.pass);
			}
		} 
	}

	@Override
	public void onDestroyView() {
		releaseViews();
		super.onDestroyView();
	}

	private void releaseViews() {
		if(HistoryTextView != null) {
			HistoryTextView.setBackground(null);
		}
		if(lilayoutHistoryPage != null) {
			lilayoutHistoryPage.setBackground(null);
		}
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.history_page, container, false);
	
		/*String[] colum = { "_id", "name", "fid", "time", "type"};
		Cursor c = dbrw.query(Const.hisinfo.getTableName(), colum, null, null, null, null, null);
		if (c.getCount() > 0) {
	    	if(Const.hisinfo.ladapter == null){//(Const.hisinfo.num != c.getCount()){	    			    			    		
	    		String[] fid_list =  new String[c.getCount()];
	    		//info.time_list = new String[c.getCount()];  
	    		//info.type_list = new String[c.getCount()];  
				c.moveToFirst();
				String friend_list = "";
				for (int i = 0; i < c.getCount(); i++) {
					//Const.hisinfo.name_list[i] = c.getString(1);
					if(!FileAccess.check_friend_exist(c.getString(2), friend_list)){
						friend_list = friend_list + c.getString(2)+"/";
					}
					fid_list[i] = c.getString(2);
					//info.time_list[i] = c.getString(3);
					//info.type_list[i] = c.getString(4);
					c.moveToNext();
				}
				//Const.hisinfo.ladapter = null;				
				
				Log.v("History", friend_list);	
				
				//info.num = c.getCount();	
				//Const.hisinfo.num = c.getCount();
	    	}			
		} else {

		}
		*/		
		if(Const.hisinfo.ladapter == null){
			//if(Const.userinfo.get_login_status())
				//PictUtil.deleteFile(FileManager.getSaveFilePath());
			Const.hisinfo.ladapter = new LoaderAdapter(context, dbhelper, Const.hisinfo.getTableName() ,
					Const.eLocation.Location_Record); 
		} else {
			//Const.hisinfo.ladapter.onUpdate(context, dbhelper, Const.hisinfo.getTableName());
		}
		
		//adapter = new SimpleAdapter(getActivity(), list, R.layout.historylist, new String[] { /*"photo","name","time",*/"icon" },
				//new int[] { /*R.id.history_photo, R.id.history_message, R.id.history_time,*/ R.id.history_icon} );
		
		//Const.hisinfo.ladapter = new LoaderAdapter(c.getCount(), getActivity(), Const.userinfo.flst, Const.contactinfo.name_list); 

		lilayoutHistoryPage = (LinearLayout) v.findViewById(R.id.lilayoutHistoryPage);
		listView = (ListView) v.findViewById(R.id.historyListView);
		//listView.setAdapter(adapter);
		listView.setAdapter(Const.hisinfo.ladapter);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				// TODO Auto-generated method stub
				Log.v("History", "listview button :" + position);
				
				//String str = Const.hisinfo.name_list[(Const.hisinfo.num-1) - position];
				final String fid = Const.hisinfo.ladapter.getlistfid(position);//Const.hisitem[position].fid;//Const.hisinfo.fid_list[(Const.hisinfo.num-1) - position];	
				//DBHelper dbhelper = new DBHelper(context);
				//SQLiteDatabase dbrw = dbhelper.getWritableDatabase();

				/*
				CallConnection(fid, dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid", fid), //dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(), fid), 
						FileAccess.getStringFromPreferences(context, getString(R.string.def_questiontext),
								Const.eUsrType.UsrType_HelpMsg, Const.projinfo.sSharePreferenceName));
				*/
				Intent chatIntent = new Intent();
				chatIntent.setClass(context, ChatActivity.class);
				chatIntent.putExtra(HISTORY_PAGE_RID, fid);
				chatIntent.putExtra(HISTORY_PAGE_TITLE, dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid", fid));
				chatIntent.putExtra(HISTORY_PAGE_MESSAGE, FileAccess.getStringFromPreferences(context, getString(R.string.def_questiontext),
						Const.eUsrType.UsrType_HelpMsg, Const.projinfo.sSharePreferenceName));
				context.startActivity(chatIntent);
				

				//dbrw.close();
				
				/*mImageButton = (ImageButton) view
						.findViewById(R.id.history_icon);
				
				mImageButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						Log.v("History", "button click ----> !!!");
						Intent i = new Intent();
						i.setClass(context, HistoryDetailPage.class);
						i.putExtra(HistoryDetailPage.DETAIL_FID, fid);
						Log.d("XXXX",String.valueOf(position) + "dsfdsf"+fid);
						context.startActivity(i);
					}
		        });*/
			}
		});
		
		HistoryTextView = (TextView )v.findViewById(R.id.HistoryTextView);
		HistoryTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 
		
		return v;
	}

	public void CallConnection(final String rid, final String title, final String message) {
		      {
	        	String Content = message;  
	          	if(Content.length()==0){
	          		ConnectToOther.MessageDialog(context,"Error", "Lack of Question Content!");
	          	} else if(!Const.isNetworkConnected(context)){
					ConnectToOther.MessageDialog(context,"Error", "You are offline!");
				} else if(!Const.userinfo.get_login_status()){
					ConnectToOther.MessageDialog(context,"Error", "You are not Logged in! Try again!");
				} else {
	          		ConnectToOther con = new ConnectToOther(context);
	          		String localuid = FileAccess.getStringFromPreferences(context, null, 
	              			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
	          		String roomid = con.genRoomId(localuid, rid);
	          		Const.Connect_Info.screen_size size = Const.getLocalRes(context);
	          		String localName = FileAccess.getStringFromPreferences(context, null, 
	              			Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
	          		con.SendConnect(title, Content,localuid, rid, 
	          				roomid, Const.hisinfo.getTableName(), size, localName);
	          		
	          		String helperName;
	          		if(dbhelper.checkexistfromSql(dbrw, Const.contactinfo.getTableName(), rid)){
	          			helperName = //dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(), rid);
	          					dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid", rid);
	          		} else {
	          			helperName = //dbhelper.getNamefromSql(dbrw, Const.hisinfo.getTableName(), rid);
	          					dbhelper.getdatafromSql(dbrw, Const.hisinfo.getTableName(), "name"," fid", rid);
	          		}
	          		
	          		Intent intent = new Intent();
	          		intent.setClass(context, AskerConnectPage.class);
	          		intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONX, String.valueOf(size.x));
	          		intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONY, String.valueOf(size.y));
	          		intent.putExtra(AskerConnectPage.ASKER_HELPERID, rid);
	          		intent.putExtra(AskerConnectPage.ASKER_LOCALID, localuid);
	          		intent.putExtra(AskerConnectPage.ASKER_ROOMID, roomid);
	          		intent.putExtra(AskerConnectPage.ASKER_MESSAGE, Content);
	          		intent.putExtra(AskerConnectPage.ASKER_HELPERNAME, helperName);
	          		intent.putExtra(AskerConnectPage.ASKER_HELPERPHOTO,Const.projinfo.sServerAdr+
	          				"upload/user_"+rid+".jpg");
	          		startActivity(intent);
	          	}
	         }
	}
	
//	public void ConnectMessageDialog(final String rid, final String title, String message) {
//	      final EditText input = new EditText(context);
//	      AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//	      input.setText(message);
//	      dialog.setView(input);
//	      dialog.setTitle(title);
//	      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//	            public void onClick( DialogInterface dialoginterface, int i) {
//	            	String Content = input.getText().toString();
//	            	if(Content.length()==0){
//	            		ConnectToOther.MessageDialog(context,"Error", "Lack of Question Content!");
//	            	} else if(!Const.userinfo.get_login_status()){
//	            		ConnectToOther.MessageDialog(context,"Error", "You are not Logged in!");
//	            	} else if (!Const.isNetworkConnected(context)){
//	            		ConnectToOther.MessageDialog(context,"Error", "You are offline!");
//	            	} else {
//	            		//String rid = "";	            		
//	            		//ConnectToOther con = new ConnectToOther(getActivity());	            		
//	            		//con.SendRequest(Const.userinfo.uid,rid);
//	            		//con.Connect(title, Content, rid);
//	            		ConnectToOther con = new ConnectToOther(context);
//	            		String localuid = FileAccess.getStringFromPreferences(context, null, 
//	                			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
//	            		String roomid = con.genRoomId(localuid, rid);
//	            		Const.Connect_Info.screen_size size = Const.getLocalRes(context);
//	            		String localName = FileAccess.getStringFromPreferences(context, null, 
//	                			Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
//	            		con.SendConnect(title, Content,localuid, rid, 
//	            				roomid, Const.hisinfo.getTableName(), size, localName);
//	            		
//	            		String helperName;
//	            		if(dbhelper.checkexistfromSql(dbrw, Const.contactinfo.getTableName(), rid)){
//	            			helperName = //dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(), rid);
//	            					dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid", rid);
//	            		} else {
//	            			helperName = //dbhelper.getNamefromSql(dbrw, Const.hisinfo.getTableName(), rid);
//	            					dbhelper.getdatafromSql(dbrw, Const.hisinfo.getTableName(), "name"," fid", rid);
//	            		}
//	            		
//	            		//Const.userinfo.State_Type = Const.eState.State_WaitAck;
//	            		
//	            		//Log.d(this.getClass().getName(), String.valueOf(size.x));
//	            		Intent intent = new Intent();
//	            		intent.setClass(context, AskerConnectPage.class);
//	            		intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONX, String.valueOf(size.x));
//	            		intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONY, String.valueOf(size.y));
//	            		intent.putExtra(AskerConnectPage.ASKER_HELPERID, rid);
//	            		intent.putExtra(AskerConnectPage.ASKER_LOCALID, localuid);
//	            		intent.putExtra(AskerConnectPage.ASKER_ROOMID, roomid);
//	            		intent.putExtra(AskerConnectPage.ASKER_MESSAGE, Content);
//	            		intent.putExtra(AskerConnectPage.ASKER_HELPERNAME, helperName);
//	            		intent.putExtra(AskerConnectPage.ASKER_HELPERPHOTO,Const.projinfo.sServerAdr+
//	            				"upload/user_"+rid+".jpg");
//	            		startActivity(intent);
//	            		//HistoryPage.this.getActivity().finish();
//	            	}
//	           }
//	      });
//	      dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//	           public void onClick( DialogInterface dialoginterface, int i) {
//	        	   //FriendEditText.setText("");
//	        	   //search_listforconnect = "";
//	        	   //FriendSearchMode(0);
//	           }
//	      });
//	      dialog.show();
//	} //EOF warningDialog
	
}

