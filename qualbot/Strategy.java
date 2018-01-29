import bc.*;
import java.util.ArrayList;
public class Strategy{
	private GameController gc;
	private MapScanner scan;
	private boolean rush;


	//ArrayList of Areas (Any connected paths)
	private ArrayList<MapData> areas;
	public Strategy(GameController gc, MapScanner scan){
		this.gc = gc;
		this.scan = scan;
		this.areas = scan.areas();
	}

	public void detectRush(){
		rush = false;
		areas.stream().forEach(area -> {
			area.unitList().stream().forEach(u->{
				Unit unit = gc.unit(u);
				System.out.println(area.getSteps(unit));
				if(area.getSteps(unit)<25){
					rush = true;
					return;
				}
			});
		});
	}
	public boolean rush() {return rush;}
}