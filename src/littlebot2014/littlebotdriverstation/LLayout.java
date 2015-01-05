package littlebot2014.littlebotdriverstation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;;

public class LLayout extends FrameLayout{
	
	private Context context;
	public boolean isEdit = false;
	
	public LLayout(Context context, AttributeSet attr) {
		super(context, attr);
		this.context = context;
		}
	
	public void setEditMode(boolean set){
		//In both loops skip first child, which is mode switch.
		if(set){
			isEdit = true;
			((Activity)context).registerForContextMenu(this);
			for(int i = 0; i < getChildCount(); i++)
				((LView)getChildAt(i)).isFrozen = true;
		}else{
			isEdit = false;
			((Activity)context).unregisterForContextMenu(this);
			for(int i = 0; i < getChildCount(); i++)
				((LView)getChildAt(i)).isFrozen = false;
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu){
		if(isEdit){
			super.onCreateContextMenu(menu);
			((Activity)context).getMenuInflater().inflate(R.menu.add_menu, menu);
		}
	}
	
	/////Backup & Restore//////
			
	
	public void backup(Activity activity){
		backupJoysticks(activity);
		backupButtons(activity);
		backupSliders(activity);
		backupDisplays(activity);
	}
	
	public void restore(Activity activity){
		restoreJoysticks(activity);
		restoreButtons(activity);
		restoreSliders(activity);
		restoreDisplays(activity);
	}
	
	public void backupJoysticks(Activity activity){
		SharedPreferences prefs = (activity)
				.getSharedPreferences("lds_joysticks", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.clear();
		int index = 0;
		for(int i = 0; i < getChildCount(); i++){
			View view = getChildAt(i);
			if(view.getClass().equals(LJoystick.class)){
				editor.putInt(Integer.toString(index++), (int)view.getX());
				editor.putInt(Integer.toString(index++), (int)view.getY());
				editor.putInt(Integer.toString(index++), view.getWidth());
				editor.putInt(Integer.toString(index++), view.getHeight());
				editor.putInt(Integer.toString(index++), ((LJoystick)view).joystick);
				editor.putInt(Integer.toString(index++), ((LJoystick)view).xAxisNum);
				editor.putInt(Integer.toString(index++), ((LJoystick)view).yAxisNum);
			}
		}
		editor.commit();
	}
	
	public void restoreJoysticks(Activity activity){
		SharedPreferences prefs = (activity)
				.getSharedPreferences("lds_joysticks", Context.MODE_PRIVATE);
		List<Integer> values = new ArrayList<Integer>();
		int value;
		int collectionSize = prefs.getAll().size();
		for(int i = 0; i < collectionSize; i++){
			value = prefs.getInt(Integer.toString(i), Integer.MAX_VALUE);
			values.add((value != Integer.MAX_VALUE)?value:0);
		}
			
		
		int[] temp = new int[7];
		for(int i = 0; i < values.size(); i += temp.length){
			for(int k = 0; k < temp.length; k++)
				temp[k] = (Integer) values.get(i+k);
			addView(new LJoystick(activity, temp));
		}
		
	}
	
	
	public void backupButtons(Activity activity){
		SharedPreferences prefs = (activity)
				.getSharedPreferences("lds_buttons", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.clear();
		int index = 0;
		for(int i = 0; i < getChildCount(); i++){
			View view = getChildAt(i);
			if(view.getClass().equals(LButton.class)){
				editor.putInt(Integer.toString(index++), (int)view.getX());
				editor.putInt(Integer.toString(index++), (int)view.getY());
				editor.putInt(Integer.toString(index++), view.getWidth());
				editor.putInt(Integer.toString(index++), view.getHeight());
				editor.putInt(Integer.toString(index++), ((LButton)view).joystick);
				editor.putInt(Integer.toString(index++), ((LButton)view).buttonNum);
			}
		}
		editor.commit();
	}
	
	public void restoreButtons(Activity activity){
		SharedPreferences prefs = (activity)
				.getSharedPreferences("lds_buttons", Context.MODE_PRIVATE);
		List<Integer> values = new ArrayList<Integer>();
		int value;
		int collectionSize = prefs.getAll().size();
		for(int i = 0; i < collectionSize; i++){
			value = prefs.getInt(Integer.toString(i), Integer.MAX_VALUE);
			values.add((value != Integer.MAX_VALUE)?value:0);
		}
			
		
		int[] temp = new int[6];
		for(int i = 0; i < values.size(); i += temp.length){
			for(int k = 0; k < temp.length; k++)
				temp[k] = (Integer) values.get(i+k);
			addView(new LButton(activity, temp));
		}
		
	}
	
	public void backupSliders(Activity activity){
		SharedPreferences prefs = (activity)
				.getSharedPreferences("lds_sliders", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.clear();
		int index = 0;
		for(int i = 0; i < getChildCount(); i++){
			View view = getChildAt(i);
			if(view.getClass().equals(LSlider.class)){
				editor.putInt(Integer.toString(index++), (int)view.getX());
				editor.putInt(Integer.toString(index++), (int)view.getY());
				editor.putInt(Integer.toString(index++), view.getWidth());
				editor.putInt(Integer.toString(index++), view.getHeight());
				editor.putInt(Integer.toString(index++), ((LSlider)view).joystick);
				editor.putInt(Integer.toString(index++), ((LSlider)view).analogNum);
			}
		}
		editor.commit();
	}
	
	public void restoreSliders(Activity activity){
		SharedPreferences prefs = (activity)
				.getSharedPreferences("lds_sliders", Context.MODE_PRIVATE);
		List<Integer> values = new ArrayList<Integer>();
		int value;
		int collectionSize = prefs.getAll().size();
		for(int i = 0; i < collectionSize; i++){
			value = prefs.getInt(Integer.toString(i), Integer.MAX_VALUE);
			values.add((value != Integer.MAX_VALUE)?value:0);
		}
			
		
		int[] temp = new int[6];
		for(int i = 0; i < values.size(); i += temp.length){
			for(int k = 0; k < temp.length; k++)
				temp[k] = (Integer) values.get(i+k);
			addView(new LSlider(activity, temp));
		}
		
	}
	
	public void backupDisplays(Activity activity){
		SharedPreferences prefs = (activity)
				.getSharedPreferences("lds_displays", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.clear();
		int index = 0;
		for(int i = 0; i < getChildCount(); i++){
			View view = getChildAt(i);
			if(view.getClass().equals(LDisplay.class)){
				editor.putInt(Integer.toString(index++), (int)view.getX());
				editor.putInt(Integer.toString(index++), (int)view.getY());
				editor.putInt(Integer.toString(index++), view.getWidth());
				editor.putInt(Integer.toString(index++), view.getHeight());
				editor.putInt(Integer.toString(index++), ((LDisplay)view).analogInput);
				editor.putFloat("multiplier", ((LDisplay)view).multiplier);
			}
		}
		editor.commit();
	}
	
	public void restoreDisplays(Activity activity){
		SharedPreferences prefs = (activity)
				.getSharedPreferences("lds_displays", Context.MODE_PRIVATE);
		List<Integer> values = new ArrayList<Integer>();
		
		float multiplier = prefs.getFloat("multiplier", 1);
		
		int collectionSize = prefs.getAll().size();
		int value;
		
		//collectionSize - 1 because 'multipler' backup is already handled.
		for(int i = 0; i < collectionSize - 1; i++){
			value = prefs.getInt(Integer.toString(i), Integer.MAX_VALUE);
			values.add((value != Integer.MAX_VALUE)?value:0);
		}
			
		
		int[] temp = new int[5];
		for(int i = 0; i < values.size(); i += temp.length){
			for(int k = 0; k < temp.length; k++)
				temp[k] = (Integer) values.get(i+k);
			addView(new LDisplay(activity, temp, multiplier));
		}
		
	}
	
	
	}
