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

	public HashMap<String,Direction> search(MapLocation start, Unit u){
		long st = System.nanoTime();
		LinkedList<MapLocation> locations = new LinkedList<MapLocation>();
		HashMap<String,Direction> paths = new HashMap<String,Direction>();
		charmap = new String[(int)pm.getWidth()][(int)pm.getHeight()];
		locations.offer(start);
		Direction[] directions = Direction.values();
		VecUnit units = gc.senseNearbyUnits(start,500);
		ArrayList<String> unitlocs = new ArrayList<String>();
		for(int i = 0; i < units.size(); i++){
			if(units.get(i).id()!=u.id())
				unitlocs.add(units.get(i).location().mapLocation().toString());
		}
		//System.out.println(unitlocs.size());
		//System.out.println("BEFORE WHILE");
		int steps = 0;
		while(!locations.isEmpty()){
			if(gc.canSenseLocation(start)&&PathFinder.numAdjacent(start,gc,pm)==0) {
				//System.out.println("BREAKING");
				return paths;
				
			}
			MapLocation current = locations.poll();
			for(Direction d : directions){
				if(d != Direction.Center){
					MapLocation next = current.add(d);
					if(!pm.onMap(next)){}
					else if(pm.isPassableTerrainAt(next)==0){}

					else if(unitlocs.contains(next.toString())){}
					else{
						if (!paths.keySet().contains(next.toString())){
							//System.out.println(next.getX()+" "+next.getY());
							locations.offer(next);
							paths.put(next.toString(),bc.bcDirectionOpposite(d));
							charmap[(int)next.getX()][(int)next.getY()]=bc.bcDirectionOpposite(d).toString();
							if(next.toString().equals(u.location().mapLocation().toString())){
								//System.out.println("Found in " + steps + "steps!");
								//System.out.println("Time: "+(System.nanoTime()-st)/1000000000.0);
								return paths;
							}
						}
					}
					
				}
			}
			steps++;
		}
		//System.out.println("AFTER WHILE");
		
		return paths;
	}

	public void printMap(){
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