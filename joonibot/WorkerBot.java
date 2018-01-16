/*---------------------- IMPORTS ----------------------*/
// Battlecode
import bc.*;

// Java Imports
import java.util.HashMap;
import java.util.Optional;
import java.util.Comparator;

/*---------------------- CLASS DEF ----------------------*/

public class WorkerBot extends Bot {

	/*---------------------- CONSTRUCTORS ----------------------*/
	/**
	 * Default constructor for WorkerBot.
	 * @param unit the unit that this WorkerBot describes
	 * @param gc the Battlecode GameController
	 * @param logs the logistics of our team
	 * @return a default WorkerBot object
	 */
	public WorkerBot(Unit unit, GameController gc, Logistics logs){
		super(unit,gc,logs);
	}

	/*---------------------- METHODS ----------------------*/
	/**
	 * The main act method.
	 */
	@Override
	public void act(){
		
		// Assign a target
		if (!targets.keySet().contains(id))
			assignTarget();
		else
			dest = targets.get(id);

		// Replicate in the beginning, limiting to 20
		if (logs.statistics().get("Worker") < 20 && gc.round() < 20){
			if(tryReplicate())
				logs.updateStats("Worker",1);
		}

		// If there is a destination, see if anything can be done/move/check again
		if(dest!=null){
			if(!checkTargets()){
				tryMove();
				checkTargets();
			}
		}
		else{
			tryHarvest();
			if(gc.isMoveReady(id)){
				tryMove();
				tryHarvest();
			}
		}
	}

	/**
	 * Worker's tryMove method. Overrides the superclass's method.
	 */
	@Override
	public boolean tryMove(){
		if (!super.tryMove()){
			// Pathing variables
			if(gc.isMoveReady(id) && dest != null && gc.round() < 50){
				System.out.println("BFSING");
				BFS rally = new BFS(gc);
				HashMap<String,Direction> paths;
				paths = rally.search(dest,unit,false);
				d = paths.get(loc.toString());
				if(paths.keySet().contains(loc.toString()) && gc.canMove(id,d)){
					gc.moveRobot(id,d);
					return true;
				}else{
					//targets.remove(id);
				}
			}
			else{
				//rando movment
				Direction[] dirs = Direction.values();
				d = dirs[(int)(Math.random()*dirs.length)];
				if(gc.isMoveReady(id)&&gc.canMove(id,d))
					gc.moveRobot(id,d);
					
			}
		}
		return false;
	}

	/**
	 * See if there is anything to be done.
	 * @return true if there is something to be done, false otherwise
	 */
	public boolean checkTargets(){
		if (gc.canSenseLocation(dest)){
			VecUnit vec = gc.senseNearbyUnitsByTeam(dest,0,logs.getOurTeam());
			if (vec.size() > 0){
				Unit building = vec.get(0);
				
				if (building.unitType() == UnitType.Factory || building.unitType() == UnitType.Rocket){
					if (building.structureIsBuilt() == 0){
						if (gc.canBuild(id,building.id())){
							gc.build(id,building.id());
							return true;
						}
					}
					else if (building.health() < building.maxHealth()){
						if (gc.canRepair(id,building.id())){
							gc.repair(id,building.id());
							return true;
						}
					}
					else if (building.unitType() == UnitType.Rocket && loc.isAdjacentTo(dest)){
						return true;
					}
					else{
						targets.remove(id);
					}
				}else{
					if (gc.karboniteAt(dest) == 0)
						targets.remove(id);
					else{
						d = loc.directionTo(dest);
					if (gc.canHarvest(id,d))
						gc.harvest(id,d);
					}
				}
				
			}else{
				if (gc.karboniteAt(dest) == 0)
					targets.remove(id);
				else{
					d = loc.directionTo(dest);
				if(gc.canHarvest(id,d))
					gc.harvest(id,d);
				}
			}
		}
		tryHarvest();
		return false;
	}
	
	/**
	 * Tries to replicate itself.
	 * @return true upon success, false upon failure
	 */
	public boolean tryReplicate(){
		if (gc.karbonite() > 15){
			if (dest != null){
				d = loc.directionTo(dest);
				if(gc.canReplicate(id,d)){
					gc.replicate(id,d);
					return true;
				}
			}
			Direction newDir = null;
			for(int i = 0; i < Fuzzy.rotateOrder.length; i++){
				newDir = Fuzzy.tryRotate(dir,Fuzzy.rotateOrder[i]);
				if(gc.canReplicate(id,newDir)){
					gc.replicate(id,newDir);
					return true;
				}
			}
			if(newDir != null && gc.canReplicate(id,newDir))
				gc.replicate(id,newDir);
		}
		return false;
	}

	/**
	 * Tries to harvest.
	 * @return true upon success, false otherwise
	 */
	public boolean tryHarvest(){
		Direction[] dirs = BFS.directions;
		for (int i = 0; i < dirs.length; i++){
			Direction d = dirs[i];
			if (gc.canHarvest(id,d)){
				gc.harvest(id,d);
				return true;
			}
		}
		return false;
	}

	/**
	 * Assign a target to current unit and update the targets hashmap.
	 */
	public void assignTarget(){
		HashMap<Integer,Integer> bps = logs.blueprints();
		if (dest == null && logs.statistics().get("Rocket") > 0){
			VecUnit vec = gc.senseNearbyUnitsByType(loc,500,UnitType.Rocket);
			for (int i = 0; i < vec.size(); i++){
				Unit u = vec.get(i);
				if (u.team() == gc.team()){//on our team
					targets.put(id,u.location().mapLocation());
					dest = targets.get(id);
					break;
				}
			}
		}

		// IF there are multiple blueprints on the map, move at them
		if (dest == null && bps.size() > 0){
			VecUnit vec = gc.senseNearbyUnitsByTeam(loc,50,gc.team()); 
			for (int j = 0; j < vec.size(); j++){
				Unit curr = vec.get(j);
				// Check to see if it is actually a blueprint, and assign up to # workers to it
				if (bps.keySet().contains(curr.id())){
					if (bps.get(curr.id()) < 4){
						targets.put(id,curr.location().mapLocation());
						bps.put(curr.id(),bps.get(curr.id())+1);
						dest = targets.get(id);
						break;
					}
				}
			}
		}
		// Build up to 2 factories if available
	    if (dest == null){
	    	if (logs.statistics().get("Factory") < gc.karbonite()/200+3 && gc.karbonite() > bc.bcUnitTypeBlueprintCost(UnitType.Factory)){
	    		Team enemyTeam = logs.getOtherTeam();
				VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,80,enemyTeam);
				VecUnit allies = gc.senseNearbyUnitsByTeam(loc,25,logs.getOurTeam());
				if (allies.size() > 3) {
	    			d = Fuzzy.findAdjacent(loc,gc);
					if (gc.canBlueprint(id,UnitType.Factory,d)){
						gc.blueprint(id,UnitType.Factory,d);
						targets.put(id,loc.add(d));
						logs.updateStats("Factory",1);
						//bps.put(gc.senseNearbyUnitsByType(loc.add(d),1,UnitType.Factory).get(0).id(),1);
						bps.put(gc.senseUnitAtLocation(loc.add(d)).id(),1);
						dest = targets.get(id);
					}
				}
	    	}
		}

		// Blueprint rocket
		if (dest == null){
			if(gc.researchInfo().getLevel(UnitType.Rocket) > 0 && logs.statistics().get("Rocket") < 1 &&
					gc.karbonite() > bc.bcUnitTypeBlueprintCost(UnitType.Rocket)){
				VecUnit allies = gc.senseNearbyUnitsByTeam(loc,25,gc.team());
				if (allies.size() > 4){
					d = Fuzzy.findAdjacent(loc,gc);
					if(gc.canBlueprint(id,UnitType.Rocket,d)){
						gc.blueprint(id,UnitType.Rocket,d);
						targets.put(id,loc.add(d));
						logs.updateStats("Rocket",1);
						rockets.put(gc.senseUnitAtLocation(loc.add(d)).id(),0);
						bps.put(gc.senseUnitAtLocation(loc.add(d)).id(),1);
						dest = targets.get(id);
					}
				}
			}
		}

		// Assign repairs
		if(dest == null){
			VecUnit vec = gc.senseNearbyUnitsByTeam(loc,50,logs.getOurTeam());
			if (vec.size() > 0){
				for (int i = 0; i < vec.size(); i++){
					Unit u = vec.get(i);
					if (u.unitType() == UnitType.Factory && u.structureIsBuilt() != 0 && u.health() < u.maxHealth()){
						targets.put(id,u.location().mapLocation());
						dest = targets.get(id);
						break;
					}
				}
			}
		}

		// Continue trying to assign a task when workers should continue working
		if(dest == null){
			if(gc.round() < 100){
				// Find the closest deposit
				Comparator<MapLocation> comp = (loc1,loc2) -> Long.compare(loc1.distanceSquaredTo(loc),loc2.distanceSquaredTo(loc));
				Optional<MapLocation> o = logs.karbLocations().parallelStream().min(comp);
				if(o.isPresent()){
					targets.put(id,o.get());
					logs.karbLocations().remove(o.get());
					dest = targets.get(id);
				}
			}
		}
	}
	
}