import bc.*;
import java.util.HashMap;
public class RocketBot extends Bot{
	public RocketBot(Unit u, GameController gc, Logistics logs){
		super(u,gc, logs);
	}

	public void act(){
		HashMap<Integer, Integer> bps = logs.blueprints();
		if(unit.structureIsBuilt()!=0){
			if(bps.size()>0&&bps.keySet().contains(id)){//check the blueprint hashset for this blueprint and remove it
      			bps.remove(id);
      		}
			VecUnit inRange = gc.senseNearbyUnitsByTeam(loc,2,gc.team());
			for(int i = 0; i < inRange.size(); i++){
				Unit u = inRange.get(i);
				if(gc.canLoad(id,u.id())){
					gc.load(id,u.id());
					if(u.unitType()==UnitType.Worker){

						targets.remove(u.id());
					}
				}
			}
			PlanetMap pm = gc.startingMap(Planet.Mars);
			int x = (int)(Math.random()*pm.getWidth());
			int y = (int)(Math.random()*pm.getHeight());
			Team enemyTeam = Team.Red;
			if(gc.team()==Team.Red)
				enemyTeam = Team.Blue;
			VecUnit enemies = gc.senseNearbyUnitsByTeam(loc,80,enemyTeam);

			if(unit.structureGarrison().size()>4||enemies.size()>3||gc.round()==749){
				while(!gc.canLaunchRocket(id,new MapLocation(Planet.Mars,x,y))){
					x = (int)(Math.random()*pm.getWidth());
					y = (int)(Math.random()*pm.getHeight());
				}
                gc.launchRocket(unit.id(),new MapLocation(Planet.Mars,x,y));
                logs.resetTargets();
			}

		}
	}
}