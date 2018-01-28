import bc.*;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Iterator;
public class MissionControl{
	MarsScanner scanner;
	PriorityQueue<ArrayList<MapLocation>> areas;
	ArrayList<String> used;
	public MissionControl(GameController gc){
		scanner = new MarsScanner(gc.startingMap(Planet.Mars),Planet.Mars);
		scanner.loadVirtualMap();
		areas = scanner.scan();
		used = new ArrayList<String>();
	}

	public MapLocation getLocation(){
		int i = (int)(Math.random()*areas.size());
		Iterator it = areas.iterator();
		ArrayList<MapLocation> pls = null;
		int count = 0;
		while(it.hasNext()){
			pls = (ArrayList<MapLocation>)it.next();
			if(i==count)
				break;
			count++;
		}
		MapLocation loc = scanner.landRandom(pls);
		System.out.println(loc);
		if(used.contains(loc.toJson()))
			return null;
		used.add(loc.toJson());
		return loc;
	}
}