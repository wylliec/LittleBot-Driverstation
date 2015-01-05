package littlebot2014.littlebotdriverstation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.app.Activity;
import android.view.View.OnLongClickListener;

public class LView extends View{
	
	protected boolean isFrozen = true;
	private OnSizeChangeListener sizeListener;
	private Context context;
	private ImageView trash;
	private ViewGroup parent;
	
	public LView(Context context, int x, int y){
		super(context);
		this.context = context;
		setLayoutParams(new LayoutParams(100,100));
		setX(x);
		setY(y);
		parent = ((MainActivity)context).layout;
		trash = (Trash)((MainActivity)context).trash;
	}

	public void changeSize(int w, int h){
		onSizeChanged(w,h,w,h);
	}
	
	public void setOnSizeChangeListener(OnSizeChangeListener listener){
		sizeListener = listener;
	}
	
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		if(sizeListener != null)
			sizeListener.onSizeChange(w, h, oldw, oldh);
	}
	
	interface OnSizeChangeListener{
		public void onSizeChange(int w, int h, int oldw, int oldh);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(isFrozen){	
			setX(event.getX()+getX()-getWidth()/2);
			setY(event.getY() + getY()-getHeight()/2);
			if(event.getAction() == MotionEvent.ACTION_UP){
				float x = event.getX() + this.getX();
				float y = (int) event.getY() + this.getY();
				if(x > (parent.getWidth() - trash.getWidth()) &&
						y < trash.getHeight() && Trash.isActive){
					parent.removeView(this);
				}
			}
		}
		return false;
	}
}
