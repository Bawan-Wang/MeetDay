package com.project1.meetday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.project1.http.Const;
import com.project1.http.ImageLoader;
import com.project1.http.PictUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SignUpPage extends Activity {
	
	public static final String USERID =
		    "com.project1.userid";
	public static final String NICKNAME =
		    "com.project1.nickname";
	public static final String ACCOUNT =
		    "com.project1.account";
	public static final String PASSWD =
		    "com.project1.passwd";
	public static final String PHOTOURL =
		    "com.project1.photourl";
	public static final String LOGINTYPE =
		    "com.project1.type";
	public static final String LOGINDATA =
		    "com.project1.data";
	
	private static ImageButton gotomainpage;
	private static EditText signup_nickname;
	private static ImageView iv = null;
	private static Intent intent;
	
	private final static int PICK_FROM_GALLERY=0x123; 
	private final static int PICK_FROM_GET=0x124; 
	
	private String userid, nickname, account, pass, fotourl,type,data;
	private Context context;
	private boolean photosaved = false;
	private String TAG = "SignUpPage";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Do not show title

		setContentView(R.layout.signup_page);
		
        Intent i = getIntent();
		userid = i.getStringExtra(USERID);
		nickname = i.getStringExtra(NICKNAME);
		account = i.getStringExtra(ACCOUNT);
		pass = i.getStringExtra(PASSWD);
		fotourl = i.getStringExtra(PHOTOURL);
		data = i.getStringExtra(LOGINDATA);
		type = i.getStringExtra(LOGINTYPE);
		this.context = getApplicationContext();
		signup_nickname = (EditText) findViewById(R.id.signup_nickname_edit);
		signup_nickname.setText(nickname);

		iv = (ImageView) findViewById(R.id.signup_photo);
		//getImageInfo();
		ImageLoader mImageLoader = new ImageLoader(this);
		mImageLoader.DisplayImage(fotourl,iv,false);
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);
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

//				startActivityForResult(intent, PICK_FROM_GALLERY);
			}
		});

		gotomainpage = (ImageButton) findViewById(R.id.signup_gotomainpage_btn);
		gotomainpage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
		        Toast.makeText(context, 
        		"Name    :" + signup_nickname.getText().toString() + "\r\n",
        		Toast.LENGTH_SHORT).show();
				ConnectToServer con = new ConnectToServer(context);
				con.DoUpdateData(userid, Const.eUsrType.UsrType_NickName, signup_nickname.getText().toString());
				if(photosaved==false){
					Bitmap imgDefault = getImageInfo();
					if(imgDefault != null) {
						PictUtil.saveImage(imgDefault, userid, context);
					}
				}
		        intent = new Intent();
		        intent.setClass(SignUpPage.this, LoginPage.class);
		        intent.putExtra(LoginPage.ACCOUNT, account);
		        intent.putExtra(LoginPage.PASSWD, pass);
		        intent.putExtra(LoginPage.REDIRECT, Const.eSocialCmd.Social_Login);
		        intent.putExtra(LoginPage.LOGINTYPE, type);
		        intent.putExtra(LoginPage.LOGINDATA, data);
				startActivity(intent);
				SignUpPage.this.finish();
			}
        });

	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d("uri", "requestCode: " + String.valueOf(requestCode) + " resultCode: " + String.valueOf(resultCode));
		if (requestCode == PICK_FROM_GALLERY) {
            if (resultCode == -1) {
                intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(data.getData(), "image/*");
				List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);
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
//                intent.putExtra("crop", "true");
//                intent.putExtra("aspectX", 1);
//                intent.putExtra("aspectY", 1);
//                intent.putExtra("outputX", 150);
//                intent.putExtra("outputY", 150);
//                intent.putExtra("return-data", true);
//                startActivityForResult(intent, PICK_FROM_GET);
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
                PictUtil.saveImage(roundBitmap, userid, context);
                photosaved = true;
             }
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
	
	private Bitmap getImageInfo(){
		Bitmap bitmap = null;
		bitmap = PictUtil.loadFromFile(Const.localProfilePicPath);
		if(bitmap!=null) {
			if(iv != null) {
				iv.setImageBitmap(bitmap);
			}
		}
		return bitmap;
	}
	
	private void saveImage(Bitmap bitmap){
		PictUtil.saveToFile(context, Const.localProfilePicPath, bitmap);
		//Upload to server
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 80, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
		//ServerFuncRun sfr = new ServerFuncRun();
		//sfr.cmd = Const.Command.Cmd_UpdatePhoto;//Const.Command.Cmd_UpdateUsrData;
        //sfr.usr_id = mlocaluid;
        //sfr.imageStr = imageString;
        //sfr1.context = getActivity();
        //sfr.DoServerFunc();
        //if(sfr.usr_id!=null)
        	//UpdateInBackground(sfr);
		ConnectToServer con = new ConnectToServer(context);
		con.DoUpdatePhoto(userid, imageString);
		//sfr.cmd = Const.Command.Cmd_UpdatePhoto;//Const.Command.Cmd_UpdateUsrData;
        //sfr.usr_id = mlocaluid;
        //sfr.imageStr = imageString;
        //sfr1.context = getActivity();
        //sfr.DoServerFunc();
	}
}
