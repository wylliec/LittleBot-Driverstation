package littlebot2014.littlebotdriverstation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;


public class LButton extends LView{
	public int joystick;
	public int buttonNum;
	public String name = "1J1";
	private Bitmap pressedBmp;
	private Bitmap depressedBmp;
	private Paint paint = new Paint();
	private Rect textBounds = new Rect();
	
	private Context context;
	private LayoutInflater inflater;
	
	
	public interface ButtonListener{
		public void onTouch(LButton bttn);
		public void onRelease(LButton bttn);
	}
	
	ButtonListener listener;
	
	int activeC = Color.LTGRAY;
	int inactiveC = Color.DKGRAY;
	
	public LButton(Context context, int x, int y) {
		super(context, x, y);
		this.context = context;
		setVisibility(View.INVISIBLE);
		inflater = (LayoutInflater)context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		BitmapManager bitMan = ((MainActivity)context).bitMan;
		
		pressedBmp = bitMan.button_pressed;
		depressedBmp = bitMan.button_depressed;
		
		setBackgroundResource(R.drawable.button_pressed);
		
		showDialog();
		
		
		((MainActivity)context).wireless.register(this);
	}
	
	public LButton(Context context, int[] attrs) {
		super(context, 0, 0);
		this.context = context;

		setX(attrs[0]);
		setY(attrs[1]);
		setLayoutParams(new LayoutParams(attrs[2], attrs[3]));
		joystick = attrs[4];
		buttonNum = attrs[5];
		
		name = Integer.toString(buttonNum) + "J" + Integer.toString(joystick);
		
		BitmapManager bitMan = ((MainActivity)context).bitMan;
		
		pressedBmp = bitMan.button_pressed;
		depressedBmp = bitMan.button_depressed;
		
		setBackgroundResource(R.drawable.button_pressed);
		
		((MainActivity)context).wireless.register(this);
	}

	
	public void setButtonListener(ButtonListener buttonListener){
		listener = buttonListener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		if(!isFrozen){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				setBackgroundResource(R.drawable.button_depressed);
				if(listener != null)
					listener.onTouch(this);
				break;
			case MotionEvent.ACTION_UP:
				setBackgroundResource(R.drawable.button_pressed);
				if(listener != null)
					listener.onRelease(this);
				break;
			}
		}
		return true;
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		//pressedBmp = Bitmap.createScaledBitmap(pressedBmp, w, h, false);
		//depressedBmp = Bitmap.createScaledBitmap(depressedBmp, w, h, false);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		paint.setTextSize(getHeight()/2);
		paint.setColor(Color.WHITE);
		paint.getTextBounds(name, 0, name.length(), textBounds);
		
		int x = (getWidth()-(textBounds.width()))/2;
		int y = (getHeight()/2)+(Math.abs(textBounds.top/2));
		canvas.drawText(name, x, y, paint);
	}
	
	public String toString(){
		return "Button";
	}
	
	
	public void showDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		ViewGroup view = (ViewGroup)inflater.inflate(R.layout.button_dialog, null);
		
		final TextView widthV = (TextView)view.findViewById(R.id.button_width);
		final TextView heightV = (TextView)view.findViewById(R.id.button_height);
		final Spinner joystickV = (Spinner)view.findViewById(R.id.joystickNum);
		final Spinner buttonV = (Spinner)view.findViewById(R.id.buttonNum);
		
		ArrayAdapter<Integer> joyAdapter = 
				new ArrayAdapter<Integer>(context, android.R.layout.select_dialog_item,
						ListFactory.create(new int[]{1,2,3,4}));
		ArrayAdapter<Integer> buttonAdapter = 
				new ArrayAdapter<Integer>(context, android.R.layout.select_dialog_item,
						ListFactory.create(new int[]{1,2,3,4,5,6,7,8,9,10
								,11,12,13,14,15,16}));
		
		joystickV.setAdapter(joyAdapter);
		buttonV.setAdapter(buttonAdapter);
		
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
				buttonNum = (Integer)buttonV.getSelectedItem();
				name = Integer.toString(buttonNum) + "J" + Integer.toString(joystick);
				
				setLayoutParams(new LayoutParams(width,height));
				
				
				setVisibility(VISIBLE);
			}
		});
		builder.setCancelable(false);
		builder.show();
	}

}
