package com.github.NickFirmani.baytransit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.github.NickFirmani.baytransit.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//setup layout
		final GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new AgencyAdapter(this));
		gridview.setBackgroundColor(Color.parseColor("WHITE"));
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parents, View v, int position, long id) {
				getRoutes(gridview.getItemAtPosition(position));
			}
		});
	}
	//this gets the routes page for an agency
	private void getRoutes(Object obj) {
		if (obj instanceof Agency) {
			Intent intent = new Intent(this, ListRoutes.class);
			intent.putExtra("agency", (Agency) obj);
			startActivity(intent);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
