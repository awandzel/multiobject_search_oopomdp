package searchWorld.src.Visualization;

import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.beliefstate.EnumerableBeliefState;
import searchWorld.src.State.SearchState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthurwandzel on 11/27/17.
 */
public class VizualizeBeliefState implements State {
  SearchState searchState;
  List<EnumerableBeliefState.StateBelief> stateBelief;

  public VizualizeBeliefState(SearchState s, List<EnumerableBeliefState.StateBelief> sb) {
      this.searchState = s;
      this.stateBelief = sb;
  }

  @Override
  public List<Object> variableKeys() {
    return null;
  }

  @Override
  public Object get(Object variableKey) {
    return null;
  }

  @Override
  public State copy() {
    return null;
  }
}
