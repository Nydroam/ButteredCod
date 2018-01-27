import bc.*;
public class AuxMapLocation extends MapLocation{
	private int priority;
	public AuxMapLocation(MapLocation l, int priority){
		super(l.getPlanet(),l.getX(),l.getY());
		this.priority = priority;
	}
	public int priority(){
		return priority;
	}
}