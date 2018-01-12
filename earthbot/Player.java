import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Comparator;
public class Player {
	public static void main(String[] args){

		// Connect to the manager, starting the game
		GameController gc = new GameController();

        //Scouting the map
		PlanetMap pm = gc.startingMap(gc.planet());

		ArrayList<MapLocation> karbLocations = new ArrayList<MapLocation>();
        //Scouting for karbonite
		for(int x = 0; x < pm.getWidth(); x++)
			for(int y = 0; y < pm.getHeight(); y++){
				MapLocation loc = new MapLocation(gc.planet(),x,y);
				if(pm.initialKarboniteAt(loc)>0)
					karbLocations.add(loc);
			}

			HashMap< String,MapLocation> workerTargets = new HashMap<String,MapLocation>();
      	//unit list to be constantly updated
			VecUnit units = gc.myUnits();

			int workerCount = 0;
			for(int i = 0; i < units.size(); i++){
				Unit u = units.get(i);
				MapLocation uloc = u.location().mapLocation();
				Comparator<MapLocation> comp = (loc1,loc2) -> Long.compare(loc1.distanceSquaredTo(uloc),loc2.distanceSquaredTo(uloc));
				Optional<MapLocation> o = karbLocations.parallelStream().min(comp);
				if(o.isPresent()){

					workerTargets.put(u.id()+"",o.get());
					karbLocations.remove(o.get());
					
				}
				workerCount++;
			}

      	if(gc.planet()==Planet.Earth){//=======================EARTH============================================

      		BFS workerRally = new BFS(gc);
      		HashMap<String,Direction> paths;
      		
      		HashMap<Integer,Integer> bps = new HashMap<Integer,Integer>();
      		int numFactories = 0;
      		
      		while (true) {

      			units = gc.myUnits();
      			System.out.println("Round: " + gc.round());
      			System.out.println("Karbonite: " + gc.karbonite());
      			System.out.println("Units: " + units.size());
      			


	      		//workerRally.printMap();
      			for(int i = 0; i < units.size(); i++){

      				Unit unit = units.get(i);
      				int id = unit.id();
      				MapLocation loc = unit.location().mapLocation();
      				MapLocation dest = null;
      				if(unit.unitType() == UnitType.Factory){
      					if(unit.structureIsBuilt()!=0){
      						if(bps.keySet().size()>0&&bps.keySet().contains(id)){
      							bps.remove(id);
      							System.out.println("REMOVAL==================================================");
      						}
      					}
      				}
	      			if(unit.unitType() == UnitType.Worker){//worker AI
	      				Direction d;
	      				if(!workerTargets.keySet().contains(id+"")){//assign a target to a worker
	      					if(bps.keySet().size()>0){
	      						VecUnit vec = gc.senseNearbyUnitsByType(loc,50,UnitType.Factory);
	      						for(int j = 0; j < vec.size(); j++){
	      							Unit curr = vec.get(j);
	      							if(bps.keySet().contains(curr.id()))
	      								if(bps.get(curr.id())<4){
	      									workerTargets.put(id+"",curr.location().mapLocation());
	      									bps.put(curr.id(),bps.get(curr.id())+1);
	      									dest = workerTargets.get(id+"");
	      								}
	      						}
	      					}
	      					if(dest!=null){}

	      					else if(numFactories<2&&gc.karbonite()>bc.bcUnitTypeBlueprintCost(UnitType.Factory)){
	      						d = PathFinder.findAdjacent(loc,gc,pm);
	      						if(gc.canBlueprint(id,UnitType.Factory,d)){
	      							gc.blueprint(id,UnitType.Factory,d);
	      							workerTargets.put(id+"",loc.add(d));
	      							bps.put(gc.senseUnitAtLocation(loc.add(d)).id(),1);
	      							dest = workerTargets.get(id+"");
	      							numFactories++;
	      							System.out.println("BLUEPRINT PLACED ----------------------");
	      						}

	      					}
	      					else if(gc.round()<40){
		      					Comparator<MapLocation> comp = (loc1,loc2) -> Long.compare(loc1.distanceSquaredTo(loc),loc2.distanceSquaredTo(loc));
		      					Optional<MapLocation> o = karbLocations.parallelStream().min(comp);
		      					if(o.isPresent()){
		      						workerTargets.put(id+"",o.get());
		      						karbLocations.remove(o.get());
		      						dest = workerTargets.get(id+"");
		      					}else{
		      						//System.out.println("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		      					}
		      				}else{

		      				}
	      				}else{
	      					dest = workerTargets.get(id+"");

	      				}
	      				
	      				if(workerCount < 15){//Earlygame

	      					d = loc.directionTo(dest);
	      					if(d==Direction.Center||d==null)
	      						d = PathFinder.findAdjacent(loc,gc,pm);
	      					if(gc.canReplicate(id,d)){
	      						if(gc.karbonite()>bc.bcUnitTypeReplicateCost(UnitType.Worker)){
	      							gc.replicate(id,d);
	      							workerCount++;
	      						}
	      					}
	      					else{
	      						
	      					}
	      				}//else{
	      					if(!(dest==null)){
		      					if(loc.isAdjacentTo(dest)){
		      						d = loc.directionTo(dest);
		      						VecUnit vec = gc.senseNearbyUnitsByType(dest,0,UnitType.Factory);
		      						if(vec.size()>0){
		      							Unit other = vec.get(0);
		      							if(other.unitType()==UnitType.Factory){
		      								if(other.structureIsBuilt()==0){
		      									if(gc.canBuild(id,other.id())){
		      										gc.build(id,other.id());
		      										System.out.println("FACTORY BUILDING ----------------------");
		      									}
		      								}else{
		      									workerTargets.remove(id+"");
		      								}
		      								
		      							}

		      						}
		      						else if(gc.canHarvest(id,d))
		      							gc.harvest(id,d);
		      						else if(gc.karboniteAt(dest)==0){
		      							workerTargets.remove(id+"");
		      						}
		      					}
		      					else if(loc.directionTo(dest)==Direction.Center)
		      					{d = loc.directionTo(dest);
		      						if(gc.canHarvest(id,d))
		      							gc.harvest(id,d);
		      						else if(gc.karboniteAt(dest)==0){
		      							workerTargets.remove(id+"");
		      						}
		      					}
		      					else if(gc.isMoveReady(id)){
		      						paths = workerRally.search(dest,unit);
		      						d = paths.get(loc.toString());

		      						 if(paths.keySet().contains(loc.toString()) && gc.canMove(id,d)){
		      							gc.moveRobot(id,d);
		      						}else{
		      							workerTargets.remove(id+"");
		      							
		      						}
		      					}
		      						
		      				}
		      				else{
		      					d = PathFinder.findAdjacent(loc,gc,pm);
		      					if(gc.isMoveReady(id)&&gc.canMove(id,d))
		      						gc.moveRobot(id,d);
		      				}
	      					//}
	      				}
	      			}

	      		//end turn
	      			System.out.println(workerTargets);
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