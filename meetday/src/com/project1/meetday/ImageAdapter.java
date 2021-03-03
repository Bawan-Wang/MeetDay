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
	 * �W�U���H���ޥ�
	 */
	private Context context;
	
	/**
	 * Image Url���Ʋ�
	 */
	private String [] imageThumbUrls;
	
	/**
	 * GridView��H������
	 */
	private GridView mGridView;
	
	/**
	 * Image �U����
	 */
	private ImageDownLoader mImageDownLoader;
	
	/**
	 * �O���O�_�襴�}�{�ǡA�Ω�ѨM�i�J�{�Ǥ��u�ʫ̹��A���|�U���Ϥ������D�C
	 * �Ѧ�http://blog.csdn.net/guolin_blog/article/details/9526203#comments
	 */
	private boolean isFirstEnter = true;
	
	/**
	 * �@�̤��Ĥ@��item����m
	 */
	private int mFirstVisibleItem;
	
	/**
	 * �@�̤��Ҧ�item���Ӽ�
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
		//�ȷ�GridView�R��ɤ~�h�U���Ϥ��AGridView�ưʮɨ����Ҧ����b�U��������  
		if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
			showImage(mFirstVisibleItem, mVisibleItemCount);
		}else{
			cancelTask();
		}
		
	}


	/**
	 * GridView�u�ʪ��ɭԽեΪ���k�A��}�l���GridView�]�|�եΦ���k
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// �]���b�o�̬������i�J�{�Ƕ}�ҤU�����ȡC 
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
		
		//��ImageView�]�mTag,�o�̤w�g�O�q�Ũ��D�F
		mImageView.setTag(mImageUrl);
		
		
		/*******************************�h���U���o�X��ոլO����ĪG****************************/
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
	 * ��ܷ�e�̹����Ϥ��A���|�h�d��LruCache�ALruCache�S���N�hsd�d�Ϊ̤���ؿ��d��A�b�S���N�}�ҽu�{�h�U��
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
	 * �����U������
	 */
	public void cancelTask(){
		mImageDownLoader.cancelTask();
	}


}

