package searchWorld.src.BeliefRoadMap;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.core.Domain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.ReflectiveHashableStateFactory;
import burlap.visualizer.Visualizer;
import searchWorld.src.BeliefRoadMap.RoutingMDP.routeDomain;
import searchWorld.src.BeliefRoadMap.RoutingMDP.routeMDPVisualizer;
import searchWorld.src.BeliefRoadMap.RoutingMDP.routeState;
import searchWorld.src.Environments.SearchMdpEnvironment;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.Visualization.SearchMdpVisualizer;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by awandzel on 8/6/18.
 */
public class RRT {
  //input map & iterations & visionDepth
  //output nodes Map<Location, Edge> nodes = new HashMap<>(); (nodes.keyset() == nodes)
  //EDGE: List<Location> edges;

  //Reference: original implementation by Steve Lavalle: http://msl.cs.illinois.edu/~lavalle/sub/rrt.py

  public int[][] worldMap;
  public int[][] beliefMap;
  Random randomSeed;

  public RRT(int[][] wm, int[][] bm, Random rs) {
    this.worldMap = wm;
    this.beliefMap = bm;
    this.randomSeed = rs;
  }

  public double euclideanDistance(Location compare1, Location compare2) {
    return Math.sqrt(Math.pow(Math.abs(compare1.x - compare2.x), 2) + Math.pow(Math.abs(compare1.y - compare2.y), 2));
  }

  public Graph buildGraph(Graph g, Location startingLocation, double visionDepth, long numberOfSeconds, boolean print, List<Location> cellsInRoom, int numberOfVertices) throws InterruptedException {
    g.nodes.put(startingLocation, new ArrayList<>());  //initialize with null edge
    double distanceBetweenVertices = 2 * visionDepth; //look from vertex A & look from vertex B < 2*visionDepth

    long start_time = System.currentTimeMillis();
    long wait_time = 1000 * numberOfSeconds;
    long end_time = start_time + wait_time;
    int i = 0;


    while (System.currentTimeMillis() < end_time) {
//      System.out.println("RRT: IS IN DEBUG MODE!");
//      for (; i < numberOfVertices; ) {
      Location randConfig = null;
      if (cellsInRoom.isEmpty()) {
        randConfig = sampleRandomConfiguration(g);
      } else {
        randConfig = sampleRandomConfigurationFromRoom(g, cellsInRoom);
      }
      Location nearestVertex = nearestVertex(g, randConfig);
      Location kStepLocation = kLengthStep(randConfig, nearestVertex, distanceBetweenVertices);
      double distanceToNearestVertex = euclideanDistance(kStepLocation, nearestVertex);

      if (worldMap[kStepLocation.x][kStepLocation.y] != WALL && !g.nodes.containsKey(kStepLocation) && distanceToNearestVertex > visionDepth) {
        Episode pathRoute = planFromAtoB(nearestVertex, kStepLocation, false);

        if (!pathConflict(pathRoute)) {
          i++;
          g = addNode(g, kStepLocation, nearestVertex, pathRoute, print);
        }
      }
      Thread.sleep(100);
    }
    System.out.println(i);
    return g;
  }

  public Graph addNode(Graph g, Location kStepLocation, Location nearestVertex, Episode pathRoute, boolean print) {
    Episode pathRouteReverse = planFromAtoB(kStepLocation, nearestVertex, print);

    Edge newEdgefromAtoB = new Edge(kStepLocation, pathRoute.actionSequence);
    Edge newEdgefromBtoA = new Edge(nearestVertex, pathRouteReverse.actionSequence);
    g.nodes.get(nearestVertex).add(newEdgefromAtoB);
    g.nodes.put(kStepLocation, new ArrayList<>(Arrays.asList(newEdgefromBtoA))); //initialize new node and add back link

    return g;
  }

  public Location sampleRandomConfiguration(Graph g) {
    boolean sampledConfiguration = false;
    int x = -1;
    int y = -1;
    while (!sampledConfiguration) {
      x = randomSeed.nextInt(worldMap.length);
      y = randomSeed.nextInt(worldMap.length);

      if (worldMap[x][y] != WALL && !g.nodes.containsKey(new Location(x, y))) {
        sampledConfiguration = true;
      }
    }
    return new Location(x, y);
  }

  public Location sampleRandomConfigurationFromRoom(Graph g, List<Location> cellsInRoom) {
    Location testConfiguration;
    while (true) {
      testConfiguration = cellsInRoom.get(randomSeed.nextInt(cellsInRoom.size()));

      if (!g.nodes.containsKey(testConfiguration)) {
        break;
      }
    }
    return testConfiguration;
  }


  public Location nearestVertex(Graph g, Location randConfig) {

    double minimum = Double.POSITIVE_INFINITY;
    Location nearestVertex = null;
    for (Location vertex : g.nodes.keySet()) {
      double distance = euclideanDistance(vertex, randConfig);
      if (distance < minimum) {
        minimum = distance;
        nearestVertex = vertex;
      }
    }
    return nearestVertex;
  }

  public Location kLengthStep(Location randConfig, Location nearestVertex, double k) {
    if (euclideanDistance(randConfig, nearestVertex) < k) {
      return randConfig;
    } else {
      double theta = Math.atan2(randConfig.y - nearestVertex.y, randConfig.x - nearestVertex.x);
      double unroundedX = k * Math.cos(theta);
      double unroundedY = k * Math.sin(theta);
      int x = nearestVertex.x + (int) Math.rint(unroundedX);
      int y = nearestVertex.y + (int) Math.rint(unroundedY);
      return new Location(x, y);
    }
  }

  public Episode planFromAtoB(Location nearestVertex, Location kStepLocation, boolean print) {


    // VI Parameters
    double viGamma = 1;
    double viMaxDelta = 0.001;
    int viMaxIterations = 100;

    Visualizer vis = new routeMDPVisualizer(worldMap, nearestVertex, kStepLocation).getVisualizer();
    ReflectiveHashableStateFactory hsf = new ReflectiveHashableStateFactory();

    routeDomain rd = new routeDomain(worldMap, kStepLocation);
    SADomain domain = (SADomain) rd.generateDomain();
    State initialState = new routeState(nearestVertex);

    SimulatedEnvironment env = new SearchMdpEnvironment(domain, initialState);
    SearchMdpEnvironment.setPrint(0);

    ValueIteration planner = new ValueIteration(domain, viGamma,
            hsf, viMaxDelta, viMaxIterations);
    DPrint.toggleCode(planner.getDebugCode(), false);
    Policy p = planner.planFromState(initialState);

    Episode e = PolicyUtils.rollout(p, env, 100);

    if (print)
      new EpisodeSequenceVisualizer(vis, domain, Arrays.asList(e));

    return e;
  }

  public boolean pathConflict(Episode e) {
    for (State s : e.stateSequence) {
      routeState ns = (routeState) s;

      if (worldMap[ns.currentPosition.x][ns.currentPosition.y] == WALL) {
        return true;
      }
    }
    return false;
  }

  public Graph addEdge(Graph g, Location l) {
    Location nearestVertex = nearestVertex(g, l);
    Episode pathRoute = planFromAtoB(nearestVertex, l, false);
    return addNode(g, l, nearestVertex, pathRoute, false);
  }
}

