package searchWorld.src.Pomcp;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import searchWorld.src.Pomdp.visionConeObservation;
import searchWorld.src.Pomdp.objectObservation;
import searchWorld.src.State.SearchObject;
import searchWorld.src.State.SearchState;

import java.util.*;

import static searchWorld.src.SearchDomain.ACTION_PARAMETERIZED_LOOK;

/**
 * Created by awandzel on 8/1/18.
 */
public class factoredObservation {

  //FactoredObservation
  public static visionConeObservation makeFactoredObservation(State observation, Action action) {
    visionConeObservation multiCell = (visionConeObservation) observation;

    visionConeObservation fo = new visionConeObservation();
    List<objectObservation> locationObservation = new ArrayList<>();

    if (action.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
      for (int l = 0; l < multiCell.observation.get(0).size(); l++) {
        boolean objectIsPresent = false;
        for (int o = 0; o < multiCell.observation.size(); o++) {
          if (multiCell.observation.get(o).get(l).objectPresent.startsWith("Obj")) {
            objectIsPresent = true;
            break;
          }
        }
        if (objectIsPresent) {
          locationObservation.add(new objectObservation("Obj"));
        } else {
          locationObservation.add(new objectObservation("notObj"));
        }

      }
    } else {
      locationObservation.add(new objectObservation("doNotCare"));
    }
    fo.observation.add(locationObservation);
    return fo;
  }

//  //FactoredObservation
//  public static visionConeObservation makeFactoredObservation(State observation, Action action) {
//    visionConeObservation multiCell = (visionConeObservation) observation;
//    visionConeObservation fo = new visionConeObservation();
//
//    for (int i = 0; i < multiCell.observation.size(); i++) {
//      List<objectObservation> multiObj = multiCell.observation.get(i);
//      Set observationSet = new HashSet();
//
//      for (int j = 0; j < multiObj.size(); j++) {
//        objectObservation obj = multiObj.get(j);
//        if (obj.objectPresent.startsWith("Obj")) {
//          observationSet.add("Obj");
//        } else if (obj.objectPresent.startsWith("notObj")) {
//          observationSet.add("notObj");
//        } else {
//          observationSet.add("doNotCare");
//        }
//      }
//
//      List<objectObservation> mo = new ArrayList<>();
//      if (observationSet.contains("Obj")){
//        mo.add(new objectObservation("Obj"));
//      } else if (observationSet.contains("notObj")) {
//        mo.add(new objectObservation("notObj"));
//      } else {
//        mo.add(new objectObservation("doNotCare"));
//      }
//      fo.observation.add(mo);
//    }
//    return fo;
//  }

//  //FactoredObservation
//  public static factoredObservation makeFactoredObservation(State observation, Action action) {
//    visionConeObservation multiCell = (visionConeObservation) observation;
//    factoredObservation fo = new factoredObservation();
//
//    for (int i = 0; i < multiCell.observation.size(); i++) {
//      multipleObjsObservation multiObj = multiCell.observation.get(i);
//      List<String> cellObservations = new ArrayList<>();
//      for (int j = 0; j < multiObj.multiObjectObservation.size(); j++) {
//        objectObservation obj = multiObj.multiObjectObservation.get(j);
//        if (obj.objectPresent.startsWith("Obj")) {
//          cellObservations.add("Obj");
//        } else if (obj.objectPresent.startsWith("notObj")) {
//          cellObservations.add("notObj");
//        } else {
//          cellObservations.add("doNotCare");
//        }
//      }
//      if (action.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
//        Collections.sort(cellObservations);
//      }
//
//      fo.factoredObjectObservation.add(cellObservations);
//    }
//    return fo;
//  }
}
