package com.github.baytransit;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.NickFirmani.baytransit.R;

public class ListRoutes extends Activity {
	
	private Agency agency;
	private String apiurl;
	protected String filename;
	private File agXmlFile;
	ListView listview;
	ProgressBar progbar;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_routes);
        Intent intent = getIntent();
        agency = intent.getParcelableExtra("agency");
        setTitle(agency.getDisplayName());
        
        listview = (ListView) findViewById(R.id.listview);
        progbar = (ProgressBar) findViewById(R.id.progressbar);
        
        makeApiUrl();
		agXmlFile = new File(getFilesDir(), filename);
		
        if (isxmlOld()) {
        	Log.d("XML Status", "Stale");
        	getRoutes();
        } else {
        	Log.d("XML Status", "Fresh");
        	doOnPostExecute();
        }

        // Show the Up button in the action bar.
        setupActionBar();
    }
    
    private void parseAgencyXML(File agencyxmlloc) {
    	//takes xml file and adds it to agency info.
    	//agency.getapistem() = 0 is for nextbus
    	// 1 for 511
    	FileReader inp = null;
    	try {
    		inp = new FileReader(agencyxmlloc);
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inp);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	String name = xpp.getName();
            	if (xpp.getEventType() == XmlPullParser.END_TAG &&
            			agency.getAPIstem() == 1) {
            		if (name != null && name.equals("route")) {
            			String routecode = xpp.getAttributeValue(null, "tag");
            			String routetitle = xpp.getAttributeValue(null, "title"); 
            			agency.addRoute(routecode, new RouteNextBus(routecode, routetitle));
            		}
            	} else if (xpp.getEventType() == XmlPullParser.START_TAG &&
            			agency.getAPIstem() == 0) {
            		if (name != null && name.equals("Route")) {
           				String routetitle = xpp.getAttributeValue(null, "Name");
           				String routecode = xpp.getAttributeValue(null, "Code");
           				RouteFiveOneOne rc = new RouteFiveOneOne(routecode, routetitle);
           				agency.addRoute(routecode, rc);
           			}
           		}
            	eventType = xpp.next();
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

	private boolean isxmlOld() {
    	//return false if XML file is there and fresh
    		long created = agXmlFile.lastModified();
    	if (created != 0) {
    		long currtime = System.currentTimeMillis();
    		long allowable = Long.valueOf(R.string.max_route_age_millis);
    		return !(currtime - created < allowable);
    	} else {
    		return true;
    	}
    }
	
    private void makeApiUrl() {
    	int val = agency.getAPIstem();
    	String api0 = val == 0 ? getString(R.string.agency_api_stem_0) :
    		getString(R.string.agency_api_stem_1);
    	String api1 = agency.getNameCode();
    	apiurl = api0 + api1;
    	filename = apiurl.substring(apiurl.length()-15).replaceAll("[^a-zA-Z]","");
    
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
    }

    public void onRouteClick(Object object) {
    	if (object instanceof Route) {
    		Intent routeIntent = new Intent(this, ListDirections.class);
    		routeIntent.putExtra("agency", agency);
    		routeIntent.putExtra("route", (Route) object);
    		routeIntent.putExtra("agXML", agXmlFile);
    		startActivity(routeIntent);
    	}

    }
    
    private void doOnPostExecute() {
    	parseAgencyXML(agXmlFile);
        final RouteAdapter routelist = new RouteAdapter(this, agency);
        listview.setAdapter(routelist);
        progbar.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);
        listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parents, View v, int position, long id) {
				onRouteClick(listview.getItemAtPosition(position));
			}
		});
    }
    
    /* ------------------Dataget ------------------- */
    
    public void getRoutes() {
		ConnectivityManager connMgr = (ConnectivityManager) 
    	getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    			class ApiListRoutes extends GetApiData {
    				protected void onPreExecute() {
    					progbar.setVisibility(View.VISIBLE);
    				}
    				protected void onPostExecute(File toReturn) {
    					agXmlFile = toReturn;
    					doOnPostExecute();
    				}
    			}
				new ApiListRoutes().execute(apiurl, getFilesDir().getPath());
    	} else {
    	Toast.makeText(this, R.string.route_data_err, Toast.LENGTH_SHORT).show();
    	}
    }
}