package searchWorld.src.Pomcp;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.oo.ObjectParameterizedActionType;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.beliefstate.EnumerableBeliefState;
import searchWorld.src.Pomdp.visionConeObservation;
import searchWorld.src.State.SearchState;

import java.util.*;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by awandzel on 7/16/18.
 */

public class OOPOMCP {
  public static PODomain DOMAIN;
  public static int SIMULATIONS;
  public static int MAX_DEPTH;
  public static int MAX_DEPTH_RANDOM_ROLLOUT = 25;
  public static double EXPLORATION;
  public static double DISCOUNT;
  public static int TRANSFORMS;
  public static int MAX_ATTEMPTS = TRANSFORMS;

  public static boolean REINVIGORATE = false;

  public static int MAX_DEPTH_REACHED = 0;
  public static boolean printDepth = false;
  public static int DEPTH;

  public objectDistribution objectDistribution;
  Random rnPomcp;

  //needs DOMAIN to take simulated actions for next nodes / observations
  public OOPOMCP(Random rn, PODomain pd, int sim, int h, double c, double d, boolean r, int ps, objectDistribution m) {
    this.DOMAIN = pd;
    this.SIMULATIONS = sim;
    this.MAX_DEPTH = h;
    this.EXPLORATION = c;
    this.DISCOUNT = d;
    this.REINVIGORATE = r;
    this.TRANSFORMS = ps;
    this.objectDistribution = m;
    this.rnPomcp = rn;
  }

  public OOPOMCP(PODomain pd) {
    this.DOMAIN = pd;
  }

  public VNode initializePOMCP(SearchState initialState, factoredBelief b) {
    DEPTH = 0;
    Action a = new ObjectParameterizedActionType.SAObjectParameterizedAction();
    State o = new visionConeObservation();
    VNode root = ExpandNode(initialState, a, o);
    root.B = b;
    return root;
  }

  public VNode ExpandNode(State s, Action a, State o) {
    VNode vn = new VNode(a, o, 0.0);
    vn.initializeVNode(s);
    return vn;
  }

  public Action SelectAction(VNode root) {
    List<SearchState> sampledParticles = new ArrayList<>();
    for (int i = 0; i < OOPOMCP.SIMULATIONS; i++) {
      sampledParticles.add(root.B.sample(rnPomcp));
    }

    for (int i = 0; i < OOPOMCP.SIMULATIONS; i++) {
      OOPOMCP.DEPTH = 0;
      root.SimulateV(sampledParticles.get(i));
    }
    if (printDepth) System.out.println(OOPOMCP.MAX_DEPTH_REACHED);

    return root.GreedyUCB(false); //selects greedy action
  }

  public Action SelectRandomAction(State s) {
    List<Action> actions = new ArrayList<>();
    for (ActionType at : DOMAIN.getActionTypes()) {
      for (Action act : at.allApplicableActions(s)) {
        actions.add(act);
      }
    }
    return actions.get(rnPomcp.nextInt(actions.size()));
  }

  public VNode Update(SearchState ns, Action a, State o, double observationAccuracy, VNode root) {
    VNode.QNode qn = root.children.get(a);
    VNode vn = qn.children.get(o);

    if (vn == null) {
      qn.children.put(o, new VNode(a, o, 0.0));
      vn = qn.children.get(o);
    }

    SearchState rs = new SearchState();
    if (!vn.B.isEmpty()) {
      rs = vn.B.sample(rnPomcp);
    } else {
      return new VNode(a, o, -1.0);
    }

    if (REINVIGORATE) {
      AddTransforms(root, vn, a, o);
    }

    return initializePOMCP(rs, vn.B.copy());
  }

  public void AddTransforms(VNode root, VNode vn, Action a, State o) {
    int attempts = 0, added = 0;

    // Local transformations of state that are consistent with history
    while (added < OOPOMCP.TRANSFORMS && attempts < OOPOMCP.MAX_ATTEMPTS) {
      SearchState t = CreateTransform(root, a, (visionConeObservation) o);
      if (t != null) {
        vn.B.add(t); //add s' to B'
        added++;
      }
      attempts++;
    }
  }

  SearchState CreateTransform(VNode root, Action a, visionConeObservation o) {
    //sample s from B
    SearchState s = root.B.sample(rnPomcp);
    EnvironmentOutcome eo = OOPOMCP.DOMAIN.getModel().sample(s, a);

    //run model to get s'
    SearchState nextState = (SearchState) eo.op;
    SearchState transformedState = objectDistribution.sampleHeatMap(nextState);

    //ensures that transformation is consistent with last real observation
    if (a.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
      visionConeObservation nextObservation = (visionConeObservation) OOPOMCP.DOMAIN.getObservationFunction().sample(transformedState, a);
      if (nextObservation.equals(o)) {
        return transformedState;
      } else {
        return null;
      }
    }
    return transformedState;
  }

  public List<EnumerableBeliefState.StateBelief> printAndSaveBeliefs(VNode vn) {

    //For visualizer
    List<SearchState> sampledB = new ArrayList<>();
    if (!vn.B.isEmpty()){
      for (int i = 0; i < 10000; i++) {
        sampledB.add(vn.B.sample(rnPomcp));
      }
    }

    Map<SearchState, Double> stateCount = new HashMap<>();
    List<EnumerableBeliefState.StateBelief> beliefState = new ArrayList<>();
    for (int i = 0; i < sampledB.size(); i++) {
      Double count = stateCount.get(sampledB.get(i));
      if (count == null) {
        stateCount.put(sampledB.get(i), 1.0);
      } else {
        stateCount.put(sampledB.get(i), count + 1.0);
      }
    }
    for (Map.Entry<SearchState, Double> entry : stateCount.entrySet()) {
      double belief = stateCount.get(entry.getKey()) / sampledB.size();
      beliefState.add(new EnumerableBeliefState.StateBelief(entry.getKey(), belief));
    }

    //print object locations foreach object
    Map<String, Map<Location, Double>> stateCountObject = new TreeMap<>();
    System.out.println("\nBeliefs particles: " + Integer.toString(stateCount.size()));
    for (Map.Entry<String, List<Location>> entry : vn.B.factoredObjectBelief.entrySet()) {
      Map<Location, Double> objectCount = new TreeMap<>();

      for (Location l : entry.getValue()) {
        if (!objectCount.containsKey(l)) {
          objectCount.put(l, 1.0);
        } else {
          Double count = objectCount.get(l);
          objectCount.put(l, count + 1.0);
        }
      }

      stateCountObject.put(entry.getKey(), objectCount);
    }

    for (Map.Entry<String, Map<Location, Double>> object : stateCountObject.entrySet()){
      for (Map.Entry<Location, Double> location : object.getValue().entrySet()){
        double belief = location.getValue()/vn.B.factoredObjectBelief.get(object.getKey()).size();
        System.out.println(Double.toString(location.getValue()) + ": " + object.getKey() + ", " + location.getKey().toString() + ": " + String.format("%.03f", belief) + ";");
      }
    }

    return beliefState;
  }


  public class VNode {
    private Action prevAction;
    private State observation;
    private List<Double> V;
    public factoredBelief B;
    public double rewardTotal;


    private Map<Action, QNode> children;

    public VNode(Action a, State o, double rt) {
      this.prevAction = a;
      this.observation = o;
      this.rewardTotal = rt;
      this.V = new ArrayList<>();
      this.B = new factoredBelief();
      this.children = new HashMap<>();
    }

    public void initializeVNode(State s) {

      //generate legal actions
      for (ActionType at : DOMAIN.getActionTypes()) {
        for (Action act : at.allApplicableActions(s)) {
          QNode qn = new QNode(act);

//          if (OOPOMCP.PREFERRED_ACTIONS) {
//            if (act.actionName().equals("move")) {
//              String[] params = ((ObjectParameterizedAction) act).getObjectParameters();
//              List<Integer> ln = util.actWithinBounds((SearchState) s, util.directionStoI(params[0]));
//              qn.V.add(1 / (objectDistribution.l1(ln) + 1));
//            }
//          }

//          qn.initializeQNode();
          this.children.put(act, qn);
        }
      }
    }

    public class QNode {
      private Action nextAction;
      private List<Double> V;
      private Map<State, VNode> children;

      public QNode(Action a) {
        this.nextAction = a;
        this.V = new ArrayList<>();
        this.children = new HashMap<>();
      }

//      public void initializeQNode() {
//        //Note: observation access depends on implementation
//        List<State> observations = ((DiscreteObservationFunction) DOMAIN.getObservationFunction()).allObservations();
//        for (State o : observations) {
//          this.children.put(o, new VNode(this.nextAction, o, 0.0));
//        }
//      }
    }

    Action GreedyUCB(boolean ucb) {

      //if debug save q-values
      double maxQ = Double.NEGATIVE_INFINITY;
      Action action = new ObjectParameterizedActionType.SAObjectParameterizedAction();

      for (QNode qn : this.children.values()) {

        double sum = 0;
        for (Double num : qn.V) sum += num;
        Double qvalue = (qn.V.isEmpty()) ? 0 : sum / qn.V.size();

        //add UCB bonus for exploration
        if (ucb) {
          if (qn.V.size() == 0)
            qvalue = Double.POSITIVE_INFINITY;
          else
            qvalue += OOPOMCP.EXPLORATION * Math.sqrt((Math.log(this.V.size() + 1) / qn.V.size()));
        }

        if (qvalue >= maxQ) {
          action = qn.nextAction;
          maxQ = qvalue;
        }
      }

      return action;
    }

    Double SimulateV(State s) {
      if (OOPOMCP.DEPTH >= OOPOMCP.MAX_DEPTH) {
        return 0.0;
      }

      Action action = GreedyUCB(true);

      //add state to B'(s)
      if (OOPOMCP.DEPTH == 1) {
        SearchState ns = (SearchState) s;
        this.B.add(ns);
      }

      QNode qn = this.children.get(action);
      double Qvalue = this.SimulateQ(s, qn, action);
      Qvalue = Math.round(Qvalue * 1e3) / 1e3;
      this.V.add(Qvalue);

      return Qvalue;
    }

    Double SimulateQ(State s, QNode qn, Action a) {
      double delayedReward = 0.0;

      EnvironmentOutcome eo = OOPOMCP.DOMAIN.getModel().sample(s, a);
      State o = OOPOMCP.DOMAIN.getObservationFunction().sample(eo.op, a);

      VNode vn = qn.children.get(o);
      //use only what is sampled not whole space
      if (vn == null) {
        qn.children.put(o, new VNode(a, o, 0.0));
        vn = qn.children.get(o);
      }

      if (!eo.terminated && vn.children.isEmpty() && qn.V.size() >= 1) {
        vn = ExpandNode(eo.op, a, o);
        qn.children.put(o, vn);
      }

      if (!eo.terminated) {
        OOPOMCP.DEPTH++;
        if (OOPOMCP.DEPTH > MAX_DEPTH_REACHED) MAX_DEPTH_REACHED = OOPOMCP.DEPTH; //debug code
        if (!vn.children.isEmpty()) {
          delayedReward = vn.SimulateV(eo.op);
        } else {
          delayedReward = vn.Rollout(eo.op);
        }
        OOPOMCP.DEPTH--;
      }

      double totalReward = eo.r + OOPOMCP.DISCOUNT * delayedReward;
      totalReward = Math.round(totalReward * 1e3) / 1e3;
      qn.V.add(totalReward);
      return totalReward;
    }

    double Rollout(State s) {
      return issueRollout(s, 0, 0, 1.0, false);
    }

    double issueRollout(State s, double r, int depth, double discount, boolean terminated) {
      if (depth >= OOPOMCP.MAX_DEPTH_RANDOM_ROLLOUT || terminated) {
        return r;
      }
      Action a = SelectRandomAction(s);
      EnvironmentOutcome eo = OOPOMCP.DOMAIN.getModel().sample(s, a);
      r += eo.r * discount;
      r = Math.round(r * 1e3) / 1e3;
      discount *= OOPOMCP.DISCOUNT;
      return issueRollout(s, r, ++depth, discount, eo.terminated);
    }
  }
}
