package com.ARFixer.ForbesPlus;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.os.Environment;
import android.util.Log;

public class Downloader {

    public static String DownloadFile(String fileURL, String fileName) {
        try {

			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 0 ");
        	File sdCard = Environment.getExternalStorageDirectory();
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 1 ");
        	File dir = new File (sdCard.getAbsolutePath() + "/forbes");
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 2 ");
        	dir.mkdirs();
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 3 ");
        	
        	
        	
            URL u = new URL(fileURL);
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 4 " + fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 5 ");
            c.setRequestMethod("GET");
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 6 ");
            c.setDoOutput(true);
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 7 ");
            c.connect();
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 8 ");
            FileOutputStream f = new FileOutputStream(new File(dir, fileName));

			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 9 ");
            InputStream in = c.getInputStream();
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 10 ");

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
			Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 11 ");
            return dir.getPath()+"/"+fileName;
        } catch (Exception e) {
            Log.i("Downloader", "Error - error - error - error");
        }

		Log.i("!!!!!! DownloadFile  ", "!!!!!! DownloadFile 8 ");
        return "";
    }
    
    
    public static String unzip(String filename) {
    	String retfile = "";
        try {
//          String filename = download(fileUrl);
          ZipFile zip = new ZipFile(filename);
          Enumeration<? extends ZipEntry> zippedFiles = zip.entries();
          while (zippedFiles.hasMoreElements()) {
            ZipEntry entry = zippedFiles.nextElement();
            InputStream is = zip.getInputStream(entry);
            String name = entry.getName();
            File sdCard = Environment.getExternalStorageDirectory();
        	File dir = new File (sdCard.getAbsolutePath() + "/forbes/");
        	
            File outputFile = new File(dir.getPath() + "/"+name);
            Log.i("unzip: ", outputFile.getPath() );
            String outputPath = outputFile.getCanonicalPath();
            name = outputPath.substring(outputPath.lastIndexOf("/") + 1);
            outputPath = outputPath.substring(0, outputPath.lastIndexOf("/"));
            File outputDir = new File(outputPath);
            //Log.i("unzip outputPath: ", outputPath );
            //Log.i("unzip: ", outputDir.getPath() );
            outputDir.mkdirs();
            outputFile = new File(outputPath, name);
            outputFile.createNewFile();
            FileOutputStream out = new FileOutputStream(outputFile);
            retfile = outputFile.getPath();
            byte buf[] = new byte[16384];
            do {
              int numread = is.read(buf);
              if (numread <= 0) {
                break;
              } else {
                out.write(buf, 0, numread);
              }
            } while (true);

            is.close();
            out.close();
          }
          File theZipFile = new File(filename);
          theZipFile.delete();
          
          
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return retfile;
      }
    
}