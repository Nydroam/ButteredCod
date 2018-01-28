import bc.*;
import java.util.HashMap;
import java.util.ArrayList;
public class RangerBot extends Bot{
	public RangerBot(GameController gc, PlanetMap pm, Unit unit, Logistics logs, MapData area){
		super(gc,pm,unit,logs,area);
	}

	public void act(){
		tryMove();
	}

	public void tryMove(){
		if(gc.isMoveReady(id)){
			ArrayList<Direction> dirs = area.getDirections(unit);
			for(Direction d : dirs)
				if(gc.canMove(id,d)){
					gc.moveRobot(id,d);
					break;
				}
		}
	}
}