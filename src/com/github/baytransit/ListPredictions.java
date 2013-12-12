package com.github.baytransit;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.NickFirmani.baytransit.R;

public class ListPredictions extends Activity {
	ListView mListview;
	ProgressBar mProgbar;
	String agencyCode;
	String[] stopInfo; //stopcode, stopname
	int agencyStem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_predictions);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_stops);
		mListview = (ListView) findViewById(R.id.pred_listview);
        mProgbar = (ProgressBar) findViewById(R.id.pred_progressbar);
		Intent mIntent = getIntent();
		stopInfo = mIntent.getStringArrayExtra("stopInfo");
		agencyCode = mIntent.getStringExtra("agencyName");
		agencyStem = (int) mIntent.getByteExtra("agencyStem", (byte) -1);
		setTitle(stopInfo[1]);
		if (agencyStem == 0) {
			String toFormat = "http://services.my511.org/Transit2.0/GetNextDeparturesByStopCode.aspx?token=7e3c99b6-1126-4e23-af9e-26cd5e5e0197&stopcode=%s";
			getApiData(String.format(toFormat, stopInfo[0]));
		} else if (agencyStem == 1) {
			String toFormat = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=&s&stopId=&s";
			getApiData(String.format(toFormat, agencyCode, stopInfo[0]));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_predictions, menu);
		return true;
	}
	
	public void getApiData(String apiurl) {
		ConnectivityManager connMgr = (ConnectivityManager) 
    	getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    			class ApiListStops extends GetApiData {
    				protected void onPreExecute() {
    					//?
    				}
    				protected void onPostExecute(File toReturn) {
    					if (agencyStem == 0) {
    						parseFiveXml(toReturn);
    					} else if (agencyStem == 1) {
    						parseNbXml(toReturn);
    					}
    				}
    			}
				new ApiListStops().execute(apiurl, getFilesDir().getPath());
    	} else {
    	Toast.makeText(this, R.string.route_data_err, Toast.LENGTH_SHORT).show(); //FIXME
    	}
    }
	private void parseFiveXml(File fileIn) {
		Log.d("ListPred", "Parsing511XML");
		
	}
	private void parseNbXml(File fileIn) {
		Log.d("ListPred", "ParsingNextBusXML");
	}

}
