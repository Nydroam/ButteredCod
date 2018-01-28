import bc.*;

public class Cors{

    private MapLocation loc;
    private int x;
    private int y;
    private int tile;
    public int pathingPriority;

    public Cors(MapLocation loc){
	this.loc = loc;
	x = loc.getX();
	y = loc.getY();
    pathingPriority = -1;
    }

    public int getX(){
	return x;
    }

    public int getY(){
	return y;
    }
    
    public void setTile(int type){
	tile = type;
    }
    
    public int getTile(){
	return tile;
    }

    public MapLocation getLoc(){
	return loc;
    }
    
}
