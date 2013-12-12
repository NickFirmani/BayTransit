package com.github.baytransit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StopAdapter extends BaseAdapter {
	private Context _context;
	private Route _route;
	private ArrayList<Stop> _stops = new ArrayList<Stop>();
	
	public StopAdapter(Context context, Route route) {
		_context = context;
		_route = route;
		Collection<Stop> _stopsColl = _route.listStops(1).values(); //used 1
		for (Iterator<Stop> stopIter = _stopsColl.iterator(); stopIter.hasNext();) {
			Stop tstop = stopIter.next();
			_stops.add(tstop);
		}
	}

	@Override
	public int getCount() {
		return _stops.size();
	}

	@Override
	public Object getItem(int arg0) {
		return _stops.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView textView = new TextView(_context);
        textView.setTextSize(30);
        textView.setText(_stops.get(arg0).getStopName());
        return textView;
	}

}
