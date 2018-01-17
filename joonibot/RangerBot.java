/*---------------------- IMPORTS ----------------------*/

// Battlecode
import bc.*;

// Java Imports
import java.util.HashMap;
import java.util.ArrayList;

/*---------------------- CLASS DEF ----------------------*/

public class RangerBot extends Bot {

	/*---------------------- VARS ----------------------*/

	// Instance Vars
	private HashMap<String,Direction> paths;

	/*---------------------- CONSTRUCTORS ----------------------*/
	/**
	 * Default constructor for RangerBot.
	 * @param unit the unit that this RangerBot describes
	 * @param gc the Battlecode GameController
	 * @param logs the logistics of our team
	 * @param paths the result of the BFS
	 * @return a default RangerBot object
	 */
	public RangerBot(Unit unit, GameController gc, Logistics logs, HashMap<String,Direction> paths){
		super(unit,gc,logs);
		this.paths = paths;
	}

	/*---------------------- METHODS ----------------------*/
	/**
	 * Attempts to move.
	 * @param opposite whether the ranger should retreat
	 * @return true upon success, false otherwise
	 */
	public boolean testMove(boolean opposite){
		if (gc.isMoveReady(id) && paths != null && paths.keySet().contains(loc.toString())){
			Direction d = paths.get(loc.toString());
			if (opposite)
				d = bc.bcDirectionOpposite(d);
			for (int i = 0; i < Fuzzy.rotateOrder.length; i++){
				Direction newDir = Fuzzy.tryRotate(d,Fuzzy.rotateOrder[i]);
				if(gc.canMove(id,newDir)){
					gc.moveRobot(id,newDir);
					loc = loc.add(newDir);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Main act method.
	 */
	@Override
	public void act(){
		Team enemyTeam = logs.getOtherTeam();
		Team ourTeam = logs.getOurTeam();
		
		Unit enemy = unitAtRange(unit.attackRange(),enemyTeam);
		if (enemy != null){
			tryAttack(enemy.id());
		}

		VecUnit enemiesVec = gc.senseNearbyUnitsByTeam(loc,100,enemyTeam);
		ArrayList<Unit> enemies = new ArrayList<Unit>();
		for (int i = 0; i < enemiesVec.size(); i++)
			if (enemiesVec.get(i).unitType() == UnitType.Ranger)
				enemies.add(enemiesVec.get(i));
		VecUnit alliesVec = gc.senseNearbyUnitsByTeam(loc,25,ourTeam);
		ArrayList<Unit> allies = new ArrayList<Unit>();
		for (int i = 0; i < alliesVec.size(); i++)
			if (alliesVec.get(i).unitType() == UnitType.Ranger)
				allies.add(alliesVec.get(i));
		VecUnit close = gc.senseNearbyUnitsByTeam(loc,2,enemyTeam);
		if (close.size()>0)
			testMove(true);
		else if(enemies.size() < allies.size())
			testMove(false);
		else if(unit.health() < unit.maxHealth())
			testMove(true);
		enemy = unitAtRange(unit.attackRange(),enemyTeam);
		if (enemy != null){
			tryAttack(enemy.id());
		}

		if (gc.isMoveReady(id)){
			if (gc.planet() == Planet.Mars){
				if (gc.senseNearbyUnitsByTeam(loc,2500,enemyTeam).size() > 0 || gc.round() < 700){
					Direction d = Fuzzy.findAdjacent(loc,gc);
			if(gc.canMove(id,d))
				gc.moveRobot(id,d);
			}
				
			}else if (gc.senseNearbyUnitsByTeam(loc,2500,enemyTeam).size() == 0){
			Direction d = Fuzzy.findAdjacent(loc,gc);
			if (gc.canMove(id,d))
				gc.moveRobot(id,d);
			}
		}
	}

	/**
	 * Main act() method for Mars.
	 */
	@Override
	public void actMars(){
		Unit enemy = unitAtRange(unit.attackRange(), logs.getOtherTeam());
		if (enemy != null)
			tryAttack(enemy.id());
	}
	

	/**
	 * Act2 for rangers.
	 */
	@Override
	public void act2(){
		if (logs.statistics().get("Rocket") > 0){
			dest = null;
			for(int i: rockets.keySet()){
				if(gc.canSenseUnit(i)){
					Unit rocket = gc.unit(i);
					MapLocation rLoc = rocket.location().mapLocation();
					if(rLoc.distanceSquaredTo(loc) < 25 || logs.rallyPoints().size() == 0){
					targets.put(id,rLoc);
					dest = targets.get(id);
					break;}
				}
			}
			if(dest != null){
				Unit enemy = unitAtRange(unit.attackRange(), logs.getOtherTeam());
				if (enemy != null)
					tryAttack(enemy.id());
				if(!loc.isAdjacentTo(dest))
					tryMove();
				enemy = unitAtRange(unit.attackRange(), logs.getOtherTeam());
				if(enemy != null)
					tryAttack(enemy.id());
			}
			else
				act();
		}
		else
			act();
	}


}