import bc.*;

public class Player{
	public static void main(String [] args){
		//Game Initialization
		GameController gc = new GameController();
		PlanetMap pm = gc.startingMap(gc.planet());

		//MapData Setup
		MapData mapData = new MapData(gc,pm);
		mapData.updateMap();
		mapData.printMap();
		System.out.println(mapData.karbonite());

		if(gc.planet()==Planet.Earth){//Earth Code
			while(true){
				gc.nextTurn();
			}
		}else{//Mars Code
			while(true){
				gc.nextTurn();
			}
		}
	}
}