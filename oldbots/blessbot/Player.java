import bc.*;
import java.util.HashMap;
public class Player{
	public static void main(String[] args){

		//Game Initialization
		
		GameController gc = new GameController();
		PlanetMap pm = gc.startingMap(gc.planet());

		//Variable Initialization
		Logistics logs = new Logistics(gc,pm);
		VecUnit units=logs.units();
		//Enemy Team
		
		
		if(gc.planet()==Planet.Earth){
			
			//pre-set research
			gc.queueResearch(UnitType.Worker);
			gc.queueResearch(UnitType.Healer);

			gc.queueResearch(UnitType.Rocket);
			gc.queueResearch(UnitType.Ranger);
			
			gc.queueResearch(UnitType.Ranger);
			gc.queueResearch(UnitType.Healer);
			gc.queueResearch(UnitType.Worker);
			gc.queueResearch(UnitType.Worker);
			gc.queueResearch(UnitType.Ranger);
			
			//bot to be set up later
			Bot bot = null;

			while(true){
				logs.updateUnits();
				units = logs.units();
				System.out.println("Round: "+ gc.round());
				System.out.println("Stats: " + logs.statistics());
				System.out.println("Time Left: " + gc.getTimeLeftMs()/1000.0 + "seconds");
				
				for(int i = 0; i < units.size(); i++){
					
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
			
				gc.nextTurn();

			}
		}
		else{
			while(true){
				logs.updateUnits();
				units = logs.units();

				System.out.println("Round: "+ gc.round());
				System.out.println("Stats: " + logs.statistics());
				System.out.println("Time Left: " + gc.getTimeLeftMs()/1000.0 + "seconds");

				
				for(int i = 0; i < units.size(); i++){
					units = logs.units();
					Unit u = units.get(i);
					if(u.location().isInGarrison()){
						continue;
					}
					MapLocation loc = u.location().mapLocation();
					if(u.unitType()==UnitType.Rocket){
						
						while(u.structureGarrison().size()>0){
							Direction d = Fuzzy.findAdjacent(loc,gc);
							if(gc.canUnload(u.id(),d))
								gc.unload(u.id(),d);
							else{break;}
						}
					}
					if(u.unitType()==UnitType.Ranger){
						RangerBot bot = new RangerBot(u,gc,logs);
						bot.act();
					}
					if(u.unitType()==UnitType.Worker){

						
						Direction[] dirs = Direction.values();

						Direction d = dirs[(int)(Math.random()*dirs.length)];
						if(gc.karbonite()>15&&gc.canReplicate(u.id(),d)&&(gc.karbonite()>100||gc.round()>749)) {//try replicating then moving
							gc.replicate(u.id(),d);
					
						//d = Fuzzy.findAdjacent(u.location().mapLocation(),gc);
							
						}else{

							d = Direction.Center;
							if(gc.canHarvest(u.id(),d)){//if you can harvest, don't move
								gc.harvest(u.id(),d);
							}else{
							d = dirs[(int)(Math.random()*dirs.length)];
								//d = Fuzzy.findAdjacent(u.location().mapLocation(),gc);
								if(gc.isMoveReady(u.id())&&gc.canMove(u.id(),d))
									gc.moveRobot(u.id(),d);

								d = Direction.Center;
								if(gc.canHarvest(u.id(),d))
									gc.harvest(u.id(),d);
							}
						}
					}
				}

				
				gc.nextTurn();
			}
		}
	}
}