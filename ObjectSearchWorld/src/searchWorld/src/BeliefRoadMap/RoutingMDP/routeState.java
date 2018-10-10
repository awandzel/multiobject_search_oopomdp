package searchWorld.src.BeliefRoadMap.RoutingMDP;

import burlap.mdp.core.state.State;
import burlap.statehashing.HashableState;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.State.SearchAgent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by awandzel on 8/6/18.
 */
public class routeState implements State, HashableState {

  public Location currentPosition;

  public routeState(Location a) {
    this.currentPosition = a;
  }

  private static List<Object> keys = Arrays.asList("Location");

  @Override
  public List<Object> variableKeys() {
    return keys;
  }

  @Override
  public Object get(Object variableKey) {
    if (!(variableKey instanceof String)) {
      throw new RuntimeException("Variable key must be a String.\n");
    }
    if (variableKey.equals("Location")) {
      return currentPosition;
    }
    throw new RuntimeException("Unable to get object " + variableKey.toString() + '\n');
  }

  @Override
  public routeState copy() {
    return new routeState(new Location(currentPosition.x, currentPosition.y));
  }

  @Override
  public State s() {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true; // If they have the same address in memory
    if (o == null || getClass() != o.getClass()) return false;
    if (!(o instanceof routeState)) return false;

    routeState that = (routeState) o;

    return this.currentPosition.equals(that.currentPosition);
  }

  public Location touchCurrentPosition() {
    this.currentPosition = currentPosition.copy();
    return this.currentPosition;
  }

  @Override
  public int hashCode() {
    return currentPosition.hashCode();
  }

  @Override
  public String toString() {
    return "{CurrentLocation=" + currentPosition.toString() + "}";
  }
}

