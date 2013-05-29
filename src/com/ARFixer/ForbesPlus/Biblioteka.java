package com.ARFixer.ForbesPlus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Biblioteka {
	 /**
     * @param text
     * @param duration Toast.LENGTH_SHORT
     */
    public static void generuj_toast( String text, int duration, Context context ){
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.setDuration(300);
    	toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 300);
    	toast.show();
    }
	
    public static void generuj_alert( String title, String msg, VideoPlayback tablet_dambonia ){
    	AlertDialog alertDialog = new AlertDialog.Builder( tablet_dambonia ).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            return;
        } }); 
        alertDialog.show();
    }
    
    public static JSONObject http_post_json( String url, ArrayList<NameValuePair> postVals ){
		try{
			InputStream is = Biblioteka.http_post_return(url, postVals);
		    String result = convert_response_to_string( is );
		    JSONObject json_data = new JSONObject(result);
			return json_data;
		}
		catch(Exception e){
	    	Log.e("log_tag", "Error in http connection "+e.toString());
	    }
		return null;
	}
    
    public static String http_post_json_get_value( String url, ArrayList<NameValuePair> postVals ){
		try{
		    InputStream is = Biblioteka.http_post_return(url, postVals);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"ISO-8859-1"),8);
		    String result = reader.readLine();
		    is.close();
			return result;
		}
		catch(Exception e){
	    	Log.e("log_tag", "Error in http connection "+e.toString());
	    }
		return null;
	}
    
    
    //OK
    public static InputStream http_post_return( String url, ArrayList<NameValuePair> postVals ){
		try{
		    HttpResponse response = Biblioteka.http_post(url, postVals);
		    HttpEntity entity = response.getEntity();
		    InputStream is = entity.getContent();
			return is;
		}
		catch(Exception e){
	    	Log.e("log_tag", "Error in http connection "+e.toString());
	    }
		return null;
	}
    
    
    //OK
    public static HttpResponse http_post( String url, ArrayList<NameValuePair> postVals ){
		try{
			HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(url);
		    httppost.setEntity(new UrlEncodedFormEntity(postVals));
		    HttpResponse response = httpclient.execute(httppost);
		    return response;
		}
		catch(Exception e){
	    	Log.e("log_tag", "Error in http connection "+e.toString());
	    }
		return null;
	}
    
    
    
    
    static String convert_response_to_string(InputStream is  ){
    	String result = "";
    	//convert response to string
	      try{
	    	  Log.i( "convert_response_to_string", "= convert_response_to_string : start" );
			   BufferedReader reader = new BufferedReader(new InputStreamReader(is,"ISO-8859-1"),8);
			   Log.i( "convert_response_to_string", "---------------- convert_response_to_string : step 1" );
	    	   StringBuilder sb = new StringBuilder();
			   Log.i( "convert_response_to_string", "---------------- convert_response_to_string : step 2" );
	    	   String line = null;
			   while ((line = reader.readLine()) != null) {
	    		   Log.i( "readLine", line );
		    	   
	    		   sb.append(line + "\n");
	    	   }
			   is.close();
			   Log.i( "convert_response_to_string", "---------------- convert_response_to_string : step 5" );
			   result=sb.toString();
		    Log.i( "convert_response_to_string", "---------------- convert_response_to_string : end" );
		   }
		   catch(Exception e){
			   result = e.toString();
	    	   Log.e("log_tag", "Error converting result "+e.toString());
		   }
		   return result;
    }
}