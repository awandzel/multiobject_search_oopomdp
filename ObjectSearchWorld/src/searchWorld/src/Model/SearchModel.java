package searchWorld.src.Model;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;
//import lavaWorld.State.LavaAgent;
//import lavaWorld.State.LavaSmoke;
//import lavaWorld.State.SearchState;
import burlap.mdp.core.state.State;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.State.SearchAgent;
import searchWorld.src.State.SearchObject;
import searchWorld.src.State.SearchState;
import searchWorld.src.State.Utilities;

//import static lavaWorld.LavaDomain.*;
import static searchWorld.src.SearchDomain.*;

import java.util.*;

/**
 * The Search Model class
 * Created by arthurwandzel on 7/6/17.
 */
public class SearchModel implements FullStateModel {

  Utilities util;

  /**
   * The constructor for the Search Model.
   */
  public SearchModel(Utilities u) {
    this.util = u;
  }

  @Override
  public State sample(State s, Action a) {
    List<StateTransitionProb> stpList = this.stateTransitions(s, a);
    return stpList.get(0).s; //deterministic transitions

//    Random rand = new Random();
//    double roll = rand.nextDouble();
//    double curSum = 0.;
//
//    for (StateTransitionProb aStpList : stpList) {
//      curSum += aStpList.p;
//      if (roll < curSum) {
//        return aStpList.s;
//      }
//    }
//    throw new RuntimeException("Probabilities don't sum to 1.0: " + curSum);
  }

  @Override
  public List<StateTransitionProb> stateTransitions(State s, Action a) {
    List<StateTransitionProb> tps = new ArrayList<StateTransitionProb>();
    SearchState ns = ((SearchState) s).copy();
    String[] params = ((ObjectParameterizedAction) a).getObjectParameters();

    if (a.actionName().equals(ACTION_PARAMETERIZED_MOVE) || a.actionName().equals(ACTION_PARAMETRIZED_MOVE_ROOM)) {
      //return move(ns, util.directionStoI(params[0]));
      return move(ns, Integer.parseInt(params[0]), Integer.parseInt(params[1]));
    }

    // For look actions
    else if (a.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
      tps.add(new StateTransitionProb(ns, 1.));
      return tps;
    }

    // For pick actions Obj1 1 0
    else if (a.actionName().startsWith(ACTION_PARAMETERIZED_PICK)) {
      List<Boolean> hasChosen = ns.touchHasChosen();

//     hasChosen.set(ns.objectIndex(params[0]), true);
      Location pickLocation = new Location(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
      if (ns.objectInLocation(pickLocation)!= -1) {
        hasChosen.set(ns.objectInLocation(pickLocation), true);
      } else {
        for (int i = 0; i < ns.searchableObjects.size(); i++) {
          if (!ns.hasChosen.get(i)) {
            hasChosen.set(i, true);
            break;
          }
        }
      }

      tps.add(new StateTransitionProb(ns, 1.));
      return tps;
    }

    throw new RuntimeException("Action was not Properly Selected");
  }

  private List<StateTransitionProb> move(SearchState ns, int nx, int ny) {
    List<StateTransitionProb> tps = new ArrayList<>();

    // Change Agent
    SearchAgent nAgent = ns.touchAgent();
    nAgent.x = nx;
    nAgent.y = ny;

    tps.add(new StateTransitionProb(ns, 1.));
    return tps;
  }

  /**
   * @return - A list of state transition probs which will always be 1 for moving, which also
   * contains the next state.
   */
//  private List<StateTransitionProb> move(SearchState ns, int at) {
//    List<StateTransitionProb> tps = new ArrayList<StateTransitionProb>();
//
//    List<Integer> action = util.actWithinBounds(ns, at);
//
//    int nx = action.get(0);
//    int ny = action.get(1);
//
//    // Change Agent
//    SearchAgent nAgent = ns.touchAgent();
//    nAgent.x = nx;
//    nAgent.y = ny;
//
//    tps.add(new StateTransitionProb(ns, 1.));
//    return tps;
//  }

//  private List<StateTransitionProb> moveRoom(SearchState ns, String room) {
//    List<StateTransitionProb> tps = new ArrayList<StateTransitionProb>();
//
//    Integer roomIndex = util.roomAbstractions.roomIndex(room);
//    Location transition = util.roomAbstractions.transitionMatrix.get(roomIndex);
//
//    //Change Agent
//    SearchAgent nAgent = ns.touchAgent();
//    nAgent.x = transition.x;
//    nAgent.y = transition.y;
//
//    tps.add(new StateTransitionProb(ns, 1.));
//    return tps;
//  }
}