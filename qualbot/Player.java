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