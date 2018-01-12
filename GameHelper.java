import bc.*;

public class GameHelper {

    private GameController gc;

    public GameHelper(GameController gc){
	loadGameController(gc);
    }
    
    public void loadGameController(GameController gc){
	this.gc = gc;
    }
    
    public boolean move(Unit unit, Direction dir){
	if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), dir)){
	    gc.moveRobot(unit.id(), dir);
	    return true;
	}
	else{
	    return false;
	}
    }

    public boolean harvest(Unit unit, Direction dir){
	if (gc.canHarvest(unit.id(), dir)){
	    gc.harvest(unit.id(), dir);
	    return true;
	}
	else{
	    return false;
	}
    }
    
    public boolean blueprint(Unit unit, UnitType type, Direction dir){
	if (gc.canBlueprint(unit.id(), type, dir)){
	    gc.blueprint(unit.id(), type, dir);
	    return true;
	}
	else{
	    return false;
	}
    }      
}
