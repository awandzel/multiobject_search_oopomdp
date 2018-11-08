'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.MapUtilities as Util
import Multi_Object_Search.Pomdp.OOState.OOState as State
import Multi_Object_Search.Pomdp.Domain.Domain as Domain
import Multi_Object_Search.Pomdp.ObservationFunction.SensingRegion as SensingRegion
import Multi_Object_Search.Pomdp.OOState.Location as Loc


def executeMultiObjectSearch(Maps, Rooms, Objects, rrtGraph,
                                              PomcpParameters, PomdpParameters, ObservationModelParameters, LanguageParameters,
                                              startState, POMCPRn, observationRn, ROBOTEXPERIMENT, debugPrintOuts):


    util = Util.mapUtilities(Maps, Rooms, rrtGraph)

    domain = Domain.SearchDomain(util, PomdpParameters, ObservationModelParameters, observationRn)
    domain.generateDomain()

    environmentState = State.OOState(startState, Objects, [False for i in range(len(Objects))])
    environmentState2 = State.OOState(Loc.Location(1,1), Objects, [False for i in range(len(Objects))])

    sensingRegion = SensingRegion.locationsInVisionBoundingBox(util, environmentState2)
    sensingRegion2 = SensingRegion.locationsInVisionCone(util, environmentState, "Look_0")
    sensingRegion3 = SensingRegion.locationsInVisionCone(util, environmentState, "Look_1")
    sensingRegion4 = SensingRegion.locationsInVisionCone(util, environmentState, "Look_2")
    sensingRegion5 = SensingRegion.locationsInVisionCone(util, environmentState, "Look_3")

    x = 1







