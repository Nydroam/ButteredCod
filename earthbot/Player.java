import bc.*;
import java.util.HashMap;
public class Player {
	public static void main(String[] args){

		// Connect to the manager, starting the game
        GameController gc = new GameController();

        //Scouting the map
        PlanetMap pm = gc.startingMap(gc.planet());



        //for(int x = 0; x < pm.getWidth(); x++)
        	//for(int y = 0; y < pm.getHeight(); y++)
        		//System.out.println(pm.isPassableTerrainAt(new MapLocation(gc.planet(),x,y)));
      	
      	//unit list to be constantly updated
      	VecUnit units = gc.myUnits();

      	int workerCount = 0;
      	for(int i = 0; i < units.size(); i++)
      		workerCount++;

      	if(gc.planet()==Planet.Earth){//=======================EARTH============================================

      		BFS workerRally = new BFS(gc);
      		HashMap<String,Direction> paths;
      		

	      	while (true) {

	      		units = gc.myUnits();
	      		System.out.println("Round: " + gc.round());
	      		MapLocation dest = new MapLocation(gc.planet(),10,10);
	      		
	      		paths = workerRally.search(dest,null);
	      		//workerRally.printMap();
	      		for(int i = 0; i < units.size(); i++){
	      				
	      			Unit unit = units.get(i);
	      			
	      			if(unit.unitType() == UnitType.Worker){//worker AI
	      				Direction d = PathFinder.findAdjacent(unit.location().mapLocation(),gc,pm);
	      				if(workerCount < 10){//Earlygame
	      					
	      					if(gc.canReplicate(unit.id(),d)&&gc.karbonite()>bc.bcUnitTypeReplicateCost(UnitType.Worker)){
	      						gc.replicate(unit.id(),d);
	      						workerCount++;
	      					}else{
	      						//if(gc.isMoveReady(unit.id()) && gc.canMove(unit.id(),d))
	      							//gc.moveRobot(unit.id(),paths.get(dest));
	      					}
	      				}else{
	      					//System.out.println(unit.location().mapLocation().toString());
	      					if(gc.isMoveReady(unit.id()) && paths.keySet().contains(unit.location().mapLocation().toString()) && gc.canMove(unit.id(),paths.get(unit.location().mapLocation().toString()))){
	      						gc.moveRobot(unit.id(),paths.get(unit.location().mapLocation().toString()));
	      						
	      					}
	      				}
	      			}
	      		}
	      		
	      		//end turn
	      		gc.nextTurn();

	      	}
	    }

      	else{//===============================================MARS=============================================

      		while (true) {

	      		gc.nextTurn();

	      	}

      	}

	}
}