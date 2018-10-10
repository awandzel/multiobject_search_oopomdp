package searchWorld.src.BeliefRoadMap.RoutingMDP;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.State.SearchAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static searchWorld.src.BeliefRoadMap.RoutingMDP.routeDomain.*;
import static searchWorld.src.SearchDomain.WALL;

/**
 * Created by awandzel on 8/6/18.
 */
public class routeModel implements FullStateModel {

  public int[][] cleanWorldMap;

  routeModel(int[][] wm){
    this.cleanWorldMap = wm;
  }

  @Override
  public List<StateTransitionProb> stateTransitions(State s, Action a) {
    List<StateTransitionProb> tps = new ArrayList<StateTransitionProb>();
    routeState ns = (routeState) s.copy();
    int nx = ns.currentPosition.x;
    int ny = ns.currentPosition.y;

    if (a.actionName().equals(ACTION_MOVE_NORTH)){
      ny++;
    } else if (a.actionName().equals(ACTION_MOVE_SOUTH)) {
      ny--;
    } else if (a.actionName().equals(ACTION_MOVE_EAST)) {
      nx++;
    } else {
      nx--;
    }

    Location ncp = ns.touchCurrentPosition();
    ncp.x = nx;
    ncp.y = ny;

    if (checkIfInBounds(nx,ny)){
      tps.add(new StateTransitionProb(ns, 1.));
    } else {
      tps.add(new StateTransitionProb(s, 1.));
    }

    return tps;
  }

  public boolean checkIfInBounds(int nx, int ny) {
    return !(nx < 0 || nx >= this.cleanWorldMap.length || ny < 0 || ny >= this.cleanWorldMap[0].length);
  }

  @Override
  public State sample(State s, Action a) {
    List<StateTransitionProb> stpList = this.stateTransitions(s, a);
    return stpList.get(0).s;
  }
}

