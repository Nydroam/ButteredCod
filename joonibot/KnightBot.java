/*---------------------- IMPORTS ----------------------*/

// Battlecode
import bc.*;

// Java Imports
import java.util.HashMap;

/*---------------------- CLASS DEF ----------------------*/

public class KnightBot extends Bot {

	/*---------------------- CONSTRUCTORS ----------------------*/
	/**
	 * Default constructor for KnightBot.
	 * @param unit the unit that this KnightBot describes
	 * @param gc the Battlecode GameController
	 * @param logs the logistics of our team
	 * @return a default KnightBot object
	 */
	public KnightBot(Unit unit, GameController gc, Logistics logs){
		super(unit,gc,logs);
	}

	/*---------------------- METHODS ----------------------*/
	/**
	 * Attacks enemies at every possible step..
	 */
	@Override
	public void act(){
		Unit enemy = unitAtRange(200,logs.getOtherTeam());
		if (enemy != null){
			tryAttack(enemy.id());
			dest = enemy.location().mapLocation();
		}

		enemy = unitAtRange(unit.attackRange(),logs.getOtherTeam());
		if (enemy != null)
			tryAttack(enemy.id());
		if (dest != null)
			tryMove();
		if (enemy != null)
			tryAttack(enemy.id());
			enemy = unitAtRange(unit.attackRange(),logs.getOtherTeam());
		if (enemy != null)
			tryAttack(enemy.id());	
	}


}