package littlebot2014.littlebotdriverstation;

import java.util.ArrayList;
import java.util.List;

public final class ListFactory {
	
	public static List<Integer> create(int[] ia){
			ArrayList<Integer> list = new ArrayList<Integer>();
			for(int i : ia)
				list.add(i);
			return list;
		}
	
	public static List<String> create(String[] sa){
		ArrayList<String> list = new ArrayList<String>();
		for(String s : sa)
			list.add(s);
		return list;
	}
}
