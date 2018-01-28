import bc.*;
import java.util.Map;
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

	public int getPriorityTarget(){
    	Team ourTeam = gc.team();
    	VecUnit allies = gc.senseNearbyUnitsByTeam(loc, unit.attackRange(), ourTeam);
    	if (allies.size() > 0){
    	    HashMap<Unit, Long> priorities = new HashMap<Unit, Long>();
    	    long threshold = 2000;
    	    for (int i = 0; i < allies.size(); i++){
    		Unit ally = allies.get(i);
    		UnitType type = ally.unitType();
    		if (type == UnitType.Mage){
    		    priorities.put(ally, threshold + ally.maxHealth() - ally.health());
    		}

    		if (type == UnitType.Knight){
    		    if (ally.health() <= 90){
                    priorities.put(ally, threshold - 250 + ally.maxHealth() - ally.health());
    		    }else{
                    priorities.put(ally, threshold - 600 + ally.maxHealth() - ally.health());
    		    }
    		}

    		if (type == UnitType.Healer){
    		    priorities.put(ally, threshold - 350 + ally.maxHealth() - ally.health());
    		}

    		if (type == UnitType.Ranger){
    		    priorities.put(ally, threshold - 800 + ally.maxHealth() - ally.health());
    		}

    		if (type == UnitType.Factory){
    		    priorities.put(ally, threshold - 1100 + ally.maxHealth() - ally.health());
    		}

    		if (type == UnitType.Rocket){
    		    priorities.put(ally, threshold - 1300 + ally.maxHealth() - ally.health());
    		}

    		if (type == UnitType.Worker){
    		    priorities.put(ally, threshold - 1400 + ally.maxHealth() - ally.health());
    		}
    	    }

    	    int max = Integer.MIN_VALUE;
    	    Unit target = allies.get(0);
    	    for (Map.Entry<Unit, Long> entry : priorities.entrySet()) {
        		Unit ally = entry.getKey();
        		long value = entry.getValue();
        		if (value > max){
        		    max = (int)value;
        		    target = ally;
        		}
    	    }
    	    return target.id();
    	}
    	return -1;
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
