import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class RangerBot extends Bot{
	public RangerBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area){
		super(gc,pm,unit,logs,area);
	}

	public void act(){
		VecUnit enemyList = gc.senseNearbyUnitsByTeam(loc,unit.attackRange(),logs.enemyTeam());
		for(int i = 0; i < enemyList.size(); i++){
			Unit enemy = enemyList.get(i);
			if(gc.isAttackReady(id)&&gc.canAttack(id,enemy.id())){
				gc.attack(id,enemy.id());
				break;
			}
		}
		Direction d = Pathing.findAdjacent(loc,gc);
		if(gc.isMoveReady(id))
			tryMove(d);
	}
}