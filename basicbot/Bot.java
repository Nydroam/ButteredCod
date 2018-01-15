import bc.*;
import java.util.HashMap;
public class Bot{
	
	protected GameController gc;
	protected Logistics logs;
	protected Unit unit;
	protected int id;
	protected MapLocation loc;
	protected MapLocation dest;
	protected HashMap<Integer,MapLocation> targets;
	protected Direction d;

	public Bot(Unit u, GameController gc, Logistics logs){
		this.gc = gc;
		this.logs = logs;
		unit = u;
		id = u.id();
		loc = u.location().mapLocation();
		targets = logs.targets();
	}

	public void act(){}
	public void act2(){}

	public boolean tryMove(){
		if(gc.isMoveReady(id)&&dest!=null){
			d = loc.directionTo(dest);
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

	public boolean tryAttack(int enemy){
		if(gc.isAttackReady(id)&&gc.canAttack(id,enemy)){
			gc.attack(id,enemy);
			return true;
		}
		return false;
	}
	public Unit enemyAtRange(long radius){
		Team enemyTeam = Team.Red;
		if(gc.team()==Team.Red)
            enemyTeam = Team.Blue;
        VecUnit vec = gc.senseNearbyUnitsByTeam(loc,radius,enemyTeam);
        if(vec.size()>0){

        	Unit enemy = null;
        	for(int i = 0; i < vec.size(); i++){
        		UnitType ut = vec.get(i).unitType();
        		if(gc.canAttack(id,vec.get(i).id())){
        			enemy = vec.get(i);
	        		if(ut==UnitType.Ranger||ut==UnitType.Mage)
	        			break;
        		}
        	}
        	return enemy;
        }
        return null;
	}
}