import bc.*;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.Stack;
/**
 * MapData stores information about MapLocations, Units, and karbonite locations and amounts
 * within an area and is to be updated and used by Units for acting
 */
public class MapData{
	
	//Game Variables
	private GameController gc;
	private PlanetMap pm;

	//An ArrayList containing locations within this Area
	private ArrayList<String> locations;

	//HashMap with locations of karbonite and the corresponding number of karbonite
	private HashMap<String,Integer> karbLocations;
	private LinkedList<MapLocation> karbQueue;

	//List of unit ids that are in this area
	private ArrayList<Integer> unitList;
	private HashMap<Integer,Integer> blueprints;

	//Unit Rally Points
	private ArrayList<String> rallyPoints;
	private PlanetScanner earthScanner;

	//Total amount of karbonite present based on karbLocations;
	private int totalKarbs;


	//Enemy Team
	private Team enemyTeam;
	public MapData(GameController gc){
		this.gc = gc;
		pm = gc.startingMap(gc.planet());
		locations = new ArrayList<>();
		karbLocations = new HashMap<String,Integer>();
		karbQueue = new LinkedList<MapLocation>();
		unitList = new ArrayList<>();
		blueprints = new HashMap<Integer, Integer>();
	
		rallyPoints = new ArrayList<String>();
		enemyTeam = Team.Red;
		if(gc.team()==Team.Red)
			enemyTeam = Team.Blue;
	}

	public ArrayList<Direction> getDirections(Unit u){
	    return earthScanner.getPathPriorityDirs(u,0);
	}

	public int getSteps(Unit u){
		return earthScanner.getSteps(u);
	}

	//Accessors
	public ArrayList<String> locations() {return locations;}
	public HashMap<String,Integer> karbLocations() {return karbLocations;}
	public ArrayList<Integer> unitList() {return unitList;}
	public int totalKarbs() {return totalKarbs;}
	public LinkedList<MapLocation> karbQueue() {return karbQueue;}
	public HashMap<Integer, Integer> blueprints() {return blueprints;}
	public ArrayList<String> rallyPoints() {return rallyPoints;}

	//mutators
	public void addUnit(int u){
		unitList.add(u);
	}

	/**
	 * sumKarbonite sets a value for totalKarbs by summing the values of karbLocations
	 */
	public void sumKarbonite(){
		totalKarbs = 0;
		karbLocations.values().stream().forEach( i -> totalKarbs += i);
	}

	/**
	 * contains indicates whether or not a MapLocation is within this area by checking
	 * the locations ArrayList
	 * @param MapLocation l to check
	 * @return boolean indicating whether or not the MapLocation is in locations
	 */
	public boolean contains(MapLocation l){
		return locations.contains(l.toJson());
	}

	/**
	 * contains indicates whether or not a Unit is within this area by checking
	 * the unitList ArrayList
	 * @param int id of Unit to check
	 * @return boolean indicating whether or not the id is in unitList
	 */
	public boolean contains(int i){
		return unitList.contains(i);
	}

	public void updateRallyPoints(){
		Iterator<String> it = rallyPoints.iterator();
		while(it.hasNext()){
			MapLocation loc = bc.bcMapLocationFromJson((String)it.next());
			if(gc.canSenseLocation(loc)){
				if(gc.hasUnitAtLocation(loc)){
					if(gc.senseUnitAtLocation(loc).team()==gc.team()&&gc.senseUnitAtLocation(loc).unitType()!=UnitType.Rocket)
						it.remove();
				}else{
					it.remove();
				}
			}
		}
		
		MapLocation centerLoc = new MapLocation(gc.planet(),(int)pm.getWidth()/2,(int)pm.getHeight()/2);
		VecUnit enemyVec = gc.senseNearbyUnitsByTeam(centerLoc,pm.getWidth()*pm.getHeight(),enemyTeam);
		for(int i = 0; i < enemyVec.size(); i++){
			Unit enemy = enemyVec.get(i);
			MapLocation loc = enemy.location().mapLocation();
			if (contains(loc)){
				if(enemy.unitType()==UnitType.Factory)
					rallyPoints.add(loc.toJson());
				else if(enemy.unitType()==UnitType.Worker&&rallyPoints.size()==0)
					rallyPoints.add(loc.toJson());

				else if(rallyPoints.size()<10&&!rallyPoints.contains(loc.toJson()))
					rallyPoints.add(loc.toJson());

			}
		}
		ArrayList<MapLocation> rp = new ArrayList<>();
		for(String s: rallyPoints)
			rp.add(bc.bcMapLocationFromJson(s));
		earthScanner = new PlanetScanner(pm,gc.planet(),rp,gc);
		earthScanner.loadVirtualMap();
		earthScanner.buildPathMap(rp);
		earthScanner.printAllAreas();
	}

	/**
	 * printMap prints a representation of the current area in a 2D char array
	 * - indicates passable locations
	 * $ indicates karbonite locations
	 * X indicates impassible locations
	 */
	public void printMap(){
		char[][] charMap = new char[(int)pm.getWidth()][(int)pm.getHeight()];
		locations.stream().forEach( loc -> {
			MapLocation l = bc.bcMapLocationFromJson(loc);
			charMap[l.getX()][l.getY()] = '-';
		});
		karbLocations.keySet().stream().forEach( loc -> {
			MapLocation l = bc.bcMapLocationFromJson(loc);
			charMap[l.getX()][l.getY()] = '$';
		});
		for(int i = charMap[0].length-1; i >= 0 ; i--){
			for(int j = 0; j < charMap.length; j++){
				if(charMap[j][i]=='-'||charMap[j][i]=='$')
					System.out.print(charMap[j][i]);
				else
					System.out.print('X');
			}
			System.out.println();
		}
		System.out.println("Total Karbonite: " + totalKarbs);
		System.out.println();
	}
}
