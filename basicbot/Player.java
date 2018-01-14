import bc.*;
import java.util.HashMap;
public class Player{
	public static void main(String[] args){

		//Game Initialization
		long start = System.currentTimeMillis();
		long time;
		GameController gc = new GameController();
		time = System.currentTimeMillis()-start;
        System.out.println("After gc: "+time/1000.0 + "s");
		PlanetMap pm = gc.startingMap(gc.planet());

		//Variable Initialization
		Logistics logs = new Logistics(gc,pm);
		VecUnit units=logs.units();
		//Enemy Team
		
		
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Rocket);

		if(gc.planet()==Planet.Earth){
				
			Bot bot = null;
			while(true){
				start = System.currentTimeMillis();
				System.out.println("Round: " + gc.round());
				

				System.out.println(logs.targets());
				logs.updateUnits();
				System.out.println("Stats: " + logs.statistics());

				for(int i = 0; i < units.size(); i++){
					units = logs.units();
					Unit u = units.get(i);

					if(u.location().isInGarrison()){
						continue;
					}
					if(u.unitType() == UnitType.Factory){
						bot = new FactoryBot(u,gc,logs);
					}
					if(u.unitType() == UnitType.Ranger){
						bot = new RangerBot(u,gc,logs);
					}
					if(u.unitType() == UnitType.Knight){
						bot = new KnightBot(u,gc,logs);
					}
					if(u.unitType()==UnitType.Worker){
						bot = new WorkerBot(u,gc,logs);
					}
					if(u.unitType()==UnitType.Rocket){
						bot = new RocketBot(u,gc,logs);
					}
					bot.act();
					
				}
				
				time += (System.currentTimeMillis()-start);
				System.out.println(gc.round()+" : " +time/1000.0 +"seconds");
				gc.nextTurn();

			}
		}
		else{
			while(true){
				logs.updateUnits();
				units = logs.units();
				for(int i = 0; i < units.size(); i++){
					units = logs.units();
					Unit u = units.get(i);
					if(u.location().isInGarrison()){
						continue;
					}
					MapLocation loc = u.location().mapLocation();
					if(u.unitType()==UnitType.Rocket){
						
						System.out.println("WOOT MARS");
						while(u.structureGarrison().size()>0){
							Direction d = Fuzzy.findAdjacent(loc,gc);
							if(gc.canUnload(u.id(),d))
								gc.unload(u.id(),d);
							else{break;}
						}
					}
				}
				gc.nextTurn();
			}
		}
	}
}