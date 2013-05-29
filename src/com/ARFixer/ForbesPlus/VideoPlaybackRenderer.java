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
    VideoPlaybackRenderer.java

@brief
    Sample for VideoPlayback

==============================================================================*/


package com.ARFixer.ForbesPlus;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ARFixer.ForbesPlus.VideoPlayerHelper.MEDIA_STATE;
import com.ARFixer.ForbesPlus.VideoPlayerHelper.MEDIA_TYPE;
import com.qualcomm.QCAR.QCAR;


/** The renderer class for the VideoPlayback sample. */
public class VideoPlaybackRenderer implements GLSurfaceView.Renderer
{
    public boolean mIsActive                            = false;

    private float[][] mTexCoordTransformationMatrix     = null;
    private VideoPlayerHelper mVideoPlayerHelper[]      = null;
    private String mMovieName[]                         = null;
    private String mMovieNameCurrent                    = null;
    private MEDIA_TYPE mCanRequestType[]                = null;
    private int mSeekPosition[]                         = null;
    private boolean mShouldPlayImmediately[]            = null;
    private long mLostTrackingSince[]                   = null;
    private boolean mLoadRequested[]                    = null;

	private JSONArray json_trackables_data;
	private Context appContext;
	
    public VideoPlaybackRenderer(Context appCont ) {
    	appContext = appCont;
        // Creat an array of the size of the number of targets we have
        mVideoPlayerHelper = new VideoPlayerHelper[VideoPlayback.NUM_TARGETS];
        mMovieName = new String[VideoPlayback.NUM_TARGETS];
        mCanRequestType = new MEDIA_TYPE[VideoPlayback.NUM_TARGETS];
        mSeekPosition = new int[VideoPlayback.NUM_TARGETS];
        mShouldPlayImmediately = new boolean[VideoPlayback.NUM_TARGETS];
        mLostTrackingSince = new long[VideoPlayback.NUM_TARGETS];
        mLoadRequested = new boolean[VideoPlayback.NUM_TARGETS];
        mTexCoordTransformationMatrix = new float[VideoPlayback.NUM_TARGETS][16];

        // Initialize the arrays to default values
        for (int i = 0; i < VideoPlayback.NUM_TARGETS; i++)
        {
            mVideoPlayerHelper[i] = null;
            mMovieName[i] = "";
            mCanRequestType[i] = MEDIA_TYPE.ON_TEXTURE_FULLSCREEN;
            mSeekPosition[i] = 0;
            mShouldPlayImmediately[i] = false;
            mLostTrackingSince[i] = -1;
            mLoadRequested[i] = false;
        }
    }

    /** Store the Player Helper object passed from the main activity */
    public void setVideoPlayerHelper(int target, VideoPlayerHelper newVideoPlayerHelper)
    {
        mVideoPlayerHelper[target] = newVideoPlayerHelper;
    }

    
    
    
    public void requestLoad(int target, String movieName, int seekPosition, boolean playImmediately)
    {
        mMovieName[target] = movieName;
        mSeekPosition[target] = seekPosition;
        mShouldPlayImmediately[target] = playImmediately;
        mLoadRequested[target] = true;
    }

    /** Native function for initializing the renderer. */
    public native void initRendering();

    /** Native function to update the renderer */
    public native void updateRendering(int width, int height);

    /** Native function to retrieve the OpenGL texture */
    public native int getVideoTextureID(int target);

    /** Native function to pass down the dimensions of the video */
    public native void setVideoDimensions(int target, float videoWidth, float videoHeight, float[] textureCoordMatrix);

    /** Native call to indicate whether the rendering should display a keyframe or the video data  */
    private native void setStatus(int target, int value);

    /** Native function that informs whether the target is currently being tracked. */
    public native boolean isTracking(int target);

    
    /** Returns the number of registered textures. */
    public int getTrackablesCount()
    {
//    	Log.i("getDupa", "get dupa: " + json_trackables_data.toString() );
        return json_trackables_data.length();
    }
    
    public int getEmtId( int trackable_id )
    {
    	JSONObject trackable_info = getTrackableInfo(trackable_id);
    	int emt_id = -1;
    	try {
			 emt_id = trackable_info.getInt("emt_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	setDefaults("trackable_id", Integer.toString(trackable_id), appContext );
    	
        return emt_id;
    }
    
    private JSONObject getTrackableInfo( int trackable_id ){
    	
//    	getDefaults("trackable_id", appContext );
    	
    	for ( int i =0; i < this.json_trackables_data.length(); i++ ){
   			try {
				JSONObject wiersz = (JSONObject)this.json_trackables_data.get(i);
				int t_id = wiersz.getInt("t_id");
				if ( t_id == trackable_id ){
					return wiersz;
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		}   
    	return null;
    }
    
    
    
    public static void setDefaults(String key, String value, Context context) {
//    	Log.i("setDefaults", "setDefaults seting: " + key + " value: " + value );
    	
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
    	
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String val = preferences.getString(key, null); 
        //Log.i("getDefaults", "getDefaults seting: " + key + " value: " + val );
    	return val;
    }
    
    

    
    /** Called when the surface is created or recreated. */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        // Call native function to initialize rendering:
        // The video texture is also created on this step
        initRendering();

        // Call QCAR function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        QCAR.onSurfaceCreated();

        for (int i = 0; i < VideoPlayback.NUM_TARGETS; i++)
        {

            if (mVideoPlayerHelper[i] != null)
            {
                // The VideoPlayerHelper needs to setup a surface texture given the texture id created in native
                // Here we inform the video player that we would like to play the movie
                // both on texture and on full screen
                // Notice that this does not mean that the platform will be able to do what we request
                // After the file has been loaded one must always check with isPlayableOnTexture() whether
                // this can be played embedded in the AR scene
                if (!mVideoPlayerHelper[i].setupSurfaceTexture(getVideoTextureID(i)))
                    mCanRequestType[i] = MEDIA_TYPE.FULLSCREEN;
                else
                    mCanRequestType[i] = MEDIA_TYPE.ON_TEXTURE_FULLSCREEN;

                // And now check if a load has been requested with the parameters passed from the main activity
                if (mLoadRequested[i])
                {
                	Log.i("onSurfaceCreated","onSurfaceCreated----->"+mMovieName[i]);
                    mVideoPlayerHelper[i].load(mMovieName[i], mCanRequestType[i], mShouldPlayImmediately[i], mSeekPosition[i]);
                    mLoadRequested[i] = false;
                }
            }
        }
    }


    /** Called when the surface changed size. */
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // Call native function to update rendering when render surface parameters have changed:
        updateRendering(width, height);

        // Call QCAR function to handle render surface size changes:
        QCAR.onSurfaceChanged(width, height);

        // Upon every on pause the movie had to be unloaded to release resources
        // Thus, upon every surface create or surface change this has to be reloaded
        // See: http://developer.android.com/reference/android/media/MediaPlayer.html#release()
        for (int i = 0; i < VideoPlayback.NUM_TARGETS; i++)
        {
            if (mLoadRequested[i] && mVideoPlayerHelper[i] != null)
            {
            	Log.i("onSurfaceChanged","onSurfaceChanged----->"+mMovieName[i]);
                mVideoPlayerHelper[i].load(mMovieName[i], mCanRequestType[i], mShouldPlayImmediately[i], mSeekPosition[i]);
                mLoadRequested[i] = false;
            }
        }
    }


    /** Store the Player Helper object passed from the main activity */
    public void setTrackablesData(JSONArray tdata)
    {
        json_trackables_data = tdata;
        //Log.i("setTrackablesData", "setTrackablesData with: " + tdata.toString() );
    }
    
    
    
    /** The native render function. */
    public native void renderFrame();

    /** Called to draw the current frame. */
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;
//VideoPlayback.NUM_TARGETS;
        for (int i = 0; i < VideoPlayback.NUM_TARGETS; i++)
        {
            if (mVideoPlayerHelper[i] != null)
            {
                if (mVideoPlayerHelper[i].isPlayableOnTexture())
                {
                    // First we need to update the video data. This is a built in Android call
                    // Here, the decoded data is uploaded to the OES texture
                    // We only need to do this if the movie is playing
                    if (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING)
                        mVideoPlayerHelper[i].updateVideoData();

                    // According to the Android API (http://developer.android.com/reference/android/graphics/SurfaceTexture.html)
                    // transforming the texture coordinates needs to happen every frame.
                    mVideoPlayerHelper[i].getSurfaceTextureTransformMatrix(mTexCoordTransformationMatrix[i]);
            
                    
//                    if ( i == 25 ){
                    if (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING)
                    setVideoDimensions(i, 
                    		mVideoPlayerHelper[i].getVideoWidth(), 
                    		mVideoPlayerHelper[i].getVideoHeight(), 
                    		mTexCoordTransformationMatrix[i]);
//                    }
                }

                setStatus(i, mVideoPlayerHelper[i].getStatus().getNumericType());
            }
        }

        // Call our native function to render content
        renderFrame();

        for (int i = 0; i < VideoPlayback.NUM_TARGETS; i++)
        {
            // Ask whether the target is currently being tracked and if so react to it
            if (isTracking(i))
            {
                // If it is tracking reset the timestamp for lost tracking
                mLostTrackingSince[i] = -1;
            }
            else
            {
                // If it isn't tracking
                // check whether it just lost it or if it's been a while
                if (mLostTrackingSince[i] < 0)
                    mLostTrackingSince[i] = SystemClock.uptimeMillis();
                else
                {
                    // If it's been more than 2 seconds then pause the player
                    if ((SystemClock.uptimeMillis()-mLostTrackingSince[i]) > 2000)
                    {
                        if (mVideoPlayerHelper[i] != null)
                            mVideoPlayerHelper[i].pause();
                    }
                }
            }
        }

        // If you would like the video to start playing as soon as it starts tracking
        // and pause as soon as tracking is lost you can do that here by commenting
        // the for-loop above and instead checking whether the isTracking() value has
        // changed since the last frame. Notice that you need to be careful not to
        // trigger automatic playback for fullscreen since that will be inconvenient
        // for your users.

    }
}
