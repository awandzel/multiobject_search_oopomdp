package searchWorld.src;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.pomdp.wrappedmdpalgs.BeliefSparseSampling;
import burlap.debugtools.DPrint;
import burlap.mdp.core.Domain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.beliefstate.TabularBeliefState;
import burlap.statehashing.ReflectiveHashableStateFactory;
import burlap.visualizer.Visualizer;
import searchWorld.src.BeliefRoadMap.Graph;
import searchWorld.src.Environments.*;
import searchWorld.src.Experiments.Data;
import searchWorld.src.BSS.BeliefSearchAgent;
import searchWorld.src.LanguageCommands.LanguageCommands;
import searchWorld.src.Pomcp.*;
import searchWorld.src.Pomdp.sensorModel;
import searchWorld.src.State.*;
import searchWorld.src.Visualization.SearchPomdpVisualizer;
import searchWorld.src.Visualization.VizualizeBeliefState;

import java.util.*;

import static searchWorld.src.SearchDomain.*;

/**
 * The Search Driver is what runs the different
 * Created by arthurwandzel on 7/6/17.
 */
public class SearchSequentialDriver {

  public SearchSequentialDriver() {
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
    System.out.println("SEQUENTIAL DRIVER IS BEING RUN");

    // Sets BURLAP debug print to false per burlap.behavior.singleagent.planning.stochastic.sparsesampling
    DPrint.toggleCode(7369430, false);
    // Sets print outs for <S,A,S',R> as well as B(s)
    SearchMdpEnvironment.setPrint(printAgentInteraction);
    SearchPomdpEnvironment.setPrint(printAgentInteraction);
    BeliefSearchAgent.setPrint(printAgentInteraction);

    // If the solution method is value iteration or a visual MDP then it is not a partially
    // observable domain.

    // Generating domain and initial state.
    SearchDomain sd =
            new SearchDomain(selectedMap, roomAbstractions, roadMap, goalReward, moveReward, pickReward, moveRoomReward, lookReward,
                    observationAccuracy, visionDepth, solutionMethod, epsilonOFModel, GOALS.size(), rnObservation,
                    obsSD, betaV, gammaV, alphaV, betaNV, gammaNV, alphaNV);
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
    SearchPomdpEnvironment.setDiscountFactor(pomcpDiscount); //set discount factor for pomdp env

    if (solutionMethod.equals("BSS")) { // Solving POMDP version with Belief Sparse Sampling
      PODomain pomdpDomain = (PODomain) domain;

      //Tests.testObservation(pomdpDomain, initialState, (SearchPomdpEnvironment) env);
      //Tests.testModel((SADomain) domain, initialState, (SearchMdpEnvironment) envMdp);

      // ============================== Belief Construction ==============================
      List<SearchState> beliefs = objDistribution.POMCPSample(environmentState, numberSamples);
      //System.out.println("Belief particles: " + bsConstruct.permutedObjectLocations.size());

      TabularBeliefState bs = new TabularBeliefState(pomdpDomain, pomdpDomain.getStateEnumerator());
      for (SearchState s : beliefs)
        bs.setBelief(s, 1.0 / beliefs.size());

      // ============================== BSS Solver ==============================
      BeliefSparseSampling bss = new BeliefSparseSampling(pomdpDomain, bssGamma, hsf, height, numberSamples);

      bss.getSparseSamplingPlanner().setDebugTraceNode(debugBSS);
      bss.getSparseSamplingPlanner().setForgetPreviousPlanResults(true); //essential for garbage collection on large runs

      Policy policy = bss.planFromState(bs); //plan from state
      BeliefSearchAgent agent = new BeliefSearchAgent(pomdpDomain, env, policy, bssGamma);
      agent.setBeliefState(bs);

      //System.exit(-1);
      // Calculating and printing out elapsed time
      double startTime = System.currentTimeMillis();
      //long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      pomdpEpisode e = (pomdpEpisode) agent.actUntilTerminalOrMaxSteps(maxNumberOfActions);
      double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
      //long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      //System.out.println(afterUsedMem - beforeUsedMem);

      // ============================== Build pomdp visualizer ==============================
      if (issuePomdpVisualizer) {
        Visualizer visPomdp = new SearchPomdpVisualizer(selectedMap).getVisualizer();

        List<State> stateVisualizer = new ArrayList<>();
        stateVisualizer.add(new VizualizeBeliefState(environmentState, bs.nonZeroBeliefs()));
        for (int i = 0; i < e.stateSequence.size(); i++) {
          stateVisualizer.add(new VizualizeBeliefState((SearchState) e.hiddenStateSequence.get(i), e.beliefSequence.get(i)));
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
    else if (solutionMethod.equals("Pomcp") || solutionMethod.equals("OOPomcp")) {

      if (ROBOTEXPERIMENT) {
        LanguageCommands lc = new LanguageCommands(psi, rooms, classToObject);
        //lc.parseLanguageCommandReal("Movo find the mugs in the kitchen");
        lc.parseLanguageCommandReal(((SearchRealEnvironment) env).receiveLanguageCommand());
        lc.languageObservation();
        objDistribution.belief = lc.beliefUpdate(GOALS.size(), objDistribution.belief);

        Random rn = new Random();
        Set<Integer> objectOrdering = new HashSet<>();
        Map<Integer, Map<Location, Double>> objectOrderedBelief = new HashMap<>();
        for (int o = 0; o < GOALS.size(); ) {
          int rnObject = rn.nextInt(GOALS.size());
          if (!objectOrdering.contains(rnObject)) {
            Map<Location, Double> rnMap = objDistribution.belief.get(rnObject);
            objectOrderedBelief.put(o, rnMap);
            objectOrdering.add(rnObject);
            o++;
          }
        }
        objDistribution.belief = objectOrderedBelief;
        if (printAgentInteraction > 1) {
          System.out.println("////////////////////LanguageUpdatedBelief////////////////////");
          objDistribution.printHeatMap(objDistribution.belief, false, null);
        }
      }

      PODomain pomdpDomain = (PODomain) domain;
      objDistribution.setDomain((PODomain) domain);
      objDistribution.numberOfGoals = 1;
      ((sensorModel) pomdpDomain.getObservationFunction()).numberOfObjects = 1;
      SearchState initialState = environmentState;

      pomdpEnvironmentOutcome eo = new pomdpEnvironmentOutcome(null, null, null, 0.0, false, null);
      pomdpEpisode e = new pomdpEpisode();

      double startTime = System.currentTimeMillis();
      int i = 0;
      //double discount = 1.0;
      double elapsedTime = 0.0;

      for (int g = 0; g < GOALS.size(); g++){
        SearchObject newSearchObject = new SearchObject(environmentState.searchableObjects.get(g).x, environmentState.searchableObjects.get(g).y, "Obj1" );
        initialState = new SearchState(initialState.agent, new ArrayList<>(Arrays.asList(newSearchObject)), new ArrayList<>(Arrays.asList(environmentState.hasChosen.get(g))));
        env = (ROBOTEXPERIMENT) ? new SearchRealEnvironment((PODomain) domain, initialState) : new SearchPomdpEnvironment((PODomain) domain, initialState);
        initialState = objDistribution.sampleHeatMap(initialState);
        objDistribution.belief.put(0, objDistribution.belief.get(g));

      List<SearchState> jb = objDistribution.POMCPSample(initialState, numberSamples);
      OOOPOMCP pomcp = new OOOPOMCP(rnPOMCP, pomdpDomain,
              numberSamples, height,
              pomcpExploration, pomcpDiscount,
              pomcpReinvigorate, pomcpPartcleSamples,
              objDistribution);
      OOOPOMCP.VNode root = pomcp.initializePOMCP(initialState, jb);

        for (; i < maxNumberOfActions; i++) {
          if (printAgentInteraction > 0) e.beliefSequence.add(pomcp.printAndSaveBeliefs(root));
          Action ga = pomcp.SelectAction(root);
          eo = (pomdpEnvironmentOutcome) env.executeAction(ga);

//        double discountedReward = eo.r * discount;
//        discountedReward = Math.round(discountedReward * 1e3) / 1e3; //round to nearest thousandth
//        discount *= pomcp.DISCOUNT;

          e.transition(ga, eo.op, eo.r, eo.hiddenState);

          if (eo.terminated) break;
          root = pomcp.Update((SearchState) eo.hiddenState, ga, eo.op, root);
          if (root.rewardTotal == -1) {
            break;
          } //no more belief particles
          if (((SearchState) eo.hiddenState).hasChosen.get(0)) {
            System.out.println("Found object! " + g);
            break;
          }
        }
        if (!ROBOTEXPERIMENT) {
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
        initialState = (SearchState) eo.hiddenState;
        elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
      }

      if (issuePomdpVisualizer) {
        Visualizer visPomdp = new SearchPomdpVisualizer(selectedMap).getVisualizer();
        List<State> stateVisualizer = new ArrayList<State>();
        for (int j = 0; j < e.hiddenStateSequence.size(); j++) {
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
