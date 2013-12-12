package com.github.baytransit;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.NickFirmani.baytransit.R;

public class PredictionAdapter extends BaseAdapter {
	private Context _context;
	private String _routeCode;
	private PredictionList _predList;
	private ArrayList<String> _lines = new ArrayList<String>();
	
	public PredictionAdapter(Context context, PredictionList predList, String routeCode) {
		_context = context;
		_predList = predList;
		_routeCode = routeCode;
		processLines(); //possibly put in asynctask
	}
	public void processLines() { 
		Log.d("PredAdapt", "ProcessingLines");
		ArrayList<String[]> fromList = _predList.getList();
		_lines.add((String) _context.getText(R.string.other_routes));
		int posRoute = 0;
		for (int k = 0; k < fromList.size(); k += 1) {
			String[] temp = fromList.get(k);
			if (temp[1].equals(_routeCode)) {
				_lines.add(posRoute, formatLine(temp));
				posRoute += 1;
				
			} else {
				_lines.add(formatLine(temp));
			}
		}
		if (posRoute == 0) {
			_lines.add(0, "No Predictons for this Route"); //FIXME
		}
		if (_lines.size() == 2) {
			_lines.add("No other routes either :(");
		}
	}
	@SuppressLint("DefaultLocale") //TODO
	private String formatLine(String[] inpData) {
		//takes seconds, routecode, routename , dirname
		int seconds = Integer.parseInt(inpData[0]);
		int minutes = seconds / 60;
		seconds = seconds - (minutes * 60);
		String str;
		if (seconds != 0) {
			str = String.format("%d:%02d", minutes, seconds); 
		} else {
			str = String.format("%d minutes", minutes);
		}
		return inpData[2] + " in " + str + " " + inpData[3];
	}
	@Override
	public int getCount() {
		return _lines.size();
	}

	@Override
	public Object getItem(int arg0) {
		return _lines.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView textView = new TextView(_context);
        textView.setTextSize(20);
        textView.setText(_lines.get(arg0));
        return textView;
	}

}
