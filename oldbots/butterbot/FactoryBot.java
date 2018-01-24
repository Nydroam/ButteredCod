import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class FactoryBot extends Bot{
	public FactoryBot(Unit u, GameController gc, Logistics logs){
		super(u,gc, logs);
	}

	public void act(){
		HashMap<Integer, Integer> bps = logs.blueprints();
		if(unit.structureIsBuilt()!=0){//if structure is finished
      		if(bps.size()>0&&bps.keySet().contains(id)){//check the blueprint hashset for this blueprint and remove it
      			bps.remove(unit.id());
      		}
      		
      		Team enemyTeam = Team.Red;
			if(gc.team()==Team.Red)
				enemyTeam = Team.Blue;
      		VecUnit c = gc.senseNearbyUnitsByTeam(loc,16,enemyTeam);
      		ArrayList<Unit> enemyFactories = new ArrayList<Unit>();
      		for(int i = 0; i < c.size(); i++){
      			if(c.get(i).unitType()==UnitType.Factory)
      				enemyFactories.add(c.get(i));
      		}

      		if(enemyFactories.size()>0){
      			produceUnit(UnitType.Knight);
      		}else{
      			VecUnit far = gc.senseNearbyUnitsByTeam(loc,400,enemyTeam);
      			if(far.size()>10){
		      		if(logs.statistics().get("Ranger")<logs.statistics().get("Healer")*5){
		      			produceUnit(UnitType.Ranger);
		      		}else{
		      			produceUnit(UnitType.Healer);
		      		}
	      		}
	      		else if(logs.statistics().get("Worker")<=5){
	      			produceUnit(UnitType.Worker);
	      		}else if(gc.karbonite()>250){
	      			if(logs.statistics().get("Ranger")<logs.statistics().get("Healer")*5){
		      			produceUnit(UnitType.Ranger);
		      		}else{
		      			produceUnit(UnitType.Healer);
		      		}
	      		}
	      	}

      		
      		/*
      		else
      			produceUnit(UnitType.Knight);*/
      		unloadUnit();
      	}
	}

	public void produceUnit(UnitType unitType){
		if(gc.karbonite()>bc.bcUnitTypeFactoryCost(unitType)&&gc.canProduceRobot(id,unitType)){
			logs.updateStats(unitType.toString(),1);
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