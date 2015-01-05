package littlebot2014.littlebotdriverstation;

import java.net.DatagramPacket;

import littlebot2014.littlebotdriverstation.WirelessManager.PacketRecievedListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener{
	
	public LLayout layout;
	public WirelessManager wireless;
	public VideoManager video;
	public Trash trash;
	public BitmapManager bitMan;
	public boolean initialized = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	@Override
	public void onStop(){
		super.onPause();
		layout.backup(this);
		wireless.stopIt();
		}
	
	@Override
	public void onStart(){
		super.onStart();
		
		
		wireless = new WirelessManager(this);
		wireless.start();
		
		setContentView(R.layout.activity_main);
		layout = (LLayout)findViewById(R.id.llayout);
		video = new VideoManager(this);
		
		bitMan = new BitmapManager(this);
		trash = (Trash)findViewById(R.id.trash1);
		
		final Dock dock = (Dock)findViewById(R.id.dock1);
		dock.setOnModeChanged(this);
		
		wireless.register(dock.getEnableBttn());
		
		wireless.addPacketRecievedListener
		(new PacketRecievedListener(){
			@Override
			public void packetRecieved(DatagramPacket packet) {
				byte[] data = packet.getData();
				dock.setVoltage(data[1], data[2]);
			}
		});
		
		layout.removeAllViews();
		layout.restore(this);
		
		dock.switcher.setActivated(false);
		layout.setEditMode(false);
		
	}
	
	@Override
	public void onCheckedChanged(CompoundButton unused, boolean checked) {
		layout.setEditMode(checked);
		if(checked)
			trash.setVisibility(View.VISIBLE);
		else
			trash.setVisibility(View.INVISIBLE);
	}
	
	/////Context Menu///////////////////////////
	@Override
	public boolean onContextItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.add_joystick:
			layout.addView(new LJoystick(this, 0, 0));
			break;
		case R.id.add_button:
			layout.addView(new LButton(this, 0, 0));
			break;
		case R.id.add_slider:
			layout.addView(new LSlider(this, 0, 0));
			break;
		}
		return false;
	}
}
