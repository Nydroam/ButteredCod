import bc.*;
import java.util.ArrayList;

public class Player{
	public static void main(String[] args){

		//General Variable Setup
		GameController gc = new GameController();
		Planet planet = gc.planet();
		PlanetMap pm = gc.startingMap(planet);

		Team ourTeam = gc.team();
		Team enemyTeam = Team.Red;
		if(ourTeam==Team.Red)
            enemyTeam = Team.Blue;

        if(planet == Planet.Earth){//Earth Code

        	
        	

        	//gc.queueResearch(UnitType.Rocket);
        	//------------------Map Preprocessing---------------------

        	/**
        	 * we loop through all our units at the beginning of the game
        	 * and use their locations to set up areas in MapScanner by
        	 * using fillArea(loc). We add the unit to the corresponding
        	 * MapData created or the existing MapData.
        	 */ 
        	MissionControl mc = new MissionControl(gc);
        	MapScanner scan = new MapScanner(gc);
        	VecUnit units = gc.myUnits();
        	for(int i = 0; i < units.size(); i++){
        		Unit u = units.get(i);
	        	MapLocation loc = u.location().mapLocation();
	        	int index = scan.findArea(loc);
	        	int id = u.id();
	        	if(index == -1){
	        		scan.fillArea(loc).addUnit(id);
	        	}else{
	        		scan.areas().get(index).addUnit(id);
	        	}
        	}
        	VecUnit enemyStarting= pm.getInitial_units();
        	//System.out.println(enemyStarting);
        	for(int i = 0; i < enemyStarting.size(); i++){
        		Unit u = enemyStarting.get(i);
        		if(u.team()==enemyTeam){
        			MapLocation loc = u.location().mapLocation();
        			int index = scan.findArea(loc);
        			if(index != -1){
        				scan.areas().get(index).rallyPoints().add(loc.toJson());
        			}
        		}
        	}
        	//scan.printMaps();

        	scan.areas().stream().forEach( area -> {
					area.updateRallyPoints();
					//System.out.println("RallyPoints: " + area.rallyPoints());
				});
        	Strategy strats = new Strategy(gc,scan);
        	strats.detectRush();

        	//--------------End Map Preprocessing------------------------
        	
        	//--------------Logistics Preprocessing----------------------
        	/* We use our data collected from the map to set up logistics
        	 * such as rally points for workers at karbonite locations
        	 * and for military units at enemy locations
        	 */
        	Logistics logs = new Logistics(gc);
        	//----------End Logistics Preprocessing----------------------------
        	//Queue Research Here(?)
        	Researcher  research= new Researcher(gc,logs,strats.rush());
        	//System.out.println("RUSHING?????: "+strats.rush());
        	while(true){//each iteration of the loop is a round
        		research.queueResearch();
        		//initialization of variables that change each round
        		long round = gc.round();
        		int timeLeft = gc.getTimeLeftMs();
        		long time = System.currentTimeMillis();
        		//printing statements for debugging
        		//System.out.println("Round: "+ round);
				//System.out.println("Time Left: " + timeLeft + " ms");

				//--------RALLY POINT SETUP-------------------------
				
				scan.areas().stream().forEach( area -> {
					area.updateRallyPoints();
					//System.out.println("RallyPoints: " + area.rallyPoints());
				});
				
				//--------RALLY POINT END----------------------------

				//------------------UNIT CODE-----------------------

				try{//failsafe
					units = gc.myUnits();
					//loop through our units, parsing by type
					time = System.currentTimeMillis();
					logs.updateUnits();
					//System.out.println("Update Time: " + (System.currentTimeMillis()-time));
					//System.out.println("PreAct");
					for(int i = 0; i < units.size(); i++){
						Unit u = units.get(i);
						UnitType type = u.unitType();
						int id = u.id();
						if(u.location().isInGarrison()){
							int sid = u.location().structure();
							MapData area = scan.areas().get(scan.findArea(sid));
							if(!area.contains(id))
								area.unitList().add(id);
							continue;
						}
						MapLocation loc = u.location().mapLocation();
						
						MapData area = scan.areas().get(scan.findArea(loc));
						//System.out.println("Scan time " + (System.currentTimeMillis()-time));
						//superclass bot to be specialized later
						Bot bot = null;
						if(type == UnitType.Worker){
							bot = new WorkerBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Factory){
							bot = new FactoryBot(gc,pm,u,logs,area,strats.rush());
						}
						if(type == UnitType.Ranger){
							bot = new RangerBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Knight){
							bot = new KnightBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Mage){
							bot = new MageBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Healer){
							bot = new HealerBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Rocket){
							bot = new RocketBot(gc,pm,u,logs,area,mc);
						}
						time = System.currentTimeMillis();
						
						bot.act();
						

						//System.out.println("Act Time: " + (System.currentTimeMillis()-time));
					}
					//System.out.println("PostAct");
				} catch(Exception e){
					System.out.println("Exception Occurred: " + e.getMessage());
					System.out.println(e.getStackTrace()[0]);
				}

				//--------------END UNIT CODE ----------------------
        		
        		if(round%10==0){//garbage cleanup
					System.runFinalization();
					System.gc();
				}
        		gc.nextTurn();

        	}
        }else{//Mars Code
        	MapScanner scan = new MapScanner(gc);
        	Logistics logs = new Logistics(gc);
        	AsteroidPattern ap = gc.asteroidPattern();
        	while(true){//each iteration of the loop is a round
        		//initialization of variables that change each round
        		long round = gc.round();
        		int timeLeft = gc.getTimeLeftMs();
        		if(timeLeft<500){
					gc.nextTurn();
					continue;
				}
        		long time = System.currentTimeMillis();
        		//printing statements for debugging
        		System.out.println("Round: "+ round);
				System.out.println("Time Left: " + timeLeft + " ms");


				scan.areas().stream().forEach( area -> {
					area.updateRallyPoints();
					if(ap.hasAsteroid(round)){
						area.locations().stream().forEach( aloc -> {
							MapLocation aLoc = bc.bcMapLocationFromJson(aloc);
							if(gc.canSenseLocation(aLoc)&&!area.karbQueue().contains(aloc)&&area.contains(aLoc)&&gc.karboniteAt(aLoc)>0)
								area.karbQueue().push(aLoc);
						});
						//System.out.println("striking~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						AsteroidStrike ast = ap.asteroid(round);
						MapLocation astLoc = ast.getLocation();
						if(area.contains(astLoc)&&!area.karbQueue().contains(astLoc.toJson()))
							area.karbQueue().push(astLoc);
					}
					//System.out.println("RallyPoints: " + area.rallyPoints());
				});
				try{//failsafe
					VecUnit units = gc.myUnits();
					//loop through our units, parsing by type
					time = System.currentTimeMillis();
					logs.updateUnits();
					//System.out.println("Update Time: " + (System.currentTimeMillis()-time));
					//System.out.println("PreAct");
					for(int i = 0; i < units.size(); i++){
						Unit u = units.get(i);
						UnitType type = u.unitType();
						int id = u.id();
						if(u.location().isInGarrison()){
							continue;
						}
						MapLocation loc = u.location().mapLocation();

						int index = scan.findArea(loc);
						
						if(index == -1){
							scan.fillArea(loc);
						}
						
						MapData area = scan.areas().get(scan.findArea(loc));
						//System.out.println("Scan time " + (System.currentTimeMillis()-time));
						//superclass bot to be specialized later
						Bot bot = null;
						if(type == UnitType.Worker){
							bot = new WorkerBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Ranger){
							bot = new RangerBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Knight){
							bot = new KnightBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Mage){
							bot = new MageBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Healer){
							bot = new HealerBot(gc,pm,u,logs,area);
						}
						if(type == UnitType.Rocket){
							bot = new RocketBot(gc,pm,u,logs,area,null);
						}
						time = System.currentTimeMillis();
						
						bot.actMars();
						

						//System.out.println("Act Time: " + (System.currentTimeMillis()-time));
					}
					//System.out.println("PostAct");
				} catch(Exception e){
					System.out.println("Exception Occurred: " + e.getMessage());
					System.out.println(e.getStackTrace()[0]);
				}

				//--------------END UNIT CODE ----------------------

        		if(gc.round()%10==0){//garbage cleanup
					System.runFinalization();
					System.gc();
				}
        		gc.nextTurn();

        	}
        }
	}
}