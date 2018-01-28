import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class KnightBot extends Bot{
	public KnightBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area){
		super(gc,pm,unit,logs,area);
	}

	public void act(){
		tryAbility();
		tryAttack();
		tryMove();
		tryAttack();
		tryAbility();
	}

	

	public void tryAttack(){
		
			/*VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,unit.attackRange(),logs.enemyTeam());
			for(int i = 0; i < enemies.size(); i++){
				Unit enemy = enemies.get(i);
				if(gc.canAttack(id,enemy.id())){
					gc.attack(id,enemy.id());
					break;
				}
			}*/
			if(gc.isAttackReady(id)){
			int enemyId = getPriorityTarget();
			if (enemyId > -1){
				gc.attack(id, enemyId);
			}
			}
		
	}

	public void tryAbility(){
		if(unit.isAbilityUnlocked()!=0&&gc.isJavelinReady(id)){
	
			int enemyId = getPriorityTarget();
			if (enemyId > -1){
				gc.javelin(id, enemyId);
			}
		
		}
	}
}