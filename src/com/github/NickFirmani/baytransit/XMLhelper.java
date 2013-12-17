package com.github.NickFirmani.baytransit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.util.Log;

public abstract class XMLhelper extends AsyncTask<File, Integer, Void> {

	@Override
	protected Void doInBackground(File... arg0) {
		FileReader inp = null;
    	try {
    		inp = new FileReader(arg0[0]);
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inp);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	doForEachTag(xpp);
               	eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
    		Log.e("XMLparser", e.getMessage());
   		} catch (FileNotFoundException e) {
   			Log.e("XMLparser", e.getMessage());
   		} catch (IOException e) {
   			Log.e("XMLparser", e.getMessage());
   		} 
       	finally {
        	if (inp != null) {
       			try {
    				inp.close();
   				} catch (IOException e) {
   					Log.e("XMLparser", e.getMessage());
   				}
      		}
        }
		return null;
	}
	
	abstract void doForEachTag(XmlPullParser xpp); //Should be an iterative statement executed for each tag.
	
	abstract void onPostExecute();

}
