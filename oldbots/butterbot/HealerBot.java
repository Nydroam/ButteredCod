import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class HealerBot extends Bot{
	public HealerBot(Unit u, GameController gc, Logistics logs){
		super(u,gc, logs);
	}

	public Unit enemyAtRange(long radius){
	
        VecUnit vec = gc.senseNearbyUnitsByTeam(loc,radius,gc.team());
        if(vec.size()>0){

        	Unit enemy = null;
        	for(int i = 0; i < vec.size(); i++){
        		Unit u = vec.get(i);
        		if(u.unitType()!=UnitType.Factory&&u.health()<u.maxHealth()){//||loc.distanceSquaredTo(vec.get(i).location().mapLocation())>10){
        			enemy = vec.get(i);
	        		if(u.unitType()==UnitType.Ranger||u.unitType()==UnitType.Healer)
	        			break;
        		}
        	}
        	return enemy;
        }
        return null;
	}

	public void act(){
		Unit enemy = enemyAtRange(2500);
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

	public boolean tryAttack(int enemy){
		if(gc.isHealReady(id)&&gc.canHeal(id,enemy)){
			gc.heal(id,enemy);
			return true;
		}
		return false;
	}

}