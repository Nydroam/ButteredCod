import bc.*;
import java.util.HashMap;
public class RocketBot extends Bot{
	public RocketBot(Unit u, GameController gc, Logistics logs){
		super(u,gc, logs);
	}

	public void act(){
		if(unit.structureIsBuilt()!=0){
			VecUnit inRange = gc.senseNearbyUnitsByTeam(loc,1,gc.team());
			for(int i = 0; i < inRange.size(); i++){
				if(gc.canLoad(id,inRange.get(i).id()))
					gc.load(id,inRange.get(i).id());
			}
			PlanetMap pm = gc.startingMap(Planet.Mars);
			int x = (int)(Math.random()*pm.getWidth());
			int y = (int)(Math.random()*pm.getHeight());
			if(unit.structureGarrison().size()>0){
				while(!gc.canLaunchRocket(id,new MapLocation(Planet.Mars,x,y))){
					x = (int)(Math.random()*pm.getWidth());
					y = (int)(Math.random()*pm.getHeight());
				}
                gc.launchRocket(unit.id(),new MapLocation(Planet.Mars,x,y));
			}

		}
	}
}