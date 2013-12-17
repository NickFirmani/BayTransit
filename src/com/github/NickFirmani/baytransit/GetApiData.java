package com.github.NickFirmani.baytransit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class GetApiData extends AsyncTask<String, Integer, File> {
	private File retFile;

	@Override
	protected File doInBackground(String... urls) {
		try {
			retFile = new File(urls[1], urls[0].substring(urls[0].length()-15).replaceAll("[^a-zA-Z]",""));
            return downloadUrl(urls[0]);
        } catch (IOException e) {
        	Log.e("GetAPIData", e.getMessage());
        	return null;
        }
	}
	
	private File downloadUrl(String urlIn) throws IOException {
		InputStream instr = null;
		try {
			URL url = new URL(urlIn);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("GetAPIData URL Status", "The response is: " + response);
            instr = conn.getInputStream();
            //convert inputstream to file
            File contentTempFile = readIt(instr);
            return contentTempFile;
		} catch (MalformedURLException e) {
			Log.e("GetAPIData", "Malformed URL");
			return null;
		} finally {
			instr.close();
		}
	}
	
	private File readIt(InputStream instr) throws IOException {
		OutputStream outfile = null;
		try {
			outfile = new FileOutputStream(retFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = instr.read(bytes)) != -1) {
				outfile.write(bytes, 0, read);
			}
		} catch (FileNotFoundException e){
			Log.e("GetAPIData", "File not found");
		} finally {
			outfile.close();
		}
		return retFile;
	}
	
}
