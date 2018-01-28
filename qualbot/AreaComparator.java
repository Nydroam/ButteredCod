import bc.*;
import java.util.Comparator;
import java.util.ArrayList;
public class AreaComparator implements Comparator<ArrayList<MapLocation>>{

    @Override
    public int compare(ArrayList<MapLocation> area1, ArrayList<MapLocation> area2){
	if (area1.size() > area2.size()){
	    return -1;
	}
	if (area1.size() < area2.size()){
	    return 1;
	}
	return 0;
	
    }
    
}
