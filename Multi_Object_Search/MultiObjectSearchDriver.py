'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.MapUtilities as Util
import Multi_Object_Search.Pomdp.OOState.OOState as State
import Multi_Object_Search.Pomdp.Domain.SearchDomain as Domain


def executeMultiObjectSearch(Maps, Rooms, Objects, rrtGraph,
                                              PomcpParameters, PomdpParameters, ObservationModelParameters, LanguageParameters,
                                              startState, POMCPRn, observationRn, ROBOTEXPERIMENT, debugPrintOuts):


    util = Util.mapUtilities(Maps, Rooms, rrtGraph)

    domain = Domain.SearchDomain(util, PomdpParameters, ObservationModelParameters, observationRn)
    domain.generateDomain()

    environmentState = State.OOState(startState, Objects, [False for i in range(len(Objects))])







