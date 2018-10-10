package searchWorld.src.Environments;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.environment.extensions.EnvironmentObserver;

import java.util.List;

import static searchWorld.src.SearchDomain.*;

/**
 * Class for the not partially observable (fully observable) Search Environments
 * Created by arthurwandzel on 7/10/17.
 */
public class SearchMdpEnvironment extends SimulatedEnvironment {

  static int searchEnvironmentPrint;

  public static void setPrint(int s) {
    searchEnvironmentPrint = s;
  }
  SADomain domain;

  /**
   * The constructor for the SearchMdpEnvironment
   *
   * @param domain       - A single agent domain.
   * @param initialState - The initial state.
   */
  public SearchMdpEnvironment(SADomain domain, State initialState) {
    super(domain, initialState);
    this.domain = domain;
  }

  @Override
  public EnvironmentOutcome executeAction(Action a) {
    for (EnvironmentObserver observer : this.observers) {
      observer.observeEnvironmentActionInitiation(this.currentObservation(), a);
    }

    EnvironmentOutcome eo;
    if (this.allowActionFromTerminalStates || !this.isInTerminalState()) {
      eo = model.sample(this.curState, a);
    } else {
      eo = new EnvironmentOutcome(this.curState, a, this.curState.copy(), 0., true);
    }

//    ActionType actionType1 = domain.getAction(ACTION_PARAMETERIZED_PICK);
//    List<Action> actions1 = actionType1.allApplicableActions(eo.op);
//    for (Action act : actions1) {
//      System.out.println(act.toString());
//    }
//    ActionType actionType2 = domain.getAction(ACTION_PARAMETERIZED_MOVE);
//    List<Action> actions2 = actionType2.allApplicableActions(eo.op);
//    for (Action act : actions2) {
//      System.out.println(act.toString());
//    }

    this.lastReward = eo.r;
    this.terminated = eo.terminated;
    this.curState = eo.op;

    if (searchEnvironmentPrint > 0) {
      System.out.print("Action: " + eo.a.toString() + "\n");
      System.out.println("State: " + eo.op.toString());
      System.out.println("Reward: " + eo.r); //reward received upon entering S'
      if (eo.terminated) {
        System.out.println("TERMINATED");
      }
    }

    for (EnvironmentObserver observer : this.observers) {
      observer.observeEnvironmentInteraction(eo);
    }

    return eo;
  }

}
