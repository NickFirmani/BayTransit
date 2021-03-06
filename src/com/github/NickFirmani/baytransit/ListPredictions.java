package com.github.NickFirmani.baytransit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.NickFirmani.baytransit.R;

public class ListPredictions extends Activity {
	ListView mListview;
	ProgressBar mProgbar;
	String agencyCode;
	String routeCode;
	String[] stopInfo; //stopcode, stopname
	int agencyStem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_predictions);
		mListview = (ListView) findViewById(R.id.pred_listview);
        mProgbar = (ProgressBar) findViewById(R.id.pred_progressbar);
		Intent mIntent = getIntent();
		stopInfo = mIntent.getStringArrayExtra("stopInfo");
		agencyCode = mIntent.getStringExtra("agencyName");
		routeCode = mIntent.getStringExtra("routeCode");
		agencyStem = (int) mIntent.getByteExtra("agencyStem", (byte) -1);
		setTitle(stopInfo[1]);
		if (agencyStem == 0) {
			String toFormat = "http://services.my511.org/Transit2.0/GetNextDeparturesByStopCode.aspx?token=7e3c99b6-1126-4e23-af9e-26cd5e5e0197&stopcode=%s";
			Log.d("Getting API Data", String.format(toFormat, stopInfo[0]));
			getApiData(String.format(toFormat, stopInfo[0]));
		} else if (agencyStem == 1) {
			String toFormat = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=%s&stopId=%s";
			Log.d("Getting API Data", String.format(toFormat, agencyCode, stopInfo[0]));
			getApiData(String.format(toFormat, agencyCode, stopInfo[0])); //TODO FIXME
		}
		// Show the Up button in the action bar.
        setupActionBar();
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
		FileReader inp = null;
		PredictionList predList = new PredictionList(stopInfo[0]);
    	try {
    		inp = new FileReader(fileIn);
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inp);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	Log.d("XPP", "Got here");
            	if (xpp.getEventType() == XmlPullParser.START_TAG && xpp.getName().equals("Route")) {
            		String routeCode;
                	String routeName;
                	String dirName = "";
                	String minutes;
            		routeName = xpp.getAttributeValue(null, "Name");
            		routeCode = xpp.getAttributeValue(null, "Code");
            		xpp.nextTag();
            		while(!xpp.getName().equals("Route")) {
            			
            			if (xpp.getEventType() == XmlPullParser.START_TAG && xpp.getName().equals("RouteDirection")) {
            				dirName = xpp.getAttributeValue(null, "Name");
            				
            			} else if (xpp.getEventType() == XmlPullParser.START_TAG && xpp.getName().equals("DepartureTime")) {
            				minutes = xpp.nextText();
            				predList.addPrediction(routeCode, routeName, dirName, minutes);
            				Log.d("XMLP", "AddPred: " + routeCode + " , " + routeName + " , " + dirName + " , " + minutes);
            			}
            			xpp.nextTag();
            			
            		}
           		}
            	eventType = xpp.next();
            	if (eventType == XmlPullParser.END_DOCUMENT) {
            		doOnFinishParse(0, predList);
            	}
            }
    	} catch (XmlPullParserException e) {
    		Log.e("ListRoutes", e.getMessage());
		} catch (FileNotFoundException e) {
			Log.e("ListRoutes", e.getMessage());
		} catch (IOException e) {
			Log.e("ListRoutes", e.getMessage());
		} 
    	finally {
    		if (inp != null) {
    			try {
					inp.close();
				} catch (IOException e) {
					Log.e("ListRoutes", e.getMessage());
				}
    		}
    	}
	}
	private void parseNbXml(File fileIn) {
		Log.d("ListPred", "ParsingNextBusXML");
		FileReader inp = null;
		PredictionList predList = new PredictionList(stopInfo[0]);
    	try {
    		inp = new FileReader(fileIn);
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inp);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	if (xpp.getEventType() == XmlPullParser.START_TAG && xpp.getName().equals("predictions")) {
            		String routeCode;
                	String routeName;
                	String dirName = "";
                	String minutes;
                	String seconds;
            		routeName = xpp.getAttributeValue(null, "routeTitle");
            		routeCode = xpp.getAttributeValue(null, "routeTag");
            		xpp.nextTag();
            		while(!xpp.getName().equals("predictions")) {
            			if (xpp.getEventType() == XmlPullParser.START_TAG && xpp.getName().equals("direction")) {
            				dirName = xpp.getAttributeValue(null, "title");
            			} else if (xpp.getEventType() == XmlPullParser.START_TAG && xpp.getName().equals("prediction")) {
            				seconds = xpp.getAttributeValue(null, "seconds");
            				minutes = xpp.getAttributeValue(null, "minutes");
            				predList.addPrediction(routeCode, routeName, dirName, minutes, seconds);
            				Log.d("XMLP", "AddPred: " + routeCode + " , " + routeName + " , " + dirName + " , " + minutes);
            			}
            			xpp.nextTag();
            		}
           		}
            	eventType = xpp.next();
            	if (eventType == XmlPullParser.END_DOCUMENT) {
            		doOnFinishParse(0, predList);
            	}
            }
    	} catch (XmlPullParserException e) {
    		Log.e("ListRoutes", e.getMessage());
		} catch (FileNotFoundException e) {
			Log.e("ListRoutes", e.getMessage());
		} catch (IOException e) {
			Log.e("ListRoutes", e.getMessage());
		} 
    	finally {
    		if (inp != null) {
    			try {
					inp.close();
				} catch (IOException e) {
					Log.e("ListRoutes", e.getMessage());
				}
    		}
    	}
    	doOnFinishParse(1, predList);
	}
	private void doOnFinishParse(int agencyStem, PredictionList predList) {
		final PredictionAdapter predictAdapt = new PredictionAdapter(this, predList, routeCode);
        mListview.setAdapter(predictAdapt);
        mProgbar.setVisibility(View.GONE);
        mListview.setVisibility(View.VISIBLE);
	}
	/* ------------------UI ------------------- */
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	//Uses main menu for now, can change later to a new item. called list_routes
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
/** TODO redo this class.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    } */
}
