package com.project1.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.project1.meetday.ConnectToServer;
import com.project1.meetday.R;
import com.project1.meetday.SingleMediaScanner;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PictUtil {
	
	//public PictUtil(){

	//}

	/*
	 * Read image from local id
	 * Same size when scale = 1,
	 * read bitmap smaller then original when inSampleSize > 1
	 * */
	public static Bitmap getLocalBitmap(Context context, int resourceId, int scale){
		Bitmap bmpRetVal = null;
		InputStream inputStream = context.getResources().openRawResource(resourceId);
		bmpRetVal = BitmapFactory.decodeStream(inputStream, null, getBitmapOptions(scale));
		return bmpRetVal;
	}	
	
	@SuppressWarnings("deprecation")
	public static BitmapFactory.Options getBitmapOptions(int scale){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = scale;
		return options;
	}	
	
  public static File getSavePath() {
      File path;
      if (hasSDCard()) { // SD card
          path = new File(getSDCardPath() + Const.projinfo.sLocalFile+"/image/");
          if(!path.exists()) {
        	  path.mkdirs();
          }
      } else { 
          path = Environment.getDataDirectory();
      }
      return path;
  }
  public static String getCacheFilename() {
      File f = getSavePath();
      return f.getAbsolutePath() + "/cache.png";
  }

  public static Bitmap loadFromFile(String filename) {
      try {
          File f = new File(filename);
          if (!f.exists()) { return null; }
          Bitmap tmp = BitmapFactory.decodeFile(filename);
          return tmp;
      } catch (Exception e) {
          return null;
      }
  }
  public static Bitmap loadFromCacheFile() {
      return loadFromFile(getCacheFilename());
  }
  public static void saveToCacheFile(Bitmap bmp) {
      saveToFile(getCacheFilename(),bmp);
  }
  
  public static FileOutputStream saveToFile(String filename,Bitmap bmp) {
      try {
    	  //System.out.println(filename);
          FileOutputStream out = new FileOutputStream(filename);
          bmp.compress(CompressFormat.PNG, 100, out);
          out.flush();
          out.close();
          return out;
      } catch(Exception e) {
    	  return null;
      }
      
  }
  
  public static FileOutputStream saveToFile(Context context,String filename,Bitmap bmp) {
	  //FileIOInBackground(context,filename,bmp);
      try {
      	  File file = new File(filename);
      	  SingleMediaScanner s = new SingleMediaScanner(context, file);
          FileOutputStream out = new FileOutputStream(filename);
          bmp.compress(CompressFormat.PNG, 100, out);
          out.flush();
          out.close();
          return out;
      } catch(Exception e) {
    	  return null;
      }
      
  }
  
  public static boolean hasSDCard() { // SD????????
      String status = Environment.getExternalStorageState();
      return status.equals(Environment.MEDIA_MOUNTED);
  }
  public static String getSDCardPath() {
      File path = Environment.getExternalStorageDirectory();
      return path.getAbsolutePath();
  }
  
  public static void deleteFile(String path) {
		File dirFile = new File(path);
		if(! dirFile.exists()){
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
		
		dirFile.delete();
	}
  
	public static Bitmap takepic(long starttime, Context c) throws Exception {
		Log.i("X", starttime + "----开始获取/dev/graphic/fb0的数据-------");
		InputStream stream = new FileInputStream(new File("/dev/graphics/fb0"));
		
		DisplayMetrics metrics = new DisplayMetrics();
		int height = metrics.heightPixels; // 屏幕高
		int width = metrics.widthPixels; // 屏幕的宽
		WindowManager WM = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
		Display display = WM.getDefaultDisplay();
		display.getMetrics(metrics);
		int pixelformat = display.getPixelFormat();
		PixelFormat localPixelFormat1 = new PixelFormat();
		PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
		int deepth = localPixelFormat1.bytesPerPixel;// 位深
		byte[] piex = new byte[height * width * deepth];
		@SuppressWarnings("resource")
		DataInputStream dStream = new DataInputStream(stream);
		dStream.read(piex, 0, height * width * deepth);
		// 保存图片
		int[] colors = new int[height * width];
		for (int m = 0; m < piex.length; m++) {
			if (m % 4 == 0) {
				int r = (piex[m] & 0xFF);
				int g = (piex[m + 1] & 0xFF);
				int b = (piex[m + 2] & 0xFF);
				int a = (piex[m + 3] & 0xFF);
				colors[m / 4] = (a << 24) + (r << 16) + (g << 8) + b;
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_4444);
		long endtime = System.currentTimeMillis();
		Log.i("XXX", endtime + "----转码完成-----耗时" + (endtime - starttime) + "毫秒------");
		return bitmap;
	}
  
	public static void saveImage(Bitmap bitmap, String userid, Context context){
		PictUtil.saveToFile(Const.localProfilePicPath, bitmap);
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
	
	public static Bitmap decodeFile(File f) {
		    try {
		        // Decode image size
		        BitmapFactory.Options o = new BitmapFactory.Options();
		        o.inJustDecodeBounds = true;
		        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

		        // The new size we want to scale to
		        final int REQUIRED_SIZE=256;

		        // Find the correct scale value. It should be the power of 2.
		        int scale = 1;
		        while(o.outWidth / scale / 2 >= REQUIRED_SIZE && 
		              o.outHeight / scale / 2 >= REQUIRED_SIZE) {
		            scale *= 2;
		        }

		        // Decode with inSampleSize
		        BitmapFactory.Options o2 = new BitmapFactory.Options();
		        o2.inSampleSize = scale;
		        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		    } catch (FileNotFoundException e) {}
		    return null;
		}
	
	public static Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);        
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
	
	public static void FileIOInBackground(final Context context,final String filename,final Bitmap bmp) {
		new AsyncTask<Void, Void, String>() {
			boolean error = false;
	        protected void onPreExecute() {
	        	//pdialog = ProgressDialog.show(WelcomPage.this, "Connecting", "Wait...",true);				
	            //this.pdialog.setMessage("Progress start");
	            //this.pdialog.show();
	        }
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";				
				try {				
					if(Looper.myLooper() == null)
						Looper.prepare();
					
					try {
				      	  File file = new File(filename);
				      	  SingleMediaScanner s = new SingleMediaScanner(context, file);
				          FileOutputStream out = new FileOutputStream(filename);
				          bmp.compress(CompressFormat.JPEG, 100, out);
				          out.flush();
				          out.close();
				      } catch(Exception e) {
				    	  error = true;			    	  
				      }					
				} catch (Exception ex) {
					error = true;
					msg = "Error :" + ex.getMessage();
					Log.d("FileIOInBackground", "Error: " + msg);
				}
				Log.d("FileIOInBackground", "FileIOInBackground completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//pdialog.dismiss();
				if(error)
					Toast.makeText(context, context.getResources().getString(R.string.comm_dialog_save_fail), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(context, context.getResources().getString(R.string.comm_dialog_save_success), Toast.LENGTH_SHORT).show();
			}
		}.execute(null, null, null);
	}
}
