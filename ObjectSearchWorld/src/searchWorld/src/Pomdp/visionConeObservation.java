package searchWorld.src.Pomdp;

import burlap.mdp.core.state.State;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.statehashing.HashableState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static searchWorld.src.SearchDomain.VAR_OBSERVATION;

/**
 * Created by awandzel on 7/15/18.
 */
public class visionConeObservation implements State, HashableState {
  public List<List<objectObservation>> observation;

  public visionConeObservation(List<List<objectObservation>> a) {
    this.observation = a;
  }

  public visionConeObservation() {
    this.observation = new ArrayList<>();
  }

  private final static List<Object> keys = Collections.singletonList(VAR_OBSERVATION);

  @Override
  public List<Object> variableKeys() {
    return keys;
  }

  @Override
  public Object get(Object o) {
    if (o.equals(VAR_OBSERVATION)) {
      return observation;
    }
    throw new UnknownKeyException(o);
  }

  @Override
  public State copy() {
    List<List<objectObservation>> newVisionConeObservation = new ArrayList<>();

    for (int o = 0; o < observation.size(); o++) {
      List<objectObservation> locationObservation = new ArrayList();
      for (int l = 0; l < observation.get(o).size(); l++) {
        objectObservation newObjObservation = (objectObservation) this.observation.get(o).get(l).copy();
        locationObservation.add(newObjObservation);
      }
      newVisionConeObservation.add(locationObservation);
    }
    return new visionConeObservation(newVisionConeObservation);
  }

  @Override
  public String toString() {
    String str = "\n{";
    for (int i = 0; i < observation.size(); i++) {
      str += observation.get(i).toString() + "\n,";
    }
    str = str.substring(0, str.length() - 1);
    str += "}";
    return str;
  }

  @Override
  public State s() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true; // If they have the same address in memory
    if (obj == null || getClass() != obj.getClass()) return false;
    if (!(obj instanceof visionConeObservation)) return false;

    visionConeObservation that = (visionConeObservation) obj;
    for (int o = 0; o < observation.size(); o++) {
      for (int l = 0; l < observation.get(o).size(); l++) {
        if (!this.observation.get(o).get(l).equals(that.observation.get(o).get(l))) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 37;
    for (int o = 0; o < observation.size(); o++) {
      result += o;
      for (int l = 0; l < observation.get(o).size(); l++) {
        result *= this.observation.get(o).get(l).hashCode();
      }
    }
    return result;
  }
}
