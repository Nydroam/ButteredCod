import bc.*;
import java.util.HashMap;
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
			VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,2500,enemyTeam);

			if(enemies.size()>0||logs.statistics().get("Ranger")<15)
				produceUnit(UnitType.Ranger);
			else if(logs.statistics().get("Worker")<=5)
				produceUnit(UnitType.Worker);

      		
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