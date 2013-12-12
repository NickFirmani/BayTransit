package com.github.baytransit;

import java.util.ArrayList;

import com.github.NickFirmani.baytransit.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PredictionAdapter extends BaseAdapter {
	private Context _context;
	private String _routeCode;
	private PredictionList _predList;
	private ArrayList<String> _lines = new ArrayList<String>();
	
	public PredictionAdapter(Context context, PredictionList predList, String routeCode) {
		_context = context;
		_predList = predList;
		_routeCode = routeCode;
	}
	public void processLines() {
		ArrayList<String[]> fromList = _predList.getList();
		int posRoute = 0;
		for (int k = 0; k < fromList.size(); k += 1) {
			String[] temp = fromList.get(k);
			if (temp[1].equals(_routeCode)) {
				_lines.add(posRoute, formatLine(temp));
			} else if (k == 0) {
				_lines.add((String) _context.getText(R.string.other_routes));
				
			} else {
				_lines.add(formatLine(temp));
			}
		}
	}
	@SuppressLint("DefaultLocale") //TODO
	private String formatLine(String[] inpData) {
		//takes seconds, route, dirtag, dirname
		int seconds = Integer.parseInt(inpData[0]);
		int minutes = seconds % 60;
		String str = String.format("%d:%02d", minutes, seconds); 
		return inpData[1] + " in " + str + " " + inpData[3];
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
        textView.setTextSize(30);
        textView.setText(_lines.get(arg0));
        return textView;
	}

}
