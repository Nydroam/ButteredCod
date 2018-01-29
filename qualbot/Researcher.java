import bc.*;
public class Researcher{

	private GameController gc;
	private Logistics logs;
	private MapScanner scan;
	private ResearchInfo ri;
	private boolean rush;

	public Researcher(GameController gc, Logistics logs, boolean rush){
		this.gc = gc;
		this.logs = logs;
		this.scan = scan;
		this.ri = gc.researchInfo();
	}

	public void queueResearch(){

		if(rush){
		gc.queueResearch(UnitType.Worker);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Healer);
		gc.queueResearch(UnitType.Healer);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Ranger);
		}
		else{
			gc.queueResearch(UnitType.Worker);
			gc.queueResearch(UnitType.Ranger);
			gc.queueResearch(UnitType.Healer);
			gc.queueResearch(UnitType.Healer);
			gc.queueResearch(UnitType.Rocket);
			gc.queueResearch(UnitType.Knight);
			gc.queueResearch(UnitType.Knight);
			gc.queueResearch(UnitType.Knight);
			gc.queueResearch(UnitType.Ranger);
		}
	}
}