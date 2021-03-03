package com.project1.meetday;

import java.util.ArrayList;
import java.util.HashMap;

import com.project1.http.ImageLoader;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MySimpleAdapterSetting extends SimpleAdapter {

    private ArrayList<HashMap<String, Object>> results;
    private Context mContext;
    private ImageLoader mImageLoader;
    
    public MySimpleAdapterSetting(Context context, ArrayList<HashMap<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.results = data;
        this.mContext = context;
        mImageLoader = new ImageLoader(context);
    }
    
    public ImageLoader getImageLoader(){
		return mImageLoader;
	}

    public View getView(int position, View view, ViewGroup parent){

        Typeface localTypeface1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/BELLB.TTF");
        if (view == null) {
        	view = LayoutInflater.from(mContext).inflate(
					R.layout.settinglist, null);
        }
        ImageView tt = (ImageView) view.findViewById(R.id.settingItemIcon);
        String url = results.get(position).get("pic").toString();
        tt.setImageResource(Integer.valueOf(url));       
        
        TextView bt = (TextView) view.findViewById(R.id.settingItemText);
        bt.setText(results.get(position).get("string").toString());
        bt.setTypeface(localTypeface1);
        return view;
    }
}