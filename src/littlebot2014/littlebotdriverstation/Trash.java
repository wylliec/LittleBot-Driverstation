package littlebot2014.littlebotdriverstation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.view.View.OnClickListener;

public class Trash extends ImageView implements OnClickListener{

	public static boolean isActive = true;
	
	public Trash(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVisibility(View.INVISIBLE);
		setImageResource(R.drawable.trash);
		setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if(Trash.isActive){
			Trash.isActive = false;
			setActivated(false);
			setImageResource(R.drawable.trash_dissabled);
		}else{
			Trash.isActive = true;
			setActivated(true);
			setImageResource(R.drawable.trash);
		}
	}
}
