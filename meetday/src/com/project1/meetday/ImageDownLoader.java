package com.project1.meetday;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.project1.http.FileUtils;

public class ImageDownLoader {
	/**
	 * 嚙緩嚙編Image嚙踝蕭嚙踝蕭嚙璀嚙踝蕭s嚙綞Image嚙踝蕭嚙篌嚙緘嚙篌嚙踝蕭LruCache嚙稽嚙緩嚙踝蕭嚙褓，嚙緣嚙諄自堆蕭嚙踝蕭嚙踏內存
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	/**
	 * 嚙豬作嚙踝蕭嚙踝蕭嚙踝蕭嚙踝蕭嚙踝蕭H嚙踝蕭嚙豬伐蕭
	 */
	private FileUtils fileUtils;
	/**
	 * 嚙磊嚙踝蕭Image嚙踝蕭嚙線嚙緹嚙踝蕭
	 */
	private ExecutorService mImageThreadPool = null;
	
	
	public ImageDownLoader(Context context){
		//
		int maxMemory = (int) Runtime.getRuntime().maxMemory();  
        int mCacheSize = maxMemory / 8;
        //嚙踝蕭LruCache嚙踝蕭嚙緣1/8 4M
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
			
		};
		
		fileUtils = new FileUtils(context);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public ExecutorService getThreadPool(){
		if(mImageThreadPool == null){
			synchronized(ExecutorService.class){
				if(mImageThreadPool == null){
					//
					mImageThreadPool = Executors.newFixedThreadPool(2);
				}
			}
		}
		
		return mImageThreadPool;
		
	}
	
	/**
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
	    if (getBitmapFromMemCache(key) == null && bitmap != null) {  
	        mMemoryCache.put(key, bitmap);  
	    }  
	}  
	 
	/**
	 *Bitmap
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {  
	    return mMemoryCache.get(key);  
	} 
	
	/**
	 * 
	 * 
	 * @param url
	 * @param listener
	 * @return
	 */
	public Bitmap downloadImage(final String url, final onImageLoaderListener listener){
		final String subUrl = url.replaceAll("[^\\w]", "");
		Bitmap bitmap = showCacheBitmap(subUrl);
		if(bitmap != null){
			return bitmap;
		}else{
			
			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					listener.onImageLoader((Bitmap)msg.obj, url);
				}
			};
			
			getThreadPool().execute(new Runnable() {
				
				@Override
				public void run() {
					Bitmap bitmap = getBitmapFormUrl(url);
					Message msg = handler.obtainMessage();
					msg.obj = bitmap;
					handler.sendMessage(msg);
					
					try {
						//嚙瞌嚙編嚙箭SD嚙範嚙諄者歹蕭嚙踝蕭媬嚙�
						fileUtils.savaBitmap(subUrl, bitmap);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//嚙瞇Bitmap 嚙稼嚙皚嚙踝蕭嚙編嚙緩嚙編
					addBitmapToMemoryCache(subUrl, bitmap);
				}
			});
		}
		
		return null;
	}
	
	/**
	 * 嚙踝蕭嚙畿itmap, 嚙踝蕭嚙編嚙踝蕭嚙磅嚙踝蕭嚙瞇嚙篁嚙踝蕭嚙踝蕭峈嚙編d嚙範嚙踝蕭嚙踝蕭嚙璀嚙緻嚙瑾嚙畿嚙箭getView嚙踝蕭嚙罵嚙調用，嚙踝蕭嚙踝蕭嚙踝蕭銂綽蕭@嚙畿
	 * @param url
	 * @return
	 */
	public Bitmap showCacheBitmap(String url){
		if(getBitmapFromMemCache(url) != null){
			return getBitmapFromMemCache(url);
		}else if(fileUtils.isFileExists(url) && fileUtils.getFileSize(url) != 0){
			//嚙緬SD嚙範嚙踝蕭嚙踝蕭嚙踝蕭怑嚙踝蕭嚙踝蕭Bitmap
			Bitmap bitmap = fileUtils.getBitmap(url);
			
			//嚙瞇Bitmap 嚙稼嚙皚嚙踝蕭嚙編嚙緩嚙編
			addBitmapToMemoryCache(url, bitmap);
			return bitmap;
		}
		
		return null;
	}
	
	
	/**
	 * 嚙緬Url嚙踝蕭嚙踝蕭嚙畿itmap
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFormUrl(String url) {
		Bitmap bitmap = null;
		HttpURLConnection con = null;
		try {
			URL mImageUrl = new URL(url);
			con = (HttpURLConnection) mImageUrl.openConnection();
			con.setConnectTimeout(10 * 1000);
			con.setReadTimeout(10 * 1000);
			con.setDoInput(true);
			con.setDoOutput(true);
			bitmap = BitmapFactory.decodeStream(con.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return bitmap;
	}
	
	/**
	 * 嚙踝蕭嚙箭嚙磊嚙踝蕭嚙踝蕭嚙�
	 */
	public synchronized void cancelTask() {
		if(mImageThreadPool != null){
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}
	
	
	/**
	 * 嚙踝蕭嚙畿嚙磊嚙踝蕭洃嚙稷嚙調梧蕭嚙篆
	 * @author len
	 *
	 */
	public interface onImageLoaderListener{
		void onImageLoader(Bitmap bitmap, String url);
	}
	
	public String getImageFolder(){
		return fileUtils.getStorageDirectory();
	}
	
}

