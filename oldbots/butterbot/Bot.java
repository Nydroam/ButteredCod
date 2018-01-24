import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
public class Bot{
	
	protected GameController gc;
	protected Logistics logs;
	protected Unit unit;
	protected int id;
	protected MapLocation loc;
	protected MapLocation dest;
	protected HashMap<Integer,MapLocation> targets;
	protected HashMap<Integer,Integer> rockets;
	protected LinkedList<String> rallyPoints;
	protected Team enemyTeam;
	protected Direction d;

	public Bot(Unit u, GameController gc, Logistics logs){
		this.gc = gc;
		this.logs = logs;
		unit = u;
		id = u.id();
		loc = u.location().mapLocation();
		targets = logs.targets();
		rockets = logs.rockets();
		rallyPoints = logs.rallyPoints();
		enemyTeam = Team.Red;
		if(gc.team()==Team.Red)
            enemyTeam = Team.Blue;
	}

	public void act(){}
	public void act2(){}
	public void actMars(){}

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
	public Unit unitAtRange(long radius, Team whichTeam){return null;}
	public Unit enemyAtRange(long radius){
		
        VecUnit vec = gc.senseNearbyUnitsByTeam(loc,radius,enemyTeam);

        if(vec.size()>0){

        	Unit enemy = null;
        	Unit chosen = null;
        	int priority = 0;
        	long hp = 500;
        	for(int i = 0; i < vec.size(); i++){
        		Unit u = vec.get(i);
        		UnitType ut = u.unitType();
        		int uid = u.id();
        		MapLocation uloc = u.location().mapLocation();

        		if(gc.canAttack(id,uid)||loc.distanceSquaredTo(uloc)>10) {
        			enemy = u;
        			long ehp = u.health();
        			if(ut==UnitType.Mage||ut==UnitType.Healer){
        				if(priority<4){
	        				chosen = enemy;
	        				priority = 4;
	        				hp = ehp;
	        			}else if(enemy.health()<hp){
	        				hp = ehp;
	        				chosen = enemy;
	        			}
        			}
	        		else if(ut==UnitType.Ranger){
	        			if(priority<3){
	        				chosen = enemy;
	        				priority = 3;
	        				hp = ehp;
	        			}else if(ehp<hp){
	        				hp = ehp;
	        				chosen = enemy;
	        			}
	        		}else{
	        			if(priority <1){
	        				if(ehp<hp){
	        					chosen = enemy;
	        					hp = ehp;
	        				}
	        			}
	        		}

        		}
        	}
        	return chosen;
        }
        return null;
	}
}