'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.MapUtilities as Util
import Multi_Object_Search.Pomdp.OOState.OOState as State
import Multi_Object_Search.Pomdp.Domain.Domain as Domain
import Multi_Object_Search.Pomdp.SimulatedEnvironment as Environment
import copy
import Multi_Object_Search.Pomdp.OOState.Location as Loc
import Multi_Object_Search.Pomdp.ObservationFunction.FanShapedSensor as F
import Multi_Object_Search.Pomdp.ObservationFunction.MultiObjectObservation as Observation


def executeMultiObjectSearch(Maps, Rooms, Objects, rrtGraph, belief,
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

    #sample heat map for initialState (OOPOMCP does not get access to environmentState)
    initialState = belief.sampleState(environmentState)

    #POMCP



