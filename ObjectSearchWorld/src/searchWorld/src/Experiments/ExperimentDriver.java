package searchWorld.src.Experiments;

import searchWorld.src.BeliefRoadMap.Graph;
import searchWorld.src.BeliefRoadMap.RRT;
import searchWorld.src.LanguageCommands.LanguageCommands;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.Pomcp.objectDistribution;
import searchWorld.src.SearchDriver;
import searchWorld.src.State.Rooms;
import searchWorld.src.State.Utilities;

import java.io.*;
import java.util.*;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by arthurwandzel on 11/14/17.
 */
public class ExperimentDriver {

  /*
  8/31
  TestRun Pomcp searchRoom 8 rasputin 1.0 .001 3 25 10000 150 true 1000 999 none uniform 2.0 1
  08/30
  OOOPOMDP TestRun Pomcp searchRoom 5 baxter 1.0 .001 1 25 10000 150 true 1000 999 none uniform 2.0 1
  RRT: debug 8 nodes : seed 0 (objects seed 1)

 Program End! --> 9/05
Time: 121.745
Reward: 3879.0583180952335
Actions: 93
Objects: 5

Program End! --> e movement reward -10, -1000, 1000
Time: 60.696
Reward: 4630.98009052403
Actions: 35
Objects: 5

Time: 148.042 --> OOOOPOMCP 10000
Reward: 3770.0
Actions: 96
Objects: 5

Program End! --> improved RRT (stochastic thus invalid)
Time: 35.86
Reward: 4610.0
Actions: 28
Objects: 5

Program End!
Time: 35.513 --> perfect epsilon model
Reward: 4620.0
Actions: 27
Objects: 5

Program End! --> perfect yoonseon model
Time: 37.474
Reward: 4630.0
Actions: 26
Objects: 5

08/31/18
Program End!
Time: 0.702 --> 10
Reward: -690.0
Actions: 53
Objects: 0

Program End!
Time: 1.736 --> 100 b = .05, y = .05, sd = .5
Reward: 2140.0
Actions: 53
Objects: 3

Time: 8.102 --> 1000 b = .05, y = .05, sd = .5
Reward: 2140.0
Actions: 53
Objects: 3

Time: 78.127 ---> 10000
Reward: 3270.0
Actions: 53
Objects: 4

Program End!
Time: 184.852 ---> 25000
Reward: 4490.0
Actions: 40
Objects: 5
   */

  //Parameters set in Burlap:
  //1). line 89 of TabularBeliefUpdate & line
  //2). line 150 of TabularBeliefUpdate: if(Math.abs(1 - sumP) > 1e-3)
  //  checks if observations add up to at least 1e-3instead of 1e-10

  //Bug fix line 552 and 580 of SparseSampling: corrects floating-point rounding errors
  //Bug fix line 361 of SparseSampling: enables forgetting each iteration

  public static void main(String[] args) throws InterruptedException {

    if (args[0].equals("help") || args[0].equals("-h")) {
      System.out.println(
              "Command line arguments must be in order and given as string literals.\n" +
                      "For example, solutionMethod is set as \"VI\" not -S \"VI\"\n" +
                      "Help prints this message and exits.\n\n" +
                      "H - \"help\" \"-h\"\n" +
                      "N - name of experiment\n" +
                      "S - solutionMethod {visualMDP, VI, BSS, POMCP}\n" +
                      "O- class-object membership {e.g. '3' = 1 class, 3 objects, '1,1,1' = 3 classes, 1 object each  \n" +
                      "SS - samples {-1-1000}\n" +
                      "MA - Max number of actions {0-5000}\n" +
                      "M - map {e.g. arthur, toy}\n" +
                      "A - observationAccuracy {.5-1.0}\n" +
                      "Sdv - standard deviation of event A in observation model\n" +
                      "V - visionDepth\n" +
                      "Ad - adversarial\n" +
                      "Ps - Psi language error\n" +
                      "Lc - class-room membership as referenced in language command  (e.g. 0,1,2 corresponding to '1,1,1' = 0 rooms for first Y, 1 room for second class, 3 rooms for last class \n" +
                      "I - experimentIterations {0-100}\n");
      System.exit(-1);
    }
    if (args.length != 13)
      throw new RuntimeException("Must set all parameters correctly. Type \"help\" or \"-h\" for more information");

    String fileName = args[0] + "__" + "S-" + args[1]
            + "_O-" + args[2] + "_SS-" + args[3] + "_MA-" + args[4] + "_M-" + args[5]
            + "_A-" + args[6] + "_Sdv-" + args[7] + "_V-" + args[8]
            + "_Ad-" + args[9] + "_Ps-" + args[10] + "_Lc-" + args[11]
            + "_I-" + args[12] + ".txt";
    //omit experimentIterations & maps for internal record keeping

    //TestRun Pomcp searchRoom 4 nine 1.0 1 25 10000 150 10 true 1000 999 none uniform 2.0 1
    /////////////////////////////EXPERIMENT PARAMETERS///////////////////////////////////////////////
    String experimentName = args[0];
    String solutionMethod = args[1];                          //{visualMDP, VI, BSS}
    String searchCommand = "searchRoom";                      //{searchWorld, searchRoom, searchCell}
    String objecsPerClass = args[2];                           //{'Y,1_C,3' 'Y,3' 'Y,1_C,4_C,5'}

    //Forward Search Tree parameters
    int height = 25;
    int numSamples = Integer.parseInt(args[3]);
    int maxNumberOfActions = Integer.parseInt(args[4]);

    String mapSelector = args[5];                             //{arthur, toy etc.}

    //BSS parameters
    boolean epsilonOFModel = false;
    double bssGamma = .95;

    //Observation Model parameters
    double observationAccuracy = Double.parseDouble(args[6]); //{.5-1.0}
    double standardDeviationForEventA = Double.parseDouble(args[7]);
    int visionDepth = Integer.parseInt(args[8]);

    double obsSD = standardDeviationForEventA;
    double betaV = (1.0 - observationAccuracy) / 2;
    double gammaV = (1.0 - observationAccuracy) / 2;
    double alphaV = observationAccuracy;
    double betaNV = (1.0 - observationAccuracy) / 2;
    double gammaNV = observationAccuracy;
    double alphaNV = (1.0 - observationAccuracy) / 2;

    //POMCP parameters
    boolean pomcpReinvigorate = true;
    int pomcpPartcleSamples = 1000;
    double pomcpExploration = 999;
    double pomcpDiscount = .95;

    //Language parameters
    boolean adversarial = Boolean.parseBoolean(args[9]);
    double psi = Double.parseDouble(args[10]);
    String roomsPerClass = args[11];

    int experimentIterations = Integer.parseInt(args[12]);                    //{0-100}

    //POMDP Reward Parameters
    double goalReward = 1000;
    double moveReward = -10;
    double pickReward = -1000;
    double moveRoomReward = -50; //abstract move reward
    double lookReward = 0;

    //////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////EXPERIMENT PARAMETERS///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    Location START_STATE = new Location(0, 0); //8,13 ROBOT EXPERIMENT CENTER
    //NOTE: invalid for A, B, C, D domains (starts in center of map)
    boolean manualMapSpecification = true; //set true if manually specifying goal location via map
    boolean ROBOTEXPERIMENT = false;

    //RRT
    long numberOfSecondsForRRTSamples = 0;
    if (numberOfSecondsForRRTSamples < 10)
      System.out.println("Warning: Insufficient Time for RRT for simulation experiments");

    //set true to print SearchDriver/SearchPomdpEnvironment/BeliefSearchAgent output
    int printAgentInteraction = 4; //{0:none, 1:env steps, 2:belief, 3:RRT, 4:else}
    //set true to launch a policy visualizer for BSS
    boolean issuePomdpVisualizer = true;
    //set true to evaluate route subroutines
    boolean printRouteMDPpaths = false;
    //sets random seed constant for goal placement & RRT nodes.
    boolean preprocessingConsistancy = true;
    //sets random seed constant for POMDP algorithm (sampling / action selection). Note: only for OOOOPOMCP right now
    boolean algorithmConsistancy = false;



    //No uncertianity POMCP for checking if agent can reach goals with RRT nodes
    boolean issueNoUncertainityPOMCP = false;
    //set true to replicate sparse sampling tree of BSS for debugging
    boolean debugBSS = false;

    if (psi > .5) throw new RuntimeException("Error: Psi must be a small value!");
    boolean domainWithRooms = false;
    if (mapSelector.equals("baxter") || mapSelector.equals("rasputin")
            || mapSelector.equals("arthur") || mapSelector.equals("toy")
            || mapSelector.equals("A") || mapSelector.equals("B")
            || mapSelector.equals("C") || mapSelector.equals("D")
            || mapSelector.equals("robot")) {
      domainWithRooms = true;
    }
    if (!domainWithRooms) throw new RuntimeException("Error: all other domains are depreciated");
    System.out.println(fileName);
    System.out.print(mapSelector + " ");

    //////////////////////////////EXPERIMENT///////////////////////////////////
    //
    //////////////////////////////EXPERIMENT///////////////////////////////////
    //----------------------------Set Randomness------------------------------------
    Random POMCPRn = (algorithmConsistancy) ? new Random(0) : new Random();
    Random observationRn = (algorithmConsistancy) ? new Random(0) : new Random();
    Random objDistraRn = (algorithmConsistancy) ? new Random(0) : new Random();

    Random objectRn = (preprocessingConsistancy) ? new Random(2) : new Random(); //5
    Random RmRn = (preprocessingConsistancy) ? new Random(1) : new Random(); //5

    Random RRTRn = new Random(0);
    //----------------------------Set Maps------------------------------------
    int[][] occupancyMap = WorldMaps.selectMap(mapSelector);
    int[][] beliefMap = new int[occupancyMap.length][occupancyMap[0].length];
    int[][] semanticMap = WorldMaps.selectRoomMap(mapSelector);
    //--------------------------Set Belief Uncertainty------------------------------------
    if (searchCommand.equals("searchRoom")) {
      int totalNumberOfUncertainCells = 0;
      System.out.print(" " + semanticMap[0].length + "," + semanticMap.length);
      for (int x = 0; x < semanticMap.length; x++) {
        for (int y = 0; y < semanticMap[0].length; y++) {
          if (semanticMap[x][y] > EMPTY && occupancyMap[x][y] != MAPCENTER) {
            totalNumberOfUncertainCells++;
            beliefMap[x][y] = UNCERTAIN;
          }
        }
      }
      System.out.print(" " + totalNumberOfUncertainCells + "\n");
    }
//--------------------------Parse ObjectsPerClass-----------------------------------
    int numberOfObjects = 0;

    List<Integer> classToObject = new ArrayList<>();
    String[] classes = objecsPerClass.split(",");
    for (int i = 0; i < classes.length; i++) {
      int objects = Integer.parseInt(classes[i]);
      numberOfObjects += objects;
      classToObject.add(objects);
    }

    //for simulation only
    List<Integer> classToRooms = new ArrayList<>();
    String[] rooms = roomsPerClass.split(",");
    for (int i = 0; i < rooms.length; i++) {
      classToRooms.add(Integer.parseInt(rooms[i]));
    }

    //All rooms to uniform b/c will be set by language
    if (ROBOTEXPERIMENT) {
      for (int i = 0; i < rooms.length; i++) classToRooms.set(i, 0);
      experimentIterations = 1;
    }

    //////////////////////////////ROOMS///////////////////////////////////
    //
    //////////////////////////////ROOMS///////////////////////////////////
    //candidate room maps include: baxter && rasputin
    Rooms roomAbstractions = new Rooms(mapSelector);
    if (domainWithRooms) {
      occupancyMap = roomAbstractions.setTransitionMatrix(false, occupancyMap, semanticMap);
      roomAbstractions.setRoomAdjacencyMatrix();
      roomAbstractions.setMappingFromAgentToRoom(semanticMap);
      roomAbstractions.setMappingFromRoomToCells(semanticMap);

      if (printAgentInteraction > 3) {
        for (int i = 0; i < roomAbstractions.totalRoomsInEnvironment; i++) {
          List<Integer> j = roomAbstractions.connectedRooms(i);
          System.out.println("\nRoom" + Integer.toString(i) + " center: " + roomAbstractions.transitionMatrix.get(i));
          System.out.print("Room" + Integer.toString(i) + " is connected to:");
          for (int k : j) {
            System.out.print(" " + k);
          }
        }
      }

      if (mapSelector.equals("A") || mapSelector.equals("B")
              || mapSelector.equals("C") || mapSelector.equals("D")) {
       START_STATE = roomAbstractions.transitionMatrix.get(roomAbstractions.totalRoomsInEnvironment);
      }
    }

    //////////////////////////////RRT///////////////////////////////////
    //
    //////////////////////////////RRT///////////////////////////////////
    Graph roadMap = new Graph();
    RRT rrt = new RRT(occupancyMap, beliefMap, RRTRn);
    Utilities util = new Utilities(occupancyMap);
    double RRTVisionDepth = (ROBOTEXPERIMENT) ? visionDepth-1  : visionDepth;
    if (domainWithRooms) {
      for (int i = 0; i < roomAbstractions.totalRoomsInEnvironment; i++) {

        List<Location> cellsInRoom = new ArrayList<>();
        if (ROBOTEXPERIMENT) {
          for (Location l : roomAbstractions.mappingFromRoomToCells.get(i)) {
              if (!util.checkIfConflict(l)) {
                cellsInRoom.add(l);
              }
          }
        } else {
          cellsInRoom = roomAbstractions.mappingFromRoomToCells.get(i);
        }

        Location roomCenter = roomAbstractions.transitionMatrix.get(i);
        roadMap = rrt.buildGraph(roadMap, roomCenter, RRTVisionDepth,
                numberOfSecondsForRRTSamples, printRouteMDPpaths, cellsInRoom, 8);
      }
      if (!roadMap.nodes.containsKey(START_STATE)) {
        roadMap = rrt.addEdge(roadMap, START_STATE);
      }
    }

    if (printAgentInteraction > 2) roadMap.printGraph(occupancyMap, START_STATE);

    //////////////////////////////EXPERIMENT DRIVER///////////////////////////////////
    //
    //////////////////////////////EXPERIMENT DRIVER///////////////////////////////////
    //Baxter 5 obj, 1 lookDepth, 5 experiments, 150 actions, 10RRT, randomseed 1
    Double averageTime = 0.0;
    Double currentBestTime = 0.0;
    Double averageReward = 0.0;
    Double currentBestReward = 0.0;
    PrintStream outputToConsole;
    for (int e = 0; e < experimentIterations; e++) {
      System.out.print("\n===============================================\n" +
              "-----------------Experiment:" + e + "------------------" +
              "\n===============================================\n");

      //////////////////////////////LANGUAGE COMMAND///////////////////////////////////
      //
      //////////////////////////////LANGUAGE COMMAND///////////////////////////////////
      objectDistribution objDistribution = new objectDistribution(beliefMap, numberOfObjects, objDistraRn, printAgentInteraction);
      Map<Integer, Map<Location, Double>> beliefPrior = objDistribution.makeUniformHeatMap();

      LanguageCommands lc = new LanguageCommands(psi, roomAbstractions, classToObject, classToRooms, false);
      lc.parseLanguageCommandSimulation(RmRn);
      lc.languageObservation();
      objDistribution.belief = lc.beliefUpdate(numberOfObjects, beliefPrior);

      if (adversarial) {
        LanguageCommands lcAnti = new LanguageCommands(1.0 - psi, roomAbstractions, classToObject, classToRooms, true);
        lcAnti.roomReferences = lc.roomReferences;
        lcAnti.languageObservation();
        objDistribution.objectDistribution = lcAnti.beliefUpdate(numberOfObjects, beliefPrior);
      } else {
        objDistribution.objectDistribution = new HashMap<>(objDistribution.belief);
      }

      if (printAgentInteraction > 1) {
        System.out.println("////////////////////BeliefDistribution////////////////////");
        objDistribution.printHeatMap(objDistribution.belief, false, null);
        System.out.println("\n////////////////////ObjectDistribution////////////////////");
        objDistribution.printHeatMap(objDistribution.objectDistribution, false, null);
      }
      //////////////////////////////OBJECT SELECTION///////////////////////////////////
      //
      //////////////////////////////OBJECT SELECTION///////////////////////////////////
      SortedMap<Integer, Location> GOALS = new TreeMap<>();
      if (!manualMapSpecification) {
        //random or deterministic selection of goals for testing algorithm
        for (int o = 0; o < numberOfObjects; o++) {
          double roll = objectRn.nextDouble();
          GOALS.put(o + NAME_OFFSET, objDistribution.sampleInitialObjectLocation(roll, o));
        }
        //GOALS = WorldMaps.generateRandomObjectLocations(numberOfObjects, mapSelector, START_STATE, searchCommand);
        occupancyMap = WorldMaps.updateSelectedMap(GOALS, occupancyMap);
      } else {
        //manually set goals
        for (int x = 0; x < occupancyMap.length; x++) {
          for (int y = 0; y < occupancyMap[0].length; y++) {
            if (occupancyMap[x][y] != EMPTY && occupancyMap[x][y] != WALL) {
              if (GOALS.containsKey(occupancyMap[x][y])) throw new RuntimeException("No duplicate objects allowed");
              GOALS.put(occupancyMap[x][y], new Location(x, y));
            }
          }
        }
        for (Integer g : GOALS.keySet())
          if (g > GOALS.keySet().size()) throw new RuntimeException("Objects must be sequentially specified");
        if (numberOfObjects != GOALS.size()) throw new RuntimeException("Number of objects must match what is on map");
      }

      if (issueNoUncertainityPOMCP) {
        if (e == 0) System.out.println("CERTAIN OOPOMCP IS BEING RUN");

        objDistribution.belief = objDistribution.makeUniformHeatMap();
        for (int o = 0; o < GOALS.size(); o++) {
          Location objectGoal = GOALS.get(o + NAME_OFFSET);
          for (int x = 0; x < semanticMap.length; x++) {
            for (int y = 0; y < semanticMap[0].length; y++) {
              Location l = new Location(x, y);
              if (objectGoal.x == x && objectGoal.y == y) {
                objDistribution.belief.get(o).put(l, 1.0);
              } else {
                objDistribution.belief.get(o).put(l, 0.0);
              }
            }
          }
        }
      }

      Data d = SearchDriver
              .executeSearchWorld(objDistribution, occupancyMap, roomAbstractions, roadMap, solutionMethod,
                      goalReward, moveReward, pickReward, moveRoomReward, lookReward,
                      START_STATE, GOALS, POMCPRn, observationRn, ROBOTEXPERIMENT, psi,
                      observationAccuracy, visionDepth, epsilonOFModel, bssGamma,
                      height, numSamples, maxNumberOfActions,
                      pomcpExploration, pomcpDiscount,
                      pomcpReinvigorate, pomcpPartcleSamples,
                      obsSD, betaV, gammaV, alphaV, betaNV, gammaNV, alphaNV,
                      roomAbstractions, classToObject,
                      printAgentInteraction, issuePomdpVisualizer, debugBSS);

      double reward = 0.0;
      for (Double r : d.episode.rewardSequence) reward += r;
      int actions = d.episode.actionSequence.size();
      double time = d.timeInSeconds;
      int objects = d.numberOfObjectsFound;

      //////////////////////////////OUTPUT///////////////////////////////////
      //
      //////////////////////////////OUTPUT///////////////////////////////////
      //Output file format
      //Iteration # Time
      //Iteration # Reward
      //Iteration # Action

      outputToConsole = System.out;
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      PrintStream outputToFile = new PrintStream(stream);
      System.setOut(outputToFile);

      String directoryName = "out/" + experimentName;
      File directory = new File(directoryName);
      if (!directory.exists()) directory.mkdirs();

      //write to file
      try {
        File f = new File(directoryName + "/" + "results.txt");

        //new file created
        //if (f.createNewFile()) {
        if (e == 0) {
          System.out.println(fileName);
          System.out.println("=========================================");
        }

        System.out.println(e + " Time: " + time);
        System.out.println(e + " Reward: " + reward);
        System.out.println(e + " Action: " + actions);
        System.out.println(e + " Objects: " + objects);
        for (Double r : d.episode.rewardSequence) System.out.print(".");
        System.out.print("\n");

        FileWriter fw = new FileWriter(f, true);
        fw.write(stream.toString());
        fw.close();
      } catch (IOException exception) {
        System.out.println("Exception Occurred:");
        exception.printStackTrace();
      } finally {
        outputToFile.close();
      }

      System.setOut(outputToConsole);
      System.gc();

      averageReward += reward;
      averageTime += time;
    }

    //regression for comparing performance of algorithm improvements
    if (printAgentInteraction > 3) {
      averageReward /= experimentIterations;
      averageTime /= experimentIterations;

      System.out.println("\n===========REGRESSION===========");
      System.out.println("Average reward: " + averageReward);
      System.out.println("Average time: " + averageTime);
    }
  }
}
