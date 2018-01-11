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

        boolean bp = false;
        int bpid = 0;
        boolean built = false;
        boolean rbp = false;
        int rbpid = 0;
        boolean rbuilt = false;
        boolean launched = false;

        int workerCount = 1;

        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);

        Team enemy = Team.Red;
        if(gc.team()==Team.Red)
            enemy = Team.Blue;
        while (true) {
            
            long round = gc.round();
           // if(round%10==0){
                System.out.println("Round: " + round);
                System.out.println(gc.rocketLandings());
           // }
                if(gc.planet()==Planet.Earth){
                    System.out.println("Working");
                // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
           
            VecUnit units = gc.myUnits();
            



                for (int i = 0; i < units.size(); i++) {
                    Direction d = randDirection();
                    Unit unit = units.get(i);

                        //if the unit is a worker
                    if(unit.unitType() == UnitType.Worker){

                        if(!bp){
                            if(gc.karbonite()>bc.bcUnitTypeBlueprintCost(UnitType.Factory)&&gc.canBlueprint(unit.id(),UnitType.Factory,d)){
                                System.out.println("FACTORY PLACED");
                                gc.blueprint(unit.id(),UnitType.Factory,d);
                                bp = true;

                            }
                        }
                        else{
                            if(workerCount<20&&gc.karbonite()>bc.bcUnitTypeReplicateCost(UnitType.Worker)&&gc.canReplicate(unit.id(),d)){
                                gc.replicate(unit.id(),d);
                                workerCount++;
                            }
                        }
                        if(rbuilt){
                            if(!launched)
                                if(gc.canLoad(rbpid,unit.id()))
                                    gc.load(rbpid,unit.id());
                        }else if (rbp){
                            if(gc.canBuild(unit.id(),rbpid)){
                                gc.build(unit.id(),rbpid);
                            }
                        } else{
                            if(gc.karbonite()>bc.bcUnitTypeBlueprintCost(UnitType.Rocket)&&gc.canBlueprint(unit.id(),UnitType.Rocket,d)){
                                gc.blueprint(unit.id(),UnitType.Rocket,d);
                                rbp = true;
                                System.out.println("ROCKET BLUEPRINTED!!");
                            }
                            


                            if(built){
                                if(gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), d))
                                    gc.moveRobot(unit.id(),d);
                            }else if (bp){
                                if(gc.canBuild(unit.id(),bpid))
                                    gc.build(unit.id(),bpid);
                                
                            }

                        }
                        if(gc.canHarvest(unit.id(), Direction.Center))
                            gc.harvest(unit.id(), Direction.Center);
                            // Most methods on gc take unit IDs, instead of the unit objects themselves.
                    }

                        //if the unit is a factory
                    if(unit.unitType() == UnitType.Factory){
                        if(!built){
                            if(unit.structureIsBuilt()==0)
                                bpid = unit.id();
                            else
                               built = true;
                       }else{
                        if(gc.canProduceRobot(unit.id(),UnitType.Knight))
                            gc.produceRobot(unit.id(),UnitType.Knight);
                        if(gc.canUnload(unit.id(),d))
                            gc.unload(unit.id(),d);
                    }
                }
                if(unit.unitType() == UnitType.Rocket){
                    if(!rbuilt){

                        if(unit.structureIsBuilt()==0){
                            rbpid = unit.id();
                        }
                        else{
                            System.out.println("ROCKET BUILT");
                            rbuilt = true;
                        }
                    }else{
                        PlanetMap pm = gc.startingMap(Planet.Mars);
                        int x = (int)(Math.random()*pm.getWidth());
                        int y = (int)(Math.random()*pm.getHeight());
                        if(unit.structureGarrison().size()>0){
                            while(!gc.canLaunchRocket(unit.id(),new MapLocation(Planet.Mars,x,y))){
                                x = (int)(Math.random()*pm.getWidth());
                                y = (int)(Math.random()*pm.getHeight());}
                                System.out.println("ROCKET LAUNCHED!");
                                System.out.println("Before Launch: "+gc.myUnits().size());
                                //gc.launchRocket(unit.id(),new MapLocation(Planet.Mars,x,y));
                                System.out.println("After Launch: "+gc.myUnits().size());
                                launched = true;
                            }
                        }
                    }

                    if(unit.unitType() == UnitType.Knight){
                        if(unit.location().isInGarrison()){}
                            else{
                                
                                if(gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), d))
                                    gc.moveRobot(unit.id(),d);


                                VecUnit u = gc.senseNearbyUnitsByTeam( unit.location().mapLocation(), 1, enemy);
                                if(u.size()>0)
                                    if(gc.isAttackReady(unit.id())&&gc.canAttack(unit.id(),u.get(0).id())){
                                        gc.attack(unit.id(),u.get(0).id());

                                    }
                                }
                            }
                        }
                    }
                    else{
                        /*for (int i = 0; i < units.size(); i++) {
                            Direction d = randDirection();
                            Unit unit = units.get(i);
                            if(unit.unitType()==UnitType.Worker){
                                if(unit.location().isInGarrison()){}
                                    else{
                                if(gc.karbonite()>bc.bcUnitTypeReplicateCost(UnitType.Worker)&&gc.canReplicate(unit.id(),d))
                                    gc.replicate(unit.id(),d);
                                if(gc.canHarvest(unit.id(), Direction.Center))
                                    gc.harvest(unit.id(), Direction.Center);
                                if(gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), d))
                                    gc.moveRobot(unit.id(),d);
                            }
                            }
                            if(unit.unitType()==UnitType.Rocket){
                                System.out.println("ROCKET LANDED");
                                if(gc.canUnload(unit.id(),d))
                                    gc.unload(unit.id(),d);
                            }
                        }*/
                    }

            // Submit the actions we've done, and wait for our next turn.
                    System.out.println("Ending Turn");
                    gc.nextTurn();
                }


            }
        }