package littlebot2014.littlebotdriverstation;

import java.net.DatagramPacket;

import littlebot2014.littlebotdriverstation.WirelessManager.PacketRecievedListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class Dock extends LinearLayout{
	
	private EnableButton enable;
	private TextView voltage;
	public ModeSwitcher switcher;
	private Switch video;

	public Dock(final Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundResource(android.R.drawable.dark_header);
		setPadding(10,5,5,5);
		addView(switcher = new ModeSwitcher(context));
		addView(enable = new EnableButton(context));
		
		voltage = new TextView(context);
		voltage.setText("0V");
		voltage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		voltage.setTextColor(Color.WHITE);
		addView(voltage);
		
		video = new Switch(context);
		video.setTextOn("Video On");
		video.setTextOff("Video Off");
		video.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton bttn, boolean on) {
				if(on)
					((MainActivity)context).video.start();
				else{
					((MainActivity)context).video.stopFeed();
				}	
			}
		});
		
		addView(video);
	}
	
	public void setVoltage(byte whole, byte decimal){
		voltage.setText(Integer.toHexString(whole) + "." + Integer.toHexString(decimal) + "V");
	}
	
	public void setEnabledListener(EnableButton.EnabledListener list){
		enable.setEnabledListener(list);
	}
	
	public void setOnModeChanged(OnCheckedChangeListener list){
			switcher.setOnCheckedChangeListener(list);
	}
	
	public EnableButton getEnableBttn(){
		return enable;
	}
	
	public ModeSwitcher getModeSwtr(){
		return switcher;
	}
}
