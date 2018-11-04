'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.MapUtilities as Util


def executeMultiObjectSearch(Maps, Rooms, Objects, rrtGraph,
                                              PomcpParameters, PomdpParameters, ObservationModelParameters, LanguageParameters,
                                              startState, POMCPRn, observationRn, ROBOTEXPERIMENT, debugPrintOuts):


    util = Util.mapUtilities(Maps, Rooms, rrtGraph)