package littlebot2014.littlebotdriverstation;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EnableButton extends ToggleButton implements OnCheckedChangeListener{
	
	public interface EnabledListener{
		public void onEnableChanged(boolean enabled);
	}
	
	EnabledListener listener;

	public EnableButton(Context context) {
		super(context);
		setText("Dissabled");
		setTextOn("Enabled");
		setTextOff("Dissabled");
		setTextColor(Color.RED);
		setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		setBackgroundColor(Color.argb(0, 0, 0, 0));
		setOnCheckedChangeListener(this);
	}
	
	public void setEnabledListener(EnabledListener list){
		listener = list;
	}

	@Override
	public void onCheckedChanged(CompoundButton bttn, boolean checked) {
		if(checked){
			setTextColor(Color.GREEN);
			setText("Dissabled");
		}else{
			setTextColor(Color.RED);
			setText("Enabled");
		}
		if(listener != null)
			listener.onEnableChanged(checked);
	}
}
