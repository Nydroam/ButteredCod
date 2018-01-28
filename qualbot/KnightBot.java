import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class KnightBot extends Bot{
	public KnightBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area){
		super(gc,pm,unit,logs,area);
	}

	public void act(){
		tryAttack();
		tryMove();
		tryAttack();
	}

	public void tryMove(){
		if(gc.isMoveReady(id)){
			ArrayList<Direction> dirs = area.getDirections(unit);
			for(Direction d : dirs)
				if(gc.canMove(id,d)){
					gc.moveRobot(id,d);
					break;
				}
		}
	}

	public void tryAttack(){
		if(gc.isAttackReady(id)){
			VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,2,logs.enemyTeam());
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