package littlebot2014.littlebotdriverstation;

import java.net.DatagramPacket;

import littlebot2014.littlebotdriverstation.WirelessManager.PacketRecievedListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

public class LDisplay extends LView{
	
	private int currentValue = 0;
	private int MAX = 65536;
	public int analogInput = 0;
	public float multiplier = 1;
	
	private Paint progressP = new Paint();
	private Paint circleP = new Paint();
	private Paint textP = new Paint();
	
	Context context;
	LayoutInflater inflater;
	

	public LDisplay(Context context, int x, int y) {
		super(context, x, y);
		this.context = context;
		inflater = (LayoutInflater)context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		setVisibility(View.INVISIBLE);
		
		progressP.setColor(Color.BLUE);
		progressP.setStyle(Style.FILL);
		progressP.setAlpha(150);
		
		circleP.setStyle(Style.FILL);
		circleP.setColor(Color.GREEN);
		circleP.setAlpha(150);
		
		showDialog();
		
		((MainActivity)context).wireless.addPacketRecievedListener
		(new PacketRecievedListener(){
			@Override
			public void packetRecieved(DatagramPacket packet) {
				byte[] data = packet.getData();
				setValue((data[1] << 8) | data[2]);
			}
		});
		
	}
	
	public LDisplay(Context context, int[] attrs, float mult) {
		super(context, 0, 0);
		this.context = context;
		
		setX(attrs[0]);
		setY(attrs[1]);
		setLayoutParams(new LayoutParams(attrs[2], attrs[3]));
		analogInput = attrs[4];
		multiplier = mult;
		
		progressP.setColor(Color.BLUE);
		progressP.setStyle(Style.FILL);
		
		circleP.setStyle(Style.FILL);
		circleP.setColor(Color.GREEN);
		
		((MainActivity)context).wireless.addPacketRecievedListener
		(new PacketRecievedListener(){
			@Override
			public void packetRecieved(DatagramPacket packet) {
				byte[] data = packet.getData();
				setValue((data[1] << 8) | data[2]);
			}
		});
	}
	
	public void setValue(int value){
		currentValue = value;
		invalidate();
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas){
		
		canvas.drawArc(new RectF(0,0,getWidth(), getHeight())
		, 180, (int)((180f/MAX)*currentValue), true, progressP);
		
		
		canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/4,circleP);
	    
		Rect bounds = new Rect();
		
		String text = Integer.toString(currentValue);

		textP.getTextBounds(text, 0, text.length(), bounds);
		
		int tx = (getWidth()/2)-(bounds.width()/2);
		int ty = (getHeight()/2)+(bounds.height()/2);
		
		canvas.drawText(text, tx, ty, textP);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		
		if(w>h)
			setLayoutParams(new LayoutParams(h,h));
		else
			setLayoutParams(new LayoutParams(w,w));
		
		textP.setTextSize(getWidth()/3);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		return true;
	}
	
	public void showDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		ViewGroup view = (ViewGroup)inflater.inflate(R.layout.display_dialog, null);
		
		final Spinner input = (Spinner) view.findViewById(R.id.display_input);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				context, android.R.layout.select_dialog_item, 
				ListFactory.create(new String[]{"1","2","3","4","5","6", "Voltage"}));
		input.setAdapter(adapter);
		
		final TextView size = (TextView)view.findViewById(R.id.display_size);
		
		final TextView multiplierV = (TextView)view.findViewById(R.id.display_multiplier);
		
		builder.setView(view);
		builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				analogInput = Integer.parseInt((String)input.getSelectedItem());
				
				int sizeI;
				
				try{
					sizeI = Integer.parseInt(size.getText().toString());
				}catch(NumberFormatException e){
					sizeI = 100;
				}
					
				if(sizeI > 10)
					setLayoutParams(new LayoutParams(sizeI,sizeI));
				
				try{
					multiplier = Float.parseFloat(multiplierV.getText().toString());
				}catch(NumberFormatException e){
					multiplier = 1;
				}
				setVisibility(VISIBLE);
			}
		});
		builder.setCancelable(false);
		builder.show();
	}
}
