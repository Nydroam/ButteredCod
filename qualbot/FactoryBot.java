import bc.*;
public class FactoryBot extends Bot{

	public FactoryBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area){
		super(gc,pm,unit,logs,area);
	}

	public void act(){
		if(unit.structureIsBuilt()!=0){
			if(area.blueprints().containsKey(id))
				area.blueprints().remove(id);
		}
		produceUnit(UnitType.Ranger);
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
		}
		d = Pathing.findAdjacent(loc,gc);
		while(gc.canUnload(id,d)){
			gc.unload(id,d);	
			d = Pathing.findAdjacent(loc,gc);	
		}
	}	

}