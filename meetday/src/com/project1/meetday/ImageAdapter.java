package com.project1.meetday;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.project1.meetday.ImageDownLoader.onImageLoaderListener;

public class ImageAdapter extends BaseAdapter implements OnScrollListener{
	/**
	 * 上下文對象的引用
	 */
	private Context context;
	
	/**
	 * Image Url的數組
	 */
	private String [] imageThumbUrls;
	
	/**
	 * GridView對象的應用
	 */
	private GridView mGridView;
	
	/**
	 * Image 下載器
	 */
	private ImageDownLoader mImageDownLoader;
	
	/**
	 * 記錄是否剛打開程序，用於解決進入程序不滾動屏幕，不會下載圖片的問題。
	 * 參考http://blog.csdn.net/guolin_blog/article/details/9526203#comments
	 */
	private boolean isFirstEnter = true;
	
	/**
	 * 一屏中第一個item的位置
	 */
	private int mFirstVisibleItem;
	
	/**
	 * 一屏中所有item的個數
	 */
	private int mVisibleItemCount;
	
	
	public ImageAdapter(Context context, GridView mGridView, String [] imageThumbUrls){
		this.context = context;
		this.mGridView = mGridView;
		this.imageThumbUrls = imageThumbUrls;
		mImageDownLoader = new ImageDownLoader(context);
		mGridView.setOnScrollListener(this);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//僅當GridView靜止時才去下載圖片，GridView滑動時取消所有正在下載的任務  
		if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
			showImage(mFirstVisibleItem, mVisibleItemCount);
		}else{
			cancelTask();
		}
		
	}


	/**
	 * GridView滾動的時候調用的方法，剛開始顯示GridView也會調用此方法
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 因此在這裡為首次進入程序開啟下載任務。 
		if(isFirstEnter && visibleItemCount > 0){
			showImage(mFirstVisibleItem, mVisibleItemCount);
			isFirstEnter = false;
		}
	}
	

	@Override
	public int getCount() {
		return imageThumbUrls.length;
	}

	@Override
	public Object getItem(int position) {
		return imageThumbUrls[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView mImageView;
		final String mImageUrl = imageThumbUrls[position];
		if(convertView == null){
			mImageView = new ImageView(context);
		}else{
			mImageView = (ImageView) convertView;
		}
		
		mImageView.setLayoutParams(new GridView.LayoutParams(150, 150));
		mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		
		//給ImageView設置Tag,這裡已經是司空見慣了
		mImageView.setTag(mImageUrl);
		
		
		/*******************************去掉下面這幾行試試是什麼效果****************************/
		Bitmap bitmap = mImageDownLoader.showCacheBitmap(mImageUrl.replaceAll("[^\\w]", ""));
		if(bitmap != null){
			mImageView.setImageBitmap(bitmap);
		}else{
			mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.person));
		}
		/**********************************************************************************/
		
		
		return mImageView;
	}
	
	/**
	 * 顯示當前屏幕的圖片，先會去查找LruCache，LruCache沒有就去sd卡或者手機目錄查找，在沒有就開啟線程去下載
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount){
		Bitmap bitmap = null;
		for(int i=firstVisibleItem; i<firstVisibleItem + visibleItemCount; i++){
			String mImageUrl = imageThumbUrls[i];
			final ImageView mImageView = (ImageView) mGridView.findViewWithTag(mImageUrl);
			bitmap = mImageDownLoader.downloadImage(mImageUrl, new onImageLoaderListener() {
				
				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if(mImageView != null && bitmap != null){
						mImageView.setImageBitmap(bitmap);
					}
					
				}
			});
			
			if(bitmap != null){
				mImageView.setImageBitmap(bitmap);
			}else{
				mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.person));
			}
		}
	}

	/**
	 * 取消下載任務
	 */
	public void cancelTask(){
		mImageDownLoader.cancelTask();
	}


}

