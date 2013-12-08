package com.github.baytransit;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.github.NickFirmani.baytransit.R;

public class ListStops extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_stops);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_stops, menu);
		return true;
	}

}
