package littlebot2014.littlebotdriverstation;
import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

public class ViewAdapter extends ArrayAdapter<View>{

	public ViewAdapter(Context context, int resource) {
		super(context, resource);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		return getItem(position);
	}
	}