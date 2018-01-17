/*---------------------- IMPORTS ----------------------*/

// Battlecode
import bc.*;

// Java Imports
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;

/*---------------------- CLASS DEF ----------------------*/
public class BFS {

	/*---------------------- VARS ----------------------*/

	// Static Vars
	public static final int NORTH = 0;
	public static final int NORTHEAST = 1;
	public static final Direction[] directions = {Direction.North, Direction.Northeast, Direction.East, Direction.Southeast, Direction.South,
		Direction.Southwest, Direction.West, Direction.Northwest, Direction.Center};

	// Instance Vars
	private String[][] charmap; 
	private PlanetMap pm;
	private GameController gc;

	/*---------------------- CONSTRUCTORS ----------------------*/

	/**
	 * Default constructor for BFS pathing.
	 * @param gc Battlecode GameController
	 * @return a new BFS object
	 */
	public BFS(GameController gc){
		this.gc = gc;
		pm = gc.startingMap(gc.planet());
		charmap = new String[(int)pm.getWidth()][(int)pm.getHeight()];
		
	}

	/*---------------------- METHODS ----------------------*/

	private void clearCharMap(){
		for (int i = 0; i < charmap.length; i++){
			for (int j = 0; j < charmap[0].length; j++)
				charmap[i][j] = "";
		}
	}

	/**
	 * Fully searches the component that the starting location is in.
	 * @param start the starting location of the unit
	 */
	public HashMap<String,Direction> fullSearch(MapLocation start){
		//long st = System.currentTimeMillis();
		LinkedList<MapLocation> locations = new LinkedList<MapLocation>();
		HashMap<String,Direction> paths = new HashMap<String,Direction>();
		MapLocation current, next;
		clearCharMap();							// clear the charmap
		locations.add(start);					// add starting location
		int steps = 0;							// keep track of the steps
		// Begin BFSing using locations as our queue
		while (!locations.isEmpty()){			
			current = locations.poll();
			for (Direction dir : directions){
				// If the next location is on the map and it is passable terrain and it is not already in paths, add it to the queue
				next = current.add(dir);
				if (pm.onMap(next) && pm.isPassableTerrainAt(next) != 0){
					if (!paths.keySet().contains(next.toString())){
						locations.add(next);
						paths.put(next.toString(),bc.bcDirectionOpposite(dir));
						charmap[(int)next.getX()][(int)next.getY()] = bc.bcDirectionOpposite(dir).toString();
					}
				}
			}
			steps++;
		}
		//System.out.println("BFS FULL TIME: "+(System.currentTimeMillis()-st));
		return paths;
	}

	/**
	 * Searches the component that the starting location is in until it reaches the specified unit.
	 * @param start the starting location
	 * @param unit the unit specified
	 * @param full 
	 */
	public HashMap<String,Direction> search(MapLocation start, Unit unit, boolean full){
		//System.out.println("BFSING");

		LinkedList<MapLocation> locations = new LinkedList<MapLocation>();
		HashMap<String,Direction> paths = new HashMap<String,Direction>();
		charmap = new String[(int)pm.getWidth()][(int)pm.getHeight()];
		locations.add(start);
		VecUnit units = gc.senseNearbyUnits(start,500);
		ArrayList<String> unitlocs = new ArrayList<String>();
		MapLocation current, next;
		for (int i = 0; i < units.size(); i++){
			if (unit == null || units.get(i).id() != unit.id())
				unitlocs.add(units.get(i).location().mapLocation().toString());
		}
		int steps = 0;
		while (!locations.isEmpty()){
			if(gc.canSenseLocation(start) && Fuzzy.numAdjacent(start,gc)==0) {
				return paths;
			}
			current = locations.poll();
			for(Direction dir : directions){
				if(dir != null){
					next = current.add(dir);
					if (pm.onMap(next) && pm.isPassableTerrainAt(next) != 0 && !unitlocs.contains(next.toString())){
						if (!paths.keySet().contains(next.toString())){
							locations.offer(next);
							paths.put(next.toString(),bc.bcDirectionOpposite(dir));
							charmap[(int)next.getX()][(int)next.getY()] = bc.bcDirectionOpposite(dir).toString();
							if (!full&&next.toString().equals(unit.location().mapLocation().toString())){
								return paths;
							}
						}
					}
				}
			}
			steps++;
		}
		//System.out.println("Steps:"+steps);
		return paths;
	}

	/**
	 * Prints a representation of the map with arrows on each tile indicating direction.
	 */
	public void printMap(){
		for (int i = charmap.length-1; i >= 0; i--){
			for (int j = 0; j < charmap[i].length; j++){
				if (charmap[j][i] != null){
					String s = charmap[j][i];
					String v = "";
					if (s.equals("North"))
						v = "^";
					if (s.equals("Northeast"))
						v = "/";
					if (s.equals("Northwest"))
						v = "\\";
					if (s.equals("South"))
						v = "v";
					if (s.equals("Southeast"))
						v = "L";
					if (s.equals("Southwest"))
						v = "Z";
					if (s.equals("East"))
						v = ">";
					if (s.equals("West"))
						v = "<";
					if (s.equals("Center"))
						v = "O";
					System.out.print(v);
				}
				else
					System.out.print(0);
			}
			System.out.println();
		}
	}
}