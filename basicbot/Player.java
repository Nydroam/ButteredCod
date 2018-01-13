import bc.*;
import java.util.HashMap;
public class Player{
	public static void main(String[] args){

		//Game Initialization
		GameController gc = new GameController();
		PlanetMap pm = gc.startingMap(gc.planet());

		//Variable Initialization
		Logistics logs = new Logistics(gc,pm);
		VecUnit units=logs.units();;
		//Enemy Team
		Team enemyTeam = Team.Red;
		if(gc.team()==Team.Red)
            enemyTeam = Team.Blue;

        
		if(gc.planet()==Planet.Earth){

			WorkerBot bot = null;
			while(true){
				
				System.out.println("Round: " + gc.round());
				System.out.println("Stats: " + logs.statistics());

				//System.out.println(logs.targets());
				logs.updateUnits();

				for(int i = 0; i < units.size(); i++){
					units = logs.units();
					Unit u = units.get(i);

					if(u.unitType() == UnitType.Factory){
						HashMap<Integer, Integer> bps = logs.blueprints();
						if(u.structureIsBuilt()!=0){//if structure is finished
      						if(bps.size()>0&&bps.keySet().contains(u.id())){//check the blueprint hashset for this blueprint and remove it
      							bps.remove(u.id());
      						}
      					}
					}	
					if(u.unitType()==UnitType.Worker){
						bot = new WorkerBot(u,gc,logs);
						bot.act();
					}
				}
				gc.nextTurn();
			}
		}
		else{
			while(true){
				gc.nextTurn();
			}
		}
	}
}