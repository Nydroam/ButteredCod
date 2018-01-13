import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Comparator;
public class Player {
	public static void main(String[] args){
// Initialization
		GameController gc = new GameController();
		PlanetMap pm = gc.startingMap(gc.planet());

		MapLocation test1 = new MapLocation(gc.planet(),10,10);

		MapLocation test2 = new MapLocation(gc.planet(),11,12);

		//Keeping track of Karbonite
		ArrayList<MapLocation> karbLocations = new ArrayList<MapLocation>();

		
        //Scans map for initial karbonite deposits and records locations
		for(int x = 0; x < pm.getWidth(); x++)
			for(int y = 0; y < pm.getHeight(); y++){
				MapLocation loc = new MapLocation(gc.planet(),x,y);
				if(pm.initialKarboniteAt(loc)>0)
					karbLocations.add(loc);
			}

		//ENEMY
		Team enemyTeam = Team.Red;
		if(gc.team()==Team.Red)
            enemyTeam = Team.Blue;

		//Acts like an arraylist of units
		VecUnit units = gc.myUnits();

		//Pathing class
			BFS rally = new BFS(gc);
		//<MapLocation as a String, direction to go if you're on that MapLocation
			HashMap<String,Direction> paths;

		//Keeps track of where workers are currently pathing to
		//<unit id, maplocation to go to>
			HashMap<Integer,MapLocation> workerTargets = new HashMap<Integer,MapLocation>();
			int workerCount = (int)units.size();
			
		//Earth Program
      	if(gc.planet()==Planet.Earth){//=======================EARTH============================================

      		//<blueprint id, # of workers that are assigned it>
      		HashMap<Integer,Integer> bps = new HashMap<Integer,Integer>();
      		int numFactories = 0;
      		
      		while (true) {// main loop

      			//update units this turn
      			units = gc.myUnits();

      			//printing stuff for debugging
      			System.out.println("Round: " + gc.round());
      			System.out.println("Karbonite: " + gc.karbonite());
      			System.out.println("Units: " + units.size());
				//rally.printMap(); Pathing Map
      			
      			for(int i = 0; i < units.size(); i++){// looping through units

      				//Convenience variables for less typing
      				Unit unit = units.get(i);
      				int id = unit.id();

      				if(unit.location().isInGarrison()){
      					continue;
      				}
      				MapLocation loc = unit.location().mapLocation();
      				
      				
      				//Current destination of unit, to be set later
      				MapLocation dest = null;

      				//Current direction to move in, to be set later
      				Direction d;

      				if(unit.unitType() == UnitType.Factory){//factory AI
      					if(unit.structureIsBuilt()!=0){//if structure is finished
      						if(bps.keySet().size()>0&&bps.keySet().contains(id)){//check the blueprint hashset for this blueprint and remove it
      							bps.remove(id);
      						}

      						if(gc.karbonite()>bc.bcUnitTypeFactoryCost(UnitType.Ranger)&&gc.canProduceRobot(id,UnitType.Ranger)){//making Rangers
      							gc.produceRobot(id,UnitType.Ranger);
      						}
      						d = PathFinder.findAdjacent(loc,gc,pm);	
      						if(gc.canUnload(id,d))
      							gc.unload(id,d);

      					}
      				}

      				if(unit.unitType() == UnitType.Ranger){//Ranger AI
      					VecUnit vec = gc.senseNearbyUnitsByTeam(loc,400,enemyTeam);
      					Unit enemy = null;
      					Unit close;
      					if(vec.size()>0){
      						 enemy = vec.get(0);
      						dest = enemy.location().mapLocation();
      					}
      					
      					if(dest!=null){
	      					d = loc.directionTo(dest);
	      					
	      					//look for a nearby close enemy
	      					vec = gc.senseNearbyUnitsByTeam(loc,50,enemyTeam);
	      					close = null;
	      					if(vec.size()>0)
	      						close = vec.get(0);

							if(close!=null&&gc.isAttackReady(id)&&gc.canAttack(id,close.id()))
	      						gc.attack(id,close.id());

	      					//move toward a target
	      					else if(gc.isMoveReady(id)&&gc.canMove(id,d))
	      						gc.moveRobot(id,d);
	      					else{
	      						int[] turns = {-1,1,-2,2};
	      						for(int j = 0; j < turns.length; j++){
	      							Direction newd = PathFinder.tryRotate(d,turns[j]);
	      							if(gc.isMoveReady(id)&&gc.canMove(id,newd)){
	      								gc.moveRobot(id,newd);
	      								break;
	      							}
	      						}
	      						if(gc.isMoveReady(id)){
	      						paths = rally.search(dest,unit,false);
	      						
	      						d = paths.get(loc.toString());
	      					if(paths.keySet().contains(loc.toString())&&gc.canMove(id,d))
	      						gc.moveRobot(id,d);
	      				}
	      					}

							//look for a nearby close enemy
	      					vec = gc.senseNearbyUnitsByTeam(loc,50,enemyTeam);
	      					close = null;
	      					if(vec.size()>0)
	      						close = vec.get(0);

	      					if(close!=null&&gc.isAttackReady(id)&&gc.canAttack(id,close.id()))
	      						gc.attack(id,close.id());
	      					//attack target if in range
	      					if(enemy!=null&&gc.isAttackReady(id)&&gc.canAttack(id,enemy.id()))
	      						gc.attack(id,enemy.id());

	      				}else{
	      					
	      				}
      				}

	      			if(unit.unitType() == UnitType.Worker){//worker AI
	      				
	      				if(!workerTargets.keySet().contains(id)){//if a worker doesn't have a target

	      					if(bps.keySet().size()>0){//if there are multiple blueprints to build
	      						VecUnit vec = gc.senseNearbyUnitsByType(loc,50,UnitType.Factory); //see if there are any nearby to path to
	      						for(int j = 0; j < vec.size(); j++){
	      							Unit curr = vec.get(j);
	      							if(bps.keySet().contains(curr.id()))//check to see if this is actually one of our blueprints
	      								if(bps.get(curr.id())<5){//assign maximum of 4 workers to this blueprint
	      									workerTargets.put(id,curr.location().mapLocation());
	      									bps.put(curr.id(),bps.get(curr.id())+1);
	      									dest = workerTargets.get(id);
	      									break;
	      								}
	      							}
	      						}
	      					if(dest!=null){}//checking to see if worker was assigned a destination, if not continue
	      					
	      					else if(numFactories<2&&gc.karbonite()>bc.bcUnitTypeBlueprintCost(UnitType.Factory)){
	      						//if we don't have 2 factories and we have enough karbonite to build more, place a blueprint
	      						
	      						//find an open space to put a blueprint
	      						d = PathFinder.findAdjacent(loc,gc,pm);
	      						if(gc.canBlueprint(id,UnitType.Factory,d)){
	      							gc.blueprint(id,UnitType.Factory,d);
	      							workerTargets.put(id,loc.add(d));
	      							bps.put(gc.senseUnitAtLocation(loc.add(d)).id(),1);
	      							dest = workerTargets.get(id);
	      							numFactories++;
	      						}

	      					}
	      					else if(karbLocations.size()>20){//if there is still a decent number of karbonite deposits left, path to them

	      						//find the closest deposit in distance
	      						Comparator<MapLocation> comp = (loc1,loc2) -> Long.compare(loc1.distanceSquaredTo(loc),loc2.distanceSquaredTo(loc));
	      						Optional<MapLocation> o = karbLocations.parallelStream().min(comp);
	      						if(o.isPresent()){
	      							workerTargets.put(id,o.get());
	      							karbLocations.remove(o.get());
	      							dest = workerTargets.get(id);
	      						}
	      					}
	      					else{
	      					}
	      				}else{
	      					dest = workerTargets.get(id);
	      				}
	      				
	      				if(workerCount < 15){//if there are less than 15 workers try to replicate

	      					//try replicating towards the worker's current destination to save movement
	      					if(dest!=null){
	      						d = loc.directionTo(dest);
	      						if(!gc.canReplicate(id,d)) //if that location is blocked, find an empty one
	      							d = PathFinder.findAdjacent(loc,gc,pm);	
	      					}
	      					else{//if there is no destination, find an adjacent tile
	      						d = PathFinder.findAdjacent(loc,gc,pm);	
	      					}
	      					if(gc.canReplicate(id,d)){//replicate when conditions work out
	      						if(gc.karbonite()>bc.bcUnitTypeReplicateCost(UnitType.Worker)){
	      							gc.replicate(id,d);
	      							workerCount++;
	      						}
	      					}
	      				}

	      				if(!(dest==null)){//if the worker has a destination

	      					if(loc.isAdjacentTo(dest)){//check to see if it's next to the destination
		      					d = loc.directionTo(dest);
		      					VecUnit vec = gc.senseNearbyUnitsByType(dest,0,UnitType.Factory);
		      						if(vec.size()>0){//if it's next its factory destination
			      						Unit other = vec.get(0);
			      						if(other.unitType()==UnitType.Factory){
			      								if(other.structureIsBuilt()==0){//if the factory isn't built, build it
			      								if(gc.canBuild(id,other.id())){
			      									gc.build(id,other.id());
			      								}
			      							}else{
			      								workerTargets.remove(id);
			      							}

			      						}

		      						}
		      						else if(gc.canHarvest(id,d))//if it can harvest its destination harvest
		      							gc.harvest(id,d);
		      						else if(gc.karboniteAt(dest)==0)//if it doesn't have karbonite, reassign worker's target
		      							workerTargets.remove(id);

	      					}
	      					else if(loc.directionTo(dest)==Direction.Center){//if it's on its destination (by replication or otherwise)
	      					d = loc.directionTo(dest);
	      							//same as previous harvest
	      					if(gc.canHarvest(id,d))
	      						gc.harvest(id,d);
	      					else if(gc.karboniteAt(dest)==0){
	      						workerTargets.remove(id);
	      					}
	      				}
	      				else if(gc.isMoveReady(id)){// if the worker is still able to move
	      					//find a path from worker to target
	      					paths = rally.search(dest,unit,false);
	      					d = paths.get(loc.toString());
	      					if(paths.keySet().contains(loc.toString()) && gc.canMove(id,d)){//
	      						gc.moveRobot(id,d);
	      					}else{//if it can't get to its destination, remove the destination
	      					workerTargets.remove(id);
	      				}
	      			}

	      		}
	      			else{//last thing to check
	      			
	      			}

	      		}
	      	}
	      	
	      	//ending turn
	      	gc.nextTurn();


	      }
	  }

      	else{//===============================================MARS=============================================
      		while (true) {
      			gc.nextTurn();
      		}
      	}
      }
  }