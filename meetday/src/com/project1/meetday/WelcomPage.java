package com.project1.meetday;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.project1.http.Const;
import com.project1.http.FBRelated;
import com.project1.http.FileAccess;
import com.project1.http.PictUtil;
import com.project1.http.ServerFuncRun;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
//import com.example.service_nonui_demo.MainActivity;
//import android.widget.EditText;
//import android.widget.Toast;
//import android.content.Context;
//import android.content.SharedPreferences;
//import com.javapapers.android.ShareExternalServer;
//import com.javapapers.android.ShareExternalServer;

public class WelcomPage extends Activity implements ConnectionCallbacks, OnConnectionFailedListener{
	private ViewPager adViewPager;
	private RelativeLayout relayoutWelcomPage;
	private LinearLayout pagerLayout;
	private List<View> pageViews;
	private ImageView[] imageViews;
//	private ImageView imageView;
	private ImageView imgIntroduction1, imgIntroduction2, imgIntroduction3, imgIntroduction4;
	private AdPageAdapter adapter;
	private Bitmap bmpSkipBtn, bmpIntro1, bmpIntro2, bmpIntro3, bmpIntro4;
    private ImageButton skipbutton;//logbutton, signupbutton;
    private ProgressDialog pdialog = null;
    //private DBHelper dbhelper = null;
	private int PageScrollState=0;
	private int currentPage=0;
	private Context context;
	private String location, name, pass;
	private Intent gcmServiceIntent;
	//private boolean googlechecked = false;
	
	//static TextView register;
	static SpannableString spannableString;
	public static final String Default = "null";
	
	GoogleCloudMessaging gcm;
	String regId;

	public static final String REG_ID = "regId";
	//private static final String APP_VERSION = "appVersion";
	//ShareExternalServer appUtil;
	AsyncTask<Void, Void, String> shareRegidTask;
	static final String TAG = "Welcome";

	//AlertDialog.Builder ReplyAdd_dialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title
		this.context = getApplicationContext();
		
		FacebookSdk.sdkInitialize(context);
		setContentView(R.layout.welcom_page);

//		final int appFlags = context.getApplicationInfo().flags;
//		Const.debug_mode = (appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

		relayoutWelcomPage = (RelativeLayout) findViewById(R.id.relayoutWelcomPage);

		location = FileAccess.getStringFromPreferences(context, 
				String.valueOf(Const.eLocation.Location_Helper), 
				Const.eUsrType.UsrType_FragmentLocation, Const.projinfo.sSharePreferenceName);
		name = FileAccess.getStringFromPreferences(context, Default, 
				Const.eUsrType.UsrType_Email, Const.projinfo.sSharePreferenceName);
		pass = FileAccess.getStringFromPreferences(context, Default, 
				Const.eUsrType.UsrType_Pass, Const.projinfo.sSharePreferenceName);
		
		ServerFuncRun sfr = new ServerFuncRun();
		Const.Log_Info info = Const.get_login_data(context);
		if(info!=null){
			sfr.datatype = info.datatype;
			sfr.data = info.data;
			sfr.pass = info.pass;
			if(!FileAccess.getStringFromPreferences(context, Default,
					Const.eUsrType.UsrType_FBId, Const.projinfo.sSharePreferenceName).toLowerCase().equals(Default)){
				if(FileAccess.getStringFromPreferences(context, Default, 
						Const.eUsrType.UsrType_FB_Status, Const.projinfo.sSharePreferenceName).equals(Const.eSocialCmd.Social_Connect)){
					if(Const.isNetworkConnected(context))
						FBRelated.GetFBFIDList(context);
				}
			}
		}
		
//		String local_regid = FileAccess.getStringFromPreferences(context, 
//				Default, Const.eUsrType.UsrType_RegId, Const.projinfo.sSharePreferenceName);
		
//		if(local_regid.equals(Default)){
//			regId = registerGCM();
//			Log.d("RegisterActivity", "GCM RegId: " + regId);
//		} else {
//			//Const.userinfo.regid = local_regid;
//			regId = local_regid;
//			Toast.makeText(getApplicationContext(),
//					"Already Registered with GCM Server!",
//					Toast.LENGTH_LONG).show();
//		}
		
		gcmServiceIntent = new Intent(context, GCMBootService.class);
		context.startService(gcmServiceIntent);		
		//Intent intent = new Intent(WelcomPage.this, GCMNotificationIntentService.class);
		//startService(intent);
		
		/*
		appUtil = new ShareExternalServer();


		//final Context context = this;
		shareRegidTask = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String result = appUtil.shareRegIdWithAppServer(context, regId);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				shareRegidTask = null;
				//Toast.makeText(getApplicationContext(), result,
						//Toast.LENGTH_LONG).show();
			}

		};
		shareRegidTask.execute(null, null, null);
		*/
		if(name.equals(Default)&& pass.equals(Default)){
			Toast.makeText(WelcomPage.this, getResources().getString(R.string.login_no_data_found), Toast.LENGTH_SHORT).show();
		} else if (pass.equals(Default)){
			Intent intent = new Intent();
			intent.setClass(WelcomPage.this, StartPage.class);
			intent.putExtra(StartPage.Start_type, Const.eSocialCmd.Social_Unbind);
			startActivity(intent);
			WelcomPage.this.finish();
		} else {			
			//if(logintype.equals("google")){
				//while(googlechecked==false)
					//;
				//Log.d("LoginActivity", "Error: " + "LINccE");
				//Const.mGoogleApiClient.connect();
			//} else {
				loginInBackground(sfr);
			//}
			
		}

		allocateBmp();

		initViewPager();
		
		skipbutton = (ImageButton) findViewById(R.id.skip_btn);
		skipbutton.setImageBitmap(bmpSkipBtn);
		skipbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {		        
				Intent i = new Intent();			
				//i.setClass(WelcomPage.this, MainPage.class);
				//i.putExtra(MainPage.MAINPAGE_LOCATION, location);
				//Change next page from MainPage to StartPage
				i.setClass(WelcomPage.this, StartPage.class);
				startActivity(i);
				WelcomPage.this.finish();
			}
        });
		
		Const.mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this).addOnConnectionFailedListener(this)
		.addApi(Plus.API, new Plus.PlusOptions.Builder().build()) 
		.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		
 /*       
        //make "color" (characters 17 to stringLength) display a toast message when touched
        ClickableSpan clickableSpan = new ClickableSpan() {
 
        	 @Override
         	public void updateDrawState(TextPaint ds) {
         	    //ds.setColor(ds.linkColor);
         	    ds.setColor(Color.WHITE);
         	    ds.setUnderlineText(false);
         	}
            
            @Override
            public void onClick(View view) {
            	Intent i = new Intent();
				i.setClass(WelcomPage.this, RegisterPage.class);
				startActivity(i);
            }
        };
       
        register = (TextView) findViewById(R.id.register_link);
        
        spannableString=new SpannableString(register.getText().toString());
        spannableString.setSpan(clickableSpan, 17, register.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //click event
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 17, register.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //���雓�鞊莎�揭���雓�鞊莎�揭
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 17, register.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //���雓嚙踝����蕭謖鞊莎�揭���雓�嚙踐狗���蕭謚啾揭
        
        register.setText(spannableString);
        register.setMovementMethod(LinkMovementMethod.getInstance());
*/
		showHashKey(this);
	}
	
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}
		Const.mConnectionResult = result;
/*
		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			Const.mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
*/
	}

	public void onConnected(Bundle arg0) {
		//mSignInClicked = false;
		Toast.makeText(this, getResources().getString(R.string.login_user_connect), Toast.LENGTH_LONG).show();
		//googlechecked = true;
		//loginInBackground(name, pass);

	}
		
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		//Const.mGoogleApiClient.connect();
	}
	
	
	@Override
    protected void onDestroy() {
        if(pdialog!=null ){
        	pdialog.dismiss();
        }
		releaseBmp();
		releaseViews();
		System.gc();
		super.onDestroy();
        Log.i(TAG, "onDestory()............");
        context.startService(gcmServiceIntent);
    }

	private void allocateBmp() {
		bmpSkipBtn = PictUtil.getLocalBitmap(this, R.drawable.skip, 1);
		bmpIntro1 = PictUtil.getLocalBitmap(this, R.drawable.introduction_1, 1);
        bmpIntro2 = PictUtil.getLocalBitmap(this, R.drawable.introduction_2, 1);
		bmpIntro3 = PictUtil.getLocalBitmap(this, R.drawable.introduction_3, 1);
		bmpIntro4 = PictUtil.getLocalBitmap(this, R.drawable.introduction_4, 1);
	}

	private void releaseBmp() {
		if(bmpSkipBtn != null){
			bmpSkipBtn.recycle();
		}
		if(bmpIntro1 != null){
			bmpIntro1.recycle();
		}
		if(bmpIntro2 != null){
			bmpIntro2.recycle();
		}
		if(bmpIntro3 != null){
			bmpIntro3.recycle();
		}
		if(bmpIntro4 != null){
			bmpIntro4.recycle();
		}
	}

	private void releaseViews() {
		if(skipbutton != null) {
			skipbutton.setImageBitmap(null);
			skipbutton.setBackground(null);
		}
		if(imgIntroduction1 != null){
			imgIntroduction1.setImageBitmap(null);
		}
		if(imgIntroduction2 != null){
			imgIntroduction2.setImageBitmap(null);
		}
		if(imgIntroduction3 != null){
			imgIntroduction3.setImageBitmap(null);
		}
		if(imgIntroduction4 != null){
			imgIntroduction4.setImageBitmap(null);
		}
		if(pageViews != null){
			pageViews.clear();
			pageViews = null;
		}
		if(imageViews != null) {
			for (int i = 0; i < imageViews.length; i++) {
				if (imageViews[i] != null) {
					imageViews[i].setBackgroundResource(0);
				}
			}
			imageViews = null;
		}
		if(relayoutWelcomPage != null) {
			relayoutWelcomPage.setBackground(null);
		}
	}

	@SuppressWarnings("deprecation")
	private void initViewPager() {

		pagerLayout = (LinearLayout) findViewById(R.id.view_pager_content);

    	adViewPager = new ViewPager(this);
    	DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
    	
        adViewPager.setLayoutParams(new LayoutParams(dm.widthPixels, dm.heightPixels/* * 2 / 5*/));
        
    	pagerLayout.addView(adViewPager);
    	
    	initPageAdapter();
    	
    	initCirclePoint();
    	
    	adViewPager.setAdapter(adapter);
    	adViewPager.setOnPageChangeListener(new AdPageChangeListener());
    	
    	/*new Thread(new Runnable() {  
            @Override  
            public void run() {  
                while (true) {  
                    if (isContinue) {  
                        viewHandler.sendEmptyMessage(atomicInteger.get());  
                        atomicOption();  
                    }  
                }  
            }  
        }).start();*/  
	}
	
	
	/*private void atomicOption() {  
        atomicInteger.incrementAndGet();  
        if (atomicInteger.get() > imageViews.length - 1) {  
        	atomicInteger.getAndAdd(-5);  
        }  
        try {  
            Thread.sleep(3000);  
        } catch (InterruptedException e) {  
              
        }  
    } 
	
    private final Handler viewHandler = new Handler() {  
  
        @Override  
        public void handleMessage(Message msg) {  
        	adViewPager.setCurrentItem(msg.what);  
            super.handleMessage(msg);  
        }  
  
    }; */
	
	private void initPageAdapter() {
		pageViews = new ArrayList<View>();
		
		/*ImageView img1 = new ImageView(this);  
		img1.setScaleType(ScaleType.FIT_CENTER);
        img1.setBackgroundResource(R.drawable.introduction);
        pageViews.add(img1);*/

		imgIntroduction1 = new ImageView(this);
		imgIntroduction1.setScaleType(ScaleType.FIT_XY);
		imgIntroduction1.setImageBitmap(bmpIntro1);
		pageViews.add(imgIntroduction1);

		imgIntroduction2 = new ImageView(this);
		imgIntroduction2.setScaleType(ScaleType.FIT_XY);
		imgIntroduction2.setImageBitmap(bmpIntro2);
		pageViews.add(imgIntroduction2);

		imgIntroduction3 = new ImageView(this);
		imgIntroduction3.setScaleType(ScaleType.FIT_XY);
		imgIntroduction3.setImageBitmap(bmpIntro3);
		pageViews.add(imgIntroduction3);

		imgIntroduction4 = new ImageView(this);
		imgIntroduction4.setScaleType(ScaleType.FIT_XY);
		imgIntroduction4.setImageBitmap(bmpIntro4);
		pageViews.add(imgIntroduction4);

//        ImageView img1 = new ImageView(this);
//        img1.setScaleType(ScaleType.FIT_CENTER);
//        img1.setBackgroundResource(R.drawable.introduction_1);
//        pageViews.add(img1);
        
//        ImageView img2 = new ImageView(this);
//        img2.setScaleType(ScaleType.FIT_CENTER);
//        img2.setBackgroundResource(R.drawable.introduction_2);
//        pageViews.add(img2);
//
//        ImageView img3 = new ImageView(this);
//        img3.setScaleType(ScaleType.FIT_CENTER);
//        img3.setBackgroundResource(R.drawable.introduction_3);
//        pageViews.add(img3);
//
//        ImageView img4 = new ImageView(this);
//        img4.setScaleType(ScaleType.FIT_CENTER);
//        img4.setBackgroundResource(R.drawable.introduction_4);
//        pageViews.add(img4);
        
        /*ImageView img5 = new ImageView(this);
        img4.setScaleType(ScaleType.FIT_CENTER);
        img5.setBackgroundResource(R.drawable.background_img);  
        pageViews.add(img5);*/
        
        /*ImageView img6 = new ImageView(this);  
        img6.setBackgroundResource(R.drawable.view_add_6);  
        pageViews.add(img6);*/
        
        adapter = new AdPageAdapter(pageViews);
	}
	
	private void initCirclePoint(){
		ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup); 
        imageViews = new ImageView[pageViews.size()];  

        for (int i = 0; i < pageViews.size(); i++) {  

//        	imageView = new ImageView(this);
//            imageView.setLayoutParams(new LayoutParams(15,15));
//            imageViews[i] = imageView;
			imageViews[i] = new ImageView(this);
			imageViews[i].setLayoutParams(new LayoutParams(15,15));
//			imageViews[i] = imageView;

			if (i == 0) {
                imageViews[i]  
                        .setBackgroundResource(R.drawable.point_focused);  
            } else {  
                imageViews[i]  
                        .setBackgroundResource(R.drawable.point_unfocused);  
            }  

            group.addView(imageViews[i]);  
        } 
	}
	
    private final class AdPageChangeListener implements OnPageChangeListener {  
    	
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
        	Log.v("WelcomPage", "onPageScrollStateChanged: " + arg0 );
        	PageScrollState = arg0;
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) { 
        	Log.v("WelcomPage", "onPageScrolled: " + arg0 + " value: " + arg1 );
        	
        	if(currentPage == (imageViews.length -1) && PageScrollState == 1 && arg1 < 0.1){
            	Intent i = new Intent();
				//i.setClass(WelcomPage.this, MainPage.class);
				//i.putExtra(MainPage.MAINPAGE_LOCATION, location);
				//Change next page from MainPage to StartPage
				i.setClass(WelcomPage.this, StartPage.class);
				startActivity(i);
				WelcomPage.this.finish();
				currentPage = PageScrollState = 0;
            }
        }  
  
        @Override  
        public void onPageSelected(int arg0) {  
        	Log.v("WelcomPage", "onPageSelected: " + arg0 );
            //atomicInteger.getAndSet(arg0);  

            for (int i = 0; i < imageViews.length; i++) {  
                imageViews[arg0]  
                        .setBackgroundResource(R.drawable.point_focused);  
                if (arg0 != i) {  
                    imageViews[i]  
                            .setBackgroundResource(R.drawable.point_unfocused);  
                }  
            }
            currentPage = arg0;
            if(arg0 == 0){
            	skipbutton.setVisibility(View.VISIBLE);
            }else if(arg0 == (imageViews.length -1)){
            	//Login_Layout.setVisibility(View.VISIBLE);
            	/*Intent i = new Intent();
				i.setClass(WelcomPage.this, MainPage.class);
				startActivity(i);
				WelcomPage.this.finish();*/
            }else{
            	//Login_Layout.setVisibility(View.INVISIBLE);
            	skipbutton.setVisibility(View.INVISIBLE);
            }
        }  
    } 
	
	
    private final class AdPageAdapter extends PagerAdapter {  
        private List<View> views = null;  

        public AdPageAdapter(List<View> views) {  
            this.views = views;  
        }  
        
        @Override  
        public void destroyItem(View container, int position, Object object) {  
            ((ViewPager) container).removeView(views.get(position));  
        }  
  
        @Override  
        public int getCount() {  
            return views.size();  
        }  
  
        @Override  
        public Object instantiateItem(View container, int position) {  
            ((ViewPager) container).addView(views.get(position), 0);  
            return views.get(position);  
        }  
  
        @Override  
        public boolean isViewFromObject(View view, Object object) {  
            return view == object;  
        }  
    }
    
//    private String registerGCM() {
//
//		gcm = GoogleCloudMessaging.getInstance(this);
//		//regId = getRegistrationId(context);
//
//		if (TextUtils.isEmpty(regId)) {
//
//			registerInBackground();
//
//			Log.d("RegisterActivity",
//					"registerGCM - successfully registered with GCM server - regId: "
//							+ regId);
//		} else {
//			Toast.makeText(getApplicationContext(),
//					"RegId already available. RegId: " + regId.length(),
//					Toast.LENGTH_LONG).show();
//		}
//		return regId;
//	}
//
//
//	private void registerInBackground() {
//		new AsyncTask<Void, Void, String>() {
//			@Override
//			protected String doInBackground(Void... params) {
//				String msg = "";
//				try {
//					if (gcm == null) {
//						gcm = GoogleCloudMessaging.getInstance(context);
//					}
//					regId = gcm.register(Const.projinfo.GOOGLE_PROJECT_ID);
//					Log.d("RegisterActivity", "registerInBackground - regId: "
//							+ regId);
//					msg = "Device registered, registration ID=" + regId;
//
//					//Const.userinfo.regid = regId;
//		        	FileAccess.putStringInPreferences(context, regId, 
//		        			Const.eUsrType.UsrType_RegId, Const.projinfo.sSharePreferenceName);
//		        	
//					//storeRegistrationId(context, regId);
//				} catch (IOException ex) {
//					msg = "Error :" + ex.getMessage();
//					Log.d("RegisterActivity", "Error: " + msg);
//				}
//				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
//				return msg;
//			}
//
//			@Override
//			protected void onPostExecute(String msg) {
//				Toast.makeText(context,
//						"Registered with GCM Server." + msg.length(), Toast.LENGTH_LONG)
//						.show();
//			}
//		}.execute(null, null, null);
//	}

    public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.project1.meetday", PackageManager.GET_SIGNATURES); //Your            package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }    
    
	private void loginInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {			
			//private ServerFuncRun sfr = new ServerFuncRun();
	        protected void onPreExecute() {
//	        	pdialog = ProgressDialog.show(WelcomPage.this, "Connecting", "Wait...",true);
				pdialog = ProgressDialog.show(WelcomPage.this, getResources().getString(R.string.login_connecting), getResources().getString(R.string.login_wait),true);
	        }
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {			
					if(Looper.myLooper() == null)
						Looper.prepare();
					//sfr.name = name;
					//sfr.pass = pass;
					sfr.cmd = Const.Command.Cmd_Login_Type;
					Log.d("LoginActivity", "sfr.: " + sfr.datatype);
					if (Const.isNetworkConnected(context) /*&& !pass.equals(Default)*/) {
						sfr.DoServerFunc();
						while (sfr.GetServResult() == null)
							;
						if(sfr.GetServResult() == Const.RRet.RRET_SUCCESS){
							String get = sfr.GetUserData() + "&";
							Log.d(this.getClass().getName(), "get: " + get);
							String[] data = FileAccess.parseDataToPara(get, "&");
							Const.userinfo.set_login_status(true);
							//PictUtil.deleteFile(FileManager.getSaveFilePath());
							//Const.userinfo.login_status = true;
							//Const.userinfo.name = name;
							if(!FileAccess.getStringFromPreferences(context,
									Default, Const.eUsrType.UsrType_FreindList,
									Const.projinfo.sSharePreferenceName).equals(data[0])){
								FileAccess.putStringInPreferences(context,
										data[0], Const.eUsrType.UsrType_FreindList,
										Const.projinfo.sSharePreferenceName);
							}
							if(!FileAccess.getStringFromPreferences(context,
									Default, Const.eUsrType.UsrType_RegId,
									Const.projinfo.sSharePreferenceName).equals(data[2])){
								ConnectToServer con = new ConnectToServer(context);
								con.DoUpdateData(data[1], Const.eUsrType.UsrType_RegId, FileAccess.getStringFromPreferences(context,
									Default, Const.eUsrType.UsrType_RegId,Const.projinfo.sSharePreferenceName));
							}
							
						}
					}
					//if(!pass.equals(Default)){
					Intent i = new Intent();
					i.setClass(WelcomPage.this, MainPage.class);
					i.putExtra(MainPage.MAINPAGE_LOCATION, location);
					startActivity(i);
					WelcomPage.this.finish();
					//}
					//dialog.dismiss();	        	
					//storeRegistrationId(context, regId);
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("LoginActivity", "Error: " + msg);
				}
				Log.d("LoginActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//Toast.makeText(getApplicationContext(),
					//	"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						//.show();
				pdialog.dismiss();	
				if(sfr.GetServResult()!= Const.RRet.RRET_SUCCESS){
					//Log.d("LoginActivity", "Const.uid: " + Const.uid);
					Const.userinfo.set_login_status(false);

					Toast.makeText(getApplicationContext(),
							"Login Fail" + msg, Toast.LENGTH_LONG)
							.show();
				}
				
			}
		}.execute(null, null, null);
	}

	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}

}


