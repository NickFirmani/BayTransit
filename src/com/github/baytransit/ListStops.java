package com.github.baytransit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_stops);
		mListview = (ListView) findViewById(R.id.stops_listview);
        mProgbar = (ProgressBar) findViewById(R.id.stops_progressbar);
		Intent mIntent = getIntent();
        mAgency = mIntent.getParcelableExtra("agency");
		mRoute = mIntent.getParcelableExtra("route");
		if (mIntent.hasExtra("dir")) {
			mDirCode = mIntent.getStringExtra("dir");
			setTitle(mRoute.getRouteName() + ": " + mDirCode); //FIXME
			mDirCode = mDirCode.replaceAll(" ", "%20");	
			Log.d("liststops", "mdircode is: " + mDirCode);
			if (mAgency.getAPIstem() == 1) { //for nextbus
				String temp = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=%s&r=%s"; //FIXME
				String apiurl = String.format(temp, mAgency.getNameCode(), mRoute.getRouteNameCode());
				routeXml = new File(getFilesDir(), apiurl.substring(apiurl.length()-15).replaceAll("[^a-zA-Z]",""));
				Log.d("apiurl", apiurl);
				parseNbXml(routeXml); //should move outside ui thread FIXME
				doOnFinish();

			} else if (mAgency.getAPIstem() == 0 && mDirCode != null) { //for 511 //FIXME
				String temp = "http://services.my511.org/Transit2.0/GetStopsForRoute.aspx?token=7e3c99b6-1126-4e23-af9e-26cd5e5e0197&routeIDF=%s~%s~%s";
				String apiurl = String.format(temp, mAgency.getNameCode(), mRoute.getRouteNameCode(), mDirCode);
				Log.d("apiurl", apiurl);
				getRoutesXML(apiurl);
			} 
		} else {
			String temp = "http://services.my511.org/Transit2.0/GetStopsForRoute.aspx?token=7e3c99b6-1126-4e23-af9e-26cd5e5e0197&routeIDF=%s~%s";
			String apiurl = String.format(temp, mAgency.getNameCode(), mRoute.getRouteNameCode());
			Log.d("apiBARTurl", apiurl);
			getRoutesXML(apiurl);
			//FIXME BACK BUTTON
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
    					doOnFinish();
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
	
	private void parseNbXml(File filein) {
		Map<String, String[]> stopsByTag = new HashMap<String, String[]>();
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
            	if (xpp.getEventType() == XmlPullParser.START_TAG || xpp.getEventType() == XmlPullParser.END_TAG) {

            		//add to stopsbytag array
            		if (name != null && name.equals("route") && xpp.getEventType() == XmlPullParser.START_TAG) {
            			Log.d("XPP", "0 Name is: " + name + " at line: " + xpp.getLineNumber());
            			xpp.nextTag();
            			Log.d("XPP", "1 Name is: " + name + " at line: " + xpp.getLineNumber());
            			while (!xpp.getName().equals("direction")) {
            				
            				String tTag = xpp.getAttributeValue(null, "tag");
            				String tTitle = xpp.getAttributeValue(null, "title");
            				String tLat = xpp.getAttributeValue(null, "lat");
            				String tLon = xpp.getAttributeValue(null, "lon");
            				String tStopId = xpp.getAttributeValue(null, "stopId");
            				String[] tArr = {tTitle, tLat, tLon, tStopId};
            				stopsByTag.put(tTag, tArr);
            				xpp.nextTag();
            				Log.d("XPP", "2 Name is: " + name + " at line: " + xpp.getLineNumber());
            			}
            		}
            		
            		//add stop to route
            		else if (name != null && name.equals("direction") 
            				&& xpp.getEventType() == XmlPullParser.START_TAG
            				&& xpp.getAttributeValue(null, "tag").equals(mDirCode)) {
            			xpp.nextTag();
            			Log.d("XPP", "3 Name is: " + name + " at line: " + xpp.getLineNumber());
            			while (!xpp.getName().equals("direction")) {
            				
            				String tTagGet = xpp.getAttributeValue(null, "tag");
            				String[] tRes = stopsByTag.get(tTagGet);
            				double tLat = Double.parseDouble(tRes[1]);
            				double tLon = Double.parseDouble(tRes[2]);
            				StopNextBus tNbStop = new StopNextBus(tRes[3], tRes[0], tLat, tLon);
            				mRoute.addStop(tRes[3], tNbStop);
            				xpp.nextTag();
            				Log.d("XPP", "4 Name is: " + name + " at line: " + xpp.getLineNumber());
            			}
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
		Log.d("doOnFinish", "Finishing");
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
	private void onRouteClick(Object stopin) {
		Stop tempStop = (Stop) stopin;
		String[] intentStringArr = {tempStop.getStopCode(), tempStop.getStopName()}; //TODO implement parcelable
		String agencyName = mAgency.getNameCode();
		Intent outIntent = new Intent(this, ListPredictions.class);
		outIntent.putExtra("stopInfo", intentStringArr);
		outIntent.putExtra("agencyName", agencyName);
		Byte outB = (byte) (mAgency.getAPIstem() == 0 ? 0 : 1);
		outIntent.putExtra("agencyStem", outB);
		startActivity(outIntent);
	}

}
