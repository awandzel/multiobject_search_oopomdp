package searchWorld.src.BSS;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.pomdp.BeliefPolicyAgent;
import burlap.mdp.core.action.Action;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.beliefstate.BeliefState;
import burlap.mdp.singleagent.pomdp.beliefstate.EnumerableBeliefState;
import burlap.mdp.singleagent.pomdp.beliefstate.TabularBeliefState;
import searchWorld.src.Environments.pomdpEnvironmentOutcome;
import searchWorld.src.Environments.pomdpEpisode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Belief Search Agent class
 */
public class BeliefSearchAgent extends BeliefPolicyAgent {

  static int beliefSearchAgentPrint;

  public static void setPrint(int s) {
    beliefSearchAgentPrint = s;
  }

  private int count = 0; // Counts how many times the method getAction has been called
  private double DISCOUNT; //BSS Discount

  /**
   * The constructor for BeliefSearchAgent
   *
   * @param poDomain    - The partially observable DOMAIN.
   * @param environment - The environment.
   * @param policy      - The policy.
   */
  public BeliefSearchAgent(PODomain poDomain, Environment environment, Policy policy, double dis) {
    super(poDomain, environment, policy);
    this.DISCOUNT = dis;
  }

  // Iterate over nonZeroBeliefs. Extract StateBelief: <s,belief> then print.
  // Investigate which S attributes are enumerated
  @Override
  public Action getAction(BeliefState curBelief) {
    // Calls a super method that returns the action, using the policy and based on the current
    // belief.
    Action a = super.getAction(curBelief);
    TabularBeliefState tbs = (TabularBeliefState) curBelief;
    List<String> currentBeliefs = new ArrayList<>();

    // String builders to build up our current beliefs
    StringBuilder printBeliefs = new StringBuilder("Belief particles: " + Integer.toString(tbs.nonZeroBeliefs().size()) + "\n");

    // Adding beliefs to respective strings
    for (EnumerableBeliefState.StateBelief sb : tbs.nonZeroBeliefs())
      currentBeliefs.add(sb.s.toString() + ": " + String.format("%.03f", sb.belief) + ";  \n");

    Collections.sort(currentBeliefs);
    for (String b : currentBeliefs) printBeliefs.append(b);

    // Print statements for our beliefs.
    if (beliefSearchAgentPrint > 0) {
      //	System.out.println(printEliminatedBeliefs);
      System.out.println(printBeliefs);
      System.out.println("======stepCount:" + count + "======");
    }
    count++;

    return a;
  }

  @Override
  public Episode actUntilTerminalOrMaxSteps(int maxSteps) {
    pomdpEpisode ea = new pomdpEpisode();
    int c = 0;
    double discount = 1.0;
    while (!this.environment.isInTerminalState() && c < maxSteps) {
      //long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      Action ga = this.getAction(this.curBelief);
      EnvironmentOutcome eo = environment.executeAction(ga);

      double discountedReward = eo.r * discount;
      discount *= DISCOUNT;

      ea.transition(ga, eo.op, discountedReward, ((pomdpEnvironmentOutcome) eo).hiddenState);

      //update our belief
      this.curBelief = this.updater.update(this.curBelief, eo.op, eo.a);
      ea.beliefSequence.add(((TabularBeliefState) curBelief).nonZeroBeliefs());

      c++;
//      long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//      System.out.println(afterUsedMem);
    }

    return ea;
  }
}
