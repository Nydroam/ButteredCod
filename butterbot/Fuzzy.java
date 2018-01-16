import bc.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Fuzzy{

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
	//returns the number of occupiable adjacent tiles
	public static int numAdjacent(MapLocation l, GameController gc){
		int num = 0;
		PlanetMap pm = gc.startingMap(gc.planet());
		Direction[] directions = Direction.values();
		for(int i = 0; i < directions.length; i++)
			if(directions[i]!=Direction.Center){
				MapLocation newl = l.add(directions[i]);
				if(gc.canSenseLocation(newl)&&pm.onMap(newl)&&gc.isOccupiable(newl)!=0)
					num++;
			}
		return num;
	}
}