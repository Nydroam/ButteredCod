import bc.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The MapScanner class stores all the areas (a collection of connected paths)
 * We can use this class to create areas and find the area corresponding to a 
 * Unit or MapLocation
 */

public class MapScanner{

	//Game Variables
	GameController gc;
	PlanetMap pm;

	//ArrayList of Areas (Any connected paths)
	ArrayList<MapData> areas;

	public MapScanner(GameController gc){
		this.gc = gc;
		pm = gc.startingMap(gc.planet());
		areas = new ArrayList<>();
	}

	//Accessors
	public ArrayList<MapData> areas() {return areas;}

	/**
	* findArea finds the area that a location belongs to
	* @param MapLocation l to find the area for
	* @return index of area in areas ArrayList, -1 if not found
	*/
	public int findArea(MapLocation l){
		for(int i = 0; i < areas.size(); i++){
			if(areas.get(i).contains(l))
				return i;
		}
		return -1;
	}

	/**
	* findArea finds the area that a unit belongs to
	* @param MapLocation l to find the area for
	* @return index of area in areas ArrayList, -1 if not found
	*/
	public int findArea(int u){
		//saves time if there's only one area
		if(areas.size()==1)
			return 0;
		for(int i = 0; i < areas.size(); i++){
			if(areas.get(i).contains(u))
				return i;
		}
		return -1;
	}

	/**
	 * fillArea creates a MapData class by using bfs to find all locations that
	 * are pathable to and from a location and each other and adds it to areas
	 * @param MapLocation to start the bfs from
	 * @return MapData that has all the information in the area
	 */
	public MapData fillArea(MapLocation loc){
		LinkedList<MapLocation> frontier = new LinkedList<>();
		frontier.offer(loc);
		MapData newArea = new MapData(gc);
		ArrayList<String> locations = newArea.locations();
		locations.add(loc.toJson());
		Direction[] values = Direction.values();
		while(frontier.size()>0){
			MapLocation current = frontier.poll();
			for(int i = 0; i < values.length; i++){
				Direction d = values[i];
				MapLocation next = current.add(d);
				if(!pm.onMap(next) || locations.contains(next.toJson())) {continue;}
				if(pm.isPassableTerrainAt(next)!=0){
					if(pm.initialKarboniteAt(next)>0)
						newArea.karbLocations().put(next.toJson(),(int)pm.initialKarboniteAt(next));
					locations.add(next.toJson());
					frontier.offer(next);
				}
			}
		}
		newArea.sumKarbonite();
		areas.add(newArea);
		return newArea;
	}

	/**
	* printMaps prints a representation of all the maps in the areas List
	*/
	public void printMaps(){
		areas.stream().forEach( m -> m.printMap() );
	}
	
}