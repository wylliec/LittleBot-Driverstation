package littlebot2014.littlebotdriverstation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class LJoystick extends LView{
	
	private JoystickListener joyList;
	public int joystick;
	public int xAxisNum;
	public int yAxisNum;
	
	private byte xAxis = 0;
	private byte yAxis = 0;
	private float virtualX = 0;
	private float virtualY = 0;
	private float actualX;
	private float actualY;
	private Bitmap inner;
	private Bitmap outer;
	private int outerRadius;
	private int innerRadius;
	
	private int width;
	private int height;
	
	private Context context;
	
	static Dialog dialog;

	public LJoystick(Context context, int x, int y) {
		super(context, x, y);
		this.context = context;
		setVisibility(INVISIBLE);
		showDialog();
		
		BitmapManager bitMan = ((MainActivity)context).bitMan;
		
		inner = bitMan.inner_joystick;
		outer = bitMan.outer_joystick;
		
		
		
		((MainActivity)context).wireless.register(this);
	}
	
	public LJoystick(Context context, int[] attrs){
		super(context,0 ,0);
		this.context = context;
		setX(attrs[0]);
		setY(attrs[1]);
		setLayoutParams(new LayoutParams(attrs[2], attrs[3]));
		joystick = attrs[4];
		yAxisNum = attrs[5];
		xAxisNum = attrs[6];
		
		BitmapManager bitMan = ((MainActivity)context).bitMan;
		inner = bitMan.inner_joystick;
		outer = bitMan.outer_joystick;
		
		((MainActivity)context).wireless.register(this);
	}
	
	
	@Override
	public void onDraw(Canvas canvas){
		canvas.drawBitmap(outer, 0, 0, null);
		canvas.drawBitmap(inner,actualX-innerRadius, actualY-innerRadius, null);
	}
	
	public byte getXAxis(){
		return xAxis;
	}
	public byte getYAxis(){
		return yAxis;
	}
	
	public void setJoystickListener(JoystickListener joystickListener){
		joyList = joystickListener;
	}
	
	public void setBitmaps(Bitmap inner, Bitmap outer){
		if(inner != null)
			this.inner = inner;
		if(outer != null)
			this.outer = outer;
		onSizeChanged(width, height, width, height);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		if(w<h){
			outerRadius = w/2;
			innerRadius = w/4;
		}else{
			outerRadius = h/2;
			innerRadius = h/4;
		}
		actualX = outerRadius;
		actualY = outerRadius;
		
		inner = Bitmap.createScaledBitmap(inner, innerRadius*2, innerRadius*2, false);
		outer = Bitmap.createScaledBitmap(outer, outerRadius*2, outerRadius*2, false);
		
		width = w;
		height = h;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if(!isFrozen){
			actualX = event.getX();
			actualY = event.getY();
			virtualX = actualX - outerRadius;
			virtualY = outerRadius-actualY;
			int radius = outerRadius-innerRadius;
			double angle= Math.atan(virtualY/virtualX);
			
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:	//Fall through ACTION_MOVE case
			case MotionEvent.ACTION_MOVE:
				if(Math.sqrt(Math.pow(virtualX, 2)+Math.pow(virtualY, 2))
						>= radius){
					
					if(virtualY > 0 && virtualX > 0){
						virtualX = (float) (Math.cos(angle)*radius);
						virtualY = (float) (Math.sin(angle)*radius);
					}else if(virtualY > 0 && virtualX < 0){
						virtualX = (float) -Math.abs(Math.cos(angle)*radius);
						virtualY = (float) Math.abs(Math.sin(angle)*radius);
					}else if(virtualY < 0 && virtualX < 0){
						virtualX = (float) -Math.abs(Math.cos(angle)*radius);
						virtualY = (float) -Math.abs(Math.sin(angle)*radius);
					}else if(virtualY < 0 && virtualX > 0){
						virtualX = (float) (Math.cos(angle)*radius);
						virtualY = (float) -Math.abs(Math.sin(angle)*radius);
					}else{
						virtualX = 0;
						virtualY = 0;
					}
					actualX = virtualX + outerRadius;
					actualY = outerRadius-virtualY ;
				}
				break;
				
			case MotionEvent.ACTION_UP:
				virtualX = 0;
				virtualY = 0;
				actualX = virtualX + outerRadius;
				actualY = outerRadius-virtualY ;
				break;
			}
			invalidate();
			xAxis = (byte) ((127f/radius)*virtualX);
			yAxis = (byte) ((127f/radius)*virtualY);
			
			if(joyList != (null))
				joyList.OnJoyEvent(this);
		}
		
		
		
		return true;
	}
	
	public void showDialog(){
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		
		ViewGroup view = (ViewGroup)inflater.inflate(R.layout.joystick_dialog, null);
		final Spinner selectJoy = (Spinner)view.findViewById(R.id.joystick);
		final Spinner selectX = (Spinner)view.findViewById(R.id.x_axis);
		final Spinner selectY = (Spinner)view.findViewById(R.id.y_axis);
		final TextView radiusV = (TextView)view.findViewById(R.id.joy_radius);
		
		ArrayAdapter<Integer> joyAdapter = 
				new ArrayAdapter<Integer>(context, android.R.layout.select_dialog_item,
						ListFactory.create(new int[]{1,2,3,4}));
		
		selectJoy.setAdapter(joyAdapter);
		
		ArrayAdapter<Integer> xAdapter = 
				new ArrayAdapter<Integer>(context, android.R.layout.select_dialog_item,
						ListFactory.create(new int[]{1,2,3,4,5,6}));
		selectX.setAdapter(xAdapter);
		
		ArrayAdapter<Integer> yAdapter = 
				new ArrayAdapter<Integer>(context, android.R.layout.select_dialog_item,ListFactory.create(new int[]{1,2,3,4,5,6}));
		selectY.setAdapter(yAdapter);
		selectY.setSelection(1);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				joystick = (Integer)selectJoy.getSelectedItem();
				xAxisNum = (Integer)selectX.getSelectedItem();
				yAxisNum = (Integer)selectY.getSelectedItem();
				
				int radius;
				try{
					radius = Integer.parseInt(radiusV.getText().toString());
				}catch(NumberFormatException e){
					radius = 100;
				}
				
				
				setLayoutParams(new LayoutParams(radius,radius));
				setVisibility(VISIBLE);
			}
		});
		builder.setCancelable(false);
		builder.show();
	}
	
	public String toString(){
		return "Joystick";
	}
	
	
	//Joystick Interface
	interface JoystickListener{
		public void OnJoyEvent(LJoystick joystick);
	}
	}
