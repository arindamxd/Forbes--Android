package com.ARFixer.ForbesPlus;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DB {

	public DefaultHttpClient httpclient;
	public HttpResponse response;
	public InputStream is;
	public String string_result;

	public DB(){
		httpclient = new DefaultHttpClient();
	}
	
	public void http_post( String url, ArrayList<NameValuePair> postVals ){
		try{
			Log.i( "http_post", "---------------- http_post : start" );
		    HttpPost httppost = new HttpPost(url);
		    if ( postVals != null ){
		    	httppost.setEntity(new UrlEncodedFormEntity(postVals));
		    }
		    Log.i( "http_post", "---------------- http_post : step1" );
		    this.response = this.httpclient.execute(httppost);
		    Log.i( "response", this.response.toString() );
		    Log.i( "http_post", "---------------- http_post : end" );
		}
		catch(Exception e){
	    	Log.e("log_tag", "Error in http connection "+e.toString());
	    }
	}
	
	public void http_post_return( String url, ArrayList<NameValuePair> postVals ){
		try{
			Log.i( "http_post_return", "---------------- http_post_return : start" );
		    this.http_post(url, postVals);
		    Log.i( "http_post_return", "---------------- http_post_return : step1" );
		    Log.i( "url", url );
		    //Log.i( "postVals", postVals.toString() );
		    HttpEntity entity = this.response.getEntity();
		    Log.i( "http_post_return", "---------------- http_post_return : step2" );
		    this.is = entity.getContent();
		    Log.i( "http_post_return", "---------------- http_post_return : step3" );
			this.string_result = Biblioteka.convert_response_to_string( this.is );
			Log.i( "http_post_return", "---------------- http_post_return : end" );
		}
		catch(Exception e){
	    	Log.e("log_tag", "Error in http connection "+e.toString());
	    	
	    }
	}
	
	public JSONObject http_post_return_with_jsonobject( String url, ArrayList<NameValuePair> postVals ){
		this.http_post_return(url, postVals);
	    try {
			JSONObject json_data = new JSONObject(this.string_result);
			return json_data;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}
	
	public JSONArray http_post_return_with_jsonarray( String url, ArrayList<NameValuePair> postVals ){
		Log.i( "http_post_return_with_jsonarray", "---------------- http_post_return_with_jsonarray : start" );
		this.http_post_return(url, postVals);
		Log.i( "http_post_return_with_jsonarray", "---------------- http_post_return_with_jsonarray : step 1" );
	    try {
	    	Log.i( "string_result", "---------------- string_result : " + string_result );
		    	
			JSONArray json_data = new JSONArray(this.string_result);
			return json_data;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i( "http_post_return_with_jsonarray", "---------------- http_post_return_with_jsonarray : end" );
	    return null;
	}
}
