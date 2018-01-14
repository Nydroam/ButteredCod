import bc.*;
import java.util.HashMap;
public class FactoryBot extends Bot{
	public FactoryBot(Unit u, GameController gc, Logistics logs){
		super(u,gc, logs);
	}

	public void act(){
		HashMap<Integer, Integer> bps = logs.blueprints();
		if(unit.structureIsBuilt()!=0){//if structure is finished
      		if(bps.size()>0&&bps.keySet().contains(unit.id())){//check the blueprint hashset for this blueprint and remove it
      			bps.remove(unit.id());
      		}
      		

      		if(Math.random()<1.0){
      			produceUnit(UnitType.Ranger);
      		}else
      			produceUnit(UnitType.Knight);
      		unloadUnit();
      	}
	}

	public void produceUnit(UnitType unitType){
		if(gc.karbonite()>bc.bcUnitTypeFactoryCost(unitType)&&gc.canProduceRobot(id,unitType)){
			gc.produceRobot(id,unitType);
		}
		
	}

	public void unloadUnit(){
		d = Fuzzy.findAdjacent(loc,gc);
		if(gc.canUnload(id,d)){
			
			
			gc.unload(id,d);
			
		}
	}
}