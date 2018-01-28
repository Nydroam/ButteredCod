import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class Bot{

	//Game Variables
	protected GameController gc;
	protected PlanetMap pm;

	//Unit Variables
	protected Unit unit;
	protected int id;
	protected MapLocation loc;
	protected MapLocation dest;

	//Data Variables
	Logistics logs;
	MapData area;
	HashMap<String,Direction> paths;

	public Bot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area){
		this.gc = gc;
		this.pm = pm;
		this.unit = unit;
		id = unit.id();
		loc = unit.location().mapLocation();
		this.logs = logs;
		this.area = area;
	}

	public void act(){}
	public void actMars(){act();}
	public boolean tryMove(){
		if(gc.isMoveReady(id)){
			ArrayList<Direction> dirs = area.getDirections(unit);
			for(Direction d : dirs){
				if(gc.canMove(id,d)){
					gc.moveRobot(id,d);
					return true;
				}
				MapLocation floc = loc.add(d);
				if(gc.hasUnitAtLocation(floc)){
					Unit fact = gc.senseUnitAtLocation(floc);
					if(fact.team()==gc.team()&&gc.canLoad(fact.id(),id)){
						gc.load(fact.id(),id);
						return true;
					}
				}
			}
		}
		return false;
	}

}