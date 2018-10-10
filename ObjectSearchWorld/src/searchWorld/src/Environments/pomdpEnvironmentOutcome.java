package searchWorld.src.Environments;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;

/**
 * Created by awandzel on 2/16/18.
 */
public class pomdpEnvironmentOutcome extends EnvironmentOutcome {

  public State hiddenState;

  public pomdpEnvironmentOutcome(State o, Action a, State op, double r, boolean terminated, State underlying) {
    super(o, a, op, r, terminated);
    this.hiddenState = underlying;
  }
}
