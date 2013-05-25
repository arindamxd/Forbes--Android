  

package com.qualcomm.QCARSamples.VideoPlayback;

import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ButtonsView extends View
                                                    
{
    public ButtonsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
//		    try
//		    {
		        // register our interest in hearing about changes to our surface
//		        SurfaceHolder holder = getHolder();
//		        holder.addCallback(this);

		        View.inflate(context, R.layout.buttons_view, null);
//		    }
		    
		// TODO Auto-generated constructor stub
//		 = getLayoutInflater().inflate(
//	            R.layout.interface_overlay, null);
				
	}

 

   }
