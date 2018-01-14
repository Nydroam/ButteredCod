import bc.*;
import java.util.HashMap;
public class RangerBot extends Bot{
	public RangerBot(Unit u, GameController gc, Logistics logs){
		super(u,gc, logs);
	}

	public void act(){

		Unit enemy = enemyAtRange(400);
		if(enemy!=null){//attempt to attack main target, set destination if exists
			

			dest = enemy.location().mapLocation();
			if(tryAttack(enemy.id()))
				return;
		}

		//shoot at any close enemy
		enemy = enemyAtRange(unit.attackRange());
		if(enemy!=null)
			tryAttack(enemy.id());

		else if(dest!=null) //if there is no close enemy, move toward a destination if there is one
			tryMove();

		else{
			Direction[] dirs = Direction.values();
			d = dirs[(int)(Math.random()*dirs.length)];
			if(gc.isMoveReady(id)&&gc.canMove(id,d))
				gc.moveRobot(id,d);
		}

		if(enemy!=null){//attempt to attack main target again
			tryAttack(enemy.id());
		}

		//shoot at any close enemy
		enemy = enemyAtRange(unit.attackRange());
		if(enemy!=null)
			tryAttack(enemy.id());		


	}


}