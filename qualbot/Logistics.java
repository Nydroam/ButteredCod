import bc.*;
import java.util.HashMap;
public class Logistics{
	
	//Game Variables
	private GameController gc;
	private PlanetMap pm;
	private Team enemyTeam;
	//Statistics
	private HashMap<String,Integer> unitCount;

	//Worker Pathing/Targeting
	private HashMap<Integer,HashMap<String,Direction>> workerRally;
	private HashMap<Integer,String> workerTargets;

	public Logistics(GameController gc){
		this.gc = gc;
		pm = gc.startingMap(gc.planet());
		workerRally = new HashMap<Integer,HashMap<String,Direction>>();
		workerTargets = new HashMap<Integer,String>();
		updateUnits();
		enemyTeam = Team.Red;
		if(gc.team()==Team.Red)
			enemyTeam = Team.Blue;
	}

	//accessors
	public HashMap<Integer,HashMap<String,Direction>> workerRally() { return workerRally; }
	public HashMap<Integer,String> workerTargets() { return workerTargets; }
	public HashMap<String,Integer> unitCount() { return unitCount; }
	public Team enemyTeam() { return enemyTeam; }
	//mutators
	public void initUnitCount(){
		unitCount = new HashMap<String,Integer>();
		unitCount.put("Worker",0);
		unitCount.put("Ranger",0);
		unitCount.put("Knight",0);
		unitCount.put("Mage",0);
		unitCount.put("Healer",0);
		unitCount.put("Factory",0);
		unitCount.put("Rocket",0);
	}
	public void updateUnits(){
		initUnitCount();
		VecUnit units = gc.myUnits();
		for(int i = 0; i < units.size(); i++){
			Unit u = units.get(i);
			String type = u.unitType().toString();
			unitCount.put(type,unitCount.get(type)+1);
		}
	}
}