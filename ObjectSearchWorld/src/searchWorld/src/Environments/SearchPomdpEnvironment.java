package searchWorld.src.Environments;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.environment.extensions.EnvironmentObserver;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.SimulatedPOEnvironment;
import burlap.mdp.singleagent.pomdp.beliefstate.TabularBeliefState;

import java.util.List;

import static searchWorld.src.SearchDomain.*;

/**
 * A Search Partially Observable Environments class
 */
public class SearchPomdpEnvironment extends SimulatedPOEnvironment {

  static double discountFactor = .95;
  static int searchPOEnvironmentPrint;
  public static void setPrint(int s) {searchPOEnvironmentPrint = s;}
  public static void setDiscountFactor(double df) {discountFactor = df;}

  PODomain domain;
  double discount = 1.0;

  /**
   * The constructor for SearchPomdpEnvironment
   *
   * @param domain       - A partially observable domain.
   * @param initialState - The initial state.
   */
  public SearchPomdpEnvironment(PODomain domain, State initialState) {
    super(domain, initialState);
    this.domain = domain;
  }

  @Override
  public EnvironmentOutcome executeAction(Action a) {

    for (EnvironmentObserver observer : this.observers) {
      observer.observeEnvironmentActionInitiation(this.currentObservation(), a);
    }

    State nextObservation = curObservation;

    EnvironmentOutcome eo;
    if (this.allowActionFromTerminalStates || !this.isInTerminalState()) {
      eo = model.sample(this.curState, a);
      nextObservation = poDomain.getObservationFunction().sample(eo.op, a);
    } else {
      eo = new EnvironmentOutcome(this.curState, a, this.curState.copy(), 0., true);
    }

    // Update reward and terminated
    this.lastReward = eo.r;
    this.terminated = eo.terminated;
    this.curState = eo.op;

    double discountedReward = eo.r * discount;
    discountedReward = Math.round(discountedReward * 1e3) / 1e3;
    discount *= discountFactor;

    // (this class handles the interaction between the state space and environment)
    if (searchPOEnvironmentPrint > 0) {
      System.out.print("Action: " + eo.a.toString() + "\n");
      System.out.println("Observation: " + nextObservation.toString());
      System.out.println("State: " + eo.op.toString());
      System.out.println("Reward: " + eo.r);
      System.out.println("Discounted Reward: " + discountedReward);
      if (eo.terminated) {
        System.out.println("TERMINATED");
      }
    } else {
      System.out.print(".");
    }

    //important: return instance of pomdpEnvironmentOutcome
    pomdpEnvironmentOutcome observedOutcome = new pomdpEnvironmentOutcome(this.curObservation, a, nextObservation, eo.r, this.terminated, eo.op);
    this.curObservation = nextObservation;

    for (EnvironmentObserver observer : this.observers) {
      observer.observeEnvironmentInteraction(observedOutcome);
    }

    return observedOutcome;
  }
}
