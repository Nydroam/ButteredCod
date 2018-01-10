// import the API.
// See xxx for the javadocs.
import bc.*;

public class Player {

    public static Direction randDirection(){
        Direction d = Direction.North;
        int r = (int)(Math.random()*8);
        if(r == 0)
            d = Direction.North;
        else if(r == 1)
            d = Direction.South;
        else if(r == 2)
            d = Direction.East;
        else if(r == 3)
            d = Direction.West;
        else if(r == 4)
            d = Direction.Northeast;
        else if(r == 5)
            d = Direction.Northwest;
        else if(r == 6)
            d = Direction.Southeast;
        else if(r == 7)
            d = Direction.Southwest;
        return d;
    }
    public static void main(String[] args) {
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northeast));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        GameController gc = new GameController();

        // Direction is a normal java enum.
        Direction[] directions = Direction.values();

        /*for(int i = 0; i < gc.startingMap(gc.planet()).getWidth(); i++)
            for(int j = 0; j < gc.startingMap(gc.planet()).getHeight(); j++){
                System.out.println(i + ", " + j + " has " +
                Long.toString(gc.startingMap(gc.planet()).initialKarboniteAt(new MapLocation(gc.planet(), i, j))) + " karbonite");
                System.out.println("Passable Terrain: " + Long.toString(gc.startingMap(gc.planet()).isPassableTerrainAt(new MapLocation(gc.planet(),i,j))));
            }*/
            //System.out.println(UnitType.Factory);
        while (true) {
            System.out.println("Current round: "+gc.round());
            System.out.println(gc.karbonite());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();

            
            

            for (int i = 0; i < units.size(); i++) {
                Direction d = randDirection();
                Unit unit = units.get(i);
                if(gc.karbonite()>bc.bcUnitTypeReplicateCost(UnitType.Worker)&&gc.canReplicate(unit.id(),d))
                    gc.replicate(unit.id(),d);
                if(gc.karboniteAt(unit.location().mapLocation())>0&&gc.canHarvest(unit.id(),d))
                    gc.harvest(unit.id(), Direction.Center);
                // Most methods on gc take unit IDs, instead of the unit objects themselves.
                if(gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), d))
                    gc.moveRobot(unit.id(),d);
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }


    }
}