package com.lizaalert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class XML_Downloader extends AsyncTask<Void, Void, XmlPullParser> {
	MainActivity obj;
	String ext_dir;

    public XML_Downloader(MainActivity obj) {
    	this.obj = obj;
		Log.d(MainActivity.TAG, "XML_Downloader::XML_Downloader");
		File f = obj.getExternalCacheDir();
		if(f == null){
			Log.e(MainActivity.TAG, "getExternalCacheDir == null");
			f = obj.getCacheDir();
		}
		ext_dir = f.toString();
		Log.d(MainActivity.TAG, ext_dir);
    }

    protected XmlPullParser doInBackground(Void... params) {
    	//String url = urls[0];
		Log.d(MainActivity.TAG, "refresh");
		XmlPullParser parser = LoadXML();
		if(parser == null){
			Log.d(MainActivity.TAG, "parser == null");
			return parser;
		}
		parse_xml(parser);
		/*
		try {
			InputStream in = new java.net.URL("http://s1.radikali.ru/uploads/2017/3/15/166aaaf0259bc951cb552e7b9d6bee3d-full.jpg").openStream();
			result = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e(MainActivity.TAG, "Exception::onPostExecute::" + e.toString());
		}
		*/
		return parser;
    }

	XmlPullParser LoadXML(){
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			//factory.setNamespaceAware(true); // если используется пространство имён
			XmlPullParser parser = factory.newPullParser();
			String url = "http://narod-fl.ru/lost_humans/lost_humans.php";
			Log.d(MainActivity.TAG, url);
			InputStream in = new java.net.URL(url).openStream();
			parser.setInput(new InputStreamReader(in));
			return parser;
		} catch (Exception e) {
			Log.e(MainActivity.TAG, e.toString());
			e.printStackTrace();
		}
		return null;
	}
	
	void parse_xml(XmlPullParser parser){
		obj.lost_humans.clear();
    	//XmlPullParser parser = getResources().getXml(R.xml.lost_humans);
    	/*
// из файла на SD-карты
XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//factory.setNamespaceAware(true); // если используется пространство имён
XmlPullParser parser = factory.newPullParser();
File file = new File(Environment.getExternalStorageDirectory()+ "/sd-contacts.xml");
FileInputStream fis = new FileInputStream(file);
parser.setInput(new InputStreamReader(fis));    	 
    	 */
    	
    	// продолжаем, пока не достигнем конца документа
    	try {
			int event;
			LostHumanItem e = new LostHumanItem();
			String tag = "";
			while ((event = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
				switch(event){
					case XmlPullParser.START_TAG:
						tag = parser.getName();
				    	Log.e(MainActivity.TAG, "TAG" + tag);
						if(tag.equals("entry")){
							e = new LostHumanItem();
						}
						break;
					case XmlPullParser.END_TAG:
						tag = parser.getName();
				    	Log.e(MainActivity.TAG, "/TAG" + tag);
						if(tag.equals("entry")){
					    	Log.e(MainActivity.TAG, "ID=" + e.id + " url=" + e.photo_url);
							e.photo_file = cache_photo(e.id, e.photo_url);
							obj.lost_humans.add(e);
						}
						break;
					case XmlPullParser.TEXT:
						String text = parser.getText();
				    	Log.e(MainActivity.TAG, "text=" + text);
				    	if(tag.equals("id")){
							e.id= text;
				    	}else if(tag.equals("date")){
							e.date = text;
				    	}else if(tag.equals("photo_url")){
							e.photo_url = text;
				    	}else if(tag.equals("src_url")){
							e.src_url = text;
				    	}else if(tag.equals("description")){
							e.description = text;
				    	}else{
					    	Log.i(MainActivity.TAG, "UNKNOUN TAG=" + tag);
				    	}
						break;
				}
			    parser.next();
/*    		
			    if (parser.getEventType() == XmlPullParser.START_TAG
			            && parser.getName().equals("contact")) {
			        list.add(parser.getAttributeValue(0) + " "
			                + parser.getAttributeValue(1) + "\n"
			                + parser.getAttributeValue(2));
			    }
*/    	    
			}
		} catch (Exception e) {
	    	Log.e(MainActivity.TAG, "Exception::parse_xml::" + e.toString());
	    	Log.e(MainActivity.TAG, e.getStackTrace().toString());
		}    	
		
	}

	String cache_photo(String id, String photo_url){
		//id = "14351";
		//photo_url = "http://s020.radikal.ru/i700/1703/2a/cea5ca072ce5.jpg"; // size=39577
		
		Log.d(MainActivity.TAG, "cache_photo");
		File sdCardFile = new File(ext_dir, id + ".jpg");
		Log.d(MainActivity.TAG, "" + sdCardFile.toString());
		if(!sdCardFile.exists()){
			Log.d(MainActivity.TAG, photo_url);
			
			try {
				InputStream in = new java.net.URL(photo_url).openStream();
				sdCardFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(sdCardFile, false);
				byte[] buf = new byte[128];
				while(true){
					int n = in.read(buf);
					if(n == -1){
						break;
					}
					fos.write(buf, 0, n);					
				}
				fos.flush();
				fos.close();
			} catch (Exception e) {
		    	Log.e(MainActivity.TAG, "Exception::cache_photo::" + e.toString());
		    	Log.e(MainActivity.TAG, e.getStackTrace().toString());
			}
		}
		Log.d(MainActivity.TAG, "/cache_photo");
		return sdCardFile.toString();
	}
	
	String load_url(String url){
		try {
			Log.d(MainActivity.TAG, "load_url::" + url);
	        URL u = new URL(url);
	        URLConnection conn = u.openConnection();
	        BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			
	        char [] buf = new char[128];
	        while(true){
	        	int c = reader.read(buf);
	        	if(c <= 0){ 
        			break;
        		}
	        	sb.append(buf, 0, c);
	        }
			Log.e(MainActivity.TAG, "SB_len = " + sb.length());
	        
	        reader.close();
			//Log.d("myLogs", sb.toString());
			return sb.toString();
		} catch (Exception e) {
			Log.e(MainActivity.TAG, "Exception::load_url::" + e.toString());
	    	Log.e(MainActivity.TAG, e.getStackTrace().toString());
		}
		return null;
	}
	//Bitmap result;
	
    protected void onPostExecute(XmlPullParser parser) {
/*
    	try {
			ImageView iv = (ImageView) obj.findViewById(R.id.imageView1);
			iv.setImageBitmap(result);
		} catch (Exception e) {
			Log.e(MainActivity.TAG, "Exception::onPostExecute::" + e.toString());
		}
*/
    	
		LostHumanItem i = obj.lost_humans.get(0);
		Log.e(MainActivity.TAG, "onPostExecute::i.photo_file=" + i.photo_file );

		try {
			FileInputStream is = new  FileInputStream(i.photo_file);
			Bitmap result = BitmapFactory.decodeStream(is);
			//Bitmap result = BitmapFactory.decodeFile(i.photo_file);
			if(result == null){
				Log.e(MainActivity.TAG, "onPostExecute::BitmapFactory Bitmap result == null" );
			}
			ImageView iv = (ImageView) obj.findViewById(R.id.imageView1);
			//iv.setImageDrawable(result);
			iv.setImageBitmap(result);
			TextView tv = (TextView)  obj.findViewById(R.id.textView2);
			tv.setText(i.description);
			tv = (TextView)  obj.findViewById(R.id.textView1);
			tv.setText(i.date);
		} catch (Exception e) {
		}  
		Log.d(MainActivity.TAG, "/refresh");
		
    }
}

//new XML_Downloader((ImageView) findViewById(R.id.imageview)).execute(ImageUrl);
