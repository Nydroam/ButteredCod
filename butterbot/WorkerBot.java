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

		if(logs.statistics().get("Worker") < 15 && gc.round()<15){//Limiting amount of workers

			if(tryReplicate())
				logs.updateStats("Worker",1);
		}

		if(dest!=null){//if the worker has a destination
			
			if(!checkTargets()){//see if anything can be done
			tryMove();//see if worker can move
			checkTargets();//check again after moving
			}
		}else{
			tryHarvest();
			if(gc.isMoveReady(id)){
				
				tryMove();
				tryHarvest();
			}
		}
	}
	public boolean tryMove(){
		if(!super.tryMove()){
		//Pathing variables
		if(gc.isMoveReady(id)&&dest!=null&&gc.round()<50){
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

	public boolean checkTargets(){

		if(gc.canSenseLocation(dest)){
			VecUnit vec = gc.senseNearbyUnitsByTeam(dest,0,gc.team());
			if(vec.size()>0){
				Unit building = vec.get(0);
				
				if(building.unitType()==UnitType.Factory||building.unitType()==UnitType.Rocket){
					if(building.structureIsBuilt()==0){
						if(gc.canBuild(id,building.id())){
							gc.build(id,building.id());
							return true;
						}
					}
					else if(building.health()<building.maxHealth()){
						if(gc.canRepair(id,building.id())){
							gc.repair(id,building.id());
							return true;
						}
					}
					else if(building.unitType()==UnitType.Rocket&&loc.isAdjacentTo(dest)){
						return true;
					}
					else{
						targets.remove(id);
					}
				}else{
					if(gc.karboniteAt(dest)==0)
						targets.remove(id);
					else{
						d = loc.directionTo(dest);
					if(gc.canHarvest(id,d))
						gc.harvest(id,d);
					}
				}
				
			}else{
				if(gc.karboniteAt(dest)==0)
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
	
	


	public boolean tryReplicate(){
		if(gc.karbonite()>15){
			if(dest!=null){
				d = loc.directionTo(dest);
				if(gc.canReplicate(id,d)){
					gc.replicate(id,d);
					return true;
				}
			}
			Direction newd = null;
			for(int i = 0; i < Fuzzy.rotateOrder.length; i++){
				newd = Fuzzy.tryRotate(d,Fuzzy.rotateOrder[i]);
				if(gc.canReplicate(id,newd)){
					gc.replicate(id,newd);
					return true;
				}
			}
			if(newd != null&&gc.canReplicate(id,newd))
				gc.replicate(id,newd);
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
		if(dest==null&&logs.statistics().get("Rocket")>0){
			VecUnit vec = gc.senseNearbyUnitsByType(loc,500,UnitType.Rocket);
			for(int i = 0; i < vec.size(); i++){
				Unit u = vec.get(i);
				if(u.team()==gc.team()){//on our team
					targets.put(id,u.location().mapLocation());

					dest = targets.get(id);
					break;
				}
			}

		}

		if(dest==null&&bps.size()>0){
			//if there are multiple blueprints on the map, move at them
				
					VecUnit vec = gc.senseNearbyUnitsByTeam(loc,50,gc.team()); 
					for(int j = 0; j < vec.size(); j++){
						Unit curr = vec.get(j);
		      			if(bps.keySet().contains(curr.id()))//check to see if this is actually one of our blueprints
		    				if(bps.get(curr.id())<4){//assign up to # workers to this blueprint
		    				
		      					targets.put(id,curr.location().mapLocation());
		      					bps.put(curr.id(),bps.get(curr.id())+1);
		      					dest = targets.get(id);
		      					
		      					break;
		      				}
		      		}
		      	
	    }

	    if(dest==null){//blueprint factory
	    	if(logs.statistics().get("Factory")<gc.karbonite()/100+2&&gc.karbonite()>bc.bcUnitTypeBlueprintCost(UnitType.Factory)){
	    		//build up to 2 factories when available

	    		Team enemyTeam = Team.Red;
				if(gc.team()==Team.Red)
					enemyTeam = Team.Blue;
				VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,70,enemyTeam);
				VecUnit allies = gc.senseNearbyUnitsByTeam(loc,25,gc.team());
				if(enemies.size()<3&&allies.size()>3) {
	    		
	    		d = Fuzzy.findAdjacent(loc,gc);
	    		
		    		if(gc.canBlueprint(id,UnitType.Factory,d)&&Fuzzy.numAdjacent(loc.add(d),gc)>0){
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

		if(dest == null){//blueprint rocket
			if(gc.researchInfo().getLevel(UnitType.Rocket)>0&&logs.statistics().get("Rocket")<2&&gc.karbonite()>bc.bcUnitTypeBlueprintCost(UnitType.Rocket)){
				VecUnit allies = gc.senseNearbyUnitsByTeam(loc,25,gc.team());
				if(allies.size()>4){
				d = Fuzzy.findAdjacent(loc,gc);
				if(gc.canBlueprint(id,UnitType.Rocket,d)&&Fuzzy.numAdjacent(loc.add(d),gc)>2){
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

		if(dest == null){//assign repairs
			VecUnit vec = gc.senseNearbyUnitsByTeam(loc,50,gc.team());
			if(vec.size()>0){
				for(int i = 0; i < vec.size(); i++){
					Unit u = vec.get(i);
					if(u.unitType()==UnitType.Factory&&u.structureIsBuilt()!=0&&u.health()<u.maxHealth()){
						targets.put(id,u.location().mapLocation());
						dest = targets.get(id);
						break;
					}
				}
			}


		}

		if(dest==null){//continue trying to assign a task
			if(gc.round()<100){//when workers should continue working
			
				//find the closest deposit in distance
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