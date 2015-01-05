package littlebot2014.littlebotdriverstation;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;

public class ModeSwitcher extends Switch {
	
	public ModeSwitcher(Context context) {
		super(context);
		
		setTextOn("Edit");
		setTextOff("Control");
		setLayoutParams(new LayoutParams(200, 25));
		setX(0);
		setY(0);
	}
	
}

