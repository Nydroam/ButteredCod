import bc.*;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;
public class BFS{

	public static final int NORTH = 0;
	public static final int NORTHEAST = 1;
	private String[][] charmap; 
	private PlanetMap pm;
	private GameController gc;

	
	public BFS(GameController gc){
		this.gc = gc;
		pm =  gc.startingMap(gc.planet());
		charmap = new String[(int)pm.getWidth()][(int)pm.getHeight()];
		
	}

	//Starting location, unit that wants to move, whether or not to do a full search of the map or only enough to path the unit
	public HashMap<String,Direction> search(MapLocation start, Unit u, boolean full){
		System.out.println("BFSING");
		//we offer the starting location and then fill in all adjacent locations with directions pointing to it
		//we offer each added location and then poll off the queue and repeat with fillign in adjacent locations

		LinkedList<MapLocation> locations = new LinkedList<MapLocation>();
		HashMap<String,Direction> paths = new HashMap<String,Direction>();

		charmap = new String[(int)pm.getWidth()][(int)pm.getHeight()];
		locations.offer(start);
		Direction[] directions = Direction.values();
		VecUnit units = gc.senseNearbyUnits(start,500);
		ArrayList<String> unitlocs = new ArrayList<String>();
		for(int i = 0; i < units.size(); i++){
			if(u==null||units.get(i).id()!=u.id())
				unitlocs.add(units.get(i).location().mapLocation().toString());
		}
		int steps = 0;
		while(!locations.isEmpty()){
			if(gc.canSenseLocation(start)&&Fuzzy.numAdjacent(start,gc)==0) {
			
				return paths;
				
			}
			MapLocation current = locations.poll();
			for(Direction d : directions){
				if(d != null){
					MapLocation next = current.add(d);
					if(!pm.onMap(next)){}
					else if(pm.isPassableTerrainAt(next)==0){}

					else if(unitlocs.contains(next.toString())){}
					else{
						if (!paths.keySet().contains(next.toString())){
							locations.offer(next);
							paths.put(next.toString(),bc.bcDirectionOpposite(d));
							charmap[(int)next.getX()][(int)next.getY()]=bc.bcDirectionOpposite(d).toString();
							if(!full&&next.toString().equals(u.location().mapLocation().toString())){
								//if the pathing reaches the unit, end the search
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

	public void printMap(){//used for printing out a representation of the map
		for(int i = charmap.length-1; i >=0;i--){
			for(int j = 0; j < charmap[i].length; j++){
				if(charmap[j][i]!=null){
					String s = charmap[j][i];
					String v = "";

					if(s.equals("North"))
						v = "^";
					if(s.equals("Northeast"))
						v = "/";
					if(s.equals("Northwest"))
						v = "\\";
					if(s.equals("South"))
						v = "v";
					if(s.equals("Southeast"))
						v = "L";
					if(s.equals("Southwest"))
						v = "Z";
					if(s.equals("East"))
						v = ">";
					if(s.equals("West"))
						v = "<";
					System.out.print(v);
				}
				else
					System.out.print(0);
			}
			System.out.println();
		}
	}
}