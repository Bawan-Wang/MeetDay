package com.project1.meetday;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.DBManager;
import com.project1.http.FileAccess;
import com.project1.http.ServerFuncRun;

import java.util.ArrayList;
import java.util.HashMap;


public class HelperPage extends ListFragment {

	private Context context;
	private View helperView;
	//private TextView helperTxtView;
	private Spinner helperSpinner;
	private EditText helperEditTxt;
	private ImageButton helperSearchBtn;
	private Boolean searchmode = false;
	
//	private SimpleAdapter listAdapter;

	
	private ArrayList<HashMap<String,Object>> helperArraylist = new ArrayList<HashMap<String,Object>>();
	private MySimpleAdapterHelper listAdapter;
	private DBManager dbmanager; 
	private DBHelper dbhelper;
	private SQLiteDatabase db; 
	private boolean dataReady = false;
	private String defmsg;
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
		dbhelper = new DBHelper(context);
		//Const.userinfo.Location_Type = Const.eLocation.Location_Helper;
		FileAccess.putStringInPreferences(context, String.valueOf(Const.eLocation.Location_Helper), 
    			Const.eUsrType.UsrType_FragmentLocation, Const.projinfo.sSharePreferenceName);
		
		defmsg = FileAccess.getStringFromPreferences(context, 
				getString(R.string.def_questiontext), Const.eUsrType.UsrType_HelpMsg, 
				Const.projinfo.sSharePreferenceName);
		Log.v("History", "start history on onCreate!!!");
/*		
		final String[] helperList_Str = new String[]{"台灣水電工", "澄品水電企業社", "大民水電冷氣行", "千友水電冷氣行", 
										"六合水電工程行", "冠s水電行", "瑞芳水電行", "生有水電行", "永全水電行"};
		final String[] helperListNum_Str = new String[]{"0963788250", "02-27582866", "02-27587111", "02-25813909", 
				"02-27591854", "02-25217341", "02-27075349", "02-25538461", "02-28207266"};		
		final Boolean[] helperCoWork_bool = new Boolean[]{ true, false, false, false, 
				                        false, false, false, false, false};
		 final int[] mPics = new int[]{
					R.drawable.helper_cowork_camera, R.drawable.helper_noncowork_camera
				};
				*/
		/* 
		for(int iIdx=0; iIdx<helperList_Str.length; iIdx++){
			 HashMap<String,Object> item = new HashMap<String,Object>();
			 item.put("helperList", helperList_Str[iIdx]);
			 item.put("helperListNum", helperListNum_Str[iIdx]);
			 if(true == helperCoWork_bool[iIdx]){
				 item.put("helper_cowork_img", mPics[0]); 
			 }else{
				 item.put("helper_cowork_img", mPics[1]); 				 
			 }
			 helperArraylist.add( item );
		}
		
		listAdapter = new MySimpleAdapterHelper(this.context, helperArraylist, R.layout.helperlist, new String[]{"helper_cowork_img", "helperList", "helperListNum"}, new int[]{ R.id.helpercowork_img, R.id.helperlist_txt,  R.id.helperlistnum_txt } );
		this.setListAdapter(listAdapter);
		*/
		 
		 getdataInBackground(0);

	}
	

	@Override
	public void onDestroy() {
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
		Log.v("History", "start history on onCreateView!!!");
		helperView = inflater.inflate(R.layout.helper_page, container, false);
//		helperTxtView = (TextView) helperView.findViewById(R.id.helperTextView);
		helperSpinner = (Spinner) helperView.findViewById(R.id.helperspinner);
		helperEditTxt = (EditText) helperView.findViewById(R.id.helperSearchText);
		helperEditTxt.setVisibility(View.INVISIBLE);
		helperSearchBtn = (ImageButton)helperView.findViewById(R.id.helperSearch_btn);
		searchmode = false;
		
		ArrayAdapter<String> helperRegionlist = new ArrayAdapter<String>(this.context, R.layout.helperregion_spinner);
		final String[] helperRegion_Str = Const.HelperCato;/*new String[]{"台北市", "新北市", "桃園市", "新竹縣", 
				"苗栗縣", "台中市", "南投縣", "彰化縣", "雲林縣", "嘉義縣", "台南市", "高雄市", "屏東縣", 
				"宜蘭縣", "花蓮縣", "台東縣", "澎湖縣", "連江縣", "金門縣"};*/

		for(int iIdx=0; iIdx<helperRegion_Str.length; iIdx++){
			helperRegionlist.add(helperRegion_Str[iIdx]);
		}		
		helperSpinner.setAdapter(helperRegionlist);
	    
		helperSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
//            	helperArraylist = new ArrayList<HashMap<String,Object>>();
//            	listAdapter = new SimpleAdapter(context, helperArraylist, R.layout.helperlist, new String[]{"helper_cowork_img", "helperList", "helperListNum"}, new int[]{ R.id.helpercowork_img, R.id.helperlist_txt,  R.id.helperlistnum_txt } );
            	//getdataInBackground(position);
            	while(!dataReady)
            		;
            	showexpertdata(position);
            	//Toast.makeText(MainActivity.this, "您選擇"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            	//Log.d("XXXXXX");
                //Toast.makeText(MainActivity.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });
		
		
		helperEditTxt.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if(true == searchmode){
//					helperEditTxt.setBackgroundResource(R.drawable.search_text);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		helperSearchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(searchmode==false){
					HelperSearchMode(1);
				}else{
					HelperSearchMode(0);
				}		
			}
		});
		
		//return inflater.inflate(R.layout.helper_page, container, false);
		return helperView;
	}

	private void HelperSearchMode(int trigger) {
		// TODO Auto-generated method stub
		if(trigger==0){
			helperEditTxt.setText("");
			helperEditTxt.setVisibility(View.INVISIBLE);
//			helperEditTxt.setBackgroundResource(R.drawable.search_bar);
//			helperTxtView.setVisibility(View.VISIBLE);
			helperSpinner.setVisibility(View.VISIBLE);
			helperSearchBtn.setBackgroundResource(R.drawable.ic_searchfriend_in_white);
			searchmode=false;
//			InputManager.hideSoftInputFromWindow(FriendEditText.getWindowToken(), 0);
		}else{
			helperEditTxt.setVisibility(View.VISIBLE);
//			helperTxtView.setVisibility(View.INVISIBLE);
			helperSpinner.setVisibility(View.INVISIBLE);
			helperSearchBtn.setBackgroundResource(R.drawable.close);
			searchmode=true;
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Log.e("History", "Item: " + position);
//		dialog(position);
	}
	
//	protected void dialog(final int position) {
//        AlertDialog.Builder builder = new Builder(this.context);
//		HashMap<String,Object> item = new HashMap<String,Object>();
//		item = (HashMap<String,Object>) helperArraylist.get(position);
//		String helper = (String)item.get("helperList"); 
//		final String helperNumber = (String)item.get("helperListNum");        
//        builder.setMessage(helperNumber);
//        builder.setTitle(getResources().getString(R.string.dialog_call) + " " + helper);
//        builder.setPositiveButton(R.string.dialog_call, new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				Intent call = new Intent(CALL, Uri.parse("tel:" + helperNumber));
//		        startActivity(call);					
//			}
//        });
//        builder.setNegativeButton(R.string.dialog_cancel, new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//			}
//
//        });
//        builder.create().show();
//	}	
	
	private void getdataInBackground(final int position) {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog pdialog = null;
			private ServerFuncRun sfr = new ServerFuncRun();
	        //private String fidlist = "";
	        //private String namelist = "";
			protected void onPreExecute() {
				//if(!Const.contactinfo.getDataStatus())
				pdialog = ProgressDialog.show(context, "Connecting", "Wait...",true);				
	            //pdialog.setMessage("Progress start");
	            //pdialog.show();
	        }
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {				
					if(Looper.myLooper() == null)
						Looper.prepare();
					//Thread.sleep(3000);
					//fidlist = friend_lst;
					
					//sfr.usr_id = mlocaluid;
					//sfr.datatype = Const.eUsrType.UsrType_NickName;
					//sfr.fidlist = friend_lst;//Const.userinfo.flst;
					sfr.loc_id = String.valueOf(position);
					//sfr.cmd = Const.Command.Cmd_GetExpertList;
					sfr.cmd = Const.Command.Cmd_GetExpertTable;
					sfr.DoServerFunc();
					while(sfr.GetServResult() == null)
						;
					dbmanager = new DBManager(context);  
					dbmanager.openDatabase();  
					  //dbHelper.closeDatabase();
					 db = dbmanager.getDatabase(); 
					 dataReady = true;
					 //queryList();
					/*if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
						String expert_list = sfr.GetUserData();
						String[] para = FileAccess.parseDataToPara(expert_list, "&");
						//Log.d("XXXXXXXXX", "expert_list: " + expert_list);
						String[] eid = FileAccess.parseDataToPara(para[0], "/");
						String[] name = FileAccess.parseDataToPara(para[1], "/");
						String[] tel = FileAccess.parseDataToPara(para[2], "/");
						helperArraylist = new ArrayList<HashMap<String,Object>>();
						for(int iIdx=0; iIdx<eid.length; iIdx++){
							 HashMap<String,Object> item = new HashMap<String,Object>();
							 item.put("helperList", name[iIdx]);
							 item.put("helperListNum", tel[iIdx]);
							 if(Integer.valueOf(eid[iIdx])>10000){
//								 item.put("helper_cowork_img", mPics[0]); 
								 item.put("helper_cowork_img", "camera");
							 }else{
//								 item.put("helper_cowork_img", mPics[1]);
								 item.put("helper_cowork_img", "nocamera"); 
							 }
							 helperArraylist.add( item );
						}
						listAdapter = new MySimpleAdapterHelper(context, helperArraylist, R.layout.helperlist, new String[]{"helper_cowork_img", "helperList", "helperListNum"}, new int[]{ R.id.helpercowork_img, R.id.helperlist_txt,  R.id.helperlistnum_txt } );
					}*/									
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Const.contactinfo.setDataStatus(true);
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				if(pdialog!=null)
					pdialog.dismiss();
				//Log.d("RegisterActivity", "sgsgs: " + dbhelper.getnumfromSql(dbrw, Const.contactinfo.getTableName()));
				
				//setListAdapter(listAdapter);	
			}
		}.execute(null, null, null);
	}

	private void showexpertdata(int pos){
		String[] eid = //dbhelper.geteidlstfromSql(db, Const.projinfo.sExpertTable, pos);//FileAccess.parseDataToPara(para[0], "/");
				dbhelper.getdatalstfromSql(db, Const.projinfo.sExpertTable, "eid","loc_id", String.valueOf(pos));	
		String[] name = //dbhelper.getnamelstfromSql(db, Const.projinfo.sExpertTable, pos);//FileAccess.parseDataToPara(para[1], "/");
				dbhelper.getdatalstfromSql(db, Const.projinfo.sExpertTable, "name","loc_id", String.valueOf(pos));
		String[] tel = //dbhelper.gettellstfromSql(db, Const.projinfo.sExpertTable, pos);//FileAccess.parseDataToPara(para[2], "/");
				dbhelper.getdatalstfromSql(db, Const.projinfo.sExpertTable, "tel","loc_id", String.valueOf(pos));
		helperArraylist = new ArrayList<HashMap<String,Object>>();
		if(eid!=null){
			for(int iIdx=0; iIdx<eid.length; iIdx++){
				 HashMap<String,Object> item = new HashMap<String,Object>();
				 item.put("helperList", name[iIdx]);
				 item.put("helperListNum", tel[iIdx]);
				 //Log.d("XXXXXXXXX", "expert_list: " + eid[iIdx]);
				 if(Integer.valueOf(eid[iIdx])>10000){
//					 item.put("helper_cowork_img", mPics[0]); 
					 item.put("helper_cowork_img", "camera");
				 }else{
//					 item.put("helper_cowork_img", mPics[1]);
					 item.put("helper_cowork_img", "nocamera"); 
				 }
				 helperArraylist.add( item );
			}
		}

		listAdapter = new MySimpleAdapterHelper(context, helperArraylist, 
				R.layout.helperlist, new String[]{"helper_cowork_img", "helperList", 
				"helperListNum"}, new int[]{ R.id.helpercowork_img, R.id.helperlist_txt, 
				R.id.helperlistnum_txt }, eid, defmsg);
		setListAdapter(listAdapter);
	}
	
	void queryList() {  
		  
		  /* 方法1 直接下指令 */  
		  String queryUser = "SELECT * FROM expert";  
		  Cursor cursor1 = db.rawQuery(queryUser, null); // 搜尋用rawQuery  

		  
		  cursor1.moveToFirst();  
		  
		  Log.d(this.getClass().getName(),cursor1.getString(0));

		  while (cursor1.moveToNext())  
			  Log.d(this.getClass().getName(),cursor1.getString(0)+"/"+cursor1.getString(1));
		  
		  //cursor2.moveToFirst();  
		  	//Log.d(this.getClass().getName(),cursor2.getString(0) + "    " + cursor2.getString(1));  
		  //while (cursor2.moveToNext())  
			  //Log.d(this.getClass().getName(),"\n" + cursor2.getString(0) + "    "  
		    // + cursor2.getString(1));  
		 }  
}