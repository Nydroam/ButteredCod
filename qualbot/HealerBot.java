import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class HealerBot extends Bot{
	public HealerBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area){
		super(gc,pm,unit,logs,area);
	}

	public void act(){
		tryAttack();
		VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,60,logs.enemyTeam());
		
		if(enemies.size()==0){
			tryMove();
			tryAttack();
		}
	}

	public void tryAttack(){
		if(gc.isHealReady(id)){
			VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,unit.attackRange(),gc.team());
			for(int i = 0; i < enemies.size(); i++){
				Unit enemy = enemies.get(i);
				if(gc.canHeal(id,enemy.id())&&enemy.health()<enemy.maxHealth()){
					gc.heal(id,enemy.id());
					break;
				}
			}
		}
	}
}