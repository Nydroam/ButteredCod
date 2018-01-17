/*---------------------- IMPORTS ----------------------*/

// Battlecode Imports
import bc.*;

// Java Imports
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashMap;

/*---------------------- CLASS DEF ----------------------*/

public class Logistics {

	/*---------------------- VARS ----------------------*/

	// Instance Vars
	private GameController gc;
	private PlanetMap pm;
	private Team ourTeam, otherTeam;
	private ArrayList<MapLocation> karbLocations;						// locations of karbonite deposits
	private VecUnit units;
	private HashMap<UnitType,Integer> unitPriorities;					// unit types/priority of each
	private HashMap<String,Integer> statistics;							// unit types/# of each
	private HashMap<Integer,Integer> blueprints;						// blueprint ids/# of workers on each
	private HashMap<Integer,MapLocation> targets;						// id of our units/where it is going
	private HashMap<Integer,Integer> rockets;							// rocket id/# of units pathing to it
	private PriorityQueue<String> rallyPoints;							// list of rallying points TODO: order by distance
	private HashMap<ArrayList<MapLocation>, Integer> MarsInfo;			// to be implemented later

	/*---------------------- CONSTRUCTORS ----------------------*/

	public Logistics(GameController gc, PlanetMap pm){
		this.gc = gc;
		this.pm = pm;
		karbLocations = new ArrayList<MapLocation>();
		units = gc.myUnits();
		blueprints = new HashMap<Integer,Integer>();
		targets = new HashMap<Integer,MapLocation>();
		rockets = new HashMap<Integer,Integer>();
		rallyPoints = new PriorityQueue<String>();
		statistics = new HashMap<String, Integer>();
		ourTeam = gc.team();
		if (ourTeam == Team.Red)
			otherTeam = Team.Blue;
		else
			otherTeam = Team.Red;
		unitPriorities = new HashMap<UnitType,Integer>();
		initStats();
		scoutKarbonite();
		setupPriorities();
	}

	/*---------------------- METHODS ----------------------*/

	// Accessors
	public ArrayList<MapLocation> karbLocations(){ return karbLocations; }
	public VecUnit units(){ return units; }
	public HashMap<String,Integer> statistics(){ return statistics; }
	public HashMap<Integer,Integer> blueprints(){ return blueprints; }
	public HashMap<Integer,MapLocation> targets(){ return targets; }
	public HashMap<Integer,Integer> rockets(){ return rockets; }
	public PriorityQueue<String> rallyPoints(){ return rallyPoints; }
	public int getUnitPriority(UnitType unitType){ return unitPriorities.get(unitType); }
	public Team getOurTeam(){ return ourTeam; }
	public Team getOtherTeam(){ return otherTeam; }

	/**
	 * Sets up the unitPriorities hashmap used to determine which enemy should be annihilated first.
	 */
	private void setupPriorities(){
		for (UnitType unitType : UnitType.values()){
			if (unitType == UnitType.Ranger || unitType == UnitType.Mage)
				unitPriorities.put(unitType,3);
			else
				unitPriorities.put(unitType,1);
		}
	}

	/**
	 * Scans map for initial karbonite deposits and records locations.
	 */
	public void scoutKarbonite(){
		long start = System.currentTimeMillis();
		for (int x = 0; x < pm.getWidth(); x++){
			for (int y = 0; y < pm.getHeight(); y++){
				MapLocation loc = new MapLocation(gc.planet(),x,y);
				if (pm.initialKarboniteAt(loc)>0)
					karbLocations.add(loc);
			}
			System.out.println("Scouting: " + (System.currentTimeMillis()-start)/1000.0);
		}
	}

	/**
	 * Update units and statistics.
	 */
	public void updateUnits(){
		units = gc.myUnits();
		initStats();
		for (int i = 0; i < units.size(); i++)
			updateStats(units.get(i).unitType().toString(),1);
	}

	/**
	 * Helper method for updateUnits.
	 */
	public void updateStats(String u, int i){
		statistics.put(u,statistics.get(u)+i);
	}

	/**
	 * Initializes statistics to be filled with 0s for all UnitTypes.
	 */
	public void initStats(){
		statistics.put("Factory",0);
		statistics.put("Rocket",0);
		statistics.put("Worker",0);
		statistics.put("Knight",0);
		statistics.put("Ranger",0);
		statistics.put("Mage",0);
		statistics.put("Healer",0);
	}

	/**
	 * Adds a new blueprint's unitID and the (initial) amount of workers building it.
	 */
	public void addBlueprint(int id){
		blueprints.put(id,1);
	}

	/**
	 * Updates the blueprint's number of workers.
	 */
	public void updateBlueprint(int id, int newWorkers){
		blueprints.put(id, blueprints.get(id) + newWorkers);
	}

	/**
	 * Removes the blueprint.
	 */
	public void removeBlueprint(int i){
		blueprints.remove(i);
	}

	/**
	 * Resets targets.
	 */
	public void resetTargets(){
		targets = new HashMap<Integer,MapLocation>();
	}

}