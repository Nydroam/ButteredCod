import bc.*;

public class Cors{

    private MapLocation loc;
    private int x;
    private int y;
    private int tile;

    public Cors(MapLocation loc){
	this.loc = loc;
	x = loc.getX();
	y = loc.getY();
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
