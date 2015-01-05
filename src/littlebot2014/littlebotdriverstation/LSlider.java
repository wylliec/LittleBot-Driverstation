package littlebot2014.littlebotdriverstation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.graphics.Paint;

public class LSlider extends LView{
	
	public interface SliderListener{
		public void onValueChanged(int value, int analog);
	}
	private SliderListener listener;
	
	public int MAX = 1023;
	public int joystick = 0;
	public int analogNum = 0;
	private int value = 0;
	
	private Bitmap base;
	private Bitmap slider;
	private Context context;
	
	private int offset;
	private Paint textPaint;
	Rect textBounds = new Rect();
	
	public LSlider(Context context, int[] attrs) {
		super(context, 0, 0);
		this.context = context;
		
		setX(attrs[0]);
		setY(attrs[1]);
		setLayoutParams(new LayoutParams(attrs[2], attrs[3]));
		joystick = attrs[4];
		analogNum = attrs[5];
		
		BitmapManager bitMan = ((MainActivity)context).bitMan;
		base = bitMan.slider_base;
		slider = bitMan.slider_knob;
		
		textPaint = new Paint();
		textPaint.getTextBounds("0000", 0, 3 ,textBounds);
		
		((MainActivity)context).wireless.register(this);
	}
	
	public LSlider(Context context, int x, int y) {
		super(context, x, y);
		this.context = context;
		setVisibility(View.INVISIBLE);
		showDialog();
		
		BitmapManager bitMan = ((MainActivity)context).bitMan;
		base = bitMan.slider_base;
		slider = bitMan.slider_knob;
		
		textPaint = new Paint();
		textPaint.getTextBounds("0000", 0, 3 ,textBounds);
		
		((MainActivity)context).wireless.register(this);
	}
	
	

	
	@Override
	public void onDraw(Canvas canvas){
		canvas.drawBitmap(base, 0, 0, null);
		
		canvas.drawBitmap(slider, 0, offset, null);
		String valueStr = Integer.toString(value);
		int textX = (getWidth() - textBounds.width())/2;
		int textY = getHeight() - textBounds.height();
		canvas.drawText(valueStr, 0, textY, textPaint);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		slider = Bitmap.createScaledBitmap(slider, w, (int)(getHeight()/5), false);
		base = Bitmap.createScaledBitmap(base, w, h, false);
		if(value == 0)
			offset = base.getHeight()- slider.getHeight();
		if(w < textBounds.width()*1.5){
			setLayoutParams(new LayoutParams((int) (textBounds.width()*1.5), h));
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		if(!isFrozen){
			if(event.getY() >= 0 && event.getY() <= base.getHeight()-slider.getHeight()){
				value = (int) ((double)MAX + slider.getHeight() - ((double)(MAX)/(double)(base.getHeight()
						-slider.getHeight())*(double)offset));
			
			if((value <= MAX && event.getY() < getY()) ||
					value >= 0 && event.getY() > getY())
					offset = (int)event.getY();
			
			if(value > MAX)
				value = MAX;
			if(value < 0)
				value = 0;
			}else if(event.getY() < 0){
				value = MAX;
				offset = 0;
			}
			else if(event.getY() > getHeight()-slider.getHeight()){
				value = 0;
				offset = getHeight() - slider.getHeight();
			}
			invalidate();
			
			if(listener != null){
				listener.onValueChanged(value, analogNum);
			}
		}
		return true;
	}
	
	public void showDialog(){
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		ViewGroup view = (ViewGroup)inflater.inflate(R.layout.slider_dialog, null);
		
		final TextView widthV = (TextView)view.findViewById(R.id.slider_width);
		final TextView heightV = (TextView)view.findViewById(R.id.slider_height);
		final Spinner joystickV = (Spinner)view.findViewById(R.id.slider_joystick);
		final Spinner analogV = (Spinner)view.findViewById(R.id.slider_analog);
		
		ArrayAdapter<Integer> joyAdapter = 
				new ArrayAdapter<Integer>(context, android.R.layout.select_dialog_item,
						ListFactory.create(new int[]{1,2,3,4}));
		ArrayAdapter<Integer> analogAdapter = 
				new ArrayAdapter<Integer>(context, android.R.layout.select_dialog_item,
						ListFactory.create(new int[]{1,2,3,4}));
		
		joystickV.setAdapter(joyAdapter);
		analogV.setAdapter(analogAdapter);
		
		builder.setView(view);
		builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				int width;
				int height;
				
				try{
					width = Integer.parseInt(widthV.getText().toString());
				}catch(NumberFormatException e){
					width = 100;
				}
				
				try{
					height = Integer.parseInt(heightV.getText().toString());
				}catch(NumberFormatException e){
					height = 100;
				}
				
				
				joystick = (Integer)joystickV.getSelectedItem();
				analogNum = (Integer)analogV.getSelectedItem();
				
				setLayoutParams(new LayoutParams(width,height));
				
				
				setVisibility(VISIBLE);
			}
		});
		builder.setCancelable(false);
		builder.show();
	}
	
	public void setSliderListener(SliderListener list){
		listener = list;
	}

}
