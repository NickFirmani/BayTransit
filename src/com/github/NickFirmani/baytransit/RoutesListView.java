package com.github.NickFirmani.baytransit;

import android.content.Context;
import android.widget.ListView;

public class RoutesListView extends ListView {

	public RoutesListView(Context context) {
		super(context);
	}
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
    	super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    	//TODO
    }
}
