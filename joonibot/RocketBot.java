/*---------------------- IMPORTS ----------------------*/
// Battlecode
import bc.*;

// Java Imports
import java.util.HashMap;

/*---------------------- CLASS DEF ----------------------*/

public class RocketBot extends Bot{

	/*---------------------- CONSTRUCTORS ----------------------*/
	/**
	 * Default constructor for RocketBot.
	 * @param unit the unit that this RocketBot describes
	 * @param gc the Battlecode GameController
	 * @param logs the logistics of our team
	 * @return a default RocketBot object
	 */
	public RocketBot(Unit unit, GameController gc, Logistics logs){
		super(unit,gc,logs);
	}

	/*---------------------- METHODS ----------------------*/
	/**
	 * The main act method.
	 */
	@Override
	public void act(){
		HashMap<Integer,Integer> bps = logs.blueprints();
		if (unit.structureIsBuilt() != 0){
			// If the rocket is built and is in the blueprints hashmap, remove it
			if (bps.size() > 0 && bps.keySet().contains(id))
      			bps.remove(id);
			VecUnit inRange = gc.senseNearbyUnitsByTeam(loc,2,logs.getOurTeam());
			for (int i = 0; i < inRange.size(); i++){
				Unit u = inRange.get(i);
				if (gc.canLoad(id,u.id())){
					gc.load(id,u.id());
					if (u.unitType() == UnitType.Worker)
						targets.remove(u.id());
				}
			}
			PlanetMap pm = gc.startingMap(Planet.Mars);
			int x = (int)(Math.random()*pm.getWidth());
			int y = (int)(Math.random()*pm.getHeight());
			Team enemyTeam = logs.getOtherTeam();
			VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,80,enemyTeam);
			if (unit.structureGarrison().size() > 4 || enemies.size() > 3 || gc.round() == 749){
				while (!gc.canLaunchRocket(id,new MapLocation(Planet.Mars,x,y))){
					x = (int)(Math.random()*pm.getWidth());
					y = (int)(Math.random()*pm.getHeight());
				}
				rockets.remove(id);
                gc.launchRocket(unit.id(),new MapLocation(Planet.Mars,x,y));
                logs.resetTargets();
			}
		}
	}
}