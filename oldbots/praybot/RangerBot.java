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
		Team enemyTeam = Team.Red;
		if(gc.team()==Team.Red)
            enemyTeam = Team.Blue;
		if(logs.statistics().get("Rocket")>0){
			dest = null;
			VecUnit rockets = gc.senseNearbyUnitsByType(loc,25,UnitType.Rocket);
			for(int i = 0; i < rockets.size(); i++){
		
					Unit rocket = rockets.get(i);
					MapLocation rloc = rocket.location().mapLocation();
					if(rloc.distanceSquaredTo(loc)<25||gc.senseNearbyUnitsByTeam(loc,2500,enemyTeam).size()==0){
					
					dest = rloc;
					break;}

				
			}
		}
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

		if(gc.isMoveReady(id)&&gc.round()<300){
		Direction[] dirs = Direction.values();
		d = dirs[(int)(Math.random()*dirs.length)];
		if(gc.canMove(id,d))
			gc.moveRobot(id,d);
		}
	}


}