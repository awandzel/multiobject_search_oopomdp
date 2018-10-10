package searchWorld.src.Environments;

import burlap.behavior.singleagent.Episode;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.beliefstate.EnumerableBeliefState;
import searchWorld.src.State.SearchState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by awandzel on 2/16/18.
 */

public class pomdpEpisode extends Episode {
  public List<State> hiddenStateSequence = new ArrayList<>();
  public List<List<EnumerableBeliefState.StateBelief>> beliefSequence = new ArrayList<>();


  public void transition(Action usingAction, State nextObservation, double r, State nextState) {
    stateSequence.add(nextObservation);
    actionSequence.add(usingAction);
    rewardSequence.add(r);
    hiddenStateSequence.add(nextState);
  }
}