import bc.*;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Iterator;
public class MissionControl{
    MarsScanner scanner;
    ArrayList<ArrayList<MapLocation>> areas;
    ArrayList<String> used;
    int i = 0;
    public MissionControl(GameController gc){
	scanner = new MarsScanner(gc.startingMap(Planet.Mars),Planet.Mars);
	scanner.loadVirtualMap();
	areas = new ArrayList<ArrayList<MapLocation>>(scanner.scan());
	used = new ArrayList<String>();
    }

    public MapLocation getLocation(){
	
	if (i > areas.size()/3){
	    i = 0;
	}
	
	ArrayList<MapLocation> area = areas.get(i);
	MapLocation target = scanner.landRandom(area);
	area.remove(target);
	i++;
	return target;

	/*
	Iterator<ArrayList<MapLocation>> it = areas.iterator();
	ArrayList<MapLocation> pls = null;
	int count = 0;
	while(it.hasNext()){
	    pls = it.next();
	    if(i==count)
		break;
	    count++;
	}
	MapLocation loc = scanner.landRandom(pls);
	//System.out.println(loc);
	if(used.contains(loc.toJson()))
	    return null;
	used.add(loc.toJson());
	return loc;
	*/
    }
}
