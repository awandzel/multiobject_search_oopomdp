package searchWorld.src.BeliefRoadMap.RoutingMDP;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import searchWorld.src.Model.SearchModel;
import searchWorld.src.Model.SearchRF;
import searchWorld.src.Model.SearchTF;
import searchWorld.src.Pomcp.Location;

import static searchWorld.src.SearchDomain.EMPTY;

/**
 * Created by awandzel on 8/6/18.
 */
public class routeDomain implements DomainGenerator {

  public final static String ACTION_MOVE_NORTH = "moveNorth";
  public final static String ACTION_MOVE_SOUTH = "moveSouth";
  public final static String ACTION_MOVE_EAST = "moveEast";
  public final static String ACTION_MOVE_WEST = "moveWest";

  public int[][] worldMap;
  public Location goal;

  public routeDomain(int[][] wm, Location g) {
    this.worldMap = wm;
    this.goal = g;
  }

  @Override
  public Domain generateDomain() {


    SADomain domain = new SADomain();

    domain.addActionTypes(new UniversalActionType(ACTION_MOVE_NORTH),
            new UniversalActionType(ACTION_MOVE_SOUTH),
            new UniversalActionType(ACTION_MOVE_EAST),
            new UniversalActionType(ACTION_MOVE_WEST));

    routeModel sModel = new routeModel(worldMap);
    RewardFunction rf = new routeRF(goal);
    TerminalFunction tf = new routeTF(goal);

    FactoredModel model = new FactoredModel(sModel, rf, tf);
    domain.setModel(model);

    return domain;
  }
}
