package com.github.baytransit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RouteAdapter extends BaseAdapter {
	private Context _context;
	private Agency _agency;
	private ArrayList<String> _routes = new ArrayList<String>();
	public RouteAdapter(Context context, Agency agency) {
		_context = context;
		_agency = agency;
		Collection<Route> rou = _agency.getAllRoutes();
		for (Iterator<Route> rouIter = rou.iterator(); rouIter.hasNext();) {
			Route tempr = rouIter.next();
			_routes.add(tempr.getRouteName()); //TODO fix this data struct?
		}
	}

	@Override
	public int getCount() {
		return _routes.size();
	}

	@Override
	public Object getItem(int arg0) {
		return _agency.getRoute(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
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
