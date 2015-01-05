package littlebot2014.littlebotdriverstation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import littlebot2014.littlebotdriverstation.WirelessManager.PacketRecievedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

public class WirelessManager extends Thread{
	
	public interface PacketRecievedListener{
		void packetRecieved(DatagramPacket packet);
	}
	
	private List<PacketRecievedListener> listeners = new ArrayList<PacketRecievedListener>();
	
	private DatagramPacket CRIOPacket;
	private DatagramPacket DSPacket;
	private CRioPacketAdapter adapter;
	private DatagramSocket sendSocket;
	private DatagramSocket recieveSocket;
	private short packetIndex = 0;
	private Context context;
	private int teamNum = -1;
	public volatile boolean running = false;
	public Timer timer = new Timer();
	
	
	//Constructor
	public WirelessManager(Context context){
		this.context = context;
		WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		byte byteTwo = IntMan.int2(wifi.getConnectionInfo().getIpAddress());
		byte byteOne = IntMan.int3(wifi.getConnectionInfo().getIpAddress());
		teamNum = (byteOne << 8) | byteTwo;
	}
	
	@Override
	public void run(){
		running = true;
		
		synchronized(this) {
			try {
				sendSocket = new DatagramSocket();
				byte[] numArray = new byte[]{
						10, IntMan.int3(teamNum),
						IntMan.int4(teamNum), 2};
				
				InetAddress addr = InetAddress.getByAddress(numArray);
				adapter = new CRioPacketAdapter(context, teamNum);
				CRIOPacket = new DatagramPacket(adapter.data, 1024, addr, 1110);
				
				timer.schedule(new TimerTask(){
					@Override
					public void run() {
						adapter.setIndex(packetIndex);
						adapter.makeCRC();
						CRIOPacket.setData(adapter.data);
						packetIndex++;
						try {
							sendSocket.send(CRIOPacket);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, 0, 20);
				
				receiveThread.start();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void register(LJoystick joystick){
		joystick.setJoystickListener(new LJoystick.JoystickListener() {
			@Override
			public void OnJoyEvent(LJoystick joystick) {
				adapter.setJoystick(joystick.getXAxis(),
						joystick.xAxisNum, joystick.joystick, false);
				adapter.setJoystick(joystick.getYAxis(),
						joystick.yAxisNum, joystick.joystick, true);
			}
		});
	}
	
	public void register(LButton button){
		button.setButtonListener(new LButton.ButtonListener(){
			@Override
			public void onTouch(LButton bttn) {
				adapter.setButton(bttn.joystick, bttn.buttonNum, true);
			}
			@Override
			public void onRelease(LButton bttn) {
				adapter.setButton(bttn.joystick, bttn.buttonNum, false);
			}
		});
	}
	
	public void register(EnableButton button){
		button.setEnabledListener(new EnableButton.EnabledListener(){
			@Override
			public void onEnableChanged(boolean enabled) {
				adapter.setEnabled(enabled);
			}
		});
	}
	
	public void register(LSlider slider){
		slider.setSliderListener(new LSlider.SliderListener() {
			@Override
			public void onValueChanged(int value, int analog) {
				adapter.setAnalog(value, analog);
			}
		});
	}
	
	public void stopIt(){
		timer.cancel();
		timer.purge();
		receiveThread.interrupt();
		running = false;
		receiveThread = null;
	}
	
	
	/////////////////////////Adding receive Thread 09/03/14 ///////////
	Thread receiveThread = new Thread(){
		@Override
		public void run(){
				try {
					recieveSocket = new DatagramSocket(1150);
					DSPacket = new DatagramPacket(new byte[1024], 1024);
					while(running){
						recieveSocket.receive(DSPacket);
						synchronized (this){
							((Activity)context).runOnUiThread(new Runnable(){
								@Override
								public void run() {
									for(PacketRecievedListener l : listeners)
										l.packetRecieved(DSPacket);
								}
							});
						}
					}
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	};
	
	public void addPacketRecievedListener(PacketRecievedListener listener) {
		listeners.add(listener);
	}
}
