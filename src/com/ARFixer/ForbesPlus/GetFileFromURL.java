package com.ARFixer.ForbesPlus;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

// usually, subclasses of AsyncTask are declared inside the activity class.
//that way, you can easily modify the UI thread from here
 class GetFileFromURL extends AsyncTask<String, Integer, String> {
 @Override
	 protected String doInBackground(String... sUrl) {
	     try {
	    	 //test();
	         URL url = new URL(sUrl[0]);
	         URLConnection connection = url.openConnection();
	         connection.connect();
	         // this will be useful so that you can show a typical 0-100% progress bar
	         int fileLength = connection.getContentLength();
	
	         // download the file
	         InputStream input = new BufferedInputStream(url.openStream());
	         
	         //create dir
	       	File sdCard = Environment.getExternalStorageDirectory();
	       	File dir = new File (sdCard.getAbsolutePath() + "/forbes");
			dir.mkdirs();
	
			Log.i("url path:", url.getPath());
			String fileName = url.getPath().substring( url.getPath().lastIndexOf('/')+1, url.getPath().length() );
			Log.i("url fileName:", fileName);
			
	         
	         File yourFile = new File(dir, fileName);
	         if(!yourFile.exists()) {
	        	 Log.i("creating new file...",yourFile.getAbsolutePath());
	             yourFile.createNewFile();
	             if(yourFile.exists()) {
		        	 Log.i("created new file...", "created new file..."); 
		        	 
		        	 //start savinbg
		        	 OutputStream output = new FileOutputStream(yourFile);
		        		
			         byte data[] = new byte[1024];
			         long total = 0;
			         int count;
			         Log.i("onPreExecute", "file size to downlo<============================="+Integer.toString(fileLength) );
			         while ((count = input.read(data)) != -1) {
			             total += count;
			             // publishing the progress....
			             
			             publishProgress((int) (total * 100 / fileLength));
			             output.write(data, 0, count);
			         }
			
			         output.flush();
			         output.close();
			         input.close();
		         }
	         } 
	         else{
	        	 Log.i("file exit...", "file exit...");
	         }
	         
	         
	     } catch (Exception e) {

		     Log.e("Exception", e.getMessage() );
	     }
	     return null;
	 }
 
 
	 @Override
	 protected void onPreExecute() {
	     super.onPreExecute();
	     Log.i("onPreExecute", "onPreExecute<=============================");
	 }
	
	 @Override
	 protected void onProgressUpdate(Integer... progress) {
	     super.onProgressUpdate(progress);
	     //Log.i("onPreExecute", "onProgressUpdate<============================="+Integer.toString(progress[0]) );
	 }
	 
 
 }
 
 
 
