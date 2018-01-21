import bc.*;
import java.util.HashMap;
public class KnightBot extends Bot{
	public KnightBot(Unit u, GameController gc, Logistics logs){
		super(u,gc, logs);
	}

	public void act(){
		Unit enemy = enemyAtRange(200);
		if(enemy!=null){//attempt to attack main target, set destination if exists
			tryAttack(enemy.id());
			dest = enemy.location().mapLocation();
		}

		//shoot at any close enemy
		enemy = enemyAtRange(unit.attackRange());
		if(enemy!=null)
			tryAttack(enemy.id());

		//try moving towards target
		if(dest!=null)
			tryMove();

		if(enemy!=null){//attempt to attack main target again
			tryAttack(enemy.id());
		}

		//shoot at any close enemy
		enemy = enemyAtRange(unit.attackRange());
		if(enemy!=null)
			tryAttack(enemy.id());		

	}


}