import bc.*;
import java.util.Map;
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


	public int getPriorityTarget(){
		VecUnit enemies = gc.senseNearbyUnitsByTeam(loc, unit.attackRange(), enemyTeam);
		if (enemies.size() > 0){
			HashMap<Unit, Long> priorities = new HashMap<Unit, Long>();
			long threshold = 2000;
			for (int i = 0; i < enemies.size(); i++){
				Unit enemy = enemies.get(i);
				UnitType type = enemy.unitType();
				if (type == UnitType.Mage){
					priorities.put(enemy, threshold + enemy.maxHealth() - enemy.health());
				}

				if (type == UnitType.Knight){
					if (enemy.health() <= 90){
						priorities.put(enemy, threshold - 250 + enemy.maxHealth() - enemy.health());
					}else{
						priorities.put(enemy, threshold - 600 + enemy.maxHealth() - enemy.health());
					}
				}

				if (type == UnitType.Healer){
					priorities.put(enemy, threshold - 350 + enemy.maxHealth() - enemy.health());
				}

				if (type == UnitType.Ranger){
					priorities.put(enemy, threshold - 800 + enemy.maxHealth() - enemy.health());
				}

				if (type == UnitType.Factory){
					priorities.put(enemy, threshold - 1100 + enemy.maxHealth() - enemy.health());
				}

				if (type == UnitType.Rocket){
					priorities.put(enemy, threshold - 1300 + enemy.maxHealth() - enemy.health());
				}

				if (type == UnitType.Worker){
					priorities.put(enemy, threshold - 1400 + enemy.maxHealth() - enemy.health());
				}
			}

			int max = Integer.MIN_VALUE;
			Unit target = enemies.get(0);
			for (Map.Entry<Unit, Long> entry : priorities.entrySet()) {
				Unit enemy = entry.getKey();
				long value = entry.getValue();
				if (value > max){
					max = (int)value;
					target = enemy;
				}
			}
			return target.id();
		}
		return -1;
	}

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
