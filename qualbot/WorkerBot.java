import bc.*;
import java.util.Comparator;
import java.util.Optional;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class WorkerBot extends Bot{
	


	//Targeting and pathing
	private HashMap<Integer,HashMap<String,Direction>> workerRally;
	private HashMap<Integer,String> workerTargets;

	public WorkerBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area){
		super(gc,pm,unit,logs,area);
		workerRally = logs.workerRally();
		workerTargets = logs.workerTargets();
	}
	public void actMars(){
		if(!workerTargets.containsKey(id)){
			if(!assignKarbTarget()){
				tryHarvest();
				tryMove();
				tryHarvest();
				return;
			}
		}else{
			dest = bc.bcMapLocationFromJson(workerTargets.get(id));
		}
		if(workerRally.containsKey(id))//assign or make a path if possible
			paths = workerRally.get(id);
		else if(gc.getTimeLeftMs()>7500){
			Pathing pathing = new Pathing(gc,pm);
			paths = pathing.aSearch(loc,dest);
			workerRally.put(id,paths);
		}
		tryHarvest();

		if(gc.isMoveReady(id)&&!tryMove()){
			workerTargets.remove(id);
			workerRally.remove(id);
			area.karbQueue().offerFirst(dest);
		}
		tryHarvest();
		checkKarbTarget();
	}
	public void act(){
		long time = System.currentTimeMillis();
		if(!workerTargets.containsKey(id)){//if this worker does not have a target/dest
			if(!assignTarget()){
				tryHarvest();
				tryMove();
				return;
			}
		}else{
			dest = bc.bcMapLocationFromJson(workerTargets.get(id));
			if(gc.karbonite()>200&&!gc.canSenseLocation(dest)){
				area.karbQueue().offerFirst(dest);
				if(!assignTarget()){
					tryHarvest();
					tryMove();
					return;
				}
			}
			
		}

		//System.out.println("Assign time: " + (System.currentTimeMillis()-time));
		time = System.currentTimeMillis();
		if(workerRally.containsKey(id))//assign or make a path if possible
			paths = workerRally.get(id);
		else if(gc.getTimeLeftMs()>7500){
			Pathing pathing = new Pathing(gc,pm);
			paths = pathing.aSearch(loc,dest);
			workerRally.put(id,paths);
		}
		//System.out.println("Pathing time: " + (System.currentTimeMillis()-time));

		tryReplicate();
		
		time = System.currentTimeMillis();
		if(checkTarget()){
			tryHarvest();
			if(gc.isMoveReady(id)&&!tryMove()){
				workerTargets.remove(id);
				workerRally.remove(id);
				if(!gc.canSenseLocation(dest))
					area.karbQueue().offerFirst(dest);
				else{
					if(gc.karboniteAt(dest)>0)
						area.karbQueue().offerFirst(dest);
					if(gc.hasUnitAtLocation(dest)){
						Unit u = gc.senseUnitAtLocation(dest);
						HashMap<Integer,Integer> blueprints = area.blueprints();
						if(blueprints.containsKey(u.id()))
							blueprints.put(u.id(),blueprints.get(u.id())-1);
					}
				}
				VecUnit nearby = gc.senseNearbyUnitsByTeam(loc,2,gc.team());
				VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,50,logs.enemyTeam());
				if(enemies.size()>0){
					for(int i = 0; i < nearby.size(); i++)
						if(nearby.get(i).unitType()==UnitType.Knight){
							gc.disintegrateUnit(id);
							System.out.println("DISINTEGRATION----------------");
							return;
						}
				}
				dest = null;
			}
		}
		//System.out.println("Moving time: " + (System.currentTimeMillis()-time));
		tryHarvest();
		checkTarget();
	}
	public boolean assignKarbTarget(){
		LinkedList<MapLocation> karbQueue = area.karbQueue();
		//System.out.println("size " + karbLocations.size());
		//find the closest deposit in distance
		long time = System.currentTimeMillis();
		//Comparator<String> comp = (loc1,loc2) -> Long.compare(bc.bcMapLocationFromJson(loc1).distanceSquaredTo(loc),bc.bcMapLocationFromJson(loc2).distanceSquaredTo(loc));
		//Optional<String> o = karbLocations.keySet().stream().min(comp);
		MapLocation o = null;
		Iterator i = karbQueue.iterator();
		int min = 5000;
		while(i.hasNext()){
			MapLocation l = (MapLocation)i.next();
			int d = (int)l.distanceSquaredTo(loc);
			if(d<min){
				o = l;
				min = d;
				if(d<=10)
					break;
			}
		}
		if(o!=null){
			dest = o;
			workerTargets.put(id,dest.toJson());
			karbQueue.remove(o);
			//System.out.println("Min time: " + (System.currentTimeMillis()-time));
			return true;
		}

		return false;
	}
	public boolean assignTarget(){
		VecUnit buildings = gc.senseNearbyUnitsByTeam(loc,25,gc.team()); 
		for(int i = 0; i < buildings.size() ; i++){

			Unit f = buildings.get(i);
			if(f.unitType()==UnitType.Rocket||f.unitType() == UnitType.Factory){
				HashMap<Integer,Integer> blueprints = area.blueprints();
				if(blueprints.containsKey(f.id())&&blueprints.get(f.id())<4) {
					dest = f.location().mapLocation();
					workerTargets.put(id,dest.toJson());
					blueprints.put(f.id(),blueprints.get(f.id())+1);
					return true;
				}
				if(f.unitType()==UnitType.Rocket||(f.structureIsBuilt()!=0&&f.health()<f.maxHealth())) {
					dest = f.location().mapLocation();
					workerTargets.put(id,dest.toJson());
					return true;
				}
			}
		}

		if(gc.researchInfo().getLevel(UnitType.Rocket)>0&&
			gc.karbonite()>bc.bcUnitTypeBlueprintCost(UnitType.Rocket)&&
			gc.senseNearbyUnitsByTeam(loc,70,logs.enemyTeam()).size()==0 ) {
				Direction d = Pathing.findAdjacent(loc,gc);
				if(gc.canBlueprint(id,UnitType.Rocket,d)){
					gc.blueprint(id,UnitType.Rocket,d);
					Unit blueprint = gc.senseUnitAtLocation(loc.add(d));
					area.blueprints().put(blueprint.id(),1);
					area.unitList().add(blueprint.id());
					dest = loc.add(d);
					workerTargets.put(id,dest.toJson());
					return true;
				}

			}

		if(logs.unitCount().get("Factory")<(gc.karbonite())/200 +3&&
			gc.karbonite()>bc.bcUnitTypeBlueprintCost(UnitType.Factory)){
			VecUnit allies = gc.senseNearbyUnitsByTeam(loc,9,gc.team());
			if(allies.size()>1) {
				Direction d = Pathing.findAdjacent(loc,gc);
				if(gc.canBlueprint(id,UnitType.Factory,d)&&Pathing.numAdjacent(loc,gc)>1&&gc.senseNearbyUnitsByType(loc.add(d),2,UnitType.Factory).size()==0){
					gc.blueprint(id,UnitType.Factory,d);
					Unit blueprint = gc.senseUnitAtLocation(loc.add(d));
					area.blueprints().put(blueprint.id(),1);
					area.unitList().add(blueprint.id());
					dest = loc.add(d);
					workerTargets.put(id,dest.toJson());
					return true;
				}
			}
		}

		

		LinkedList<MapLocation> karbQueue = area.karbQueue();
		//System.out.println("size " + karbLocations.size());
		//find the closest deposit in distance
		long time = System.currentTimeMillis();
		//Comparator<String> comp = (loc1,loc2) -> Long.compare(bc.bcMapLocationFromJson(loc1).distanceSquaredTo(loc),bc.bcMapLocationFromJson(loc2).distanceSquaredTo(loc));
		//Optional<String> o = karbLocations.keySet().stream().min(comp);
		MapLocation o = null;
		Iterator i = karbQueue.iterator();
		int min = 5000;
		while(i.hasNext()){
			MapLocation l = (MapLocation)i.next();
			int d = (int)l.distanceSquaredTo(loc);
			if(d<min){
				o = l;
				min = d;
				if(d<=10)
					break;
			}
		}
		if(o!=null){
			dest = o;
			workerTargets.put(id,dest.toJson());
			karbQueue.remove(o);
			//System.out.println("Min time: " + (System.currentTimeMillis()-time));
			return true;
		}

		return false;
	}

	public Direction findDirection(){
		Direction d = null;
		if (paths != null && paths.containsKey(loc.toJson())){
			d = paths.get(loc.toJson());
			if(d == Direction.Center){
				d = Pathing.findAdjacent(loc,gc);
			}
			return d;
		}
		else{
			if(dest!=null)
				d = loc.directionTo(dest);
			if(d == Direction.Center){
				d = Pathing.findAdjacent(loc,gc);
			}
			return d;
		}
	}

	public boolean tryReplicate(){
		int threshold = area.totalKarbs()/300 + 5;
		if( unit.abilityHeat()<unit.abilityCooldown() &&
			gc.karbonite() > bc.bcUnitTypeReplicateCost(UnitType.Worker) &&
			logs.unitCount().get("Worker")<threshold
			&& gc.karbonite()>logs.unitCount().get("Factory")*40) {
			tryMove();
			Direction d = findDirection();

			if(gc.canReplicate(id,d)){

				gc.replicate(id,d);
				Unit newUnit = gc.senseUnitAtLocation(loc.add(d));
				WorkerBot newBot = new WorkerBot(gc,pm,newUnit,logs,area);
				newBot.act();
				area.addUnit(newUnit.id());
				return true;
			}
		}
		return false;

	}

	public boolean tryMove(){
		if(gc.isMoveReady(id)){

			Direction d = findDirection();
			int[] values = Pathing.rotateOrder;
			for(int i = 0; i < values.length; i++){
				Direction fuzzy = Pathing.tryRotate(d,values[i]);
				MapLocation newl = loc.add(fuzzy);
				if(gc.hasUnitAtLocation(newl)){
					Unit f =gc.senseUnitAtLocation(newl);
					if(f.team()==gc.team()){
						if(f.unitType()==UnitType.Factory)
							if(gc.canLoad(f.id(),id)){
								gc.load(f.id(),id);
								return true;
							}
						/*if(f.unitType()==UnitType.Worker){

							if(gc.isMoveReady(f.id())){
								Direction newd = Pathing.findAdjacent(newl,gc);
								if(gc.canMove(f.id(),newd)){
									gc.moveRobot(f.id(),newd);
								}
							}
						}*/
					}

				}
				if(gc.canMove(id,fuzzy)){
					gc.moveRobot(id,fuzzy);
					loc = loc.add(fuzzy);
					return true;
				}
			}
		}
		return false;
	}

	public boolean tryHarvest(){
		Direction[] values = Direction.values();
		for(int i = 0; i < values.length ;i++)
			if(gc.canHarvest(id,values[i])){
				gc.harvest(id,values[i]);
				return true;
			}
		return false;
	}
	public boolean checkTarget(){

		if(dest!=null){
			if(gc.hasUnitAtLocation(dest)){
				Unit u = gc.senseUnitAtLocation(dest);
				if((u.unitType()==UnitType.Factory || u.unitType() == UnitType.Rocket)&&u.team()==gc.team()){
					if(u.structureIsBuilt()!=0 && u.health()==u.maxHealth()){

						workerTargets.remove(id);
						workerRally.remove(id);
						return true;
					}else if(gc.canBuild(id,u.id())){
						gc.build(id,u.id());
						return false;
					}else if(u.health()<u.maxHealth()&&gc.canRepair(id,u.id())){
						gc.repair(id,u.id());
						return false;
					}
					return true;
				}
			}
			if(gc.canSenseLocation(dest)&&gc.karboniteAt(dest)==0){
				workerTargets.remove(id);
				workerRally.remove(id);
				return true;
			}
		}
		return true;
	}

	public boolean checkKarbTarget(){
		if(gc.canSenseLocation(dest)&&gc.karboniteAt(dest)==0){
			workerTargets.remove(id);
			workerRally.remove(id);

			return true;
		}
		return false;
	}
	
}