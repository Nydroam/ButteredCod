import bc.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.Comparator;
public class WorkerBot extends Bot{
	public WorkerBot(Unit u, GameController gc, Logistics logs){
		super(u,gc, logs);
	}


	public void act(){
		
		//assign a target, aka giving a destination
		if(!targets.keySet().contains(id)){
			assignTarget();
		}else{
			dest = targets.get(id);
		}

		if(logs.statistics().get("Worker") < 15){//Limiting amount of workers

			if(tryReplicate()){
				logs.updateStats("Worker",1);
			}
		}

		if(dest!=null){//if the worker has a destination
			//System.out.println("NOT DEST NULL");
			if(!checkAdjacents()){//see if anything adjacent can be done
			tryMove();//see if worker can move
			checkAdjacents();//check adjacents again after moving
			}
		}
	}
	public boolean tryMove(){
		//Pathing variables
		if(gc.isMoveReady(id)){
		BFS rally = new BFS(gc);
		HashMap<String,Direction> paths;
		paths = rally.search(dest,unit,false);
		d = paths.get(loc.toString());
		if(paths.keySet().contains(loc.toString()) && gc.canMove(id,d)){
			gc.moveRobot(id,d);
			return true;
		}else{
			targets.remove(id);
		}
	}
			return false;
		
	}
	
	public boolean checkAdjacents(){
		d = loc.directionTo(dest);
		if(loc.isAdjacentTo(dest)){
				
				if(tryBuild()){return true;}
				else if(gc.canHarvest(id,d))
					gc.harvest(id,d);
				else if(gc.karboniteAt(dest)==0)
					targets.remove(id);
		}
		else if(loc.directionTo(dest)==Direction.Center){
			if(gc.canHarvest(id,d))
					gc.harvest(id,d);
				else if(gc.karboniteAt(dest)==0)
					targets.remove(id);
		}
		tryHarvest();
		return false;
	}


	public boolean tryReplicate(){
		if(gc.karbonite()>15){
			if(dest!=null){
				d = loc.directionTo(dest);
				if(gc.canReplicate(id,d)){
					gc.replicate(id,d);
					return true;
				}
			}

			for(int i = 0; i < Fuzzy.rotateOrder.length; i++){
				Direction newd = Fuzzy.tryRotate(d,Fuzzy.rotateOrder[i]);
				if(gc.canReplicate(id,newd)){
					gc.replicate(id,newd);
					return true;
				}
			}
		}

		return false;
	}
	public boolean tryBuild(){

		VecUnit vec = gc.senseNearbyUnitsByTeam(dest,0,gc.team());
		if(vec.size()>0){
			
			Unit building = vec.get(0);
			System.out.println("TRYING BUILD:" + building.unitType());
			if(building.unitType()==UnitType.Factory||building.unitType()==UnitType.Rocket){
				if(building.structureIsBuilt()==0){
					if(gc.canBuild(id,building.id())){
					gc.build(id,building.id());
					return true;
					}
				}
				else{
					targets.remove(id);
				}
			}
			return true;
		}else{
			targets.remove(id);
		}
		return false;
	}
	public boolean tryHarvest(){
		Direction[] dirs = Direction.values();
		for(int i = 0; i < dirs.length; i++){
			Direction d = dirs[i];
			if(gc.canHarvest(id,d)){
				gc.harvest(id,d);
				return true;
			}
		}
		return false;
	}

	public void assignTarget(){
		//assign a target to current unit and update targets hashmap
		
		HashMap<Integer, Integer> bps = logs.blueprints();
		if(bps.size()>0){//if there are multiple blueprints on the map
				//System.out.println("MOAR THAN 1???");
				VecUnit vec = gc.senseNearbyUnitsByType(loc,16,UnitType.Factory); //see if there are any nearby to path to
				for(int j = 0; j < vec.size(); j++){
					Unit curr = vec.get(j);
	      			if(bps.keySet().contains(curr.id()))//check to see if this is actually one of our blueprints
	    				if(bps.get(curr.id())<5){//assign up to # workers to this blueprint
	      					targets.put(id,curr.location().mapLocation());
	      					bps.put(curr.id(),bps.get(curr.id())+1);
	      					dest = targets.get(id);
	      					break;
	      				}
	      		}
	    }

	    if(dest==null){//continue trying to assign a task
	    	if(logs.statistics().get("Factory")<2&&gc.karbonite()>bc.bcUnitTypeBlueprintCost(UnitType.Factory)){
	    		//build up to 2 factories when available
	    		d = Fuzzy.findAdjacent(loc,gc);
	    		System.out.println(d);
	    		if(gc.canBlueprint(id,UnitType.Factory,d)){
	    			gc.blueprint(id,UnitType.Factory,d);
	    			targets.put(id,loc.add(d));
	    			//bps.put(gc.senseNearbyUnitsByType(loc.add(d),1,UnitType.Factory).get(0).id(),1);
	    			bps.put(gc.senseUnitAtLocation(loc.add(d)).id(),1);
	    			dest = targets.get(id);
	    			logs.updateStats("Factory",1);
	    		}

	    	}
		}

		if(dest==null){//continue trying to assign a task
			if(logs.karbLocations().size()>20){//when workers should continue working
				System.out.println("KARBS?");
				//find the closest deposit in distance
				Comparator<MapLocation> comp = (loc1,loc2) -> Long.compare(loc1.distanceSquaredTo(loc),loc2.distanceSquaredTo(loc));
				Optional<MapLocation> o = logs.karbLocations().parallelStream().min(comp);
				
				if(o.isPresent()){
					System.out.println("KARBS?2");
					targets.put(id,o.get());
					logs.karbLocations().remove(o.get());
					dest = targets.get(id);
				}
			}
		}
	}
	
}