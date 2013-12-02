package com.github.baytransit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class ListDirections extends Activity {
	private Agency agency;
	private Route route;
	private File agXmlFile;
	private String[] dirDispNames = {"this is", "hidden"};
	private File stopXmlFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_directions);
		Intent intent = getIntent();
        agency = intent.getParcelableExtra("agency");
        //download xml in bg
        if (agency.gethasDir() == false) {
        	Toast.makeText(this, "this agency has no direction", Toast.LENGTH_SHORT).show();
        	//next activity.
        }
        route = intent.getParcelableExtra("route");
        agXmlFile = (File) intent.getSerializableExtra("agXML");
        setTitle(agency.getDisplayName());
        int apistem = agency.getAPIstem();
        if (apistem == 0) {
        	showFiveOneOneDirs(agXmlFile);
        } else if (apistem == 1) {
        	Toast.makeText(this, "im on a plane.", Toast.LENGTH_SHORT).show();
        	dirDispNames[0] = "lol";
        	dirDispNames[1] = "lolol";
        	//wait for bg task
        	//showNextBusDirs(stopXmlFile);
        }
        //set string names appropriately. 
        final TextView dir1view = (TextView) findViewById(R.id.direction1);
        final TextView dir2view = (TextView) findViewById(R.id.direction2);
        dir1view.setText(dirDispNames[0]);
        dir2view.setText(dirDispNames[1]);
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
            			xpp.getName();
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
            String[] dirnames = {};
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	String name = xpp.getName();
            	if (xpp.getEventType() == XmlPullParser.END_TAG &&
            			agency.getAPIstem() == 1) {
                	if (name != null && name.equals("direction")) {
                		dirnames[dircount] = xpp.getAttributeValue(null, "tag");
                		dirDispNames[dircount] = xpp.getAttributeValue(null, "title");
                		if (xpp.getAttributeValue(null, "useForUI").equals("false")) {
                			Log.e("Stop511", "Shouldn't use this for UI?");
                		}
               			dircount += 1;
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

}
