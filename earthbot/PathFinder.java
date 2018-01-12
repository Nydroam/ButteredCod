import bc.*;

public class PathFinder{

	//find an adjacent Direction given a Maplocation, prioritizing 
	public static Direction findAdjacent(MapLocation l, GameController gc, PlanetMap pm){
		int max = 0;
		Direction d = Direction.Center;
		Direction[] directions = Direction.values();
		for(int i = 0; i < directions.length; i++)
			if(directions[i]!=Direction.Center){
				MapLocation newl = l.add(directions[i]);
				if(pm.onMap(newl)&&gc.isOccupiable(newl)!=0){
					int tmp = numAdjacent(newl,gc,pm);
					if(tmp>max){
						d = directions[i];
						max = tmp;
					}
					else if(tmp==max&&Math.random()<0.3){
						d = directions[i];
						max = tmp;
					}
				}
			}
		return d;
	}
	//returns the number of occupiable adjacent tiles
	public static int numAdjacent(MapLocation l, GameController gc, PlanetMap pm){
		int num = 0;
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