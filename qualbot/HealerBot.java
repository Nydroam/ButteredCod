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

	// public int getPriorityTarget(){
    // 	Team ourTeam = gc.team();
    // 	VecUnit enemies = gc.senseNearbyUnitsByTeam(loc, unit.attackRange(), enemyTeam);
    // 	if (enemies.size() > 0){
    // 	    HashMap<Unit, Long> priorities = new HashMap<Unit, Long>();
    // 	    long threshold = 2000;
    // 	    for (int i = 0; i < enemies.size(); i++){
    // 		Unit enemy = enemies.get(i);
    // 		UnitType type = enemy.unitType();
    // 		if (type == UnitType.Mage){
    // 		    priorities.put(enemy, threshold + enemy.maxHealth() - enemy.health());
    // 		}
    //
    // 		if (type == UnitType.Knight){
    // 		    if (enemy.health() <= 90){
    //                 priorities.put(enemy, threshold - 250 + enemy.maxHealth() - enemy.health());
    // 		    }else{
    //                 priorities.put(enemy, threshold - 600 + enemy.maxHealth() - enemy.health());
    // 		    }
    // 		}
    //
    // 		if (type == UnitType.Healer){
    // 		    priorities.put(enemy, threshold - 350 + enemy.maxHealth() - enemy.health());
    // 		}
    //
    // 		if (type == UnitType.Ranger){
    // 		    priorities.put(enemy, threshold - 800 + enemy.maxHealth() - enemy.health());
    // 		}
    //
    // 		if (type == UnitType.Factory){
    // 		    priorities.put(enemy, threshold - 1100 + enemy.maxHealth() - enemy.health());
    // 		}
    //
    // 		if (type == UnitType.Rocket){
    // 		    priorities.put(enemy, threshold - 1300 + enemy.maxHealth() - enemy.health());
    // 		}
    //
    // 		if (type == UnitType.Worker){
    // 		    priorities.put(enemy, threshold - 1400 + enemy.maxHealth() - enemy.health());
    // 		}
    // 	    }
    //
    // 	    int max = Integer.MIN_VALUE;
    // 	    Unit target = enemies.get(0);
    // 	    for (Map.Entry<Unit, Long> entry : priorities.entrySet()) {
    //     		Unit enemy = entry.getKey();
    //     		long value = entry.getValue();
    //     		if (value > max){
    //     		    max = (int)value;
    //     		    target = enemy;
    //     		}
    // 	    }
    // 	    return target.id();
    // 	}
    // 	return -1;
    // }


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
