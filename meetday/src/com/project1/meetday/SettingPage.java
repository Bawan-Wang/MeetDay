package com.project1.meetday;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project1.http.Const;
import com.project1.http.FileAccess;
import com.project1.http.PictUtil;
import com.project1.http.ServerFuncRun;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SettingPage extends Fragment {

	protected static final int RESULT_OK = 0;
	private LinearLayout lilayoutSetPage;
	private TextView setting_username, txtVerNum, txtPrivacyPolicy;
	private View v;
	int status;
	private ListView listView;
	private Intent intent;
	private String mlocaluid;
	private String TAG = "SettingPage";
	
	private static final int[] mPics = new int[]{
		R.drawable.personal, R.drawable.addfriend,/* R.drawable.thanksletter,*/
		/*R.drawable.message,*/ R.drawable.feedback
	};

	private static final int[] arr = new int[]{R.string.sett_personal_info, 
        R.string.sett_add_friends,
        /*R.string.sett_thanks_context,*/
        //R.string.sett_message_context,
        R.string.sett_feedback};			
	
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	private MySimpleAdapterSetting adapter;
	
	private AlertDialog.Builder dialog;
	private EditText input;
	private String strInput;
	private Context context;
	private Bitmap bmpProtrait;
	private ImageView iv;
	static String strPath;
	
	//private ArrayAdapter<String> adapter;
	
	private final static int PICK_FROM_GALLERY=0x123; 
	private final static int PICK_FROM_GET=0x124; 
	private final static int RESULT_LOAD_IMAGE=0x125;
	InputMethodManager InputManager;
	
	//private SharedPreferences settings;
	//private static final String Sharedquestionata = "SHARE_QUESTIONDATA";
	//private static final String Sharedletterdata = "SHARE_LETTERDATA";
	//private static final String Sharedquestion = "SHARE_QUESTION";
    //private static final String Sharedletter = "SHARE_LETTER";
    
    //private String ShareString;
    
    //private static final String ACTIVITY_TAG="LogDemo";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(!isAdded()){
			Log.d(this.getClass().getName(),"On isAdded");
		    //return;
		}
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		//Const.userinfo.Location_Type = Const.eLocation.Location_Setting;
		this.context = getActivity();
		mlocaluid = FileAccess.getStringFromPreferences(context, 
				null, Const.eUsrType.UsrType_UserId, Const.projinfo.sSharePreferenceName);
		FileAccess.putStringInPreferences(context, String.valueOf(Const.eLocation.Location_Setting), 
    			Const.eUsrType.UsrType_FragmentLocation, Const.projinfo.sSharePreferenceName);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.setting_page, container, false);
		InputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		lilayoutSetPage = (LinearLayout) v.findViewById(R.id.lilayoutSetPage);
		setting_username = (TextView) v.findViewById(R.id.setting_username);
		setting_username.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BELLB.TTF"));
		
	    String strTmpName = getResources().getString(R.string.login_nickname_hint) + " :\n  " + FileAccess.getStringFromPreferences(context, "", Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
		setting_username.setText(strTmpName);
		setting_username.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog = new AlertDialog.Builder(context);
				input = new EditText(context);
				//String name = settings.getString("nickname", "N/A");
				dialog.setTitle(getResources().getString(R.string.login_nickname_hint) + " :");
				//dialog.setMessage(xMessage);
				input.setText(FileAccess.getStringFromPreferences(context,
						"", Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName));
				dialog.setView(input);
				dialog.setPositiveButton(/*"OK"*/getResources().getString(R.string.ok_label), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						strInput = input.getText().toString();
						String strTmpName = getResources().getString(R.string.login_nickname_hint) + " :\n  " + strInput;
						setting_username.setText(strTmpName);
						ServerFuncRun sfr = new ServerFuncRun();
						sfr.cmd = Const.Command.Cmd_UpdateUsrData;//Const.Command.Cmd_UpdateUsrData;
						sfr.datatype = Const.eUsrType.UsrType_NickName;
						sfr.usr_id = mlocaluid;
						sfr.data = strInput;
						if (sfr.usr_id != null)
							UpdateInBackground(sfr);
					}
				});
				dialog.setNegativeButton(/*"Cancel"*/getResources().getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
					}
				});
				dialog.show();
			}
		});

		iv = (ImageView)v.findViewById(R.id.personal_photo);
		getImageInfo();
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				
				List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(intent, 0);
			    if (!resInfo.isEmpty()){
			    	List<Intent> targetedShareIntents = new ArrayList<Intent>();
			        for (ResolveInfo info : resInfo) {
			        	Intent targeted = new Intent(Intent.ACTION_PICK, null);
			        	ActivityInfo activityInfo = info.activityInfo;
			        	if (!activityInfo.packageName.contains("android.apps.photos")) {
							Log.d(TAG, activityInfo.packageName.toString());
			        		targeted.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			        		targeted.setPackage(activityInfo.packageName);
			        		targetedShareIntents.add(targeted);
						}
			        }
			        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select Picture.");
		            if (chooserIntent == null) {
		                return;
		            }
		            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray());
		            try {
						startActivityForResult(chooserIntent, PICK_FROM_GALLERY);
		            } catch (android.content.ActivityNotFoundException ex) {
		                Toast.makeText(context, "Can't find any gallery", Toast.LENGTH_SHORT).show();
		            }			        
			    }
			}
		});
		
		listView = (ListView) v.findViewById(R.id.contact_listView);

		for(int i=0; i<arr.length; i++){
			 HashMap<String,Object> item = new HashMap<String,Object>();
			 item.put("pic", mPics[i]);
//			 item.put( "string", arr[i]);
			 item.put( "string", getResources().getString(arr[i]));
			 list.add( item );
			 }
		
		adapter = new MySimpleAdapterSetting(context, list, R.layout.settinglist, new String[] { "pic","string"},new int[] { R.id.settingItemIcon, R.id.settingItemText} );
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch(arg2){
					case 0:
						intent = new Intent(context, PersonalPage.class);
						startActivity(intent);
					break;
					case 1:
						intent = new Intent(context, AddFriendNewPage.class);
						startActivity(intent);
					break;
					case 2:
//						EditMessageDialog(getResources().getString(arr[2]), FileAccess.getStringFromPreferences(context,
//								getString(R.string.def_thanktext), Const.eUsrType.UsrType_ThanksMsg, Const.projinfo.sSharePreferenceName));
//					break;
//					case 3:
//						EditMessageDialog(getResources().getString(arr[3]), FileAccess.getStringFromPreferences(context, 
//								getString(R.string.def_questiontext), Const.eUsrType.UsrType_HelpMsg, Const.projinfo.sSharePreferenceName));
//					break;
//					case 4:
						TalktoUsDialog();
					break;
				}
			}
		});
		
		txtVerNum = (TextView) v.findViewById(R.id.setting_ver_number);
        String versionName;
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			txtVerNum.setText(txtVerNum.getHint() + " : " + versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		txtPrivacyPolicy = (TextView) v.findViewById(R.id.setting_privacy_policy);
		txtPrivacyPolicy.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(context, PrivacyPolicyPage.class);
				startActivity(intent);
			}});

		return v;
	}  

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d("uri", "requestCode: " + String.valueOf(requestCode) + " resultCode: " + String.valueOf(resultCode));
		if (requestCode == PICK_FROM_GALLERY) {
            if (resultCode == -1) {
                intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(data.getData(), "image/*");
				List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(intent, 0);
				if (!resInfo.isEmpty()) {
					List<Intent> targetedShareIntents = new ArrayList<Intent>();
					for (ResolveInfo info : resInfo) {
						Intent targeted = new Intent("com.android.camera.action.CROP");
						ActivityInfo activityInfo = info.activityInfo;
						if (!activityInfo.packageName.contains("android.apps.photos")) {
						    Log.d(TAG, activityInfo.packageName.toString());
							targeted.setDataAndType(data.getData(), "image/*");
							targeted.setPackage(activityInfo.packageName);
							targeted.putExtra("crop", "true");
							targeted.putExtra("aspectX", 1);
							targeted.putExtra("aspectY", 1);
							targeted.putExtra("outputX", 150);
							targeted.putExtra("outputY", 150);
							targeted.putExtra("return-data", true);
							targetedShareIntents.add(targeted);
						}
					}
					Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select Picture.");
					if (chooserIntent == null) {
						return;
					}
					chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray());
					try {
						startActivityForResult(chooserIntent, PICK_FROM_GET);
					} catch (android.content.ActivityNotFoundException ex) {
						Toast.makeText(context, "Can't find any gallery", Toast.LENGTH_SHORT).show();
					}
				}
            }
		}else if (requestCode == PICK_FROM_GET) {
             Bundle extras = data.getExtras();
             if (extras != null) {
                Bitmap photo = extras.getParcelable("data");

                int oldwidth = photo.getWidth();
                int oldheight = photo.getHeight();
                float scaleWidth = 230 / (float) oldwidth;
                float scaleHeight = 230 / (float) oldheight;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                // create the new Bitmap object
                Bitmap resizedBitmap = Bitmap.createBitmap(photo, 0, 0, oldwidth, oldheight, matrix, true);
                Bitmap roundBitmap = getRoundedCornerBitmap(resizedBitmap,70.0f);
                iv.setImageBitmap(roundBitmap);
                //saveImage(roundBitmap);
                PictUtil.saveImage(roundBitmap, mlocaluid, context);
             }
        } 
	}

	@Override
	public void onDestroyView() {
		releaseBmp();
		releaseViews();
		System.gc();
		super.onDestroyView();
	}

	private void allocateBmp() {
		bmpProtrait = PictUtil.getLocalBitmap(getActivity(), R.drawable.greenman, 1);
	}

	private void releaseBmp() {
		if(bmpProtrait != null){
			bmpProtrait.recycle();
			bmpProtrait = null;
		}
	}

	private void releaseViews() {
		if(iv != null){
			iv.setImageBitmap(null);
			iv.setBackground(null);
		}
		if(lilayoutSetPage != null){
			lilayoutSetPage.setBackground(null);
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){         
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
	    Canvas canvas = new Canvas(output);  
	    final int color = 0xff424242;  
	    final Paint paint = new Paint();  
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(),
	                                     bitmap.getHeight());  
	    final RectF rectF = new RectF(rect);  
	    paint.setAntiAlias(true);  
	    canvas.drawARGB(0, 0, 0, 0);  
	    paint.setColor(color);  
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
	    paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));  
	    canvas.drawBitmap(bitmap, rect, rect, paint);  
	    return output;  
	}  
	
	private void getImageInfo(){
//		Bitmap bitmap = PictUtil.loadFromFile(Const.localProfilePicPath);
		bmpProtrait = PictUtil.loadFromFile(Const.localProfilePicPath);
		if(bmpProtrait != null) {
			iv.setImageBitmap(bmpProtrait);
		}else{
			allocateBmp();
			iv.setImageBitmap(bmpProtrait);
		}
	}
	
	private void saveImage(Bitmap bitmap){
		PictUtil.saveToFile(context, Const.localProfilePicPath, bitmap);
		//Upload to server
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 80, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
		ServerFuncRun sfr = new ServerFuncRun();
		sfr.cmd = Const.Command.Cmd_UpdatePhoto;//Const.Command.Cmd_UpdateUsrData;
        sfr.usr_id = mlocaluid;
        sfr.imageStr = imageString;
        //sfr1.context = getActivity();
        //sfr.DoServerFunc();
        if(sfr.usr_id!=null)
        	UpdateInBackground(sfr);
	}
	
	public void TalktoUsDialog() {

//		intent = new Intent();
//	    intent.setAction(Intent.ACTION_SENDTO);
//	    intent.setData(Uri.parse("mailto:sylapp7@gmail.com"));
//	    intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.sett_feedback_title));
//	    startActivity(intent);

		String url="https://goo.gl/forms/qm53rMowkuy8POTu1";
		Intent googleDoc = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
		startActivity(googleDoc);

	} //EOF InviteDialog
	
	public void EditMessageDialog(final String title, String message) {
	      final EditText input = new EditText(context);
	      AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	      input.setText(message);
	      dialog.setView(input);
	      dialog.setTitle(title);
	      dialog.setPositiveButton(getResources().getString(R.string.sett_OK), new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {
	            	String Content = input.getText().toString();
	            	if(Content.length()==0){
//	            		if(title=="question"){
	            		if(title.equals(getResources().getString(R.string.sett_message_context))){
	            			FileAccess.putStringInPreferences(context, getString(R.string.def_questiontext), 
	    	              			Const.eUsrType.UsrType_HelpMsg, Const.projinfo.sSharePreferenceName);
//	            		}else if(title=="letter"){
	            		}else if(title.equals(getResources().getString(R.string.sett_thanks_context))){
	            			FileAccess.putStringInPreferences(context, getString(R.string.def_thanktext), 
	    	              			Const.eUsrType.UsrType_ThanksMsg, Const.projinfo.sSharePreferenceName);	            	
	            		}
	            		InputManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
	            	}else{
//	            		if(title=="question"){
	            		if(title.equals(getResources().getString(R.string.sett_message_context))){
	            			FileAccess.putStringInPreferences(context, input.getText().toString(), 
	    	              			Const.eUsrType.UsrType_HelpMsg, Const.projinfo.sSharePreferenceName);
//		            	}else if(title=="letter"){
	            		}else if(title.equals(getResources().getString(R.string.sett_thanks_context))){
	            			FileAccess.putStringInPreferences(context, input.getText().toString(), 
	    	              			Const.eUsrType.UsrType_ThanksMsg, Const.projinfo.sSharePreferenceName);
	            		}
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
	      AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	      dialog.setTitle(title);
	      dialog.setMessage(message);
	      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialoginterface, int i) {

	           }
	      });
	      dialog.show();
	} //EOF warningDialog
	
	private void UpdateInBackground(final ServerFuncRun sfr) {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog pdialog;
			//private ServerFuncRun sfr = new ServerFuncRun();
	        protected void onPreExecute() {
	        	pdialog = ProgressDialog.show(context, "Connecting", "Wait...",true);				
	            //this.pdialog.setMessage("Progress start");
	            //this.pdialog.show();
	        }
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {				
					if(Looper.myLooper() == null)
						Looper.prepare();
					sfr.DoServerFunc();
					while(sfr.GetServResult() == null)
						;
					//dialog.dismiss();
					
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("UpdateActivity", "Error: " + msg);
				}
				Log.d("UpdateActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				pdialog.dismiss();
				if(Const.RRet.RRET_SUCCESS == sfr.GetServResult()){
					if(Const.Command.Cmd_UpdateUsrData == sfr.cmd)
						FileAccess.putStringInPreferences(context, sfr.data, 
	              			Const.eUsrType.UsrType_NickName, Const.projinfo.sSharePreferenceName);
					//Toast.makeText(getActivity(), "You Ask "+ + " for Help!", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(context, "Update Fail", Toast.LENGTH_SHORT).show();		        	
				}
			}
		}.execute(null, null, null);
	}

	
}