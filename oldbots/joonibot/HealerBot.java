/*---------------------- IMPORTS ----------------------*/
// Battlecode
import bc.*;

// Java Imports
import java.util.HashMap;
import java.util.ArrayList;

/*---------------------- CLASS DEF ----------------------*/

public class HealerBot extends Bot {

	/*---------------------- CONSTRUCTORS ----------------------*/
	/**
	 * Default constructor for HealerBot.
	 * @param unit the unit that this HealerBot describes
	 * @param gc the Battlecode GameController
	 * @param logs the logistics of our team
	 * @return a default HealerBot object
	 */
	public HealerBot(Unit unit, GameController gc, Logistics logs){
		super(unit,gc,logs);
	}

	/**
	 * Finds the highest priority (closest, non-full hp) ally.
	 * @param radius the range to check in
	 * @param whichTeam which team to look at (for healer, our team)
	 * @return the unit to go heal
	 */
	@Override
	public Unit unitAtRange(long radius, Team whichTeam){
		VecUnit vec = gc.senseNearbyUnitsByTeam(loc,radius,whichTeam);
		if (vec.size() > 0){
        	Unit found = null;
			Unit chosen = null;
			int chosenUnitIndex = 0, maxPriority = 0, currPriority = 0;
			long hp;
			for (int i = 0; i< vec.size(); i++){
				found = vec.get(i);
				if (found.health() < found.maxHealth()){
					currPriority = logs.getUnitPriority(found.unitType());
					if (currPriority > maxPriority || (currPriority == maxPriority && vec.get(chosenUnitIndex).health() < found.health())){
						maxPriority = currPriority;
						chosenUnitIndex = i;
					}
				}
			}
			chosen = vec.get(chosenUnitIndex);
			hp = chosen.health();
			return chosen;
		}
        return null;
	}

	/**
	 * Tries to heal allies at every possible step.
	 */
	@Override
	public void act(){
		Unit ally = unitAtRange(2500, gc.team());
		if (ally != null){
			tryAttack(ally.id());
			dest = ally.location().mapLocation();
		}
		ally = unitAtRange(unit.attackRange(),  gc.team());
		if (ally != null)
			tryAttack(ally.id());
		if (dest != null)
			tryMove();
		if (ally != null)
			tryAttack(ally.id());
		ally = unitAtRange(unit.attackRange(),  gc.team());
		if (ally != null)
			tryAttack(ally.id());	
	}

	/**
	 * Tries to heal.
	 * @param allyID unit ID of its ally to heal
	 * @return true upon success, false otherwise
	 */
	@Override
	public boolean tryAttack(int allyID){
		if (gc.isHealReady(id) && gc.canHeal(id,allyID)){
			gc.heal(id,allyID);
			return true;
		}
		return false;
	}

}