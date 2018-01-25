import bc.*;

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

        	//Queue Research Here(?)

        	//------------------Map Preprocessing---------------------

        	/**
        	 * we loop through all our units at the beginning of the game
        	 * and use their locations to set up areas in MapScanner by
        	 * using fillArea(loc). We add the unit to the corresponding
        	 * MapData created or the existing MapData.
        	 */
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
        	//scan.areas().stream().forEach(m->System.out.println("Area: " + m.unitList()));
        	scan.printMaps();

        	//--------------End Map Preprocessing---------------------
        	while(true){//each iteration of the loop is a round

        		//initialization of variables that change each round
        		long round = gc.round();
        		int timeLeft = gc.getTimeLeftMs();

        		//printing statements for debugging
        		System.out.println("Round: "+ round);
				System.out.println("Time Left: " + timeLeft + " ms");

				//------------------UNIT CODE-----------------------



				//--------------END UNIT CODE ----------------------
        		
        		if(round%10==0){//garbage cleanup
					System.runFinalization();
					System.gc();
				}
        		gc.nextTurn();

        	}
        }else{//Mars Code

        	while(true){//each iteration of the loop is a round

        		if(gc.round()%10==0){//garbage cleanup
					System.runFinalization();
					System.gc();
				}
        		gc.nextTurn();

        	}
        }
	}
}