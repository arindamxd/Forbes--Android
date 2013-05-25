/*==============================================================================
            Copyright (c) 2012-2013 QUALCOMM Austria Research Center GmbH.
            All Rights Reserved.
            Qualcomm Confidential and Proprietary

This  Vuforia(TM) sample application in source code form ("Sample Code") for the
Vuforia Software Development Kit and/or Vuforia Extension for Unity
(collectively, the "Vuforia SDK") may in all cases only be used in conjunction
with use of the Vuforia SDK, and is subject in all respects to all of the terms
and conditions of the Vuforia SDK License Agreement, which may be found at
https://developer.vuforia.com/legal/license.

By retaining or using the Sample Code in any manner, you confirm your agreement
to all the terms and conditions of the Vuforia SDK License Agreement.  If you do
not agree to all the terms and conditions of the Vuforia SDK License Agreement,
then you may not retain or use any of the Sample Code in any manner.


@file
    VideoPlayback.java

@brief
    This sample application shows how to play a video in AR mode.
    Devices that support video on texture can play the video directly
    on the image target.

    Other devices will play the video in full screen mode.
==============================================================================*/


package com.qualcomm.QCARSamples.VideoPlayback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.MotionEvent;

import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//
//import com.ARFixer.PARplus.DB;
//import com.ARFixer.PARplus.Logs;
//import com.ARFixer.PARplus.Texture;
//import com.ARFixer.PARplus.DB;
//import com.ARFixer.PARplus.Downloader;
import com.qualcomm.QCAR.QCAR;
import com.qualcomm.QCARSamples.VideoPlayback.VideoPlayerHelper.MEDIA_STATE;
import com.qualcomm.QCARSamples.VideoPlayback.ButtonsView;

/** The AR activity for the VideoPlayback sample. */
public class VideoPlayback extends Activity
{
	// Menu item string constants:
    private static final String MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS =
        "Activate Cont. Auto Focus";
    private static final String MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS =
        "Deactivate Cont. Auto Focus";
    private static final String MENU_ITEM_TRIGGER_AUTO_FOCUS = 
    		"Trigger Auto Focus";
    
	// Focus mode constants:
    private static final int FOCUS_MODE_NORMAL = 0;
    private static final int FOCUS_MODE_CONTINUOUS_AUTO = 1;
    
    // Application status constants:
    private static final int APPSTATUS_UNINITED         = -1;
    private static final int APPSTATUS_INIT_APP         = 0;
    private static final int APPSTATUS_INIT_QCAR        = 1;
    private static final int APPSTATUS_INIT_TRACKER     = 2;
    private static final int APPSTATUS_INIT_APP_AR      = 3;
    private static final int APPSTATUS_LOAD_TRACKER     = 4;
    private static final int APPSTATUS_INITED           = 5;
    private static final int APPSTATUS_CAMERA_STOPPED   = 6;
    private static final int APPSTATUS_CAMERA_RUNNING   = 7;

    // Name of the native dynamic libraries to load:
    private static final String NATIVE_LIB_SAMPLE       = "VideoPlayback";
    private static final String NATIVE_LIB_QCAR         = "QCAR";

    // Helpers to detect events such as double tapping:
    private GestureDetector mGestureDetector            = null;
    private SimpleOnGestureListener mSimpleListener     = null;

    // Pointer to the current activity:
    private Activity mCurrentActivity                   = null;

    public DB oDB;
    private JSONArray json_trackers_data, all_trackablesdir_by_k_active, json_marker_trackers_data, json_audio_trackers_data, json_globalna_tablica_dirs;
    private ArrayList<JSONObject> json_tracker_active_data_arrlist;
    private ArrayList<String> files_to_download;
	private JSONArray json_media_data;
	private JSONArray json_news_data;
	private JSONObject json_tracker_active_data;
	private String audio_action, video_action, www_action, autostart_action;
	private String gallery_img_url, filename_to_download_active, date_picked_from_calendar, td_id;
	
	public static final String BASE_URL = "http://ygd13959497c.nazwa.pl/ar_forbes";
	private TelephonyManager phone_details;
	private Logs oLogs;
	private JSONArray json_news_data_readed;
	private String app_version;
	private int app_code;
	
    
    // Movie for the Targets:
    public static final int NUM_TARGETS                 = 20;
    public static final int STONES                      = 0;
    public static final int CHIPS                       = 1;
    private VideoPlayerHelper mVideoPlayerHelper[]      = null;
    private int mSeekPosition[]                         = null;
    private boolean mWasPlaying[]                       = null;
    private String mMovieName[]                         = null;

    // A boolean to indicate whether we come from full screen:
    private boolean mReturningFromFullScreen            = false;

    // Our OpenGL view:
    private QCARSampleGLView mGlView;

    // The StartupScreen view and the start button:
    private View mStartupView                           = null;
    private ImageView mStartButton                      = null;
    private boolean mStartScreenShowing                 = false;

    
  //button view
    private View mButtonView                           = null;
    private boolean mButtonScreenShowing                 = false;
    
  //button view
    private ButtonsView mButtonView2                           = null;
    
    
    //tutorial view
    private View mTutorialView                           = null;
    private boolean mTutorialScreenShowing                 = false;
    
    
    // The view to display the sample splash screen:
    private ImageView mSplashScreenView;

    // The handler and runnable for the splash screen time out task:
    private Handler mSplashScreenHandler;
    private Runnable mSplashScreenRunnable;

    // The minimum time the splash screen should be visible:
    private static final long MIN_SPLASH_SCREEN_TIME    = 2000;

    // The time when the splash screen has become visible:
    long mSplashScreenStartTime = 0;

    // Our renderer:
    private VideoPlaybackRenderer mRenderer;

    // Display size of the device:
    private int mScreenWidth                            = 0;
    private int mScreenHeight                           = 0;

    // The current application status:
    private int mAppStatus                              = APPSTATUS_UNINITED;

    // The async tasks to initialize the QCAR SDK:
    private InitQCARTask mInitQCARTask;
    private LoadTrackerTask mLoadTrackerTask;

    // An object used for synchronizing QCAR initialization, dataset loading and
    // the Android onDestroy() life cycle event. If the application is destroyed
    // while a data set is still being loaded, then we wait for the loading
    // operation to finish before shutting down QCAR.
    private Object mShutdownLock = new Object();

    // QCAR initialization flags:
    private int mQCARFlags = 0;

    // The textures we will use for rendering:
    private Vector<Texture> mTextures;
    private int mSplashScreenImageResource = 0;
    
    // Current focus mode:
    private int mFocusMode;

    /** Static initializer block to load native libraries on start-up. */
    static
    {
        loadLibrary(NATIVE_LIB_QCAR);
        loadLibrary(NATIVE_LIB_SAMPLE);
    }

    /** An async task to initialize QCAR asynchronously. */
    private class InitQCARTask extends AsyncTask<Void, Integer, Boolean>
    {
        // Initialize with invalid value:
        private int mProgressValue = -1;

        protected Boolean doInBackground(Void... params)
        {
            // Prevent the onDestroy() method to overlap with initialization:
            synchronized (mShutdownLock)
            {
                QCAR.setInitParameters(VideoPlayback.this, mQCARFlags);

                do
                {
                    // QCAR.init() blocks until an initialization step is
                    // complete, then it proceeds to the next step and reports 
                    // progress in percents (0 ... 100%).
                    // If QCAR.init() returns -1, it indicates an error.
                    // Initialization is done when progress has reached 100%.
                    mProgressValue = QCAR.init();

                    // Publish the progress value:
                    publishProgress(mProgressValue);

                    // We check whether the task has been canceled in the
                    // meantime (by calling AsyncTask.cancel(true))
                    // and bail out if it has, thus stopping this thread.
                    // This is necessary as the AsyncTask will run to completion
                    // regardless of the status of the component that 
                    // started is.
                } while (!isCancelled() && mProgressValue >= 0 
                         && mProgressValue < 100);

                return (mProgressValue > 0);
            }
        }


        protected void onProgressUpdate(Integer... values)
        {
            // Do something with the progress value "values[0]", e.g. update
            // splash screen, progress bar, etc.
        }


        protected void onPostExecute(Boolean result)
        {
            // Done initializing QCAR, proceed to next application
            // initialization status:
            if (result)
            {
                DebugLog.LOGD("InitQCARTask::onPostExecute: QCAR " +
                              "initialization successful");

                updateApplicationStatus(APPSTATUS_INIT_TRACKER);
            }
            else
            {
                // Create dialog box for display error:
                AlertDialog dialogError = new AlertDialog.Builder(VideoPlayback.this).create();
                dialogError.setButton
                (
                    DialogInterface.BUTTON_POSITIVE,
                    "Close",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // Exiting application:
                            System.exit(1);
                        }
                    }
                );

                String logMessage;

                // NOTE: Check if initialization failed because the device is
                // not supported. At this point the user should be informed
                // with a message.
                if (mProgressValue == QCAR.INIT_DEVICE_NOT_SUPPORTED)
                {
                    logMessage = "Failed to initialize QCAR because this " +
                        "device is not supported.";
                }
                else
                {
                    logMessage = "Failed to initialize QCAR.";
                }

                // Log error:
                DebugLog.LOGE("InitQCARTask::onPostExecute: " + logMessage +
                                " Exiting.");

                // Show dialog box with error message:
                dialogError.setMessage(logMessage);
                dialogError.show();
            }
        }
    }

    /** An async task to load the tracker data asynchronously. */
    private class LoadTrackerTask extends AsyncTask<Void, Integer, Boolean>
    {
        protected Boolean doInBackground(Void... params)
        {
            // Prevent the onDestroy() method to overlap:
            synchronized (mShutdownLock)
            {
                // Load the tracker data set:
                return (loadTrackerData() > 0);
            }
        }

        protected void onPostExecute(Boolean result)
        {
            DebugLog.LOGD("LoadTrackerTask::onPostExecute: execution " +
                        (result ? "successful" : "failed"));

            if (result)
            {
                // Done loading the tracker, update application status:
                updateApplicationStatus(APPSTATUS_INITED);
            }
            else
            {
                // Create dialog box for display error:
                AlertDialog dialogError = new AlertDialog.Builder
                (
                    VideoPlayback.this
                ).create();

                dialogError.setButton
                (
                    DialogInterface.BUTTON_POSITIVE,
                    "Close",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // Exiting application
                            System.exit(1);
                        }
                    }
                );

                // Show dialog box with error message:
                dialogError.setMessage("Failed to load tracker data.");
                dialogError.show();
            }
        }
    }

    private void storeScreenDimensions()
    {
        // Query display dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    /** Called when the activity first starts or the user navigates back
     * to an activity. */
    protected void onCreate(Bundle savedInstanceState)
    {
        DebugLog.LOGD("VideoPlayback::onCreate");
        super.onCreate(savedInstanceState);

        // Set the splash screen image to display during initialization:
        mSplashScreenImageResource = R.drawable.splash_screen_video_playback;

        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();

        // Query the QCAR initialization flags:
        mQCARFlags = getInitializationFlags();

        // Update the application status to start initializing application
        updateApplicationStatus(APPSTATUS_INIT_APP);

        // Create the gesture detector that will handle the single and 
        // double taps:
        mSimpleListener = new SimpleOnGestureListener();
        mGestureDetector = new GestureDetector(
            getApplicationContext(), mSimpleListener);

        
        
        
        
//        for (int i = 0; i < this.json_trackers_data.length(); i++)
//        {
//        	try {
//        		JSONObject wiersz;
//    			wiersz = (JSONObject)this.json_trackers_data.get(i);
//				int emt_id = wiersz.getInt("emt_id");
//				if ( emt_id == 3 ){
//					String em_url = wiersz.getString("em_url");
//					Log.i("offline_movies->", "offline_movies->" + em_url );
////					files_to_download.add(em_url);
//				}
//				
//			} catch (JSONException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
//        }
        
//        for (int i = 0; i < NUM_TARGETS; i++)
//        {
//            mVideoPlayerHelper[i] = new VideoPlayerHelper();
//            mVideoPlayerHelper[i].init();
//            mVideoPlayerHelper[i].setActivity(this);
//            JSONObject wiersz;
//			try {
//				wiersz = (JSONObject)this.json_trackers_data.get(i);
//			z	Log.i("dsfdsf:",wiersz.toString());
//	            mMovieName[i] = wiersz.getString("em_url");
//			} catch (JSONException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//        }
        
        
        mVideoPlayerHelper = new VideoPlayerHelper[NUM_TARGETS];
        mSeekPosition = new int[NUM_TARGETS];
        mWasPlaying = new boolean[NUM_TARGETS];
        mMovieName = new String[NUM_TARGETS];

        // Create the video player helper that handles the playback of the movie
        // for the targets:
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            mVideoPlayerHelper[i] = new VideoPlayerHelper();
            mVideoPlayerHelper[i].init();
            mVideoPlayerHelper[i].setActivity(this);

            mMovieName[i] = Integer.toString(i) + ".mp4";
            Log.i("movieName", mMovieName[i] );
        }
//
    //    mMovieName[0] = "0.mp4";
    //    mMovieName[1] = "1.mp4";

        mCurrentActivity = this;

        
        try{
	        if ( isOnline() ){
		        this.oDB = new DB();
		        Log.i( " isOnline", "isOnline <--------------------------------------------------------"
		         );
		        this.get_trackables_dirs();
		        JSONObject last = (JSONObject) this.json_globalna_tablica_dirs.get( json_globalna_tablica_dirs.length()-1 );
		        filename_to_download_active = last.getString("data_wydania").replace("-", "") + "_" + last.getString("td_id") + "_" +last.getString("wersja");
		        Log.i( " last", "++++++++++++++++"+filename_to_download_active );
			    this.get_audios_trackers();
		        this.oLogs = new Logs( phone_details,oDB );
		        this.get_app_info(); 

		        
	        }
	        else{
	        	AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
	    	    dlgAlert.setMessage("Unable to connect with server. Check Internet connection.");
	    	    dlgAlert.setTitle("INTERNET CONNECTION PROBLEM");
	    	    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	System.exit(0);
	                }
	            } );
	    	    dlgAlert.setCancelable(false);
	    	    dlgAlert.create().show();
	        }
        }
        catch( Exception e ){
        	Test( e.toString(), "error238" );
        }
        
        
        
        
        
        // Set the double tap listener:
        mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener()
        {
            /** Handle the double tap */
            public boolean onDoubleTap(MotionEvent e)
            {
                // Do not react if the StartupScreen is being displayed:
                if (mStartScreenShowing)
                    return false;

                for (int i = 0; i < NUM_TARGETS; i++)
                {
                    // Verify that the tap happens inside the target:
                    if (isTapOnScreenInsideTarget(i, e.getX(), e.getY()))
                    {
                        // Check whether we can play full screen at all:
                        if (mVideoPlayerHelper[i].isPlayableFullscreen())
                        {
                            // Pause all other media:
                            pauseAll(i);

                            // Request the playback in fullscreen:
                            mVideoPlayerHelper[i].play(true,VideoPlayerHelper.CURRENT_POSITION);
                        }

                        // Even though multiple videos can be loaded only one 
                        // can be playing at any point in time. This break 
                        // prevents that, say, overlapping videos trigger 
                        // simultaneously playback.
                        break;
                    }
                }

                return true;
            }

            public boolean onDoubleTapEvent(MotionEvent e)
            {
                // We do not react to this event
                return false;
            }

            /** Handle the single tap */
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                // Do not react if the StartupScreen is being displayed
                if (mStartScreenShowing)
                    return false;

                for (int i = 0; i < NUM_TARGETS; i++)
                {
                    // Verify that the tap happened inside the target
                    if (isTapOnScreenInsideTarget(i, e.getX(), e.getY()))
                    {
                        // Check if it is playable on texture
                        if (mVideoPlayerHelper[i].isPlayableOnTexture())
                        {
                            // We can play only if the movie was paused, ready or stopped
                            if ((mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PAUSED) ||
                                (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.READY)  ||
                                (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.STOPPED) ||
                                (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.REACHED_END))
                            {
                                // Pause all other media
                                pauseAll(i);

                                // If it has reached the end then rewind
                                if ((mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.REACHED_END))
                                    mSeekPosition[i] = 0;

                                mVideoPlayerHelper[i].play(false, mSeekPosition[i]);
                                mSeekPosition[i] = VideoPlayerHelper.CURRENT_POSITION;
                            }
                            else if (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING)
                            {
                                // If it is playing then we pause it
                                mVideoPlayerHelper[i].pause();
                            }
                        }
                        else if (mVideoPlayerHelper[i].isPlayableFullscreen())
                        {
                            // If it isn't playable on texture
                            // Either because it wasn't requested or because it
                            // isn't supported then request playback fullscreen.
                            mVideoPlayerHelper[i].play(true,VideoPlayerHelper.CURRENT_POSITION);
                        }

                        // Even though multiple videos can be loaded only one 
                        // can be playing at any point in time. This break 
                        // prevents that, say, overlapping videos trigger 
                        // simultaneously playback.
                        break;
                    }
                }

                return true;
            }
        });
    }

    static JSONObject jObj = null;
    static String json = "";
    String url=null;
       List<NameValuePair> nvp=null;
       
       
    // function get json from url
    // by making HTTP POST or GET mehtod
     public JSONArray makeHttpRequest(String url, String method,
          List<NameValuePair> params) {
    	 GetJSONFromURL Task= new GetJSONFromURL(url, method,  params);
      try {
    	  Log.i("makeHttpRequest", "makeHttpRequest OK +++++++ " + Task.URL );
          return Task.execute().get();
      } catch (InterruptedException e) {
    	  Log.i("makeHttpRequest", "makeHttpRequest InterruptedException +++++++ ");
          // TODO Auto-generated catch block
          e.printStackTrace();
          return null;
      } catch (ExecutionException e) {
    	  Log.i("makeHttpRequest", "makeHttpRequest ExecutionException +++++++ ");
          // TODO Auto-generated catch block
          e.printStackTrace();
          return null;
      }
  }
    
    
     
    public void get_trackables_dirs(){	
    	//nvp.add(null);
    	Log.i( " get_trackables_dirs", "get_trackables_dirs <-------------------------------------0");        
		String url = BASE_URL + "/index.php/ajax/Common/Trackables/get_all_trackablesdir_by_k?k_id=1";
		Log.i( " get_trackables_dirs", "get_trackables_dirs <-------------------------------------1");        
		Log.i("get_trackables_dirs", "get_trackables_dirs ++++++++++++++++++++++++++++++++ "  );
		this.json_globalna_tablica_dirs = this.makeHttpRequest(url, "GET", nvp);
    	Log.i( " get_trackables_dirs", "get_trackables_dirs <-------------------------------------2");        
    }
    

    public void get_audios_trackers(){	
//    	try {
    		Log.i( "getaudiotrackers", "---------------- get_audios_trackers : start" );
    		String url = BASE_URL + "/index.php/ajax/Common/Audio/get_audios";
    		this.json_audio_trackers_data =  this.makeHttpRequest(url, "GET", nvp);
//    				this.oDB.http_post_return_with_jsonarray(url, null);
    		Log.i( "get_audios_trackers", "++++++++++++++++"+Integer.toString( this.json_audio_trackers_data.length()) );
    		Log.i( "get_audios_trackers", "---------------- get_audios_trackers: end" );
//    	}
//    	catch (Exception e) {
//    		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
//    	    dlgAlert.setMessage("Unable to connect with server. Check Internet connection.");
//    	    dlgAlert.setTitle("INTERNET CONNECTION PROBLEM");
//    	    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                	System.exit(0);
//                }
//            } );
//    	    dlgAlert.setCancelable(false);
//    	    dlgAlert.create().show();
//		}
    }
    

    public void get_app_info(){	
	    PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			app_version = pInfo.versionName;
			app_code = pInfo.versionCode;
		} catch (NameNotFoundException e) {
		}
	    
    }
    
    public void get_trackers(){	
    	try {
    		JSONObject last = (JSONObject) this.json_globalna_tablica_dirs.get( json_globalna_tablica_dirs.length()-1 );
	        Log.i( "gettracker", "---------------- getTrackers : start" );
    		String url = BASE_URL + "/index.php/ajax/Common/Trackables/get_tracker_by_td?td_id=" + last.getString("td_id");
    		Log.i( "gettracker url", url );    		
    		this.json_trackers_data = this.makeHttpRequest(url, "GET", nvp);
    		Log.i( "gettracker", "++++++++++++++++"+Integer.toString( this.json_trackers_data.length()) );
    		Log.i( "gettracker", "---------------- getTrackers: end" );
    	}
    	catch (Exception e) {
    		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
    	    dlgAlert.setMessage("Unable to connect with server. Check Internet connection.");
    	    dlgAlert.setTitle("INTERNET CONNECTION PROBLEM");
    	    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	System.exit(0);
                }
            } );
    	    dlgAlert.setCancelable(false);
    	    dlgAlert.create().show();
		}
    }
    
    

	public boolean dupa(String extension) {
        
		Log.i("--------------------------filename:", "start");
		String fileUrl2 = "http://ygd13959497c.nazwa.pl/forbes.arfixer.eu/biblioteki/client/1/"
				+ this.filename_to_download_active + "." + extension;
		Log.i("--------------------------filename:", "---316--->"+fileUrl2);
		// file exist ?
		File sdCard = Environment.getExternalStorageDirectory();
		Log.i("--------------------------filename:", "---319---");
		String urltest = sdCard.getAbsolutePath() + "/forbes/"
				+ this.filename_to_download_active + "." + extension;
		Log.i("!!!!!!  ", urltest);
		// File file = applicationContext.getFileStreamPath(urltest);
		File f = new File(urltest);
		Log.i("--------------------------filename:", "---323---");
		if (f.exists()) {
			Log.i("!!!!!! EXISTY ", "!!!!!! EXISTY ");
			return true;
		} else {
			Log.i("!!!!!! NOT EXIST ", "!!!!!! NOT EXIST 0 ");
			// execute this when the downloader must be fired
			GetFileFromURL downloadFile = new GetFileFromURL();
			downloadFile.execute(fileUrl2);
//			String filename = Downloader.DownloadFile(fileUrl2,
//					filename_to_download_active + "." + extension);
//			Log.i("--------------------------filename:", filename);

			File fn = new File(urltest);
			Log.i("--------------------------filename:", "---335---");
			if (fn.exists()) {
				Log.i("!!!!!! EXISTY ", "!!!!!! EXISTY ");
				return true;
			} else {
				return false;
			}
		}
	}

	
	
	
	public void aktywujClicked( Boolean from_button, String _filename_to_download_active ) {
		
		
		Log.i("------_filename_to_download_active----", _filename_to_download_active );
		if ( _filename_to_download_active.length()>0 ){
			filename_to_download_active = _filename_to_download_active;
			
			if ( from_button ){
				
			}
			else{
				Log.i("------aktywujClicked----", json_globalna_tablica_dirs.toString() );
				JSONObject wiersz;
				try {
					wiersz = (JSONObject) this.json_globalna_tablica_dirs.get(json_globalna_tablica_dirs.length()-1);
					Log.i("------wierszwierszwierszwierszwiersz----", wiersz.toString() );
					String data_wydania = wiersz.getString("data_wydania");
					td_id = wiersz.getString("td_id");
					date_picked_from_calendar = wiersz.getString("data_wydania");
					Log.i("------date_picked_from_calendar----", td_id + " ////// " + date_picked_from_calendar );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(this, "error: 135", 0).show();
				}
			}
			
				
		}
//		rlLoading.setVisibility(View.VISIBLE);
//		
		if (dupa("xml") && dupa("dat")) {
			Log.i("-----------", "DOWNLOADED CORECTLY");
			activate_library();
			
			
			Log.i("-----------", "ukryjWszystkieButtonki ukryjWszystkieButtonki ukryjWszystkieButtonki ukryjWszystkieButtonki");

			
			//			ukryjWszystkieButtonki();
////			show_window_setN(Slownik.WINDOWS_SET_TOUCHTOGO);
//			show_window_setN(Slownik.WINDOWS_SET_CLEAR);
//			
		}
//
		else {
			Log.i("-----------", "DOWNLOADED ERRROR");
		}
	}
	
	
	public void get_offline_movies(){	
//   	 for()
		files_to_download  = new ArrayList<String>();
   		for ( int i =0; i < this.json_trackers_data.length(); i++ ){
   			try {
				JSONObject wiersz = (JSONObject)this.json_trackers_data.get(i);
				int emt_id = wiersz.getInt("emt_id");
				if ( emt_id == 3 ){
					String em_url = wiersz.getString("em_url");
					Log.i("get_offline_movies->", "get_offline_movies->" + em_url );
					files_to_download.add(em_url);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		}       
    }
	
	public void download_offline_movies(){	
//  	 for()
		for ( int i =0; i < files_to_download.size(); i++ ){
  
				String em_url = files_to_download.get(i);
				Log.i("download_offline_movies->", "download_offline_movies->" + em_url );
				download_file(em_url);
  		}       
   }
	
	public boolean download_file( String url ){
		Log.i("--------------------------download_file:", "start");
		Log.i("--------------------------download_file:", "---url--->"+url);
		// file exist ?
		File sdCard = Environment.getExternalStorageDirectory();
		//get file name
		String[] tmp = url.split("/");
		String filename = tmp[tmp.length-1];
		String urltest = sdCard.getAbsolutePath() + "/forbes/"
				+ filename;
		Log.i("--------------------------download_file:", "---urltotest--->" + urltest);
		// File file = applicationContext.getFileStreamPath(urltest);
		File f = new File(urltest);
		if (f.exists()) {
			Log.i("--------------------------download_file:", "---EXIST---");
			return true;
		} else {
			Log.i("--------------------------download_file:", "---NOT EXIST---");
			// execute this when the downloader must be fired
			GetFileFromURL downloadFile = new GetFileFromURL();
			downloadFile.execute(url);
////			String filename = Downloader.DownloadFile(fileUrl2,
////					filename_to_download_active + "." + extension);
////			Log.i("--------------------------filename:", filename);
//
			File fn = new File(urltest);
//			Log.i("--------------------------filename:", "---335---");
			if (fn.exists()) {
				Log.i("--------------------------download_file:", "---EXIST---");
			
				return true;
			} else {
				return false;
			}
		}
	}
	
	
	public void activate_library(){
		Log.i("ACTION_ACTIVATELIB START", "ACTION_ACTIVATELIB START");
		File sdCard = Environment.getExternalStorageDirectory();
		String action = sdCard.getAbsolutePath() + "/forbes/"+ filename_to_download_active+".xml";
		Log.i("ACTION_ACTIVATELIB", "ACTION_ACTIVATELIB:"+ action);
		
		//@todo show loading here
		get_trackers();
		
		mRenderer.setTrackablesData(this.json_trackers_data);
		get_offline_movies();
	    download_offline_movies();
		 
		 
  	//pobieramy guziki dla tej biblioteki
		//Log.i("pobieramy guziki dla tej biblioteki", mGUIManager.date_picked_from_calendar);
		
		
//  	  get_markers_trackers_for_lib(mGUIManager.date_picked_from_calendar);
	    
		loadActive( action );
		
		//Declare the timer
//		Timer t = new Timer();
////		t.scheduleAtFixedRate(task, delay, period);
//		//Set the schedule function and rate
//		t.scheduleAtFixedRate(new TimerTask() {
//
//		    @Override
//		    public void run() {
//		        //Called each time when 1000 milliseconds (1 second) (the period parameter)
//		    	Log.i("df","dsf");
//		    }
//		         
//		},
//		//Set how long before to start calling the TimerTask (in milliseconds)
//		0,
//		//Set the amount of time between each execution (in milliseconds)
//		1000);
		
		
		
		
//		int curent_tid = getCurrentTid();
		
		
//		Log.i("curent_tid", "<--------------"+Integer.toString(curent_tid));
//		Log.i("ACTION_ACTIVATELIB", "ACTION_ACTIVATELIB: after load active");
//		rlLoading.setVisibility(View.INVISIBLE);
//		try {
//			Log.i("rlLoading.getVisibility", Integer.toString(rlLoading.getVisibility()) );
//			if ( guzik_info.getString("action").equalsIgnoreCase("auto") ){
//				rlTutorial.setVisibility(View.VISIBLE);
//				rlKameraLogo.setVisibility(View.INVISIBLE);
//				Log.i("rlLoading.getVisibility", Integer.toString(rlLoading.getVisibility()) );
//				
//			}else{
//				rlTutorial.setVisibility(View.GONE);
//				rlKameraLogo.setVisibility(View.VISIBLE);
//			}
//			Log.i("rlLoading.getVisibility", Integer.toString(rlLoading.getVisibility()) );
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
    public void get_libraries(){	
    	try {
    		Log.i( "get_all_trackablesdir_by_k_active", "---------------- get_all_trackablesdir_by_k_active : start" );
    		String url = BASE_URL + "/index.php/ajax/Common/Trackables/get_all_trackablesdir_by_k_active?k_id=1";
    		Log.i( "get_all_trackablesdir_by_k_active url", url );
    		this.all_trackablesdir_by_k_active = this.oDB.http_post_return_with_jsonarray(url, null);
    		Log.i( "get_all_trackablesdir_by_k_active", "++++++++++++++++"+Integer.toString( this.all_trackablesdir_by_k_active.length()) );
    		Log.i( "get_all_trackablesdir_by_k_active", this.all_trackablesdir_by_k_active.toString() );
//    		mGUIManager.all_trackablesdir_by_k_active = this.all_trackablesdir_by_k_active;
    		Log.i( "get_all_trackablesdir_by_k_active", "---------------- getTrackers: end" );
    	}
    	catch (Exception e) {
    		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
    	    dlgAlert.setMessage("Unable to connect with server. Check Internet connection.");
    	    dlgAlert.setTitle("INTERNET CONNECTION PROBLEM");
    	    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	System.exit(0);
                }
            } );
    	    dlgAlert.setCancelable(false);
    	    dlgAlert.create().show();
		}
    }
    
    public void display_toast( CharSequence text ){
    	Context context = getApplicationContext();
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
    }
    
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    
    public Builder Test( String msg, String topic )
    {
    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
    dlgAlert.setMessage(msg);
    dlgAlert.setTitle(topic);
    dlgAlert.setPositiveButton("OK", null);
    dlgAlert.setCancelable(true);
    dlgAlert.create().show();
    return dlgAlert;	
    }
    
    
    public int setCurrentName(String dupa){
		
    	Log.i("setCurrentName", "setCurrentName: "+dupa);
    	return 1;
    	
    }
    
    /** We want to load specific textures from the APK, which we will later
     * use for rendering. */
    private void loadTextures()
    {
        mTextures.add(Texture.loadTextureFromApk("VuforiaSizzleReel_1.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("VuforiaSizzleReel_2.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("play.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("busy.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("error.png",
                getAssets()));
    }


    /** Configure QCAR with the desired version of OpenGL ES. */
    private int getInitializationFlags()
    {
        return QCAR.GL_20;
    }


    /** Native tracker initialization and deinitialization. */
    public native int initTracker();
    public native void deinitTracker();
    
    /**  */
    private native void loadActive(String isPortrait);
//    public native int setCurrentName(String track_name); 

    public native int getCurrentTid();

    /** Native functions to load and destroy tracking data. */
    public native int loadTrackerData();
    public native void destroyTrackerData();

    /** Native sample initialization. */
    public native void onQCARInitializedNative();

    /** Native methods for starting and stopping the camera. */
    private native void startCamera();
    private native void stopCamera();

    /** Native method for setting / updating the projection matrix for 
     * AR content rendering */
    private native void setProjectionMatrix();

    private native boolean isTapOnScreenInsideTarget(
        int target, float x, float y);

   /** Called when the activity will start interacting with the user.*/
    protected void onResume()
    {
        DebugLog.LOGD("VideoPlayback::onResume");
        super.onResume();

        // QCAR-specific resume operation
        QCAR.onResume();

        // We may start the camera only if the QCAR SDK has already been
        // initialized:
        if (mAppStatus == APPSTATUS_CAMERA_STOPPED)
        {
            updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);
        }

        // Setup the start button:
  
        
//        setupButtonScreen();
        //hideButtonScreen();
//        setupTutorialScreen();
//        setupStartButton();
        
        
        // Resume the GL view:
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }

        // Do not show the startup screen if we're returning from full screen:
        if (!mReturningFromFullScreen)
            showStartupScreen();
        
//        showTutorialScreen();
//        showButtonScreen();
        

        // Reload all the movies
        if (mRenderer != null)
        {
            for (int i = 0; i < NUM_TARGETS; i++)
            {
            	Log.i("VP", "1105!!!!!!!!!!!!!!!");
                if (!mReturningFromFullScreen)
                {
                    mRenderer.requestLoad(
                        i, mMovieName[i], mSeekPosition[i], false);
                }
                else
                {
                    mRenderer.requestLoad(
                        i, mMovieName[i], mSeekPosition[i], mWasPlaying[i]);
                }
            }
        }

        mReturningFromFullScreen = false;
    }

    /** Called when returning from the full screen player */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                // The following values are used to indicate the position in 
                // which the video was being played and whether it was being 
                // played or not:
                String movieBeingPlayed = data.getStringExtra("movieName");
                mReturningFromFullScreen = true;

                // Find the movie that was being played full screen
                for (int i = 0; i < NUM_TARGETS; i++)
                {
                    if (movieBeingPlayed.compareTo(mMovieName[i]) == 0)
                    {
                        mSeekPosition[i] = data.getIntExtra("currentSeekPosition", 0);
                        mWasPlaying[i] = data.getBooleanExtra("playing", false);
                    }
                }
            }
        }
    }

    public void onConfigurationChanged(Configuration config)
    {
        DebugLog.LOGD("VideoPlayback::onConfigurationChanged");
        super.onConfigurationChanged(config);

        storeScreenDimensions();

        // Set projection matrix:
        if (QCAR.isInitialized() && (mAppStatus == APPSTATUS_CAMERA_RUNNING))
            setProjectionMatrix();
    }


    /** Called when the system is about to start resuming a previous activity.*/
    protected void onPause()
    {
        DebugLog.LOGD("VideoPlayback::onPause");
        super.onPause();

        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }

        if (mAppStatus == APPSTATUS_CAMERA_RUNNING)
        {
            updateApplicationStatus(APPSTATUS_CAMERA_STOPPED);
        }

        // Store the playback state of the movies and unload them:
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            // If the activity is paused we need to store the position in which 
            // this was currently playing:
            if (mVideoPlayerHelper[i].isPlayableOnTexture())
            {
                mSeekPosition[i] = mVideoPlayerHelper[i].getCurrentPosition();
                mWasPlaying[i] = mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING ? true : false;
            }

            // We also need to release the resources used by the helper, though
            // we don't need to destroy it:
            if (mVideoPlayerHelper[i]!= null)
                mVideoPlayerHelper[i].unload();
        }

        // Hide the Startup View:
        //hideStartupScreen();

        mReturningFromFullScreen = false;

        // QCAR-specific pause operation:
        QCAR.onPause();
    }


    /** Native function to deinitialize the application.*/
    private native void deinitApplicationNative();


    /** The final call you receive before your activity is destroyed.*/
    protected void onDestroy()
    {
        DebugLog.LOGD("::onDestroy");
        super.onDestroy();

        for (int i = 0; i < NUM_TARGETS; i++)
        {
            // If the activity is destroyed we need to release all resources:
            if (mVideoPlayerHelper[i] != null)
                mVideoPlayerHelper[i].deinit();
            mVideoPlayerHelper[i] = null;
        }

        // Dismiss the splash screen time out handler:
        if (mSplashScreenHandler != null)
        {
            mSplashScreenHandler.removeCallbacks(mSplashScreenRunnable);
            mSplashScreenRunnable = null;
            mSplashScreenHandler = null;
        }

        // Cancel potentially running tasks:
        if (mInitQCARTask != null &&
            mInitQCARTask.getStatus() != InitQCARTask.Status.FINISHED)
        {
            mInitQCARTask.cancel(true);
            mInitQCARTask = null;
        }

        if (mLoadTrackerTask != null &&
            mLoadTrackerTask.getStatus() != LoadTrackerTask.Status.FINISHED)
        {
            mLoadTrackerTask.cancel(true);
            mLoadTrackerTask = null;
        }

        // Ensure that all asynchronous operations to initialize QCAR and 
        // loading the tracker datasets do not overlap:
        synchronized (mShutdownLock) {

            // Do application deinitialization in native code:
            deinitApplicationNative();

            // Unload texture:
            mTextures.clear();
            mTextures = null;

            // Destroy the tracking data set:
            destroyTrackerData();

            // Deinit the tracker:
            deinitTracker();

            // Deinitialize QCAR SDK:
            QCAR.deinit();
        }

        System.gc();
    }




	
    /** NOTE: this method is synchronized because of a potential concurrent
     * access by ::onResume() and InitQCARTask::onPostExecute(). */
    private synchronized void updateApplicationStatus(int appStatus)
    {
        // Exit if there is no change in status:
        if (mAppStatus == appStatus)
            return;

        // Store new status value:
        mAppStatus = appStatus;

        // Execute application state-specific actions:
        switch (mAppStatus)
        {
            case APPSTATUS_INIT_APP:
                // Initialize application elements that do not rely on QCAR
                // initialization:
                initApplication();

                // Proceed to next application initialization status:
                updateApplicationStatus(APPSTATUS_INIT_QCAR);
                break;

            case APPSTATUS_INIT_QCAR:
                // Initialize QCAR SDK asynchronously to avoid blocking the
                // main (UI) thread.
                //
                // NOTE: This task instance must be created and invoked on the
                // UI thread and it can be executed only once!
                try
                {
                    mInitQCARTask = new InitQCARTask();
                    mInitQCARTask.execute();
                }
                catch (Exception e)
                {
                    DebugLog.LOGE("Initializing QCAR SDK failed");
                }
                break;

            case APPSTATUS_INIT_TRACKER:
                // Initialize the ImageTracker:
                if (initTracker() > 0)
                {
                    // Proceed to next application initialization status:
                    updateApplicationStatus(APPSTATUS_INIT_APP_AR);
                }
                break;

            case APPSTATUS_INIT_APP_AR:
                // Initialize Augmented Reality-specific application elements
                // that may rely on the fact that the QCAR SDK has been
                // already initialized:
                initApplicationAR();

                // Proceed to next application initialization status:
                updateApplicationStatus(APPSTATUS_LOAD_TRACKER);
                break;

            case APPSTATUS_LOAD_TRACKER:
                // Load the tracking data set:
                //
                // NOTE: This task instance must be created and invoked on the 
                // UI thread and it can be executed only once!
                try
                {
                    mLoadTrackerTask = new LoadTrackerTask();
                    mLoadTrackerTask.execute();
                }
                catch (Exception e)
                {
                    DebugLog.LOGE("Loading tracking data set failed");
                }
                break;

            case APPSTATUS_INITED:
                // Hint to the virtual machine that it would be a good time to
                // run the garbage collector.
                //
                // NOTE: This is only a hint. There is no guarantee that the
                // garbage collector will actually be run.
                System.gc();

                // Native post initialization:
                onQCARInitializedNative();

                // The elapsed time since the splash screen was visible:
                long splashScreenTime = System.currentTimeMillis() -
                                            mSplashScreenStartTime;
                long newSplashScreenTime = 0;
                if (splashScreenTime < MIN_SPLASH_SCREEN_TIME)
                {
                    newSplashScreenTime = MIN_SPLASH_SCREEN_TIME -
                                            splashScreenTime;
                }

                // Request a callback function after a given timeout to dismiss
                // the splash screen:
                mSplashScreenHandler = new Handler();
                mSplashScreenRunnable =
                    new Runnable() {
                        public void run()
                        {
                            // Hide the splash screen:
                            mSplashScreenView.setVisibility(View.INVISIBLE);

                            // Activate the renderer:
                            mRenderer.mIsActive = true;

                            // Now add the GL surface view. It is important
                            // that the OpenGL ES surface view gets added
                            // BEFORE the camera is started and video
                            // background is configured.
                            addContentView(mGlView, new LayoutParams(
                                            LayoutParams.MATCH_PARENT,
                                            LayoutParams.MATCH_PARENT));

                            
                            Log.i("====================================>", "AKTYWUJ LIB " );
                            aktywujClicked( false, filename_to_download_active );
                            
                            // Setup the start screen:
                            setupButtonScreen();
                            //hideButtonScreen();

//                            setupStartScreen();
                            
                            // Start the camera:
                            updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);
                        }
                };

                mSplashScreenHandler.postDelayed(mSplashScreenRunnable,
                                                    newSplashScreenTime);
                break;

            case APPSTATUS_CAMERA_STOPPED:
                // Call the native function to stop the camera:
                stopCamera();
                break;

            case APPSTATUS_CAMERA_RUNNING:
                // Call the native function to start the camera:
                startCamera();
                setProjectionMatrix();
                
                // Set continuous auto-focus if supported by the device,
                // otherwise default back to regular auto-focus mode.
                mFocusMode = FOCUS_MODE_CONTINUOUS_AUTO;
                if(!setFocusMode(FOCUS_MODE_CONTINUOUS_AUTO))
                {
                    mFocusMode = FOCUS_MODE_NORMAL;
                    setFocusMode(FOCUS_MODE_NORMAL);
                }

                break;

            default:
                throw new RuntimeException("Invalid application state");
        }
    }

    
    
	
    private void setupTutorialScreen()
    {
        // Inflate the view from the xml file:
        mTutorialView = getLayoutInflater().inflate(
            R.layout.tutorial, null);

        // Add it to the content view:
        addContentView(mTutorialView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        // Align and center the background container for the description:
        ImageButton ibTutorial = (ImageButton) findViewById(
            R.id.ibTutorial);

        if (ibTutorial != null)
        {
        	Log.e("-----------ibTutorial.setOnClickListener","ibTutorial.setOnClickListener seting");
        	ibTutorial.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

		            Log.e("-----------ibTutorial.setOnClickListener","ibTutorial.setOnClickListener pressed ");
					hideTutorialScreen();
				}
        	});
					
        }

        
        mStartButton = (ImageView) findViewById(R.id.ibTutorial);
        
                if (mStartButton != null)
                {
                    // Setup a click listener that hides the StartupScreen:
                    mStartButton.setOnClickListener(new ImageView.OnClickListener() {
                            public void onClick(View arg0) {
                            	Log.e("-----------ibTutorial.setOnClickListener","ibTutorial.setOnClickListener");
                            }
                    });
                }
        
        

     //   mTutorialView.setVisibility(View.INVISIBLE);       
        mTutorialScreenShowing = true;
    }
    
    /** Show the startup screen */
    private void showTutorialScreen()
    {
        if (mTutorialView != null)
        {
        	mTutorialView.setVisibility(View.VISIBLE);
            mTutorialScreenShowing = true;
            Log.e("-----------showTutorialScreen","showTutorialScreen");
        }
    }


    /** Hide the startup screen */
    private void hideTutorialScreen()
    {
    	Log.e("---------------hideTutorialScreen", "hideTutorialScreen");
        if (mTutorialView != null)
        {
        	
        	mTutorialView.setVisibility(View.INVISIBLE);
            mTutorialScreenShowing = false;
        }
    }
    
    
    
    
    
    
    
    
    
    
    /** TUTORIAL START */
    private void setupStartScreen()
    {
        // Inflate the view from the xml file:
        mStartupView = getLayoutInflater().inflate( R.layout.startup_screen, null);

        // Add it to the content view:
        addContentView(mStartupView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        this.setupTutorialButton();
        mStartScreenShowing = true;
    }
    /** This call sets the start button variable up */
    private void setupTutorialButton()
    {
        mStartButton = (ImageView) findViewById(R.id.start_button);

        if (mStartButton != null)
        {
            // Setup a click listener that hides the StartupScreen:
            mStartButton.setOnClickListener(new ImageView.OnClickListener() {
                    public void onClick(View arg0) {
                    	Log.e("---------------mStartButton.setOnClickListene", "mStartButton.setOnClickListene");
                        
                        hideStartupScreen();
                    }
            });
        }
    }
    /** Show the startup screen */
    private void showStartupScreen()
    {
        if (mStartupView != null)
        {
            mStartupView.setVisibility(View.VISIBLE);
            mStartScreenShowing = true;
        }
    }
    /** Hide the startup screen */
    private void hideStartupScreen()
    {
        if (mStartupView != null)
        {
            mStartupView.setVisibility(View.INVISIBLE);
            mStartScreenShowing = false;
        }
    }
    /** TUTORIAL END */
     
    
    /** BUTTON VIEW START */
    private void setupButtonScreen()
    {
        // Inflate the view from the xml file:
//        mButtonView = getLayoutInflater().inflate(
//            R.layout.interface_overlay, null);
//        
        mButtonView2 = new ButtonsView(getApplicationContext(), null);
        
        // Add it to the content view:
        addContentView(mButtonView2, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        mButtonScreenShowing = true;
    }
    private void showButtonScreen()
    {
    	Log.e("-----------showButtonScreen","showButtonScreen start");
        if (mButtonView != null)
        {
        	mButtonView.setVisibility(View.VISIBLE);
            mButtonScreenShowing = true;

        	Log.e("-----------showButtonScreen","showButtonScreen end");
        }
    }
    private void hideButtonScreen()
    {
        if (mButtonView != null)
        {
            mButtonView.setVisibility(View.INVISIBLE);
            mButtonScreenShowing = false;
            Log.e("-----------hideButtonScreen","hideButtonScreen");
            
        }
    }
    
    /** BUTTON VIEW END */
    

    /** Pause all movies except one
        if the value of 'except' is -1 then
        do a blanket pause */
    private void pauseAll(int except)
    {
        // And pause all the playing videos:
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            // We can make one exception to the pause all calls:
            if (i != except)
            {
                // Check if the video is playable on texture
                if (mVideoPlayerHelper[i].isPlayableOnTexture())
                {
                    // If it is playing then we pause it
                    mVideoPlayerHelper[i].pause();
                }
            }
        }
    }

    /** Do not exit immediately and instead show the startup screen */
    public void onBackPressed() {

        // If this is the first time the back button is pressed
        // show the StartupScreen and pause all media:
        if (!mStartScreenShowing)
        {
            // Show the startup screen:
            showStartupScreen();

            pauseAll(-1);
        }
        else // if this is the second time the user pressed the back button
        {
            // Hide the Startup View:
            hideStartupScreen();

            // And exit:
            super.onBackPressed();
        }
    }


    /** Tells native code whether we are in portait or landscape mode */
    private native void setActivityPortraitMode(boolean isPortrait);


    /** Initialize application GUI elements that are not related to AR. */
    private void initApplication()
    {
        // Set the screen orientation:
        int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        // Apply screen orientation:
        setRequestedOrientation(screenOrientation);

        // Pass on screen orientation info to native code:
        setActivityPortraitMode(
            screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        storeScreenDimensions();

        // As long as this window is visible to the user, keep the device's
        // screen turned on and bright:
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Create and add the splash screen view:
        mSplashScreenView = new ImageView(this);
        mSplashScreenView.setImageResource(mSplashScreenImageResource);
        addContentView(mSplashScreenView, new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mSplashScreenStartTime = System.currentTimeMillis();

    }


    /** Native function to initialize the application. */
    private native void initApplicationNative(int width, int height);


    /** Initializes AR application components. */
    private void initApplicationAR()
    {
        // Do application initialization in native code (e.g. registering
        // callbacks, etc.):
        initApplicationNative(mScreenWidth, mScreenHeight);

        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = QCAR.requiresAlpha();

        mGlView = new QCARSampleGLView(this);
        mGlView.init(mQCARFlags, translucent, depthSize, stencilSize);

        mRenderer = new VideoPlaybackRenderer();

        // The renderer comes has the OpenGL context, thus, loading to texture
        // must happen when the surface has been created. This means that we
        // can't load the movie from this thread (GUI) but instead we must
        // tell the GL thread to load it once the surface has been created.
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            mRenderer.setVideoPlayerHelper(i, mVideoPlayerHelper[i]);
            mRenderer.requestLoad(i, mMovieName[i], 0, false);
        }

        mGlView.setRenderer(mRenderer);
    }
    
    /** Invoked every time before the options menu gets displayed to give
     *  the Activity a chance to populate its Menu with menu items. */
    public boolean onPrepareOptionsMenu(Menu menu) 
    {
        super.onPrepareOptionsMenu(menu);
        
        menu.clear();

        if(mFocusMode == FOCUS_MODE_CONTINUOUS_AUTO)
            menu.add(MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS);
        else
            menu.add(MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS);

        menu.add(MENU_ITEM_TRIGGER_AUTO_FOCUS);

        return true;
    }

    /** Invoked when the user selects an item from the Menu */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getTitle().equals(MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS))
        {
            if(setFocusMode(FOCUS_MODE_CONTINUOUS_AUTO))
            {
                mFocusMode = FOCUS_MODE_CONTINUOUS_AUTO;
                item.setTitle(MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS);
            }
            else
            {
                Toast.makeText
                (
                    this,
                    "Unable to activate Continuous Auto-Focus",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        else if(item.getTitle().equals(MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS))
        {
            if(setFocusMode(FOCUS_MODE_NORMAL))
            {
                mFocusMode = FOCUS_MODE_NORMAL;
                item.setTitle(MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS);
            }
            else
            {
                Toast.makeText
                (
                    this,
                    "Unable to deactivate Continuous Auto-Focus",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        else if(item.getTitle().equals(MENU_ITEM_TRIGGER_AUTO_FOCUS))
        {
            boolean result = autofocus();
            
            // Autofocus action resets focus mode
            mFocusMode = FOCUS_MODE_NORMAL;
            
            DebugLog.LOGI
            (
                "Autofocus requested" +
                (result ?
                    " successfully." :
                    ". Not supported in current mode or on this device.")
            );
        }

        return true;
    }
    
    private native boolean autofocus();
    private native boolean setFocusMode(int mode);

    /** Returns the number of registered textures. */
    public int getTextureCount()
    {
        return mTextures.size();
    }


    /** Returns the texture object at the specified index. */
    public Texture getTexture(int i)
    {
        return mTextures.elementAt(i);
    }


    /** A helper for loading native libraries stored in "libs/armeabi*". */
    public static boolean loadLibrary(String nLibName)
    {
        try
        {
            System.loadLibrary(nLibName);
            DebugLog.LOGI("Native library lib" + nLibName + ".so loaded");
            return true;
        }
        catch (UnsatisfiedLinkError ulee)
        {
            DebugLog.LOGE("The library lib" + nLibName +
                            ".so could not be loaded");
        }
        catch (SecurityException se)
        {
            DebugLog.LOGE("The library lib" + nLibName +
                            ".so was not allowed to be loaded");
        }

        return false;
    }

    /** We do not handle the touch event here, we just forward it to the 
     * gesture detector */
    public boolean onTouchEvent(MotionEvent event)
    {
        return mGestureDetector.onTouchEvent(event);
    }
}
