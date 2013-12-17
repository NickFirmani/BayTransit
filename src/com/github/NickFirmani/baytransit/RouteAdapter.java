package com.github.NickFirmani.baytransit;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RouteAdapter extends BaseAdapter {
	private Context _context;
	private Agency _agency;
	
	public RouteAdapter(Context context, Agency agency) {
		_context = context;
		_agency = agency;
	}

	@Override
	public int getCount() {
		return _agency.getNumberOfRoutes();
	}

	@Override
	public Object getItem(int arg0) {
		return _agency.getRoute(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView textView = new TextView(_context);
        textView.setTextSize(30);
        textView.setText(_agency.getRoute(arg0).getRouteName());
        return textView;
	}

}
