'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.MapUtilities as Util
import Multi_Object_Search.Pomdp.OOState.OOState as State
import Multi_Object_Search.Pomdp.Domain.Domain as Domain
import Multi_Object_Search.Pomdp.ObservationFunction.SensingRegion as SensingRegion
import Multi_Object_Search.Pomdp.OOState.Location as Loc
import Multi_Object_Search.Pomdp.ObservationFunction.FanShapedSensor as FanShapedSensor


def executeMultiObjectSearch(Maps, Rooms, Objects, rrtGraph,
                                              PomcpParameters, PomdpParameters, ObservationModelParameters, LanguageParameters,
                                              startState, POMCPRn, observationRn, ROBOTEXPERIMENT, debugPrintOuts):


    util = Util.mapUtilities(Maps, Rooms, rrtGraph)

    domain = Domain.SearchDomain(util, PomdpParameters, ObservationModelParameters, observationRn)
    domain.generateDomain()

    environmentState = State.OOState(startState, Objects, [False for i in range(len(Objects))])

    fan = FanShapedSensor.FanShapedSensor(util, ObservationModelParameters, observationRn)
    o = fan.sample(environmentState, "Look_0") #look right
    fan.toString(o, "Look_0")
    o1 = fan.sample(environmentState, "Look_1") #null
    fan.toString(o1, "Look_1")
    o2 = fan.sample(environmentState, "Look_2")
    fan.toString(o2, "Look_2")






