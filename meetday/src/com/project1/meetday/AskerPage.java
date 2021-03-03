package com.project1.meetday;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.FBRelated;
import com.project1.http.FileAccess;
import com.project1.http.FileManager;
import com.project1.http.PictUtil;
import com.project1.http.ServerFuncRun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("HandlerLeak")
public class AskerPage extends Fragment {
	//implements HistoryPage.AskerServerEvent {
	
	//private String TAG = "AskerPage";

	public static final String ASK_PAGE_RID =
			"com.project1.meetday.AskerPage.RID";
	public static final String ASK_PAGE_TITLE =
			"com.project1.meetday.AskerPage.TITLE";

//	private ListView listOfficialAccount;
//	private SimpleAdapter adapterOfficialAccount;
	private ListView listView;
	private EditText FriendEditText;
	private View v;
	//private SharedPreferences settings;
	//String friend_lst = null;
	int textlength=0;
	int AskerType=0;
	private String search_list[];// = "";
	private String search_listforconnect[]=null;// = "";
	private LoaderAdapter ladapter;
	//private SimpleAdapter adapter;
	static private int pos = 0;
	public static final String Default = "";
	private RelativeLayout relayoutAskerPage, relayoutAskerTitle;
	private Bitmap bmpSearch, bmpClose, bmpInvite;
	private ImageView SearchIcon, InviteIcon;
//	private TextView AskerTextView, txtOfficalTitle, txtFriendTitle;
	private TextView AskerTextView;
	private Boolean searchmode = false;
	private InputMethodManager InputManager;
	private Context context;
	private String defalutContent;
	private DBHelper dbhelper;
	private SQLiteDatabase dbrw;
	private String friend_lst = "";
	private String mlocaluid = null; 
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	
	private static LinearLayout Login_Layout;
    private static ImageButton logbutton, signupbutton;
    private static String TAG = "AskerPage";

	public interface AskerServerEvent {
	    public void onAskerPeerConnection(String usrId, String helperId, String message);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(!isAdded()){
			Log.d(this.getClass().getName(),"On isAdded");
			//super.onCreate(savedInstanceState);
		    //return;
		}
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		//Const.userinfo.Location_Type = Const.eLocation.Location_Contact;
		Log.d(this.getClass().getName(),"On Create");
		dbhelper = new DBHelper(context);
		dbrw = dbhelper.getWritableDatabase();
		FileAccess.putStringInPreferences(context, String.valueOf(Const.eLocation.Location_Contact), 
    			Const.eUsrType.UsrType_FragmentLocation, Const.projinfo.sSharePreferenceName);
		mlocaluid = FileAccess.getStringFromPreferences(context, null, 
    			Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);

		if(Const.isNetworkConnected(context) && !Const.userinfo.get_login_status()){
			Const.Log_Info info = Const.get_login_data(context);
			if(info!=null){
				ConnectToServer con = new ConnectToServer(context);
				con.DoLogin(info.datatype, info.data, info.pass);
			}
		} 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//int count = 0;
		v = inflater.inflate(R.layout.asker_page, container, false);
		Log.d(this.getClass().getName(), "On onCreateView");
	/*
		Login_Layout = (LinearLayout) v.findViewById(R.id.loginbox_layout);
		//Login_Layout.setVisibility(View.INVISIBLE);

		logbutton = (ImageButton) v.findViewById(R.id.login_btn);
        logbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent();
				i.setClass(context, LoginPage.class);
				startActivity(i);
			}
        });

        signupbutton = (ImageButton) v.findViewById(R.id.login_signup);
        signupbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent();
				i.setClass(context, RegisterPage.class);
				startActivity(i);
			}
        });
	*/
		defalutContent = FileAccess.getStringFromPreferences(context,
				getString(R.string.def_questiontext),
				Const.eUsrType.UsrType_HelpMsg, Const.projinfo.sSharePreferenceName);

		relayoutAskerPage  = (RelativeLayout) v.findViewById(R.id.relayoutAskerPage);
		relayoutAskerTitle  = (RelativeLayout) v.findViewById(R.id.relayoutAskerTitle);
//		txtOfficalTitle  = (TextView) v.findViewById(R.id.txtOfficalTitle);
//		txtFriendTitle  = (TextView) v.findViewById(R.id.txtFriendTitle);
		FriendEditText = (EditText) v.findViewById(R.id.FriendSearchText);
		FriendEditText.setVisibility(View.INVISIBLE);
		FriendEditText.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ARCENA.TTF"));
		//FriendEditText.setEnabled(false);

//		listOfficialAccount = (ListView)v.findViewById(R.id.listOfficialAccount);
//		String[] strOfficialAccout = new String[]{"MeetDay"};
//		int[] mPics = new int[]{ R.drawable.icon3_3 };
//		ArrayList<HashMap<String, Object>> officalArraylist = new ArrayList<>();
//		for(int iIdx=0; iIdx<strOfficialAccout.length; iIdx++){
//			HashMap<String,Object> item = new HashMap<String,Object>();
//			item.put("officailList", strOfficialAccout[iIdx]);
//			item.put("officailList_img", mPics[iIdx]);
//			officalArraylist.add( item );
//		}
//		adapterOfficialAccount = new SimpleAdapter(this.context, officalArraylist, R.layout.asker_officiallist, new String[]{"officailList_img", "officailList"}, new int[]{R.id.asker_official_image,  R.id.asker_official_account });
//		listOfficialAccount.setAdapter(adapterOfficialAccount);
//		listOfficialAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//				// TODO Auto-generated method stub
//				//Toast.makeText(getActivity(),
//				//"Select MeetDay", Toast.LENGTH_LONG)
//				//.show();
//				CallConnection(Const.officialname, defalutContent, true);
//			}
//		});
		listView = (ListView)v.findViewById(R.id.ContactsListView);

		allocateBmp();

		SearchIcon = (ImageView)v.findViewById(R.id.Search_btn);
		SearchIcon.setImageBitmap(bmpSearch);
		searchmode = false;
		InviteIcon = (ImageView)v.findViewById(R.id.asker_Invite_btn);
		InviteIcon.setImageBitmap(bmpInvite);

		AskerTextView = (TextView )v.findViewById(R.id.AskerTextView);
		AskerTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF"));

		InputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		friend_lst = FileAccess.getStringFromPreferences(context, "",
				Const.eUsrType.UsrType_FreindList, Const.projinfo.sSharePreferenceName);//Const.userinfo.flst;//sfr.GetFList();

		/*if(friend_lst.equals(""))
			count = 0;
		else
			count = friend_lst.length() - friend_lst.replace("/", "").length();*/
		//int count_local = 0;
		//if(flst.equals(Default))
		//;
		//else
		//count_local = flst.length() - flst.replace("/", "").length();
		//Const.userinfo.fnum = count;

		//String local_uid = FileAccess.getStringFromPreferences(context,
		//"", Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);W

		//Change next page from MainPage to StartPage
//		if(!Const.userinfo.get_login_status() && mlocaluid == null){
//			//Show Login Page
//			SearchIcon.setVisibility(View.INVISIBLE);
//		} else {
//			//Log.d(this.getClass().getName(),String.valueOf(Const.userinfo.fnum));
//			Login_Layout.setVisibility(View.INVISIBLE);
//			getdataInBackground();
//		}
		if(Const.contactinfo.ladapter == null/* || Const.userinfo.refresh_flag == true*/){
			if(Const.userinfo.get_login_status())
				PictUtil.deleteFile(FileManager.getSaveFilePath());
			Const.contactinfo.ladapter = new LoaderAdapter(context, dbhelper,
					Const.contactinfo.getTableName());
			//new Thread().start();
		}

		getdataInBackground();

		//}
	/*else {
			//Const.userinfo.flst = flst;
			bDataReady = true;
		}*/



		if(Const.contactinfo.getDataStatus()){
			listView.setAdapter(Const.contactinfo.ladapter);
		}

		FriendEditText.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s){
				// Abstract Method of TextWatcher Interface.
			}

			public void beforeTextChanged(CharSequence s,int start, int count, int after){
				// Abstract Method of TextWatcher Interface.
			}

			public void onTextChanged(CharSequence s,int start, int before, int count){
//				FriendEditText.setBackgroundResource(R.drawable.search_text);
				textlength = FriendEditText.getText().length();
				//String[] search_namelist = new String[Const.userinfo.fnum];
				//String[] search_imglist = new String[Const.userinfo.fnum];
				int j =0;
				int num = dbhelper.getnumfromSql(dbrw, Const.contactinfo.getTableName());
				search_list = new String[num];
				String[] idlist;
				idlist = dbhelper.getidfromSql(dbrw, Const.contactinfo.getTableName());
				//HashMap<String, Object> item = new HashMap<String, Object>();
				for (int i=0; i < num; i++){
					//String idtmp = dbhelper.getfidfromSql(dbrw, Const.contactinfo.getTableName(),idlist[i]);
					String idtmp = dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "fid", "_id",
							idlist[i]);
					String nametmp = //dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(),
							dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid",
									idtmp);//dbhelper.getfidfromSql(dbrw, Const.contactinfo.getTableName(),idlist[i]));
					if (nametmp.toLowerCase().indexOf(FriendEditText.getText().toString().toLowerCase()) >= 0) {
						search_list[j++] = idtmp;
					}
				}

				ladapter = new LoaderAdapter(j, context, dbhelper, Const.contactinfo.getTableName() ,search_list);
				listView.setAdapter(ladapter);
			}
		});

		///listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				//FriendEditText.setText(listView.getItemAtPosition((int) id).toString());
				TextView txtNumber = (TextView) view.findViewById(R.id.FriendName);
				String str = (String) txtNumber.getText();
				pos = position;

				if(textlength!=0){
					search_listforconnect = search_list;
				}
//				FriendSearchMode(0);
//				FriendEditText.setText(str);
				Intent friendCardIntent = new Intent(context, FriendCardDialogActivity.class);
				if(search_listforconnect == null ){
					friendCardIntent.putExtra(ASK_PAGE_RID, Const.contactinfo.ladapter.getlistfid(pos));
				}else{
					friendCardIntent.putExtra(ASK_PAGE_RID, search_listforconnect[pos]);
					search_listforconnect = null;
				}

				friendCardIntent.putExtra(ASK_PAGE_TITLE, str);
				startActivity(friendCardIntent);
				//CallConnection(str, defalutContent, false);
			}
		});

		SearchIcon.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				searchmode = !searchmode;
				FriendSearchMode(searchmode);
			}
		});

		InviteIcon.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, AddFriendNewPage.class);
				startActivity(intent);
			}
		});

		return v;
	}

	@Override
	public void onStart() {
		Log.d(this.getClass().getName(), "On onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.d(this.getClass().getName(), "On onResume");
		super.onResume();
	}

	@Override
	public void onStop() {
		Log.d(this.getClass().getName(), "On onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		releaseBmp();
		releaseViews();
		System.gc();
		Log.d(this.getClass().getName(), "On onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		dbrw.close();
		dbhelper.close();
		Log.d(this.getClass().getName(), "On onDestroy");
		super.onDestroy();	
	}

	private void allocateBmp() {
		bmpSearch = PictUtil.getLocalBitmap(getActivity(), R.drawable.ic_searchfriend_in_white, 1);
		bmpClose = PictUtil.getLocalBitmap(getActivity(), R.drawable.close, 1);
		bmpInvite = PictUtil.getLocalBitmap(getActivity(), R.drawable.ic_askaddfriend_in_white, 1);
	}

	private void releaseBmp() {
		if(bmpSearch != null){
			bmpSearch.recycle();
		}
		if(bmpClose != null){
			bmpClose.recycle();
		}
		if(bmpInvite != null){
			bmpInvite.recycle();
		}
	}

	private void releaseViews() {
		if(SearchIcon != null) {
			SearchIcon.setImageBitmap(null);
			SearchIcon.setBackground(null);
		}
		if(InviteIcon != null) {
			InviteIcon.setImageBitmap(null);
			InviteIcon.setBackground(null);
		}
//		if(txtFriendTitle != null) {
//			txtFriendTitle.setBackground(null);
//		}
//		if(txtOfficalTitle != null) {
//			txtOfficalTitle.setBackground(null);
//		}
		if(FriendEditText != null) {
			FriendEditText.setBackground(null);
		}
		if(relayoutAskerTitle != null) {
			relayoutAskerTitle.setBackground(null);
		}
		if(relayoutAskerPage != null) {
			relayoutAskerPage.setBackground(null);
		}
	}

	@Override
	public void onAttach(Activity activity) {
	  super.onAttach(activity);
	  //askerServerEvent = (AskerServerEvent) activity;
	}	

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
		switch(msg.what){ 
		}
		super.handleMessage(msg);
		//refresh();
		//Const.contactinfo.ladapter.getImageLoader().clearCache();
		}
	};
		
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(1000);
				handler.sendMessage(handler.obtainMessage());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void CallConnection(final String title, final String message, boolean ismeetday) {
				String Content = message;

				if(Content.length()==0){
					ConnectToOther.MessageDialog(context,"Error", "Lack of Question Content!");
				} else if(!Const.isNetworkConnected(context)){
					ConnectToOther.MessageDialog(context,"Error", "You are offline!");
				} else if(!Const.userinfo.get_login_status()){
					ConnectToOther.MessageDialog(context,"Error", "You are not Logged in! Try again!");
				} else {
					String rid = "0";
					if(ismeetday){
						rid = Const.officialid;
					} else {
						if(search_listforconnect == null ){
							rid = Const.contactinfo.ladapter.getlistfid(pos);//dbhelper.getfidfromSql(dbrw, Const.contactinfo.tablename, idlist[pos]);
						} else {
							rid = search_listforconnect[pos];//get_fid(search_listforconnect, pos+1);
							search_listforconnect = null;
						}
					}
					if(rid.equals(mlocaluid)){
						ConnectToOther.MessageDialog(context,"Error", "You can't call yourself!");
						return;
					}
					ConnectToOther con = new ConnectToOther(context);

					String roomid = con.genRoomId(mlocaluid, rid);
					Const.Connect_Info.screen_size size = Const.getLocalRes(context);
					Log.d(this.getClass().getName(),rid);
					//String helperName = dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(), rid);
					String localName = FileAccess.getStringFromPreferences(context, null,
							Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
					con.SendConnect(title, Content, mlocaluid, rid, roomid,
							Const.hisinfo.getTableName(),size, localName);

					String helperName = "";//dbhelper.getNamefromSql(dbrw,
							//Const.contactinfo.getTableName(), rid);
							//dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid", rid);
					if(ismeetday){
						helperName = Const.officialname;
					} else {
						helperName = //dbhelper.getNamefromSql(dbrw,
								//Const.contactinfo.getTableName(), rid);
								dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid", rid);
					}
					if(helperName != null){
						Intent intent = new Intent();
						intent.setClass(context, AskerConnectPage.class);
						intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONX, String.valueOf(size.x));
						intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONY, String.valueOf(size.y));
						intent.putExtra(AskerConnectPage.ASKER_HELPERID, rid);
						intent.putExtra(AskerConnectPage.ASKER_LOCALID, mlocaluid);
						intent.putExtra(AskerConnectPage.ASKER_ROOMID, roomid);
						intent.putExtra(AskerConnectPage.ASKER_MESSAGE, Content);
						intent.putExtra(AskerConnectPage.ASKER_HELPERNAME, helperName);
						intent.putExtra(AskerConnectPage.ASKER_HELPERPHOTO,
								Const.projinfo.sServerAdr + "upload/user_" + rid + ".jpg");
						startActivity(intent);
					}

					FriendEditText.setText("");
					searchmode = false;
					FriendSearchMode(searchmode);

				}
	}

//	public void ConnectMessageDialog(final String title, String message) {
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
//	            	} else{
//	            		String rid;
//	            		if(search_listforconnect == null ){
//	            			rid = Const.contactinfo.ladapter.getlistfid(pos);//dbhelper.getfidfromSql(dbrw, Const.contactinfo.tablename, idlist[pos]);
//	            		} else {
//	            			rid = search_listforconnect[pos];//get_fid(search_listforconnect, pos+1);
//	            			search_listforconnect = null;
//	            		}
//	            		//ConnectToOther con = new ConnectToOther(getActivity());
//	            		//con.SendRequest(Const.userinfo.uid,rid);
//	            		//con.Connect(title, Content, rid);
//	            		ConnectToOther con = new ConnectToOther(context);
//
//	            		/*try {
//							//Bitmap bm = PictUtil.takepic(System.currentTimeMillis(), context);
//							//PictUtil.saveToCacheFile(bm);
//	            			screencap ss = new screencap();
//	            			//ss.init(getActivity());
//	            			ss.shoot(getActivity());
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}*/
//
//	            		String roomid = con.genRoomId(mlocaluid, rid);
//	            		Const.Connect_Info.screen_size size = Const.getLocalRes(context);
//	            		Log.d(this.getClass().getName(),rid);
//	            		//String helperName = dbhelper.getNamefromSql(dbrw, Const.contactinfo.getTableName(), rid);
//	            		String localName = FileAccess.getStringFromPreferences(context, null,
//	                			Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
//	            		con.SendConnect(title, Content, mlocaluid, rid, roomid,
//	            				Const.hisinfo.getTableName(),size, localName);
//
//	            		String helperName = //dbhelper.getNamefromSql(dbrw,
//	            				//Const.contactinfo.getTableName(), rid);
//	            				dbhelper.getdatafromSql(dbrw, Const.contactinfo.getTableName(), "name"," fid", rid);
//	            		if(helperName != null){
//		            		Intent intent = new Intent();
//		            		intent.setClass(context, AskerConnectPage.class);
//		            		intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONX, String.valueOf(size.x));
//		            		intent.putExtra(AskerConnectPage.ASKER_LOCALRESOLUTIONY, String.valueOf(size.y));
//		            		intent.putExtra(AskerConnectPage.ASKER_HELPERID, rid);
//		            		intent.putExtra(AskerConnectPage.ASKER_LOCALID, mlocaluid);
//		            		intent.putExtra(AskerConnectPage.ASKER_ROOMID, roomid);
//		            		intent.putExtra(AskerConnectPage.ASKER_MESSAGE, Content);
//		            		intent.putExtra(AskerConnectPage.ASKER_HELPERNAME, helperName);
//		            		intent.putExtra(AskerConnectPage.ASKER_HELPERPHOTO,
//		            				Const.projinfo.sServerAdr+"upload/user_"+rid+".jpg");
//		            		startActivity(intent);
//		            		//AskerPage.this.getActivity().finish();
//	            		}
//
//	            		//Connect(title, Content);
//	            		FriendEditText.setText("");
//	            		FriendSearchMode(0);
//
//
//	            	}
//	           }
//	      });
//	      dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//	           public void onClick( DialogInterface dialoginterface, int i) {
//	        	   FriendEditText.setText("");
//	        	   search_listforconnect = null;
//	        	   FriendSearchMode(0);
//	           }
//	      });
//	      dialog.show();
//	} //EOF warningDialog
	
	 
	
	@SuppressWarnings("unused")
	private List<ResolveInfo> getShareTargets(){
		Intent intent=new Intent(Intent.ACTION_SEND,null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		PackageManager pm=context.getPackageManager();
		return pm.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
	}
	
	OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				ladapter.setFlagBusy(true);
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				ladapter.setFlagBusy(false);
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				ladapter.setFlagBusy(false);
				break;
			default:
				break;
			}
			ladapter.notifyDataSetChanged();
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};	

	public void FriendSearchMode(boolean trigger) {
		// TODO Auto-generated method stub
		if (!trigger) {
			FriendEditText.setText("");
			FriendEditText.setVisibility(View.INVISIBLE);
//			FriendEditText.setBackgroundResource(R.drawable.search_bar);
			AskerTextView.setVisibility(View.VISIBLE);
//			SearchIcon.setBackgroundResource(R.drawable.ic_searchfriend_in_white);
			SearchIcon.setImageBitmap(bmpSearch);
			InviteIcon.setVisibility(View.VISIBLE);
			
			InputManager.hideSoftInputFromWindow(FriendEditText.getWindowToken(), 0);
		}else{
			FriendEditText.setVisibility(View.VISIBLE);
			AskerTextView.setVisibility(View.INVISIBLE);
			InviteIcon.setVisibility(View.INVISIBLE);
//			SearchIcon.setBackgroundResource(R.drawable.close);
			SearchIcon.setImageBitmap(bmpClose);
		}
	}
	
	private void getdataInBackground() {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog pdialog = null;
			private ServerFuncRun sfr = new ServerFuncRun();
	        private String fidlist = "";
	        private String namelist = "";
			protected void onPreExecute() {
				if(!Const.contactinfo.getDataStatus())
					pdialog = ProgressDialog.show(context, getResources().getString(R.string.login_connecting), getResources().getString(R.string.login_wait),true);
	            //pdialog.setMessage("Progress start");
	            //pdialog.show();
	        }
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {				
					if(Looper.myLooper() == null)
						Looper.prepare();
					
					if (!Const.isNetworkConnected(context)){
						return msg;
					}
					fidlist = friend_lst;
					
					sfr.usr_id = mlocaluid;
					sfr.datatype = Const.eUsrType.UsrType_NickName;
					sfr.fidlist = friend_lst;//Const.userinfo.flst;
					sfr.cmd = Const.Command.Cmd_GetUsrDataList;
					sfr.DoServerFunc();
					while(sfr.GetServResult() == null)
						;
					if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){	
						namelist = sfr.GetUserData();
						Log.d(this.getClass().getName(),fidlist);
						Log.d(this.getClass().getName(),namelist);
						if(!FileAccess.getStringFromPreferences(context, 
								"", Const.eUsrType.UsrType_FriendNameList, 
								Const.projinfo.sSharePreferenceName).equals(namelist)){
							FileAccess.putStringInPreferences(context, 
									namelist, Const.eUsrType.UsrType_FriendNameList, 
									Const.projinfo.sSharePreferenceName);
							Const.contactinfo.ladapter = null;
							Const.contactinfo.setDataStatus(false);
							dbhelper.onDropTable(dbrw, Const.contactinfo.getTableName());
							dbhelper.onCreateTable(dbrw, Const.contactinfo.getTableName());							
							while(fidlist.contains("/")){
								//ServerFuncRun sfr = new ServerFuncRun();
								int tmpf = fidlist.indexOf("/");
								int tmpn = namelist.indexOf("/");
								String fid = fidlist.substring(0, tmpf);
								String name = namelist.substring(0, tmpn);
								if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){		
									Const.SQL_data data = new Const.SQL_data(name, fid);
									dbhelper.insertData(dbrw, Const.contactinfo.getTableName(), data);
								} else {				
								}
								fidlist = fidlist.substring(tmpf+1);
								namelist = namelist.substring(tmpn+1);
							}
						} else {
							//Log.d(this.getClass().getName(),namelist);
						}		
						//System.out.println((Const.refresh_friends));
						if((Const.refresh_friends & 0x01)==0x01){
							FBRelated.AddFBFriend(context, FileAccess.getStringFromPreferences(context, null, 
					    			Const.eUsrType.UsrType_FBList, Const.projinfo.sSharePreferenceName), mlocaluid);
							Const.refresh_friends &= 0xFFFFFFFE;
						}
						//System.out.println((Const.refresh_friends));
						if((Const.refresh_friends&0x02)==0x02){
							Log.d(this.getClass().getName(),"DDDDDDDD");
							ConnectToServer con = new ConnectToServer(context);
							con.DoAddFriendList(mlocaluid, FileAccess.getStringFromPreferences(context, null, 
					    			Const.eUsrType.UsrType_PhoneList, Const.projinfo.sSharePreferenceName), Const.eUsrType.UsrType_Fone);
							Const.refresh_friends &= 0xFFFFFFFD;
						}
						//Const.contactinfo.setDataStatus(true);
					}
									
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					Log.d(this.getClass().getName(), "Error: " + msg);
				}
				Const.contactinfo.setDataStatus(true);
				Log.d(this.getClass().getName(), "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				if(pdialog!=null)
					pdialog.dismiss();
				//Log.d("RegisterActivity", "sgsgs: " + dbhelper.getnumfromSql(dbrw, Const.contactinfo.getTableName()));
				if(Const.contactinfo.ladapter != null/* && Const.contactinfo.getDataStatus()*/){
					//Log.d(this.getClass().getName(),"fdsfsf");
					//Const.contactinfo.ladapter.onUpdate(context, dbhelper, Const.contactinfo.getTableName());
				}else{
					//Log.d(this.getClass().getName(), "sgsdgsgs: " + msg);
					Const.contactinfo.ladapter = 
							new LoaderAdapter(context, dbhelper,Const.contactinfo.getTableName());					
				}
				listView.setAdapter(Const.contactinfo.ladapter);
				if (Const.isNetworkConnected(context)){
					if (sfr.GetServResult() != Const.RRet.RRET_SUCCESS) {
						Toast.makeText(getActivity(),
								getResources().getString(R.string.login_fail) + msg, Toast.LENGTH_LONG)
								.show();
					}
				}else{
					Toast.makeText(getActivity(),
							getResources().getString(R.string.login_offline) + msg, Toast.LENGTH_LONG)
							.show();
			    }
			}
		}.execute(null, null, null);
	}

}

