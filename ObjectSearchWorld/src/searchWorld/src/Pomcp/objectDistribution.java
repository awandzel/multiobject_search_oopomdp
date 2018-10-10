package searchWorld.src.Pomcp;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.PODomain;
import org.apache.commons.math3.distribution.NormalDistribution;
import searchWorld.src.Experiments.WorldMaps;
import searchWorld.src.Pomdp.*;
import searchWorld.src.State.SearchObject;
import searchWorld.src.State.SearchState;

import java.util.*;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by awandzel on 8/2/18.
 */
public class objectDistribution {
  public Map<Integer, Map<Location, Double>> belief = new HashMap<>();
  public Map<Integer, Map<Location, Double>> objectDistribution = new HashMap<>();

  public int[][] beliefMap;
  public int numberOfGoals;
  PODomain sd;
  Random rn;
  int printAgentInteraction;

  public objectDistribution(int[][] bm, int ng, Random rn,  int p) {
    this.beliefMap = bm;
    this.numberOfGoals = ng;
    this.rn = rn;
    this.printAgentInteraction = p;
  }

  /////////////////////////////////////FACTORED OOPOMCP DISTRIBUTION//////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void setDomain(PODomain sd){
    this.sd = sd;
  }

  public void beliefUpdate(SearchState currentState, Action a, State o) {
    if (a.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
      updateHeatMap(currentState, a, o);
    }
  }

  public void updateHeatMap(SearchState currentState, Action a, State observation) {
    Map<Integer, Map<Location, Double>> newheatMap = new HashMap<>();
    sensorModel OF = ((sensorModel) sd.getObservationFunction());
    visionConeObservation no = (visionConeObservation) observation;
    List<Location> visionCone = util.locationsInVisionCone(currentState, a);
    //List<locationInVisionCone> ls = util.locationsInVisionBoundingBox(currentState,observationAccuracy);

    for (int o = 0; o < no.observation.size(); o++) {
      Map<Location, Double> observationProbability = new HashMap<>();
      Map<Location, Double> newObjectHeatMap = new HashMap<>();
      String strObj = "Obj" + Integer.toString(o + NAME_OFFSET);

      //need to see if object is present by observation
      boolean objectIsPresent = false;
      Location objectLocation = null;
      for (int i = 0; i < visionCone.size(); i++){
        if (no.observation.get(o).get(i).objectPresent.equals(strObj)) {
          objectLocation = visionCone.get(i);
          objectIsPresent = true;
          break;
        }
      }

      //hash up locations in visionCone
      HashSet<Location> InVisionCone = new HashSet<>();
      for (Location l : visionCone) InVisionCone.add(l);

      for (Location l : this.belief.get(o).keySet()){
        double p;
        if (objectIsPresent){
          p = (InVisionCone.contains(l)) ?
                  OF.probabilityOfObservationPresent("V", l, objectLocation, visionCone.size()) :
                  OF.probabilityOfObservationPresent("NV", l, objectLocation, visionCone.size());
        } else{
          p = (InVisionCone.contains(l)) ?
                  OF.probabilityOfObservationNOTPresent("V") :
                  OF.probabilityOfObservationNOTPresent("NV");
        }
        observationProbability.put(l, p);
      }

      for (Location l : this.belief.get(o).keySet()) {
        double updateProbability = this.belief.get(o).get(l) * observationProbability.get(l);
        newObjectHeatMap.put(l, updateProbability);
      }

      Double normalize = 0.0;
      for (Double d : newObjectHeatMap.values()) normalize += d;
      for (Location l : newObjectHeatMap.keySet()) newObjectHeatMap.put(l, newObjectHeatMap.get(l) / normalize);
      this.belief.put(o, newObjectHeatMap);
    }
    if (printAgentInteraction > 1) printHeatMap(this.belief, true, currentState);
  }

  /////////////////////////////////////SAMPLING//////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////
  public factoredBelief completeOOPOMCPSample(SearchState ns) {
    factoredBelief fbs = new factoredBelief();
    fbs.candidateState = ns;

    for (int o = 0; o < ns.searchableObjects.size(); o++) {
      for (Location l : belief.get(o).keySet()) {
        String objName = ns.searchableObjects.get(o).name;
        if (!fbs.factoredObjectBelief.containsKey(objName)) {
          fbs.factoredObjectBelief.put(objName, new ArrayList<>(Arrays.asList(l)));
        } else {
          fbs.factoredObjectBelief.get(objName).add(l);
        }
      }
    }
    return fbs;
  }

  public factoredBelief partialOOPOMCPSample(SearchState ns, int numberOfSamples) {
    factoredBelief fbs = new factoredBelief();
    fbs.candidateState = ns;

    for (int i = 0; i < numberOfSamples; i++) {
      fbs.add(sampleHeatMap(ns));
    }
    return fbs;
  }

  public List<SearchState> POMCPSample(SearchState ns, int numberOfSamples) {
    List<SearchState> jb = new ArrayList<>();

    for (int i = 0; i < numberOfSamples; i++) {
      jb.add(sampleHeatMap(ns));
    }
    return jb;
  }

  public SearchState sampleHeatMap(SearchState currentState) {
    List<SearchObject> objects = new ArrayList<>();

    for (int o = 0; o < numberOfGoals; o++) {
      if (!currentState.hasChosen.get(o)) {
        double roll = rn.nextDouble();
        Location nl = sampleHeatMapObject(roll, o);
        objects.add(new SearchObject(nl.x, nl.y, currentState.searchableObjects.get(o).name));
      } else {
        objects.add(currentState.searchableObjects.get(o));
      }
    }
    SearchState newState = new SearchState(currentState.agent, objects, currentState.hasChosen);
    return newState.deepCopy();
    //return newState;
  }

  public Location sampleHeatMapObject(double roll, int o) {
    double curSum = 0.;
    Map<Location, Double> hm = this.belief.get(o);

    for (Map.Entry<Location, Double> e : hm.entrySet()) {
      curSum += e.getValue();
      if (roll <= curSum) {
        return new Location(e.getKey());
      }
    }
    throw new RuntimeException("Probabilities don't sum to 1.0: " + curSum);
  }

  public Location sampleInitialObjectLocation(double roll, int o) {
    double curSum = 0.;
    Map<Location, Double> hm = this.objectDistribution.get(o);

    for (Map.Entry<Location, Double> e : hm.entrySet()) {
      curSum += e.getValue();
      if (roll <= curSum) {
        return new Location(e.getKey());
      }
    }
    throw new RuntimeException("Probabilities don't sum to 1.0: " + curSum);
  }

  /////////////////////////////////////MAKE HEAT MAP//////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////
  public Map<Integer, Map<Location, Double>> makeUniformHeatMap() {
    Map<Integer, Map<Location, Double>> heatMap = new HashMap<>();
    Map<Location, Double> newHeatMap = new HashMap<>();

    for (int x = 0; x < beliefMap.length; x++) {
      for (int y = 0; y < beliefMap[0].length; y++) {
        if (beliefMap[x][y] == UNCERTAIN) {
          Location currentLocation = new Location(x, y);
          if (!newHeatMap.containsKey(currentLocation)) {
            newHeatMap.put(currentLocation, 1.0);
          } else {
            newHeatMap.put(currentLocation, newHeatMap.get(currentLocation) + 1.0);
          }
        }
      }
    }

    Double normalization = 0.0;
    for (Double d : newHeatMap.values()) normalization += d;
    for (Location l : newHeatMap.keySet()) {
      newHeatMap.put(l, newHeatMap.get(l) / normalization);
    }
    for (int o = 0; o < numberOfGoals; o++) {
      Map<Location, Double> addHeatMap = new HashMap<>(newHeatMap);
      heatMap.put(o, addHeatMap);
    }
//    if (printAgentInteraction > 1) printHeatMap(heatMap);
    return heatMap;
  }

  public void makeGaussianHeatMap(double standardDeviation, Location center1, Location center2, boolean multimodal) {
    NormalDistribution nd = new NormalDistribution(0, standardDeviation);
    Map<Location, Double> newHeatMap = new HashMap<>();
    double normalization = 0.0;

    //each cell gets weight proportional to euclidean distance to object o goal
    for (int x = 0; x < beliefMap.length; x++) {
      for (int y = 0; y < beliefMap[0].length; y++) {
        if (beliefMap[x][y] == UNCERTAIN) {
          //double euclideanDistanceOnMap = Math.sqrt(Math.pow(Math.abs(goals.get(o).x - x), 2) + Math.pow(Math.abs(goals.get(o).y - y), 2));
          double euclideanDistanceFromCenter1 = Math.sqrt(Math.pow(Math.abs(center1.x - x), 2) + Math.pow(Math.abs(center1.y - y), 2));
          double prob = nd.cumulativeProbability(-euclideanDistanceFromCenter1);

          if (multimodal) {
            double euclideanDistanceFromCenter2 = Math.sqrt(Math.pow(Math.abs(center2.x - x), 2) + Math.pow(Math.abs(center2.y - y), 2));
            prob += nd.cumulativeProbability(-euclideanDistanceFromCenter2);
          }

          newHeatMap.put(new Location(x, y), prob);
          normalization += prob;
        }
      }
    }
    //normalize
    for (Location l : newHeatMap.keySet()) {
      newHeatMap.put(l, newHeatMap.get(l) / normalization);
    }
    for (int o = 0; o < numberOfGoals; o++) {
      Map<Location, Double> addHeatMap = new HashMap<>(newHeatMap);
      belief.put(o, addHeatMap);
    }
    //printHeatMap(this.belief);
  }

  /////////////////////////////////////PRINT HEAT MAP/////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void printHeatMap(Map<Integer, Map<Location, Double>> belief, boolean beliefUpdate, SearchState ns) {
    for (int o = 0; o < numberOfGoals; o++) {
      System.out.print("============Object: " + Integer.toString(o) + "============\n");

      double[][] m = new double[beliefMap.length][beliefMap[0].length];

      for (int x = 0; x < beliefMap.length; x++) {
        for (int y = 0; y < beliefMap[0].length; y++) {
          Location currentLocaton = new Location(x, y);
          if (belief.get(o).containsKey(currentLocaton)) {
            double prob = Math.round(belief.get(o).get(currentLocaton) * 1e4) / 1e4;
            m[x][y] = prob;
          } else {
            m[x][y] = -100; //no probability mass
          }

          if (beliefUpdate && (ns.agent.x == x && ns.agent.y == y)) {
            m[x][y] = -1;
          }
        }
      }

      double[][] mRotated = WorldMaps.rotateToLeft(m);

      for (int x = 0; x < beliefMap.length; x++) {
        System.out.print("{");
        for (int y = 0; y < beliefMap[0].length; y++) {
          String token = Double.toString(mRotated[x][y]);
          if (token.length() < 6){
            int bounds = token.length();
            for (int i = 0; i < 6 - bounds; i++) {
              token += "0";
            }
          }
          if (mRotated[x][y] == 1.0) {
            token = "XXXXXX";
          } else if (mRotated[x][y] == -1.0){
            token = "AAAAAA";
          }
          System.out.print(token+ ", ");
        }
        System.out.print("}\n");
      }
    }
  }
}


