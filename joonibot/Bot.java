/*---------------------- IMPORTS ----------------------*/

// Battlcode Imports
import bc.*;

// Java Imports
import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;

/*---------------------- CLASS DEF ----------------------*/

public class Bot {
	
	/*---------------------- VARS ----------------------*/

	// Instance Vars
	protected GameController gc;
	protected Logistics logs;
	protected Unit unit;
	protected int id;
	protected MapLocation loc;
	protected MapLocation dest;
	protected HashMap<Integer,MapLocation> targets;
	protected HashMap<Integer,Integer> rockets;
	protected PriorityQueue<String> rallyPoints;
	protected Direction dir;

	/*---------------------- CONSTRUCTORS ----------------------*/

	/**
	 * Default Bot constructor that initializes instance vars.
	 * @param unit the unit that this bot describes
	 * @param gc the Battlecode GameController
	 * @param logs the logistics of the map that the bot is on
	 * @return a new Bot that describes the specified unit
	 */
	public Bot(Unit unit, GameController gc, Logistics logs){
		this.gc = gc;
		this.logs = logs;
		this.unit = unit;
		id = unit.id();
		loc = unit.location().mapLocation();
		targets = logs.targets();
		rockets = logs.rockets();
		rallyPoints = logs.rallyPoints();
	}

	/*---------------------- METHODS ----------------------*/

	// pseudo-interface/abstract methods that subclasses will implement
	public void act(){ }
	public void act2(){ }
	public void actMars(){ }

	/**
	 * Attempts to move, using FuzzyGoTo.
	 * @return true if it is possible, false otherwise
	 */
	public boolean tryMove(){
		if (gc.isMoveReady(id) && dest != null){
			dir = loc.directionTo(dest);
			for (int i = 0; i < Fuzzy.rotateOrder.length; i++){
				Direction newDir = Fuzzy.tryRotate(dir,Fuzzy.rotateOrder[i]);
				if (gc.canMove(id,newDir)){
					gc.moveRobot(id,newDir);
					loc = loc.add(newDir);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Attempts to attack the specified enemy.
	 * @param enemy the unit ID of the enemy
	 * @return true if attack is successful, false otherwise
	 */
	public boolean tryAttack(int enemy){
		if (gc.isAttackReady(id) && gc.canAttack(id,enemy)){
			gc.attack(id,enemy);
			return true;
		}
		return false;
	}

	/**
	 * Finds the unit within a certain range.
	 * @param radius the range to search within
	 * @param whichTeam which team to look at
	 * @return the unit with the highest priority
	 */
	public Unit unitAtRange(long radius, Team whichTeam){

        VecUnit vec = gc.senseNearbyUnitsByTeam(loc,radius,whichTeam);

        if (vec.size() > 0){
        	Unit found = null;
			Unit chosen = null;
			int chosenUnitIndex = 0, priority = 0, currPriority = 0;
			long hp;

			// find the highest priority unit that we can attack
			for (int i = 0; i< vec.size(); i++){
				found = vec.get(i);
				if (gc.canAttack(id, found.id())){
					currPriority = logs.getUnitPriority(found.unitType());
					if (currPriority > priority){
						priority = currPriority;
						chosenUnitIndex = i;
					}
				}
			}
			chosen = vec.get(chosenUnitIndex);
			hp = chosen.health();
			return chosen;
        }
        return null;
	}
}