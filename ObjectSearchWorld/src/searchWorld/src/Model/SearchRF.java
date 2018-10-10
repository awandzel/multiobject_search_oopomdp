package searchWorld.src.Model;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.State.SearchState;
import searchWorld.src.State.Utilities;

import static searchWorld.src.SearchDomain.*;


/**
 * The Search Reward Function class
 */
public class SearchRF implements RewardFunction {

  private double goalReward;
  private double moveReward;
  private double pickReward;
  private double moveRoomReward;
  private double lookReward;

  public Utilities util;

  /**
   * The constructor for SearchRF
   *
   * @param goalReward - The goal reward.
   * @param moveReward - The move reward.
   * @param pickReward - The reward for picking.
   */
  public SearchRF(Utilities u, double goalReward, double moveReward, double pickReward, double moveRoomReward, double lookReward) {
    this.goalReward = goalReward;
    this.moveReward = moveReward;
    this.pickReward = pickReward;
    this.moveRoomReward = moveRoomReward;
    this.lookReward = lookReward;
    this.util = u;
  }

  @Override
  public double reward(State s, Action a, State sprime) {
    SearchState ns = ((SearchState) s);
    String[] params = ((ObjectParameterizedAction) a).getObjectParameters();

    //Action: pick Obj1 1 0
    if (a.actionName().equals(ACTION_PARAMETERIZED_PICK)) {

      Location pickLocation = new Location(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
      Integer objectIndex = ns.objectInLocation(pickLocation);
      if (objectIndex!=-1 && util.ObjectInLookAnyLocation(ns, ns.searchableObjects.get(objectIndex).name, pickLocation)) {
        return goalReward;
      } else {
        return pickReward;
      }
    }

    if (a.actionName().equals(ACTION_PARAMETERIZED_MOVE) || a.actionName().equals(ACTION_PARAMETRIZED_MOVE_ROOM)) {
      Location currentLocation = new Location(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
      return moveReward + -util.euclideanDistance(currentLocation, ns.agent.x, ns.agent.y);
    }

    return moveReward;
  }
}