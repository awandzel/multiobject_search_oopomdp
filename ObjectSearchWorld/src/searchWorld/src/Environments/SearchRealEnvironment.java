package searchWorld.src.Environments;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.state.NullState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.pomdp.PODomain;
import com.fasterxml.jackson.databind.JsonNode;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;
import ros.tools.MessageUnpacker;
import searchWorld.src.Pomdp.sensorModel;
import searchWorld.src.State.SearchObject;
import searchWorld.src.State.SearchState;

import java.util.List;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by awandzel on 8/29/18.
 */
public class SearchRealEnvironment implements Environment, RosListenDelegate {
  static double discountFactor = .95;
  static int searchREALEnvironmentPrint;

  public static void setPrint(int s) {
    searchREALEnvironmentPrint = s;
  }

  public static void setDiscountFactor(double df) {
    discountFactor = df;
  }
  double discount = 1.0;

  protected RosBridge bridge = new RosBridge();
  protected Publisher ein;
  protected boolean ACTIONMESSAGEISPUBLISHED = false;
  protected String ACTIONMESSAGE = "";
  protected boolean LANGUAGEMESSAGEISPUBLISHED = false;
  protected String LANGUAGEMESSAGE = "";

  final String host = "ws://localhost:9090";
  final String stateMessage = "std_msgs/String";
  final String actionSubscriberTopic = "/burlap_state";
  final String actionPublisherTopic = "/burlap_action";
  final String languageSubscriberTopic = "/movo_speech";

  protected State curObservation = NullState.instance;
  protected State curState = NullState.instance;
  protected double lastReward = 0;
  protected boolean terminated = false;
  PODomain domain;

  public SearchRealEnvironment(PODomain domain, SearchState environmentState) {
    bridge.connect(host, true);
    //bridge.waitForConnection();
    bridge.subscribe(SubscriptionRequestMsg.generate(actionSubscriberTopic).setType(stateMessage).setThrottleRate(1).setQueueLength(1), this);
    bridge.subscribe(SubscriptionRequestMsg.generate(languageSubscriberTopic).setType(stateMessage).setThrottleRate(1).setQueueLength(1), this);
    ein = new Publisher(actionPublisherTopic, stateMessage, bridge);

    this.domain = domain;
    curState = environmentState;
  }

  @Override
  public State currentObservation() {
    return curObservation;
  }

  public String receiveLanguageCommand(){
    System.out.println("\n=================================\n" +
            "ENTER LANGUAGE: ");

    //wait till received message (occurs automatically)
    while (!LANGUAGEMESSAGEISPUBLISHED) {
      try {
        Thread.sleep(1000 * 10);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }
    LANGUAGEMESSAGEISPUBLISHED = false;
    System.out.println("COMMAND RETURNED AS: " + LANGUAGEMESSAGE);
    return LANGUAGEMESSAGE;
  }

  @Override
  public EnvironmentOutcome executeAction(Action a) {
    String[] params = ((ObjectParameterizedAction) a).getObjectParameters();
    SearchState ns = ((SearchState) this.curState).copy();
    String msg = "";

    if (searchREALEnvironmentPrint > 0) {System.out.print("Action: " + a.toString() + "\n");}

    if (a.actionName().equals(ACTION_PARAMETERIZED_MOVE) || a.actionName().equals(ACTION_PARAMETRIZED_MOVE_ROOM)) {
      msg = ACTION_PARAMETERIZED_MOVE + "," + params[0] + "," + params[1];
    }
    else if (a.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
      msg = ACTION_PARAMETERIZED_LOOK + "," + params[0];
    }
    else if (a.actionName().startsWith(ACTION_PARAMETERIZED_PICK)) {
      msg = ACTION_PARAMETERIZED_PICK + "," + params[0] + "," + params[1];
    }
    else {
      throw new RuntimeException("Action was not Properly Selected");
    }
    ein.publish(new PrimitiveMsg<>(msg));

    //wait till received message (occurs automatically)
    while (!ACTIONMESSAGEISPUBLISHED){
      try {
        Thread.sleep(1000 *10);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }
    ACTIONMESSAGEISPUBLISHED = false;
    String[] message = ACTIONMESSAGE.split(",");

    State nextObservation = null;
    if (a.actionName().equals(ACTION_PARAMETERIZED_LOOK)) {
      nextObservation = ((sensorModel) domain.getObservationFunction()).realWorldExperimentObservation(message, curState, a);
    } else {
      nextObservation = ((sensorModel) domain.getObservationFunction()).sampleMoveAction();
    }

    //model (emphasis on pick)
    EnvironmentOutcome eo;
    if (a.actionName().equals(ACTION_PARAMETERIZED_PICK)){
      int objectIndex = -1;
      List<Boolean> hasChosen = ns.touchHasChosen();
      List<SearchObject> searchObjects = ns.touchSearchableObjects();

      double reward = 0;
      boolean terminated = true;
      if (Boolean.parseBoolean(message[0])){
        objectIndex = Integer.parseInt(message[1]);
        hasChosen.set(objectIndex, true);
        reward = 1000;
      } else {
        for (int i = 0; i < ns.hasChosen.size(); i++) {
          if (!ns.hasChosen.get(i)) {
            objectIndex = i;
            hasChosen.set(i, true);
            break;
          }
        }
        reward = -1000;
      }
      searchObjects.set(objectIndex, new SearchObject(Integer.parseInt(params[0]), Integer.parseInt(params[1]), "Obj" + Integer.toString(objectIndex)));

      for (Boolean b : ns.hasChosen) {
        if (!b) terminated = false;
      }
      eo = new EnvironmentOutcome(this.curState, a, ns, reward, terminated);

    } else if (a.actionName().equals(ACTION_PARAMETERIZED_LOOK)){
      eo = new EnvironmentOutcome(this.curState, a, this.curState.copy(), -10, false);
    }else {
      eo = domain.getModel().sample(this.curState, a);
    }

    // Update reward and terminated
    this.lastReward = eo.r;
    this.terminated = eo.terminated;
    this.curState = eo.op;

    double discountedReward = eo.r * discount;
    discountedReward = Math.round(discountedReward * 1e3) / 1e3;
    discount *= discountFactor;

    // (this class handles the interaction between the state space and environment)
    if (searchREALEnvironmentPrint > 0) {
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

    pomdpEnvironmentOutcome observedOutcome = new pomdpEnvironmentOutcome(this.curObservation, a, nextObservation, eo.r, this.terminated, eo.op);
    this.curObservation = nextObservation;

    return observedOutcome;
  }

  @Override
  public double lastReward() {
    return 0;
  }

  @Override
  public boolean isInTerminalState() {
    return terminated;
  }

  @Override
  public void resetEnvironment() {
    terminated = false;
  }

  @Override
  public void receive(JsonNode jsonNode, String s) {
    String topic = jsonNode.get("topic").asText();
    if (topic.equals(actionSubscriberTopic)) {
      MessageUnpacker<PrimitiveMsg<String>> unpacker = new MessageUnpacker<>(PrimitiveMsg.class);
      PrimitiveMsg<String> msg = unpacker.unpackRosMessage(jsonNode);
      ACTIONMESSAGE = msg.data.toLowerCase();
      ACTIONMESSAGEISPUBLISHED = true;
    }
    if (topic.equals(languageSubscriberTopic)) {
      MessageUnpacker<PrimitiveMsg<String>> unpacker = new MessageUnpacker<>(PrimitiveMsg.class);
      PrimitiveMsg<String> msg = unpacker.unpackRosMessage(jsonNode);
      LANGUAGEMESSAGE = msg.data.toLowerCase();
      LANGUAGEMESSAGEISPUBLISHED = true;
    }
  }
}
