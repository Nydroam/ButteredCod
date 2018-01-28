import bc.*;
public class Researcher{

	private GameController gc;
	private Logistics logs;
	private MapScanner scan;
	private ResearchInfo ri;

	public Researcher(GameController gc, Logistics logs){
		this.gc = gc;
		this.logs = logs;
		this.scan = scan;
		this.ri = gc.researchInfo();
	}

	public void queueResearch(){
		gc.queueResearch(UnitType.Worker);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Ranger);
	}
}