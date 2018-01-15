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
			if(gc.senseNearbyUnits(loc,2).size()>5){
			Direction[] dirs = Direction.values();
			d = dirs[(int)(Math.random()*dirs.length)];
			if(gc.isMoveReady(id)&&gc.canMove(id,d))
				gc.moveRobot(id,d);
			}
		}

		if(enemy!=null){//attempt to attack main target again
			tryAttack(enemy.id());
		}

		//shoot at any close enemy
		enemy = enemyAtRange(unit.attackRange());
		if(enemy!=null)
			tryAttack(enemy.id());		

		if(gc.isMoveReady(id)){
		Direction[] dirs = Direction.values();
		d = dirs[(int)(Math.random()*dirs.length)];
		if(gc.canMove(id,d))
			gc.moveRobot(id,d);
		}
	}

	public void act2(){
		if(logs.statistics().get("Rocket")>0){
			dest = null;
			VecUnit vec = gc.senseNearbyUnitsByType(loc,500,UnitType.Rocket);
			for(int i = 0; i < vec.size(); i++){
				Unit u = vec.get(i);
				if(u.team()==gc.team()){//on our team
					targets.put(id,u.location().mapLocation());
					dest = targets.get(id);
					break;
				}
			}
		
		if(dest!=null){//attack move attack
			Unit enemy = enemyAtRange(unit.attackRange());
			if(enemy!=null)
				tryAttack(enemy.id());
			tryMove();
			enemy = enemyAtRange(unit.attackRange());
			if(enemy!=null)
				tryAttack(enemy.id());
		}
		}
		else{
			act();
		}
	}


}