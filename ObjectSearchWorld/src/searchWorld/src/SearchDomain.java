package searchWorld.src;

import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.mdp.singleagent.oo.ObjectParameterizedActionType;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.beliefstate.EnumerableBeliefState;
import burlap.mdp.singleagent.pomdp.beliefstate.TabularBeliefState;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;
import burlap.statehashing.ReflectiveHashableStateFactory;
import searchWorld.src.BeliefRoadMap.Edge;
import searchWorld.src.BeliefRoadMap.Graph;
import searchWorld.src.Model.SearchModel;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.Pomdp.sensorModel;
import searchWorld.src.Model.SearchRF;
import searchWorld.src.Model.SearchTF;
import searchWorld.src.Pomdp.locationInVisionCone;
import searchWorld.src.State.Rooms;
import searchWorld.src.State.SearchObject;
import searchWorld.src.State.SearchState;
import searchWorld.src.State.Utilities;

import java.util.*;

/**
 * The Search Domain.  Can return a fully observable or partially observable domain.
 */
public class SearchDomain implements DomainGenerator {

  public final static String VAR_AGENT_X = "ax";
  public final static String VAR_AGENT_Y = "ay";
  public final static String VAR_AGENT_THETA = "at";
  public final static String VAR_OBJECT_X = "ox";
  public final static String VAR_OBJECT_Y = "oy";
  public final static String VAR_HAS_CHOSEN = "hasChosen";
  public final static String VAR_OBSERVATION = "obs";

  public final static String NORTH = "north";
  public final static String SOUTH = "south";
  public final static String EAST = "east";
  public final static String WEST = "west";

  public final static String ACTION_PARAMETERIZED_MOVE = "move";
  public final static String ACTION_PARAMETRIZED_MOVE_ROOM = "moveTo";
  public final static String ACTION_PARAMETERIZED_LOOK = "look";
  public final static String ACTION_PARAMETERIZED_PICK = "pick";

  public final static String CLASS_OBJECTS = "objectToFind";
  public final static String CLASS_AGENT = "agent";

  public final static int EMPTY = 0;
  public final static int WALL = -2;
  public final static int UNCERTAIN = -1;
  public final static int ROOMCENTER = -3;
  public final static int MAPCENTER = -4;
  public final static int NAME_OFFSET = 1; //off set used to convert object range 1-n into index range 0-n and vice versa

  public final static String NO_OBJECT = "notObj";

  private double goalReward;
  private double moveReward;
  private double pickReward;
  private double moveRoomReward;
  private double lookReward;

  private double observationAccuracy;
  FactoredModel FM;
  public static Utilities util;

  String solutionMethod;
  int numberOfObjects;

  ObservationFunction OF;
  boolean epsilonOFModel;
  Random rnObservation;
  double obsSD;
  double betaV;
  double gammaV;
  double alphaV;
  double betaNV;
  double gammaNV;
  double alphaNV;


  /**
   * Constructor for SearchDomain
   * <p>
   * fullyObservable     - A bool telling us whether or not we should return a fully or
   * partially observable domain in generate domain
   *
   * @param map                 - The worldMap of our environment
   * @param goalReward          - The goal reward
   * @param moveReward          - The move reward
   * @param pickReward          - The scan or pick reward
   * @param observationAccuracy - The observationAccuracy to be passed into the observation
   *                            function.
   */
  public SearchDomain(int[][] map, Rooms rooms, Graph rm, double goalReward, double moveReward,
                      double pickReward, double moveRoomReward, double scanReward,
                      double observationAccuracy, int v, String sm,
                      boolean t, int no, Random rnO,
                      double sd, double bv, double gv, double av, double bnv, double gnv, double anv) {
    this.util = new Utilities(map, rooms, rm, v);
    this.goalReward = goalReward;
    this.moveReward = moveReward;
    this.pickReward = pickReward;
    this.moveRoomReward = moveRoomReward;
    this.lookReward = scanReward;
    this.observationAccuracy = observationAccuracy;
    this.solutionMethod = sm;
    this.epsilonOFModel = t;
    this.numberOfObjects = no;
    this.rnObservation = rnO;
    this.obsSD = sd;
    this.betaV = bv;
    this.gammaV = gv;
    this.alphaV = av;
    this.betaNV = bnv;
    this.gammaNV = gnv;
    this.alphaNV = anv;
  }

  public FactoredModel getModel() {
    return this.FM;
  }

  public Domain generateDomain() {
    // Domain depends on whether fully observable or partially observable.
    SADomain domain = (solutionMethod.equals("VI") || solutionMethod.equals("visualMDP")) ? new OOSADomain() : new PODomain();
    boolean tabularBeliefState = solutionMethod.equals("BSS");

//    domain.addActionTypes(new UniversalActionType(ACTION_ORIENT_NORTH),
//            new UniversalActionType(ACTION_ORIENT_SOUTH),
//            new UniversalActionType(ACTION_ORIENT_EAST),
//            new UniversalActionType(ACTION_ORIENT_WEST));
    domain.addActionType(new Move(ACTION_PARAMETERIZED_MOVE, new String[]{"Location"}, tabularBeliefState));
    domain.addActionType(new Look(ACTION_PARAMETERIZED_LOOK, new String[]{"Direction"}, tabularBeliefState));
    domain.addActionType(new MoveRoom(ACTION_PARAMETRIZED_MOVE_ROOM, new String[]{"Location, Room"}, tabularBeliefState));
    domain.addActionType(new Pickup(ACTION_PARAMETERIZED_PICK, new String[]{CLASS_OBJECTS, "Location"}, tabularBeliefState));


    SearchModel sModel = new SearchModel(util);
    RewardFunction rf = new SearchRF(util, goalReward, moveReward, pickReward, moveRoomReward, lookReward);
    TerminalFunction tf = new SearchTF();

    FactoredModel model = new FactoredModel(sModel, rf, tf);
    this.FM = model;
    domain.setModel(model);

    // If not fully observable needs a observation function a state enumerator
    if (solutionMethod.equals("BSS") || solutionMethod.equals("Pomcp") || solutionMethod.equals("Random")) {
      //epsilonOF otf = new epsilonOF(util, observationAccuracy, numberOfObjects);
      sensorModel sof = new sensorModel(util, numberOfObjects, rnObservation, obsSD, betaV, gammaV, alphaV, betaNV, gammaNV, alphaNV);
      ((PODomain) domain).setObservationFunction(sof);

//      //set observation function (epsilonOFModel or gaussian)
//      if (epsilonOFModel) {
//        ((PODomain) domain).setObservationFunction(otf);
//      } else {
//        ((PODomain) domain).setObservationFunction(sof);
//      }

      StateEnumerator stateEnumerator =
              new StateEnumerator(domain, new ReflectiveHashableStateFactory());
      ((PODomain) domain).setStateEnumerator(stateEnumerator);
    }
    return domain;
  }

  public class MoveRoom extends ObjectParameterizedActionType {

    Boolean tabularBeliefState;

    public MoveRoom(String name, String[] parameterClasses, Boolean fo) {
      super(name, parameterClasses);
      this.tabularBeliefState = fo;
    }

    @Override
    protected boolean applicableInState(State s, ObjectParameterizedAction a) {
      return true;
    }

    @Override
    public List<Action> allApplicableActions(State s) {
      List<Action> res = new ArrayList<>();

      SearchState ns = (this.tabularBeliefState) ? (SearchState) ((TabularBeliefState) s).nonZeroBeliefs().get(0).s : (SearchState) s;
      Location currentLocation = new Location(ns.agent.x, ns.agent.y);

      List<Integer> connectivity = new ArrayList<>();
      if (!util.roomAbstractions.mappingFromAgentToRoom.isEmpty()) {
        Integer currentRoom = util.roomAbstractions.mappingFromAgentToRoom.get(currentLocation);
        connectivity = util.roomAbstractions.connectedRooms(currentRoom);
      }

      for (Integer roomIndex : connectivity) {
        String room = "Room" + Integer.toString(roomIndex);
        Location newRoomLocation = util.roomAbstractions.transitionMatrix.get(roomIndex);
        res.add(new SAObjectParameterizedAction(this.typeName(), new String[]{Integer.toString(newRoomLocation.x), Integer.toString(newRoomLocation.y), room}));
      }
      return res;
    }
  }

  public class Move extends ObjectParameterizedActionType {

    Boolean tabularBeliefState;

    public Move(String name, String[] parameterClasses, Boolean fo) {
      super(name, parameterClasses);
      this.tabularBeliefState = fo;
    }

    @Override
    protected boolean applicableInState(State s, ObjectParameterizedAction a) {
      return true;
    }

    @Override
    public List<Action> allApplicableActions(State s) {
      List<Action> res = new ArrayList<>();

      SearchState ns = (this.tabularBeliefState) ? (SearchState) ((TabularBeliefState) s).nonZeroBeliefs().get(0).s : (SearchState) s;
      List<Edge> transitions = util.roadMap.nodes.get(new Location(ns.agent.x, ns.agent.y));
      if (transitions != null){
        for (Edge e : transitions) {
          Location l = e.traversableLocation;
          res.add(new SAObjectParameterizedAction(this.typeName(), new String[]{Integer.toString(l.x), Integer.toString(l.y)}));
        }
      }
//      for (int d = 0; d < 4; d++) {
//        if (util.inBoundsForForwardLocation(ns, d)) {
//          res.add(new SAObjectParameterizedAction(this.typeName(), new String[]{util.directionItoS(d)}));
//        }
//      }
      return res;
    }
  }

  public class Look extends ObjectParameterizedActionType {
    boolean tabularBeliefState;

    public Look(String name, String[] parameterClasses, boolean fop) {
      super(name, parameterClasses);
      this.tabularBeliefState = fop;
    }

    @Override
    protected boolean applicableInState(State s, ObjectParameterizedAction a) {
      return true;
    }

    @Override
    public List<Action> allApplicableActions(State s) {
      List<Action> res = new ArrayList<>();
      Set<String> uniqueActions = new HashSet<String>();

      if (!tabularBeliefState) {
        SearchState ns = (SearchState) s;
        for (int d = 0; d < 4; d++) {
          List<Integer> action = util.actWithinBounds(ns, d);
          int x = action.get(0);
          int y = action.get(1);
          //check if look is out of bounds (as determined w/in actWithinBounds)
          if (!(action.get(0) == ns.agent.x && action.get(1) == ns.agent.y)) {
            res.add(new SAObjectParameterizedAction(this.typeName(), new String[]{util.directionItoS(d), Integer.toString(x), Integer.toString(y)}));
          }
        }
      }
//      if (!tabularBeliefState) {
//        res.add(new SAObjectParameterizedAction(this.typeName(), new String[]{"Look"}));
//      }
      else {
        TabularBeliefState tbs = (TabularBeliefState) s;
        for (EnumerableBeliefState.StateBelief sb : tbs.nonZeroBeliefs()) {
          SearchState ns = (SearchState) sb.s;
          for (int d = 0; d < 4; d++) {
            String name = ACTION_PARAMETERIZED_LOOK + "_" + util.directionItoS(d);
            if (!uniqueActions.contains(name) && util.inBoundsForForwardLocation(ns, d)) {
              List<Integer> action = util.actWithinBounds(ns, d);
              int x = action.get(0);
              int y = action.get(1);
              if (!(action.get(0) == ns.agent.x && action.get(1) == ns.agent.y)) {
                uniqueActions.add(name);
                res.add(new SAObjectParameterizedAction(this.typeName(), new String[]{util.directionItoS(d), Integer.toString(x), Integer.toString(y)}));
              }

            }
          }
        }
      }
      return res;
    }
  }


  public class Pickup extends ObjectParameterizedActionType {
    boolean tabularBeliefState;

    public Pickup(String name, String[] parameterClasses, boolean fop) {
      super(name, parameterClasses);
      this.tabularBeliefState = fop;
    }

    @Override
    protected boolean applicableInState(State s, ObjectParameterizedAction a) {
      return true;
    }

    @Override
    public List<Action> allApplicableActions(State s) {
      List<Action> res = new ArrayList<Action>();
      Set<String> uniqueActions = new HashSet<String>();

      if (!this.tabularBeliefState) {
        SearchState ns = (SearchState) s;
        List<Location> pickLocations = util.locationsInVisionBoundingBox(ns);

        for (Location l : pickLocations) {
          boolean objectIsPresent = false;
          for (SearchObject o : ns.searchableObjects) {
            if (l.x == o.x && l.y == o.y && !ns.hasChosen.get(ns.objectIndex(o.name))) {
              //if (!ns.hasChosen.get(ns.objectIndex(o.name))){
              objectIsPresent = true;
              break;
            }
          }
          if (objectIsPresent) {
            res.add(new SAObjectParameterizedAction(this.typeName(), new String[]{Integer.toString(l.x), Integer.toString(l.y)}));
          }
        }


      }
      //POMDP: must iterate over all possible belief states
      else {
        TabularBeliefState tbs = (TabularBeliefState) s;
        for (EnumerableBeliefState.StateBelief sb : tbs.nonZeroBeliefs()) {
          SearchState ns = (SearchState) sb.s;

          for (int i = 0; i < ns.numObjects(); i++) {
            String objectName = ns.searchableObjects.get(i).name();

            for (int d = 0; d < 4; d++) {
              if (util.ObjectInLookForwardLocation(ns, objectName, d) && !ns.hasChosen.get(i)) {
                List<Integer> action = util.actWithinBounds(ns, d);
                int x = action.get(0);
                int y = action.get(1);
                String name = ACTION_PARAMETERIZED_PICK + "_" + objectName + "_" + util.directionItoS(d) + "_" + x + "_" + y;

                if (!uniqueActions.contains(name)) {
                  uniqueActions.add(name);
                  res.add(new SAObjectParameterizedAction(this.typeName(), new String[]{ns.searchableObjects.get(i).name(), util.directionItoS(d), Integer.toString(x), Integer.toString(y)}));
                }
              }
            }
          }
        }
      }
      return res;
    }
  }
}
