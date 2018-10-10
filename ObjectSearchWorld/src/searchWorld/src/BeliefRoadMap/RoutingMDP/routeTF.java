package searchWorld.src.BeliefRoadMap.RoutingMDP;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import searchWorld.src.Pomcp.Location;

/**
 * Created by awandzel on 8/6/18.
 */
public class routeTF implements TerminalFunction {

  Location goal;

  public routeTF(Location g) {
    this.goal = g;
  }


  @Override
  public boolean isTerminal(State s) {
    routeState ns = (routeState) s;
    return ns.currentPosition.equals(goal);
  }
}
