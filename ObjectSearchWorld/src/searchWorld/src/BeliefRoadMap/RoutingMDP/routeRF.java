package searchWorld.src.BeliefRoadMap.RoutingMDP;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import searchWorld.src.Pomcp.Location;

/**
 * Created by awandzel on 8/6/18.
 */
public class routeRF implements RewardFunction {

  Location goal;

  public routeRF(Location g) {this.goal = g;}


  @Override
  public double reward(State s, Action a, State sprime) {
    routeState ns = (routeState) s;

    if (ns.currentPosition.equals(goal)) return 100;
    else return -1;
  }
}
