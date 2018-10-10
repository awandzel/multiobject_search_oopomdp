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

/**
 * Created by awandzel on 7/16/18.
 */

public class OOOOPOMCP {
  public static PODomain DOMAIN;
  public static int SIMULATIONS;
  public static int MAX_DEPTH;
  public static int MAX_DEPTH_RANDOM_ROLLOUT = 25;
  public static double EXPLORATION;
  public static double DISCOUNT;
  public static int TRANSFORMS ;
  public static int MAX_ATTEMPTS = TRANSFORMS;

  public static boolean REINVIGORATE = false;

  public static int MAX_DEPTH_REACHED = 0;
  public static boolean printDepth = false;
  public static int DEPTH;

  public objectDistribution objectDistribution;
  public Random rnPomcp;

  //needs DOMAIN to take simulated actions for next nodes / observations
  public OOOOPOMCP(Random rn, PODomain pd, int sim, int h, double c, double d, boolean r, int ps, objectDistribution fbe) {
    this.DOMAIN = pd;
    this.SIMULATIONS = sim;
    this.MAX_DEPTH = h;
    this.EXPLORATION = c;
    this.DISCOUNT = d;
    this.REINVIGORATE = r;
    this.TRANSFORMS = ps;
    this.objectDistribution = fbe;
    this.rnPomcp = rn;
  }

  public OOOOPOMCP(PODomain pd) {
    this.DOMAIN = pd;
  }

  public VNode initializePOMCP(SearchState initialState, List<SearchState> b) {
    DEPTH = 0;
    Action a = new ObjectParameterizedActionType.SAObjectParameterizedAction();
    visionConeObservation o = new visionConeObservation();
    VNode root = ExpandNode(initialState, a, o);
    root.B = b;
    return root;
  }

  public VNode ExpandNode(State s, Action a, visionConeObservation o) {
    VNode vn = new VNode(a, o, 0.0);
    vn.initializeVNode(s);
    return vn;
  }

  public Action SelectAction(VNode root) {
    for (int i = 0; i < OOOOPOMCP.SIMULATIONS; i++) {
      OOOOPOMCP.DEPTH = 0;
      SearchState s = root.B.get(rnPomcp.nextInt(root.B.size()));
      root.SimulateV(s);
    }
    if (printDepth) System.out.println(OOOOPOMCP.MAX_DEPTH_REACHED);

    return root.GreedyUCB(false); //selects greedy action
  }

//  public Action SelectAction(VNode root) {
//    List<SearchState> sampledParticles = new ArrayList<>();
//    for (int i = 0; i < OOOOPOMCP.SIMULATIONS; i++) {
//      sampledParticles.add(root.B.sample());
//    }
//
//    for (int i = 0; i < OOOOPOMCP.SIMULATIONS; i++) {
//      OOOOPOMCP.DEPTH = 0;
//      root.SimulateV(sampledParticles.get(i));
//    }
//    if (printDepth) System.out.println(OOOOPOMCP.MAX_DEPTH_REACHED);
//
//    return root.GreedyUCB(false); //selects greedy action
//  }

  public Action SelectRandomAction(State s) {
    List<Action> actions = new ArrayList<>();
    for (ActionType at : DOMAIN.getActionTypes()) {
      for (Action act : at.allApplicableActions(s)) {
        actions.add(act);
      }
    }
    return actions.get(rnPomcp.nextInt(actions.size()));
  }

  public VNode Update(SearchState currentState, Action a, State o, VNode vn) {
    objectDistribution.beliefUpdate(currentState, a, o);
    List<SearchState> newBelief = objectDistribution.POMCPSample(currentState, OOOOPOMCP.SIMULATIONS);
    return initializePOMCP(objectDistribution.sampleHeatMap(currentState), newBelief);
  }

  public List<EnumerableBeliefState.StateBelief> printAndSaveBeliefs(VNode vn) {
    factoredBelief fb = new factoredBelief();

    //For visualizer
    if (!vn.B.isEmpty()) {
      for (int i = 0; i < vn.B.size(); i++) {
        fb.add(vn.B.get(i));
      }
    }

    Map<SearchState, Double> stateCount = new HashMap<>();
    List<EnumerableBeliefState.StateBelief> beliefState = new ArrayList<>();
    for (int i = 0; i < vn.B.size(); i++) {
      Double count = stateCount.get(vn.B.get(i));
      if (count == null) {
        stateCount.put(vn.B.get(i), 1.0);
      } else {
        stateCount.put(vn.B.get(i), count + 1.0);
      }
    }
    for (Map.Entry<SearchState, Double> entry : stateCount.entrySet()) {
      double belief = stateCount.get(entry.getKey()) / vn.B.size();
      beliefState.add(new EnumerableBeliefState.StateBelief(entry.getKey(), belief));
    }

    //print object locations foreach object
    Map<String, Map<Location, Double>> stateCountObject = new TreeMap<>();
    System.out.println("\nBeliefs particles: " + Integer.toString(stateCount.size()));
    for (Map.Entry<String, List<Location>> entry : fb.factoredObjectBelief.entrySet()) {
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

    for (Map.Entry<String, Map<Location, Double>> object : stateCountObject.entrySet()) {
      for (Map.Entry<Location, Double> location : object.getValue().entrySet()) {
        double belief = location.getValue() / fb.factoredObjectBelief.get(object.getKey()).size();
        System.out.println(Double.toString(location.getValue()) + ": " + object.getKey() + ", " + location.getKey().toString() + ": " + String.format("%.03f", belief) + ";");
      }
    }

    return beliefState;
  }


  public class VNode {
    private Action prevAction;
    private visionConeObservation observation;
    private List<Double> V;
    public List<SearchState> B;
    public double rewardTotal;


    private Map<Action, QNode> children;

    public VNode(Action a, visionConeObservation o, double rt) {
      this.prevAction = a;
      this.observation = o;
      this.rewardTotal = rt;
      this.V = new ArrayList<>();
      this.B = new ArrayList<>();
      this.children = new HashMap<>();
    }

    public void initializeVNode(State s) {

      //generate legal actions
      for (ActionType at : DOMAIN.getActionTypes()) {
        for (Action act : at.allApplicableActions(s)) {
          QNode qn = new QNode(act);

//          qn.initializeQNode();
          this.children.put(act, qn);
        }
      }
    }

    public class QNode {
      private Action nextAction;
      private List<Double> V;
      private Map<visionConeObservation, VNode> children;

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
            qvalue += OOOOPOMCP.EXPLORATION * Math.sqrt((Math.log(this.V.size() + 1) / qn.V.size()));
        }

        if (qvalue >= maxQ) {
          action = qn.nextAction;
          maxQ = qvalue;
        }
      }

      return action;
    }

    Double SimulateV(State s) {
      if (OOOOPOMCP.DEPTH >= OOOOPOMCP.MAX_DEPTH) {
        return 0.0;
      }

      Action action = GreedyUCB(true);

//      //add state to B'(s)
//      if (OOOOPOMCP.DEPTH == 1) {
//        SearchState ns = (SearchState) s;
//        this.B.add(ns);
//      }

      QNode qn = this.children.get(action);
      double Qvalue = this.SimulateQ(s, qn, action);
      Qvalue = Math.round(Qvalue * 1e3) / 1e3;
      this.V.add(Qvalue);

      return Qvalue;
    }

    Double SimulateQ(State s, QNode qn, Action a) {
      double delayedReward = 0.0;

      EnvironmentOutcome eo = OOOOPOMCP.DOMAIN.getModel().sample(s, a);
      State o = OOOOPOMCP.DOMAIN.getObservationFunction().sample(eo.op, a);
      visionConeObservation fo = factoredObservation.makeFactoredObservation(o, a);

      VNode vn = qn.children.get(fo);
      //use only what is sampled not whole space
      if (vn == null) {
        qn.children.put(fo, new VNode(a, fo, 0.0));
        vn = qn.children.get(fo);
      }

      if (!eo.terminated && vn.children.isEmpty() && qn.V.size() >= 1) {
        vn = ExpandNode(eo.op, a, fo);
        qn.children.put(fo, vn);
      }

      if (!eo.terminated) {
        OOOOPOMCP.DEPTH++;
        if (OOOOPOMCP.DEPTH > MAX_DEPTH_REACHED) MAX_DEPTH_REACHED = OOOOPOMCP.DEPTH; //debug code
        if (!vn.children.isEmpty()) {
          delayedReward = vn.SimulateV(eo.op);
        } else {
          delayedReward = vn.Rollout(eo.op);
        }
        OOOOPOMCP.DEPTH--;
      }

      double totalReward = eo.r + OOOOPOMCP.DISCOUNT * delayedReward;
      totalReward = Math.round(totalReward * 1e3) / 1e3;
      qn.V.add(totalReward);
      return totalReward;
    }

    double Rollout(State s) {
      return issueRollout(s, 0, 0, 1.0, false);
    }

    double issueRollout(State s, double r, int depth, double discount, boolean terminated) {
      if (depth >= OOOOPOMCP.MAX_DEPTH_RANDOM_ROLLOUT || terminated) {
        return r;
      }
      Action a = SelectRandomAction(s);
      EnvironmentOutcome eo = OOOOPOMCP.DOMAIN.getModel().sample(s, a);
      r += eo.r * discount;
      r = Math.round(r * 1e3) / 1e3;
      discount *= OOOOPOMCP.DISCOUNT;
      return issueRollout(s, r, ++depth, discount, eo.terminated);
    }
  }
}
