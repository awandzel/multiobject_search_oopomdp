package searchWorld.src.Debug;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.pomdp.PODomain;
import searchWorld.src.Environments.SearchMdpEnvironment;
import searchWorld.src.Environments.SearchPomdpEnvironment;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.Pomdp.locationInVisionCone;
import searchWorld.src.Pomdp.objectObservation;
import searchWorld.src.Pomdp.visionConeObservation;
import searchWorld.src.State.SearchState;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by arthurwandzel on 11/28/17.
 */
public class Tests {

  //Need to test if proportion of observations (e.g. NotObject) corresponds to lookAccuracy
  //Expect if lookAccuracy = .8 then 8/10 should be correct observation
  public static void testObservation(PODomain domain, SearchState initialState, SearchPomdpEnvironment env) {
    env.setPrint(0);
    ActionType actionType = domain.getAction(ACTION_PARAMETERIZED_LOOK);
    List<Action> actions = actionType.allApplicableActions(initialState);

    double TOTAL = 100000; //converges to lookAccuracy with enough samples

    System.out.println("\n" + actions.get(0).toString());
    List<Location> vc = util.locationsInVisionCone(initialState, actions.get(0));
    Map<String, Location> presence = new TreeMap<>();
    for (int i = 0; i < TOTAL; i++) {
      EnvironmentOutcome eo2 = env.executeAction(actions.get(0));
      visionConeObservation vo = (visionConeObservation) eo2.op;
      for (int l = 0; l < vc.size(); l++){
        String key = vo.observation.get(0).get(l).objectPresent + vc.get(l).x + "," + vc.get(l).y;
        presence.put(key, vc.get(l));
      }
    }
    for (Map.Entry<String, Location> e : presence.entrySet()){
      System.out.println(e.getKey() + " , " + e.getValue().toString());
    }
    System.exit(0);
    return;
  }

  public static void testObservationOG(PODomain domain, SearchState initialState, SearchPomdpEnvironment env) {
    env.setPrint(0);
    ActionType actionType = domain.getAction(ACTION_PARAMETERIZED_LOOK);
    List<Action> actions = actionType.allApplicableActions(initialState);

    double object = 0;
    double notObject = 0;
    double TOTAL = 100000; //converges to lookAccuracy with enough samples

    System.out.println("\n" + actions.get(0).toString());
    for (int i = 0; i < TOTAL; i++) {
      //EnvironmentOutcome eo = domain.getModel().sample(initialState, actions.get(0));
      EnvironmentOutcome eo2 = env.executeAction(actions.get(0));
      if (eo2.o.get(VAR_OBSERVATION).equals("notObject")) notObject++;
      if (eo2.o.get(VAR_OBSERVATION).equals("object")) object++;
      //System.out.println(eo2.o.toString());
    }
    System.out.println("\nNot Object Count: " + notObject / TOTAL);
    System.out.println("Object Count: " + object / TOTAL);

    System.exit(0);
    return;
  }

  public static void testModel(SADomain domain, State initialState, SearchMdpEnvironment env) {
    env.setPrint(0);
    String actionName = ACTION_PARAMETERIZED_PICK;
    ActionType actionType = domain.getAction(actionName);
    List<Action> actions = actionType.allApplicableActions(initialState);

    double action = 0;
    double notAction = 0;
    double TOTAL = 100000; //converges to lookAccuracy with enough samples

    System.out.println("\n" + actions.get(0).toString());
    for (int i = 0; i < TOTAL; i++) {
      EnvironmentOutcome eo = domain.getModel().sample(initialState, actions.get(0));
      EnvironmentOutcome eo2 = env.executeAction(actions.get(0));
      if (!eo2.a.actionName().equals(actionName)) notAction++;
      //if (!eo2.op.get(VAR_AGENT_Y).equals(1)) action++;
      //System.out.println(eo2.o.toString());
    }
    System.out.println("\nNot Action Count: " + notAction);
    System.out.println("No Change: " + action);

    System.exit(0);
    return;
  }


}


