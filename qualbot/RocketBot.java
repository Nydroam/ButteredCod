import bc.*;
public class RocketBot extends Bot{

	MissionControl mc;
	public RocketBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area ,MissionControl mc){
		super(gc,pm,unit,logs,area);
		this.mc = mc;
	}

	public void act(){
		if(unit.structureIsBuilt()!=0){
			if(area.blueprints().containsKey(id))
				area.blueprints().remove(id);
			if(!area.rallyPoints().contains(loc.toJson()))
				area.rallyPoints().add(loc.toJson());

			VecUnit inRange = gc.senseNearbyUnitsByTeam(loc,2,gc.team());
			for(int i = 0; i < inRange.size(); i++){
				Unit u = inRange.get(i);
				if(gc.canLoad(id,u.id())){
					gc.load(id,u.id());
					if(u.unitType()==UnitType.Worker){
						logs.workerTargets().remove(u.id());
					}
				}
			}
			if(unit.structureGarrison().size()>6||gc.round()==749||(unit.health()<unit.maxHealth()&&unit.health()!=100)){
				MapLocation landingLoc = null;
				while(landingLoc == null){
					landingLoc = mc.getLocation();
				}
				if(gc.canLaunchRocket(id,landingLoc))
					gc.launchRocket(id,landingLoc);
			}
		}
		
	}

	public void actMars(){
		Direction d = Pathing.findAdjacent(loc,gc);
		while(gc.canUnload(id,d)){
			gc.unload(id,d);	
			d = Pathing.findAdjacent(loc,gc);	
		}
	}
}