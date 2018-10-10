package searchWorld.src;

import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.singleagent.pomdp.wrappedmdpalgs.BeliefSparseSampling;
import burlap.debugtools.DPrint;
import burlap.mdp.core.Domain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.beliefstate.TabularBeliefState;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.ReflectiveHashableStateFactory;
import burlap.visualizer.Visualizer;
import searchWorld.src.BeliefRoadMap.Graph;
import searchWorld.src.Debug.Tests;
import searchWorld.src.Environments.*;
import searchWorld.src.Experiments.Data;
import searchWorld.src.BSS.BeliefSearchAgent;
import searchWorld.src.LanguageCommands.LanguageCommands;
import searchWorld.src.Pomcp.*;
import searchWorld.src.Pomdp.sensorModel;
import searchWorld.src.State.*;
import searchWorld.src.Visualization.SearchPomdpVisualizer;
import searchWorld.src.Visualization.SearchMdpVisualizer;
import searchWorld.src.Visualization.VizualizeBeliefState;

import java.util.*;

import static searchWorld.src.SearchDomain.*;

/**
 * The Search Driver is what runs the different
 * Created by arthurwandzel on 7/6/17.
 */
public class SearchDriver {

  public SearchDriver() {
  }

  /**
   * Runs through different planners or visualizers depending on what you set the string
   * solutionMethod as. Note that this works for both POMDPs (using Belief Sparse Sampling) and
   * MDPs.
   */
  public static Data executeSearchWorld(objectDistribution objDistribution, int[][] selectedMap, Rooms roomAbstractions, Graph roadMap, String solutionMethod,
                                        Double goalReward, Double moveReward, Double pickReward, Double moveRoomReward, Double lookReward,
                                        Location START_STATE, SortedMap<Integer, Location> GOALS, Random rnPOMCP, Random rnObservation,
                                        boolean ROBOTEXPERIMENT, double psi,
                                        Double observationAccuracy, int visionDepth, boolean epsilonOFModel, double bssGamma,
                                        int height, int numberSamples, int maxNumberOfActions,
                                        double pomcpExploration, double pomcpDiscount,
                                        boolean pomcpReinvigorate, int pomcpPartcleSamples,
                                        double obsSD, double betaV, double gammaV, double alphaV, double betaNV, double gammaNV, double alphaNV,
                                        Rooms rooms, List<Integer> classToObject,
                                        int printAgentInteraction, boolean issuePomdpVisualizer, boolean debugBSS) {

    Random copy = rnPOMCP;
    // Sets BURLAP debug print to false per burlap.behavior.singleagent.planning.stochastic.sparsesampling
    DPrint.toggleCode(7369430, false);
    // Sets print outs for <S,A,S',R> as well as B(s)
    SearchMdpEnvironment.setPrint(printAgentInteraction);
    SearchPomdpEnvironment.setPrint(printAgentInteraction);
    SearchRealEnvironment.setPrint(printAgentInteraction);
    BeliefSearchAgent.setPrint(printAgentInteraction);

    // If the solution method is value iteration or a visual MDP then it is not a partially
    // observable domain.

    // Generating domain and initial state.
    SearchDomain sd =
            new SearchDomain(selectedMap, roomAbstractions, roadMap, goalReward, moveReward, pickReward, moveRoomReward, lookReward,
                    observationAccuracy, visionDepth, solutionMethod, epsilonOFModel, GOALS.size(), rnObservation,
                 obsSD, betaV, gammaV,  alphaV, betaNV, gammaNV, alphaNV);
    Domain domain = sd.generateDomain();

    //Set the objects to find
    List<SearchObject> lostObjects = new ArrayList<>();
    int objectNumber = 0;
    for (Location coordinates : GOALS.values()) {
      //EMPTY FOR ROBOT EXPERIMENT
      if (ROBOTEXPERIMENT)
        lostObjects.add(new SearchObject(-1, -1, "Obj" + Integer.toString(objectNumber++ + NAME_OFFSET)));
      else
        lostObjects.add(new SearchObject(coordinates.x, coordinates.y, "Obj" + Integer.toString(objectNumber++ + NAME_OFFSET)));
    }
    List<Boolean> hasChosen = new ArrayList<>(Collections.nCopies(GOALS.size(), false));

    //Initialize state
    SearchState environmentState = new SearchState(new SearchAgent(START_STATE.x, START_STATE.y, 0), lostObjects, hasChosen);

    //Reflective hashing (BYOB for hash function per state variable)
    ReflectiveHashableStateFactory hsf = new ReflectiveHashableStateFactory();

    // The environment depends on whether the solution method is fully observable or not.
    Environment env = null;
    if (ROBOTEXPERIMENT){
      env = new SearchRealEnvironment((PODomain) domain, environmentState);
    } else if (solutionMethod.equals("visualMDP") || solutionMethod.equals("VI")){
      env = new SearchMdpEnvironment((SADomain) domain, environmentState);
    } else {
      env = new SearchPomdpEnvironment((PODomain) domain, environmentState);
    }
    SearchPomdpEnvironment.setDiscountFactor(pomcpDiscount); //set discount factor for pomdp env

    if (solutionMethod.equals("visualMDP")) { // Visualizing for MDP
      Visualizer vis = new SearchMdpVisualizer(selectedMap).getVisualizer();
      VisualExplorer exp = new VisualExplorer((SADomain) domain, env, vis);
      // EDSF controls for movement
      exp.addKeyAction("e", ACTION_PARAMETERIZED_LOOK, "north");
      exp.addKeyAction("d", ACTION_PARAMETERIZED_LOOK, "south");
      exp.addKeyAction("f", ACTION_PARAMETERIZED_LOOK, "east");
      exp.addKeyAction("s", ACTION_PARAMETERIZED_LOOK, "west");

      exp.addKeyAction("q", ACTION_PARAMETRIZED_MOVE_ROOM, "Room2");

      // IJKL controls for look directions
      exp.addKeyAction("i", ACTION_PARAMETERIZED_MOVE, "north");
      exp.addKeyAction("k", ACTION_PARAMETERIZED_MOVE, "south");
      exp.addKeyAction("l", ACTION_PARAMETERIZED_MOVE, "east");
      exp.addKeyAction("j", ACTION_PARAMETERIZED_MOVE, "west");

//      // Add any picks you find necessary and map to your preferred key.
      exp.addKeyAction("1", "pick", "Obj2 west 1 2");
      exp.addKeyAction("2", "pick", "Obj3 east 2 0");
      exp.addKeyAction("3", "pick", "Obj1 north 2 1");
      exp.addKeyAction("4", "pick", "Obj2 1 0");

      exp.initGUI();
    } else if (solutionMethod.equals("VI")) { // Solving MDP version with value iteration
      Visualizer vis = new SearchMdpVisualizer(selectedMap).getVisualizer();

      // VI Parameters
      double viGamma = 1;
      double viMaxDelta = 0.001;
      int viMaxIterations = 10000;

      ValueIteration planner = new ValueIteration((SADomain) domain, viGamma,
              hsf, viMaxDelta, viMaxIterations);
      Policy p2 = planner.planFromState(environmentState);
      Episode e = PolicyUtils.rollout(p2, env, 1000);
      new EpisodeSequenceVisualizer(vis, domain, Arrays.asList(e));


    }  else if (solutionMethod.equals("Pomcp") || solutionMethod.equals("Random")) {
      //Natural Language Command
      if (ROBOTEXPERIMENT){
        LanguageCommands lc = new LanguageCommands(psi, rooms, classToObject);
        //lc.parseLanguageCommandReal("Movo find the mugs in the kitchen");
        lc.parseLanguageCommandReal(((SearchRealEnvironment)env).receiveLanguageCommand());
        lc.languageObservation();
        objDistribution.belief = lc.beliefUpdate(GOALS.size(), objDistribution.belief);
        if (printAgentInteraction > 1) {
          System.out.println("////////////////////LanguageUpdatedBelief////////////////////");
          objDistribution.printHeatMap(objDistribution.belief, false, null);}
      }

      SearchState initialState = objDistribution.sampleHeatMap(environmentState);
      PODomain pomdpDomain = (PODomain) domain;

//      List<SearchState> jb = objDistribution.POMCPSample(initialState, numberSamples);
//      POMCP pomcp = new POMCP(rnPOMCP, pomdpDomain,
//              numberSamples, height,
//              pomcpExploration, pomcpDiscount,
//              pomcpReinvigorate, pomcpPartcleSamples,
//              objDistribution);
//      POMCP.VNode root = pomcp.initializePOMCP(initialState, jb);

      List<SearchState> jb = objDistribution.POMCPSample(initialState, numberSamples);
      OOOPOMCP pomcp = new OOOPOMCP(rnPOMCP, pomdpDomain,
              numberSamples, height,
              pomcpExploration, pomcpDiscount,
              pomcpReinvigorate, pomcpPartcleSamples,
              objDistribution);
      OOOPOMCP.VNode root = pomcp.initializePOMCP(initialState, jb);

      double startTime = System.currentTimeMillis();
      int i = 0;
      //double discount = 1.0;
      pomdpEnvironmentOutcome eo = new pomdpEnvironmentOutcome(null, null, null, 0.0, false, null);
      pomdpEpisode e = new pomdpEpisode();
      e.hiddenStateSequence.add(environmentState);
      objDistribution.setDomain((PODomain)  domain);

      if (solutionMethod.equals("Pomcp")){
        for (; i < maxNumberOfActions; i++) {
          if (printAgentInteraction > 0) e.beliefSequence.add(pomcp.printAndSaveBeliefs(root));
          Action ga = pomcp.SelectAction(root);
          eo = (pomdpEnvironmentOutcome) env.executeAction(ga);

          e.transition(ga, eo.op, eo.r, eo.hiddenState);

          if (eo.terminated) break;
          root = pomcp.Update((SearchState) eo.hiddenState, ga, eo.op, root);
          if (root.rewardTotal == -1) {
            break;
          } //no more belief particles
        }
      }
      if (!ROBOTEXPERIMENT){
        //no more particles than do random behavior
        if (solutionMethod.equals("Random") || root.rewardTotal == -1) {
          //do not assume access to underlying hidden state instead sample from belief
          if (printAgentInteraction > 0) System.out.print("\nRANDOM!\n");
          if (solutionMethod.equals("Random")) eo.hiddenState = initialState;

          for (; i < maxNumberOfActions; i++) {
            if (printAgentInteraction > 0) e.beliefSequence.add(pomcp.printAndSaveBeliefs(root));
            Action ga = pomcp.SelectRandomAction(objDistribution.sampleHeatMap((SearchState) eo.hiddenState));
            eo = (pomdpEnvironmentOutcome) env.executeAction(ga);

//          double discountedReward = eo.r * discount;
//          discountedReward = Math.round(discountedReward * 1e3) / 1e3;
//          discount *= pomcp.DISCOUNT;

            e.transition(ga, eo.op, eo.r, eo.hiddenState);

            if (eo.terminated) break;
          }
        }
      }
      double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;

      if (issuePomdpVisualizer) {
        Visualizer visPomdp = new SearchPomdpVisualizer(selectedMap).getVisualizer();
        List<State> stateVisualizer = new ArrayList<State>();
        e.beliefSequence.add(pomcp.printAndSaveBeliefs(root));
        for (int j = 0; j < e.beliefSequence.size(); j++) {
          stateVisualizer.add(new VizualizeBeliefState((SearchState) e.hiddenStateSequence.get(j), e.beliefSequence.get(j)));
        }
        e.stateSequence = stateVisualizer;
        new EpisodeSequenceVisualizer(visPomdp, domain, Arrays.asList(e));
      }

      //return data
      int numberOfObjectsFound = Data.calculateNumberOfObjectsFound(e.rewardSequence, goalReward, pickReward);
      if (printAgentInteraction > 0) {
        Data.printData(elapsedTime, e, numberOfObjectsFound);
      }
      return new Data(e, elapsedTime, numberOfObjectsFound);
    }


    return new Data(new pomdpEpisode(), 0, 0); //return null data
  }
}
