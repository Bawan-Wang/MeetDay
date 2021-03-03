package com.project1.meetday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.project1.http.DBHelper;
import com.project1.http.DBManager;

import java.util.ArrayList;
import java.util.HashMap;

public class SmsInvitePage extends Activity {
	private Context context;
    private static final String NAME = "name";
    private static final String NUMBER = "number";	
//	private List<Map<String, String>> contactsArrayList;
	private View helperView;
	//private TextView helperTxtView;
//	private Spinner helperSpinner;
	private EditText helperEditTxt;
	private ImageButton helperSearchBtn;
	private Boolean searchmode = false;
	
//	private SimpleAdapter listAdapter;

	
	private ArrayList<HashMap<String,String>> contactArraylist = new ArrayList<HashMap<String,String>>();
//	private MySimpleAdapterHelper listAdapter;
	private ListView listView;
	private SimpleAdapter listAdapter;
	private DBManager dbmanager; 
	private DBHelper dbhelper;
	private SQLiteDatabase db; 
	private boolean dataReady = false;
	private String defmsg;
	private String TAG = "SmsInvitePage";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sms_invite_page);
	
		initData();
		initView();
//		final String[] smsContectList_Str = new String[]{"台灣水電工", "澄品水電企業社", "大民水電冷氣行", "千友水電冷氣行", 
//										"六合水電工程行", "冠s水電行", "瑞芳水電行", "生有水電行", "永全水電行"};
//		final String[] smsContectListNum_Str = new String[]{"0963788250", "02-27582866", "02-27587111", "02-25813909", 
//				"02-27591854", "02-25217341", "02-27075349", "02-25538461", "02-28207266"};		
//		final Boolean[] helperCoWork_bool = new Boolean[]{ true, false, false, false, 
//				                        false, false, false, false, false};
////		 final int[] mPics = new int[]{
////					R.drawable.helper_cowork_camera, R.drawable.helper_noncowork_camera
////				};
//
//		for(int iIdx=0; iIdx<smsContectList_Str.length; iIdx++){
//			 HashMap<String,Object> item = new HashMap<String,Object>();
//			 item.put("smsContectList", smsContectList_Str[iIdx]);
//			 item.put("smsContectListNum", smsContectListNum_Str[iIdx]);
////			 if(true == helperCoWork_bool[iIdx]){
////				 item.put("helper_cowork_img", mPics[0]); 
////			 }else{
////				 item.put("helper_cowork_img", mPics[1]); 				 
////			 }
//			 contactArraylist.add( item );
//		}
//		
//		listView = (ListView)findViewById(R.id.contactlist);
//		listAdapter = new SimpleAdapter(SmsInvitePage.this, contactArraylist, R.layout.smscontactlist, new String[]{/*"helper_cowork_img",*/ "smsContectList", "smsContectListNum"}, new int[]{ /*R.id.helpercowork_img,*/ R.id.smslist_txt,  R.id.smslistnum_txt });
//		listView.setAdapter(listAdapter);
//		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mmsto:"+smsContectListNum_Str[position]+","+smsContectListNum_Str[position+1]+","+smsContectListNum_Str[position+2]));
//				intent.putExtra("subject", "hello");
//				startActivity(intent);
//			}
//		});
	}
	
	private void initData(){
        getPhoneBookData();
    }
    public void getPhoneBookData(){
//    	contactArraylist = new ArrayList<>();
        Log.v(TAG, "Sms 1-11");
        Cursor contacts_name = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null);
        Log.v(TAG, "Sms 1-22");
        Log.v(TAG, "contacts_name: " + contacts_name.getCount());
        if(contacts_name.getCount()>0){
//	        while (contacts_name.moveToNext()) {
//	        	Log.v(TAG, "Sms 1-2-1");
//	        	HashMap<String, String> map = new HashMap<String, String>();
//	        	Log.v(TAG, "Sms 1-2-2");
//	            String phoneNumber = "";
//	            Log.v(TAG, "Sms 1-3");
//	            long id = contacts_name.getLong(
//	                    contacts_name.getColumnIndex(ContactsContract.Contacts._ID));
//	            Log.v(TAG, "Sms 1-4");
//	            Cursor contacts_number = getContentResolver().query(
//	                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//	                    null,
//	                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//	                            + "=" + Long.toString(id),
//	                    null,
//	                    null);
//	            Log.v(TAG, "Sms 1-5");
//	            while (contacts_number.moveToNext()) {
//	                phoneNumber = contacts_number
//	                        .getString(contacts_number.getColumnIndex(
//	                                ContactsContract.CommonDataKinds.Phone.NUMBER));
//	            }
//	            contacts_number.close();
//	            Log.v(TAG, "Sms 1-6");
//	            String name = contacts_name.getString(contacts_name
//	                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//	            map.put(NAME, name);
//	            map.put(NUMBER, phoneNumber);
//	            contactArraylist.add(map);
//	            Log.v(TAG, "Sms 1-7");
//	        }
        }
        contacts_name.close();
    }

    private void initView(){
        listView = (ListView) findViewById(R.id.contactlist);
        listAdapter = new SimpleAdapter(SmsInvitePage.this, contactArraylist, R.layout.smscontactlist, new String[]{/*"helper_cowork_img",*/ "smsContectList", "smsContectListNum"}, new int[]{ /*R.id.helpercowork_img,*/ R.id.smslist_txt,  R.id.smslistnum_txt });
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String number = contactArraylist.get(position).get(NUMBER);
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mmsto:"+number));  
				intent.putExtra("subject", "hello");  
				startActivity(intent);

            }
        });
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
	
}
