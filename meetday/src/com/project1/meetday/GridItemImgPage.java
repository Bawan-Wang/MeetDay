package com.project1.meetday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridItemImgPage extends Activity {
	private GridView gridView;
	private int[] image = {
			R.drawable.thanksletter_hands, R.drawable.thanksletter_bro, R.drawable.thanksletter_hug, R.drawable.thanksletter_office
		};
	private int[] imgText = {
			R.string.sett_thanks_letter1, R.string.sett_thanks_letter2, R.string.sett_thanks_letter3, R.string.sett_thanks_letter4
	};
	private RelativeLayout relativeLayout;
	
	private ImageView previewImg, cancelBtn, chooseBtn;
	private int repIdx;
	
	private Intent intent;
	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.griditemimg_page);
		intent = new Intent();
		bundle = new Bundle();
		
		List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
	    for (int i = 0; i < image.length; i++) {
	        Map<String, Object> item = new HashMap<String, Object>();
	        item.put("image", image[i]);
	        item.put("text", getResources().getString(imgText[i]));
	        items.add(item);
	    }
	    SimpleAdapter adapter = new SimpleAdapter(this, 
	        items, R.layout.griditem, new String[]{"image", "text"},
	        new int[]{R.id.image, R.id.text});
	  
	    gridView = (GridView)findViewById(R.id.main_page_gridview);
	    gridView.setNumColumns(3);
	    gridView.setAdapter(adapter);
	    gridView.setOnItemClickListener(new GridView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				showImage(arg2);
			}
	         
	    });
	    
	    previewImg = (ImageView)findViewById(R.id.preview_imgview);
	    previewImg.setVisibility(View.INVISIBLE);
	    
	    relativeLayout = (RelativeLayout)findViewById(R.id.RelativeLayoutGrid);
	    relativeLayout.setVisibility(View.INVISIBLE);
	    
	    cancelBtn = (ImageView)findViewById(R.id.cancel_btn);
	    cancelBtn.setVisibility(View.INVISIBLE);
	    cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gridView.setVisibility(View.VISIBLE);
				previewImg.setVisibility(View.INVISIBLE);
				previewImg.setVisibility(View.INVISIBLE);
				cancelBtn.setVisibility(View.INVISIBLE);
				chooseBtn.setVisibility(View.INVISIBLE);
			}
	    	
	    });
	    
	    chooseBtn = (ImageView)findViewById(R.id.choose_btn);
	    chooseBtn.setVisibility(View.INVISIBLE);
	    chooseBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				bundle.putInt("ImgValue", repIdx);
			    intent.putExtras(bundle); //將key2放入Bundle
			    intent.setClass(GridItemImgPage.this, ReviewThanksPage.class);
			    GridItemImgPage.this.setResult(Activity.RESULT_OK, intent); //回傳RESULT_OK
			    GridItemImgPage.this.finish(); //關閉Activity
			}
	    	
	    });
	}
	
	public void showImage(int index) {
		gridView.setVisibility(View.INVISIBLE);
		previewImg.setBackgroundResource(image[index]);
		previewImg.setVisibility(View.VISIBLE);
		relativeLayout.setVisibility(View.VISIBLE);
		cancelBtn.setVisibility(View.VISIBLE);
	    chooseBtn.setVisibility(View.VISIBLE);
	    repIdx = index;
    }
}
