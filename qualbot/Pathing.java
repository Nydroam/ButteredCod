import bc.*;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
public class Pathing{

	//Game Variables
	GameController gc;
	PlanetMap pm;
	Comparator<AuxMapLocation> aStar;
	HashMap<String,Direction> paths;
	int[][] intMap;
	public Pathing(GameController gc, PlanetMap pm){
		this.gc = gc;
		this.pm = pm;
		aStar = (loc1, loc2) ->Integer.compare(loc1.priority(),loc2.priority());
		intMap = new int[(int)pm.getWidth()][(int)pm.getHeight()];
	}

	public HashMap<String,Direction> aSearch(MapLocation e, MapLocation s){
		long time = System.currentTimeMillis();
		AuxMapLocation start = new AuxMapLocation(s,0);
		//AuxMapLocation end = new AuxMapLocation(e);
		PriorityQueue<AuxMapLocation> frontier = new PriorityQueue<AuxMapLocation>(aStar);
		frontier.offer(start);
		HashMap<String,Integer> cost = new HashMap<String,Integer>();
		cost.put(start.toJson(),0);
		paths = new HashMap<String,Direction>();
		paths.put(start.toJson(),Direction.Center);
		
		Direction[] values = Direction.values();
		while(frontier.size()>0){
			MapLocation current = frontier.poll(); 
			String curr = current.toJson();
			if(curr.equals(e.toJson())){
				intMap[e.getX()][e.getY()] = 900;
				break;
			}
			int newCost = cost.get(curr)+1;
			for(int i = 0; i < values.length; i++){
				MapLocation next = current.add(values[i]);
				String n = next.toJson();
				if(!pm.onMap(next) || pm.isPassableTerrainAt(next)==0)
					continue;
				if(!cost.containsKey(n)||newCost < cost.get(n)){
					
					cost.put(n,newCost);
					int priority = newCost + (int)(Math.sqrt(next.distanceSquaredTo(s))) + (int)(Math.sqrt(next.distanceSquaredTo(e)));
					intMap[next.getX()][next.getY()] = priority;
					frontier.add(new AuxMapLocation(next,priority));
					paths.put(n,next.directionTo(current));
				}
			}
		}
		//System.out.println("A* Time: " + (System.currentTimeMillis()-time) + " ms");
		return paths;
	}

	public void printMap(){
		
		System.out.println("PATHING:");
		for(int i = intMap[0].length-1; i >= 0 ; i--){
			for(int j = 0; j < intMap.length; j++){
				int num = intMap[j][i];
				if(num==0){
					num = 1;
				}
				
				System.out.print("[");
				while(num<100){
					System.out.print(" ");
					num *= 10;
				}
				System.out.print(intMap[j][i]);
				
				System.out.print("]");
			}
			System.out.println();
		}
		
	}

	public static int[] rotateOrder = {0,-1,1};

	public static Direction tryRotate(Direction d, int i){
		ArrayList<Direction> dirList = new ArrayList<Direction>(Arrays.asList(Direction.values()));
		dirList.remove(Direction.Center);
		return dirList.get((dirList.indexOf(d)+i+dirList.size())%dirList.size());	
	}

	public static Direction findAdjacent(MapLocation l, GameController gc){
		Direction d = Direction.Center;
		Direction[] directions = Direction.values();
		PlanetMap pm = gc.startingMap(gc.planet());
		for(int i = 0; i < directions.length; i++)
			if(directions[i]!=Direction.Center){
				MapLocation newl = l.add(directions[i]);
				if(pm.onMap(newl)&&gc.canSenseLocation(newl)&&gc.isOccupiable(newl)!=0){
					d=directions[i];
					if(Math.random()<0.2)
						return d;
				}
			}
		return d;
	}
	public static int numAdjacent(MapLocation l, GameController gc){
		int num = 0;
		PlanetMap pm = gc.startingMap(gc.planet());
		Direction[] directions = Direction.values();
		for(int i = 0; i < directions.length; i++)
			if(directions[i]!=Direction.Center){
				MapLocation newl = l.add(directions[i]);
				if(pm.onMap(newl)&&pm.isPassableTerrainAt(newl)!=0)
					num++;
			}
		return num;
	}

}