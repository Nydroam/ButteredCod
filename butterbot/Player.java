import bc.*;
import java.util.HashMap;
import java.util.LinkedList;
public class Player{
	public static void main(String[] args){

		//Game Initialization
		
		GameController gc = new GameController();
		PlanetMap pm = gc.startingMap(gc.planet());

		//Variable Initialization
		Logistics logs = new Logistics(gc,pm);
		VecUnit units=logs.units();
		//Enemy Team
		
		Team enemyTeam = Team.Red;
		if(gc.team()==Team.Red)
            enemyTeam = Team.Blue;
		
        LinkedList<String> rallyPoints = logs.rallyPoints();
		HashMap<String,Direction> paths = null;
		MapLocation loc1 = null;

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
				//System.out.println("Stats: " + logs.statistics());
				System.out.println("Time Left: " + gc.getTimeLeftMs()/1000.0 + "seconds");
				
				//removing empty visible rally points, leaving the most recent one if there aren't any left
				
				for(int i = 0; i < rallyPoints.size(); i++){
					MapLocation rLoc = bc.bcMapLocationFromJson(rallyPoints.peek());
					if(gc.canSenseLocation(rLoc)&&gc.senseNearbyUnitsByTeam(rLoc,0,enemyTeam).size()==0)
						rallyPoints.poll();
					else
						rallyPoints.offer(rallyPoints.poll());

				}

				//setting rally points to enemy factories whenever possible
				MapLocation centerLoc = new MapLocation(gc.planet(),(int)pm.getWidth()/2,(int)pm.getHeight()/2);
				VecUnit enemyVec = gc.senseNearbyUnitsByTeam(centerLoc,pm.getWidth()*pm.getHeight(),enemyTeam);
				for(int i = 0; i < enemyVec.size(); i++){
					Unit enemy = enemyVec.get(i);
					MapLocation eloc = enemy.location().mapLocation();
					if(!rallyPoints.contains(eloc.toJson())){
						if(enemy.unitType()==UnitType.Factory)
							rallyPoints.offer(eloc.toJson());
						else if(rallyPoints.size()==0)
							rallyPoints.offer(eloc.toJson());
					}
				}
				if(rallyPoints.size()>0&&gc.round()%100==0){
					rallyPoints.offer(rallyPoints.poll());
				}
				if(rallyPoints.size()>0&&gc.round()%10==0&&logs.statistics().get("Ranger")>0){

					BFS testPath = new BFS(gc);
					paths = testPath.fullSearch(bc.bcMapLocationFromJson(rallyPoints.peek()));
					loc1 = bc.bcMapLocationFromJson(rallyPoints.peek());
					
					//testPath.printMap();
				}
				

				for(int i = 0; i < units.size(); i++){
					
					Unit u = units.get(i);

					if(u.location().isInGarrison()){
						continue;
					}
					MapLocation loc = u.location().mapLocation();
					if(u.unitType() == UnitType.Factory){
						bot = new FactoryBot(u,gc,logs);
						bot.act();
					}
					if(u.unitType() == UnitType.Ranger){
					
							bot = new RangerBot(u,gc,logs,paths);
						if(gc.round()<100)
							bot.act();
						else
							bot.act2();
					}
					if(u.unitType() == UnitType.Knight){
						bot = new KnightBot(u,gc,logs);
						bot.act();
					}
					if(u.unitType()==UnitType.Worker){
						bot = new WorkerBot(u,gc,logs);
						bot.act();
					}
					if(u.unitType()==UnitType.Healer){
						bot = new HealerBot(u,gc,logs);
						bot.act();
					}
					if(u.unitType()==UnitType.Rocket){
						bot = new RocketBot(u,gc,logs);
						bot.act();
					}
					
					
				}
			
				gc.nextTurn();

			}
		}
		else{
			while(true){
				logs.updateUnits();
				units = logs.units();

				System.out.println("Round: "+ gc.round());
				//System.out.println("Stats: " + logs.statistics());
				System.out.println("Time Left: " + gc.getTimeLeftMs()/1000.0 + "seconds");


				//removing empty visible rally points, leaving the most recent one if there aren't any left
				
				for(int i = 0; i < rallyPoints.size(); i++){
					MapLocation rLoc = bc.bcMapLocationFromJson(rallyPoints.peek());
					if(gc.canSenseLocation(rLoc)&&gc.senseNearbyUnitsByTeam(rLoc,0,enemyTeam).size()==0)
						rallyPoints.poll();
					else
						rallyPoints.offer(rallyPoints.poll());

				}

				//setting rally points to enemy factories whenever possible
				MapLocation centerLoc = new MapLocation(gc.planet(),(int)pm.getWidth()/2,(int)pm.getHeight()/2);
				VecUnit enemyVec = gc.senseNearbyUnitsByTeam(centerLoc,pm.getWidth()*pm.getHeight(),enemyTeam);
				for(int i = 0; i < enemyVec.size(); i++){
					Unit enemy = enemyVec.get(i);
					MapLocation eloc = enemy.location().mapLocation();
					if(!rallyPoints.contains(eloc.toJson())){
						if(enemy.unitType()==UnitType.Rocket||enemy.unitType()==UnitType.Ranger)
							rallyPoints.offer(eloc.toJson());
						else if(rallyPoints.size()==0)
							rallyPoints.offer(eloc.toJson());
					}
				}

				
				if(rallyPoints.size()>0&&gc.round()%10==0&&logs.statistics().get("Ranger")>0){

					BFS testPath = new BFS(gc);
					paths = testPath.fullSearch(bc.bcMapLocationFromJson(rallyPoints.peek()));
					//testPath.printMap();
				}

				
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
						RangerBot bot = new RangerBot(u,gc,logs,paths);

						bot.act();
					}
					if(u.unitType()==UnitType.Healer){
						HealerBot bot = new HealerBot(u,gc,logs);
						bot.act();
					}
					if(u.unitType()==UnitType.Worker){
						VecUnit surround = gc.senseNearbyUnits(u.location().mapLocation(),2);
						Direction[] dirs = Direction.values();
						Direction d = dirs[(int)(Math.random()*dirs.length)];
						if(gc.round()>749||gc.karbonite()>200){
						
						
						if(gc.karbonite()>15){//&&logs.statistics().get("Ranger")>logs.statistics().get("Worker")*3){
							for(int j = 0; j < dirs.length; j++){
								if(gc.canReplicate(u.id(),dirs[j])){
									d = dirs[j];
									if(Math.random()<0.15)
										break;
								}
							}
							if(gc.canReplicate(u.id(),d)){//try replicating then moving
								gc.replicate(u.id(),d);
							}
						}
						//d = Fuzzy.findAdjacent(u.location().mapLocation(),gc);
							
						}
						if(gc.karbonite()<300){
							if(gc.isMoveReady(u.id())&&gc.canMove(u.id(),d))
									gc.moveRobot(u.id(),d);
							
							
							for(int j = 0; j < dirs.length; j++){
								 d = dirs[j];
								if(gc.canHarvest(u.id(),d)){
									gc.harvest(u.id(),d);
								}
							}
							}
								
								
							
					}
					
				}

				
				gc.nextTurn();
			}
		}
	}
}