package com.qualcomm.QCARSamples.VideoPlayback;

import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

public class Slownik {
	//CONSTANTS
    public static final int SHOW_BUY_BUTTON = 0;
    public static final int HIDE_BUY_BUTTON = 1;
    public static final int SHOW_VIDEO_BUTTON = 2;
    public static final int HIDE_VIDEO_BUTTON = 3;
    public static final int SHOW_WWW_BUTTON = 4;
    public static final int HIDE_WWW_BUTTON = 5;
    public static final int HIDE_ALL_BUTTON = 6;
    public static final int SHOW_RATING = 7;
    public static final int HIDE_RATING = 8;
    public static final int SHOW_VIDEO_BUY_BUTTON = 9;
    public static final int SHOW_VIDEO_WWW_BUTTON = 10;
    public static final int START = 11;
    public static final int CHANGE_LOTNISKO_BUTTON_NAME = 12;
    public static final int CHANGE_TERMIN_BUTTON_NAME = 13;
    public static final int CHANGE_WYZYWIENIE_BUTTON_NAME = 14;
    public static final int CHANGE_OSOBY_BUTTON_NAME = 15;
    public static final int CHANGE_DLUGOSC_BUTTON_NAME = 16;
    
	public static final int ACTION_CLICKTOGO = 100;
    public static final int ACTION_GALERY = 101;
    public static final int ACTION_VIDEO = 102;
    public static final int ACTION_MAPA = 103;
    public static final int ACTION_CLEAR = 104;
    public static final int ACTION_POGODA = 105;
    public static final int ACTION_WWWDD = 106;
    public static final int ACTION_AUDIO = 107;
    public static final int ACTION_ACTIVATELIB = 108;
    public static final int ACTION_ARTYKUL = 109;
    public static final int ACTION_PDF = 110;

    
  //  public static final int ACTION_CENA = 108;
  //  public static final int ACTION_ZMIENTERMIN = 109;
   // public static final int ACTION_ZMIENLOTNISKO = 110;
    public static final int ACTION_CENAWYNIK = 111;
    public static final int ACTION_WWW = 112;
    public static final int ACTION_ZMIENWYZYWIENIE = 113;
    public static final int ACTION_ZMIENOSOBY = 114;
    public static final int ACTION_ZMIENDLUGOSC = 115;
    public static final int ACTION_D1 = 116;
    public static final int ACTION_D2 = 117;
    public static final int ACTION_D3 = 118;
    public static final int ACTION_D4 = 119;
    public static final int ACTION_ZMIENPREMIUM = 120;
    public static final int ACTION_POBIERZAKTYWUJ = 121;
    public static final int ACTION_ZMIENWYBIERZKATALOG = 122;
    
    
    public static final int WINDOWS_SET_CLEAR = 1000;
    public static final int WINDOWS_SET_TOUCHTOGO = 1001;
    public static final int WINDOWS_SET_GALERY = 1002;
    public static final int WINDOWS_SET_VIDEO = 1003;
    public static final int WINDOWS_SET_MAPA = 1004;
    public static final int WINDOWS_SET_LIBRARY = 1005;
    public static final int WINDOWS_SET_PREMIUM = 1013;
    public static final int WINDOWS_SET_DOWNLOADING = 1014;
    public static final int WINDOWS_SET_ZMIENWYBIERZKATALOG = 1015;
    
    public static final String URL_MINIATURA_TRACK_BASE = "http://detal.arfixer.com/img_t"; //1/104_mini.jpg
    
    static int getActionName ( int znacznik_id ){
	    switch ( znacznik_id ){
			case 1: return ACTION_WWW;  //WWW
			case 2: return ACTION_VIDEO;  //VIDEO
			case 3: return ACTION_GALERY;  //Galeria
			case 4: return ACTION_MAPA;  //Mapa
			case 5: return ACTION_AUDIO;  //Audio
			case 6: return ACTION_ARTYKUL;  //Audio
			case 7: return ACTION_PDF;  //PDF
	    }
		return 0;
    }
    
    
    static int getActiveDrawableForButton( int typ_g_id ){
    	switch ( typ_g_id ){
			case 3:  return R.drawable.btn_artykul;
			case 4:  return R.drawable.btn_audio;
			case 6:  return R.drawable.btn_facebook;
			case 7:  return R.drawable.btn_galeria;
			case 9:  return R.drawable.btn_kontakt;
			case 11: return R.drawable.btn_link;
			case 12:  return R.drawable.btn_mapa;
			case 14:  return R.drawable.btn_pdf;
			case 15:  return R.drawable.btn_polecamy;
			case 16:  return R.drawable.btn_reklama;
			case 18:  return R.drawable.btn_video;
			case 31:  return R.drawable.btn_ankieta;
			default: return R.drawable.btn_none;
			
	        
				
		}
	}
        
}
