import bc.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * MapData stores information about MapLocations, Units, and karbonite locations and amounts
 * within an area and is to be updated and used by Units for acting
 */
public class MapData{
	
	//Game Variables
	GameController gc;
	PlanetMap pm;

	//An ArrayList containing locations within this Area
	private ArrayList<String> locations;

	//HashMap with locations of karbonite and the corresponding number of karbonite
	private HashMap<String,Integer> karbLocations;

	//List of unit ids that are in this area
	private ArrayList<Integer> unitList;

	//Total amount of karbonite present based on karbLocations;
	private int totalKarbs;

	public MapData(GameController gc){
		this.gc = gc;
		pm = gc.startingMap(gc.planet());
		locations = new ArrayList<>();
		karbLocations = new HashMap<String,Integer>();
		unitList = new ArrayList<>();
	}

	//Accessors
	public ArrayList<String> locations() {return locations;}
	public HashMap<String,Integer> karbLocations() {return karbLocations;}
	public ArrayList<Integer> unitList() {return unitList;}
	public int totalKarbs() {return totalKarbs;}

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