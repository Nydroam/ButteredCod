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
	      			if(unit.unitType() == UnitType.Worker){//worker AI
	      				if(!workerTargets.keySet().contains(id+"")){//assign a target to a worker
	      					
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
	      					dest = workerTargets.get(id+"");

	      				}
	      				Direction d;
	      				if(workerCount < 10){//Earlygame

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
		      					if(loc.isAdjacentTo(dest)||loc.toString().equals(dest.toString())){
		      						d = loc.directionTo(dest);
		      						if(gc.canHarvest(id,d))
		      							gc.harvest(id,d);
		      						else if(gc.karboniteAt(dest)==0){
		      							workerTargets.remove(id+"");
		      							//System.out.println("Done Mining!");
		      						}
		      					}
		      					else if(gc.isMoveReady(id)){
		      						paths = workerRally.search(dest,unit);
		      						d = paths.get(loc.toString());
		      						if(paths.keySet().contains(loc.toString()) && gc.canMove(id,d)){
		      							gc.moveRobot(id,d);
		      						}
		      					}
		      						
		      				}

	      					//}
	      				}
	      			}

	      		//end turn
	      			//System.out.println(workerTargets);
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