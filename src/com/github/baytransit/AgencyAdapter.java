package com.github.baytransit;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.github.NickFirmani.baytransit.R;

public class AgencyAdapter extends BaseAdapter {
	private Context mContext;
	private Resources res;
	
	public AgencyAdapter(Context c) {
		mContext = c;
		res = mContext.getResources();
		buildarray();
	}
	
	public int getCount() {
		//find a better way to do this.
		if (objlist.size() != 0) {
			return objlist.size();
		} else {
			Log.e("AgencyAdapter", "Went to bad code.");
			return 13; 
		}
	}
	
	public Object getItem(int position) {
		return objlist.get(position);
	}
	
	public long getItemId(int position) {
		return 0;
	}
	/**
	 *  Method for displaying the gridview, uses objlist to do this.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8,8,8,8);
		} else {
			imageView = (ImageView) convertView;
		}
		//int represents first location of agency class
		if (position <= 2) {
			//FIXME EVENTUALLY
			imageView.setImageResource(R.drawable.sample_2);
			//DO STUFF FOR NOT AGENCIES
		} else {
			Agency targetagency = (Agency) objlist.get(position);
			imageView.setImageResource(targetagency.getImageid());
		}
		return imageView;
	}

	private SparseArray<Object> objlist = new SparseArray<Object>();
	//TODO make this an async task
	private void buildarray() {
		//Icon i0 = new icon("meh");
		//Icon i1 = new icon("bleh");
		//Icon i2 = new icon("bleh");
		Agency a3 = new Agency(res.getString(R.string.a3_c), R.drawable.actransit, res.getString(R.string.a3_p));
		Agency a4 = new Agency(res.getString(R.string.a4_c), R.drawable.bart, res.getString(R.string.a4_p));
		Agency a5 = new Agency(res.getString(R.string.a5_c), R.drawable.caltrain, res.getString(R.string.a5_p));
		Agency a6 = new Agency(res.getString(R.string.a6_c), R.drawable.db, res.getString(R.string.a6_p));
		Agency a7 = new Agency(res.getString(R.string.a7_c), R.drawable.emery, res.getString(R.string.a7_p));
		Agency a8 = new Agency(res.getString(R.string.a8_c), R.drawable.muni, res.getString(R.string.a8_p));
		Agency a9 = new Agency(res.getString(R.string.a9_c), R.drawable.samtrans, res.getString(R.string.a9_p));
		Agency a10 = new Agency(res.getString(R.string.a10_c), R.drawable.ucsf, res.getString(R.string.a10_p));
		Agency a11 = new Agency(res.getString(R.string.a11_c), R.drawable.vta, res.getString(R.string.a11_p));
		Agency a12 = new Agency(res.getString(R.string.a12_c), R.drawable.westcat, res.getString(R.string.a12_p));
		
		objlist.put(0, a10);//FIXME
		objlist.put(1, a10);//q
		objlist.put(2, a10);//q
		objlist.put(3, a3);
		objlist.put(4, a4);
		objlist.put(5, a5);
		objlist.put(6, a6);
		objlist.put(7, a7);
		objlist.put(8, a8);
		objlist.put(9, a9);
		objlist.put(10, a10);
		objlist.put(11, a11);
		objlist.put(12, a12);
	}

}
