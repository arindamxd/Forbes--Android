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
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.telephony.TelephonyManager;
import android.util.Log;

public class Logs {
	 private static final String LOG_TYPE_LAUNCH = "0";
	 private static final String LOG_TYPE_TRACKABLE = "10";
	 private static final String LOG_TYPE_BUTTON = "20";
	public TelephonyManager local_phone_details;
	private DB local_oDB;
	
	
	

	public  Logs( TelephonyManager phone_details, DB oDB ){
		 local_phone_details = phone_details;
		 local_oDB = oDB;
	 }
	 
	public Integer newUserSaveLogAndGetLogUserId()
	{
		Integer _user_id = 0;
		String url = "http://ygd13959497c.nazwa.pl/ar_par/index.php/ajax/Common/UserInfo/save_new_user_log";
		ArrayList<NameValuePair> postVals = new ArrayList<NameValuePair>();
		local_oDB.http_post_return(url, postVals);
		String result = local_oDB.string_result.replaceAll("[^0-9.]","");
		_user_id = Integer.parseInt( result );
		return _user_id;
	}
	
	public void addLogAtButtonPressed( int user_id, String t_id, String b_id, String b_type )
	{
		this.set_log( Logs.LOG_TYPE_BUTTON, "0", Integer.toString(user_id), b_id, b_type );
	}
	
	public void addLogAtLaunch( int user_id )
	{
		this.set_log( Logs.LOG_TYPE_LAUNCH, "0", Integer.toString(user_id), "0", "0" );
	}
	
	
	
	

	 private void set_log( String type, String trackable_id, String user_id_string, String b_id, String b_type ){	
	    	try {
	    		String imei = local_phone_details.getDeviceId(); // Requires READ_PHONE_STATE 
	            String phoneNumber=local_phone_details.getLine1Number(); // Requires READ_PHONE_STATE 
	            String softwareVer = local_phone_details.getDeviceSoftwareVersion(); // Requires READ_PHONE_STATE 
	            String simSerial = local_phone_details.getSimSerialNumber(); // Requires READ_PHONE_STATE 
	            String subscriberId = local_phone_details.getSubscriberId(); // Requires READ_PHONE_STATE 
	            String PhoneModel = android.os.Build.MODEL;
	            String PhoneBrand = android.os.Build.BRAND;
	            String PhoneDevice = android.os.Build.DEVICE;
	            String PhoneDisplay = android.os.Build.DISPLAY;
	            String PhoneProduct = android.os.Build.PRODUCT;
	            
	    		String url = "http://ygd13959497c.nazwa.pl/ar_par/index.php/ajax/Common/ArLogs/dodaj_log";
	    		ArrayList<NameValuePair> postVals = new ArrayList<NameValuePair>();
	    		postVals.add( new BasicNameValuePair("type", type ));
	    		postVals.add( new BasicNameValuePair("imei", imei));
	    		postVals.add( new BasicNameValuePair("phoneNumber", phoneNumber));
	    		postVals.add( new BasicNameValuePair("softwareVer", softwareVer));
	    		postVals.add( new BasicNameValuePair("simSerial", simSerial));
	    		postVals.add( new BasicNameValuePair("subscriberId", subscriberId));
	    		postVals.add( new BasicNameValuePair("trackable_id", trackable_id ));
	    		postVals.add( new BasicNameValuePair("button_id", b_id ));
	    		postVals.add( new BasicNameValuePair("button_type", b_type ));
	    		postVals.add( new BasicNameValuePair("PhoneModel", PhoneModel ));
	    		postVals.add( new BasicNameValuePair("PhoneBrand", PhoneBrand ));
	    		postVals.add( new BasicNameValuePair("PhoneDevice", PhoneDevice ));
	    		postVals.add( new BasicNameValuePair("PhoneDisplay", PhoneDisplay ));
	    		postVals.add( new BasicNameValuePair("PhoneProduct", PhoneProduct ));
	    		postVals.add( new BasicNameValuePair("app_id", "1"));
	    		postVals.add( new BasicNameValuePair("u_id", user_id_string));
	    		
	    		
	    		local_oDB.http_post(url, postVals);
	    	}
	    	catch (Exception e) {
				// TODO: handle exception
	    		
			}
	    }

	
	 
}
