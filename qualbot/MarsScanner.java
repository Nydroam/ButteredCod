import bc.*;

import java.io.File;
import java.io.FileNotFoundException;

import java.lang.Integer;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class MarsScanner{

    /* 
      in Player.java
      1. instantiate a new MarScanner object
      2. call loadVirtualMap()
      3. call scan()
      4. use landingSpot() or landRandom() to get a Rocket destination

     */

    final static int WALL = 999999997;
    final static int SPACE = 999999998;
    final static int PAINT = 999999999;

    private Random rand = new Random();
    
    private PlanetMap map;
    private Planet planet;
    private int width;
    private int height;
    private Cors[][] v_map; //storing MapLocations in v_map to reduce API calls
    
    //Player should pass a gc.startingMap(Planet.Mars)
    //and Planet.Mars (reduces API calls)
    public MarsScanner(PlanetMap map, Planet planet){
	this.map = map;
	this.planet = planet;
	this.width = (int)map.getWidth();
	this.height = (int)map.getHeight();
	
	v_map = new Cors[height][width]; // [y][x]
    }

    //fill the v_map with WALL or SPACE matching Mars
    public void loadVirtualMap(){
	int real_y = height - 1;
	for (int y = 0; y < height; y++){
	    for (int x = 0; x < width; x++){
		MapLocation loc = new MapLocation(planet, x, y);
		Cors cors = new Cors(loc);
		if (map.isPassableTerrainAt(loc) != 0){
		    cors.setTile(SPACE);
		}else{
		    cors.setTile(WALL);
		}
		
		v_map[real_y][x] = cors;
	    }
	    real_y--;
	}
	//-----------------------IGNORE-----------------------
	/*
	printMap();
	try{
	    Thread.sleep(100);
	    
	}catch(InterruptedException ex) {
	    Thread.currentThread().interrupt();
	}
	*/
	//====================================================
    }

    // returns a bunch of areas from largest to smallest
    public PriorityQueue<ArrayList<MapLocation>> scan(){
	
	Comparator<ArrayList<MapLocation>> comp = new AreaComparator();
	PriorityQueue<ArrayList<MapLocation>> areas = new PriorityQueue<ArrayList<MapLocation>>(10, comp);

	char area_no = (char)20;
	for (int y = 0; y < height; y++){
	    for (int x = 0; x < width; x++){
		
		if (v_map[height - 1 - y][x].getTile() == SPACE){
		    
		    ArrayList<MapLocation> temp = fill(x, y, areas, (int)area_no);
		    areas.offer(temp);
		    area_no++;
		}
	    }
	}

	//-------------------IGNORE------------------------
	
	/*printAllAreas();
	System.out.println("XDDDDDDDDDDDDDDDDDDDDDDDDDDD");

	try{
	    Thread.sleep(3000);
	    
	}catch(InterruptedException ex) {
	    Thread.currentThread().interrupt();
	}*/
	
	//=================================================
	return areas;
    }

    // returns the average max_x, min_x and max_y, min_y
    // if the spot is impassable, call landRandom()
    public MapLocation landingSpot(ArrayList<MapLocation> area){

	int smallest_y = 50;
	int smallest_x = 50;
	int biggest_y = 0;
	int biggest_x = 0;
	
	for (int i = 0; i < area.size(); i++){
	    MapLocation temp = area.get(i);
	    if (temp.getY() < smallest_y){
		smallest_y = temp.getY();
	    }
	    if (temp.getY() > biggest_y){
		biggest_y = temp.getY();
	    }
	    if (temp.getX() < smallest_x){
		smallest_x = temp.getX();
	    }
	    if (temp.getX() > biggest_x){
		biggest_x = temp.getX();
	    }
	}

	int avg_x = (biggest_x + smallest_x) / 2;
	int avg_y = (biggest_y + smallest_y) / 2;

	if (v_map[height - 1 - avg_y][avg_x].getTile() == WALL){

	    return landRandom(area);
	}
	return v_map[height - 1 - avg_y][avg_x].getLoc();
	
    }

    // returns a random MapLocation given an area
    public MapLocation landRandom(ArrayList<MapLocation> area){
	return area.get(rand.nextInt(area.size()));
	
    }
    
    //Identifies a good area to land on, represented as ArrayList<MapLocation>
    private ArrayList<MapLocation> fill(int x_start, int y_start, PriorityQueue<ArrayList<MapLocation>> areas, int paint){

	
	LinkedList<MapLocation> frontier = new LinkedList<MapLocation>(); //stores locations to be visited
	LinkedList<Integer> growths = new LinkedList<Integer>(); //stores how much each area grew
	ArrayList<MapLocation> area = new ArrayList<MapLocation>(); //stores locations visited

	frontier.add(v_map[height - 1 - y_start][x_start].getLoc());

	int strikes = 0; //keeps track of constant small growths, Ex: 1 x 8 area
	while(!frontier.isEmpty()){ //while there are no tiles to be visited

	    int curr_size = frontier.size(); //the current growth rate
	    
	    //breaks if:
	    //    current growthrate is less than the prev/1.5
	    //    the area grew twice by 1 or 2 tiles
	    if (!growths.isEmpty() && (double)curr_size < growths.getLast()/1.5 ||
		strikes == 2){
		break;
	    }
	    if (curr_size == 1 || curr_size == 2){
		strikes++;
	    }

	    growths.add(curr_size);

	    for (int j = 0; j < curr_size; j++){
		// loop that marks visited
		MapLocation curr = frontier.poll(); 
		area.add(curr);
		v_map[height - 1 - curr.getY()][curr.getX()].setTile(paint);

		// adjacent locations
		MapLocation[] adj = {
		    curr.add(Direction.East),
		    curr.add(Direction.North),
		    curr.add(Direction.Northeast),
		    curr.add(Direction.Northwest),
		    curr.add(Direction.South),
		    curr.add(Direction.Southeast),
		    curr.add(Direction.Southwest),
		    curr.add(Direction.West)
		    
		};
		
		for (int i = 0; i < 8; i++){
		    // loop that marks next locations to be visited
		    MapLocation temp = adj[i];

		    if (inBounds(temp.getX(), temp.getY()) &&
			!contains(frontier, temp) &&
			numPassableAdjacent(temp) > 4 &&
			v_map[height - 1 - temp.getY()][temp.getX()].getTile() == SPACE){

			frontier.add(v_map[height - 1 - temp.getY()][temp.getX()].getLoc());
		    }   
		}
	    }
	    //printAllAreas();
	  

	    
	}
	return area;
    }

    private boolean inBounds(int x, int y){

	return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    private boolean contains(LinkedList<MapLocation> l, MapLocation target){
	for (MapLocation loc: l){
	    if (loc.getX() == target.getX() &&
		loc.getY() == target.getY()){

		return true;
	    }
	    
	}
	return false;
	
    }

    private int numPassableAdjacent(MapLocation loc){

	// adjacent locations
	MapLocation[] adj = {
	    loc.add(Direction.East),
	    loc.add(Direction.North),
	    loc.add(Direction.Northeast),
	    loc.add(Direction.Northwest),
	    loc.add(Direction.South),
	    loc.add(Direction.Southeast),
	    loc.add(Direction.Southwest),
	    loc.add(Direction.West)
	    
	};
	int counter = 0;
	for (MapLocation l: adj){
	    if (inBounds(l.getX(), l.getY()) &&
		map.isPassableTerrainAt(l) != 0){
		counter++;
	    }
	}
	return counter;
    }

    private void printMap(){
	
	String s = "";
	for (int y = 0; y < height; y++){
	    for (int x = 0; x < width; x++){
		String filler = "#";
		if (map.isPassableTerrainAt(v_map[y][x].getLoc()) != 0){
		    filler = " ";
		}
		
		s += filler + " ";
	    }
	    s += "\n";
	}
	System.out.println(s);
    }
    
    private void printAllAreas(){
	String s = "";
	for (int y = 0; y < height; y++){
	    for (int x = 0; x < width; x++){
		int temp = v_map[height - 1 - y][x].getTile();
		
		String filler = "" + (char)temp;
		
		if (temp == WALL){
		    filler = "#";
		}
		if (temp == PAINT){
		    filler = ".";
		}
		if (temp == SPACE){
		    filler = " ";
		}
		
		s += filler + " ";
	    }
	    s += "\n";
	}
	System.out.println(s);
    }
}
