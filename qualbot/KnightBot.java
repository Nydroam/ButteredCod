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
		if(gc.isAttackReady(id)){
			VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,unit.attackRange(),logs.enemyTeam());
			for(int i = 0; i < enemies.size(); i++){
				Unit enemy = enemies.get(i);
				if(gc.canAttack(id,enemy.id())){
					gc.attack(id,enemy.id());
					break;
				}
			}
		}
	}

	public void tryAbility(){
		if(unit.isAbilityUnlocked()!=0&&gc.isJavelinReady(id)){
			VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,unit.abilityRange(),logs.enemyTeam());
			for(int i = 0; i < enemies.size(); i++){
				Unit enemy = enemies.get(i);
				if(gc.canJavelin(id,enemy.id())){
					gc.javelin(id,enemy.id());
					break;
				}
			}
		}
	}
}