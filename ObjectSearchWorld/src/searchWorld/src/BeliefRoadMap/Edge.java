package searchWorld.src.BeliefRoadMap;

import burlap.behavior.policy.Policy;
import burlap.mdp.core.action.Action;
import searchWorld.src.Pomcp.Location;

import java.util.List;

/**
 * Created by awandzel on 7/30/18.
 */
public class Edge {
  public Location traversableLocation;
  public List<Action> primitiveActionPolicy;

  Edge(Location l, List<Action> p) {this.traversableLocation = l; this.primitiveActionPolicy = p;}
}
