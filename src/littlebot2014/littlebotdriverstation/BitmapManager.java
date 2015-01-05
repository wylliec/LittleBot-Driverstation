package littlebot2014.littlebotdriverstation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapManager {
	
	Bitmap inner_joystick;
	Bitmap outer_joystick;
	Bitmap button_pressed;
	Bitmap button_depressed;
	Bitmap slider_base;
	Bitmap slider_knob;

	public BitmapManager(Context context) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		inner_joystick = BitmapFactory.decodeResource(context.getResources(), R.drawable.littlebot_joystick_inner, options);
		outer_joystick = BitmapFactory.decodeResource(context.getResources(), R.drawable.simple_joystick_outer, options);
		button_pressed = BitmapFactory.decodeResource(context.getResources(), R.drawable.button_depressed, options);
		button_depressed = BitmapFactory.decodeResource(context.getResources(), R.drawable.button_pressed, options);
		slider_base = BitmapFactory.decodeResource(context.getResources(), R.drawable.slider_base, options);
		slider_knob = BitmapFactory.decodeResource(context.getResources(), R.drawable.slider, options);
	}

}
