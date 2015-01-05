package littlebot2014.littlebotdriverstation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import littlebot2014.littlebotdriverstation.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.view.View;

public class VideoManager extends Thread{
	private URL url;
	private Drawable draw;
	private InputStream is;
	private MainActivity activity;
	private View mainView;
	private volatile boolean running = false;
	private Thread videoThread;
	
	public VideoManager(MainActivity activity){
		this.activity = activity;
		mainView = this.activity.findViewById(R.id.llayout);
	}
	
	public void start(){
		videoThread = new Thread(){
			
			@Override
			public void run(){;
			running = true;
			WifiManager wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
			int address = wifi.getConnectionInfo().getIpAddress();
			byte teamNum1 = (byte)((address & 0xff00)>>8);
			byte teamNum2 = (byte)((address & 0xff0000)>>16);
			

						 
				while(running){
					
					try {
						url = new URL("http://10." + Byte.toString(teamNum1) + "." + 
								Byte.toString(teamNum2) + ".11/jpg/image.jpg");
						is = url.openStream();
						 draw = Drawable.createFromStream(is, "image.jpg");
						 activity.runOnUiThread(new Runnable(){
							@SuppressLint("NewApi")
							@Override
							public void run() {
								if(android.os.Build.VERSION.SDK_INT >= 16)
									mainView.setBackground(draw);	//Only UI thread can access
																		//layout views.
								else
									mainView.setBackgroundDrawable(draw);
							} 											
						 });
						 
						 is.close();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				activity.runOnUiThread(new Runnable(){
					@SuppressLint("NewApi")
					@Override
					public void run() {
						mainView.setBackgroundColor(Color.WHITE);
					} 											
				 });
			}
		};
		videoThread.start();
	}
	
	public void stopFeed(){
		running = false;
		videoThread = null;
	}
	
}
