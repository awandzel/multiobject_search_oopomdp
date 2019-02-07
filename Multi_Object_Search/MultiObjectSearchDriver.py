'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.MapUtilities as Util
import Multi_Object_Search.Pomdp.OOState.OOState as State
import Multi_Object_Search.Pomdp.Domain.Domain as Domain
import Multi_Object_Search.Pomdp.SimulatedEnvironment as Environment
import Multi_Object_Search.OOPOMCP.Pomcp as Pomcp
import Multi_Object_Search.OOPOMCP.OOPomcp as OOPomcp
import time
import Multi_Object_Search.Data as Data
import copy
import Multi_Object_Search.Pomdp.OOState.Location as Loc
import Multi_Object_Search.Pomdp.ObservationFunction.FanShapedSensor as F
import Multi_Object_Search.Pomdp.ObservationFunction.MultiObjectObservation as Observation


def executeMultiObjectSearch(Maps, Rooms, Objects, rrtGraph, belief, sol,
                                              PomcpParameters, PomdpParameters, ObservationModelParameters, LanguageParameters,
                                              startState, POMCPRn, observationRn, ROBOTEXPERIMENT, debugPrintOuts):


    util = Util.mapUtilities(Maps, Rooms, rrtGraph)

    domain = Domain.SearchDomain(util, PomdpParameters, ObservationModelParameters, observationRn)
    domain.generateDomain()

    environmentState = State.OOState(startState, Objects, [False for i in range(len(Objects))])

    if ROBOTEXPERIMENT:
        env = Environment.RealEnvironment(domain, environmentState, debugPrintOuts)
    else:
        env = Environment.SimulatedEnvironment(domain, environmentState, debugPrintOuts)

    #sample belief for initialState (OOPOMCP does not get access to environmentState)
    initialState = belief.sampleState(environmentState)

    #POMCP
    beliefEstimate = belief.pomcpSample(initialState, PomcpParameters.maxSamples)
    if sol == "Pomcp":
        solver = Pomcp.Pomcp(domain, PomcpParameters, belief, POMCPRn)
    else:
        solver = OOPomcp.OOPomcp(domain, PomcpParameters, belief, POMCPRn)
    root = solver.initializePomcp(initialState, beliefEstimate)

    start = time.time()
    actionCount = 0
    while actionCount < PomcpParameters.maxActions:
        action = solver.selectAction(root)

        [s_, o, a, r, t] = env.executeAction(action)
        env.episode.transition(s_, o, a, r)

        if t: break; #termination condition
        root = solver.beliefUpdate(a, o, root)
        if root == "particleDepletion": #no more particles
            break

        actionCount += 1

    #no more particles then do random policy
    if not ROBOTEXPERIMENT and root.rewardTotal == -1:
        if debugPrintOuts > 0: print("RANDOM!")

        while actionCount < PomcpParameters.maxActions:
            #do not assume access to underlying hiddden state
            action = domain.selectRandomAction(belief.sampleState(environmentState), PomcpParameters.POMCPRn)

            [s_, o, a, r, t] = env.executeAction(action)
            env.episode.transition(s_, o, a, r)

            if t: break

            actionCount += 1

    end = time.time()

    # TODO: visualization

    elapsedTime = end - start
    data = Data.Data(env.episode, elapsedTime, PomdpParameters)

    if debugPrintOuts > 0:
        data.printData()

    return Data

