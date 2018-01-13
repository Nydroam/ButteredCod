import bc.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Logistics{
	private GameController gc;
	private PlanetMap pm;
	private ArrayList<MapLocation> karbLocations;
	private VecUnit units;
	private HashMap<String, Integer> statistics;
	private HashMap<Integer, Integer> blueprints;
	private HashMap<Integer,MapLocation> targets;

	private HashMap<MapLocation, Integer> rallyPoints;
	private HashMap<ArrayList<MapLocation>, Integer> MarsInfo;

	public Logistics(GameController gc, PlanetMap pm){
		this.gc = gc;
		this.pm = pm;
		karbLocations = new ArrayList<MapLocation>();
		units = gc.myUnits();
		blueprints = new HashMap<Integer, Integer>();
		targets = new HashMap<Integer,MapLocation>();
		initStats();
		scoutKarbonite();
	}

	//accessors
	public ArrayList<MapLocation> karbLocations(){return karbLocations;}
	public VecUnit units(){return units;}
	public HashMap<String, Integer> statistics(){return statistics;}
	public HashMap<Integer, Integer> blueprints(){return blueprints;}
	public HashMap<Integer,MapLocation> targets(){return targets;}
	//Scans map for initial karbonite deposits and records locations
	public void scoutKarbonite(){
		for(int x = 0; x < pm.getWidth(); x++)
			for(int y = 0; y < pm.getHeight(); y++){
				MapLocation loc = new MapLocation(gc.planet(),x,y);
				if(pm.initialKarboniteAt(loc)>0)
					karbLocations.add(loc);
			}
	}

	//Update units and statistics
	public void updateUnits(){
		units = gc.myUnits();
	}

	//adds the integer to the current unit amount in the statistics hashmap
	public void updateStats(String u, int i){
		statistics.put(u,statistics.get(u)+i);
	}
	public void initStats(){
		statistics = new HashMap<String, Integer>();
		statistics.put("Factory",0);
		statistics.put("Rocket",0);
		statistics.put("Worker",(int)units.size());
		statistics.put("Knight",0);
		statistics.put("Ranger",0);
		statistics.put("Mage",0);
		statistics.put("Healer",0);
	}

	//adding to and updating blueprint hashmap
	public void addBlueprint(int i){
		blueprints.put(i,1);
	}
	public void updateBlueprint(int i, int j){
		blueprints.put(i,blueprints.get(i)+j);
	}
	public void removeBlueprint(int i){
		blueprints.remove(i);
	}


}