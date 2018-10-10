package searchWorld.src.Pomcp;

import searchWorld.src.State.SearchObject;
import searchWorld.src.State.SearchState;

import java.util.*;

/**
 * Created by awandzel on 7/17/18.
 */
public class factoredBelief {
    public Map<String, List<Location>> factoredObjectBelief;
    public SearchState candidateState;

    factoredBelief() {
      this.factoredObjectBelief = new TreeMap<>();
      this.candidateState = new SearchState();
    }

    factoredBelief(Map<String, List<Location>> fo, SearchState ns) {
      this.factoredObjectBelief = fo;
      this.candidateState = ns;
    }

  public factoredBelief copy(){
    return new factoredBelief(copyFactoredObjects(), this.candidateState.copy());

  }

  public Map<String, List<Location>> copyFactoredObjects(){
    Map<String, List<Location>> newFactoredObjects = new TreeMap<>();

    for (Map.Entry<String, List<Location>> obj : this.factoredObjectBelief.entrySet()) {
      newFactoredObjects.put(obj.getKey(), new ArrayList<>());
      List<Location> LS = newFactoredObjects.get(obj.getKey());

      for (Location l : obj.getValue()) {
        LS.add(new Location(l.x, l.y));
      }
    }

    return newFactoredObjects;
  }

  public SearchState sample(Random rn) {
    List<SearchObject> objects = new ArrayList<>();
    for (Map.Entry<String, List<Location>> obj : this.factoredObjectBelief.entrySet()) {
      Location hashedLocation = obj.getValue().get(rn.nextInt(obj.getValue().size()));
      objects.add(new SearchObject(hashedLocation.x, hashedLocation.y, obj.getKey()));
    }
    return new SearchState(this.candidateState.agent, objects, this.candidateState.hasChosen);
  }

  public void add(SearchState ns) {

    if (this.factoredObjectBelief.isEmpty()) {
      this.candidateState.agent = ns.agent.copy();
      this.candidateState.hasChosen = ns.copyHasChosen();

      for (int o = 0; o < ns.searchableObjects.size(); o++) {
        String objName = ns.searchableObjects.get(o).name;
        this.factoredObjectBelief.put(objName, new ArrayList<>());
      }

    } else{
      for (int o = 0; o < ns.searchableObjects.size(); o++) {
        String objName = ns.searchableObjects.get(o).name;
        this.factoredObjectBelief.get(objName).add(new Location(ns.searchableObjects.get(o).x, ns.searchableObjects.get(o).y));
      }
    }
  }

  public boolean isEmpty(){
      if (factoredObjectBelief.keySet().isEmpty()){
        return true;
      }
      for (List<Location> ls : factoredObjectBelief.values()){
        if (ls.isEmpty()){
          return true;
        }
      }
      return false;
  }

}