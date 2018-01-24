/*---------------------- IMPORTS ----------------------*/

// Battlecode
import bc.*;

// Java Imports
import java.util.HashMap;

/*---------------------- CLASS DEF ----------------------*/

public class FactoryBot extends Bot {

	/*---------------------- CONSTRUCTORSS ----------------------*/

	/**
	 * Default FactoryBot constructor.
	 * @param unit the unit this FactoryBot describes
	 * @param gc the Battlecode GameController
	 * @param logs the logistics of our team
	 * @return a default FactoryBot object
	 */
	public FactoryBot(Unit unit, GameController gc, Logistics logs){
		super(unit,gc,logs);
	}

	/*---------------------- METHODS ----------------------*/

	/**
	 * Produces units accordingly. 
	 * If the factory blueprint is done, remove it from the blueprints hashmap in logistics.
	 */
	public void act(){
		HashMap<Integer,Integer> bps = logs.blueprints();
		if (unit.structureIsBuilt() != 0){
      		if (bps.size() > 0 && bps.keySet().contains(id))
				bps.remove(unit.id());  
      		if (logs.statistics().get("Ranger") <= 12)
      			produceUnit(UnitType.Ranger);
      		else if (logs.statistics().get("Healer") <= 2)
      			produceUnit(UnitType.Healer);
      		else if(logs.statistics().get("Worker") <= 5)
      			produceUnit(UnitType.Worker);
      		else if(gc.karbonite()>300)
      			produceUnit(UnitType.Ranger);
      		unloadUnit();
      	}
	}

	/**
	 * Makes sure that the unit can be produced before producing it.
	 */
	public void produceUnit(UnitType unitType){
		if (gc.karbonite() > bc.bcUnitTypeFactoryCost(unitType) && gc.canProduceRobot(id,unitType)){
			logs.updateStats(unitType.toString(),1);
			gc.produceRobot(id,unitType);
		}
	}

	/**
	 * Unloads the unit using fuzzy's findAdjacent.
	 */
	public void unloadUnit(){
		dir = Fuzzy.findAdjacent(loc,gc);
		if (gc.canUnload(id,dir))
			gc.unload(id,dir);
	}
}