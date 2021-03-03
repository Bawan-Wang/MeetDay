package com.project1.meetday;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project1.http.Const;
import com.project1.http.DBHelper;
import com.project1.http.PictUtil;

public class ReviewThanksPage extends Activity {
	public static final String LOCAL_ID =
		      "org.appspot.apprtc.LOCALID";
	public static final String SEND_ID =
				  "org.appspot.apprtc.SENDID";
	public static final String ROOM_ID =
			  "org.appspot.apprtc.ROOMID";
	public static final String ISROLEPLAY_FALG =
	         "org.appspot.apprtc.ISROLEPLAY";
	  
	private int[] image = {
			R.drawable.thanksletter_hands, R.drawable.thanksletter_bro, R.drawable.thanksletter_hug, R.drawable.thanksletter_office
		};
	private String TAG = "ReviewThanksPage";
	private LinearLayout layoutBackground;
	private RelativeLayout layoutTitle, layoutBottom;
	private Bitmap bmpThankCard, bmpCloseBtn, bmpChangeImgBtn, bmpEditBtn, bmpSendLetterBtn;
	private ImageView background, close_btn, changeimg_btn, edit_btn, sendletter_btn;
	private Intent i;
	private int selectImgIdx = 0;
	private String defalutContent;
	private TextView thankText;
	InputMethodManager InputManager;
	
	private SharedPreferences settings;
	private static final String Sharedletterdata = "SHARE_LETTERDATA";
    private static final String Sharedletter = "SHARE_LETTER";
	private String msendid = null;
	private String mlocalid = null;
	private String mroomid = null;
	private boolean mIsRolePlay = false;
	private Context context;
	private DBHelper dbhelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		Log.d(TAG, "test1");
		setContentView(R.layout.reviewthanks_page);
		Log.d(TAG, "test2");		
		this.context = getBaseContext();
		
		//Const.userinfo.Location_Type = Const.eLocation.Location_Contact;

		i = new Intent();
		InputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		settings = getSharedPreferences(Sharedletterdata, 0);
		defalutContent = settings.getString(Sharedletter, getString(R.string.def_thanktext));
	    final Intent intent = getIntent();
	    msendid = intent.getStringExtra(SEND_ID);
	    mlocalid = intent.getStringExtra(LOCAL_ID);
	    mroomid = intent.getStringExtra(ROOM_ID);
	    mIsRolePlay = intent.getBooleanExtra(ISROLEPLAY_FALG, false);
		
	    allocateBmp();

		layoutBackground = (LinearLayout) findViewById(R.id.lirlayoutReviewThxCard);
		layoutTitle = (RelativeLayout) findViewById(R.id.relayoutReviewThxCard_title);
		layoutBottom = (RelativeLayout) findViewById(R.id.relayoutReviewThxCard_bottom);
        if(close_btn != null) {
        	close_btn.getDrawingCache().recycle();
        }
		close_btn = (ImageView)findViewById(R.id.closethank_btn);
		close_btn.setImageBitmap(bmpCloseBtn);
		close_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {		        
				//i.setClass(ReviewThanksPage.this, MainPage.class);
				//startActivity(i);
				ReviewThanksPage.this.finish();			
			}
        });
		
		if(changeimg_btn != null){
			changeimg_btn.getDrawingCache().recycle();			
		}
		changeimg_btn = (ImageView)findViewById(R.id.changethankimg_btn);
		changeimg_btn.setImageBitmap(bmpChangeImgBtn);
		changeimg_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {		        
				i.setClass(ReviewThanksPage.this, GridItemImgPage.class);
				startActivityForResult(i, 0);
			}
        });
		
		if(edit_btn != null){
			edit_btn.getDrawingCache().recycle();			
		}
		edit_btn = (ImageView)findViewById(R.id.changethanktext_btn);
		edit_btn.setImageBitmap(bmpEditBtn);
		edit_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				EditMessageDialog(getResources().getString(R.string.sett_thank_content), defalutContent);
			}
        });

        if(background != null) {
        	background.getDrawingCache().recycle();
        }
		background = (ImageView)findViewById(R.id.thankimg);
		background.setImageBitmap(bmpThankCard);
		
		thankText = (TextView)findViewById(R.id.thankText);
		thankText.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF")); 
		thankText.setText(defalutContent);
		
		if(sendletter_btn != null){
			sendletter_btn.getDrawingCache().recycle();
		}
		sendletter_btn = (ImageView)findViewById(R.id.thanklettersend_btn);
		sendletter_btn.setImageBitmap(bmpSendLetterBtn);
		sendletter_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {		        
				//i.setClass(ReviewThanksPage.this, MainPage.class);
				//startActivity(i);
				ConnectToOther con = new ConnectToOther(context);		
				con.SendThanks(mlocalid, msendid, defalutContent, mroomid, String.valueOf(selectImgIdx));
				dbhelper = new DBHelper(context);
				dbhelper.getWritableDatabase();
				if(Const.hisinfo.ladapter!=null)
					Const.hisinfo.ladapter.onUpdate(context, dbhelper, Const.hisinfo.getTableName());
				ReviewThanksPage.this.finish();	
			}
        });
	}
	
	@Override
	protected void onDestroy() {
		releaseBmp();
		releaseImgView();
		releaseLayout();
		System.gc();
		disconnect(mIsRolePlay);
		super.onDestroy();		
	}	

	private void allocateBmp() {
		bmpCloseBtn = PictUtil.getLocalBitmap(this, R.drawable.close, 1);
		bmpChangeImgBtn = PictUtil.getLocalBitmap(this, R.drawable.actionbar_background, 1);
		bmpEditBtn = PictUtil.getLocalBitmap(this, R.drawable.actionbar_words, 1);
		bmpThankCard = PictUtil.getLocalBitmap(this, R.drawable.thanksletter_hands, 1);
		bmpSendLetterBtn = PictUtil.getLocalBitmap(this, R.drawable.actionbar_send, 1);
	}	
	
	private void releaseBmp() {
		if(bmpCloseBtn != null){
			bmpCloseBtn.recycle();
		}
		if(bmpChangeImgBtn != null){
			bmpChangeImgBtn.recycle();
		}
		if(bmpEditBtn != null){
			bmpEditBtn.recycle();
		}
		if(bmpSendLetterBtn != null){
			bmpSendLetterBtn.recycle();
		}
		if(bmpThankCard != null){
			bmpThankCard.recycle();
		}		
	}	
	
	private void releaseImgView() {
		if(close_btn != null) {
			close_btn.setImageBitmap(null);
			close_btn.setBackground(null);
		}
		if(changeimg_btn != null) {
			changeimg_btn.setImageBitmap(null);
			changeimg_btn.setBackground(null);
		}
		if(edit_btn != null) {
			edit_btn.setImageBitmap(null);
			edit_btn.setBackground(null);
		}
		if(background != null) {
			background.setImageBitmap(null);
		}
		if(sendletter_btn != null) {
			sendletter_btn.setImageBitmap(null);
			sendletter_btn.setBackground(null);
		}
	}

	private void releaseLayout() {
		if(layoutBackground != null) {
			layoutBackground.setBackground(null);
		}
		if(layoutTitle != null) {
			layoutTitle.setBackground(null);
		}
		if(layoutBottom != null) {
			layoutBottom.setBackground(null);
		}
	}

	public void EditMessageDialog(final String title, String message) {
	      final EditText input = new EditText(this);
	      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	      input.setText(message);
	      dialog.setView(input);
	      dialog.setTitle(title);
	      dialog.setPositiveButton(getResources().getString(R.string.sett_OK), new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
	            	String Content = input.getText().toString();
	            	if(Content.length()==0){
	            		MessageDialog("Error", "Lack of Thanks Content!");
	            		InputManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
	            	}else{
	            		defalutContent = Content;
	            		thankText.setText(defalutContent);
	            		InputManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
	            	}
	           }
	      });
	      dialog.setNegativeButton(getResources().getString(R.string.sett_Cancel), new DialogInterface.OnClickListener() {
	           public void onClick( DialogInterface dialoginterface, int i) {

	           }
	      });
	      dialog.show();
	} //EOF warningDialog
	
	public void MessageDialog(String title, String message) {
	      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	      dialog.setTitle(title);
	      dialog.setMessage(message);
	      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {

	           }
	      });
	      dialog.show();
	} //EOF warningDialog
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
		      Bundle bundle = data.getExtras();
		      selectImgIdx = bundle.getInt("ImgValue");
		      
		      background.setImageResource(image[selectImgIdx]);
		}
	};
	
	private void disconnect(boolean isCaller) {
	    if (isCaller) {
	        //Log.e(TAG, "hdhhdfhff");
//	        MainPage.Mainact.finish();
//	        Intent intent = new Intent(this, MainPage.class);
//	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	        intent.putExtra(MainPage.MAINPAGE_LOCATION,
//	        String.valueOf(Const.eLocation.Location_Record));
//	        startActivity(intent);
	    	//Do nothing
        } else {
	        if (MainPage.Mainact != null) {
		        //Do nothing
	        }
	        if (Const.userinfo.get_front_running() == false) {
	            //Log.e(TAG, "vdsvds");
	            //finish();
                //Intent intent = new Intent(this, WelcomPage.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(intent);
	            android.os.Process.killProcess(android.os.Process.myPid());
            } else if (MainPage.isON != true) {
                //MainPage.Mainact.finish();
                Intent intent = new Intent(this, MainPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MainPage.MAINPAGE_LOCATION,
                String.valueOf(Const.eLocation.Location_Record));
                //startActivity(intent);
            } else {
                Intent intent = new Intent(this, MainPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MainPage.MAINPAGE_LOCATION,
                String.valueOf(Const.eLocation.Location_Record));
                //startActivity(intent);
            }
	    }
	    DBHelper dbhelper = new DBHelper(this);
	    if (Const.hisinfo.ladapter != null) {
	        Const.hisinfo.ladapter.onUpdate(this, dbhelper, Const.hisinfo.getTableName());
	    }
    }		

}
