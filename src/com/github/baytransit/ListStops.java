package com.github.baytransit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.github.NickFirmani.baytransit.R;


public class ListStops extends Activity {
	Agency mAgency;
	Route mRoute;
	String mDirCode;
	File routeXml;
	File stopsXml;
	ListView mListview;
	ProgressBar mProgbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Created", "create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_stops);
		mListview = (ListView) findViewById(R.id.stops_listview);
        mProgbar = (ProgressBar) findViewById(R.id.stops_progressbar);
        
		Intent mIntent = getIntent();
        mAgency = mIntent.getParcelableExtra("agency");
		mRoute = mIntent.getParcelableExtra("route");
		int tempint = mIntent.getIntExtra("dirstem", -1);
		if (tempint >= 0) {mRoute.getDirNames(tempint);}
		
		if (mIntent.hasExtra("routeXml")) { //for nextbus
			routeXml = (File) mIntent.getSerializableExtra("routeXml");
			Log.d("apifile", "yes");
			parseNbXml(routeXml);
			doOnFinish();
		} else { //for 511 //FIXME
			String temp = "http://services.my511.org/Transit2.0/GetStopsForRoute.aspx?token=7e3c99b6-1126-4e23-af9e-26cd5e5e0197&routeIDF=%s~%s~%s";
			String apiurl = String.format(temp, mAgency.getNameCode(), mRoute.getRouteNameCode(), mDirCode);
			Log.d("apiurl", apiurl);
			getRoutesXML(apiurl);
			doOnFinish();
		}
	}
	
	public void getRoutesXML(String apiurl) {
		ConnectivityManager connMgr = (ConnectivityManager) 
    	getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    			class ApiListStops extends GetApiData {
    				protected void onPreExecute() {
    					//?
    				}
    				protected void onPostExecute(File toReturn) {
    					routeXml = toReturn;
    					parseFiveXml(routeXml);
    				}
    			}
				new ApiListStops().execute(apiurl, getFilesDir().getPath());
    	} else {
    	Toast.makeText(this, R.string.route_data_err, Toast.LENGTH_SHORT).show(); //FIXME
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_stops, menu);
		return true;
	}
	
	private void parseNbXml(File filein) {}
	
	private void parseFiveXml(File filein) {
		FileReader inp = null;
    	try {
    		inp = new FileReader(filein);
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inp);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	String name = xpp.getName();
            	if (xpp.getEventType() == XmlPullParser.START_TAG) {
            		if (name != null && name.equals("Stop")) {
            			String dispName = xpp.getAttributeValue(null, "name");
            			String stopCode = xpp.getAttributeValue(null, "StopCode");
            			Log.d("ListStops", "dn: " + dispName + " sC: " + stopCode);
            			StopFiveOneOne stop = new StopFiveOneOne(stopCode, dispName);
            			mRoute.addStop(stopCode, stop);
            		}
             	}
               	eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
    		Log.e("ListDirections", e.getMessage());
   		} catch (FileNotFoundException e) {
   			Log.e("ListDirections", e.getMessage());
   		} catch (IOException e) {
   			Log.e("ListDirections", e.getMessage());
   		} 
       	finally {
        	if (inp != null) {
       			try {
    				inp.close();
   				} catch (IOException e) {
   					Log.e("ListDirections", e.getMessage());
   				}
      		}
        }
	}
	private void doOnFinish() {
		final StopAdapter stoplist = new StopAdapter(this, mRoute);
        mListview.setAdapter(stoplist);
        mProgbar.setVisibility(View.GONE);
        mListview.setVisibility(View.VISIBLE);
        mListview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parents, View v, int position, long id) {
				onRouteClick(mListview.getItemAtPosition(position));
			}
		});
	}
	private void onRouteClick(Object routein) {}

}
