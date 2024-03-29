import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class RangerBot extends Bot{

		private HashMap<String,Direction> paths;
	public RangerBot(Unit u, GameController gc, Logistics logs, HashMap<String,Direction> paths){

		super(u,gc, logs);
		this.paths = paths;
	}

	public boolean testMove(boolean opposite){
		if(gc.isMoveReady(id)&&paths!=null&&paths.keySet().contains(loc.toString())){
			Direction d = paths.get(loc.toString());
			if(opposite)
				d = bc.bcDirectionOpposite(d);
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
		long attackHeat = unit.attackHeat();
		long moveHeat = unit.movementHeat();


		Unit enemy = null;
		if(attackHeat<10){
			enemy = enemyAtRange(unit.attackRange());
			
			if(enemy!=null){
				if(tryAttack(enemy.id())){
					attackHeat = unit.attackHeat();
				}
			}
		}


		//VecUnit enem = gc.senseNearbyUnitsByTeam(loc,100,enemyTeam);
		/*ArrayList<Unit> enemies = new ArrayList<Unit>();
		for(int i = 0; i < enem.size(); i++)
			if(enem.get(i).unitType()==UnitType.Ranger)
				enemies.add(enem.get(i));
		VecUnit all = gc.senseNearbyUnitsByTeam(loc,25,gc.team());
		ArrayList<Unit> allies = new ArrayList<Unit>();
		for(int i = 0; i < all.size(); i++)
			if(all.get(i).unitType()==UnitType.Ranger)
				allies.add(all.get(i));*/
		//VecUnit allies = gc.senseNearbyUnitsByTeam(loc,25,gc.team());
		//VecUnit close = gc.senseNearbyUnitsByTeam(loc,2,enemyTeam);
		//if(close.size()>0||attackHeat>=10){
			//testMove(true);
		//}
		//else if(0<allies.size())
			//testMove(false);
		//else if(gc.senseNearbyUnitsByType(loc,9,UnitType.Factory).size()>0){}
		//else if(unit.health()<unit.maxHealth())
		//	testMove(true);
		if(moveHeat<10){
			if(attackHeat>=10)
				testMove(true);
			else
				testMove(false);
			
		}
		if(attackHeat<10){
			enemy = enemyAtRange(unit.attackRange());
			if(enemy!=null){

				if(tryAttack(enemy.id())){}
					
			}
		}

		if(gc.isMoveReady(id)){
			if(gc.planet()==Planet.Mars){
				if (gc.senseNearbyUnitsByTeam(loc,2500,enemyTeam).size()>0||gc.round()<700){
					Direction d = Fuzzy.findAdjacent(loc,gc);
			if(gc.canMove(id,d))
				gc.moveRobot(id,d);
			}
				
			}else if (gc.getTimeLeftMs()>5000&&gc.senseNearbyUnitsByTeam(loc,2500,enemyTeam).size()==0) {
			Direction d = Fuzzy.findAdjacent(loc,gc);
			if(gc.canMove(id,d))
				gc.moveRobot(id,d);
			}
		}
	}

	public void actMars(){
		Unit enemy = enemyAtRange(unit.attackRange());
		if(enemy!=null)
			tryAttack(enemy.id());
	}
	

	public void act2(){
		if(logs.statistics().get("Rocket")>0){
			dest = null;
			for(int i: rockets.keySet()){
				if(gc.canSenseUnit(i)){
					Unit rocket = gc.unit(i);
					MapLocation rloc = rocket.location().mapLocation();
					if(rloc.distanceSquaredTo(loc)<50||logs.rallyPoints().size()==0){
					targets.put(id,rloc);
					dest = targets.get(id);
					break;}

				}
			}

		
		
			if(dest!=null){//attack move attack
				Unit enemy = enemyAtRange(unit.attackRange());
				if(enemy!=null)
					tryAttack(enemy.id());
				if(!loc.isAdjacentTo(dest))
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