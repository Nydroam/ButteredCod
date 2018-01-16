import bc.*;
import java.util.HashMap;
public class RangerBot extends Bot{

		private HashMap<String,Direction> paths;
	public RangerBot(Unit u, GameController gc, Logistics logs, HashMap<String,Direction> paths){

		super(u,gc, logs);
		this.paths = paths;
	}

	public boolean testMove(){
		if(gc.isMoveReady(id)&&paths!=null){
			Direction d = paths.get(loc.toString());
			for(int i = 0; i < Fuzzy.rotateOrder.length; i++){
				Direction newd = Fuzzy.tryRotate(d,Fuzzy.rotateOrder[i]);
				if(gc.canMove(id,newd)){
					gc.moveRobot(id,newd);
					loc = loc.add(newd);
					return true;
				}
			}
		}
		return false;
	}

	public void act(){
		
		Unit enemy = enemyAtRange(unit.attackRange());
		if(enemy!=null){
			if(tryAttack(enemy.id()))
				return;
		}

		Team enemyTeam = Team.Red;
		if(gc.team()==Team.Red)
            enemyTeam = Team.Blue;

		VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,70,enemyTeam);
		VecUnit allies = gc.senseNearbyUnitsByType(loc,3,UnitType.Ranger);
		if(enemies.size()<allies.size()*2)
			testMove();
		enemy = enemyAtRange(unit.attackRange());
		if(enemy!=null)
			tryAttack(enemy.id());
	}

	public void actMars(){
		Unit enemy = enemyAtRange(unit.attackRange());
		if(enemy!=null)
			tryAttack(enemy.id());
	}
	/*public void act(){

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
	}*/

	public void act2(){
		if(logs.statistics().get("Rocket")>0){
			dest = null;
			for(int i: rockets.keySet()){
				if(gc.canSenseUnit(i)){
					Unit rocket = gc.unit(i);
					MapLocation rloc = rocket.location().mapLocation();
					if(rloc.distanceSquaredTo(loc)<36||logs.rallyPoints().size()==0){
					targets.put(id,rloc);
					dest = targets.get(id);
					break;}

				}
			}

			
			
			/*VecUnit vec = gc.senseNearbyUnitsByType(loc,500,UnitType.Rocket);
			for(int i = 0; i < vec.size(); i++){
				Unit u = vec.get(i);
				if(u.team()==gc.team()){//on our team
					targets.put(id,u.location().mapLocation());
					dest = targets.get(id);
					break;
				}
			*/
		
		
			if(dest!=null){//attack move attack
				Unit enemy = enemyAtRange(unit.attackRange());
				if(enemy!=null)
					tryAttack(enemy.id());
				tryMove();
				enemy = enemyAtRange(unit.attackRange());
				if(enemy!=null)
					tryAttack(enemy.id());
			}else{
				act();
			}
		}
		else{
			act();
		}
	}


}