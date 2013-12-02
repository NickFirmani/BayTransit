package com.github.baytransit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListRoutes extends Activity {
	
	private Agency agency;
	private String apiurl;
	protected String fileprefix;
	private File agXmlFile;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_routes);
        Intent intent = getIntent();
        agency = intent.getParcelableExtra("agency");
        setTitle(agency.getDisplayName());
        
        makeApiUrl();
		agXmlFile = new File(getFilesDir(), fileprefix);
        if (isxmlOld()) {agXmlFile = getRoutes();}
        parseAgencyXML(agXmlFile);
		
        final ListView listview = (ListView) findViewById(R.id.listview);
        final RouteAdapter routelist = new RouteAdapter(this, agency);
        listview.setAdapter(routelist);
        listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parents, View v, int position, long id) {
				onRouteClick(listview.getItemAtPosition(position));
			}
		});
        
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
    		return currtime - created < allowable ? false : true;
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
    	String f0 = val == 0 ? "nb" : "51";
    	fileprefix = f0 + api1;
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
    
    /* ------------------Dataget ------------------- */
    
    public File getRoutes() {
    	File retfile = null;
		ConnectivityManager connMgr = (ConnectivityManager) 
    	getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    		try {
				retfile = new QueryAgencyAPI().execute(apiurl).get(); //dont do this. TODO
			} catch (InterruptedException e) {
				Log.e("ListRoutes/getRoutes", e.getMessage());
			} catch (ExecutionException e) {
				Log.e("ListRoutes/getRoutes", e.getMessage());
			}
    	} else {
    	Toast.makeText(this, R.string.route_data_err, Toast.LENGTH_SHORT).show();
    	}
    	return retfile;
    }
    
    private class QueryAgencyAPI extends AsyncTask<String, Void, File> {
    	@Override
    	protected File doInBackground(String ... urls) {
    		// params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
            	Log.e("ListRoutes/background", e.getMessage());
            	return null;
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(File result) {
            //DO
        }
        private File downloadUrl(String urlq) throws IOException {
        	InputStream is = null;
        	try {
        		URL url = new URL(urlq);
        		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("GetRoutes URL Status", "The response is: " + response);
                is = conn.getInputStream();
                //convert inputstream to file
                File contentTempFile = readIt(is, fileprefix);
                return contentTempFile;
        	} finally {
        		if (is != null) {
        			is.close();
        		}
        	}
        }
        private File readIt(InputStream inp, String fileprefix) throws IOException {
        	try {
                FileOutputStream out = new FileOutputStream(agXmlFile);
                try { //replace this with buffered implementation TODO
                	while (true) {
                		int tmp = inp.read();
                		if (tmp == -1) {
                			break;
                		} else {
                			out.write(tmp);
                		}
                	}
                } finally { // close out
                	out.close();
                }
            	return agXmlFile;
        	} finally {
        		inp.close();
        	}
        }
    }
}
