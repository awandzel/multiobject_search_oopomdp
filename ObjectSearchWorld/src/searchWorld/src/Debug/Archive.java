//package searchWorld.src.Debug;
//
//import burlap.mdp.core.action.Action;
//import burlap.mdp.core.state.State;
//import burlap.mdp.singleagent.pomdp.observations.DiscreteObservationFunction;
//import burlap.mdp.singleagent.pomdp.observations.ObservationProbability;
//import burlap.mdp.singleagent.pomdp.observations.ObservationUtilities;
//import org.apache.commons.math3.distribution.NormalDistribution;
//import searchWorld.src.Pomdp.objectObservation;
//import searchWorld.src.State.SearchAgent;
//import searchWorld.src.State.SearchObject;
//import searchWorld.src.State.SearchState;
//import searchWorld.src.State.Utilities;
//
//import java.util.*;
//
//import static searchWorld.src.SearchDomain.NAME_OFFSET;
//import static searchWorld.src.SearchDomain.UNCERTAIN;
//
///**
// * Created by awandzel on 8/7/18.
// */
//
////archieved method to make complete joint distribution
//public class Archive {
//  public List<List<Integer>> permutedObjectLocations = new ArrayList<>(); //List<List<Obj1(1,1), Obj2(2,2)...>>
//  public Map<Integer, List<Integer>> objectLocations = new TreeMap<>(); //maps integer to (1,1) coordinate
//  public List<SearchState> beliefs = new ArrayList<>();
//
//  int[][] beliefMap;
//  List<SearchObject> goals;
//
//  public Archive(int[][] bm, List<SearchObject> g) {
//    this.beliefMap = bm;
//    this.goals = g;
//  }
//  /////////////////////////////////////JOINT DISTRIBUTION/////////////////////////////////////////////////
//  ////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //Possible locations
//  public void setObjectLocations() {
//    int counter = 0;
//    for (int x = 0; x < beliefMap.length; x++) {
//      for (int y = 0; y < beliefMap[0].length; y++) {
//        if (beliefMap[x][y] == UNCERTAIN) {
//          objectLocations.put(counter++, new ArrayList<>(Arrays.asList(x, y)));
//        }
//      }
//    }
//    if (objectLocations.keySet().size() < goals.size()) {
//      throw new RuntimeException("Number of objects cannot exceed uncertain locations");
//    }
//  }
//
//  //Permutation with replacement for numberOfObject locations from choices of objectLocations
//  public void generateDistribution(List<Integer> s, int distributionSize) {
//    if (s.size() >= distributionSize) {
//      this.permutedObjectLocations.add(s);
//      return;
//
//    } else {
//      for (Integer locationIndex : this.objectLocations.keySet()) {
//        List<Integer> s_ = new ArrayList<>(s);
//        s_.add(locationIndex);
//        generateDistribution(s_, distributionSize);
//      }
//    }
//  }
//
//  public void sampleJointBelief(int numberofSamples, SearchState initialState) {
//
//    if (permutedObjectLocations.isEmpty()) {
//      setObjectLocations();
//      generateDistribution(new ArrayList<>(), 1);
//    }
//
//    Random rn = new Random();
//    for (int i = 0; i < numberofSamples; i++) {
//      List<SearchObject> objects = new ArrayList<>();
//      for (int o = 0; o < goals.size(); o++) {
//        int randomLocation = rn.nextInt(permutedObjectLocations.size());
//        List<Integer> location = objectLocations.get(permutedObjectLocations.get(randomLocation).get(0));
//        SearchObject object = new SearchObject(location.get(0), location.get(1), initialState.searchableObjects.get(o).name);
//        objects.add(object);
//      }
//      beliefs.add(new SearchState(initialState.agent, objects, initialState.hasChosen));
//    }
//  }
//
//  public void makeJointBelief(SearchState initialState) {
//    setObjectLocations();
//    generateDistribution(new ArrayList<>(), goals.size());
//    //removeSameSpaceObjectLocations();
//
//    for (int i = 0; i < permutedObjectLocations.size(); i++) {
//      List<Integer> ol = permutedObjectLocations.get(i);
//      List<SearchObject> searchableObjects = new ArrayList<>();
//
//      for (int j = 0; j < goals.size(); j++) {
//        List<Integer> location = objectLocations.get(ol.get(j));
//        searchableObjects.add(new SearchObject(location.get(0), location.get(1), initialState.searchableObjects.get(j).name));
//      }
//      SearchState ss = new SearchState(new SearchAgent(initialState.agent.x, initialState.agent.y, initialState.agent.theta), searchableObjects, initialState.hasChosen);
//      this.beliefs.add(ss);
//    }
//  }
//}

//package searchWorld.src.Pomdp;
//
//        import burlap.mdp.core.action.Action;
//        import burlap.mdp.core.state.State;
//        import burlap.mdp.singleagent.pomdp.observations.DiscreteObservationFunction;
//        import burlap.mdp.singleagent.pomdp.observations.ObservationProbability;
//        import burlap.mdp.singleagent.pomdp.observations.ObservationUtilities;
//        import org.apache.commons.math3.distribution.NormalDistribution;
//        import searchWorld.src.State.SearchObject;
//        import searchWorld.src.State.Utilities;
//
//        import java.util.ArrayList;
//        import java.util.List;
//        import java.util.Random;
//
//        import static searchWorld.src.SearchDomain.*;
//
///**
// * Search Observation Function class
// */
//public class SearchGaussianOF implements DiscreteObservationFunction {
//  private double observationAccuracy;
//  private Utilities util;
//  private int numberOfObjects;
//  private NormalDistribution nd;
//
//  /**
//   * Constructor for the Search Observation Function
//   */
//  public SearchGaussianOF(Utilities u, double oa, int no, double sd) {
//    this.util = u;
//    this.observationAccuracy = oa;
//    this.numberOfObjects = no;
//    this.nd = new NormalDistribution(0, sd);
//  }
//
//  @Override
//  public List<State> allObservations() {
//    ArrayList result = new ArrayList<>();
//
//    for (int i = 0; i < numberOfObjects; i++) {
//      String strObj = "Obj" + Integer.toString(i + NAME_OFFSET);
//      String strNotObj = "notObj" + Integer.toString(i + NAME_OFFSET);
//      result.add(new objectObservation(strObj));
//      result.add(new objectObservation(strNotObj));
//    }
//    //result.add(new objectObservation(NO_OBJECT));
//
//    return result;
//  }
//
//  @Override
//  public List<ObservationProbability> probabilities(State state, Action action) {
//    return ObservationUtilities.probabilitiesByEnumeration(this, state, action);
//  }
//
//  //Look in location to determine if object or not. Correct observation gets observationAccuracy.
////All other (not chosen or incorrect) observations get 1.0 - observationAccuracy / [total not chosen observations]
//  @Override
//  public double probability(State observation, State state, Action action) {
//    throw new java.lang.Error("Depreciated: needs to reflect new observation model (no parameterization/multiple objects&locations");
//
////    SearchState ns = (SearchState) state;
////    objectObservation o = (objectObservation) observation;
////
////    if (action.actionName().equals(ACTION_PARAMETERIZED_SCAN)) {
////      String[] params = ((ObjectParameterizedAction) action).getObjectParameters();
////      List<Integer> lookLocation = util.actWithinBounds(ns, Integer.valueOf(params[1])); //THIS WILL FAIL (need to parameterize scan with look directionItoS)
////      List<Integer> agentLocation = new ArrayList<>(Arrays.asList(ns.agent.x, ns.agent.y));
////
//////      if (o.objectPresent.startsWith("Obj"))
//////        return twoTailedProb(euclideanDistance(ns.searchableObjects.get(ns.objectIndex(params[0])), lookLocation)) / numberOfObjects;
//////      if (o.objectPresent.startsWith("notObj"))
//////        return (1.0 - twoTailedProb(euclideanDistance(ns.searchableObjects.get(ns.objectIndex(params[0])), lookLocation))) / numberOfObjects;
//////      return 0.0;
////
////      if (o.objectPresent.equals(params[0]))
////        return twoTailedProb(euclideanDistance(ns.searchableObjects.get(ns.objectIndex(params[0])), lookLocation));
////      if (o.objectPresent.equals("not" + params[0]))
////        return (1.0 - twoTailedProb(euclideanDistance(ns.searchableObjects.get(ns.objectIndex(params[0])), lookLocation)));
////      return 0.0;
////
////    } else if (action.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
////      String[] params = ((ObjectParameterizedAction) action).getObjectParameters();
////
////      if (util.ObjectInLookForwardLocation(ns, params[0], Integer.valueOf(params[1]))) {
////        if (o.objectPresent.equals(params[0])) return observationAccuracy;
////      } else {
////        if (o.objectPresent.equals("not" + params[0])) return observationAccuracy;
////      }
////      return (1.0 - observationAccuracy) / (allObservations().size() - 1);
////
////    } else {
////      return 1.0 / (double) allObservations().size();
////    }
//  }
//
//  @Override
//  public State sample(State state, Action action) {
//    List<ObservationProbability> opList = this.probabilities(state, action);
//
//    Random rand = new Random();
//    double roll = rand.nextDouble();
//    double curSum = 0.;
//
//    for (int i = 0; i < opList.size(); i++) {
//      curSum += opList.get(i).p;
//      if (roll < curSum) {
//        return opList.get(i).observation;
//      }
//    }
//    throw new RuntimeException("Probabilities don't sum to 1.0: " + curSum);
//  }
//
//  public double euclideanDistance(SearchObject obj, List<Integer> location) {
//    return Math.sqrt(Math.pow(Math.abs(location.get(0) - obj.x), 2) + Math.pow(Math.abs(location.get(1) - obj.y), 2));
//  }
//
//  public double twoTailedProb(double x) {
//    double y = x;
//    double prob = 2.0 * nd.cumulativeProbability(-x);
//    prob = Math.round(prob * 1e4) / 1e4;
//    return prob;
//  }
//}

/////////////////////////////////////DISTANCE METRICS///////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////

//  public double euclideanDistance(SearchState initialState) {
//    SearchAgent agent = initialState.agent;
//    SearchObject goal = goals.get(0);
//    return Math.sqrt(Math.pow(Math.abs(goal.x - agent.x), 2) + Math.pow(Math.abs(goal.y - agent.y), 2));
//  }

//  public double l1(List<Integer> ln) {
//    double sumOverDistances = 0.0;
//    for (int o = 0; o < goals.size(); o++) {
//      sumOverDistances += Math.abs(goals.get(o).x - ln.get(0)) + Math.abs(goals.get(o).y - ln.get(1));
//    }
//    return sumOverDistances;
//  }

//  public double l2(SearchState initialState) {
//    SearchAgent agent = initialState.agent;
//    SearchObject goal = goals.get(0);
//    return Math.pow(Math.abs(goal.x - agent.x), 2) + Math.pow(Math.abs(goal.y - agent.y), 2);
//  }

//package searchWorld.src.Pomdp;
//
//import burlap.mdp.core.action.Action;
//import burlap.mdp.core.state.State;
//import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;
//import searchWorld.src.State.SearchState;
//import searchWorld.src.State.Utilities;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import static searchWorld.src.SearchDomain.*;
//
///**
// * Search Observation Function class
// */
//public class epsilonOF implements ObservationFunction {
//  public double observationAccuracy;
//  public Utilities util;
//  public int numberOfObjects;
//
//  /**
//   * Constructor for the Search Observation Function
//   *
//   * @param oa
//   */
//  public epsilonOF(Utilities u, double oa, int no) {
//    this.util = u;
//    this.observationAccuracy = oa;
//    this.numberOfObjects = no;
//  }
//
////  @Override
////  public List<State> allObservations() {
////    ArrayList<State> result = new ArrayList<>();
////
////	//add all objects and notObject observations
////    for (int i = 0; i < numberOfObjects; i++) {
////      String strObj = "Obj" + Integer.toString(i + NAME_OFFSET);
////      String strNotObj = "notObj" + Integer.toString(i + NAME_OFFSET);
////      result.add(new objectObservation(strObj));
////      result.add(new objectObservation(strNotObj));
////    }
////    result.add(new objectObservation("doNotCare"));
////
////    return result;
////  }
//
////  @Override
////  public List<ObservationProbability> probabilities(State state, Action action) {
////    return ObservationUtilities.probabilitiesByEnumeration(this, state, action);
////  }
//
////Look in location to determine if object or not. Correct observation gets observationAccuracy.
////All other (not chosen or incorrect) observations get 1.0 - observationAccuracy / [total not chosen observations]
//  @Override
//  public double probability(State observation, State state, Action action) {
//    throw new RuntimeException("No specified probability lookup table");
////    SearchState ns = (SearchState) state;
////    objectObservation o = (objectObservation) observation;
////
////    if (action.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
////      String[] params = ((ObjectParameterizedAction) action).getObjectParameters();
////
////      if (o.objectPresent.equals(params[0])){
////        if (util.ObjectInLookForwardLocation(ns, params[0], util.directionStoI(params[1]))) return observationAccuracy;
////      } else {
////        if (!util.ObjectInLookForwardLocation(ns, params[0], util.directionStoI(params[1]))) return observationAccuracy;
////      }
////      return (1.0 - observationAccuracy) / (allObservations().size() - 2);
////
////    } else {
////      if (o.objectPresent.equals("doNotCare")){
////        return 1.0;
////      }
////      return 0.0;
////    }
//  }
//
//  @Override
//  public State sample(State state, Action action) {
//    SearchState ns = (SearchState) state;
//    visionConeObservation vco;
//
//    if (action.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
//      vco = sampleLookAction(ns, action);
//    } else {
//      vco = sampleMoveAction();
//    }
//    return vco;
//  }
//
//  public visionConeObservation sampleLookAction(SearchState ns, Action action) {
//    Random rand = new Random();
//    List<locationInVisionCone> visionCone = util.locationsInVisionCone(ns, action, observationAccuracy);
//    //List<locationInVisionCone> visionCone = util.locationsInVisionBoundingBox(ns, observationAccuracy);
//    visionConeObservation vco = new visionConeObservation();
//
//    for (int i = 0; i < numberOfObjects; i++) {
//      List<objectObservation> newMultiObjectObservation = new ArrayList<>();
//      String strObj = "Obj" + Integer.toString(i + NAME_OFFSET);
//      String strNotObj = "notObj" + Integer.toString(i + NAME_OFFSET);
//
//      for (locationInVisionCone vl : visionCone) {
//        double roll = rand.nextDouble();
//        objectObservation observation = null;
//
//        //if object
//        if (!ns.hasChosen.get(i)) {
//          if (util.ObjectInLookAnyLocation(ns, strObj, vl.location)) {
//            //if correct
//            observation = (roll < vl.accuracy) ? new objectObservation(strObj) : new objectObservation(strNotObj);
//          } else {
//            observation = (roll < vl.accuracy) ? new objectObservation(strNotObj) : new objectObservation(strObj);
//          }
//        } else {
//          observation = new objectObservation("doNotCare");
//        }
//        newMultiObjectObservation.add(observation);
//      }
//      vco.observation.add(newMultiObjectObservation);
//    }
//    return vco;
//  }
//
//  public visionConeObservation sampleMoveAction() {
//    visionConeObservation newMultiCellObservation = new visionConeObservation();
//    List<objectObservation> newMultiObjectObservation = new ArrayList<>();
//    for (int i = 0; i < numberOfObjects; i++) {
//      newMultiObjectObservation.add(new objectObservation("doNotCare"));
//    }
//    newMultiCellObservation.observation.add(newMultiObjectObservation);
//    return newMultiCellObservation;
//  }
//
//  //if object then --> .95 in object present and .05 everywhere else
//  //if no object then --> .05 in vision cone and 1 outside visioncone
//
///*
//
//
//
//
//
// */
//
//}

//  public void updateHeatMap(SearchState currentState, Action a, State observation, double observationAccuracy) {
//    visionConeObservation no = (visionConeObservation) observation;
//    List<locationInVisionCone> ls = util.locationsInVisionCone(currentState, a, observationAccuracy);
//    //List<locationInVisionCone> ls = util.locationsInVisionBoundingBox(currentState,observationAccuracy);
//
//    for (int o = 0; o < no.observation.size(); o++) {
//      String objName = currentState.searchableObjects.get(o).name;
//      for (int l = 0; l < no.observation.get(o).size(); l++) {
//        int x = ls.get(l).location.x;
//        int y = ls.get(l).location.y;
//
//        //objectPresent
//        if (no.observation.get(o).get(l).objectPresent.equals(objName)) {
//          setValueAndRenormalize(o, observationAccuracy, x, y);
//        } else if (no.observation.get(o).get(l).objectPresent.equals("not" + objName)) {
//          setValueAndRenormalize(o, 1.0 - observationAccuracy, x, y);
//        }
//      }
//    }
//    //printHeatMap();
//  }
//
//  public void setValueAndRenormalize(int objectIndex, double value, int nx, int ny) {
//    Map<Location, Double> newHeatMap = new HashMap<>();
//    Location currentLoaction = new Location(nx, ny);
//    Double removePastThreshold = 0.0;
//
//    if (value == 1) {
//      newHeatMap.put(currentLoaction, 1.0);
//    }
//    if (value == 0) {
//      newHeatMap = this.belief.get(objectIndex);
//      if (newHeatMap.containsKey(currentLoaction)) {
//        newHeatMap.put(currentLoaction, value);
//        if (newHeatMap.get(currentLoaction) <= removePastThreshold) {
//          newHeatMap.remove(currentLoaction);
//        }
//        Double normalize = 0.0;
//        for (Double d : newHeatMap.values()) normalize += d;
//        for (Location l : newHeatMap.keySet()) newHeatMap.put(l, newHeatMap.get(l) / normalize);
//      }
//    }
//    this.belief.put(objectIndex, newHeatMap);
//  }

