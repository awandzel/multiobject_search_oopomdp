package searchWorld.src.Pomdp;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;
import org.apache.commons.math3.distribution.NormalDistribution;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.State.SearchObject;
import searchWorld.src.State.SearchState;
import searchWorld.src.State.Utilities;

import java.util.*;

import static searchWorld.src.SearchDomain.ACTION_PARAMETERIZED_LOOK;
import static searchWorld.src.SearchDomain.NAME_OFFSET;

/**
 * Created by awandzel on 8/14/18.
 */
public class sensorModel implements ObservationFunction {
  public Utilities util;
  public int numberOfObjects;
  NormalDistribution nd;
  Random rnObservation;

  double obsSD;
  double betaV;
  double gammaV;
  double alphaV;
  double betaNV;
  double gammaNV;
  double alphaNV;

  public sensorModel(Utilities u, int no, Random rnO,
                     double sd, double bv, double gv, double av, double bnv, double gnv, double anv) {
    this.util = u;
    this.numberOfObjects = no;
    this.nd = new NormalDistribution(0, sd);
    this.rnObservation = rnO;
    this.obsSD = sd;
    this.betaV = bv;
    this.gammaV = gv;
    this.alphaV = av;
    this.betaNV = bnv;
    this.gammaNV = gnv;
    this.alphaNV = anv;
  }

  @Override
  public double probability(State observation, State state, Action action) {
    throw new RuntimeException("No specified probability lookup table");
  }

  public State realWorldExperimentObservation(String[] message, State s, Action a) {
    SearchState ns = (SearchState) s;
    List<Location> visionCone = util.locationsInVisionCone(ns, a);
    visionConeObservation newObservation = makeNullObservation(ns, visionCone);

    int objectIndex = 1;
    //int(the number of objects), id, cell_x, cell_y, id, cell_x, cell_y,..."
    for (int o = 0; o < Integer.parseInt(message[0]); o++) {
      int id = Integer.parseInt(message[objectIndex++]);
      int x = Integer.parseInt(message[objectIndex++]);
      int y = Integer.parseInt(message[objectIndex++]);

      for (int l = 0; l < visionCone.size(); l++) {
        if (visionCone.get(l).x == x && visionCone.get(l).y == y) {
          String strObj = "Obj" + Integer.toString(id + NAME_OFFSET);
          newObservation.observation.get(id).set(l, new objectObservation(strObj));
          break;
        }
      }
    }
    return newObservation;
  }

  public visionConeObservation makeNullObservation(SearchState ns, List<Location> vc) {
    visionConeObservation newObservation = new visionConeObservation();

    for (int o = 0; o < ns.searchableObjects.size(); o++) {
      List<objectObservation> locationObservation = new ArrayList();
      String strNotObj = "notObj" + Integer.toString(o + NAME_OFFSET);
      for (int l = 0; l < vc.size(); l++) {
        objectObservation newObjObservation = new objectObservation(strNotObj);
        locationObservation.add(newObjObservation);
      }
      newObservation.observation.add(locationObservation);
    }
    return newObservation;
  }

  @Override
  public State sample(State state, Action action) {
    SearchState ns = (SearchState) state;
    visionConeObservation newMultiCellObservation;

    if (action.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
      newMultiCellObservation = sampleLookAction(ns, action);
    } else {
      newMultiCellObservation = sampleMoveAction();
    }
    return newMultiCellObservation;
  }

  public visionConeObservation sampleMoveAction() {
    visionConeObservation newMultiCellObservation = new visionConeObservation();
    List<objectObservation> newMultiObjectObservation = new ArrayList<>();
    for (int i = 0; i < numberOfObjects; i++) {
      newMultiObjectObservation.add(new objectObservation("doNotCare"));
    }
    newMultiCellObservation.observation.add(newMultiObjectObservation);
    return newMultiCellObservation;
  }


  //note multipleObjectObservation & observation are switched!
  public visionConeObservation sampleLookAction(SearchState ns, Action action) {
    List<Location> visionCone = util.locationsInVisionCone(ns, action);
    //List<locationInVisionCone> visionCone = util.locationsInVisionBoundingBox(ns, observationAccuracy);
    visionConeObservation newMultiCellObservation = new visionConeObservation();

    for (int i = 0; i < numberOfObjects; i++) {
      String strObj = "Obj" + Integer.toString(i + NAME_OFFSET);
      String strNotObj = "notObj" + Integer.toString(i + NAME_OFFSET);
      SearchObject currentObject = ns.searchableObjects.get(i);

      List<objectObservation> newMultiObjectObservation = new ArrayList<>();
      //check if object is in vision cone
      boolean objectInVisionCone = false;
      for (Location vl : visionCone) {
        if (vl.x == currentObject.x && vl.y == currentObject.y) {
          objectInVisionCone = true;
          break;
        }
      }

      //sample object location
      if (!ns.hasChosen.get(i)) {
        if (objectInVisionCone){
          double roll = rnObservation.nextDouble();
          if (roll <= betaV) {
            newMultiObjectObservation = sampleEventB(objectInVisionCone, visionCone, rnObservation, strObj, strNotObj);
          } else if (betaV < roll && roll <= betaV + gammaV) {
            newMultiObjectObservation = sampleEventC(objectInVisionCone, visionCone, strNotObj);
          } else {
            newMultiObjectObservation = sampleEventA(objectInVisionCone, visionCone, rnObservation, strObj, strNotObj, currentObject);
          }
        } else {
          double roll = rnObservation.nextDouble();
          if (roll <= betaNV) {
            newMultiObjectObservation = sampleEventB(objectInVisionCone, visionCone, rnObservation, strObj, strNotObj);
          } else if (betaNV < roll && roll <= betaNV + gammaNV) {
            newMultiObjectObservation = sampleEventC(objectInVisionCone, visionCone, strNotObj);
          } else {
            newMultiObjectObservation = sampleEventA(objectInVisionCone, visionCone, rnObservation, strObj, strNotObj, currentObject);
          }
        }

      } else {
        for (Location vl : visionCone)
          newMultiObjectObservation.add(new objectObservation("doNotCare"));
      }
      newMultiCellObservation.observation.add(newMultiObjectObservation);
    }
    return newMultiCellObservation;
  }

  public List<objectObservation> sampleEventA(boolean objectInVisionCone, List<Location> visionCone, Random rand, String strObj, String strNotObj, SearchObject currentObject) {
    List<objectObservation> newMultiObjectObservation = new ArrayList<>();
    Map<Integer, Double> heatMap = new HashMap<>();

    if (!objectInVisionCone) {
      for (Location vl : visionCone)
        newMultiObjectObservation.add(new objectObservation(strNotObj));
      return newMultiObjectObservation;
    }

    double normalization = 0.0;
    for (int i = 0; i < visionCone.size(); i++) {
      double euclideanDistanceFromCenter1 = Math.sqrt(Math.pow(Math.abs(currentObject.x - visionCone.get(i).x), 2) + Math.pow(Math.abs(currentObject.y - visionCone.get(i).y), 2));
      double prob = nd.cumulativeProbability(-euclideanDistanceFromCenter1);
      heatMap.put(i, prob);
      normalization += prob;
    }

    for (int i = 0; i < visionCone.size(); i++) {
      heatMap.put(i, heatMap.get(i) / normalization);
    }

    double curSum = 0.0;
    double roll = rand.nextDouble();
    int selectedLocationIndex = -1;
    for (int i = 0; i < visionCone.size(); i++) {
      curSum += heatMap.get(i);
      if (roll <= curSum) {
        selectedLocationIndex = i;
        break;
      }
    }

    for (Location vl : visionCone)
      newMultiObjectObservation.add(new objectObservation(strNotObj));
    newMultiObjectObservation.set(selectedLocationIndex, new objectObservation(strObj));
    return newMultiObjectObservation;
  }

  public List<objectObservation> sampleEventB(boolean objectInVisionCone, List<Location> visionCone, Random rand, String strObj, String strNotObj) {
    List<objectObservation> newMultiObjectObservation = new ArrayList<>();
    Map<Integer, Double> heatMap = new HashMap<>();

    if (!objectInVisionCone) {
      for (Location vl : visionCone)
        newMultiObjectObservation.add(new objectObservation(strNotObj));
      return newMultiObjectObservation;
    }

    for (int i = 0; i < visionCone.size(); i++) {
      heatMap.put(i, 1.0 / visionCone.size());
    }

    double curSum = 0.0;
    double roll = rand.nextDouble();
    int selectedLocationIndex = -1;
    for (int i = 0; i < visionCone.size(); i++) {
      curSum += heatMap.get(i);
      if (roll <= curSum) {
        selectedLocationIndex = i;
        break;
      }
    }

    for (Location vl : visionCone)
      newMultiObjectObservation.add(new objectObservation(strNotObj));
    newMultiObjectObservation.set(selectedLocationIndex, new objectObservation(strObj));
    return newMultiObjectObservation;
  }

  public List<objectObservation> sampleEventC(boolean objectInVisionCone, List<Location> visionCone, String strNotObj) {
    List<objectObservation> newMultiObjectObservation = new ArrayList<>();
    for (Location vl : visionCone)
      newMultiObjectObservation.add(new objectObservation(strNotObj));
    return newMultiObjectObservation;
  }

  public double probabilityOfObservationPresent(String observationType, Location beliefCell, Location objectObservation, Integer sizeOfVisionCone) {
    if (observationType.equals("V")) {
      double euclideanDistanceFromCenter1 = Math.sqrt(Math.pow(Math.abs(objectObservation.x - beliefCell.x), 2) + Math.pow(Math.abs(objectObservation.y - beliefCell.y), 2));
      double prob = nd.cumulativeProbability(-euclideanDistanceFromCenter1);
      return (prob * alphaV) + ((1.0 / sizeOfVisionCone) * betaV);
    } else {
      double euclideanDistanceFromCenter1 = Math.sqrt(Math.pow(Math.abs(objectObservation.x - beliefCell.x), 2) + Math.pow(Math.abs(objectObservation.y - beliefCell.y), 2));
      double prob = nd.cumulativeProbability(-euclideanDistanceFromCenter1);
      return (prob * alphaNV) + ((1.0 / sizeOfVisionCone) * betaNV);
    }
  }

  public double probabilityOfObservationNOTPresent(String observationType) {
    if (observationType.equals("V")) {
      return gammaV;
    } else {
      return gammaNV;
    }
  }
}
