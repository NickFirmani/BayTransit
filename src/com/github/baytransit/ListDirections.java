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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.NickFirmani.baytransit.R;

public class ListDirections extends Activity {
	private Agency agency;
	private Route route;
	private File agXmlFile;
	private String[] dirDispNames = {"this is", "hidden"};
	private File stopXmlFile;
	ProgressBar progbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_directions);
		progbar = (ProgressBar) findViewById(R.id.listdir_progressbar);
		Intent intent = getIntent();
        agency = intent.getParcelableExtra("agency");
        //TODO change xml download implementation to background svc
        
        
        route = intent.getParcelableExtra("route");
        if (agency.gethasDir() == false) {
        	Log.d("ListDirections", "Skipped this activity, has no directions");
        	nextActivity(-1);
        }
        agXmlFile = (File) intent.getSerializableExtra("agXML");
        setTitle(route.getRouteName());
        int apistem = agency.getAPIstem();
        if (apistem == 0) {
        	showFiveOneOneDirs(agXmlFile);
        	doOnFinish();    	
        } else if (apistem == 1) {
        	String temp = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=%s&r=%s"; //FIXME
        	String apiurl = String.format(temp, agency.getNameCode(), route.getRouteNameCode());
        	stopXmlFile = new File(getFilesDir(), apiurl.substring(apiurl.length()-15).replaceAll("[^a-zA-Z]",""));
        	getRoutesXML(apiurl);
        }
	}
	
	private void doOnFinish() {
		if (agency.getAPIstem() == 1) {
			showNextBusDirs(stopXmlFile);
		}
		final TextView dir1view = (TextView) findViewById(R.id.direction1);
        final TextView dir2view = (TextView) findViewById(R.id.direction2);
        dir1view.setText(dirDispNames[0]);
        dir2view.setText(dirDispNames[1]);
        progbar.setVisibility(View.GONE);
        dir1view.setVisibility(View.VISIBLE);
        dir2view.setVisibility(View.VISIBLE);
        dir1view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nextActivity(0);
			}
		});
        dir2view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nextActivity(1);
			}
		});
	}
	
	private void showFiveOneOneDirs(File agXml) {
		FileReader inp = null;
    	try {
    		inp = new FileReader(agXml);
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inp);
            int eventType = xpp.getEventType();
            String[] dirnames = {"should not", "see this"};
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	String name = xpp.getName();
            	if (xpp.getEventType() == XmlPullParser.START_TAG &&
                		agency.getAPIstem() == 0) {
            		if (name != null && name.equals("Route") && 
            				xpp.getAttributeValue(null, "Code").equals(route.getRouteNameCode())) {
            			xpp.nextTag();
            			xpp.nextTag();
            			dirnames[0] = xpp.getAttributeValue(null, "Code");
            			dirDispNames[0] = xpp.getAttributeValue(null, "Name");
            			xpp.nextTag();
            			xpp.nextTag();
            			dirnames[1] = xpp.getAttributeValue(null, "Code");
            			dirDispNames[1] = xpp.getAttributeValue(null, "Name");
            		}
             	}
               	eventType = xpp.next();
            }
            RouteFiveOneOne tr = (RouteFiveOneOne) route;
            tr.setDirNames(dirnames);
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
	
	private void showNextBusDirs(File rouXml) { //TODO merge these
		FileReader inp = null;
    	try {
    		inp = new FileReader(rouXml);
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inp);
            int eventType = xpp.getEventType();
            int dircount = 0;
            String[] dirnames = {"this is", "invisible"};
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	String name = xpp.getName();
            	if (xpp.getEventType() == XmlPullParser.START_TAG) {
                	if (name != null && name.equals("direction")) {
                		dirnames[dircount] = xpp.getAttributeValue(null, "tag");
                		Log.d("Direction tag", xpp.getAttributeValue(null, "tag"));
                		dirDispNames[dircount] = xpp.getAttributeValue(null, "title");
                		Log.d("Direction titlex", xpp.getAttributeValue(null, "title"));
               			dircount += 1; //TODO Plan for more than 2 directions (WTF)
               		}
             	}
               	eventType = xpp.next();
            }
            RouteNextBus tr = (RouteNextBus) route;
            tr.setDirNames(dirnames);
            
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_directions, menu);
		return true;
	}
	
	public void getRoutesXML(String apiurl) {
		ConnectivityManager connMgr = (ConnectivityManager) 
    	getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    			class ApiListStops extends GetApiData {
    				protected void onPreExecute() {
    					if (agency.getAPIstem() == 1) {
    						progbar.setVisibility(View.VISIBLE);
    					}
    				}
    				protected void onPostExecute(File toReturn) {
    					stopXmlFile = toReturn;
    					doOnFinish();
    				}
    			}
				new ApiListStops().execute(apiurl, getFilesDir().getPath());
    	} else {
    	Toast.makeText(this, R.string.route_data_err, Toast.LENGTH_SHORT).show();
    	}
    }
	
	private void nextActivity(int n) {
		Intent dirIntent = new Intent(this, ListStops.class);
		dirIntent.putExtra("agency", agency);
		dirIntent.putExtra("route", route);
		//if (stopXmlFile != null && stopXmlFile.length() > 0) {
			//dirIntent.putExtra("routeXML", stopXmlFile);
		//}
		if (n >= 0) {
			dirIntent.putExtra("dir", route.getDirNames(n)); 
		}
		startActivity(dirIntent);
	}
	
	
}



