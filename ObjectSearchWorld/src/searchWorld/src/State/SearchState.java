package searchWorld.src.State;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.statehashing.HashableState;
import searchWorld.src.Pomcp.Location;
//import sun.jvm.hotspot.types.basic.BasicOopField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static searchWorld.src.SearchDomain.*;

public class SearchState implements OOState, HashableState {

  public SearchAgent agent;
  public List<SearchObject> searchableObjects = new ArrayList<>();
  public List<Boolean> hasChosen = new ArrayList<>();

  public SearchState(SearchAgent a, List<SearchObject> to, List<Boolean> hc) {
    this.agent = a;
    this.searchableObjects = to;
    this.hasChosen = hc;
  }

  public SearchState(){};

  private static List<Object> keys = Arrays.asList(CLASS_AGENT, CLASS_OBJECTS, VAR_HAS_CHOSEN);

  @Override
  public int numObjects() {
    return searchableObjects.size();
  }

  public ObjectInstance object(String oName) {
    if (oName.equals(agent.name())) {
      SearchAgent nagent = this.agent.copy();
      return nagent;
    }

    int objectIndex = this.objectIndex(oName);
    if (objectIndex != -100) {
      SearchObject object = this.searchableObjects.get(objectIndex).copy();
      return object;
    }

    if (oName.equals(VAR_HAS_CHOSEN)){
      return (ObjectInstance) copyHasChosen();
    }

    throw new RuntimeException("Cannot find object: " + oName);
  }

  // List all object instances e.g. agent1
  public List<ObjectInstance> objects() {
    List<ObjectInstance> obs = new ArrayList<ObjectInstance>(1 + searchableObjects.size());
    obs.add(agent);
    obs.addAll(searchableObjects);
    return obs;
  }

  // List all object instances of class
  public List<ObjectInstance> objectsOfClass(String oClass) {
    if (oClass.equals(CLASS_AGENT)) {
      return Arrays.<ObjectInstance>asList(agent);
    } else if (oClass.equals(CLASS_OBJECTS)) {
      return new ArrayList<ObjectInstance>(searchableObjects);
    } else {
      throw new RuntimeException("Unknown class type: " + oClass + '\n');
    }
  }

  @Override
  public List<Object> variableKeys() {
    return keys;
  } // Keys of state e.g. VAR_X...

  @Override
  public Object get(Object variableKey) { // Query for particular object key e.g. VAR_X must be formatted as object_name:variable_key
    //OOVariableKey key = .generateKey(variableKey);

    if (!(variableKey instanceof String)) {
      throw new RuntimeException("Variable key must be a String.\n");
    }

    if (variableKey.equals(agent.name())) {
      return this.agent.get(variableKey);
    }

    int objectIndex = this.objectIndex((String) variableKey);
    if (objectIndex != -100) {
      SearchObject object = this.searchableObjects.get(objectIndex).copy();
      return object;
    }

    if (variableKey.equals(VAR_HAS_CHOSEN)) {
      return this.hasChosen;
    }

    throw new RuntimeException("Unable to get object " + variableKey.toString() + '\n');
  }

  @Override
  public SearchState copy() {
    return new SearchState(this.agent, this.searchableObjects, this.hasChosen);
  }

  public SearchState deepCopy() {return new SearchState(agent.copy(), copySearchableObjects(), copyHasChosen());}

  public SearchAgent touchAgent() {
    this.agent = agent.copy();
    return this.agent;
  }

  public List<Boolean> touchHasChosen() {
    this.hasChosen = this.copyHasChosen();
    return this.hasChosen;
  }

  public List<SearchObject> touchSearchableObjects(){
    this.searchableObjects = this.copySearchableObjects();
    return this.searchableObjects;
  }

  public List<Boolean> copyHasChosen() {
    ArrayList<Boolean> newA = new ArrayList<>(hasChosen.size());
    for (int i = 0; i < hasChosen.size(); i++) {
      Boolean newInt = false;
      if (hasChosen.get(i)) {
        newInt = true;
      }
      newA.add(newInt);
    }
    return newA;
  }

  public List<SearchObject> copySearchableObjects(){
    List<SearchObject> nso = new ArrayList<>();
    for (SearchObject o: searchableObjects){
      nso.add(o.copy());
    }
    return nso;
  }

  //Returns index of SearchObject in searchableObjects via name string matching
  public int objectIndex(String oname) {
    int ind = -100;
    for (int i = 0; i < searchableObjects.size(); i++) {
      if (searchableObjects.get(i).name().equals(oname)) {
        ind = i;
        break;
      }
    }
    return ind;
  }

  public String objectName(int oindex) {
    if (oindex < NAME_OFFSET) throw new RuntimeException("Index must be greater than " + String.valueOf(NAME_OFFSET) + '\n');
    return searchableObjects.get(oindex- NAME_OFFSET).name(); //shift by 1 because of 1-100 ordering
  }

  public int objectInLocation(Location l){
    for (int i = 0; i < searchableObjects.size(); i++){
      SearchObject o = searchableObjects.get(i);
      if (l.x == o.x && l.y == o.y && !hasChosen.get(i)){
        return i;
      }
    }
    return -1;
  }

  @Override
  public State s() {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true; // If they have the same address in memory
    if (o == null || getClass() != o.getClass()) return false;
    if (!(o instanceof SearchState)) return false;

    SearchState that = (SearchState) o;

    for (int i = 0; i < searchableObjects.size(); i++) {
      if (!this.searchableObjects.get(i).equals(that.searchableObjects.get(i))) {
        return false;
      }
    }
    for (int i = 0; i < hasChosen.size(); i++) {
      if (!this.hasChosen.get(i).equals(that.hasChosen.get(i))) {
        return false;
      }
    }
    return this.agent.equals(that.agent);
  }

  @Override
  public int hashCode() {
    int result = searchableObjects.hashCode();
    result = 37 * result + agent.hashCode();
    for (int i = 0; i < hasChosen.size(); i++) {
      if (this.hasChosen.get(i)) {
        result = 37 * result + (this.hasChosen.get(i) ? 1 : 0)*i;
      }
    }
    return result;
  }

  public String toStringHasChosen() {
    String str = "";
    for (int i = 0; i < hasChosen.size(); i++) {
      str += ", " + "Obj"+Integer.toString(i) + "=" + ((this.hasChosen.get(i)) ? ("true") : "false");
    }
    return str;
  }

  @Override
  public String toString() {
    return "{agent=" + agent.toString() + ", objectToFind=" + searchableObjects.toString() + toStringHasChosen() + '}';
  }
}
