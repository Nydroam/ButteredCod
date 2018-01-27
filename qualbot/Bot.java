import bc.*;
import java.util.HashMap;
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
	public boolean tryMove(Direction d){
		if(gc.canMove(id,d)){
			gc.moveRobot(id,d);
			return true;
		}
		return false;
	}

}