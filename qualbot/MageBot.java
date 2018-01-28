import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class MageBot extends Bot{
	public MageBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area){
		super(gc,pm,unit,logs,area);
	}

	public void act(){
		tryAttack();
		tryMove();
		tryAttack();
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
}