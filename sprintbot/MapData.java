import bc.*;
import java.util.HashMap;

public class MapData{
	
	GameController gc;
	PlanetMap pm;

	//Karbonite Locations
	//<MapLocation as JSON, Amount of Karbonite>
	private HashMap<String,Integer> karbLocations;

	//2D array containing information about terrain and units on the map
	//0 - impassible, 1 - passable, 2 - has unit
	private int[][] obstacleMap;
	private Planet planet;
	private int width;
	private int height;


	public MapData(GameController gc, PlanetMap pm){
		this.gc = gc;
		this.pm = pm;
		planet = gc.planet();
		width = (int)(pm.getWidth());
		height = (int)(pm.getHeight());
		obstacleMap = new int[width][height];
		karbLocations = new HashMap<String,Integer>();
		initMap();
	}

	//accessors
	public HashMap<String,Integer> karbLocations(){ return karbLocations; }
	public int[][] obstacleMap() { return obstacleMap; }

	//initialize map with all starting information
	public void initMap(){
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++){
				MapLocation loc = new MapLocation(planet,x,y);
				karbLocations.put(loc.toJson(),(int)pm.initialKarboniteAt(loc));
				obstacleMap[x][y] = pm.isPassableTerrainAt(loc);
			}
	}

	//update map with units in vision and karbonite
	public void updateMap(){
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++){
				MapLocation loc = new MapLocation(planet,x,y);
				if(gc.canSenseLocation(loc)){
					int karbs = (int)gc.karboniteAt(loc);
					if(karbs>0)
						karbLocations.put(loc.toJson(),karbs);
					else
						karbLocations.remove(loc.toJson());

					if(gc.hasUnitAtLocation(loc))
						obstacleMap[x][y] = 2;
				}
			}
	}

	//get total karbonite
	public int karbonite(){
		int karbs = 0;
		for(String s: karbLocations.keySet())
			karbs += karbLocations.get(s);
		return karbs;
	}

	//number of clear squares

	//print representation of map
	public void printMap(){
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				System.out.print(obstacleMap[x][y]);
			}
			System.out.println();
		}
	}
}