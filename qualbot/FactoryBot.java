import bc.*;
import java.util.ArrayList;
public class FactoryBot extends Bot{


	private boolean rush;
	public FactoryBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area, boolean rush){
		super(gc,pm,unit,logs,area);
		this.rush = rush;
	}

	public UnitType priorityUnit(){
		ArrayList<String> rp = area.rallyPoints();
		if(gc.researchInfo().getLevel(UnitType.Rocket)>0&&gc.senseNearbyUnitsByTeam(loc,200,logs.enemyTeam()).size()==0) {
			if(logs.unitCount().get("Worker")<2)
				return UnitType.Worker;
			if(gc.karbonite()<175)
				return null;
			if(Math.random()<0.5)
				return UnitType.Knight;
		}
		if(rp.size()>0&&rush){
			
			int units = logs.unitCount().get("Ranger") + logs.unitCount().get("Knight");
			if(logs.unitCount().get("Healer")<units/4)
				return UnitType.Healer;
			int steps = area.getSteps(unit);
			if(steps<25)
				return UnitType.Knight;
			return UnitType.Ranger;
		}else{
			int units = logs.unitCount().get("Ranger");
			if(logs.unitCount().get("Healer")<units/3)
				return UnitType.Healer;
			return UnitType.Ranger;
		}
	}
	public void act(){
		if(unit.structureIsBuilt()!=0){
			if(area.blueprints().containsKey(id))
				area.blueprints().remove(id);
		}
		UnitType ut = priorityUnit();
		if(ut!=null)
			produceUnit(ut);
		unloadUnit();
	}

	public void produceUnit(UnitType unitType){
		if(gc.karbonite()>bc.bcUnitTypeFactoryCost(unitType)&&gc.canProduceRobot(id,unitType)){
			logs.unitCount().put(unitType.toString(),logs.unitCount().get(unitType.toString())+1);
			gc.produceRobot(id,unitType);
		}

	}

	public void unloadUnit(){
		VecUnitID idList = unit.structureGarrison();
		Direction d = null;
		if(idList.size()>0){
			int uid = idList.get(0);
			if(logs.workerRally().containsKey(uid)){
				d = logs.workerRally().get(uid).get(loc);
				if(d!=null&&gc.canUnload(id,d)){
					gc.unload(id,d);
				}
			}
			else{
				ArrayList<Direction> dirs = area.getDirections(unit,1);
				for(Direction di : dirs){
					if(gc.canUnload(id,di)){
						gc.unload(id,di);
					}
				}
			}
		}
		d = Pathing.findAdjacent(loc,gc);
		while(gc.canUnload(id,d)){
			gc.unload(id,d);
			d = Pathing.findAdjacent(loc,gc);
		}
	}

}
